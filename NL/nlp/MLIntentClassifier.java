package view.nlp;

import view.ParsedQuery;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Machine learning-based intent classification using OpenNLP models
 */
public class MLIntentClassifier {

    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private Map<String, Set<String>> intentPatterns;
    private Map<String, Pattern> contextPatterns;

    public MLIntentClassifier(String modelBasePath) throws IOException {
        initializeModels(modelBasePath);
        initializeIntentPatterns();
        initializeContextPatterns();
    }

    private void initializeModels(String modelBasePath) throws IOException {
        // Initialize OpenNLP models
        TokenizerModel tokenizerModel = new TokenizerModel(getModelInputStream(modelBasePath + "en-token.bin"));
        tokenizer = new TokenizerME(tokenizerModel);

        POSModel posModel = new POSModel(getModelInputStream(modelBasePath + "en-pos-maxent.bin"));
        posTagger = new POSTaggerME(posModel);
    }

    private void initializeIntentPatterns() {
        intentPatterns = new HashMap<>();

        intentPatterns.put("CONTRACT_INFO",
                           new HashSet<>(Arrays.asList("show", "contract", "details", "info", "find", "get",
                                                       "display")));

        intentPatterns.put("PARTS_INFO",
                           new HashSet<>(Arrays.asList("part", "parts", "specifications", "datasheet", "compatible",
                                                       "stock", "manufacturer", "spec", "product")));

        intentPatterns.put("STATUS_CHECK",
                           new HashSet<>(Arrays.asList("status", "expired", "active", "check", "state", "condition")));

        intentPatterns.put("CUSTOMER_INFO",
                           new HashSet<>(Arrays.asList("customer", "account", "client", "company", "organization")));

        intentPatterns.put("HELP_CREATE",
                           new HashSet<>(Arrays.asList("create", "help", "how", "new", "want", "make", "generate")));

        intentPatterns.put("FAILED_PARTS",
                           new HashSet<>(Arrays.asList("failed", "filed", "defective", "broken", "issues",
                                                       "problems")));

        intentPatterns.put("USER_CONTRACTS",
                           new HashSet<>(Arrays.asList("user", "person", "employee", "rep", "representative")));

        intentPatterns.put("LIST_PARTS", new HashSet<>(Arrays.asList("list", "all", "show", "parts", "components")));
    }

    private void initializeContextPatterns() {
        contextPatterns = new HashMap<>();

        // Contract-related patterns
        contextPatterns.put("CONTRACT_CONTEXT",
                            Pattern.compile(".*\\b(?:show|display|get|find)\\s+contract\\s+\\d{6}\\b.*",
                                            Pattern.CASE_INSENSITIVE));

        // Parts-related patterns
        contextPatterns.put("PARTS_CONTEXT",
                            Pattern.compile(".*\\b(?:specifications?|datasheet|manufacturer)\\s+(?:of|for)\\s+[A-Z0-9]+\\b.*",
                                            Pattern.CASE_INSENSITIVE));

        // Status patterns
        contextPatterns.put("STATUS_CONTEXT",
                            Pattern.compile(".*\\bstatus\\s+of\\s+\\d{6}\\b.*", Pattern.CASE_INSENSITIVE));

        // Failed parts patterns
        contextPatterns.put("FAILED_PARTS_CONTEXT",
                            Pattern.compile(".*\\b(?:failed|defective|broken)\\s+(?:parts?|components?)\\s+(?:in|for|of)\\s+\\d{6}\\b.*",
                                            Pattern.CASE_INSENSITIVE));

        // Customer-related patterns
        contextPatterns.put("CUSTOMER_CONTEXT",
                            Pattern.compile(".*\\b(?:customer|client|company)\\s+(?:info|information|details)\\s+(?:for|of)\\s+[A-Z0-9]+\\b.*",
                                            Pattern.CASE_INSENSITIVE));

        // Help/Create patterns
        contextPatterns.put("HELP_CREATE_CONTEXT",
                            Pattern.compile(".*\\b(?:how\\s+to|help\\s+me|create|make|generate)\\s+(?:new|a)\\s+\\w+\\b.*",
                                            Pattern.CASE_INSENSITIVE));

        // User contracts patterns
        contextPatterns.put("USER_CONTRACTS_CONTEXT",
                            Pattern.compile(".*\\b(?:contracts?|agreements?)\\s+(?:for|of|by)\\s+(?:user|person|employee|rep)\\s+\\w+\\b.*",
                                            Pattern.CASE_INSENSITIVE));
    }

    private InputStream getModelInputStream(String modelPath) throws IOException {
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            return new FileInputStream(modelFile);
        } else {
            // Try to load from classpath
            InputStream stream = getClass().getClassLoader().getResourceAsStream(modelPath);
            if (stream == null) {
                throw new IOException("Model file not found: " + modelPath);
            }
            return stream;
        }
    }

    /**
     * Classifies the intent of a given query using ML models and pattern matching
     * @param query The input query to classify
     * @return ParsedQuery object containing the classified intent and extracted entities
     */
    public ParsedQuery classifyIntent(String query) {
        if (query == null || query.trim().isEmpty()) {
            ParsedQuery parsedQuery = new ParsedQuery();
            parsedQuery.setQueryType(ParsedQuery.QueryType.UNKNOWN);
            parsedQuery.setConfidence(0.0);
            return parsedQuery;
        }

        String normalizedQuery = query.toLowerCase().trim();

        // Tokenize the query
        String[] tokens = tokenizer.tokenize(normalizedQuery);

        // Get POS tags
        String[] posTags = posTagger.tag(tokens);

        // Extract entities and features
        Map<String, Object> entities = extractEntities(tokens, posTags, normalizedQuery);

        // Classify intent using multiple approaches
        String intentString = classifyUsingPatterns(normalizedQuery, tokens);
        double confidence = calculateConfidence(intentString, tokens, normalizedQuery);

        // Create and populate ParsedQuery
        ParsedQuery parsedQuery = new ParsedQuery();
        parsedQuery.setQueryType(mapStringToQueryType(intentString));
        parsedQuery.setActionType(determineActionType(tokens, intentString));
        parsedQuery.setConfidence(confidence);

        // Extract and set specific entities
        populateEntities(parsedQuery, entities, tokens, normalizedQuery);

        return parsedQuery;
    }

    private ParsedQuery.QueryType mapStringToQueryType(String intentString) {
        switch (intentString) {
        case "CONTRACT_INFO":
            return ParsedQuery.QueryType.CONTRACT_INFO;
        case "PARTS_INFO":
            return ParsedQuery.QueryType.PARTS_INFO;
        case "STATUS_CHECK":
            return ParsedQuery.QueryType.STATUS_CHECK;
        case "CUSTOMER_INFO":
            return ParsedQuery.QueryType.CUSTOMER_INFO;
        case "HELP_CREATE":
            return ParsedQuery.QueryType.HELP_CREATE_CONTRACT;
        case "FAILED_PARTS":
            return ParsedQuery.QueryType.FAILED_PARTS;
        case "USER_CONTRACTS":
            return ParsedQuery.QueryType.USER_CONTRACT_QUERY;
        case "LIST_PARTS":
            return ParsedQuery.QueryType.LIST_PARTS;
        default:
            return ParsedQuery.QueryType.UNKNOWN;
        }
    }

    private ParsedQuery.ActionType determineActionType(String[] tokens, String intentString) {
        Set<String> tokenSet = new HashSet<>(Arrays.asList(tokens));

        // Check for specific action keywords
        if (tokenSet.contains("show") || tokenSet.contains("display")) {
            return ParsedQuery.ActionType.SHOW;
        } else if (tokenSet.contains("list")) {
            return ParsedQuery.ActionType.LIST;
        } else if (tokenSet.contains("info") || tokenSet.contains("information")) {
            return ParsedQuery.ActionType.INFO;
        } else if (tokenSet.contains("details")) {
            return ParsedQuery.ActionType.DETAILS;
        } else if (tokenSet.contains("status") || tokenSet.contains("check")) {
            return ParsedQuery.ActionType.CHECK_STATUS;
        } else if (tokenSet.contains("specifications") || tokenSet.contains("spec")) {
            return ParsedQuery.ActionType.GET_SPECIFICATIONS;
        } else if (tokenSet.contains("datasheet")) {
            return ParsedQuery.ActionType.GET_DATASHEET;
        } else if (tokenSet.contains("manufacturer")) {
            return ParsedQuery.ActionType.GET_MANUFACTURER;
        } else if (tokenSet.contains("stock")) {
            return ParsedQuery.ActionType.CHECK_STOCK;
        } else if (tokenSet.contains("compatible")) {
            return ParsedQuery.ActionType.GET_COMPATIBLE;
        } else if (tokenSet.contains("create") || tokenSet.contains("make") || tokenSet.contains("generate")) {
            return ParsedQuery.ActionType.CREATE;
        } else if (tokenSet.contains("help")) {
            return ParsedQuery.ActionType.HELP;
        } else if (tokenSet.contains("active")) {
            return ParsedQuery.ActionType.CHECK_ACTIVE;
        } else if (tokenSet.contains("warranty")) {
            return ParsedQuery.ActionType.GET_WARRANTY;
        } else if (tokenSet.contains("issues") || tokenSet.contains("problems")) {
            return ParsedQuery.ActionType.CHECK_ISSUES;
        }

        // Default actions based on intent
        switch (intentString) {
        case "CONTRACT_INFO":
            return ParsedQuery.ActionType.INFO;
        case "PARTS_INFO":
            return ParsedQuery.ActionType.GET_SPECIFICATIONS;
        case "STATUS_CHECK":
            return ParsedQuery.ActionType.CHECK_STATUS;
        case "CUSTOMER_INFO":
            return ParsedQuery.ActionType.INFO;
        case "HELP_CREATE":
            return ParsedQuery.ActionType.CREATE;
        case "FAILED_PARTS":
            return ParsedQuery.ActionType.CHECK_ISSUES;
        case "LIST_PARTS":
            return ParsedQuery.ActionType.LIST_PARTS;
        default:
            return ParsedQuery.ActionType.INFO;
        }
    }

    private void populateEntities(ParsedQuery parsedQuery, Map<String, Object> entities, String[] tokens,
                                  String query) {
        // Extract contract numbers
        if (entities.containsKey("contract_numbers")) {
            @SuppressWarnings("unchecked")
            List<String> contractNumbers = (List<String>) entities.get("contract_numbers");
            if (!contractNumbers.isEmpty()) {
                parsedQuery.setContractNumber(contractNumbers.get(0));
            }
        }

        // Extract part numbers
        if (entities.containsKey("part_numbers")) {
            @SuppressWarnings("unchecked")
            List<String> partNumbers = (List<String>) entities.get("part_numbers");
            if (!partNumbers.isEmpty()) {
                parsedQuery.setPartNumber(partNumbers.get(0));
            }
        }

        // Extract user names from proper nouns
        if (entities.containsKey("proper_nouns")) {
            @SuppressWarnings("unchecked")
            List<String> properNouns = (List<String>) entities.get("proper_nouns");
            if (!properNouns.isEmpty()) {
                // Try to determine if it's a user name or customer name based on context
                String firstProperNoun = properNouns.get(0);
                if (containsUserContext(tokens)) {
                    parsedQuery.setUserName(firstProperNoun);
                } else if (containsCustomerContext(tokens)) {
                    parsedQuery.setCustomerName(firstProperNoun);
                }
            }
        }

        // Extract status type
        String statusType = extractStatusType(tokens);
        if (statusType != null) {
            parsedQuery.setStatusType(statusType);
        }

        // Extract account numbers (could be similar to contract numbers but in different context)
        Pattern accountPattern = Pattern.compile("\\baccount\\s+(\\w+)\\b", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher accountMatcher = accountPattern.matcher(query);
        if (accountMatcher.find()) {
            parsedQuery.setAccountNumber(accountMatcher.group(1));
        }
    }

    private boolean containsUserContext(String[] tokens) {
        Set<String> userKeywords = new HashSet<>(Arrays.asList("user", "person", "employee", "rep", "representative"));
        for (String token : tokens) {
            if (userKeywords.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsCustomerContext(String[] tokens) {
        Set<String> customerKeywords = new HashSet<>(Arrays.asList("customer", "client", "company", "organization"));
        for (String token : tokens) {
            if (customerKeywords.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String extractStatusType(String[] tokens) {
        Set<String> statusTypes = new HashSet<>(Arrays.asList("active", "inactive", "expired", "pending", "cancelled"));
        for (String token : tokens) {
            if (statusTypes.contains(token)) {
                return token;
            }
        }
        return null;
    }

    private String classifyUsingPatterns(String query, String[] tokens) {
        // First, try context patterns (more specific)
        for (Map.Entry<String, Pattern> entry : contextPatterns.entrySet()) {
            if (entry.getValue()
                     .matcher(query)
                     .matches()) {
                return mapContextToIntent(entry.getKey());
            }
        }

        // Then try keyword-based classification
        Map<String, Integer> intentScores = new HashMap<>();

        for (String token : tokens) {
            for (Map.Entry<String, Set<String>> entry : intentPatterns.entrySet()) {
                if (entry.getValue().contains(token)) {
                    intentScores.put(entry.getKey(), intentScores.getOrDefault(entry.getKey(), 0) + 1);
                }
            }
        }

        // Return the intent with the highest score
        return intentScores.entrySet()
                           .stream()
                           .max(Map.Entry.comparingByValue())
                           .map(Map.Entry::getKey)
                           .orElse("UNKNOWN");
    }

    private String mapContextToIntent(String contextKey) {
        switch (contextKey) {
        case "CONTRACT_CONTEXT":
            return "CONTRACT_INFO";
        case "PARTS_CONTEXT":
            return "PARTS_INFO";
        case "STATUS_CONTEXT":
            return "STATUS_CHECK";
        case "FAILED_PARTS_CONTEXT":
            return "FAILED_PARTS";
        case "CUSTOMER_CONTEXT":
            return "CUSTOMER_INFO";
        case "HELP_CREATE_CONTEXT":
            return "HELP_CREATE";
        case "USER_CONTRACTS_CONTEXT":
            return "USER_CONTRACTS";
        default:
            return "UNKNOWN";
        }
    }

    private Map<String, Object> extractEntities(String[] tokens, String[] posTags, String query) {
        Map<String, Object> entities = new HashMap<>();

        // Extract contract numbers (6-digit patterns)
        Pattern contractPattern = Pattern.compile("\\b\\d{6}\\b");
        java.util.regex.Matcher contractMatcher = contractPattern.matcher(query);
        List<String> contractNumbers = new ArrayList<>();
        while (contractMatcher.find()) {
            contractNumbers.add(contractMatcher.group());
        }
        if (!contractNumbers.isEmpty()) {
            entities.put("contract_numbers", contractNumbers);
        }

        // Extract part numbers (alphanumeric patterns)
        Pattern partPattern = Pattern.compile("\\b[A-Z0-9]{3,}\\b");
        java.util.regex.Matcher partMatcher = partPattern.matcher(query.toUpperCase());
        List<String> partNumbers = new ArrayList<>();
        while (partMatcher.find()) {
            String part = partMatcher.group();
            // Filter out common words that might match the pattern
            if (!isCommonWord(part)) {
                partNumbers.add(part);
            }
        }
        if (!partNumbers.isEmpty()) {
            entities.put("part_numbers", partNumbers);
        }

        // Extract proper nouns (potential names, companies)
        List<String> properNouns = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (posTags[i].startsWith("NNP")) {
                properNouns.add(tokens[i]);
            }
        }
        if (!properNouns.isEmpty()) {
            entities.put("proper_nouns", properNouns);
        }

        // Extract numbers
        List<String> numbers = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (posTags[i].equals("CD")) {
                numbers.add(tokens[i]);
            }
        }
        if (!numbers.isEmpty()) {
            entities.put("numbers", numbers);
        }

        return entities;
    }

    private boolean isCommonWord(String word) {
        Set<String> commonWords =
            new HashSet<>(Arrays.asList("THE", "AND", "FOR", "ARE", "BUT", "NOT", "YOU", "ALL", "CAN", "HER", "WAS",
                                        "ONE", "OUR", "HAD", "BUT", "WHAT", "SO", "UP", "OUT", "IF", "ABOUT", "WHO",
                                        "GET", "WHICH", "GO", "ME"));
        return commonWords.contains(word);
    }

    private double calculateConfidence(String intent, String[] tokens, String query) {
        if ("UNKNOWN".equals(intent)) {
            return 0.0;
        }

        double confidence = 0.0;

        // Base confidence from keyword matches
        Set<String> intentKeywords = intentPatterns.get(intent);
        if (intentKeywords != null) {
            int matches = 0;
            for (String token : tokens) {
                if (intentKeywords.contains(token)) {
                    matches++;
                }
            }
            confidence = (double) matches / tokens.length;
        }

        // Boost confidence for context pattern matches
        for (Map.Entry<String, Pattern> entry : contextPatterns.entrySet()) {
            if (entry.getValue()
                     .matcher(query)
                     .matches() && intent.equals(mapContextToIntent(entry.getKey()))) {
                confidence = Math.max(confidence, 0.8);
                break;
            }
        }

        // Additional confidence boost for specific entity matches
        if (query.matches(".*\\b\\d{6}\\b.*")) { // Contains contract number
            confidence = Math.max(confidence, 0.7);
        }

        if (query.matches(".*\\b[A-Z0-9]{3,}\\b.*")) { // Contains part number
            confidence = Math.max(confidence, 0.6);
        }

        // Ensure confidence is between 0 and 1
        return Math.min(1.0, Math.max(0.0, confidence));
    }

    /**
     * Classifies intent with additional context for better accuracy
     * @param query The input query
     * @param previousQuery Previous query for context (can be null)
     * @return ParsedQuery with classified intent and entities
     */
    public ParsedQuery classifyIntentWithContext(String query, String previousQuery) {
        ParsedQuery result = classifyIntent(query);

        // If current query has low confidence and we have previous context
        if (result.getConfidence() < 0.5 && previousQuery != null && !previousQuery.trim().isEmpty()) {
            ParsedQuery previousResult = classifyIntent(previousQuery);

            // Inherit some context from previous query if it had high confidence
            if (previousResult.getConfidence() > 0.7) {
                if (result.getContractNumber() == null && previousResult.getContractNumber() != null) {
                    result.setContractNumber(previousResult.getContractNumber());
                }
                if (result.getPartNumber() == null && previousResult.getPartNumber() != null) {
                    result.setPartNumber(previousResult.getPartNumber());
                }
                if (result.getCustomerName() == null && previousResult.getCustomerName() != null) {
                    result.setCustomerName(previousResult.getCustomerName());
                }

                // Boost confidence slightly due to context
                result.setConfidence(Math.min(1.0, result.getConfidence() + 0.2));
            }
        }

        return result;
    }

    /**
     * Adds a new intent pattern to the classifier
     * @param intent The intent name
     * @param keywords Set of keywords associated with the intent
     */
    public void addIntentPattern(String intent, Set<String> keywords) {
        intentPatterns.put(intent, new HashSet<>(keywords));
    }

    /**
     * Adds a new context pattern to the classifier
     * @param contextKey The context key
     * @param pattern The regex pattern for this context
     */
    public void addContextPattern(String contextKey, Pattern pattern) {
        contextPatterns.put(contextKey, pattern);
    }

    /**
     * Gets all supported intents
     * @return Set of supported intent names
     */
    public Set<String> getSupportedIntents() {
        return new HashSet<>(intentPatterns.keySet());
    }

    /**
     * Gets all supported query types from the ParsedQuery enum
     * @return Set of supported QueryType values
     */
    public Set<ParsedQuery.QueryType> getSupportedQueryTypes() {
        return EnumSet.allOf(ParsedQuery.QueryType.class);
    }

    /**
     * Gets all supported action types from the ParsedQuery enum
     * @return Set of supported ActionType values
     */
    public Set<ParsedQuery.ActionType> getSupportedActionTypes() {
        return EnumSet.allOf(ParsedQuery.ActionType.class);
    }

    /**
     * Validates if a query can be processed
     * @param query The query to validate
     * @return true if query is valid for processing
     */
    public boolean isValidQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }

        // Check minimum length
        if (query.trim().length() < 3) {
            return false;
        }

        // Check if it contains at least one alphabetic character
        return query.matches(".*[a-zA-Z].*");
    }

    /**
     * Gets detailed classification results including all possible intents with scores
     * @param query The input query
     * @return Map of intent names to confidence scores
     */
    public Map<String, Double> getDetailedClassification(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new HashMap<>();
        }

        String normalizedQuery = query.toLowerCase().trim();
        String[] tokens = tokenizer.tokenize(normalizedQuery);

        Map<String, Double> results = new HashMap<>();

        // Calculate scores for each intent
        for (String intent : intentPatterns.keySet()) {
            double score = calculateConfidence(intent, tokens, normalizedQuery);
            results.put(intent, score);
        }

        return results;
    }

    /**
     * Cleanup resources
     */
    public void close() {
        // OpenNLP models don't require explicit cleanup, but this method
        // is provided for future extensibility
        tokenizer = null;
        posTagger = null;
        intentPatterns = null;
        contextPatterns = null;
    }
}

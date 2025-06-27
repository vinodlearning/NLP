package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class ChatbotNLPService {
    private static final String MODEL_BASE_PATH =
        "C:\\JDeveloper\\mywork\\ChatbotTest\\ViewController\\public_html\\models\\";

    // NLP Models
    private TokenizerME tokenizer;
    private SentenceDetectorME sentenceDetector;
    private POSTaggerME posTagger;
    private NameFinderME personNameFinder;
    private NameFinderME organizationNameFinder;
    private NameFinderME locationNameFinder;
    private ChunkerME chunker;
    private DictionaryLemmatizer lemmatizer;

    // Intent keywords
    private Map<String, Set<String>> intentKeywords;
    private Map<String, String> commonTypos;

    public ChatbotNLPService() {
        initializeModels();
        initializeIntentKeywords();
        initializeTypoCorrections();
    }

    private void initializeModels() {
        Map<String, String> modelFiles = new HashMap<>();
        modelFiles.put("tokenizer", "en-token.bin");
        modelFiles.put("pos", "en-pos-maxent.bin");
        modelFiles.put("sentence", "en-sent.bin");
        modelFiles.put("person", "en-ner-person.bin");
        modelFiles.put("organization", "en-ner-organization.bin");
        modelFiles.put("location", "en-ner-location.bin");
        modelFiles.put("chunker", "en-chunker.bin");
        modelFiles.put("lemmatizer", "en-lemmatizer.txt");

        try {
            // Load tokenizer
            TokenizerModel tokenizerModel = new TokenizerModel(getModelInputStream(modelFiles.get("tokenizer")));
            tokenizer = new TokenizerME(tokenizerModel);

            // Load sentence detector
            SentenceModel sentenceModel = new SentenceModel(getModelInputStream(modelFiles.get("sentence")));
            sentenceDetector = new SentenceDetectorME(sentenceModel);

            // Load POS tagger
            POSModel posModel = new POSModel(getModelInputStream(modelFiles.get("pos")));
            posTagger = new POSTaggerME(posModel);

            // Load NER models
            TokenNameFinderModel personModel = new TokenNameFinderModel(getModelInputStream(modelFiles.get("person")));
            personNameFinder = new NameFinderME(personModel);

            TokenNameFinderModel orgModel =
                new TokenNameFinderModel(getModelInputStream(modelFiles.get("organization")));
            organizationNameFinder = new NameFinderME(orgModel);

            TokenNameFinderModel locModel = new TokenNameFinderModel(getModelInputStream(modelFiles.get("location")));
            locationNameFinder = new NameFinderME(locModel);

            // Load chunker
            ChunkerModel chunkerModel = new ChunkerModel(getModelInputStream(modelFiles.get("chunker")));
            chunker = new ChunkerME(chunkerModel);

            // Load lemmatizer
            lemmatizer = new DictionaryLemmatizer(getModelInputStream(modelFiles.get("lemmatizer")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load NLP models", e);
        }
    }

    private void initializeIntentKeywords() {
        intentKeywords = new HashMap<>();

        // Contract keywords
        intentKeywords.put("CONTRACT_INFO",
                           new HashSet<>(Arrays.asList("show", "contract", "details", "info", "find", "get",
                                                       "display")));

        // Parts keywords
        intentKeywords.put("PARTS_INFO",
                           new HashSet<>(Arrays.asList("parts", "part", "specifications", "spec", "datasheet",
                                                       "compatible", "stock", "lead", "time", "manufacturer", "issues",
                                                       "defects", "warranty", "active", "discontinued")));

        // Status keywords
        intentKeywords.put("STATUS_CHECK",
                           new HashSet<>(Arrays.asList("status", "expired", "active", "check", "state")));

        // Customer keywords
        intentKeywords.put("CUSTOMER_INFO",
                           new HashSet<>(Arrays.asList("customer", "account", "boeing", "honeywell", "client")));

        // Help keywords
        intentKeywords.put("HELP_CREATE", new HashSet<>(Arrays.asList("create", "help", "how", "new", "make", "want")));

        // Failed parts keywords
        intentKeywords.put("FAILED_PARTS",
                           new HashSet<>(Arrays.asList("failed", "failure", "error", "defective", "broken")));
    }

    private void initializeTypoCorrections() {
        commonTypos = new HashMap<>();
        commonTypos.put("cntrs", "contracts");
        commonTypos.put("cntr", "contract");
        commonTypos.put("shw", "show");
        commonTypos.put("contarct", "contract");
        commonTypos.put("contacrt", "contract");
        commonTypos.put("pasrt", "part");
        commonTypos.put("parst", "part");
        commonTypos.put("filed", "failed");
        commonTypos.put("crate", "create");
        commonTypos.put("contarctNume", "contract number");
    }

    public String processQuery(String userInput) {
        try {
            ParsedQuery parsedQuery = parseQuery(userInput);
            return executeQuery(parsedQuery);
        } catch (Exception e) {
            return "I'm sorry, I encountered an error processing your request: " + e.getMessage();
        }
    }

    public ParsedQuery parseQuery(String userInput) {
        ParsedQuery query = new ParsedQuery();
        query.setOriginalQuery(userInput);

        // Step 1: Enhanced spell checking
        String correctedInput = enhancedSpellCheck(userInput.toLowerCase());
        query.setCorrectedQuery(correctedInput);

        // Step 2: Sentence detection for multi-sentence queries
        String[] sentences = sentenceDetector.sentDetect(correctedInput);
        String primarySentence = sentences.length > 0 ? sentences[0] : correctedInput;

        // Step 3: Tokenize and analyze
        String[] tokens = tokenizer.tokenize(primarySentence);
        String[] posTags = posTagger.tag(tokens);

        // Step 4: Try lemmatization, fallback to simple version if it fails
        String[] lemmatizedTokens;
        try {
            lemmatizedTokens = lemmatizeTokens(tokens, posTags);
        } catch (Exception e) {
            System.err.println("Lemmatization failed, using simple version: " + e.getMessage());
            lemmatizedTokens = simpleLemmatizeTokens(tokens, posTags);
        }

        // Step 5: Extract entities
        extractEntities(query, tokens, correctedInput);

        // Step 6: Determine intent using lemmatized tokens
        determineIntentWithLemmas(query, lemmatizedTokens, posTags);

        // Step 7: Calculate enhanced confidence
        query.setConfidence(calculateEnhancedConfidence(query, tokens, posTags));

        return query;
    }
    private double calculateEnhancedConfidence(ParsedQuery query, String[] tokens, String[] posTags) {
        double confidence = 0.0;
        
        // Base confidence based on query type
        if (query.getQueryType() != QueryType.UNKNOWN) {
            confidence += 0.3; // Base confidence for recognized query type
        }
        
        // Boost confidence based on extracted entities
        if (query.getContractNumber() != null) {
            confidence += 0.25; // Contract number found
        }
        
        if (query.getPartNumber() != null) {
            confidence += 0.25; // Part number found
        }
        
        if (query.getCustomerName() != null) {
            confidence += 0.2; // Customer name found
        }
        
        if (query.getUserName() != null) {
            confidence += 0.2; // User name found
        }
        
        // Boost confidence based on keyword matches
        String tokenString = String.join(" ", tokens).toLowerCase();
        int keywordMatches = 0;
        int totalKeywords = 0;
        
        for (Set<String> keywords : intentKeywords.values()) {
            totalKeywords += keywords.size();
            for (String keyword : keywords) {
                if (tokenString.contains(keyword.toLowerCase())) {
                    keywordMatches++;
                }
            }
        }
        
        if (totalKeywords > 0) {
            double keywordScore = (double) keywordMatches / totalKeywords;
            confidence += keywordScore * 0.3; // Up to 30% boost from keyword matches
        }
        
        // Reduce confidence for very short queries
        if (tokens.length < 2) {
            confidence *= 0.7;
        }
        
        // Reduce confidence if no specific entities were found
        if (query.getContractNumber() == null && query.getPartNumber() == null && 
            query.getCustomerName() == null && query.getUserName() == null) {
            confidence *= 0.8;
        }
        
        // Ensure confidence is between 0 and 1
        return Math.min(1.0, Math.max(0.0, confidence));
    }

    
    private String[] lemmatizeTokens(String[] tokens, String[] posTags) {
        String[] lemmatizedTokens = new String[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            try {
                // Use the correct public method - lemmatize takes an array
                String[] lemmas = lemmatizer.lemmatize(new String[]{tokens[i]}, new String[]{posTags[i]});
                lemmatizedTokens[i] = (lemmas != null && lemmas.length > 0 && !lemmas[0].equals("O"))
                                      ? lemmas[0] : tokens[i];
            } catch (Exception e) {
                // Fallback to original token if lemmatization fails
                lemmatizedTokens[i] = tokens[i];
            }
        }

        return lemmatizedTokens;
    }

    private String[] simpleLemmatizeTokens(String[] tokens, String[] posTags) {
        String[] lemmatizedTokens = new String[tokens.length];

        // Simple rule-based lemmatization for common cases
        Map<String, String> commonLemmas = new HashMap<>();
        commonLemmas.put("contracts", "contract");
        commonLemmas.put("parts", "part");
        commonLemmas.put("customers", "customer");
        commonLemmas.put("specifications", "specification");
        commonLemmas.put("issues", "issue");
        commonLemmas.put("defects", "defect");
        commonLemmas.put("warranties", "warranty");
        commonLemmas.put("manufacturers", "manufacturer");
        commonLemmas.put("companies", "company");
        commonLemmas.put("systems", "system");
        commonLemmas.put("products", "product");
        commonLemmas.put("components", "component");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].toLowerCase();
            lemmatizedTokens[i] = commonLemmas.getOrDefault(token, tokens[i]);
        }

        return lemmatizedTokens;
    }

    private String correctTypos(String input) {
        String corrected = input;
        for (Map.Entry<String, String> typo : commonTypos.entrySet()) {
            corrected = corrected.replaceAll("\\b" + typo.getKey() + "\\b", typo.getValue());
        }
        return corrected;
    }
    private String enhancedSpellCheck(String input) {
        // First apply basic typo corrections
        String corrected = correctTypos(input);
        return corrected;
        
    }
    private void determineIntentWithLemmas(ParsedQuery query, String[] lemmatizedTokens, String[] posTags) {
        Map<String, Integer> intentScores = new HashMap<>();
        
        // Initialize all intent scores to 0
        for (String intent : intentKeywords.keySet()) {
            intentScores.put(intent, 0);
        }
        
        // Score each intent based on lemmatized tokens
        for (String token : lemmatizedTokens) {
            String lowerToken = token.toLowerCase();
            
            for (Map.Entry<String, Set<String>> entry : intentKeywords.entrySet()) {
                String intent = entry.getKey();
                Set<String> keywords = entry.getValue();
                
                if (keywords.contains(lowerToken)) {
                    intentScores.put(intent, intentScores.get(intent) + 1);
                }
            }
        }
        
        // Find the intent with the highest score
        String bestIntent = "UNKNOWN";
        int maxScore = 0;
        
        for (Map.Entry<String, Integer> entry : intentScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestIntent = entry.getKey();
            }
        }
        
        // Set the query type and action based on the determined intent
        setQueryTypeAndAction(query, bestIntent, lemmatizedTokens);
    }
    private void extractEntities(ParsedQuery query, String[] tokens, String input) {
        String upperInput = input.toUpperCase();
        String lowerInput = input.toLowerCase();

        // Enhanced entity extraction with context awareness
        extractNumbersWithContext(query, tokens, upperInput, lowerInput);

        // Don't extract person names for status queries
        if (!lowerInput.contains("expired contracts") && !lowerInput.contains("active contracts")) {
            // Extract person names
            Span[] personSpans = personNameFinder.find(tokens);
            if (personSpans.length > 0) {
                StringBuilder personName = new StringBuilder();
                for (int i = personSpans[0].getStart(); i < personSpans[0].getEnd(); i++) {
                    personName.append(tokens[i]).append(" ");
                }
                String extractedName = personName.toString().trim();
                // Check context to determine if it's a user name or customer name
                if (lowerInput.contains("customer " + extractedName.toLowerCase()) ||
                    lowerInput.contains("client " + extractedName.toLowerCase())) {
                    query.setCustomerName(extractedName);
                } else if (lowerInput.contains("contracts") && !isLikelyCompanyName(extractedName)) {
                    query.setCustomerName(extractedName);
                }
            }
        }

        // Enhanced organization extraction
        Span[] orgSpans = organizationNameFinder.find(tokens);
        if (orgSpans.length > 0) {
            StringBuilder orgName = new StringBuilder();
            for (int i = orgSpans[0].getStart(); i < orgSpans[0].getEnd(); i++) {
                orgName.append(tokens[i]).append(" ");
            }
            query.setCustomerName(orgName.toString().trim());
        }

        // Handle specific company names
        if (lowerInput.contains("boeing") || lowerInput.contains("honeywell")) {
            if (lowerInput.contains("boeing")) {
                query.setCustomerName("boeing");
            } else if (lowerInput.contains("honeywell")) {
                query.setCustomerName("honeywell");
            }
        }

        // Extract user names from contract queries - with better filtering
        if (lowerInput.contains("contracts") && !lowerInput.contains("customer") &&
            !lowerInput.contains("expired") && !lowerInput.contains("active") &&
            query.getCustomerName() == null) {

            // Pattern: "username contracts" or "contracts by username"
            if (lowerInput.matches(".*\\w+\\s+contracts.*")) {
                String[] words = lowerInput.split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    if (words[i + 1].equals("contracts") && words[i].length() > 2 &&
                        !isCommonWord(words[i]) && !isLikelyCompanyName(words[i])) {
                        query.setUserName(words[i]);
                        break;
                    }
                }
            } else if (lowerInput.contains("by ")) {
                String[] parts = lowerInput.split("by ");
                if (parts.length > 1) {
                    String userName = parts[1].trim().split("\\s+")[0];
                    if (userName.length() > 2 && !isCommonWord(userName)) {
                        query.setUserName(userName);
                    }
                }
            }
        }
    }

    private void extractNumbersWithContext(ParsedQuery query, String[] tokens, String upperInput, String lowerInput) {
        // Contract number pattern - exactly 6 digits
        Pattern contractPattern = Pattern.compile("\\b(\\d{6})\\b");
        // Part number pattern - alphanumeric (flexible length)
        Pattern partPattern = Pattern.compile("\\b([A-Z0-9]+)\\b");
        // Account number pattern - numeric only (flexible length, but typically longer than 6)
        Pattern accountPattern = Pattern.compile("\\b(\\d{7,})\\b");

        // Extract account numbers first (7+ digits)
        java.util.regex.Matcher accountMatcher = accountPattern.matcher(upperInput);
        if (accountMatcher.find()) {
            String accountNum = accountMatcher.group(1);
            // Only set as account if it's not exactly 6 digits (which would be contract)
            if (accountNum.length() != 6) {
                // Fixed: Add accountNumber field to ParsedQuery or use existing field
                // For now, using customerName to store account info
                query.setCustomerName("Account: " + accountNum);
                return; // Don't process as contract number
            }
}

        // Extract contract numbers (exactly 6 digits)
        java.util.regex.Matcher contractMatcher = contractPattern.matcher(upperInput);
        if (contractMatcher.find()) {
            query.setContractNumber(contractMatcher.group(1));
        }

        // Extract part numbers (alphanumeric)
        java.util.regex.Matcher partMatcher = partPattern.matcher(upperInput);
        while (partMatcher.find()) {
            String partNum = partMatcher.group(1);
            // Skip if it's purely numeric (likely contract/account number)
            if (!partNum.matches("\\d+") && partNum.length() >= 2) {
                // Check context to confirm it's a part number
                if (hasPartContext(lowerInput, partNum.toLowerCase()) ||
                     (!lowerInput.contains("contract") && !lowerInput.contains("account"))) {
                    query.setPartNumber(partNum);
                    break;
                }
            }
        }

        // Handle "contracts 123456" pattern specifically
        Pattern contractsPattern = Pattern.compile("contracts?\\s+(\\d{6})\\b", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher contractsMatcher = contractsPattern.matcher(lowerInput);
        if (contractsMatcher.find()) {
            query.setContractNumber(contractsMatcher.group(1));
        }

        // Handle "parts of/for ABC123" pattern
        Pattern partsOfPattern = Pattern.compile("parts\\s+(?:of|for)\\s+([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher partsOfMatcher = partsOfPattern.matcher(upperInput);
        if (partsOfMatcher.find()) {
            String identifier = partsOfMatcher.group(1);
            // If it's 6 digits, it's a contract number
            if (identifier.matches("\\d{6}")) {
                query.setContractNumber(identifier);
            } else {
                query.setPartNumber(identifier);
            }
        }

        // Handle "failed parts of/for ABC123" pattern
        Pattern failedPartsOfPattern = Pattern.compile("failed\\s+parts\\s+(?:of|for)\\s+([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher failedPartsOfMatcher = failedPartsOfPattern.matcher(upperInput);
        if (failedPartsOfMatcher.find()) {
            String identifier = failedPartsOfMatcher.group(1);
            // If it's 6 digits, it's a contract number
            if (identifier.matches("\\d{6}")) {
                query.setContractNumber(identifier);
            } else {
                query.setPartNumber(identifier);
            }
        }
    }

    private boolean hasPartContext(String input, String pattern) {
        String[] partContextPhrases = {
            "part " + pattern, "part number " + pattern, "component " + pattern, "product " + pattern,
            pattern + " part", pattern + " component", "specifications of " + pattern, "datasheet for " + pattern,
            "manufacturer of " + pattern, "warranty for " + pattern
        };
        for (String phrase : partContextPhrases) {
            if (input.contains(phrase)) {
                return true;
            }
        }
        return false;
    }

    private void setQueryTypeAndAction(ParsedQuery query, String intent, String[] tokens) {
        String tokenString = String.join(" ", tokens).toLowerCase();

        // Check for contract status queries first
        if ((tokenString.equals("active contracts") || tokenString.equals("expired contracts")) ||
            (tokenString.contains("contracts") && (tokenString.contains("expired") || tokenString.contains("active")))) {
            query.setQueryType(QueryType.CONTRACT_STATUS_CHECK);
            query.setActionType(ActionType.CHECK_STATUS);
            if (tokenString.contains("expired")) {
                query.setStatusType("expired");
            } else if (tokenString.contains("active")) {
                query.setStatusType("active");
            }
            // Clear any incorrectly extracted fields
            query.setContractNumber(null);
            query.setCustomerName(null);
            return;
        }

        // Check for customer queries
        if (query.getCustomerName() != null) {
            query.setQueryType(QueryType.CUSTOMER_INFO);
            query.setActionType(ActionType.INFO);
            query.setContractNumber(null); // Clear contract number for customer queries
            return;
        }

        // Check for user contract queries
        if (tokenString.contains("contracts") && query.getUserName() != null) {
            query.setQueryType(QueryType.USER_CONTRACT_QUERY);
            query.setActionType(ActionType.LIST);
            query.setContractNumber(null); // Clear contract number for user queries
            return;
        }

        // Check for help create contract
        if (tokenString.contains("create contract") || tokenString.contains("help create") ||
             tokenString.contains("how to create")) {
            query.setQueryType(QueryType.HELP_CREATE_CONTRACT);
            query.setActionType(ActionType.CREATE);
            query.setContractNumber(null); // Clear contract number for help queries
            return;
        }

        switch (intent) {
            case "CONTRACT_INFO":
                query.setQueryType(QueryType.CONTRACT_INFO);
                if (tokenString.contains("show") || tokenString.contains("display")) {
                    query.setActionType(ActionType.SHOW);
                } else if (tokenString.contains("details")) {
                    query.setActionType(ActionType.DETAILS);
                } else {
                    query.setActionType(ActionType.INFO);
                }
                break;
            case "PARTS_INFO":
                if (tokenString.contains("failed parts of") || tokenString.contains("failed parts for")) {
                    query.setQueryType(QueryType.FAILED_PARTS_BY_CONTRACT);
                    query.setActionType(ActionType.LIST);
                } else if (tokenString.equals("failed parts")) {
                    query.setQueryType(QueryType.FAILED_PARTS);
                    query.setActionType(ActionType.LIST);
                    query.setContractNumber(null); // Clear contract number for general failed parts
                } else if ((tokenString.contains("parts of") || tokenString.contains("parts for") ||
                            tokenString.contains("show me the parts") || tokenString.contains("list the parts") ||
                           tokenString.contains("how many parts")) && !tokenString.contains("compatible")) {
                    query.setQueryType(QueryType.PARTS_BY_CONTRACT);
                    query.setActionType(ActionType.LIST);
                } else {
                    query.setQueryType(QueryType.PARTS_INFO);
                    determinePartsAction(query, tokenString);
                    query.setContractNumber(null); // Clear contract number for parts info queries
                }
                break;
            case "STATUS_CHECK":
                if (query.getContractNumber() != null) {
                    query.setQueryType(QueryType.STATUS_CHECK);
                    query.setActionType(ActionType.CHECK_STATUS);
                } else {
                    query.setQueryType(QueryType.CONTRACT_STATUS_CHECK);
                    query.setActionType(ActionType.CHECK_STATUS);
                    if (tokenString.contains("expired")) {
                        query.setStatusType("expired");
                    } else if (tokenString.contains("active")) {
                        query.setStatusType("active");
                    }
                }
                break;
            case "CUSTOMER_INFO":
                query.setQueryType(QueryType.CUSTOMER_INFO);
                query.setActionType(ActionType.INFO);
                break;
            case "HELP_CREATE":
                query.setQueryType(QueryType.HELP_CREATE_CONTRACT);
                query.setActionType(ActionType.CREATE);
                break;
            case "FAILED_PARTS":
                if (query.getContractNumber() != null) {
                    query.setQueryType(QueryType.FAILED_PARTS_BY_CONTRACT);
                } else {
                    query.setQueryType(QueryType.FAILED_PARTS);
                    query.setContractNumber(null); // Clear contract number
                }
                query.setActionType(ActionType.LIST);
                break;
            default:
                query.setQueryType(QueryType.UNKNOWN);
                query.setActionType(ActionType.INFO);
        }
    }

    private void determinePartsAction(ParsedQuery query, String tokenString) {
        if (tokenString.contains("specifications") || tokenString.contains("spec")) {
            query.setActionType(ActionType.GET_SPECIFICATIONS);
        } else if (tokenString.contains("active") || tokenString.contains("discontinued")) {
            query.setActionType(ActionType.CHECK_ACTIVE);
        } else if (tokenString.contains("datasheet")) {
            query.setActionType(ActionType.GET_DATASHEET);
        } else if (tokenString.contains("compatible")) {
            query.setActionType(ActionType.GET_COMPATIBLE);
        } else if (tokenString.contains("stock")) {
            query.setActionType(ActionType.CHECK_STOCK);
        } else if (tokenString.contains("lead") && tokenString.contains("time")) {
            query.setActionType(ActionType.GET_LEAD_TIME);
        } else if (tokenString.contains("manufacturer")) {
            query.setActionType(ActionType.GET_MANUFACTURER);
        } else if (tokenString.contains("issues") || tokenString.contains("defects")) {
            query.setActionType(ActionType.CHECK_ISSUES);
        } else if (tokenString.contains("warranty")) {
            query.setActionType(ActionType.GET_WARRANTY);
        } else {
            query.setActionType(ActionType.INFO);
        }
    }

    private String executeQuery(ParsedQuery query) {
        switch (query.getQueryType()) {
            case CONTRACT_INFO:
                return handleContractInfo(query);
            case USER_CONTRACT_QUERY:
                return handleUserContractQuery(query);
            case STATUS_CHECK:
                return handleStatusCheck(query);
            case CONTRACT_STATUS_CHECK:
                return handleContractStatusCheck(query);
            case CUSTOMER_INFO:
                return handleCustomerInfo(query);
            case PARTS_INFO:
                return handlePartsInfo(query);
            case PARTS_BY_CONTRACT:
                return handlePartsByContract(query);
            case FAILED_PARTS:
                return handleFailedParts(query);
            case FAILED_PARTS_BY_CONTRACT:
                return handleFailedPartsByContract(query);
            case HELP_CREATE_CONTRACT:
                return handleCreateContractHelp(query);
            case UNKNOWN:
                return "? I'm sorry, I didn't understand your request. Could you please rephrase it?\n\n" +
                       "? Try asking:\n" + "? 'Show contract 123456'\n" + "? 'Parts for contract 789012'\n" +
                       "? 'Status of part ABC123'\n" + "? 'Customer info for Boeing'";
            default:
                return "? I'm sorry, I didn't understand your request. Could you please rephrase it?";
        }
    }

    // Handler methods for different query types
    private String handleContractInfo(ParsedQuery query) {
        if (query.getContractNumber() != null) {
            return ChatModelServiceImpl.contractByContractNumber(query.getContractNumber());
        }
        return "Please provide a contract number to get contract information.";
    }

    private String handleUserContractQuery(ParsedQuery query) {
        if (query.getUserName() != null) {
            return ChatModelServiceImpl.contractByUser(query.getUserName());
        }
        return "Please specify a user name to search for contracts.";
    }

    private String handleStatusCheck(ParsedQuery query) {
        if (query.getContractNumber() != null) {
            return ChatModelServiceImpl.checkContractStatus(query.getContractNumber());
        } else if (query.getStatusType() != null) {
            return ChatModelServiceImpl.getContractsByStatus(query.getStatusType());
        }
        return "Please specify a contract number or status type.";
    }

    private String handleContractStatusCheck(ParsedQuery query) {
        if (query.getStatusType() != null) {
            return ChatModelServiceImpl.getContractsByStatus(query.getStatusType());
        }
        return "Please specify a status type (active or expired).";
    }

    private String handleCustomerInfo(ParsedQuery query) {
        if (query.getCustomerName() != null) {
            return ChatModelServiceImpl.getCustomerContracts(query.getCustomerName());
        }
        return "Please specify a customer name or account number.";
    }

    private String handlePartsInfo(ParsedQuery query) {
        if (query.getPartNumber() == null) {
            return "Please specify a part number.";
        }
        switch (query.getActionType()) {
            case GET_SPECIFICATIONS:
                return ChatModelServiceImpl.getPartSpecifications(query.getPartNumber());
            case CHECK_ACTIVE:
                return ChatModelServiceImpl.checkPartStatus(query.getPartNumber());                   
            case GET_LEAD_TIME:
                return ChatModelServiceImpl.getPartLeadTime(query.getPartNumber());          
            case CHECK_ISSUES:
                return getPartIssues(query.getPartNumber());           
            default:
                return ChatModelServiceImpl.pullPartsInfoByPartsNumber(query.getPartNumber());
        }
    }

    private String handlePartsByContract(ParsedQuery query) {
        if (query.getContractNumber() != null) {
            return ChatModelServiceImpl.pullPartsByContract(query.getContractNumber());
        }
        return "Please specify a contract number to get parts information.";
    }

    private String handleFailedParts(ParsedQuery query) {
        if (query.getContractNumber() != null) {
            return ChatModelServiceImpl.failedPartsByContract(query.getContractNumber());
        } else if (query.getPartNumber() != null) {
            return ChatModelServiceImpl.isPartsFailed(query.getPartNumber());
        } else {
            return ChatModelServiceImpl.failedParts();
        }
    }

    private String handleFailedPartsByContract(ParsedQuery query) {
        if (query.getContractNumber() != null) {
            return ChatModelServiceImpl.failedPartsByContract(query.getContractNumber());
        }
        return "Please specify a contract number to get failed parts information.";
    }

    private String handleCreateContractHelp(ParsedQuery query) {
        return ChatModelServiceImpl.createContractHelp();
    }


    private String getPartIssues(String string) {
        return null;
    }
    private InputStream getModelInputStream(String fileName) {
            if (fileName != null) {
                try {
                    String fullPath = MODEL_BASE_PATH + fileName;
                    File modelFile = new File(fullPath);
                    System.out.println("=== Diagnosing: " + fileName + " ===");
                    System.out.println("Exists: " + modelFile.exists());
                    System.out.println("Size: " + modelFile.length() + " bytes");
                    System.out.println("Readable: " + modelFile.canRead());

                    // Verify file before opening
                    if (!modelFile.exists()) {
                        System.err.println("File does not exist: " + fullPath);
                        return null;
                    }
                    if (!modelFile.canRead()) {
                        System.err.println("Cannot read file: " + fullPath);
                        return null;
                    }
                    if (modelFile.length() == 0) {
                        System.err.println("File is empty: " + fullPath);
                        return null;
                    }

                    return new FileInputStream(modelFile);
                } catch (Exception e) {
                    System.err.println("Error opening file " + fileName + ": " + e.getMessage());
                    return null;
                }
            }
            return null;
        }
    private boolean isCommonWord(String word) {
            // List of common words that should not be considered as user names
            String[] commonWords = {
                "the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by",
                "from", "up", "about", "into", "through", "during", "before", "after", "above",
                "below", "between", "among", "under", "over", "within", "without", "against",
                "show", "get", "find", "search", "look", "see", "view", "display", "list",
                "tell", "give", "provide", "fetch", "retrieve", "pull", "check", "verify",
                "contract", "contracts", "part", "parts", "customer", "user", "account",
                "number", "info", "information", "details", "status", "failed", "active",
                "expired", "help", "create", "how", "what", "when", "where", "why", "who",
                "is", "are", "was", "were", "be", "been", "being", "have", "has", "had",
                "do", "does", "did", "will", "would", "could", "should", "may", "might",
                "can", "must", "shall", "this", "that", "these", "those", "a", "an",
                "all", "any", "some", "many", "much", "few", "little", "more", "most",
                "other", "another", "such", "no", "not", "only", "own", "same", "so",
                "than", "too", "very", "just", "now", "here", "there", "then", "once",
                "again", "also", "still", "back", "down", "out", "off", "away", "around",
                "because", "if", "when", "where", "how", "what", "which", "who", "whom",
                "whose", "why", "whether", "although", "though", "unless", "until", "while",
                "since", "as", "like", "than", "except", "besides", "instead", "rather",
                "me", "my", "mine", "you", "your", "yours", "he", "his", "him", "she",
                "her", "hers", "it", "its", "we", "our", "ours", "us", "they", "their",
                "theirs", "them", "i", "myself", "yourself", "himself", "herself", "itself",
                "ourselves", "yourselves", "themselves"
            };
            
            String lowerWord = word.toLowerCase();
            for (String common : commonWords) {
                if (common.equals(lowerWord)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isLikelyCompanyName(String word) {
            // Check if the word is likely a company name based on common patterns
            String upperWord = word.toUpperCase();
            
            // Common company suffixes
            String[] companySuffixes = {
                "INC", "CORP", "LLC", "LTD", "CO", "COMPANY", "CORPORATION", 
                "INCORPORATED", "LIMITED", "GROUP", "SYSTEMS", "TECHNOLOGIES",
                "TECH", "SOLUTIONS", "SERVICES", "INDUSTRIES", "MANUFACTURING",
                "MFG", "INTERNATIONAL", "INTL", "GLOBAL", "WORLDWIDE", "ENTERPRISES"
            };
            
            // Check if word ends with company suffix
            for (String suffix : companySuffixes) {
                if (upperWord.endsWith(suffix)) {
                    return true;
                }
            }
            
            // Common well-known company names that might appear in queries
            String[] knownCompanies = {
                "BOEING", "AIRBUS", "LOCKHEED", "MARTIN", "RAYTHEON", "NORTHROP",
                "GRUMMAN", "GENERAL", "ELECTRIC", "HONEYWELL", "COLLINS", "ROCKWELL",
                "PRATT", "WHITNEY", "ROLLS", "ROYCE", "SAFRAN", "THALES", "BAE",
                "LEONARDO", "SAAB", "EMBRAER", "BOMBARDIER", "TEXTRON", "BELL",
                "SIKORSKY", "CESSNA", "BEECHCRAFT", "PIPER", "CIRRUS", "DIAMOND",
                "MOONEY", "SOCATA", "EXTRA", "PILATUS", "DAHER", "QUEST", "AVIAT",
                "AMERICAN", "CHAMPION", "MAULE", "AVIONS", "ROBIN", "TECNAM",
                "PIPISTREL", "EVEKTOR", "CZECH", "SPORT", "FLIGHT", "DESIGN",
                "MICROSOFT", "APPLE", "GOOGLE", "AMAZON", "IBM", "ORACLE", "SAP",
                "CISCO", "INTEL", "AMD", "NVIDIA", "QUALCOMM", "BROADCOM", "TEXAS",
                "INSTRUMENTS", "ANALOG", "DEVICES", "MAXIM", "INTEGRATED", "LINEAR",
                "TECHNOLOGY", "INFINEON", "STMICROELECTRONICS", "NXPI", "MICROCHIP"
            };
            
            for (String company : knownCompanies) {
                if (upperWord.equals(company) || upperWord.contains(company)) {
                    return true;
                }
            }
            
            // Check if it's an acronym (all caps, 2-6 characters)
            if (upperWord.matches("^[A-Z]{2,6}$") && !upperWord.matches("^[A-Z]{6}$")) {
                // 6-character all caps might be contract numbers, so exclude them
                return true;
            }
            
            // Check if it contains numbers (likely part numbers or contract numbers, not user names)
            if (word.matches(".*\\d.*")) {
                return false; // Contains numbers, probably not a user name
            }
            
            // Check if it's all uppercase and longer than 3 characters (likely company/product name)
            if (upperWord.equals(word) && word.length() > 3) {
                return true;
            }
            
            return false;
        }
}

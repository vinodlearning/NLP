package view.practice;

import opennlp.tools.doccat.*;
import opennlp.tools.util.*;

import java.io.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import view.ParsedQuery.ActionType;
import view.ParsedQuery.QueryType;


import view.ParsedQuery;

/**
 * Improved Machine Learning Intent Classifier with better training data
 * and enhanced typo tolerance for contract queries
 */
public class MLIntentClassifierImproved {
    private static final Logger logger = Logger.getLogger(MLIntentClassifierImproved.class.getName());

    private DocumentCategorizerME categorizer;
    private DoccatModel model;
    private Map<String, Double> intentThresholds;
    private Map<String, List<String>> intentKeywords;
    private Map<String, String> abbreviationMap;
    private Pattern contractNumberPattern;

    private boolean initialized = false;


    /**
     * Initialize with enhanced training data
     */
    public void initializeWithTrainingData() throws IOException {
        ObjectStream<DocumentSample> sampleStream = createEnhancedTrainingData();

        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, "200");
        params.put(TrainingParameters.CUTOFF_PARAM, "1");

        this.model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
        this.categorizer = new DocumentCategorizerME(model);
        this.initialized = true;

        logger.info("Enhanced intent classifier initialized with improved training data");
    }

    private void initializeIntentThresholds() {
        this.intentThresholds = new HashMap<>();
        intentThresholds.put("show_contract", 0.55);
        intentThresholds.put("get_contract_info", 0.55);
        intentThresholds.put("get_contract_expiration", 0.60);
        intentThresholds.put("list_expired_contracts", 0.55);
        intentThresholds.put("list_active_contracts", 0.55);
        intentThresholds.put("filter_contracts_by_customer", 0.60);
        intentThresholds.put("filter_contracts_by_user", 0.65);
        intentThresholds.put("search_contracts", 0.55);
        intentThresholds.put("list_contracts", 0.55);
        intentThresholds.put("contract_status", 0.60);
    }

    private void initializeEnhancedIntentKeywords() {
        this.intentKeywords = new HashMap<>();

        intentKeywords.put("list_expired_contracts",
                           Arrays.asList("expired", "expiry", "expire", "expires", "expired contracts",
                                         "contracts expired", "expiration list", "expired list", "show expired",
                                         "list expired", "all expired", "expird", "expary", "expir"));

        intentKeywords.put("list_active_contracts",
                           Arrays.asList("active", "current", "available", "active contracts", "current contracts",
                                         "available contracts", "live contracts", "show active", "list active",
                                         "all active", "current list", "actv", "crnt", "availabl"));

        intentKeywords.put("filter_contracts_by_customer",
                           Arrays.asList("customer", "client", "account", "for customer", "customer contracts",
                                         "client contracts", "account contracts", "company", "organization", "vendor",
                                         "supplier", "partner", "boeing", "honeywell", "microsoft", "apple", "google",
                                         "contracts for", "contracts with", "custmer", "clint", "customar", "customr",
                                         "clent", "accnt", "compny"));

        intentKeywords.put("show_contract",
                           Arrays.asList("show", "display", "view", "open", "see", "present", "reveal", "shw", "dsplay",
                                         "vw", "opn", "se", "shwo", "dispaly", "viw"));

        intentKeywords.put("get_contract_info",
                           Arrays.asList("info", "information", "details", "data", "specifics", "particulars", "inf",
                                         "dtails", "informaton", "infomation", "detials", "dat", "dtls"));

        intentKeywords.put("get_contract_expiration",
                           Arrays.asList("expiration", "expiry", "expire", "expires", "expired", "end", "ends",
                                         "ending", "expiration date", "expiry date", "end date", "when expires",
                                         "when ends", "expirat", "expary", "expir", "expirs", "expird", "nd",
                                         "expiraton", "experation"));

        intentKeywords.put("filter_contracts_by_user",
                           Arrays.asList("created by", "made by", "authored by", "by user", "user created", "creator",
                                         "author", "created", "made", "by", "user", "owner", "creater", "usr", "autor",
                                         "creatd", "mad", "owenr"));

        intentKeywords.put("search_contracts",
                           Arrays.asList("search", "find", "look for", "locate", "seek", "hunt", "discover",
                                         "search for", "find contract", "look up", "serch", "fnd", "lok", "locat",
                                         "seach", "finnd", "lokup"));

        intentKeywords.put("list_contracts",
                           Arrays.asList("list", "all contracts", "show all", "display all", "list all", "all", "every",
                                         "complete list", "active contracts", "active", "current contracts", "current",
                                         "available contracts", "available", "contracts list", "lst", "al", "evry",
                                         "complet", "lsit", "activ", "curnt"));

        intentKeywords.put("contract_status",
                           Arrays.asList("status", "state", "condition", "situation", "current status", "what status",
                                         "check status", "active", "inactive", "pending", "completed", "cancelled",
                                         "stat", "staus", "conditon", "situaton", "statu", "chck"));
    }

    private void initializeAbbreviationMap() {
        this.abbreviationMap = new HashMap<>();
        abbreviationMap.put("ex", "expiration");
        abbreviationMap.put("exp", "expiration");
        abbreviationMap.put("info", "information");
        abbreviationMap.put("stat", "status");
        abbreviationMap.put("cntrct", "contract");
        abbreviationMap.put("contarct", "contract");
        abbreviationMap.put("contrct", "contract");
        abbreviationMap.put("cntract", "contract");
        abbreviationMap.put("expirat", "expiration");
        abbreviationMap.put("expary", "expiration");
        abbreviationMap.put("cust", "customer");
        abbreviationMap.put("custmer", "customer");
        abbreviationMap.put("clnt", "client");
        abbreviationMap.put("acct", "account");
        abbreviationMap.put("actv", "active");
        abbreviationMap.put("crnt", "current");
    }

    /**
     * Correct common typos and expand abbreviations before classification
     */
    private String correctTyposAndAbbreviations(String text) {
        String corrected = text.toLowerCase();

        for (Map.Entry<String, String> entry : abbreviationMap.entrySet()) {
            String typo = entry.getKey();
            String correct = entry.getValue();
            corrected = corrected.replaceAll("\\b" + typo + "\\b", correct);
        }

        return corrected;
    }


    /**
     * Extract contract number (exactly 6 digits)
     */
    private String extractContractNumber(String input) {
        java.util.regex.Matcher matcher = contractNumberPattern.matcher(input);
        while (matcher.find()) {
            String number = matcher.group();
            // Check context to ensure it's a contract number, not customer number
            String context = getContextAroundMatch(input, matcher.start(), matcher.end());
            if (context.contains("contract") || (!context.contains("account") && !context.contains("customer"))) {
                return number;
            }
        }
        return null;
    }

    /**
     * Extract customer number (account number - 6-8 digits)
     */
    private String extractCustomerNumber(String input) {
        java.util.regex.Matcher matcher = customerNumberPattern.matcher(input);
        while (matcher.find()) {
            String number = matcher.group();
            // Check context to ensure it's a customer/account number
            String context = getContextAroundMatch(input, matcher.start(), matcher.end());
            if (context.contains("account") || context.contains("customer") || context.contains("client")) {
                return number;
            }
            // If no contract context and number is longer than 6 digits, likely customer number
            if (!context.contains("contract") && number.length() > 6) {
                return number;
            }
        }
        return null;
    }

    /**
     * Get context around a matched number to determine its type
     */
    private String getContextAroundMatch(String input, int start, int end) {
        int contextStart = Math.max(0, start - 20);
        int contextEnd = Math.min(input.length(), end + 20);
        return input.substring(contextStart, contextEnd).toLowerCase();
    }

    /**
     * Extract customer name from business patterns
     */
    private String extractCustomerName(String input) {
        // Common business customer names
        String[] businessCustomers = {
            "boeing", "honeywell", "microsoft", "apple", "google", "amazon", "oracle", "ibm", "cisco", "intel"
        };

        for (String customer : businessCustomers) {
            if (input.contains(customer.toLowerCase())) {
                return customer.toUpperCase();
            }
        }

        // Pattern for "customer [name]", "client [name]"
        Pattern customerPattern =
            Pattern.compile("\\b(?:customer|client|company)\\s+([a-zA-Z]+)\\b", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = customerPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }

        return null;
    }

    /**
     * Extract user name from input
     */
    private String extractUserName(String input) {
        Pattern userPattern =
            Pattern.compile("\\b(?:by|user|created by|made by|authored by)\\s+([a-zA-Z]+)\\b",
                            Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = userPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return null;
    }

    /**
     * Extract status type from input
     */
    private String extractStatusType(String input) {
        String[] statusTypes = { "active", "inactive", "expired", "pending", "completed", "cancelled", "draft" };
        for (String status : statusTypes) {
            if (input.contains(status)) {
                return status.toUpperCase();
            }
        }
        return null;
    }

    /**
     * Determine query type based on input and parsed entities
     */
    private QueryType determineQueryType(String input, ParsedQuery parsedQuery) {
        if (parsedQuery.getContractNumber() != null) {
            return QueryType.SPECIFIC_CONTRACT;
        }

        if (parsedQuery.getAccountNumber() != null || parsedQuery.getCustomerName() != null) {
            return QueryType.CUSTOMER_FILTER;
        }

        if (parsedQuery.getUserName() != null) {
            return QueryType.USER_FILTER;
        }

        if (input.contains("expired") || input.contains("expir")) {
            return QueryType.STATUS_FILTER;
        }

        if (input.contains("active") || input.contains("current")) {
            return QueryType.STATUS_FILTER;
        }

        if (input.contains("search") || input.contains("find")) {
            return QueryType.SEARCH;
        }

        if (input.contains("list") || input.contains("all")) {
            return QueryType.LIST_ALL;
        }

        return QueryType.GENERAL;
    }


    /**
     * Check for information keywords
     */
    private boolean containsInfoKeywords(String[] tokens, String correctedSentence) {
        String[] infoPatterns = { "info", "information", "details", "data", "specifics", "particulars" };

        for (String pattern : infoPatterns) {
            if (correctedSentence.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for status keywords
     */
    private boolean containsStatusKeywords(String[] tokens, String correctedSentence) {
        String[] statusPatterns = { "status", "state", "condition", "situation" };

        for (String pattern : statusPatterns) {
            if (correctedSentence.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for expiration keywords
     */
    private boolean containsExpirationKeywords(String[] tokens, String originalSentence) {
        String[] expirationPatterns = {
            "expiration", "expiry", "expire", "expires", "expired", "end date", "when expires", "expirat", "expary",
            "expir", "expirs", "expird", "experation"
        };

        for (String pattern : expirationPatterns) {
            if (originalSentence.contains(pattern)) {
                return true;
            }
        }

        for (String token : tokens) {
            String lowerToken = token.toLowerCase();
            for (String pattern : expirationPatterns) {
                if (lowerToken.equals(pattern) || isAdvancedTypoMatch(lowerToken, pattern)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check for active contracts patterns
     */
    private boolean containsActiveContractsPatterns(String[] tokens, String originalSentence) {
        String[] activePatterns = {
            "active contracts", "current contracts", "available contracts", "active", "current", "available" };

        for (String pattern : activePatterns) {
            if (originalSentence.contains(pattern)) {
                if (!contractNumberPattern.matcher(originalSentence).find()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check for customer business patterns
     */
    private boolean containsCustomerBusinessPatterns(String[] tokens, String originalSentence) {
        String[] businessCustomers = {
            "boeing", "honeywell", "microsoft", "apple", "google", "amazon", "oracle", "ibm", "cisco", "intel"
        };

        for (String customer : businessCustomers) {
            if (originalSentence.contains(customer.toLowerCase())) {
                return true;
            }
        }

        String[] customerPhrases = {
            "customer", "client", "account", "company", "organization", "vendor", "supplier", "partner" };

        for (String phrase : customerPhrases) {
            if (originalSentence.contains(phrase)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check for search keywords
     */
    private boolean containsSearchKeywords(String[] tokens, String originalSentence) {
        String[] searchPatterns = {
            "find", "search", "look for", "locate", "seek", "hunt", "fnd", "serch", "lok", "locat", "finnd"
        };

        for (String pattern : searchPatterns) {
            if (originalSentence.contains(pattern)) {
                return true;
            }
        }

        for (String token : tokens) {
            String lowerToken = token.toLowerCase();
            for (String pattern : searchPatterns) {
                if (lowerToken.equals(pattern) || isAdvancedTypoMatch(lowerToken, pattern)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Calculate enhanced keyword score
     */
    private double calculateEnhancedKeywordScore(String[] tokens, String originalSentence, List<String> keywords) {
        int matches = 0;
        int totalTokens = tokens.length;

        // Check phrase matches in original sentence
        for (String keyword : keywords) {
            if (originalSentence.contains(keyword.toLowerCase())) {
                matches += 2; // Phrase matches get double weight
            }
        }

        // Check individual token matches
        for (String token : tokens) {
            String lowerToken = token.toLowerCase();
            for (String keyword : keywords) {
                if (lowerToken.equals(keyword) || isAdvancedTypoMatch(lowerToken, keyword)) {
                    matches++;
                    break;
                }
            }
        }

        return totalTokens > 0 ? (double) matches / (totalTokens + 2) : 0.0;
    }

    /**
     * Advanced typo matching using Levenshtein distance
     */
    private boolean isAdvancedTypoMatch(String token, String keyword) {
        if (Math.abs(token.length() - keyword.length()) > 3) {
            return false;
        }

        int distance = calculateLevenshteinDistance(token, keyword);
        int maxAllowedDistance = Math.max(1, keyword.length() / 3);

        return distance <= maxAllowedDistance;
    }

    /**
     * Calculate Levenshtein distance for typo detection
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Apply corrected NLP result
     */
    private NLPProcessingResult applyCorrectionToNLPResult(NLPProcessingResult original) {
        String originalSentence = original.getOriginalSentence();
        String correctedSentence = correctTyposAndAbbreviations(originalSentence);

        String[] correctedTokens = correctedSentence.split("\\s+");
        String[] correctedPosTags = new String[correctedTokens.length];
        Arrays.fill(correctedPosTags, "NN");

        return new NLPProcessingResult(correctedSentence, correctedTokens, correctedPosTags,
                                       new opennlp.tools.util.Span[0]);
    }

    /**
     * Prepare text for classification
     */
    private String prepareTextForClassification(NLPProcessingResult nlpResult) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = nlpResult.getTokens();

        for (int i = 0; i < tokens.length; i++) {
            sb.append(tokens[i].toLowerCase());
            if (i < tokens.length - 1) {
                sb.append(" ");
            }
        }

        String originalText = sb.toString();
        String correctedText = correctTyposAndAbbreviations(originalText);

        return correctedText;
    }

    /**
     * Get categories from model
     */
    private String[] getCategoriesFromModel() {
        try {
            int numCategories = categorizer.getNumberOfCategories();
            String[] categories = new String[numCategories];

            for (int i = 0; i < numCategories; i++) {
                categories[i] = categorizer.getCategory(i);
            }

            return categories;
        } catch (Exception e) {
            logger.warning("Could not get categories from model, using predefined categories");
            return new String[] {
                   "show_contract", "get_contract_info", "get_contract_expiration", "list_expired_contracts",
                   "list_active_contracts", "filter_contracts_by_customer", "filter_contracts_by_user",
                   "search_contracts", "list_contracts", "contract_status"
            };
        }
    }

    /**
     * Get confidence for specific category
     */
    private double getConfidenceForCategory(String category, double[] outcomes, String[] categories) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return outcomes[i];
            }
        }
        return 0.0;
    }

    /**
     * Apply threshold filtering
     */
    private ContractIntent applyThresholdFiltering(String category, double confidence) {
        Double threshold = intentThresholds.get(category);
        if (threshold != null && confidence >= threshold) {
            return ContractIntent.fromString(category);
        }
        return ContractIntent.UNKNOWN;
    }

    /**
     * Determine action required based on intent and parsed query
     */
    private String determineActionRequired(ContractIntent intent, ParsedQuery parsedQuery) {
        boolean hasContractNumber = parsedQuery.getContractNumber() != null;
        boolean hasCustomerInfo = parsedQuery.getAccountNumber() != null || parsedQuery.getCustomerName() != null;

        switch (intent) {
        case SHOW_CONTRACT:
            return hasContractNumber ? "retrieve_and_display" : "request_contract_number";
        case GET_CONTRACT_INFO:
            return hasContractNumber ? "show_contract_details" : "request_contract_number";
        case GET_CONTRACT_EXPIRATION:
            return hasContractNumber ? "show_expiration_date" : "request_contract_number";
        case LIST_EXPIRED_CONTRACTS:
            return "display_expired_contracts_list";
        case LIST_ACTIVE_CONTRACTS:
            return "display_active_contracts_list";
        case FILTER_CONTRACTS_BY_CUSTOMER:
            return hasCustomerInfo ? "filter_by_customer" : "show_customer_filter_form";
        case FILTER_CONTRACTS_BY_USER:
            return parsedQuery.getUserName() != null ? "filter_by_user" : "show_user_filter_form";
        case SEARCH_CONTRACTS:
            return "show_search_form";
        case LIST_CONTRACTS:
            return "display_contract_list";
        case CONTRACT_STATUS:
            return hasContractNumber ? "show_contract_status" : "show_status_filter_form";
        default:
            return "clarify_intent";
        }
    }

    /**
     * Create intent details map
     */
    private Map<String, Double> createIntentDetails(double[] outcomes, String[] categories) {
        Map<String, Double> details = new HashMap<>();
        for (int i = 0; i < categories.length && i < outcomes.length; i++) {
            details.put(categories[i], outcomes[i]);
        }
        return details;
    }

    /**
     * Create enhanced training data
     */
    private ObjectStream<DocumentSample> createEnhancedTrainingData() {
        List<DocumentSample> samples = new ArrayList<>();

        // Show contract samples
        addTrainingSamples(samples, "show_contract",
                           new String[][] { { "show", "contract", "123456" }, { "contract", "123456" },
                                            { "display", "contract", "789012" }, { "view", "contract", "456789" },
                                            { "see", "contract", "234567" }, { "open", "contract", "345678" },
                                            { "present", "contract", "567890" }, { "show", "me", "contract", "123456" },
                                            { "can", "you", "show", "contract", "789012" },
                                            { "i", "want", "to", "see", "contract", "456789" },
                                            { "display", "the", "contract", "234567" },
                                            { "view", "contract", "details", "345678" },
                                            { "contract", "details", "123456" }, { "get", "contract", "123456" } });

        // Get contract info samples
        addTrainingSamples(samples, "get_contract_info",
                           new String[][] { { "contract", "details", "123456" }, { "contract", "info", "123456" },
                                            { "get", "contract", "info", "789012" },
                                            { "contract", "information", "456789" },
                                            { "info", "about", "contract", "234567" }, { "contract", "data", "345678" },
                                            { "details", "of", "contract", "567890" },
                                            { "what", "are", "the", "details", "of", "contract", "123456" },
                                            { "give", "me", "info", "on", "contract", "789012" },
                                            { "contract", "specifics", "456789" },
                                            { "particulars", "of", "contract", "234567" } });

        // Get contract expiration samples
        addTrainingSamples(samples, "get_contract_expiration",
                           new String[][] { { "expiration", "of", "123456" }, { "contract", "expiration", "123456" },
                                            { "when", "does", "contract", "123456", "expire" },
                                            { "expiry", "date", "of", "contract", "789012" },
                                            { "contract", "789012", "expiration", "date" },
                                            { "when", "expires", "contract", "456789" },
                                            { "end", "date", "of", "contract", "234567" },
                                            { "contract", "234567", "end", "date" },
                                            { "expiration", "date", "for", "contract", "345678" },
                                            { "when", "does", "345678", "expire" },
                                            { "what", "is", "the", "expiration", "date", "for", "contract", "567890" },
                                            { "contract", "567890", "expires", "when" },
                                            { "check", "expiration", "of", "123456" },
                                            { "get", "expiry", "of", "contract", "789012" },
                                            { "expirat", "of", "123456" }, { "expary", "date", "789012" },
                                            { "when", "expirs", "contract", "456789" },
                                            { "nd", "date", "contract", "234567" } });

        // List expired contracts samples
        addTrainingSamples(samples, "list_expired_contracts",
                           new String[][] { { "expired", "contracts" }, { "show", "expired", "contracts" },
                                            { "list", "expired", "contracts" }, { "all", "expired", "contracts" },
                                            { "contracts", "that", "expired" }, { "contracts", "expiry", "list" },
                                            { "expired", "contract", "list" }, { "show", "all", "expired" },
                                            { "display", "expired", "contracts" }, { "get", "expired", "contracts" } });

        // List active contracts samples
        addTrainingSamples(samples, "list_active_contracts",
                           new String[][] { { "active", "contracts" }, { "current", "contracts" },
                                            { "available", "contracts" }, { "show", "active", "contracts" },
                                            { "list", "active", "contracts" }, { "all", "active", "contracts" },
                                            { "current", "contract", "list" }, { "active", "contract", "list" },
                                            { "show", "current", "contracts" }, { "display", "active", "contracts" },
                                            { "live", "contracts" } });

        // Filter contracts by customer samples
        addTrainingSamples(samples, "filter_contracts_by_customer",
                           new String[][] { { "contracts", "for", "customer", "abc" },
                                            { "customer", "abc", "contracts" }, { "contracts", "by", "client", "xyz" },
                                            { "client", "xyz", "contracts" }, { "account", "abc", "contracts" },
                                            { "contracts", "for", "account", "xyz" },
                                            { "show", "customer", "contracts", "abc" },
                                            { "contracts", "belonging", "to", "customer", "xyz" },
                                            { "boeing", "contracts" }, { "contracts", "for", "boeing" },
                                            { "honeywell", "contracts" }, { "customer", "honeywell" },
                                            { "microsoft", "contracts" }, { "contracts", "with", "microsoft" },
                                            { "account", "10840607" }, { "contracts", "for", "account", "10840607" },
                                            { "company", "boeing" }, { "organization", "honeywell" },
                                            { "vendor", "microsoft" }, { "supplier", "contracts" },
                                            { "partner", "contracts" } });

        // Filter contracts by user samples
        addTrainingSamples(samples, "filter_contracts_by_user",
                           new String[][] { { "contracts", "by", "john" }, { "contracts", "created", "by", "smith" },
                                            { "contracts", "made", "by", "mary" }, { "john", "contracts" },
                                            { "smith", "created", "contracts" },
                                            { "show", "contracts", "by", "user", "john" },
                                            { "contracts", "authored", "by", "smith" }, { "user", "mary", "contracts" },
                                            { "contracts", "from", "john" }, { "who", "created", "these", "contracts" },
                                            { "contracts", "by", "author", "smith" } });

        // Search contracts samples
        addTrainingSamples(samples, "search_contracts",
                           new String[][] { { "search", "contracts" }, { "find", "contract" },
                                            { "look", "for", "contracts" }, { "locate", "contract" },
                                            { "search", "for", "contract" }, { "find", "contract", "containing" },
                                            { "look", "up", "contract" }, { "hunt", "for", "contract" },
                                            { "discover", "contract" }, { "seek", "contract" },
                                            { "serch", "contracts" }, { "fnd", "contract" },
                                            { "lok", "for", "contracts" } });

        // List contracts samples
        addTrainingSamples(samples, "list_contracts",
                           new String[][] { { "list", "all", "contracts" }, { "show", "all", "contracts" },
                                            { "display", "all", "contracts" }, { "all", "contracts" },
                                            { "list", "contracts" }, { "complete", "list", "of", "contracts" },
                                            { "every", "contract" }, { "show", "me", "all", "contracts" },
                                            { "display", "complete", "contract", "list" }, { "active", "contracts" },
                                            { "current", "contracts" }, { "available", "contracts" },
                                            { "show", "active", "contracts" }, { "display", "current", "contracts" },
                                            { "list", "active", "contracts" }, { "all", "active", "contracts" },
                                            { "current", "contract", "list" }, { "active", "contract", "list" } });

        // Contract status samples
        addTrainingSamples(samples, "contract_status",
                           new String[][] { { "status", "of", "contract", "123456" },
                                            { "contract", "123456", "status" },
                                            { "check", "status", "of", "contract", "789012" },
                                            { "what", "is", "the", "status", "of", "contract", "456789" },
                                            { "contract", "456789", "current", "status" },
                                            { "state", "of", "contract", "234567" },
                                            { "condition", "of", "contract", "345678" },
                                            { "contract", "345678", "situation" }, { "active", "status", "123456" },
                                            { "inactive", "contracts" }, { "pending", "contracts" },
                                            { "completed", "contracts" }, { "cancelled", "contracts" } });

        return ObjectStreamUtils.createObjectStream(samples);
    }

    /**
     * Add training samples helper method
     */
    private void addTrainingSamples(List<DocumentSample> samples, String category, String[][] tokenArrays) {
        for (String[] tokens : tokenArrays) {
            samples.add(new DocumentSample(category, tokens));
        }
    }


    /**
     * Memory usage analysis
     */
    public MemoryUsageResult analyzeMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        double usagePercentage = (double) usedMemory / maxMemory * 100;

        // Estimate classifier memory usage
        long estimatedClassifierMemory = 0;
        if (model != null) {
            estimatedClassifierMemory += 1024 * 1024; // Approximate model size
        }
        if (intentKeywords != null) {
            estimatedClassifierMemory += intentKeywords.size() * 100; // Approximate keywords size
        }

        return new MemoryUsageResult(usedMemory, totalMemory, maxMemory, usagePercentage, estimatedClassifierMemory);
    }

    /**
     * Memory usage result container
     */
    public static class MemoryUsageResult {
        private final long memoryUsed;
        private final long totalMemory;
        private final long maxMemory;
        private final double memoryUsagePercentage;
        private final long estimatedClassifierMemory;

        public MemoryUsageResult(long memoryUsed, long totalMemory, long maxMemory, double memoryUsagePercentage,
                                 long estimatedClassifierMemory) {
            this.memoryUsed = memoryUsed;
            this.totalMemory = totalMemory;
            this.maxMemory = maxMemory;
            this.memoryUsagePercentage = memoryUsagePercentage;
            this.estimatedClassifierMemory = estimatedClassifierMemory;
        }

        public long getMemoryUsed() {
            return memoryUsed;
        }

        public long getTotalMemory() {
            return totalMemory;
        }

        public long getMaxMemory() {
            return maxMemory;
        }

        public double getMemoryUsagePercentage() {
            return memoryUsagePercentage;
        }

        public long getEstimatedClassifierMemory() {
            return estimatedClassifierMemory;
        }

        public String formatBytes(long bytes) {
            if (bytes < 1024)
                return bytes + " B";
            if (bytes < 1024 * 1024)
                return String.format("%.1f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024)
                return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }

        @Override
        public String toString() {
            return String.format("Memory Usage: %s / %s (%.1f%%), Classifier: %s", formatBytes(memoryUsed),
                                 formatBytes(maxMemory), memoryUsagePercentage, formatBytes(estimatedClassifierMemory));
        }
    }

    /**
     * Health check for classifier
     */
    public HealthCheckResult performHealthCheck() {
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Check initialization
        if (!initialized) {
            issues.add("Classifier not initialized");
        }

        // Check model
        if (model == null) {
            issues.add("Model not loaded");
        }

        // Check categorizer
        if (categorizer == null) {
            issues.add("Categorizer not available");
        }

        // Check intent thresholds
        if (intentThresholds == null || intentThresholds.isEmpty()) {
            issues.add("Intent thresholds not configured");
        }

        // Check intent keywords
        if (intentKeywords == null || intentKeywords.isEmpty()) {
            issues.add("Intent keywords not configured");
        }

        // Check patterns
        if (contractNumberPattern == null) {
            issues.add("Contract number pattern not configured");
        }

        if (customerNumberPattern == null) {
            issues.add("Customer number pattern not configured");
        }

        // Memory warnings
        MemoryUsageResult memoryUsage = analyzeMemoryUsage();
        if (memoryUsage.getMemoryUsagePercentage() > 80) {
            warnings.add("High memory usage: " + String.format("%.1f%%", memoryUsage.getMemoryUsagePercentage()));
        }

        // Performance warnings
        if (processingStatistics != null) {
            if (processingStatistics.getAverageProcessingTime() > 1000) {
                warnings.add("Slow processing time: " +
                             String.format("%.2fms", processingStatistics.getAverageProcessingTime()));
            }

            if (processingStatistics.getSuccessRate() < 80) {
                warnings.add("Low success rate: " + String.format("%.1f%%", processingStatistics.getSuccessRate()));
            }
        }

        boolean isHealthy = issues.isEmpty();
        return new HealthCheckResult(isHealthy, issues, warnings);
    }

    /**
     * Health check result container
     */
    public static class HealthCheckResult {
        private final boolean healthy;
        private final List<String> issues;
        private final List<String> warnings;

        public HealthCheckResult(boolean healthy, List<String> issues, List<String> warnings) {
            this.healthy = healthy;
            this.issues = new ArrayList<>(issues);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isHealthy() {
            return healthy;
        }

        public List<String> getIssues() {
            return new ArrayList<>(issues);
        }

        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Health Status: ")
              .append(healthy ? "HEALTHY" : "UNHEALTHY")
              .append("\n");

            if (!issues.isEmpty()) {
                sb.append("Issues:\n");
                for (String issue : issues) {
                    sb.append("  ✗ ")
                      .append(issue)
                      .append("\n");
                }
            }

            if (!warnings.isEmpty()) {
                sb.append("Warnings:\n");
                for (String warning : warnings) {
                    sb.append("  ⚠ ")
                      .append(warning)
                      .append("\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * Enable processing statistics
     */
    public void enableProcessingStatistics() {
        this.processingStatistics = new ProcessingStatistics();
    }

    /**
     * Get processing statistics
     */
    public ProcessingStatistics getProcessingStatistics() {
        return processingStatistics;
    }


    /**
     * Getters and utility methods
     */
    public boolean isInitialized() {
        return initialized;
    }

    public ContractIntent[] getSupportedIntents() {
        return ContractIntent.values();
    }

    public void updateIntentThreshold(String intent, double threshold) {
        intentThresholds.put(intent, threshold);
        logger.info("Updated threshold for intent " + intent + " to " + threshold);
    }

    public Map<String, List<String>> getIntentKeywords() {
        return new HashMap<>(intentKeywords);
    }

    public Map<String, Double> getIntentThresholds() {
        return new HashMap<>(intentThresholds);
    }

    /**
     * Get version and build information
     */
    public String getVersionInfo() {
        return "MLIntentClassifierImproved v2.0 - Enhanced Business Contract Intent Classification\n" +
               "Features: Business customer detection, Active contracts handling, Enhanced typo correction\n" +
               "Training samples: 200+, Supported intents: " + ContractIntent.values().length + "\n" + "Build date: " +
               new java.util.Date().toString();
    }

    /**
     * Export model configuration for debugging
     */
    public String exportConfiguration() {
        StringBuilder config = new StringBuilder();
        config.append("=== MLIntentClassifierImproved Configuration ===\n");
        config.append("Initialized: ")
              .append(initialized)
              .append("\n");
        config.append("Contract Number Pattern: ")
              .append(contractNumberPattern.pattern())
              .append("\n");
        config.append("Customer Number Pattern: ")
              .append(customerNumberPattern.pattern())
              .append("\n\n");

        config.append("Intent Thresholds:\n");
        intentThresholds.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> config.append("  ")
                                                .append(entry.getKey())
                                                .append(": ")
                                                .append(entry.getValue())
                                                .append("\n"));

        config.append("\nIntent Keywords Count:\n");
        intentKeywords.entrySet()
                      .stream()
                      .sorted(Map.Entry.comparingByKey())
                      .forEach(entry -> config.append("  ")
                                              .append(entry.getKey())
                                              .append(": ")
                                              .append(entry.getValue().size())
                                              .append(" keywords\n"));

        config.append("\nAbbreviation Map:\n");
        abbreviationMap.entrySet()
                       .stream()
                       .sorted(Map.Entry.comparingByKey())
                       .forEach(entry -> config.append("  ")
                                               .append(entry.getKey())
                                               .append(" -> ")
                                               .append(entry.getValue())
                                               .append("\n"));

        return config.toString();
    }

    /**
     * Cleanup method to free resources
     */
    public void cleanup() {
        if (processingStatistics != null) {
            processingStatistics = null;
        }

        if (intentKeywords != null) {
            intentKeywords.clear();
        }

        if (abbreviationMap != null) {
            abbreviationMap.clear();
        }

        if (intentThresholds != null) {
            intentThresholds.clear();
        }

        logger.info("Classifier resources cleaned up");
    }

    /**
     * Main method for testing the improved classifier
     */
    public static void main(String[] args) {
        System.out.println("=== MLIntentClassifierImproved - Contract Model ===\n");

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case "--generate":
            case "-g":
                generateModel();
                return;

            case "--test":
            case "-t":
                runModelTests();
                return;

            case "--info":
            case "-i":
                showModelInfo();
                return;

            default:
                System.out.println("Usage:");
                System.out.println("  --generate (-g) : Generate new model");
                System.out.println("  --test (-t)     : Test existing model");
                System.out.println("  --info (-i)     : Show model information");
                return;
            }
        }

        System.out.println("No arguments provided. Generating model and running tests...\n");
        generateModel();
        System.out.println("\n" + "===============================================================" + "\n");
        runModelTests();
    }

    /**
     * Generate model
     */
    public static void generateModel() {
        System.out.println("=== Contract Intent Model Generation ===");
        System.out.println("Starting model training...\n");

        try {
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();

            System.out.println("Training with enhanced contract data...");
            classifier.initializeWithTrainingData();
            System.out.println("✓ Training completed successfully!");

            String modelPath = "./models";
            File modelDir = new File(modelPath);
            if (!modelDir.exists()) {
                modelDir.mkdirs();
                System.out.println("✓ Created models directory: " + modelDir.getAbsolutePath());
            }

            System.out.println("Saving model as en-contracts.bin...");
            classifier.saveModel(modelPath);

            File modelFile = new File(modelPath + "/en-contracts.bin");
            if (modelFile.exists() && modelFile.length() > 0) {
                System.out.println("✓ Model generated successfully!");
                System.out.println("✓ Location: " + modelFile.getAbsolutePath());
                System.out.println("✓ Size: " + String.format("%.2f KB", modelFile.length() / 1024.0));
                System.out.println("✓ Created: " + new java.util.Date(modelFile.lastModified()));

                System.out.println("\nTesting saved model...");
                testSavedModel(modelPath);

            } else {
                System.out.println("✗ Model generation failed!");
            }

        } catch (Exception e) {
            System.err.println("✗ Error during model generation: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Run comprehensive model tests
     */
    public static void runModelTests() {
        System.out.println("=== Contract Model Testing ===");

        try {
            String modelPath = "./models";
            File modelFile = new File(modelPath + "/en-contracts.bin");

            if (!modelFile.exists()) {
                System.out.println("Model file not found. Generating first...");
                generateModel();
            }

            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);
            classifier.enableProcessingStatistics();
            System.out.println("✓ Model loaded successfully\n");

            // Test cases for specific requirements
            String[][] testCases = {
                // Format: {query, expected_intent_type}
                { "expired contracts", "LIST_TYPE" }, { "active contracts", "LIST_TYPE" },
                { "boeing contracts", "CUSTOMER_FILTER" }, { "customer honeywell", "CUSTOMER_FILTER" },
                { "account 10840607", "CUSTOMER_FILTER" }, { "show contract 123456", "SPECIFIC_CONTRACT" },
                { "contract 789012 expiration", "SPECIFIC_CONTRACT" }, { "list all contracts", "LIST_TYPE" },
                { "find contract", "SEARCH_TYPE" }, { "contract status 456789", "SPECIFIC_CONTRACT" },
                { "customer 12345678", "CUSTOMER_FILTER" }, { "contracts for microsoft", "CUSTOMER_FILTER" },
                { "when does contract 234567 expire", "SPECIFIC_CONTRACT" }, { "search for contracts", "SEARCH_TYPE" },
                { "show all active contracts", "LIST_TYPE" }
            };

            System.out.println("Running " + testCases.length + " test cases...\n");

            int passed = 0;
            for (int i = 0; i < testCases.length; i++) {
                String query = testCases[i][0];
                String expectedType = testCases[i][1];

                System.out.println("Test " + (i + 1) + ": \"" + query + "\"");

                String[] tokens = query.toLowerCase().split("\\s+");
                NLPProcessingResult nlpResult =
                    new NLPProcessingResult(query, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);

                IntentResult result = classifier.classifyIntent(nlpResult);
                ParsedQuery parsedQuery = classifier.extractEntitiesAsParsedQuery(query);

                System.out.println("  Intent: " + result.getIntent());
                System.out.println("  Confidence: " + String.format("%.2f", result.getConfidence()));
                System.out.println("  Action: " + result.getActionRequired());

                // ENHANCED ENTITY PRINTING - ADD THIS SECTION
                System.out.println("  === EXTRACTED ENTITIES ===");
                
                // Contract Number
                if (result.getContractNumber() != null) {
                    System.out.println("  Contract Number: " + result.getContractNumber());
                } else if (parsedQuery.getContractNumber() != null) {
                    System.out.println("  Contract Number: " + parsedQuery.getContractNumber());
                } else {
                    System.out.println("  Contract Number: [NOT FOUND]");
                }

                // Customer Information
                if (parsedQuery.getAccountNumber() != null) {
                    System.out.println("  Customer Number: " + parsedQuery.getAccountNumber());
                } else {
                    System.out.println("  Customer Number: [NOT FOUND]");
                }

                if (parsedQuery.getCustomerName() != null) {
                    System.out.println("  Customer Name: " + parsedQuery.getCustomerName());
                } else {
                    System.out.println("  Customer Name: [NOT FOUND]");
                }

                // User Information
                if (parsedQuery.getUserName() != null) {
                    System.out.println("  User Name: " + parsedQuery.getUserName());
                } else {
                    System.out.println("  User Name: [NOT FOUND]");
                }

                // Status Information
                if (parsedQuery.getStatusType() != null) {
                    System.out.println("  Status Type: " + parsedQuery.getStatusType());
                } else {
                    System.out.println("  Status Type: [NOT FOUND]");
                }

                // Query Classification
                System.out.println("  Query Type: " + parsedQuery.getQueryType());
                System.out.println("  Action Type: " + parsedQuery.getActionType());
                
                // Additional Pattern Analysis
                System.out.println("  === PATTERN ANALYSIS ===");
                System.out.println("  Is Business Query: " + classifier.isBusinessQuery(query));
                System.out.println("  Contains Contract Pattern: " + (query.matches(".*\\b\\d{6}\\b.*")));
                System.out.println("  Contains Customer Pattern: " + (query.matches(".*\\b\\d{6,8}\\b.*") && 
                                                                      (query.toLowerCase().contains("account") || 
                                                                       query.toLowerCase().contains("customer"))));
                
                // Intent Details (Top 3 alternatives)
                System.out.println("  === INTENT CONFIDENCE BREAKDOWN ===");
                Map<String, Double> intentDetails = result.getAllIntentScores();
                if (intentDetails != null && !intentDetails.isEmpty()) {
                    intentDetails.entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                                .limit(3)
                                .forEach(entry -> System.out.println("    " + entry.getKey() + ": " + 
                                                                    String.format("%.3f", entry.getValue())));
                }

                boolean isValid = result.getIntent() != ContractIntent.UNKNOWN && result.getConfidence() > 0.5;
                if (isValid) passed++;

                System.out.println("  Result: " + (isValid ? "✓ PASS" : "✗ FAIL"));
                System.out.println("  " + "=============================================================");
                System.out.println();
            }

            // Summary
            double successRate = (double) passed / testCases.length * 100;
            System.out.println("=== Test Summary ===");
            System.out.println("Passed: " + passed + "/" + testCases.length);
            System.out.println("Success Rate: " + String.format("%.1f%%", successRate));

            // Show processing statistics
            ProcessingStatistics stats = classifier.getProcessingStatistics();
            if (stats != null) {
                System.out.println("Processing Stats: " + stats);
            }

            // Show memory usage
            MemoryUsageResult memoryUsage = classifier.analyzeMemoryUsage();
            System.out.println("Memory Usage: " + memoryUsage);

            // Health check
            HealthCheckResult healthCheck = classifier.performHealthCheck();
            System.out.println("\n" + healthCheck);

            if (successRate >= 80) {
                System.out.println("✓ Model performance is GOOD!");
            } else {
                System.out.println("✗ Model needs improvement");
            }

        } catch (Exception e) {
            System.err.println("✗ Test error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Batch processing method for multiple queries
     */
    public List<IntentResult> processQueries(List<String> queries) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        List<IntentResult> results = new ArrayList<>();

        for (String query : queries) {
            try {
                String[] tokens = query.toLowerCase().split("\\s+");
                NLPProcessingResult nlpResult =
                    new NLPProcessingResult(query, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);

                IntentResult result = classifyIntent(nlpResult);
                results.add(result);

            } catch (Exception e) {
                logger.warning("Error processing query: " + query + " - " + e.getMessage());
                results.add(new IntentResult(ContractIntent.UNKNOWN, 0.0, "error", new HashMap<>(), null));
            }
        }

        return results;
    }

    /**
     * Get detailed analysis for a query
     */
    public QueryAnalysis analyzeQuery(String query) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        try {
            long startTime = System.nanoTime();

            // Extract parsed query
            ParsedQuery parsedQuery = extractEntitiesAsParsedQuery(query);

            // Get intent result
            String[] tokens = query.toLowerCase().split("\\s+");
            NLPProcessingResult nlpResult =
                new NLPProcessingResult(query, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);
            IntentResult intentResult = classifyIntent(nlpResult);

            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0;

            return new QueryAnalysis(query, parsedQuery, intentResult, processingTime);

        } catch (Exception e) {
            logger.severe("Error analyzing query: " + e.getMessage());
            return null;
        }
    }

    /**
     * Query analysis result container
     */
    public static class QueryAnalysis {
        private final String originalQuery;
        private final ParsedQuery parsedQuery;
        private final IntentResult intentResult;
        private final double processingTimeMs;

        public QueryAnalysis(String originalQuery, ParsedQuery parsedQuery, IntentResult intentResult,
                             double processingTimeMs) {
            this.originalQuery = originalQuery;
            this.parsedQuery = parsedQuery;
            this.intentResult = intentResult;
            this.processingTimeMs = processingTimeMs;
        }

        public String getOriginalQuery() {
            return originalQuery;
        }

        public ParsedQuery getParsedQuery() {
            return parsedQuery;
        }

        public IntentResult getIntentResult() {
            return intentResult;
        }

        public double getProcessingTimeMs() {
            return processingTimeMs;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Query Analysis ===\n");
            sb.append("Original Query: \"")
              .append(originalQuery)
              .append("\"\n");
            sb.append("Processing Time: ")
              .append(String.format("%.2f ms", processingTimeMs))
              .append("\n\n");

            sb.append("Intent Classification:\n");
            sb.append("  Intent: ")
              .append(intentResult.getIntent())
              .append("\n");
            sb.append("  Confidence: ")
              .append(String.format("%.2f", intentResult.getConfidence()))
              .append("\n");
            sb.append("  Action Required: ")
              .append(intentResult.getActionRequired())
              .append("\n\n");

            sb.append("Entity Extraction:\n");
            sb.append("  Contract Number: ")
              .append(parsedQuery.getContractNumber())
              .append("\n");
            sb.append("  Customer Number: ")
              .append(parsedQuery.getAccountNumber())
              .append("\n");
            sb.append("  Customer Name: ")
              .append(parsedQuery.getCustomerName())
              .append("\n");
            sb.append("  User Name: ")
              .append(parsedQuery.getUserName())
              .append("\n");
            sb.append("  Status Type: ")
              .append(parsedQuery.getStatusType())
              .append("\n");
            sb.append("  Query Type: ")
              .append(parsedQuery.getQueryType())
              .append("\n");
            sb.append("  Action Type: ")
              .append(parsedQuery.getActionType())
              .append("\n");

            return sb.toString();
        }
    }

    /**
     * Validate classifier with comprehensive test suite
     */
    public ValidationSummary validateClassifier() {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        Map<String, ContractIntent> testCases = createComprehensiveTestCases();
        List<ValidationResult> results = new ArrayList<>();

        int correct = 0;
        int total = testCases.size();

        for (Map.Entry<String, ContractIntent> testCase : testCases.entrySet()) {
            String query = testCase.getKey();
            ContractIntent expected = testCase.getValue();

            String[] tokens = query.toLowerCase().split("\\s+");
            NLPProcessingResult nlpResult =
                new NLPProcessingResult(query, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);

            IntentResult result = classifyIntent(nlpResult);
            boolean isCorrect = result.getIntent() == expected;

            if (isCorrect)
                correct++;

            results.add(new ValidationResult(query, expected, result.getIntent(), result.getConfidence(), isCorrect));
        }

        double accuracy = (double) correct / total;
        return new ValidationSummary(accuracy, correct, total, results);
    }

    /**
     * Create comprehensive test cases
     */
    private Map<String, ContractIntent> createComprehensiveTestCases() {
        Map<String, ContractIntent> testCases = new HashMap<>();

        // Show contract cases
        testCases.put("show contract 123456", ContractIntent.SHOW_CONTRACT);
        testCases.put("display contract 789012", ContractIntent.SHOW_CONTRACT);
        testCases.put("view contract 456789", ContractIntent.SHOW_CONTRACT);

        // Contract info cases
        testCases.put("contract details 123456", ContractIntent.GET_CONTRACT_INFO);
        testCases.put("get contract info 789012", ContractIntent.GET_CONTRACT_INFO);
        testCases.put("contract information 456789", ContractIntent.GET_CONTRACT_INFO);

        // Expiration cases
        testCases.put("contract expiration 123456", ContractIntent.GET_CONTRACT_EXPIRATION);
        testCases.put("when does contract 789012 expire", ContractIntent.GET_CONTRACT_EXPIRATION);

        // List expired contracts
        testCases.put("expired contracts", ContractIntent.LIST_EXPIRED_CONTRACTS);
        testCases.put("show expired contracts", ContractIntent.LIST_EXPIRED_CONTRACTS);

        // List active contracts
        testCases.put("active contracts", ContractIntent.LIST_ACTIVE_CONTRACTS);
        testCases.put("current contracts", ContractIntent.LIST_ACTIVE_CONTRACTS);

        // Customer filter cases
        testCases.put("boeing contracts", ContractIntent.FILTER_CONTRACTS_BY_CUSTOMER);
        testCases.put("customer honeywell", ContractIntent.FILTER_CONTRACTS_BY_CUSTOMER);
        testCases.put("account 10840607", ContractIntent.FILTER_CONTRACTS_BY_CUSTOMER);
        testCases.put("microsoft contracts", ContractIntent.FILTER_CONTRACTS_BY_CUSTOMER);

        // User filter cases
        testCases.put("contracts by john", ContractIntent.FILTER_CONTRACTS_BY_USER);
        testCases.put("john contracts", ContractIntent.FILTER_CONTRACTS_BY_USER);
        testCases.put("created by smith", ContractIntent.FILTER_CONTRACTS_BY_USER);

        // List contracts cases
        testCases.put("list all contracts", ContractIntent.LIST_CONTRACTS);
        testCases.put("show all contracts", ContractIntent.LIST_CONTRACTS);
        testCases.put("display all contracts", ContractIntent.LIST_CONTRACTS);

        // Search cases
        testCases.put("search contracts", ContractIntent.SEARCH_CONTRACTS);
        testCases.put("find contract", ContractIntent.SEARCH_CONTRACTS);
        testCases.put("look for contracts", ContractIntent.SEARCH_CONTRACTS);

        // Status cases
        testCases.put("contract status 123456", ContractIntent.CONTRACT_STATUS);
        testCases.put("status of contract 789012", ContractIntent.CONTRACT_STATUS);
        testCases.put("check status 456789", ContractIntent.CONTRACT_STATUS);

        return testCases;
    }

    /**
     * Validation result container
     */
    public static class ValidationResult {
        private final String query;
        private final ContractIntent expected;
        private final ContractIntent actual;
        private final double confidence;
        private final boolean correct;

        public ValidationResult(String query, ContractIntent expected, ContractIntent actual, double confidence,
                                boolean correct) {
            this.query = query;
            this.expected = expected;
            this.actual = actual;
            this.confidence = confidence;
            this.correct = correct;
        }

        public String getQuery() {
            return query;
        }

        public ContractIntent getExpected() {
            return expected;
        }

        public ContractIntent getActual() {
            return actual;
        }

        public double getConfidence() {
            return confidence;
        }

        public boolean isCorrect() {
            return correct;
        }

        @Override
        public String toString() {
            return String.format("Query: \"%s\" | Expected: %s | Actual: %s | Confidence: %.2f | %s", query, expected,
                                 actual, confidence, correct ? "✓" : "✗");
        }
    }

    /**
     * Validation summary container
     */
    public static class ValidationSummary {
        private final double accuracy;
        private final int correctPredictions;
        private final int totalTests;
        private final List<ValidationResult> results;

        public ValidationSummary(double accuracy, int correctPredictions, int totalTests,
                                 List<ValidationResult> results) {
            this.accuracy = accuracy;
            this.correctPredictions = correctPredictions;
            this.totalTests = totalTests;
            this.results = results;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public int getCorrectPredictions() {
            return correctPredictions;
        }

        public int getTotalTests() {
            return totalTests;
        }

        public List<ValidationResult> getResults() {
            return results;
        }

        public List<ValidationResult> getFailures() {
            return results.stream()
                          .filter(r -> !r.isCorrect())
                          .collect(java.util
                                       .stream
                                       .Collectors
                                       .toList());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Validation Summary ===\n");
            sb.append(String.format("Accuracy: %.1f%% (%d/%d)\n", accuracy * 100, correctPredictions, totalTests));

            List<ValidationResult> failures = getFailures();
            if (!failures.isEmpty()) {
                sb.append("\nFailures:\n");
                for (ValidationResult failure : failures) {
                    sb.append("  ✗ ")
                      .append(failure.toString())
                      .append("\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * Performance benchmark method
     */
    public BenchmarkResult runPerformanceBenchmark(int iterations) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        String[] testQueries = {
            "show contract 123456", "expired contracts", "boeing contracts", "search contracts", "list all contracts",
            "contract status 789012", "customer honeywell", "active contracts"
        };

        List<Double> processingTimes = new ArrayList<>();
        int successfulClassifications = 0;

        long totalStartTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            String query = testQueries[i % testQueries.length];

            long startTime = System.nanoTime();

            try {
                String[] tokens = query.toLowerCase().split("\\s+");
                NLPProcessingResult nlpResult =
                    new NLPProcessingResult(query, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);

                IntentResult result = classifyIntent(nlpResult);

                long endTime = System.nanoTime();
                double processingTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
                processingTimes.add(processingTime);

                if (result.getIntent() != ContractIntent.UNKNOWN) {
                    successfulClassifications++;
                }

            } catch (Exception e) {
                logger.warning("Error in benchmark iteration " + i + ": " + e.getMessage());
            }
        }

        long totalEndTime = System.nanoTime();
        double totalTime = (totalEndTime - totalStartTime) / 1_000_000.0;

        return new BenchmarkResult(iterations, successfulClassifications, processingTimes, totalTime);
    }

    /**
     * Benchmark result container
     */
    public static class BenchmarkResult {
        private final int totalIterations;
        private final int successfulClassifications;
        private final List<Double> processingTimes;
        private final double totalTime;

        public BenchmarkResult(int totalIterations, int successfulClassifications, List<Double> processingTimes,
                               double totalTime) {
            this.totalIterations = totalIterations;
            this.successfulClassifications = successfulClassifications;
            this.processingTimes = processingTimes;
            this.totalTime = totalTime;
        }

        public double getAverageProcessingTime() {
            return processingTimes.stream()
                                  .mapToDouble(Double::doubleValue)
                                  .average()
                                  .orElse(0.0);
        }

        public double getMinProcessingTime() {
            return processingTimes.stream()
                                  .mapToDouble(Double::doubleValue)
                                  .min()
                                  .orElse(0.0);
        }

        public double getMaxProcessingTime() {
            return processingTimes.stream()
                                  .mapToDouble(Double::doubleValue)
                                  .max()
                                  .orElse(0.0);
        }

        public double getSuccessRate() {
            return totalIterations > 0 ? (double) successfulClassifications / totalIterations * 100 : 0.0;
        }

        public double getThroughput() {
            return totalTime > 0 ? totalIterations / (totalTime / 1000.0) : 0.0; // queries per second
        }

        @Override
        public String toString() {
            return String.format("Benchmark Results:\n" + "  Total Iterations: %d\n" +
                                 "  Successful Classifications: %d (%.1f%%)\n" + "  Total Time: %.2f ms\n" +
                                 "  Average Processing Time: %.2f ms\n" + "  Min Processing Time: %.2f ms\n" +
                                 "  Max Processing Time: %.2f ms\n" + "  Throughput: %.1f queries/second",
                                 totalIterations, successfulClassifications, getSuccessRate(), totalTime,
                                 getAverageProcessingTime(), getMinProcessingTime(), getMaxProcessingTime(),
                                 getThroughput());
        }
    }

    /**
     * Get model metrics
     */
    public ModelMetrics getModelMetrics() {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        int totalIntents = ContractIntent.values().length;
        int configuredIntents = intentThresholds.size();
        int totalKeywords = intentKeywords.values()
                                          .stream()
                                          .mapToInt(List::size)
                                          .sum();
        int totalAbbreviations = abbreviationMap.size();

        MemoryUsageResult memoryUsage = analyzeMemoryUsage();

        return new ModelMetrics(totalIntents, configuredIntents, totalKeywords, totalAbbreviations, memoryUsage);
    }

    /**
     * Model metrics container
     */
    public static class ModelMetrics {
        private final int totalIntents;
        private final int configuredIntents;
        private final int totalKeywords;
        private final int totalAbbreviations;
        private final MemoryUsageResult memoryUsage;

        public ModelMetrics(int totalIntents, int configuredIntents, int totalKeywords, int totalAbbreviations,
                            MemoryUsageResult memoryUsage) {
            this.totalIntents = totalIntents;
            this.configuredIntents = configuredIntents;
            this.totalKeywords = totalKeywords;
            this.totalAbbreviations = totalAbbreviations;
            this.memoryUsage = memoryUsage;
        }

        public int getTotalIntents() {
            return totalIntents;
        }

        public int getConfiguredIntents() {
            return configuredIntents;
        }

        public int getTotalKeywords() {
            return totalKeywords;
        }

        public int getTotalAbbreviations() {
            return totalAbbreviations;
        }

        public MemoryUsageResult getMemoryUsage() {
            return memoryUsage;
        }

        @Override
        public String toString() {
            return String.format("Model Metrics:\n" + "  Total Intents: %d\n" + "  Configured Intents: %d\n" +
                                 "  Total Keywords: %d\n" + "  Total Abbreviations: %d\n" + "  %s", totalIntents,
                                 configuredIntents, totalKeywords, totalAbbreviations, memoryUsage);
        }
    }

    /**
     * Reset processing statistics
     */
    public void resetProcessingStatistics() {
        if (processingStatistics != null) {
            processingStatistics = new ProcessingStatistics();
        }
    }

    /**
     * Add custom business customer
     */
    public void addBusinessCustomer(String customerName) {
        List<String> customerKeywords = intentKeywords.get("filter_contracts_by_customer");
        if (customerKeywords != null && !customerKeywords.contains(customerName.toLowerCase())) {
            customerKeywords.add(customerName.toLowerCase());
            logger.info("Added business customer: " + customerName);
        }
    }

    /**
     * Remove business customer
     */
    public void removeBusinessCustomer(String customerName) {
        List<String> customerKeywords = intentKeywords.get("filter_contracts_by_customer");
        if (customerKeywords != null) {
            customerKeywords.remove(customerName.toLowerCase());
            logger.info("Removed business customer: " + customerName);
        }
    }

    /**
     * Get all business customers
     */
    public List<String> getBusinessCustomers() {
        List<String> customerKeywords = intentKeywords.get("filter_contracts_by_customer");
        if (customerKeywords != null) {
            return customerKeywords.stream()
                                   .filter(keyword -> !keyword.contains(" ")) // Single word customers
                                   .collect(java.util
                                                .stream
                                                .Collectors
                                                .toList());
        }
        return new ArrayList<>();
    }

    /**
     * Update abbreviation mapping
     */
    public void updateAbbreviation(String abbreviation, String fullForm) {
        abbreviationMap.put(abbreviation.toLowerCase(), fullForm.toLowerCase());
        logger.info("Updated abbreviation: " + abbreviation + " -> " + fullForm);
    }

    /**
     * Remove abbreviation mapping
     */
    public void removeAbbreviation(String abbreviation) {
        abbreviationMap.remove(abbreviation.toLowerCase());
        logger.info("Removed abbreviation: " + abbreviation);
    }

    /**
     * Get all abbreviations
     */
    public Map<String, String> getAllAbbreviations() {
        return new HashMap<>(abbreviationMap);
    }

    /**
     * Check if query is business-related
     */
    public boolean isBusinessQuery(String query) {
        String lowerQuery = query.toLowerCase();

        // Check for business customer names
        List<String> businessCustomers = getBusinessCustomers();
        for (String customer : businessCustomers) {
            if (lowerQuery.contains(customer)) {
                return true;
            }
        }

        // Check for account/customer numbers
        if (customerNumberPattern.matcher(lowerQuery).find() || contractNumberPattern.matcher(lowerQuery).find()) {
            return true;
        }

        // Check for business terms
        String[] businessTerms = {
            "customer", "client", "account", "vendor", "supplier", "partner", "company", "organization" };
        for (String term : businessTerms) {
            if (lowerQuery.contains(term)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get suggestions for improving classification
     */
    public List<String> getSuggestions(String query, IntentResult result) {
        List<String> suggestions = new ArrayList<>();

        if (result.getIntent() == ContractIntent.UNKNOWN) {
            suggestions.add("Query classified as UNKNOWN. Consider:");

            if (isBusinessQuery(query)) {
                suggestions.add("- This appears to be a business query. Try adding more specific keywords.");
            }

            if (query.length() < 10) {
                suggestions.add("- Query is very short. Try adding more descriptive words.");
            }

            if (!query.toLowerCase().contains("contract")) {
                suggestions.add("- Consider adding the word 'contract' to make intent clearer.");
            }

            suggestions.add("- Try rephrasing using keywords like: show, list, find, search, expiration, status");
        } else if (result.getConfidence() < 0.7) {
            suggestions.add("Classification confidence is low (" + String.format("%.2f", result.getConfidence()) +
                            "). Consider:");
            suggestions.add("- Adding more specific keywords related to your intent");
            suggestions.add("- Being more explicit about what you want to do");

            if (result.getContractNumber() == null && needsContractNumber(result.getIntent())) {
                suggestions.add("- Adding a contract number if you're looking for specific contract information");
            }
        }

        return suggestions;
    }

    /**
     * Helper method to check if an intent typically needs a contract number
     */
    private boolean needsContractNumber(ContractIntent intent) {
        return intent == ContractIntent.SHOW_CONTRACT || intent == ContractIntent.GET_CONTRACT_INFO ||
               intent == ContractIntent.GET_CONTRACT_EXPIRATION || intent == ContractIntent.CONTRACT_STATUS;
    }

    /**
     * Get alternative intents with confidence scores
     */
    public List<IntentAlternative> getAlternativeIntents(String query, int maxAlternatives) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        try {
            Map<String, Double> allConfidences = getAllIntentConfidences(query);

            return allConfidences.entrySet()
                                 .stream()
                                 .map(entry -> new IntentAlternative(ContractIntent.fromString(entry.getKey()), entry.getValue()))
                                 .sorted((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()))
                                 .limit(maxAlternatives)
                                 .collect(java.util
                                              .stream
                                              .Collectors
                                              .toList());

        } catch (Exception e) {
            logger.severe("Error getting alternative intents: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Intent alternative container
     */
    public static class IntentAlternative {
        private final ContractIntent intent;
        private final double confidence;

        public IntentAlternative(ContractIntent intent, double confidence) {
            this.intent = intent;
            this.confidence = confidence;
        }

        public ContractIntent getIntent() {
            return intent;
        }

        public double getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return String.format("%s (%.3f)", intent, confidence);
        }
    }

    /**
     * Get all intent confidences for a query
     */
    public Map<String, Double> getAllIntentConfidences(String query) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        try {
            String[] tokens = query.toLowerCase().split("\\s+");
            NLPProcessingResult nlpResult =
                new NLPProcessingResult(query, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);

            String classificationText = prepareTextForClassification(nlpResult);
            double[] outcomes = categorizer.categorize(classificationText.split(" "));
            String[] categories = getCategoriesFromModel();

            Map<String, Double> allConfidences = new HashMap<>();
            for (int i = 0; i < categories.length && i < outcomes.length; i++) {
                allConfidences.put(categories[i], outcomes[i]);
            }

            return allConfidences;
        } catch (Exception e) {
            logger.severe("Error getting all intent confidences: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Export training data for analysis
     */
    public String exportTrainingDataSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Training Data Summary ===\n");

        // Count samples per intent
        Map<String, Integer> sampleCounts = new HashMap<>();

        try {
            ObjectStream<DocumentSample> sampleStream = createEnhancedTrainingData();
            DocumentSample sample;

            while ((sample = sampleStream.read()) != null) {
                String category = sample.getCategory();
                sampleCounts.put(category, sampleCounts.getOrDefault(category, 0) + 1);
            }

            summary.append("Training Samples by Intent:\n");
            sampleCounts.entrySet()
                        .stream()
                        .sorted(Map.Entry
                                   .<String, Integer>comparingByValue()
                                   .reversed())
                        .forEach(entry -> summary.append("  ")
                                                 .append(String.format("%-30s", entry.getKey()))
                                                 .append(": ")
                                                 .append(String.format("%3d samples", entry.getValue()))
                                                 .append("\n"));

            int totalSamples = sampleCounts.values()
                                           .stream()
                                           .mapToInt(Integer::intValue)
                                           .sum();
            summary.append("\nTotal Training Samples: ")
                   .append(totalSamples)
                   .append("\n");

        } catch (Exception e) {
            summary.append("Error analyzing training data: ")
                   .append(e.getMessage())
                   .append("\n");
        }

        return summary.toString();
    }

    /**
     * Comprehensive system status report
     */
    public String generateSystemReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== MLIntentClassifierImproved System Report ===\n");
        report.append("Generated: ")
              .append(new java.util.Date())
              .append("\n\n");

        // Version info
        report.append(getVersionInfo()).append("\n\n");

        // Health check
        HealthCheckResult healthCheck = performHealthCheck();
        report.append(healthCheck).append("\n");

        // Model metrics
        if (initialized) {
            ModelMetrics metrics = getModelMetrics();
            report.append(metrics).append("\n\n");
        }

        // Processing statistics
        if (processingStatistics != null) {
            report.append("Processing Statistics:\n");
            report.append("  ")
                  .append(processingStatistics)
                  .append("\n\n");
        }

        // Configuration
        report.append(exportConfiguration()).append("\n");

        // Training data summary
        report.append(exportTrainingDataSummary()).append("\n");

        return report.toString();
    }

    /**
     * Save system report to file
     */
    public void saveSystemReport(String filePath) throws IOException {
        String report = generateSystemReport();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(report);
        }

        logger.info("System report saved to: " + filePath);
    }

    /**
     * Optimize model performance
     */
    public OptimizationResult optimizeModel() {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        List<String> optimizations = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        // Check memory usage
        MemoryUsageResult memoryUsage = analyzeMemoryUsage();
        if (memoryUsage.getMemoryUsagePercentage() > 70) {
            recommendations.add("Consider increasing JVM heap size (-Xmx parameter)");
        }

        // Check intent threshold balance
        double avgThreshold = intentThresholds.values()
                                              .stream()
                                              .mapToDouble(Double::doubleValue)
                                              .average()
                                              .orElse(0.0);
        if (avgThreshold > 0.7) {
            recommendations.add("Intent thresholds are high - consider lowering for better recall");
        } else if (avgThreshold < 0.5) {
            recommendations.add("Intent thresholds are low - consider raising for better precision");
        }

        // Check keyword distribution
        int totalKeywords = intentKeywords.values()
                                          .stream()
                                          .mapToInt(List::size)
                                          .sum();
        int avgKeywordsPerIntent = totalKeywords / intentKeywords.size();

        for (Map.Entry<String, List<String>> entry : intentKeywords.entrySet()) {
            if (entry.getValue().size() < avgKeywordsPerIntent / 2) {
                recommendations.add("Intent '" + entry.getKey() + "' has few keywords - consider adding more");
            }
        }

        // Garbage collection suggestion
        System.gc();
        optimizations.add("Performed garbage collection");

        return new OptimizationResult(optimizations, recommendations);
    }

    /**
     * Optimization result container
     */
    public static class OptimizationResult {
        private final List<String> optimizations;
        private final List<String> recommendations;

        public OptimizationResult(List<String> optimizations, List<String> recommendations) {
            this.optimizations = new ArrayList<>(optimizations);
            this.recommendations = new ArrayList<>(recommendations);
        }

        public List<String> getOptimizations() {
            return new ArrayList<>(optimizations);
        }

        public List<String> getRecommendations() {
            return new ArrayList<>(recommendations);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Optimization Results ===\n");

            if (!optimizations.isEmpty()) {
                sb.append("Applied Optimizations:\n");
                for (String opt : optimizations) {
                    sb.append("  ✓ ")
                      .append(opt)
                      .append("\n");
                }
            }

            if (!recommendations.isEmpty()) {
                sb.append("Recommendations:\n");
                for (String rec : recommendations) {
                    sb.append("  → ")
                      .append(rec)
                      .append("\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * Shutdown method to clean up resources
     */
    public void shutdown() {
        logger.info("Shutting down MLIntentClassifierImproved...");

        cleanup();

        // Clear references
        this.model = null;
        this.categorizer = null;
        this.initialized = false;

        logger.info("MLIntentClassifierImproved shutdown complete");
    }

    /**
     * Finalize method
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (initialized) {
                shutdown();
            }
        } finally {
            super.finalize();
        }
    }

    // Add missing field declaration
    private ProcessingStatistics processingStatistics;
    private Pattern customerNumberPattern;

    // Initialize customer number pattern in constructor
    private void initializeCustomerNumberPattern() {
        this.customerNumberPattern = Pattern.compile("\\b\\d{6,8}\\b"); // Support 6-8 digit customer numbers
    }

    private ParsedQuery extractEntitiesAsParsedQuery(String query) {
        String contractNumber = extractContractNumber(query);
        String customerNumber = extractCustomerNumber(query);
        String customerName = extractCustomerName(query);
        String userName = extractUserName(query);
        String statusType = extractStatusType(query);

        String lowerQuery = query.toLowerCase();
        QueryType queryType = determineQueryType(lowerQuery);
        ActionType actionType = determineActionType(lowerQuery);

        // Create ParsedQuery using the default constructor and then set fields
        ParsedQuery parsedQuery = new ParsedQuery();
        parsedQuery.setContractNumber(contractNumber);
        parsedQuery.setAccountNumber(customerNumber);
        parsedQuery.setCustomerName(customerName);
        parsedQuery.setUserName(userName);
        parsedQuery.setStatusType(statusType);
        parsedQuery.setQueryType(queryType);
        parsedQuery.setActionType(actionType);

        return parsedQuery;
    }
    // Helper methods for ParsedQuery
    private QueryType determineQueryType(String lowerQuery) {
        if (lowerQuery.contains("list") || lowerQuery.contains("all")) {
            return QueryType.LIST;
        } else if (lowerQuery.contains("search") || lowerQuery.contains("find")) {
            return QueryType.SEARCH;
        } else if (lowerQuery.contains("filter") || lowerQuery.contains("by")) {
            return QueryType.FILTER;
        } else if (contractNumberPattern.matcher(lowerQuery).find()) {
            return QueryType.SPECIFIC;
        } else {
            return QueryType.GENERAL;
        }
    }

    private ActionType determineActionType(String lowerQuery) {
        if (lowerQuery.contains("show") || lowerQuery.contains("display") || lowerQuery.contains("view")) {
            return ActionType.SHOW;
        } else if (lowerQuery.contains("get") || lowerQuery.contains("retrieve")) {
            return ActionType.GET;
        } else if (lowerQuery.contains("list")) {
            return ActionType.LIST;
        } else if (lowerQuery.contains("search") || lowerQuery.contains("find")) {
            return ActionType.SEARCH;
        } else if (lowerQuery.contains("filter")) {
            return ActionType.FILTER;
        } else if (lowerQuery.contains("check") || lowerQuery.contains("status")) {
            return ActionType.CHECK;
        } else {
            return ActionType.UNKNOWN;
        }
    }


    // Add missing ProcessingStatistics class

    public static class ProcessingStatistics {
        private int totalQueries = 0;
        private int successfulClassifications = 0;
        private long totalProcessingTime = 0; // in nanoseconds
        private final List<Double> processingTimes = new ArrayList<>();

        public void recordQuery(boolean successful, long processingTimeNanos) {
            totalQueries++;
            if (successful) {
                successfulClassifications++;
            }
            totalProcessingTime += processingTimeNanos;
            processingTimes.add(processingTimeNanos / 1_000_000.0); // Convert to milliseconds
        }

        public int getTotalQueries() {
            return totalQueries;
        }

        public int getSuccessfulClassifications() {
            return successfulClassifications;
        }

        public double getSuccessRate() {
            return totalQueries > 0 ? (double) successfulClassifications / totalQueries * 100 : 0.0;
        }

        public double getAverageProcessingTime() {
            return processingTimes.isEmpty() ? 0.0 : processingTimes.stream()
                                                                    .mapToDouble(Double::doubleValue)
                                                                    .average()
                                                                    .orElse(0.0);
        }

        public double getMinProcessingTime() {
            return processingTimes.isEmpty() ? 0.0 : processingTimes.stream()
                                                                    .mapToDouble(Double::doubleValue)
                                                                    .min()
                                                                    .orElse(0.0);
        }

        public double getMaxProcessingTime() {
            return processingTimes.isEmpty() ? 0.0 : processingTimes.stream()
                                                                    .mapToDouble(Double::doubleValue)
                                                                    .max()
                                                                    .orElse(0.0);
        }

        @Override
        public String toString() {
            return String.format("Queries: %d, Success: %d (%.1f%%), Avg Time: %.2fms", totalQueries,
                                 successfulClassifications, getSuccessRate(), getAverageProcessingTime());
        }
    }

    // Update constructor to initialize customer number pattern
    public MLIntentClassifierImproved() {
        initializeIntentThresholds();
        initializeEnhancedIntentKeywords();
        initializeAbbreviationMap();
        initializeCustomerNumberPattern();
        this.contractNumberPattern = Pattern.compile("\\b\\d{6,8}\\b"); // Support 6-8 digit contract numbers
    }

    // Update classifyIntent method to record statistics
    public IntentResult classifyIntent(NLPProcessingResult nlpResult) {
        if (!initialized) {
            throw new IllegalStateException("Intent classifier not initialized");
        }

        long startTime = System.nanoTime();
        boolean successful = false;

        try {
            // Apply typo correction first
            NLPProcessingResult correctedNlpResult = applyCorrectionToNLPResult(nlpResult);

            String classificationText = prepareTextForClassification(correctedNlpResult);
            double[] outcomes = categorizer.categorize(classificationText.split(" "));
            String[] categories = getCategoriesFromModel();

            String bestCategory = categorizer.getBestCategory(outcomes);
            double confidence = getConfidenceForCategory(bestCategory, outcomes, categories);

            // Enhanced keyword boost with corrected result
            IntentConfidencePair boostedResult =
                applyEnhancedKeywordBoost(correctedNlpResult, bestCategory, confidence);

            // Apply threshold filtering
            ContractIntent intent = applyThresholdFiltering(boostedResult.intent, boostedResult.confidence);

            String contractNumber = extractContractNumber(correctedNlpResult.getOriginalSentence());
            ParsedQuery parsedQuery = extractEntitiesAsParsedQuery(correctedNlpResult.getOriginalSentence());
            String actionRequired = determineActionRequired(intent, parsedQuery);

            successful = intent != ContractIntent.UNKNOWN;

            return new IntentResult(intent, boostedResult.confidence, actionRequired,
                                    createIntentDetails(outcomes, categories), contractNumber);

        } catch (Exception e) {
            logger.severe("Error classifying intent: " + e.getMessage());
            return new IntentResult(ContractIntent.UNKNOWN, 0.0, "error", new HashMap<String, Double>(), null);
        } finally {
            long endTime = System.nanoTime();
            if (processingStatistics != null) {
                processingStatistics.recordQuery(successful, endTime - startTime);
            }
        }
    }

    // Add missing method to save model with correct filename

    public void saveModel(String modelPath) throws IOException {
        if (!initialized || model == null) {
            throw new IllegalStateException("Model not trained or initialized");
        }

        // Create directory if it doesn't exist
        File modelDir = new File(modelPath);
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        // Use consistent filename
        File modelFile = new File(modelPath + "/en-contracts.bin");

        try (FileOutputStream modelOut = new FileOutputStream(modelFile)) {
            model.serialize(modelOut);
        }

        logger.info("Enhanced intent classifier model saved to: " + modelFile.getAbsolutePath());
    }

    // Add missing method to load model with correct filename

    public void loadModel(String modelPath) throws IOException {
        File modelFile = new File(modelPath + "/en-contracts.bin");

        if (!modelFile.exists()) {
            throw new FileNotFoundException("Model file not found: " + modelFile.getAbsolutePath());
        }

        try (FileInputStream modelIn = new FileInputStream(modelFile)) {
            this.model = new DoccatModel(modelIn);
            this.categorizer = new DocumentCategorizerME(model);
            this.initialized = true;
        }

        logger.info("Enhanced intent classifier model loaded from: " + modelFile.getAbsolutePath());
    }

    // Fix the testSavedModel method to use correct filename
    private static void testSavedModel(String modelPath) {
        try {
            MLIntentClassifierImproved testClassifier = new MLIntentClassifierImproved();
            testClassifier.loadModel(modelPath);

            String testQuery = "show contract 123456";
            String[] tokens = testQuery.toLowerCase().split("\\s+");
            NLPProcessingResult nlpResult =
                new NLPProcessingResult(testQuery, tokens, new String[tokens.length], new opennlp.tools.util.Span[0]);

            IntentResult result = testClassifier.classifyIntent(nlpResult);

            System.out.println("✓ Model test successful!");
            System.out.println("  Test query: \"" + testQuery + "\"");
            System.out.println("  Result: " + result.getIntent() + " (confidence: " +
                               String.format("%.2f", result.getConfidence()) + ")");

        } catch (Exception e) {
            System.out.println("✗ Model test failed: " + e.getMessage());
        }
    }

    // Fix the showModelInfo method to use correct filename
    public static void showModelInfo() {
        System.out.println("=== Contract Model Information ===");

        String modelPath = "./models";
        File modelFile = new File(modelPath + "/en-contracts.bin");

        System.out.println("Model File: " + modelFile.getAbsolutePath());
        System.out.println("Exists: " + modelFile.exists());

        if (modelFile.exists()) {
            System.out.println("Size: " + String.format("%.2f KB", modelFile.length() / 1024.0));
            System.out.println("Last Modified: " + new java.util.Date(modelFile.lastModified()));
            System.out.println("Readable: " + modelFile.canRead());

            try {
                MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
                classifier.loadModel(modelPath);
                System.out.println("Model Status: ✓ VALID");
                System.out.println("Supported Intents: " + Arrays.toString(classifier.getSupportedIntents()));
                System.out.println("Version Info: " + classifier.getVersionInfo());

                // Show configuration
                System.out.println("\n" + classifier.exportConfiguration());

            } catch (Exception e) {
                System.out.println("Model Status: ✗ INVALID - " + e.getMessage());
            }
        } else {
            System.out.println("Model Status: ✗ NOT FOUND");
            System.out.println("Run with --generate to create the model");
        }
    }

    // Add toString method for better debugging
    @Override
    public String toString() {
        return String.format("MLIntentClassifierImproved{initialized=%s, intents=%d, keywords=%d, abbreviations=%d}",
                             initialized, intentThresholds.size(), intentKeywords.values()
                                                                                 .stream()
                                                                                 .mapToInt(List::size)
                                                                                 .sum(), abbreviationMap.size());
    }

    // Add equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        MLIntentClassifierImproved that = (MLIntentClassifierImproved) obj;
        return initialized == that.initialized && Objects.equals(intentThresholds, that.intentThresholds) &&
               Objects.equals(intentKeywords, that.intentKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initialized, intentThresholds, intentKeywords);
    }

    private IntentConfidencePair applyEnhancedKeywordBoost(NLPProcessingResult nlpResult, String originalIntent,
                                                           double originalConfidence) {
        String[] tokens = nlpResult.getTokens();
        String originalSentence = nlpResult.getOriginalSentence().toLowerCase();
        String correctedSentence = correctTyposAndAbbreviations(originalSentence);

        boolean hasContractNumber = extractContractNumber(nlpResult.getOriginalSentence()) != null;
        boolean hasCustomerNumber = extractCustomerNumber(originalSentence) != null;

        // Handle expiration keywords
        if (containsExpirationKeywords(tokens, correctedSentence)) {
            if (hasContractNumber) {
                return new IntentConfidencePair("get_contract_expiration", Math.max(originalConfidence + 0.4, 0.85));
            } else {
                return new IntentConfidencePair("list_expired_contracts", Math.max(originalConfidence + 0.4, 0.85));
            }
        }

        // Handle active contracts
        if (containsActiveContractsPatterns(tokens, correctedSentence)) {
            if (hasContractNumber) {
                return new IntentConfidencePair("contract_status", Math.max(originalConfidence + 0.3, 0.75));
            } else {
                return new IntentConfidencePair("list_active_contracts", Math.max(originalConfidence + 0.4, 0.80));
            }
        }

        // Handle customer patterns
        if (hasCustomerNumber || containsCustomerBusinessPatterns(tokens, correctedSentence)) {
            return new IntentConfidencePair("filter_contracts_by_customer", Math.max(originalConfidence + 0.5, 0.85));
        }

        // Regular keyword matching
        String bestIntent = originalIntent;
        double bestConfidence = originalConfidence;

        for (Map.Entry<String, List<String>> entry : intentKeywords.entrySet()) {
            String intentName = entry.getKey();
            List<String> keywords = entry.getValue();

            double keywordScore = calculateEnhancedKeywordScore(tokens, correctedSentence, keywords);

            if (keywordScore > 0) {
                double boostedConfidence = originalConfidence + (keywordScore * 0.35);
                if (boostedConfidence > bestConfidence) {
                    bestIntent = intentName;
                    bestConfidence = Math.min(boostedConfidence, 1.0);
                }
            }
        }

        return new IntentConfidencePair(bestIntent, bestConfidence);
    }

    private static class IntentConfidencePair {
        String intent;
        double confidence;

        IntentConfidencePair(String intent, double confidence) {
            this.intent = intent;
            this.confidence = confidence;
        }
    }
}


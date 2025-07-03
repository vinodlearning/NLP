package view.practice;


import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ParsedQuery - Comprehensive query parsing and entity extraction for contract management
 *
 * This class represents a parsed natural language query with extracted entities,
 * classified query types, and determined actions for contract management operations.
 *
 * Features:
 * - Entity extraction (contract numbers, customer info, dates, etc.)
 * - Query type classification
 * - Action type determination
 * - Validation and error handling
 * - JSON serialization support
 * - Builder pattern for construction
 *
 * @version 3.0
 * @author Contract Management System
 */
public class ParsedQuery implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private List<FilterCriteria> filters = new ArrayList<>();

    // Core query information
    private String originalQuery;
    private String normalizedQuery;
    private double confidence;
    private Date timestamp;

    // Entity fields
    private String contractNumber;
    private String accountNumber;
    private String customerName;
    private String userName;
    private String statusType;
    private String contractName;
    private String priceList;
    private String expirationDate;
    private String description;

    // Classification fields
    private QueryType queryType;
    private ActionType actionType;

    // Additional metadata
    private Map<String, Object> additionalEntities;
    private List<String> extractedKeywords;
    private List<String> validationErrors;
    private List<String> validationWarnings;
    private Map<String, Double> entityConfidences;

    // Static patterns for entity extraction
    private static final Map<String, Pattern> ENTITY_PATTERNS = initializePatterns();

    /**
     * Query Type enumeration - defines the nature of the query
     */
    public enum QueryType {
        // Basic query types
        SPECIFIC_CONTRACT("specific_contract", "Query about a specific contract"),
        CUSTOMER_FILTER("customer_filter", "Filter contracts by customer"),
        USER_FILTER("user_filter", "Filter contracts by user"),
        STATUS_FILTER("status_filter", "Filter contracts by status"),
        SEARCH("search", "Search for contracts"),
        LIST_ALL("list_all", "List all contracts"),
        GENERAL("general", "General query"),

        // Enhanced query types
        CONTRACT("contract", "Contract-related query"),
        LIST("list", "List operation"),
        FILTER("filter", "Filter operation"),
        HELP("help", "Help or guidance request"),
        SPECIFIC("specific", "Specific item query"),

        // Creation and modification types
        CREATE("create", "Create new contract"),
        UPDATE("update", "Update existing contract"),
        DELETE("delete", "Delete contract"),

        // Unknown type
        UNKNOWN("unknown", "Unknown query type");

        private final String value;
        private final String description;

        QueryType(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static QueryType fromString(String text) {
            if (text == null)
                return UNKNOWN;
            for (QueryType type : QueryType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Action Type enumeration - defines the action to be performed
     */
    public enum ActionType {
        // Display actions
        SHOW("show", "Display information"),
        VIEW("view", "View details"),
        DISPLAY("display", "Display data"),
        // Retrieval actions
        GET("get", "Retrieve information"),
        GET_ATTRIBUTE("get_attribute", "Get single attribute"), // ADD THIS
        GET_MULTIPLE_ATTRIBUTES("get_multiple_attributes", "Get multiple attributes"), // ADD THIS
        FETCH("fetch", "Fetch data"),
        RETRIEVE("retrieve", "Retrieve records"),

        // List actions
        LIST("list", "List items"),
        ENUMERATE("enumerate", "Enumerate items"),

        // Search actions
        SEARCH("search", "Search for items"),
        FIND("find", "Find specific items"),
        LOCATE("locate", "Locate items"),

        // Filter actions
        FILTER("filter", "Filter results"),
        SORT("sort", "Sort results"),

        // Status actions
        CHECK("check", "Check status"),
        VERIFY("verify", "Verify information"),
        VALIDATE("validate", "Validate data"),

        // Creation actions
        CREATE("create", "Create new item"),
        MAKE("make", "Make new item"),
        GENERATE("generate", "Generate item"),
        BUILD("build", "Build item"),

        // Modification actions
        UPDATE("update", "Update existing item"),
        MODIFY("modify", "Modify item"),
        CHANGE("change", "Change item"),
        EDIT("edit", "Edit item"),

        // Deletion actions
        DELETE("delete", "Delete item"),
        REMOVE("remove", "Remove item"),

        // Help actions
        GUIDE("guide", "Provide guidance"),
        HELP("help", "Provide help"),
        ASSIST("assist", "Provide assistance"),

        // Unknown action
        UNKNOWN("unknown", "Unknown action");

        private final String value;
        private final String description;

        ActionType(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static ActionType fromString(String text) {
            if (text == null)
                return UNKNOWN;
            for (ActionType type : ActionType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        @Override
        public String toString() {
            return value;
        }
    }


    /**
     * Default constructor
     */
    public ParsedQuery() {
        this.timestamp = new Date();
        this.additionalEntities = new HashMap<>();
        this.extractedKeywords = new ArrayList<>();
        this.validationErrors = new ArrayList<>();
        this.validationWarnings = new ArrayList<>();
        this.entityConfidences = new HashMap<>();
        this.queryType = QueryType.UNKNOWN;
        this.actionType = ActionType.UNKNOWN;
        this.confidence = 0.0;
    }

    /**
     * Constructor with original query
     */
    public ParsedQuery(String originalQuery) {
        this();
        this.originalQuery = originalQuery;
        this.normalizedQuery = normalizeQuery(originalQuery);
    }

    /**
     * Full constructor
     */
    public ParsedQuery(String originalQuery, QueryType queryType, ActionType actionType) {
        this(originalQuery);
        this.queryType = queryType;
        this.actionType = actionType;
    }
    public ParsedQuery(QueryType queryType, ActionType actionType, List<FilterCriteria> filters) {
            this.queryType = queryType;
            this.actionType = actionType;
            this.filters = filters;
        }

    /**
     * Initialize entity extraction patterns
     */
    private static Map<String, Pattern> initializePatterns() {
        Map<String, Pattern> patterns = new HashMap<>();

        // Contract number patterns (6-8 digits)
        patterns.put("contractNumber",
                     Pattern.compile("\\b(?:contract\\s+)?(?:#)?([0-9]{6,8})\\b", Pattern.CASE_INSENSITIVE));

        // Account number patterns (6-10 digits)
        patterns.put("accountNumber",
                     Pattern.compile("\\b(?:account\\s+)?(?:#)?([0-9]{6,10})\\b", Pattern.CASE_INSENSITIVE));

        // Customer name patterns
        patterns.put("customerName",
                     Pattern.compile("\\b(?:customer|client|company)\\s+([a-zA-Z][a-zA-Z0-9\\s&.-]{1,30})\\b",
                                     Pattern.CASE_INSENSITIVE));

        // User name patterns
        patterns.put("userName",
                     Pattern.compile("\\b(?:by|user|created\\s+by|made\\s+by|author)\\s+([a-zA-Z][a-zA-Z0-9._-]{1,20})\\b",
                                     Pattern.CASE_INSENSITIVE));

        // Status patterns
        patterns.put("statusType",
                     Pattern.compile("\\b(active|inactive|expired|pending|completed|cancelled|draft|suspended)\\b",
                                     Pattern.CASE_INSENSITIVE));

        // Contract name patterns
        patterns.put("contractName",
                     Pattern.compile("\\b(?:contract\\s+)?(?:named|called|titled)\\s+[\"']?([a-zA-Z][a-zA-Z0-9\\s&.-]{2,50})[\"']?\\b",
                                     Pattern.CASE_INSENSITIVE));

        // Price patterns
        patterns.put("priceList",
                     Pattern.compile("\\b(?:price|cost|amount|value)\\s+\\$?([0-9]+(?:\\.[0-9]{2})?)\\b",
                                     Pattern.CASE_INSENSITIVE));

        // Date patterns (multiple formats)
        patterns.put("expirationDate",
                     Pattern.compile("\\b(?:expires?|expiration|end(?:s|ing)?|due)\\s+(?:date\\s+)?([0-9]{4}-[0-9]{2}-[0-9]{2}|[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}|[0-9]{1,2}-[0-9]{1,2}-[0-9]{4})\\b",
                                     Pattern.CASE_INSENSITIVE));

        // Description patterns
        patterns.put("description",
                     Pattern.compile("\\b(?:description|details|notes)\\s+[\"']?([a-zA-Z][a-zA-Z0-9\\s&.,-]{5,100})[\"']?\\b",
                                     Pattern.CASE_INSENSITIVE));

        return patterns;
    }

    /**
     * Normalize query text for processing
     */
    private String normalizeQuery(String query) {
        if (query == null)
            return "";

        return query.toLowerCase()
                    .trim()
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^a-zA-Z0-9\\s#$.-]", " ")
                    .trim();
    }

    /**
     * Extract entities from the query using patterns
     */
    public void extractEntities() {
        if (originalQuery == null || originalQuery.trim().isEmpty()) {
            return;
        }

        String queryText = originalQuery;

        // Extract each entity type
        for (Map.Entry<String, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
            String entityType = entry.getKey();
            Pattern pattern = entry.getValue();

            Matcher matcher = pattern.matcher(queryText);
            if (matcher.find()) {
                String extractedValue = matcher.group(1).trim();
                double confidence = calculateExtractionConfidence(entityType, extractedValue, queryText);

                // Set the appropriate field
                setEntityValue(entityType, extractedValue);
                entityConfidences.put(entityType, confidence);
            }
        }

        // Extract keywords
        extractKeywords();

        // Validate extracted entities
        validateEntities();
    }

    /**
     * Set entity value based on type
     */
    private void setEntityValue(String entityType, String value) {
        switch (entityType) {
        case "contractNumber":
            this.contractNumber = value;
            break;
        case "accountNumber":
            this.accountNumber = value;
            break;
        case "customerName":
            this.customerName = value;
            break;
        case "userName":
            this.userName = value;
            break;
        case "statusType":
            this.statusType = value.toUpperCase();
            break;
        case "contractName":
            this.contractName = value;
            break;
        case "priceList":
            this.priceList = value;
            break;
        case "expirationDate":
            this.expirationDate = normalizeDate(value);
            break;
        case "description":
            this.description = value;
            break;
        default:
            additionalEntities.put(entityType, value);
            break;
        }
    }

    /**
     * Calculate confidence score for entity extraction
     */
    private double calculateExtractionConfidence(String entityType, String value, String context) {
        double confidence = 0.5; // Base confidence

        // Increase confidence based on context
        String lowerContext = context.toLowerCase();

        switch (entityType) {
        case "contractNumber":
            if (lowerContext.contains("contract"))
                confidence += 0.3;
            if (value.length() == 6)
                confidence += 0.2;
            break;
        case "accountNumber":
            if (lowerContext.contains("account") || lowerContext.contains("customer"))
                confidence += 0.3;
            if (value.length() >= 6 && value.length() <= 10)
                confidence += 0.2;
            break;
        case "customerName":
            if (lowerContext.contains("customer") || lowerContext.contains("client"))
                confidence += 0.4;
            break;
        case "statusType":
            confidence += 0.4; // Status keywords are usually clear
            break;
        default:
            confidence += 0.1;
            break;
        }

        return Math.min(confidence, 1.0);
    }

    /**
     * Extract keywords from the query
     */
    private void extractKeywords() {
        if (normalizedQuery == null || normalizedQuery.isEmpty()) {
            return;
        }

        String[] words = normalizedQuery.split("\\s+");
        Set<String> keywords = new HashSet<>();

        // Common contract-related keywords
        String[] importantKeywords = {
            "contract", "agreement", "customer", "client", "account", "user", "status", "active", "inactive", "expired",
            "pending", "create", "make", "new", "update", "modify", "show", "display", "list", "search", "find", "help",
            "guide"
        };

        for (String word : words) {
            for (String keyword : importantKeywords) {
                if (word.equals(keyword) || isTypoMatch(word, keyword)) {
                    keywords.add(keyword);
                }
            }
        }

        this.extractedKeywords = new ArrayList<>(keywords);
    }

    /**
     * Simple typo matching using edit distance
     */
    private boolean isTypoMatch(String word, String keyword) {
        if (Math.abs(word.length() - keyword.length()) > 2) {
            return false;
        }

        int distance = calculateEditDistance(word, keyword);
        return distance <= Math.max(1, keyword.length() / 3);
    }

    /**
     * Calculate edit distance between two strings
     */
    private int calculateEditDistance(String s1, String s2) {
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
     * Normalize date format
     */
    private String normalizeDate(String dateStr) {
        if (dateStr == null)
            return null;

        // Try to convert various date formats to YYYY-MM-DD
        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return dateStr; // Already in correct format
        } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            // Convert MM/DD/YYYY to YYYY-MM-DD
            String[] parts = dateStr.split("/");
            if (parts.length == 3) {
                String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                return parts[2] + "-" + month + "-" + day;
            }
        } else if (dateStr.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
            // Convert MM-DD-YYYY to YYYY-MM-DD
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                return parts[2] + "-" + month + "-" + day;
            }
        }

        return dateStr; // Return as-is if can't normalize
    }

    /**
     * Validate extracted entities
     */
    private void validateEntities() {
        validationErrors.clear();
        validationWarnings.clear();

        // Validate contract number
        if (contractNumber != null) {
            if (!contractNumber.matches("\\d{6,8}")) {
                validationErrors.add("Contract number must be 6-8 digits");
            }
        }

        // Validate account number
        if (accountNumber != null) {
            if (!accountNumber.matches("\\d{6,10}")) {
                validationErrors.add("Account number must be 6-10 digits");
            }
        }

        // Validate customer name
        if (customerName != null) {
            if (customerName.length() < 2) {
                validationWarnings.add("Customer name is very short");
            } else if (customerName.length() > 50) {
                validationWarnings.add("Customer name is very long");
            }
        }

        // Validate user name
        if (userName != null) {
            if (!userName.matches("[a-zA-Z][a-zA-Z0-9._-]*")) {
                validationWarnings.add("User name format may be invalid");
            }
        }

        // Validate expiration date
        if (expirationDate != null) {
            if (!expirationDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                validationErrors.add("Expiration date must be in YYYY-MM-DD format");
            } else {
                // Check if date is in the past
                try {
                    java.time.LocalDate date = java.time
                                                   .LocalDate
                                                   .parse(expirationDate);
                    if (date.isBefore(java.time
                                          .LocalDate
                                          .now())) {
                        validationWarnings.add("Expiration date is in the past");
                    }
                } catch (Exception e) {
                    validationErrors.add("Invalid expiration date format");
                }
            }
        }

        // Validate price
        if (priceList != null) {
            if (!priceList.matches("\\d+(?:\\.\\d{2})?")) {
                validationWarnings.add("Price format may not be standard");
            }
        }
    }

    /**
     * Determine query type based on extracted entities and keywords
     */
    public void determineQueryType() {
        if (contractNumber != null) {
            this.queryType = QueryType.SPECIFIC_CONTRACT;
        } else if (accountNumber != null || customerName != null) {
            this.queryType = QueryType.CUSTOMER_FILTER;
        } else if (userName != null) {
            this.queryType = QueryType.USER_FILTER;
        } else if (statusType != null) {
            this.queryType = QueryType.STATUS_FILTER;
        } else if (extractedKeywords.contains("search") || extractedKeywords.contains("find")) {
            this.queryType = QueryType.SEARCH;
        } else if (extractedKeywords.contains("list") || extractedKeywords.contains("all")) {
            this.queryType = QueryType.LIST_ALL;
        } else if (extractedKeywords.contains("create") || extractedKeywords.contains("new")) {
            this.queryType = QueryType.CREATE;
        } else if (extractedKeywords.contains("update") || extractedKeywords.contains("modify")) {
            this.queryType = QueryType.UPDATE;
        } else if (extractedKeywords.contains("help") || extractedKeywords.contains("guide")) {
            this.queryType = QueryType.HELP;
        } else {
            this.queryType = QueryType.GENERAL;
        }
    }

    /**
     * Determine action type based on extracted keywords
     */
    public void determineActionType() {
        String lowerQuery = normalizedQuery.toLowerCase();

        // Creation actions
        if (lowerQuery.contains("create") || lowerQuery.contains("make") || lowerQuery.contains("new")) {
            this.actionType = ActionType.CREATE;
        }
        // Update actions
        else if (lowerQuery.contains("update") || lowerQuery.contains("modify") || lowerQuery.contains("change")) {
            this.actionType = ActionType.UPDATE;
        }
        // Display actions
        else if (lowerQuery.contains("show") || lowerQuery.contains("display") || lowerQuery.contains("view")) {
            this.actionType = ActionType.SHOW;
        }
        // Retrieval actions
        else if (lowerQuery.contains("get") || lowerQuery.contains("retrieve") || lowerQuery.contains("fetch")) {
            this.actionType = ActionType.GET;
        }
        // List actions
        else if (lowerQuery.contains("list") || lowerQuery.contains("enumerate")) {
            this.actionType = ActionType.LIST;
        }
        // Search actions
        else if (lowerQuery.contains("search") || lowerQuery.contains("find") || lowerQuery.contains("locate")) {
            this.actionType = ActionType.SEARCH;
        }
        // Filter actions
        else if (lowerQuery.contains("filter") || lowerQuery.contains("sort")) {
            this.actionType = ActionType.FILTER;
        }
        // Status check actions
        else if (lowerQuery.contains("check") || lowerQuery.contains("verify") || lowerQuery.contains("status")) {
            this.actionType = ActionType.CHECK;
        }
        // Help actions
        else if (lowerQuery.contains("help") || lowerQuery.contains("guide") || lowerQuery.contains("how")) {
            this.actionType = ActionType.HELP;
        }
        // Delete actions
        else if (lowerQuery.contains("delete") || lowerQuery.contains("remove")) {
            this.actionType = ActionType.DELETE;
        } else {
            this.actionType = ActionType.UNKNOWN;
        }
    }

    /**
     * Process the query - extract entities and determine types
     */
    public void processQuery() {
        extractEntities();
        determineQueryType();
        determineActionType();
        calculateOverallConfidence();
    }

    /**
     * Calculate overall confidence based on entity extractions and classifications
     */
    private void calculateOverallConfidence() {
        double totalConfidence = 0.0;
        int factors = 0;

        // Entity confidence
        if (!entityConfidences.isEmpty()) {
            double avgEntityConfidence = entityConfidences.values()
                                                          .stream()
                                                          .mapToDouble(Double::doubleValue)
                                                          .average()
                                                          .orElse(0.0);
            totalConfidence += avgEntityConfidence;
            factors++;
        }

        // Query type confidence
        if (queryType != QueryType.UNKNOWN) {
            totalConfidence += 0.7;
            factors++;
        }

        // Action type confidence
        if (actionType != ActionType.UNKNOWN) {
            totalConfidence += 0.7;
            factors++;
        }

        // Keyword match confidence
        if (!extractedKeywords.isEmpty()) {
            totalConfidence += Math.min(extractedKeywords.size() * 0.1, 0.5);
            factors++;
        }

        // Validation confidence (negative impact for errors)
        if (!validationErrors.isEmpty()) {
            totalConfidence -= validationErrors.size() * 0.2;
        }

        this.confidence = factors > 0 ? Math.max(0.0, Math.min(1.0, totalConfidence / factors)) : 0.0;
    }

    // Getter and Setter methods

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
        this.normalizedQuery = normalizeQuery(originalQuery);
    }

    public String getNormalizedQuery() {
        return normalizedQuery;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getPriceList() {
        return priceList;
    }

    public void setPriceList(String priceList) {
        this.priceList = priceList;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Map<String, Object> getAdditionalEntities() {
        return new HashMap<>(additionalEntities);
    }

    public void setAdditionalEntities(Map<String, Object> additionalEntities) {
        this.additionalEntities = new HashMap<>(additionalEntities);
    }

    public List<String> getExtractedKeywords() {
        return new ArrayList<>(extractedKeywords);
    }

    public void setExtractedKeywords(List<String> extractedKeywords) {
        this.extractedKeywords = new ArrayList<>(extractedKeywords);
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    public List<String> getValidationWarnings() {
        return new ArrayList<>(validationWarnings);
    }

    public Map<String, Double> getEntityConfidences() {
        return new HashMap<>(entityConfidences);
    }

    // Utility methods

    /**
     * Check if the query is valid (no validation errors)
     */
    public boolean isValid() {
        return validationErrors.isEmpty();
    }

    /**
     * Check if the query has warnings
     */
    public boolean hasWarnings() {
        return !validationWarnings.isEmpty();
    }

    /**
     * Check if specific entity is present
     */
    public boolean hasEntity(String entityType) {
        switch (entityType.toLowerCase()) {
        case "contractnumber":
            return contractNumber != null;
        case "accountnumber":
            return accountNumber != null;
        case "customername":
            return customerName != null;
        case "username":
            return userName != null;
        case "statustype":
            return statusType != null;
        case "contractname":
            return contractName != null;
        case "pricelist":
            return priceList != null;
        case "expirationdate":
            return expirationDate != null;
        case "description":
            return description != null;
        default:
            return additionalEntities.containsKey(entityType);
        }
    }

    /**
     * Get entity value by type
     */
    public Object getEntityValue(String entityType) {
        switch (entityType.toLowerCase()) {
        case "contractnumber":
            return contractNumber;
        case "accountnumber":
            return accountNumber;
        case "customername":
            return customerName;
        case "username":
            return userName;
        case "statustype":
            return statusType;
        case "contractname":
            return contractName;
        case "pricelist":
            return priceList;
        case "expirationdate":
            return expirationDate;
        case "description":
            return description;
        default:
            return additionalEntities.get(entityType);
        }
    }

    /**
     * Add additional entity
     */
    public void addEntity(String key, Object value) {
        additionalEntities.put(key, value);
    }

    /**
     * Add extracted keyword
     */
    public void addKeyword(String keyword) {
        if (!extractedKeywords.contains(keyword)) {
            extractedKeywords.add(keyword);
        }
    }

    /**
     * Get all extracted entities as a map
     */
    public Map<String, Object> getAllEntities() {
        Map<String, Object> allEntities = new HashMap<>(additionalEntities);

        if (contractNumber != null)
            allEntities.put("contractNumber", contractNumber);
        if (contractNumber != null)
            allEntities.put("contractNumber", contractNumber);
        if (accountNumber != null)
            allEntities.put("accountNumber", accountNumber);
        if (customerName != null)
            allEntities.put("customerName", customerName);
        if (userName != null)
            allEntities.put("userName", userName);
        if (statusType != null)
            allEntities.put("statusType", statusType);
        if (contractName != null)
            allEntities.put("contractName", contractName);
        if (priceList != null)
            allEntities.put("priceList", priceList);
        if (expirationDate != null)
            allEntities.put("expirationDate", expirationDate);
        if (description != null)
            allEntities.put("description", description);

        return allEntities;
    }

    /**
     * Get count of extracted entities
     */
    public int getEntityCount() {
        return getAllEntities().size();
    }

    /**
     * Check if query is about contract creation
     */
    public boolean isCreationQuery() {
        return queryType == QueryType.CREATE || actionType == ActionType.CREATE ||
               extractedKeywords.contains("create") || extractedKeywords.contains("new") ||
               extractedKeywords.contains("make");
    }

    /**
     * Check if query is about contract modification
     */
    public boolean isModificationQuery() {
        return queryType == QueryType.UPDATE || actionType == ActionType.UPDATE ||
               extractedKeywords.contains("update") || extractedKeywords.contains("modify") ||
               extractedKeywords.contains("change") || extractedKeywords.contains("edit");
    }

    /**
     * Check if query is about specific contract
     */
    public boolean isSpecificContractQuery() {
        return contractNumber != null || queryType == QueryType.SPECIFIC_CONTRACT;
    }

    /**
     * Check if query is about customer filtering
     */
    public boolean isCustomerFilterQuery() {
        return (accountNumber != null || customerName != null) || queryType == QueryType.CUSTOMER_FILTER;
    }

    /**
     * Check if query needs help or guidance
     */
    public boolean isHelpQuery() {
        return queryType == QueryType.HELP || actionType == ActionType.HELP || extractedKeywords.contains("help") ||
               extractedKeywords.contains("guide") || extractedKeywords.contains("how");
    }

    /**
     * Get missing required fields for contract creation
     */
    public List<String> getMissingRequiredFields() {
        List<String> missing = new ArrayList<>();

        if (isCreationQuery()) {
            if (accountNumber == null && customerName == null) {
                missing.add("Customer information (account number or name)");
            }
            if (contractName == null) {
                missing.add("Contract name");
            }
            if (expirationDate == null) {
                missing.add("Expiration date");
            }
            if (priceList == null) {
                missing.add("Price information");
            }
        }

        return missing;
    }

    /**
     * Get suggested next actions based on query analysis
     */
    public List<String> getSuggestedActions() {
        List<String> suggestions = new ArrayList<>();

        if (isCreationQuery()) {
            List<String> missing = getMissingRequiredFields();
            if (!missing.isEmpty()) {
                suggestions.add("Provide missing information: " + String.join(", ", missing));
            } else {
                suggestions.add("All required information present - ready to create contract");
            }
        } else if (isSpecificContractQuery()) {
            suggestions.add("Retrieve contract details for: " + contractNumber);
        } else if (isCustomerFilterQuery()) {
            if (customerName != null) {
                suggestions.add("Filter contracts by customer: " + customerName);
            } else if (accountNumber != null) {
                suggestions.add("Filter contracts by account: " + accountNumber);
            }
        } else if (isHelpQuery()) {
            suggestions.add("Provide guidance for contract operations");
        } else {
            suggestions.add("Clarify intent - query type: " + queryType);
        }

        return suggestions;
    }

    /**
     * Generate summary of the parsed query
     */
    public String generateSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("Query Analysis Summary:\n");
        summary.append("  Original: \"")
               .append(originalQuery)
               .append("\"\n");
        summary.append("  Type: ")
               .append(queryType)
               .append("\n");
        summary.append("  Action: ")
               .append(actionType)
               .append("\n");
        summary.append("  Confidence: ")
               .append(String.format("%.1f%%", confidence * 100))
               .append("\n");

        if (getEntityCount() > 0) {
            summary.append("  Entities (")
                   .append(getEntityCount())
                   .append("):\n");
            getAllEntities().forEach((key, value) -> summary.append("    ")
                                                            .append(key)
                                                            .append(": ")
                                                            .append(value)
                                                            .append("\n"));
        }

        if (!extractedKeywords.isEmpty()) {
            summary.append("  Keywords: ")
                   .append(String.join(", ", extractedKeywords))
                   .append("\n");
        }

        if (!validationErrors.isEmpty()) {
            summary.append("  Errors: ")
                   .append(String.join(", ", validationErrors))
                   .append("\n");
        }

        if (!validationWarnings.isEmpty()) {
            summary.append("  Warnings: ")
                   .append(String.join(", ", validationWarnings))
                   .append("\n");
        }

        return summary.toString();
    }

    /**
     * Convert to JSON string
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"originalQuery\": \"")
            .append(escapeJson(originalQuery))
            .append("\",\n");
        json.append("  \"normalizedQuery\": \"")
            .append(escapeJson(normalizedQuery))
            .append("\",\n");
        json.append("  \"queryType\": \"")
            .append(queryType)
            .append("\",\n");
        json.append("  \"actionType\": \"")
            .append(actionType)
            .append("\",\n");
        json.append("  \"confidence\": ")
            .append(confidence)
            .append(",\n");
        json.append("  \"timestamp\": \"")
            .append(timestamp)
            .append("\",\n");

        // Entities
        json.append("  \"entities\": {\n");
        Map<String, Object> entities = getAllEntities();
        int entityCount = 0;
        for (Map.Entry<String, Object> entry : entities.entrySet()) {
            if (entityCount > 0)
                json.append(",\n");
            json.append("    \"")
                .append(entry.getKey())
                .append("\": \"")
                .append(escapeJson(String.valueOf(entry.getValue())))
                .append("\"");
            entityCount++;
        }
        json.append("\n  },\n");

        // Keywords
        json.append("  \"keywords\": [");
        for (int i = 0; i < extractedKeywords.size(); i++) {
            if (i > 0)
                json.append(", ");
            json.append("\"")
                .append(extractedKeywords.get(i))
                .append("\"");
        }
        json.append("],\n");

        // Validation
        json.append("  \"validation\": {\n");
        json.append("    \"isValid\": ")
            .append(isValid())
            .append(",\n");
        json.append("    \"errors\": [");
        for (int i = 0; i < validationErrors.size(); i++) {
            if (i > 0)
                json.append(", ");
            json.append("\"")
                .append(escapeJson(validationErrors.get(i)))
                .append("\"");
        }
        json.append("],\n");
        json.append("    \"warnings\": [");
        for (int i = 0; i < validationWarnings.size(); i++) {
            if (i > 0)
                json.append(", ");
            json.append("\"")
                .append(escapeJson(validationWarnings.get(i)))
                .append("\"");
        }
        json.append("]\n");
        json.append("  }\n");
        json.append("}");

        return json.toString();
    }

    /**
     * Escape JSON special characters
     */
    private String escapeJson(String str) {
        if (str == null)
            return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Create from JSON string (basic implementation)
     */
    public static ParsedQuery fromJson(String jsonStr) {
        // This is a simplified JSON parser - in production, use a proper JSON library
        ParsedQuery query = new ParsedQuery();

        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return query;
        }

        // Extract basic fields using regex (simplified approach)
        String originalQuery = extractJsonValue(jsonStr, "originalQuery");
        if (originalQuery != null) {
            query.setOriginalQuery(originalQuery);
        }

        String queryTypeStr = extractJsonValue(jsonStr, "queryType");
        if (queryTypeStr != null) {
            query.setQueryType(QueryType.fromString(queryTypeStr));
        }

        String actionTypeStr = extractJsonValue(jsonStr, "actionType");
        if (actionTypeStr != null) {
            query.setActionType(ActionType.fromString(actionTypeStr));
        }

        return query;
    }

    /**
     * Extract value from JSON string (simplified)
     */
    private static String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Builder pattern for ParsedQuery construction
     */
    public static class Builder {
        private ParsedQuery query;

        public Builder() {
            this.query = new ParsedQuery();
        }

        public Builder(String originalQuery) {
            this.query = new ParsedQuery(originalQuery);
        }

        public Builder contractNumber(String contractNumber) {
            query.setContractNumber(contractNumber);
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            query.setAccountNumber(accountNumber);
            return this;
        }

        public Builder customerName(String customerName) {
            query.setCustomerName(customerName);
            return this;
        }

        public Builder userName(String userName) {
            query.setUserName(userName);
            return this;
        }

        public Builder statusType(String statusType) {
            query.setStatusType(statusType);
            return this;
        }

        public Builder contractName(String contractName) {
            query.setContractName(contractName);
            return this;
        }

        public Builder priceList(String priceList) {
            query.setPriceList(priceList);
            return this;
        }

        public Builder expirationDate(String expirationDate) {
            query.setExpirationDate(expirationDate);
            return this;
        }

        public Builder description(String description) {
            query.setDescription(description);
            return this;
        }

        public Builder queryType(QueryType queryType) {
            query.setQueryType(queryType);
            return this;
        }

        public Builder actionType(ActionType actionType) {
            query.setActionType(actionType);
            return this;
        }

        public Builder confidence(double confidence) {
            query.setConfidence(confidence);
            return this;
        }

        public Builder addEntity(String key, Object value) {
            query.addEntity(key, value);
            return this;
        }

        public Builder addKeyword(String keyword) {
            query.addKeyword(keyword);
            return this;
        }

        public ParsedQuery build() {
            query.processQuery();
            return query;
        }
    }

    /**
     * Clone method implementation
     */
    @Override
    public ParsedQuery clone() {
        try {
            ParsedQuery cloned = (ParsedQuery) super.clone();

            // Deep copy mutable objects
            cloned.additionalEntities = new HashMap<>(this.additionalEntities);
            cloned.extractedKeywords = new ArrayList<>(this.extractedKeywords);
            cloned.validationErrors = new ArrayList<>(this.validationErrors);
            cloned.validationWarnings = new ArrayList<>(this.validationWarnings);
            cloned.entityConfidences = new HashMap<>(this.entityConfidences);
            cloned.timestamp = new Date(this.timestamp.getTime());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    /**
     * Equals method implementation
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        ParsedQuery that = (ParsedQuery) obj;

        return Double.compare(that.confidence, confidence) == 0 && Objects.equals(originalQuery, that.originalQuery) &&
               Objects.equals(normalizedQuery, that.normalizedQuery) &&
               Objects.equals(contractNumber, that.contractNumber) &&
               Objects.equals(accountNumber, that.accountNumber) && Objects.equals(customerName, that.customerName) &&
               Objects.equals(userName, that.userName) && Objects.equals(statusType, that.statusType) &&
               Objects.equals(contractName, that.contractName) && Objects.equals(priceList, that.priceList) &&
               Objects.equals(expirationDate, that.expirationDate) && Objects.equals(description, that.description) &&
               queryType == that.queryType && actionType == that.actionType &&
               Objects.equals(additionalEntities, that.additionalEntities) &&
               Objects.equals(extractedKeywords, that.extractedKeywords);
    }

    /**
     * HashCode method implementation
     */
    @Override
    public int hashCode() {
        return Objects.hash(originalQuery, normalizedQuery, confidence, contractNumber, accountNumber, customerName,
                            userName, statusType, contractName, priceList, expirationDate, description, queryType,
                            actionType, additionalEntities, extractedKeywords);
    }

    /**
     * ToString method implementation
     */
    @Override
    public String toString() {
        return String.format("ParsedQuery{query='%s', type=%s, action=%s, confidence=%.2f, entities=%d}", originalQuery,
                             queryType, actionType, confidence, getEntityCount());
    }

    /**
     * Merge with another ParsedQuery (useful for combining results)
     */
    public void mergeWith(ParsedQuery other) {
        if (other == null)
            return;
        // Merge entities (other takes precedence if not null)
        if (other.contractNumber != null)
            this.contractNumber = other.contractNumber;
        if (other.accountNumber != null)
            this.accountNumber = other.accountNumber;
        if (other.customerName != null)
            this.customerName = other.customerName;
        if (other.userName != null)
            this.userName = other.userName;
        if (other.statusType != null)
            this.statusType = other.statusType;
        if (other.contractName != null)
            this.contractName = other.contractName;
        if (other.priceList != null)
            this.priceList = other.priceList;
        if (other.expirationDate != null)
            this.expirationDate = other.expirationDate;
        if (other.description != null)
            this.description = other.description;
        // Merge additional entities
        this.additionalEntities.putAll(other.additionalEntities);
        // Merge keywords (avoid duplicates)
        for (String keyword : other.extractedKeywords) {
            if (!this.extractedKeywords.contains(keyword)) {
                this.extractedKeywords.add(keyword);
            }
        }

        // Merge entity confidences (take higher confidence)
        for (Map.Entry<String, Double> entry : other.entityConfidences.entrySet()) {
            String key = entry.getKey();
            Double otherConfidence = entry.getValue();
            Double thisConfidence = this.entityConfidences.get(key);

            if (thisConfidence == null || otherConfidence > thisConfidence) {
                this.entityConfidences.put(key, otherConfidence);
            }
        }

        // Update query type and action type if other has better classification
        if (other.queryType != QueryType.UNKNOWN &&
            (this.queryType == QueryType.UNKNOWN || other.confidence > this.confidence)) {
            this.queryType = other.queryType;
        }

        if (other.actionType != ActionType.UNKNOWN &&
            (this.actionType == ActionType.UNKNOWN || other.confidence > this.confidence)) {
            this.actionType = other.actionType;
        }

        // Update confidence to the higher value
        this.confidence = Math.max(this.confidence, other.confidence);

        // Re-validate after merge
        validateEntities();
    }

    /**
     * Reset all extracted data (useful for reprocessing)
     */
    public void reset() {
        this.contractNumber = null;
        this.accountNumber = null;
        this.customerName = null;
        this.userName = null;
        this.statusType = null;
        this.contractName = null;
        this.priceList = null;
        this.expirationDate = null;
        this.description = null;
        this.queryType = QueryType.UNKNOWN;
        this.actionType = ActionType.UNKNOWN;
        this.confidence = 0.0;
        this.additionalEntities.clear();
        this.extractedKeywords.clear();
        this.validationErrors.clear();
        this.validationWarnings.clear();
        this.entityConfidences.clear();
    }

    /**
     * Get entity extraction statistics
     */
    public EntityStatistics getEntityStatistics() {
        return new EntityStatistics(getEntityCount(), entityConfidences.size(), extractedKeywords.size(),
                                    validationErrors.size(), validationWarnings.size(), confidence);
    }

    /**
     * Entity Statistics inner class
     */
    public static class EntityStatistics {
        private final int totalEntities;
        private final int entitiesWithConfidence;
        private final int keywordCount;
        private final int errorCount;
        private final int warningCount;
        private final double overallConfidence;

        public EntityStatistics(int totalEntities, int entitiesWithConfidence, int keywordCount, int errorCount,
                                int warningCount, double overallConfidence) {
            this.totalEntities = totalEntities;
            this.entitiesWithConfidence = entitiesWithConfidence;
            this.keywordCount = keywordCount;
            this.errorCount = errorCount;
            this.warningCount = warningCount;
            this.overallConfidence = overallConfidence;
        }

        public int getTotalEntities() {
            return totalEntities;
        }

        public int getEntitiesWithConfidence() {
            return entitiesWithConfidence;
        }

        public int getKeywordCount() {
            return keywordCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public int getWarningCount() {
            return warningCount;
        }

        public double getOverallConfidence() {
            return overallConfidence;
        }

        public boolean isHighQuality() {
            return totalEntities > 0 && errorCount == 0 && overallConfidence > 0.7;
        }

        @Override
        public String toString() {
            return String.format("EntityStats{entities=%d, keywords=%d, confidence=%.2f, errors=%d, warnings=%d}",
                                 totalEntities, keywordCount, overallConfidence, errorCount, warningCount);
        }
    }

    /**
     * Export to CSV format (useful for analysis)
     */
    public String toCsv() {
        StringBuilder csv = new StringBuilder();

        // Header (if needed)
        // csv.append("originalQuery,queryType,actionType,confidence,contractNumber,accountNumber,customerName,userName,statusType,contractName,priceList,expirationDate,description,keywords,errors,warnings\n");

        // Data row
        csv.append(escapeCsv(originalQuery)).append(",");
        csv.append(queryType).append(",");
        csv.append(actionType).append(",");
        csv.append(confidence).append(",");
        csv.append(escapeCsv(contractNumber)).append(",");
        csv.append(escapeCsv(accountNumber)).append(",");
        csv.append(escapeCsv(customerName)).append(",");
        csv.append(escapeCsv(userName)).append(",");
        csv.append(escapeCsv(statusType)).append(",");
        csv.append(escapeCsv(contractName)).append(",");
        csv.append(escapeCsv(priceList)).append(",");
        csv.append(escapeCsv(expirationDate)).append(",");
        csv.append(escapeCsv(description)).append(",");
        csv.append(escapeCsv(String.join(";", extractedKeywords))).append(",");
        csv.append(escapeCsv(String.join(";", validationErrors))).append(",");
        csv.append(escapeCsv(String.join(";", validationWarnings)));

        return csv.toString();
    }

    /**
     * Escape CSV special characters
     */
    private String escapeCsv(String str) {
        if (str == null)
            return "";
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    /**
     * Get CSV header
     */
    public static String getCsvHeader() {
        return "originalQuery,queryType,actionType,confidence,contractNumber,accountNumber," +
               "customerName,userName,statusType,contractName,priceList,expirationDate," +
               "description,keywords,errors,warnings";
    }

    /**
     * Validate specific entity type
     */
    public boolean validateEntity(String entityType) {
        switch (entityType.toLowerCase()) {
        case "contractnumber":
            return contractNumber != null && contractNumber.matches("\\d{6,8}");
        case "accountnumber":
            return accountNumber != null && accountNumber.matches("\\d{6,10}");
        case "customername":
            return customerName != null && customerName.length() >= 2;
        case "username":
            return userName != null && userName.matches("[a-zA-Z][a-zA-Z0-9._-]*");
        case "statustype":
            return statusType != null &&
                   Arrays.asList("ACTIVE", "INACTIVE", "EXPIRED", "PENDING", "COMPLETED", "CANCELLED", "DRAFT",
                                 "SUSPENDED").contains(statusType.toUpperCase());
        case "contractname":
            return contractName != null && contractName.length() >= 2;
        case "pricelist":
            return priceList != null && priceList.matches("\\d+(?:\\.\\d{2})?");
        case "expirationdate":
            return expirationDate != null && expirationDate.matches("\\d{4}-\\d{2}-\\d{2}");
        case "description":
            return description != null && description.length() >= 5;
        default:
            return additionalEntities.containsKey(entityType) && additionalEntities.get(entityType) != null;
        }
    }

    /**
     * Get validation status for all entities
     */
    public Map<String, Boolean> getEntityValidationStatus() {
        Map<String, Boolean> status = new HashMap<>();

        status.put("contractNumber", validateEntity("contractNumber"));
        status.put("accountNumber", validateEntity("accountNumber"));
        status.put("customerName", validateEntity("customerName"));
        status.put("userName", validateEntity("userName"));
        status.put("statusType", validateEntity("statusType"));
        status.put("contractName", validateEntity("contractName"));
        status.put("priceList", validateEntity("priceList"));
        status.put("expirationDate", validateEntity("expirationDate"));
        status.put("description", validateEntity("description"));

        // Add additional entities
        for (String key : additionalEntities.keySet()) {
            status.put(key, validateEntity(key));
        }

        return status;
    }

    /**
     * Get completion percentage (how many expected entities are present)
     */
    public double getCompletionPercentage() {
        if (queryType == QueryType.CREATE) {
            int required = 4; // accountNumber/customerName, contractName, expirationDate, priceList
            int present = 0;

            if (accountNumber != null || customerName != null)
                present++;
            if (contractName != null)
                present++;
            if (expirationDate != null)
                present++;
            if (priceList != null)
                present++;

            return (double) present / required;
        } else if (queryType == QueryType.SPECIFIC_CONTRACT) {
            return contractNumber != null ? 1.0 : 0.0;
        } else if (queryType == QueryType.CUSTOMER_FILTER) {
            return (accountNumber != null || customerName != null) ? 1.0 : 0.0;
        } else if (queryType == QueryType.USER_FILTER) {
            return userName != null ? 1.0 : 0.0;
        } else {
            // For other query types, calculate based on any entities present
            int totalPossible = 9; // All main entity types
            int present = 0;

            if (contractNumber != null)
                present++;
            if (accountNumber != null)
                present++;
            if (customerName != null)
                present++;
            if (userName != null)
                present++;
            if (statusType != null)
                present++;
            if (contractName != null)
                present++;
            if (priceList != null)
                present++;
            if (expirationDate != null)
                present++;
            if (description != null)
                present++;

            return (double) present / totalPossible;
        }
    }

    /**
     * Generate recommendations for improving the query
     */
    public List<String> getRecommendations() {
        List<String> recommendations = new ArrayList<>();

        if (confidence < 0.5) {
            recommendations.add("Query confidence is low - consider adding more specific keywords");
        }

        if (queryType == QueryType.UNKNOWN) {
            recommendations.add("Query type unclear - try using keywords like 'show', 'list', 'create', 'search'");
        }

        if (actionType == ActionType.UNKNOWN) {
            recommendations.add("Action unclear - specify what you want to do (show, create, update, etc.)");
        }

        if (isCreationQuery()) {
            List<String> missing = getMissingRequiredFields();
            if (!missing.isEmpty()) {
                recommendations.add("For contract creation, provide: " + String.join(", ", missing));
            }
        }

        if (originalQuery != null && originalQuery.length() < 10) {
            recommendations.add("Query is very short - consider adding more descriptive words");
        }

        if (!validationErrors.isEmpty()) {
            recommendations.add("Fix validation errors: " + String.join(", ", validationErrors));
        }

        if (extractedKeywords.isEmpty()) {
            recommendations.add("No keywords detected - use contract-related terms for better results");
        }

        return recommendations;
    }

    /**
     * Create a simplified version for logging/debugging
     */
    public ParsedQuery createSimplified() {
        ParsedQuery simplified = new ParsedQuery();
        simplified.originalQuery = this.originalQuery;
        simplified.queryType = this.queryType;
        simplified.actionType = this.actionType;
        simplified.confidence = this.confidence;
        simplified.contractNumber = this.contractNumber;
        simplified.accountNumber = this.accountNumber;
        simplified.customerName = this.customerName;

        return simplified;
    }

    /**
     * Static factory methods for common query types
     */
    public static ParsedQuery createContractQuery(String contractNumber) {
        return new Builder().contractNumber(contractNumber)
                            .queryType(QueryType.SPECIFIC_CONTRACT)
                            .actionType(ActionType.SHOW)
                            .build();
    }

    public static ParsedQuery createCustomerQuery(String customerName, String accountNumber) {
        Builder builder = new Builder().queryType(QueryType.CUSTOMER_FILTER).actionType(ActionType.FILTER);

        if (customerName != null)
            builder.customerName(customerName);
        if (accountNumber != null)
            builder.accountNumber(accountNumber);

        return builder.build();
    }

    public static ParsedQuery createSearchQuery(String searchTerm) {
        return new Builder(searchTerm).queryType(QueryType.SEARCH)
                                      .actionType(ActionType.SEARCH)
                                      .build();
    }

    public static ParsedQuery createListQuery() {
        return new Builder().queryType(QueryType.LIST_ALL)
                            .actionType(ActionType.LIST)
                            .build();
    }

    public static ParsedQuery createHelpQuery(String helpTopic) {
        return new Builder(helpTopic).queryType(QueryType.HELP)
                                     .actionType(ActionType.HELP)
                                     .build();
    }

    /**
     * Performance metrics for query processing
     */
    public static class ProcessingMetrics {
        private final long processingTimeMs;
        private final int entitiesExtracted;
        private final int keywordsFound;
        private final double confidence;
        private final boolean successful;

        public ProcessingMetrics(long processingTimeMs, int entitiesExtracted, int keywordsFound, double confidence,
                                 boolean successful) {
            this.processingTimeMs = processingTimeMs;
            this.entitiesExtracted = entitiesExtracted;
            this.keywordsFound = keywordsFound;
            this.confidence = confidence;
            this.successful = successful;
        }

        public long getProcessingTimeMs() {
            return processingTimeMs;
        }

        public int getEntitiesExtracted() {
            return entitiesExtracted;
        }

        public int getKeywordsFound() {
            return keywordsFound;
        }

        public double getConfidence() {
            return confidence;
        }

        public boolean isSuccessful() {
            return successful;
        }

        @Override
        public String toString() {
            return String.format("ProcessingMetrics{time=%dms, entities=%d, keywords=%d, confidence=%.2f, success=%s}",
                                 processingTimeMs, entitiesExtracted, keywordsFound, confidence, successful);
        }
    }

    /**
     * Process query with timing metrics
     */
    public ProcessingMetrics processQueryWithMetrics() {
        long startTime = System.currentTimeMillis();

        try {
            processQuery();

            long endTime = System.currentTimeMillis();
            boolean successful = confidence > 0.3 && queryType != QueryType.UNKNOWN;

            return new ProcessingMetrics(endTime - startTime, getEntityCount(), extractedKeywords.size(), confidence,
                                         successful);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            return new ProcessingMetrics(endTime - startTime, 0, 0, 0.0, false);
        }
    }

    /**
     * Compare with another ParsedQuery for similarity
     */
    public double calculateSimilarity(ParsedQuery other) {
        if (other == null)
            return 0.0;

        double similarity = 0.0;
        int factors = 0;

        // Query type similarity
        if (this.queryType == other.queryType) {
            similarity += 0.3;
        }
        factors++;

        // Action type similarity
        if (this.actionType == other.actionType) {
            similarity += 0.2;
        }
        factors++;

        // Entity similarity
        double entitySimilarity = calculateEntitySimilarity(other);
        similarity += entitySimilarity * 0.4;
        factors++;

        // Keyword similarity
        double keywordSimilarity = calculateKeywordSimilarity(other);
        similarity += keywordSimilarity * 0.1;
        factors++;

        return factors > 0 ? similarity : 0.0;
    }

    /**
     * Calculate entity similarity with another ParsedQuery
     */
    private double calculateEntitySimilarity(ParsedQuery other) {
        int matches = 0;
        int total = 0;

        // Compare main entities
        if (Objects.equals(this.contractNumber, other.contractNumber))
            matches++;
        if (this.contractNumber != null || other.contractNumber != null)
            total++;

        if (Objects.equals(this.accountNumber, other.accountNumber))
            matches++;
        if (this.accountNumber != null || other.accountNumber != null)
            total++;

        if (Objects.equals(this.customerName, other.customerName))
            matches++;
        if (this.customerName != null || other.customerName != null)
            total++;

        if (Objects.equals(this.userName, other.userName))
            matches++;
        if (this.userName != null || other.userName != null)
            total++;

        if (Objects.equals(this.statusType, other.statusType))
            matches++;
        if (this.statusType != null || other.statusType != null)
            total++;

        return total > 0 ? (double) matches / total : 0.0;
    }

    /**
     * Calculate keyword similarity with another ParsedQuery
     */
    private double calculateKeywordSimilarity(ParsedQuery other) {
        if (this.extractedKeywords.isEmpty() && other.extractedKeywords.isEmpty()) {
            return 1.0;
        }

        Set<String> thisKeywords = new HashSet<>(this.extractedKeywords);
        Set<String> otherKeywords = new HashSet<>(other.extractedKeywords);

        Set<String> intersection = new HashSet<>(thisKeywords);
        intersection.retainAll(otherKeywords);

        Set<String> union = new HashSet<>(thisKeywords);
        union.addAll(otherKeywords);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * Generate unique hash for the query (useful for caching)
     */
    public String generateQueryHash() {
        StringBuilder hashInput = new StringBuilder();

        if (originalQuery != null)
            hashInput.append(originalQuery);
        hashInput.append("|").append(queryType);
        hashInput.append("|").append(actionType);

        // Add significant entities
        if (contractNumber != null)
            hashInput.append("|contract:").append(contractNumber);
        if (accountNumber != null)
            hashInput.append("|account:").append(accountNumber);
        if (customerName != null)
            hashInput.append("|customer:").append(customerName);
        if (userName != null)
            hashInput.append("|user:").append(userName);
        if (statusType != null)
            hashInput.append("|status:").append(statusType);

        return String.valueOf(hashInput.toString().hashCode());
    }

    /**
     * Check if query is cacheable (has stable entities)
     */
    public boolean isCacheable() {
        return contractNumber != null || (accountNumber != null || customerName != null) ||
               (queryType != QueryType.UNKNOWN && actionType != ActionType.UNKNOWN);
    }

    /**
     * Get cache key for this query
     */
    public String getCacheKey() {
        if (!isCacheable())
            return null;

        return "query_" + generateQueryHash();
    }

    /**
     * Validate query for specific operation
     */
    public ValidationResult validateForOperation(String operation) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        switch (operation.toLowerCase()) {
        case "create":
            if (accountNumber == null && customerName == null) {
                errors.add("Customer information required for contract creation");
            }
            if (contractName == null) {
                errors.add("Contract name required");
            }
            if (expirationDate == null) {
                warnings.add("Expiration date recommended");
            }
            break;

        case "show":
        case "display":
            if (contractNumber == null) {
                errors.add("Contract number required to show specific contract");
            }
            break;

        case "update":
            if (contractNumber == null) {
                errors.add("Contract number required for updates");
            }
            if (getEntityCount() < 2) {
                warnings.add("At least one field to update should be specified");
            }
            break;

        case "delete":
            if (contractNumber == null) {
                errors.add("Contract number required for deletion");
            }
            break;

        case "search":
            if (getEntityCount() == 0 && extractedKeywords.isEmpty()) {
                warnings.add("Search criteria recommended for better results");
            }
            break;

        case "filter":
            if (accountNumber == null && customerName == null && userName == null && statusType == null) {
                errors.add("At least one filter criterion required");
            }
            break;
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{valid=").append(valid);
            if (!errors.isEmpty()) {
                sb.append(", errors=").append(errors);
            }
            if (!warnings.isEmpty()) {
                sb.append(", warnings=").append(warnings);
            }
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * Create debug information string
     */
    public String getDebugInfo() {
        StringBuilder debug = new StringBuilder();

        debug.append("=== ParsedQuery Debug Info ===\n");
        debug.append("Original Query: ")
             .append(originalQuery)
             .append("\n");
        debug.append("Normalized Query: ")
             .append(normalizedQuery)
             .append("\n");
        debug.append("Query Type: ")
             .append(queryType)
             .append("\n");
        debug.append("Action Type: ")
             .append(actionType)
             .append("\n");
        debug.append("Confidence: ")
             .append(String.format("%.3f", confidence))
             .append("\n");
        debug.append("Timestamp: ")
             .append(timestamp)
             .append("\n");

        debug.append("\n--- Extracted Entities ---\n");
        Map<String, Object> entities = getAllEntities();
        if (entities.isEmpty()) {
            debug.append("No entities extracted\n");
        } else {
            entities.forEach((key, value) -> debug.append(String.format("%-15s: %s", key, value))
                                                  .append(entityConfidences.containsKey(key) ?
                                                          String.format(" (confidence: %.3f)",
                                                                        entityConfidences.get(key)) : "")
                                                  .append("\n"));
        }

        debug.append("\n--- Keywords ---\n");
        if (extractedKeywords.isEmpty()) {
            debug.append("No keywords extracted\n");
        } else {
            debug.append(String.join(", ", extractedKeywords)).append("\n");
        }

        debug.append("\n--- Validation ---\n");
        debug.append("Valid: ")
             .append(isValid())
             .append("\n");
        if (!validationErrors.isEmpty()) {
            debug.append("Errors: ")
                 .append(String.join(", ", validationErrors))
                 .append("\n");
        }
        if (!validationWarnings.isEmpty()) {
            debug.append("Warnings: ")
                 .append(String.join(", ", validationWarnings))
                 .append("\n");
        }

        debug.append("\n--- Statistics ---\n");
        EntityStatistics stats = getEntityStatistics();
        debug.append(stats.toString()).append("\n");
        debug.append("Completion: ")
             .append(String.format("%.1f%%", getCompletionPercentage() * 100))
             .append("\n");
        debug.append("High Quality: ")
             .append(stats.isHighQuality())
             .append("\n");
        debug.append("Cacheable: ")
             .append(isCacheable())
             .append("\n");

        if (!getRecommendations().isEmpty()) {
            debug.append("\n--- Recommendations ---\n");
            getRecommendations().forEach(rec -> debug.append("- ")
                                                     .append(rec)
                                                     .append("\n"));
        }

        return debug.toString();
    }

    /**
     * Main method for testing ParsedQuery functionality
     */
    public static void main(String[] args) {
        System.out.println("=== ParsedQuery Testing ===\n");

        // Test cases
        String[] testQueries = {
            "show contract 123456", "create contract for customer Boeing with price $50000 expires 2024-12-31",
            "list all active contracts", "search contracts by user john", "customer Microsoft account 10840607",
            "help with contract creation", "update contract 789012 status to completed",
            "expired contracts for customer Honeywell"
        };

        for (String query : testQueries) {
            System.out.println("Testing query: \"" + query + "\"");
            System.out.println("----------------------------------------");

            ParsedQuery parsedQuery = new ParsedQuery(query);
            ProcessingMetrics metrics = parsedQuery.processQueryWithMetrics();

            System.out.println("Processing Metrics: " + metrics);
            System.out.println("Summary: " + parsedQuery.generateSummary());

            // Test validation for different operations
            if (parsedQuery.isCreationQuery()) {
                ValidationResult createValidation = parsedQuery.validateForOperation("create");
                System.out.println("Create Validation: " + createValidation);
            }

            if (parsedQuery.isSpecificContractQuery()) {
                ValidationResult showValidation = parsedQuery.validateForOperation("show");
                System.out.println("Show Validation: " + showValidation);
            }

            System.out.println("Recommendations: " + parsedQuery.getRecommendations());
            System.out.println("JSON: " + parsedQuery.toJson());
            System.out.println("CSV: " + parsedQuery.toCsv());

            System.out.println("\n" + "========================================" + "\n");
        }

        // Test builder pattern
        System.out.println("=== Builder Pattern Test ===");
        ParsedQuery builtQuery = new ParsedQuery.Builder("create new contract").contractName("Service Agreement 2024")
                                                                               .customerName("Boeing")
                                                                               .accountNumber("10840607")
                                                                               .expirationDate("2024-12-31")
                                                                               .priceList("75000.00")
                                                                               .description("Annual service contract for Boeing")
                                                                               .build();

        System.out.println("Built Query Summary:");
        System.out.println(builtQuery.generateSummary());

        // Test similarity comparison
        System.out.println("=== Similarity Test ===");
        ParsedQuery query1 = new ParsedQuery("show contract 123456");
        query1.processQuery();

        ParsedQuery query2 = new ParsedQuery("display contract 123456");
        query2.processQuery();

        double similarity = query1.calculateSimilarity(query2);
        System.out.println("Similarity between queries: " + String.format("%.3f", similarity));

        // Test merge functionality
        System.out.println("=== Merge Test ===");
        ParsedQuery baseQuery = new ParsedQuery("contract 123456");
        baseQuery.processQuery();

        ParsedQuery additionalInfo = new ParsedQuery.Builder().customerName("Boeing")
                                                              .statusType("ACTIVE")
                                                              .build();

        System.out.println("Before merge: " + baseQuery.getEntityCount() + " entities");
        baseQuery.mergeWith(additionalInfo);
        System.out.println("After merge: " + baseQuery.getEntityCount() + " entities");
        System.out.println("Merged query: " + baseQuery.generateSummary());

        System.out.println("=== Testing Complete ===");
    }

    public static class FilterCriteria {
        private final String field;
        private final String operator;
        private final Object value;
        private final Object value2;
        
        public FilterCriteria(String field, String operator, Object value) {
            this(field, operator, value, null);
        }
        
        public FilterCriteria(String field, String operator, Object value, Object value2) {
            this.field = field;
            this.operator = operator;
            this.value = value;
            this.value2 = value2;
        }
        
        // Getters
        public String getField() { return field; }
        public String getOperator() { return operator; }
        public Object getValue() { return value; }
        public Object getValue2() { return value2; }
    }

    public void setFilters(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public List<FilterCriteria> getFilters() {
        return filters;
    }
}

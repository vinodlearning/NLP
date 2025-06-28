package view.nlp;
import java.util.*;
public class EnhancedNLPProcessor {
    
    private MLIntentClassifier intentClassifier;
    private AdvancedTypoHandler typoHandler;
    private GrammarEnforcer grammarEnforcer;
    private EntityResolver entityResolver;
    private QueryNormalizer queryNormalizer;
    
    // Configuration
    private static final String MODEL_BASE_PATH = 
        "C:\\JDeveloper\\mywork\\MYChatTest\\ViewController\\public_html\\models\\";
    
    public EnhancedNLPProcessor() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        try {
            this.typoHandler = new AdvancedTypoHandler();
            this.grammarEnforcer = new GrammarEnforcer();
            this.entityResolver = new EntityResolver(MODEL_BASE_PATH);
            this.queryNormalizer = new QueryNormalizer();
            this.intentClassifier = new MLIntentClassifier(MODEL_BASE_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize NLP components", e);
        }
    }
    
    /**
     * Main processing pipeline - coordinates all NLP steps
     */
    public ParsedQuery processQuery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return createEmptyQuery();
        }
        
        try {
            // Step 1: Text preprocessing and typo correction
            String correctedText = typoHandler.correctTypos(userInput);
            
            // Step 2: Grammar enforcement
            String grammaticalText = grammarEnforcer.enforceGrammar(correctedText);
            
            // Step 3: Query normalization
            String normalizedQuery = queryNormalizer.normalizeQuery(grammaticalText);
            
            // Step 4: Entity extraction
            Map<String, Object> entities = entityResolver.extractEntities(normalizedQuery);
            
            // Step 5: Intent classification
            ParsedQuery.QueryType intent = intentClassifier.classifyIntent(normalizedQuery, entities);
            
            // Step 6: Build parsed query
            ParsedQuery parsedQuery = buildParsedQuery(normalizedQuery, entities, intent);
            
            // Step 7: Calculate confidence
            double confidence = calculateOverallConfidence(parsedQuery, normalizedQuery);
            parsedQuery.setConfidence(confidence);
            
            // Step 8: Final validation
            validateQuery(parsedQuery, normalizedQuery);
            
            return parsedQuery;
            
        } catch (Exception e) {
            System.err.println("Error processing query: " + e.getMessage());
            return createErrorQuery(e.getMessage());
        }
    }
    
    private ParsedQuery buildParsedQuery(String query, Map<String, Object> entities, ParsedQuery.QueryType intent) {
        ParsedQuery parsedQuery = new ParsedQuery();
        
        // Set entities
        parsedQuery.setContractNumber((String) entities.get("contractNumber"));
        parsedQuery.setPartNumber((String) entities.get("partNumber"));
        parsedQuery.setUserName((String) entities.get("userName"));
        parsedQuery.setCustomerName((String) entities.get("customerName"));
        parsedQuery.setAccountNumber((String) entities.get("accountNumber"));
        parsedQuery.setStatusType((String) entities.get("statusType"));
        
        // Set query type
        parsedQuery.setQueryType(intent);
        
        // Set action type based on intent and query content
        ParsedQuery.ActionType actionType = determineActionType(intent, query, entities);
        parsedQuery.setActionType(actionType);
        
        return parsedQuery;
    }
    
    private ParsedQuery.ActionType determineActionType(ParsedQuery.QueryType intent, String query, Map<String, Object> entities) {
        String lowerQuery = query.toLowerCase();
        
        switch (intent) {
            case CONTRACT_INFO:
                if (lowerQuery.contains("show") || lowerQuery.contains("display")) {
                    return ParsedQuery.ActionType.SHOW;
                } else if (lowerQuery.contains("details")) {
                    return ParsedQuery.ActionType.DETAILS;
                }
                return ParsedQuery.ActionType.INFO;
                
            case PARTS_BY_CONTRACT:
            case FAILED_PARTS_BY_CONTRACT:
            case USER_CONTRACT_QUERY:
                return ParsedQuery.ActionType.LIST;
                
            case STATUS_CHECK:
            case CONTRACT_STATUS_CHECK:
                return ParsedQuery.ActionType.CHECK_STATUS;
                
            case PARTS_INFO:
                return determinePartsAction(lowerQuery);
                
            case HELP_CREATE_CONTRACT:
                return ParsedQuery.ActionType.CREATE;
                
            default:
                return ParsedQuery.ActionType.INFO;
        }
    }
    
    private ParsedQuery.ActionType determinePartsAction(String query) {
        if (query.contains("specifications") || query.contains("spec")) {
            return ParsedQuery.ActionType.GET_SPECIFICATIONS;
        } else if (query.contains("datasheet")) {
            return ParsedQuery.ActionType.GET_DATASHEET;
        } else if (query.contains("manufacturer")) {
            return ParsedQuery.ActionType.GET_MANUFACTURER;
        } else if (query.contains("stock")) {
            return ParsedQuery.ActionType.CHECK_STOCK;
        } else if (query.contains("lead") && query.contains("time")) {
            return ParsedQuery.ActionType.GET_LEAD_TIME;
        } else if (query.contains("warranty")) {
            return ParsedQuery.ActionType.GET_WARRANTY;
        } else if (query.contains("issues") || query.contains("defects")) {
            return ParsedQuery.ActionType.CHECK_ISSUES;
        }
        return ParsedQuery.ActionType.INFO;
    }
    
    private double calculateOverallConfidence(ParsedQuery query, String originalQuery) {
        double confidence = 0.3; // Base confidence
        
        // Entity extraction confidence
        if (query.getContractNumber() != null) confidence += 0.25;
        if (query.getPartNumber() != null) confidence += 0.25;
        if (query.getUserName() != null) confidence += 0.15;
        if (query.getCustomerName() != null) confidence += 0.15;
        if (query.getAccountNumber() != null) confidence += 0.1;
        
        // Query type confidence
        if (query.getQueryType() != ParsedQuery.QueryType.UNKNOWN) confidence += 0.2;
        
        // Pattern matching confidence
        if (hasStrongPatternMatch(query)) confidence += 0.1;
        
        return Math.min(confidence, 1.0);
    }
    
    private boolean hasStrongPatternMatch(ParsedQuery query) {
        return (query.getContractNumber() != null && 
                (query.getQueryType() == ParsedQuery.QueryType.CONTRACT_INFO ||
                 query.getQueryType() == ParsedQuery.QueryType.PARTS_BY_CONTRACT ||
                 query.getQueryType() == ParsedQuery.QueryType.STATUS_CHECK)) ||
               (query.getPartNumber() != null && 
                query.getQueryType() == ParsedQuery.QueryType.PARTS_INFO);
    }
    
    private void validateQuery(ParsedQuery query, String originalQuery) {
        // Remove invalid part numbers that are actually question words
        if (query.getPartNumber() != null && isQuestionWord(query.getPartNumber())) {
            query.setPartNumber(null);
        }
        
        // Clear inappropriate fields for general status queries
        if (query.getQueryType() == ParsedQuery.QueryType.CONTRACT_STATUS_CHECK &&
            (originalQuery.contains("expired contracts") || originalQuery.contains("active contracts"))) {
            query.setContractNumber(null);
            query.setUserName(null);
        }
    }
    
    private boolean isQuestionWord(String word) {
        String[] questionWords = {"what", "where", "when", "why", "who", "which", "how", 
                                 "is", "are", "was", "were", "can", "will", "show", "list"};
        String lowerWord = word.toLowerCase();
        for (String qw : questionWords) {
            if (qw.equals(lowerWord)) return true;
        }
        return false;
    }
    
    private ParsedQuery createEmptyQuery() {
        ParsedQuery query = new ParsedQuery();
        query.setQueryType(ParsedQuery.QueryType.UNKNOWN);
        query.setActionType(ParsedQuery.ActionType.INFO);
        query.setConfidence(0.0);
        return query;
    }
    
    private ParsedQuery createErrorQuery(String error) {
        ParsedQuery query = new ParsedQuery();
        query.setQueryType(ParsedQuery.QueryType.UNKNOWN);
        query.setActionType(ParsedQuery.ActionType.INFO);
        query.setConfidence(0.0);
        return query;
    }
    
    // Public methods for testing and debugging
    public String getDetailedParsingInfo(String input) {
        try {
            ParsedQuery query = processQuery(input);
            StringBuilder info = new StringBuilder();
            info.append("Input: ").append(input).append("\n");
            info.append("Query Type: ").append(query.getQueryType()).append("\n");
            info.append("Action Type: ").append(query.getActionType()).append("\n");
            info.append("Contract Number: ").append(query.getContractNumber()).append("\n");
            info.append("Part Number: ").append(query.getPartNumber()).append("\n");
            info.append("User Name: ").append(query.getUserName()).append("\n");
            info.append("Customer Name: ").append(query.getCustomerName()).append("\n");
            info.append("Account Number: ").append(query.getAccountNumber()).append("\n");
            info.append("Confidence: ").append(String.format("%.2f", query.getConfidence())).append("\n");
            return info.toString();
        } catch (Exception e) {
            return "Error getting parsing info: " + e.getMessage();
        }
    }
    
    public Map<String, String> getExtractedEntities(String input) {
        try {
            ParsedQuery query = processQuery(input);
            Map<String, String> entities = new HashMap<>();
            if (query.getContractNumber() != null) entities.put("contractNumber", query.getContractNumber());
            if (query.getPartNumber() != null) entities.put("partNumber", query.getPartNumber());
            if (query.getUserName() != null) entities.put("userName", query.getUserName());
            if (query.getCustomerName() != null) entities.put("customerName", query.getCustomerName());
            if (query.getAccountNumber() != null) entities.put("accountNumber", query.getAccountNumber());
            return entities;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
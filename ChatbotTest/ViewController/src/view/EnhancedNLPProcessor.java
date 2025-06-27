//package view;
//
//import java.util.*;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;
//
//public class EnhancedNLPProcessor {
//    private MLIntentClassifier intentClassifier;
//    private AdvancedTypoHandler typoHandler;
//    private GrammarEnforcer grammarEnforcer;
//    private EntityResolver entityResolver;
//    private QueryNormalizer queryNormalizer;
//    private ContextManager contextManager;
//    private ChatModelServiceImpl chatModelService; // Your existing service
//    
//    // Pattern-based processors for lightweight processing
//    private Map<ParsedQuery.QueryType, List<PatternRule>> queryPatterns;
//    private Map<String, EntityExtractor> entityExtractors;
//    
//    public EnhancedNLPProcessor() {
//        initializePatternProcessors();
//    }
//    
//    private void initializePatternProcessors() {
//        queryPatterns = new HashMap<>();
//        entityExtractors = new HashMap<>();
//        
//        // CONTRACT_INFO patterns
//        queryPatterns.put(ParsedQuery.QueryType.CONTRACT_INFO, Arrays.asList(
//            new PatternRule("(?i).*(show|display|get|find)\\s+(contract|agreement|order)\\s+([A-Z0-9]{6,}).*", 
//                           ParsedQuery.ActionType.SHOW, 0.95),
//            new PatternRule("(?i).*contract\\s+([A-Z0-9]{6,}).*details.*", 
//                           ParsedQuery.ActionType.DETAILS, 0.90),
//            new PatternRule("(?i).*([A-Z0-9]{6,})\\s+contract.*info.*", 
//                           ParsedQuery.ActionType.INFO, 0.85)
//        ));
//        
//        // USER_CONTRACT_QUERY patterns
//        queryPatterns.put(ParsedQuery.QueryType.USER_CONTRACT_QUERY, Arrays.asList(
//            new PatternRule("(?i).*(\\w+)\\s+(contracts?|agreements?|orders?).*", 
//                           ParsedQuery.ActionType.LIST, 0.90),
//            new PatternRule("(?i).*contracts?\\s+(by|for|of)\\s+(\\w+).*", 
//                           ParsedQuery.ActionType.FIND, 0.85)
//        ));
//        
//        // PARTS_INFO patterns
//        queryPatterns.put(ParsedQuery.QueryType.PARTS_INFO, Arrays.asList(
//            new PatternRule("(?i).*(spec|specification)s?.*(?:product|part).*(?:id|number)\\s+([A-Z]{2}\\d{3,}).*", 
//                           ParsedQuery.ActionType.GET_SPECIFICATIONS, 0.95),
//            new PatternRule("(?i).*(datasheet|details).*(?:for|of)\\s+([A-Z]{2}\\d{3,}).*", 
//                           ParsedQuery.ActionType.GET_DATASHEET, 0.95),
//            new PatternRule("(?i).*([A-Z]{2}\\d{3,})\\s+(active|discontinued).*", 
//                           ParsedQuery.ActionType.CHECK_ACTIVE, 0.90),
//            new PatternRule("(?i).*([A-Z]{2}\\d{3,})\\s+(available|stock).*", 
//                           ParsedQuery.ActionType.CHECK_STOCK, 0.90),
//            new PatternRule("(?i).*lead\\s+time.*(?:for|of)\\s+([A-Z]{2}\\d{3,}).*", 
//                           ParsedQuery.ActionType.GET_LEAD_TIME, 0.90)
//        ));
//        
//        // PARTS_BY_CONTRACT patterns
//        queryPatterns.put(ParsedQuery.QueryType.PARTS_BY_CONTRACT, Arrays.asList(
//            new PatternRule("(?i).*(show|get|list)\\s+(parts?|components?)\\s+(of|for|in)\\s+([A-Z0-9]{6,}).*", 
//                           ParsedQuery.ActionType.LIST, 0.95),
//            new PatternRule("(?i).*parts?\\s+([A-Z0-9]{6,}).*", 
//                           ParsedQuery.ActionType.SHOW, 0.85),
//            new PatternRule("(?i).*how\\s+many\\s+parts.*([A-Z0-9]{6,}).*", 
//                           ParsedQuery.ActionType.COUNT_PARTS, 0.90)
//        ));
//        
//        // FAILED_PARTS_BY_CONTRACT patterns
//        queryPatterns.put(ParsedQuery.QueryType.FAILED_PARTS_BY_CONTRACT, Arrays.asList(
//            new PatternRule("(?i).*(failed|broken|defective|faulty)\\s+(parts?|components?).*(?:of|for|in)\\s+([A-Z0-9]{6,}).*", 
//                           ParsedQuery.ActionType.LIST, 0.95)
//        ));
//        
//        // STATUS_CHECK patterns
//        queryPatterns.put(ParsedQuery.QueryType.STATUS_CHECK, Arrays.asList(
//            new PatternRule("(?i).*status\\s+(?:of|for)\\s+([A-Z0-9]{6,}).*", 
//                           ParsedQuery.ActionType.CHECK_STATUS, 0.95)
//        ));
//        
//        // ACCOUNT_INFO patterns
//        queryPatterns.put(ParsedQuery.QueryType.ACCOUNT_INFO, Arrays.asList(
//            new PatternRule("(?i).*account\\s+(\\d{8,}).*", 
//                           ParsedQuery.ActionType.SHOW, 0.95)
//        ));
//        
//        // Initialize entity extractors
//        initializeEntityExtractors();
//    }
//    
//    private void initializeEntityExtractors() {
//        entityExtractors.put("contractNumber", new EntityExtractor(
//            Pattern.compile("\\b([A-Z0-9]{6,})\\b"), "CONTRACT"
//        ));
//        
//        entityExtractors.put("partNumber", new EntityExtractor(
//            Pattern.compile("\\b([A-Z]{2}\\d{3,})\\b"), "PART"
//        ));
//        
//        entityExtractors.put("accountNumber", new EntityExtractor(
//            Pattern.compile("\\b(\\d{8,})\\b"), "ACCOUNT"
//        ));
//        
//        entityExtractors.put("username", new EntityExtractor(
//            Pattern.compile("(?i)\\b([a-zA-Z]{3,})(?=\\s+(?:contracts?|agreements?|orders?))\\b"), "USER"
//        ));
//    }
//
//    public ParsedQuery processUserInput(String userInput, String sessionId, String username) {
//        try {
//            System.out.println("Processing input: " + userInput + " for user: " + username);
//            
//            // 1. Context-aware preprocessing (if contextManager is available)
//            String contextualInput = userInput;
//            if (contextManager != null) {
//                contextualInput = contextManager.enrichWithContext(userInput, sessionId);
//            }
//            
//            // 2. Typo correction with domain-specific terms (if typoHandler is available)
//            String correctedInput = contextualInput;
//            if (typoHandler != null) {
//                correctedInput = typoHandler.correctTypos(contextualInput);
//                System.out.println("After typo correction: " + correctedInput);
//            }
//            
//            // 3. Grammar enforcement (if grammarEnforcer is available)
//            String grammaticalInput = correctedInput;
//            if (grammarEnforcer != null) {
//                grammaticalInput = grammarEnforcer.enforceGrammar(correctedInput);
//                System.out.println("After grammar correction: " + grammaticalInput);
//            }
//            
//            // 4. Query normalization (if queryNormalizer is available)
//            String normalizedQuery = grammaticalInput;
//            if (queryNormalizer != null) {
//                normalizedQuery = queryNormalizer.normalize(grammaticalInput);
//            } else {
//                // Simple normalization
//                normalizedQuery = grammaticalInput.toLowerCase().trim();
//            }
//            
//            // 5. Pattern-based processing (lightweight approach)
//            ParsedQuery parsedQuery = processWithPatterns(normalizedQuery, userInput, sessionId, username);
//            
//            // 6. If ML classifier is available, use it for validation/enhancement
//            if (intentClassifier != null) {
//                enhanceWithMLClassifier(parsedQuery, normalizedQuery);
//            }
//            
//            // 7. Entity extraction enhancement (if entityResolver is available)
//            if (entityResolver != null) {
//                enhanceEntityExtraction(parsedQuery, normalizedQuery);
//            }
//            
//            // 8. Update context (if contextManager is available)
//            if (contextManager != null) {
//                // Convert ParsedQuery to legacy format for context manager
//                Map<String, Object> entities = createEntityMap(parsedQuery);
//                contextManager.updateContext(sessionId, convertToIntent(parsedQuery.getQueryType()), entities);
//            }
//            
//            return parsedQuery;
//            
//        } catch (Exception e) {
//            System.out.println("Error in NLP processing: " + e.getMessage());
//            e.printStackTrace();
//            return createFallbackQuery(userInput, sessionId, username);
//        }
//    }
//    
//    private ParsedQuery processWithPatterns(String normalizedQuery, String originalInput, String sessionId, String username) {
//        // Find best matching query type and action
//        QueryMatch bestMatch = findBestMatch(normalizedQuery);
//        
//        // Extract all entities
//        Map<String, Object> entities = extractAllEntities(originalInput);
//        
//        // Build ParsedQuery
//        ParsedQuery parsedQuery = new ParsedQuery();
//        parsedQuery.setOriginalQuery(originalInput);
//        parsedQuery.setCorrectedQuery(normalizedQuery);
//        parsedQuery.setQueryType(bestMatch.getQueryType());
//        parsedQuery.setActionType(bestMatch.getActionType());
//        parsedQuery.setConfidence(bestMatch.getConfidence());
//        
//        // Set extracted entities
//        setExtractedEntities(parsedQuery, entities);
//        
//        // Set query parameters
//        Map<String, String> queryParams = new HashMap<>();
//        queryParams.put("sessionId", sessionId);
//        queryParams.put("requestingUser", username);
//        queryParams.put("processingTime", String.valueOf(System.currentTimeMillis()));
//        parsedQuery.setQueryParameters(queryParams);
//        
//        return parsedQuery;
//    }
//    
//    private QueryMatch findBestMatch(String input) {
//        QueryMatch bestMatch = new QueryMatch(ParsedQuery.QueryType.UNKNOWN, ParsedQuery.ActionType.SHOW, 0.0);
//        
//        for (Map.Entry<ParsedQuery.QueryType, List<PatternRule>> entry : queryPatterns.entrySet()) {
//            for (PatternRule rule : entry.getValue()) {
//                Pattern pattern = Pattern.compile(rule.getRegex());
//                if (pattern.matcher(input).matches()) {
//                    if (rule.getConfidence() > bestMatch.getConfidence()) {
//                        bestMatch = new QueryMatch(entry.getKey(), rule.getActionType(), rule.getConfidence());
//                    }
//                }
//            }
//        }
//        
//        return bestMatch;
//    }
//    
//    private Map<String, Object> extractAllEntities(String input) {
//        Map<String, Object> entities = new HashMap<>();
//        
//        for (Map.Entry<String, EntityExtractor> entry : entityExtractors.entrySet()) {
//            EntityExtractor extractor = entry.getValue();
//            Matcher matcher = extractor.getPattern().matcher(input);
//            
//            if (matcher.find()) {
//                entities.put(entry.getKey(), matcher.group(1));
//                entities.put(entry.getKey() + "_type", extractor.getType());
//            }
//        }
//        
//        return entities;
//    }
//    
//    private void setExtractedEntities(ParsedQuery parsedQuery, Map<String, Object> entities) {
//        // Set specific entity fields
//        if (entities.containsKey("contractNumber")) {
//            parsedQuery.setContractNumber((String) entities.get("contractNumber"));
//        }
//        if (entities.containsKey("partNumber")) {
//            parsedQuery.setPartNumber((String) entities.get("partNumber"));
//        }
//        if (entities.containsKey("accountNumber")) {
//            parsedQuery.setAccountNumber((String) entities.get("accountNumber"));
//        }
//        if (entities.containsKey("username")) {
//            parsedQuery.setUserName((String) entities.get("username"));
//        }
//        
//        // Set extracted entities list
//        List<String> extractedList = new ArrayList<>();
//        for (Map.Entry<String, Object> entry : entities.entrySet()) {
//            if (!entry.getKey().endsWith("_type")) {
//                extractedList.add(entry.getKey() + ":" + entry.getValue());
//            }
//        }
//        parsedQuery.setExtractedEntities(extractedList);
//    }
//    
//    private void enhanceWithMLClassifier(ParsedQuery parsedQuery, String normalizedQuery) {
//        try {
//            // Use ML classifier to validate/enhance the pattern-based result
//            Intent mlIntent = intentClassifier.classifyIntent(normalizedQuery);
//            
//            // If ML confidence is higher, consider updating the result
//            // This is where you can add logic to combine pattern-based and ML results
//            System.out.println("ML Intent classification: " + mlIntent);
//            
//        } catch (Exception e) {
//            System.out.println("ML enhancement failed: " + e.getMessage());
//        }
//    }
//    
//    private void enhanceEntityExtraction(ParsedQuery parsedQuery, String normalizedQuery) {
//        try {
//            // Use advanced entity resolver if available
//            Map<String, Object> mlEntities = entityResolver.extractEntities(normalizedQuery, 
//                convertToIntent(parsedQuery.getQueryType()));
//            
//            // Merge with existing entities
//            // Add logic here to enhance entity extraction
//            System.out.println("Enhanced entities: " + mlEntities);
//            
//        } catch (Exception e) {
//            System.out.println("Entity enhancement failed: " + e.getMessage());
//        }
//    }
//    
//    private Map<String, Object> createEntityMap(ParsedQuery parsedQuery) {
//        Map<String, Object> entities = new HashMap<>();
//        
//        if (parsedQuery.getContractNumber() != null) {
//            entities.put("contractNumber", parsedQuery.getContractNumber());
//        }
//        if (parsedQuery.getPartNumber() != null) {
//            entities.put("partNumber", parsedQuery.getPartNumber());
//        }
//        if (parsedQuery.getAccountNumber() != null) {
//            entities.put("accountNumber", parsedQuery.getAccountNumber());
//        }
//        if (parsedQuery.getUserName() != null) {
//            entities.put("username", parsedQuery.getUserName());
//        }
//        
//        return entities;
//    }
//    
//    private Intent convertToIntent(ParsedQuery.QueryType queryType) {
//        // Convert ParsedQuery.QueryType to Intent enum for backward compatibility
//        switch (queryType) {
//            case CONTRACT_INFO: return Intent.CONTRACT_DETAILS;
//            case USER_CONTRACT_QUERY: return Intent.CONTRACT_BY_USER;
//            case PARTS_INFO: return Intent.PARTS_SPECIFICATIONS;
//            case PARTS_BY_CONTRACT: return Intent.PARTS_BY_CONTRACT;
//            case FAILED_PARTS: return Intent.FAILED_PARTS_ANALYSIS;
//            case FAILED_PARTS_BY_CONTRACT: return Intent.FAILED_PARTS_BY_CONTRACT;
//            case STATUS_CHECK: return Intent.PARTS_STATUS;
//            case ACCOUNT_INFO: return Intent.CONTRACT_BY_ACCOUNT;
//            case HELP_CREATE_CONTRACT: return Intent.HELP_CREATE_CONTRACT;
//            default: return Intent.UNKNOWN;
//        }
//    }
//    
//    private ParsedQuery createFallbackQuery(String input, String sessionId, String username) {
//        ParsedQuery parsedQuery = new ParsedQuery();
//        parsedQuery.setOriginalQuery(input);
//        parsedQuery.setCorrectedQuery(input.toLowerCase());
//        parsedQuery.setQueryType(ParsedQuery.QueryType.UNKNOWN);
//        parsedQuery.setActionType(ParsedQuery.ActionType.SHOW);
//        parsedQuery.setConfidence(0.3);
//        parsedQuery.setAmbiguous(true);
//        
//        // Add suggestions for unknown queries
//        List<String> suggestions = Arrays.asList(
//            "Try: 'show contract 123456'",
//            "Try: 'john contracts'", 
//            "Try: 'specifications of AE125'",
//            "Try: 'failed parts of 123456'",
//            "Try: 'account 10840607'"
//        );
//        parsedQuery.setSuggestions(suggestions);
//        
//        // Set query parameters
//        Map<String, String> queryParams = new HashMap<>();
//        queryParams.put("sessionId", sessionId);
//        queryParams.put("requestingUser", username);
//        queryParams.put("errorType", "UNKNOWN_QUERY");
//        parsedQuery.setQueryParameters(queryParams);
//        
//        return parsedQuery;
//    }
//    
//    // Getters and setters for dependency injection
//    public void setIntentClassifier(MLIntentClassifier intentClassifier) {
//        this.intentClassifier = intentClassifier;
//    }
//    
//    public void setTypoHandler(AdvancedTypoHandler typoHandler) {
//        this.typoHandler = typoHandler;
//    }
//    
//    public void setGrammarEnforcer(GrammarEnforcer grammarEnforcer) {
//        this.grammarEnforcer = grammarEnforcer;
//    }
//    
//    public void setEntityResolver(EntityResolver entityResolver) {
//        this.entityResolver = entityResolver;
//    }
//    
//    public void setQueryNormalizer(QueryNormalizer queryNormalizer) {
//        this.queryNormalizer = queryNormalizer;
//    }
//    
//    public void setContextManager(ContextManager contextManager) {
//        this.contextManager = contextManager;
//    }
//    
//    public void setChatModelService(ChatModelServiceImpl chatModelService) {
//        this.chatModelService = chatModelService;
//    }
//}
//
//// Supporting classes
//class PatternRule {
//    private String regex;
//    private ParsedQuery.ActionType actionType;
//    private double confidence;
//    
//    public PatternRule(String regex, ParsedQuery.ActionType actionType, double confidence) {
//        this.regex = regex;
//        this.actionType = actionType;
//        this.confidence = confidence;
//    }
//    
//    public String getRegex() { return regex; }
//    public ParsedQuery.ActionType getActionType() { return actionType; }
//    public double getConfidence() { return confidence; }
//}
//
//class QueryMatch {
//    private ParsedQuery.QueryType queryType;
//    private ParsedQuery.ActionType actionType;
//    private double confidence;
//    
//    public QueryMatch(ParsedQuery.QueryType queryType, ParsedQuery.ActionType actionType, double confidence) {
//        this.queryType = queryType;
//        this.actionType = actionType;
//        this.confidence = confidence;
//    }
//    
//    public ParsedQuery.QueryType getQueryType() { return queryType; }
//    public ParsedQuery.ActionType getActionType() { return actionType; }
//    public double getConfidence() { return confidence; }
//}
//
//class EntityExtractor {
//    private Pattern pattern;
//    private String type;
//    
//    public EntityExtractor(Pattern pattern, String type) {
//        this.pattern = pattern;
//        this.type = type;
//    }
//    
//    public Pattern getPattern() { return pattern; }
//    public String getType() { return type; }
//}
package view;

import java.util.HashMap;
import java.util.Map;

public class EnhancedNLPProcessor {
    
    private OpenNLPProcessor openNLPProcessor;
    private MLIntentClassifier intentClassifier;
    
    public EnhancedNLPProcessor() {
        this.openNLPProcessor = new OpenNLPProcessor();
        this.intentClassifier = new MLIntentClassifier();
    }
    
    public ParsedQuery processQuery(String query) {
        try {
            System.out.println("Processing query with OpenNLP: " + query);
            
            // Step 1: Use OpenNLP for advanced processing
            ParsedQuery parsedQuery = openNLPProcessor.processQuery(query);
            
            // Step 2: Enhance with ML classifier
            enhanceWithMLClassifier(parsedQuery, query);
            
            // Step 3: Validate and finalize
            validateAndFinalize(parsedQuery);
            
            System.out.println("Final parsed query: " + parsedQuery.getQueryType() + 
                             " (confidence: " + parsedQuery.getConfidence() + ")");
            
            return parsedQuery;
            
        } catch (Exception e) {
            System.err.println("Error in enhanced NLP processing: " + e.getMessage());
            return createErrorQuery(query, e.getMessage());
        }
    }
    
    private void enhanceWithMLClassifier(ParsedQuery parsedQuery, String normalizedQuery) {
        try {
            // Use ML classifier to validate/enhance the OpenNLP result
            Intent mlIntent = intentClassifier.classifyIntent(normalizedQuery);
            
            // If ML confidence is significantly higher, consider updating the result
            if (mlIntent.getConfidence() > parsedQuery.getConfidence() + 0.1) {
                System.out.println("ML classifier suggests different intent: " + mlIntent.getQueryType() + 
                                 " (confidence: " + mlIntent.getConfidence() + ")");
                
                // You can choose to override or blend the results
                parsedQuery.setQueryType(mlIntent.getQueryType());
                parsedQuery.setConfidence((parsedQuery.getConfidence() + mlIntent.getConfidence()) / 2);
            }
            
            System.out.println("ML Intent classification: " + mlIntent);
            
        } catch (Exception e) {
            System.out.println("ML enhancement failed: " + e.getMessage());
        }
    }
    
    private void validateAndFinalize(ParsedQuery parsedQuery) {
        // Ensure minimum confidence threshold
        if (parsedQuery.getConfidence() < 0.3) {
            parsedQuery.setQueryType(QueryType.UNKNOWN);
            parsedQuery.setConfidence(0.3);
        }
        
        // Validate that required parameters are present for certain query types
        switch (parsedQuery.getQueryType()) {
            case CONTRACT_INFO:
            case PARTS_BY_CONTRACT:
            case FAILED_PARTS_BY_CONTRACT:
                if (parsedQuery.getContractNumber() == null) {
                    // Lower confidence if expected parameter is missing
                    parsedQuery.setConfidence(parsedQuery.getConfidence() * 0.7);
                }
                break;
            case PARTS_INFO:
                if (parsedQuery.getPartNumber() == null) {
                    parsedQuery.setConfidence(parsedQuery.getConfidence() * 0.7);
                }
                break;
            case USER_CONTRACT_QUERY:
                if (parsedQuery.getUserName() == null) {
                    parsedQuery.setConfidence(parsedQuery.getConfidence() * 0.7);
                }
                break;
            case ACCOUNT_INFO:
                if (parsedQuery.getAccountNumner() == null) {
                    parsedQuery.setConfidence(parsedQuery.getConfidence() * 0.7);
                }
                break;
        }
    }
    
    private ParsedQuery createErrorQuery(String originalQuery, String errorMessage) {
        ParsedQuery errorQuery = new ParsedQuery();
        errorQuery.setOriginalQuery(originalQuery);
        errorQuery.setQueryType(QueryType.UNKNOWN);
        errorQuery.setConfidence(0.0);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("error", errorMessage);
        parameters.put("requestingUser", "current_user");
        errorQuery.setQueryParameters(parameters);
        
        return errorQuery;
    }
    
    // Cleanup method
    public void cleanup() {
        if (openNLPProcessor != null) {
            openNLPProcessor.cleanup();
        }
    }
}
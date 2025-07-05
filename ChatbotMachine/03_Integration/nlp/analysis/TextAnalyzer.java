package nlp.analysis;

import nlp.core.QueryStructure;
import nlp.core.QueryStructure.QueryType;
import nlp.core.QueryStructure.ActionType;
import nlp.core.QueryStructure.QueryOperator;
import nlp.correction.SpellChecker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Text Analyzer - Determines QueryType and extracts entities/operators
 * 
 * Processes corrected text to:
 * - Determine query type (contract/parts/help)
 * - Extract entities (requested fields to display)
 * - Parse operators (filter conditions)
 * - Identify action types
 * 
 * @author Contract/Parts Search System
 * @version 1.0
 */
public class TextAnalyzer {
    
    private SpellChecker spellChecker;
    
    // Entity extraction patterns
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("\\b\\d{4,8}\\b");
    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile("\\b[A-Z]{2,3}\\d{3,6}\\b");
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\b\\d{6,12}\\b");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b\\d{4}[-/]\\d{1,2}[-/]\\d{1,2}\\b|\\b\\d{1,2}[-/]\\d{1,2}[-/]\\d{4}\\b|\\b\\d{4}\\b");
    private static final Pattern USER_NAME_PATTERN = Pattern.compile("\\b[a-zA-Z]{3,}\\b");
    
    // Operator patterns
    private static final Pattern AFTER_PATTERN = Pattern.compile("after\\s+(\\d{4}(?:[-/]\\d{1,2}[-/]\\d{1,2})?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEFORE_PATTERN = Pattern.compile("before\\s+(\\d{4}(?:[-/]\\d{1,2}[-/]\\d{1,2})?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BETWEEN_PATTERN = Pattern.compile("between\\s+(\\d{4})\\s+(?:and\\s+)?(\\d{4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern CREATED_BY_PATTERN = Pattern.compile("(?:created\\s+by|by)\\s+([a-zA-Z]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern STATUS_PATTERN = Pattern.compile("status\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile("(?:customer|client)\\s+([a-zA-Z\\s]+?)(?:\\s|$)", Pattern.CASE_INSENSITIVE);
    
    // Keyword sets for classification
    private Set<String> contractKeywords;
    private Set<String> partsKeywords;
    private Set<String> helpKeywords;
    private Set<String> actionKeywords;
    
    // Default entities to return
    private Set<String> defaultContractEntities;
    private Set<String> defaultPartsEntities;
    
    public TextAnalyzer() {
        this.spellChecker = SpellChecker.getInstance();
        initializeKeywords();
        initializeDefaultEntities();
    }
    
    /**
     * Initialize keyword sets for classification
     */
    private void initializeKeywords() {
        contractKeywords = new HashSet<>(Arrays.asList(
            "contract", "contracts", "agreement", "agreements", "deal", "deals",
            "contract_number", "contract_id"
        ));
        
        partsKeywords = new HashSet<>(Arrays.asList(
            "part", "parts", "component", "components", "item", "items",
            "product", "products", "material", "materials", "supply", "supplies",
            "part_number", "part_id"
        ));
        
        helpKeywords = new HashSet<>(Arrays.asList(
            "help", "how", "guide", "instruction", "tutorial", "create", "make",
            "add", "new", "assist", "support", "explain"
        ));
        
        actionKeywords = new HashSet<>(Arrays.asList(
            "show", "display", "list", "get", "find", "search", "retrieve",
            "pull", "fetch", "bring", "grab"
        ));
    }
    
    /**
     * Initialize default entities to return for each query type
     */
    private void initializeDefaultEntities() {
        defaultContractEntities = new HashSet<>(Arrays.asList(
            "contract_number", "customer_name", "account_number", "created_by",
            "created_date", "effective_date", "expiration_date", "status", "amount"
        ));
        
        defaultPartsEntities = new HashSet<>(Arrays.asList(
            "part_number", "contract_number", "customer_name", "created_by",
            "created_date", "status", "description", "amount"
        ));
    }
    
    /**
     * MAIN METHOD: Analyze corrected text and populate QueryStructure
     */
    public QueryStructure analyzeText(String correctedText, String originalText) {
        if (correctedText == null || correctedText.trim().isEmpty()) {
            QueryStructure structure = new QueryStructure();
            structure.setOriginalQuery(originalText);
            structure.setCorrectedQuery(correctedText);
            structure.addError("Empty or null query text");
            return structure;
        }
        
        long startTime = System.currentTimeMillis();
        
        // Create query structure
        QueryStructure structure = new QueryStructure();
        structure.setOriginalQuery(originalText);
        structure.setCorrectedQuery(correctedText);
        structure.setHasSpellCorrections(!originalText.equals(correctedText));
        
        try {
            // Step 1: Determine query type
            QueryType queryType = determineQueryType(correctedText);
            structure.setQueryType(queryType);
            
            // Step 2: Determine action type
            ActionType actionType = determineActionType(correctedText, queryType);
            structure.setActionType(actionType);
            
            // Step 3: Extract entities
            extractEntities(correctedText, structure);
            
            // Step 4: Parse operators
            parseOperators(correctedText, structure);
            
            // Step 5: Set default entities if none requested
            setDefaultEntities(structure);
            
            // Step 6: Calculate confidence
            double confidence = calculateConfidence(structure);
            structure.setConfidence(confidence);
            
            // Step 7: Validate and suggest improvements
            validateAndSuggest(structure);
            
        } catch (Exception e) {
            structure.addError("Analysis error: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        structure.setProcessingTime(endTime - startTime);
        
        return structure;
    }
    
    /**
     * Determine the primary query type
     */
    private QueryType determineQueryType(String text) {
        String lowerText = text.toLowerCase();
        
        int contractScore = 0;
        int partsScore = 0;
        int helpScore = 0;
        
        // Enhanced scoring logic with context awareness
        
        // Check for explicit parts queries first
        if (lowerText.startsWith("parts ") || lowerText.startsWith("part ") || 
            lowerText.contains("show part") || lowerText.contains("list parts") ||
            lowerText.contains("get part") || lowerText.contains("find parts")) {
            partsScore += 5;
        }
        
        // Check for explicit contract queries
        if (lowerText.startsWith("contracts ") || lowerText.startsWith("contract ") ||
            lowerText.contains("show contract") || lowerText.contains("list contracts") ||
            lowerText.contains("get contract") || lowerText.contains("find contracts")) {
            contractScore += 5;
        }
        
        // Score based on keyword presence
        for (String keyword : contractKeywords) {
            if (lowerText.contains(keyword)) {
                contractScore += keyword.equals("contract") || keyword.equals("contracts") ? 3 : 1;
            }
        }
        
        for (String keyword : partsKeywords) {
            if (lowerText.contains(keyword)) {
                partsScore += keyword.equals("part") || keyword.equals("parts") ? 3 : 1;
            }
        }
        
        for (String keyword : helpKeywords) {
            if (lowerText.contains(keyword)) {
                helpScore += keyword.equals("help") || keyword.equals("how") ? 3 : 1;
            }
        }
        
        // Additional scoring based on patterns
        if (CONTRACT_NUMBER_PATTERN.matcher(text).find()) {
            // Don't automatically give contract score if it's clearly a parts query
            if (!lowerText.startsWith("parts") && !lowerText.startsWith("part")) {
                contractScore += 2;
            }
        }
        
        if (PART_NUMBER_PATTERN.matcher(text).find()) {
            partsScore += 3; // Part numbers are strong indicators
        }
        
        if (lowerText.contains("create") || lowerText.contains("make") || lowerText.contains("new")) {
            helpScore += 2;
        }
        
        // Context-specific adjustments
        if (lowerText.contains("parts for contract") || lowerText.contains("part for contract")) {
            partsScore += 3; // This is clearly a parts query
        }
        
        if (lowerText.contains("contracts containing part") || lowerText.contains("contracts with part")) {
            contractScore += 3; // This is clearly a contract query
        }
        
        // Determine highest score
        if (helpScore > contractScore && helpScore > partsScore) {
            return QueryType.HELP;
        } else if (partsScore > contractScore) {
            return QueryType.PARTS;
        } else if (contractScore > 0) {
            return QueryType.CONTRACT;
        } else {
            return QueryType.UNKNOWN;
        }
    }
    
    /**
     * Determine the specific action type based on query type and content
     */
    private ActionType determineActionType(String text, QueryType queryType) {
        String lowerText = text.toLowerCase();
        
        switch (queryType) {
            case CONTRACT:
                // Check for user-based queries first (higher priority)
                if (CREATED_BY_PATTERN.matcher(text).find()) {
                    return ActionType.CONTRACTS_BY_USER;
                }
                // Check for customer-based queries
                if (lowerText.contains("customer") || lowerText.contains("client")) {
                    return ActionType.CONTRACTS_BY_CUSTOMER_NAME;
                }
                // Check for account-based queries
                if (lowerText.contains("account") && ACCOUNT_NUMBER_PATTERN.matcher(text).find()) {
                    return ActionType.CONTRACTS_BY_ACCOUNT_NUMBER;
                }
                // Check for parts-based contract queries
                if ((lowerText.contains("part") || lowerText.contains("component")) && 
                    (lowerText.contains("containing") || lowerText.contains("with"))) {
                    return ActionType.CONTRACTS_BY_PARTS;
                }
                // Check for contract number queries
                if (CONTRACT_NUMBER_PATTERN.matcher(text).find()) {
                    return ActionType.CONTRACTS_BY_CONTRACT_NUMBER;
                }
                return ActionType.CONTRACTS_BY_CONTRACT_NUMBER; // Default
                
            case PARTS:
                // Check for user-based parts queries first
                if (CREATED_BY_PATTERN.matcher(text).find()) {
                    return ActionType.PARTS_BY_USER;
                }
                // Check for customer-based parts queries
                if (lowerText.contains("customer") || lowerText.contains("client")) {
                    return ActionType.PARTS_BY_CUSTOMER;
                }
                // Check for part number queries
                if (PART_NUMBER_PATTERN.matcher(text).find()) {
                    return ActionType.PARTS_BY_PART_NUMBER;
                }
                // Check for contract-based parts queries
                if (CONTRACT_NUMBER_PATTERN.matcher(text).find() || 
                    lowerText.contains("contract")) {
                    return ActionType.PARTS_BY_CONTRACT;
                }
                return ActionType.PARTS_BY_CONTRACT; // Default
                
            case HELP:
                if (lowerText.contains("contract") && (lowerText.contains("create") || lowerText.contains("make"))) {
                    return ActionType.HELP_CONTRACT_CREATION;
                }
                if (lowerText.contains("part") && lowerText.contains("search")) {
                    return ActionType.HELP_PARTS_SEARCH;
                }
                return ActionType.HELP_GENERAL;
                
            default:
                return ActionType.UNKNOWN;
        }
    }
    
    /**
     * Extract entities from the text
     */
    private void extractEntities(String text, QueryStructure structure) {
        // Extract contract numbers
        Matcher contractMatcher = CONTRACT_NUMBER_PATTERN.matcher(text);
        while (contractMatcher.find()) {
            structure.addExtractedEntity("contract_number", contractMatcher.group());
        }
        
        // Extract part numbers
        Matcher partMatcher = PART_NUMBER_PATTERN.matcher(text);
        while (partMatcher.find()) {
            structure.addExtractedEntity("part_number", partMatcher.group());
        }
        
        // Extract account numbers
        Matcher accountMatcher = ACCOUNT_NUMBER_PATTERN.matcher(text);
        while (accountMatcher.find()) {
            String accountNum = accountMatcher.group();
            // Only consider as account if it's longer than typical contract numbers
            if (accountNum.length() >= 6) {
                structure.addExtractedEntity("account_number", accountNum);
            }
        }
        
        // Extract user names
        Matcher userMatcher = CREATED_BY_PATTERN.matcher(text);
        if (userMatcher.find()) {
            structure.addExtractedEntity("created_by", userMatcher.group(1));
        }
        
        // Extract customer names
        Matcher customerMatcher = CUSTOMER_PATTERN.matcher(text);
        if (customerMatcher.find()) {
            structure.addExtractedEntity("customer_name", customerMatcher.group(1).trim());
        }
        
        // Extract status
        Matcher statusMatcher = STATUS_PATTERN.matcher(text);
        if (statusMatcher.find()) {
            structure.addExtractedEntity("status", statusMatcher.group(1));
        }
        
        // Extract dates
        Matcher dateMatcher = DATE_PATTERN.matcher(text);
        while (dateMatcher.find()) {
            structure.addExtractedEntity("date", dateMatcher.group());
        }
    }
    
    /**
     * Parse operators and conditions from the text
     */
    private void parseOperators(String text, QueryStructure structure) {
        // Parse "after" conditions
        Matcher afterMatcher = AFTER_PATTERN.matcher(text);
        while (afterMatcher.find()) {
            structure.addOperator("created_date", QueryOperator.OperatorType.AFTER, 
                                afterMatcher.group(1), afterMatcher.group());
        }
        
        // Parse "before" conditions
        Matcher beforeMatcher = BEFORE_PATTERN.matcher(text);
        while (beforeMatcher.find()) {
            structure.addOperator("created_date", QueryOperator.OperatorType.BEFORE, 
                                beforeMatcher.group(1), beforeMatcher.group());
        }
        
        // Parse "between" conditions
        Matcher betweenMatcher = BETWEEN_PATTERN.matcher(text);
        if (betweenMatcher.find()) {
            String startDate = betweenMatcher.group(1);
            String endDate = betweenMatcher.group(2);
            structure.addOperator("created_date", QueryOperator.OperatorType.BETWEEN, 
                                Arrays.asList(startDate, endDate), betweenMatcher.group());
        }
        
        // Parse "created by" conditions
        Matcher createdByMatcher = CREATED_BY_PATTERN.matcher(text);
        if (createdByMatcher.find()) {
            structure.addOperator("created_by", QueryOperator.OperatorType.EQUALS, 
                                createdByMatcher.group(1), createdByMatcher.group());
        }
        
        // Parse status conditions
        Matcher statusMatcher = STATUS_PATTERN.matcher(text);
        if (statusMatcher.find()) {
            structure.addOperator("status", QueryOperator.OperatorType.EQUALS, 
                                statusMatcher.group(1), statusMatcher.group());
        }
        
        // Parse customer conditions
        Matcher customerMatcher = CUSTOMER_PATTERN.matcher(text);
        if (customerMatcher.find()) {
            structure.addOperator("customer_name", QueryOperator.OperatorType.EQUALS, 
                                customerMatcher.group(1).trim(), customerMatcher.group());
        }
        
        // Parse contract number conditions
        Matcher contractMatcher = CONTRACT_NUMBER_PATTERN.matcher(text);
        if (contractMatcher.find()) {
            structure.addOperator("contract_number", QueryOperator.OperatorType.EQUALS, 
                                contractMatcher.group(), contractMatcher.group());
        }
        
        // Parse part number conditions
        Matcher partMatcher = PART_NUMBER_PATTERN.matcher(text);
        if (partMatcher.find()) {
            structure.addOperator("part_number", QueryOperator.OperatorType.EQUALS, 
                                partMatcher.group(), partMatcher.group());
        }
    }
    
    /**
     * Set default entities if none specifically requested
     */
    private void setDefaultEntities(QueryStructure structure) {
        if (structure.getRequestedEntities().isEmpty()) {
            switch (structure.getQueryType()) {
                case CONTRACT:
                    structure.setRequestedEntities(new HashSet<>(defaultContractEntities));
                    break;
                case PARTS:
                    structure.setRequestedEntities(new HashSet<>(defaultPartsEntities));
                    break;
                case HELP:
                    // Help queries don't need specific entities
                    break;
                default:
                    structure.setRequestedEntities(new HashSet<>(defaultContractEntities));
                    break;
            }
        }
    }
    
    /**
     * Calculate confidence score based on various factors
     */
    private double calculateConfidence(QueryStructure structure) {
        double confidence = 0.0;
        
        // Base confidence based on query type recognition
        if (structure.getQueryType() != QueryType.UNKNOWN) {
            confidence += 0.3;
        }
        
        // Confidence based on action type recognition
        if (structure.getActionType() != ActionType.UNKNOWN) {
            confidence += 0.2;
        }
        
        // Confidence based on extracted entities
        confidence += Math.min(0.3, structure.getExtractedEntities().size() * 0.1);
        
        // Confidence based on operators
        confidence += Math.min(0.2, structure.getOperators().size() * 0.05);
        
        // Penalty for errors
        if (structure.isHasErrors()) {
            confidence -= 0.1;
        }
        
        return Math.max(0.0, Math.min(1.0, confidence));
    }
    
    /**
     * Validate query structure and provide suggestions
     */
    private void validateAndSuggest(QueryStructure structure) {
        String lowerText = structure.getCorrectedQuery().toLowerCase();
        
        // Check for ambiguous queries
        if (structure.getQueryType() == QueryType.UNKNOWN) {
            structure.addError("Could not determine query type");
            structure.addSuggestion("Try using words like 'contract', 'parts', or 'help'");
        }
        
        // Check for missing key information
        if (structure.getQueryType() == QueryType.CONTRACT && 
            !structure.hasExtractedEntity("contract_number") && 
            !structure.hasExtractedEntity("created_by") &&
            !structure.hasExtractedEntity("customer_name")) {
            structure.addSuggestion("Consider adding a contract number, user name, or customer name for more specific results");
        }
        
        if (structure.getQueryType() == QueryType.PARTS && 
            !structure.hasExtractedEntity("part_number") && 
            !structure.hasExtractedEntity("contract_number")) {
            structure.addSuggestion("Consider adding a part number or contract number for more specific results");
        }
        
        // Check for potentially misspelled entities
        if (lowerText.contains("vinod") || lowerText.contains("john") || lowerText.contains("jane")) {
            // These look like user names, make sure they're captured
            if (!structure.hasExtractedEntity("created_by")) {
                structure.addSuggestion("Make sure user names are properly recognized");
            }
        }
        
        // Suggest using specific operators
        if (lowerText.contains("recent") || lowerText.contains("latest")) {
            structure.addSuggestion("Try using 'after 2023' or 'before 2024' for date-based filtering");
        }
        
        if (lowerText.contains("expired") || lowerText.contains("active")) {
            if (!structure.hasExtractedEntity("status")) {
                structure.addSuggestion("Use 'status expired' or 'status active' for status filtering");
            }
        }
    }
    
    /**
     * Get suggestions for similar queries
     */
    public List<String> getSimilarQueries(String query) {
        List<String> suggestions = new ArrayList<>();
        
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("contract")) {
            suggestions.add("show contracts created by john");
            suggestions.add("get contract 123456");
            suggestions.add("list contracts for customer ABC Corp");
            suggestions.add("contracts created after 2020");
        }
        
        if (lowerQuery.contains("part")) {
            suggestions.add("show parts for contract 123456");
            suggestions.add("get part AE12345");
            suggestions.add("list parts created by jane");
            suggestions.add("parts for customer XYZ Inc");
        }
        
        if (lowerQuery.contains("help")) {
            suggestions.add("help create contract");
            suggestions.add("how to search parts");
            suggestions.add("help with contract creation");
        }
        
        return suggestions;
    }
}
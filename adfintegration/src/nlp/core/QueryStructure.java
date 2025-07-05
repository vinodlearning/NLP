package nlp.core;

import java.util.*;

/**
 * Structured Query Object - Core data structure for processed user queries
 * 
 * This class represents the output of the NLP processing pipeline,
 * containing all extracted information from the user's natural language query.
 * 
 * @author Contract/Parts Search System
 * @version 1.0
 */
public class QueryStructure {
    
    // Core query classification
    private QueryType queryType;
    private ActionType actionType;
    
    // Original and processed text
    private String originalQuery;
    private String correctedQuery;
    private boolean hasSpellCorrections;
    
    // Extracted entities
    private Set<String> requestedEntities;
    private Map<String, Object> extractedEntities;
    
    // Query operators and conditions
    private List<QueryOperator> operators;
    
    // Processing metadata
    private double confidence;
    private long processingTime;
    private String timestamp;
    
    // Error handling
    private boolean hasErrors;
    private List<String> errors;
    private List<String> suggestions;
    
    /**
     * Query Types - Primary classification of user intent
     */
    public enum QueryType {
        CONTRACT("contract", "Contract-related queries"),
        PARTS("parts", "Parts-related queries"),
        HELP("help", "Help and guidance queries"),
        UNKNOWN("unknown", "Unrecognized query type");
        
        private final String code;
        private final String description;
        
        QueryType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    /**
     * Action Types - Specific operations to perform
     */
    public enum ActionType {
        // Contract actions
        CONTRACTS_BY_USER("contracts_by_user", "Find contracts created by specific user"),
        CONTRACTS_BY_CONTRACT_NUMBER("contracts_by_contractNumber", "Find contract by contract number"),
        CONTRACTS_BY_ACCOUNT_NUMBER("contracts_by_accountNumber", "Find contracts by account number"),
        CONTRACTS_BY_CUSTOMER_NAME("contracts_by_customerName", "Find contracts by customer name"),
        CONTRACTS_BY_PARTS("contracts_by_parts", "Find contracts containing specific parts"),
        
        // Parts actions
        PARTS_BY_USER("parts_by_user", "Find parts created by specific user"),
        PARTS_BY_CONTRACT("parts_by_contract", "Find parts in specific contract"),
        PARTS_BY_PART_NUMBER("parts_by_partNumber", "Find parts by part number"),
        PARTS_BY_CUSTOMER("parts_by_customer", "Find parts for specific customer"),
        
        // Help actions
        HELP_CONTRACT_CREATION("help_contract_creation", "Help with contract creation"),
        HELP_PARTS_SEARCH("help_parts_search", "Help with parts search"),
        HELP_GENERAL("help_general", "General help"),
        
        // Unknown
        UNKNOWN("unknown", "Unrecognized action type");
        
        private final String code;
        private final String description;
        
        ActionType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    /**
     * Query Operator - Represents filter conditions
     */
    public static class QueryOperator {
        private String field;
        private OperatorType operator;
        private Object value;
        private String originalText;
        
        public enum OperatorType {
            EQUALS("=", "equals"),
            GREATER_THAN(">", "greater than"),
            LESS_THAN("<", "less than"),
            GREATER_EQUAL(">=", "greater than or equal"),
            LESS_EQUAL("<=", "less than or equal"),
            BETWEEN("between", "between"),
            AFTER("after", "after"),
            BEFORE("before", "before"),
            CONTAINS("contains", "contains"),
            STARTS_WITH("starts_with", "starts with"),
            ENDS_WITH("ends_with", "ends with"),
            IN("in", "in list"),
            NOT_EQUALS("!=", "not equals");
            
            private final String symbol;
            private final String description;
            
            OperatorType(String symbol, String description) {
                this.symbol = symbol;
                this.description = description;
            }
            
            public String getSymbol() { return symbol; }
            public String getDescription() { return description; }
        }
        
        public QueryOperator(String field, OperatorType operator, Object value, String originalText) {
            this.field = field;
            this.operator = operator;
            this.value = value;
            this.originalText = originalText;
        }
        
        // Getters and setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        
        public OperatorType getOperator() { return operator; }
        public void setOperator(OperatorType operator) { this.operator = operator; }
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        
        public String getOriginalText() { return originalText; }
        public void setOriginalText(String originalText) { this.originalText = originalText; }
        
        @Override
        public String toString() {
            return String.format("%s %s %s", field, operator.getSymbol(), value);
        }
    }
    
    /**
     * Default constructor
     */
    public QueryStructure() {
        this.requestedEntities = new HashSet<>();
        this.extractedEntities = new HashMap<>();
        this.operators = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.timestamp = new Date().toString();
        this.hasErrors = false;
        this.hasSpellCorrections = false;
    }
    
    /**
     * Full constructor
     */
    public QueryStructure(QueryType queryType, ActionType actionType, String originalQuery, String correctedQuery) {
        this();
        this.queryType = queryType;
        this.actionType = actionType;
        this.originalQuery = originalQuery;
        this.correctedQuery = correctedQuery;
        this.hasSpellCorrections = !originalQuery.equals(correctedQuery);
    }
    
    // Getters and setters
    public QueryType getQueryType() { return queryType; }
    public void setQueryType(QueryType queryType) { this.queryType = queryType; }
    
    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
    
    public String getOriginalQuery() { return originalQuery; }
    public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }
    
    public String getCorrectedQuery() { return correctedQuery; }
    public void setCorrectedQuery(String correctedQuery) { this.correctedQuery = correctedQuery; }
    
    public boolean isHasSpellCorrections() { return hasSpellCorrections; }
    public void setHasSpellCorrections(boolean hasSpellCorrections) { this.hasSpellCorrections = hasSpellCorrections; }
    
    public Set<String> getRequestedEntities() { return requestedEntities; }
    public void setRequestedEntities(Set<String> requestedEntities) { this.requestedEntities = requestedEntities; }
    
    public Map<String, Object> getExtractedEntities() { return extractedEntities; }
    public void setExtractedEntities(Map<String, Object> extractedEntities) { this.extractedEntities = extractedEntities; }
    
    public List<QueryOperator> getOperators() { return operators; }
    public void setOperators(List<QueryOperator> operators) { this.operators = operators; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public boolean isHasErrors() { return hasErrors; }
    public void setHasErrors(boolean hasErrors) { this.hasErrors = hasErrors; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    
    /**
     * Utility methods
     */
    public void addRequestedEntity(String entity) {
        this.requestedEntities.add(entity);
    }
    
    public void addExtractedEntity(String key, Object value) {
        this.extractedEntities.put(key, value);
    }
    
    public void addOperator(QueryOperator operator) {
        this.operators.add(operator);
    }
    
    public void addOperator(String field, QueryOperator.OperatorType operator, Object value, String originalText) {
        this.operators.add(new QueryOperator(field, operator, value, originalText));
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.hasErrors = true;
    }
    
    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }
    
    /**
     * Get operators for a specific field
     */
    public List<QueryOperator> getOperatorsForField(String field) {
        return operators.stream()
                .filter(op -> op.getField().equals(field))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Check if query has specific entity extracted
     */
    public boolean hasExtractedEntity(String key) {
        return extractedEntities.containsKey(key);
    }
    
    /**
     * Get extracted entity value
     */
    public Object getExtractedEntityValue(String key) {
        return extractedEntities.get(key);
    }
    
    /**
     * Check if query requests specific entity
     */
    public boolean requestsEntity(String entity) {
        return requestedEntities.contains(entity);
    }
    
    /**
     * Get summary of the query structure
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Query Type: ").append(queryType.getDescription()).append("\n");
        summary.append("Action Type: ").append(actionType.getDescription()).append("\n");
        summary.append("Original Query: ").append(originalQuery).append("\n");
        summary.append("Corrected Query: ").append(correctedQuery).append("\n");
        summary.append("Has Spell Corrections: ").append(hasSpellCorrections).append("\n");
        summary.append("Requested Entities: ").append(requestedEntities).append("\n");
        summary.append("Extracted Entities: ").append(extractedEntities.size()).append("\n");
        summary.append("Operators: ").append(operators.size()).append("\n");
        summary.append("Confidence: ").append(String.format("%.2f", confidence)).append("\n");
        summary.append("Processing Time: ").append(processingTime).append("ms\n");
        summary.append("Has Errors: ").append(hasErrors).append("\n");
        if (hasErrors) {
            summary.append("Errors: ").append(errors).append("\n");
        }
        if (!suggestions.isEmpty()) {
            summary.append("Suggestions: ").append(suggestions).append("\n");
        }
        return summary.toString();
    }
    
    /**
     * Convert to JSON string for debugging
     */
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"queryType\": \"").append(queryType.getCode()).append("\",\n");
        json.append("  \"actionType\": \"").append(actionType.getCode()).append("\",\n");
        json.append("  \"originalQuery\": \"").append(originalQuery).append("\",\n");
        json.append("  \"correctedQuery\": \"").append(correctedQuery).append("\",\n");
        json.append("  \"hasSpellCorrections\": ").append(hasSpellCorrections).append(",\n");
        json.append("  \"requestedEntities\": ").append(requestedEntities.toString()).append(",\n");
        json.append("  \"extractedEntities\": ").append(extractedEntities.toString()).append(",\n");
        json.append("  \"operators\": [\n");
        for (int i = 0; i < operators.size(); i++) {
            QueryOperator op = operators.get(i);
            json.append("    {");
            json.append("\"field\": \"").append(op.getField()).append("\", ");
            json.append("\"operator\": \"").append(op.getOperator().getSymbol()).append("\", ");
            json.append("\"value\": \"").append(op.getValue()).append("\", ");
            json.append("\"originalText\": \"").append(op.getOriginalText()).append("\"");
            json.append("}");
            if (i < operators.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        json.append("  \"confidence\": ").append(confidence).append(",\n");
        json.append("  \"processingTime\": ").append(processingTime).append(",\n");
        json.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        json.append("  \"hasErrors\": ").append(hasErrors).append(",\n");
        json.append("  \"errors\": ").append(errors.toString()).append(",\n");
        json.append("  \"suggestions\": ").append(suggestions.toString()).append("\n");
        json.append("}");
        return json.toString();
    }
    
    @Override
    public String toString() {
        return String.format("QueryStructure{type=%s, action=%s, confidence=%.2f, entities=%d, operators=%d}", 
                           queryType, actionType, confidence, extractedEntities.size(), operators.size());
    }
}
package com.contractml.response;

import java.util.*;

/**
 * ContractML Response - Implements the exact JSON structure specified
 * 
 * Core JSON Structure:
 * {
 *   "header": { contractNumber, partNumber, customerNumber, customerName, createdBy },
 *   "queryMetadata": { queryType, actionType, processingTimeMs },
 *   "entities": [ { attribute, operation, value } ],
 *   "displayEntities": [ "FIELD_NAME" ],
 *   "errors": [ { code, message } ]
 * }
 */
public class ContractMLResponse {
    
    private Header header;
    private QueryMetadata queryMetadata;
    private List<Entity> entities;
    private List<String> displayEntities;
    private List<Error> errors;
    
    /**
     * Constructor
     */
    public ContractMLResponse() {
        this.header = new Header();
        this.queryMetadata = new QueryMetadata();
        this.entities = new ArrayList<>();
        this.displayEntities = new ArrayList<>();
        this.errors = new ArrayList<>();
    }
    
    // Getters and Setters
    public Header getHeader() { return header; }
    public void setHeader(Header header) { this.header = header; }
    
    public QueryMetadata getQueryMetadata() { return queryMetadata; }
    public void setQueryMetadata(QueryMetadata queryMetadata) { this.queryMetadata = queryMetadata; }
    
    public List<Entity> getEntities() { return entities; }
    public void setEntities(List<Entity> entities) { this.entities = entities; }
    
    public List<String> getDisplayEntities() { return displayEntities; }
    public void setDisplayEntities(List<String> displayEntities) { this.displayEntities = displayEntities; }
    
    public List<Error> getErrors() { return errors; }
    public void setErrors(List<Error> errors) { this.errors = errors; }
    
    /**
     * Add entity filter
     */
    public void addEntity(String attribute, String operation, String value) {
        this.entities.add(new Entity(attribute, operation, value));
    }
    
    /**
     * Add display entity
     */
    public void addDisplayEntity(String fieldName) {
        if (!this.displayEntities.contains(fieldName)) {
            this.displayEntities.add(fieldName);
        }
    }
    
    /**
     * Add error
     */
    public void addError(String code, String message) {
        this.errors.add(new Error(code, message));
    }
    
    /**
     * Check if response has valid headers or entities (business rule)
     */
    public boolean isValid() {
        return header.hasAnyValue() || !entities.isEmpty();
    }
    
    /**
     * Convert to JSON string
     */
    public String toJsonString() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // Header
        json.append("  \"header\": {\n");
        json.append("    \"contractNumber\": ").append(quote(header.contractNumber)).append(",\n");
        json.append("    \"partNumber\": ").append(quote(header.partNumber)).append(",\n");
        json.append("    \"customerNumber\": ").append(quote(header.customerNumber)).append(",\n");
        json.append("    \"customerName\": ").append(quote(header.customerName)).append(",\n");
        json.append("    \"createdBy\": ").append(quote(header.createdBy)).append("\n");
        json.append("  },\n");
        
        // Query Metadata
        json.append("  \"queryMetadata\": {\n");
        json.append("    \"queryType\": ").append(quote(queryMetadata.queryType)).append(",\n");
        json.append("    \"actionType\": ").append(quote(queryMetadata.actionType)).append(",\n");
        json.append("    \"processingTimeMs\": ").append(queryMetadata.processingTimeMs).append("\n");
        json.append("  },\n");
        
        // Entities
        json.append("  \"entities\": [\n");
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            json.append("    {\n");
            json.append("      \"attribute\": ").append(quote(entity.attribute)).append(",\n");
            json.append("      \"operation\": ").append(quote(entity.operation)).append(",\n");
            json.append("      \"value\": ").append(quote(entity.value)).append("\n");
            json.append("    }");
            if (i < entities.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        
        // Display Entities
        json.append("  \"displayEntities\": [\n");
        for (int i = 0; i < displayEntities.size(); i++) {
            json.append("    ").append(quote(displayEntities.get(i)));
            if (i < displayEntities.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        
        // Errors
        json.append("  \"errors\": [\n");
        for (int i = 0; i < errors.size(); i++) {
            Error error = errors.get(i);
            json.append("    {\n");
            json.append("      \"code\": ").append(quote(error.code)).append(",\n");
            json.append("      \"message\": ").append(quote(error.message)).append("\n");
            json.append("    }");
            if (i < errors.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ]\n");
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Helper method to quote strings for JSON
     */
    private String quote(String value) {
        return value == null ? "null" : "\"" + escapeJson(value) + "\"";
    }
    
    /**
     * Escape JSON special characters
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Header class - contains entity identifiers
     */
    public static class Header {
        private String contractNumber;
        private String partNumber;
        private String customerNumber;
        private String customerName;
        private String createdBy;
        
        public Header() {}
        
        // Getters and Setters
        public String getContractNumber() { return contractNumber; }
        public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
        
        public String getPartNumber() { return partNumber; }
        public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
        
        public String getCustomerNumber() { return customerNumber; }
        public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        
        /**
         * Check if header has any non-null value (business rule)
         */
        public boolean hasAnyValue() {
            return contractNumber != null || partNumber != null || customerNumber != null || 
                   customerName != null || createdBy != null;
        }
    }
    
    /**
     * Query Metadata class
     */
    public static class QueryMetadata {
        private String queryType;    // "CONTRACTS" or "PARTS"
        private String actionType;   // "contracts_by_contractNumber", etc.
        private Long processingTimeMs;
        
        public QueryMetadata() {}
        
        public QueryMetadata(String queryType, String actionType, Long processingTimeMs) {
            this.queryType = queryType;
            this.actionType = actionType;
            this.processingTimeMs = processingTimeMs;
        }
        
        // Getters and Setters
        public String getQueryType() { return queryType; }
        public void setQueryType(String queryType) { this.queryType = queryType; }
        
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        
        public Long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    }
    
    /**
     * Entity class - for filters
     */
    public static class Entity {
        private String attribute;
        private String operation;
        private String value;
        
        public Entity() {}
        
        public Entity(String attribute, String operation, String value) {
            this.attribute = attribute;
            this.operation = operation;
            this.value = value;
        }
        
        // Getters and Setters
        public String getAttribute() { return attribute; }
        public void setAttribute(String attribute) { this.attribute = attribute; }
        
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
        
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
    
    /**
     * Error class
     */
    public static class Error {
        private String code;
        private String message;
        
        public Error() {}
        
        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
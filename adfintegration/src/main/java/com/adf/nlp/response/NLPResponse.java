package com.adf.nlp.response;

import java.util.*;

/**
 * NLP Response class representing the structured JSON response
 * Follows the exact specification provided for ADF integration
 */
public class NLPResponse {
    
    private Header header;
    private QueryMetadata queryMetadata;
    private List<Entity> entities;
    private List<String> displayEntities;
    private List<Error> errors;
    
    /**
     * Constructor
     */
    public NLPResponse() {
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
     * Add entity to the response
     */
    public void addEntity(String attribute, String operation, String value) {
        this.entities.add(new Entity(attribute, operation, value));
    }
    
    /**
     * Add display entity to the response
     */
    public void addDisplayEntity(String entity) {
        if (!this.displayEntities.contains(entity)) {
            this.displayEntities.add(entity);
        }
    }
    
    /**
     * Add error to the response
     */
    public void addError(String code, String message) {
        this.errors.add(new Error(code, message));
    }
    
    /**
     * Check if response has valid headers
     */
    public boolean hasValidHeaders() {
        return header.hasAnyValue();
    }
    
    /**
     * Check if response has entities (filters)
     */
    public boolean hasEntities() {
        return !entities.isEmpty();
    }
    
    /**
     * Check if response has errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Header class
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
         * Check if header has any non-null value
         */
        public boolean hasAnyValue() {
            return contractNumber != null || partNumber != null || customerNumber != null || 
                   customerName != null || createdBy != null;
        }
        
        /**
         * Get the primary identifier type
         */
        public String getPrimaryIdentifierType() {
            if (contractNumber != null) return "CONTRACT_NUMBER";
            if (partNumber != null) return "PART_NUMBER";
            if (customerNumber != null) return "CUSTOMER_NUMBER";
            if (customerName != null) return "CUSTOMER_NAME";
            if (createdBy != null) return "CREATED_BY";
            return null;
        }
        
        /**
         * Get the primary identifier value
         */
        public String getPrimaryIdentifierValue() {
            if (contractNumber != null) return contractNumber;
            if (partNumber != null) return partNumber;
            if (customerNumber != null) return customerNumber;
            if (customerName != null) return customerName;
            if (createdBy != null) return createdBy;
            return null;
        }
    }
    
    /**
     * Query Metadata class
     */
    public static class QueryMetadata {
        private String queryType;
        private String actionType;
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
     * Entity class for filters
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
        
        @Override
        public String toString() {
            return String.format("Entity{attribute='%s', operation='%s', value='%s'}", 
                               attribute, operation, value);
        }
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
        
        @Override
        public String toString() {
            return String.format("Error{code='%s', message='%s'}", code, message);
        }
    }
    
    /**
     * Convert to JSON string representation
     */
    @Override
    public String toString() {
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
        return value == null ? "null" : "\"" + value + "\"";
    }
}
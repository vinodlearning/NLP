package com.company.contracts.service;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * NLP Response Object
 * 
 * This class represents the response from natural language processing
 * operations in the Contract Portal system.
 * 
 * Contains information about:
 * - Original and corrected queries
 * - Intent classification results
 * - Extracted entities
 * - Confidence scores
 * - Processing metrics
 * - Response text
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class NLPResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Query information
    private String originalQuery;
    private String correctedQuery;
    
    // Classification results
    private String intent;
    private String domain;
    private double confidence;
    
    // Extracted entities
    private Map<String, String> entities;
    
    // Response content
    private String responseText;
    private String responseType;
    
    // Processing metadata
    private long processingTimeMs;
    private Date timestamp;
    private boolean success;
    private String errorMessage;
    
    // Additional metadata
    private String modelUsed;
    private String processingMethod;
    private Map<String, Object> additionalData;
    
    /**
     * Default constructor
     */
    public NLPResponse() {
        this.timestamp = new Date();
        this.success = false;
        this.confidence = 0.0;
    }
    
    /**
     * Constructor with basic information
     */
    public NLPResponse(String originalQuery, String intent, double confidence) {
        this();
        this.originalQuery = originalQuery;
        this.intent = intent;
        this.confidence = confidence;
    }
    
    // Getters and Setters
    
    public String getOriginalQuery() {
        return originalQuery;
    }
    
    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }
    
    public String getCorrectedQuery() {
        return correctedQuery;
    }
    
    public void setCorrectedQuery(String correctedQuery) {
        this.correctedQuery = correctedQuery;
    }
    
    public String getIntent() {
        return intent;
    }
    
    public void setIntent(String intent) {
        this.intent = intent;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public Map<String, String> getEntities() {
        return entities;
    }
    
    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }
    
    public String getResponseText() {
        return responseText;
    }
    
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
    
    public String getResponseType() {
        return responseType;
    }
    
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getModelUsed() {
        return modelUsed;
    }
    
    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
    
    public String getProcessingMethod() {
        return processingMethod;
    }
    
    public void setProcessingMethod(String processingMethod) {
        this.processingMethod = processingMethod;
    }
    
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }
    
    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
    
    // Helper methods
    
    /**
     * Check if spell correction was applied
     */
    public boolean isSpellCorrected() {
        return correctedQuery != null && 
               !correctedQuery.equals(originalQuery);
    }
    
    /**
     * Get confidence level as string
     */
    public String getConfidenceLevel() {
        if (confidence >= 0.9) {
            return "VERY_HIGH";
        } else if (confidence >= 0.7) {
            return "HIGH";
        } else if (confidence >= 0.5) {
            return "MEDIUM";
        } else if (confidence >= 0.3) {
            return "LOW";
        } else {
            return "VERY_LOW";
        }
    }
    
    /**
     * Check if confidence is above threshold
     */
    public boolean isConfidenceAboveThreshold(double threshold) {
        return confidence >= threshold;
    }
    
    /**
     * Get formatted processing time
     */
    public String getFormattedProcessingTime() {
        if (processingTimeMs < 1000) {
            return processingTimeMs + "ms";
        } else {
            return String.format("%.2fs", processingTimeMs / 1000.0);
        }
    }
    
    /**
     * Get entity value by key
     */
    public String getEntityValue(String key) {
        if (entities == null) {
            return null;
        }
        return entities.get(key);
    }
    
    /**
     * Check if entity exists
     */
    public boolean hasEntity(String key) {
        return entities != null && entities.containsKey(key);
    }
    
    /**
     * Get entity count
     */
    public int getEntityCount() {
        return entities != null ? entities.size() : 0;
    }
    
    /**
     * Check if response is an error
     */
    public boolean isError() {
        return !success || "ERROR".equals(intent);
    }
    
    /**
     * Get formatted timestamp
     */
    public String getFormattedTimestamp() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
    }
    
    /**
     * ToString method for debugging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NLPResponse{");
        sb.append("originalQuery='").append(originalQuery).append('\'');
        sb.append(", intent='").append(intent).append('\'');
        sb.append(", confidence=").append(confidence);
        sb.append(", domain='").append(domain).append('\'');
        sb.append(", success=").append(success);
        sb.append(", processingTimeMs=").append(processingTimeMs);
        sb.append(", entityCount=").append(getEntityCount());
        sb.append('}');
        return sb.toString();
    }
}
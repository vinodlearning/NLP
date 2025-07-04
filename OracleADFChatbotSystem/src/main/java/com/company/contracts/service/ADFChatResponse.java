package com.company.contracts.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ADF Chat Response
 * 
 * This class represents a formatted response specifically designed
 * for Oracle ADF UI components in the Contract Portal chatbot.
 * 
 * Contains:
 * - Formatted response text for display
 * - UI hints for styling and presentation
 * - Metadata for debugging and analytics
 * - Suggested actions for user interaction
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ADFChatResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Basic response information
    private String originalQuery;
    private String formattedResponse;
    private String intent;
    private String domain;
    private String responseType;
    
    // Processing results
    private double confidence;
    private boolean success;
    private long processingTimeMs;
    private Date timestamp;
    
    // UI-specific information
    private Map<String, Object> uiHints;
    private Map<String, Object> metadata;
    private List<String> suggestedActions;
    
    /**
     * Default constructor
     */
    public ADFChatResponse() {
        this.timestamp = new Date();
        this.success = true;
        this.confidence = 0.0;
    }
    
    /**
     * Constructor with basic information
     */
    public ADFChatResponse(String originalQuery, String formattedResponse, String intent) {
        this();
        this.originalQuery = originalQuery;
        this.formattedResponse = formattedResponse;
        this.intent = intent;
    }
    
    // Getters and Setters
    
    public String getOriginalQuery() {
        return originalQuery;
    }
    
    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }
    
    public String getFormattedResponse() {
        return formattedResponse;
    }
    
    public void setFormattedResponse(String formattedResponse) {
        this.formattedResponse = formattedResponse;
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
    
    public String getResponseType() {
        return responseType;
    }
    
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
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
    
    public Map<String, Object> getUiHints() {
        return uiHints;
    }
    
    public void setUiHints(Map<String, Object> uiHints) {
        this.uiHints = uiHints;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public List<String> getSuggestedActions() {
        return suggestedActions;
    }
    
    public void setSuggestedActions(List<String> suggestedActions) {
        this.suggestedActions = suggestedActions;
    }
    
    // Helper methods for ADF UI
    
    /**
     * Get UI hint value
     */
    public Object getUiHint(String key) {
        return uiHints != null ? uiHints.get(key) : null;
    }
    
    /**
     * Get metadata value
     */
    public Object getMetadataValue(String key) {
        return metadata != null ? metadata.get(key) : null;
    }
    
    /**
     * Check if response is an error
     */
    public boolean isError() {
        return !success || "ERROR".equals(responseType);
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
     * Get formatted timestamp for display
     */
    public String getFormattedTimestamp() {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(timestamp);
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
     * Get CSS class for styling based on confidence
     */
    public String getCssClass() {
        if (isError()) {
            return "chat-response-error";
        } else if (confidence >= 0.8) {
            return "chat-response-high-confidence";
        } else if (confidence >= 0.6) {
            return "chat-response-medium-confidence";
        } else {
            return "chat-response-low-confidence";
        }
    }
    
    /**
     * Get icon class for display
     */
    public String getIconClass() {
        if (isError()) {
            return "fa fa-exclamation-triangle";
        } else if (intent != null) {
            if (intent.startsWith("CONTRACT_")) {
                return "fa fa-file-contract";
            } else if (intent.startsWith("PARTS_")) {
                return "fa fa-cogs";
            } else if (intent.startsWith("HELP_")) {
                return "fa fa-question-circle";
            }
        }
        return "fa fa-comment";
    }
    
    /**
     * Get background color for response
     */
    public String getBackgroundColor() {
        Object bgColor = getUiHint("backgroundColor");
        return bgColor != null ? bgColor.toString() : "#F5F5F5";
    }
    
    /**
     * Check if response has suggested actions
     */
    public boolean hasSuggestedActions() {
        return suggestedActions != null && !suggestedActions.isEmpty();
    }
    
    /**
     * Get suggested actions count
     */
    public int getSuggestedActionsCount() {
        return suggestedActions != null ? suggestedActions.size() : 0;
    }
    
    /**
     * Check if response is from a specific domain
     */
    public boolean isDomainResponse(String domainName) {
        return domain != null && domain.equalsIgnoreCase(domainName);
    }
    
    /**
     * Check if response is from a specific intent
     */
    public boolean isIntentResponse(String intentName) {
        return intent != null && intent.equalsIgnoreCase(intentName);
    }
    
    /**
     * Get short summary of response (for display in lists)
     */
    public String getShortSummary() {
        if (formattedResponse == null || formattedResponse.isEmpty()) {
            return "No response";
        }
        
        // Get first line or first 100 characters
        String[] lines = formattedResponse.split("\n");
        String firstLine = lines[0];
        
        if (firstLine.length() > 100) {
            return firstLine.substring(0, 97) + "...";
        }
        
        return firstLine;
    }
    
    /**
     * Get response length
     */
    public int getResponseLength() {
        return formattedResponse != null ? formattedResponse.length() : 0;
    }
    
    /**
     * Check if response is long (for UI pagination)
     */
    public boolean isLongResponse() {
        return getResponseLength() > 500;
    }
    
    /**
     * Get response in HTML format for rich display
     */
    public String getHtmlFormattedResponse() {
        if (formattedResponse == null) {
            return "";
        }
        
        // Convert line breaks to HTML
        String htmlResponse = formattedResponse.replace("\n", "<br/>");
        
        // Add basic HTML formatting for common patterns
        htmlResponse = htmlResponse.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        htmlResponse = htmlResponse.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        htmlResponse = htmlResponse.replaceAll("```(.*?)```", "<code>$1</code>");
        
        return htmlResponse;
    }
    
    /**
     * Get metadata as formatted string for debugging
     */
    public String getFormattedMetadata() {
        if (metadata == null || metadata.isEmpty()) {
            return "No metadata";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Create a copy of this response
     */
    public ADFChatResponse copy() {
        ADFChatResponse copy = new ADFChatResponse();
        copy.setOriginalQuery(this.originalQuery);
        copy.setFormattedResponse(this.formattedResponse);
        copy.setIntent(this.intent);
        copy.setDomain(this.domain);
        copy.setResponseType(this.responseType);
        copy.setConfidence(this.confidence);
        copy.setSuccess(this.success);
        copy.setProcessingTimeMs(this.processingTimeMs);
        copy.setTimestamp(this.timestamp);
        copy.setUiHints(this.uiHints);
        copy.setMetadata(this.metadata);
        copy.setSuggestedActions(this.suggestedActions);
        
        return copy;
    }
    
    /**
     * ToString method for debugging
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ADFChatResponse{");
        sb.append("intent='").append(intent).append('\'');
        sb.append(", responseType='").append(responseType).append('\'');
        sb.append(", confidence=").append(confidence);
        sb.append(", success=").append(success);
        sb.append(", processingTimeMs=").append(processingTimeMs);
        sb.append(", hasUiHints=").append(uiHints != null);
        sb.append(", hasMetadata=").append(metadata != null);
        sb.append(", suggestedActionsCount=").append(getSuggestedActionsCount());
        sb.append('}');
        return sb.toString();
    }
}
package model.nlp;

import java.util.Set;
import java.util.Date;

/**
 * Enhanced ML Response class for Oracle ADF Integration
 * Contains comprehensive response data with debugging information
 * 
 * @author Contract Query Processing System
 * @version 1.1 - Enhanced Response
 */
public class EnhancedMLResponse {
    
    // Core response fields
    private String route;
    private String reason;
    private String intentType;
    private String originalInput;
    private String correctedInput;
    private boolean hasSpellCorrections;
    private String contractId;
    
    // Analysis fields
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private String businessRuleViolation;
    private String enhancementApplied;
    private double contextScore;
    
    // Performance and debugging
    private double processingTime;
    private String timestamp;
    
    // Default constructor
    public EnhancedMLResponse() {
        this.timestamp = new Date().toString();
    }
    
    // Full constructor
    public EnhancedMLResponse(String route, String reason, String intentType, 
                              String originalInput, String correctedInput, 
                              boolean hasSpellCorrections, String contractId,
                              Set<String> partsKeywords, Set<String> createKeywords,
                              String businessRuleViolation, String enhancementApplied,
                              double contextScore, double processingTime) {
        this.route = route;
        this.reason = reason;
        this.intentType = intentType;
        this.originalInput = originalInput;
        this.correctedInput = correctedInput;
        this.hasSpellCorrections = hasSpellCorrections;
        this.contractId = contractId != null ? contractId : "N/A";
        this.partsKeywords = partsKeywords;
        this.createKeywords = createKeywords;
        this.businessRuleViolation = businessRuleViolation != null ? businessRuleViolation : "N/A";
        this.enhancementApplied = enhancementApplied != null ? enhancementApplied : "N/A";
        this.contextScore = contextScore;
        this.processingTime = processingTime;
        this.timestamp = new Date().toString();
    }
    
    // Getters and setters
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getIntentType() { return intentType; }
    public void setIntentType(String intentType) { this.intentType = intentType; }
    
    public String getOriginalInput() { return originalInput; }
    public void setOriginalInput(String originalInput) { this.originalInput = originalInput; }
    
    public String getCorrectedInput() { return correctedInput; }
    public void setCorrectedInput(String correctedInput) { this.correctedInput = correctedInput; }
    
    public boolean isHasSpellCorrections() { return hasSpellCorrections; }
    public void setHasSpellCorrections(boolean hasSpellCorrections) { this.hasSpellCorrections = hasSpellCorrections; }
    
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    
    public Set<String> getPartsKeywords() { return partsKeywords; }
    public void setPartsKeywords(Set<String> partsKeywords) { this.partsKeywords = partsKeywords; }
    
    public Set<String> getCreateKeywords() { return createKeywords; }
    public void setCreateKeywords(Set<String> createKeywords) { this.createKeywords = createKeywords; }
    
    public String getBusinessRuleViolation() { return businessRuleViolation; }
    public void setBusinessRuleViolation(String businessRuleViolation) { this.businessRuleViolation = businessRuleViolation; }
    
    public String getEnhancementApplied() { return enhancementApplied; }
    public void setEnhancementApplied(String enhancementApplied) { this.enhancementApplied = enhancementApplied; }
    
    public double getContextScore() { return contextScore; }
    public void setContextScore(double contextScore) { this.contextScore = contextScore; }
    
    public double getProcessingTime() { return processingTime; }
    public void setProcessingTime(double processingTime) { this.processingTime = processingTime; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    /**
     * Convert to JSON format for debugging
     */
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"route\": \"").append(route).append("\",\n");
        json.append("  \"reason\": \"").append(reason).append("\",\n");
        json.append("  \"intentType\": \"").append(intentType).append("\",\n");
        json.append("  \"originalInput\": \"").append(originalInput).append("\",\n");
        json.append("  \"correctedInput\": \"").append(correctedInput).append("\",\n");
        json.append("  \"contractId\": \"").append(contractId).append("\",\n");
        json.append("  \"hasSpellCorrections\": ").append(hasSpellCorrections).append(",\n");
        json.append("  \"processingTime\": ").append(processingTime).append(",\n");
        json.append("  \"contextScore\": ").append(contextScore).append(",\n");
        json.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        json.append("  \"partsKeywords\": ").append(partsKeywords).append(",\n");
        json.append("  \"createKeywords\": ").append(createKeywords).append(",\n");
        json.append("  \"businessRuleViolation\": \"").append(businessRuleViolation).append("\",\n");
        json.append("  \"enhancementApplied\": \"").append(enhancementApplied).append("\"\n");
        json.append("}");
        return json.toString();
    }
    
    /**
     * Check if the response indicates successful processing
     */
    public boolean isSuccessful() {
        return !route.equals("ERROR") && !route.equals("PARTS_CREATE_ERROR");
    }
    
    /**
     * Check if contract ID was successfully extracted
     */
    public boolean hasContractId() {
        return contractId != null && !contractId.equals("N/A");
    }
    
    /**
     * Get summary of the response
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Route: ").append(route);
        if (hasContractId()) {
            summary.append(" | Contract ID: ").append(contractId);
        }
        if (hasSpellCorrections) {
            summary.append(" | Spell Corrected: ").append(originalInput).append(" → ").append(correctedInput);
        }
        summary.append(" | Processing Time: ").append(String.format("%.2f", processingTime)).append("μs");
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
}
package model.nlp;

import java.util.Set;
import java.util.Date;

/**
 * Enhanced ML Response for ADF Integration
 * Contains all routing information and metadata
 * Perfect for JSF managed bean integration
 */
public class EnhancedMLResponse {
    
    private String actualRoute;
    private String routingReason;
    private String intentType;
    private String originalInput;
    private String correctedInput;
    private boolean hasSpellCorrections;
    private String contractId;
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private String businessRuleViolation;
    private String enhancementApplied;
    private double contextScore;
    private double processingTime;
    private Date timestamp;
    
    public EnhancedMLResponse(String actualRoute, String routingReason, String intentType,
                             String originalInput, String correctedInput, boolean hasSpellCorrections,
                             String contractId, Set<String> partsKeywords, Set<String> createKeywords,
                             String businessRuleViolation, String enhancementApplied, 
                             double contextScore, double processingTime) {
        this.actualRoute = actualRoute;
        this.routingReason = routingReason;
        this.intentType = intentType;
        this.originalInput = originalInput;
        this.correctedInput = correctedInput;
        this.hasSpellCorrections = hasSpellCorrections;
        this.contractId = contractId;
        this.partsKeywords = partsKeywords;
        this.createKeywords = createKeywords;
        this.businessRuleViolation = businessRuleViolation;
        this.enhancementApplied = enhancementApplied;
        this.contextScore = contextScore;
        this.processingTime = processingTime;
        this.timestamp = new Date();
    }
    
    // Getters for all properties
    public String getActualRoute() { return actualRoute; }
    public String getRoutingReason() { return routingReason; }
    public String getIntentType() { return intentType; }
    public String getOriginalInput() { return originalInput; }
    public String getCorrectedInput() { return correctedInput; }
    public boolean hasSpellCorrections() { return hasSpellCorrections; }
    public String getContractId() { return contractId; }
    public Set<String> getPartsKeywords() { return partsKeywords; }
    public Set<String> getCreateKeywords() { return createKeywords; }
    public String getBusinessRuleViolation() { return businessRuleViolation; }
    public String getEnhancementApplied() { return enhancementApplied; }
    public double getContextScore() { return contextScore; }
    public double getProcessingTime() { return processingTime; }
    public Date getTimestamp() { return timestamp; }
    
    // Utility methods for ADF JSF integration
    public boolean isContractQuery() { return "CONTRACT".equals(actualRoute); }
    public boolean isPartsQuery() { return "PARTS".equals(actualRoute); }
    public boolean isHelpQuery() { return "HELP".equals(actualRoute); }
    public boolean isError() { return "ERROR".equals(actualRoute); }
    public boolean isPartsCreateError() { return "PARTS_CREATE_ERROR".equals(actualRoute); }
    public boolean hasContractId() { return contractId != null && !contractId.isEmpty(); }
    
    // Helper methods for UI display
    public String getResponseType() { return actualRoute + "_RESPONSE"; }
    public String getDisplayRoute() { return actualRoute.replace("_", " "); }
    public String getDisplayIntent() { return intentType.replace("_", " "); }
    
    // Formatted processing time for display
    public String getFormattedProcessingTime() {
        return String.format("%.2f μs", processingTime);
    }
    
    // Summary info for UI
    public String getSummaryInfo() {
        StringBuilder summary = new StringBuilder();
        summary.append("Route: ").append(getDisplayRoute());
        summary.append(" | Intent: ").append(getDisplayIntent());
        summary.append(" | Time: ").append(getFormattedProcessingTime());
        
        if (hasContractId()) {
            summary.append(" | Contract ID: ").append(contractId);
        }
        
        if (hasSpellCorrections) {
            summary.append(" | Spell Corrected");
        }
        
        if (enhancementApplied != null) {
            summary.append(" | Enhanced");
        }
        
        return summary.toString();
    }
    
    // JSON conversion for logging/debugging
    public String toJsonString() {
        return "{\n" +
               "  \"route\": \"" + actualRoute + "\",\n" +
               "  \"reason\": \"" + routingReason + "\",\n" +
               "  \"intentType\": \"" + intentType + "\",\n" +
               "  \"originalInput\": \"" + originalInput + "\",\n" +
               "  \"correctedInput\": \"" + correctedInput + "\",\n" +
               "  \"contractId\": \"" + (contractId != null ? contractId : "N/A") + "\",\n" +
               "  \"hasSpellCorrections\": " + hasSpellCorrections + ",\n" +
               "  \"processingTime\": " + processingTime + ",\n" +
               "  \"contextScore\": " + contextScore + ",\n" +
               "  \"timestamp\": \"" + timestamp + "\",\n" +
               "  \"partsKeywords\": " + partsKeywords + ",\n" +
               "  \"createKeywords\": " + createKeywords + ",\n" +
               "  \"businessRuleViolation\": \"" + (businessRuleViolation != null ? businessRuleViolation : "N/A") + "\",\n" +
               "  \"enhancementApplied\": \"" + (enhancementApplied != null ? enhancementApplied : "N/A") + "\"\n" +
               "}";
    }
    
    // Compact string representation
    @Override
    public String toString() {
        return String.format("EnhancedMLResponse{route=%s, intent=%s, contractId=%s, time=%.2fμs}", 
                           actualRoute, intentType, contractId, processingTime);
    }
    
    // Validation helper
    public boolean isValid() {
        return actualRoute != null && !actualRoute.isEmpty() && 
               routingReason != null && !routingReason.isEmpty() &&
               intentType != null && !intentType.isEmpty();
    }
    
    // Success indicator
    public boolean isSuccess() {
        return !isError() && isValid();
    }
}
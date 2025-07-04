package model.nlp;

import java.util.Set;
import java.util.Date;

/**
 * Enhanced ML Response for Oracle ADF Integration
 * Contains comprehensive routing information and metadata
 * 
 * This class provides all the information needed by your JSF managed bean
 * to make routing decisions and display relevant information to users.
 * 
 * Features:
 * - Complete routing decision information
 * - Spell correction metadata
 * - Contract ID extraction results
 * - Processing performance metrics
 * - Business rule violation details
 * - UI-friendly helper methods
 * 
 * @author Contract Query Processing System
 * @version 1.0
 */
public class EnhancedMLResponse {
    
    // Core routing information
    private String actualRoute;
    private String routingReason;
    private String intentType;
    
    // Input processing results
    private String originalInput;
    private String correctedInput;
    private boolean hasSpellCorrections;
    
    // Extracted information
    private String contractId;
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    
    // Enhancement and validation results
    private String businessRuleViolation;
    private String enhancementApplied;
    private double contextScore;
    
    // Performance metrics
    private double processingTime;
    private Date timestamp;
    
    /**
     * Constructor for EnhancedMLResponse
     */
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
    
    // ==================== GETTERS ====================
    
    /**
     * Get the actual route decision (CONTRACT, PARTS, HELP, ERROR, PARTS_CREATE_ERROR)
     */
    public String getActualRoute() { return actualRoute; }
    
    /**
     * Get the detailed reason for the routing decision
     */
    public String getRoutingReason() { return routingReason; }
    
    /**
     * Get the specific intent type identified
     */
    public String getIntentType() { return intentType; }
    
    /**
     * Get the original user input (before spell correction)
     */
    public String getOriginalInput() { return originalInput; }
    
    /**
     * Get the corrected input (after spell correction)
     */
    public String getCorrectedInput() { return correctedInput; }
    
    /**
     * Check if spell corrections were applied
     */
    public boolean hasSpellCorrections() { return hasSpellCorrections; }
    
    /**
     * Get the extracted contract ID (null if none found)
     */
    public String getContractId() { return contractId; }
    
    /**
     * Get the parts-related keywords found in the input
     */
    public Set<String> getPartsKeywords() { return partsKeywords; }
    
    /**
     * Get the create-related keywords found in the input
     */
    public Set<String> getCreateKeywords() { return createKeywords; }
    
    /**
     * Get any business rule violation message
     */
    public String getBusinessRuleViolation() { return businessRuleViolation; }
    
    /**
     * Get information about any enhancement applied
     */
    public String getEnhancementApplied() { return enhancementApplied; }
    
    /**
     * Get the context score (used for advanced routing)
     */
    public double getContextScore() { return contextScore; }
    
    /**
     * Get the processing time in microseconds
     */
    public double getProcessingTime() { return processingTime; }
    
    /**
     * Get the timestamp when the response was created
     */
    public Date getTimestamp() { return timestamp; }
    
    // ==================== UTILITY METHODS FOR ADF JSF INTEGRATION ====================
    
    /**
     * Check if this is a contract query
     */
    public boolean isContractQuery() { return "CONTRACT".equals(actualRoute); }
    
    /**
     * Check if this is a parts query
     */
    public boolean isPartsQuery() { return "PARTS".equals(actualRoute); }
    
    /**
     * Check if this is a help query
     */
    public boolean isHelpQuery() { return "HELP".equals(actualRoute); }
    
    /**
     * Check if this is an error response
     */
    public boolean isError() { return "ERROR".equals(actualRoute); }
    
    /**
     * Check if this is a parts creation error
     */
    public boolean isPartsCreateError() { return "PARTS_CREATE_ERROR".equals(actualRoute); }
    
    /**
     * Check if a contract ID was extracted
     */
    public boolean hasContractId() { return contractId != null && !contractId.isEmpty(); }
    
    /**
     * Check if any business rule was violated
     */
    public boolean hasBusinessRuleViolation() { return businessRuleViolation != null; }
    
    /**
     * Check if any enhancement was applied
     */
    public boolean hasEnhancement() { return enhancementApplied != null; }
    
    // ==================== UI DISPLAY HELPER METHODS ====================
    
    /**
     * Get the response type for UI display
     */
    public String getResponseType() { return actualRoute + "_RESPONSE"; }
    
    /**
     * Get the display-friendly route name
     */
    public String getDisplayRoute() { return actualRoute.replace("_", " "); }
    
    /**
     * Get the display-friendly intent type
     */
    public String getDisplayIntent() { return intentType.replace("_", " "); }
    
    /**
     * Get formatted processing time for display
     */
    public String getFormattedProcessingTime() {
        return String.format("%.2f μs", processingTime);
    }
    
    /**
     * Get comprehensive summary information for UI display
     */
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
        
        if (hasEnhancement()) {
            summary.append(" | Enhanced");
        }
        
        if (hasBusinessRuleViolation()) {
            summary.append(" | Business Rule Applied");
        }
        
        return summary.toString();
    }
    
    /**
     * Get spell correction message for UI display
     */
    public String getSpellCorrectionMessage() {
        if (hasSpellCorrections) {
            return "Query corrected from: \"" + originalInput + "\" to: \"" + correctedInput + "\"";
        }
        return "No spell corrections applied";
    }
    
    /**
     * Get performance summary for monitoring
     */
    public String getPerformanceSummary() {
        return String.format("Processing completed in %.2f μs at %s", processingTime, timestamp);
    }
    
    // ==================== LOGGING AND DEBUGGING METHODS ====================
    
    /**
     * Convert to JSON string for logging/debugging
     */
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
    
    /**
     * Compact string representation for logging
     */
    @Override
    public String toString() {
        return String.format("EnhancedMLResponse{route=%s, intent=%s, contractId=%s, time=%.2fμs}", 
                           actualRoute, intentType, contractId, processingTime);
    }
    
    // ==================== VALIDATION METHODS ====================
    
    /**
     * Validate that the response is properly formed
     */
    public boolean isValid() {
        return actualRoute != null && !actualRoute.isEmpty() && 
               routingReason != null && !routingReason.isEmpty() &&
               intentType != null && !intentType.isEmpty();
    }
    
    /**
     * Check if the response indicates success (not an error)
     */
    public boolean isSuccess() {
        return !isError() && isValid();
    }
    
    /**
     * Check if the response requires immediate user attention
     */
    public boolean requiresUserAttention() {
        return isError() || isPartsCreateError();
    }
    
    // ==================== ADF-SPECIFIC HELPER METHODS ====================
    
    /**
     * Get the appropriate ADF operation name based on the route
     */
    public String getADFOperationName() {
        switch (actualRoute) {
            case "CONTRACT":
                return hasContractId() ? "getContractDetails" : "searchContracts";
            case "PARTS":
                return hasContractId() ? "getContractParts" : "searchParts";
            case "HELP":
                return "showHelp";
            default:
                return "handleError";
        }
    }
    
    /**
     * Get the parameter name for ADF operation
     */
    public String getADFParameterName() {
        switch (actualRoute) {
            case "CONTRACT":
                return hasContractId() ? "contractId" : "searchCriteria";
            case "PARTS":
                return hasContractId() ? "contractId" : "searchCriteria";
            default:
                return "query";
        }
    }
    
    /**
     * Get the parameter value for ADF operation
     */
    public String getADFParameterValue() {
        switch (actualRoute) {
            case "CONTRACT":
                return hasContractId() ? contractId : correctedInput;
            case "PARTS":
                return hasContractId() ? contractId : correctedInput;
            default:
                return correctedInput;
        }
    }
    
    /**
     * Get the success message for UI display
     */
    public String getSuccessMessage() {
        switch (actualRoute) {
            case "CONTRACT":
                return hasContractId() ? 
                    "Contract " + contractId + " details retrieved successfully" :
                    "Contract search completed for: " + correctedInput;
            case "PARTS":
                return hasContractId() ? 
                    "Parts for contract " + contractId + " retrieved successfully" :
                    "Parts search completed for: " + correctedInput;
            case "HELP":
                return "Help information displayed successfully";
            default:
                return "Query processed successfully";
        }
    }
}
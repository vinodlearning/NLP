package view.beans;

// ==================== REQUIRED IMPORTS ====================
// Add these imports to your existing JSF managed bean

import model.nlp.EnhancedMLController;
import model.nlp.EnhancedMLResponse;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.binding.OperationBinding;
import oracle.jbo.ApplicationModule;

/**
 * JSF Managed Bean Template for Contract Query Processing
 * 
 * INTEGRATION INSTRUCTIONS:
 * 1. Copy the imports above to your existing managed bean
 * 2. Copy the properties below to your bean
 * 3. Replace your onUserSendAction() method with the one provided
 * 4. Copy all the helper methods to your bean
 * 5. Ensure your ADF bindings are properly configured
 * 
 * @author Contract Query Processing System
 * @version 1.0
 */
@ManagedBean(name = "contractQueryBean")
@ViewScoped
public class ContractQueryBeanTemplate {
    
    // ==================== PROPERTIES TO ADD TO YOUR BEAN ====================
    
    // User input and response properties
    private String userQuery;
    private String responseMessage;
    private String routingInfo;
    private boolean showResponse = false;
    
    // ML processing results
    private EnhancedMLResponse lastResponse;
    private String contractId;
    private String queryType;
    
    // UI control properties
    private boolean showSpellCorrection = false;
    private boolean showPerformanceInfo = false;
    private String spellCorrectionMessage;
    private String performanceMessage;
    
    // ==================== MAIN ACTION METHOD ====================
    
    /**
     * MAIN METHOD - Replace your existing onUserSendAction() with this code
     * 
     * This method handles all user query processing with intelligent routing
     */
    public void onUserSendAction() {
        try {
            // Step 1: Validate input
            if (userQuery == null || userQuery.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Please enter a query");
                return;
            }
            
            // Step 2: Process query with Enhanced ML Controller
            EnhancedMLController controller = EnhancedMLController.getInstance();
            EnhancedMLResponse response = controller.processUserQuery(userQuery);
            
            // Step 3: Store response for UI display
            this.lastResponse = response;
            this.showResponse = true;
            
            // Step 4: Update UI properties
            updateUIProperties(response);
            
            // Step 5: Route to appropriate ADF operation based on ML decision
            switch (response.getActualRoute()) {
                case "CONTRACT":
                    handleContractQuery(response);
                    break;
                    
                case "PARTS":
                    handlePartsQuery(response);
                    break;
                    
                case "HELP":
                    handleHelpQuery(response);
                    break;
                    
                case "PARTS_CREATE_ERROR":
                    handlePartsCreateError(response);
                    break;
                    
                case "ERROR":
                    handleSystemError(response);
                    break;
                    
                default:
                    addMessage(FacesMessage.SEVERITY_ERROR, "Unknown query type: " + response.getActualRoute());
            }
            
            // Step 6: Log for monitoring (optional)
            logQueryProcessing(response);
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "System error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== ROUTING HANDLER METHODS ====================
    
    /**
     * Handle CONTRACT queries - Call your ADF operations
     */
    private void handleContractQuery(EnhancedMLResponse response) {
        try {
            if (response.hasContractId()) {
                // Specific contract lookup
                this.contractId = response.getContractId();
                this.queryType = "CONTRACT_SPECIFIC";
                
                // Call ADF method operation for specific contract
                callADFOperation("getContractDetails", "contractId", contractId);
                
                this.responseMessage = response.getSuccessMessage();
                addMessage(FacesMessage.SEVERITY_INFO, "Contract " + contractId + " details retrieved");
                
            } else {
                // General contract search
                this.queryType = "CONTRACT_SEARCH";
                
                // Call ADF method operation for general contract queries
                callADFOperation("searchContracts", "searchCriteria", response.getCorrectedInput());
                
                this.responseMessage = response.getSuccessMessage();
                addMessage(FacesMessage.SEVERITY_INFO, "Contract search completed");
            }
            
            // Show spell corrections if applied
            if (response.hasSpellCorrections()) {
                this.showSpellCorrection = true;
                this.spellCorrectionMessage = response.getSpellCorrectionMessage();
                addMessage(FacesMessage.SEVERITY_INFO, "Query corrected: " + response.getCorrectedInput());
            }
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error processing contract query: " + e.getMessage());
            this.responseMessage = "Failed to process contract query";
        }
    }
    
    /**
     * Handle PARTS queries - Call your ADF operations
     */
    private void handlePartsQuery(EnhancedMLResponse response) {
        try {
            if (response.hasContractId()) {
                // Parts lookup for specific contract
                this.contractId = response.getContractId();
                this.queryType = "PARTS_BY_CONTRACT";
                
                // Call ADF method operation for contract parts
                callADFOperation("getContractParts", "contractId", contractId);
                
                this.responseMessage = response.getSuccessMessage();
                addMessage(FacesMessage.SEVERITY_INFO, "Parts for contract " + contractId + " retrieved");
                
            } else {
                // General parts search
                this.queryType = "PARTS_SEARCH";
                
                // Call ADF method operation for general parts search
                callADFOperation("searchParts", "searchCriteria", response.getCorrectedInput());
                
                this.responseMessage = response.getSuccessMessage();
                addMessage(FacesMessage.SEVERITY_INFO, "Parts search completed");
            }
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error processing parts query: " + e.getMessage());
            this.responseMessage = "Failed to process parts query";
        }
    }
    
    /**
     * Handle HELP queries - Show help information
     */
    private void handleHelpQuery(EnhancedMLResponse response) {
        this.responseMessage = "Contract Creation Help:\n\n" +
                              "1. Navigate to Contracts → Create New Contract\n" +
                              "2. Fill in all required fields:\n" +
                              "   - Contract Name\n" +
                              "   - Customer Information\n" +
                              "   - Effective Date\n" +
                              "   - Expiration Date\n" +
                              "3. Add contract terms and conditions\n" +
                              "4. Submit for approval workflow\n" +
                              "5. Track status in contract management\n\n" +
                              "For additional assistance, contact your system administrator.";
        
        this.queryType = "HELP_RESPONSE";
        addMessage(FacesMessage.SEVERITY_INFO, "Help information displayed successfully");
    }
    
    /**
     * Handle PARTS_CREATE_ERROR - Business rule violation
     */
    private void handlePartsCreateError(EnhancedMLResponse response) {
        this.responseMessage = "❌ Parts Creation Not Supported\n\n" +
                              "Parts cannot be created through this interface. " +
                              "Parts are loaded from Excel files by the system administrator.\n\n" +
                              "If you need to:\n" +
                              "• View parts: Use queries like 'show parts for contract 123456'\n" +
                              "• Add parts: Contact your system administrator\n" +
                              "• Update parts: Use the Parts Management module";
        
        this.queryType = "BUSINESS_RULE_ERROR";
        addMessage(FacesMessage.SEVERITY_WARN, "Parts creation not allowed - Business rule enforced");
    }
    
    /**
     * Handle system errors
     */
    private void handleSystemError(EnhancedMLResponse response) {
        this.responseMessage = "⚠️ System Error\n\n" +
                              "An error occurred while processing your query:\n" +
                              response.getRoutingReason() + "\n\n" +
                              "Please try again or contact support if the issue persists.";
        
        this.queryType = "SYSTEM_ERROR";
        addMessage(FacesMessage.SEVERITY_ERROR, "System error: " + response.getRoutingReason());
    }
    
    // ==================== ADF INTEGRATION METHODS ====================
    
    /**
     * Call ADF method operation with parameters
     */
    private void callADFOperation(String operationName, String paramName, String paramValue) {
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            OperationBinding operationBinding = bindings.getOperationBinding(operationName);
            
            if (operationBinding == null) {
                throw new Exception("Operation '" + operationName + "' not found in page bindings");
            }
            
            // Set parameter
            operationBinding.getParamsMap().put(paramName, paramValue);
            
            // Execute operation
            Object result = operationBinding.execute();
            
            // Check for errors
            if (!operationBinding.getErrors().isEmpty()) {
                throw new Exception("ADF operation failed: " + operationBinding.getErrors());
            }
            
            // Operation succeeded
            System.out.println("ADF Operation '" + operationName + "' executed successfully");
            
        } catch (Exception e) {
            System.err.println("ADF Operation Error: " + e.getMessage());
            throw new RuntimeException("Failed to call ADF operation: " + operationName, e);
        }
    }
    
    /**
     * Get ADF bindings container
     */
    private DCBindingContainer getBindings() {
        return (DCBindingContainer) FacesContext.getCurrentInstance()
            .getExternalContext().getRequestMap().get("bindings");
    }
    
    // ==================== UI HELPER METHODS ====================
    
    /**
     * Update UI properties based on ML response
     */
    private void updateUIProperties(EnhancedMLResponse response) {
        // Update routing information
        this.routingInfo = response.getSummaryInfo();
        
        // Update spell correction info
        if (response.hasSpellCorrections()) {
            this.showSpellCorrection = true;
            this.spellCorrectionMessage = response.getSpellCorrectionMessage();
        }
        
        // Update performance info
        this.showPerformanceInfo = true;
        this.performanceMessage = response.getPerformanceSummary();
        
        // Extract contract ID if available
        if (response.hasContractId()) {
            this.contractId = response.getContractId();
        }
    }
    
    /**
     * Add FacesMessage for user feedback
     */
    private void addMessage(FacesMessage.Severity severity, String message) {
        String summary = severity == FacesMessage.SEVERITY_ERROR ? "Error" : 
                        severity == FacesMessage.SEVERITY_WARN ? "Warning" : "Info";
        
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, message));
    }
    
    /**
     * Log query processing for monitoring
     */
    private void logQueryProcessing(EnhancedMLResponse response) {
        System.out.println("=== Contract Query Processing Log ===");
        System.out.println("Timestamp: " + response.getTimestamp());
        System.out.println("Original Query: " + response.getOriginalInput());
        System.out.println("Corrected Query: " + response.getCorrectedInput());
        System.out.println("Route: " + response.getActualRoute());
        System.out.println("Intent: " + response.getIntentType());
        System.out.println("Contract ID: " + response.getContractId());
        System.out.println("Processing Time: " + response.getFormattedProcessingTime());
        System.out.println("Spell Corrections: " + response.hasSpellCorrections());
        System.out.println("Business Rule: " + response.getBusinessRuleViolation());
        System.out.println("Enhancement: " + response.getEnhancementApplied());
        System.out.println("JSON: " + response.toJsonString());
        System.out.println("=====================================");
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Clear all form data
     */
    public void clearForm() {
        this.userQuery = null;
        this.responseMessage = null;
        this.routingInfo = null;
        this.showResponse = false;
        this.lastResponse = null;
        this.contractId = null;
        this.queryType = null;
        this.showSpellCorrection = false;
        this.showPerformanceInfo = false;
        this.spellCorrectionMessage = null;
        this.performanceMessage = null;
    }
    
    /**
     * Reset form for new query
     */
    public void resetForm() {
        clearForm();
        addMessage(FacesMessage.SEVERITY_INFO, "Form reset - Ready for new query");
    }
    
    // ==================== GETTERS AND SETTERS ====================
    
    // User input properties
    public String getUserQuery() { return userQuery; }
    public void setUserQuery(String userQuery) { this.userQuery = userQuery; }
    
    // Response properties
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    
    public String getRoutingInfo() { return routingInfo; }
    public void setRoutingInfo(String routingInfo) { this.routingInfo = routingInfo; }
    
    public boolean isShowResponse() { return showResponse; }
    public void setShowResponse(boolean showResponse) { this.showResponse = showResponse; }
    
    // ML processing results
    public EnhancedMLResponse getLastResponse() { return lastResponse; }
    public void setLastResponse(EnhancedMLResponse lastResponse) { this.lastResponse = lastResponse; }
    
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    
    public String getQueryType() { return queryType; }
    public void setQueryType(String queryType) { this.queryType = queryType; }
    
    // UI control properties
    public boolean isShowSpellCorrection() { return showSpellCorrection; }
    public void setShowSpellCorrection(boolean showSpellCorrection) { this.showSpellCorrection = showSpellCorrection; }
    
    public boolean isShowPerformanceInfo() { return showPerformanceInfo; }
    public void setShowPerformanceInfo(boolean showPerformanceInfo) { this.showPerformanceInfo = showPerformanceInfo; }
    
    public String getSpellCorrectionMessage() { return spellCorrectionMessage; }
    public void setSpellCorrectionMessage(String spellCorrectionMessage) { this.spellCorrectionMessage = spellCorrectionMessage; }
    
    public String getPerformanceMessage() { return performanceMessage; }
    public void setPerformanceMessage(String performanceMessage) { this.performanceMessage = performanceMessage; }
    
    // ==================== COMPUTED PROPERTIES FOR UI ====================
    
    /**
     * Check if there's a valid response to display
     */
    public boolean hasValidResponse() {
        return lastResponse != null && lastResponse.isValid();
    }
    
    /**
     * Get display-friendly route name
     */
    public String getDisplayRoute() {
        return lastResponse != null ? lastResponse.getDisplayRoute() : "N/A";
    }
    
    /**
     * Get display-friendly intent type
     */
    public String getDisplayIntent() {
        return lastResponse != null ? lastResponse.getDisplayIntent() : "N/A";
    }
    
    /**
     * Check if contract ID is available
     */
    public boolean hasContractId() {
        return contractId != null && !contractId.isEmpty();
    }
    
    /**
     * Get formatted processing time
     */
    public String getFormattedProcessingTime() {
        return lastResponse != null ? lastResponse.getFormattedProcessingTime() : "N/A";
    }
}
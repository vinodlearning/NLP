package view.practice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.Serializable;

/**
 * Contract Query Managed Bean for JSF UI Integration
 * 
 * Architecture Flow:
 * UI Screen → ContractQueryManagedBean → OptimizedNLPController → [Helper, ContractModel, PartsModel] → JSON Response → Bean → UI
 * 
 * This bean serves as the intermediary between JSF UI components and the NLP processing system,
 * providing a clean separation of concerns and maintaining session state.
 * 
 * @author Contract Management System
 * @version 3.0
 */
@ManagedBean(name = "contractQueryBean")
@SessionScoped
public class ContractQueryManagedBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ContractQueryManagedBean.class.getName());
    
    // UI Binding Properties
    private String userQuery = "";
    private String systemResponse = "";
    private boolean loading = false;
    private boolean hasResponse = false;
    
    // Session Management
    private List<QueryResponsePair> conversationHistory;
    private Map<String, Object> sessionMetrics;
    private Date sessionStartTime;
    
    // NLP Controller instance
    private OptimizedNLPController nlpController;
    
    // Configuration and State
    private boolean showRoutingInfo = false;
    private boolean showPerformanceMetrics = false;
    private String currentModel = "";
    private String routingReason = "";
    
    /**
     * Initialize the managed bean
     * Time Complexity: O(n) where n is configuration items (one-time cost)
     */
    public ContractQueryManagedBean() {
        try {
            // Initialize NLP Controller
            this.nlpController = new OptimizedNLPController();
            
            // Initialize session state
            this.conversationHistory = new ArrayList<>();
            this.sessionMetrics = new HashMap<>();
            this.sessionStartTime = new Date();
            
            logger.info("ContractQueryManagedBean initialized successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize ContractQueryManagedBean", e);
            addErrorMessage("System initialization failed. Please refresh the page.");
        }
    }
    
    /**
     * Process user query - main entry point from UI
     * 
     * Time Complexity: O(w) where w is number of words in query
     * Space Complexity: O(1) additional space
     */
    public void processQuery() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate input
            if (userQuery == null || userQuery.trim().isEmpty()) {
                addErrorMessage("Please enter a query.");
                return;
            }
            
            // Set loading state for UI
            loading = true;
            hasResponse = false;
            
            logger.info("Processing user query: " + userQuery);
            
            // Call NLP Controller - this is where the routing logic happens
            String response = nlpController.processUserInput(userQuery.trim());
            
            // Parse response to extract routing information
            parseResponseMetadata(response);
            
            // Update UI state
            systemResponse = response;
            hasResponse = true;
            
            // Store in conversation history
            conversationHistory.add(new QueryResponsePair(userQuery.trim(), response, new Date()));
            
            // Update session metrics
            updateSessionMetrics(System.currentTimeMillis() - startTime);
            
            // Clear input for next query
            userQuery = "";
            
            addSuccessMessage("Query processed successfully and routed to " + currentModel);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing query: " + userQuery, e);
            addErrorMessage("Error processing query: " + e.getMessage());
            systemResponse = "An error occurred while processing your request.";
            
        } finally {
            loading = false;
        }
    }
    
    /**
     * Parse response metadata to extract routing information
     * Time Complexity: O(1) for simple JSON parsing
     */
    private void parseResponseMetadata(String response) {
        try {
            // Simple parsing to extract routing info
            if (response.contains("\"targetModel\"")) {
                int start = response.indexOf("\"targetModel\": \"") + 16;
                int end = response.indexOf("\"", start);
                if (end > start) {
                    currentModel = response.substring(start, end);
                }
            }
            
            if (response.contains("\"routingReason\"")) {
                int start = response.indexOf("\"routingReason\": \"") + 18;
                int end = response.indexOf("\"", start);
                if (end > start) {
                    routingReason = response.substring(start, end);
                }
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to parse response metadata", e);
            currentModel = "Unknown";
            routingReason = "Parsing failed";
        }
    }
    
    /**
     * Update session performance metrics
     * Time Complexity: O(1)
     */
    private void updateSessionMetrics(long processingTime) {
        sessionMetrics.put("lastProcessingTime", processingTime);
        sessionMetrics.put("totalQueries", conversationHistory.size());
        sessionMetrics.put("sessionDuration", new Date().getTime() - sessionStartTime.getTime());
        
        // Get metrics from NLP Controller
        Map<String, Object> nlpMetrics = nlpController.getPerformanceMetrics();
        sessionMetrics.putAll(nlpMetrics);
    }
    
    /**
     * Clear conversation history and reset session
     * Time Complexity: O(1)
     */
    public void clearHistory() {
        conversationHistory.clear();
        sessionMetrics.clear();
        sessionStartTime = new Date();
        nlpController.resetSession();
        
        userQuery = "";
        systemResponse = "";
        hasResponse = false;
        
        addInfoMessage("Conversation history cleared.");
    }
    
    /**
     * Reload system configurations from files
     * Time Complexity: O(n) where n is configuration items
     */
    public void reloadConfigurations() {
        try {
            nlpController.reloadConfigurations();
            addSuccessMessage("Configurations reloaded successfully.");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to reload configurations", e);
            addErrorMessage("Failed to reload configurations: " + e.getMessage());
        }
    }
    
    /**
     * Test routing logic with sample queries
     * Time Complexity: O(w * q) where w is words per query and q is number of queries
     */
    public void testRoutingLogic() {
        String[] testQueries = {
            "show parts for contract 123456",
            "create a new contract",
            "display contract details for ABC789",
            "failed parts in production line",
            "how to create contract step by step",
            "show contract 12345 for account 567890"
        };
        
        StringBuilder testResults = new StringBuilder();
        testResults.append("Routing Test Results:\n\n");
        
        for (String query : testQueries) {
            String response = nlpController.processUserInput(query);
            parseResponseMetadata(response);
            
            testResults.append("Query: ").append(query).append("\n");
            testResults.append("Routed to: ").append(currentModel).append("\n");
            testResults.append("Reason: ").append(routingReason).append("\n\n");
        }
        
        systemResponse = testResults.toString();
        hasResponse = true;
        
        addInfoMessage("Routing logic test completed.");
    }
    
    /**
     * Export conversation history for analysis
     * Time Complexity: O(n) where n is number of conversations
     */
    public String exportConversationHistory() {
        StringBuilder export = new StringBuilder();
        export.append("Conversation History Export\n");
        export.append("Session started: ").append(sessionStartTime).append("\n");
        export.append("Total queries: ").append(conversationHistory.size()).append("\n\n");
        
        for (int i = 0; i < conversationHistory.size(); i++) {
            QueryResponsePair pair = conversationHistory.get(i);
            export.append("Query ").append(i + 1).append(" (").append(pair.getTimestamp()).append("):\n");
            export.append("User: ").append(pair.getQuery()).append("\n");
            export.append("System: ").append(pair.getResponse()).append("\n\n");
        }
        
        return export.toString();
    }
    
    /**
     * Add success message to JSF context
     */
    private void addSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }
    
    /**
     * Add error message to JSF context
     */
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }
    
    /**
     * Add info message to JSF context
     */
    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }
    
    // UI Action Methods
    
    /**
     * Handle Enter key press in query input
     */
    public void handleKeyPress() {
        processQuery();
    }
    
    /**
     * Toggle routing information display
     */
    public void toggleRoutingInfo() {
        showRoutingInfo = !showRoutingInfo;
    }
    
    /**
     * Toggle performance metrics display
     */
    public void toggleMetrics() {
        showPerformanceMetrics = !showPerformanceMetrics;
    }
    
    // Getters and Setters for JSF binding
    
    public String getUserQuery() {
        return userQuery;
    }
    
    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }
    
    public String getSystemResponse() {
        return systemResponse;
    }
    
    public boolean isLoading() {
        return loading;
    }
    
    public boolean isHasResponse() {
        return hasResponse;
    }
    
    public List<QueryResponsePair> getConversationHistory() {
        return conversationHistory;
    }
    
    public Map<String, Object> getSessionMetrics() {
        return sessionMetrics;
    }
    
    public boolean isShowRoutingInfo() {
        return showRoutingInfo;
    }
    
    public void setShowRoutingInfo(boolean showRoutingInfo) {
        this.showRoutingInfo = showRoutingInfo;
    }
    
    public boolean isShowPerformanceMetrics() {
        return showPerformanceMetrics;
    }
    
    public void setShowPerformanceMetrics(boolean showPerformanceMetrics) {
        this.showPerformanceMetrics = showPerformanceMetrics;
    }
    
    public String getCurrentModel() {
        return currentModel;
    }
    
    public String getRoutingReason() {
        return routingReason;
    }
    
    public Date getSessionStartTime() {
        return sessionStartTime;
    }
    
    public int getConversationCount() {
        return conversationHistory.size();
    }
    
    public long getSessionDuration() {
        return new Date().getTime() - sessionStartTime.getTime();
    }
    
    /**
     * Inner class to represent query-response pairs in conversation history
     */
    public static class QueryResponsePair implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final String query;
        private final String response;
        private final Date timestamp;
        
        public QueryResponsePair(String query, String response, Date timestamp) {
            this.query = query;
            this.response = response;
            this.timestamp = timestamp;
        }
        
        public String getQuery() {
            return query;
        }
        
        public String getResponse() {
            return response;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
    }
}
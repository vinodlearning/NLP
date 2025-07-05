package com.adf.nlp.bean;

import com.adf.nlp.controller.EnhancedNLPController;
import com.adf.nlp.response.NLPResponse;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * JSF Managed Bean for ADF NLP Query Processing
 * Handles user queries and returns structured JSON responses
 */
@ManagedBean(name = "nlpQueryBean")
@ViewScoped
public class NLPQueryBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Controller instance
    private EnhancedNLPController nlpController;
    
    // UI Properties
    private String userQuery;
    private String queryResult;
    private boolean showResult;
    private List<QueryHistory> queryHistory;
    
    // Response tracking
    private NLPResponse currentResponse;
    private String currentResponseJson;
    
    /**
     * Constructor
     */
    public NLPQueryBean() {
        this.nlpController = new EnhancedNLPController();
        this.queryHistory = new ArrayList<>();
        this.showResult = false;
    }
    
    /**
     * Process user query and return JSON response
     */
    public String processQuery() {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            queryResult = "Please enter a valid query.";
            showResult = true;
            return null;
        }
        
        try {
            // Process the query
            currentResponse = nlpController.processQuery(userQuery);
            currentResponseJson = currentResponse.toString();
            
            // Add to history
            addToHistory(userQuery, currentResponse);
            
            // Set result for display
            queryResult = formatResultForDisplay(currentResponse);
            showResult = true;
            
            // Clear input
            userQuery = "";
            
        } catch (Exception e) {
            queryResult = "Error processing query: " + e.getMessage();
            showResult = true;
        }
        
        return null; // Stay on same page
    }
    
    /**
     * Get JSON response for ADF integration
     */
    public void getJSONResponse() {
        if (currentResponse == null) {
            sendJSONError("No query processed yet");
            return;
        }
        
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(currentResponseJson);
            response.getWriter().flush();
            
            context.responseComplete();
            
        } catch (IOException e) {
            sendJSONError("Error generating JSON response: " + e.getMessage());
        }
    }
    
    /**
     * Process query and return JSON directly (for AJAX calls)
     */
    public void processQueryAjax() {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            sendJSONError("Query cannot be empty");
            return;
        }
        
        try {
            // Process the query
            currentResponse = nlpController.processQuery(userQuery);
            currentResponseJson = currentResponse.toString();
            
            // Add to history
            addToHistory(userQuery, currentResponse);
            
            // Send JSON response
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(currentResponseJson);
            response.getWriter().flush();
            
            context.responseComplete();
            
        } catch (Exception e) {
            sendJSONError("Error processing query: " + e.getMessage());
        }
    }
    
    /**
     * Clear query history
     */
    public String clearHistory() {
        queryHistory.clear();
        return null;
    }
    
    /**
     * Reset form
     */
    public String reset() {
        userQuery = "";
        queryResult = "";
        showResult = false;
        currentResponse = null;
        currentResponseJson = null;
        return null;
    }
    
    /**
     * Format response for display in UI
     */
    private String formatResultForDisplay(NLPResponse response) {
        StringBuilder result = new StringBuilder();
        
        // Query metadata
        result.append("Query Type: ").append(response.getQueryMetadata().getQueryType()).append("\n");
        result.append("Action Type: ").append(response.getQueryMetadata().getActionType()).append("\n");
        result.append("Processing Time: ").append(response.getQueryMetadata().getProcessingTimeMs()).append("ms\n\n");
        
        // Header information
        if (response.getHeader().hasAnyValue()) {
            result.append("Extracted Information:\n");
            if (response.getHeader().getContractNumber() != null) {
                result.append("- Contract Number: ").append(response.getHeader().getContractNumber()).append("\n");
            }
            if (response.getHeader().getPartNumber() != null) {
                result.append("- Part Number: ").append(response.getHeader().getPartNumber()).append("\n");
            }
            if (response.getHeader().getCustomerNumber() != null) {
                result.append("- Customer Number: ").append(response.getHeader().getCustomerNumber()).append("\n");
            }
            if (response.getHeader().getCustomerName() != null) {
                result.append("- Customer Name: ").append(response.getHeader().getCustomerName()).append("\n");
            }
            if (response.getHeader().getCreatedBy() != null) {
                result.append("- Created By: ").append(response.getHeader().getCreatedBy()).append("\n");
            }
            result.append("\n");
        }
        
        // Display entities
        if (!response.getDisplayEntities().isEmpty()) {
            result.append("Display Fields:\n");
            for (String entity : response.getDisplayEntities()) {
                result.append("- ").append(entity).append("\n");
            }
            result.append("\n");
        }
        
        // Filters/Entities
        if (response.hasEntities()) {
            result.append("Applied Filters:\n");
            for (NLPResponse.Entity entity : response.getEntities()) {
                result.append("- ").append(entity.getAttribute())
                      .append(" ").append(entity.getOperation())
                      .append(" ").append(entity.getValue()).append("\n");
            }
            result.append("\n");
        }
        
        // Errors
        if (response.hasErrors()) {
            result.append("Errors:\n");
            for (NLPResponse.Error error : response.getErrors()) {
                result.append("- ").append(error.getCode())
                      .append(": ").append(error.getMessage()).append("\n");
            }
        }
        
        return result.toString();
    }
    
    /**
     * Add query to history
     */
    private void addToHistory(String query, NLPResponse response) {
        QueryHistory history = new QueryHistory();
        history.setQuery(query);
        history.setQueryType(response.getQueryMetadata().getQueryType());
        history.setActionType(response.getQueryMetadata().getActionType());
        history.setProcessingTime(response.getQueryMetadata().getProcessingTimeMs());
        history.setTimestamp(new java.util.Date());
        history.setHasErrors(response.hasErrors());
        
        queryHistory.add(0, history); // Add to beginning
        
        // Keep only last 50 queries
        if (queryHistory.size() > 50) {
            queryHistory = queryHistory.subList(0, 50);
        }
    }
    
    /**
     * Send JSON error response
     */
    private void sendJSONError(String errorMessage) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String errorJson = "{\n" +
                "  \"header\": {\n" +
                "    \"contractNumber\": null,\n" +
                "    \"partNumber\": null,\n" +
                "    \"customerNumber\": null,\n" +
                "    \"customerName\": null,\n" +
                "    \"createdBy\": null\n" +
                "  },\n" +
                "  \"queryMetadata\": {\n" +
                "    \"queryType\": \"ERROR\",\n" +
                "    \"actionType\": \"ERROR\",\n" +
                "    \"processingTimeMs\": 0\n" +
                "  },\n" +
                "  \"entities\": [],\n" +
                "  \"displayEntities\": [],\n" +
                "  \"errors\": [\n" +
                "    {\n" +
                "      \"code\": \"PROCESSING_ERROR\",\n" +
                "      \"message\": \"" + errorMessage + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
            
            response.getWriter().write(errorJson);
            response.getWriter().flush();
            
            context.responseComplete();
            
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Error sending JSON error response: " + e.getMessage());
        }
    }
    
    // Getters and Setters
    public String getUserQuery() {
        return userQuery;
    }
    
    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }
    
    public String getQueryResult() {
        return queryResult;
    }
    
    public void setQueryResult(String queryResult) {
        this.queryResult = queryResult;
    }
    
    public boolean isShowResult() {
        return showResult;
    }
    
    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }
    
    public List<QueryHistory> getQueryHistory() {
        return queryHistory;
    }
    
    public void setQueryHistory(List<QueryHistory> queryHistory) {
        this.queryHistory = queryHistory;
    }
    
    public NLPResponse getCurrentResponse() {
        return currentResponse;
    }
    
    public String getCurrentResponseJson() {
        return currentResponseJson;
    }
    
    public boolean hasCurrentResponse() {
        return currentResponse != null;
    }
    
    public int getHistorySize() {
        return queryHistory.size();
    }
    
    /**
     * Inner class for query history
     */
    public static class QueryHistory implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String query;
        private String queryType;
        private String actionType;
        private Long processingTime;
        private java.util.Date timestamp;
        private boolean hasErrors;
        
        // Getters and Setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public String getQueryType() { return queryType; }
        public void setQueryType(String queryType) { this.queryType = queryType; }
        
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        
        public Long getProcessingTime() { return processingTime; }
        public void setProcessingTime(Long processingTime) { this.processingTime = processingTime; }
        
        public java.util.Date getTimestamp() { return timestamp; }
        public void setTimestamp(java.util.Date timestamp) { this.timestamp = timestamp; }
        
        public boolean isHasErrors() { return hasErrors; }
        public void setHasErrors(boolean hasErrors) { this.hasErrors = hasErrors; }
        
        public String getFormattedTimestamp() {
            if (timestamp == null) return "";
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        }
    }
}
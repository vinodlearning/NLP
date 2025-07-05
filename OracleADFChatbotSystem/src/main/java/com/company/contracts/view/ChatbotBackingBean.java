package com.company.contracts.view;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Oracle ADF Chatbot Backing Bean for Contract Portal
 * 
 * This backing bean handles the interaction between ADF UI components
 * and the Apache OpenNLP-based contract processing system.
 * 
 * Integration Flow:
 * ADF Page → ChatbotBackingBean → ContractNLPService → OpenNLP Models → Response
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ChatbotBackingBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChatbotBackingBean.class.getName());
    
    // UI Component Bindings
    private RichInputText userQueryInput;
    private RichOutputText chatResponseOutput;
    
    // Bean Properties
    private String userQuery = "";
    private String chatResponse = "";
    private String responseType = "";
    private boolean hasResponse = false;
    private boolean isProcessing = false;
    
    // Conversation Management
    private List<ChatMessage> conversationHistory;
    private Map<String, Object> sessionContext;
    private Date sessionStartTime;
    
    // Services
    private ContractNLPService nlpService;
    private OpenNLPModelManager modelManager;
    private ADFResponseBuilder responseBuilder;
    
    // Configuration
    private Properties adfConfig;
    
    /**
     * Constructor - Initialize the backing bean
     */
    public ChatbotBackingBean() {
        try {
            initializeServices();
            initializeSession();
            loadConfiguration();
            
            logger.info("ChatbotBackingBean initialized successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize ChatbotBackingBean", e);
            addErrorMessage("Chatbot initialization failed. Please refresh the page.");
        }
    }
    
    /**
     * Initialize NLP services
     */
    private void initializeServices() throws Exception {
        // Initialize Model Manager first
        modelManager = new OpenNLPModelManager();
        
        // Initialize NLP Service with Model Manager
        nlpService = new ContractNLPService(modelManager);
        
        // Initialize Response Builder
        responseBuilder = new ADFResponseBuilder();
    }
    
    /**
     * Initialize session state
     */
    private void initializeSession() {
        conversationHistory = new ArrayList<>();
        sessionContext = new HashMap<>();
        sessionStartTime = new Date();
        
        // Add welcome message
        ChatMessage welcomeMessage = new ChatMessage(
            "system",
            "Hello! I'm your Contract Portal assistant. I can help you with contracts, parts, and general guidance. What can I help you with today?",
            new Date(),
            "WELCOME"
        );
        conversationHistory.add(welcomeMessage);
    }
    
    /**
     * Load ADF configuration from properties file
     */
    private void loadConfiguration() {
        try {
            adfConfig = new Properties();
            // Load from classpath
            adfConfig.load(getClass().getClassLoader().getResourceAsStream("config/adf_integration.properties"));
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load ADF configuration, using defaults", e);
            adfConfig = getDefaultConfiguration();
        }
    }
    
    /**
     * Get default configuration if file not found
     */
    private Properties getDefaultConfiguration() {
        Properties defaults = new Properties();
        defaults.setProperty("session_scope", "true");
        defaults.setProperty("cache_responses", "true");
        defaults.setProperty("max_cache_size", "100");
        defaults.setProperty("include_confidence_scores", "true");
        defaults.setProperty("enable_spell_correction", "true");
        return defaults;
    }
    
    /**
     * Main action method - Process user query
     * Called from ADF page when user submits query
     */
    public void processUserQuery(ActionEvent actionEvent) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate input
            if (userQuery == null || userQuery.trim().isEmpty()) {
                addErrorMessage("Please enter a query.");
                return;
            }
            
            // Set processing state
            isProcessing = true;
            hasResponse = false;
            
            logger.info("Processing user query: " + userQuery);
            
            // Process with NLP Service
            NLPResponse nlpResponse = nlpService.processQuery(userQuery.trim());
            
            // Build ADF-friendly response
            ADFChatResponse adfResponse = responseBuilder.buildChatResponse(nlpResponse);
            
            // Update UI properties
            chatResponse = adfResponse.getFormattedResponse();
            responseType = adfResponse.getResponseType();
            hasResponse = true;
            
            // Add to conversation history
            addToConversationHistory(userQuery.trim(), chatResponse, nlpResponse.getConfidence());
            
            // Update session context
            updateSessionContext(nlpResponse);
            
            // Clear input for next query
            userQuery = "";
            
            // Add success message
            long processingTime = System.currentTimeMillis() - startTime;
            String successMsg = "Query processed in " + processingTime + "ms";
            addInfoMessage(successMsg);
            
            // Refresh UI components
            AdfFacesContext.getCurrentInstance().addPartialTarget(chatResponseOutput);
            AdfFacesContext.getCurrentInstance().addPartialTarget(userQueryInput);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing query: " + userQuery, e);
            chatResponse = "I encountered an error processing your request. Please try again.";
            responseType = "ERROR";
            hasResponse = true;
            addErrorMessage("Error processing query: " + e.getMessage());
            
        } finally {
            isProcessing = false;
        }
    }
    
    /**
     * Add message to conversation history
     */
    private void addToConversationHistory(String query, String response, double confidence) {
        // Add user message
        ChatMessage userMessage = new ChatMessage("user", query, new Date(), "USER_QUERY");
        conversationHistory.add(userMessage);
        
        // Add system response
        ChatMessage systemMessage = new ChatMessage("system", response, new Date(), responseType);
        systemMessage.setConfidence(confidence);
        conversationHistory.add(systemMessage);
        
        // Keep conversation history manageable
        int maxHistorySize = Integer.parseInt(adfConfig.getProperty("max_cache_size", "100"));
        if (conversationHistory.size() > maxHistorySize) {
            conversationHistory.subList(0, conversationHistory.size() - maxHistorySize).clear();
        }
    }
    
    /**
     * Update session context with query information
     */
    private void updateSessionContext(NLPResponse nlpResponse) {
        sessionContext.put("lastQueryTime", new Date());
        sessionContext.put("lastIntent", nlpResponse.getIntent());
        sessionContext.put("lastConfidence", nlpResponse.getConfidence());
        sessionContext.put("totalQueries", conversationHistory.size() / 2); // Divide by 2 for user+system pairs
        
        // Extract entities for context
        if (nlpResponse.getEntities() != null) {
            sessionContext.putAll(nlpResponse.getEntities());
        }
    }
    
    /**
     * Clear conversation history - Action method for ADF
     */
    public void clearConversation(ActionEvent actionEvent) {
        conversationHistory.clear();
        sessionContext.clear();
        
        // Add welcome message back
        ChatMessage welcomeMessage = new ChatMessage(
            "system",
            "Conversation cleared. How can I help you?",
            new Date(),
            "SYSTEM"
        );
        conversationHistory.add(welcomeMessage);
        
        chatResponse = "";
        hasResponse = false;
        
        addInfoMessage("Conversation history cleared.");
        
        // Refresh UI
        AdfFacesContext.getCurrentInstance().addPartialTarget(chatResponseOutput);
    }
    
    /**
     * Reload models and configuration - Action method for ADF
     */
    public void reloadModels(ActionEvent actionEvent) {
        try {
            modelManager.reloadModels();
            loadConfiguration();
            
            addInfoMessage("Models and configuration reloaded successfully.");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reloading models", e);
            addErrorMessage("Error reloading models: " + e.getMessage());
        }
    }
    
    /**
     * Get conversation history for display in ADF table/list
     */
    public List<ChatMessage> getConversationHistoryForDisplay() {
        // Return last 20 messages for UI display
        int displayCount = Math.min(20, conversationHistory.size());
        return conversationHistory.subList(
            Math.max(0, conversationHistory.size() - displayCount),
            conversationHistory.size()
        );
    }
    
    /**
     * Get session statistics for display
     */
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("sessionDuration", (new Date().getTime() - sessionStartTime.getTime()) / 1000 / 60); // minutes
        stats.put("totalMessages", conversationHistory.size());
        stats.put("totalQueries", conversationHistory.size() / 2);
        stats.put("modelsLoaded", modelManager.getLoadedModelsCount());
        return stats;
    }
    
    /**
     * Handle enter key press in input field
     */
    public void handleEnterKeyPress(ActionEvent actionEvent) {
        processUserQuery(actionEvent);
    }
    
    /**
     * Quick action methods for common queries
     */
    public void askForHelp(ActionEvent actionEvent) {
        userQuery = "How can you help me?";
        processUserQuery(actionEvent);
    }
    
    public void showContractHelp(ActionEvent actionEvent) {
        userQuery = "How to create a new contract?";
        processUserQuery(actionEvent);
    }
    
    public void showPartsHelp(ActionEvent actionEvent) {
        userQuery = "How to search for parts?";
        processUserQuery(actionEvent);
    }
    
    // Utility methods for ADF messaging
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }
    
    private void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }
    
    private void addWarningMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", message));
    }
    
    // Getters and Setters for ADF binding
    
    public String getUserQuery() {
        return userQuery;
    }
    
    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }
    
    public String getChatResponse() {
        return chatResponse;
    }
    
    public void setChatResponse(String chatResponse) {
        this.chatResponse = chatResponse;
    }
    
    public String getResponseType() {
        return responseType;
    }
    
    public boolean isHasResponse() {
        return hasResponse;
    }
    
    public boolean isProcessing() {
        return isProcessing;
    }
    
    public List<ChatMessage> getConversationHistory() {
        return conversationHistory;
    }
    
    public Date getSessionStartTime() {
        return sessionStartTime;
    }
    
    public RichInputText getUserQueryInput() {
        return userQueryInput;
    }
    
    public void setUserQueryInput(RichInputText userQueryInput) {
        this.userQueryInput = userQueryInput;
    }
    
    public RichOutputText getChatResponseOutput() {
        return chatResponseOutput;
    }
    
    public void setChatResponseOutput(RichOutputText chatResponseOutput) {
        this.chatResponseOutput = chatResponseOutput;
    }
    
    /**
     * Inner class representing a chat message
     */
    public static class ChatMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String sender;
        private String message;
        private Date timestamp;
        private String messageType;
        private double confidence;
        
        public ChatMessage(String sender, String message, Date timestamp, String messageType) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
            this.messageType = messageType;
            this.confidence = 0.0;
        }
        
        // Getters and setters
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public String getFormattedTimestamp() {
            return new java.text.SimpleDateFormat("HH:mm:ss").format(timestamp);
        }
        
        public boolean isUserMessage() {
            return "user".equals(sender);
        }
        
        public boolean isSystemMessage() {
            return "system".equals(sender);
        }
    }
}
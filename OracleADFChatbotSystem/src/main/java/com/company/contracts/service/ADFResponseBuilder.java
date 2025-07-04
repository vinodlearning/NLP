package com.company.contracts.service;

import java.util.*;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

/**
 * ADF Response Builder
 * 
 * This class converts NLP responses into ADF-friendly format
 * suitable for display in Oracle ADF UI components.
 * 
 * Features:
 * - Converts NLP responses to ADF chat responses
 * - Formats text for ADF UI display
 * - Handles error responses
 * - Includes confidence indicators
 * - Supports rich formatting
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ADFResponseBuilder {
    
    private static final Logger logger = Logger.getLogger(ADFResponseBuilder.class.getName());
    
    // Response formatting templates
    private Map<String, String> responseTemplates;
    
    /**
     * Constructor
     */
    public ADFResponseBuilder() {
        initializeResponseTemplates();
    }
    
    /**
     * Initialize response templates
     */
    private void initializeResponseTemplates() {
        responseTemplates = new HashMap<>();
        
        // Contract templates
        responseTemplates.put("CONTRACT_LOOKUP", "📋 Contract Information:\n{responseText}");
        responseTemplates.put("CONTRACT_DATES", "📅 Contract Dates:\n{responseText}");
        responseTemplates.put("CONTRACT_CUSTOMER", "👤 Customer Information:\n{responseText}");
        responseTemplates.put("CONTRACT_STATUS", "📊 Contract Status:\n{responseText}");
        responseTemplates.put("CONTRACT_VALUE", "💰 Contract Value:\n{responseText}");
        
        // Parts templates
        responseTemplates.put("PARTS_LOOKUP", "🔧 Parts Information:\n{responseText}");
        responseTemplates.put("PARTS_COUNT", "🔢 Parts Count:\n{responseText}");
        responseTemplates.put("PARTS_DETAILS", "📝 Parts Details:\n{responseText}");
        responseTemplates.put("PARTS_INVENTORY", "📦 Parts Inventory:\n{responseText}");
        
        // Help templates
        responseTemplates.put("HELP_CREATE", "📖 How to Create Contract:\n{responseText}");
        responseTemplates.put("HELP_GENERAL", "❓ General Help:\n{responseText}");
        responseTemplates.put("HELP_WORKFLOW", "🔄 Workflow Help:\n{responseText}");
        
        // Error templates
        responseTemplates.put("ERROR", "⚠️ Error:\n{responseText}");
        responseTemplates.put("UNKNOWN", "❓ I'm not sure about that. {responseText}");
    }
    
    /**
     * Build ADF chat response from NLP response
     */
    public ADFChatResponse buildChatResponse(NLPResponse nlpResponse) {
        ADFChatResponse adfResponse = new ADFChatResponse();
        
        try {
            // Basic information
            adfResponse.setOriginalQuery(nlpResponse.getOriginalQuery());
            adfResponse.setIntent(nlpResponse.getIntent());
            adfResponse.setConfidence(nlpResponse.getConfidence());
            adfResponse.setDomain(nlpResponse.getDomain());
            adfResponse.setSuccess(nlpResponse.isSuccess());
            adfResponse.setProcessingTimeMs(nlpResponse.getProcessingTimeMs());
            
            // Format response text
            String formattedResponse = formatResponseText(nlpResponse);
            adfResponse.setFormattedResponse(formattedResponse);
            
            // Set response type
            adfResponse.setResponseType(determineResponseType(nlpResponse));
            
            // Add metadata
            adfResponse.setMetadata(buildMetadata(nlpResponse));
            
            // Add UI hints
            adfResponse.setUiHints(buildUiHints(nlpResponse));
            
            logger.info("Built ADF chat response for intent: " + nlpResponse.getIntent());
            
        } catch (Exception e) {
            logger.severe("Error building ADF chat response: " + e.getMessage());
            
            // Build error response
            adfResponse = buildErrorResponse(nlpResponse, e);
        }
        
        return adfResponse;
    }
    
    /**
     * Format response text for ADF display
     */
    private String formatResponseText(NLPResponse nlpResponse) {
        String intent = nlpResponse.getIntent();
        String responseText = nlpResponse.getResponseText();
        
        // Get template for intent
        String template = responseTemplates.get(intent);
        if (template == null) {
            template = responseTemplates.get("UNKNOWN");
        }
        
        // Replace placeholder with actual response
        String formatted = template.replace("{responseText}", responseText);
        
        // Add spell correction note if applicable
        if (nlpResponse.isSpellCorrected()) {
            formatted += "\n\n💡 Note: I corrected some spelling in your query.";
        }
        
        // Add confidence indicator if low
        if (nlpResponse.getConfidence() < 0.6) {
            formatted += "\n\n🤔 I'm not entirely confident about this response. Please provide more details if needed.";
        }
        
        // Add processing time if requested
        if (nlpResponse.getProcessingTimeMs() > 1000) {
            formatted += "\n\n⏱️ Processing took " + nlpResponse.getFormattedProcessingTime();
        }
        
        return formatted;
    }
    
    /**
     * Determine response type for ADF UI
     */
    private String determineResponseType(NLPResponse nlpResponse) {
        if (!nlpResponse.isSuccess()) {
            return "ERROR";
        }
        
        String intent = nlpResponse.getIntent();
        String domain = nlpResponse.getDomain();
        
        // Map intent to ADF response type
        if (intent.startsWith("CONTRACT_")) {
            return "CONTRACT_INFO";
        } else if (intent.startsWith("PARTS_")) {
            return "PARTS_INFO";
        } else if (intent.startsWith("HELP_")) {
            return "HELP_INFO";
        } else if (intent.equals("ERROR")) {
            return "ERROR";
        } else {
            return "GENERAL";
        }
    }
    
    /**
     * Build metadata for ADF response
     */
    private Map<String, Object> buildMetadata(NLPResponse nlpResponse) {
        Map<String, Object> metadata = new HashMap<>();
        
        metadata.put("timestamp", nlpResponse.getFormattedTimestamp());
        metadata.put("confidence", nlpResponse.getConfidence());
        metadata.put("confidenceLevel", nlpResponse.getConfidenceLevel());
        metadata.put("processingTime", nlpResponse.getFormattedProcessingTime());
        metadata.put("domain", nlpResponse.getDomain());
        metadata.put("modelUsed", nlpResponse.getModelUsed());
        metadata.put("entityCount", nlpResponse.getEntityCount());
        
        // Add entity information
        if (nlpResponse.getEntities() != null && !nlpResponse.getEntities().isEmpty()) {
            metadata.put("entities", nlpResponse.getEntities());
        }
        
        return metadata;
    }
    
    /**
     * Build UI hints for ADF components
     */
    private Map<String, Object> buildUiHints(NLPResponse nlpResponse) {
        Map<String, Object> uiHints = new HashMap<>();
        
        // Style hints based on confidence
        if (nlpResponse.getConfidence() >= 0.8) {
            uiHints.put("responseStyle", "high-confidence");
            uiHints.put("backgroundColor", "#E8F5E8");
        } else if (nlpResponse.getConfidence() >= 0.6) {
            uiHints.put("responseStyle", "medium-confidence");
            uiHints.put("backgroundColor", "#FFF8E1");
        } else {
            uiHints.put("responseStyle", "low-confidence");
            uiHints.put("backgroundColor", "#FFF3E0");
        }
        
        // Error styling
        if (!nlpResponse.isSuccess()) {
            uiHints.put("responseStyle", "error");
            uiHints.put("backgroundColor", "#FFEBEE");
        }
        
        // Icon hints
        uiHints.put("icon", getIconForIntent(nlpResponse.getIntent()));
        
        // Action hints
        uiHints.put("suggestedActions", getSuggestedActions(nlpResponse));
        
        return uiHints;
    }
    
    /**
     * Get icon for intent
     */
    private String getIconForIntent(String intent) {
        if (intent.startsWith("CONTRACT_")) {
            return "contract-icon";
        } else if (intent.startsWith("PARTS_")) {
            return "parts-icon";
        } else if (intent.startsWith("HELP_")) {
            return "help-icon";
        } else {
            return "general-icon";
        }
    }
    
    /**
     * Get suggested actions based on response
     */
    private List<String> getSuggestedActions(NLPResponse nlpResponse) {
        List<String> actions = new ArrayList<>();
        
        String intent = nlpResponse.getIntent();
        
        if (intent.startsWith("CONTRACT_")) {
            actions.add("View Contract Details");
            actions.add("Edit Contract");
            actions.add("Show Contract History");
        } else if (intent.startsWith("PARTS_")) {
            actions.add("View Parts List");
            actions.add("Check Inventory");
            actions.add("Order Parts");
        } else if (intent.startsWith("HELP_")) {
            actions.add("Show More Help");
            actions.add("Contact Support");
            actions.add("View Documentation");
        }
        
        return actions;
    }
    
    /**
     * Build error response
     */
    private ADFChatResponse buildErrorResponse(NLPResponse nlpResponse, Exception error) {
        ADFChatResponse errorResponse = new ADFChatResponse();
        
        errorResponse.setOriginalQuery(nlpResponse.getOriginalQuery());
        errorResponse.setIntent("ERROR");
        errorResponse.setConfidence(0.0);
        errorResponse.setSuccess(false);
        errorResponse.setResponseType("ERROR");
        
        String errorMessage = "I encountered an error processing your request. Please try again.";
        if (error != null) {
            errorMessage += "\n\nError details: " + error.getMessage();
        }
        
        errorResponse.setFormattedResponse("⚠️ Error:\n" + errorMessage);
        
        // Error metadata
        Map<String, Object> errorMetadata = new HashMap<>();
        errorMetadata.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        errorMetadata.put("errorType", error != null ? error.getClass().getSimpleName() : "Unknown");
        errorResponse.setMetadata(errorMetadata);
        
        // Error UI hints
        Map<String, Object> errorUiHints = new HashMap<>();
        errorUiHints.put("responseStyle", "error");
        errorUiHints.put("backgroundColor", "#FFEBEE");
        errorUiHints.put("icon", "error-icon");
        errorResponse.setUiHints(errorUiHints);
        
        return errorResponse;
    }
    
    /**
     * Build simple text response
     */
    public ADFChatResponse buildSimpleResponse(String query, String responseText) {
        ADFChatResponse simpleResponse = new ADFChatResponse();
        
        simpleResponse.setOriginalQuery(query);
        simpleResponse.setIntent("GENERAL");
        simpleResponse.setConfidence(1.0);
        simpleResponse.setSuccess(true);
        simpleResponse.setResponseType("GENERAL");
        simpleResponse.setFormattedResponse(responseText);
        
        // Simple metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        metadata.put("responseType", "simple");
        simpleResponse.setMetadata(metadata);
        
        // Simple UI hints
        Map<String, Object> uiHints = new HashMap<>();
        uiHints.put("responseStyle", "simple");
        uiHints.put("backgroundColor", "#F5F5F5");
        uiHints.put("icon", "info-icon");
        simpleResponse.setUiHints(uiHints);
        
        return simpleResponse;
    }
    
    /**
     * Build welcome response
     */
    public ADFChatResponse buildWelcomeResponse() {
        return buildSimpleResponse("", 
            "👋 Welcome to the Contract Portal Assistant!\n\n" +
            "I can help you with:\n" +
            "• Contract information and details\n" +
            "• Parts lookup and inventory\n" +
            "• General guidance and help\n\n" +
            "What would you like to know?");
    }
    
    /**
     * Build help response
     */
    public ADFChatResponse buildHelpResponse() {
        return buildSimpleResponse("help", 
            "❓ Here's how I can help you:\n\n" +
            "📋 Contract Queries:\n" +
            "• \"show contract 123456\"\n" +
            "• \"effective date for contract ABC-789\"\n" +
            "• \"who is the customer for contract XYZ\"\n\n" +
            "🔧 Parts Queries:\n" +
            "• \"parts for contract 123456\"\n" +
            "• \"how many parts in contract ABC-789\"\n" +
            "• \"part P12345 details\"\n\n" +
            "📖 Help Queries:\n" +
            "• \"how to create contract\"\n" +
            "• \"contract creation steps\"\n" +
            "• \"workflow help\"\n\n" +
            "Just ask me anything about contracts or parts!");
    }
}
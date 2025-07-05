package view.practice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Enhanced NLP Controller for Contract Management System
 * 
 * Features:
 * - Multi-model routing (Contract Creation, Contract Query, Parts Query)
 * - Session management for progressive contract creation
 * - Integrated spell checking and validation
 * - JSON response standardization
 * - UI bean integration for seamless user experience
 * 
 * Handles:
 * 1. Contract Creation ("how to create contract", "create contract for account 123")
 * 2. Contract Queries ("show contract 12345", "contracts by vinod")
 * 3. Parts Queries ("specs of AE125", "failed parts in 123456")
 * 4. Help and Instructions
 * 
 * @author Contract Management System
 * @version 2.0
 */
@ManagedBean(name = "nlpController")
@SessionScoped
public class NLPController {
    
    private static final Logger logger = Logger.getLogger(NLPController.class.getName());
    
    // Core processing models
    private ContractCreationModel contractCreationModel;
    private ContractQueryProcessor contractQueryProcessor;
    private PartsQueryProcessor partsQueryProcessor;
    private SpellChecker spellChecker;
    private Helper helper;
    
    // Session state
    private boolean sessionActive = false;
    private String lastQuery = "";
    private String lastResponse = "";
    private Map<String, Object> sessionContext;
    
    // Query routing patterns
    private static final Map<String, String> INTENT_PATTERNS = new HashMap<>();
    static {
        // Contract Creation patterns
        INTENT_PATTERNS.put("CONTRACT_CREATION", "(?i).*(how.*create.*contract|create.*contract|contract.*creation).*");
        
        // Contract Query patterns
        INTENT_PATTERNS.put("CONTRACT_QUERY", "(?i).*(show.*contract|display.*contract|contract.*\\d+|contracts.*by|contract.*status|contract.*effective|contract.*expiration).*");
        
        // Parts Query patterns
        INTENT_PATTERNS.put("PARTS_QUERY", "(?i).*(specs.*of|parts.*\\d+|failed.*parts|part.*error|parts.*status|parts.*staging|parts.*loaded).*");
        
        // Help patterns
        INTENT_PATTERNS.put("HELP", "(?i).*(help|assist|support|guide|instructions).*");
    }
    
    /**
     * Initialize the NLP Controller
     */
    public NLPController() {
        try {
            this.contractCreationModel = new ContractCreationModel();
            this.contractQueryProcessor = new ContractQueryProcessor();
            this.partsQueryProcessor = new PartsQueryProcessor();
            this.spellChecker = new SpellChecker();
            this.helper = new Helper();
            this.sessionContext = new HashMap<>();
            
            logger.info("NLPController initialized with all models");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize NLPController", e);
            throw new RuntimeException("NLPController initialization failed", e);
        }
    }
    
    /**
     * Main entry point for processing user input
     * This is the primary method called by the UI
     */
    public String handleUserInput(String userInput) {
        try {
            if (userInput == null || userInput.trim().isEmpty()) {
                return generateErrorResponse("Please provide a valid input");
            }
            
            // Store query for session tracking
            lastQuery = userInput.trim();
            sessionActive = true;
            
            logger.info("Processing user input: " + userInput);
            
            // Step 1: Perform spell correction
            String correctedInput = spellChecker.performComprehensiveSpellCheck(userInput);
            logger.info("Spell-corrected input: " + correctedInput);
            
            // Step 2: Detect intent and route to appropriate model
            String intent = detectIntent(correctedInput);
            logger.info("Detected intent: " + intent);
            
            // Step 3: Route to appropriate processor
            String response = routeToProcessor(intent, correctedInput);
            
            // Step 4: Store response and return
            lastResponse = response;
            
            return response;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing user input: " + userInput, e);
            return generateErrorResponse("Processing error: " + e.getMessage());
        }
    }
    
    /**
     * Detect the intent from user input
     */
    private String detectIntent(String input) {
        String normalizedInput = input.toLowerCase().trim();
        
        // Check each intent pattern
        for (Map.Entry<String, String> entry : INTENT_PATTERNS.entrySet()) {
            if (normalizedInput.matches(entry.getValue())) {
                return entry.getKey();
            }
        }
        
        // Default to help if no specific intent detected
        return "HELP";
    }
    
    /**
     * Route query to appropriate processor based on intent
     */
    private String routeToProcessor(String intent, String input) {
        try {
            switch (intent) {
                case "CONTRACT_CREATION":
                    return contractCreationModel.processContractQuery(input);
                
                case "CONTRACT_QUERY":
                    return contractQueryProcessor.processQuery(input);
                
                case "PARTS_QUERY":
                    return partsQueryProcessor.processQuery(input);
                
                case "HELP":
                    return generateHelpResponse();
                
                default:
                    return generateUnknownIntentResponse(input);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error routing to processor for intent: " + intent, e);
            return generateErrorResponse("Routing error: " + e.getMessage());
        }
    }
    
    /**
     * Generate help response with available commands
     */
    private String generateHelpResponse() {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", "HELP");
            response.put("message", "Available Commands:");
            
            JSONArray commands = new JSONArray();
            commands.put("Contract Creation:");
            commands.put("  • 'How to create contract' - Get step-by-step instructions");
            commands.put("  • 'Create contract for account 123456' - Start automated creation");
            commands.put("");
            commands.put("Contract Queries:");
            commands.put("  • 'Show contract 12345' - Display specific contract");
            commands.put("  • 'Contracts by vinod' - Find contracts by user");
            commands.put("  • 'Contract status ABC-789' - Check contract status");
            commands.put("");
            commands.put("Parts Queries:");
            commands.put("  • 'Specs of AE125' - Get part specifications");
            commands.put("  • 'Failed parts in 123456' - Show failed parts");
            commands.put("  • 'Parts status loaded' - Check parts by status");
            
            response.put("commands", commands);
            response.put("tip", "I can handle typos and variations in your queries!");
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating help response");
        }
    }
    
    /**
     * Generate response for unknown intents
     */
    private String generateUnknownIntentResponse(String input) {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", "UNKNOWN");
            response.put("message", "I didn't understand that request");
            response.put("input", input);
            response.put("suggestion", "Try saying 'help' to see available commands");
            
            JSONArray suggestions = new JSONArray();
            suggestions.put("How to create contract");
            suggestions.put("Create contract for account [number]");
            suggestions.put("Show contract [number]");
            suggestions.put("Parts specs [part number]");
            
            response.put("suggestions", suggestions);
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating unknown intent response");
        }
    }
    
    /**
     * Generate standardized error response
     */
    private String generateErrorResponse(String errorMessage) {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", "ERROR");
            response.put("message", errorMessage);
            response.put("timestamp", new Date().toString());
            response.put("sessionId", getSessionId());
            
            return response.toString();
        } catch (Exception e) {
            return "{\"responseType\":\"ERROR\",\"message\":\"System error occurred\"}";
        }
    }
    
    /**
     * Get current session ID
     */
    private String getSessionId() {
        return "session_" + System.currentTimeMillis();
    }
    
    /**
     * Reset session state (for new conversations)
     */
    public void resetSession() {
        try {
            sessionActive = false;
            lastQuery = "";
            lastResponse = "";
            sessionContext.clear();
            
            // Reset model sessions
            if (contractCreationModel != null) {
                contractCreationModel.resetCreationSession();
            }
            
            logger.info("Session reset completed");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error resetting session", e);
        }
    }
    
    /**
     * Get session information for debugging
     */
    public Map<String, Object> getSessionInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionActive", sessionActive);
        info.put("lastQuery", lastQuery);
        info.put("lastResponse", lastResponse);
        info.put("sessionContext", new HashMap<>(sessionContext));
        
        // Add contract creation session state if available
        if (contractCreationModel != null) {
            info.put("contractCreationState", contractCreationModel.getSessionState());
        }
        
        return info;
    }
    
    /**
     * Process follow-up queries in the same session
     */
    public String processFollowUp(String followUpInput) {
        try {
            if (!sessionActive) {
                return handleUserInput(followUpInput);
            }
            
            // Context-aware processing for follow-ups
            String contextualInput = enhanceWithContext(followUpInput);
            return handleUserInput(contextualInput);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing follow-up", e);
            return generateErrorResponse("Follow-up processing error");
        }
    }
    
    /**
     * Enhance input with session context
     */
    private String enhanceWithContext(String input) {
        // Add context from previous queries if needed
        if (lastQuery.contains("create contract") && 
            !input.contains("create") && 
            !input.contains("contract")) {
            // This might be data for contract creation
            return input; // Pass through as-is for data collection
        }
        
        return input;
    }
    
    /**
     * Validate input before processing
     */
    public boolean validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        // Check for suspicious patterns
        if (input.length() > 1000) {
            return false;
        }
        
        // Check for script injection attempts
        if (input.matches(".*<script.*|.*javascript.*|.*eval\\(.*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get response statistics
     */
    public Map<String, Object> getResponseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get stats from each processor
            if (contractQueryProcessor != null) {
                stats.put("contractQueryCount", "Available");
            }
            
            if (partsQueryProcessor != null) {
                stats.put("partsQueryCount", "Available");
            }
            
            if (contractCreationModel != null) {
                stats.put("contractCreationSession", contractCreationModel.getSessionState());
            }
            
            stats.put("sessionActive", sessionActive);
            stats.put("totalQueries", sessionContext.size());
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error getting response stats", e);
        }
        
        return stats;
    }
    
    // Getters and setters for JSF binding
    public String getLastQuery() {
        return lastQuery;
    }
    
    public void setLastQuery(String lastQuery) {
        this.lastQuery = lastQuery;
    }
    
    public String getLastResponse() {
        return lastResponse;
    }
    
    public void setLastResponse(String lastResponse) {
        this.lastResponse = lastResponse;
    }
    
    public boolean isSessionActive() {
        return sessionActive;
    }
    
    public void setSessionActive(boolean sessionActive) {
        this.sessionActive = sessionActive;
    }
    
    /**
     * JSF action method for form submission
     */
    public String submitQuery() {
        try {
            if (lastQuery != null && !lastQuery.trim().isEmpty()) {
                String response = handleUserInput(lastQuery);
                lastResponse = response;
                return "success";
            }
            return "error";
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in submitQuery", e);
            return "error";
        }
    }
    
    /**
     * JSF action method for session reset
     */
    public String resetUserSession() {
        resetSession();
        return "reset";
    }
}
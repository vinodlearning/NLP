package com.adf.nlp.core;

import com.adf.nlp.config.ConfigurationManager;
import com.adf.nlp.models.ContractModel;
import com.adf.nlp.models.PartsModel;
import com.adf.nlp.models.HelpModel;
import com.adf.nlp.utils.SpellChecker;
import com.adf.nlp.utils.InputAnalyzer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Enhanced NLP Controller with Configuration-Driven Architecture
 * All routing logic, keywords, and responses are externalized in txt files
 * Optimized for O(w) time complexity where w = word count
 */
@ManagedBean(name = "enhancedNLPController")
@SessionScoped
public class EnhancedNLPController {
    private static final Logger logger = Logger.getLogger(EnhancedNLPController.class.getName());
    
    // Configuration manager for all settings
    private final ConfigurationManager configManager;
    
    // Model instances
    private final ContractModel contractModel;
    private final PartsModel partsModel;
    private final HelpModel helpModel;
    
    // Utility components
    private final SpellChecker spellChecker;
    private final InputAnalyzer inputAnalyzer;
    
    // Session tracking
    private String sessionId;
    private List<String> queryHistory;
    private long lastQueryTime;
    
    /**
     * Constructor - Initialize all components
     */
    public EnhancedNLPController() {
        this.configManager = ConfigurationManager.getInstance();
        this.contractModel = new ContractModel();
        this.partsModel = new PartsModel();
        this.helpModel = new HelpModel();
        this.spellChecker = new SpellChecker(configManager);
        this.inputAnalyzer = new InputAnalyzer(configManager);
        
        this.sessionId = UUID.randomUUID().toString();
        this.queryHistory = new ArrayList<>();
        this.lastQueryTime = System.currentTimeMillis();
        
        logger.info("Enhanced NLP Controller initialized with session: " + sessionId);
    }
    
    /**
     * Main processing method - Entry point for all NLP queries
     * Implements the business routing logic as specified
     */
    public String processQuery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return generateErrorResponse("error_invalid_input", null);
        }
        
        long startTime = System.nanoTime();
        
        try {
            // Track query in history
            queryHistory.add(userInput);
            lastQueryTime = System.currentTimeMillis();
            
            // Step 1: Apply spell correction
            String correctedInput = spellChecker.correctSpelling(userInput);
            boolean spellCorrectionApplied = !userInput.equals(correctedInput);
            
            // Step 2: Analyze input for routing decision
            InputAnalyzer.AnalysisResult analysis = inputAnalyzer.analyzeInput(correctedInput);
            
            // Step 3: Route to appropriate model based on business rules
            String routingDecision = determineRouting(analysis);
            String response = routeToModel(routingDecision, correctedInput, analysis);
            
            // Step 4: Add spell correction notification if applied
            if (spellCorrectionApplied) {
                Map<String, Object> params = new HashMap<>();
                params.put("original", userInput);
                params.put("corrected", correctedInput);
                String spellNotification = configManager.formatResponseTemplate("spell_correction_applied", params);
                response = spellNotification + "\n\n" + response;
            }
            
            // Step 5: Log performance
            long endTime = System.nanoTime();
            long processingTime = (endTime - startTime) / 1000; // microseconds
            
            logger.info(String.format("Query processed - Session: %s, Route: %s, Time: %d μs", 
                       sessionId, routingDecision, processingTime));
            
            return response;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing query: " + userInput, e);
            return generateErrorResponse("error_system_busy", null);
        }
    }
    
    /**
     * Determine routing based on business rules
     * Priority: Parts+Create > Parts > Create > Default(Contract)
     */
    private String determineRouting(InputAnalyzer.AnalysisResult analysis) {
        boolean hasPartsKeywords = analysis.hasPartsKeywords();
        boolean hasCreateKeywords = analysis.hasCreateKeywords();
        
        // Rule 1: Parts + Create keywords = Parts Creation Error
        if (hasPartsKeywords && hasCreateKeywords) {
            return "PARTS_CREATE_ERROR";
        }
        
        // Rule 2: Parts keywords only = Parts Model
        if (hasPartsKeywords) {
            return "PARTS_MODEL";
        }
        
        // Rule 3: Create keywords only = Help Model
        if (hasCreateKeywords) {
            return "HELP_MODEL";
        }
        
        // Rule 4: Default = Contract Model
        return "CONTRACT_MODEL";
    }
    
    /**
     * Route to appropriate model
     */
    private String routeToModel(String routingDecision, String input, InputAnalyzer.AnalysisResult analysis) {
        switch (routingDecision) {
            case "PARTS_CREATE_ERROR":
                return handlePartsCreateError(input, analysis);
                
            case "PARTS_MODEL":
                return partsModel.processQuery(input, analysis);
                
            case "HELP_MODEL":
                return helpModel.processQuery(input, analysis);
                
            case "CONTRACT_MODEL":
                return contractModel.processQuery(input, analysis);
                
            default:
                return generateErrorResponse("error_invalid_input", null);
        }
    }
    
    /**
     * Handle parts creation error - explain limitation
     */
    private String handlePartsCreateError(String input, InputAnalyzer.AnalysisResult analysis) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_input", input);
        
        String errorMessage = "I understand you want to create parts, but our system only supports " +
                             "creating CONTRACTS. Parts are loaded from Excel files and cannot be created manually.\n\n" +
                             "If you need to:\n" +
                             "• View existing parts: Try 'show parts for contract [number]'\n" +
                             "• Create a new contract: Try 'help me create contract'\n" +
                             "• Get contract information: Try 'show contract [number]'";
        
        return errorMessage;
    }
    
    /**
     * Generate error response using templates
     */
    private String generateErrorResponse(String errorType, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        return configManager.formatResponseTemplate(errorType, params);
    }
    
    /**
     * Get query history for session
     */
    public List<String> getQueryHistory() {
        return new ArrayList<>(queryHistory);
    }
    
    /**
     * Get session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Get last query time
     */
    public long getLastQueryTime() {
        return lastQueryTime;
    }
    
    /**
     * Clear query history
     */
    public void clearHistory() {
        queryHistory.clear();
        logger.info("Query history cleared for session: " + sessionId);
    }
    
    /**
     * Get session statistics
     */
    public Map<String, Object> getSessionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("sessionId", sessionId);
        stats.put("queryCount", queryHistory.size());
        stats.put("lastQueryTime", lastQueryTime);
        stats.put("configurationLoaded", configManager.isConfigurationLoaded());
        
        return stats;
    }
    
    /**
     * Test routing logic with sample input
     */
    public String testRouting(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Invalid input for testing";
        }
        
        try {
            String correctedInput = spellChecker.correctSpelling(input);
            InputAnalyzer.AnalysisResult analysis = inputAnalyzer.analyzeInput(correctedInput);
            String routingDecision = determineRouting(analysis);
            
            StringBuilder result = new StringBuilder();
            result.append("Routing Test Results:\n");
            result.append("Original Input: ").append(input).append("\n");
            result.append("Corrected Input: ").append(correctedInput).append("\n");
            result.append("Has Parts Keywords: ").append(analysis.hasPartsKeywords()).append("\n");
            result.append("Has Create Keywords: ").append(analysis.hasCreateKeywords()).append("\n");
            result.append("Routing Decision: ").append(routingDecision).append("\n");
            result.append("Detected Keywords: ").append(analysis.getDetectedKeywords()).append("\n");
            
            return result.toString();
            
        } catch (Exception e) {
            return "Error testing routing: " + e.getMessage();
        }
    }
    
    /**
     * Reload configuration
     */
    public String reloadConfiguration() {
        try {
            configManager.reloadConfiguration();
            return "Configuration reloaded successfully";
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reloading configuration", e);
            return "Error reloading configuration: " + e.getMessage();
        }
    }
    
    /**
     * Get configuration summary
     */
    public String getConfigurationSummary() {
        return configManager.getConfigurationSummary();
    }
}
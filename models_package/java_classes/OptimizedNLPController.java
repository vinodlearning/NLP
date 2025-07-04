package view.practice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Optimized NLP Controller for Contract Management System
 * 
 * Routing Logic:
 * 1. If input contains Parts/Part/Line/Lines keywords → Route to PartsModel
 * 2. If no parts keywords AND contains "create" → Route to HelpModel
 * 3. Else → Route to ContractModel
 * 
 * Architecture Flow:
 * UI Screen → Managed Bean → NLPController → [Helper, ContractModel, PartsModel] → JSON Response
 * 
 * Time Complexity: O(w) where w is number of words in input
 * Space Complexity: O(1) additional space for processing
 * 
 * @author Contract Management System
 * @version 3.0
 */
@ManagedBean(name = "optimizedNLPController")
@SessionScoped
public class OptimizedNLPController {
    
    private static final Logger logger = Logger.getLogger(OptimizedNLPController.class.getName());
    
    // Configuration loader for keyword and spell correction management
    private final ConfigurationLoader configLoader;
    
    // Model instances - lazy initialization for memory efficiency
    private ContractModel contractModel;
    private PartsModel partsModel;
    private HelpModel helpModel;
    private Helper helper;
    
    // Session state for optimization
    private String lastQuery = "";
    private String lastResponse = "";
    private long lastProcessingTime = 0;
    private Map<String, Object> sessionContext;
    
    // Performance metrics
    private int totalQueries = 0;
    private long totalProcessingTime = 0;
    
    /**
     * Initialize the optimized NLP Controller
     * Time Complexity: O(n) where n is configuration items (one-time cost)
     */
    public OptimizedNLPController() {
        try {
            // Load configurations once at startup
            this.configLoader = ConfigurationLoader.getInstance();
            this.sessionContext = new HashMap<>();
            
            // Initialize helper immediately as it's used for validation
            this.helper = new Helper();
            
            logger.info("OptimizedNLPController initialized successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize OptimizedNLPController", e);
            throw new RuntimeException("Controller initialization failed", e);
        }
    }
    
    /**
     * Main processing method - handles all user input with optimized routing
     * 
     * Time Complexity: O(w) where w is number of words in input
     * Space Complexity: O(1) additional space
     */
    public String processUserInput(String userInput) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Input validation - O(1)
            if (userInput == null || userInput.trim().isEmpty()) {
                return generateErrorResponse("Please provide a valid input", "EMPTY_INPUT");
            }
            
            // Update metrics
            totalQueries++;
            lastQuery = userInput.trim();
            
            logger.info("Processing query #" + totalQueries + ": " + userInput);
            
            // Step 1: Spell correction - O(w) where w is number of words
            String correctedInput = configLoader.performSpellCorrection(userInput);
            
            // Step 2: Optimized routing decision - O(w) where w is number of words
            RouteDecision decision = determineRoute(correctedInput);
            
            // Step 3: Route to appropriate model - O(1) model selection
            String response = routeToModel(decision, correctedInput);
            
            // Step 4: Update session state and metrics
            lastResponse = response;
            lastProcessingTime = System.currentTimeMillis() - startTime;
            totalProcessingTime += lastProcessingTime;
            
            logger.info("Query processed in " + lastProcessingTime + "ms, routed to: " + decision.getTargetModel());
            
            return response;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing user input: " + userInput, e);
            return generateErrorResponse("Processing error: " + e.getMessage(), "PROCESSING_ERROR");
        }
    }
    
    /**
     * Optimized route determination following exact business logic
     * 
     * Logic:
     * 1. Check for Parts/Part/Line/Lines keywords → PartsModel
     * 2. If no parts keywords AND contains "create" → HelpModel  
     * 3. Else → ContractModel
     * 
     * Time Complexity: O(w) where w is number of words
     * Space Complexity: O(1)
     */
    private RouteDecision determineRoute(String input) {
        
        // Step 1: Check for parts keywords - O(w) operation
        boolean hasPartsKeywords = configLoader.containsPartsKeywords(input);
        
        if (hasPartsKeywords) {
            // Route to PartsModel - highest priority
            return new RouteDecision("PartsModel", "PARTS_QUERY", 
                "Input contains parts/line keywords - routing to PartsModel");
        }
        
        // Step 2: Check for create keywords - O(w) operation  
        boolean hasCreateKeywords = configLoader.containsCreateKeywords(input);
        
        if (hasCreateKeywords) {
            // Route to HelpModel - second priority
            return new RouteDecision("HelpModel", "HELP_REQUEST", 
                "Input contains create keywords - routing to HelpModel");
        }
        
        // Step 3: Default to ContractModel - O(1) operation
        return new RouteDecision("ContractModel", "CONTRACT_QUERY", 
            "Default routing to ContractModel");
    }
    
    /**
     * Route request to appropriate model with lazy initialization
     * 
     * Time Complexity: O(1) for routing + O(model processing)
     * Space Complexity: O(1) for model instances
     */
    private String routeToModel(RouteDecision decision, String input) {
        
        switch (decision.getTargetModel()) {
            case "PartsModel":
                return routeToPartsModel(input, decision);
                
            case "HelpModel":
                return routeToHelpModel(input, decision);
                
            case "ContractModel":
                return routeToContractModel(input, decision);
                
            default:
                return generateErrorResponse("Invalid routing decision: " + decision.getTargetModel(), "ROUTING_ERROR");
        }
    }
    
    /**
     * Route to Parts Model with lazy initialization
     * Time Complexity: O(1) + O(PartsModel processing)
     */
    private String routeToPartsModel(String input, RouteDecision decision) {
        try {
            // Lazy initialization for memory efficiency
            if (partsModel == null) {
                partsModel = new PartsModel();
            }
            
            // Process the query
            String response = partsModel.processPartsQuery(input);
            
            // Add routing metadata to response
            return addRoutingMetadata(response, decision);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in PartsModel processing", e);
            return generateErrorResponse("PartsModel processing error: " + e.getMessage(), "PARTS_MODEL_ERROR");
        }
    }
    
    /**
     * Route to Help Model with lazy initialization
     * Time Complexity: O(1) + O(HelpModel processing)
     */
    private String routeToHelpModel(String input, RouteDecision decision) {
        try {
            // Lazy initialization for memory efficiency
            if (helpModel == null) {
                helpModel = new HelpModel();
            }
            
            // Process the help request
            String response = helpModel.processHelpRequest(input);
            
            // Add routing metadata to response
            return addRoutingMetadata(response, decision);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in HelpModel processing", e);
            return generateErrorResponse("HelpModel processing error: " + e.getMessage(), "HELP_MODEL_ERROR");
        }
    }
    
    /**
     * Route to Contract Model with lazy initialization
     * Time Complexity: O(1) + O(ContractModel processing)
     */
    private String routeToContractModel(String input, RouteDecision decision) {
        try {
            // Lazy initialization for memory efficiency
            if (contractModel == null) {
                contractModel = new ContractModel();
            }
            
            // Process the contract query
            String response = contractModel.processContractQuery(input);
            
            // Add routing metadata to response
            return addRoutingMetadata(response, decision);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in ContractModel processing", e);
            return generateErrorResponse("ContractModel processing error: " + e.getMessage(), "CONTRACT_MODEL_ERROR");
        }
    }
    
    /**
     * Add routing metadata to model response
     * Time Complexity: O(1)
     */
    private String addRoutingMetadata(String modelResponse, RouteDecision decision) {
        try {
            // Parse existing response if it's JSON format
            if (modelResponse.trim().startsWith("{")) {
                // Insert routing metadata into existing JSON
                int lastBrace = modelResponse.lastIndexOf("}");
                if (lastBrace > 0) {
                    StringBuilder enhanced = new StringBuilder();
                    enhanced.append(modelResponse, 0, lastBrace);
                    enhanced.append(",\n  \"routingInfo\": {\n");
                    enhanced.append("    \"targetModel\": \"").append(decision.getTargetModel()).append("\",\n");
                    enhanced.append("    \"routingReason\": \"").append(decision.getReason()).append("\",\n");
                    enhanced.append("    \"processingTime\": ").append(lastProcessingTime).append("\n");
                    enhanced.append("  }\n}");
                    return enhanced.toString();
                }
            }
            
            // If not JSON, wrap with routing info
            return "{\n" +
                   "  \"response\": " + modelResponse + ",\n" +
                   "  \"routingInfo\": {\n" +
                   "    \"targetModel\": \"" + decision.getTargetModel() + "\",\n" +
                   "    \"routingReason\": \"" + decision.getReason() + "\",\n" +
                   "    \"processingTime\": " + lastProcessingTime + "\n" +
                   "  }\n" +
                   "}";
                   
        } catch (Exception e) {
            // If metadata addition fails, return original response
            logger.log(Level.WARNING, "Failed to add routing metadata", e);
            return modelResponse;
        }
    }
    
    /**
     * Generate standardized error response
     * Time Complexity: O(1)
     */
    private String generateErrorResponse(String errorMessage, String errorCode) {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"" + errorMessage + "\",\n" +
               "  \"errorCode\": \"" + errorCode + "\",\n" +
               "  \"timestamp\": \"" + new Date().toString() + "\",\n" +
               "  \"processingTime\": " + lastProcessingTime + "\n" +
               "}";
    }
    
    /**
     * Get performance metrics
     * Time Complexity: O(1)
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalQueries", totalQueries);
        metrics.put("totalProcessingTime", totalProcessingTime);
        metrics.put("averageProcessingTime", totalQueries > 0 ? totalProcessingTime / totalQueries : 0);
        metrics.put("lastProcessingTime", lastProcessingTime);
        metrics.put("configurationStats", configLoader.getConfigurationStats());
        return metrics;
    }
    
    /**
     * Reset session state
     * Time Complexity: O(1)
     */
    public void resetSession() {
        sessionContext.clear();
        lastQuery = "";
        lastResponse = "";
        lastProcessingTime = 0;
        logger.info("Session reset completed");
    }
    
    /**
     * Reload configurations from files
     * Time Complexity: O(n) where n is configuration items
     */
    public void reloadConfigurations() {
        configLoader.reloadConfigurations();
        logger.info("Configurations reloaded");
    }
    
    // Getters for JSF binding
    public String getLastQuery() { return lastQuery; }
    public String getLastResponse() { return lastResponse; }
    public long getLastProcessingTime() { return lastProcessingTime; }
    public int getTotalQueries() { return totalQueries; }
    
    /**
     * Route Decision inner class for encapsulating routing logic
     */
    private static class RouteDecision {
        private final String targetModel;
        private final String intentType;
        private final String reason;
        
        public RouteDecision(String targetModel, String intentType, String reason) {
            this.targetModel = targetModel;
            this.intentType = intentType;
            this.reason = reason;
        }
        
        public String getTargetModel() { return targetModel; }
        public String getIntentType() { return intentType; }
        public String getReason() { return reason; }
    }
    
    /**
     * Simplified model classes for demonstration
     * These would be replaced with actual model implementations
     */
    
    private static class PartsModel {
        public String processPartsQuery(String input) {
            return "{\n" +
                   "  \"responseType\": \"PARTS_RESULT\",\n" +
                   "  \"message\": \"Parts query processed\",\n" +
                   "  \"query\": \"" + input + "\",\n" +
                   "  \"results\": []\n" +
                   "}";
        }
    }
    
    private static class HelpModel {
        public String processHelpRequest(String input) {
            return "{\n" +
                   "  \"responseType\": \"HELP_RESPONSE\",\n" +
                   "  \"message\": \"Help information provided\",\n" +
                   "  \"query\": \"" + input + "\",\n" +
                   "  \"helpContent\": \"Instructions for creating contracts\"\n" +
                   "}";
        }
    }
    
    private static class ContractModel {
        public String processContractQuery(String input) {
            return "{\n" +
                   "  \"responseType\": \"CONTRACT_RESULT\",\n" +
                   "  \"message\": \"Contract query processed\",\n" +
                   "  \"query\": \"" + input + "\",\n" +
                   "  \"results\": []\n" +
                   "}";
        }
    }
}
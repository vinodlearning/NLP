package view.practice;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simplified Optimized NLP Controller for testing (without JSF dependencies)
 */
public class SimpleOptimizedNLPController {
    
    private static final Logger logger = Logger.getLogger(SimpleOptimizedNLPController.class.getName());
    
    private final ConfigurationLoader configLoader;
    private ContractModel contractModel;
    private PartsModel partsModel;
    private HelpModel helpModel;
    private Helper helper;
    
    private String lastQuery = "";
    private String lastResponse = "";
    private long lastProcessingTime = 0;
    private Map<String, Object> sessionContext;
    
    private int totalQueries = 0;
    private long totalProcessingTime = 0;
    
    public SimpleOptimizedNLPController() {
        try {
            this.configLoader = ConfigurationLoader.getInstance();
            this.sessionContext = new HashMap<>();
            this.helper = new Helper();
            
            logger.info("SimpleOptimizedNLPController initialized successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize SimpleOptimizedNLPController", e);
            throw new RuntimeException("Controller initialization failed", e);
        }
    }
    
    public String processUserInput(String userInput) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (userInput == null || userInput.trim().isEmpty()) {
                return generateErrorResponse("Please provide a valid input", "EMPTY_INPUT");
            }
            
            totalQueries++;
            lastQuery = userInput.trim();
            
            String correctedInput = configLoader.performSpellCorrection(userInput);
            RouteDecision decision = determineRoute(correctedInput);
            String response = routeToModel(decision, correctedInput);
            
            lastResponse = response;
            lastProcessingTime = System.currentTimeMillis() - startTime;
            totalProcessingTime += lastProcessingTime;
            
            return response;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing user input: " + userInput, e);
            return generateErrorResponse("Processing error: " + e.getMessage(), "PROCESSING_ERROR");
        }
    }
    
    private RouteDecision determineRoute(String input) {
        boolean hasPartsKeywords = configLoader.containsPartsKeywords(input);
        
        if (hasPartsKeywords) {
            return new RouteDecision("PartsModel", "PARTS_QUERY", 
                "Input contains parts/line keywords - routing to PartsModel");
        }
        
        boolean hasCreateKeywords = configLoader.containsCreateKeywords(input);
        
        if (hasCreateKeywords) {
            return new RouteDecision("HelpModel", "HELP_REQUEST", 
                "Input contains create keywords - routing to HelpModel");
        }
        
        return new RouteDecision("ContractModel", "CONTRACT_QUERY", 
            "Default routing to ContractModel");
    }
    
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
    
    private String routeToPartsModel(String input, RouteDecision decision) {
        try {
            if (partsModel == null) {
                partsModel = new PartsModel();
            }
            
            String response = partsModel.processPartsQuery(input);
            return addRoutingMetadata(response, decision);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in PartsModel processing", e);
            return generateErrorResponse("PartsModel processing error: " + e.getMessage(), "PARTS_MODEL_ERROR");
        }
    }
    
    private String routeToHelpModel(String input, RouteDecision decision) {
        try {
            if (helpModel == null) {
                helpModel = new HelpModel();
            }
            
            String response = helpModel.processHelpRequest(input);
            return addRoutingMetadata(response, decision);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in HelpModel processing", e);
            return generateErrorResponse("HelpModel processing error: " + e.getMessage(), "HELP_MODEL_ERROR");
        }
    }
    
    private String routeToContractModel(String input, RouteDecision decision) {
        try {
            if (contractModel == null) {
                contractModel = new ContractModel();
            }
            
            String response = contractModel.processContractQuery(input);
            return addRoutingMetadata(response, decision);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in ContractModel processing", e);
            return generateErrorResponse("ContractModel processing error: " + e.getMessage(), "CONTRACT_MODEL_ERROR");
        }
    }
    
    private String addRoutingMetadata(String modelResponse, RouteDecision decision) {
        try {
            if (modelResponse.trim().startsWith("{")) {
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
            
            return "{\n" +
                   "  \"response\": " + modelResponse + ",\n" +
                   "  \"routingInfo\": {\n" +
                   "    \"targetModel\": \"" + decision.getTargetModel() + "\",\n" +
                   "    \"routingReason\": \"" + decision.getReason() + "\",\n" +
                   "    \"processingTime\": " + lastProcessingTime + "\n" +
                   "  }\n" +
                   "}";
                   
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to add routing metadata", e);
            return modelResponse;
        }
    }
    
    private String generateErrorResponse(String errorMessage, String errorCode) {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"" + errorMessage + "\",\n" +
               "  \"errorCode\": \"" + errorCode + "\",\n" +
               "  \"timestamp\": \"" + new Date().toString() + "\",\n" +
               "  \"processingTime\": " + lastProcessingTime + "\n" +
               "}";
    }
    
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalQueries", totalQueries);
        metrics.put("totalProcessingTime", totalProcessingTime);
        metrics.put("averageProcessingTime", totalQueries > 0 ? totalProcessingTime / totalQueries : 0);
        metrics.put("lastProcessingTime", lastProcessingTime);
        metrics.put("configurationStats", configLoader.getConfigurationStats());
        return metrics;
    }
    
    public void resetSession() {
        sessionContext.clear();
        lastQuery = "";
        lastResponse = "";
        lastProcessingTime = 0;
    }
    
    public void reloadConfigurations() {
        configLoader.reloadConfigurations();
    }
    
    // Getters
    public String getLastQuery() { return lastQuery; }
    public String getLastResponse() { return lastResponse; }
    public long getLastProcessingTime() { return lastProcessingTime; }
    public int getTotalQueries() { return totalQueries; }
    
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
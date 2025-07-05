package MachineLearning.integration;

import MachineLearning.models.MLModelController;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

/**
 * ML Integration Manager for Contract Portal Applications
 * 
 * This class provides a simple interface for integrating the ML system
 * into any Contract Portal application. It handles:
 * - Model initialization and loading
 * - Simple query processing interface
 * - Error handling and fallback responses
 * - Performance monitoring
 * 
 * Usage in your existing bean:
 * 
 * @ManagedBean
 * public class YourExistingBean {
 *     private MLIntegrationManager mlManager;
 *     
 *     @PostConstruct
 *     public void init() {
 *         mlManager = new MLIntegrationManager();
 *         mlManager.initialize();
 *     }
 *     
 *     public void processUserQuery() {
 *         String response = mlManager.processQuery(userInput);
 *         // Handle response...
 *     }
 * }
 */
public class MLIntegrationManager {
    
    private MLModelController mlController;
    private boolean isInitialized = false;
    private Map<String, Long> performanceMetrics;
    private long totalQueries = 0;
    private long totalProcessingTime = 0;
    
    /**
     * Initialize the ML Integration Manager
     */
    public MLIntegrationManager() {
        this.performanceMetrics = new HashMap<>();
        System.out.println("MLIntegrationManager created - call initialize() to start");
    }
    
    /**
     * Initialize ML models - call this in your @PostConstruct method
     */
    @PostConstruct
    public void initialize() {
        try {
            System.out.println("Initializing ML Integration Manager...");
            
            // Initialize ML Controller
            mlController = new MLModelController();
            
            // Try to load pre-trained models
            try {
                mlController.loadPreTrainedModels();
                System.out.println("✅ Pre-trained models loaded successfully");
            } catch (IOException e) {
                System.out.println("⚠️  Pre-trained models not found, using default models");
                // Models will work with default/fallback responses
            }
            
            isInitialized = true;
            System.out.println("✅ ML Integration Manager initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing ML Integration Manager: " + e.getMessage());
            isInitialized = false;
        }
    }
    
    /**
     * Main method to process user queries - THIS IS THE METHOD TO CALL
     * 
     * @param userInput The user's natural language query
     * @return JSON response string
     */
    public String processQuery(String userInput) {
        if (!isInitialized) {
            return generateFallbackResponse("ML system not initialized", userInput);
        }
        
        if (userInput == null || userInput.trim().isEmpty()) {
            return generateFallbackResponse("Empty input provided", userInput);
        }
        
        try {
            long startTime = System.nanoTime();
            
            // Process with ML controller
            String response = mlController.processUserInput(userInput);
            
            // Update performance metrics
            long processingTime = (System.nanoTime() - startTime) / 1000;
            updatePerformanceMetrics(processingTime);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error processing query: " + e.getMessage());
            return generateFallbackResponse("Error processing query: " + e.getMessage(), userInput);
        }
    }
    
    /**
     * Simple method to check if input is parts-related
     */
    public boolean isPartsQuery(String userInput) {
        if (userInput == null) return false;
        String lower = userInput.toLowerCase();
        return lower.contains("parts") || lower.contains("part") || 
               lower.contains("components") || lower.contains("inventory");
    }
    
    /**
     * Simple method to check if input is help-related
     */
    public boolean isHelpQuery(String userInput) {
        if (userInput == null) return false;
        String lower = userInput.toLowerCase();
        return lower.contains("help") || lower.contains("how") || 
               lower.contains("create") || lower.contains("guide");
    }
    
    /**
     * Simple method to check if input is contract-related
     */
    public boolean isContractQuery(String userInput) {
        if (userInput == null) return false;
        String lower = userInput.toLowerCase();
        return lower.contains("contract") || lower.contains("agreement") || 
               lower.matches(".*\\d{6}.*"); // Contains 6-digit contract ID
    }
    
    /**
     * Get system status information
     */
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isInitialized", isInitialized);
        status.put("totalQueries", totalQueries);
        status.put("averageProcessingTime", getAverageProcessingTime());
        status.put("version", "1.0.0");
        status.put("framework", "Apache OpenNLP");
        
        if (isInitialized && mlController != null) {
            status.put("modelInfo", mlController.getSystemInfo());
        }
        
        return status;
    }
    
    /**
     * Get performance metrics
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalQueries", totalQueries);
        metrics.put("totalProcessingTimeMs", totalProcessingTime / 1000.0);
        metrics.put("averageProcessingTimeMs", getAverageProcessingTime());
        metrics.put("queriesPerformanceBreakdown", performanceMetrics);
        return metrics;
    }
    
    /**
     * Reset performance metrics
     */
    public void resetMetrics() {
        totalQueries = 0;
        totalProcessingTime = 0;
        performanceMetrics.clear();
        System.out.println("Performance metrics reset");
    }
    
    /**
     * Train models (if needed)
     */
    public void trainModels() throws IOException {
        if (!isInitialized) {
            throw new IllegalStateException("Manager not initialized");
        }
        
        System.out.println("Training ML models...");
        mlController.trainAllModels();
        mlController.exportAllModels();
        System.out.println("Models trained and exported successfully");
    }
    
    /**
     * Update performance metrics
     */
    private void updatePerformanceMetrics(long processingTimeMicros) {
        totalQueries++;
        totalProcessingTime += processingTimeMicros;
        
        // Track performance by processing time ranges
        String range;
        double timeMs = processingTimeMicros / 1000.0;
        
        if (timeMs < 1.0) range = "< 1ms";
        else if (timeMs < 5.0) range = "1-5ms";
        else if (timeMs < 10.0) range = "5-10ms";
        else if (timeMs < 50.0) range = "10-50ms";
        else range = "> 50ms";
        
        performanceMetrics.put(range, performanceMetrics.getOrDefault(range, 0L) + 1);
    }
    
    /**
     * Get average processing time in milliseconds
     */
    private double getAverageProcessingTime() {
        if (totalQueries == 0) return 0.0;
        return (totalProcessingTime / 1000.0) / totalQueries;
    }
    
    /**
     * Generate fallback response when ML system is not available
     */
    private String generateFallbackResponse(String error, String userInput) {
        return "{\n" +
               "  \"responseType\": \"FALLBACK_RESPONSE\",\n" +
               "  \"message\": \"ML system temporarily unavailable\",\n" +
               "  \"error\": \"" + error + "\",\n" +
               "  \"originalInput\": \"" + (userInput != null ? userInput : "N/A") + "\",\n" +
               "  \"suggestion\": \"Please try again later or contact support\",\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    /**
     * Utility method to extract response type from JSON response
     */
    public String getResponseType(String jsonResponse) {
        if (jsonResponse == null) return "UNKNOWN";
        
        if (jsonResponse.contains("\"responseType\": \"")) {
            int start = jsonResponse.indexOf("\"responseType\": \"") + 17;
            int end = jsonResponse.indexOf("\"", start);
            if (end > start) {
                return jsonResponse.substring(start, end);
            }
        }
        return "UNKNOWN";
    }
    
    /**
     * Utility method to extract message from JSON response
     */
    public String getMessage(String jsonResponse) {
        if (jsonResponse == null) return "No response";
        
        if (jsonResponse.contains("\"message\": \"")) {
            int start = jsonResponse.indexOf("\"message\": \"") + 12;
            int end = jsonResponse.indexOf("\"", start);
            if (end > start) {
                return jsonResponse.substring(start, end);
            }
        }
        return "Message not found";
    }
    
    /**
     * Check if the system is healthy and responding
     */
    public boolean isHealthy() {
        if (!isInitialized) return false;
        
        try {
            // Test with a simple query
            String testResponse = processQuery("test system health");
            return testResponse != null && !testResponse.contains("FALLBACK_RESPONSE");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Reinitialize the system (useful for recovery)
     */
    public void reinitialize() {
        isInitialized = false;
        mlController = null;
        initialize();
    }
}
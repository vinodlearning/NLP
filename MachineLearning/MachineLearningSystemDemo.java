import MachineLearning.models.MLModelController;
import MachineLearning.integration.MLIntegrationManager;
import MachineLearning.training.ModelTrainer;

/**
 * Complete Machine Learning System Demo
 * 
 * This demo shows how to:
 * 1. Train all ML models
 * 2. Use the MLIntegrationManager (recommended)
 * 3. Use the MLModelController directly
 * 4. Test various query types
 * 5. Monitor performance
 */
public class MachineLearningSystemDemo {
    
    public static void main(String[] args) {
        System.out.println("🤖 Contract Portal ML System - Complete Demo");
        System.out.println("============================================\n");
        
        try {
            // Step 1: Train models (optional - only if you want fresh models)
            boolean trainModels = false; // Set to true to train fresh models
            if (trainModels) {
                trainAndExportModels();
            }
            
            // Step 2: Demonstrate ML Integration Manager (RECOMMENDED)
            demonstrateMLIntegrationManager();
            
            // Step 3: Demonstrate direct controller usage
            demonstrateDirectController();
            
            // Step 4: Performance testing
            performanceTest();
            
            System.out.println("\n🎉 Demo completed successfully!");
            System.out.println("The ML system is ready for integration into your Contract Portal!");
            
        } catch (Exception e) {
            System.err.println("❌ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Step 1: Train and export all models
     */
    private static void trainAndExportModels() throws Exception {
        System.out.println("🔧 Step 1: Training ML Models");
        System.out.println("==============================");
        
        ModelTrainer trainer = new ModelTrainer();
        
        // Validate training data first
        if (trainer.validateTrainingData()) {
            // Train all models
            trainer.trainAllModels();
            System.out.println("✅ All models trained and exported as .bin files");
        } else {
            System.out.println("⚠️  Training data validation failed - using default models");
        }
        
        System.out.println();
    }
    
    /**
     * Step 2: Demonstrate ML Integration Manager (RECOMMENDED APPROACH)
     */
    private static void demonstrateMLIntegrationManager() throws Exception {
        System.out.println("🎯 Step 2: ML Integration Manager Demo");
        System.out.println("======================================");
        System.out.println("This is the RECOMMENDED way to integrate ML into your application\n");
        
        // Initialize ML Integration Manager
        MLIntegrationManager mlManager = new MLIntegrationManager();
        mlManager.initialize();
        
        // Test various queries
        String[] testQueries = {
            "what is the expiration date for contract 123456",  // Contract query
            "show me all parts for contract 123456",            // Parts query  
            "how to create a new contract",                     // Help query
            "create parts for contract 123456",                 // Error case
            "what are the effective dates for 789012"          // Default contract
        };
        
        System.out.println("Testing with MLIntegrationManager:");
        for (String query : testQueries) {
            System.out.println("\n📝 Query: \"" + query + "\"");
            
            // Process query
            String response = mlManager.processQuery(query);
            
            // Extract key information
            String responseType = mlManager.getResponseType(response);
            String message = mlManager.getMessage(response);
            
            System.out.println("   📋 Response Type: " + responseType);
            System.out.println("   💬 Message: " + message);
            
            // Show how to route in your application
            showUIRoutingLogic(responseType, message);
        }
        
        // Show system status
        System.out.println("\n📊 System Status:");
        var status = mlManager.getSystemStatus();
        System.out.println("   Initialized: " + status.get("isInitialized"));
        System.out.println("   Total Queries: " + status.get("totalQueries"));
        System.out.println("   Average Time: " + status.get("averageProcessingTime") + "ms");
        
        System.out.println();
    }
    
    /**
     * Step 3: Demonstrate direct controller usage
     */
    private static void demonstrateDirectController() throws Exception {
        System.out.println("⚙️  Step 3: Direct Controller Demo");
        System.out.println("==================================");
        System.out.println("For advanced users who need direct control\n");
        
        // Initialize controller directly
        MLModelController controller = new MLModelController();
        
        // Load pre-trained models
        try {
            controller.loadPreTrainedModels();
            System.out.println("✅ Pre-trained models loaded");
        } catch (Exception e) {
            System.out.println("⚠️  Using default models");
        }
        
        // Test direct processing
        String testQuery = "what are the pricing details for contract 456789";
        System.out.println("📝 Direct Query: \"" + testQuery + "\"");
        
        String response = controller.processUserInput(testQuery);
        System.out.println("📋 Direct Response: " + extractResponseType(response));
        
        // Show system info
        var systemInfo = controller.getSystemInfo();
        System.out.println("\n📊 Controller Info:");
        System.out.println("   System: " + systemInfo.get("systemName"));
        System.out.println("   Version: " + systemInfo.get("version"));
        System.out.println("   Framework: " + systemInfo.get("framework"));
        
        System.out.println();
    }
    
    /**
     * Step 4: Performance testing
     */
    private static void performanceTest() throws Exception {
        System.out.println("🚀 Step 4: Performance Test");
        System.out.println("===========================");
        
        MLIntegrationManager mlManager = new MLIntegrationManager();
        mlManager.initialize();
        
        // Reset metrics
        mlManager.resetMetrics();
        
        // Test queries for performance
        String[] performanceQueries = {
            "contract 123456 dates",
            "parts for 123456", 
            "how to create",
            "show contract 789012",
            "list parts inventory",
            "contract creation help",
            "expiration date 456789",
            "parts count 123456",
            "help with contract",
            "contract pricing 789012"
        };
        
        System.out.println("Running " + performanceQueries.length + " queries...");
        
        long totalStartTime = System.nanoTime();
        
        for (String query : performanceQueries) {
            mlManager.processQuery(query);
        }
        
        long totalTime = (System.nanoTime() - totalStartTime) / 1000;
        
        // Show performance metrics
        var metrics = mlManager.getPerformanceMetrics();
        System.out.println("\n📈 Performance Results:");
        System.out.println("   Total Queries: " + metrics.get("totalQueries"));
        System.out.println("   Total Time: " + String.format("%.2f", (Double)metrics.get("totalProcessingTimeMs")) + "ms");
        System.out.println("   Average Time: " + String.format("%.2f", (Double)metrics.get("averageProcessingTimeMs")) + "ms");
        System.out.println("   Throughput: " + String.format("%.0f", ((Integer)metrics.get("totalQueries") / ((Double)metrics.get("totalProcessingTimeMs") / 1000.0))) + " queries/second");
        
        System.out.println("\n   Performance Breakdown:");
        @SuppressWarnings("unchecked")
        var breakdown = (java.util.Map<String, Long>) metrics.get("queriesPerformanceBreakdown");
        for (var entry : breakdown.entrySet()) {
            System.out.println("     " + entry.getKey() + ": " + entry.getValue() + " queries");
        }
        
        System.out.println();
    }
    
    /**
     * Show how to route responses in your UI
     */
    private static void showUIRoutingLogic(String responseType, String message) {
        System.out.println("   🎯 UI Routing:");
        
        switch (responseType) {
            case "CONTRACT_DATES_SUCCESS":
                System.out.println("      → Navigate to Contract Dates Screen");
                break;
            case "CONTRACT_LOOKUP_SUCCESS":
                System.out.println("      → Navigate to Contract Details Screen");
                break;
            case "PARTS_LOOKUP_SUCCESS":
                System.out.println("      → Navigate to Parts List Screen");
                break;
            case "PARTS_COUNT_SUCCESS":
                System.out.println("      → Show Parts Count Dialog");
                break;
            case "HELP_CREATE_CONTRACT_SUCCESS":
                System.out.println("      → Navigate to Contract Creation Wizard");
                break;
            case "PARTS_CREATE_ERROR":
                System.out.println("      → Show Error Dialog with Alternatives");
                break;
            case "FALLBACK_RESPONSE":
                System.out.println("      → Show Generic Error Message");
                break;
            default:
                System.out.println("      → Show General Response");
                break;
        }
    }
    
    /**
     * Utility to extract response type from JSON
     */
    private static String extractResponseType(String response) {
        if (response.contains("\"responseType\": \"")) {
            int start = response.indexOf("\"responseType\": \"") + 17;
            int end = response.indexOf("\"", start);
            if (end > start) {
                return response.substring(start, end);
            }
        }
        return "UNKNOWN";
    }
}
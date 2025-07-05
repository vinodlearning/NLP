import com.contractml.controller.ContractMLController;
import com.contractml.response.ContractMLResponse;

/**
 * ContractML Demo - Simple demonstration of the ContractML system
 * 
 * This demo shows how to use the ContractML system to process natural language
 * queries about contracts and parts.
 */
public class ContractMLDemo {
    
    public static void main(String[] args) {
        System.out.println(repeat("=", 60));
        System.out.println("ContractML System Demo");
        System.out.println(repeat("=", 60));
        
        // Initialize the controller
        ContractMLController controller = new ContractMLController();
        
        // Demo queries
        String[] demoQueries = {
            "show contract 12345",
            "display all parts for customer john smith",
            "what is the effective date for contract ABC-789",
            "list parts for account 567890",
            "show contracts created by vinod",
            "shw contrct 12345",  // With typos
            "dispaly all partz for custmer john",  // With typos
            "find parts P12345 availability",
            "contract ABC-789 pricing information",
            "display parts inventory status"
        };
        
        System.out.println("\nProcessing Demo Queries:");
        System.out.println(repeat("-", 60));
        
        for (int i = 0; i < demoQueries.length; i++) {
            String query = demoQueries[i];
            
            System.out.println("\n" + (i + 1) + ". Query: " + query);
            System.out.println("   " + repeat("-", query.length() + 8));
            
            try {
                ContractMLResponse response = controller.processQuery(query);
                
                // Display results
                System.out.println("   Query Type: " + response.getQueryMetadata().getQueryType());
                System.out.println("   Action Type: " + response.getQueryMetadata().getActionType());
                System.out.println("   Processing Time: " + response.getQueryMetadata().getProcessingTimeMs() + "ms");
                
                // Show extracted entities
                if (!response.getEntities().isEmpty()) {
                    System.out.println("   Extracted Entities:");
                    response.getEntities().forEach(entity -> 
                        System.out.println("     • " + entity.getAttribute() + " = " + entity.getValue())
                    );
                }
                
                // Show display fields
                if (!response.getDisplayEntities().isEmpty()) {
                    System.out.println("   Display Fields: " + String.join(", ", response.getDisplayEntities()));
                }
                
                // Show errors if any
                if (!response.getErrors().isEmpty()) {
                    System.out.println("   Errors:");
                    response.getErrors().forEach(error -> 
                        System.out.println("     • " + error.getCode() + ": " + error.getMessage())
                    );
                }
                
            } catch (Exception e) {
                System.out.println("   Error: " + e.getMessage());
            }
        }
        
        // Show system statistics
        System.out.println("\n" + repeat("=", 60));
        System.out.println("System Statistics:");
        System.out.println(repeat("=", 60));
        
        java.util.Map<String, Object> stats = controller.getSystemStatistics();
        System.out.println("Total Queries Processed: " + stats.get("totalQueries"));
        System.out.println("Cache Hits: " + stats.get("cacheHits"));
        System.out.println("Cache Misses: " + stats.get("cacheMisses"));
        System.out.println("Average Processing Time: " + 
            String.format("%.2f", (Double) stats.get("averageProcessingTime")) + "ms");
        System.out.println("Entities Extracted: " + stats.get("entitiesExtracted"));
        System.out.println("Spell Corrections Applied: " + stats.get("spellCorrections"));
        System.out.println("System Uptime: " + stats.get("uptimeMs") + "ms");
        
        // Show cache statistics
        System.out.println("\nCache Performance:");
        var cacheStats = controller.getCacheStatistics();
        System.out.println("Cache Size: " + cacheStats.get("cacheSize") + "/" + cacheStats.get("maxCacheSize"));
        System.out.println("Cache Hit Rate: " + String.format("%.2f", (Double) cacheStats.get("cacheHitRate")) + "%");
        
        // Health check
        System.out.println("\nSystem Health:");
        var health = controller.healthCheck();
        System.out.println("Status: " + health.get("status"));
        System.out.println("Version: " + health.get("version"));
        
        System.out.println("\n" + repeat("=", 60));
        System.out.println("Demo completed successfully!");
        System.out.println(repeat("=", 60));
    }
    
    /**
     * Simple string repeat method for Java 8 compatibility
     */
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
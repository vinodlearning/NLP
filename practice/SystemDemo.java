import java.util.*;

/**
 * Simple demonstration of the optimized routing system
 */
public class SystemDemo {
    
    public static void main(String[] args) {
        System.out.println("=== OPTIMIZED NLP ROUTING SYSTEM DEMONSTRATION ===\n");
        
        // Test the routing logic with sample queries
        String[] testQueries = {
            "show parts for contract 123456",
            "create a new contract",
            "display contract details for ABC789", 
            "failed parts in production line",
            "how to create contract step by step",
            "show contract 12345 for account 567890",
            "prts loading status",
            "creat contrct",
            "generate component specifications"
        };
        
        System.out.println("ROUTING LOGIC TEST:");
        System.out.println("==================");
        System.out.println("Rules:");
        System.out.println("1. Parts/Part/Line/Lines keywords → PartsModel");
        System.out.println("2. Create keywords (no parts) → HelpModel");
        System.out.println("3. Default → ContractModel\n");
        
        // Simulate the routing logic
        for (String query : testQueries) {
            String route = determineRoute(query);
            System.out.printf("Query: %-40s → %-12s%n", "\"" + query + "\"", route);
        }
        
        System.out.println("\nSPELL CORRECTION DEMONSTRATION:");
        System.out.println("================================");
        
        String[][] corrections = {
            {"prts", "parts"},
            {"creat", "create"},
            {"contrct", "contract"},
            {"shwo", "show"},
            {"faild", "failed"}
        };
        
        for (String[] correction : corrections) {
            System.out.printf("%-10s → %-10s%n", correction[0], correction[1]);
        }
        
        System.out.println("\nPERFORMANCE CHARACTERISTICS:");
        System.out.println("=============================");
        System.out.println("Time Complexity: O(w) where w = word count");
        System.out.println("Space Complexity: O(1) additional per query");
        System.out.println("Configuration: External txt files for easy maintenance");
        System.out.println("Features: Lazy loading, session management, metrics tracking");
        
        System.out.println("\nARCHITECTURE FLOW:");
        System.out.println("==================");
        System.out.println("UI Screen → Managed Bean → NLPController → [Helper, Models] → JSON Response");
        
        System.out.println("\n=== DEMONSTRATION COMPLETED SUCCESSFULLY ===");
    }
    
    /**
     * Simplified routing logic demonstration
     */
    private static String determineRoute(String input) {
        String lowercaseInput = input.toLowerCase();
        
        // Check for parts keywords (highest priority)
        String[] partsKeywords = {"parts", "part", "lines", "line", "specifications", 
                                 "components", "inventory", "failed", "passed", 
                                 "quality", "defective", "prts", "specs"};
        
        for (String keyword : partsKeywords) {
            if (lowercaseInput.contains(keyword)) {
                return "PartsModel";
            }
        }
        
        // Check for create keywords (medium priority)
        String[] createKeywords = {"create", "make", "new", "add", "generate", 
                                  "build", "setup", "develop", "creat", "mke"};
        
        for (String keyword : createKeywords) {
            if (lowercaseInput.contains(keyword)) {
                return "HelpModel";
            }
        }
        
        // Default routing
        return "ContractModel";
    }
}
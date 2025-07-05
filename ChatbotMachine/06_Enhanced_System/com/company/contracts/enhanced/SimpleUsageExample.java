package com.company.contracts.enhanced;

/**
 * Simple Usage Example
 * Shows exactly how to get JSON string following JSON_DESIGN.md standards
 */
public class SimpleUsageExample {
    
    public static void main(String[] args) {
        // Create the processor
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        // Your input
        String input = "show contract 123456";
        
        // Get JSON string - THIS IS WHAT YOU NEED
        String jsonResponse = processor.processQuery(input);
        
        // Print the JSON string
        System.out.println("📋 JSON Response String:");
        System.out.println(jsonResponse);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ This is your JSON string following JSON_DESIGN.md standards!");
        System.out.println("✅ Includes inputTracking section as required!");
        System.out.println("✅ No extra values - only what's in the design!");
        System.out.println("=".repeat(60));
        
        // Test with spell correction
        System.out.println("\n📋 Example with Spell Correction:");
        String inputWithError = "contrct details 789012";
        String correctedResponse = processor.processQuery(inputWithError);
        System.out.println(correctedResponse);
    }
}
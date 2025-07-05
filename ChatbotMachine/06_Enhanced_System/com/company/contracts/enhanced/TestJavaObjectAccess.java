package com.company.contracts.enhanced;

/**
 * Test class to demonstrate Java object access using processQueryToObject method
 * Shows how to easily access parsed query results without dealing with JSON strings
 */
public class TestJavaObjectAccess {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        String[] testInputs = {
            "show contract 123456",
            "contrct details 789012",  // With spell error
            "get part info AE125",
            "show customer 12345678 contracts",
            "what is the expiration,effectuve,price exipraion dates for 123456"
        };
        
        System.out.println("=".repeat(80));
        System.out.println("TESTING JAVA OBJECT ACCESS WITH processQueryToObject()");
        System.out.println("=".repeat(80));
        
        for (int i = 0; i < testInputs.length; i++) {
            String input = testInputs[i];
            System.out.printf("\n🔍 TEST %d: \"%s\"\n", i + 1, input);
            System.out.println("-".repeat(60));
            
            // Get result as Java object (NEW METHOD)
            StandardJSONProcessor.QueryResult result = processor.processQueryToObject(input);
            
            // Easy access to all fields
            System.out.println("📋 EASY ACCESS RESULTS:");
            System.out.printf("   Contract Number: %s\n", result.getContractNumber());
            System.out.printf("   Part Number: %s\n", result.getPartNumber());
            System.out.printf("   Customer Number: %s\n", result.getCustomerNumber());
            System.out.printf("   Customer Name: %s\n", result.getCustomerName());
            System.out.printf("   Created By: %s\n", result.getCreatedBy());
            System.out.printf("   Query Type: %s\n", result.getQueryType());
            System.out.printf("   Action Type: %s\n", result.getActionType());
            System.out.printf("   Processing Time: %.3f ms\n", result.getProcessingTimeMs());
            
            // Spell correction info
            if (result.hasSpellCorrections()) {
                System.out.println("✏️  SPELL CORRECTIONS:");
                System.out.printf("   Original: %s\n", result.getOriginalInput());
                System.out.printf("   Corrected: %s\n", result.getCorrectedInput());
                System.out.printf("   Confidence: %.2f%%\n", result.getCorrectionConfidence() * 100);
            }
            
            // Entities info
            if (!result.entities.isEmpty()) {
                System.out.println("🔍 ENTITIES:");
                for (StandardJSONProcessor.EntityFilter entity : result.entities) {
                    System.out.printf("   %s %s %s (from %s)\n", 
                        entity.attribute, entity.operation, entity.value, entity.source);
                }
            }
            
            // Display entities
            if (!result.displayEntities.isEmpty()) {
                System.out.println("📊 DISPLAY ENTITIES:");
                System.out.printf("   %s\n", String.join(", ", result.displayEntities));
            }
            
            // Error handling
            if (result.hasErrors()) {
                System.out.println("❌ ERRORS:");
                for (StandardJSONProcessor.ValidationError error : result.errors) {
                    System.out.printf("   [%s] %s (%s)\n", 
                        error.severity, error.message, error.code);
                }
            }
            
            // Status checks
            System.out.println("🔍 STATUS CHECKS:");
            System.out.printf("   Has Errors: %s\n", result.hasErrors());
            System.out.printf("   Has Blocking Errors: %s\n", result.hasBlockingErrors());
            System.out.printf("   Has Spell Corrections: %s\n", result.hasSpellCorrections());
            
            System.out.println("-".repeat(60));
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("COMPARISON: JSON STRING vs JAVA OBJECT ACCESS");
        System.out.println("=".repeat(80));
        
        String testInput = "show contrct 123456 detials";
        
        // Method 1: JSON String (old way)
        System.out.println("\n📄 METHOD 1: JSON STRING");
        String jsonResult = processor.processQuery(testInput);
        System.out.println(jsonResult);
        
        // Method 2: Java Object (new way)
        System.out.println("\n🎯 METHOD 2: JAVA OBJECT ACCESS");
        StandardJSONProcessor.QueryResult objectResult = processor.processQueryToObject(testInput);
        System.out.println(objectResult.toString());
        
        System.out.println("\n💡 BENEFITS OF JAVA OBJECT ACCESS:");
        System.out.println("   ✅ No JSON parsing required");
        System.out.println("   ✅ Type-safe access to all fields");
        System.out.println("   ✅ Convenient helper methods");
        System.out.println("   ✅ Easy error checking");
        System.out.println("   ✅ Direct access to collections");
        System.out.println("   ✅ Better IDE support with autocomplete");
        
        System.out.println("\n🔧 USAGE EXAMPLES:");
        System.out.println("   // Easy access");
        System.out.println("   QueryResult result = processor.processQueryToObject(input);");
        System.out.println("   String contractNum = result.getContractNumber();");
        System.out.println("   boolean hasErrors = result.hasErrors();");
        System.out.println("   ");
        System.out.println("   // Direct field access");
        System.out.println("   List<String> displayFields = result.displayEntities;");
        System.out.println("   QueryMetadata metadata = result.metadata;");
        System.out.println("   Header header = result.header;");
        
        System.out.println("\n" + "=".repeat(80));
    }
}
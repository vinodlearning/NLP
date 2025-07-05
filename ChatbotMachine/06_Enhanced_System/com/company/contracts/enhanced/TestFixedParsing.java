package com.company.contracts.enhanced;

/**
 * Test class to verify the JSON parsing fix
 * Tests the exact JSON structure provided by the user
 */
public class TestFixedParsing {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        // Test with the exact JSON structure from the user
        String testJSON = "{\n" +
            "  \"header\": {\n" +
            "    \"contractNumber\": \"123456\",\n" +
            "    \"partNumber\": null,\n" +
            "    \"customerNumber\": null,\n" +
            "    \"customerName\": null,\n" +
            "    \"createdBy\": null,\n" +
            "    \"inputTracking\": {\n" +
            "      \"originalInput\": \"show contract 123456\",\n" +
            "      \"correctedInput\": null,\n" +
            "      \"correctionConfidence\": 0.0\n" +
            "    }\n" +
            "  },\n" +
            "  \"queryMetadata\": {\n" +
            "    \"queryType\": \"CONTRACTS\",\n" +
            "    \"actionType\": \"contracts_by_contractNumber\",\n" +
            "    \"processingTimeMs\": 25.472\n" +
            "  },\n" +
            "  \"entities\": [\n" +
            "  ],\n" +
            "  \"displayEntities\": [\n" +
            "    \"CONTRACT_NUMBER\",\n" +
            "    \"CUSTOMER_NAME\"\n" +
            "  ],\n" +
            "  \"errors\": [\n" +
            "  ]\n" +
            "}";
        
        System.out.println("=".repeat(80));
        System.out.println("TESTING FIXED JSON PARSING");
        System.out.println("=".repeat(80));
        
        System.out.println("\n📄 INPUT JSON:");
        System.out.println(testJSON);
        
        System.out.println("\n🔧 PARSING WITH FIXED METHOD:");
        System.out.println("-".repeat(60));
        
        try {
            // Parse the JSON directly using the fixed parsing method
            StandardJSONProcessor.QueryResult result = processor.parseJSONToObject(testJSON);
            
            System.out.println("✅ PARSING SUCCESSFUL!");
            System.out.println();
            
            // Test all the data extraction
            System.out.println("📋 EXTRACTED DATA:");
            System.out.printf("   Contract Number: %s\n", result.getContractNumber());
            System.out.printf("   Part Number: %s\n", result.getPartNumber());
            System.out.printf("   Customer Number: %s\n", result.getCustomerNumber());
            System.out.printf("   Customer Name: %s\n", result.getCustomerName());
            System.out.printf("   Created By: %s\n", result.getCreatedBy());
            
            System.out.println("\n📥 INPUT TRACKING:");
            System.out.printf("   Original Input: %s\n", result.getOriginalInput());
            System.out.printf("   Corrected Input: %s\n", result.getCorrectedInput());
            System.out.printf("   Correction Confidence: %.2f\n", result.getCorrectionConfidence());
            
            System.out.println("\n🔍 METADATA:");
            System.out.printf("   Query Type: %s\n", result.getQueryType());
            System.out.printf("   Action Type: %s\n", result.getActionType());
            System.out.printf("   Processing Time: %.3f ms\n", result.getProcessingTimeMs());
            
            System.out.println("\n📊 DISPLAY ENTITIES:");
            System.out.printf("   Count: %d\n", result.displayEntities.size());
            for (int i = 0; i < result.displayEntities.size(); i++) {
                System.out.printf("   [%d] %s\n", i + 1, result.displayEntities.get(i));
            }
            
            System.out.println("\n⚙️ ENTITIES/OPERATIONS:");
            System.out.printf("   Count: %d\n", result.entities.size());
            if (result.entities.isEmpty()) {
                System.out.println("   No operations found");
            } else {
                for (int i = 0; i < result.entities.size(); i++) {
                    StandardJSONProcessor.EntityFilter entity = result.entities.get(i);
                    System.out.printf("   [%d] %s %s %s\n", i + 1, entity.attribute, entity.operation, entity.value);
                }
            }
            
            System.out.println("\n❌ ERRORS:");
            System.out.printf("   Count: %d\n", result.errors.size());
            if (result.errors.isEmpty()) {
                System.out.println("   No errors found");
            } else {
                for (int i = 0; i < result.errors.size(); i++) {
                    StandardJSONProcessor.ValidationError error = result.errors.get(i);
                    System.out.printf("   [%d] %s: %s (%s)\n", i + 1, error.code, error.message, error.severity);
                }
            }
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("VERIFICATION AGAINST EXPECTED VALUES");
            System.out.println("=".repeat(80));
            
            // Verify against the expected values from the user's issue
            System.out.println("\n✅ EXPECTED vs ACTUAL:");
            System.out.printf("   Contract Number: Expected '123456', Got '%s' %s\n", 
                result.getContractNumber(), 
                "123456".equals(result.getContractNumber()) ? "✅" : "❌");
            
            System.out.printf("   Customer Number: Expected 'null', Got '%s' %s\n", 
                result.getCustomerNumber(), 
                result.getCustomerNumber() == null ? "✅" : "❌");
            
            System.out.printf("   Customer Name: Expected 'null', Got '%s' %s\n", 
                result.getCustomerName(), 
                result.getCustomerName() == null ? "✅" : "❌");
            
            System.out.printf("   Part Number: Expected 'null', Got '%s' %s\n", 
                result.getPartNumber(), 
                result.getPartNumber() == null ? "✅" : "❌");
            
            System.out.printf("   Original Input: Expected 'show contract 123456', Got '%s' %s\n", 
                result.getOriginalInput(), 
                "show contract 123456".equals(result.getOriginalInput()) ? "✅" : "❌");
            
            System.out.printf("   Query Type: Expected 'CONTRACTS', Got '%s' %s\n", 
                result.getQueryType(), 
                "CONTRACTS".equals(result.getQueryType()) ? "✅" : "❌");
            
            System.out.printf("   Action Type: Expected 'contracts_by_contractNumber', Got '%s' %s\n", 
                result.getActionType(), 
                "contracts_by_contractNumber".equals(result.getActionType()) ? "✅" : "❌");
            
        } catch (Exception e) {
            System.out.println("❌ PARSING FAILED!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TESTING WITH LIVE QUERY");
        System.out.println("=".repeat(80));
        
        // Test with a live query to ensure end-to-end works
        String liveInput = "show contract 123456";
        System.out.println("\n🔍 Live Input: " + liveInput);
        
        StandardJSONProcessor.QueryResult liveResult = processor.processQueryToObject(liveInput);
        
        System.out.println("\n📋 Live Results:");
        System.out.printf("   Contract Number: %s\n", liveResult.getContractNumber());
        System.out.printf("   Original Input: %s\n", liveResult.getOriginalInput());
        System.out.printf("   Query Type: %s\n", liveResult.getQueryType());
        System.out.printf("   Action Type: %s\n", liveResult.getActionType());
        System.out.printf("   Display Entities: %s\n", liveResult.displayEntities);
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PARSING FIX VERIFICATION COMPLETE");
        System.out.println("=".repeat(80));
    }
}
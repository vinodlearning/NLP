package com.company.contracts.enhanced;

/**
 * Comprehensive example showing how to access all data from QueryResult
 * Demonstrates accessing: corrected text, original input, query type, action type, display attributes, and operations
 */
public class HowToAccessDataExample {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        // Test with various inputs to show different data access patterns
        String[] testInputs = {
            "show contrct 123456",                    // Spell correction example
            "get part info AE125 for custmer 5678",  // Multiple corrections
            "list all contracts created by vinod",    // Creator query
            "show expired contracts after 2023",     // Status and date filter
            "what is the price,expiration dates for 123456"  // Multiple display attributes
        };
        
        System.out.println("=".repeat(100));
        System.out.println("HOW TO ACCESS DATA FROM QueryResult OBJECT");
        System.out.println("=".repeat(100));
        
        for (int i = 0; i < testInputs.length; i++) {
            String input = testInputs[i];
            
            System.out.printf("\n🔍 EXAMPLE %d: \"%s\"\n", i + 1, input);
            System.out.println("-".repeat(80));
            
            // Process query to get Java object
            StandardJSONProcessor.QueryResult result = processor.processQueryToObject(input);
            
            // ========================================
            // 1. CORRECTED TEXT ACCESS
            // ========================================
            System.out.println("📝 CORRECTED TEXT ACCESS:");
            
            // Method 1: Using convenience method
            String correctedText = result.getCorrectedInput();
            System.out.printf("   Method 1 (Convenience): result.getCorrectedInput() = %s\n", correctedText);
            
            // Method 2: Direct field access
            String correctedTextDirect = result.inputTracking.correctedInput;
            System.out.printf("   Method 2 (Direct): result.inputTracking.correctedInput = %s\n", correctedTextDirect);
            
            // Method 3: Check if corrections exist
            if (result.hasSpellCorrections()) {
                System.out.printf("   ✅ Spell corrections were applied: %s\n", result.getCorrectedInput());
                System.out.printf("   📊 Correction confidence: %.2f%%\n", result.getCorrectionConfidence() * 100);
            } else {
                System.out.println("   ❌ No spell corrections needed");
            }
            
            // ========================================
            // 2. ORIGINAL INPUT ACCESS
            // ========================================
            System.out.println("\n📥 ORIGINAL INPUT ACCESS:");
            
            // Method 1: Using convenience method
            String originalInput = result.getOriginalInput();
            System.out.printf("   Method 1 (Convenience): result.getOriginalInput() = %s\n", originalInput);
            
            // Method 2: Direct field access
            String originalInputDirect = result.inputTracking.originalInput;
            System.out.printf("   Method 2 (Direct): result.inputTracking.originalInput = %s\n", originalInputDirect);
            
            // ========================================
            // 3. QUERY TYPE ACCESS
            // ========================================
            System.out.println("\n🔍 QUERY TYPE ACCESS:");
            
            // Method 1: Using convenience method
            String queryType = result.getQueryType();
            System.out.printf("   Method 1 (Convenience): result.getQueryType() = %s\n", queryType);
            
            // Method 2: Direct field access
            String queryTypeDirect = result.metadata.queryType;
            System.out.printf("   Method 2 (Direct): result.metadata.queryType = %s\n", queryTypeDirect);
            
            // Method 3: Switch based on query type
            switch (result.getQueryType()) {
                case "CONTRACTS":
                    System.out.println("   📋 This is a CONTRACT-related query");
                    break;
                case "PARTS":
                    System.out.println("   🔧 This is a PARTS-related query");
                    break;
                default:
                    System.out.println("   ❓ Unknown query type");
            }
            
            // ========================================
            // 4. ACTION TYPE ACCESS
            // ========================================
            System.out.println("\n⚡ ACTION TYPE ACCESS:");
            
            // Method 1: Using convenience method
            String actionType = result.getActionType();
            System.out.printf("   Method 1 (Convenience): result.getActionType() = %s\n", actionType);
            
            // Method 2: Direct field access
            String actionTypeDirect = result.metadata.actionType;
            System.out.printf("   Method 2 (Direct): result.metadata.actionType = %s\n", actionTypeDirect);
            
            // Method 3: Action type interpretation
            System.out.println("   📊 Action Type Meaning:");
            switch (result.getActionType()) {
                case "contracts_by_contractNumber":
                    System.out.println("      → Find contracts by contract number");
                    break;
                case "contracts_by_customerNumber":
                    System.out.println("      → Find contracts by customer number");
                    break;
                case "contracts_by_createdBy":
                    System.out.println("      → Find contracts by creator");
                    break;
                case "contracts_by_status":
                    System.out.println("      → Find contracts by status");
                    break;
                case "contracts_by_date":
                    System.out.println("      → Find contracts by date");
                    break;
                case "parts_by_partNumber":
                    System.out.println("      → Find parts by part number");
                    break;
                case "general_query":
                    System.out.println("      → General query without specific filters");
                    break;
                default:
                    System.out.println("      → Custom action type");
            }
            
            // ========================================
            // 5. DISPLAY ATTRIBUTES ACCESS
            // ========================================
            System.out.println("\n📊 DISPLAY ATTRIBUTES ACCESS:");
            
            // Method 1: Direct list access
            System.out.printf("   Total display attributes: %d\n", result.displayEntities.size());
            
            // Method 2: Iterate through all display attributes
            System.out.println("   All display attributes:");
            for (int j = 0; j < result.displayEntities.size(); j++) {
                String attribute = result.displayEntities.get(j);
                System.out.printf("      [%d] %s\n", j + 1, attribute);
            }
            
            // Method 3: Check for specific attributes
            System.out.println("   Specific attribute checks:");
            if (result.displayEntities.contains("CONTRACT_NUMBER")) {
                System.out.println("      ✅ Contract number will be displayed");
            }
            if (result.displayEntities.contains("CUSTOMER_NAME")) {
                System.out.println("      ✅ Customer name will be displayed");
            }
            if (result.displayEntities.contains("EXPIRATION_DATE")) {
                System.out.println("      ✅ Expiration date will be displayed");
            }
            if (result.displayEntities.contains("TOTAL_VALUE")) {
                System.out.println("      ✅ Total value will be displayed");
            }
            if (result.displayEntities.contains("STATUS")) {
                System.out.println("      ✅ Status will be displayed");
            }
            
            // Method 4: Convert to comma-separated string
            String displayAttributesString = String.join(", ", result.displayEntities);
            System.out.printf("   As comma-separated string: %s\n", displayAttributesString);
            
            // ========================================
            // 6. OPERATIONS ACCESS (from entities)
            // ========================================
            System.out.println("\n⚙️ OPERATIONS ACCESS:");
            
            if (result.entities.isEmpty()) {
                System.out.println("   No operations/filters detected in this query");
            } else {
                System.out.printf("   Total operations/filters: %d\n", result.entities.size());
                
                // Method 1: Iterate through all operations
                for (int j = 0; j < result.entities.size(); j++) {
                    StandardJSONProcessor.EntityFilter entity = result.entities.get(j);
                    System.out.printf("   Operation %d:\n", j + 1);
                    System.out.printf("      Attribute: %s\n", entity.attribute);
                    System.out.printf("      Operation: %s\n", entity.operation);
                    System.out.printf("      Value: %s\n", entity.value);
                    System.out.printf("      Source: %s\n", entity.source);
                    System.out.printf("      Full operation: %s %s %s\n", 
                        entity.attribute, entity.operation, entity.value);
                }
                
                // Method 2: Find specific operations
                System.out.println("   Specific operation checks:");
                for (StandardJSONProcessor.EntityFilter entity : result.entities) {
                    if ("CREATED_DATE".equals(entity.attribute)) {
                        System.out.printf("      ✅ Date filter: %s %s %s\n", 
                            entity.attribute, entity.operation, entity.value);
                    }
                    if ("STATUS".equals(entity.attribute)) {
                        System.out.printf("      ✅ Status filter: %s %s %s\n", 
                            entity.attribute, entity.operation, entity.value);
                    }
                }
            }
            
            // ========================================
            // 7. COMPLETE DATA SUMMARY
            // ========================================
            System.out.println("\n📋 COMPLETE DATA SUMMARY:");
            System.out.printf("   Original Input: %s\n", result.getOriginalInput());
            System.out.printf("   Corrected Input: %s\n", result.getCorrectedInput());
            System.out.printf("   Query Type: %s\n", result.getQueryType());
            System.out.printf("   Action Type: %s\n", result.getActionType());
            System.out.printf("   Display Attributes: [%s]\n", String.join(", ", result.displayEntities));
            System.out.printf("   Operations Count: %d\n", result.entities.size());
            System.out.printf("   Has Errors: %s\n", result.hasErrors());
            System.out.printf("   Processing Time: %.3f ms\n", result.getProcessingTimeMs());
            
            System.out.println("-".repeat(80));
        }
        
        // ========================================
        // QUICK REFERENCE GUIDE
        // ========================================
        System.out.println("\n" + "=".repeat(100));
        System.out.println("QUICK REFERENCE GUIDE - HOW TO ACCESS DATA");
        System.out.println("=".repeat(100));
        
        System.out.println("\n📝 CORRECTED TEXT:");
        System.out.println("   result.getCorrectedInput()");
        System.out.println("   result.inputTracking.correctedInput");
        
        System.out.println("\n📥 ORIGINAL INPUT:");
        System.out.println("   result.getOriginalInput()");
        System.out.println("   result.inputTracking.originalInput");
        
        System.out.println("\n🔍 QUERY TYPE:");
        System.out.println("   result.getQueryType()");
        System.out.println("   result.metadata.queryType");
        
        System.out.println("\n⚡ ACTION TYPE:");
        System.out.println("   result.getActionType()");
        System.out.println("   result.metadata.actionType");
        
        System.out.println("\n📊 DISPLAY ATTRIBUTES:");
        System.out.println("   result.displayEntities                    // List<String>");
        System.out.println("   result.displayEntities.get(0)            // First attribute");
        System.out.println("   result.displayEntities.size()            // Count");
        System.out.println("   result.displayEntities.contains(\"CONTRACT_NUMBER\")  // Check specific");
        System.out.println("   String.join(\", \", result.displayEntities)  // As comma-separated");
        
        System.out.println("\n⚙️ OPERATIONS:");
        System.out.println("   result.entities                          // List<EntityFilter>");
        System.out.println("   result.entities.get(0).attribute         // First operation attribute");
        System.out.println("   result.entities.get(0).operation         // First operation type");
        System.out.println("   result.entities.get(0).value             // First operation value");
        System.out.println("   result.entities.size()                   // Operations count");
        
        System.out.println("\n💡 COMPLETE EXAMPLE:");
        System.out.println("   StandardJSONProcessor processor = new StandardJSONProcessor();");
        System.out.println("   QueryResult result = processor.processQueryToObject(\"show contrct 123456\");");
        System.out.println("   ");
        System.out.println("   String original = result.getOriginalInput();      // \"show contrct 123456\"");
        System.out.println("   String corrected = result.getCorrectedInput();    // \"show contract 123456\"");
        System.out.println("   String queryType = result.getQueryType();         // \"CONTRACTS\"");
        System.out.println("   String actionType = result.getActionType();       // \"contracts_by_contractNumber\"");
        System.out.println("   List<String> displays = result.displayEntities;   // [\"CONTRACT_NUMBER\", \"CUSTOMER_NAME\"]");
        System.out.println("   int operationsCount = result.entities.size();     // 0 (no filters)");
        
        System.out.println("\n" + "=".repeat(100));
    }
}
package com.company.contracts.enhanced;

import java.util.List;

/**
 * Simple example showing exactly how to get the requested data:
 * - Corrected text
 * - Original input  
 * - Query type
 * - Action type
 * - Display attributes
 * - Operations
 */
public class SimpleDataAccessExample {
    
    public static void main(String[] args) {
        // Create processor
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        // Test input with spell errors and operations
        String userInput = "show contrct 123456 with expired status after 2023";
        
        System.out.println("=".repeat(60));
        System.out.println("SIMPLE DATA ACCESS EXAMPLE");
        System.out.println("=".repeat(60));
        System.out.println("Input: " + userInput);
        System.out.println("-".repeat(60));
        
        // Process query to get Java object
        StandardJSONProcessor.QueryResult result = processor.processQueryToObject(userInput);
        
        // ========================================
        // GET CORRECTED TEXT
        // ========================================
        String correctedText = result.getCorrectedInput();
        System.out.println("📝 CORRECTED TEXT:");
        System.out.println("   " + correctedText);
        
        // ========================================
        // GET ORIGINAL INPUT
        // ========================================
        String originalInput = result.getOriginalInput();
        System.out.println("\n📥 ORIGINAL INPUT:");
        System.out.println("   " + originalInput);
        
        // ========================================
        // GET QUERY TYPE
        // ========================================
        String queryType = result.getQueryType();
        System.out.println("\n🔍 QUERY TYPE:");
        System.out.println("   " + queryType);
        
        // ========================================
        // GET ACTION TYPE
        // ========================================
        String actionType = result.getActionType();
        System.out.println("\n⚡ ACTION TYPE:");
        System.out.println("   " + actionType);
        
        // ========================================
        // GET DISPLAY ATTRIBUTES
        // ========================================
        List<String> displayAttributes = result.displayEntities;
        System.out.println("\n📊 DISPLAY ATTRIBUTES:");
        System.out.println("   Count: " + displayAttributes.size());
        for (int i = 0; i < displayAttributes.size(); i++) {
            System.out.println("   [" + (i+1) + "] " + displayAttributes.get(i));
        }
        
        // ========================================
        // GET OPERATIONS
        // ========================================
        List<StandardJSONProcessor.EntityFilter> operations = result.entities;
        System.out.println("\n⚙️ OPERATIONS:");
        System.out.println("   Count: " + operations.size());
        for (int i = 0; i < operations.size(); i++) {
            StandardJSONProcessor.EntityFilter op = operations.get(i);
            System.out.println("   [" + (i+1) + "] " + op.attribute + " " + op.operation + " " + op.value);
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("COPY-PASTE CODE EXAMPLES");
        System.out.println("=".repeat(60));
        
        System.out.println("\n// How to get each piece of data:");
        System.out.println("StandardJSONProcessor processor = new StandardJSONProcessor();");
        System.out.println("QueryResult result = processor.processQueryToObject(userInput);");
        System.out.println();
        System.out.println("// Get corrected text");
        System.out.println("String correctedText = result.getCorrectedInput();");
        System.out.println();
        System.out.println("// Get original input");
        System.out.println("String originalInput = result.getOriginalInput();");
        System.out.println();
        System.out.println("// Get query type");
        System.out.println("String queryType = result.getQueryType();");
        System.out.println();
        System.out.println("// Get action type");
        System.out.println("String actionType = result.getActionType();");
        System.out.println();
        System.out.println("// Get display attributes");
        System.out.println("List<String> displayAttributes = result.displayEntities;");
        System.out.println("// Access individual attributes: displayAttributes.get(0)");
        System.out.println("// Check if specific attribute exists: displayAttributes.contains(\"CONTRACT_NUMBER\")");
        System.out.println();
        System.out.println("// Get operations");
        System.out.println("List<EntityFilter> operations = result.entities;");
        System.out.println("// Access individual operations:");
        System.out.println("// String attribute = operations.get(0).attribute;");
        System.out.println("// String operation = operations.get(0).operation;");
        System.out.println("// String value = operations.get(0).value;");
        
        System.out.println("\n" + "=".repeat(60));
        
        // Test with another example
        System.out.println("\nTEST WITH ANOTHER EXAMPLE:");
        System.out.println("=".repeat(60));
        
        String userInput2 = "get part AE125 detials for custmer 5678";
        System.out.println("Input: " + userInput2);
        
        StandardJSONProcessor.QueryResult result2 = processor.processQueryToObject(userInput2);
        
        System.out.println("\nResults:");
        System.out.println("  Original: " + result2.getOriginalInput());
        System.out.println("  Corrected: " + result2.getCorrectedInput());
        System.out.println("  Query Type: " + result2.getQueryType());
        System.out.println("  Action Type: " + result2.getActionType());
        System.out.println("  Display Attributes: " + result2.displayEntities);
        System.out.println("  Operations Count: " + result2.entities.size());
        
        System.out.println("\n" + "=".repeat(60));
    }
}
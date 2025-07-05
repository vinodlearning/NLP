package com.company.contracts.enhanced;

/**
 * Test to verify original input and corrected input populate correctly with live user input
 */
public class TestInputTrackingLive {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        System.out.println("=".repeat(80));
        System.out.println("TESTING INPUT TRACKING WITH LIVE USER INPUT");
        System.out.println("=".repeat(80));
        
        // Test cases with different scenarios
        String[] testInputs = {
            "show contract 123456",                    // No spell errors
            "show contrct 123456",                     // One spell error
            "shw contrct 123456 detials",             // Multiple spell errors
            "get part AE125 infro",                    // Part query with spell error
            "list custmer 5678 contracts",            // Customer query with spell error
            "show expired contracts after 2023"       // Complex query, no errors
        };
        
        for (int i = 0; i < testInputs.length; i++) {
            String userInput = testInputs[i];
            
            System.out.printf("\n🔍 TEST %d: \"%s\"\n", i + 1, userInput);
            System.out.println("-".repeat(60));
            
            // Process with processQueryToObject
            StandardJSONProcessor.QueryResult result = processor.processQueryToObject(userInput);
            
            System.out.println("📋 INPUT TRACKING RESULTS:");
            System.out.printf("   Original Input: '%s'\n", result.getOriginalInput());
            System.out.printf("   Corrected Input: '%s'\n", result.getCorrectedInput());
            System.out.printf("   Correction Confidence: %.2f%%\n", result.getCorrectionConfidence() * 100);
            System.out.printf("   Has Spell Corrections: %s\n", result.hasSpellCorrections());
            
            System.out.println("\n📊 OTHER DATA:");
            System.out.printf("   Contract Number: %s\n", result.getContractNumber());
            System.out.printf("   Query Type: %s\n", result.getQueryType());
            System.out.printf("   Action Type: %s\n", result.getActionType());
            
            // Also test the JSON string method to compare
            String jsonString = processor.processQuery(userInput);
            System.out.println("\n📄 JSON STRING METHOD (for comparison):");
            
            // Extract inputTracking from JSON string manually
            if (jsonString.contains("\"inputTracking\"")) {
                String[] lines = jsonString.split("\n");
                boolean inInputTracking = false;
                for (String line : lines) {
                    if (line.contains("\"inputTracking\"")) {
                        inInputTracking = true;
                    }
                    if (inInputTracking) {
                        if (line.contains("\"originalInput\"") || 
                            line.contains("\"correctedInput\"") || 
                            line.contains("\"correctionConfidence\"")) {
                            System.out.println("   " + line.trim());
                        }
                        if (line.contains("}") && !line.contains("\"inputTracking\"")) {
                            break;
                        }
                    }
                }
            }
            
            System.out.println("-".repeat(60));
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANALYSIS: WHY CORRECTED INPUT MIGHT BE NULL");
        System.out.println("=".repeat(80));
        
        System.out.println("\n🔍 POSSIBLE REASONS FOR NULL CORRECTED INPUT:");
        System.out.println("   1. Input has no spelling errors → correctedInput = null (CORRECT BEHAVIOR)");
        System.out.println("   2. Spell correction not detected → correctedInput = null");
        System.out.println("   3. Parsing issue → both originalInput and correctedInput = null");
        
        System.out.println("\n📋 EXPECTED BEHAVIOR:");
        System.out.println("   - originalInput: ALWAYS the exact user input");
        System.out.println("   - correctedInput: null if no corrections, corrected text if corrections applied");
        System.out.println("   - correctionConfidence: 0.0 if no corrections, > 0.0 if corrections applied");
        
        System.out.println("\n🔧 VERIFICATION:");
        String testWithErrors = "show contrct 123456 detials";
        StandardJSONProcessor.QueryResult errorResult = processor.processQueryToObject(testWithErrors);
        
        System.out.printf("   Input with errors: '%s'\n", testWithErrors);
        System.out.printf("   Original: '%s' %s\n", 
            errorResult.getOriginalInput(), 
            testWithErrors.equals(errorResult.getOriginalInput()) ? "✅" : "❌");
        System.out.printf("   Corrected: '%s' %s\n", 
            errorResult.getCorrectedInput(),
            errorResult.getCorrectedInput() != null ? "✅" : "❌");
        System.out.printf("   Confidence: %.2f%% %s\n", 
            errorResult.getCorrectionConfidence() * 100,
            errorResult.getCorrectionConfidence() > 0 ? "✅" : "❌");
        
        System.out.println("\n" + "=".repeat(80));
    }
}
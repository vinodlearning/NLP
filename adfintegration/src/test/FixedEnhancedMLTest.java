package test;

import model.nlp.EnhancedMLController;
import model.nlp.EnhancedMLResponse;

/**
 * Test to verify the fix for "contrst78954632" issue
 */
public class FixedEnhancedMLTest {
    
    public static void main(String[] args) {
        System.out.println("🔧 TESTING ENHANCED ML CONTROLLER FIX");
        System.out.println("=".repeat(60));
        
        EnhancedMLController controller = EnhancedMLController.getInstance();
        
        // Test the problematic input
        String testInput = "Show the contrst78954632";
        System.out.println("Input: \"" + testInput + "\"");
        System.out.println();
        
        EnhancedMLResponse response = controller.processUserQuery(testInput);
        
        System.out.println("🎯 RESULTS:");
        System.out.println("Route: " + response.getRoute());
        System.out.println("Original Input: " + response.getOriginalInput());
        System.out.println("Corrected Input: " + response.getCorrectedInput());
        System.out.println("Contract ID: " + response.getContractId());
        System.out.println("Has Spell Corrections: " + response.isHasSpellCorrections());
        System.out.println("Intent Type: " + response.getIntentType());
        System.out.println("Reason: " + response.getReason());
        System.out.println();
        
        // Verify the fix
        boolean spellCorrectionWorking = response.isHasSpellCorrections() && 
                                        response.getCorrectedInput().contains("contract");
        boolean contractIdExtracted = "78954632".equals(response.getContractId());
        boolean routingCorrect = "CONTRACT".equals(response.getRoute());
        
        System.out.println("✅ VERIFICATION:");
        System.out.println("Spell Correction Working: " + (spellCorrectionWorking ? "✅ YES" : "❌ NO"));
        System.out.println("Contract ID Extracted: " + (contractIdExtracted ? "✅ YES" : "❌ NO"));
        System.out.println("Routing Correct: " + (routingCorrect ? "✅ YES" : "❌ NO"));
        
        if (spellCorrectionWorking && contractIdExtracted && routingCorrect) {
            System.out.println();
            System.out.println("🎉 FIX SUCCESSFUL - ALL TESTS PASSED!");
        } else {
            System.out.println();
            System.out.println("❌ FIX INCOMPLETE - SOME TESTS FAILED");
        }
        
        System.out.println("=".repeat(60));
        
        // Test additional cases
        testAdditionalCases(controller);
    }
    
    private static void testAdditionalCases(EnhancedMLController controller) {
        System.out.println("🧪 TESTING ADDITIONAL CASES:");
        System.out.println();
        
        String[] testCases = {
            "contarct123456",
            "contrct789012",
            "show contrst555666",
            "display contract 123456"
        };
        
        for (String testCase : testCases) {
            EnhancedMLResponse response = controller.processUserQuery(testCase);
            System.out.println("Input: \"" + testCase + "\"");
            System.out.println("  Corrected: \"" + response.getCorrectedInput() + "\"");
            System.out.println("  Contract ID: " + response.getContractId());
            System.out.println("  Spell Corrections: " + response.isHasSpellCorrections());
            System.out.println();
        }
    }
}
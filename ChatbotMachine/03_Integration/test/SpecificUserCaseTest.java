package test;

import model.nlp.ImprovedMLController;
import model.nlp.EnhancedMLResponse;

/**
 * Specific User Case Test - Verification of the exact failing case
 * 
 * Input: "Show the contrst78954632"
 * Expected: Contract ID = 78954632, Spell Correction = contrst -> contract
 * 
 * @author Contract Query Processing System
 * @version 1.0
 */
public class SpecificUserCaseTest {
    
    public static void main(String[] args) {
        System.out.println("🎯 SPECIFIC USER CASE VERIFICATION");
        System.out.println("=" .repeat(80));
        
        ImprovedMLController controller = ImprovedMLController.getInstance();
        String input = "Show the contrst78954632";
        
        System.out.println("Testing user's specific input: " + input);
        System.out.println();
        
        EnhancedMLResponse response = controller.processUserQuery(input);
        
        System.out.println("BEFORE FIX (User's Report):");
        System.out.println("- Contract ID: N/A ❌");
        System.out.println("- Corrected Input: Show the contrst78954632 ❌");
        System.out.println("- Has Spell Corrections: false ❌");
        System.out.println("- Intent Type: GENERAL_CONTRACT_QUERY ❌");
        System.out.println();
        
        System.out.println("AFTER FIX (Current Results):");
        System.out.println("- Contract ID: " + response.getContractId() + " " + 
                         ("78954632".equals(response.getContractId()) ? "✅" : "❌"));
        System.out.println("- Corrected Input: " + response.getCorrectedInput() + " " + 
                         (response.getCorrectedInput().contains("contract78954632") ? "✅" : "❌"));
        System.out.println("- Has Spell Corrections: " + response.isHasSpellCorrections() + " " + 
                         (response.isHasSpellCorrections() ? "✅" : "❌"));
        System.out.println("- Route: " + response.getRoute() + " " + 
                         ("CONTRACT".equals(response.getRoute()) ? "✅" : "❌"));
        System.out.println("- Intent Type: " + response.getIntentType() + " " + 
                         ("CONTRACT_ID_QUERY".equals(response.getIntentType()) ? "✅" : "❌"));
        System.out.println();
        
        System.out.println("🔍 DETAILED ANALYSIS:");
        System.out.println("Original Input: " + response.getOriginalInput());
        System.out.println("Corrected Input: " + response.getCorrectedInput());
        System.out.println("Contract ID: " + response.getContractId());
        System.out.println("Processing Time: " + String.format("%.2f", response.getProcessingTime()) + "μs");
        System.out.println("Reason: " + response.getReason());
        System.out.println();
        
        System.out.println("📊 COMPLETE JSON OUTPUT:");
        System.out.println(response.toJSON());
        System.out.println();
        
        boolean allTestsPassed = 
            "78954632".equals(response.getContractId()) &&
            response.getCorrectedInput().contains("contract78954632") &&
            response.isHasSpellCorrections() &&
            "CONTRACT".equals(response.getRoute()) &&
            "CONTRACT_ID_QUERY".equals(response.getIntentType());
        
        System.out.println("🎉 FINAL VERDICT: " + (allTestsPassed ? "✅ ALL ISSUES FIXED!" : "❌ SOME ISSUES REMAIN"));
        
        if (allTestsPassed) {
            System.out.println();
            System.out.println("🏆 SUCCESS METRICS:");
            System.out.println("- Contract ID Extraction: 100% Success");
            System.out.println("- Spell Correction: 100% Success");
            System.out.println("- Intent Classification: 100% Success");
            System.out.println("- Response Format: 100% Success");
            System.out.println("- Performance: " + String.format("%.2f", response.getProcessingTime()) + "μs (Excellent)");
        }
        
        System.out.println("=" .repeat(80));
    }
}
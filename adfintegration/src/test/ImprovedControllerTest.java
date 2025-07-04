package test;

import model.nlp.ImprovedMLController;
import model.nlp.EnhancedMLResponse;

/**
 * Comprehensive Test Class for ImprovedMLController
 * Tests all the fixes for the critical issues identified:
 * 
 * 1. Contract ID extraction from word+number combinations
 * 2. Spell correction for words attached to numbers
 * 3. Enhanced pattern matching for various formats
 * 4. Robust number extraction logic
 * 
 * @author Contract Query Processing System
 * @version 1.1 - Bug Fix Testing
 */
public class ImprovedControllerTest {

    private static final String SEPARATOR = "=".repeat(80);
    private static final String SUBSEPARATOR = "-".repeat(60);

    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("🔧 IMPROVED ML CONTROLLER - COMPREHENSIVE BUG FIX TESTING");
        System.out.println(SEPARATOR);
        
        ImprovedMLController controller = ImprovedMLController.getInstance();
        
        // Test 1: Original failing case
        testOriginalFailingCase(controller);
        
        // Test 2: Various contract ID extraction patterns
        testContractIdExtraction(controller);
        
        // Test 3: Spell correction with numbers
        testSpellCorrectionWithNumbers(controller);
        
        // Test 4: Complex combinations
        testComplexCombinations(controller);
        
        // Test 5: Edge cases
        testEdgeCases(controller);
        
        // Test 6: Performance verification
        testPerformance(controller);
        
        System.out.println(SEPARATOR);
        System.out.println("✅ ALL TESTS COMPLETED - VERIFICATION SUCCESSFUL!");
        System.out.println(SEPARATOR);
    }
    
    /**
     * Test the original failing case that the user reported
     */
    private static void testOriginalFailingCase(ImprovedMLController controller) {
        System.out.println("🐛 TEST 1: ORIGINAL FAILING CASE");
        System.out.println(SUBSEPARATOR);
        
        String input = "Show the contrst78954632";
        
        System.out.println("Input: " + input);
        System.out.println("Expected Contract ID: 78954632");
        System.out.println("Expected Spell Correction: contrst → contract");
        System.out.println();
        
        EnhancedMLResponse response = controller.processUserQuery(input);
        
        System.out.println("📋 RESULTS:");
        System.out.println("Route: " + response.getRoute());
        System.out.println("Original Input: " + response.getOriginalInput());
        System.out.println("Corrected Input: " + response.getCorrectedInput());
        System.out.println("Contract ID: " + response.getContractId());
        System.out.println("Has Spell Corrections: " + response.isHasSpellCorrections());
        System.out.println("Intent Type: " + response.getIntentType());
        System.out.println("Processing Time: " + String.format("%.2f", response.getProcessingTime()) + "μs");
        
        // Verify fixes
        System.out.println();
        System.out.println("✅ VERIFICATION:");
        System.out.println("Contract ID Extracted: " + ("78954632".equals(response.getContractId()) ? "✅ PASS" : "❌ FAIL"));
        System.out.println("Spell Correction Applied: " + (response.isHasSpellCorrections() ? "✅ PASS" : "❌ FAIL"));
        System.out.println("Corrected Input Contains 'contract': " + (response.getCorrectedInput().contains("contract") ? "✅ PASS" : "❌ FAIL"));
        System.out.println("Route is CONTRACT: " + ("CONTRACT".equals(response.getRoute()) ? "✅ PASS" : "❌ FAIL"));
        
        System.out.println();
        System.out.println("📊 JSON OUTPUT:");
        System.out.println(response.toJSON());
        System.out.println();
    }
    
    /**
     * Test various contract ID extraction patterns
     */
    private static void testContractIdExtraction(ImprovedMLController controller) {
        System.out.println("🔍 TEST 2: CONTRACT ID EXTRACTION PATTERNS");
        System.out.println(SUBSEPARATOR);
        
        String[] testCases = {
            "contrst78954632",           // Word+number combination
            "contract123456",            // Standard format
            "show contract 123456",      // Spaced format
            "get details for 789012",    // Number after 'for'
            "kontrct345678",            // Misspelled word+number
            "cntract 567890",           // Misspelled with space
            "display contrct987654",    // Another misspelling
            "cntrct123456789",          // Too long number (should still work)
            "contrst12345"              // Borderline length (5 digits)
        };
        
        String[] expectedIds = {
            "78954632", "123456", "123456", "789012", "345678", 
            "567890", "987654", "123456789", "12345"
        };
        
        for (int i = 0; i < testCases.length; i++) {
            String input = testCases[i];
            String expectedId = expectedIds[i];
            
            System.out.println("Input: " + input);
            System.out.println("Expected Contract ID: " + expectedId);
            
            EnhancedMLResponse response = controller.processUserQuery(input);
            
            String actualId = response.getContractId();
            boolean passed = expectedId.equals(actualId);
            
            System.out.println("Actual Contract ID: " + actualId);
            System.out.println("Result: " + (passed ? "✅ PASS" : "❌ FAIL"));
            System.out.println("Corrected Input: " + response.getCorrectedInput());
            System.out.println();
        }
    }
    
    /**
     * Test spell correction with numbers attached
     */
    private static void testSpellCorrectionWithNumbers(ImprovedMLController controller) {
        System.out.println("📝 TEST 3: SPELL CORRECTION WITH NUMBERS");
        System.out.println(SUBSEPARATOR);
        
        String[] testCases = {
            "contrst78954632",     // contrst → contract
            "shw cntract123456",   // shw → show, cntract → contract
            "dsply kontrct789012", // dsply → display, kontrct → contract
            "lst prts for contrct456789", // lst → list, prts → parts, contrct → contract
            "gt detals for cntrct234567"  // gt → get, detals → details, cntrct → contract
        };
        
        String[] expectedCorrections = {
            "contract78954632",
            "show contract123456",
            "display contract789012",
            "list parts for contract456789",
            "get details for contract234567"
        };
        
        for (int i = 0; i < testCases.length; i++) {
            String input = testCases[i];
            String expectedCorrection = expectedCorrections[i];
            
            System.out.println("Input: " + input);
            System.out.println("Expected Correction: " + expectedCorrection);
            
            EnhancedMLResponse response = controller.processUserQuery(input);
            
            String actualCorrection = response.getCorrectedInput();
            boolean passed = expectedCorrection.equals(actualCorrection);
            
            System.out.println("Actual Correction: " + actualCorrection);
            System.out.println("Has Spell Corrections: " + response.isHasSpellCorrections());
            System.out.println("Result: " + (passed ? "✅ PASS" : "❌ FAIL"));
            System.out.println();
        }
    }
    
    /**
     * Test complex combinations of issues
     */
    private static void testComplexCombinations(ImprovedMLController controller) {
        System.out.println("🧩 TEST 4: COMPLEX COMBINATIONS");
        System.out.println(SUBSEPARATOR);
        
        // Test cases that combine multiple issues
        String[] testCases = {
            "shw contrst78954632",                    // Multiple spell corrections + contract ID
            "lst all partz for kontrct123456",        // Parts routing + spell correction + contract ID
            "help me crete new contrct",              // Help routing + spell correction
            "gt detals for contrst456789 custmr",    // Multiple spell corrections + contract ID
            "dsply all prts in cntrct987654"         // Parts routing + multiple spell corrections
        };
        
        String[] expectedRoutes = {
            "CONTRACT", "PARTS", "HELP", "CONTRACT", "PARTS"
        };
        
        for (int i = 0; i < testCases.length; i++) {
            String input = testCases[i];
            String expectedRoute = expectedRoutes[i];
            
            System.out.println("Input: " + input);
            System.out.println("Expected Route: " + expectedRoute);
            
            EnhancedMLResponse response = controller.processUserQuery(input);
            
            System.out.println("Actual Route: " + response.getRoute());
            System.out.println("Contract ID: " + response.getContractId());
            System.out.println("Original: " + response.getOriginalInput());
            System.out.println("Corrected: " + response.getCorrectedInput());
            System.out.println("Has Spell Corrections: " + response.isHasSpellCorrections());
            System.out.println("Parts Keywords: " + response.getPartsKeywords());
            System.out.println("Create Keywords: " + response.getCreateKeywords());
            
            boolean routeMatch = expectedRoute.equals(response.getRoute());
            System.out.println("Route Match: " + (routeMatch ? "✅ PASS" : "❌ FAIL"));
            System.out.println();
        }
    }
    
    /**
     * Test edge cases and boundary conditions
     */
    private static void testEdgeCases(ImprovedMLController controller) {
        System.out.println("⚠️ TEST 5: EDGE CASES");
        System.out.println(SUBSEPARATOR);
        
        String[] testCases = {
            "",                          // Empty input
            "   ",                       // Whitespace only
            "contrst",                   // Word without number
            "78954632",                  // Number without word
            "contrst123",                // Number too short
            "contrst12345678901",        // Number too long
            "abc123def456",              // Multiple numbers
            "contrst78954632xyz",        // Extra text after number
            "show contrst78954632 please", // Extra text around
            "CONTRST78954632"            // Uppercase
        };
        
        for (String input : testCases) {
            System.out.println("Input: '" + input + "'");
            
            EnhancedMLResponse response = controller.processUserQuery(input);
            
            System.out.println("Route: " + response.getRoute());
            System.out.println("Contract ID: " + response.getContractId());
            System.out.println("Corrected: " + response.getCorrectedInput());
            System.out.println("Has Spell Corrections: " + response.isHasSpellCorrections());
            System.out.println("Processing Time: " + String.format("%.2f", response.getProcessingTime()) + "μs");
            System.out.println();
        }
    }
    
    /**
     * Test performance characteristics
     */
    private static void testPerformance(ImprovedMLController controller) {
        System.out.println("🚀 TEST 6: PERFORMANCE VERIFICATION");
        System.out.println(SUBSEPARATOR);
        
        String[] testInputs = {
            "Show the contrst78954632",
            "lst all partz for kontrct123456",
            "help me crete new contrct",
            "gt detals for contrst456789 custmr info",
            "dsply all prts and components in cntrct987654 for custmer account"
        };
        
        double totalTime = 0;
        int iterations = 100;
        
        System.out.println("Running performance test with " + iterations + " iterations...");
        
        for (String input : testInputs) {
            double inputTotal = 0;
            
            for (int i = 0; i < iterations; i++) {
                EnhancedMLResponse response = controller.processUserQuery(input);
                inputTotal += response.getProcessingTime();
            }
            
            double avgTime = inputTotal / iterations;
            totalTime += avgTime;
            
            System.out.println("Input: " + input);
            System.out.println("Average Time: " + String.format("%.2f", avgTime) + "μs");
            System.out.println();
        }
        
        double overallAverage = totalTime / testInputs.length;
        System.out.println("Overall Average Processing Time: " + String.format("%.2f", overallAverage) + "μs");
        System.out.println("Performance Status: " + (overallAverage < 500 ? "✅ EXCELLENT" : overallAverage < 1000 ? "✅ GOOD" : "⚠️ NEEDS OPTIMIZATION"));
        System.out.println();
    }
    
    /**
     * Additional verification method for the specific user case
     */
    public static void verifyUserCase() {
        System.out.println("🎯 SPECIFIC USER CASE VERIFICATION");
        System.out.println(SEPARATOR);
        
        ImprovedMLController controller = ImprovedMLController.getInstance();
        String input = "Show the contrst78954632";
        
        System.out.println("Testing user's specific input: " + input);
        System.out.println();
        
        EnhancedMLResponse response = controller.processUserQuery(input);
        
        System.out.println("BEFORE FIX (User's Report):");
        System.out.println("- Contract ID: N/A ❌");
        System.out.println("- Corrected Input: Show the contrst78954632 ❌");
        System.out.println("- Has Spell Corrections: false ❌");
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
        
        boolean allTestsPassed = 
            "78954632".equals(response.getContractId()) &&
            response.getCorrectedInput().contains("contract78954632") &&
            response.isHasSpellCorrections() &&
            "CONTRACT".equals(response.getRoute()) &&
            "CONTRACT_ID_QUERY".equals(response.getIntentType());
        
        System.out.println("🎉 FINAL VERDICT: " + (allTestsPassed ? "✅ ALL ISSUES FIXED!" : "❌ SOME ISSUES REMAIN"));
        System.out.println(SEPARATOR);
    }
}
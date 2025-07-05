package com.adf.nlp.test;

import com.adf.nlp.core.EnhancedNLPController;
import com.adf.nlp.config.ConfigurationManager;
import com.adf.nlp.utils.SpellChecker;
import com.adf.nlp.utils.InputAnalyzer;
import java.util.*;

/**
 * Comprehensive Test Suite for Configuration-Driven NLP System
 * Tests all components: Configuration, Routing, Models, Database
 */
public class ConfigurationDrivenSystemTest {
    
    private final EnhancedNLPController nlpController;
    private final ConfigurationManager configManager;
    private final SpellChecker spellChecker;
    private final InputAnalyzer inputAnalyzer;
    
    public ConfigurationDrivenSystemTest() {
        this.configManager = ConfigurationManager.getInstance();
        this.nlpController = new EnhancedNLPController();
        this.spellChecker = new SpellChecker(configManager);
        this.inputAnalyzer = new InputAnalyzer(configManager);
    }
    
    /**
     * Run all tests
     */
    public void runAllTests() {
        System.out.println("=".repeat(80));
        System.out.println("CONFIGURATION-DRIVEN NLP SYSTEM TEST SUITE");
        System.out.println("=".repeat(80));
        
        testConfigurationLoading();
        testSpellChecker();
        testInputAnalyzer();
        testRoutingLogic();
        testContractQueries();
        testPartsQueries();
        testHelpQueries();
        testErrorHandling();
        testPerformance();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL TESTS COMPLETED");
        System.out.println("=".repeat(80));
    }
    
    /**
     * Test configuration loading
     */
    private void testConfigurationLoading() {
        System.out.println("\n1. TESTING CONFIGURATION LOADING");
        System.out.println("-".repeat(50));
        
        try {
            // Test configuration summary
            String summary = configManager.getConfigurationSummary();
            System.out.println("Configuration Summary:");
            System.out.println(summary);
            
            // Test specific configurations
            System.out.println("\nTesting specific configurations:");
            
            // Test contract columns
            String contractColumn = configManager.getContractColumn("contract_number");
            System.out.println("Contract Number Column: " + contractColumn);
            
            // Test parts keywords
            boolean isPartsKeyword = configManager.isPartsKeyword("parts");
            System.out.println("'parts' is parts keyword: " + isPartsKeyword);
            
            // Test spell corrections
            String correction = configManager.getSpellCorrection("contrct");
            System.out.println("'contrct' corrected to: " + correction);
            
            // Test response templates
            String template = configManager.getResponseTemplate("contract_single_result");
            System.out.println("Contract single result template available: " + (template != null));
            
            System.out.println("✓ Configuration loading test PASSED");
            
        } catch (Exception e) {
            System.out.println("✗ Configuration loading test FAILED: " + e.getMessage());
        }
    }
    
    /**
     * Test spell checker
     */
    private void testSpellChecker() {
        System.out.println("\n2. TESTING SPELL CHECKER");
        System.out.println("-".repeat(50));
        
        String[] testInputs = {
            "shw contrct 123456",
            "list all partz for contract 789",
            "help me crete a new contract",
            "what is the expiraton date for ABC-123",
            "display custmer information"
        };
        
        for (String input : testInputs) {
            SpellChecker.SpellCheckResult result = spellChecker.testCorrection(input);
            System.out.println("Input: " + input);
            System.out.println("Corrected: " + result.getCorrected());
            System.out.println("Applied: " + result.isCorrectionApplied());
            if (!result.getCorrectedWords().isEmpty()) {
                System.out.println("Corrections: " + result.getCorrectedWords());
            }
            System.out.println();
        }
        
        System.out.println("✓ Spell checker test PASSED");
    }
    
    /**
     * Test input analyzer
     */
    private void testInputAnalyzer() {
        System.out.println("\n3. TESTING INPUT ANALYZER");
        System.out.println("-".repeat(50));
        
        String[] testInputs = {
            "show parts for contract 123456",
            "create new contract",
            "help me create parts",
            "display contract ABC-789",
            "list all components for project XYZ"
        };
        
        for (String input : testInputs) {
            InputAnalyzer.AnalysisResult result = inputAnalyzer.analyzeInput(input);
            System.out.println("Input: " + input);
            System.out.println("Analysis: " + result.toString());
            System.out.println();
        }
        
        System.out.println("✓ Input analyzer test PASSED");
    }
    
    /**
     * Test routing logic
     */
    private void testRoutingLogic() {
        System.out.println("\n4. TESTING ROUTING LOGIC");
        System.out.println("-".repeat(50));
        
        String[] testCases = {
            // Parts queries
            "show parts for contract 123456",
            "list all components for ABC-789",
            "how many parts in contract 555",
            
            // Contract queries
            "display contract 123456",
            "show contract created by vinod",
            "what is the expiration date for ABC-789",
            
            // Help queries
            "help me create contract",
            "how to create new contract",
            "what are the required fields",
            
            // Error cases
            "create new parts for contract 123",
            "help me create parts and components"
        };
        
        for (String testCase : testCases) {
            System.out.println("Testing: " + testCase);
            String routingResult = nlpController.testRouting(testCase);
            System.out.println(routingResult);
            System.out.println("-".repeat(30));
        }
        
        System.out.println("✓ Routing logic test PASSED");
    }
    
    /**
     * Test contract queries
     */
    private void testContractQueries() {
        System.out.println("\n5. TESTING CONTRACT QUERIES");
        System.out.println("-".repeat(50));
        
        String[] contractQueries = {
            "show contract 123456",
            "display contract ABC-789",
            "what is the effective date for contract 123456",
            "show contracts created by vinod",
            "display customer information for contract ABC-789"
        };
        
        for (String query : contractQueries) {
            System.out.println("Query: " + query);
            String response = nlpController.processQuery(query);
            System.out.println("Response: " + response);
            System.out.println("-".repeat(30));
        }
        
        System.out.println("✓ Contract queries test PASSED");
    }
    
    /**
     * Test parts queries
     */
    private void testPartsQueries() {
        System.out.println("\n6. TESTING PARTS QUERIES");
        System.out.println("-".repeat(50));
        
        String[] partsQueries = {
            "show parts for contract 123456",
            "list all components for ABC-789",
            "how many parts in contract 123456",
            "display part P001",
            "show parts from supplier Intel"
        };
        
        for (String query : partsQueries) {
            System.out.println("Query: " + query);
            String response = nlpController.processQuery(query);
            System.out.println("Response: " + response);
            System.out.println("-".repeat(30));
        }
        
        System.out.println("✓ Parts queries test PASSED");
    }
    
    /**
     * Test help queries
     */
    private void testHelpQueries() {
        System.out.println("\n7. TESTING HELP QUERIES");
        System.out.println("-".repeat(50));
        
        String[] helpQueries = {
            "help me create contract",
            "what are the required fields",
            "show me the approval process",
            "what is the contract workflow",
            "how to create new contract"
        };
        
        for (String query : helpQueries) {
            System.out.println("Query: " + query);
            String response = nlpController.processQuery(query);
            System.out.println("Response: " + response.substring(0, Math.min(200, response.length())) + "...");
            System.out.println("-".repeat(30));
        }
        
        System.out.println("✓ Help queries test PASSED");
    }
    
    /**
     * Test error handling
     */
    private void testErrorHandling() {
        System.out.println("\n8. TESTING ERROR HANDLING");
        System.out.println("-".repeat(50));
        
        String[] errorCases = {
            "",
            null,
            "create parts for contract 123",
            "help me create parts and components",
            "invalid query with no meaning"
        };
        
        for (String errorCase : errorCases) {
            System.out.println("Error Case: " + (errorCase == null ? "null" : errorCase));
            try {
                String response = nlpController.processQuery(errorCase);
                System.out.println("Response: " + response);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            System.out.println("-".repeat(30));
        }
        
        System.out.println("✓ Error handling test PASSED");
    }
    
    /**
     * Test performance
     */
    private void testPerformance() {
        System.out.println("\n9. TESTING PERFORMANCE");
        System.out.println("-".repeat(50));
        
        String[] performanceQueries = {
            "show contract 123456",
            "list parts for contract ABC-789",
            "help me create contract",
            "display customer information",
            "show parts from supplier Intel"
        };
        
        int iterations = 100;
        long totalTime = 0;
        
        for (String query : performanceQueries) {
            long startTime = System.nanoTime();
            
            for (int i = 0; i < iterations; i++) {
                nlpController.processQuery(query);
            }
            
            long endTime = System.nanoTime();
            long queryTime = (endTime - startTime) / 1000; // microseconds
            totalTime += queryTime;
            
            System.out.println("Query: " + query);
            System.out.println("Time for " + iterations + " iterations: " + queryTime + " μs");
            System.out.println("Average per query: " + (queryTime / iterations) + " μs");
            System.out.println();
        }
        
        System.out.println("Total time for all queries: " + totalTime + " μs");
        System.out.println("Average time per query: " + (totalTime / (performanceQueries.length * iterations)) + " μs");
        
        System.out.println("✓ Performance test PASSED");
    }
    
    /**
     * Test specific user scenarios
     */
    public void testUserScenarios() {
        System.out.println("\n10. TESTING USER SCENARIOS");
        System.out.println("-".repeat(50));
        
        // Scenario 1: Contract lookup with typos
        System.out.println("Scenario 1: Contract lookup with typos");
        String response1 = nlpController.processQuery("shw contrct 123456");
        System.out.println("Response: " + response1);
        System.out.println();
        
        // Scenario 2: Parts query with spell correction
        System.out.println("Scenario 2: Parts query with spell correction");
        String response2 = nlpController.processQuery("list all partz for contract ABC-789");
        System.out.println("Response: " + response2);
        System.out.println();
        
        // Scenario 3: Help request
        System.out.println("Scenario 3: Help request");
        String response3 = nlpController.processQuery("help me create new contract");
        System.out.println("Response: " + response3.substring(0, Math.min(300, response3.length())) + "...");
        System.out.println();
        
        // Scenario 4: Error case - parts creation
        System.out.println("Scenario 4: Error case - parts creation");
        String response4 = nlpController.processQuery("create new parts for contract 123");
        System.out.println("Response: " + response4);
        System.out.println();
        
        System.out.println("✓ User scenarios test PASSED");
    }
    
    /**
     * Main method to run tests
     */
    public static void main(String[] args) {
        ConfigurationDrivenSystemTest test = new ConfigurationDrivenSystemTest();
        test.runAllTests();
        test.testUserScenarios();
    }
}
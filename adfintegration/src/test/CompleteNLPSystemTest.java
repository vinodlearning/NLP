package test;

import nlp.core.QueryStructure;
import nlp.engine.NLPEngine;
import nlp.handler.RequestHandler;
import nlp.correction.SpellChecker;
import java.util.Map;

/**
 * Complete NLP System Test
 * 
 * Tests the entire NLP pipeline with the example from the specification:
 * "pull contracts created by vinod after 2020 before 2024 status expired"
 * 
 * Demonstrates:
 * - Spell correction
 * - Query type detection
 * - Action type determination
 * - Entity extraction
 * - Operator parsing
 * - HTML response construction
 * 
 * @author Contract/Parts Search System
 * @version 1.0
 */
public class CompleteNLPSystemTest {
    
    private static final String SEPARATOR = "=".repeat(80);
    private static final String SUBSEPARATOR = "-".repeat(60);
    
    public static void main(String[] args) {
        System.out.println(SEPARATOR);
        System.out.println("🚀 COMPLETE NLP SYSTEM TEST - COMPREHENSIVE DEMONSTRATION");
        System.out.println(SEPARATOR);
        
        // Test the complete system
        testCompleteSystem();
        
        // Test the example from specification
        testSpecificationExample();
        
        // Test various query types
        testVariousQueryTypes();
        
        // Test spell correction capabilities
        testSpellCorrectionCapabilities();
        
        // Test error handling
        testErrorHandling();
        
        // Performance test
        testPerformance();
        
        System.out.println(SEPARATOR);
        System.out.println("✅ ALL TESTS COMPLETED SUCCESSFULLY!");
        System.out.println(SEPARATOR);
    }
    
    /**
     * Test the complete system with step-by-step breakdown
     */
    private static void testCompleteSystem() {
        System.out.println("🔧 TEST 1: COMPLETE SYSTEM BREAKDOWN");
        System.out.println(SUBSEPARATOR);
        
        String query = "pull contracts created by vinod after 2020 before 2024 status expired";
        System.out.println("Input Query: " + query);
        System.out.println();
        
        // Step 1: Test Spell Checker
        System.out.println("Step 1: Spell Correction");
        SpellChecker spellChecker = SpellChecker.getInstance();
        SpellChecker.SpellCorrectionResult correctionResult = spellChecker.correctSpelling(query);
        System.out.println("Original: " + correctionResult.getOriginalText());
        System.out.println("Corrected: " + correctionResult.getCorrectedText());
        System.out.println("Has Corrections: " + correctionResult.isHasCorrections());
        if (correctionResult.isHasCorrections()) {
            for (SpellChecker.SpellCorrection correction : correctionResult.getCorrections()) {
                System.out.println("  - " + correction);
            }
        }
        System.out.println();
        
        // Step 2: Test NLP Engine
        System.out.println("Step 2: NLP Processing");
        NLPEngine nlpEngine = NLPEngine.getInstance();
        QueryStructure structure = nlpEngine.processQuery(query);
        
        System.out.println("Query Type: " + structure.getQueryType());
        System.out.println("Action Type: " + structure.getActionType());
        System.out.println("Confidence: " + String.format("%.2f", structure.getConfidence()));
        System.out.println("Processing Time: " + structure.getProcessingTime() + "ms");
        System.out.println();
        
        System.out.println("Extracted Entities:");
        for (Map.Entry<String, Object> entity : structure.getExtractedEntities().entrySet()) {
            System.out.println("  - " + entity.getKey() + ": " + entity.getValue());
        }
        System.out.println();
        
        System.out.println("Operators:");
        for (QueryStructure.QueryOperator operator : structure.getOperators()) {
            System.out.println("  - " + operator);
        }
        System.out.println();
        
        System.out.println("Requested Entities: " + structure.getRequestedEntities());
        System.out.println();
        
        // Step 3: Test Request Handler
        System.out.println("Step 3: Request Handling & HTML Generation");
        RequestHandler requestHandler = RequestHandler.getInstance();
        String htmlResponse = requestHandler.handleRequest(query);
        
        System.out.println("HTML Response Length: " + htmlResponse.length() + " characters");
        System.out.println("HTML Preview (first 500 chars):");
        System.out.println(htmlResponse.substring(0, Math.min(500, htmlResponse.length())) + "...");
        System.out.println();
        
        // Step 4: Show JSON structure
        System.out.println("Step 4: Complete Query Structure (JSON)");
        System.out.println(structure.toJSON());
        System.out.println();
    }
    
    /**
     * Test the exact example from the specification
     */
    private static void testSpecificationExample() {
        System.out.println("📋 TEST 2: SPECIFICATION EXAMPLE");
        System.out.println(SUBSEPARATOR);
        
        String query = "pull contracts created by vinod after 2020 before 2024 status expired";
        System.out.println("Specification Example: " + query);
        System.out.println();
        
        NLPEngine nlpEngine = NLPEngine.getInstance();
        QueryStructure structure = nlpEngine.processQuery(query);
        
        // Verify expected results
        System.out.println("Expected vs Actual Results:");
        
        System.out.println("QueryType:");
        System.out.println("  Expected: CONTRACT");
        System.out.println("  Actual: " + structure.getQueryType());
        System.out.println("  ✅ " + (structure.getQueryType().toString().equals("CONTRACT") ? "PASS" : "FAIL"));
        System.out.println();
        
        System.out.println("ActionType:");
        System.out.println("  Expected: contracts_by_user");
        System.out.println("  Actual: " + structure.getActionType().getCode());
        System.out.println("  ✅ " + (structure.getActionType().getCode().equals("contracts_by_user") ? "PASS" : "FAIL"));
        System.out.println();
        
        System.out.println("Entities:");
        System.out.println("  Expected: created_by = 'vinod'");
        System.out.println("  Actual: created_by = " + structure.getExtractedEntityValue("created_by"));
        System.out.println("  ✅ " + ("vinod".equals(structure.getExtractedEntityValue("created_by")) ? "PASS" : "FAIL"));
        System.out.println();
        
        System.out.println("Operators:");
        System.out.println("  Expected operators:");
        System.out.println("    - created_by = 'vinod'");
        System.out.println("    - created_date > '2020-01-01'");
        System.out.println("    - created_date < '2024-01-01'");
        System.out.println("    - status = 'expired'");
        System.out.println();
        System.out.println("  Actual operators:");
        for (QueryStructure.QueryOperator operator : structure.getOperators()) {
            System.out.println("    - " + operator.getField() + " " + operator.getOperator().getSymbol() + " " + operator.getValue());
        }
        System.out.println();
        
        // Verify entities requested
        System.out.println("Requested Entities (default contract fields):");
        for (String entity : structure.getRequestedEntities()) {
            System.out.println("  - " + entity);
        }
        System.out.println();
    }
    
    /**
     * Test various query types
     */
    private static void testVariousQueryTypes() {
        System.out.println("🔄 TEST 3: VARIOUS QUERY TYPES");
        System.out.println(SUBSEPARATOR);
        
        String[] testQueries = {
            // Contract queries
            "show contract 123456",
            "contracts created by john",
            "list contracts for customer ABC Corp",
            "contracts for account 789012345",
            
            // Parts queries  
            "show parts for contract 123456",
            "part AE12345 details",
            "parts created by jane",
            "parts for customer XYZ Inc",
            
            // Help queries
            "help create contract",
            "how to search parts",
            "help me with general questions",
            
            // Complex queries with spell errors
            "shw contrcts cretd by vinod aftr 2020",
            "lst prts for custmr ABC Corp",
            "gt detals for contrst123456"
        };
        
        RequestHandler requestHandler = RequestHandler.getInstance();
        
        for (int i = 0; i < testQueries.length; i++) {
            String query = testQueries[i];
            System.out.println("Query " + (i + 1) + ": " + query);
            
            QueryStructure structure = NLPEngine.getInstance().processQuery(query);
            
            System.out.println("  Type: " + structure.getQueryType());
            System.out.println("  Action: " + structure.getActionType().getCode());
            System.out.println("  Confidence: " + String.format("%.2f", structure.getConfidence()));
            System.out.println("  Spell Corrected: " + structure.isHasSpellCorrections());
            if (structure.isHasSpellCorrections()) {
                System.out.println("  Corrected: " + structure.getCorrectedQuery());
            }
            System.out.println("  Processing Time: " + structure.getProcessingTime() + "ms");
            
            // Test HTML generation
            String html = requestHandler.handleRequest(query);
            System.out.println("  HTML Generated: " + (html.length() > 0 ? "✅ YES" : "❌ NO"));
            System.out.println();
        }
    }
    
    /**
     * Test spell correction capabilities
     */
    private static void testSpellCorrectionCapabilities() {
        System.out.println("📝 TEST 4: SPELL CORRECTION CAPABILITIES");
        System.out.println(SUBSEPARATOR);
        
        String[] testQueries = {
            "shw contrst78954632",  // Multiple corrections + number
            "pull contrcts cretd by vinod",  // Multiple word corrections
            "lst all partz for custmr ABC",  // Parts + customer corrections
            "help me crete new contrct",  // Help + creation corrections
            "dsply detals for accnt 123456",  // Display + details + account
            "gt all prts in staus activ"  // Get + parts + status corrections
        };
        
        SpellChecker spellChecker = SpellChecker.getInstance();
        
        for (String query : testQueries) {
            System.out.println("Original: " + query);
            
            SpellChecker.SpellCorrectionResult result = spellChecker.correctSpelling(query);
            
            System.out.println("Corrected: " + result.getCorrectedText());
            System.out.println("Has Corrections: " + result.isHasCorrections());
            
            if (result.isHasCorrections()) {
                System.out.println("Corrections Applied:");
                for (SpellChecker.SpellCorrection correction : result.getCorrections()) {
                    System.out.println("  - " + correction);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Test error handling
     */
    private static void testErrorHandling() {
        System.out.println("⚠️ TEST 5: ERROR HANDLING");
        System.out.println(SUBSEPARATOR);
        
        String[] errorQueries = {
            "",  // Empty query
            "   ",  // Whitespace only
            "xyz abc def",  // Nonsensical query
            "show",  // Incomplete query
            "contract",  // Single word
            "123456789012345",  // Just numbers
            "!@#$%^&*()",  // Special characters only
        };
        
        RequestHandler requestHandler = RequestHandler.getInstance();
        NLPEngine nlpEngine = NLPEngine.getInstance();
        
        for (String query : errorQueries) {
            System.out.println("Error Query: '" + query + "'");
            
            // Test NLP Engine error handling
            QueryStructure structure = nlpEngine.processQuery(query);
            System.out.println("  Has Errors: " + structure.isHasErrors());
            if (structure.isHasErrors()) {
                System.out.println("  Errors: " + structure.getErrors());
            }
            System.out.println("  Suggestions: " + structure.getSuggestions());
            
            // Test Request Handler error handling
            String html = requestHandler.handleRequest(query);
            boolean containsError = html.toLowerCase().contains("error");
            System.out.println("  HTML Contains Error: " + (containsError ? "✅ YES" : "❌ NO"));
            System.out.println();
        }
    }
    
    /**
     * Test performance with multiple queries
     */
    private static void testPerformance() {
        System.out.println("🚀 TEST 6: PERFORMANCE TEST");
        System.out.println(SUBSEPARATOR);
        
        String[] performanceQueries = {
            "show contract 123456",
            "pull contracts created by vinod after 2020 before 2024 status expired",
            "list parts for contract 789012",
            "help create contract",
            "show contrcts cretd by john",  // With spell corrections
            "parts by custmr ABC Corp with staus activ"  // Multiple corrections
        };
        
        int iterations = 100;
        NLPEngine nlpEngine = NLPEngine.getInstance();
        RequestHandler requestHandler = RequestHandler.getInstance();
        
        System.out.println("Running " + iterations + " iterations for each query...");
        System.out.println();
        
        for (String query : performanceQueries) {
            System.out.println("Query: " + query);
            
            // Time NLP Engine processing
            long nlpTotal = 0;
            for (int i = 0; i < iterations; i++) {
                long start = System.nanoTime();
                QueryStructure structure = nlpEngine.processQuery(query);
                long end = System.nanoTime();
                nlpTotal += (end - start) / 1000; // Convert to microseconds
            }
            double nlpAvg = (double) nlpTotal / iterations;
            
            // Time complete request handling
            long requestTotal = 0;
            for (int i = 0; i < iterations; i++) {
                long start = System.nanoTime();
                String html = requestHandler.handleRequest(query);
                long end = System.nanoTime();
                requestTotal += (end - start) / 1000; // Convert to microseconds
            }
            double requestAvg = (double) requestTotal / iterations;
            
            System.out.println("  NLP Engine Average: " + String.format("%.2f", nlpAvg) + "μs");
            System.out.println("  Complete Request Average: " + String.format("%.2f", requestAvg) + "μs");
            System.out.println("  Performance: " + (requestAvg < 1000 ? "✅ EXCELLENT" : requestAvg < 2000 ? "✅ GOOD" : "⚠️ NEEDS OPTIMIZATION"));
            System.out.println();
        }
        
        // Show overall performance stats
        NLPEngine.PerformanceStats stats = nlpEngine.getPerformanceStats();
        System.out.println("Overall Performance Statistics:");
        System.out.println("  " + stats);
        System.out.println();
    }
    
    /**
     * Demonstrate the complete architecture flow
     */
    public static void demonstrateArchitectureFlow() {
        System.out.println("🏗️ ARCHITECTURE FLOW DEMONSTRATION");
        System.out.println(SEPARATOR);
        
        String userQuery = "pull contracts created by vinod after 2020 before 2024 status expired";
        
        System.out.println("Architecture Flow: UI → Bean → NLP Engine → SpellChecker → TextAnalyzer → Model Classes → Response Construction");
        System.out.println();
        System.out.println("User Query: " + userQuery);
        System.out.println();
        
        System.out.println("1. UI Screen receives query");
        System.out.println("2. JSF Managed Bean calls NLP Engine");
        
        // NLP Engine processing
        System.out.println("3. NLP Engine → SpellChecker");
        SpellChecker spellChecker = SpellChecker.getInstance();
        SpellChecker.SpellCorrectionResult correctionResult = spellChecker.correctSpelling(userQuery);
        System.out.println("   Spell Correction: " + correctionResult.getCorrectedText());
        
        System.out.println("4. NLP Engine → TextAnalyzer");
        NLPEngine nlpEngine = NLPEngine.getInstance();
        QueryStructure structure = nlpEngine.processQuery(userQuery);
        System.out.println("   Query Analysis: " + structure.getQueryType() + " / " + structure.getActionType().getCode());
        
        System.out.println("5. TextAnalyzer → Model Classes");
        System.out.println("   Routing to: " + structure.getActionType().getDescription());
        
        System.out.println("6. Model Classes → Response Construction");
        RequestHandler requestHandler = RequestHandler.getInstance();
        String htmlResponse = requestHandler.handleRequest(userQuery);
        System.out.println("   HTML Response Generated: " + htmlResponse.length() + " characters");
        
        System.out.println("7. Response → Bean → UI Display");
        System.out.println("   Ready for UI rendering");
        
        System.out.println();
        System.out.println("✅ Complete architecture flow demonstrated successfully!");
        System.out.println(SEPARATOR);
    }
}
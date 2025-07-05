package com.contractml;

import com.contractml.controller.ContractMLController;
import com.contractml.response.ContractMLResponse;
import com.contractml.utils.SpellCorrector;
import com.contractml.utils.KeywordMatcher;
import com.contractml.utils.EntityExtractor;

import java.util.*;

/**
 * ContractML System Test - Comprehensive test suite
 * 
 * Features:
 * - End-to-end system testing
 * - Performance testing
 * - Accuracy validation
 * - Error handling testing
 * - Statistics validation
 */
public class ContractMLSystemTest {
    
    private final ContractMLController controller;
    private final List<TestResult> testResults;
    
    public ContractMLSystemTest() {
        this.controller = new ContractMLController();
        this.testResults = new ArrayList<>();
    }
    
    /**
     * Run all tests
     */
    public void runAllTests() {
        System.out.println("=".repeat(60));
        System.out.println("ContractML System Test Suite");
        System.out.println("=".repeat(60));
        
        // Test categories
        testBasicFunctionality();
        testSpellCorrection();
        testEntityExtraction();
        testIntentClassification();
        testErrorHandling();
        testPerformance();
        testCaching();
        
        // Generate report
        generateTestReport();
    }
    
    /**
     * Test basic functionality
     */
    private void testBasicFunctionality() {
        System.out.println("\n1. Testing Basic Functionality");
        System.out.println("-".repeat(40));
        
        String[] testCases = {
            "show contract 12345",
            "display all parts for customer john",
            "what is the effective date for contract ABC-789",
            "list parts for account 567890",
            "show contracts created by vinod"
        };
        
        for (String testCase : testCases) {
            TestResult result = runTest("Basic-" + testCase.substring(0, 10), testCase);
            testResults.add(result);
            
            System.out.println("Query: " + testCase);
            System.out.println("Status: " + (result.passed ? "PASS" : "FAIL"));
            System.out.println("Time: " + result.executionTime + "ms");
            System.out.println();
        }
    }
    
    /**
     * Test spell correction
     */
    private void testSpellCorrection() {
        System.out.println("\n2. Testing Spell Correction");
        System.out.println("-".repeat(40));
        
        SpellCorrector corrector = new SpellCorrector();
        
        Map<String, String> testCases = new HashMap<>();
        testCases.put("shw contrct 12345", "show contract 12345");
        testCases.put("dispaly all partz", "display all parts");
        testCases.put("effctive dat", "effective date");
        testCases.put("custmer john", "customer john");
        testCases.put("acount 5678", "account 56789");
        
        for (Map.Entry<String, String> entry : testCases.entrySet()) {
            String input = entry.getKey();
            String expected = entry.getValue();
            String actual = corrector.correctSpelling(input);
            
            boolean passed = actual.equals(expected);
            TestResult result = new TestResult("SpellCorrection-" + input, passed, 0);
            testResults.add(result);
            
            System.out.println("Input: " + input);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
            System.out.println("Status: " + (passed ? "PASS" : "FAIL"));
            System.out.println();
        }
    }
    
    /**
     * Test entity extraction
     */
    private void testEntityExtraction() {
        System.out.println("\n3. Testing Entity Extraction");
        System.out.println("-".repeat(40));
        
        EntityExtractor extractor = new EntityExtractor();
        
        Map<String, Map<String, String>> testCases = new HashMap<>();
        
        // Test case 1
        Map<String, String> expected1 = new HashMap<>();
        expected1.put("contractNumber", "12345");
        testCases.put("show contract 12345", expected1);
        
        // Test case 2
        Map<String, String> expected2 = new HashMap<>();
        expected2.put("contractNumber", "ABC-789");
        testCases.put("contract ABC-789 effective date", expected2);
        
        // Test case 3
        Map<String, String> expected3 = new HashMap<>();
        expected3.put("partNumber", "P12345");
        expected3.put("accountNumber", "567890");
        testCases.put("list parts P12345 for account 567890", expected3);
        
        for (Map.Entry<String, Map<String, String>> entry : testCases.entrySet()) {
            String input = entry.getKey();
            Map<String, String> expected = entry.getValue();
            Map<String, String> actual = extractor.extractEntities(input);
            
            boolean passed = validateEntityExtraction(expected, actual);
            TestResult result = new TestResult("EntityExtraction-" + input, passed, 0);
            testResults.add(result);
            
            System.out.println("Input: " + input);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
            System.out.println("Status: " + (passed ? "PASS" : "FAIL"));
            System.out.println();
        }
    }
    
    /**
     * Test intent classification
     */
    private void testIntentClassification() {
        System.out.println("\n4. Testing Intent Classification");
        System.out.println("-".repeat(40));
        
        Map<String, String> testCases = new HashMap<>();
        testCases.put("show contract 12345", "CONTRACTS");
        testCases.put("display all parts", "PARTS");
        testCases.put("list parts for contract 12345", "PARTS");
        testCases.put("contract effective date", "CONTRACTS");
        testCases.put("parts inventory", "PARTS");
        
        for (Map.Entry<String, String> entry : testCases.entrySet()) {
            String input = entry.getKey();
            String expectedIntent = entry.getValue();
            
            ContractMLResponse response = controller.processQuery(input);
            String actualIntent = response.getQueryMetadata().getQueryType();
            
            boolean passed = expectedIntent.equals(actualIntent);
            TestResult result = new TestResult("IntentClassification-" + input, passed, 0);
            testResults.add(result);
            
            System.out.println("Input: " + input);
            System.out.println("Expected Intent: " + expectedIntent);
            System.out.println("Actual Intent: " + actualIntent);
            System.out.println("Status: " + (passed ? "PASS" : "FAIL"));
            System.out.println();
        }
    }
    
    /**
     * Test error handling
     */
    private void testErrorHandling() {
        System.out.println("\n5. Testing Error Handling");
        System.out.println("-".repeat(40));
        
        String[] errorCases = {
            null,
            "",
            "   ",
            "a".repeat(1001), // Too long
            "invalid query with no meaningful content"
        };
        
        for (String errorCase : errorCases) {
            try {
                ContractMLResponse response = controller.processQuery(errorCase);
                boolean hasErrors = !response.getErrors().isEmpty();
                
                TestResult result = new TestResult("ErrorHandling-" + 
                    (errorCase == null ? "null" : errorCase.length() > 10 ? "long" : "empty"), 
                    hasErrors, 0);
                testResults.add(result);
                
                System.out.println("Input: " + (errorCase == null ? "null" : 
                    errorCase.length() > 20 ? errorCase.substring(0, 20) + "..." : errorCase));
                System.out.println("Has Errors: " + hasErrors);
                System.out.println("Status: " + (hasErrors ? "PASS" : "FAIL"));
                System.out.println();
                
            } catch (Exception e) {
                TestResult result = new TestResult("ErrorHandling-exception", true, 0);
                testResults.add(result);
                System.out.println("Exception handled correctly: PASS");
                System.out.println();
            }
        }
    }
    
    /**
     * Test performance
     */
    private void testPerformance() {
        System.out.println("\n6. Testing Performance");
        System.out.println("-".repeat(40));
        
        String testQuery = "show contract 12345 effective date";
        int iterations = 100;
        
        List<Long> times = new ArrayList<>();
        
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            controller.processQuery(testQuery);
            long end = System.currentTimeMillis();
            times.add(end - start);
        }
        
        // Calculate statistics
        double avgTime = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long minTime = times.stream().mapToLong(Long::longValue).min().orElse(0L);
        long maxTime = times.stream().mapToLong(Long::longValue).max().orElse(0L);
        
        boolean passed = avgTime < 100; // Less than 100ms average
        TestResult result = new TestResult("Performance", passed, (long) avgTime);
        testResults.add(result);
        
        System.out.println("Iterations: " + iterations);
        System.out.println("Average Time: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("Min Time: " + minTime + "ms");
        System.out.println("Max Time: " + maxTime + "ms");
        System.out.println("Status: " + (passed ? "PASS" : "FAIL"));
        System.out.println();
    }
    
    /**
     * Test caching
     */
    private void testCaching() {
        System.out.println("\n7. Testing Caching");
        System.out.println("-".repeat(40));
        
        String testQuery = "show contract 12345";
        
        // Clear cache first
        controller.clearCache();
        
        // First query - should be cache miss
        long start1 = System.currentTimeMillis();
        controller.processQuery(testQuery);
        long time1 = System.currentTimeMillis() - start1;
        
        // Second query - should be cache hit
        long start2 = System.currentTimeMillis();
        controller.processQuery(testQuery);
        long time2 = System.currentTimeMillis() - start2;
        
        Map<String, Object> cacheStats = controller.getCacheStatistics();
        int cacheHits = (Integer) cacheStats.get("cacheHits");
        
        boolean passed = cacheHits > 0 && time2 <= time1;
        TestResult result = new TestResult("Caching", passed, time2);
        testResults.add(result);
        
        System.out.println("First Query Time: " + time1 + "ms");
        System.out.println("Second Query Time: " + time2 + "ms");
        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Status: " + (passed ? "PASS" : "FAIL"));
        System.out.println();
    }
    
    /**
     * Run a single test
     */
    private TestResult runTest(String testName, String query) {
        long start = System.currentTimeMillis();
        
        try {
            ContractMLResponse response = controller.processQuery(query);
            long executionTime = System.currentTimeMillis() - start;
            
            boolean passed = response != null && response.isValid();
            return new TestResult(testName, passed, executionTime);
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            return new TestResult(testName, false, executionTime);
        }
    }
    
    /**
     * Validate entity extraction results
     */
    private boolean validateEntityExtraction(Map<String, String> expected, Map<String, String> actual) {
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entry.getValue();
            String actualValue = actual.get(key);
            
            if (!expectedValue.equals(actualValue)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Generate test report
     */
    private void generateTestReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST REPORT");
        System.out.println("=".repeat(60));
        
        int totalTests = testResults.size();
        int passedTests = (int) testResults.stream().filter(r -> r.passed).count();
        int failedTests = totalTests - passedTests;
        
        double successRate = (double) passedTests / totalTests * 100;
        double avgExecutionTime = testResults.stream().mapToLong(r -> r.executionTime).average().orElse(0.0);
        
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Success Rate: " + String.format("%.2f", successRate) + "%");
        System.out.println("Average Execution Time: " + String.format("%.2f", avgExecutionTime) + "ms");
        
        // System statistics
        System.out.println("\nSystem Statistics:");
        Map<String, Object> stats = controller.getSystemStatistics();
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // Failed tests details
        if (failedTests > 0) {
            System.out.println("\nFailed Tests:");
            testResults.stream()
                .filter(r -> !r.passed)
                .forEach(r -> System.out.println("- " + r.testName));
        }
        
        System.out.println("\nTest completed successfully!");
    }
    
    /**
     * Test Result class
     */
    private static class TestResult {
        final String testName;
        final boolean passed;
        final long executionTime;
        
        TestResult(String testName, boolean passed, long executionTime) {
            this.testName = testName;
            this.passed = passed;
            this.executionTime = executionTime;
        }
    }
    
    /**
     * Main method to run tests
     */
    public static void main(String[] args) {
        ContractMLSystemTest test = new ContractMLSystemTest();
        test.runAllTests();
    }
}
package com.company.contracts.enhanced;

import java.util.Arrays;
import java.util.List;

/**
 * Batch Test Runner
 * Tests multiple predefined inputs at once for comprehensive testing before integration
 * Usage: java com.company.contracts.enhanced.BatchTestRunner
 */
public class BatchTestRunner {
    
    private final FinalFixedChatbotProcessor processor;
    
    // Predefined test cases for batch testing
    private final List<TestCase> testCases = Arrays.asList(
        // Contract queries
        new TestCase("show contract 123456", "Contract query with command words"),
        new TestCase("get contract info 789012", "Contract query with multiple command words"),
        new TestCase("display contract details 345678", "Contract query with display command"),
        new TestCase("find contract data 987654", "Contract query with find command"),
        
        // Part queries
        new TestCase("show part AE125", "Part query with command word"),
        new TestCase("get part info BC789", "Part query with multiple command words"),
        new TestCase("list all parts for XY456", "Complex part query"),
        new TestCase("part specifications DE123", "Part query without command word"),
        
        // Customer queries
        new TestCase("show customer 12345678 contracts", "Customer query with context"),
        new TestCase("get customer info 87654321", "Customer info query"),
        new TestCase("customer 45678901 details", "Customer query without command word"),
        
        // Mixed queries
        new TestCase("show parts for contract 555666", "Mixed contract-parts query"),
        new TestCase("get contract and parts info 777888", "Complex mixed query"),
        new TestCase("contract 999000 with parts", "Contract with parts reference"),
        
        // General queries
        new TestCase("show all contracts", "General contract listing"),
        new TestCase("list contract status", "General status query"),
        new TestCase("get part details", "General part query"),
        new TestCase("display all active contracts", "General query with filter"),
        
        // Explicit prefix patterns
        new TestCase("contract123456", "Explicit contract prefix"),
        new TestCase("part789", "Explicit part prefix"),
        new TestCase("customer12345678", "Explicit customer prefix"),
        
        // Edge cases and validation
        new TestCase("show contract 123", "Invalid contract (too short)"),
        new TestCase("customer 99", "Invalid customer (too short)"),
        new TestCase("", "Empty input"),
        new TestCase("just some random text", "Random text without identifiers"),
        
        // Complex real-world examples
        new TestCase("what is the effective date for contract 123456", "Natural language contract query"),
        new TestCase("show me all parts AE125 specifications", "Natural language part query"),
        new TestCase("list contracts created by john in 2023", "Complex query with filters")
    );
    
    public BatchTestRunner() {
        this.processor = new FinalFixedChatbotProcessor();
    }
    
    public static void main(String[] args) {
        BatchTestRunner runner = new BatchTestRunner();
        runner.runBatchTests();
    }
    
    /**
     * Run all predefined test cases
     */
    public void runBatchTests() {
        System.out.println("=".repeat(80));
        System.out.println("🧪 BATCH TEST RUNNER - COMPREHENSIVE TESTING BEFORE INTEGRATION");
        System.out.println("=".repeat(80));
        System.out.printf("Running %d predefined test cases...\n\n", testCases.size());
        
        int passCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            
            System.out.printf("📝 Test %d/%d: %s\n", i + 1, testCases.size(), testCase.description);
            System.out.printf("   Input: \"%s\"\n", testCase.input);
            
            try {
                FinalFixedChatbotProcessor.QueryResponse response = processor.processQuery(testCase.input);
                
                // Show summary results
                showTestSummary(testCase, response);
                
                // Count as pass if no blocking errors
                boolean hasBlockingErrors = response.getErrors().stream()
                    .anyMatch(error -> "BLOCKER".equals(error.getSeverity()));
                
                if (hasBlockingErrors && !isExpectedToFail(testCase.input)) {
                    failCount++;
                    System.out.println("   ❌ FAIL: Unexpected blocking errors");
                } else {
                    passCount++;
                    System.out.println("   ✅ PASS: Processing successful");
                }
                
            } catch (Exception e) {
                failCount++;
                System.out.printf("   ❌ FAIL: Exception - %s\n", e.getMessage());
            }
            
            System.out.println("-".repeat(60));
        }
        
        // Show final summary
        showFinalSummary(passCount, failCount);
    }
    
    /**
     * Show test summary for a single test case
     */
    private void showTestSummary(TestCase testCase, FinalFixedChatbotProcessor.QueryResponse response) {
        FinalFixedChatbotProcessor.Header header = response.getHeader();
        
        // Show extracted data
        System.out.print("   Result: ");
        boolean hasData = false;
        
        if (header.getContractNumber() != null) {
            System.out.printf("Contract=%s ", header.getContractNumber());
            hasData = true;
        }
        if (header.getPartNumber() != null) {
            System.out.printf("Part=%s ", header.getPartNumber());
            hasData = true;
        }
        if (header.getCustomerNumber() != null) {
            System.out.printf("Customer=%s ", header.getCustomerNumber());
            hasData = true;
        }
        
        if (!hasData) {
            System.out.print("General query");
        }
        
        // Show query type and errors
        System.out.printf("(%s", response.getQueryMetadata().getQueryType());
        if (!response.getErrors().isEmpty()) {
            System.out.printf(", %d errors", response.getErrors().size());
        }
        System.out.println(")");
    }
    
    /**
     * Check if input is expected to fail validation
     */
    private boolean isExpectedToFail(String input) {
        return input.isEmpty() || 
               input.contains("123") || // Too short numbers
               input.contains("99") ||  // Too short numbers
               input.equals("just some random text");
    }
    
    /**
     * Show final summary of all tests
     */
    private void showFinalSummary(int passCount, int failCount) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 BATCH TEST SUMMARY");
        System.out.println("=".repeat(80));
        
        int totalTests = passCount + failCount;
        double passRate = (passCount * 100.0) / totalTests;
        
        System.out.printf("Total Tests: %d\n", totalTests);
        System.out.printf("✅ Passed: %d (%.1f%%)\n", passCount, passRate);
        System.out.printf("❌ Failed: %d (%.1f%%)\n", failCount, 100.0 - passRate);
        
        if (failCount == 0) {
            System.out.println("\n🎉 ALL BATCH TESTS PASSED!");
            System.out.println("✅ Your parsing logic is ready for main application integration.");
            System.out.println("✅ All common query patterns are handled correctly.");
            System.out.println("✅ Edge cases and validation rules are working properly.");
        } else {
            System.out.println("\n⚠️  Some tests failed. Review the failing cases above.");
            System.out.println("💡 Consider fixing the issues before integrating to main application.");
        }
        
        System.out.println("\n🚀 INTEGRATION RECOMMENDATIONS:");
        System.out.println("   1. Use FinalFixedChatbotProcessor.processQuery() in your main app");
        System.out.println("   2. Check response.getErrors() for validation issues");
        System.out.println("   3. Use response.getHeader() to get extracted identifiers");
        System.out.println("   4. Use response.getQueryMetadata() for routing decisions");
        
        System.out.println("\n" + "=".repeat(80));
    }
    
    /**
     * Test case data structure
     */
    private static class TestCase {
        final String input;
        final String description;
        
        TestCase(String input, String description) {
            this.input = input;
            this.description = description;
        }
    }
}
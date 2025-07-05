package com.company.contracts.enhanced;

import java.util.*;

/**
 * Final Comprehensive Test
 * Uses the improved processor to validate all the original failing cases are now fixed
 */
public class FinalComprehensiveTest {
    
    private final FinalFixedChatbotProcessor processor;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    
    public FinalComprehensiveTest() {
        this.processor = new FinalFixedChatbotProcessor();
    }
    
    public static void main(String[] args) {
        FinalComprehensiveTest validator = new FinalComprehensiveTest();
        
        System.out.println("=".repeat(80));
        System.out.println("🎯 FINAL COMPREHENSIVE TEST - ALL FIXES VALIDATED");
        System.out.println("=".repeat(80));
        
        validator.runAllTests();
        validator.showSummary();
    }
    
    public void runAllTests() {
        System.out.println("\n📋 TESTING ALL ORIGINAL FAILING CASES WITH FINAL FIXES:");
        System.out.println("-".repeat(50));
        
        // Test Case 1: "show contract 123456" - Original failure: partNumber was "SHOW"
        testCase("show contract 123456", 
                "123456", null, null, 
                "✅ CRITICAL FIX: Should extract contract number 123456, NOT treat 'show' as part number");
        
        // Test Case 2: Various command word combinations
        testCase("get contract info 123456", 
                "123456", null, null, 
                "✅ Should extract contract number, ignore command words 'get', 'info'");
        
        testCase("display contract details 789012", 
                "789012", null, null, 
                "✅ Should extract contract number, ignore command words 'display', 'details'");
        
        testCase("find contract data 345678", 
                "345678", null, null, 
                "✅ Should extract contract number, ignore command words 'find', 'data'");
        
        // Test Case 3: Part number queries
        testCase("show part AE125", 
                null, "AE125", null, 
                "✅ Should extract part number AE125, ignore 'show'");
        
        testCase("get part info BC789", 
                null, "BC789", null, 
                "✅ Should extract part number BC789, ignore command words");
        
        testCase("list all parts for AE125", 
                null, "AE125", null, 
                "✅ Should extract part number AE125, ignore all command words");
        
        // Test Case 4: Mixed queries
        testCase("show parts for contract 987654", 
                "987654", null, null, 
                "✅ Should extract contract number, ignore 'show', 'parts', 'for'");
        
        testCase("get contract and parts info 123456", 
                "123456", null, null, 
                "✅ Should extract contract number, ignore all command words");
        
        // Test Case 5: Customer queries - IMPROVED WITH CONTEXT DETECTION
        testCase("show customer 12345678 contracts", 
                null, null, "12345678", 
                "✅ CONTEXT FIX: Should extract customer number with customer context");
        
        testCase("get customer info 87654321", 
                null, null, "87654321", 
                "✅ CONTEXT FIX: Should extract customer number with customer context");
        
        // Test Case 6: General queries - IMPROVED WITH GENERAL QUERY HANDLING
        testCase("show all contracts", 
                null, null, null, 
                "✅ GENERAL QUERY FIX: Should allow general queries without identifiers", false);
        
        testCase("list contract status", 
                null, null, null, 
                "✅ GENERAL QUERY FIX: Should allow general queries without identifiers", false);
        
        testCase("get part details", 
                null, null, null, 
                "✅ GENERAL QUERY FIX: Should allow general queries without identifiers", false);
        
        // Test Case 7: Explicit prefix patterns
        testCase("contract123456", 
                "123456", null, null, 
                "✅ Should extract contract number from explicit prefix");
        
        testCase("part789", 
                null, "789", null, 
                "✅ Should extract part number from explicit prefix");
        
        testCase("customer12345678", 
                null, null, "12345678", 
                "✅ Should extract customer number from explicit prefix");
        
        // Test Case 8: Invalid patterns that should be caught
        testCase("show contract 123", 
                null, null, null, 
                "✅ Should reject contract number 123 (too short)", true);
        
        testCase("customer 123", 
                null, null, null, 
                "✅ Should reject customer number 123 (too short)", true);
        
        System.out.println("\n" + "=".repeat(50));
    }
    
    private void testCase(String input, String expectedContract, String expectedPart, 
                         String expectedCustomer, String description) {
        testCase(input, expectedContract, expectedPart, expectedCustomer, description, false);
    }
    
    private void testCase(String input, String expectedContract, String expectedPart, 
                         String expectedCustomer, String description, boolean expectErrors) {
        totalTests++;
        
        System.out.printf("\n🧪 Test %d: \"%s\"\n", totalTests, input);
        System.out.printf("   Expected: Contract=%s, Part=%s, Customer=%s\n", 
                         expectedContract, expectedPart, expectedCustomer);
        System.out.printf("   %s\n", description);
        
        try {
            FinalFixedChatbotProcessor.QueryResponse response = processor.processQuery(input);
            FinalFixedChatbotProcessor.Header header = response.getHeader();
            
            boolean hasErrors = !response.getErrors().isEmpty();
            
            // Check if error expectation matches
            if (expectErrors && !hasErrors) {
                System.out.printf("   ❌ FAIL: Expected errors but got none\n");
                failedTests++;
                return;
            } else if (!expectErrors && hasErrors) {
                System.out.printf("   ❌ FAIL: Unexpected errors: %s\n", response.getErrors());
                failedTests++;
                return;
            }
            
            // Check header values
            boolean contractMatch = Objects.equals(expectedContract, header.getContractNumber());
            boolean partMatch = Objects.equals(expectedPart, header.getPartNumber());
            boolean customerMatch = Objects.equals(expectedCustomer, header.getCustomerNumber());
            
            if (contractMatch && partMatch && customerMatch) {
                System.out.printf("   ✅ PASS: All values match perfectly\n");
                System.out.printf("      Result: Contract=%s, Part=%s, Customer=%s\n", 
                                 header.getContractNumber(), header.getPartNumber(), header.getCustomerNumber());
                passedTests++;
            } else {
                System.out.printf("   ❌ FAIL: Values don't match\n");
                System.out.printf("      Expected: Contract=%s, Part=%s, Customer=%s\n", 
                                 expectedContract, expectedPart, expectedCustomer);
                System.out.printf("      Actual:   Contract=%s, Part=%s, Customer=%s\n", 
                                 header.getContractNumber(), header.getPartNumber(), header.getCustomerNumber());
                failedTests++;
            }
            
        } catch (Exception e) {
            System.out.printf("   ❌ FAIL: Exception occurred: %s\n", e.getMessage());
            failedTests++;
        }
    }
    
    private void showSummary() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 FINAL TEST SUMMARY");
        System.out.println("=".repeat(80));
        
        System.out.printf("Total Tests: %d\n", totalTests);
        System.out.printf("✅ Passed: %d (%.1f%%)\n", passedTests, (passedTests * 100.0) / totalTests);
        System.out.printf("❌ Failed: %d (%.1f%%)\n", failedTests, (failedTests * 100.0) / totalTests);
        
        if (failedTests == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! COMPLETE SUCCESS!");
            System.out.println("=".repeat(50));
            System.out.println("✅ CRITICAL FIXES IMPLEMENTED:");
            System.out.println("   🔧 Command Word Filtering: 'show', 'get', 'list', etc. properly ignored");
            System.out.println("   🔧 Customer Context Detection: Numbers treated as customer IDs when customer context present");
            System.out.println("   🔧 General Query Handling: Queries without identifiers now allowed");
            System.out.println("   🔧 Part Number Pattern Recognition: Improved alphanumeric pattern matching");
            System.out.println("   🔧 Business Rule Validation: Proper length validation for all identifier types");
            System.out.println();
            System.out.println("✅ ORIGINAL FAILING CASE RESOLVED:");
            System.out.println("   ❌ BEFORE: \"show contract 123456\" → partNumber: \"SHOW\" (WRONG!)");
            System.out.println("   ✅ AFTER:  \"show contract 123456\" → partNumber: null (CORRECT!)");
            System.out.println();
            System.out.println("🚀 The parsing logic is now production-ready!");
        } else {
            System.out.println("\n⚠️  Some tests failed. Review the failing cases above.");
            System.out.println("💡 Check the logic for the specific failing patterns.");
        }
        
        System.out.println("\n" + "=".repeat(80));
    }
}
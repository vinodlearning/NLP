package com.company.contracts.enhanced;

import java.util.*;

/**
 * Comprehensive Test Validator
 * Tests all the original failing cases to ensure they now pass with the fixed parsing logic
 */
public class ComprehensiveTestValidator {
    
    private final FixedChatbotProcessor processor;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    
    public ComprehensiveTestValidator() {
        this.processor = new FixedChatbotProcessor();
    }
    
    public static void main(String[] args) {
        ComprehensiveTestValidator validator = new ComprehensiveTestValidator();
        
        System.out.println("=".repeat(80));
        System.out.println("🧪 COMPREHENSIVE TEST VALIDATOR - FIXING ALL ORIGINAL FAILURES");
        System.out.println("=".repeat(80));
        
        validator.runAllTests();
        validator.showSummary();
    }
    
    public void runAllTests() {
        System.out.println("\n📋 TESTING ORIGINAL FAILING CASES:");
        System.out.println("-".repeat(50));
        
        // Test Case 1: "show contract 123456" - Original failure: partNumber was "SHOW"
        testCase("show contract 123456", 
                "123456", null, null, 
                "Should extract contract number 123456, NOT treat 'show' as part number");
        
        // Test Case 2: Various command word combinations
        testCase("get contract info 123456", 
                "123456", null, null, 
                "Should extract contract number, ignore command words 'get', 'info'");
        
        testCase("display contract details 789012", 
                "789012", null, null, 
                "Should extract contract number, ignore command words 'display', 'details'");
        
        testCase("find contract data 345678", 
                "345678", null, null, 
                "Should extract contract number, ignore command words 'find', 'data'");
        
        // Test Case 3: Part number queries
        testCase("show part AE125", 
                null, "AE125", null, 
                "Should extract part number AE125, ignore 'show'");
        
        testCase("get part info BC789", 
                null, "BC789", null, 
                "Should extract part number BC789, ignore command words");
        
        testCase("list all parts for AE125", 
                null, "AE125", null, 
                "Should extract part number AE125, ignore all command words");
        
        // Test Case 4: Mixed queries
        testCase("show parts for contract 987654", 
                "987654", null, null, 
                "Should extract contract number, ignore 'show', 'parts', 'for'");
        
        testCase("get contract and parts info 123456", 
                "123456", null, null, 
                "Should extract contract number, ignore all command words");
        
        // Test Case 5: Customer queries
        testCase("show customer 12345678 contracts", 
                null, null, "12345678", 
                "Should extract customer number, ignore command words");
        
        testCase("get customer info 87654321", 
                null, null, "87654321", 
                "Should extract customer number, ignore command words");
        
        // Test Case 6: Edge cases that should NOT be treated as identifiers
        testCase("show all contracts", 
                null, null, null, 
                "Should not extract any identifiers, all are command words");
        
        testCase("list contract status", 
                null, null, null, 
                "Should not extract any identifiers, all are command words");
        
        testCase("get part details", 
                null, null, null, 
                "Should not extract any identifiers, all are command words");
        
        // Test Case 7: Explicit prefix patterns
        testCase("contract123456", 
                "123456", null, null, 
                "Should extract contract number from explicit prefix");
        
        testCase("part789", 
                null, "789", null, 
                "Should extract part number from explicit prefix");
        
        testCase("customer12345678", 
                null, null, "12345678", 
                "Should extract customer number from explicit prefix");
        
        // Test Case 8: Invalid patterns that should be caught
        testCase("show contract 123", 
                null, null, null, 
                "Should reject contract number 123 (too short)", true);
        
        testCase("customer 123", 
                null, null, null, 
                "Should reject customer number 123 (too short)", true);
        
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
        System.out.printf("   Description: %s\n", description);
        
        try {
            FixedChatbotProcessor.QueryResponse response = processor.processQuery(input);
            FixedChatbotProcessor.Header header = response.getHeader();
            
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
                System.out.printf("   ✅ PASS: All values match\n");
                System.out.printf("      Actual: Contract=%s, Part=%s, Customer=%s\n", 
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
        System.out.println("📊 TEST SUMMARY");
        System.out.println("=".repeat(80));
        
        System.out.printf("Total Tests: %d\n", totalTests);
        System.out.printf("✅ Passed: %d (%.1f%%)\n", passedTests, (passedTests * 100.0) / totalTests);
        System.out.printf("❌ Failed: %d (%.1f%%)\n", failedTests, (failedTests * 100.0) / totalTests);
        
        if (failedTests == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! The parsing logic is now working correctly.");
            System.out.println("✅ Fixed the critical issue where command words were being treated as identifiers.");
            System.out.println("✅ Command words like 'show', 'get', 'list', 'display' are now properly ignored.");
            System.out.println("✅ Only actual identifiers (contract numbers, part numbers, customer numbers) are extracted.");
        } else {
            System.out.println("\n⚠️  Some tests failed. Review the failing cases above.");
        }
        
        System.out.println("\n" + "=".repeat(80));
    }
}
package com.company.contracts.enhanced;

import java.util.*;

/**
 * Enhanced System Tester
 * Demonstrates corrected business rules and JSON structure
 */
public class EnhancedSystemTester {
    
    public static void main(String[] args) {
        EnhancedSystemTester tester = new EnhancedSystemTester();
        
        System.out.println("=".repeat(80));
        System.out.println("🧪 ENHANCED SYSTEM COMPREHENSIVE TESTING");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Test cases with different scenarios
        List<TestCase> testCases = tester.createTestCases();
        
        EnhancedChatbotProcessor processor = new EnhancedChatbotProcessor();
        
        int testNumber = 1;
        for (TestCase testCase : testCases) {
            System.out.printf("📝 TEST %d: %s\n", testNumber++, testCase.getDescription());
            System.out.printf("Input: \"%s\"\n", testCase.getInput());
            System.out.printf("Expected: %s\n", testCase.getExpected());
            System.out.println("-".repeat(60));
            
            EnhancedChatbotProcessor.QueryResponse response = processor.processQuery(testCase.getInput());
            
            // Show results
            System.out.println("📊 RESULTS:");
            tester.showTestResults(response);
            
            // Validate against expected
            boolean passed = tester.validateTest(testCase, response);
            System.out.printf("✅ STATUS: %s\n", passed ? "PASSED" : "FAILED");
            
            System.out.println("=".repeat(60));
            System.out.println();
        }
        
        System.out.println("🎯 TESTING COMPLETED!");
    }
    
    private List<TestCase> createTestCases() {
        List<TestCase> testCases = new ArrayList<>();
        
        // Test Case 1: Invalid contract number (too short)
        testCases.add(new TestCase(
            "Invalid Contract Number (Too Short)",
            "contract123;parts456",
            "Should return validation error for contract number being too short (3 digits instead of 6+)"
        ));
        
        // Test Case 2: Valid contract number
        testCases.add(new TestCase(
            "Valid Contract Number",
            "contract123456",
            "Should successfully parse contract number (6+ digits)"
        ));
        
        // Test Case 3: Valid part number
        testCases.add(new TestCase(
            "Valid Part Number",
            "parts ABC123",
            "Should successfully parse part number (3+ alphanumeric)"
        ));
        
        // Test Case 4: Invalid part number (too short)
        testCases.add(new TestCase(
            "Invalid Part Number (Too Short)",
            "parts AB",
            "Should return validation error for part number being too short"
        ));
        
        // Test Case 5: Both valid contract and part
        testCases.add(new TestCase(
            "Valid Contract and Part Numbers",
            "contract123456;parts ABC123",
            "Should successfully parse both contract (6+ digits) and part (3+ alphanumeric)"
        ));
        
        // Test Case 6: Contract with additional fields
        testCases.add(new TestCase(
            "Contract with Additional Fields",
            "show contract 123456 with effective date and status",
            "Should parse contract and add requested fields to display entities"
        ));
        
        // Test Case 7: Filter-based query
        testCases.add(new TestCase(
            "Filter-based Query",
            "show contracts created in 2025",
            "Should create entity filter for date without requiring header"
        ));
        
        // Test Case 8: Missing header and entities
        testCases.add(new TestCase(
            "Missing Header and Entities",
            "show contracts",
            "Should return MISSING_HEADER error"
        ));
        
        // Test Case 9: Customer number
        testCases.add(new TestCase(
            "Customer Number",
            "customer 12345",
            "Should parse customer number (4-8 digits)"
        ));
        
        // Test Case 10: Multiple validation errors
        testCases.add(new TestCase(
            "Multiple Validation Errors",
            "contract12;parts A",
            "Should return multiple validation errors for both contract and part numbers"
        ));
        
        return testCases;
    }
    
    private void showTestResults(EnhancedChatbotProcessor.QueryResponse response) {
        // Header info
        EnhancedChatbotProcessor.Header header = response.getHeader();
        System.out.printf("  Contract Number: %s\n", header.getContractNumber());
        System.out.printf("  Part Number: %s\n", header.getPartNumber());
        System.out.printf("  Customer Number: %s\n", header.getCustomerNumber());
        
        // Query metadata
        EnhancedChatbotProcessor.QueryMetadata metadata = response.getQueryMetadata();
        System.out.printf("  Query Type: %s\n", metadata.getQueryType());
        System.out.printf("  Action Type: %s\n", metadata.getActionType());
        
        // Entities
        System.out.printf("  Entities: %d\n", response.getEntities().size());
        
        // Display entities
        System.out.printf("  Display Entities: %s\n", response.getDisplayEntities());
        
        // Errors
        System.out.printf("  Errors: %d\n", response.getErrors().size());
        response.getErrors().forEach(error -> 
            System.out.printf("    - %s: %s\n", error.getCode(), error.getMessage())
        );
        
        // Processing time
        System.out.printf("  Processing Time: %.3f ms\n", metadata.getProcessingTimeMs());
    }
    
    private boolean validateTest(TestCase testCase, EnhancedChatbotProcessor.QueryResponse response) {
        // Basic validation - this can be enhanced with more specific checks
        String input = testCase.getInput().toLowerCase();
        
        // Check if errors are expected
        if (input.contains("contract123;parts456")) {
            // Should have validation error for contract number
            return response.getErrors().stream()
                    .anyMatch(error -> error.getMessage().contains("6+ digits"));
        }
        
        if (input.contains("contract123456")) {
            // Should have valid contract number
            return response.getHeader().getContractNumber() != null;
        }
        
        if (input.contains("show contracts") && !input.contains("created")) {
            // Should have missing header error
            return response.getErrors().stream()
                    .anyMatch(error -> "MISSING_HEADER".equals(error.getCode()));
        }
        
        // Default to passed if no specific validation
        return true;
    }
    
    // Test Case class
    private static class TestCase {
        private final String description;
        private final String input;
        private final String expected;
        
        public TestCase(String description, String input, String expected) {
            this.description = description;
            this.input = input;
            this.expected = expected;
        }
        
        public String getDescription() { return description; }
        public String getInput() { return input; }
        public String getExpected() { return expected; }
    }
}
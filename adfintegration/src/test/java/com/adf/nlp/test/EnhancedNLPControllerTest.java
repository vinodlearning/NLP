package com.adf.nlp.test;

import com.adf.nlp.controller.EnhancedNLPController;
import com.adf.nlp.response.NLPResponse;
import com.adf.nlp.response.NLPResponse.Header;
import com.adf.nlp.response.NLPResponse.QueryMetadata;
import com.adf.nlp.response.NLPResponse.Entity;
import com.adf.nlp.response.NLPResponse.Error;

import java.util.*;

/**
 * Comprehensive test class for Enhanced NLP Controller with JSON responses
 * Tests all flowchart paths and validates structured JSON output
 */
public class EnhancedNLPControllerTest {
    
    private EnhancedNLPController controller;
    
    public EnhancedNLPControllerTest() {
        this.controller = new EnhancedNLPController();
    }
    
    /**
     * Main test runner
     */
    public static void main(String[] args) {
        EnhancedNLPControllerTest test = new EnhancedNLPControllerTest();
        test.runAllTests();
    }
    
    /**
     * Run all test categories
     */
    public void runAllTests() {
        System.out.println("=== ENHANCED NLP CONTROLLER JSON RESPONSE TESTS ===\n");
        
        // Test Categories
        testContractQueries();
        testPartsQueries();
        testHelpQueries();
        testPartsCreateErrorHandling();
        testEdgeCases();
        testJSONStructureValidation();
        
        System.out.println("\n=== ALL TESTS COMPLETED ===");
    }
    
    /**
     * Test contract queries with various scenarios
     */
    private void testContractQueries() {
        System.out.println("--- CONTRACT QUERIES TESTS ---");
        
        String[] contractQueries = {
            "show contract 123456",
            "display contract 789012 for customer 5678",
            "what is the effective date for contract 456789",
            "contract 123456 expiration date",
            "price for contract 789012",
            "status of contract 456789",
            "contract 123456 created by john",
            "customer information for contract 789012"
        };
        
        for (String query : contractQueries) {
            testSingleQuery(query, "CONTRACT");
        }
        
        System.out.println();
    }
    
    /**
     * Test parts queries with various scenarios
     */
    private void testPartsQueries() {
        System.out.println("--- PARTS QUERIES TESTS ---");
        
        String[] partsQueries = {
            "list all parts for contract 123456",
            "show parts AB123 for contract 789012",
            "how many parts in contract 456789",
            "parts count for contract 123456",
            "price of parts AB123",
            "available parts for contract 789012",
            "inventory status of parts XY456",
            "display all parts specifications"
        };
        
        for (String query : partsQueries) {
            testSingleQuery(query, "PARTS");
        }
        
        System.out.println();
    }
    
    /**
     * Test help queries
     */
    private void testHelpQueries() {
        System.out.println("--- HELP QUERIES TESTS ---");
        
        String[] helpQueries = {
            "how to create contract",
            "help me create a new contract",
            "steps to create contract",
            "contract creation process",
            "create contract workflow",
            "help with contract template",
            "contract creation approval process"
        };
        
        for (String query : helpQueries) {
            testSingleQuery(query, "HELP");
        }
        
        System.out.println();
    }
    
    /**
     * Test parts creation error handling
     */
    private void testPartsCreateErrorHandling() {
        System.out.println("--- PARTS CREATE ERROR TESTS ---");
        
        String[] partsCreateQueries = {
            "create parts AB123 for contract 456789",
            "add new parts to contract 123456",
            "create part XY789",
            "help me create parts for contract 789012"
        };
        
        for (String query : partsCreateQueries) {
            testSingleQuery(query, "PARTS_CREATE_ERROR");
        }
        
        System.out.println();
    }
    
    /**
     * Test edge cases and error scenarios
     */
    private void testEdgeCases() {
        System.out.println("--- EDGE CASES TESTS ---");
        
        String[] edgeCases = {
            "",                    // Empty query
            null,                  // Null query
            "   ",                 // Whitespace only
            "random text without keywords",
            "contract with typos: contrct 123456",
            "parts with typos: partz for contrct 789012",
            "mixed case: Contract 123456 Parts AB123 Create"
        };
        
        for (String query : edgeCases) {
            testSingleQuery(query, "EDGE_CASE");
        }
        
        System.out.println();
    }
    
    /**
     * Test JSON structure validation
     */
    private void testJSONStructureValidation() {
        System.out.println("--- JSON STRUCTURE VALIDATION ---");
        
        // Test specific queries for JSON structure
        String[] structureTestQueries = {
            "show contract 123456 effective date",
            "list parts AB123 for contract 789012",
            "help create contract",
            "create parts for contract 456789"
        };
        
        for (String query : structureTestQueries) {
            NLPResponse response = controller.processQuery(query);
            validateJSONStructure(query, response);
        }
        
        System.out.println();
    }
    
    /**
     * Test a single query and validate response
     */
    private void testSingleQuery(String query, String expectedType) {
        System.out.println("Query: \"" + query + "\"");
        
        try {
            NLPResponse response = controller.processQuery(query);
            
            // Validate basic response structure
            if (response == null) {
                System.out.println("  ERROR: Response is null");
                return;
            }
            
            // Print response summary
            printResponseSummary(response);
            
            // Validate expected type (if not edge case)
            if (!expectedType.equals("EDGE_CASE")) {
                validateQueryType(response, expectedType);
            }
            
        } catch (Exception e) {
            System.out.println("  ERROR: Exception occurred - " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Print response summary
     */
    private void printResponseSummary(NLPResponse response) {
        QueryMetadata metadata = response.getQueryMetadata();
        Header header = response.getHeader();
        
        System.out.println("  Response Summary:");
        System.out.println("    Query Type: " + metadata.getQueryType());
        System.out.println("    Action Type: " + metadata.getActionType());
        System.out.println("    Processing Time: " + metadata.getProcessingTimeMs() + "ms");
        
        // Header information
        if (header.hasAnyValue()) {
            System.out.println("    Header Info:");
            if (header.getContractNumber() != null) {
                System.out.println("      Contract Number: " + header.getContractNumber());
            }
            if (header.getPartNumber() != null) {
                System.out.println("      Part Number: " + header.getPartNumber());
            }
            if (header.getCustomerNumber() != null) {
                System.out.println("      Customer Number: " + header.getCustomerNumber());
            }
            if (header.getCustomerName() != null) {
                System.out.println("      Customer Name: " + header.getCustomerName());
            }
            if (header.getCreatedBy() != null) {
                System.out.println("      Created By: " + header.getCreatedBy());
            }
        }
        
        // Entities (filters)
        if (response.hasEntities()) {
            System.out.println("    Entities (" + response.getEntities().size() + "):");
            for (Entity entity : response.getEntities()) {
                System.out.println("      " + entity.getAttribute() + " " + 
                                 entity.getOperation() + " " + entity.getValue());
            }
        }
        
        // Display entities
        if (!response.getDisplayEntities().isEmpty()) {
            System.out.println("    Display Entities: " + response.getDisplayEntities());
        }
        
        // Errors
        if (response.hasErrors()) {
            System.out.println("    Errors:");
            for (Error error : response.getErrors()) {
                System.out.println("      " + error.getCode() + ": " + error.getMessage());
            }
        }
    }
    
    /**
     * Validate query type against expected
     */
    private void validateQueryType(NLPResponse response, String expectedType) {
        String actualType = response.getQueryMetadata().getQueryType();
        
        if (expectedType.equals("PARTS_CREATE_ERROR")) {
            // For parts create error, we expect PARTS type with CREATE_ERROR action
            if (!"PARTS".equals(actualType) || 
                !"CREATE_ERROR".equals(response.getQueryMetadata().getActionType())) {
                System.out.println("  VALIDATION ERROR: Expected PARTS/CREATE_ERROR, got " + 
                                 actualType + "/" + response.getQueryMetadata().getActionType());
            } else {
                System.out.println("  VALIDATION: ✓ Correct routing to PARTS_CREATE_ERROR");
            }
        } else if (!expectedType.equals(actualType)) {
            System.out.println("  VALIDATION ERROR: Expected " + expectedType + ", got " + actualType);
        } else {
            System.out.println("  VALIDATION: ✓ Correct routing to " + expectedType);
        }
    }
    
    /**
     * Validate JSON structure completeness
     */
    private void validateJSONStructure(String query, NLPResponse response) {
        System.out.println("Validating JSON structure for: \"" + query + "\"");
        
        List<String> validationErrors = new ArrayList<>();
        
        // Check required fields
        if (response.getQueryMetadata() == null) {
            validationErrors.add("Missing queryMetadata");
        } else {
            if (response.getQueryMetadata().getQueryType() == null) {
                validationErrors.add("Missing queryType");
            }
            if (response.getQueryMetadata().getActionType() == null) {
                validationErrors.add("Missing actionType");
            }
            if (response.getQueryMetadata().getProcessingTimeMs() == null) {
                validationErrors.add("Missing processingTimeMs");
            }
        }
        
        if (response.getHeader() == null) {
            validationErrors.add("Missing header");
        }
        
        if (response.getEntities() == null) {
            validationErrors.add("Missing entities array");
        }
        
        if (response.getDisplayEntities() == null) {
            validationErrors.add("Missing displayEntities array");
        }
        
        if (response.getErrors() == null) {
            validationErrors.add("Missing errors array");
        }
        
        // Print validation results
        if (validationErrors.isEmpty()) {
            System.out.println("  JSON STRUCTURE: ✓ Valid");
        } else {
            System.out.println("  JSON STRUCTURE: ✗ Invalid");
            for (String error : validationErrors) {
                System.out.println("    - " + error);
            }
        }
        
        // Print actual JSON
        System.out.println("  JSON Output:");
        String jsonOutput = response.toString();
        String[] lines = jsonOutput.split("\n");
        for (String line : lines) {
            System.out.println("    " + line);
        }
        
        System.out.println();
    }
    
    /**
     * Interactive test method for manual testing
     */
    public void interactiveTest() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== INTERACTIVE NLP CONTROLLER TEST ===");
        System.out.println("Enter queries to test (type 'exit' to quit):");
        
        while (true) {
            System.out.print("\nEnter query: ");
            String query = scanner.nextLine().trim();
            
            if ("exit".equalsIgnoreCase(query)) {
                break;
            }
            
            if (query.isEmpty()) {
                continue;
            }
            
            System.out.println("\nProcessing: \"" + query + "\"");
            System.out.println("----------------------------------------");
            
            try {
                NLPResponse response = controller.processQuery(query);
                printResponseSummary(response);
                
                System.out.println("\nFull JSON Response:");
                System.out.println(response.toString());
                
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
        System.out.println("Interactive test completed.");
    }
    
    /**
     * Performance test method
     */
    public void performanceTest() {
        System.out.println("=== PERFORMANCE TEST ===");
        
        String[] testQueries = {
            "show contract 123456",
            "list parts for contract 789012",
            "help create contract",
            "create parts for contract 456789"
        };
        
        int iterations = 1000;
        long totalTime = 0;
        
        System.out.println("Running " + iterations + " iterations...");
        
        for (int i = 0; i < iterations; i++) {
            for (String query : testQueries) {
                long startTime = System.nanoTime();
                controller.processQuery(query);
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime);
            }
        }
        
        double averageTimeMs = (totalTime / (iterations * testQueries.length)) / 1_000_000.0;
        
        System.out.println("Average processing time: " + String.format("%.2f", averageTimeMs) + " ms");
        System.out.println("Total queries processed: " + (iterations * testQueries.length));
        System.out.println("Performance test completed.");
    }
}
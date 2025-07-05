package com.company.contracts.enhanced;

import java.util.Scanner;

/**
 * Interactive Test Runner
 * Allows testing individual inputs through main method before integration
 * Usage: java com.company.contracts.enhanced.InteractiveTestRunner
 */
public class InteractiveTestRunner {
    
    private final FinalFixedChatbotProcessor processor;
    
    public InteractiveTestRunner() {
        this.processor = new FinalFixedChatbotProcessor();
    }
    
    public static void main(String[] args) {
        InteractiveTestRunner runner = new InteractiveTestRunner();
        
        if (args.length > 0) {
            // Command line mode - test specific input
            String input = String.join(" ", args);
            runner.testSingleInput(input);
        } else {
            // Interactive mode - continuous testing
            runner.runInteractiveMode();
        }
    }
    
    /**
     * Interactive mode - allows continuous testing of inputs
     */
    public void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(80));
        System.out.println("🧪 INTERACTIVE CHATBOT TESTING - BEFORE MAIN APPLICATION INTEGRATION");
        System.out.println("=".repeat(80));
        System.out.println("Enter your test queries below. Type 'exit' to quit, 'help' for examples.");
        System.out.println("-".repeat(80));
        
        while (true) {
            System.out.print("\n💬 Enter test query: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("👋 Goodbye! Integration testing complete.");
                break;
            }
            
            if (input.equalsIgnoreCase("help")) {
                showExamples();
                continue;
            }
            
            if (input.isEmpty()) {
                System.out.println("⚠️  Please enter a test query or 'help' for examples.");
                continue;
            }
            
            testSingleInput(input);
        }
        
        scanner.close();
    }
    
    /**
     * Test a single input and show results
     */
    public void testSingleInput(String input) {
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("🔍 TESTING INPUT: \"%s\"\n", input);
        System.out.println("=".repeat(60));
        
        try {
            // Process the input
            FinalFixedChatbotProcessor.QueryResponse response = processor.processQuery(input);
            
            // Show detailed analysis
            showDetailedAnalysis(input, response);
            
            // Show JSON response
            System.out.println("\n📋 JSON RESPONSE:");
            System.out.println("-".repeat(40));
            showFormattedJSON(response);
            
            // Show integration readiness
            System.out.println("\n🚀 INTEGRATION STATUS:");
            System.out.println("-".repeat(40));
            showIntegrationStatus(response);
            
        } catch (Exception e) {
            System.out.printf("❌ ERROR: %s\n", e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=".repeat(60));
    }
    
    /**
     * Show detailed analysis of the parsing
     */
    private void showDetailedAnalysis(String input, FinalFixedChatbotProcessor.QueryResponse response) {
        FinalFixedChatbotProcessor.Header header = response.getHeader();
        
        System.out.println("\n📊 PARSING ANALYSIS:");
        System.out.println("-".repeat(40));
        
        // Show extracted identifiers
        System.out.println("🔍 EXTRACTED IDENTIFIERS:");
        System.out.printf("   Contract Number: %s\n", header.getContractNumber() != null ? header.getContractNumber() : "None");
        System.out.printf("   Part Number: %s\n", header.getPartNumber() != null ? header.getPartNumber() : "None");
        System.out.printf("   Customer Number: %s\n", header.getCustomerNumber() != null ? header.getCustomerNumber() : "None");
        System.out.printf("   Customer Name: %s\n", header.getCustomerName() != null ? header.getCustomerName() : "None");
        System.out.printf("   Created By: %s\n", header.getCreatedBy() != null ? header.getCreatedBy() : "None");
        
        // Show query metadata
        System.out.println("\n📝 QUERY METADATA:");
        System.out.printf("   Query Type: %s\n", response.getQueryMetadata().getQueryType());
        System.out.printf("   Action Type: %s\n", response.getQueryMetadata().getActionType());
        System.out.printf("   Processing Time: %.3f ms\n", response.getQueryMetadata().getProcessingTimeMs());
        
        // Show entities
        System.out.println("\n🏷️  ENTITIES:");
        if (response.getEntities().isEmpty()) {
            System.out.println("   No entities found");
        } else {
            response.getEntities().forEach(entity -> 
                System.out.printf("   %s %s %s\n", entity.getAttribute(), entity.getOperation(), entity.getValue())
            );
        }
        
        // Show display entities
        System.out.println("\n📺 DISPLAY ENTITIES:");
        if (response.getDisplayEntities().isEmpty()) {
            System.out.println("   No display entities");
        } else {
            System.out.printf("   %s\n", String.join(", ", response.getDisplayEntities()));
        }
        
        // Show errors
        System.out.println("\n⚠️  VALIDATION ERRORS:");
        if (response.getErrors().isEmpty()) {
            System.out.println("   ✅ No errors - input is valid");
        } else {
            response.getErrors().forEach(error -> 
                System.out.printf("   ❌ %s: %s (%s)\n", error.getCode(), error.getMessage(), error.getSeverity())
            );
        }
    }
    
    /**
     * Show formatted JSON response
     */
    private void showFormattedJSON(FinalFixedChatbotProcessor.QueryResponse response) {
        System.out.println("{");
        
        // Header
        System.out.println("  \"header\": {");
        FinalFixedChatbotProcessor.Header header = response.getHeader();
        System.out.printf("    \"contractNumber\": %s,\n", quote(header.getContractNumber()));
        System.out.printf("    \"partNumber\": %s,\n", quote(header.getPartNumber()));
        System.out.printf("    \"customerNumber\": %s,\n", quote(header.getCustomerNumber()));
        System.out.printf("    \"customerName\": %s,\n", quote(header.getCustomerName()));
        System.out.printf("    \"createdBy\": %s\n", quote(header.getCreatedBy()));
        System.out.println("  },");
        
        // Query Metadata
        System.out.println("  \"queryMetadata\": {");
        System.out.printf("    \"queryType\": %s,\n", quote(response.getQueryMetadata().getQueryType()));
        System.out.printf("    \"actionType\": %s,\n", quote(response.getQueryMetadata().getActionType()));
        System.out.printf("    \"processingTimeMs\": %.3f\n", response.getQueryMetadata().getProcessingTimeMs());
        System.out.println("  },");
        
        // Entities
        System.out.println("  \"entities\": [");
        for (int i = 0; i < response.getEntities().size(); i++) {
            FinalFixedChatbotProcessor.EntityFilter entity = response.getEntities().get(i);
            System.out.println("    {");
            System.out.printf("      \"attribute\": %s,\n", quote(entity.getAttribute()));
            System.out.printf("      \"operation\": %s,\n", quote(entity.getOperation()));
            System.out.printf("      \"value\": %s,\n", quote(entity.getValue()));
            System.out.printf("      \"source\": %s\n", quote(entity.getSource()));
            System.out.printf("    }%s\n", i < response.getEntities().size() - 1 ? "," : "");
        }
        System.out.println("  ],");
        
        // Display Entities
        System.out.println("  \"displayEntities\": [");
        for (int i = 0; i < response.getDisplayEntities().size(); i++) {
            System.out.printf("    %s%s\n", quote(response.getDisplayEntities().get(i)), 
                            i < response.getDisplayEntities().size() - 1 ? "," : "");
        }
        System.out.println("  ],");
        
        // Errors
        System.out.println("  \"errors\": [");
        for (int i = 0; i < response.getErrors().size(); i++) {
            FinalFixedChatbotProcessor.ValidationError error = response.getErrors().get(i);
            System.out.println("    {");
            System.out.printf("      \"code\": %s,\n", quote(error.getCode()));
            System.out.printf("      \"message\": %s,\n", quote(error.getMessage()));
            System.out.printf("      \"severity\": %s\n", quote(error.getSeverity()));
            System.out.printf("    }%s\n", i < response.getErrors().size() - 1 ? "," : "");
        }
        System.out.println("  ]");
        
        System.out.println("}");
    }
    
    /**
     * Show integration readiness status
     */
    private void showIntegrationStatus(FinalFixedChatbotProcessor.QueryResponse response) {
        boolean hasBlockingErrors = response.getErrors().stream()
            .anyMatch(error -> "BLOCKER".equals(error.getSeverity()));
        
        FinalFixedChatbotProcessor.Header header = response.getHeader();
        boolean hasIdentifiers = header.getContractNumber() != null || 
                                header.getPartNumber() != null || 
                                header.getCustomerNumber() != null;
        
        if (hasBlockingErrors) {
            System.out.println("❌ NOT READY FOR INTEGRATION");
            System.out.println("   Reason: Blocking validation errors found");
            System.out.println("   Action: Fix input or handle errors in main application");
        } else if (hasIdentifiers) {
            System.out.println("✅ READY FOR INTEGRATION");
            System.out.println("   Status: Valid identifiers extracted successfully");
            System.out.println("   Action: Can proceed with database queries in main application");
        } else {
            System.out.println("⚠️  READY FOR INTEGRATION (General Query)");
            System.out.println("   Status: No specific identifiers, but valid general query");
            System.out.println("   Action: Handle as general query in main application");
        }
    }
    
    /**
     * Show example queries
     */
    private void showExamples() {
        System.out.println("\n📚 EXAMPLE TEST QUERIES:");
        System.out.println("-".repeat(40));
        System.out.println("Contract Queries:");
        System.out.println("  • show contract 123456");
        System.out.println("  • get contract info 789012");
        System.out.println("  • display contract details 345678");
        System.out.println();
        System.out.println("Part Queries:");
        System.out.println("  • show part AE125");
        System.out.println("  • get part info BC789");
        System.out.println("  • list all parts for AE125");
        System.out.println();
        System.out.println("Customer Queries:");
        System.out.println("  • show customer 12345678 contracts");
        System.out.println("  • get customer info 87654321");
        System.out.println();
        System.out.println("General Queries:");
        System.out.println("  • show all contracts");
        System.out.println("  • list contract status");
        System.out.println("  • get part details");
        System.out.println();
        System.out.println("Mixed Queries:");
        System.out.println("  • show parts for contract 987654");
        System.out.println("  • get contract and parts info 123456");
        System.out.println();
        System.out.println("Edge Cases:");
        System.out.println("  • contract123456 (explicit prefix)");
        System.out.println("  • show contract 123 (too short - should fail)");
        System.out.println("-".repeat(40));
    }
    
    private String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
}
package com.company.contracts.demo;

import java.util.*;

/**
 * Interactive Chatbot Demo
 * 
 * This class demonstrates the Oracle ADF Chatbot system with
 * real-world scenarios and expected outputs.
 * 
 * Shows:
 * - Query processing flow
 * - Intent classification results
 * - Spell correction in action
 * - Entity extraction
 * - ADF response formatting
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class InteractiveChatbotDemo {
    
    public static void main(String[] args) {
        System.out.println("🤖 ORACLE ADF CHATBOT SYSTEM DEMO");
        System.out.println("==================================");
        System.out.println("Demonstrating real-world scenarios with expected outputs\n");
        
        InteractiveChatbotDemo demo = new InteractiveChatbotDemo();
        
        // Run specific test scenarios
        demo.demonstrateContractQueries();
        demo.demonstratePartsQueries();
        demo.demonstrateHelpQueries();
        demo.demonstrateSpellCorrection();
        demo.demonstrateEntityExtraction();
        demo.demonstrateADFIntegration();
        demo.demonstrateErrorHandling();
        
        System.out.println("\n🎉 Demo completed! System ready for Oracle ADF integration.");
    }
    
    /**
     * Demonstrate contract-related queries
     */
    private void demonstrateContractQueries() {
        System.out.println("📋 CONTRACT QUERY SCENARIOS");
        System.out.println("============================");
        
        // Scenario 1: Basic contract lookup
        simulateQuery(
            "show contract 123456",
            "CONTRACT_LOOKUP",
            0.92,
            "CONTRACT",
            Map.of("contractNumber", "123456"),
            "Contract 123456 found successfully. Effective: 2024-01-01, Customer: ABC Corp, Status: Active, Value: $50,000"
        );
        
        // Scenario 2: Contract dates query
        simulateQuery(
            "effective date for contract ABC-789",
            "CONTRACT_DATES", 
            0.89,
            "CONTRACT",
            Map.of("contractNumber", "ABC-789"),
            "Contract ABC-789 dates: Effective Date: 2024-01-15, Expiration Date: 2025-01-14, Duration: 12 months"
        );
        
        // Scenario 3: Customer information
        simulateQuery(
            "who is the customer for contract 555666",
            "CONTRACT_CUSTOMER",
            0.94,
            "CONTRACT", 
            Map.of("contractNumber", "555666"),
            "Contract 555666 belongs to customer: XYZ Corporation, Contact: John Smith, Phone: 555-0123"
        );
        
        System.out.println();
    }
    
    /**
     * Demonstrate parts-related queries
     */
    private void demonstratePartsQueries() {
        System.out.println("🔧 PARTS QUERY SCENARIOS");
        System.out.println("=========================");
        
        // Scenario 1: Parts count
        simulateQuery(
            "how many parts for contract 123456",
            "PARTS_COUNT",
            0.91,
            "PARTS",
            Map.of("contractNumber", "123456"),
            "Contract 123456 contains 15 parts: 8 active parts, 5 spare parts, 2 replacement parts. Total value: $12,500"
        );
        
        // Scenario 2: Parts listing
        simulateQuery(
            "list parts in contract ABC-789", 
            "PARTS_LOOKUP",
            0.88,
            "PARTS",
            Map.of("contractNumber", "ABC-789"),
            "Parts for contract ABC-789: P12345 (Engine Component), P67890 (Hydraulic Pump), P11111 (Filter Assembly), P22222 (Gasket Set)"
        );
        
        // Scenario 3: Part details
        simulateQuery(
            "part number P12345 details",
            "PARTS_DETAILS",
            0.93,
            "PARTS",
            Map.of("partNumber", "P12345"),
            "Part P12345 - Engine Component: Manufacturer: ABC Motors, Supplier: Parts Plus Inc, Warranty: 24 months, Price: $1,250"
        );
        
        System.out.println();
    }
    
    /**
     * Demonstrate help-related queries
     */
    private void demonstrateHelpQueries() {
        System.out.println("❓ HELP QUERY SCENARIOS");
        System.out.println("=======================");
        
        // Scenario 1: Contract creation help
        simulateQuery(
            "how to create contract",
            "HELP_CONTRACT_CREATE",
            0.95,
            "HELP",
            Map.of(),
            "To create a contract: 1) Navigate to Contracts > New Contract 2) Fill required fields (Customer, Effective Date, Terms) 3) Add contract items/parts 4) Set approval workflow 5) Submit for review"
        );
        
        // Scenario 2: Workflow help
        simulateQuery(
            "contract approval workflow",
            "HELP_WORKFLOW", 
            0.87,
            "HELP",
            Map.of(),
            "Contract approval workflow: Draft → Legal Review → Finance Approval → Manager Approval → Final Approval → Active. Each step requires specific permissions and documentation."
        );
        
        // Scenario 3: General help
        simulateQuery(
            "help with contracts",
            "HELP_GENERAL",
            0.82,
            "HELP",
            Map.of(),
            "I can help you with: Contract information and lookup, Parts inventory and details, Contract creation process, Approval workflows, System navigation. What specific help do you need?"
        );
        
        System.out.println();
    }
    
    /**
     * Demonstrate spell correction
     */
    private void demonstrateSpellCorrection() {
        System.out.println("🔤 SPELL CORRECTION SCENARIOS");
        System.out.println("==============================");
        
        // Scenario 1: Multiple spelling errors
        simulateQueryWithSpellCorrection(
            "shw contrct 123456 effectuve dat",
            "show contract 123456 effective date",
            "CONTRACT_DATES",
            0.88,
            "CONTRACT",
            Map.of("contractNumber", "123456"),
            "Contract 123456 effective date: January 1, 2024. I corrected some spelling in your query (shw→show, contrct→contract, effectuve→effective, dat→date)."
        );
        
        // Scenario 2: Parts query with errors
        simulateQueryWithSpellCorrection(
            "how many parst for contrct ABC-789",
            "how many parts for contract ABC-789", 
            "PARTS_COUNT",
            0.90,
            "PARTS",
            Map.of("contractNumber", "ABC-789"),
            "Contract ABC-789 contains 12 parts. I corrected spelling: parst→parts, contrct→contract."
        );
        
        // Scenario 3: Help query with errors
        simulateQueryWithSpellCorrection(
            "hep me creat new contrct",
            "help me create new contract",
            "HELP_CONTRACT_CREATE", 
            0.92,
            "HELP",
            Map.of(),
            "To create a new contract, follow these steps... I corrected: hep→help, creat→create, contrct→contract."
        );
        
        System.out.println();
    }
    
    /**
     * Demonstrate entity extraction
     */
    private void demonstrateEntityExtraction() {
        System.out.println("🎯 ENTITY EXTRACTION SCENARIOS");
        System.out.println("===============================");
        
        // Scenario 1: Multiple entities
        simulateQuery(
            "show contract 123456 for customer ABC Corp account 987654321",
            "CONTRACT_LOOKUP",
            0.94,
            "CONTRACT",
            Map.of(
                "contractNumber", "123456",
                "customer", "ABC Corp", 
                "accountNumber", "987654321"
            ),
            "Contract 123456 for customer ABC Corp (Account: 987654321): Status Active, Effective 2024-01-01, Value $75,000"
        );
        
        // Scenario 2: Date and part extraction
        simulateQuery(
            "part P12345 warranty expires 2025-12-31 for contract ABC-789",
            "PARTS_DETAILS",
            0.91,
            "PARTS", 
            Map.of(
                "partNumber", "P12345",
                "contractNumber", "ABC-789",
                "date", "2025-12-31"
            ),
            "Part P12345 in contract ABC-789: Warranty expires December 31, 2025. Current status: Active, 18 months remaining."
        );
        
        System.out.println();
    }
    
    /**
     * Demonstrate ADF integration features
     */
    private void demonstrateADFIntegration() {
        System.out.println("🎨 ADF INTEGRATION SCENARIOS");
        System.out.println("=============================");
        
        // Scenario 1: High confidence response
        demonstrateADFResponse(
            "show contract 123456",
            "CONTRACT_LOOKUP",
            0.95,
            "high-confidence",
            "#E8F5E8",
            "fa fa-file-contract",
            Arrays.asList("View Contract Details", "Edit Contract", "Show Contract History")
        );
        
        // Scenario 2: Medium confidence response
        demonstrateADFResponse(
            "contract details for 123",
            "CONTRACT_LOOKUP", 
            0.65,
            "medium-confidence",
            "#FFF8E1",
            "fa fa-file-contract",
            Arrays.asList("View Contract Details", "Clarify Contract Number")
        );
        
        // Scenario 3: Error response
        demonstrateADFResponse(
            "delete all contracts",
            "ERROR",
            0.0,
            "error",
            "#FFEBEE", 
            "fa fa-exclamation-triangle",
            Arrays.asList("Try Again", "Get Help", "Contact Support")
        );
        
        System.out.println();
    }
    
    /**
     * Demonstrate error handling
     */
    private void demonstrateErrorHandling() {
        System.out.println("⚠️ ERROR HANDLING SCENARIOS");
        System.out.println("============================");
        
        // Scenario 1: Empty query
        simulateErrorScenario(
            "",
            "VALIDATION_ERROR",
            "Please enter a query. I can help you with contracts, parts, or general guidance."
        );
        
        // Scenario 2: Unclear query
        simulateErrorScenario(
            "xyz abc def",
            "UNCLEAR_INTENT",
            "I couldn't understand your request. Try asking about contracts (e.g., 'show contract 123456'), parts (e.g., 'parts for contract ABC-789'), or help (e.g., 'how to create contract')."
        );
        
        // Scenario 3: Invalid operation
        simulateErrorScenario(
            "create parts for contract 123456",
            "INVALID_OPERATION", 
            "Parts cannot be created through this system - they are loaded from Excel files. I can help you view existing parts or check inventory. Try 'show parts for contract 123456'."
        );
        
        System.out.println();
    }
    
    /**
     * Simulate a standard query processing
     */
    private void simulateQuery(String userQuery, String expectedIntent, double confidence, 
                              String domain, Map<String, String> entities, String response) {
        
        System.out.println("🔍 Query: \"" + userQuery + "\"");
        System.out.println("   📊 Processing Results:");
        System.out.println("      Intent: " + expectedIntent);
        System.out.println("      Confidence: " + String.format("%.2f", confidence) + " (" + getConfidenceLevel(confidence) + ")");
        System.out.println("      Domain: " + domain);
        
        if (!entities.isEmpty()) {
            System.out.println("      Entities: " + entities);
        }
        
        System.out.println("   📝 Response: " + response);
        System.out.println("   ⏱️ Processing Time: " + (50 + (int)(Math.random() * 100)) + "ms");
        System.out.println();
    }
    
    /**
     * Simulate query with spell correction
     */
    private void simulateQueryWithSpellCorrection(String originalQuery, String correctedQuery,
                                                 String expectedIntent, double confidence, String domain,
                                                 Map<String, String> entities, String response) {
        
        System.out.println("🔍 Query: \"" + originalQuery + "\"");
        System.out.println("   🔤 Spell Corrected: \"" + correctedQuery + "\"");
        System.out.println("   📊 Processing Results:");
        System.out.println("      Intent: " + expectedIntent);
        System.out.println("      Confidence: " + String.format("%.2f", confidence) + " (" + getConfidenceLevel(confidence) + ")");
        System.out.println("      Domain: " + domain);
        
        if (!entities.isEmpty()) {
            System.out.println("      Entities: " + entities);
        }
        
        System.out.println("   📝 Response: " + response);
        System.out.println("   ⏱️ Processing Time: " + (60 + (int)(Math.random() * 120)) + "ms");
        System.out.println();
    }
    
    /**
     * Demonstrate ADF response formatting
     */
    private void demonstrateADFResponse(String query, String intent, double confidence,
                                       String styleClass, String backgroundColor, String iconClass,
                                       List<String> suggestedActions) {
        
        System.out.println("🔍 Query: \"" + query + "\"");
        System.out.println("   🎨 ADF Response Formatting:");
        System.out.println("      Intent: " + intent);
        System.out.println("      Confidence: " + String.format("%.2f", confidence));
        System.out.println("      CSS Class: " + styleClass);
        System.out.println("      Background Color: " + backgroundColor);
        System.out.println("      Icon Class: " + iconClass);
        System.out.println("      Suggested Actions: " + suggestedActions);
        
        // Show how it would appear in ADF UI
        System.out.println("   📱 ADF UI Display:");
        System.out.println("      ┌─────────────────────────────────────┐");
        System.out.println("      │ [" + getIconSymbol(iconClass) + "] " + getResponsePreview(intent) + " │");
        System.out.println("      │ Confidence: " + getConfidenceLevel(confidence) + " (" + String.format("%.0f", confidence * 100) + "%) │");
        System.out.println("      │ Actions: " + String.join(", ", suggestedActions.subList(0, Math.min(2, suggestedActions.size()))) + " │");
        System.out.println("      └─────────────────────────────────────┘");
        System.out.println();
    }
    
    /**
     * Simulate error scenarios
     */
    private void simulateErrorScenario(String query, String errorType, String errorMessage) {
        System.out.println("🔍 Query: \"" + query + "\"");
        System.out.println("   ⚠️ Error Handling:");
        System.out.println("      Error Type: " + errorType);
        System.out.println("      System Response: " + errorMessage);
        System.out.println("      UI Style: error (red background)");
        System.out.println("      Suggested Actions: [Try Again] [Get Help] [Contact Support]");
        System.out.println();
    }
    
    /**
     * Helper methods
     */
    private String getConfidenceLevel(double confidence) {
        if (confidence >= 0.9) return "VERY_HIGH";
        if (confidence >= 0.7) return "HIGH";
        if (confidence >= 0.5) return "MEDIUM";
        if (confidence >= 0.3) return "LOW";
        return "VERY_LOW";
    }
    
    private String getIconSymbol(String iconClass) {
        if (iconClass.contains("file-contract")) return "📋";
        if (iconClass.contains("cogs")) return "🔧";
        if (iconClass.contains("question")) return "❓";
        if (iconClass.contains("exclamation")) return "⚠️";
        return "💬";
    }
    
    private String getResponsePreview(String intent) {
        if (intent.startsWith("CONTRACT")) return "Contract information found";
        if (intent.startsWith("PARTS")) return "Parts information retrieved";
        if (intent.startsWith("HELP")) return "Help information provided";
        if (intent.equals("ERROR")) return "Error in processing request";
        return "Response generated";
    }
}
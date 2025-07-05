package com.adf.nlp.test;

import com.adf.nlp.controller.EnhancedNLPController;
import com.adf.nlp.response.NLPResponse;

/**
 * Demonstration of JSON Response System
 * Shows specific examples from the requirements
 */
public class JSONResponseDemo {
    
    private EnhancedNLPController controller;
    
    public JSONResponseDemo() {
        this.controller = new EnhancedNLPController();
    }
    
    public static void main(String[] args) {
        JSONResponseDemo demo = new JSONResponseDemo();
        demo.runDemo();
    }
    
    public void runDemo() {
        System.out.println("=== JSON RESPONSE SYSTEM DEMONSTRATION ===\n");
        
        // Test queries from requirements
        String[] testQueries = {
            "show the contract 12345",
            "display contracts created by vinod",
            "what's the effective date for contract ABC-789",
            "shw contrct 1245 for acount 5678",
            "list all parts for contract 123456",
            "how many parts in contract 789012",
            "help me create contract",
            "create parts for contract 456789"
        };
        
        for (String query : testQueries) {
            demonstrateQuery(query);
        }
        
        System.out.println("=== DEMONSTRATION COMPLETED ===");
    }
    
    private void demonstrateQuery(String query) {
        System.out.println("INPUT QUERY: \"" + query + "\"");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            NLPResponse response = controller.processQuery(query);
            
            // Print summary
            System.out.println("PROCESSING SUMMARY:");
            System.out.println("  Query Type: " + response.getQueryMetadata().getQueryType());
            System.out.println("  Action Type: " + response.getQueryMetadata().getActionType());
            System.out.println("  Processing Time: " + response.getQueryMetadata().getProcessingTimeMs() + "ms");
            
            // Print extracted information
            if (response.getHeader().hasAnyValue()) {
                System.out.println("  Extracted Information:");
                if (response.getHeader().getContractNumber() != null) {
                    System.out.println("    - Contract Number: " + response.getHeader().getContractNumber());
                }
                if (response.getHeader().getPartNumber() != null) {
                    System.out.println("    - Part Number: " + response.getHeader().getPartNumber());
                }
                if (response.getHeader().getCustomerNumber() != null) {
                    System.out.println("    - Customer Number: " + response.getHeader().getCustomerNumber());
                }
                if (response.getHeader().getCreatedBy() != null) {
                    System.out.println("    - Created By: " + response.getHeader().getCreatedBy());
                }
            }
            
            // Print routing decision
            System.out.println("  Routing Decision: " + determineRouting(response));
            
            // Print full JSON response
            System.out.println("\nFULL JSON RESPONSE:");
            System.out.println(response.toString());
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        
        System.out.println("\n" + "─".repeat(120) + "\n");
    }
    
    private String determineRouting(NLPResponse response) {
        String queryType = response.getQueryMetadata().getQueryType();
        String actionType = response.getQueryMetadata().getActionType();
        
        switch (queryType) {
            case "CONTRACT":
                return "Routed to CONTRACT model for contract-related queries";
            case "PARTS":
                if ("CREATE_ERROR".equals(actionType)) {
                    return "Routed to PARTS model with CREATE_ERROR (parts cannot be created)";
                } else {
                    return "Routed to PARTS model for parts-related queries";
                }
            case "HELP":
                return "Routed to HELP model for contract creation guidance";
            case "ERROR":
                return "Error in processing - invalid input";
            default:
                return "Unknown routing";
        }
    }
}
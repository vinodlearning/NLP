package com.company.contracts.enhanced;

/**
 * JSON Printer Utility
 * Standalone utility to print QueryResponse objects as formatted JSON
 */
public class JSONPrinter {
    
    /**
     * Print QueryResponse as formatted JSON
     */
    public static void printJSON(FinalFixedChatbotProcessor.QueryResponse response) {
        System.out.println(toJSONString(response));
    }
    
    /**
     * Convert QueryResponse to JSON string
     */
    public static String toJSONString(FinalFixedChatbotProcessor.QueryResponse response) {
        StringBuilder json = new StringBuilder();
        
        json.append("{\n");
        
        // Header
        json.append("  \"header\": {\n");
        FinalFixedChatbotProcessor.Header header = response.getHeader();
        json.append("    \"contractNumber\": ").append(quote(header.getContractNumber())).append(",\n");
        json.append("    \"partNumber\": ").append(quote(header.getPartNumber())).append(",\n");
        json.append("    \"customerNumber\": ").append(quote(header.getCustomerNumber())).append(",\n");
        json.append("    \"customerName\": ").append(quote(header.getCustomerName())).append(",\n");
        json.append("    \"createdBy\": ").append(quote(header.getCreatedBy())).append("\n");
        json.append("  },\n");
        
        // Query Metadata
        json.append("  \"queryMetadata\": {\n");
        json.append("    \"queryType\": ").append(quote(response.getQueryMetadata().getQueryType())).append(",\n");
        json.append("    \"actionType\": ").append(quote(response.getQueryMetadata().getActionType())).append(",\n");
        json.append("    \"processingTimeMs\": ").append(String.format("%.3f", response.getQueryMetadata().getProcessingTimeMs())).append("\n");
        json.append("  },\n");
        
        // Entities
        json.append("  \"entities\": [\n");
        for (int i = 0; i < response.getEntities().size(); i++) {
            FinalFixedChatbotProcessor.EntityFilter entity = response.getEntities().get(i);
            json.append("    {\n");
            json.append("      \"attribute\": ").append(quote(entity.getAttribute())).append(",\n");
            json.append("      \"operation\": ").append(quote(entity.getOperation())).append(",\n");
            json.append("      \"value\": ").append(quote(entity.getValue())).append(",\n");
            json.append("      \"source\": ").append(quote(entity.getSource())).append("\n");
            json.append("    }").append(i < response.getEntities().size() - 1 ? "," : "").append("\n");
        }
        json.append("  ],\n");
        
        // Display Entities
        json.append("  \"displayEntities\": [\n");
        for (int i = 0; i < response.getDisplayEntities().size(); i++) {
            json.append("    ").append(quote(response.getDisplayEntities().get(i)));
            json.append(i < response.getDisplayEntities().size() - 1 ? "," : "").append("\n");
        }
        json.append("  ],\n");
        
        // Errors
        json.append("  \"errors\": [\n");
        for (int i = 0; i < response.getErrors().size(); i++) {
            FinalFixedChatbotProcessor.ValidationError error = response.getErrors().get(i);
            json.append("    {\n");
            json.append("      \"code\": ").append(quote(error.getCode())).append(",\n");
            json.append("      \"message\": ").append(quote(error.getMessage())).append(",\n");
            json.append("      \"severity\": ").append(quote(error.getSeverity())).append("\n");
            json.append("    }").append(i < response.getErrors().size() - 1 ? "," : "").append("\n");
        }
        json.append("  ]\n");
        
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Print QueryResponse with custom title
     */
    public static void printJSON(FinalFixedChatbotProcessor.QueryResponse response, String title) {
        System.out.println("=".repeat(60));
        System.out.println(title);
        System.out.println("=".repeat(60));
        printJSON(response);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Helper method to quote strings for JSON
     */
    private static String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        // Example usage
        FinalFixedChatbotProcessor processor = new FinalFixedChatbotProcessor();
        
        String[] testInputs = {
            "show contract 123456",
            "get part info AE125",
            "show customer 12345678 contracts"
        };
        
        for (String input : testInputs) {
            FinalFixedChatbotProcessor.QueryResponse response = processor.processQuery(input);
            
            System.out.printf("\n🔍 INPUT: \"%s\"\n", input);
            printJSON(response, "📋 JSON RESPONSE");
        }
    }
}
package com.company.contracts.enhanced;

/**
 * JSON Example - Demonstrates all ways to print JSON from QueryResponse
 */
public class JSONExample {
    
    public static void main(String[] args) {
        // Your exact code
        String input = "show contract 123456";
        FinalFixedChatbotProcessor ob = new FinalFixedChatbotProcessor();
        FinalFixedChatbotProcessor.QueryResponse response = ob.processQuery(input);
        
        System.out.println("=".repeat(80));
        System.out.println("🎯 ALL WAYS TO PRINT JSON FROM QueryResponse");
        System.out.println("=".repeat(80));
        
        // METHOD 1: Using the processor's built-in method (NEW!)
        System.out.println("\n📋 METHOD 1: Using processor.printJSON()");
        System.out.println("-".repeat(50));
        ob.printJSON(response);
        
        // METHOD 2: Using the processor's toJSONString method (NEW!)
        System.out.println("\n📋 METHOD 2: Using processor.toJSONString()");
        System.out.println("-".repeat(50));
        String jsonString = ob.toJSONString(response);
        System.out.println(jsonString);
        
        // METHOD 3: Using JSONPrinter utility
        System.out.println("\n📋 METHOD 3: Using JSONPrinter.printJSON()");
        System.out.println("-".repeat(50));
        JSONPrinter.printJSON(response);
        
        // METHOD 4: Using JSONPrinter with title
        System.out.println("\n📋 METHOD 4: Using JSONPrinter with custom title");
        System.out.println("-".repeat(50));
        JSONPrinter.printJSON(response, "🎯 MY CUSTOM TITLE");
        
        // METHOD 5: Manual field access (if you want specific fields only)
        System.out.println("\n📋 METHOD 5: Manual field access");
        System.out.println("-".repeat(50));
        printManualJSON(response);
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ Choose any method that works best for your needs!");
        System.out.println("=".repeat(80));
    }
    
    /**
     * Example of manual JSON printing (if you want custom format)
     */
    private static void printManualJSON(FinalFixedChatbotProcessor.QueryResponse response) {
        FinalFixedChatbotProcessor.Header header = response.getHeader();
        
        System.out.println("{");
        System.out.printf("  \"contractNumber\": \"%s\",\n", header.getContractNumber());
        System.out.printf("  \"partNumber\": %s,\n", header.getPartNumber() == null ? "null" : "\"" + header.getPartNumber() + "\"");
        System.out.printf("  \"queryType\": \"%s\",\n", response.getQueryMetadata().getQueryType());
        System.out.printf("  \"actionType\": \"%s\",\n", response.getQueryMetadata().getActionType());
        System.out.printf("  \"processingTime\": %.3f,\n", response.getQueryMetadata().getProcessingTimeMs());
        System.out.printf("  \"hasErrors\": %s\n", !response.getErrors().isEmpty());
        System.out.println("}");
    }
}
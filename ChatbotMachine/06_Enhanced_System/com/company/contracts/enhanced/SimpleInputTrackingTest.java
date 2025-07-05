package com.company.contracts.enhanced;

/**
 * Simple test to debug input tracking issue
 */
public class SimpleInputTrackingTest {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        System.out.println("=".repeat(60));
        System.out.println("SIMPLE INPUT TRACKING DEBUG");
        System.out.println("=".repeat(60));
        
        // Test 1: Input with no spell errors
        String input1 = "show contract 123456";
        System.out.println("\n🔍 TEST 1: " + input1);
        StandardJSONProcessor.QueryResult result1 = processor.processQueryToObject(input1);
        
        System.out.println("Raw values:");
        System.out.println("  inputTracking.originalInput: '" + result1.inputTracking.originalInput + "'");
        System.out.println("  inputTracking.correctedInput: '" + result1.inputTracking.correctedInput + "'");
        System.out.println("  inputTracking.correctionConfidence: " + result1.inputTracking.correctionConfidence);
        
        System.out.println("Getter methods:");
        System.out.println("  getOriginalInput(): '" + result1.getOriginalInput() + "'");
        System.out.println("  getCorrectedInput(): '" + result1.getCorrectedInput() + "'");
        System.out.println("  getCorrectionConfidence(): " + result1.getCorrectionConfidence());
        System.out.println("  hasSpellCorrections(): " + result1.hasSpellCorrections());
        
        System.out.println("Null checks:");
        System.out.println("  correctedInput == null: " + (result1.inputTracking.correctedInput == null));
        System.out.println("  correctedInput.equals(\"null\"): " + (result1.inputTracking.correctedInput != null && result1.inputTracking.correctedInput.equals("null")));
        
        // Test 2: Input with spell errors
        String input2 = "show contrct 123456";
        System.out.println("\n🔍 TEST 2: " + input2);
        StandardJSONProcessor.QueryResult result2 = processor.processQueryToObject(input2);
        
        System.out.println("Raw values:");
        System.out.println("  inputTracking.originalInput: '" + result2.inputTracking.originalInput + "'");
        System.out.println("  inputTracking.correctedInput: '" + result2.inputTracking.correctedInput + "'");
        System.out.println("  inputTracking.correctionConfidence: " + result2.inputTracking.correctionConfidence);
        
        System.out.println("Getter methods:");
        System.out.println("  getOriginalInput(): '" + result2.getOriginalInput() + "'");
        System.out.println("  getCorrectedInput(): '" + result2.getCorrectedInput() + "'");
        System.out.println("  getCorrectionConfidence(): " + result2.getCorrectionConfidence());
        System.out.println("  hasSpellCorrections(): " + result2.hasSpellCorrections());
        
        System.out.println("Null checks:");
        System.out.println("  correctedInput == null: " + (result2.inputTracking.correctedInput == null));
        System.out.println("  correctedInput.equals(\"null\"): " + (result2.inputTracking.correctedInput != null && result2.inputTracking.correctedInput.equals("null")));
        
        // Test 3: Direct JSON parsing
        String testJSON = "{\n" +
            "  \"header\": {\n" +
            "    \"contractNumber\": \"123456\",\n" +
            "    \"inputTracking\": {\n" +
            "      \"originalInput\": \"show contract 123456\",\n" +
            "      \"correctedInput\": null,\n" +
            "      \"correctionConfidence\": 0.0\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        System.out.println("\n🔍 TEST 3: Direct JSON Parsing");
        StandardJSONProcessor.QueryResult result3 = processor.parseJSONToObject(testJSON);
        
        System.out.println("Raw values:");
        System.out.println("  inputTracking.originalInput: '" + result3.inputTracking.originalInput + "'");
        System.out.println("  inputTracking.correctedInput: '" + result3.inputTracking.correctedInput + "'");
        System.out.println("  inputTracking.correctionConfidence: " + result3.inputTracking.correctionConfidence);
        
        System.out.println("Getter methods:");
        System.out.println("  getOriginalInput(): '" + result3.getOriginalInput() + "'");
        System.out.println("  getCorrectedInput(): '" + result3.getCorrectedInput() + "'");
        System.out.println("  getCorrectionConfidence(): " + result3.getCorrectionConfidence());
        System.out.println("  hasSpellCorrections(): " + result3.hasSpellCorrections());
        
        System.out.println("Null checks:");
        System.out.println("  correctedInput == null: " + (result3.inputTracking.correctedInput == null));
        System.out.println("  correctedInput.equals(\"null\"): " + (result3.inputTracking.correctedInput != null && result3.inputTracking.correctedInput.equals("null")));
        
        System.out.println("\n" + "=".repeat(60));
    }
}
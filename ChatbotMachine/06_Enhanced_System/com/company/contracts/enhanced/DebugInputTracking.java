package com.company.contracts.enhanced;

/**
 * Debug test to see why inputTracking extraction is not working
 */
public class DebugInputTracking {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        // Test JSON with the exact structure
        String testJSON = "{\n" +
            "  \"header\": {\n" +
            "    \"contractNumber\": \"123456\",\n" +
            "    \"partNumber\": null,\n" +
            "    \"customerNumber\": null,\n" +
            "    \"customerName\": null,\n" +
            "    \"createdBy\": null,\n" +
            "    \"inputTracking\": {\n" +
            "      \"originalInput\": \"show contract 123456\",\n" +
            "      \"correctedInput\": null,\n" +
            "      \"correctionConfidence\": 0.0\n" +
            "    }\n" +
            "  },\n" +
            "  \"queryMetadata\": {\n" +
            "    \"queryType\": \"CONTRACTS\",\n" +
            "    \"actionType\": \"contracts_by_contractNumber\",\n" +
            "    \"processingTimeMs\": 25.472\n" +
            "  },\n" +
            "  \"entities\": [],\n" +
            "  \"displayEntities\": [\"CONTRACT_NUMBER\", \"CUSTOMER_NAME\"],\n" +
            "  \"errors\": []\n" +
            "}";
        
        System.out.println("=".repeat(80));
        System.out.println("DEBUG INPUT TRACKING EXTRACTION");
        System.out.println("=".repeat(80));
        
        System.out.println("\n📄 TEST JSON:");
        System.out.println(testJSON);
        
        System.out.println("\n🔍 DEBUGGING EXTRACTION STEPS:");
        
        // Test extractJSONObject for header
        String headerSection = extractJSONObject(testJSON, "header");
        System.out.println("\n1. Header Section Extracted:");
        System.out.println("   Result: " + (headerSection != null ? "SUCCESS" : "FAILED"));
        if (headerSection != null) {
            System.out.println("   Content: " + headerSection);
        }
        
        // Test extractNestedJSONObject for inputTracking
        String inputTrackingSection = extractNestedJSONObject(testJSON, "header", "inputTracking");
        System.out.println("\n2. InputTracking Section Extracted:");
        System.out.println("   Result: " + (inputTrackingSection != null ? "SUCCESS" : "FAILED"));
        if (inputTrackingSection != null) {
            System.out.println("   Content: " + inputTrackingSection);
        }
        
        // Test individual field extraction from inputTracking
        if (inputTrackingSection != null) {
            String originalInput = extractJSONValue(inputTrackingSection, "originalInput");
            String correctedInput = extractJSONValue(inputTrackingSection, "correctedInput");
            String confidence = extractJSONValue(inputTrackingSection, "correctionConfidence");
            
            System.out.println("\n3. Individual Fields from InputTracking:");
            System.out.println("   originalInput: " + originalInput);
            System.out.println("   correctedInput: " + correctedInput);
            System.out.println("   correctionConfidence: " + confidence);
        }
        
        // Test the full parsing
        System.out.println("\n4. Full Parsing Test:");
        StandardJSONProcessor.QueryResult result = processor.parseJSONToObject(testJSON);
        System.out.println("   Original Input: " + result.getOriginalInput());
        System.out.println("   Corrected Input: " + result.getCorrectedInput());
        System.out.println("   Correction Confidence: " + result.getCorrectionConfidence());
        
        // Test with a live query to compare
        System.out.println("\n5. Live Query Test:");
        String liveInput = "show contract 123456";
        StandardJSONProcessor.QueryResult liveResult = processor.processQueryToObject(liveInput);
        System.out.println("   Live Original Input: " + liveResult.getOriginalInput());
        System.out.println("   Live Corrected Input: " + liveResult.getCorrectedInput());
        System.out.println("   Live Correction Confidence: " + liveResult.getCorrectionConfidence());
        
        System.out.println("\n" + "=".repeat(80));
    }
    
    // Copy the helper methods to test them directly
    private static String extractJSONObject(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\{([^}]*(?:\\{[^}]*\\}[^}]*)*)\\}";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        
        if (m.find()) {
            return m.group(1);
        }
        
        return null;
    }
    
    private static String extractNestedJSONObject(String json, String parentKey, String childKey) {
        // First extract the parent object
        String parentObject = extractJSONObject(json, parentKey);
        if (parentObject == null) {
            return null;
        }
        
        // Then extract the child object from the parent
        return extractJSONObject("{" + parentObject + "}", childKey);
    }
    
    private static String extractJSONValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"|\"" + key + "\"\\s*:\\s*([^,}\\]]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        
        if (m.find()) {
            return m.group(1) != null ? m.group(1) : m.group(2).trim();
        }
        
        return null;
    }
}
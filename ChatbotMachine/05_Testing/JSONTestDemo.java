/**
 * JSON Test Demo
 * 
 * This class demonstrates the processQueryJSON functionality
 * of the EnhancedMLController system
 */
public class JSONTestDemo {
    
    public static void main(String[] args) {
        System.out.println("🚀 JSON Test Demo");
        System.out.println("=================\n");
        
        EnhancedMLController controller = new EnhancedMLController();
        
        // Test queries
        String[] testQueries = {
            "effective date,expiration,price expiration,projecttype for contarct 124563",
            "show all parts for contract 123456",
            "help me create a contract",
            "create parts for contract 123",
            "show contract 999999"
        };
        
        for (int i = 0; i < testQueries.length; i++) {
            System.out.println("📝 Test Query " + (i + 1) + ":");
            System.out.println("Input: \"" + testQueries[i] + "\"");
            System.out.println();
            
            try {
                String jsonResponse = controller.processQueryJSON(testQueries[i]);
                System.out.println("JSON Response:");
                System.out.println(jsonResponse);
                
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("\n" + "=".repeat(80) + "\n");
        }
        
        // Demonstrate JSON parsing capabilities
        demonstrateJSONParsing(controller);
    }
    
    /**
     * Demonstrate how to work with the JSON response
     */
    private static void demonstrateJSONParsing(EnhancedMLController controller) {
        System.out.println("🔍 JSON Parsing Demonstration");
        System.out.println("=============================\n");
        
        String testQuery = "effective date,expiration,price expiration,projecttype for contarct 124563";
        String jsonResponse = controller.processQueryJSON(testQuery);
        
        System.out.println("📋 Original Query: \"" + testQuery + "\"");
        System.out.println();
        System.out.println("📊 JSON Response Structure:");
        System.out.println(jsonResponse);
        System.out.println();
        
        // Show how to extract specific information from JSON
        System.out.println("🎯 Key Information Extraction:");
        System.out.println("------------------------------");
        
        // Extract response type
        String responseType = extractJSONValue(jsonResponse, "responseType");
        System.out.println("Response Type: " + responseType);
        
        // Extract success status
        String success = extractJSONValue(jsonResponse, "success");
        System.out.println("Success: " + success);
        
        // Extract message
        String message = extractJSONValue(jsonResponse, "message");
        System.out.println("Message: " + message);
        
        // Extract entity ID from request
        String entityId = extractNestedJSONValue(jsonResponse, "request", "entityId");
        System.out.println("Entity ID: " + entityId);
        
        // Extract entity type
        String entityType = extractNestedJSONValue(jsonResponse, "request", "entityType");
        System.out.println("Entity Type: " + entityType);
        
        // Extract processing time
        String processingTime = extractNestedJSONValue(jsonResponse, "processing", "processingTimeMs");
        System.out.println("Processing Time: " + processingTime + " ms");
        
        System.out.println();
        System.out.println("✅ JSON processing complete!");
    }
    
    /**
     * Simple JSON value extraction (for demonstration)
     */
    private static String extractJSONValue(String json, String key) {
        String searchPattern = "\"" + key + "\": ";
        int start = json.indexOf(searchPattern);
        if (start == -1) return "Not found";
        
        start += searchPattern.length();
        
        // Skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        if (start >= json.length()) return "Not found";
        
        char firstChar = json.charAt(start);
        if (firstChar == '"') {
            // String value
            start++; // Skip opening quote
            int end = json.indexOf('"', start);
            if (end == -1) return "Not found";
            return json.substring(start, end);
        } else if (firstChar == 't' || firstChar == 'f') {
            // Boolean value
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('\n', start);
            if (end == -1) end = json.indexOf('}', start);
            if (end == -1) return "Not found";
            return json.substring(start, end).trim();
        } else if (Character.isDigit(firstChar) || firstChar == '-') {
            // Number value
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('\n', start);
            if (end == -1) end = json.indexOf('}', start);
            if (end == -1) return "Not found";
            return json.substring(start, end).trim();
        }
        
        return "Not found";
    }
    
    /**
     * Extract nested JSON value (for demonstration)
     */
    private static String extractNestedJSONValue(String json, String parentKey, String childKey) {
        String parentPattern = "\"" + parentKey + "\": {";
        int parentStart = json.indexOf(parentPattern);
        if (parentStart == -1) return "Not found";
        
        // Find the end of the parent object
        int braceCount = 1;
        int searchStart = parentStart + parentPattern.length();
        int parentEnd = searchStart;
        
        while (parentEnd < json.length() && braceCount > 0) {
            char c = json.charAt(parentEnd);
            if (c == '{') braceCount++;
            else if (c == '}') braceCount--;
            parentEnd++;
        }
        
        if (braceCount > 0) return "Not found";
        
        // Extract the parent object content
        String parentContent = json.substring(searchStart, parentEnd - 1);
        
        // Find the child key in the parent content
        return extractJSONValue(parentContent, childKey);
    }
}
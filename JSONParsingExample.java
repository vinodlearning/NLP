/**
 * Practical Example: How to Parse JSON Response and Use Contract Data
 * 
 * This example shows how to integrate the ML system JSON response
 * into your existing Contract Portal application.
 */
public class JSONParsingExample {
    
    public static void main(String[] args) {
        System.out.println("📋 JSON Parsing Example - Contract Data");
        System.out.println("=======================================\n");
        
        // Example: How you would use this in your existing managed bean
        demonstrateJSONParsing();
        
        // Example: How to extract specific attributes
        demonstrateAttributeExtraction();
        
        // Example: How to handle the response in your UI
        demonstrateUIIntegration();
    }
    
    /**
     * Example 1: Basic JSON Parsing (using simple string parsing for demo)
     */
    public static void demonstrateJSONParsing() {
        System.out.println("🔧 Example 1: JSON Parsing in Your Bean");
        System.out.println("----------------------------------------");
        
        // This is what your ML system returns
        String jsonResponse = getExampleJSONResponse();
        
        // In a real application, you would use Jackson or Gson:
        // ObjectMapper mapper = new ObjectMapper();
        // ContractResponse response = mapper.readValue(jsonResponse, ContractResponse.class);
        
        // For demo purposes, we'll extract values manually
        String effectiveDate = extractValue(jsonResponse, "effectiveDate");
        String expirationDate = extractValue(jsonResponse, "expirationDate");
        String priceExpirationDate = extractValue(jsonResponse, "priceExpirationDate");
        String projectType = extractValue(jsonResponse, "projectType");
        String contractId = extractValue(jsonResponse, "contractId");
        
        System.out.println("✅ Extracted Contract Data:");
        System.out.println("   Contract ID: " + contractId);
        System.out.println("   Effective Date: " + effectiveDate);
        System.out.println("   Expiration Date: " + expirationDate);
        System.out.println("   Price Expiration: " + priceExpirationDate);
        System.out.println("   Project Type: " + projectType);
        System.out.println();
    }
    
    /**
     * Example 2: How to extract specific attributes you requested
     */
    public static void demonstrateAttributeExtraction() {
        System.out.println("🎯 Example 2: Extract Requested Attributes");
        System.out.println("------------------------------------------");
        
        // Your original input was: "effective date,expiration,price expiration,projecttype for contarct 124563"
        // The system detected these attributes: ["effectiveDate", "expirationDate", "priceExpirationDate", "projectType"]
        
        String[] requestedAttributes = {"effectiveDate", "expirationDate", "priceExpirationDate", "projectType"};
        String jsonResponse = getExampleJSONResponse();
        
        System.out.println("📝 Attributes you requested:");
        for (String attribute : requestedAttributes) {
            String value = extractValue(jsonResponse, attribute);
            System.out.println("   " + attribute + ": " + value);
        }
        System.out.println();
    }
    
    /**
     * Example 3: How to integrate this into your UI/Bean
     */
    public static void demonstrateUIIntegration() {
        System.out.println("💻 Example 3: UI Integration in Your Bean");
        System.out.println("-----------------------------------------");
        
        System.out.println("// In your existing managed bean:");
        System.out.println("@ManagedBean");
        System.out.println("public class YourContractBean {");
        System.out.println("    private String userInput;");
        System.out.println("    private String effectiveDate;");
        System.out.println("    private String expirationDate;");
        System.out.println("    private String priceExpirationDate;");
        System.out.println("    private String projectType;");
        System.out.println("    private String responseMessage;");
        System.out.println("");
        System.out.println("    public void processUserQuery() {");
        System.out.println("        // Call ML system");
        System.out.println("        String jsonResponse = mlSystem.processQuery(userInput);");
        System.out.println("        ");
        System.out.println("        // Parse JSON (using Jackson/Gson)");
        System.out.println("        ContractResponse response = parseJSON(jsonResponse);");
        System.out.println("        ");
        System.out.println("        // Extract data for UI");
        System.out.println("        this.effectiveDate = response.getEffectiveDate();");
        System.out.println("        this.expirationDate = response.getExpirationDate();");
        System.out.println("        this.priceExpirationDate = response.getPriceExpirationDate();");
        System.out.println("        this.projectType = response.getProjectType();");
        System.out.println("        this.responseMessage = response.getMessage();");
        System.out.println("        ");
        System.out.println("        // Navigate to appropriate screen");
        System.out.println("        String action = response.getActions().getRecommendedUIAction();");
        System.out.println("        if (\"SHOW_CONTRACT_DETAILS\".equals(action)) {");
        System.out.println("            navigateToContractDetails();");
        System.out.println("        }");
        System.out.println("    }");
        System.out.println("}");
        System.out.println();
        
        // Show actual extracted values
        String jsonResponse = getExampleJSONResponse();
        String responseType = extractValue(jsonResponse, "responseType");
        String message = extractValue(jsonResponse, "message");
        String uiAction = extractValue(jsonResponse, "recommendedUIAction");
        
        System.out.println("📊 Current Response Values:");
        System.out.println("   Response Type: " + responseType);
        System.out.println("   Message: " + message);
        System.out.println("   UI Action: " + uiAction);
        System.out.println();
    }
    
    /**
     * Helper method to extract values from JSON (simplified for demo)
     */
    private static String extractValue(String json, String key) {
        String searchPattern = "\"" + key + "\": \"";
        int start = json.indexOf(searchPattern);
        if (start == -1) return "N/A";
        
        start += searchPattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return "N/A";
        
        return json.substring(start, end);
    }
    
    /**
     * Example JSON response for demonstration
     */
    private static String getExampleJSONResponse() {
        return "{\n" +
               "  \"responseType\": \"CONTRACT_DETAILS_SUCCESS\",\n" +
               "  \"message\": \"Contract details retrieved successfully\",\n" +
               "  \"success\": true,\n" +
               "  \"timestamp\": 1751630546728,\n" +
               "  \"request\": {\n" +
               "    \"originalInput\": \"effective date,expiration,price expiration,projecttype for contarct 124563\",\n" +
               "    \"correctedInput\": \"effective date,expiration,price expiration,projecttype for contract 124563\",\n" +
               "    \"contractId\": \"124563\",\n" +
               "    \"requestedAttributes\": [\"effectiveDate\", \"expirationDate\", \"priceExpirationDate\", \"projectType\"]\n" +
               "  },\n" +
               "  \"data\": {\n" +
               "    \"contractId\": \"124563\",\n" +
               "    \"effectiveDate\": \"2024-01-15\",\n" +
               "    \"expirationDate\": \"2025-12-31\",\n" +
               "    \"priceExpirationDate\": \"2025-06-30\",\n" +
               "    \"projectType\": \"Software Development\",\n" +
               "    \"status\": \"ACTIVE\",\n" +
               "    \"totalValue\": \"$125,000.00\"\n" +
               "  },\n" +
               "  \"actions\": {\n" +
               "    \"recommendedUIAction\": \"SHOW_CONTRACT_DETAILS\",\n" +
               "    \"navigationTarget\": \"contract-details-screen\"\n" +
               "  }\n" +
               "}";
    }
}
public class JSONParsingExample {
    
    public static void main(String[] args) {
        System.out.println("📋 JSON Parsing Example - Your Contract Data");
        System.out.println("============================================\n");
        
        System.out.println("Your Input: \"effective date,expiration,price expiration,projecttype for contarct 124563\"");
        System.out.println("Spell Corrected: \"effective date,expiration,price expiration,projecttype for contract 124563\"\n");
        
        // Demonstrate extraction of requested attributes
        String jsonResponse = getExampleJSON();
        
        System.out.println("✅ Extracted Your Requested Attributes:");
        System.out.println("   Contract ID: " + extractValue(jsonResponse, "contractId"));
        System.out.println("   Effective Date: " + extractValue(jsonResponse, "effectiveDate"));
        System.out.println("   Expiration Date: " + extractValue(jsonResponse, "expirationDate"));
        System.out.println("   Price Expiration Date: " + extractValue(jsonResponse, "priceExpirationDate"));
        System.out.println("   Project Type: " + extractValue(jsonResponse, "projectType"));
        
        System.out.println("\n💻 How to Use in Your Bean:");
        System.out.println("===========================");
        System.out.println("// In your managed bean:");
        System.out.println("public void processUserQuery() {");
        System.out.println("    String response = mlSystem.processQuery(userInput);");
        System.out.println("    ContractResponse contract = parseJSON(response);");
        System.out.println("    ");
        System.out.println("    // Get the attributes you need:");
        System.out.println("    this.effectiveDate = contract.getEffectiveDate();");
        System.out.println("    this.expirationDate = contract.getExpirationDate();");
        System.out.println("    this.priceExpirationDate = contract.getPriceExpirationDate();");
        System.out.println("    this.projectType = contract.getProjectType();");
        System.out.println("}");
        
        System.out.println("\n📊 Full JSON Response Structure:");
        System.out.println("=================================");
        System.out.println(jsonResponse);
    }
    
    private static String extractValue(String json, String key) {
        String pattern = "\"" + key + "\": \"";
        int start = json.indexOf(pattern);
        if (start == -1) return "N/A";
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return end != -1 ? json.substring(start, end) : "N/A";
    }
    
    private static String getExampleJSON() {
        return "{\n" +
               "  \"responseType\": \"CONTRACT_DETAILS_SUCCESS\",\n" +
               "  \"message\": \"Contract details retrieved successfully\",\n" +
               "  \"success\": true,\n" +
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
               "    \"recommendedUIAction\": \"SHOW_CONTRACT_DETAILS\"\n" +
               "  }\n" +
               "}";
    }
}

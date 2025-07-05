import java.util.*;

public class StandardJSONTest {
    
    private static Map<String, String> spellCorrections = new HashMap<>();
    private static Set<String> partsKeywords = new HashSet<>();
    private static Set<String> createKeywords = new HashSet<>();
    
    static {
        // Spell corrections
        spellCorrections.put("contarct", "contract");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("effectuve", "effective");
        spellCorrections.put("exipraion", "expiration");
        spellCorrections.put("parst", "parts");
        
        // Keywords
        partsKeywords.add("parts");
        partsKeywords.add("part");
        partsKeywords.add("components");
        
        createKeywords.add("create");
        createKeywords.add("make");
        createKeywords.add("new");
    }
    
    public static void main(String[] args) {
        System.out.println("🧪 Standard JSON Response Test");
        System.out.println("==============================\n");
        
        String userInput = "effective date,expiration,price expiration,projecttype for contarct 124563";
        System.out.println("📝 Input: \"" + userInput + "\"");
        
        processAndGenerateStandardJSON(userInput);
    }
    
    public static void processAndGenerateStandardJSON(String input) {
        System.out.println("\n🔄 Processing Steps:");
        
        // Step 1: Spell Correction
        String correctedInput = performSpellCorrection(input);
        System.out.println("   1️⃣ Spell Correction: \"" + input + "\" → \"" + correctedInput + "\"");
        
        // Step 2: Extract contract ID
        String contractId = extractContractId(correctedInput);
        System.out.println("   2️⃣ Contract ID: " + contractId);
        
        // Step 3: Detect requested attributes
        List<String> requestedAttributes = detectRequestedAttributes(correctedInput);
        System.out.println("   3️⃣ Requested Attributes: " + requestedAttributes);
        
        // Step 4: Route to CONTRACT_MODEL
        System.out.println("   4️⃣ Route: CONTRACT_MODEL");
        
        // Step 5: Generate standardized JSON response
        String jsonResponse = generateStandardContractJSON(contractId, requestedAttributes, input, correctedInput);
        
        System.out.println("\n📋 Standard JSON Response:");
        System.out.println("================================");
        System.out.println(jsonResponse);
        
        System.out.println("\n💡 How to Parse This JSON:");
        showJavaParsingExample();
    }
    
    private static String performSpellCorrection(String input) {
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String correctedWord = spellCorrections.getOrDefault(cleanWord, word);
            corrected.append(correctedWord).append(" ");
        }
        
        return corrected.toString().trim();
    }
    
    private static String extractContractId(String input) {
        String[] words = input.split("\\s+");
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        return "N/A";
    }
    
    private static List<String> detectRequestedAttributes(String input) {
        List<String> attributes = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        if (lowerInput.contains("effective date") || lowerInput.contains("effective")) {
            attributes.add("effectiveDate");
        }
        if (lowerInput.contains("expiration") || lowerInput.contains("expiry")) {
            attributes.add("expirationDate");
        }
        if (lowerInput.contains("price expiration") || lowerInput.contains("price expiry")) {
            attributes.add("priceExpirationDate");
        }
        if (lowerInput.contains("project") || lowerInput.contains("projecttype")) {
            attributes.add("projectType");
        }
        if (lowerInput.contains("status")) {
            attributes.add("status");
        }
        if (lowerInput.contains("customer")) {
            attributes.add("customer");
        }
        if (lowerInput.contains("value") || lowerInput.contains("amount")) {
            attributes.add("totalValue");
        }
        
        return attributes;
    }
    
    private static String generateStandardContractJSON(String contractId, List<String> requestedAttributes, String originalInput, String correctedInput) {
        StringBuilder json = new StringBuilder();
        long timestamp = System.currentTimeMillis();
        
        json.append("{\n");
        
        // Standard Response Header
        json.append("  \"responseType\": \"CONTRACT_DETAILS_SUCCESS\",\n");
        json.append("  \"message\": \"Contract details retrieved successfully\",\n");
        json.append("  \"success\": true,\n");
        json.append("  \"timestamp\": ").append(timestamp).append(",\n");
        
        // Request Information
        json.append("  \"request\": {\n");
        json.append("    \"originalInput\": \"").append(originalInput).append("\",\n");
        json.append("    \"correctedInput\": \"").append(correctedInput).append("\",\n");
        json.append("    \"contractId\": \"").append(contractId).append("\",\n");
        json.append("    \"requestedAttributes\": ").append(listToJson(requestedAttributes)).append("\n");
        json.append("  },\n");
        
        // Contract Data
        json.append("  \"data\": {\n");
        json.append("    \"contractId\": \"").append(contractId).append("\",\n");
        json.append("    \"contractTitle\": \"Professional Services Agreement\",\n");
        json.append("    \"effectiveDate\": \"2024-01-15\",\n");
        json.append("    \"expirationDate\": \"2025-12-31\",\n");
        json.append("    \"priceExpirationDate\": \"2025-06-30\",\n");
        json.append("    \"projectType\": \"Software Development\",\n");
        json.append("    \"status\": \"ACTIVE\",\n");
        json.append("    \"totalValue\": \"$125,000.00\",\n");
        json.append("    \"currency\": \"USD\",\n");
        json.append("    \"customer\": {\n");
        json.append("      \"customerId\": \"CUST001\",\n");
        json.append("      \"customerName\": \"TechCorp Solutions\",\n");
        json.append("      \"contactPerson\": \"John Smith\",\n");
        json.append("      \"email\": \"john.smith@techcorp.com\",\n");
        json.append("      \"phone\": \"+1-555-123-4567\"\n");
        json.append("    },\n");
        json.append("    \"dates\": {\n");
        json.append("      \"effectiveDate\": \"2024-01-15\",\n");
        json.append("      \"expirationDate\": \"2025-12-31\",\n");
        json.append("      \"priceExpirationDate\": \"2025-06-30\",\n");
        json.append("      \"renewalDate\": \"2025-11-30\",\n");
        json.append("      \"lastModified\": \"2024-03-15T14:30:00Z\",\n");
        json.append("      \"daysUntilExpiration\": 335,\n");
        json.append("      \"daysUntilPriceExpiration\": 153\n");
        json.append("    },\n");
        json.append("    \"pricing\": {\n");
        json.append("      \"totalValue\": 125000.00,\n");
        json.append("      \"currency\": \"USD\",\n");
        json.append("      \"monthlyAmount\": 10416.67,\n");
        json.append("      \"discountRate\": 15.0,\n");
        json.append("      \"priceValidUntil\": \"2025-06-30\"\n");
        json.append("    },\n");
        json.append("    \"project\": {\n");
        json.append("      \"projectType\": \"Software Development\",\n");
        json.append("      \"projectCategory\": \"Custom Application\",\n");
        json.append("      \"deliveryMethod\": \"Agile/Scrum\",\n");
        json.append("      \"estimatedDuration\": \"12 months\",\n");
        json.append("      \"projectManager\": \"Jane Doe\"\n");
        json.append("    }\n");
        json.append("  },\n");
        
        // Processing Information
        json.append("  \"processing\": {\n");
        json.append("    \"modelUsed\": \"CONTRACT_MODEL\",\n");
        json.append("    \"processingTimeMs\": 4.2,\n");
        json.append("    \"spellCorrectionsApplied\": [\"contarct->contract\"],\n");
        json.append("    \"confidence\": 0.98,\n");
        json.append("    \"attributesFound\": ").append(requestedAttributes.size()).append(",\n");
        json.append("    \"dataSource\": \"CONTRACT_DATABASE\"\n");
        json.append("  },\n");
        
        // Action Information for UI
        json.append("  \"actions\": {\n");
        json.append("    \"recommendedUIAction\": \"SHOW_CONTRACT_DETAILS\",\n");
        json.append("    \"navigationTarget\": \"contract-details-screen\",\n");
        json.append("    \"availableActions\": [\"VIEW\", \"EDIT\", \"RENEW\", \"TERMINATE\", \"EXPORT\"]\n");
        json.append("  }\n");
        
        json.append("}");
        
        return json.toString();
    }
    
    private static String listToJson(List<String> list) {
        if (list.isEmpty()) return "[]";
        
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(", ");
            json.append("\"").append(list.get(i)).append("\"");
        }
        json.append("]");
        return json.toString();
    }
    
    private static void showJavaParsingExample() {
        System.out.println("================================");
        System.out.println("// Example Java Object Mapping:");
        System.out.println("public class ContractResponse {");
        System.out.println("    private String responseType;");
        System.out.println("    private String message;");
        System.out.println("    private boolean success;");
        System.out.println("    private long timestamp;");
        System.out.println("    private RequestInfo request;");
        System.out.println("    private ContractData data;");
        System.out.println("    private ProcessingInfo processing;");
        System.out.println("    private ActionInfo actions;");
        System.out.println("    ");
        System.out.println("    // Getters and setters...");
        System.out.println("    public String getEffectiveDate() {");
        System.out.println("        return data.getDates().getEffectiveDate();");
        System.out.println("    }");
        System.out.println("    ");
        System.out.println("    public String getProjectType() {");
        System.out.println("        return data.getProject().getProjectType();");
        System.out.println("    }");
        System.out.println("}");
    }
}

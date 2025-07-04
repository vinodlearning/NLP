import java.util.*;

/**
 * Simple ML System Test
 * 
 * Tests the user input: "how many parst for 123456"
 * - Should correct "parst" to "parts"
 * - Should route to PartsModel
 * - Should return parts count response
 */
public class SimpleMLTest {
    
    // Spell corrections map
    private static Map<String, String> spellCorrections = new HashMap<>();
    
    // Parts keywords
    private static Set<String> partsKeywords = new HashSet<>();
    
    // Create keywords  
    private static Set<String> createKeywords = new HashSet<>();
    
    static {
        // Initialize spell corrections
        spellCorrections.put("parst", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("effectuve", "effective");
        spellCorrections.put("exipraion", "expiration");
        spellCorrections.put("contrct", "contract");
        
        // Initialize parts keywords
        partsKeywords.add("parts");
        partsKeywords.add("part");
        partsKeywords.add("components");
        partsKeywords.add("inventory");
        partsKeywords.add("lines");
        partsKeywords.add("line");
        
        // Initialize create keywords
        createKeywords.add("create");
        createKeywords.add("make");
        createKeywords.add("new");
        createKeywords.add("add");
        createKeywords.add("generate");
    }
    
    public static void main(String[] args) {
        System.out.println("🧪 Simple ML System Test");
        System.out.println("========================\n");
        
        // Test the user's specific input
        String userInput = "how many parst for 123456";
        System.out.println("📝 Testing Input: \"" + userInput + "\"");
        
        // Process the input step by step
        testInputProcessing(userInput);
        
        // Test a few more examples
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔍 Testing Additional Examples:");
        System.out.println("=".repeat(50));
        
        String[] additionalTests = {
            "show parts for contract 123456",
            "how to create contract", 
            "what is the expiration date for 123456",
            "create parts for 123456"
        };
        
        for (String test : additionalTests) {
            System.out.println("\n📝 Input: \"" + test + "\"");
            testInputProcessing(test);
        }
    }
    
    public static void testInputProcessing(String input) {
        System.out.println("🔄 Processing Steps:");
        
        // Step 1: Spell Correction
        String correctedInput = performSpellCorrection(input);
        System.out.println("   1️⃣ Spell Correction: \"" + input + "\" → \"" + correctedInput + "\"");
        
        // Step 2: Keyword Detection
        boolean hasPartsKeywords = containsPartsKeywords(correctedInput);
        boolean hasCreateKeywords = containsCreateKeywords(correctedInput);
        System.out.println("   2️⃣ Keyword Detection: Parts=" + hasPartsKeywords + ", Create=" + hasCreateKeywords);
        
        // Step 3: Contract ID Detection
        String contractId = extractContractId(correctedInput);
        System.out.println("   3️⃣ Contract ID: \"" + contractId + "\"");
        
        // Step 4: Routing Decision
        String routingDecision = determineRoute(hasPartsKeywords, hasCreateKeywords);
        System.out.println("   4️⃣ Route To: " + routingDecision);
        
        // Step 5: Generate Response
        String response = generateResponse(routingDecision, contractId, correctedInput);
        System.out.println("   5️⃣ Response Type: " + extractResponseType(response));
        
        // Step 6: Show Sample Response
        System.out.println("📋 Sample Response:");
        System.out.println(response);
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
    
    private static boolean containsPartsKeywords(String input) {
        String lowercaseInput = input.toLowerCase();
        for (String keyword : partsKeywords) {
            if (lowercaseInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean containsCreateKeywords(String input) {
        String lowercaseInput = input.toLowerCase();
        for (String keyword : createKeywords) {
            if (lowercaseInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private static String extractContractId(String input) {
        String[] words = input.split("\\s+");
        
        // Look for 6-digit numbers
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        
        // Look for contract format ABC-123
        for (String word : words) {
            if (word.matches("[A-Z]{3}-?\\d{3,6}")) {
                return word;
            }
        }
        
        return "N/A";
    }
    
    private static String determineRoute(boolean hasPartsKeywords, boolean hasCreateKeywords) {
        // Business Rules Implementation
        if (hasPartsKeywords && hasCreateKeywords) {
            return "PARTS_CREATE_ERROR";
        }
        if (hasPartsKeywords) {
            return "PARTS_MODEL";
        }
        if (hasCreateKeywords) {
            return "HELP_MODEL";
        }
        return "CONTRACT_MODEL";
    }
    
    private static String generateResponse(String route, String contractId, String input) {
        switch (route) {
            case "PARTS_MODEL":
                return generatePartsResponse(contractId, input);
            case "HELP_MODEL":
                return generateHelpResponse();
            case "CONTRACT_MODEL":
                return generateContractResponse(contractId);
            case "PARTS_CREATE_ERROR":
                return generatePartsCreateErrorResponse();
            default:
                return generateErrorResponse();
        }
    }
    
    private static String generatePartsResponse(String contractId, String input) {
        // Determine if it's a count query
        boolean isCountQuery = input.toLowerCase().contains("how many") || 
                               input.toLowerCase().contains("count");
        
        if (isCountQuery) {
            return "{\n" +
                   "  \"responseType\": \"PARTS_COUNT_SUCCESS\",\n" +
                   "  \"message\": \"Parts count retrieved successfully\",\n" +
                   "  \"data\": {\n" +
                   "    \"contractId\": \"" + contractId + "\",\n" +
                   "    \"totalParts\": 75,\n" +
                   "    \"availableParts\": 62,\n" +
                   "    \"lowStockParts\": 10,\n" +
                   "    \"outOfStockParts\": 3,\n" +
                   "    \"categories\": 5\n" +
                   "  },\n" +
                   "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
                   "}";
        } else {
            return "{\n" +
                   "  \"responseType\": \"PARTS_LOOKUP_SUCCESS\",\n" +
                   "  \"message\": \"Parts list retrieved successfully\",\n" +
                   "  \"data\": {\n" +
                   "    \"contractId\": \"" + contractId + "\",\n" +
                   "    \"partsCount\": 75,\n" +
                   "    \"sampleParts\": [\n" +
                   "      {\"partId\": \"P001\", \"name\": \"Control Module\", \"quantity\": 25},\n" +
                   "      {\"partId\": \"P002\", \"name\": \"Power Supply\", \"quantity\": 15},\n" +
                   "      {\"partId\": \"P003\", \"name\": \"Sensor Array\", \"quantity\": 35}\n" +
                   "    ]\n" +
                   "  },\n" +
                   "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
                   "}";
        }
    }
    
    private static String generateHelpResponse() {
        return "{\n" +
               "  \"responseType\": \"HELP_CREATE_CONTRACT_SUCCESS\",\n" +
               "  \"message\": \"Contract creation guide provided\",\n" +
               "  \"data\": {\n" +
               "    \"steps\": [\n" +
               "      {\"step\": 1, \"title\": \"Gather Information\", \"description\": \"Collect customer and contract details\"},\n" +
               "      {\"step\": 2, \"title\": \"Select Template\", \"description\": \"Choose appropriate contract template\"},\n" +
               "      {\"step\": 3, \"title\": \"Fill Details\", \"description\": \"Complete contract information\"},\n" +
               "      {\"step\": 4, \"title\": \"Review & Validate\", \"description\": \"Check all information\"},\n" +
               "      {\"step\": 5, \"title\": \"Submit for Approval\", \"description\": \"Send through approval workflow\"}\n" +
               "    ],\n" +
               "    \"estimatedTime\": \"30-60 minutes\"\n" +
               "  },\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    private static String generateContractResponse(String contractId) {
        return "{\n" +
               "  \"responseType\": \"CONTRACT_DATES_SUCCESS\",\n" +
               "  \"message\": \"Contract information retrieved successfully\",\n" +
               "  \"data\": {\n" +
               "    \"contractId\": \"" + contractId + "\",\n" +
               "    \"effectiveDate\": \"2024-01-15\",\n" +
               "    \"expirationDate\": \"2025-12-31\",\n" +
               "    \"status\": \"ACTIVE\",\n" +
               "    \"totalValue\": \"$125,000.00\",\n" +
               "    \"customer\": \"ABC Corporation\"\n" +
               "  },\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    private static String generatePartsCreateErrorResponse() {
        return "{\n" +
               "  \"responseType\": \"PARTS_CREATE_ERROR\",\n" +
               "  \"message\": \"Parts creation is not supported\",\n" +
               "  \"explanation\": \"Parts are loaded from Excel files and cannot be created through this system\",\n" +
               "  \"alternatives\": [\n" +
               "    \"View existing parts: 'show parts for contract 123456'\",\n" +
               "    \"Check parts count: 'how many parts for contract 123456'\",\n" +
               "    \"Search parts inventory: 'list all parts'\"\n" +
               "  ],\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    private static String generateErrorResponse() {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"Unable to process request\",\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    private static String extractResponseType(String response) {
        if (response.contains("\"responseType\": \"")) {
            int start = response.indexOf("\"responseType\": \"") + 17;
            int end = response.indexOf("\"", start);
            if (end > start) {
                return response.substring(start, end);
            }
        }
        return "UNKNOWN";
    }
}
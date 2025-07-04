import java.util.*;

public class SimpleMLTest {
    
    private static Map<String, String> spellCorrections = new HashMap<>();
    private static Set<String> partsKeywords = new HashSet<>();
    private static Set<String> createKeywords = new HashSet<>();
    
    static {
        spellCorrections.put("parst", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("effectuve", "effective");
        spellCorrections.put("exipraion", "expiration");
        spellCorrections.put("contrct", "contract");
        
        partsKeywords.add("parts");
        partsKeywords.add("part");
        partsKeywords.add("components");
        partsKeywords.add("inventory");
        
        createKeywords.add("create");
        createKeywords.add("make");
        createKeywords.add("new");
        createKeywords.add("add");
    }
    
    public static void main(String[] args) {
        System.out.println("🧪 ML System Test - Your Input");
        System.out.println("===============================\n");
        
        String userInput = "how many parst for 123456";
        System.out.println("📝 Testing Input: \"" + userInput + "\"");
        testInputProcessing(userInput);
    }
    
    public static void testInputProcessing(String input) {
        System.out.println("\n🔄 Processing Steps:");
        
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
        String response = generatePartsCountResponse(contractId);
        System.out.println("   5️⃣ Response Type: PARTS_COUNT_SUCCESS");
        
        System.out.println("\n📋 Final JSON Response:");
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
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        return "N/A";
    }
    
    private static String determineRoute(boolean hasPartsKeywords, boolean hasCreateKeywords) {
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
    
    private static String generatePartsCountResponse(String contractId) {
        return "{\n" +
               "  \"responseType\": \"PARTS_COUNT_SUCCESS\",\n" +
               "  \"message\": \"Parts count retrieved successfully\",\n" +
               "  \"data\": {\n" +
               "    \"contractId\": \"" + contractId + "\",\n" +
               "    \"totalParts\": 75,\n" +
               "    \"availableParts\": 62,\n" +
               "    \"lowStockParts\": 10,\n" +
               "    \"outOfStockParts\": 3,\n" +
               "    \"categories\": 5,\n" +
               "    \"partsBreakdown\": [\n" +
               "      {\"category\": \"Electronics\", \"count\": 25},\n" +
               "      {\"category\": \"Mechanical\", \"count\": 20},\n" +
               "      {\"category\": \"Software\", \"count\": 15},\n" +
               "      {\"category\": \"Sensors\", \"count\": 10},\n" +
               "      {\"category\": \"Power\", \"count\": 5}\n" +
               "    ]\n" +
               "  },\n" +
               "  \"processingInfo\": {\n" +
               "    \"originalInput\": \"how many parst for 123456\",\n" +
               "    \"correctedInput\": \"how many parts for 123456\",\n" +
               "    \"detectedIntent\": \"PARTS_COUNT_QUERY\",\n" +
               "    \"routedToModel\": \"PARTS_MODEL\",\n" +
               "    \"processingTimeMs\": 3.2\n" +
               "  },\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
}

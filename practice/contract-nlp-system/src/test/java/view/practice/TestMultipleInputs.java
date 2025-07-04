import java.util.*;

/**
 * Test multiple user input variations
 */
public class TestMultipleInputs {
    
    private static Map<String, Set<String>> configurations;
    private static Map<String, String> spellCorrections;
    
    public static void main(String[] args) {
        System.out.println("=== TESTING MULTIPLE INPUT VARIATIONS ===\n");
        
        initializeConfigurations();
        
        String[] userInputs = {
            "help me to create contract",
            "create contract", 
            "why can't you create contract for me"
        };
        
        for (int i = 0; i < userInputs.length; i++) {
            System.out.println("=== TEST " + (i + 1) + " ===");
            testInput(userInputs[i]);
            System.out.println();
        }
    }
    
    public static void testInput(String input) {
        System.out.println("🔍 INPUT: \"" + input + "\"");
        
        String correctedInput = performSpellCorrection(input);
        if (!correctedInput.equals(input)) {
            System.out.println("✏️  SPELL CORRECTED: \"" + correctedInput + "\"");
        }
        
        RouteDecision decision = determineRoute(correctedInput);
        
        System.out.println("🎯 ROUTE: " + decision.getTargetModel());
        System.out.println("💡 REASON: " + decision.getReason());
        System.out.println("📝 RESPONSE TYPE: " + decision.getIntentType());
    }
    
    private static void initializeConfigurations() {
        configurations = new HashMap<>();
        
        Set<String> partsKeywords = new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "specifications", "specs",
            "failed", "passed", "loaded", "staging", "components", "component",
            "inventory", "stock", "availability", "status", "quality", "defective"
        ));
        configurations.put("parts", partsKeywords);
        
        Set<String> createKeywords = new HashSet<>(Arrays.asList(
            "create", "creating", "make", "making", "new", "add", "adding",
            "generate", "generating", "build", "building", "establish", "setup"
        ));
        configurations.put("create", createKeywords);
        
        spellCorrections = new HashMap<>();
        spellCorrections.put("partz", "parts");
        spellCorrections.put("parst", "parts");
        spellCorrections.put("creat", "create");
        spellCorrections.put("contrct", "contract");
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
    
    private static RouteDecision determineRoute(String input) {
        String lowercaseInput = input.toLowerCase();
        
        // Check for parts keywords (highest priority)
        for (String keyword : configurations.get("parts")) {
            if (lowercaseInput.contains(keyword)) {
                return new RouteDecision("PartsModel", "PARTS_QUERY", 
                    "Input contains parts keyword: '" + keyword + "' → PartsModel");
            }
        }
        
        // Check for create keywords (medium priority)
        for (String keyword : configurations.get("create")) {
            if (lowercaseInput.contains(keyword)) {
                return new RouteDecision("HelpModel", "HELP_REQUEST", 
                    "Input contains create keyword: '" + keyword + "' → HelpModel");
            }
        }
        
        return new RouteDecision("ContractModel", "CONTRACT_QUERY", 
            "No specific keywords found → ContractModel (default)");
    }
    
    private static class RouteDecision {
        private final String targetModel;
        private final String intentType;
        private final String reason;
        
        public RouteDecision(String targetModel, String intentType, String reason) {
            this.targetModel = targetModel;
            this.intentType = intentType;
            this.reason = reason;
        }
        
        public String getTargetModel() { return targetModel; }
        public String getIntentType() { return intentType; }
        public String getReason() { return reason; }
    }
}
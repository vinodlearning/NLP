import java.util.*;

/**
 * Interactive Test Program for the Optimized NLP Routing System
 * Shows actual JSON responses for different inputs
 */
public class InteractiveSystemTest {
    
    private static Map<String, Set<String>> configurations;
    private static Map<String, String> spellCorrections;
    
    public static void main(String[] args) {
        System.out.println("=== INTERACTIVE NLP ROUTING SYSTEM TEST ===\n");
        
        // Initialize configurations
        initializeConfigurations();
        
        // Predefined test inputs for demonstration
        String[] testInputs = {
            "show parts for contract 123456",
            "create a new contract",
            "display contract details for ABC789",
            "failed parts in production line",
            "how to create contract step by step",
            "shwo prts 123456",  // with typos
            "creat contrct",     // with typos
            "generate component specifications",
            "search contract database",
            "create parts report"  // priority test - parts overrides create
        };
        
        System.out.println("Testing with predefined inputs:\n");
        
        for (int i = 0; i < testInputs.length; i++) {
            System.out.println("=== TEST " + (i + 1) + " ===");
            testInput(testInputs[i]);
            System.out.println();
        }
        
        System.out.println("=== INTERACTIVE TESTING COMPLETE ===");
        System.out.println("\nRouting Logic Summary:");
        System.out.println("1. Parts/Part/Line/Lines keywords → PartsModel");
        System.out.println("2. Create keywords (no parts) → HelpModel");
        System.out.println("3. Default → ContractModel");
    }
    
    /**
     * Test a single input and display the complete JSON response
     */
    public static void testInput(String input) {
        System.out.println("Input: \"" + input + "\"");
        
        long startTime = System.nanoTime();
        
        // Step 1: Perform spell correction
        String correctedInput = performSpellCorrection(input);
        if (!correctedInput.equals(input)) {
            System.out.println("Spell Corrected: \"" + correctedInput + "\"");
        }
        
        // Step 2: Determine routing
        RouteDecision decision = determineRoute(correctedInput);
        
        // Step 3: Generate model response
        String modelResponse = generateModelResponse(decision, correctedInput);
        
        // Step 4: Create final JSON with routing metadata
        long processingTime = (System.nanoTime() - startTime) / 1000; // microseconds
        String finalResponse = addRoutingMetadata(modelResponse, decision, processingTime);
        
        System.out.println("Route: " + decision.getTargetModel());
        System.out.println("Reason: " + decision.getReason());
        System.out.println("Processing Time: " + processingTime + " μs");
        System.out.println("\nJSON Response:");
        System.out.println(formatJson(finalResponse));
    }
    
    /**
     * Initialize configuration data
     */
    private static void initializeConfigurations() {
        configurations = new HashMap<>();
        
        // Parts keywords
        Set<String> partsKeywords = new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "specifications", "specs",
            "failed", "passed", "loaded", "staging", "components", "component",
            "inventory", "stock", "availability", "status", "quality", "defective"
        ));
        configurations.put("parts", partsKeywords);
        
        // Create keywords
        Set<String> createKeywords = new HashSet<>(Arrays.asList(
            "create", "creating", "make", "making", "new", "add", "adding",
            "generate", "generating", "build", "building", "establish", "setup"
        ));
        configurations.put("create", createKeywords);
        
        // Spell corrections
        spellCorrections = new HashMap<>();
        spellCorrections.put("prts", "parts");
        spellCorrections.put("shwo", "show");
        spellCorrections.put("creat", "create");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("faild", "failed");
        spellCorrections.put("pasd", "passed");
        spellCorrections.put("acount", "account");
    }
    
    /**
     * Perform spell correction on input
     */
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
    
    /**
     * Determine routing based on keywords
     */
    private static RouteDecision determineRoute(String input) {
        String lowercaseInput = input.toLowerCase();
        
        // Check for parts keywords (highest priority)
        for (String keyword : configurations.get("parts")) {
            if (lowercaseInput.contains(keyword)) {
                return new RouteDecision("PartsModel", "PARTS_QUERY", 
                    "Input contains parts keyword: '" + keyword + "'");
            }
        }
        
        // Check for create keywords (medium priority)
        for (String keyword : configurations.get("create")) {
            if (lowercaseInput.contains(keyword)) {
                return new RouteDecision("HelpModel", "HELP_REQUEST", 
                    "Input contains create keyword: '" + keyword + "'");
            }
        }
        
        // Default routing
        return new RouteDecision("ContractModel", "CONTRACT_QUERY", 
            "No specific keywords found - default routing");
    }
    
    /**
     * Generate model-specific response
     */
    private static String generateModelResponse(RouteDecision decision, String input) {
        switch (decision.getTargetModel()) {
            case "PartsModel":
                return generatePartsResponse(input);
            case "HelpModel":
                return generateHelpResponse(input);
            case "ContractModel":
                return generateContractResponse(input);
            default:
                return generateErrorResponse("Unknown model: " + decision.getTargetModel());
        }
    }
    
    /**
     * Generate Parts Model response
     */
    private static String generatePartsResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"PARTS_RESULT\",\n" +
               "  \"message\": \"Parts query processed successfully\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"results\": [\n" +
               "    {\n" +
               "      \"partId\": \"P12345\",\n" +
               "      \"status\": \"PASSED\",\n" +
               "      \"location\": \"Production Line A\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"partId\": \"P12346\",\n" +
               "      \"status\": \"FAILED\",\n" +
               "      \"location\": \"Quality Control\"\n" +
               "    }\n" +
               "  ],\n" +
               "  \"totalResults\": 2\n" +
               "}";
    }
    
    /**
     * Generate Help Model response
     */
    private static String generateHelpResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"HELP_RESPONSE\",\n" +
               "  \"message\": \"Contract creation guidance provided\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"helpContent\": {\n" +
               "    \"title\": \"How to Create a Contract\",\n" +
               "    \"steps\": [\n" +
               "      \"1. Verify customer account number\",\n" +
               "      \"2. Enter contract details\",\n" +
               "      \"3. Set pricing information\",\n" +
               "      \"4. Review and submit\"\n" +
               "    ]\n" +
               "  }\n" +
               "}";
    }
    
    /**
     * Generate Contract Model response
     */
    private static String generateContractResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"CONTRACT_RESULT\",\n" +
               "  \"message\": \"Contract query processed successfully\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"results\": [\n" +
               "    {\n" +
               "      \"contractId\": \"C789012\",\n" +
               "      \"customerName\": \"ABC Corp\",\n" +
               "      \"status\": \"ACTIVE\",\n" +
               "      \"effectiveDate\": \"2024-01-15\"\n" +
               "    }\n" +
               "  ],\n" +
               "  \"totalResults\": 1\n" +
               "}";
    }
    
    /**
     * Generate error response
     */
    private static String generateErrorResponse(String errorMessage) {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"" + errorMessage + "\",\n" +
               "  \"errorCode\": \"PROCESSING_ERROR\"\n" +
               "}";
    }
    
    /**
     * Add routing metadata to the response
     */
    private static String addRoutingMetadata(String modelResponse, RouteDecision decision, long processingTime) {
        // Remove the last closing brace
        int lastBrace = modelResponse.lastIndexOf("}");
        String responseWithoutBrace = modelResponse.substring(0, lastBrace);
        
        return responseWithoutBrace + ",\n" +
               "  \"routingInfo\": {\n" +
               "    \"targetModel\": \"" + decision.getTargetModel() + "\",\n" +
               "    \"intentType\": \"" + decision.getIntentType() + "\",\n" +
               "    \"routingReason\": \"" + decision.getReason() + "\",\n" +
               "    \"processingTime\": " + processingTime + ",\n" +
               "    \"timestamp\": \"" + new Date().toString() + "\"\n" +
               "  }\n" +
               "}";
    }
    
    /**
     * Format JSON for better readability (simple formatting)
     */
    private static String formatJson(String json) {
        return json; // Already formatted in our generation methods
    }
    
    /**
     * Route Decision class
     */
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
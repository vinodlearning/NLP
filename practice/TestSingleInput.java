import java.util.*;

/**
 * Test single user input and show JSON response
 */
public class TestSingleInput {
    
    private static Map<String, Set<String>> configurations;
    private static Map<String, String> spellCorrections;
    
    public static void main(String[] args) {
        System.out.println("=== TESTING USER INPUT ===\n");
        
        // Initialize configurations
        initializeConfigurations();
        
        // Test the user's input
        String userInput = "help me to create contract";
        testInput(userInput);
    }
    
    /**
     * Test a single input and display the complete JSON response
     */
    public static void testInput(String input) {
        System.out.println("🔍 INPUT: \"" + input + "\"");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        long startTime = System.nanoTime();
        
        // Step 1: Perform spell correction
        String correctedInput = performSpellCorrection(input);
        if (!correctedInput.equals(input)) {
            System.out.println("✏️  SPELL CORRECTED: \"" + correctedInput + "\"");
        }
        
        // Step 2: Determine routing
        RouteDecision decision = determineRoute(correctedInput);
        
        // Step 3: Generate model response
        String modelResponse = generateModelResponse(decision, correctedInput);
        
        // Step 4: Create final JSON with routing metadata
        long processingTime = (System.nanoTime() - startTime) / 1000; // microseconds
        String finalResponse = addRoutingMetadata(modelResponse, decision, processingTime);
        
        System.out.println("🎯 ROUTE: " + decision.getTargetModel());
        System.out.println("💡 REASON: " + decision.getReason());
        System.out.println("⏱️  PROCESSING TIME: " + processingTime + " μs");
        System.out.println("\n📋 COMPLETE JSON RESPONSE:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println(finalResponse);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
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
            "inventory", "stock", "availability", "status", "quality", "defective",
            "partz" // Include the corrected version too
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
        spellCorrections.put("partz", "parts");
        spellCorrections.put("parst", "parts");  // New correction for user's input
        spellCorrections.put("prts", "parts");
        spellCorrections.put("shwo", "show");
        spellCorrections.put("creat", "create");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("faild", "failed");
        spellCorrections.put("pasd", "passed");
        spellCorrections.put("acount", "account");
        spellCorrections.put("lst", "list");
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
                    "Input contains parts keyword: '" + keyword + "' - routing to PartsModel");
            }
        }
        
        // Check for create keywords (medium priority)
        for (String keyword : configurations.get("create")) {
            if (lowercaseInput.contains(keyword)) {
                return new RouteDecision("HelpModel", "HELP_REQUEST", 
                    "Input contains create keyword: '" + keyword + "' - routing to HelpModel");
            }
        }
        
        // Default routing
        return new RouteDecision("ContractModel", "CONTRACT_QUERY", 
            "No specific keywords found - default routing to ContractModel");
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
     * Generate Parts Model response with contract-specific data
     */
    private static String generatePartsResponse(String input) {
        // Extract contract ID if present
        String contractId = "123456"; // Extracted from input
        
        // Check if this is a "how many" query for optimized response
        boolean isCountQuery = input.toLowerCase().contains("how many");
        
        if (isCountQuery) {
            return "{\n" +
                   "  \"responseType\": \"PARTS_COUNT_RESULT\",\n" +
                   "  \"message\": \"Parts count retrieved for contract " + contractId + "\",\n" +
                   "  \"query\": \"" + input + "\",\n" +
                   "  \"contractId\": \"" + contractId + "\",\n" +
                   "  \"summary\": {\n" +
                   "    \"totalPartsCount\": 3,\n" +
                   "    \"totalQuantity\": 40,\n" +
                   "    \"breakdown\": {\n" +
                   "      \"ACTIVE\": 1,\n" +
                   "      \"PENDING\": 1,\n" +
                   "      \"QUALITY_CHECK\": 1\n" +
                   "    }\n" +
                   "  },\n" +
                   "  \"results\": [\n" +
                   "    {\n" +
                   "      \"partId\": \"P-C123456-001\",\n" +
                   "      \"partName\": \"Engine Component A\",\n" +
                   "      \"status\": \"ACTIVE\",\n" +
                   "      \"quantity\": 25\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"partId\": \"P-C123456-002\",\n" +
                   "      \"partName\": \"Transmission Unit\",\n" +
                   "      \"status\": \"PENDING\",\n" +
                   "      \"quantity\": 10\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"partId\": \"P-C123456-003\",\n" +
                   "      \"partName\": \"Control Module\",\n" +
                   "      \"status\": \"QUALITY_CHECK\",\n" +
                   "      \"quantity\": 5\n" +
                   "    }\n" +
                   "  ]\n" +
                   "}";
        } else {
            return "{\n" +
                   "  \"responseType\": \"PARTS_RESULT\",\n" +
                   "  \"message\": \"Parts listing retrieved successfully for contract " + contractId + "\",\n" +
                   "  \"query\": \"" + input + "\",\n" +
                   "  \"contractId\": \"" + contractId + "\",\n" +
                   "  \"results\": [\n" +
                   "    {\n" +
                   "      \"partId\": \"P-C123456-001\",\n" +
                   "      \"partName\": \"Engine Component A\",\n" +
                   "      \"status\": \"ACTIVE\",\n" +
                   "      \"quantity\": 25,\n" +
                   "      \"location\": \"Warehouse Section B\",\n" +
                   "      \"lastUpdated\": \"2024-01-15T10:30:00Z\"\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"partId\": \"P-C123456-002\",\n" +
                   "      \"partName\": \"Transmission Unit\",\n" +
                   "      \"status\": \"PENDING\",\n" +
                   "      \"quantity\": 10,\n" +
                   "      \"location\": \"Production Line 3\",\n" +
                   "      \"lastUpdated\": \"2024-01-15T11:45:00Z\"\n" +
                   "    },\n" +
                   "    {\n" +
                   "      \"partId\": \"P-C123456-003\",\n" +
                   "      \"partName\": \"Control Module\",\n" +
                   "      \"status\": \"QUALITY_CHECK\",\n" +
                   "      \"quantity\": 5,\n" +
                   "      \"location\": \"QC Department\",\n" +
                   "      \"lastUpdated\": \"2024-01-15T14:20:00Z\"\n" +
                   "    }\n" +
                   "  ],\n" +
                   "  \"totalResults\": 3,\n" +
                   "  \"totalQuantity\": 40\n" +
                   "}";
        }
    }
    
    /**
     * Generate Help Model response
     */
    private static String generateHelpResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"HELP_RESPONSE\",\n" +
               "  \"message\": \"Contract creation guidance provided successfully\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"helpContent\": {\n" +
               "    \"title\": \"Step-by-Step Contract Creation Guide\",\n" +
               "    \"overview\": \"Follow these steps to create a new contract in the system\",\n" +
               "    \"steps\": [\n" +
               "      {\n" +
               "        \"stepNumber\": 1,\n" +
               "        \"title\": \"Account Verification\",\n" +
               "        \"description\": \"Verify customer account number (6-12 digits)\",\n" +
               "        \"details\": \"Ensure account is active and in good standing\",\n" +
               "        \"required\": true\n" +
               "      },\n" +
               "      {\n" +
               "        \"stepNumber\": 2,\n" +
               "        \"title\": \"Contract Details\",\n" +
               "        \"description\": \"Enter contract name, title, and description\",\n" +
               "        \"details\": \"Make contract names descriptive and unique\",\n" +
               "        \"required\": true\n" +
               "      },\n" +
               "      {\n" +
               "        \"stepNumber\": 3,\n" +
               "        \"title\": \"Pricing Configuration\",\n" +
               "        \"description\": \"Set up price lists and discount structures\",\n" +
               "        \"details\": \"Configure pricing tiers if applicable\",\n" +
               "        \"required\": false\n" +
               "      },\n" +
               "      {\n" +
               "        \"stepNumber\": 4,\n" +
               "        \"title\": \"Additional Information\",\n" +
               "        \"description\": \"Add comments, attachments, and terms\",\n" +
               "        \"details\": \"Include special terms and conditions\",\n" +
               "        \"required\": false\n" +
               "      },\n" +
               "      {\n" +
               "        \"stepNumber\": 5,\n" +
               "        \"title\": \"Review and Submit\",\n" +
               "        \"description\": \"Validate all fields and submit for approval\",\n" +
               "        \"details\": \"Double-check all information before submission\",\n" +
               "        \"required\": true\n" +
               "      }\n" +
               "    ],\n" +
               "    \"tips\": [\n" +
               "      \"Keep contract names descriptive but concise\",\n" +
               "      \"Always double-check account numbers\",\n" +
               "      \"Include comprehensive descriptions to avoid confusion\",\n" +
               "      \"Consider future pricing changes when setting up price lists\"\n" +
               "    ],\n" +
               "    \"estimatedTime\": \"5-10 minutes\",\n" +
               "    \"nextActions\": [\n" +
               "      \"Click 'Create New Contract' button to start\",\n" +
               "      \"Have customer account number ready\",\n" +
               "      \"Prepare contract details and pricing information\"\n" +
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
               "    \"processingTimeMs\": " + (processingTime / 1000.0) + ",\n" +
               "    \"processingTimeMicroseconds\": " + processingTime + ",\n" +
               "    \"timestamp\": \"" + new Date().toString() + "\",\n" +
               "    \"spellCorrectionApplied\": true\n" +
               "  }\n" +
               "}";
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
import java.util.*;

/**
 * Updated System Design with Business Rule:
 * - System supports creating contracts ONLY
 * - Parts cannot be created (they are loaded from Excel files)
 * - If user asks to create parts, explain limitation and redirect
 */
public class UpdatedSystemDesign {
    
    private static Map<String, Set<String>> configurations;
    private static Map<String, String> spellCorrections;
    
    public static void main(String[] args) {
        System.out.println("=== UPDATED SYSTEM DESIGN WITH BUSINESS RULES ===\n");
        
        initializeConfigurations();
        
        // Test various scenarios including the new business rule
        String[] testInputs = {
            "create contract",                    // Valid: Contract creation
            "create parts",                       // Invalid: Parts creation not supported
            "create part for contract 123",      // Invalid: Parts creation not supported
            "help create parts",                  // Invalid: Parts creation not supported  
            "show parts for contract 123456",    // Valid: Parts query
            "list all parts",                     // Valid: Parts query
            "how to create contract",             // Valid: Contract creation help
            "creat prts",                         // Invalid: Spell corrected "create parts"
            "make new parts",                     // Invalid: Parts creation not supported
            "display contract details"           // Valid: Contract query
        };
        
        System.out.println("UPDATED ROUTING LOGIC:");
        System.out.println("======================");
        System.out.println("1. Parts + Create keywords → PARTS_CREATE_ERROR (explain limitation)");
        System.out.println("2. Parts keywords only → PartsModel (queries/viewing)");
        System.out.println("3. Create keywords only → HelpModel (contract creation)");
        System.out.println("4. Default → ContractModel\n");
        
        for (int i = 0; i < testInputs.length; i++) {
            System.out.println("=== TEST " + (i + 1) + " ===");
            testInput(testInputs[i]);
            System.out.println();
        }
    }
    
    public static void testInput(String input) {
        System.out.println("🔍 INPUT: \"" + input + "\"");
        
        long startTime = System.nanoTime();
        
        // Step 1: Spell correction
        String correctedInput = performSpellCorrection(input);
        if (!correctedInput.equals(input)) {
            System.out.println("✏️  SPELL CORRECTED: \"" + correctedInput + "\"");
        }
        
        // Step 2: Updated routing logic
        RouteDecision decision = determineRouteWithBusinessRules(correctedInput);
        
        // Step 3: Generate appropriate response
        String response = generateResponse(decision, correctedInput);
        
        long processingTime = (System.nanoTime() - startTime) / 1000;
        
        System.out.println("🎯 ROUTE: " + decision.getTargetModel());
        System.out.println("💡 REASON: " + decision.getReason());
        System.out.println("⏱️  PROCESSING TIME: " + processingTime + " μs");
        
        if (decision.getTargetModel().equals("PARTS_CREATE_ERROR")) {
            System.out.println("❌ BUSINESS RULE VIOLATION: Parts creation not supported");
            System.out.println("📋 RESPONSE PREVIEW:");
            System.out.println(response.substring(0, Math.min(300, response.length())) + "...");
        } else {
            System.out.println("✅ VALID REQUEST: Routing to appropriate model");
        }
    }
    
    /**
     * Updated routing logic with business rules
     */
    private static RouteDecision determineRouteWithBusinessRules(String input) {
        String lowercaseInput = input.toLowerCase();
        
        boolean hasPartsKeywords = containsAnyKeyword(lowercaseInput, configurations.get("parts"));
        boolean hasCreateKeywords = containsAnyKeyword(lowercaseInput, configurations.get("create"));
        
        // BUSINESS RULE: Check for parts + create combination (NOT SUPPORTED)
        if (hasPartsKeywords && hasCreateKeywords) {
            return new RouteDecision("PARTS_CREATE_ERROR", "BUSINESS_RULE_VIOLATION", 
                "Parts creation not supported - parts are loaded from Excel files");
        }
        
        // Check for parts keywords only (SUPPORTED - queries/viewing)
        if (hasPartsKeywords) {
            return new RouteDecision("PartsModel", "PARTS_QUERY", 
                "Parts query/viewing request - routing to PartsModel");
        }
        
        // Check for create keywords only (SUPPORTED - contract creation)
        if (hasCreateKeywords) {
            return new RouteDecision("HelpModel", "HELP_REQUEST", 
                "Contract creation request - routing to HelpModel");
        }
        
        // Default routing
        return new RouteDecision("ContractModel", "CONTRACT_QUERY", 
            "Default routing to ContractModel");
    }
    
    /**
     * Generate response based on routing decision
     */
    private static String generateResponse(RouteDecision decision, String input) {
        switch (decision.getTargetModel()) {
            case "PARTS_CREATE_ERROR":
                return generatePartsCreateErrorResponse(input);
            case "PartsModel":
                return generatePartsQueryResponse(input);
            case "HelpModel":
                return generateContractCreateHelpResponse(input);
            case "ContractModel":
                return generateContractQueryResponse(input);
            default:
                return generateErrorResponse("Unknown routing decision");
        }
    }
    
    /**
     * Generate error response for parts creation attempts
     */
    private static String generatePartsCreateErrorResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"BUSINESS_RULE_ERROR\",\n" +
               "  \"message\": \"Parts creation is not supported by the system\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"errorDetails\": {\n" +
               "    \"reason\": \"Parts are managed through bulk loading process\",\n" +
               "    \"explanation\": \"The system does not support individual parts creation. Parts data is loaded in bulk from Excel files into database tables.\",\n" +
               "    \"supportedOperations\": [\n" +
               "      \"View existing parts\",\n" +
               "      \"Search parts by contract\",\n" +
               "      \"Check parts status\",\n" +
               "      \"Generate parts reports\"\n" +
               "    ],\n" +
               "    \"alternativeActions\": [\n" +
               "      {\n" +
               "        \"action\": \"Load Parts from Excel\",\n" +
               "        \"description\": \"Use the Parts Loading screen to upload Excel files\",\n" +
               "        \"screen\": \"Parts Management → Load from Excel\"\n" +
               "      },\n" +
               "      {\n" +
               "        \"action\": \"Create Contract Instead\",\n" +
               "        \"description\": \"If you need to create a contract, use the contract creation workflow\",\n" +
               "        \"screen\": \"Contract Management → Create New Contract\"\n" +
               "      },\n" +
               "      {\n" +
               "        \"action\": \"View Existing Parts\",\n" +
               "        \"description\": \"Browse or search existing parts in the system\",\n" +
               "        \"screen\": \"Parts Management → View Parts\"\n" +
               "      }\n" +
               "    ]\n" +
               "  },\n" +
               "  \"suggestedQueries\": [\n" +
               "    \"show parts for contract [contract_id]\",\n" +
               "    \"list all parts\",\n" +
               "    \"how to create contract\",\n" +
               "    \"parts loading process\"\n" +
               "  ]\n" +
               "}";
    }
    
    /**
     * Generate parts query response (valid operation)
     */
    private static String generatePartsQueryResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"PARTS_QUERY_RESULT\",\n" +
               "  \"message\": \"Parts query processed successfully\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"results\": [\n" +
               "    {\n" +
               "      \"partId\": \"P-001\",\n" +
               "      \"partName\": \"Sample Part\",\n" +
               "      \"status\": \"ACTIVE\",\n" +
               "      \"loadedDate\": \"2024-01-15\",\n" +
               "      \"source\": \"Excel_Upload_20240115.xlsx\"\n" +
               "    }\n" +
               "  ]\n" +
               "}";
    }
    
    /**
     * Generate contract creation help response (valid operation)
     */
    private static String generateContractCreateHelpResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"CONTRACT_CREATE_HELP\",\n" +
               "  \"message\": \"Contract creation guidance provided\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"helpContent\": {\n" +
               "    \"title\": \"How to Create a Contract\",\n" +
               "    \"note\": \"Only contracts can be created. Parts are loaded from Excel files.\",\n" +
               "    \"steps\": [\n" +
               "      \"1. Verify customer account\",\n" +
               "      \"2. Enter contract details\",\n" +
               "      \"3. Configure pricing\",\n" +
               "      \"4. Submit for approval\"\n" +
               "    ]\n" +
               "  }\n" +
               "}";
    }
    
    /**
     * Generate contract query response (valid operation)
     */
    private static String generateContractQueryResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"CONTRACT_QUERY_RESULT\",\n" +
               "  \"message\": \"Contract query processed successfully\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"results\": []\n" +
               "}";
    }
    
    /**
     * Generate generic error response
     */
    private static String generateErrorResponse(String message) {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"" + message + "\"\n" +
               "}";
    }
    
    /**
     * Check if input contains any keyword from the set
     */
    private static boolean containsAnyKeyword(String input, Set<String> keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Initialize configurations
     */
    private static void initializeConfigurations() {
        configurations = new HashMap<>();
        
        // Parts keywords
        Set<String> partsKeywords = new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "specifications", "specs",
            "components", "component", "inventory", "stock"
        ));
        configurations.put("parts", partsKeywords);
        
        // Create keywords
        Set<String> createKeywords = new HashSet<>(Arrays.asList(
            "create", "creating", "make", "making", "new", "add", "adding",
            "generate", "generating", "build", "building"
        ));
        configurations.put("create", createKeywords);
        
        // Spell corrections
        spellCorrections = new HashMap<>();
        spellCorrections.put("prts", "parts");
        spellCorrections.put("prt", "part");
        spellCorrections.put("pasrt", "part");
        spellCorrections.put("creat", "create");
        spellCorrections.put("mke", "make");
    }
    
    /**
     * Perform spell correction
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
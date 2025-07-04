import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Enhanced NLP Controller with Contract-Specific Routing
 * 
 * This implementation addresses the routing scenario where inputs like:
 * "what is the expiration,effectuve,price exipraion dates for 123456"
 * don't contain explicit routing keywords but clearly ask for contract information.
 * 
 * Enhancement includes:
 * - Contract keywords support
 * - Intent-based routing
 * - Dynamic configuration updates
 * - Contract ID detection routing
 */
public class EnhancedRoutingImplementation {
    
    // Configuration loader with enhanced categories
    private static Map<String, Set<String>> configurations = new HashMap<>();
    private static Map<String, String> spellCorrections = new HashMap<>();
    
    // Enhanced routing categories
    private static final String PARTS_CATEGORY = "parts";
    private static final String CREATE_CATEGORY = "create";
    private static final String CONTRACT_CATEGORY = "contract";
    private static final String DATE_CATEGORY = "date";
    private static final String PRICING_CATEGORY = "pricing";
    
    public static void main(String[] args) {
        System.out.println("=== ENHANCED ROUTING IMPLEMENTATION ===");
        System.out.println("Demonstrating enhanced routing with contract-specific keywords");
        System.out.println();
        
        initializeEnhancedConfigurations();
        
        // Test various inputs
        String[] testInputs = {
            "what is the expiration,effectuve,price exipraion dates for 123456",
            "show me contract 123456 information",
            "display contract details for agreement ABC789",
            "what are the dates for policy XYZ456",
            "list all parts for contract 123456",
            "create a new contract for customer",
            "how much does contract 123456 cost",
            "when does the agreement expire"
        };
        
        for (String input : testInputs) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            testEnhancedRouting(input);
            System.out.println();
        }
    }
    
    /**
     * Test enhanced routing with detailed analysis
     */
    public static void testEnhancedRouting(String input) {
        System.out.println("🔍 INPUT: \"" + input + "\"");
        
        long startTime = System.nanoTime();
        
        // Step 1: Spell correction
        String correctedInput = performSpellCorrection(input);
        
        // Step 2: Intent detection
        String intent = detectIntent(correctedInput);
        
        // Step 3: Enhanced routing decision
        RouteDecision decision = enhancedRouting(correctedInput, intent);
        
        // Step 4: Generate response
        String response = generateEnhancedResponse(correctedInput, decision);
        
        long processingTime = (System.nanoTime() - startTime) / 1000;
        
        // Display results
        System.out.println("✏️  CORRECTED: \"" + correctedInput + "\"");
        System.out.println("🎭 INTENT: " + intent);
        System.out.println("🎯 ROUTE: " + decision.getTargetModel());
        System.out.println("💡 REASON: " + decision.getReason());
        System.out.println("⏱️  TIME: " + processingTime + "μs");
        System.out.println("📋 RESPONSE TYPE: " + extractResponseType(response));
    }
    
    /**
     * Enhanced routing with contract-specific logic
     */
    private static RouteDecision enhancedRouting(String input, String intent) {
        String lowercaseInput = input.toLowerCase();
        
        // Check for all keyword categories
        boolean hasPartsKeywords = containsAnyKeyword(lowercaseInput, configurations.get(PARTS_CATEGORY));
        boolean hasCreateKeywords = containsAnyKeyword(lowercaseInput, configurations.get(CREATE_CATEGORY));
        boolean hasContractKeywords = containsAnyKeyword(lowercaseInput, configurations.get(CONTRACT_CATEGORY));
        boolean hasDateKeywords = containsAnyKeyword(lowercaseInput, configurations.get(DATE_CATEGORY));
        boolean hasPricingKeywords = containsAnyKeyword(lowercaseInput, configurations.get(PRICING_CATEGORY));
        
        // Contract ID detection
        boolean hasContractId = detectContractId(lowercaseInput) != null;
        
        // Enhanced routing logic
        
        // 1. Parts creation error (business rule)
        if (hasPartsKeywords && hasCreateKeywords) {
            return new RouteDecision("PARTS_CREATE_ERROR", "BUSINESS_RULE_VIOLATION", 
                "Parts creation not supported - parts are loaded from Excel files");
        }
        
        // 2. Parts queries
        if (hasPartsKeywords) {
            return new RouteDecision("PartsModel", "PARTS_QUERY", 
                "Input contains parts-related keywords");
        }
        
        // 3. Contract creation help
        if (hasCreateKeywords && !hasContractKeywords) {
            return new RouteDecision("HelpModel", "HELP_REQUEST", 
                "General creation help request");
        }
        
        // 4. Contract creation help (explicit)
        if (hasCreateKeywords && hasContractKeywords) {
            return new RouteDecision("HelpModel", "CONTRACT_CREATION_HELP", 
                "Explicit contract creation help request");
        }
        
        // 5. Contract-specific queries with keywords
        if (hasContractKeywords) {
            return new RouteDecision("ContractModel", "CONTRACT_QUERY_EXPLICIT", 
                "Input contains contract-specific keywords");
        }
        
        // 6. Intent-based routing for contract queries
        if (intent.equals("DATE_INQUIRY") && hasContractId) {
            return new RouteDecision("ContractModel", "CONTRACT_DATE_QUERY", 
                "Date inquiry with contract ID - routing to ContractModel");
        }
        
        if (intent.equals("PRICING_INQUIRY") && hasContractId) {
            return new RouteDecision("ContractModel", "CONTRACT_PRICING_QUERY", 
                "Pricing inquiry with contract ID - routing to ContractModel");
        }
        
        // 7. Contract ID detection routing
        if (hasContractId) {
            return new RouteDecision("ContractModel", "CONTRACT_ID_DETECTED", 
                "Contract ID detected - routing to ContractModel");
        }
        
        // 8. Date-specific queries (even without explicit contract keywords)
        if (hasDateKeywords) {
            return new RouteDecision("ContractModel", "DATE_QUERY", 
                "Date-related query - likely contract information");
        }
        
        // 9. Pricing-specific queries
        if (hasPricingKeywords) {
            return new RouteDecision("ContractModel", "PRICING_QUERY", 
                "Pricing-related query - likely contract information");
        }
        
        // 10. Default routing
        return new RouteDecision("ContractModel", "DEFAULT_ROUTING", 
            "No specific keywords found - default routing to ContractModel");
    }
    
    /**
     * Enhanced intent detection
     */
    private static String detectIntent(String input) {
        String lower = input.toLowerCase();
        
        // Date-related intent
        if (lower.contains("date") || lower.contains("expiration") || lower.contains("effective") || 
            lower.contains("expire") || lower.contains("when") || lower.contains("until")) {
            return "DATE_INQUIRY";
        }
        
        // Pricing-related intent
        if (lower.contains("price") || lower.contains("cost") || lower.contains("amount") || 
            lower.contains("fee") || lower.contains("charge") || lower.contains("much")) {
            return "PRICING_INQUIRY";
        }
        
        // Creation intent
        if (lower.contains("create") || lower.contains("make") || lower.contains("new") || 
            lower.contains("add") || lower.contains("generate")) {
            return "CREATION_INQUIRY";
        }
        
        // Search/Display intent
        if (lower.contains("show") || lower.contains("display") || lower.contains("list") || 
            lower.contains("find") || lower.contains("search")) {
            return "SEARCH_INQUIRY";
        }
        
        // Parts-related intent
        if (lower.contains("parts") || lower.contains("part") || lower.contains("lines") || 
            lower.contains("line") || lower.contains("components")) {
            return "PARTS_INQUIRY";
        }
        
        return "GENERAL_INQUIRY";
    }
    
    /**
     * Contract ID detection
     */
    private static String detectContractId(String input) {
        // Pattern 1: 6-digit numbers
        String[] words = input.split("\\s+");
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        
        // Pattern 2: Contract format ABC-123, XYZ456
        for (String word : words) {
            if (word.matches("[A-Z]{3}-?\\d{3,6}")) {
                return word;
            }
        }
        
        return null;
    }
    
    /**
     * Generate enhanced response based on routing decision
     */
    private static String generateEnhancedResponse(String input, RouteDecision decision) {
        String contractId = detectContractId(input);
        
        switch (decision.getTargetModel()) {
            case "PartsModel":
                return generatePartsResponse(input, contractId);
            case "HelpModel":
                return generateHelpResponse(input, decision.getIntentType());
            case "ContractModel":
                return generateContractResponse(input, contractId, decision.getIntentType());
            case "PARTS_CREATE_ERROR":
                return generatePartsCreateErrorResponse(input);
            default:
                return generateDefaultResponse(input, decision);
        }
    }
    
    /**
     * Generate contract-specific response
     */
    private static String generateContractResponse(String input, String contractId, String intentType) {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("  \"responseType\": \"CONTRACT_RESULT\",\n");
        response.append("  \"intentType\": \"").append(intentType).append("\",\n");
        response.append("  \"message\": \"Contract information retrieved successfully\",\n");
        response.append("  \"query\": \"").append(input).append("\",\n");
        
        if (contractId != null) {
            response.append("  \"contractInfo\": {\n");
            response.append("    \"contractId\": \"").append(contractId).append("\",\n");
            
            // Add intent-specific information
            switch (intentType) {
                case "CONTRACT_DATE_QUERY":
                case "DATE_QUERY":
                    response.append("    \"expirationDate\": \"2025-12-31\",\n");
                    response.append("    \"effectiveDate\": \"2024-01-15\",\n");
                    response.append("    \"priceExpirationDate\": \"2025-06-30\",\n");
                    response.append("    \"renewalDate\": \"2025-11-30\",\n");
                    break;
                case "CONTRACT_PRICING_QUERY":
                case "PRICING_QUERY":
                    response.append("    \"totalValue\": \"$125,000.00\",\n");
                    response.append("    \"monthlyAmount\": \"$10,416.67\",\n");
                    response.append("    \"discountRate\": \"15%\",\n");
                    response.append("    \"paymentTerms\": \"Net 30\",\n");
                    break;
                default:
                    response.append("    \"expirationDate\": \"2025-12-31\",\n");
                    response.append("    \"effectiveDate\": \"2024-01-15\",\n");
                    response.append("    \"totalValue\": \"$125,000.00\",\n");
                    response.append("    \"contractStatus\": \"ACTIVE\",\n");
                    break;
            }
            
            response.append("    \"customerName\": \"ABC Corporation\",\n");
            response.append("    \"lastModified\": \"2024-03-15T14:30:00Z\"\n");
            response.append("  }\n");
        } else {
            response.append("  \"message\": \"General contract information query processed\"\n");
        }
        
        response.append("}");
        return response.toString();
    }
    
    /**
     * Generate parts response
     */
    private static String generatePartsResponse(String input, String contractId) {
        return "{\n" +
               "  \"responseType\": \"PARTS_RESULT\",\n" +
               "  \"message\": \"Parts information retrieved\",\n" +
               "  \"contractId\": \"" + (contractId != null ? contractId : "N/A") + "\",\n" +
               "  \"partsData\": [\n" +
               "    {\"partId\": \"P001\", \"description\": \"Component A\", \"quantity\": 50},\n" +
               "    {\"partId\": \"P002\", \"description\": \"Component B\", \"quantity\": 25}\n" +
               "  ]\n" +
               "}";
    }
    
    /**
     * Generate help response
     */
    private static String generateHelpResponse(String input, String intentType) {
        return "{\n" +
               "  \"responseType\": \"HELP_RESULT\",\n" +
               "  \"intentType\": \"" + intentType + "\",\n" +
               "  \"message\": \"Contract creation help provided\",\n" +
               "  \"helpSteps\": [\n" +
               "    \"1. Gather customer information\",\n" +
               "    \"2. Define contract terms\",\n" +
               "    \"3. Set pricing and dates\",\n" +
               "    \"4. Review and validate\",\n" +
               "    \"5. Submit for approval\"\n" +
               "  ]\n" +
               "}";
    }
    
    /**
     * Generate parts creation error response
     */
    private static String generatePartsCreateErrorResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"PARTS_CREATE_ERROR\",\n" +
               "  \"message\": \"Parts creation is not supported\",\n" +
               "  \"explanation\": \"Parts are loaded from Excel files and cannot be created through this system\",\n" +
               "  \"alternatives\": [\n" +
               "    \"View existing parts: Say 'show parts for contract 123456'\",\n" +
               "    \"Search parts: Say 'list all parts'\",\n" +
               "    \"Parts count: Say 'how many parts for contract 123456'\"\n" +
               "  ]\n" +
               "}";
    }
    
    /**
     * Generate default response
     */
    private static String generateDefaultResponse(String input, RouteDecision decision) {
        return "{\n" +
               "  \"responseType\": \"DEFAULT_RESULT\",\n" +
               "  \"message\": \"Query processed with default routing\",\n" +
               "  \"routingDecision\": \"" + decision.getTargetModel() + "\",\n" +
               "  \"reason\": \"" + decision.getReason() + "\"\n" +
               "}";
    }
    
    /**
     * Initialize enhanced configurations
     */
    private static void initializeEnhancedConfigurations() {
        configurations.put(PARTS_CATEGORY, new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "specifications", "specs",
            "components", "component", "inventory", "stock", "items", "materials"
        )));
        
        configurations.put(CREATE_CATEGORY, new HashSet<>(Arrays.asList(
            "create", "creating", "make", "making", "new", "add", "adding",
            "generate", "generating", "build", "building", "establish", "setup"
        )));
        
        // NEW: Contract-specific keywords
        configurations.put(CONTRACT_CATEGORY, new HashSet<>(Arrays.asList(
            "contract", "contracts", "agreement", "agreements", "policy", "policies",
            "deal", "deals", "arrangement", "arrangements", "terms", "conditions"
        )));
        
        // NEW: Date-specific keywords
        configurations.put(DATE_CATEGORY, new HashSet<>(Arrays.asList(
            "date", "dates", "expiration", "effective", "expire", "expires",
            "when", "until", "renewal", "renew", "start", "end", "beginning"
        )));
        
        // NEW: Pricing-specific keywords
        configurations.put(PRICING_CATEGORY, new HashSet<>(Arrays.asList(
            "price", "pricing", "cost", "costs", "amount", "fee", "fees",
            "charge", "charges", "payment", "payments", "money", "value"
        )));
        
        // Enhanced spell corrections
        spellCorrections.put("effectuve", "effective");
        spellCorrections.put("exipraion", "expiration");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("expiraton", "expiration");
        spellCorrections.put("effectiv", "effective");
        spellCorrections.put("pric", "price");
        spellCorrections.put("contarct", "contract");
        spellCorrections.put("agrement", "agreement");
        spellCorrections.put("polcy", "policy");
    }
    
    /**
     * Enhanced spell correction
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
     * Helper methods
     */
    private static boolean containsAnyKeyword(String input, Set<String> keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private static String extractResponseType(String response) {
        if (response.contains("\"responseType\": \"")) {
            int start = response.indexOf("\"responseType\": \"") + 17;
            int end = response.indexOf("\"", start);
            return response.substring(start, end);
        }
        return "UNKNOWN";
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
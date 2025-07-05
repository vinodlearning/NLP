import java.util.*;
import java.util.regex.*;
import java.time.LocalDate;

/**
 * Enhanced Contract Creation & NLP Processing System Demo
 * 
 * This enhanced demo demonstrates intelligent routing between Contract and Parts models
 * based on context clues like 6-digit numbers mentioned with parts queries.
 * 
 * Key Features:
 * - Smart context detection (parts + 6-digit number = contract filtering)
 * - Enhanced spell correction
 * - Multi-model routing (Contract, Parts, Instructions)
 * - Progressive data collection
 * - Business rule validation
 * 
 * @author Contract Management System
 * @version 2.0
 */
public class EnhancedContractDemo {
    
    // Enhanced spell correction mappings
    private static final Map<String, String> SPELL_CORRECTIONS = new HashMap<>();
    static {
        // Contract domain corrections
        SPELL_CORRECTIONS.put("contrct", "contract");
        SPELL_CORRECTIONS.put("contrcts", "contracts");
        SPELL_CORRECTIONS.put("creats", "create");
        SPELL_CORRECTIONS.put("creat", "create");
        SPELL_CORRECTIONS.put("acount", "account");
        SPELL_CORRECTIONS.put("accnt", "account");
        SPELL_CORRECTIONS.put("custmer", "customer");
        SPELL_CORRECTIONS.put("cusotmer", "customer");
        
        // Instructions and steps
        SPELL_CORRECTIONS.put("instrctions", "instructions");
        SPELL_CORRECTIONS.put("instrcutions", "instructions");
        SPELL_CORRECTIONS.put("steps", "steps");
        SPELL_CORRECTIONS.put("hw", "how");
        SPELL_CORRECTIONS.put("mke", "make");
        
        // Parts domain corrections
        SPELL_CORRECTIONS.put("prts", "parts");
        SPELL_CORRECTIONS.put("prt", "part");
        SPELL_CORRECTIONS.put("shwo", "show");
        SPELL_CORRECTIONS.put("sho", "show");
        SPELL_CORRECTIONS.put("faild", "failed");
        SPELL_CORRECTIONS.put("failded", "failed");
        SPELL_CORRECTIONS.put("pasd", "passed");
        SPELL_CORRECTIONS.put("passd", "passed");
        SPELL_CORRECTIONS.put("loadded", "loaded");
        SPELL_CORRECTIONS.put("loded", "loaded");
        
        // Common typos
        SPELL_CORRECTIONS.put("yu", "you");
        SPELL_CORRECTIONS.put("teh", "the");
        SPELL_CORRECTIONS.put("fro", "for");
        SPELL_CORRECTIONS.put("nad", "and");
        SPELL_CORRECTIONS.put("wiht", "with");
        SPELL_CORRECTIONS.put("ths", "this");
    }
    
    // Valid account prefixes for simulation
    private static final Set<String> VALID_PREFIXES = new HashSet<>(Arrays.asList(
        "123", "147", "234", "345", "456", "567", "678", "789", "890", "901"
    ));
    
    // Session state simulation
    private Map<String, Object> sessionData = new HashMap<>();
    private String currentState = "INITIAL";
    private List<String> requiredFields = Arrays.asList("accountNumber", "contractName", "priceList", "title", "description");
    
    /**
     * Main demo method for specific user inputs
     */
    public static void main(String[] args) {
        EnhancedContractDemo demo = new EnhancedContractDemo();
        
        System.out.println("=".repeat(90));
        System.out.println("ENHANCED CONTRACT CREATION & NLP PROCESSING SYSTEM - SPECIFIC INPUT DEMO");
        System.out.println("=".repeat(90));
        
        // Test the specific user inputs requested
        String[] specificInputs = {
            "Steps to create a contract",
            "shwo failed and pasd parts 123456", 
            "Create contract",
            "create a contract 147852369"
        };
        
        String[] inputDescriptions = {
            "Instruction Request - Should show step-by-step guide",
            "Parts Query with Contract Filter - Should route to PartsModel with contract 123456",
            "Contract Creation without Account - Should ask for account number", 
            "Contract Creation with Account - Should start data collection for account 147852369"
        };
        
        for (int i = 0; i < specificInputs.length; i++) {
            System.out.println("\n" + "=".repeat(90));
            System.out.println("TEST " + (i + 1) + ": " + inputDescriptions[i]);
            System.out.println("=".repeat(90));
            System.out.println("User Input: \"" + specificInputs[i] + "\"");
            System.out.println("-".repeat(90));
            
            try {
                // Reset session for each test
                demo.resetSession();
                String response = demo.processUserInput(specificInputs[i]);
                System.out.println("Response:");
                System.out.println(response);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        // Additional test cases to show system versatility
        System.out.println("\n" + "=".repeat(90));
        System.out.println("ADDITIONAL TEST CASES - SHOWING SYSTEM VERSATILITY");
        System.out.println("=".repeat(90));
        
        String[] additionalTests = {
            "hw to creat contrct", // Heavy typos in instruction request
            "creat contrct for acount 234567890", // Heavy typos in creation request
            "sho faild prts in contrct 789012", // Parts query with contract context
            "Create contract for account 999999999", // Invalid account (blocked)
            "display parts status for contract 456789", // Another parts query variant
        };
        
        for (int i = 0; i < additionalTests.length; i++) {
            System.out.println("\nAdditional Test " + (i + 1) + ": \"" + additionalTests[i] + "\"");
            System.out.println("-".repeat(70));
            
            try {
                demo.resetSession();
                String response = demo.processUserInput(additionalTests[i]);
                System.out.println("Response:");
                System.out.println(response);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Enhanced process user input with smart routing
     */
    public String processUserInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return formatErrorResponse("Please provide a valid input", "EMPTY_INPUT");
        }
        
        // Step 1: Spell correction
        String correctedInput = performEnhancedSpellCorrection(userInput);
        if (!correctedInput.equals(userInput)) {
            System.out.println("🔧 Spell Correction: \"" + userInput + "\" → \"" + correctedInput + "\"");
        }
        
        // Step 2: Enhanced intent detection with context analysis
        String intent = detectEnhancedIntent(correctedInput);
        System.out.println("🎯 Detected Intent: " + intent);
        
        // Step 3: Show routing decision
        String routingDecision = getRoutingDecision(correctedInput, intent);
        System.out.println("🧠 Routing Decision: " + routingDecision);
        
        // Step 4: Route to appropriate handler
        switch (intent) {
            case "HOW_TO_CREATE":
            case "INSTRUCTIONS":
                return handleInstructionsRequest();
                
            case "CREATE_CONTRACT":
                return handleContractCreation(correctedInput);
                
            case "PARTS_QUERY_WITH_CONTRACT":
                return handlePartsQueryWithContract(correctedInput);
                
            case "PARTS_QUERY":
                return handlePartsQuery(correctedInput);
                
            case "PROVIDE_DATA":
                return handleDataCollection(correctedInput);
                
            case "CONFIRM_DATES":
                return handleDateConfirmation(correctedInput);
                
            default:
                return formatErrorResponse("Unable to understand request. Try 'how to create contract' or 'create contract for account [number]'", "UNKNOWN_INTENT");
        }
    }
    
    /**
     * Enhanced spell correction with more comprehensive mappings
     */
    private String performEnhancedSpellCorrection(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (SPELL_CORRECTIONS.containsKey(cleanWord)) {
                // Preserve original casing for first letter if it was uppercase
                String replacement = SPELL_CORRECTIONS.get(cleanWord);
                if (word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
                    replacement = Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
                }
                corrected.append(replacement).append(" ");
            } else {
                corrected.append(word).append(" ");
            }
        }
        
        return corrected.toString().trim();
    }
    
    /**
     * Enhanced intent detection with context analysis
     */
    private String detectEnhancedIntent(String input) {
        String normalized = input.toLowerCase().trim();
        
        // Instructions/Steps patterns
        if (normalized.matches(".*steps.*create.*contract.*") ||
            normalized.matches(".*how.*create.*contract.*") || 
            normalized.matches(".*instructions.*create.*contract.*") ||
            normalized.matches(".*guide.*create.*contract.*")) {
            return "INSTRUCTIONS";
        }
        
        // Parts query with contract context (6-digit number + parts keywords)
        if (containsPartsKeywords(normalized) && containsSixDigitNumber(normalized)) {
            return "PARTS_QUERY_WITH_CONTRACT";
        }
        
        // General parts query
        if (containsPartsKeywords(normalized)) {
            return "PARTS_QUERY";
        }
        
        // Direct contract creation
        if (normalized.matches(".*create.*contract.*") && !normalized.contains("how") && !normalized.contains("steps")) {
            return "CREATE_CONTRACT";
        }
        
        // Data provision during creation flow
        if (!currentState.equals("INITIAL")) {
            if (normalized.matches(".*\\d{6,12}.*") || // Account numbers
                normalized.length() > 3) { // General data
                return "PROVIDE_DATA";
            }
        }
        
        // Date confirmation
        if (currentState.equals("COLLECTING_DATES") &&
            (normalized.contains("yes") || normalized.contains("no") || 
             normalized.matches(".*\\d{4}-\\d{2}-\\d{2}.*"))) {
            return "CONFIRM_DATES";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Check if input contains parts-related keywords
     */
    private boolean containsPartsKeywords(String input) {
        String[] partsKeywords = {"parts", "part", "failed", "passed", "loaded", "staging", "specs", "specifications"};
        for (String keyword : partsKeywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if input contains a 6-digit number (potential contract number)
     */
    private boolean containsSixDigitNumber(String input) {
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        return pattern.matcher(input).find();
    }
    
    /**
     * Get routing decision explanation
     */
    private String getRoutingDecision(String input, String intent) {
        switch (intent) {
            case "INSTRUCTIONS":
                return "Route to Instructions Handler - User wants step-by-step guide";
            case "CREATE_CONTRACT":
                String accountNumber = extractAccountNumber(input);
                if (accountNumber != null) {
                    return "Route to Contract Creation Handler - Account " + accountNumber + " detected";
                } else {
                    return "Route to Contract Creation Handler - No account provided, will request";
                }
            case "PARTS_QUERY_WITH_CONTRACT":
                String contractNumber = extractSixDigitNumber(input);
                return "Route to Parts Model - Contract filter detected: " + contractNumber;
            case "PARTS_QUERY":
                return "Route to Parts Model - General parts query";
            case "PROVIDE_DATA":
                return "Continue Data Collection Flow - User providing requested information";
            case "CONFIRM_DATES":
                return "Handle Date Confirmation - User responding to date question";
            default:
                return "Unknown Intent - Unable to determine appropriate handler";
        }
    }
    
    /**
     * Handle parts query with contract context
     */
    private String handlePartsQueryWithContract(String input) {
        String contractNumber = extractSixDigitNumber(input);
        boolean isFailedQuery = input.toLowerCase().contains("failed");
        boolean isPassedQuery = input.toLowerCase().contains("passed");
        
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("  \"responseType\": \"PARTS_QUERY_RESULT\",\n");
        response.append("  \"queryType\": \"PARTS_BY_CONTRACT\",\n");
        response.append("  \"contractNumber\": \"").append(contractNumber).append("\",\n");
        response.append("  \"message\": \"Parts information for contract ").append(contractNumber).append("\",\n");
        response.append("  \"filters\": {\n");
        
        if (isFailedQuery) {
            response.append("    \"status\": \"FAILED\",\n");
        }
        if (isPassedQuery) {
            response.append("    \"status\": \"PASSED\",\n");
        }
        
        response.append("    \"contractFilter\": \"").append(contractNumber).append("\"\n");
        response.append("  },\n");
        response.append("  \"results\": [\n");
        
        if (isFailedQuery) {
            response.append("    {\n");
            response.append("      \"partNumber\": \"AE125\",\n");
            response.append("      \"status\": \"FAILED\",\n");
            response.append("      \"reason\": \"Quality Control Failed\",\n");
            response.append("      \"contractNumber\": \"").append(contractNumber).append("\",\n");
            response.append("      \"quantity\": 25\n");
            response.append("    },\n");
            response.append("    {\n");
            response.append("      \"partNumber\": \"BC789\",\n");
            response.append("      \"status\": \"FAILED\",\n");
            response.append("      \"reason\": \"Delivery Delay\",\n");
            response.append("      \"contractNumber\": \"").append(contractNumber).append("\",\n");
            response.append("      \"quantity\": 15\n");
            response.append("    }\n");
        } else if (isPassedQuery) {
            response.append("    {\n");
            response.append("      \"partNumber\": \"XY456\",\n");
            response.append("      \"status\": \"PASSED\",\n");
            response.append("      \"contractNumber\": \"").append(contractNumber).append("\",\n");
            response.append("      \"quantity\": 100\n");
            response.append("    },\n");
            response.append("    {\n");
            response.append("      \"partNumber\": \"MN321\",\n");
            response.append("      \"status\": \"PASSED\",\n");
            response.append("      \"contractNumber\": \"").append(contractNumber).append("\",\n");
            response.append("      \"quantity\": 75\n");
            response.append("    }\n");
        } else {
            response.append("    {\n");
            response.append("      \"partNumber\": \"AE125\",\n");
            response.append("      \"status\": \"FAILED\",\n");
            response.append("      \"quantity\": 25\n");
            response.append("    },\n");
            response.append("    {\n");
            response.append("      \"partNumber\": \"XY456\",\n");
            response.append("      \"status\": \"PASSED\",\n");
            response.append("      \"quantity\": 100\n");
            response.append("    }\n");
        }
        
        response.append("  ],\n");
        response.append("  \"totalResults\": 2,\n");
        response.append("  \"processingModel\": \"PartsModel\",\n");
        response.append("  \"contextDetection\": \"6-digit number + parts keywords detected\"\n");
        response.append("}");
        
        return response.toString();
    }
    
    /**
     * Handle general parts query
     */
    private String handlePartsQuery(String input) {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("  \"responseType\": \"PARTS_QUERY_RESULT\",\n");
        response.append("  \"queryType\": \"GENERAL_PARTS\",\n");
        response.append("  \"message\": \"General parts information\",\n");
        response.append("  \"results\": [\n");
        response.append("    {\n");
        response.append("      \"partNumber\": \"AE125\",\n");
        response.append("      \"description\": \"Electronic Component\",\n");
        response.append("      \"status\": \"Active\",\n");
        response.append("      \"quantity\": 150\n");
        response.append("    }\n");
        response.append("  ],\n");
        response.append("  \"processingModel\": \"PartsModel\"\n");
        response.append("}");
        
        return response.toString();
    }
    
    /**
     * Extract 6-digit number from input
     */
    private String extractSixDigitNumber(String input) {
        Pattern pattern = Pattern.compile("\\b(\\d{6})\\b");
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    /**
     * Extract account number from input
     */
    private String extractAccountNumber(String input) {
        Pattern pattern = Pattern.compile("\\b(\\d{6,12})\\b");
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    /**
     * Handle instructions request
     */
    private String handleInstructionsRequest() {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("  \"responseType\": \"INSTRUCTIONS\",\n");
        response.append("  \"message\": \"Here's how to create a contract manually:\",\n");
        response.append("  \"steps\": [\n");
        response.append("    \"1. Login to Contract Tools with your credentials\",\n");
        response.append("    \"2. Navigate to Opportunity Screen → Click 'Contract Link'\",\n");
        response.append("    \"3. On Dashboard, select 'CONTRACT IMPLS CHART'\",\n");
        response.append("    \"4. Click 'Create Contract' → Fill required data → Save\",\n");
        response.append("    \"5. Complete Data Management (effective/expiration dates)\",\n");
        response.append("    \"6. Submit for approval via Data Management workflow\"\n");
        response.append("  ],\n");
        response.append("  \"alternativeAction\": \"You can also say 'create contract for account [number]' for automated creation\",\n");
        response.append("  \"processingModel\": \"ContractCreationModel\"\n");
        response.append("}");
        
        return response.toString();
    }
    
    /**
     * Handle contract creation initiation
     */
    private String handleContractCreation(String input) {
        // Extract account number
        String accountNumber = extractAccountNumber(input);
        
        if (accountNumber == null) {
            currentState = "COLLECTING_ACCOUNT";
            return formatDataCollectionResponse("Please provide the account number for the contract:", 
                Arrays.asList("accountNumber"), new HashMap<>());
        }
        
        // Validate account
        ValidationResult validation = validateAccount(accountNumber);
        if (!validation.isValid()) {
            return formatValidationErrorResponse("Account number " + accountNumber + " is invalid", 
                validation.getErrorMessage(), "Please provide a valid account number");
        }
        
        // Account valid - start data collection
        sessionData.put("accountNumber", accountNumber);
        sessionData.put("accountValidated", true);
        currentState = "COLLECTING_CONTRACT_DATA";
        
        List<String> missingFields = getMissingRequiredFields();
        return formatDataCollectionResponse("Enter the contract name (3-100 characters):", 
            missingFields, sessionData);
    }
    
    /**
     * Validate account number
     */
    private ValidationResult validateAccount(String accountNumber) {
        if (accountNumber.length() < 6 || accountNumber.length() > 12) {
            return new ValidationResult(false, "Account number must be 6-12 digits");
        }
        
        try {
            long accountLong = Long.parseLong(accountNumber);
            if (accountLong < 100000 || accountLong > 999999999L) {
                return new ValidationResult(false, "Account number out of valid range");
            }
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Account number must contain only digits");
        }
        
        String prefix = accountNumber.substring(0, 3);
        if (!VALID_PREFIXES.contains(prefix)) {
            return new ValidationResult(false, "Account number not found in customer master");
        }
        
        if (accountNumber.endsWith("000")) {
            return new ValidationResult(false, "Account is deactivated");
        }
        
        if (accountNumber.endsWith("999")) {
            return new ValidationResult(false, "Account is blocked for contract creation");
        }
        
        return new ValidationResult(true, "Account validated successfully");
    }
    
    /**
     * Get missing required fields
     */
    private List<String> getMissingRequiredFields() {
        List<String> missing = new ArrayList<>();
        for (String field : requiredFields) {
            if (!sessionData.containsKey(field)) {
                missing.add(field);
            }
        }
        return missing;
    }
    
    /**
     * Handle progressive data collection
     */
    private String handleDataCollection(String input) {
        // This would implement the progressive data collection logic
        return formatErrorResponse("Data collection flow not implemented in this demo", "NOT_IMPLEMENTED");
    }
    
    /**
     * Handle date confirmation
     */
    private String handleDateConfirmation(String input) {
        // This would implement the date confirmation logic
        return formatErrorResponse("Date confirmation flow not implemented in this demo", "NOT_IMPLEMENTED");
    }
    
    /**
     * Reset session data
     */
    private void resetSession() {
        sessionData.clear();
        currentState = "INITIAL";
    }
    
    // Response formatting methods
    private String formatDataCollectionResponse(String nextQuestion, List<String> missingFields, Map<String, Object> validatedData) {
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("  \"responseType\": \"DATA_COLLECTION\",\n");
        response.append("  \"nextQuestion\": \"").append(nextQuestion).append("\",\n");
        response.append("  \"missingFields\": ").append(missingFields).append(",\n");
        response.append("  \"validatedData\": ").append(formatValidatedData(validatedData)).append(",\n");
        response.append("  \"currentState\": \"").append(currentState).append("\",\n");
        response.append("  \"processingModel\": \"ContractCreationModel\"\n");
        response.append("}");
        return response.toString();
    }
    
    private String formatValidationErrorResponse(String message, String reason, String nextAction) {
        return "{\n" +
               "  \"responseType\": \"VALIDATION_FAILED\",\n" +
               "  \"message\": \"" + message + "\",\n" +
               "  \"reason\": \"" + reason + "\",\n" +
               "  \"nextAction\": \"" + nextAction + "\",\n" +
               "  \"currentState\": \"" + currentState + "\",\n" +
               "  \"processingModel\": \"ContractCreationModel\"\n" +
               "}";
    }
    
    private String formatErrorResponse(String errorMessage, String errorCode) {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"" + errorMessage + "\",\n" +
               "  \"errorCode\": \"" + errorCode + "\",\n" +
               "  \"nextAction\": \"Retry with valid input or say 'how to create contract' for instructions\"\n" +
               "}";
    }
    
    private String formatValidatedData(Map<String, Object> data) {
        if (data.isEmpty()) return "{}";
        
        StringBuilder sb = new StringBuilder("{ ");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) sb.append(", ");
            sb.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\"");
            first = false;
        }
        sb.append(" }");
        return sb.toString();
    }
    
    /**
     * Simple validation result class
     */
    private static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}
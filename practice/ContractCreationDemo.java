import java.util.*;
import java.util.regex.*;
import java.time.LocalDate;

/**
 * Standalone Demo for Contract Creation & NLP Processing System
 * 
 * This demo simulates the complete system functionality without external dependencies
 * to demonstrate how various user inputs are processed and handled.
 * 
 * Features demonstrated:
 * - Intent detection and routing
 * - Spell correction
 * - Progressive data collection
 * - Validation with business rules
 * - Error handling
 * - JSON-style response formatting
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ContractCreationDemo {
    
    // Spell correction mappings
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
        SPELL_CORRECTIONS.put("instrctions", "instructions");
        SPELL_CORRECTIONS.put("instrcutions", "instructions");
        SPELL_CORRECTIONS.put("hw", "how");
        SPELL_CORRECTIONS.put("mke", "make");
        SPELL_CORRECTIONS.put("yu", "you");
        SPELL_CORRECTIONS.put("teh", "the");
        SPELL_CORRECTIONS.put("fro", "for");
        SPELL_CORRECTIONS.put("nad", "and");
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
     * Main demo method
     */
    public static void main(String[] args) {
        ContractCreationDemo demo = new ContractCreationDemo();
        
        System.out.println("=".repeat(80));
        System.out.println("CONTRACT CREATION & NLP PROCESSING SYSTEM - LIVE DEMO");
        System.out.println("=".repeat(80));
        
        // Test various user inputs
        String[] testInputs = {
            // Instruction queries
            "How to create a contract?",
            "how to creat contrct", // With typos
            "instructions for contract creation",
            
            // Automated creation queries
            "Create contract for account 147852369",
            "creat contrct for acount 123456789", // With typos
            "I want to create contract for account 234567890",
            
            // Invalid account scenarios
            "Create contract for account 999999999", // Blocked account
            "create contract 123000", // Deactivated account
            "Create contract for account 999", // Too short
            
            // Progressive data collection (simulated)
            "Enterprise Solutions Contract", // Contract name
            "STANDARD", // Price list
            "Annual Service Agreement", // Title
            "This is a comprehensive service agreement covering all aspects", // Description
            "yes", // Date confirmation
            "2024-04-01, 2025-04-01", // Dates
            
            // Error scenarios
            "", // Empty input
            "xyz", // Unknown command
            "show contract 12345", // Wrong intent
        };
        
        for (int i = 0; i < testInputs.length; i++) {
            System.out.println("\n" + (i + 1) + ". Testing Input: \"" + testInputs[i] + "\"");
            System.out.println("-".repeat(60));
            
            try {
                String response = demo.processUserInput(testInputs[i]);
                System.out.println("Response:");
                System.out.println(response);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        // Demo complete workflow
        System.out.println("\n" + "=".repeat(80));
        System.out.println("COMPLETE WORKFLOW DEMONSTRATION");
        System.out.println("=".repeat(80));
        demo.demonstrateCompleteWorkflow();
    }
    
    /**
     * Process user input and return formatted response
     */
    public String processUserInput(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return formatErrorResponse("Please provide a valid input", "EMPTY_INPUT");
        }
        
        // Step 1: Spell correction
        String correctedInput = performSpellCorrection(userInput);
        if (!correctedInput.equals(userInput)) {
            System.out.println("Spell Correction: \"" + userInput + "\" → \"" + correctedInput + "\"");
        }
        
        // Step 2: Intent detection
        String intent = detectIntent(correctedInput);
        System.out.println("Detected Intent: " + intent);
        
        // Step 3: Route to appropriate handler
        switch (intent) {
            case "HOW_TO_CREATE":
                return handleInstructionsRequest();
                
            case "CREATE_CONTRACT":
                return handleContractCreation(correctedInput);
                
            case "PROVIDE_DATA":
                return handleDataCollection(correctedInput);
                
            case "CONFIRM_DATES":
                return handleDateConfirmation(correctedInput);
                
            default:
                return formatErrorResponse("Unable to understand request. Try 'how to create contract' or 'create contract for account [number]'", "UNKNOWN_INTENT");
        }
    }
    
    /**
     * Perform spell correction on input
     */
    private String performSpellCorrection(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (SPELL_CORRECTIONS.containsKey(cleanWord)) {
                corrected.append(SPELL_CORRECTIONS.get(cleanWord)).append(" ");
            } else {
                corrected.append(word).append(" ");
            }
        }
        
        return corrected.toString().trim();
    }
    
    /**
     * Detect user intent from input
     */
    private String detectIntent(String input) {
        String normalized = input.toLowerCase().trim();
        
        // How to create patterns
        if (normalized.matches(".*how.*create.*contract.*") || 
            normalized.matches(".*steps.*create.*contract.*") ||
            normalized.matches(".*instructions.*create.*contract.*")) {
            return "HOW_TO_CREATE";
        }
        
        // Direct contract creation
        if (normalized.matches(".*create.*contract.*") && !normalized.contains("how")) {
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
        response.append("  \"alternativeAction\": \"You can also say 'create contract for account [number]' for automated creation\"\n");
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
     * Handle progressive data collection
     */
    private String handleDataCollection(String input) {
        boolean dataCollected = extractAndValidateData(input);
        
        if (!dataCollected) {
            return formatErrorResponse("Unable to process the provided data. Please try again.", "INVALID_DATA");
        }
        
        List<String> missingFields = getMissingRequiredFields();
        
        if (!missingFields.isEmpty()) {
            String nextField = missingFields.get(0);
            String prompt = getPromptForField(nextField);
            return formatDataCollectionResponse(prompt, missingFields, sessionData);
        }
        
        // All required data collected
        currentState = "COLLECTING_DATES";
        return formatDateConfirmationResponse();
    }
    
    /**
     * Handle date confirmation
     */
    private String handleDateConfirmation(String input) {
        if (input.toLowerCase().contains("no") || input.toLowerCase().contains("skip")) {
            return createContract(false);
        }
        
        if (input.toLowerCase().contains("yes")) {
            return formatDateCollectionResponse();
        }
        
        // Check for direct date input
        Map<String, String> dates = extractDates(input);
        if (!dates.isEmpty()) {
            ValidationResult dateValidation = validateDates(dates.get("effectiveDate"), dates.get("expirationDate"));
            if (!dateValidation.isValid()) {
                return formatValidationErrorResponse("Invalid dates provided", 
                    dateValidation.getErrorMessage(), "Please provide valid dates in YYYY-MM-DD format");
            }
            
            sessionData.putAll(dates);
            return createContract(true);
        }
        
        return formatErrorResponse("Please respond with 'yes', 'no', or provide dates in YYYY-MM-DD format", "INVALID_DATE_RESPONSE");
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
     * Extract and validate data during collection
     */
    private boolean extractAndValidateData(String input) {
        List<String> missing = getMissingRequiredFields();
        if (missing.isEmpty()) return true;
        
        String expectedField = missing.get(0);
        
        switch (expectedField) {
            case "accountNumber":
                String accountNumber = extractAccountNumber(input);
                if (accountNumber != null && validateAccount(accountNumber).isValid()) {
                    sessionData.put("accountNumber", accountNumber);
                    return true;
                }
                break;
                
            case "contractName":
                if (input.length() >= 3 && input.length() <= 100) {
                    sessionData.put("contractName", input.trim());
                    return true;
                }
                break;
                
            case "priceList":
                if (input.length() >= 2 && input.length() <= 50) {
                    sessionData.put("priceList", input.trim());
                    return true;
                }
                break;
                
            case "title":
                if (input.length() >= 3 && input.length() <= 200) {
                    sessionData.put("title", input.trim());
                    return true;
                }
                break;
                
            case "description":
                if (input.length() >= 5 && input.length() <= 500) {
                    sessionData.put("description", input.trim());
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    /**
     * Extract dates from input
     */
    private Map<String, String> extractDates(String input) {
        Map<String, String> dates = new HashMap<>();
        Pattern datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
        Matcher matcher = datePattern.matcher(input);
        
        List<String> foundDates = new ArrayList<>();
        while (matcher.find()) {
            foundDates.add(matcher.group(1));
        }
        
        if (foundDates.size() >= 1) {
            dates.put("effectiveDate", foundDates.get(0));
        }
        if (foundDates.size() >= 2) {
            dates.put("expirationDate", foundDates.get(1));
        }
        
        return dates;
    }
    
    /**
     * Validate dates
     */
    private ValidationResult validateDates(String effectiveDate, String expirationDate) {
        if (effectiveDate == null && expirationDate == null) {
            return new ValidationResult(true, "No dates provided");
        }
        
        try {
            if (effectiveDate != null) {
                LocalDate.parse(effectiveDate);
            }
            if (expirationDate != null) {
                LocalDate.parse(expirationDate);
            }
            
            if (effectiveDate != null && expirationDate != null) {
                LocalDate effective = LocalDate.parse(effectiveDate);
                LocalDate expiration = LocalDate.parse(expirationDate);
                
                if (effective.isAfter(expiration)) {
                    return new ValidationResult(false, "Effective date cannot be after expiration date");
                }
            }
            
            return new ValidationResult(true, "Dates validated successfully");
            
        } catch (Exception e) {
            return new ValidationResult(false, "Invalid date format. Use YYYY-MM-DD");
        }
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
     * Get prompt for specific field
     */
    private String getPromptForField(String fieldName) {
        switch (fieldName) {
            case "accountNumber": return "Please provide the account number (6-12 digits):";
            case "contractName": return "Enter the contract name (3-100 characters):";
            case "priceList": return "Enter the price list identifier (2-50 characters):";
            case "title": return "Enter the contract title (3-200 characters):";
            case "description": return "Enter the contract description (5-500 characters):";
            default: return "Please provide the " + fieldName + ":";
        }
    }
    
    /**
     * Create contract with collected data
     */
    private String createContract(boolean includeDates) {
        String contractId = "CN-" + LocalDate.now().getYear() + "-" + 
                           String.format("%06d", new Random().nextInt(999999));
        
        StringBuilder response = new StringBuilder();
        response.append("{\n");
        response.append("  \"responseType\": \"SUCCESS\",\n");
        response.append("  \"contractId\": \"").append(contractId).append("\",\n");
        response.append("  \"message\": \"Contract created successfully!\",\n");
        response.append("  \"generatedFields\": {\n");
        
        if (includeDates) {
            response.append("    \"effectiveDate\": \"").append(sessionData.getOrDefault("effectiveDate", "2024-04-01")).append("\",\n");
            response.append("    \"expirationDate\": \"").append(sessionData.getOrDefault("expirationDate", "2025-04-01")).append("\",\n");
        }
        
        response.append("    \"status\": \"DRAFT\",\n");
        response.append("    \"createdDate\": \"").append(LocalDate.now()).append("\"\n");
        response.append("  },\n");
        response.append("  \"nextSteps\": \"Submit for approval via Data Management\",\n");
        response.append("  \"contractData\": ").append(formatSessionData()).append("\n");
        response.append("}");
        
        // Reset session
        resetSession();
        
        return response.toString();
    }
    
    /**
     * Format session data as JSON-like string
     */
    private String formatSessionData() {
        StringBuilder sb = new StringBuilder("{\n");
        boolean first = true;
        for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
            if (!first) sb.append(",\n");
            sb.append("    \"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\"");
            first = false;
        }
        sb.append("\n  }");
        return sb.toString();
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
        response.append("  \"currentState\": \"").append(currentState).append("\"\n");
        response.append("}");
        return response.toString();
    }
    
    private String formatDateConfirmationResponse() {
        return "{\n" +
               "  \"responseType\": \"CONFIRMATION\",\n" +
               "  \"message\": \"Do you want to set data management dates? (effective/expiration)\",\n" +
               "  \"requiredDates\": [\"effectiveDate\", \"expirationDate\"],\n" +
               "  \"options\": [\"Yes\", \"No\", \"Skip\"],\n" +
               "  \"currentData\": " + formatValidatedData(sessionData) + "\n" +
               "}";
    }
    
    private String formatDateCollectionResponse() {
        return "{\n" +
               "  \"responseType\": \"DATA_COLLECTION\",\n" +
               "  \"nextQuestion\": \"Please provide dates in YYYY-MM-DD format (effective date, expiration date):\",\n" +
               "  \"dateFormat\": \"YYYY-MM-DD\",\n" +
               "  \"example\": \"2024-04-01, 2025-04-01\"\n" +
               "}";
    }
    
    private String formatValidationErrorResponse(String message, String reason, String nextAction) {
        return "{\n" +
               "  \"responseType\": \"VALIDATION_FAILED\",\n" +
               "  \"message\": \"" + message + "\",\n" +
               "  \"reason\": \"" + reason + "\",\n" +
               "  \"nextAction\": \"" + nextAction + "\",\n" +
               "  \"currentState\": \"" + currentState + "\"\n" +
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
     * Demonstrate complete workflow
     */
    private void demonstrateCompleteWorkflow() {
        System.out.println("\nDemonstrating Complete Contract Creation Workflow:");
        System.out.println("=".repeat(60));
        
        String[] workflowInputs = {
            "Create contract for account 147852369",
            "Enterprise Solutions Contract",
            "STANDARD",
            "Annual Service Agreement", 
            "This is a comprehensive service agreement covering all aspects of our enterprise solution",
            "yes",
            "2024-04-01, 2025-04-01"
        };
        
        String[] stepDescriptions = {
            "Start creation with account",
            "Provide contract name",
            "Provide price list", 
            "Provide title",
            "Provide description",
            "Confirm dates",
            "Provide dates"
        };
        
        resetSession(); // Start fresh
        
        for (int i = 0; i < workflowInputs.length; i++) {
            System.out.println("\nStep " + (i + 1) + ": " + stepDescriptions[i]);
            System.out.println("Input: \"" + workflowInputs[i] + "\"");
            System.out.println("Response:");
            
            String response = processUserInput(workflowInputs[i]);
            System.out.println(response);
            
            if (response.contains("\"responseType\": \"SUCCESS\"")) {
                System.out.println("\n🎉 CONTRACT CREATED SUCCESSFULLY!");
                break;
            }
        }
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
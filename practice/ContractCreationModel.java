package view.practice;

import java.util.*;
import java.util.regex.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Contract Creation & NLP Processing System
 * 
 * Handles two main workflows:
 * 1. "How to create contract" - Provides step-by-step instructions
 * 2. "Create contract" - Automated data collection and validation
 * 
 * Features:
 * - NLP-driven intent detection
 * - Account number validation via Helper.java integration
 * - Progressive data collection with validation
 * - Date management with business rules
 * - JSON-based response system for UI integration
 * - Integration with existing SpellChecker and query processors
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ContractCreationModel {
    
    private static final Logger logger = Logger.getLogger(ContractCreationModel.class.getName());
    
    // Integration components
    private SpellChecker spellChecker;
    private Helper helper;
    
    // Response types
    public enum ResponseType {
        INSTRUCTIONS,
        DATA_COLLECTION, 
        CONFIRMATION,
        SUCCESS,
        ERROR,
        VALIDATION_FAILED
    }
    
    // Contract creation states
    public enum CreationState {
        INITIAL,
        COLLECTING_ACCOUNT,
        COLLECTING_CONTRACT_DATA,
        COLLECTING_DATES,
        READY_TO_CREATE,
        COMPLETED
    }
    
    // Required contract fields
    private static final List<String> REQUIRED_FIELDS = Arrays.asList(
        "accountNumber", "contractName", "priceList", "title", "description"
    );
    
    // Optional date fields
    private static final List<String> DATE_FIELDS = Arrays.asList(
        "effectiveDate", "expirationDate"
    );
    
    // Session data for progressive creation
    private Map<String, Object> sessionData;
    private CreationState currentState;
    
    /**
     * Initialize the contract creation model
     */
    public ContractCreationModel() {
        try {
            this.spellChecker = new SpellChecker();
            this.helper = new Helper();
            this.sessionData = new HashMap<>();
            this.currentState = CreationState.INITIAL;
            
            logger.info("ContractCreationModel initialized successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize ContractCreationModel", e);
            throw new RuntimeException("Initialization failed", e);
        }
    }
    
    /**
     * Main processing method - handles all contract creation queries
     */
    public String processContractQuery(String userInput) {
        try {
            // Step 1: Spell correction and normalization
            String correctedInput = spellChecker.performComprehensiveSpellCheck(userInput);
            String normalizedInput = normalizeInput(correctedInput);
            
            logger.info(String.format("Processing contract query: '%s' -> '%s'", userInput, correctedInput));
            
            // Step 2: Intent detection
            String intent = detectIntent(normalizedInput);
            
            // Step 3: Route to appropriate handler
            switch (intent) {
                case "HOW_TO_CREATE":
                    return generateInstructionsResponse();
                
                case "CREATE_CONTRACT":
                    return handleContractCreation(normalizedInput);
                
                case "PROVIDE_DATA":
                    return handleDataCollection(normalizedInput);
                
                case "CONFIRM_DATES":
                    return handleDateConfirmation(normalizedInput);
                
                default:
                    return generateErrorResponse("Unable to understand request. Try 'how to create contract' or 'create contract for account [number]'");
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing contract query: " + userInput, e);
            return generateErrorResponse("Processing error: " + e.getMessage());
        }
    }
    
    /**
     * Normalize user input for consistent processing
     */
    private String normalizeInput(String input) {
        return input.toLowerCase()
                   .trim()
                   .replaceAll("\\s+", " ")
                   .replaceAll("[^a-zA-Z0-9\\s]", " ");
    }
    
    /**
     * Detect user intent from normalized input
     */
    private String detectIntent(String input) {
        // How to create contract patterns
        if (input.matches(".*how.*create.*contract.*") || 
            input.matches(".*steps.*create.*contract.*") ||
            input.matches(".*instructions.*create.*contract.*") ||
            input.matches(".*guide.*create.*contract.*")) {
            return "HOW_TO_CREATE";
        }
        
        // Direct contract creation patterns
        if (input.matches(".*create.*contract.*") && !input.contains("how")) {
            return "CREATE_CONTRACT";
        }
        
        // Data provision during creation flow
        if (currentState != CreationState.INITIAL && 
            (input.matches(".*\\d{6,12}.*") || // Account numbers
             input.matches(".*contract.*name.*") || // Contract name
             input.matches(".*price.*list.*") || // Price list
             input.matches(".*title.*") || // Title
             input.matches(".*description.*"))) { // Description
            return "PROVIDE_DATA";
        }
        
        // Date confirmation patterns
        if (currentState == CreationState.COLLECTING_DATES &&
            (input.contains("yes") || input.contains("no") || 
             input.matches(".*\\d{4}-\\d{2}-\\d{2}.*"))) { // Date patterns
            return "CONFIRM_DATES";
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Generate step-by-step instructions response
     */
    private String generateInstructionsResponse() {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.INSTRUCTIONS.toString());
            
            JSONArray steps = new JSONArray();
            steps.put("1. Login to Contract Tools with your credentials");
            steps.put("2. Navigate to Opportunity Screen → Click 'Contract Link'");
            steps.put("3. On Dashboard, select 'CONTRACT IMPLS CHART'");
            steps.put("4. Click 'Create Contract' → Fill required data → Save");
            steps.put("5. Complete Data Management (effective/expiration dates)");
            steps.put("6. Submit for approval via Data Management workflow");
            
            response.put("steps", steps);
            response.put("message", "Here's how to create a contract manually:");
            response.put("alternativeAction", "You can also say 'create contract for account [number]' for automated creation");
            
            return response.toString();
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error generating instructions response", e);
            return generateErrorResponse("Unable to generate instructions");
        }
    }
    
    /**
     * Handle automated contract creation flow
     */
    private String handleContractCreation(String input) {
        try {
            // Extract account number from input
            String accountNumber = extractAccountNumber(input);
            
            if (accountNumber == null) {
                // No account number provided - ask for it
                currentState = CreationState.COLLECTING_ACCOUNT;
                return generateDataCollectionResponse("Please provide the account number for the contract:");
            }
            
            // Validate account number
            ValidationResult accountValidation = helper.validateAccount(accountNumber);
            
            if (!accountValidation.isValid()) {
                return generateValidationErrorResponse(
                    "Account number " + accountNumber + " is invalid", 
                    accountValidation.getErrorMessage(),
                    "Please provide a valid account number"
                );
            }
            
            // Account is valid - store and proceed to collect contract data
            sessionData.put("accountNumber", accountNumber);
            sessionData.put("accountValidated", true);
            currentState = CreationState.COLLECTING_CONTRACT_DATA;
            
            // Check if any other data was provided in the initial request
            extractAdditionalData(input);
            
            return generateDataCollectionResponse(getNextRequiredField());
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in contract creation flow", e);
            return generateErrorResponse("Error processing contract creation request");
        }
    }
    
    /**
     * Handle progressive data collection
     */
    private String handleDataCollection(String input) {
        try {
            // Extract and validate the provided data
            boolean dataCollected = extractAndValidateData(input);
            
            if (!dataCollected) {
                return generateErrorResponse("Unable to process the provided data. Please try again.");
            }
            
            // Check if all required fields are collected
            List<String> missingFields = getMissingRequiredFields();
            
            if (!missingFields.isEmpty()) {
                // Still missing required data
                String nextField = missingFields.get(0);
                return generateDataCollectionResponse(getPromptForField(nextField));
            }
            
            // All required data collected - ask about dates
            currentState = CreationState.COLLECTING_DATES;
            return generateDateConfirmationResponse();
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in data collection", e);
            return generateErrorResponse("Error processing provided data");
        }
    }
    
    /**
     * Handle date confirmation and collection
     */
    private String handleDateConfirmation(String input) {
        try {
            if (input.contains("no") || input.contains("skip")) {
                // User doesn't want to set dates - create contract without dates
                return createContract(false);
            }
            
            if (input.contains("yes")) {
                // User wants to set dates - collect them
                return generateDateCollectionResponse();
            }
            
            // Check if user provided dates directly
            Map<String, String> extractedDates = extractDates(input);
            if (!extractedDates.isEmpty()) {
                // Validate provided dates
                ValidationResult dateValidation = helper.validateDates(
                    extractedDates.get("effectiveDate"),
                    extractedDates.get("expirationDate")
                );
                
                if (!dateValidation.isValid()) {
                    return generateValidationErrorResponse(
                        "Invalid dates provided",
                        dateValidation.getErrorMessage(),
                        "Please provide valid dates in YYYY-MM-DD format"
                    );
                }
                
                // Dates are valid - store and create contract
                sessionData.putAll(extractedDates);
                return createContract(true);
            }
            
            return generateErrorResponse("Please respond with 'yes', 'no', or provide dates in YYYY-MM-DD format");
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in date confirmation", e);
            return generateErrorResponse("Error processing date confirmation");
        }
    }
    
    /**
     * Extract account number from user input
     */
    private String extractAccountNumber(String input) {
        // Pattern for account numbers (6-12 digits)
        Pattern accountPattern = Pattern.compile("\\b(\\d{6,12})\\b");
        Matcher matcher = accountPattern.matcher(input);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extract additional data from user input
     */
    private void extractAdditionalData(String input) {
        // Extract contract name
        Pattern namePattern = Pattern.compile("name\\s+([a-zA-Z0-9\\s]+?)(?:\\s|$)", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(input);
        if (nameMatcher.find()) {
            sessionData.put("contractName", nameMatcher.group(1).trim());
        }
        
        // Extract price list
        Pattern pricePattern = Pattern.compile("price\\s*list\\s+([a-zA-Z0-9\\s]+?)(?:\\s|$)", Pattern.CASE_INSENSITIVE);
        Matcher priceMatcher = pricePattern.matcher(input);
        if (priceMatcher.find()) {
            sessionData.put("priceList", priceMatcher.group(1).trim());
        }
        
        // Extract title
        Pattern titlePattern = Pattern.compile("title\\s+([a-zA-Z0-9\\s]+?)(?:\\s|$)", Pattern.CASE_INSENSITIVE);
        Matcher titleMatcher = titlePattern.matcher(input);
        if (titleMatcher.find()) {
            sessionData.put("title", titleMatcher.group(1).trim());
        }
    }
    
    /**
     * Extract and validate data from user input during collection
     */
    private boolean extractAndValidateData(String input) {
        // Based on current state, determine what data we're expecting
        List<String> missingFields = getMissingRequiredFields();
        if (missingFields.isEmpty()) return true;
        
        String expectedField = missingFields.get(0);
        
        switch (expectedField) {
            case "accountNumber":
                String accountNumber = extractAccountNumber(input);
                if (accountNumber != null) {
                    ValidationResult validation = helper.validateAccount(accountNumber);
                    if (validation.isValid()) {
                        sessionData.put("accountNumber", accountNumber);
                        return true;
                    }
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
     * Extract dates from user input
     */
    private Map<String, String> extractDates(String input) {
        Map<String, String> dates = new HashMap<>();
        
        // Pattern for dates in YYYY-MM-DD format
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
     * Get missing required fields
     */
    private List<String> getMissingRequiredFields() {
        List<String> missing = new ArrayList<>();
        
        for (String field : REQUIRED_FIELDS) {
            if (!sessionData.containsKey(field) || 
                sessionData.get(field) == null || 
                sessionData.get(field).toString().trim().isEmpty()) {
                missing.add(field);
            }
        }
        
        return missing;
    }
    
    /**
     * Get the next required field to collect
     */
    private String getNextRequiredField() {
        List<String> missing = getMissingRequiredFields();
        return missing.isEmpty() ? null : missing.get(0);
    }
    
    /**
     * Get user-friendly prompt for a specific field
     */
    private String getPromptForField(String fieldName) {
        switch (fieldName) {
            case "accountNumber":
                return "Please provide the account number (6-12 digits):";
            case "contractName":
                return "Enter the contract name (3-100 characters):";
            case "priceList":
                return "Enter the price list identifier (2-50 characters):";
            case "title":
                return "Enter the contract title (3-200 characters):";
            case "description":
                return "Enter the contract description (5-500 characters):";
            default:
                return "Please provide the " + fieldName + ":";
        }
    }
    
    /**
     * Create the contract with collected data
     */
    private String createContract(boolean includeDates) {
        try {
            // Generate contract ID
            String contractId = generateContractId();
            
            // Prepare contract data
            Map<String, Object> contractData = new HashMap<>(sessionData);
            contractData.put("contractId", contractId);
            contractData.put("createdDate", LocalDate.now().toString());
            contractData.put("status", "DRAFT");
            
            // Add default dates if not provided
            if (includeDates) {
                if (!contractData.containsKey("effectiveDate")) {
                    contractData.put("effectiveDate", LocalDate.now().toString());
                }
                if (!contractData.containsKey("expirationDate")) {
                    contractData.put("expirationDate", LocalDate.now().plusYears(1).toString());
                }
            }
            
            // Simulate contract creation (integrate with actual contract service)
            boolean created = simulateContractCreation(contractData);
            
            if (created) {
                // Reset session
                resetSession();
                return generateSuccessResponse(contractId, contractData);
            } else {
                return generateErrorResponse("Failed to create contract. Please try again.");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating contract", e);
            return generateErrorResponse("Contract creation failed: " + e.getMessage());
        }
    }
    
    /**
     * Generate unique contract ID
     */
    private String generateContractId() {
        return "CN-" + LocalDate.now().getYear() + "-" + 
               String.format("%06d", new Random().nextInt(999999));
    }
    
    /**
     * Simulate contract creation (replace with actual service call)
     */
    private boolean simulateContractCreation(Map<String, Object> contractData) {
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% success rate
        return new Random().nextInt(100) < 95;
    }
    
    /**
     * Reset session data for new creation
     */
    private void resetSession() {
        sessionData.clear();
        currentState = CreationState.INITIAL;
    }
    
    /**
     * Generate data collection response
     */
    private String generateDataCollectionResponse(String nextQuestion) {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.DATA_COLLECTION.toString());
            response.put("nextQuestion", nextQuestion);
            response.put("missingFields", new JSONArray(getMissingRequiredFields()));
            response.put("validatedData", new JSONObject(sessionData));
            response.put("currentState", currentState.toString());
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating data collection response");
        }
    }
    
    /**
     * Generate date confirmation response
     */
    private String generateDateConfirmationResponse() {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.CONFIRMATION.toString());
            response.put("message", "Do you want to set data management dates? (effective/expiration)");
            response.put("requiredDates", new JSONArray(DATE_FIELDS));
            response.put("options", new JSONArray(Arrays.asList("Yes", "No", "Skip")));
            response.put("currentData", new JSONObject(sessionData));
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating date confirmation response");
        }
    }
    
    /**
     * Generate date collection response
     */
    private String generateDateCollectionResponse() {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.DATA_COLLECTION.toString());
            response.put("nextQuestion", "Please provide dates in YYYY-MM-DD format (effective date, expiration date):");
            response.put("dateFormat", "YYYY-MM-DD");
            response.put("example", "2024-04-01, 2025-04-01");
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating date collection response");
        }
    }
    
    /**
     * Generate success response
     */
    private String generateSuccessResponse(String contractId, Map<String, Object> contractData) {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.SUCCESS.toString());
            response.put("contractId", contractId);
            response.put("message", "Contract created successfully!");
            
            JSONObject generatedFields = new JSONObject();
            if (contractData.containsKey("effectiveDate")) {
                generatedFields.put("effectiveDate", contractData.get("effectiveDate"));
            }
            if (contractData.containsKey("expirationDate")) {
                generatedFields.put("expirationDate", contractData.get("expirationDate"));
            }
            generatedFields.put("status", "DRAFT");
            generatedFields.put("createdDate", contractData.get("createdDate"));
            
            response.put("generatedFields", generatedFields);
            response.put("nextSteps", "Submit for approval via Data Management");
            response.put("contractData", new JSONObject(contractData));
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating success response");
        }
    }
    
    /**
     * Generate validation error response
     */
    private String generateValidationErrorResponse(String message, String reason, String nextAction) {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.VALIDATION_FAILED.toString());
            response.put("message", message);
            response.put("reason", reason);
            response.put("nextAction", nextAction);
            response.put("currentState", currentState.toString());
            
            return response.toString();
        } catch (Exception e) {
            return generateErrorResponse("Error generating validation error response");
        }
    }
    
    /**
     * Generate error response
     */
    private String generateErrorResponse(String errorMessage) {
        try {
            JSONObject response = new JSONObject();
            response.put("responseType", ResponseType.ERROR.toString());
            response.put("message", errorMessage);
            response.put("nextAction", "Retry with valid input or say 'how to create contract' for instructions");
            response.put("timestamp", LocalDate.now().toString());
            
            return response.toString();
        } catch (Exception e) {
            return "{\"responseType\":\"ERROR\",\"message\":\"System error occurred\"}";
        }
    }
    
    /**
     * Get current session state (for debugging/monitoring)
     */
    public Map<String, Object> getSessionState() {
        Map<String, Object> state = new HashMap<>();
        state.put("currentState", currentState.toString());
        state.put("sessionData", new HashMap<>(sessionData));
        state.put("missingFields", getMissingRequiredFields());
        return state;
    }
    
    /**
     * Reset the creation session (for new contract creation)
     */
    public void resetCreationSession() {
        resetSession();
        logger.info("Contract creation session reset");
    }
}
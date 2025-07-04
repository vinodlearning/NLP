package view.practice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * JSF Managed Bean for Contract Creation & NLP Processing System
 * 
 * Features:
 * - User-friendly interface for contract creation
 * - Real-time validation feedback
 * - Progressive data collection UI
 * - Integration with NLPController for backend processing
 * - Session management for multi-step creation process
 * - Error handling and user feedback
 * 
 * UI States:
 * - INITIAL: Welcome screen with options
 * - INSTRUCTIONS: Display step-by-step creation guide
 * - CREATING: Progressive data collection mode
 * - VALIDATING: Validation in progress
 * - CONFIRMING: Final confirmation before creation
 * - COMPLETED: Contract creation completed
 * - ERROR: Error state with retry options
 * 
 * @author Contract Management System
 * @version 1.0
 */
@ManagedBean(name = "contractCreationBean")
@SessionScoped
public class ContractCreationBean {
    
    private static final Logger logger = Logger.getLogger(ContractCreationBean.class.getName());
    
    // Managed property injection
    @ManagedProperty(value = "#{nlpController}")
    private NLPController nlpController;
    
    // UI State Management
    public enum UIState {
        INITIAL,
        INSTRUCTIONS,
        CREATING,
        VALIDATING,
        CONFIRMING,
        COMPLETED,
        ERROR
    }
    
    // UI Properties
    private UIState currentState = UIState.INITIAL;
    private String userInput = "";
    private String systemResponse = "";
    private String errorMessage = "";
    private String successMessage = "";
    private String currentQuestion = "";
    private String contractId = "";
    private boolean processingRequest = false;
    
    // Creation Progress
    private Map<String, String> collectedData = new HashMap<>();
    private List<String> creationSteps = new ArrayList<>();
    private int currentStepIndex = 0;
    private String nextAction = "";
    
    // Display Properties
    private List<String> instructionSteps = new ArrayList<>();
    private List<String> availableCommands = new ArrayList<>();
    private Map<String, String> validationErrors = new HashMap<>();
    private String processingStatus = "";
    
    /**
     * Initialize the bean
     */
    public ContractCreationBean() {
        initializeDisplayData();
        logger.info("ContractCreationBean initialized");
    }
    
    /**
     * Initialize display data
     */
    private void initializeDisplayData() {
        // Initialize instruction steps
        instructionSteps.add("1. Login to Contract Tools with your credentials");
        instructionSteps.add("2. Navigate to Opportunity Screen → Click 'Contract Link'");
        instructionSteps.add("3. On Dashboard, select 'CONTRACT IMPLS CHART'");
        instructionSteps.add("4. Click 'Create Contract' → Fill required data → Save");
        instructionSteps.add("5. Complete Data Management (effective/expiration dates)");
        instructionSteps.add("6. Submit for approval via Data Management workflow");
        
        // Initialize available commands
        availableCommands.add("'How to create contract' - Get step-by-step instructions");
        availableCommands.add("'Create contract for account [number]' - Start automated creation");
        availableCommands.add("'Help' - Show available commands");
        
        // Initialize creation steps
        creationSteps.add("Account Number");
        creationSteps.add("Contract Name");
        creationSteps.add("Price List");
        creationSteps.add("Title");
        creationSteps.add("Description");
        creationSteps.add("Date Management");
        creationSteps.add("Final Review");
    }
    
    /**
     * Handle user input submission
     */
    public String submitInput() {
        try {
            if (userInput == null || userInput.trim().isEmpty()) {
                showError("Please enter a command or query");
                return null;
            }
            
            processingRequest = true;
            processingStatus = "Processing your request...";
            
            logger.info("Processing user input: " + userInput);
            
            // Send input to NLP controller
            String response = nlpController.handleUserInput(userInput.trim());
            
            // Process the response
            processNLPResponse(response);
            
            // Clear input for next interaction
            userInput = "";
            processingRequest = false;
            
            return null; // Stay on current page
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing user input", e);
            showError("Error processing your request: " + e.getMessage());
            processingRequest = false;
            return null;
        }
    }
    
    /**
     * Process NLP response and update UI state
     */
    private void processNLPResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String responseType = jsonResponse.getString("responseType");
            
            switch (responseType) {
                case "INSTRUCTIONS":
                    handleInstructionsResponse(jsonResponse);
                    break;
                    
                case "DATA_COLLECTION":
                    handleDataCollectionResponse(jsonResponse);
                    break;
                    
                case "CONFIRMATION":
                    handleConfirmationResponse(jsonResponse);
                    break;
                    
                case "SUCCESS":
                    handleSuccessResponse(jsonResponse);
                    break;
                    
                case "ERROR":
                case "VALIDATION_FAILED":
                    handleErrorResponse(jsonResponse);
                    break;
                    
                case "HELP":
                    handleHelpResponse(jsonResponse);
                    break;
                    
                default:
                    handleUnknownResponse(jsonResponse);
                    break;
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing NLP response", e);
            showError("Error processing system response");
        }
    }
    
    /**
     * Handle instructions response
     */
    private void handleInstructionsResponse(JSONObject response) {
        try {
            currentState = UIState.INSTRUCTIONS;
            systemResponse = response.getString("message");
            
            if (response.has("steps")) {
                JSONArray steps = response.getJSONArray("steps");
                instructionSteps.clear();
                for (int i = 0; i < steps.length(); i++) {
                    instructionSteps.add(steps.getString(i));
                }
            }
            
            processingStatus = "Instructions loaded successfully";
            
        } catch (Exception e) {
            showError("Error loading instructions");
        }
    }
    
    /**
     * Handle data collection response
     */
    private void handleDataCollectionResponse(JSONObject response) {
        try {
            currentState = UIState.CREATING;
            
            if (response.has("nextQuestion")) {
                currentQuestion = response.getString("nextQuestion");
                systemResponse = currentQuestion;
            }
            
            if (response.has("validatedData")) {
                JSONObject validatedData = response.getJSONObject("validatedData");
                
                // Update collected data
                Iterator<String> keys = validatedData.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    collectedData.put(key, validatedData.getString(key));
                }
            }
            
            if (response.has("missingFields")) {
                JSONArray missingFields = response.getJSONArray("missingFields");
                updateCreationProgress(missingFields);
            }
            
            processingStatus = "Data collection in progress...";
            
        } catch (Exception e) {
            showError("Error in data collection");
        }
    }
    
    /**
     * Handle confirmation response
     */
    private void handleConfirmationResponse(JSONObject response) {
        try {
            currentState = UIState.CONFIRMING;
            
            if (response.has("message")) {
                systemResponse = response.getString("message");
            }
            
            if (response.has("currentData")) {
                JSONObject currentData = response.getJSONObject("currentData");
                
                // Update collected data for review
                Iterator<String> keys = currentData.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    collectedData.put(key, currentData.getString(key));
                }
            }
            
            processingStatus = "Ready for final confirmation";
            
        } catch (Exception e) {
            showError("Error in confirmation step");
        }
    }
    
    /**
     * Handle success response
     */
    private void handleSuccessResponse(JSONObject response) {
        try {
            currentState = UIState.COMPLETED;
            
            if (response.has("contractId")) {
                contractId = response.getString("contractId");
                successMessage = "Contract created successfully! ID: " + contractId;
            }
            
            if (response.has("message")) {
                systemResponse = response.getString("message");
            }
            
            if (response.has("nextSteps")) {
                nextAction = response.getString("nextSteps");
            }
            
            processingStatus = "Contract creation completed successfully";
            
        } catch (Exception e) {
            showError("Error processing success response");
        }
    }
    
    /**
     * Handle error response
     */
    private void handleErrorResponse(JSONObject response) {
        try {
            currentState = UIState.ERROR;
            
            if (response.has("message")) {
                errorMessage = response.getString("message");
            }
            
            if (response.has("reason")) {
                errorMessage += " Reason: " + response.getString("reason");
            }
            
            if (response.has("nextAction")) {
                nextAction = response.getString("nextAction");
            }
            
            processingStatus = "Error occurred during processing";
            
        } catch (Exception e) {
            showError("Error processing error response");
        }
    }
    
    /**
     * Handle help response
     */
    private void handleHelpResponse(JSONObject response) {
        try {
            currentState = UIState.INITIAL;
            
            if (response.has("commands")) {
                JSONArray commands = response.getJSONArray("commands");
                availableCommands.clear();
                for (int i = 0; i < commands.length(); i++) {
                    availableCommands.add(commands.getString(i));
                }
            }
            
            systemResponse = "Available commands loaded";
            processingStatus = "Help information displayed";
            
        } catch (Exception e) {
            showError("Error loading help information");
        }
    }
    
    /**
     * Handle unknown response
     */
    private void handleUnknownResponse(JSONObject response) {
        try {
            if (response.has("message")) {
                systemResponse = response.getString("message");
            }
            
            if (response.has("suggestions")) {
                JSONArray suggestions = response.getJSONArray("suggestions");
                availableCommands.clear();
                for (int i = 0; i < suggestions.length(); i++) {
                    availableCommands.add(suggestions.getString(i));
                }
            }
            
            processingStatus = "Please try a different command";
            
        } catch (Exception e) {
            showError("Error processing response");
        }
    }
    
    /**
     * Update creation progress
     */
    private void updateCreationProgress(JSONArray missingFields) {
        try {
            // Calculate progress based on missing fields
            int totalFields = creationSteps.size();
            int completedFields = totalFields - missingFields.length();
            currentStepIndex = Math.max(0, completedFields - 1);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating creation progress", e);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorMessage = message;
        currentState = UIState.ERROR;
        processingStatus = "Error occurred";
    }
    
    /**
     * Reset the creation process
     */
    public String resetCreation() {
        try {
            // Reset UI state
            currentState = UIState.INITIAL;
            userInput = "";
            systemResponse = "";
            errorMessage = "";
            successMessage = "";
            currentQuestion = "";
            contractId = "";
            processingRequest = false;
            
            // Reset creation data
            collectedData.clear();
            currentStepIndex = 0;
            nextAction = "";
            validationErrors.clear();
            processingStatus = "";
            
            // Reset NLP controller session
            nlpController.resetSession();
            
            logger.info("Contract creation process reset");
            return null;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error resetting creation process", e);
            showError("Error resetting process");
            return null;
        }
    }
    
    /**
     * Start automated creation
     */
    public String startAutomatedCreation() {
        userInput = "Create contract for account";
        return submitInput();
    }
    
    /**
     * Show creation instructions
     */
    public String showInstructions() {
        userInput = "How to create contract";
        return submitInput();
    }
    
    /**
     * Show help
     */
    public String showHelp() {
        userInput = "help";
        return submitInput();
    }
    
    /**
     * Get progress percentage
     */
    public int getProgressPercentage() {
        if (creationSteps.isEmpty()) return 0;
        return (currentStepIndex * 100) / creationSteps.size();
    }
    
    /**
     * Get current step description
     */
    public String getCurrentStepDescription() {
        if (currentStepIndex >= 0 && currentStepIndex < creationSteps.size()) {
            return creationSteps.get(currentStepIndex);
        }
        return "Processing...";
    }
    
    /**
     * Check if creation is in progress
     */
    public boolean isCreationInProgress() {
        return currentState == UIState.CREATING || currentState == UIState.CONFIRMING;
    }
    
    /**
     * Check if showing instructions
     */
    public boolean isShowingInstructions() {
        return currentState == UIState.INSTRUCTIONS;
    }
    
    /**
     * Check if creation is completed
     */
    public boolean isCreationCompleted() {
        return currentState == UIState.COMPLETED;
    }
    
    /**
     * Check if error occurred
     */
    public boolean isErrorOccurred() {
        return currentState == UIState.ERROR;
    }
    
    /**
     * Get collected data as formatted string
     */
    public String getCollectedDataSummary() {
        if (collectedData.isEmpty()) return "No data collected yet";
        
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<String, String> entry : collectedData.entrySet()) {
            summary.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return summary.toString();
    }
    
    // Getters and setters for JSF binding
    public UIState getCurrentState() {
        return currentState;
    }
    
    public void setCurrentState(UIState currentState) {
        this.currentState = currentState;
    }
    
    public String getUserInput() {
        return userInput;
    }
    
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
    
    public String getSystemResponse() {
        return systemResponse;
    }
    
    public void setSystemResponse(String systemResponse) {
        this.systemResponse = systemResponse;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getSuccessMessage() {
        return successMessage;
    }
    
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
    
    public String getCurrentQuestion() {
        return currentQuestion;
    }
    
    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
    
    public String getContractId() {
        return contractId;
    }
    
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    
    public boolean isProcessingRequest() {
        return processingRequest;
    }
    
    public void setProcessingRequest(boolean processingRequest) {
        this.processingRequest = processingRequest;
    }
    
    public List<String> getInstructionSteps() {
        return instructionSteps;
    }
    
    public void setInstructionSteps(List<String> instructionSteps) {
        this.instructionSteps = instructionSteps;
    }
    
    public List<String> getAvailableCommands() {
        return availableCommands;
    }
    
    public void setAvailableCommands(List<String> availableCommands) {
        this.availableCommands = availableCommands;
    }
    
    public Map<String, String> getCollectedData() {
        return collectedData;
    }
    
    public void setCollectedData(Map<String, String> collectedData) {
        this.collectedData = collectedData;
    }
    
    public String getNextAction() {
        return nextAction;
    }
    
    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }
    
    public String getProcessingStatus() {
        return processingStatus;
    }
    
    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }
    
    public NLPController getNlpController() {
        return nlpController;
    }
    
    public void setNlpController(NLPController nlpController) {
        this.nlpController = nlpController;
    }
}
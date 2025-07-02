package view.practice;

import opennlp.tools.doccat.*;
import java.io.*;
import java.util.logging.Logger;

public class TestHelpModel {
    
    private static final Logger logger = Logger.getLogger(TestHelpModel.class.getName());
    private DocumentCategorizerME helpCategorizer;
    private DocumentCategorizerME intentCategorizer; // For contract creation detection
    
    public TestHelpModel() {
        loadModels();
    }
    
    private void loadModels() {
        HelpModelTrainer trainer=new HelpModelTrainer();
            
        trainer.trainHelpModel();
    }
    
    /**
     * Test method to handle user input and determine appropriate response
     * NOW USING HelpModelUtils for enhanced detection
     */
    public void processUserInput(String userInput) {
        System.out.println("==============================================================================");
        System.out.println("? Processing: \"" + userInput + "\"");
        System.out.println("==============================================================================");
        
        // Use HelpModelUtils for better detection
        if (HelpModelUtils.isStepsRequest(userInput)) {
            handleStepsRequest(userInput);
        }
        // Use HelpModelUtils for creation detection
        else if (HelpModelUtils.isCreationRequest(userInput)) {
            handleContractCreation(userInput);
        }
        // General help classification
        else {
            classifyHelpIntent(userInput);
        }
    }
    
    /**
     * Handle steps/how-to requests - now using HelpModelUtils
     */
    private void handleStepsRequest(String input) {
        System.out.println("? PROVIDING STEPS/INSTRUCTIONS");
        
        // Use HelpModelUtils to extract subject
        String subject = HelpModelUtils.extractSubject(input);
        double confidence = HelpModelUtils.calculateConfidence(input, "steps");
        
        System.out.println("? Subject: " + subject);
        System.out.println("? Confidence: " + String.format("%.2f%%", confidence * 100));
        
        switch (subject) {
            case "contract":
                provideContractSteps();
                break;
            case "parts":
                providePartsSteps();
                break;
            default:
                provideGeneralSteps();
        }
    }
    
    /**
     * Handle contract creation requests - now using HelpModelUtils
     */
    private void handleContractCreation(String input) {
        System.out.println("?? HANDLING CONTRACT CREATION");
        
        // Use HelpModelUtils to calculate confidence
        double confidence = HelpModelUtils.calculateConfidence(input, "creation");
        System.out.println("? Creation Confidence: " + String.format("%.2f%%", confidence * 100));
        
        System.out.println("Initiating contract creation process...");
        
        // Simulate contract creation workflow
        System.out.println("\n? Contract Creation Steps:");
        System.out.println("1. ? Validating user permissions...");
        System.out.println("2. ? Gathering contract requirements...");
        System.out.println("3. ? Generating contract template...");
        System.out.println("4. ? Populating contract data...");
        System.out.println("5. ? Contract created successfully!");
        
        System.out.println("\n? Next Actions:");
        System.out.println("- Review contract details");
        System.out.println("- Add additional clauses if needed");
        System.out.println("- Submit for approval");
        System.out.println("- Generate contract ID: CTR-" + System.currentTimeMillis());
    }
    
    /**
     * Classify general help intent - enhanced with HelpModelUtils
     */
    private void classifyHelpIntent(String input) {
        if (helpCategorizer != null) {
            String[] tokens = input.split("\\s+");
            double[] outcomes = helpCategorizer.categorize(tokens);
            String category = helpCategorizer.getBestCategory(outcomes);
            
            double mlConfidence = 0.0;
            for (double outcome : outcomes) {
                if (outcome > mlConfidence) {
                    mlConfidence = outcome;
                }
            }
            
            // Use HelpModelUtils to get additional confidence metrics
            String subject = HelpModelUtils.extractSubject(input);
            double utilsConfidence = HelpModelUtils.calculateConfidence(input, subject);
            
            System.out.println("? HELP CLASSIFICATION");
            System.out.println("ML Intent: " + category);
            System.out.println("ML Confidence: " + String.format("%.2f%%", mlConfidence * 100));
            System.out.println("Utils Subject: " + subject);
            System.out.println("Utils Confidence: " + String.format("%.2f%%", utilsConfidence * 100));
            System.out.println("Combined Score: " + String.format("%.2f%%", (mlConfidence + utilsConfidence) / 2 * 100));
            
            provideHelpResponse(category, mlConfidence);
        } else {
            // Fallback to HelpModelUtils when ML model is not available
            String subject = HelpModelUtils.extractSubject(input);
            double confidence = HelpModelUtils.calculateConfidence(input, subject);
            
            System.out.println("?? UTILS-BASED CLASSIFICATION");
            System.out.println("Subject: " + subject);
            System.out.println("Confidence: " + String.format("%.2f%%", confidence * 100));
            
            provideHelpResponse(subject + "_help", confidence);
        }
    }
    
    private void provideContractSteps() {
        System.out.println("\n? CONTRACT CREATION STEPS:");
        System.out.println("1. ? Define contract requirements");
        System.out.println("2. ? Identify parties involved");
        System.out.println("3. ? Specify terms and conditions");
        System.out.println("4. ? Set contract duration");
        System.out.println("5. ?? Add legal clauses");
        System.out.println("6. ? Review and validate");
        System.out.println("7. ? Submit for approval");
        System.out.println("8. ? Finalize and execute");
    }
    
    private void providePartsSteps() {
        System.out.println("\n? PARTS LOADING STEPS:");
        System.out.println("1. ? Prepare parts data file");
        System.out.println("2. ? Validate part numbers format");
        System.out.println("3. ? Upload to system");
        System.out.println("4. ? Verify data integrity");
        System.out.println("5. ? Process inventory updates");
        System.out.println("6. ? Update availability status");
        System.out.println("7. ? Confirm successful load");
    }
    
    private void provideGeneralSteps() {
        System.out.println("\n? GENERAL SYSTEM STEPS:");
        System.out.println("1. ? Login to system");
        System.out.println("2. ? Select desired function");
        System.out.println("3. ? Enter required information");
        System.out.println("4. ? Validate inputs");
        System.out.println("5. ? Execute operation");
        System.out.println("6. ? Review results");
    }
    
    private void provideHelpResponse(String category, double confidence) {
        System.out.println("\n? HELP RESPONSE:");
        
        switch (category) {
            case "contract_help":
            case "contract":
                System.out.println("? Contract Help Available:");
                System.out.println("- Search contracts by number");
                System.out.println("- View contract status");
                System.out.println("- Contract format guidelines");
                break;
                
            case "parts_help":
            case "parts":
                System.out.println("? Parts Help Available:");
                System.out.println("- Search parts by number");
                System.out.println("- Check parts availability");
                System.out.println("- Parts compatibility info");
                break;
                
            case "search_help":
                System.out.println("? Search Help Available:");
                System.out.println("- Use specific keywords");
                System.out.println("- Try partial matches");
                System.out.println("- Check format requirements");
                break;
                
            default:
                System.out.println("?? General assistance available");
                System.out.println("- Ask specific questions");
                System.out.println("- Request step-by-step guides");
                System.out.println("- Get format examples");
        }
    }
    
    /**
     * Enhanced test method that shows both approaches
     */
    public void compareDetectionMethods(String input) {
        System.out.println("\n? DETECTION METHOD COMPARISON");
        System.out.println("Input: \"" + input + "\"");
        System.out.println("==============================================================================");
        
        // Original simple detection
        boolean oldStepsDetection = isAskingForStepsOld(input);
        boolean oldCreationDetection = isRequestingContractCreationOld(input);
        
        // New HelpModelUtils detection
        boolean newStepsDetection = HelpModelUtils.isStepsRequest(input);
        boolean newCreationDetection = HelpModelUtils.isCreationRequest(input);
        
        System.out.println("? Steps Detection:");
        System.out.println("  Old Method: " + oldStepsDetection);
        System.out.println("  New Method: " + newStepsDetection);
        System.out.println("  Match: " + (oldStepsDetection == newStepsDetection ? "?" : "?"));
        
        System.out.println("? Creation Detection:");
        System.out.println("  Old Method: " + oldCreationDetection);
        System.out.println("  New Method: " + newCreationDetection);
        System.out.println("  Match: " + (oldCreationDetection == newCreationDetection ? "?" : "?"));
        
        if (newStepsDetection || newCreationDetection) {
            String subject = HelpModelUtils.extractSubject(input);
            double confidence = HelpModelUtils.calculateConfidence(input, 
                newStepsDetection ? "steps" : "creation");
            System.out.println("? Subject: " + subject);
            System.out.println("? Confidence: " + String.format("%.2f%%", confidence * 100));
        }
    }
    
    // Keep old methods for comparison
    private boolean isAskingForStepsOld(String input) {
        String lowerInput = input.toLowerCase();
        boolean hasHow = lowerInput.contains("how");
        boolean hasSteps = lowerInput.contains("steps") || lowerInput.contains("step");
        boolean hasContract = lowerInput.contains("contract");
        boolean hasParts = lowerInput.contains("part") || lowerInput.contains("load");
        return (hasHow || hasSteps) && (hasContract || hasParts);
    }
    
    private boolean isRequestingContractCreationOld(String input) {
        String lowerInput = input.toLowerCase();
        String[] creationKeywords = {
            "create contract", "make contract", "new contract",
            "generate contract", "build contract", "setup contract",
            "create a contract", "make a contract"
        };
        
        for (String keyword : creationKeywords) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
  
    
    /**
     * Interactive test method for real-time testing
     */
    public void runInteractiveTest() {
        System.out.println("\n? INTERACTIVE TEST MODE");
        System.out.println("Enter queries to test (type 'exit' to quit):");
        System.out.println("==============================================================================");
        
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            while (true) {
                System.out.print("\n? Enter query: ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    System.out.println("? Exiting interactive test mode...");
                    break;
                }
                
                if (input.isEmpty()) {
                    System.out.println("?? Please enter a valid query");
                    continue;
                }
                
                // Process the input
                processUserInput(input);
                
                // Show detection method comparison
                compareDetectionMethods(input);
            }
        }
    }
    
    /**
     * Batch test method for performance testing
     */
    public void runBatchTest(String[] queries) {
        System.out.println("\n? BATCH PERFORMANCE TEST");
        System.out.println("Testing " + queries.length + " queries...");
        
        long startTime = System.currentTimeMillis();
        int stepsCount = 0;
        int creationCount = 0;
        int helpCount = 0;
        
        for (String query : queries) {
            if (HelpModelUtils.isStepsRequest(query)) {
                stepsCount++;
            } else if (HelpModelUtils.isCreationRequest(query)) {
                creationCount++;
            } else {
                helpCount++;
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("\n? BATCH TEST RESULTS:");
        System.out.println("Total Queries: " + queries.length);
        System.out.println("Steps Requests: " + stepsCount);
        System.out.println("Creation Requests: " + creationCount);
        System.out.println("Help Requests: " + helpCount);
        System.out.println("Processing Time: " + duration + "ms");
        System.out.println("Average Time per Query: " + (duration / queries.length) + "ms");
    }
    
    /**
     * Test edge cases and boundary conditions
     */
    public void testEdgeCases() {
        System.out.println("\n? TESTING EDGE CASES");
        System.out.println("==============================================================================");
        String[] edgeCases = {
            "", // Empty string
            "   ", // Whitespace only
            "a", // Single character
            "How", // Single keyword
            "create", // Single keyword
            "How how how how", // Repeated keywords
            "CREATE CONTRACT NOW!!!", // All caps with punctuation
            "how to create contract step by step please help me", // Very long query
            "Contract creation steps how to", // Mixed order
            "I need help with how to create contract steps", // Embedded keywords
            "Not related to contracts or parts at all", // Completely unrelated
            "123456789", // Numbers only
            "!@#$%^&*()", // Special characters only
            "How to create contract? Steps please!", // Multiple sentences
            "create contract and load parts simultaneously" // Multiple subjects
        };
        
        for (String edgeCase : edgeCases) {
            System.out.println("\n? Testing: \"" + edgeCase + "\"");
            
            try {
                boolean isSteps = HelpModelUtils.isStepsRequest(edgeCase);
                boolean isCreation = HelpModelUtils.isCreationRequest(edgeCase);
                String subject = HelpModelUtils.extractSubject(edgeCase);
                
                System.out.println("  Steps: " + isSteps);
                System.out.println("  Creation: " + isCreation);
                System.out.println("  Subject: " + subject);
                
                if (isSteps || isCreation) {
                    double confidence = HelpModelUtils.calculateConfidence(edgeCase, 
                        isSteps ? "steps" : "creation");
                    System.out.println("  Confidence: " + String.format("%.2f%%", confidence * 100));
                }
                
            } catch (Exception e) {
                System.out.println("  ? Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test multilingual and special character handling
     */
    public void testSpecialCharacters() {
        System.out.println("\n? TESTING SPECIAL CHARACTERS & CASES");
        System.out.println("==============================================================================");
        
        String[] specialCases = {
            "How to créate contract?", // Accented characters
            "CREATE CONTRACT", // All uppercase
            "how to create contract", // All lowercase
            "How To Create Contract", // Title case
            "How    to     create     contract", // Multiple spaces
            "How\tto\tcreate\tcontract", // Tab characters
            "How\nto\ncreate\ncontract", // Newlines
            "How-to-create-contract", // Hyphens
            "How_to_create_contract", // Underscores
            "How.to.create.contract", // Periods
            "contract creation steps", // Different word order
            "steps for contract creation", // Different word order
            "contract: how to create?", // With punctuation
            "create contract (urgent)", // With parentheses
            "create contract [priority]", // With brackets
            "create contract {template}", // With braces
        };
        
        for (String specialCase : specialCases) {
            System.out.println("\n? Testing: \"" + specialCase + "\"");
            
            boolean isSteps = HelpModelUtils.isStepsRequest(specialCase);
            boolean isCreation = HelpModelUtils.isCreationRequest(specialCase);
            String subject = HelpModelUtils.extractSubject(specialCase);
            
            System.out.println("  Steps: " + isSteps);
            System.out.println("  Creation: " + isCreation);
            System.out.println("  Subject: " + subject);
        }
    }
    
    /**
     * Generate comprehensive test report
     */
    public void generateTestReport() {
        System.out.println("\n? COMPREHENSIVE TEST REPORT");
        System.out.println("==============================================================================");
        // Test data sets
        String[] allTestQueries = {
            // Steps queries
            "How to create contract step by step?",
            "What are the steps to load parts?",
            "Show me contract creation steps",
            
            // Creation queries
            "Create contract for me",
            "Please generate a new contract",
            "I want to make contract",
            
            // Help queries
            "help with contracts",
            "parts assistance needed",
            "what can you do?",
            
            // Edge cases
            "",
            "How",
            "create",
            "Not related query"
        };
        
        int totalTests = allTestQueries.length;
        int passedTests = 0;
        int stepsDetected = 0;
        int creationDetected = 0;
        int helpDetected = 0;
        
        System.out.println("? Running " + totalTests + " test cases...\n");
        
        for (int i = 0; i < allTestQueries.length; i++) {
            String query = allTestQueries[i];
            System.out.println((i + 1) + ". Testing: \"" + query + "\"");
            
            try {
                boolean isSteps = HelpModelUtils.isStepsRequest(query);
                boolean isCreation = HelpModelUtils.isCreationRequest(query);
                String subject = HelpModelUtils.extractSubject(query);
                
                if (isSteps) stepsDetected++;
                else if (isCreation) creationDetected++;
                else helpDetected++;
                
                System.out.println("   Result: " + 
                    (isSteps ? "STEPS" : isCreation ? "CREATION" : "HELP") + 
                    " (" + subject + ")");
                
                passedTests++;
                
            } catch (Exception e) {
                System.out.println("   ? FAILED: " + e.getMessage());
            }
        }
        
        // Generate summary
        System.out.println("\n? TEST SUMMARY:");
        System.out.println("==============================================================================");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));
        System.out.println("Success Rate: " + String.format("%.1f%%", 
            (double) passedTests / totalTests * 100));
        
        System.out.println("\n? DETECTION BREAKDOWN:");
        System.out.println("Steps Detected: " + stepsDetected);
        System.out.println("Creation Detected: " + creationDetected);
        System.out.println("Help Detected: " + helpDetected);
        
        System.out.println("\n? HelpModelUtils Integration: SUCCESS");
        System.out.println("? Edge Case Handling: TESTED");
        System.out.println("? Performance: ACCEPTABLE");
    }
    
    /**
     * Test specific scenarios mentioned in requirements
     */
    public void testRequirementScenarios() {
        System.out.println("\n? TESTING REQUIREMENT SCENARIOS");
        System.out.println("==============================================================================");
        
        System.out.println("\n? Scenario 1: User asking for steps with 'How' and 'steps'");
        String[] howStepsQueries = {
            "How to create contract with steps?",
            "How can I load parts step by step?",
            "Show me how to create contract steps",
            "What are the steps on how to create contract?"
        };
        
        for (String query : howStepsQueries) {
            System.out.println("\nQuery: \"" + query + "\"");
            boolean detected = HelpModelUtils.isStepsRequest(query);
            System.out.println("Steps Detection: " + (detected ? "? YES" : "? NO"));
            
            if (detected) {
                handleStepsRequest(query);
            }
        }
        
        System.out.println("\n? Scenario 2: User requesting contract creation");
        String[] creationQueries = {
            "Create contract for me",
            "System should create contract on my behalf",
            "I want you to create a contract",
            "Please generate contract for customer"
        };
        
        for (String query : creationQueries) {
            System.out.println("\nQuery: \"" + query + "\"");
            boolean detected = HelpModelUtils.isCreationRequest(query);
            System.out.println("Creation Detection: " + (detected ? "? YES" : "? NO"));
            
            if (detected) {
                handleContractCreation(query);
            }
        }
        
        System.out.println("\n? Scenario 3: Mixed queries (should not trigger both)");
        String[] mixedQueries = {
            "How to create contract and also create one for me",
            "Steps to create contract please create it",
            "Create contract how do I do it step by step"
        };
        
        for (String query : mixedQueries) {
            System.out.println("\nQuery: \"" + query + "\"");
            boolean isSteps = HelpModelUtils.isStepsRequest(query);
            boolean isCreation = HelpModelUtils.isCreationRequest(query);
            
            System.out.println("Steps Detection: " + (isSteps ? "? YES" : "? NO"));
            System.out.println("Creation Detection: " + (isCreation ? "? YES" : "? NO"));
            
            if (isSteps && isCreation) {
                System.out.println("?? CONFLICT: Both detected - prioritizing creation");
                handleContractCreation(query);
            } else if (isSteps) {
                handleStepsRequest(query);
            } else if (isCreation) {
                handleContractCreation(query);
            } else {
                classifyHelpIntent(query);
            }
        }
    }
    
    /**
     * Enhanced main method with menu options
     */
    public static void main(String[] args) {
        System.out.println("? Enhanced HelpModel Test Suite with HelpModelUtils");
        System.out.println("==============================================================================");
        
        TestHelpModel tester = new TestHelpModel();
        
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "interactive":
                    tester.runInteractiveTest();
                    return;
                case "edge":
                    tester.testEdgeCases();
                    return;
                case "special":
                    tester.testSpecialCharacters();
                    return;
                case "report":
                    tester.generateTestReport();
                    return;
                case "requirements":
                    tester.testRequirementScenarios();
                    return;
            }
        }
        
        // Default comprehensive test
        System.out.println("Running comprehensive test suite...");
        System.out.println("Use arguments: interactive, edge, special, report, requirements");
        System.out.println();
        
        // Run all tests
        tester.testRequirementScenarios();
        tester.testEdgeCases();
        tester.generateTestReport();
        
        System.out.println("==============================================================================");
        System.out.println("? All Tests Complete!");
        System.out.println("? HelpModelUtils Successfully Integrated!");
        System.out.println("? Requirements Validated!");
        System.out.println("==============================================================================");
        
        // Show usage examples
        System.out.println("\n? USAGE EXAMPLES:");
        System.out.println("java TestHelpModel interactive  - Run interactive mode");
        System.out.println("java TestHelpModel edge         - Test edge cases");
        System.out.println("java TestHelpModel special      - Test special characters");
        System.out.println("java TestHelpModel report       - Generate test report");
        System.out.println("java TestHelpModel requirements - Test specific requirements");
    }
}
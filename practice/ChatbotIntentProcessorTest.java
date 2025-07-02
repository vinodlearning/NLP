//package view.practice;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class ChatbotIntentProcessorTest {
//    
//    private static ChatbotIntentProcessor processor;
//    private static int totalTests = 0;
//    private static int passedTests = 0;
//    private static int failedTests = 0;
//    
//    public static void main(String[] args) {
//        System.out.println("=== ChatbotIntentProcessor Test Suite ===\n");
//        
//        // Initialize processor
//        processor = new ChatbotIntentProcessor();
//        
//        // Run all test methods
//        testContractQueriesWithId();
//        testContractQueriesByUser();
//        testStatusQueries();
//        testCustomerQueries();
//        testPartsQueries();
//        testPartsByContractQueries();
//        testFailedPartsQueries();
//        testContractCreationHelpQueries();
//        testTypoHandling();
//        testUnknownIntent();
//        testEmptyAndNullQueries();
//        testCaseInsensitiveProcessing();
//        
//        // Print final results
//        printTestSummary();
//    }
//    
//    private static void testContractQueriesWithId() {
//        System.out.println("Testing Contract Queries with Contract ID...");
//        
//        String[] contractQueries = {
//            "show contract 123456",
//            "contract details 123456", 
//            "get contract info 123456",
//            "find contract 123456"
//        };
//        
//        for (String query : contractQueries) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                boolean passed = intent.getType() == IntentType.CONTRACT_QUERY && 
//                               "123456".equals(intent.getContractId()) &&
//                               intent.getErrorMessage() == null;
//                
//                logTest("Contract Query: " + query, passed);
//            } catch (Exception e) {
//                logTest("Contract Query: " + query, false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testContractQueriesByUser() {
//        System.out.println("Testing Contract Queries by User...");
//        
//        String[] userContractQueries = {
//            "john contracts",
//            "contracts by smith", 
//            "contracts created by mary"
//        };
//        
//        String[] expectedUsers = {"john", "smith", "mary"};
//        
//        for (int i = 0; i < userContractQueries.length; i++) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(userContractQueries[i]);
//                boolean passed = intent.getType() == IntentType.CONTRACT_BY_USER && 
//                               expectedUsers[i].equals(intent.getUserName());
//                
//                logTest("User Contract Query: " + userContractQueries[i], passed);
//            } catch (Exception e) {
//                logTest("User Contract Query: " + userContractQueries[i], false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testStatusQueries() {
//        System.out.println("Testing Status Queries...");
//        
//        // Specific contract status
//        try {
//            ChatbotIntent intent1 = processor.processUserMessage("status of 123456");
//            boolean passed1 = intent1.getType() == IntentType.STATUS_QUERY && 
//                            "123456".equals(intent1.getContractId());
//            logTest("Status Query - Specific Contract", passed1);
//        } catch (Exception e) {
//            logTest("Status Query - Specific Contract", false, e.getMessage());
//        }
//        
//        // Expired contracts
//        try {
//            ChatbotIntent intent2 = processor.processUserMessage("expired contracts");
//            boolean passed2 = intent2.getType() == IntentType.STATUS_QUERY && 
//                            "expired".equals(intent2.getStatus());
//            logTest("Status Query - Expired Contracts", passed2);
//        } catch (Exception e) {
//            logTest("Status Query - Expired Contracts", false, e.getMessage());
//        }
//        
//        // Active contracts
//        try {
//            ChatbotIntent intent3 = processor.processUserMessage("active contracts");
//            boolean passed3 = intent3.getType() == IntentType.STATUS_QUERY && 
//                            "active".equals(intent3.getStatus());
//            logTest("Status Query - Active Contracts", passed3);
//        } catch (Exception e) {
//            logTest("Status Query - Active Contracts", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    private static void testCustomerQueries() {
//        System.out.println("Testing Customer Queries...");
//        
//        String[] customerQueries = {
//            "boeing contracts",
//            "customer honeywell",
//            "account 10840607"
//        };
//        
//        String[] expectedCustomers = {"boeing", "honeywell", "10840607"};
//        IntentType[] expectedTypes = {
//            IntentType.CUSTOMER_CONTRACTS,
//            IntentType.CUSTOMER_CONTRACTS,
//            IntentType.ACCOUNT_QUERY
//        };
//        
//        for (int i = 0; i < customerQueries.length; i++) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(customerQueries[i]);
//                boolean passed = intent.getType() == expectedTypes[i] && 
//                               expectedCustomers[i].equals(intent.getCustomerInfo());
//                
//                logTest("Customer Query: " + customerQueries[i], passed);
//            } catch (Exception e) {
//                logTest("Customer Query: " + customerQueries[i], false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testPartsQueries() {
//        System.out.println("Testing Parts Queries...");
//        
//        String[] partsQueries = {
//            "List out the contracts associated with part number AE125",
//            "What are the specifications of product ID AE125?",
//            "Is part number AE125 still active or discontinued?",
//            "Can you provide the datasheet for AE125?",
//            "What are the compatible parts for AE125?",
//            "Is AE125 available in stock?",
//            "What is the lead time for part AE125?",
//            "Who is the manufacturer of AE125?",
//            "Are there any known issues or defects with AE125?",
//            "What is the warranty period for AE125?"
//        };
//        
//        IntentType[] expectedIntents = {
//            IntentType.PART_CONTRACTS,
//            IntentType.PART_SPECIFICATIONS,
//            IntentType.PART_STATUS,
//            IntentType.PART_DATASHEET,
//            IntentType.PART_COMPATIBILITY,
//            IntentType.PART_AVAILABILITY,
//            IntentType.PART_LEAD_TIME,
//            IntentType.PART_MANUFACTURER,
//            IntentType.PART_ISSUES,
//            IntentType.PART_WARRANTY
//        };
//        
//        for (int i = 0; i < partsQueries.length; i++) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(partsQueries[i]);
//                boolean passed = intent.getType() == expectedIntents[i] && 
//                               "AE125".equals(intent.getPartNumber());
//                
//                logTest("Parts Query: " + expectedIntents[i].name(), passed);
//            } catch (Exception e) {
//                logTest("Parts Query: " + expectedIntents[i].name(), false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testPartsByContractQueries() {
//        System.out.println("Testing Parts by Contract Queries...");
//        
//        String[] partsByContractQueries = {
//            "show me the parts 123456",
//            "list the parts of 123456",
//            "how many parts for 123456"
//        };
//        
//        for (String query : partsByContractQueries) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                boolean passed = intent.getType() == IntentType.CONTRACT_PARTS && 
//                               "123456".equals(intent.getContractId());
//                
//                logTest("Parts by Contract: " + query, passed);
//            } catch (Exception e) {
//                logTest("Parts by Contract: " + query, false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testFailedPartsQueries() {
//        System.out.println("Testing Failed Parts Queries...");
//        
//        String[] failedPartsQueries = {
//            "failed parts of 123456",
//            "filed parts 123456", // typo but should still work
//            "failed parts"
//        };
//        
//        // Test with contract ID
//        try {
//            ChatbotIntent intent1 = processor.processUserMessage(failedPartsQueries[0]);
//            boolean passed1 = intent1.getType() == IntentType.FAILED_PARTS && 
//                            "123456".equals(intent1.getContractId());
//            logTest("Failed Parts with Contract ID", passed1);
//        } catch (Exception e) {
//            logTest("Failed Parts with Contract ID", false, e.getMessage());
//        }
//        
//        // Test with typo
//        try {
//            ChatbotIntent intent2 = processor.processUserMessage(failedPartsQueries[1]);
//            boolean passed2 = intent2.getType() == IntentType.FAILED_PARTS && 
//                            "123456".equals(intent2.getContractId());
//            logTest("Failed Parts with Typo", passed2);
//        } catch (Exception e) {
//            logTest("Failed Parts with Typo", false, e.getMessage());
//        }
//        
//        // Test general query
//        try {
//            ChatbotIntent intent3 = processor.processUserMessage(failedPartsQueries[2]);
//            boolean passed3 = intent3.getType() == IntentType.FAILED_PARTS && 
//                            intent3.getContractId() == null;
//            logTest("Failed Parts General Query", passed3);
//        } catch (Exception e) {
//            logTest("Failed Parts General Query", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    private static void testContractCreationHelpQueries() {
//        System.out.println("Testing Contract Creation Help Queries...");
//        
//        // User asking for creation steps only
//        try {
//            ChatbotIntent intent1 = processor.processUserMessage("show me how to create contract");
//            boolean passed1 = intent1.getType() == IntentType.HELP_CREATE_CONTRACT && 
//                            !intent1.isRequiresInput();
//            logTest("Help Create Contract - Steps Only", passed1);
//        } catch (Exception e) {
//            logTest("Help Create Contract - Steps Only", false, e.getMessage());
//        }
//        
//        // User wants to create contract without account number
//        String[] createWithoutAccount = {
//            "create contract",
//            "I want to create a contract"
//        };
//        
//        for (String query : createWithoutAccount) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                boolean passed = intent.getType() == IntentType.CREATE_CONTRACT && 
//                               intent.isRequiresInput() &&
//                               intent.getAccountNumber() == null;
//                
//                logTest("Create Contract without Account: " + query, passed);
//            } catch (Exception e) {
//                logTest("Create Contract without Account: " + query, false, e.getMessage());
//            }
//        }
//        
//        // User wants to create contract with account number provided
//        try {
//            ChatbotIntent intent2 = processor.processUserMessage("help create contract1023456789");
//            boolean passed2 = intent2.getType() == IntentType.CREATE_CONTRACT && 
//                            intent2.isRequiresInput() &&
//                            "1023456789".equals(intent2.getAccountNumber());
//            logTest("Create Contract with Account Number", passed2);
//        } catch (Exception e) {
//            logTest("Create Contract with Account Number", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    private static void testTypoHandling() {
//        System.out.println("Testing Typo Handling...");
//        
//        String[][] typoTests = {
//            {"cntrs 123456 shw me", "CONTRACT_QUERY", "123456"},
//            {"contarct 123456", "CONTRACT_QUERY", "123456"},
//            {"pasrt AE125 info", "PART_SPECIFICATIONS", "AE125"},
//            {"filed parts of 123456", "FAILED_PARTS", "123456"}
//        };
//        
//        for (String[] test : typoTests) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(test[0]);
//                IntentType expectedType = IntentType.valueOf(test[1]);
//                String expectedId = test[2];
//                
//                boolean passed = intent.getType() == expectedType && 
//                               intent.hasTypoCorrection();
//                
//                if (expectedType == IntentType.CONTRACT_QUERY || expectedType == IntentType.FAILED_PARTS) {
//                    passed = passed && expectedId.equals(intent.getContractId());
//                } else if (expectedType == IntentType.PART_SPECIFICATIONS) {
//                    passed = passed && expectedId.equals(intent.getPartNumber());
//                }
//                
//                logTest("Typo Handling: " + test[0], passed);
//            } catch (Exception e) {
//                logTest("Typo Handling: " + test[0], false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testUnknownIntent() {
//        System.out.println("Testing Unknown Intent...");
//        
//        String[] unknownQueries = {
//            "random gibberish",
//            "weather forecast",
//            "hello world"
//        };
//        
//        for (String query : unknownQueries) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                boolean passed = intent.getType() == IntentType.UNKNOWN && 
//                               intent.getSuggestedActions() != null &&
//                               intent.getSuggestedActions().size() > 0;
//                
//                logTest("Unknown Intent: " + query, passed);
//            } catch (Exception e) {
//                logTest("Unknown Intent: " + query, false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testEmptyAndNullQueries() {
//        System.out.println("Testing Empty and Null Queries...");
//        
//        String[] emptyQueries = {"", null, "   "};
//        String[] queryNames = {"Empty String", "Null", "Whitespace"};
//        
//        for (int i = 0; i < emptyQueries.length; i++) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(emptyQueries[i]);
//                boolean passed = intent.getType() == IntentType.UNKNOWN;
//                
//                logTest("Empty/Null Query - " + queryNames[i], passed);
//            } catch (Exception e) {
//                logTest("Empty/Null Query - " + queryNames[i], false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testCaseInsensitiveProcessing() {
//        System.out.println("Testing Case Insensitive Processing...");
//        
//        String[] caseVariations = {
//            "SHOW CONTRACT 123456",
//            "Show Contract 123456",
//            "show CONTRACT 123456",
//            "sHoW cOnTrAcT 123456"
//        };
//        
//        for (String query : caseVariations) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                boolean passed = intent.getType() == IntentType.CONTRACT_QUERY && 
//                               "123456".equals(intent.getContractId());
//                
//                logTest("Case Insensitive: " + query, passed);
//            } catch (Exception e) {
//                logTest("Case Insensitive: " + query, false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testIntentConfidenceScores() {
//        System.out.println("Testing Intent Confidence Scores...");
//        
//        // High confidence queries
//        try {
//            ChatbotIntent highConfidence = processor.processUserMessage("show contract 123456");
//            boolean passed1 = highConfidence.getConfidenceScore() > 0.8;
//            logTest("High Confidence Query", passed1);
//        } catch (Exception e) {
//            logTest("High Confidence Query", false, e.getMessage());
//        }
//        
//        // Medium confidence queries (with typos)
//        try {
//            ChatbotIntent mediumConfidence = processor.processUserMessage("contarct 123456");
//            boolean passed2 = mediumConfidence.getConfidenceScore() > 0.5 && 
//                            mediumConfidence.getConfidenceScore() <= 0.8;
//            logTest("Medium Confidence Query (Typo)", passed2);
//        } catch (Exception e) {
//            logTest("Medium Confidence Query (Typo)", false, e.getMessage());
//        }
//        
//        // Low confidence queries
//        try {
//            ChatbotIntent lowConfidence = processor.processUserMessage("random text 123456");
//            boolean passed3 = lowConfidence.getConfidenceScore() <= 0.5;
//            logTest("Low Confidence Query", passed3);
//        } catch (Exception e) {
//            logTest("Low Confidence Query", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    private static void testMultipleEntityExtraction() {
//        System.out.println("Testing Multiple Entity Extraction...");
//        
//        try {
//            ChatbotIntent intent = processor.processUserMessage("show failed parts of contract 123456 for customer boeing");
//            boolean passed = intent.getType() == IntentType.FAILED_PARTS && 
//                           "123456".equals(intent.getContractId()) &&
//                           "boeing".equals(intent.getCustomerInfo()) &&
//                           intent.hasMultipleEntities();
//            
//            logTest("Multiple Entity Extraction", passed);
//        } catch (Exception e) {
//            logTest("Multiple Entity Extraction", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    private static void testResponseGeneration() {
//        System.out.println("Testing Response Generation...");
//        
//        // Test contract creation response
//        try {
//            ChatbotIntent createIntent = processor.processUserMessage("create contract");
//            String response = processor.generateResponse(createIntent);
//            
//            boolean passed1 = response != null &&
//                            response.contains("account number") &&
//                            response.contains("contract name") &&
//                            response.contains("expiration date") &&
//                            response.contains("effective date") &&
//                            response.contains("pricelist");
//            
//            logTest("Contract Creation Response", passed1);
//        } catch (Exception e) {
//            logTest("Contract Creation Response", false, e.getMessage());
//        }
//        
//        // Test help response
//        try {
//            ChatbotIntent helpIntent = processor.processUserMessage("show me how to create contract");
//            String helpResponse = processor.generateResponse(helpIntent);
//            
//            boolean passed2 = helpResponse != null &&
//                            helpResponse.contains("steps") &&
//                            helpResponse.contains("create") &&
//                            helpResponse.contains("contract");
//            
//            logTest("Help Response Generation", passed2);
//        } catch (Exception e) {
//            logTest("Help Response Generation", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    private static void testComplexQueries() {
//        System.out.println("Testing Complex Queries...");
//        
//        String[] complexQueries = {
//            "show me all active contracts for boeing with failed parts",
//            "list expired contracts created by john for customer honeywell",
//            "find all parts AE125 in active contracts for account 10840607"
//        };
//        
//        for (String query : complexQueries) {
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                boolean passed = intent.getType() != IntentType.UNKNOWN &&
//                               intent.hasMultipleEntities();
//                
//                logTest("Complex Query: " + query.substring(0, Math.min(30, query.length())) + "...", passed);
//            } catch (Exception e) {
//                logTest("Complex Query: " + query.substring(0, Math.min(30, query.length())) + "...", false, e.getMessage());
//            }
//        }
//        System.out.println();
//    }
//    
//    private static void testEdgeCases() {
//        System.out.println("Testing Edge Cases...");
//        
//        // Very long query
//        try {
//            String longQuery = "show me the contract details for contract number 123456 " +
//                             "and also provide information about all the parts associated " +
//                             "with this contract including their specifications and availability";
//            ChatbotIntent intent = processor.processUserMessage(longQuery);
//            boolean passed1 = intent.getType() != IntentType.UNKNOWN;
//            logTest("Very Long Query", passed1);
//        } catch (Exception e) {
//            logTest("Very Long Query", false, e.getMessage());
//        }
//        
//        // Query with special characters
//        try {
//            ChatbotIntent intent2 = processor.processUserMessage("show contract #123456 @details!");
//            boolean passed2 = intent2.getType() == IntentType.CONTRACT_QUERY &&
//                            "123456".equals(intent2.getContractId());
//            logTest("Query with Special Characters", passed2);
//        } catch (Exception e) {
//            logTest("Query with Special Characters", false, e.getMessage());
//        }
//        
//        // Query with numbers only
//        try {
//            ChatbotIntent intent3 = processor.processUserMessage("123456");
//            boolean passed3 = intent3.getType() != IntentType.UNKNOWN;
//            logTest("Numbers Only Query", passed3);
//        } catch (Exception e) {
//            logTest("Numbers Only Query", false, e.getMessage());
//        }
//        System.out.println();
//    }
//    
//    // Helper method to log test results
//    private static void logTest(String testName, boolean passed) {
//        logTest(testName, passed, null);
//    }
//    
//    private static void logTest(String testName, boolean passed, String errorMessage) {
//        totalTests++;
//        if (passed) {
//            passedTests++;
//            System.out.println("? PASS: " + testName);
//        } else {
//            failedTests++;
//            System.out.println("? FAIL: " + testName);
//            if (errorMessage != null) {
//                System.out.println("    Error: " + errorMessage);
//            }
//        }
//    }
//    
//    private static void printTestSummary() {
//        System.out.println("=== Test Summary ===");
//        System.out.println("Total Tests: " + totalTests);
//        System.out.println("Passed: " + passedTests);
//        System.out.println("Failed: " + failedTests);
//        System.out.println("Success Rate: " + String.format("%.2f", (double) passedTests / totalTests * 100) + "%");
//        
//        if (failedTests == 0) {
//            System.out.println("\n? All tests passed!");
//        } else {
//            System.out.println("\n??  Some tests failed. Please review the implementation.");
//        }
//    }
//    
//    // Additional test method for comprehensive coverage
//    private static void runAllTests() {
//        System.out.println("Running comprehensive test suite...\n");
//        
//        testContractQueriesWithId();
//        testContractQueriesByUser();
//        testStatusQueries();
//        testCustomerQueries();
//        testPartsQueries();
//        testPartsByContractQueries();
//        testFailedPartsQueries();
//        testContractCreationHelpQueries();
//        testTypoHandling();
//        testUnknownIntent();
//        testEmptyAndNullQueries();
//        testCaseInsensitiveProcessing();
//        testIntentConfidenceScores();
//        testMultipleEntityExtraction();
//        testResponseGeneration();
//        testComplexQueries();
//        testEdgeCases();
//    }
//    
//    // Method to test specific query types
//    private static void testSpecificQueryType(String queryType) {
//        System.out.println("Testing specific query type: " + queryType + "\n");
//        
//        switch (queryType.toLowerCase()) {
//            case "contract":
//                testContractQueriesWithId();
//                testContractQueriesByUser();
//                break;
//            case "parts":
//                testPartsQueries();
//                testPartsByContractQueries();
//                testFailedPartsQueries();
//                break;
//            case "customer":
//                testCustomerQueries();
//                break;
//            case "status":
//                testStatusQueries();
//                break;
//            case "help":
//                testContractCreationHelpQueries();
//                break;
//            case "typo":
//                testTypoHandling();
//                break;
//            default:
//                System.out.println("Unknown query type. Running all tests...");
//                runAllTests();
//                break;
//        }
//    }
//    
//    // Method to run interactive tests
//    private static void runInteractiveTest() {
//        System.out.println("=== Interactive Test Mode ===");
//        System.out.println("Enter queries to test (type 'exit' to quit):\n");
//        
//        java.util.Scanner scanner = new java.util.Scanner(System.in);
//        
//        while (true) {
//            System.out.print("Query: ");
//            String query = scanner.nextLine();
//            
//            if ("exit".equalsIgnoreCase(query.trim())) {
//                break;
//            }
//            
//            try {
//                ChatbotIntent intent = processor.processUserMessage(query);
//                System.out.println("Intent Type: " + intent.getType());
//                System.out.println("Confidence: " + String.format("%.2f", intent.getConfidenceScore()));
//                
//                if (intent.getContractId() != null) {
//                    System.out.println("Contract ID: " + intent.getContractId());
//                }
//                if (intent.getPartNumber() != null) {
//                    System.out.println("Part Number: " + intent.getPartNumber());
//                }
//                if (intent.getCustomerInfo() != null) {
//                    System.out.println("Customer: " + intent.getCustomerInfo());
//                }
//                if (intent.hasTypoCorrection()) {
//                    System.out.println("Typo corrected: Yes");
//                }
//                
//                String response = processor.generateResponse(intent);
//                System.out.println("Response: " + response);
//                System.out.println("---");
//                
//            } catch (Exception e) {
//                System.out.println("Error processing query: " + e.getMessage());
//                System.out.println("---");
//            }
//        }
//        
//        scanner.close();
//        System.out.println("Interactive test completed.");
//    }
//}
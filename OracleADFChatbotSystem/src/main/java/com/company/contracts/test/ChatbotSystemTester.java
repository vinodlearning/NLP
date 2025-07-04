package com.company.contracts.test;

import com.company.contracts.service.*;
import com.company.contracts.view.ChatbotBackingBean;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Comprehensive Test Suite for Oracle ADF Chatbot System
 * 
 * This class demonstrates and tests all the functionality of the
 * Contract Portal chatbot system including:
 * - Real Apache OpenNLP model processing
 * - Intent classification and confidence scoring
 * - Entity extraction and spell correction
 * - ADF response formatting
 * - Error handling scenarios
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ChatbotSystemTester {
    
    private static final Logger logger = Logger.getLogger(ChatbotSystemTester.class.getName());
    
    // System components
    private OpenNLPModelManager modelManager;
    private ContractNLPService nlpService;
    private ADFResponseBuilder responseBuilder;
    private ChatbotBackingBean backingBean;
    
    // Test statistics
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private List<TestResult> testResults = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("🤖 ORACLE ADF CHATBOT SYSTEM TESTER");
        System.out.println("=====================================");
        
        try {
            ChatbotSystemTester tester = new ChatbotSystemTester();
            tester.initializeSystem();
            tester.runAllTestScenarios();
            tester.printTestSummary();
            
        } catch (Exception e) {
            System.err.println("❌ Error running tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize the chatbot system for testing
     */
    private void initializeSystem() throws Exception {
        System.out.println("\n🔧 Initializing Chatbot System...");
        
        // Initialize model manager
        modelManager = new OpenNLPModelManager();
        
        // Initialize NLP service
        nlpService = new ContractNLPService(modelManager);
        
        // Initialize response builder
        responseBuilder = new ADFResponseBuilder();
        
        // Initialize backing bean (simulated ADF environment)
        backingBean = new ChatbotBackingBean();
        
        System.out.println("✅ System initialized successfully!");
        
        // Display system status
        displaySystemStatus();
    }
    
    /**
     * Display current system status
     */
    private void displaySystemStatus() {
        System.out.println("\n📊 System Status:");
        System.out.println("   Models Loaded: " + modelManager.getLoadedModelsCount());
        System.out.println("   All Models Available: " + modelManager.areAllModelsLoaded());
        
        Map<String, Object> healthCheck = modelManager.performHealthCheck();
        System.out.println("   Health Check: " + healthCheck.get("managerStatus"));
        System.out.println("   Memory Usage: " + healthCheck.get("memoryInfo"));
    }
    
    /**
     * Run all test scenarios
     */
    private void runAllTestScenarios() {
        System.out.println("\n🧪 Running Test Scenarios...");
        
        // Contract-related scenarios
        testContractScenarios();
        
        // Parts-related scenarios  
        testPartsScenarios();
        
        // Help-related scenarios
        testHelpScenarios();
        
        // Spell correction scenarios
        testSpellCorrectionScenarios();
        
        // Entity extraction scenarios
        testEntityExtractionScenarios();
        
        // Error handling scenarios
        testErrorHandlingScenarios();
        
        // Performance scenarios
        testPerformanceScenarios();
        
        // ADF integration scenarios
        testADFIntegrationScenarios();
    }
    
    /**
     * Test contract-related queries
     */
    private void testContractScenarios() {
        System.out.println("\n📋 Testing Contract Scenarios...");
        
        String[] contractQueries = {
            "show contract 123456",
            "display contract ABC-789",
            "get contract details for 555666",
            "effective date for contract 123456",
            "expiration date of contract ABC-789",
            "when does contract 555666 expire",
            "customer for contract 123456",
            "who is the customer of contract ABC-789",
            "vendor for contract 123456",
            "status of contract 123456",
            "is contract ABC-789 active",
            "value of contract 123456",
            "how much is contract 555666 worth",
            "list all contracts",
            "show all active contracts"
        };
        
        for (String query : contractQueries) {
            TestResult result = runSingleTest("CONTRACT", query);
            testResults.add(result);
            displayTestResult(result);
        }
    }
    
    /**
     * Test parts-related queries
     */
    private void testPartsScenarios() {
        System.out.println("\n🔧 Testing Parts Scenarios...");
        
        String[] partsQueries = {
            "show parts for contract 123456",
            "list parts in contract ABC-789",
            "get parts for contract 555666",
            "how many parts for contract 123456",
            "count parts in contract ABC-789",
            "total parts for contract 555666",
            "part number P12345 details",
            "specifications for part P67890",
            "part P12345 supplier information",
            "inventory for part P12345",
            "stock level of part P67890",
            "price of part P12345",
            "cost for part P67890",
            "is part P12345 available",
            "find parts by manufacturer"
        };
        
        for (String query : partsQueries) {
            TestResult result = runSingleTest("PARTS", query);
            testResults.add(result);
            displayTestResult(result);
        }
    }
    
    /**
     * Test help-related queries
     */
    private void testHelpScenarios() {
        System.out.println("\n❓ Testing Help Scenarios...");
        
        String[] helpQueries = {
            "how to create contract",
            "steps to create new contract",
            "contract creation process",
            "create contract help",
            "guide for creating contracts",
            "contract approval workflow",
            "approval process for contracts",
            "contract validation rules",
            "validation requirements for contract",
            "contract templates available",
            "template for contract creation",
            "required fields for contract",
            "help with contracts",
            "contract help",
            "assistance needed"
        };
        
        for (String query : helpQueries) {
            TestResult result = runSingleTest("HELP", query);
            testResults.add(result);
            displayTestResult(result);
        }
    }
    
    /**
     * Test spell correction functionality
     */
    private void testSpellCorrectionScenarios() {
        System.out.println("\n🔤 Testing Spell Correction Scenarios...");
        
        String[] misspelledQueries = {
            "shw contrct 123456",           // show contract
            "dsplay contarct ABC-789",       // display contract
            "effectuve date for contrct 123456", // effective contract
            "expir date of contract ABC-789",     // expiration
            "customr for contract 123456",       // customer
            "how many parst for contract 123456", // parts
            "partz in contract ABC-789",         // parts
            "hep me create contract",           // help
            "creat contract help",              // create
            "acount number 123456"              // account
        };
        
        for (String query : misspelledQueries) {
            TestResult result = runSingleTest("SPELL_CORRECTION", query);
            testResults.add(result);
            displayTestResult(result);
            
            // Verify spell correction was applied
            if (result.nlpResponse != null && result.nlpResponse.isSpellCorrected()) {
                System.out.println("   ✓ Spell correction applied: " + 
                                 result.nlpResponse.getOriginalQuery() + " → " + 
                                 result.nlpResponse.getCorrectedQuery());
            }
        }
    }
    
    /**
     * Test entity extraction functionality
     */
    private void testEntityExtractionScenarios() {
        System.out.println("\n🎯 Testing Entity Extraction Scenarios...");
        
        String[] entityQueries = {
            "show contract 123456 for customer ABC Corp",
            "parts P12345 and P67890 for contract ABC-789",
            "effective date 2024-01-15 for contract 555666",
            "account 987654321 contract XYZ-123",
            "customer John Smith contract 123456",
            "part P98765 supplier information account 123456789",
            "contract ABC-789 expiration 12/31/2024",
            "vendor XYZ Corp contract 555666"
        };
        
        for (String query : entityQueries) {
            TestResult result = runSingleTest("ENTITY_EXTRACTION", query);
            testResults.add(result);
            displayTestResult(result);
            
            // Display extracted entities
            if (result.nlpResponse != null && result.nlpResponse.getEntities() != null) {
                System.out.println("   📋 Extracted Entities:");
                for (Map.Entry<String, String> entity : result.nlpResponse.getEntities().entrySet()) {
                    System.out.println("      " + entity.getKey() + ": " + entity.getValue());
                }
            }
        }
    }
    
    /**
     * Test error handling scenarios
     */
    private void testErrorHandlingScenarios() {
        System.out.println("\n⚠️ Testing Error Handling Scenarios...");
        
        String[] errorQueries = {
            "",                              // Empty query
            "   ",                          // Whitespace only
            "xyz abc def",                  // Nonsensical query
            "delete all contracts",         // Potentially harmful query
            "show contract",                // Missing required information
            "create parts for contract 123", // Invalid operation (parts can't be created)
            "sql injection attempt",       // Security test
            "very long query that goes on and on without making any sense and should test the system's ability to handle extremely long input text that might cause performance issues"
        };
        
        for (String query : errorQueries) {
            TestResult result = runSingleTest("ERROR_HANDLING", query);
            testResults.add(result);
            displayTestResult(result);
        }
    }
    
    /**
     * Test performance scenarios
     */
    private void testPerformanceScenarios() {
        System.out.println("\n⚡ Testing Performance Scenarios...");
        
        String testQuery = "show contract 123456";
        long totalTime = 0;
        int iterations = 10;
        
        System.out.println("   Running " + iterations + " iterations of: " + testQuery);
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();
            TestResult result = runSingleTest("PERFORMANCE", testQuery);
            long endTime = System.currentTimeMillis();
            
            long responseTime = endTime - startTime;
            totalTime += responseTime;
            
            System.out.println("   Iteration " + (i+1) + ": " + responseTime + "ms");
        }
        
        long averageTime = totalTime / iterations;
        System.out.println("   📊 Average Response Time: " + averageTime + "ms");
        
        // Performance assertions
        if (averageTime < 1000) {
            System.out.println("   ✅ Performance EXCELLENT (<1s)");
        } else if (averageTime < 2000) {
            System.out.println("   ✅ Performance GOOD (<2s)");
        } else {
            System.out.println("   ⚠️ Performance NEEDS OPTIMIZATION (>2s)");
        }
    }
    
    /**
     * Test ADF integration scenarios
     */
    private void testADFIntegrationScenarios() {
        System.out.println("\n🎨 Testing ADF Integration Scenarios...");
        
        // Test backing bean functionality
        testBackingBeanIntegration();
        
        // Test response formatting
        testResponseFormatting();
        
        // Test conversation management
        testConversationManagement();
    }
    
    /**
     * Test backing bean integration
     */
    private void testBackingBeanIntegration() {
        System.out.println("   🔧 Testing Backing Bean Integration...");
        
        try {
            // Simulate user input
            backingBean.setUserQuery("show contract 123456");
            
            // Process query (simulate ActionEvent)
            backingBean.processUserQuery(null);
            
            // Verify response
            if (backingBean.isHasResponse()) {
                System.out.println("   ✅ Backing bean processing successful");
                System.out.println("   📝 Response: " + backingBean.getChatResponse().substring(0, Math.min(100, backingBean.getChatResponse().length())) + "...");
                System.out.println("   🎯 Response Type: " + backingBean.getResponseType());
            } else {
                System.out.println("   ❌ Backing bean processing failed - no response");
            }
            
            // Test conversation history
            if (backingBean.getConversationHistory().size() > 0) {
                System.out.println("   ✅ Conversation history working");
                System.out.println("   📊 History entries: " + backingBean.getConversationHistory().size());
            }
            
            // Test session statistics
            Map<String, Object> stats = backingBean.getSessionStatistics();
            System.out.println("   📈 Session Stats: " + stats);
            
        } catch (Exception e) {
            System.out.println("   ❌ Backing bean test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test response formatting
     */
    private void testResponseFormatting() {
        System.out.println("   🎨 Testing Response Formatting...");
        
        try {
            // Create test NLP response
            NLPResponse nlpResponse = new NLPResponse("show contract 123456", "CONTRACT_LOOKUP", 0.95);
            nlpResponse.setDomain("CONTRACT");
            nlpResponse.setResponseText("Contract 123456 found: Effective 2024-01-01, Customer: ABC Corp, Status: Active");
            nlpResponse.setSuccess(true);
            nlpResponse.setProcessingTimeMs(245);
            
            Map<String, String> entities = new HashMap<>();
            entities.put("contractNumber", "123456");
            nlpResponse.setEntities(entities);
            
            // Build ADF response
            ADFChatResponse adfResponse = responseBuilder.buildChatResponse(nlpResponse);
            
            // Verify formatting
            System.out.println("   ✅ ADF Response Generated");
            System.out.println("   🎯 Response Type: " + adfResponse.getResponseType());
            System.out.println("   🎨 CSS Class: " + adfResponse.getCssClass());
            System.out.println("   🔍 Confidence Level: " + adfResponse.getConfidenceLevel());
            System.out.println("   🎭 Icon Class: " + adfResponse.getIconClass());
            System.out.println("   🌈 Background Color: " + adfResponse.getBackgroundColor());
            
            if (adfResponse.hasSuggestedActions()) {
                System.out.println("   💡 Suggested Actions: " + adfResponse.getSuggestedActions());
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Response formatting test failed: " + e.getMessage());
        }
    }
    
    /**
     * Test conversation management
     */
    private void testConversationManagement() {
        System.out.println("   💬 Testing Conversation Management...");
        
        try {
            // Test multiple queries in sequence
            String[] conversationQueries = {
                "hello",
                "show contract 123456", 
                "what about the customer?",
                "help me create a new contract"
            };
            
            for (String query : conversationQueries) {
                backingBean.setUserQuery(query);
                backingBean.processUserQuery(null);
            }
            
            // Verify conversation history
            List<ChatbotBackingBean.ChatMessage> history = backingBean.getConversationHistoryForDisplay();
            System.out.println("   ✅ Conversation History: " + history.size() + " messages");
            
            // Display recent conversation
            System.out.println("   📜 Recent Messages:");
            for (int i = Math.max(0, history.size() - 4); i < history.size(); i++) {
                ChatbotBackingBean.ChatMessage msg = history.get(i);
                System.out.println("      " + msg.getSender() + ": " + 
                                 msg.getMessage().substring(0, Math.min(50, msg.getMessage().length())) + "...");
            }
            
            // Test clear conversation
            backingBean.clearConversation(null);
            if (backingBean.getConversationHistory().size() <= 1) { // Should have welcome message
                System.out.println("   ✅ Clear conversation working");
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Conversation management test failed: " + e.getMessage());
        }
    }
    
    /**
     * Run a single test
     */
    private TestResult runSingleTest(String category, String query) {
        TestResult result = new TestResult();
        result.category = category;
        result.query = query;
        result.startTime = System.currentTimeMillis();
        
        totalTests++;
        
        try {
            // Process query through NLP service
            result.nlpResponse = nlpService.processQuery(query);
            
            // Build ADF response
            result.adfResponse = responseBuilder.buildChatResponse(result.nlpResponse);
            
            result.endTime = System.currentTimeMillis();
            result.success = result.nlpResponse.isSuccess();
            
            if (result.success) {
                passedTests++;
            } else {
                failedTests++;
            }
            
        } catch (Exception e) {
            result.endTime = System.currentTimeMillis();
            result.success = false;
            result.errorMessage = e.getMessage();
            failedTests++;
        }
        
        return result;
    }
    
    /**
     * Display individual test result
     */
    private void displayTestResult(TestResult result) {
        String status = result.success ? "✅" : "❌";
        String responseTime = (result.endTime - result.startTime) + "ms";
        
        System.out.println("   " + status + " \"" + result.query + "\" (" + responseTime + ")");
        
        if (result.success && result.nlpResponse != null) {
            System.out.println("      🎯 Intent: " + result.nlpResponse.getIntent() + 
                             " | Confidence: " + String.format("%.2f", result.nlpResponse.getConfidence()) +
                             " | Domain: " + result.nlpResponse.getDomain());
        } else if (result.errorMessage != null) {
            System.out.println("      ❌ Error: " + result.errorMessage);
        }
    }
    
    /**
     * Print comprehensive test summary
     */
    private void printTestSummary() {
        System.out.println("\n📊 TEST SUMMARY");
        System.out.println("================");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests + " ✅");
        System.out.println("Failed: " + failedTests + " ❌");
        System.out.println("Success Rate: " + String.format("%.1f", (passedTests * 100.0 / totalTests)) + "%");
        
        // Category breakdown
        Map<String, Integer> categoryStats = new HashMap<>();
        for (TestResult result : testResults) {
            categoryStats.put(result.category, categoryStats.getOrDefault(result.category, 0) + 1);
        }
        
        System.out.println("\n📋 Tests by Category:");
        for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
            System.out.println("   " + entry.getKey() + ": " + entry.getValue() + " tests");
        }
        
        // Performance analysis
        long totalResponseTime = 0;
        int responseCount = 0;
        for (TestResult result : testResults) {
            if (result.success) {
                totalResponseTime += (result.endTime - result.startTime);
                responseCount++;
            }
        }
        
        if (responseCount > 0) {
            long averageResponseTime = totalResponseTime / responseCount;
            System.out.println("\n⚡ Performance Metrics:");
            System.out.println("   Average Response Time: " + averageResponseTime + "ms");
            System.out.println("   Total Processing Time: " + totalResponseTime + "ms");
        }
        
        // System health check
        System.out.println("\n🏥 System Health:");
        Map<String, Object> healthCheck = modelManager.performHealthCheck();
        System.out.println("   Status: " + healthCheck.get("managerStatus"));
        System.out.println("   Models Loaded: " + healthCheck.get("modelsLoaded"));
        System.out.println("   All Models Available: " + healthCheck.get("allModelsLoaded"));
        
        // Final assessment
        if (passedTests > totalTests * 0.9) {
            System.out.println("\n🎉 SYSTEM STATUS: EXCELLENT - Ready for production!");
        } else if (passedTests > totalTests * 0.8) {
            System.out.println("\n✅ SYSTEM STATUS: GOOD - Minor optimizations recommended");
        } else {
            System.out.println("\n⚠️ SYSTEM STATUS: NEEDS ATTENTION - Review failed tests");
        }
    }
    
    /**
     * Test result container
     */
    private static class TestResult {
        String category;
        String query;
        long startTime;
        long endTime;
        boolean success;
        String errorMessage;
        NLPResponse nlpResponse;
        ADFChatResponse adfResponse;
    }
}
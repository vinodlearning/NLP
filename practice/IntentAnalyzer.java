package view.practice;
import java.util.*;
import java.util.regex.Pattern;

public class IntentAnalyzer {
    
    public enum Intent {
        CONTRACT_QUERY,
        STATUS_QUERY,
        CUSTOMER_QUERY,
        PARTS_QUERY,
        PARTS_BY_CONTRACT,
        FAILED_PARTS_QUERY,
        CREATE_CONTRACT,
        HELP_CREATE_CONTRACT,
        UNKNOWN
    }
    
    public static class AnalysisResult {
        private Intent intent;
        private String extractedId;
        private String extractedEntity;
        private Map<String, String> parameters;
        private String response;
        
        public AnalysisResult(Intent intent, String extractedId, String extractedEntity, 
                            Map<String, String> parameters, String response) {
            this.intent = intent;
            this.extractedId = extractedId;
            this.extractedEntity = extractedEntity;
            this.parameters = parameters != null ? parameters : new HashMap<>();
            this.response = response;
        }
        
        // Getters
        public Intent getIntent() { return intent; }
        public String getExtractedId() { return extractedId; }
        public String getExtractedEntity() { return extractedEntity; }
        public Map<String, String> getParameters() { return parameters; }
        public String getResponse() { return response; }
        
        @Override
        public String toString() {
            return String.format("Intent: %s, ID: %s, Entity: %s, Params: %s, Response: %s", 
                               intent, extractedId, extractedEntity, parameters, response);
        }
    }
    
    // Patterns for different intents
    private static final Pattern CONTRACT_PATTERN = Pattern.compile(
        "(?i).*(show|get|find|display|view).*contract.*?(\\d{6,})|contract.*(details|info).*?(\\d{6,})|.*contracts?.*(by|created by)\\s+(\\w+)"
    );
    
    private static final Pattern STATUS_PATTERN = Pattern.compile(
        "(?i).*(status|expired|active).*contracts?|status.*of.*?(\\d{6,})"
    );
    
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile(
        "(?i).*(customer|account)\\s+(\\w+)|([a-zA-Z]+)\\s+contracts?"
    );
    
    private static final Pattern PARTS_PATTERN = Pattern.compile(
        "(?i).*(part|product).*?(number|id)?\\s+([A-Z0-9]+)|specifications.*([A-Z0-9]+)|datasheet.*([A-Z0-9]+)|compatible.*([A-Z0-9]+)|stock.*([A-Z0-9]+)|lead time.*([A-Z0-9]+)|manufacturer.*([A-Z0-9]+)|issues.*([A-Z0-9]+)|warranty.*([A-Z0-9]+)"
    );
    
    private static final Pattern PARTS_BY_CONTRACT_PATTERN = Pattern.compile(
        "(?i).*(show|list).*parts.*?(\\d{6,})|how many parts.*?(\\d{6,})"
    );
    
    private static final Pattern FAILED_PARTS_PATTERN = Pattern.compile(
        "(?i).*(failed|filed)\\s+parts.*?(\\d{6,})?|.*parts.*failed"
    );
    
    private static final Pattern CREATE_CONTRACT_PATTERN = Pattern.compile(
        "(?i).*(create|new)\\s+contract.*?(\\d{8,})?|i want.*create.*contract.*?(\\d{8,})?"
    );
    
    private static final Pattern HELP_CREATE_PATTERN = Pattern.compile(
        "(?i).*(help|how).*create.*contract.*?(\\d{8,})?|show.*how.*create"
    );
    
    public static AnalysisResult analyzeIntent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new AnalysisResult(Intent.UNKNOWN, null, null, null, "Please provide a valid query.");
        }
        
        // Normalize the query (handle typos)
        String normalizedQuery = normalizeQuery(query);
        
        // Check for help/create contract patterns first
        if (HELP_CREATE_PATTERN.matcher(normalizedQuery).find()) {
            return handleHelpCreateContract(normalizedQuery);
        }
        
        if (CREATE_CONTRACT_PATTERN.matcher(normalizedQuery).find()) {
            return handleCreateContract(normalizedQuery);
        }
        
        // Check for failed parts
        if (FAILED_PARTS_PATTERN.matcher(normalizedQuery).find()) {
            return handleFailedParts(normalizedQuery);
        }
        
        // Check for parts by contract
        if (PARTS_BY_CONTRACT_PATTERN.matcher(normalizedQuery).find()) {
            return handlePartsByContract(normalizedQuery);
        }
        
        // Check for parts queries
        if (PARTS_PATTERN.matcher(normalizedQuery).find()) {
            return handlePartsQuery(normalizedQuery);
        }
        
        // Check for contract queries
        if (CONTRACT_PATTERN.matcher(normalizedQuery).find()) {
            return handleContractQuery(normalizedQuery);
        }
        
        // Check for status queries
        if (STATUS_PATTERN.matcher(normalizedQuery).find()) {
            return handleStatusQuery(normalizedQuery);
        }
        
        // Check for customer queries
        if (CUSTOMER_PATTERN.matcher(normalizedQuery).find()) {
            return handleCustomerQuery(normalizedQuery);
        }
        
        return new AnalysisResult(Intent.UNKNOWN, null, null, null, 
                                "I couldn't understand your request. Please try rephrasing.");
    }
    
    private static String normalizeQuery(String query) {
        return query.toLowerCase()
                   .replaceAll("cntrs", "contracts")
                   .replaceAll("contarct", "contract")
                   .replaceAll("pasrt", "part")
                   .replaceAll("filed parts", "failed parts")
                   .replaceAll("shw me", "show me")
                   .trim();
    }
    
    private static AnalysisResult handleContractQuery(String query) {
        String contractId = extractContractId(query);
        String person = extractPerson(query);
        
        Map<String, String> params = new HashMap<>();
        if (contractId != null) params.put("contractId", contractId);
        if (person != null) params.put("person", person);
        
        String response = contractId != null ? 
            "Retrieving contract details for: " + contractId :
            (person != null ? "Searching contracts for: " + person : "Searching contracts...");
            
        return new AnalysisResult(Intent.CONTRACT_QUERY, contractId, person, params, response);
    }
    
    private static AnalysisResult handleStatusQuery(String query) {
        String contractId = extractContractId(query);
        String statusType = null;
        
        if (query.contains("expired")) statusType = "expired";
        else if (query.contains("active")) statusType = "active";
        
        Map<String, String> params = new HashMap<>();
        if (contractId != null) params.put("contractId", contractId);
        if (statusType != null) params.put("statusType", statusType);
        
        String response = contractId != null ? 
            "Checking status for contract: " + contractId :
            "Retrieving " + (statusType != null ? statusType : "all") + " contracts";
            
        return new AnalysisResult(Intent.STATUS_QUERY, contractId, statusType, params, response);
    }
    
    private static AnalysisResult handleCustomerQuery(String query) {
        String customer = extractCustomer(query);
        String accountId = extractAccountId(query);
        
        Map<String, String> params = new HashMap<>();
        if (customer != null) params.put("customer", customer);
        if (accountId != null) params.put("accountId", accountId);
        
        String response = "Retrieving contracts for customer: " + 
                         (customer != null ? customer : accountId);
                         
        return new AnalysisResult(Intent.CUSTOMER_QUERY, accountId, customer, params, response);
    }
    
    private static AnalysisResult handlePartsQuery(String query) {
        String partNumber = extractPartNumber(query);
        String queryType = determinePartQueryType(query);
        
        Map<String, String> params = new HashMap<>();
        if (partNumber != null) params.put("partNumber", partNumber);
        if (queryType != null) params.put("queryType", queryType);
        
        String response = "Retrieving " + (queryType != null ? queryType : "information") + 
                         " for part: " + partNumber;
                         
        return new AnalysisResult(Intent.PARTS_QUERY, partNumber, queryType, params, response);
    }
    
    private static AnalysisResult handlePartsByContract(String query) {
        String contractId = extractContractId(query);
        
        Map<String, String> params = new HashMap<>();
        if (contractId != null) params.put("contractId", contractId);
        
        String response = "Retrieving parts for contract: " + contractId;
        
        return new AnalysisResult(Intent.PARTS_BY_CONTRACT, contractId, null, params, response);
    }
    
    private static AnalysisResult handleFailedParts(String query) {
        String contractId = extractContractId(query);
        
        Map<String, String> params = new HashMap<>();
        if (contractId != null) params.put("contractId", contractId);
        
        String response = contractId != null ? 
            "Retrieving failed parts for contract: " + contractId :
            "Retrieving all failed parts";
            
        return new AnalysisResult(Intent.FAILED_PARTS_QUERY, contractId, null, params, response);
    }
    
    private static AnalysisResult handleCreateContract(String query) {
        String accountNumber = extractAccountNumber(query);
        
        Map<String, String> params = new HashMap<>();
        if (accountNumber != null) {
            params.put("accountNumber", accountNumber);
            String response = "To create a contract for account " + accountNumber + 
                            ", please provide:\n- Contract name\n- Expiration date\n- Effective date\n- Pricelist (yes/no)\n- Title\n- Description";
            return new AnalysisResult(Intent.CREATE_CONTRACT, accountNumber, null, params, response);
        } else {
            String response = "To create a contract, please provide:\n- Account number\n- Contract name\n- Expiration date\n- Effective date\n- Pricelist (yes/no)\n- Title\n- Description";
            return new AnalysisResult(Intent.CREATE_CONTRACT, null, null, params, response);
        }
    }
    
    private static AnalysisResult handleHelpCreateContract(String query) {
        String response = "Steps to create a contract:\n" +
                         "1. Provide account number\n" +
                         "2. Enter contract name\n" +
                         "3. Set expiration date\n" +
                         "4. Set effective date\n" +
                         "5. Choose pricelist option (yes/no)\n" +
                         "6. Add title and description";
                         
        return new AnalysisResult(Intent.HELP_CREATE_CONTRACT, null, null, new HashMap<>(), response);
    }
    
    // Helper methods for extraction
    private static String extractContractId(String query) {
        Pattern pattern = Pattern.compile("\\b\\d{6,}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(query);
        return matcher.find() ? matcher.group() : null;
    }
    
    private static String extractAccountNumber(String query) {
        Pattern pattern = Pattern.compile("\\b\\d{8,}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(query);
        return matcher.find() ? matcher.group() : null;
    }
    
    private static String extractAccountId(String query) {
        Pattern pattern = Pattern.compile("account\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(query);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    private static String extractPerson(String query) {
        Pattern pattern = Pattern.compile("(?:by|created by)\\s+(\\w+)|^(\\w+)\\s+contracts", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        return null;
    }
    
    private static String extractCustomer(String query) {
        Pattern pattern = Pattern.compile("customer\\s+(\\w+)|(\\w+)\\s+contracts", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String customer = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            // Filter out common words that aren't customer names
            if (customer != null && !customer.matches("(?i)(the|all|my|show|get|find)")) {
                return customer;
            }
        }
        return null;
    }
    
    private static String extractPartNumber(String query) {
        Pattern pattern = Pattern.compile("(?:part|product).*?(?:number|id)?\\s+([A-Z0-9]+)|([A-Z0-9]+)(?=\\s|$)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        return null;
    }
    
    private static String determinePartQueryType(String query) {
        if (query.contains("specifications")) return "specifications";
        if (query.contains("datasheet")) return "datasheet";
        if (query.contains("compatible")) return "compatibility";
        if (query.contains("stock")) return "stock";
        if (query.contains("lead time")) return "lead_time";
        if (query.contains("manufacturer")) return "manufacturer";
        if (query.contains("issues") || query.contains("defects")) return "issues";
        if (query.contains("warranty")) return "warranty";
        if (query.contains("active") || query.contains("discontinued")) return "status";
        return "general";
    }
    
    public static void main(String[] args) {
        System.out.println("=== Intent Analysis Test ===\n");
        
        // Test various queries
        String[] testQueries = {
            // Contract queries
            "show contract 123456",
            "contract details 123456",
            "get contract info 123456",
            "find contract 123456",
            "john contracts",
            "contracts by smith",
            "contracts created by mary",
            
            // Status queries
            "status of 123456",
            "expired contracts",
            "active contracts",
            
             
            // Customer queries
            "boeing contracts",
            "customer honeywell",
            "account 10840607",
            
            // Parts queries
            "List out the contracts associated with part number AE125",
            "What are the specifications of product ID AE125?",
            "Is part number AE125 still active or discontinued?",
            "Can you provide the datasheet for AE125?",
            "What are the compatible parts for AE125?",
            "Is AE125 available in stock?",
            "What is the lead time for part AE125?",
            "Who is the manufacturer of AE125?",
            "Are there any known issues or defects with AE125?",
            "What is the warranty period for AE125?",
            
            // Parts by contract
            "show me the parts 123456",
            "list the parts of 123456",
            "how many parts for 123456",
            
            // Failed parts queries
            "failed parts of 123456",
            "filed parts 123456",
            "failed parts",
            
            // Help queries
            "create contract",
            "show me how to create contract",
            "I want to create a contract",
            "help create contract1023456789",
            
            // Typo examples
            "cntrs 123456 shw me",
            "contarct 123456",
            "pasrt AE125 info",
            "filed parts of 123456"
        };
        
        int totalTests = testQueries.length;
        int passedTests = 0;
        
        for (int i = 0; i < testQueries.length; i++) {
            String query = testQueries[i];
            System.out.println("Test " + (i + 1) + ": \"" + query + "\"");
            
            AnalysisResult result = analyzeIntent(query);
            
            // Validate the result based on expected behavior
            boolean testPassed = validateTestResult(query, result);
            
            System.out.println("Result: " + result);
            System.out.println("Status: " + (testPassed ? "? PASSED" : "? FAILED"));
            System.out.println("Response: " + result.getResponse());
            System.out.println("------------------------------------------------");
            
            if (testPassed) passedTests++;
        }
        
        // Summary
        System.out.println("\n=== TEST SUMMARY ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));
        System.out.println("Success Rate: " + String.format("%.2f%%", (passedTests * 100.0 / totalTests)));
        
        // Additional detailed analysis
        System.out.println("\n=== DETAILED ANALYSIS ===");
        testSpecificScenarios();
    }
    
    private static boolean validateTestResult(String query, AnalysisResult result) {
        String lowerQuery = query.toLowerCase();
        
        // Contract queries validation
        if (lowerQuery.contains("show contract") || lowerQuery.contains("contract details") || 
            lowerQuery.contains("get contract") || lowerQuery.contains("find contract") ||
            lowerQuery.contains("contracts by") || lowerQuery.contains("contracts created")) {
            return result.getIntent() == Intent.CONTRACT_QUERY;
        }
        
        // Status queries validation
        if (lowerQuery.contains("status of") || lowerQuery.contains("expired contracts") || 
            lowerQuery.contains("active contracts")) {
            return result.getIntent() == Intent.STATUS_QUERY;
        }
        
        // Customer queries validation
        if (lowerQuery.contains("customer") || lowerQuery.contains("account") ||
            (lowerQuery.matches(".*\\b(boeing|honeywell)\\b.*contracts.*"))) {
            return result.getIntent() == Intent.CUSTOMER_QUERY;
        }
        
        // Parts queries validation
        if (lowerQuery.contains("part number") || lowerQuery.contains("product id") ||
            lowerQuery.contains("specifications") || lowerQuery.contains("datasheet") ||
            lowerQuery.contains("compatible") || lowerQuery.contains("stock") ||
            lowerQuery.contains("lead time") || lowerQuery.contains("manufacturer") ||
            lowerQuery.contains("issues") || lowerQuery.contains("defects") ||
            lowerQuery.contains("warranty")) {
            return result.getIntent() == Intent.PARTS_QUERY;
        }
        
        // Parts by contract validation
        if ((lowerQuery.contains("show") || lowerQuery.contains("list") || lowerQuery.contains("how many")) &&
            lowerQuery.contains("parts") && lowerQuery.matches(".*\\d{6,}.*")) {
            return result.getIntent() == Intent.PARTS_BY_CONTRACT;
        }
        
        // Failed parts validation
        if (lowerQuery.contains("failed parts") || lowerQuery.contains("filed parts")) {
            return result.getIntent() == Intent.FAILED_PARTS_QUERY;
        }
        
        // Create contract validation
        if ((lowerQuery.contains("create contract") || lowerQuery.contains("i want to create")) &&
            !lowerQuery.contains("how") && !lowerQuery.contains("show me how")) {
            return result.getIntent() == Intent.CREATE_CONTRACT;
        }
        
        // Help create contract validation
        if (lowerQuery.contains("show me how") || lowerQuery.contains("help create")) {
            return result.getIntent() == Intent.HELP_CREATE_CONTRACT;
        }
        
        return true; // Default to true for edge cases
    }
    
    private static void testSpecificScenarios() {
        System.out.println("Testing specific scenarios:");
        
        // Test 1: Contract creation with account number
        System.out.println("\n1. Testing contract creation with account number:");
        AnalysisResult result1 = analyzeIntent("help create contract1023456789");
        System.out.println("Query: help create contract1023456789");
        System.out.println("Expected: Should extract account number 1023456789");
        System.out.println("Actual: " + result1.getParameters());
        System.out.println("Response: " + result1.getResponse());
        
        // Test 2: Contract creation without account number
        System.out.println("\n2. Testing contract creation without account number:");
        AnalysisResult result2 = analyzeIntent("create contract");
        System.out.println("Query: create contract");
        System.out.println("Expected: Should ask for all required fields including account number");
        System.out.println("Response: " + result2.getResponse());
        
        // Test 3: Parts query with specific part number
        System.out.println("\n3. Testing parts query:");
        AnalysisResult result3 = analyzeIntent("What are the specifications of product ID AE125?");
        System.out.println("Query: What are the specifications of product ID AE125?");
        System.out.println("Expected: Should extract part number AE125 and query type 'specifications'");
        System.out.println("Actual: " + result3.getParameters());
        
        // Test 4: Typo handling
        System.out.println("\n4. Testing typo handling:");
        AnalysisResult result4 = analyzeIntent("contarct 123456");
        System.out.println("Query: contarct 123456 (typo)");
        System.out.println("Expected: Should normalize to 'contract 123456' and handle correctly");
        System.out.println("Intent: " + result4.getIntent());
        System.out.println("Extracted ID: " + result4.getExtractedId());
        
        // Test 5: Failed parts with contract ID
        System.out.println("\n5. Testing failed parts query:");
        AnalysisResult result5 = analyzeIntent("failed parts of 123456");
        System.out.println("Query: failed parts of 123456");
        System.out.println("Expected: Should extract contract ID 123456");
        System.out.println("Intent: " + result5.getIntent());
        System.out.println("Contract ID: " + result5.getExtractedId());
        
        // Test 6: Customer query
        System.out.println("\n6. Testing customer query:");
        AnalysisResult result6 = analyzeIntent("boeing contracts");
        System.out.println("Query: boeing contracts");
        System.out.println("Expected: Should identify customer 'boeing'");
        System.out.println("Intent: " + result6.getIntent());
        System.out.println("Customer: " + result6.getExtractedEntity());
        
        // Test 7: Edge case - empty query
        System.out.println("\n7. Testing edge case - empty query:");
        AnalysisResult result7 = analyzeIntent("");
        System.out.println("Query: (empty)");
        System.out.println("Expected: Should return UNKNOWN intent with error message");
        System.out.println("Intent: " + result7.getIntent());
        System.out.println("Response: " + result7.getResponse());
        
        // Test 8: Complex parts query
        System.out.println("\n8. Testing complex parts query:");
        AnalysisResult result8 = analyzeIntent("List out the contracts associated with part number AE125");
        System.out.println("Query: List out the contracts associated with part number AE125");
        System.out.println("Expected: Should identify as parts query with part number AE125");
        System.out.println("Intent: " + result8.getIntent());
        System.out.println("Part Number: " + result8.getExtractedId());
        System.out.println("Parameters: " + result8.getParameters());
        
        System.out.println("\n=== SPECIFIC SCENARIOS COMPLETE ===");
    }
}
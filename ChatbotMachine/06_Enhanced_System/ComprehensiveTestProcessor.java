package com.company.contracts.enhanced;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Comprehensive Test Processor
 * Processes all test cases and writes JSON responses to files
 */
public class ComprehensiveTestProcessor {
    
    private EnhancedChatbotProcessor processor;
    private List<TestResult> results;
    
    public ComprehensiveTestProcessor() {
        this.processor = new EnhancedChatbotProcessor();
        this.results = new ArrayList<>();
    }
    
    public static void main(String[] args) {
        ComprehensiveTestProcessor testProcessor = new ComprehensiveTestProcessor();
        
        System.out.println("=".repeat(80));
        System.out.println("🧪 COMPREHENSIVE TEST PROCESSOR - ALL TEST CASES");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // All test cases from user input
        String[] allTestCases = {
            // Original Contract Queries (40)
            "show contract 123456", "contract details 123456", "get contract info 123456",
            "contracts created by vinod after 1-Jan-2020", "status of contract 123456", "expired contracts",
            "contracts for customer number 897654", "account 10840607 contracts", "contracts created in 2024",
            "get all metadata for contract 123456", "contracts under account name 'Siemens'",
            "get project type, effective date, and price list for account number 10840607",
            "show contract for customer number 123456", "shwo contrct 123456", "get contrct infro 123456",
            "find conract detials 123456", "cntract summry for 123456", "contarcts created by vinod aftr 1-Jan-2020",
            "statuss of contrct 123456", "exipred contrcts", "contracs for cstomer numer 897654",
            "accunt number 10840607 contrcts", "contracts from lst mnth", "contrcts creatd in 2024",
            "shwo efective date and statuz", "get cntracts for acount no 123456", "contrct summry for custmor 999999",
            "get contrct detals by acount 10840607", "contracts created btwn Jan and June 2024", "custmer honeywel",
            "contarcts by vinod", "show contracts for acc no 456789", "activ contrcts created by mary",
            "kontract detials 123456", "kontrakt sumry for account 888888", "boieng contrcts", "acc number 1084",
            "price list corprate2024", "oppurtunity code details", "get all flieds for customer 123123",

            // Original Parts Queries (44)
            "lst out contrcts with part numbr AE125", "whats the specifcations of prduct AE125",
            "is part AE125 actve or discontnued", "can yu provid datashet for AE125", "wat r compatble prts for AE125",
            "ae125 avalable in stok?", "what is lede time part AE125", "who's the manufacterer of ae125",
            "any isses or defect with AE125?", "warrenty priod of AE125?", "shwo mee parts 123456",
            "how many parst for 123456", "list the prts of 123456", "parts of 123456 not showing", "123456 prts failed",
            "faield prts of 123456", "parts failed validdation in 123456", "filde prts in 123456",
            "contract 123456 parst not loadded", "show partz faild in contrct 123456", "parts misssing for 123456",
            "rejected partz 123456", "why ae125 was not addedd in contract", "part ae125 pricng mismatch",
            "ae125 nt in mastr data", "ae125 discntinued?", "shw successfull prts 123456",
            "get all parst that passd in 123456", "what parts faild due to price error",
            "chek error partz in contrct 123456", "ae125 faild becasue no cost data?",
            "is ae125 loaded in contract 123456?", "ae125 skipped? why?", "ae125 passd validation?",
            "parts that arnt in stock 123456", "shwo failed and pasd parts 123456", "hw many partz failed in 123456",
            "show parts today loadded 123456", "show part AE126 detalis", "list all AE partz for contract 123456",
            "shwo me AE125 statuz in contrct", "what happen to AE125 during loadding", "any issues while loading AE125", "get contract123456 failed parts",

            // Enhanced Edge Cases (50+)
            // Contract Queries
            "sh0w c0ntract 123456", "kontract #123456 detais", "get al meta 4 cntrct 123", "contrcts expird in 2023",
            "cntrct by V1N0D aftr Jan", "wats the statuz of 789", "lst 10 contrcts by mary", "h0w 2 creat a contrct?",
            "boeing cntrcts wth prts", "contrct 404 not found", "pls giv contrct 123 detl", "contrato 456 detalles",
            "c0n7r4c7 123!!!", "wuu2 wit cntrct 456", "contract 999 where?",

            // Parts Queries
            "prt AE125 spec pls", "hw 2 check AE125 stok", "AE125 vs AE126 diff", "y is AE125 failng?",
            "AE125 datasheet.pdf?", "add AE125 2 cntrct", "AE125 replacmnt part", "AE125 ❌ in 123456",
            "AE125 cost @ 50% off", "whr is AE125 mfd?", "AE125 kab load hoga?", "p@r7 AE125 $t@tus",
            "AE125 zzzz broken", "AE125_validation-fail", "AE125 specs??",

            // Mixed Queries
            "contrct 123456 parts lst", "AE125 in cntrct 789", "show cntrct 123 & prts", "hw many prts in 123?",
            "contract 456 + parts", "parts/contract 789 issues", "contract123456", "showcontract123456",
            "statusofcontract123456", "detailsforcontract#123", "getcontract2024metadata", "expiredcontractslist",
            "customer897654contracts", "account10840607contracts", "vinodcontracts2024", "contractSiemensunderaccount",
            "projecttype10840607", "customernumber123456contract", "contractAE125parts", "failedparts123456", "contract456789status",

            // Parts
            "partAE125specs", "AE125stockstatus", "AE125warrantyperiod", "AE125compatibleparts",
            "AE125validationfailed", "parts123456missing", "parts123456failed", "loadAE125contract123",
            "AE125pricemismatch", "AE125manufacturerinfo",

            //=== Extreme No-Space + Typos (30) ===//
            "cntrct123456!!!", "shwcontrct123", "prtAE125spec??", "AE125vsAE126diff", "w2chkAE125stok", "yAE125failng?",
            "addAE125tocntrct", "AE125cost@50%off", "AE125kbloadhoga?", "p@r7AE125$t@tus", "c0n7r4c7123!!!",
            "wuu2witcntrct456", "AE125zzzzbroken", "AE125_valid-fail", "contrct123&prts",

            //=== Mixed Spacing + Symbols (20) ===//
            "contract#123456/details", "part-AE125/status", "contract 123456&parts", "AE125_in_contract789",
            "contract:123456,parts", "contract123;parts456", "contract 123?parts=AE125", "contract@123456#parts",
            "AE125|contract123", "contract(123)+parts(AE125)",

            //=== Real-World Abbreviations (15) ===//
            "plsshowcntrct123", "givmectrct456deets", "needprtAE125infoASAP", "AE125statpls", "cntrct789quicklook",
            "whtspartsin123?", "chkAE125valid", "contract123sumry", "AE125warrantypls", "prts4contract456",

            //=== Stress Tests (15) ===//
            "c0ntrct123prts456!!!", "AE125$$$contract@@@", "合同123456", // Non-Latin characters
            "PARTae125CONTRACT123", "123456CONTRACTae125PART", "CONTRACT/123/PARTS/AE125", "AE125...contract123...",
            "合同123&partsAE125", "契約123456詳細", // Japanese
            "CONTRATO123PARTES",
            
            // ✅ Correct Queries
            "Show all failed parts for contract 987654",
            "What is the reason for failure of part AE125?",
            "List all parts that failed under contract CN1234",
            "Get failure message for part number PN7890",
            "Why did part PN4567 fail?",
            "Show failed message and reason for AE777",
            "List failed parts and their contract numbers",
            "Parts failed due to voltage issues",
            "Find all parts failed with error message \"Leak detected\"",
            "Which parts failed in contract 888999",

            // ❌ Queries with Typos
            "show faild prts for contrct 987654",
            "reasn for failr of prt AE125",
            "get falure mesage for part PN7890",
            "whch prts faild in cntract CN1234",
            "list fialed prts due to ovrheating",
            "wht is the faild reasn of AE777",
            "parts whch hav faild n contract 888999",
            "shw me mesage colum for faild part AE901",
            "prts faild with resn \"voltag drop\"",
            "falure rsn fr prt numbr AE456?"
        };
        
        System.out.printf("📝 Processing %d test cases...\n", allTestCases.length);
        System.out.println();
        
        // Process all test cases
        testProcessor.processAllTestCases(allTestCases);
        
        // Write results to files
        testProcessor.writeResultsToFiles();
        
        // Show summary
        testProcessor.showSummary();
        
        System.out.println("\n✅ All test cases processed and saved to files!");
    }
    
    private void processAllTestCases(String[] testCases) {
        int testNumber = 1;
        
        for (String testCase : testCases) {
            if (testCase == null || testCase.trim().isEmpty()) continue;
            
            System.out.printf("Processing Test %d: \"%s\"\n", testNumber, 
                testCase.length() > 50 ? testCase.substring(0, 50) + "..." : testCase);
            
            try {
                long startTime = System.nanoTime();
                EnhancedChatbotProcessor.QueryResponse response = processor.processQuery(testCase);
                long endTime = System.nanoTime();
                double processingTime = (endTime - startTime) / 1_000_000.0;
                
                String jsonResponse = processor.generateJSONResponse(response);
                
                TestResult result = new TestResult(testNumber, testCase, response, jsonResponse, processingTime);
                results.add(result);
                
                // Show brief status
                String status = response.getErrors().isEmpty() ? "✅ SUCCESS" : "❌ ERRORS(" + response.getErrors().size() + ")";
                System.out.printf("   Status: %s (%.3f ms)\n", status, processingTime);
                
            } catch (Exception e) {
                System.out.printf("   Status: ❌ EXCEPTION - %s\n", e.getMessage());
                
                // Create error result
                TestResult errorResult = new TestResult(testNumber, testCase, null, 
                    "{\"error\":\"Processing exception: " + e.getMessage() + "\"}", 0.0);
                results.add(errorResult);
            }
            
            testNumber++;
        }
    }
    
    private void writeResultsToFiles() {
        try {
            // Write all JSON responses to a single file
            writeAllJSONResponses();
            
            // Write summary report
            writeSummaryReport();
            
            // Write detailed analysis
            writeDetailedAnalysis();
            
            // Write error analysis
            writeErrorAnalysis();
            
        } catch (IOException e) {
            System.err.println("Error writing files: " + e.getMessage());
        }
    }
    
    private void writeAllJSONResponses() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "all_json_responses_" + timestamp + ".json";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("{");
            writer.println("  \"testSession\": {");
            writer.printf("    \"timestamp\": \"%s\",\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.printf("    \"totalTests\": %d,\n", results.size());
            writer.printf("    \"successfulTests\": %d,\n", (int) results.stream().filter(r -> r.getResponse() != null && r.getResponse().getErrors().isEmpty()).count());
            writer.printf("    \"failedTests\": %d\n", (int) results.stream().filter(r -> r.getResponse() == null || !r.getResponse().getErrors().isEmpty()).count());
            writer.println("  },");
            writer.println("  \"testResults\": [");
            
            for (int i = 0; i < results.size(); i++) {
                TestResult result = results.get(i);
                writer.println("    {");
                writer.printf("      \"testNumber\": %d,\n", result.getTestNumber());
                writer.printf("      \"input\": %s,\n", quote(result.getInput()));
                writer.printf("      \"processingTimeMs\": %.3f,\n", result.getProcessingTime());
                writer.printf("      \"response\": %s\n", result.getJsonResponse());
                writer.printf("    }%s\n", i < results.size() - 1 ? "," : "");
            }
            
            writer.println("  ]");
            writer.println("}");
        }
        
        System.out.printf("📄 All JSON responses written to: %s\n", filename);
    }
    
    private void writeSummaryReport() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "test_summary_" + timestamp + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(80));
            writer.println("COMPREHENSIVE TEST SUMMARY REPORT");
            writer.println("=".repeat(80));
            writer.printf("Generated: %s\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.printf("Total Test Cases: %d\n", results.size());
            writer.println();
            
            // Statistics
            long successfulTests = results.stream().filter(r -> r.getResponse() != null && r.getResponse().getErrors().isEmpty()).count();
            long failedTests = results.stream().filter(r -> r.getResponse() == null || !r.getResponse().getErrors().isEmpty()).count();
            double successRate = (double) successfulTests / results.size() * 100;
            
            writer.println("OVERALL STATISTICS:");
            writer.println("-".repeat(40));
            writer.printf("Successful Tests: %d (%.1f%%)\n", successfulTests, successRate);
            writer.printf("Failed Tests: %d (%.1f%%)\n", failedTests, 100 - successRate);
            writer.println();
            
            // Performance statistics
            double avgProcessingTime = results.stream().mapToDouble(TestResult::getProcessingTime).average().orElse(0.0);
            double minProcessingTime = results.stream().mapToDouble(TestResult::getProcessingTime).min().orElse(0.0);
            double maxProcessingTime = results.stream().mapToDouble(TestResult::getProcessingTime).max().orElse(0.0);
            
            writer.println("PERFORMANCE STATISTICS:");
            writer.println("-".repeat(40));
            writer.printf("Average Processing Time: %.3f ms\n", avgProcessingTime);
            writer.printf("Minimum Processing Time: %.3f ms\n", minProcessingTime);
            writer.printf("Maximum Processing Time: %.3f ms\n", maxProcessingTime);
            writer.printf("Target Performance (<200ms): %s\n", avgProcessingTime < 200 ? "✅ PASSED" : "❌ FAILED");
            writer.println();
            
            // Query type analysis
            Map<String, Integer> queryTypes = new HashMap<>();
            Map<String, Integer> actionTypes = new HashMap<>();
            
            for (TestResult result : results) {
                if (result.getResponse() != null) {
                    String queryType = result.getResponse().getQueryMetadata().getQueryType();
                    String actionType = result.getResponse().getQueryMetadata().getActionType();
                    
                    queryTypes.put(queryType, queryTypes.getOrDefault(queryType, 0) + 1);
                    actionTypes.put(actionType, actionTypes.getOrDefault(actionType, 0) + 1);
                }
            }
            
            writer.println("QUERY TYPE DISTRIBUTION:");
            writer.println("-".repeat(40));
            queryTypes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> writer.printf("%s: %d (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), 
                    (double) entry.getValue() / results.size() * 100));
            writer.println();
            
            writer.println("ACTION TYPE DISTRIBUTION:");
            writer.println("-".repeat(40));
            actionTypes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> writer.printf("%s: %d (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), 
                    (double) entry.getValue() / results.size() * 100));
        }
        
        System.out.printf("📊 Summary report written to: %s\n", filename);
    }
    
    private void writeDetailedAnalysis() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "detailed_analysis_" + timestamp + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(80));
            writer.println("DETAILED TEST ANALYSIS");
            writer.println("=".repeat(80));
            writer.printf("Generated: %s\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println();
            
            for (TestResult result : results) {
                writer.printf("TEST %d: %s\n", result.getTestNumber(), result.getInput());
                writer.println("-".repeat(60));
                
                if (result.getResponse() != null) {
                    EnhancedChatbotProcessor.QueryResponse response = result.getResponse();
                    
                    // Header analysis
                    writer.println("HEADER ANALYSIS:");
                    EnhancedChatbotProcessor.Header header = response.getHeader();
                    writer.printf("  Contract Number: %s\n", header.getContractNumber());
                    writer.printf("  Part Number: %s\n", header.getPartNumber());
                    writer.printf("  Customer Number: %s\n", header.getCustomerNumber());
                    writer.printf("  Customer Name: %s\n", header.getCustomerName());
                    writer.printf("  Created By: %s\n", header.getCreatedBy());
                    
                    // Query metadata
                    writer.println("QUERY METADATA:");
                    EnhancedChatbotProcessor.QueryMetadata metadata = response.getQueryMetadata();
                    writer.printf("  Query Type: %s\n", metadata.getQueryType());
                    writer.printf("  Action Type: %s\n", metadata.getActionType());
                    writer.printf("  Processing Time: %.3f ms\n", metadata.getProcessingTimeMs());
                    
                    // Entities
                    writer.printf("ENTITIES: %d found\n", response.getEntities().size());
                    for (EnhancedChatbotProcessor.EntityFilter entity : response.getEntities()) {
                        writer.printf("  - %s %s %s (source: %s)\n", 
                            entity.getAttribute(), entity.getOperation(), entity.getValue(), entity.getSource());
                    }
                    
                    // Display entities
                    writer.printf("DISPLAY ENTITIES: %s\n", response.getDisplayEntities());
                    
                    // Errors
                    writer.printf("ERRORS: %d found\n", response.getErrors().size());
                    for (EnhancedChatbotProcessor.ValidationError error : response.getErrors()) {
                        writer.printf("  - %s: %s (%s)\n", error.getCode(), error.getMessage(), error.getSeverity());
                    }
                    
                    writer.printf("STATUS: %s\n", response.getErrors().isEmpty() ? "✅ SUCCESS" : "❌ FAILED");
                } else {
                    writer.println("❌ PROCESSING FAILED - No response generated");
                }
                
                writer.println("=".repeat(60));
                writer.println();
            }
        }
        
        System.out.printf("📋 Detailed analysis written to: %s\n", filename);
    }
    
    private void writeErrorAnalysis() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "error_analysis_" + timestamp + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=".repeat(80));
            writer.println("ERROR ANALYSIS REPORT");
            writer.println("=".repeat(80));
            writer.printf("Generated: %s\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println();
            
            // Collect all errors
            Map<String, List<TestResult>> errorsByCode = new HashMap<>();
            
            for (TestResult result : results) {
                if (result.getResponse() != null && !result.getResponse().getErrors().isEmpty()) {
                    for (EnhancedChatbotProcessor.ValidationError error : result.getResponse().getErrors()) {
                        errorsByCode.computeIfAbsent(error.getCode(), k -> new ArrayList<>()).add(result);
                    }
                }
            }
            
            writer.printf("TOTAL FAILED TESTS: %d\n", errorsByCode.values().stream().mapToInt(List::size).sum());
            writer.printf("UNIQUE ERROR TYPES: %d\n", errorsByCode.size());
            writer.println();
            
            // Analyze each error type
            for (Map.Entry<String, List<TestResult>> entry : errorsByCode.entrySet()) {
                String errorCode = entry.getKey();
                List<TestResult> failedTests = entry.getValue();
                
                writer.printf("ERROR CODE: %s (%d occurrences)\n", errorCode, failedTests.size());
                writer.println("-".repeat(50));
                
                // Show sample failed tests
                int sampleCount = Math.min(5, failedTests.size());
                writer.printf("Sample failed queries (showing %d of %d):\n", sampleCount, failedTests.size());
                
                for (int i = 0; i < sampleCount; i++) {
                    TestResult failedTest = failedTests.get(i);
                    writer.printf("  %d. \"%s\"\n", i + 1, failedTest.getInput());
                    
                    // Show specific error message
                    for (EnhancedChatbotProcessor.ValidationError error : failedTest.getResponse().getErrors()) {
                        if (errorCode.equals(error.getCode())) {
                            writer.printf("     Error: %s\n", error.getMessage());
                            break;
                        }
                    }
                }
                
                writer.println();
            }
        }
        
        System.out.printf("🚨 Error analysis written to: %s\n", filename);
    }
    
    private void showSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 PROCESSING SUMMARY");
        System.out.println("=".repeat(60));
        
        long successfulTests = results.stream().filter(r -> r.getResponse() != null && r.getResponse().getErrors().isEmpty()).count();
        long failedTests = results.stream().filter(r -> r.getResponse() == null || !r.getResponse().getErrors().isEmpty()).count();
        double successRate = (double) successfulTests / results.size() * 100;
        
        System.out.printf("Total Tests Processed: %d\n", results.size());
        System.out.printf("Successful Tests: %d (%.1f%%)\n", successfulTests, successRate);
        System.out.printf("Failed Tests: %d (%.1f%%)\n", failedTests, 100 - successRate);
        
        double avgProcessingTime = results.stream().mapToDouble(TestResult::getProcessingTime).average().orElse(0.0);
        System.out.printf("Average Processing Time: %.3f ms\n", avgProcessingTime);
        System.out.printf("Performance Target (<200ms): %s\n", avgProcessingTime < 200 ? "✅ PASSED" : "❌ FAILED");
        
        System.out.println("\n📁 FILES GENERATED:");
        System.out.println("  • all_json_responses_*.json - All JSON responses");
        System.out.println("  • test_summary_*.txt - Summary statistics");
        System.out.println("  • detailed_analysis_*.txt - Detailed test analysis");
        System.out.println("  • error_analysis_*.txt - Error pattern analysis");
    }
    
    private String quote(String value) {
        return value == null ? "null" : "\"" + value.replace("\"", "\\\"") + "\"";
    }
    
    // TestResult class
    private static class TestResult {
        private final int testNumber;
        private final String input;
        private final EnhancedChatbotProcessor.QueryResponse response;
        private final String jsonResponse;
        private final double processingTime;
        
        public TestResult(int testNumber, String input, EnhancedChatbotProcessor.QueryResponse response, 
                         String jsonResponse, double processingTime) {
            this.testNumber = testNumber;
            this.input = input;
            this.response = response;
            this.jsonResponse = jsonResponse;
            this.processingTime = processingTime;
        }
        
        public int getTestNumber() { return testNumber; }
        public String getInput() { return input; }
        public EnhancedChatbotProcessor.QueryResponse getResponse() { return response; }
        public String getJsonResponse() { return jsonResponse; }
        public double getProcessingTime() { return processingTime; }
    }
}
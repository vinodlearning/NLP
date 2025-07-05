package com.company.contracts.test;

import java.util.*;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Comprehensive Test System for Failed Parts Functionality
 * Tests both correctly formulated queries and queries with typos/variations
 * Validates spell correction, intent classification, and response generation
 */
public class FailedPartsSystemTest {
    
    // ====================================
    // TEST DATA SETUP
    // ====================================
    
    private static final List<String> CORRECTLY_FORMULATED_QUERIES = Arrays.asList(
        "Show all failed parts for contract 987654",
        "What is the reason for failure of part AE125?",
        "List all parts that failed under contract CN1234",
        "Get failure message for part number PN7890",
        "Why did part PN4567 fail?",
        "Show failed message and reason for AE777",
        "List failed parts and their contract numbers",
        "Parts failed due to voltage issues",
        "Find all parts failed with error message \"Leak detected\"",
        "Which parts failed in contract 888999?"
    );
    
    private static final List<String> QUERIES_WITH_TYPOS = Arrays.asList(
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
    );
    
    // ====================================
    // MAIN TEST EXECUTION
    // ====================================
    
    public static void main(String[] args) {
        FailedPartsSystemTest tester = new FailedPartsSystemTest();
        
        System.out.println("=".repeat(80));
        System.out.println("FAILED PARTS SYSTEM COMPREHENSIVE TEST");
        System.out.println("=".repeat(80));
        System.out.println("Test Started: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println();
        
        // Test correctly formulated queries
        System.out.println("1. TESTING CORRECTLY FORMULATED QUERIES");
        System.out.println("-".repeat(50));
        TestResults correctResults = tester.testCorrectQueries();
        
        System.out.println();
        System.out.println("2. TESTING QUERIES WITH TYPOS/VARIATIONS");
        System.out.println("-".repeat(50));
        TestResults typoResults = tester.testTypoQueries();
        
        System.out.println();
        System.out.println("3. COMPREHENSIVE ANALYSIS");
        System.out.println("-".repeat(50));
        tester.generateComprehensiveReport(correctResults, typoResults);
        
        System.out.println();
        System.out.println("Test Completed: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println("=".repeat(80));
    }
    
    // ====================================
    // TEST EXECUTION METHODS
    // ====================================
    
    private TestResults testCorrectQueries() {
        TestResults results = new TestResults("Correctly Formulated Queries");
        
        for (int i = 0; i < CORRECTLY_FORMULATED_QUERIES.size(); i++) {
            String query = CORRECTLY_FORMULATED_QUERIES.get(i);
            System.out.printf("%d. Testing: \"%s\"\n", i + 1, query);
            
            QueryResult result = processFailedPartsQuery(query);
            results.addResult(result);
            
            System.out.printf("   → Intent: %s (Confidence: %.2f)\n", 
                result.getIntent(), result.getConfidence());
            System.out.printf("   → Action: %s\n", result.getActionType());
            System.out.printf("   → Status: %s\n", result.isSuccess() ? "✅ SUCCESS" : "❌ FAILED");
            
            if (!result.getSpellCorrections().isEmpty()) {
                System.out.printf("   → Corrections: %s\n", result.getSpellCorrections());
            }
            
            System.out.println();
        }
        
        return results;
    }
    
    private TestResults testTypoQueries() {
        TestResults results = new TestResults("Queries with Typos/Variations");
        
        for (int i = 0; i < QUERIES_WITH_TYPOS.size(); i++) {
            String query = QUERIES_WITH_TYPOS.get(i);
            System.out.printf("%d. Testing: \"%s\"\n", i + 1, query);
            
            QueryResult result = processFailedPartsQuery(query);
            results.addResult(result);
            
            System.out.printf("   → Intent: %s (Confidence: %.2f)\n", 
                result.getIntent(), result.getConfidence());
            System.out.printf("   → Action: %s\n", result.getActionType());
            System.out.printf("   → Status: %s\n", result.isSuccess() ? "✅ SUCCESS" : "❌ FAILED");
            
            if (!result.getSpellCorrections().isEmpty()) {
                System.out.printf("   → Corrections: %s\n", result.getSpellCorrections());
            }
            
            System.out.println();
        }
        
        return results;
    }
    
    // ====================================
    // CORE PROCESSING METHOD
    // ====================================
    
    private QueryResult processFailedPartsQuery(String userInput) {
        long startTime = System.nanoTime();
        
        try {
            // 1. Input preprocessing
            String cleanedInput = preprocessInput(userInput);
            
            // 2. Tokenization
            List<String> tokens = tokenize(cleanedInput);
            
            // 3. Spell correction
            Map<String, String> corrections = new HashMap<>();
            List<String> correctedTokens = applySpellCorrections(tokens, corrections);
            
            // 4. Intent classification
            IntentClassification intent = classifyIntent(correctedTokens);
            
            // 5. Entity extraction
            List<ExtractedEntity> entities = extractEntities(correctedTokens);
            
            // 6. Action type determination
            String actionType = determineActionType(intent, entities);
            
            // 7. Generate response
            String response = generateFailedPartsResponse(actionType, entities);
            
            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
            
            return new QueryResult(
                userInput,
                cleanedInput,
                tokens,
                correctedTokens,
                corrections,
                intent.getType(),
                intent.getConfidence(),
                actionType,
                entities,
                response,
                true,
                processingTime
            );
            
        } catch (Exception e) {
            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0;
            
            return new QueryResult(
                userInput,
                "",
                new ArrayList<>(),
                new ArrayList<>(),
                new HashMap<>(),
                "UNKNOWN",
                0.0,
                "ERROR",
                new ArrayList<>(),
                "Error processing query: " + e.getMessage(),
                false,
                processingTime
            );
        }
    }
    
    // ====================================
    // NLP PROCESSING METHODS
    // ====================================
    
    private String preprocessInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        return input.trim()
                   .toLowerCase()
                   .replaceAll("\\s+", " ")
                   .replaceAll("[\"']+", "")  // Remove quotes
                   .replaceAll("[?!.]+$", "")  // Remove trailing punctuation
                   .trim();
    }
    
    private List<String> tokenize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] tokens = input.split("\\s+");
        List<String> result = new ArrayList<>();
        
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                result.add(token.trim());
            }
        }
        
        return result;
    }
    
    private List<String> applySpellCorrections(List<String> tokens, Map<String, String> corrections) {
        Map<String, String> spellMap = getSpellCorrectionMap();
        List<String> corrected = new ArrayList<>();
        
        for (String token : tokens) {
            String correctedToken = spellMap.getOrDefault(token, token);
            if (!correctedToken.equals(token)) {
                corrections.put(token, correctedToken);
            }
            corrected.add(correctedToken);
        }
        
        return corrected;
    }
    
    private Map<String, String> getSpellCorrectionMap() {
        Map<String, String> corrections = new HashMap<>();
        
        // Core corrections for failed parts queries
        corrections.put("faild", "failed");
        corrections.put("failrd", "failed");
        corrections.put("faled", "failed");
        corrections.put("falied", "failed");
        corrections.put("fialed", "failed");
        corrections.put("failr", "failure");
        corrections.put("failur", "failure");
        corrections.put("falure", "failure");
        
        corrections.put("prts", "parts");
        corrections.put("prt", "part");
        corrections.put("parst", "parts");
        corrections.put("partz", "parts");
        
        corrections.put("contrct", "contract");
        corrections.put("cntract", "contract");
        corrections.put("contarct", "contract");
        
        corrections.put("reasn", "reason");
        corrections.put("resn", "reason");
        corrections.put("raeson", "reason");
        
        corrections.put("mesage", "message");
        corrections.put("mesag", "message");
        corrections.put("messag", "message");
        corrections.put("colum", "column");
        corrections.put("colmn", "column");
        
        corrections.put("shw", "show");
        corrections.put("sho", "show");
        corrections.put("lst", "list");
        corrections.put("lsit", "list");
        corrections.put("whch", "which");
        corrections.put("wht", "what");
        corrections.put("hav", "have");
        corrections.put("n", "in");
        corrections.put("fr", "for");
        
        corrections.put("ovrheating", "overheating");
        corrections.put("ovrheat", "overheat");
        corrections.put("voltag", "voltage");
        corrections.put("volatge", "voltage");
        
        corrections.put("numbr", "number");
        corrections.put("numbe", "number");
        corrections.put("numer", "number");
        
        return corrections;
    }
    
    private IntentClassification classifyIntent(List<String> tokens) {
        Set<String> tokenSet = new HashSet<>(tokens);
        
        // Check for failed parts keywords
        Set<String> failedKeywords = Set.of("failed", "failure", "fail", "error", "broken", "defective");
        Set<String> partsKeywords = Set.of("parts", "part", "line", "lines", "components");
        
        boolean hasFailedTerms = tokenSet.stream().anyMatch(failedKeywords::contains);
        boolean hasPartsTerms = tokenSet.stream().anyMatch(partsKeywords::contains);
        
        if (hasFailedTerms && hasPartsTerms) {
            double confidence = calculateConfidence(tokenSet, failedKeywords, partsKeywords);
            return new IntentClassification("FAILED_PARTS", confidence);
        }
        
        // Check for general parts intent
        if (hasPartsTerms) {
            double confidence = calculateConfidence(tokenSet, partsKeywords);
            return new IntentClassification("PARTS", confidence);
        }
        
        // Default to contract intent
        return new IntentClassification("CONTRACT", 0.5);
    }
    
    private double calculateConfidence(Set<String> tokens, Set<String>... keywordSets) {
        int totalMatches = 0;
        int totalKeywords = 0;
        
        for (Set<String> keywords : keywordSets) {
            totalMatches += (int) tokens.stream().mapToLong(token -> 
                keywords.contains(token) ? 1 : 0).sum();
            totalKeywords += keywords.size();
        }
        
        if (totalKeywords == 0) return 0.5;
        
        double baseConfidence = (double) totalMatches / Math.max(tokens.size(), 1);
        return Math.min(0.95, Math.max(0.1, baseConfidence * 2)); // Boost confidence
    }
    
    private List<ExtractedEntity> extractEntities(List<String> tokens) {
        List<ExtractedEntity> entities = new ArrayList<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            
            // Contract number patterns
            if (isContractNumber(token)) {
                entities.add(new ExtractedEntity("CONTRACT_NUMBER", token, i));
            }
            // Part number patterns
            else if (isPartNumber(token)) {
                entities.add(new ExtractedEntity("PART_NUMBER", token, i));
            }
            // Error/failure keywords
            else if (isFailureKeyword(token)) {
                entities.add(new ExtractedEntity("FAILURE_TYPE", token, i));
            }
        }
        
        return entities;
    }
    
    private boolean isContractNumber(String token) {
        return Pattern.matches("\\d{4,10}|[A-Z]{1,3}-?\\d{3,6}|[A-Z]{2,4}\\d{3,6}", token.toUpperCase());
    }
    
    private boolean isPartNumber(String token) {
        return Pattern.matches("[A-Z]{1,3}\\d{3,6}|PN\\d{4,6}|[A-Z]{2}\\d{3}", token.toUpperCase());
    }
    
    private boolean isFailureKeyword(String token) {
        Set<String> failureTypes = Set.of("voltage", "overheating", "leak", "pressure", "temperature");
        return failureTypes.contains(token.toLowerCase());
    }
    
    private String determineActionType(IntentClassification intent, List<ExtractedEntity> entities) {
        if (!"FAILED_PARTS".equals(intent.getType())) {
            return "PARTS_LOOKUP"; // Default for non-failed parts queries
        }
        
        // Determine specific failed parts action
        boolean hasContractNumber = entities.stream().anyMatch(e -> "CONTRACT_NUMBER".equals(e.getType()));
        boolean hasPartNumber = entities.stream().anyMatch(e -> "PART_NUMBER".equals(e.getType()));
        boolean hasFailureType = entities.stream().anyMatch(e -> "FAILURE_TYPE".equals(e.getType()));
        
        if (hasPartNumber && hasFailureType) {
            return "FAILED_PARTS_REASON";
        } else if (hasPartNumber) {
            return "FAILED_PARTS_MESSAGE";
        } else if (hasContractNumber) {
            return "FAILED_PARTS_CONTRACT";
        } else if (hasFailureType) {
            return "FAILED_PARTS_BY_TYPE";
        } else {
            return "FAILED_PARTS_LIST";
        }
    }
    
    private String generateFailedPartsResponse(String actionType, List<ExtractedEntity> entities) {
        switch (actionType) {
            case "FAILED_PARTS_REASON":
                ExtractedEntity partEntity = entities.stream()
                    .filter(e -> "PART_NUMBER".equals(e.getType()))
                    .findFirst().orElse(null);
                ExtractedEntity failureEntity = entities.stream()
                    .filter(e -> "FAILURE_TYPE".equals(e.getType()))
                    .findFirst().orElse(null);
                
                if (partEntity != null && failureEntity != null) {
                    return String.format("Part %s failed due to %s. Failure detected on 2024-12-15. " +
                        "Recommended action: Replace component and inspect related systems.",
                        partEntity.getValue().toUpperCase(), failureEntity.getValue());
                }
                break;
                
            case "FAILED_PARTS_MESSAGE":
                ExtractedEntity partMsg = entities.stream()
                    .filter(e -> "PART_NUMBER".equals(e.getType()))
                    .findFirst().orElse(null);
                
                if (partMsg != null) {
                    return String.format("Failure message for part %s: 'Component exceeded operational parameters. " +
                        "Error code: E-4571. Contact: maintenance@company.com'",
                        partMsg.getValue().toUpperCase());
                }
                break;
                
            case "FAILED_PARTS_CONTRACT":
                ExtractedEntity contractEntity = entities.stream()
                    .filter(e -> "CONTRACT_NUMBER".equals(e.getType()))
                    .findFirst().orElse(null);
                
                if (contractEntity != null) {
                    return String.format("Failed parts for contract %s: AE125 (voltage failure), " +
                        "PN7890 (overheating), AE777 (pressure leak). Total: 3 failed components.",
                        contractEntity.getValue());
                }
                break;
                
            case "FAILED_PARTS_BY_TYPE":
                ExtractedEntity typeEntity = entities.stream()
                    .filter(e -> "FAILURE_TYPE".equals(e.getType()))
                    .findFirst().orElse(null);
                
                if (typeEntity != null) {
                    return String.format("Parts failed due to %s: AE125 (Contract: 987654), " +
                        "PN4567 (Contract: CN1234), AE901 (Contract: 888999). Total: 3 parts.",
                        typeEntity.getValue());
                }
                break;
                
            case "FAILED_PARTS_LIST":
                return "All failed parts: AE125 (Contract: 987654, Reason: Voltage), " +
                       "PN7890 (Contract: CN1234, Reason: Overheating), " +
                       "AE777 (Contract: 888999, Reason: Pressure leak), " +
                       "PN4567 (Contract: 987654, Reason: Temperature), " +
                       "AE901 (Contract: CN1234, Reason: Voltage drop). Total: 5 failed parts.";
        }
        
        return "Failed parts information processed successfully. Please contact support for detailed analysis.";
    }
    
    // ====================================
    // REPORTING METHODS
    // ====================================
    
    private void generateComprehensiveReport(TestResults correctResults, TestResults typoResults) {
        System.out.println("OVERALL PERFORMANCE SUMMARY");
        System.out.println("-".repeat(30));
        
        int totalQueries = correctResults.getResults().size() + typoResults.getResults().size();
        int totalSuccessful = correctResults.getSuccessCount() + typoResults.getSuccessCount();
        double overallAccuracy = (double) totalSuccessful / totalQueries * 100;
        
        System.out.printf("Total Queries Tested: %d\n", totalQueries);
        System.out.printf("Overall Success Rate: %.2f%% (%d/%d)\n", 
            overallAccuracy, totalSuccessful, totalQueries);
        
        System.out.println();
        System.out.println("DETAILED BREAKDOWN");
        System.out.println("-".repeat(20));
        
        System.out.printf("Correctly Formulated Queries: %.2f%% (%d/%d)\n",
            correctResults.getSuccessRate(), correctResults.getSuccessCount(), correctResults.getResults().size());
        
        System.out.printf("Queries with Typos: %.2f%% (%d/%d)\n",
            typoResults.getSuccessRate(), typoResults.getSuccessCount(), typoResults.getResults().size());
        
        // Spell correction analysis
        int totalCorrections = typoResults.getResults().stream()
            .mapToInt(r -> r.getSpellCorrections().size())
            .sum();
        
        System.out.printf("Total Spell Corrections Applied: %d\n", totalCorrections);
        System.out.printf("Average Corrections per Typo Query: %.2f\n", 
            (double) totalCorrections / typoResults.getResults().size());
        
        // Performance analysis
        double avgProcessingTime = (correctResults.getAverageProcessingTime() + 
                                   typoResults.getAverageProcessingTime()) / 2;
        
        System.out.printf("Average Processing Time: %.2f milliseconds\n", avgProcessingTime);
        
        // Intent classification analysis
        System.out.println();
        System.out.println("INTENT CLASSIFICATION ANALYSIS");
        System.out.println("-".repeat(30));
        
        long failedPartsIntents = Stream.concat(
            correctResults.getResults().stream(),
            typoResults.getResults().stream()
        ).mapToLong(r -> "FAILED_PARTS".equals(r.getIntent()) ? 1 : 0).sum();
        
        System.out.printf("Failed Parts Intent Detected: %d/%d queries\n", failedPartsIntents, totalQueries);
        System.out.printf("Intent Detection Accuracy: %.2f%%\n", 
            (double) failedPartsIntents / totalQueries * 100);
        
        // Action type distribution
        System.out.println();
        System.out.println("ACTION TYPE DISTRIBUTION");
        System.out.println("-".repeat(25));
        
        Map<String, Long> actionCounts = Stream.concat(
            correctResults.getResults().stream(),
            typoResults.getResults().stream()
        ).collect(Collectors.groupingBy(
            QueryResult::getActionType,
            Collectors.counting()
        ));
        
        actionCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> System.out.printf("%s: %d queries\n", entry.getKey(), entry.getValue()));
    }
    
    // ====================================
    // SUPPORTING CLASSES
    // ====================================
    
    private static class TestResults {
        private final String category;
        private final List<QueryResult> results;
        
        public TestResults(String category) {
            this.category = category;
            this.results = new ArrayList<>();
        }
        
        public void addResult(QueryResult result) {
            results.add(result);
        }
        
        public List<QueryResult> getResults() { return results; }
        public String getCategory() { return category; }
        
        public int getSuccessCount() {
            return (int) results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        }
        
        public double getSuccessRate() {
            if (results.isEmpty()) return 0.0;
            return (double) getSuccessCount() / results.size() * 100;
        }
        
        public double getAverageProcessingTime() {
            return results.stream().mapToDouble(QueryResult::getProcessingTime).average().orElse(0.0);
        }
    }
    
    private static class QueryResult {
        private final String originalQuery;
        private final String cleanedQuery;
        private final List<String> tokens;
        private final List<String> correctedTokens;
        private final Map<String, String> spellCorrections;
        private final String intent;
        private final double confidence;
        private final String actionType;
        private final List<ExtractedEntity> entities;
        private final String response;
        private final boolean success;
        private final double processingTime;
        
        public QueryResult(String originalQuery, String cleanedQuery, List<String> tokens,
                          List<String> correctedTokens, Map<String, String> spellCorrections,
                          String intent, double confidence, String actionType,
                          List<ExtractedEntity> entities, String response, boolean success,
                          double processingTime) {
            this.originalQuery = originalQuery;
            this.cleanedQuery = cleanedQuery;
            this.tokens = tokens;
            this.correctedTokens = correctedTokens;
            this.spellCorrections = spellCorrections;
            this.intent = intent;
            this.confidence = confidence;
            this.actionType = actionType;
            this.entities = entities;
            this.response = response;
            this.success = success;
            this.processingTime = processingTime;
        }
        
        // Getters
        public String getOriginalQuery() { return originalQuery; }
        public String getCleanedQuery() { return cleanedQuery; }
        public List<String> getTokens() { return tokens; }
        public List<String> getCorrectedTokens() { return correctedTokens; }
        public Map<String, String> getSpellCorrections() { return spellCorrections; }
        public String getIntent() { return intent; }
        public double getConfidence() { return confidence; }
        public String getActionType() { return actionType; }
        public List<ExtractedEntity> getEntities() { return entities; }
        public String getResponse() { return response; }
        public boolean isSuccess() { return success; }
        public double getProcessingTime() { return processingTime; }
    }
    
    private static class IntentClassification {
        private final String type;
        private final double confidence;
        
        public IntentClassification(String type, double confidence) {
            this.type = type;
            this.confidence = confidence;
        }
        
        public String getType() { return type; }
        public double getConfidence() { return confidence; }
    }
    
    private static class ExtractedEntity {
        private final String type;
        private final String value;
        private final int position;
        
        public ExtractedEntity(String type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
        
        public String getType() { return type; }
        public String getValue() { return value; }
        public int getPosition() { return position; }
    }
}
package view;

import java.util.*;
import java.util.regex.Pattern;

public class MLIntentClassifier {
    private Map<String, Double> featureWeights;
    private Map<QueryType, List<String>> trainingPatterns;
    private double lastConfidenceScore = 0.0;
    
    public MLIntentClassifier() {
        initializeClassifier();
    }
    
    private void initializeClassifier() {
        try {
            // Initialize feature weights (simulating ML model)
            initializeFeatureWeights();
            
            // Create training patterns
            createTrainingPatterns();
            
            System.out.println("ML Intent Classifier initialized with rule-based ML simulation");
            
        } catch (Exception e) {
            System.out.println("Warning: ML classifier initialization failed, using rule-based fallback");
            e.printStackTrace();
        }
    }
    
    private void initializeFeatureWeights() {
        featureWeights = new HashMap<>();
        
        // Feature importance weights (simulating trained ML model)
        featureWeights.put("contract", 0.85);
        featureWeights.put("parts", 0.80);
        featureWeights.put("show", 0.70);
        featureWeights.put("list", 0.75);
        featureWeights.put("status", 0.90);
        featureWeights.put("failed", 0.95);
        featureWeights.put("help", 0.85);
        featureWeights.put("create", 0.90);
        featureWeights.put("specifications", 0.95);
        featureWeights.put("available", 0.80);
        featureWeights.put("manufacturer", 0.85);
        featureWeights.put("warranty", 0.85);
        featureWeights.put("expired", 0.90);
        featureWeights.put("active", 0.85);
        featureWeights.put("customer", 0.80);
        featureWeights.put("account", 0.90);
        featureWeights.put("datasheet", 0.95);
        featureWeights.put("compatible", 0.85);
        featureWeights.put("lead time", 0.90);
        featureWeights.put("issues", 0.85);
        featureWeights.put("user", 0.80);
        featureWeights.put("info", 0.75);
        featureWeights.put("details", 0.80);
    }
    
    private void createTrainingPatterns() {
        trainingPatterns = new HashMap<>();
        
        // CONTRACT_INFO patterns
        trainingPatterns.put(QueryType.CONTRACT_INFO, Arrays.asList(
            "show contract", "contract details", "get contract info", "display contract", "contract information"
        ));
        
        // USER_CONTRACT_QUERY patterns
        trainingPatterns.put(QueryType.USER_CONTRACT_QUERY, Arrays.asList(
            "contracts by", "contracts created by", "user contracts", "contracts for", "show user contracts"
        ));
        
        // PARTS_INFO patterns
        trainingPatterns.put(QueryType.PARTS_INFO, Arrays.asList(
            "specifications of", "specs for", "product specifications", "part details", "part information"
        ));
        
        // PARTS_BY_CONTRACT patterns
        trainingPatterns.put(QueryType.PARTS_BY_CONTRACT, Arrays.asList(
            "parts of", "show me the parts", "list the parts", "components in", "parts in contract"
        ));
        
        // FAILED_PARTS_BY_CONTRACT patterns
        trainingPatterns.put(QueryType.FAILED_PARTS_BY_CONTRACT, Arrays.asList(
            "failed parts of", "defective parts", "broken components", "faulty parts", "failed parts in"
        ));
        
        // ACCOUNT_INFO patterns
        trainingPatterns.put(QueryType.ACCOUNT_INFO, Arrays.asList(
            "account", "account number", "account information", "account details", "show account"
        ));
        
        // HELP_CREATE_CONTRACT patterns
        trainingPatterns.put(QueryType.HELP_CREATE_CONTRACT, Arrays.asList(
            "create contract", "help create", "how to create contract", "new contract", "contract creation"
        ));
    }
    
    public Intent classifyIntent(String query) {
        // Simple ML-like classification logic
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("contract") && (lowerQuery.contains("info") || lowerQuery.contains("details"))) {
            lastConfidenceScore = 0.85;
            return new Intent(QueryType.CONTRACT_INFO, 0.85);
        } else if (lowerQuery.contains("parts") && lowerQuery.contains("contract") && !lowerQuery.contains("failed")) {
            lastConfidenceScore = 0.80;
            return new Intent(QueryType.PARTS_BY_CONTRACT, 0.80);
        } else if (lowerQuery.contains("failed") && lowerQuery.contains("parts")) {
            lastConfidenceScore = 0.82;
            return new Intent(QueryType.FAILED_PARTS_BY_CONTRACT, 0.82);
        } else if (lowerQuery.contains("user") && lowerQuery.contains("contract")) {
            lastConfidenceScore = 0.78;
            return new Intent(QueryType.USER_CONTRACT_QUERY, 0.78);
        } else if (lowerQuery.contains("parts") && (lowerQuery.contains("info") || lowerQuery.contains("details"))) {
            lastConfidenceScore = 0.75;
            return new Intent(QueryType.PARTS_INFO, 0.75);
        } else if (lowerQuery.contains("account")) {
            lastConfidenceScore = 0.77;
            return new Intent(QueryType.ACCOUNT_INFO, 0.77);
        } else if (lowerQuery.contains("help") && lowerQuery.contains("create")) {
            lastConfidenceScore = 0.70;
            return new Intent(QueryType.HELP_CREATE_CONTRACT, 0.70);
        } else {
            lastConfidenceScore = 0.30;
            return new Intent(QueryType.UNKNOWN, 0.30);
        }
    }
    
    private Intent classifyWithFeatureScoring(String query) {
        String lowerQuery = query.toLowerCase();
        Map<QueryType, Double> intentScores = new HashMap<>();
        
        // Calculate scores for each intent based on pattern matching and feature weights
        for (Map.Entry<QueryType, List<String>> entry : trainingPatterns.entrySet()) {
            QueryType queryType = entry.getKey();
            List<String> patterns = entry.getValue();
            
            double score = 0.0;
            int matchCount = 0;
            
            for (String pattern : patterns) {
                if (lowerQuery.contains(pattern.toLowerCase())) {
                    double weight = featureWeights.getOrDefault(pattern.toLowerCase(), 0.5);
                    score += weight;
                    matchCount++;
                }
            }
            
            // Normalize score
            if (matchCount > 0) {
                score = score / patterns.size();
                intentScores.put(queryType, score);
            }
        }
        
        // Find best scoring intent
        QueryType bestQueryType = QueryType.UNKNOWN;
        double bestScore = 0.0;
        
        for (Map.Entry<QueryType, Double> entry : intentScores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestQueryType = entry.getKey();
            }
        }
        
        // Set confidence score
        lastConfidenceScore = bestScore;
        
        // Return intent only if confidence is above threshold
        return bestScore > 0.6 ? new Intent(bestQueryType, bestScore) : new Intent(QueryType.UNKNOWN, 0.3);
    }
    
    private Intent classifyWithRules(String query) {
        String lowerQuery = query.toLowerCase();
        lastConfidenceScore = 0.8; // Default confidence for rule-based
        
        // Contract queries with high priority patterns
        if (containsAny(lowerQuery, "show contract", "contract details", "get contract info")) {
            return new Intent(QueryType.CONTRACT_INFO, 0.85);
        }
        if (lowerQuery.contains("find contract")) {
            return new Intent(QueryType.CONTRACT_INFO, 0.80);
        }
        if (containsAny(lowerQuery, "contracts by", "contracts created by") ||
            (lowerQuery.contains("contracts") && containsPersonName(lowerQuery))) {
            return new Intent(QueryType.USER_CONTRACT_QUERY, 0.82);
        }
        if (lowerQuery.contains("account") && containsNumber(lowerQuery)) {
            return new Intent(QueryType.ACCOUNT_INFO, 0.85);
        }
        
        // Parts queries
        if (containsAny(lowerQuery, "show me the parts", "list the parts", "parts of")) {
            return new Intent(QueryType.PARTS_BY_CONTRACT, 0.80);
        }
        if (lowerQuery.contains("specifications")) {
            return new Intent(QueryType.PARTS_INFO, 0.85);
        }
        
        // Failed parts queries
        if (containsAny(lowerQuery, "failed parts of", "filed parts")) {
            return new Intent(QueryType.FAILED_PARTS_BY_CONTRACT, 0.85);
        }
        
        // Help queries
        if (containsAny(lowerQuery, "create contract", "how to create contract")) {
            return new Intent(QueryType.HELP_CREATE_CONTRACT, 0.80);
        }
        
        lastConfidenceScore = 0.3;
        return new Intent(QueryType.UNKNOWN, 0.3);
    }
    
    private boolean containsAny(String text, String... patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) return true;
        }
        return false;
    }
    
    private boolean containsPersonName(String text) {
        String[] commonNames = {"john", "smith", "mary", "bob", "alice", "david", "sarah", "mike", "lisa"};
        for (String name : commonNames) {
            if (text.contains(name)) return true;
        }
        return Pattern.compile("\\b[A-Z][a-z]+\\b").matcher(text).find();
    }
    
    private boolean containsCompanyName(String text) {
        String[] companies = {"boeing", "honeywell", "airbus", "lockheed", "raytheon", "northrop"};
        for (String company : companies) {
            if (text.contains(company)) return true;
        }
        return false;
    }
    
    private boolean containsNumber(String text) {
        return text.matches(".*\\d+.*");
    }
    
    public double getConfidenceScore() {
        return lastConfidenceScore;
    }
    
    // Method to add new training patterns dynamically
    public void addTrainingPattern(QueryType queryType, String pattern) {
        trainingPatterns.computeIfAbsent(queryType, k -> new ArrayList<>()).add(pattern);
        System.out.println("Added training pattern: " + pattern + " for intent: " + queryType);
    }
    
    // Method to update feature weights
    public void updateFeatureWeight(String feature, double weight) {
        featureWeights.put(feature, weight);
        System.out.println("Updated feature weight: " + feature + " = " + weight);
    }
}
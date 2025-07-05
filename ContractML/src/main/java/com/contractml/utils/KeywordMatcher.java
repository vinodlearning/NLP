package com.contractml.utils;

import java.util.*;

/**
 * Keyword Matcher - Efficient keyword matching and scoring
 * 
 * Features:
 * - Fast keyword matching using HashSet lookups
 * - Scoring system for intent classification
 * - Support for multiple keyword categories
 * - Performance optimized for real-time processing
 */
public class KeywordMatcher {
    
    private final Set<String> contractKeywords;
    private final Set<String> partsKeywords;
    private final Set<String> createKeywords;
    private final Set<String> queryKeywords;
    
    /**
     * Constructor - Initialize keyword sets
     */
    public KeywordMatcher() {
        this.contractKeywords = new HashSet<>();
        this.partsKeywords = new HashSet<>();
        this.createKeywords = new HashSet<>();
        this.queryKeywords = new HashSet<>();
        
        initializeKeywords();
    }
    
    /**
     * Initialize all keyword sets
     */
    private void initializeKeywords() {
        // Contract keywords
        contractKeywords.addAll(Arrays.asList(
            "contract", "agreement", "deal", "effective", "expiration", 
            "price", "customer", "terms", "status", "created", "signed", 
            "renewed", "billing", "payment", "invoice", "renewal", 
            "amendment", "clause", "provision", "liability", "warranty"
        ));
        
        // Parts keywords
        partsKeywords.addAll(Arrays.asList(
            "part", "parts", "component", "item", "inventory", "stock", 
            "quantity", "available", "specification", "model", "serial", 
            "catalog", "sku", "manufacturer", "vendor", "supplier", 
            "assembly", "subassembly", "material", "product"
        ));
        
        // Create keywords
        createKeywords.addAll(Arrays.asList(
            "create", "add", "new", "insert", "generate", "make", 
            "build", "establish", "setup", "initialize", "start", 
            "begin", "initiate", "form", "construct"
        ));
        
        // Query keywords
        queryKeywords.addAll(Arrays.asList(
            "show", "display", "list", "get", "find", "search", 
            "retrieve", "fetch", "lookup", "view", "see", "check", 
            "examine", "review", "what", "where", "when", "how", 
            "which", "who", "why"
        ));
    }
    
    /**
     * Check if text contains contract keywords
     */
    public boolean hasContractKeywords(String text) {
        return hasKeywords(text, contractKeywords);
    }
    
    /**
     * Check if text contains parts keywords
     */
    public boolean hasPartsKeywords(String text) {
        return hasKeywords(text, partsKeywords);
    }
    
    /**
     * Check if text contains create keywords
     */
    public boolean hasCreateKeywords(String text) {
        return hasKeywords(text, createKeywords);
    }
    
    /**
     * Check if text contains query keywords
     */
    public boolean hasQueryKeywords(String text) {
        return hasKeywords(text, queryKeywords);
    }
    
    /**
     * Generic keyword matching
     */
    private boolean hasKeywords(String text, Set<String> keywords) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        String normalizedText = text.toLowerCase();
        for (String keyword : keywords) {
            if (normalizedText.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Score text based on contract keywords
     */
    public int scoreContractKeywords(String text) {
        return scoreKeywords(text, contractKeywords);
    }
    
    /**
     * Score text based on parts keywords
     */
    public int scorePartsKeywords(String text) {
        return scoreKeywords(text, partsKeywords);
    }
    
    /**
     * Score text based on create keywords
     */
    public int scoreCreateKeywords(String text) {
        return scoreKeywords(text, createKeywords);
    }
    
    /**
     * Score text based on query keywords
     */
    public int scoreQueryKeywords(String text) {
        return scoreKeywords(text, queryKeywords);
    }
    
    /**
     * Generic keyword scoring
     */
    private int scoreKeywords(String text, Set<String> keywords) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        String normalizedText = text.toLowerCase();
        int score = 0;
        
        for (String keyword : keywords) {
            if (normalizedText.contains(keyword)) {
                score++;
            }
        }
        
        return score;
    }
    
    /**
     * Get all matching keywords from text
     */
    public Set<String> getMatchingKeywords(String text, String category) {
        Set<String> matches = new HashSet<>();
        Set<String> keywordSet = getKeywordSet(category);
        
        if (text == null || keywordSet == null) {
            return matches;
        }
        
        String normalizedText = text.toLowerCase();
        for (String keyword : keywordSet) {
            if (normalizedText.contains(keyword)) {
                matches.add(keyword);
            }
        }
        
        return matches;
    }
    
    /**
     * Get keyword set by category
     */
    private Set<String> getKeywordSet(String category) {
        switch (category.toLowerCase()) {
            case "contract":
            case "contracts":
                return contractKeywords;
            case "part":
            case "parts":
                return partsKeywords;
            case "create":
                return createKeywords;
            case "query":
                return queryKeywords;
            default:
                return null;
        }
    }
    
    /**
     * Comprehensive keyword analysis
     */
    public KeywordAnalysis analyzeKeywords(String text) {
        KeywordAnalysis analysis = new KeywordAnalysis();
        
        analysis.contractScore = scoreContractKeywords(text);
        analysis.partsScore = scorePartsKeywords(text);
        analysis.createScore = scoreCreateKeywords(text);
        analysis.queryScore = scoreQueryKeywords(text);
        
        analysis.contractKeywords = getMatchingKeywords(text, "contract");
        analysis.partsKeywords = getMatchingKeywords(text, "parts");
        analysis.createKeywords = getMatchingKeywords(text, "create");
        analysis.queryKeywords = getMatchingKeywords(text, "query");
        
        analysis.dominantCategory = getDominantCategory(analysis);
        
        return analysis;
    }
    
    /**
     * Determine dominant category based on scores
     */
    private String getDominantCategory(KeywordAnalysis analysis) {
        int maxScore = Math.max(
            Math.max(analysis.contractScore, analysis.partsScore),
            Math.max(analysis.createScore, analysis.queryScore)
        );
        
        if (maxScore == 0) {
            return "UNKNOWN";
        }
        
        if (analysis.contractScore == maxScore) {
            return "CONTRACT";
        } else if (analysis.partsScore == maxScore) {
            return "PARTS";
        } else if (analysis.createScore == maxScore) {
            return "CREATE";
        } else {
            return "QUERY";
        }
    }
    
    /**
     * Add custom keyword to category
     */
    public void addKeyword(String keyword, String category) {
        Set<String> keywordSet = getKeywordSet(category);
        if (keywordSet != null) {
            keywordSet.add(keyword.toLowerCase());
        }
    }
    
    /**
     * Remove keyword from category
     */
    public void removeKeyword(String keyword, String category) {
        Set<String> keywordSet = getKeywordSet(category);
        if (keywordSet != null) {
            keywordSet.remove(keyword.toLowerCase());
        }
    }
    
    /**
     * Get all keywords in a category
     */
    public Set<String> getKeywords(String category) {
        Set<String> keywordSet = getKeywordSet(category);
        return keywordSet != null ? new HashSet<>(keywordSet) : new HashSet<>();
    }
    
    /**
     * Get keyword statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("contractKeywords", contractKeywords.size());
        stats.put("partsKeywords", partsKeywords.size());
        stats.put("createKeywords", createKeywords.size());
        stats.put("queryKeywords", queryKeywords.size());
        stats.put("totalKeywords", contractKeywords.size() + partsKeywords.size() + 
                                   createKeywords.size() + queryKeywords.size());
        return stats;
    }
    
    /**
     * Keyword Analysis Result Class
     */
    public static class KeywordAnalysis {
        public int contractScore;
        public int partsScore;
        public int createScore;
        public int queryScore;
        
        public Set<String> contractKeywords;
        public Set<String> partsKeywords;
        public Set<String> createKeywords;
        public Set<String> queryKeywords;
        
        public String dominantCategory;
        
        @Override
        public String toString() {
            return String.format(
                "KeywordAnalysis{contract=%d, parts=%d, create=%d, query=%d, dominant=%s}",
                contractScore, partsScore, createScore, queryScore, dominantCategory
            );
        }
    }
    
    /**
     * Test method for keyword matching
     */
    public static void main(String[] args) {
        KeywordMatcher matcher = new KeywordMatcher();
        
        // Test cases
        String[] testCases = {
            "show contract 12345",
            "list all parts for customer john",
            "create new agreement for account 5678",
            "what is the effective date for contract ABC-789",
            "display available parts inventory",
            "add new component to stock"
        };
        
        System.out.println("Keyword Matching Test:");
        System.out.println("=====================");
        
        for (String test : testCases) {
            KeywordAnalysis analysis = matcher.analyzeKeywords(test);
            System.out.println("Query: " + test);
            System.out.println("Analysis: " + analysis);
            System.out.println();
        }
        
        System.out.println("Statistics: " + matcher.getStatistics());
    }
}
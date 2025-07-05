package com.adf.nlp.utils;

import com.adf.nlp.config.ConfigurationManager;
import java.util.*;
import java.util.logging.Logger;

/**
 * Input Analyzer Utility using Configuration-Driven Keywords
 * All keywords are loaded from txt files for easy maintenance
 * Optimized for O(w) time complexity where w = word count
 */
public class InputAnalyzer {
    private static final Logger logger = Logger.getLogger(InputAnalyzer.class.getName());
    
    private final ConfigurationManager configManager;
    
    /**
     * Constructor
     */
    public InputAnalyzer(ConfigurationManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Analyze input text for routing keywords
     * O(w) time complexity where w = number of words
     */
    public AnalysisResult analyzeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new AnalysisResult(false, false, false, new ArrayList<>(), new ArrayList<>());
        }
        
        // Convert to lowercase and split into words
        String[] words = input.toLowerCase().split("\\s+");
        
        // Track detected keywords
        List<String> detectedPartsKeywords = new ArrayList<>();
        List<String> detectedCreateKeywords = new ArrayList<>();
        List<String> detectedContractKeywords = new ArrayList<>();
        
        // Analyze each word - O(w) complexity
        for (String word : words) {
            // Remove punctuation for keyword matching
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            
            // Check against each keyword category
            if (configManager.isPartsKeyword(cleanWord)) {
                detectedPartsKeywords.add(cleanWord);
            }
            
            if (configManager.isCreateKeyword(cleanWord)) {
                detectedCreateKeywords.add(cleanWord);
            }
            
            if (configManager.isContractKeyword(cleanWord)) {
                detectedContractKeywords.add(cleanWord);
            }
        }
        
        // Determine boolean flags
        boolean hasPartsKeywords = !detectedPartsKeywords.isEmpty();
        boolean hasCreateKeywords = !detectedCreateKeywords.isEmpty();
        boolean hasContractKeywords = !detectedContractKeywords.isEmpty();
        
        // Combine all detected keywords
        List<String> allDetectedKeywords = new ArrayList<>();
        allDetectedKeywords.addAll(detectedPartsKeywords);
        allDetectedKeywords.addAll(detectedCreateKeywords);
        allDetectedKeywords.addAll(detectedContractKeywords);
        
        return new AnalysisResult(hasPartsKeywords, hasCreateKeywords, hasContractKeywords, 
                                 allDetectedKeywords, extractIdentifiers(input));
    }
    
    /**
     * Extract potential identifiers (contract numbers, part numbers, etc.)
     */
    private List<String> extractIdentifiers(String input) {
        List<String> identifiers = new ArrayList<>();
        
        // Pattern for contract/part numbers (alphanumeric with possible hyphens)
        String[] words = input.split("\\s+");
        
        for (String word : words) {
            // Remove punctuation except hyphens and underscores
            String cleanWord = word.replaceAll("[^a-zA-Z0-9\\-_]", "");
            
            // Check if it looks like an identifier
            if (isLikelyIdentifier(cleanWord)) {
                identifiers.add(cleanWord);
            }
        }
        
        return identifiers;
    }
    
    /**
     * Check if string looks like a contract/part identifier
     */
    private boolean isLikelyIdentifier(String word) {
        if (word.length() < 3) {
            return false;
        }
        
        // Must contain at least one digit
        if (!word.matches(".*\\d.*")) {
            return false;
        }
        
        // Should be alphanumeric with possible hyphens/underscores
        return word.matches("[a-zA-Z0-9\\-_]+");
    }
    
    /**
     * Get keyword analysis for debugging
     */
    public String getKeywordAnalysis(String input) {
        AnalysisResult result = analyzeInput(input);
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("Keyword Analysis for: '").append(input).append("'\n");
        analysis.append("Has Parts Keywords: ").append(result.hasPartsKeywords()).append("\n");
        analysis.append("Has Create Keywords: ").append(result.hasCreateKeywords()).append("\n");
        analysis.append("Has Contract Keywords: ").append(result.hasContractKeywords()).append("\n");
        analysis.append("Detected Keywords: ").append(result.getDetectedKeywords()).append("\n");
        analysis.append("Extracted Identifiers: ").append(result.getExtractedIdentifiers()).append("\n");
        
        return analysis.toString();
    }
    
    /**
     * Analysis Result class
     */
    public static class AnalysisResult {
        private final boolean hasPartsKeywords;
        private final boolean hasCreateKeywords;
        private final boolean hasContractKeywords;
        private final List<String> detectedKeywords;
        private final List<String> extractedIdentifiers;
        
        public AnalysisResult(boolean hasPartsKeywords, boolean hasCreateKeywords, 
                             boolean hasContractKeywords, List<String> detectedKeywords, 
                             List<String> extractedIdentifiers) {
            this.hasPartsKeywords = hasPartsKeywords;
            this.hasCreateKeywords = hasCreateKeywords;
            this.hasContractKeywords = hasContractKeywords;
            this.detectedKeywords = new ArrayList<>(detectedKeywords);
            this.extractedIdentifiers = new ArrayList<>(extractedIdentifiers);
        }
        
        public boolean hasPartsKeywords() { return hasPartsKeywords; }
        public boolean hasCreateKeywords() { return hasCreateKeywords; }
        public boolean hasContractKeywords() { return hasContractKeywords; }
        public List<String> getDetectedKeywords() { return new ArrayList<>(detectedKeywords); }
        public List<String> getExtractedIdentifiers() { return new ArrayList<>(extractedIdentifiers); }
        
        /**
         * Get the primary identifier (first one found)
         */
        public String getPrimaryIdentifier() {
            return extractedIdentifiers.isEmpty() ? null : extractedIdentifiers.get(0);
        }
        
        /**
         * Check if any identifiers were found
         */
        public boolean hasIdentifiers() {
            return !extractedIdentifiers.isEmpty();
        }
        
        /**
         * Get routing priority score for debugging
         */
        public int getRoutingPriorityScore() {
            int score = 0;
            if (hasPartsKeywords && hasCreateKeywords) score += 1000; // Highest priority
            else if (hasPartsKeywords) score += 100;
            else if (hasCreateKeywords) score += 10;
            else score += 1; // Default contract routing
            
            return score;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("AnalysisResult{");
            sb.append("partsKeywords=").append(hasPartsKeywords);
            sb.append(", createKeywords=").append(hasCreateKeywords);
            sb.append(", contractKeywords=").append(hasContractKeywords);
            sb.append(", detectedKeywords=").append(detectedKeywords);
            sb.append(", extractedIdentifiers=").append(extractedIdentifiers);
            sb.append(", routingPriority=").append(getRoutingPriorityScore());
            sb.append('}');
            return sb.toString();
        }
    }
}
package com.adf.nlp.utils;

import com.adf.nlp.config.ConfigurationManager;
import java.util.*;
import java.util.logging.Logger;

/**
 * Spell Checker Utility using Configuration-Driven Corrections
 * All spell corrections are loaded from txt files for easy maintenance
 * Optimized for O(w) time complexity where w = word count
 */
public class SpellChecker {
    private static final Logger logger = Logger.getLogger(SpellChecker.class.getName());
    
    private final ConfigurationManager configManager;
    
    /**
     * Constructor
     */
    public SpellChecker(ConfigurationManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Apply spell correction to input text
     * O(w) time complexity where w = number of words
     */
    public String correctSpelling(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // Split into words while preserving delimiters
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        boolean correctionApplied = false;
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String cleanWord = extractCleanWord(word);
            String correctedWord = getCorrectedWord(cleanWord);
            
            if (!cleanWord.equals(correctedWord)) {
                // Replace the clean word part with corrected word
                String finalWord = word.replace(cleanWord, correctedWord);
                corrected.append(finalWord);
                correctionApplied = true;
            } else {
                corrected.append(word);
            }
            
            // Add space except for last word
            if (i < words.length - 1) {
                corrected.append(" ");
            }
        }
        
        String result = corrected.toString();
        
        if (correctionApplied) {
            logger.info("Spell correction applied: '" + input + "' -> '" + result + "'");
        }
        
        return result;
    }
    
    /**
     * Extract clean word from word with punctuation
     */
    private String extractCleanWord(String word) {
        // Remove punctuation from start and end
        return word.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
    }
    
    /**
     * Get corrected word from configuration
     */
    private String getCorrectedWord(String word) {
        if (word.isEmpty()) {
            return word;
        }
        
        String correction = configManager.getSpellCorrection(word);
        return correction != null ? correction : word;
    }
    
    /**
     * Check if word needs correction
     */
    public boolean needsCorrection(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }
        
        String cleanWord = extractCleanWord(word);
        return configManager.getSpellCorrection(cleanWord) != null;
    }
    
    /**
     * Get all available corrections
     */
    public Map<String, String> getAllCorrections() {
        return configManager.getAllSpellCorrections();
    }
    
    /**
     * Get correction for specific word
     */
    public String getCorrection(String word) {
        if (word == null || word.trim().isEmpty()) {
            return word;
        }
        
        String cleanWord = extractCleanWord(word);
        return getCorrectedWord(cleanWord);
    }
    
    /**
     * Test spell correction with sample text
     */
    public SpellCheckResult testCorrection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new SpellCheckResult(input, input, false, new ArrayList<>());
        }
        
        String corrected = correctSpelling(input);
        boolean correctionApplied = !input.equals(corrected);
        
        // Find corrected words
        List<String> correctedWords = new ArrayList<>();
        String[] originalWords = input.split("\\s+");
        String[] correctedWordsArray = corrected.split("\\s+");
        
        for (int i = 0; i < originalWords.length && i < correctedWordsArray.length; i++) {
            String originalClean = extractCleanWord(originalWords[i]);
            String correctedClean = extractCleanWord(correctedWordsArray[i]);
            
            if (!originalClean.equals(correctedClean)) {
                correctedWords.add(originalClean + " -> " + correctedClean);
            }
        }
        
        return new SpellCheckResult(input, corrected, correctionApplied, correctedWords);
    }
    
    /**
     * Spell check result class
     */
    public static class SpellCheckResult {
        private final String original;
        private final String corrected;
        private final boolean correctionApplied;
        private final List<String> correctedWords;
        
        public SpellCheckResult(String original, String corrected, boolean correctionApplied, List<String> correctedWords) {
            this.original = original;
            this.corrected = corrected;
            this.correctionApplied = correctionApplied;
            this.correctedWords = correctedWords;
        }
        
        public String getOriginal() { return original; }
        public String getCorrected() { return corrected; }
        public boolean isCorrectionApplied() { return correctionApplied; }
        public List<String> getCorrectedWords() { return correctedWords; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Original: ").append(original).append("\n");
            sb.append("Corrected: ").append(corrected).append("\n");
            sb.append("Correction Applied: ").append(correctionApplied).append("\n");
            if (!correctedWords.isEmpty()) {
                sb.append("Corrected Words: ").append(String.join(", ", correctedWords));
            }
            return sb.toString();
        }
    }
}
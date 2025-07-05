package com.contractml.utils;

import java.util.*;
import java.io.*;

/**
 * Spell Corrector - Handles domain-specific spell corrections
 * 
 * Features:
 * - Predefined correction mappings for contract/parts domain
 * - Configurable corrections via external file
 * - Performance optimized with HashMap lookups
 * - Statistics tracking
 */
public class SpellCorrector {
    
    private final Map<String, String> corrections;
    private int correctionCount = 0;
    
    /**
     * Constructor - Initialize with predefined corrections
     */
    public SpellCorrector() {
        this.corrections = new HashMap<>();
        loadPredefinedCorrections();
        loadExternalCorrections();
    }
    
    /**
     * Load predefined domain-specific corrections
     */
    private void loadPredefinedCorrections() {
        // Contract-related corrections
        corrections.put("contrct", "contract");
        corrections.put("contrat", "contract");
        corrections.put("contarct", "contract");
        corrections.put("agrement", "agreement");
        corrections.put("agreemnt", "agreement");
        corrections.put("effectiv", "effective");
        corrections.put("effctive", "effective");
        corrections.put("expirtion", "expiration");
        corrections.put("expiratoin", "expiration");
        corrections.put("custmer", "customer");
        corrections.put("customr", "customer");
        corrections.put("cusotmer", "customer");
        
        // Parts-related corrections
        corrections.put("part", "part");
        corrections.put("prt", "part");
        corrections.put("prts", "parts");
        corrections.put("parst", "parts");
        corrections.put("partz", "parts");
        corrections.put("componnt", "component");
        corrections.put("compnent", "component");
        corrections.put("quantiy", "quantity");
        corrections.put("quntity", "quantity");
        corrections.put("availble", "available");
        corrections.put("availabe", "available");
        corrections.put("specfication", "specification");
        corrections.put("specificaton", "specification");
        
        // Common typos
        corrections.put("shw", "show");
        corrections.put("sho", "show");
        corrections.put("dispaly", "display");
        corrections.put("disply", "display");
        corrections.put("lst", "list");
        corrections.put("lsit", "list");
        corrections.put("cretae", "create");
        corrections.put("creat", "create");
        corrections.put("acount", "account");
        corrections.put("accont", "account");
        corrections.put("numbr", "number");
        corrections.put("numer", "number");
        corrections.put("staus", "status");
        corrections.put("stauts", "status");
        corrections.put("pric", "price");
        corrections.put("prce", "price");
        corrections.put("dat", "date");
        corrections.put("dte", "date");
        corrections.put("nam", "name");
        corrections.put("nme", "name");
        
        // Number-related corrections
        corrections.put("1245", "12345");  // Common number typo
        corrections.put("5678", "56789");  // Common number typo
        
        // Action-related corrections
        corrections.put("gt", "get");
        corrections.put("gt", "get");
        corrections.put("retreive", "retrieve");
        corrections.put("retrive", "retrieve");
        corrections.put("serch", "search");
        corrections.put("seach", "search");
        corrections.put("fnd", "find");
        corrections.put("lokup", "lookup");
        corrections.put("looku", "lookup");
    }
    
    /**
     * Load external corrections from file (if exists)
     */
    private void loadExternalCorrections() {
        try {
            // Try to load from resources first
            InputStream is = getClass().getResourceAsStream("/spell_corrections.txt");
            if (is != null) {
                loadCorrectionsFromStream(is);
                is.close();
            }
            
            // Try to load from file system
            File file = new File("spell_corrections.txt");
            if (file.exists()) {
                loadCorrectionsFromFile(file);
            }
        } catch (Exception e) {
            // Silently ignore - external corrections are optional
            System.out.println("Note: External spell corrections file not found, using predefined corrections only.");
        }
    }
    
    /**
     * Load corrections from input stream
     */
    private void loadCorrectionsFromStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            String[] parts = line.split("\\s*->\\s*");
            if (parts.length == 2) {
                corrections.put(parts[0].trim(), parts[1].trim());
            }
        }
    }
    
    /**
     * Load corrections from file
     */
    private void loadCorrectionsFromFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            loadCorrectionsFromStream(fis);
        }
    }
    
    /**
     * Correct spelling in the given text
     */
    public String correctSpelling(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder corrected = new StringBuilder();
        boolean hasCorrected = false;
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String cleanWord = cleanWord(word);
            
            if (corrections.containsKey(cleanWord)) {
                String correction = corrections.get(cleanWord);
                corrected.append(correction);
                hasCorrected = true;
            } else {
                corrected.append(word);
            }
            
            if (i < words.length - 1) {
                corrected.append(" ");
            }
        }
        
        if (hasCorrected) {
            correctionCount++;
        }
        
        return corrected.toString();
    }
    
    /**
     * Clean word - remove punctuation for matching
     */
    private String cleanWord(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "");
    }
    
    /**
     * Check if a word needs correction
     */
    public boolean needsCorrection(String word) {
        if (word == null) return false;
        return corrections.containsKey(cleanWord(word.toLowerCase()));
    }
    
    /**
     * Get correction for a specific word
     */
    public String getCorrection(String word) {
        if (word == null) return word;
        return corrections.getOrDefault(cleanWord(word.toLowerCase()), word);
    }
    
    /**
     * Add a new correction mapping
     */
    public void addCorrection(String incorrect, String correct) {
        corrections.put(incorrect.toLowerCase(), correct.toLowerCase());
    }
    
    /**
     * Get all corrections
     */
    public Map<String, String> getAllCorrections() {
        return new HashMap<>(corrections);
    }
    
    /**
     * Get correction count
     */
    public int getCorrectionCount() {
        return correctionCount;
    }
    
    /**
     * Reset correction count
     */
    public void resetCorrectionCount() {
        correctionCount = 0;
    }
    
    /**
     * Get statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCorrections", corrections.size());
        stats.put("correctionsApplied", correctionCount);
        return stats;
    }
    
    /**
     * Export corrections to file
     */
    public void exportCorrections(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# Spell Corrections - Domain Specific");
            writer.println("# Format: incorrect -> correct");
            writer.println();
            
            List<String> sortedKeys = new ArrayList<>(corrections.keySet());
            Collections.sort(sortedKeys);
            
            for (String key : sortedKeys) {
                writer.println(key + " -> " + corrections.get(key));
            }
        }
    }
    
    /**
     * Test method for spell correction
     */
    public static void main(String[] args) {
        SpellCorrector corrector = new SpellCorrector();
        
        // Test cases
        String[] testCases = {
            "shw contrct 12345",
            "dispaly all partz for custmer john",
            "what is the effctive dat for agrement ABC-789",
            "lst all availble componnt",
            "cretae new contrct for acount 5678"
        };
        
        System.out.println("Spell Correction Test:");
        System.out.println("=====================");
        
        for (String test : testCases) {
            String corrected = corrector.correctSpelling(test);
            System.out.println("Original:  " + test);
            System.out.println("Corrected: " + corrected);
            System.out.println();
        }
        
        System.out.println("Statistics: " + corrector.getStatistics());
    }
}
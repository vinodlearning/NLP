package view.practice;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Configuration Loader for NLP Processing System
 * 
 * Loads configuration from text files for:
 * - Parts keywords
 * - Create keywords  
 * - Spell corrections
 * 
 * Optimized for Time Complexity: O(1) lookups using HashSet/HashMap
 * Space Complexity: O(n) where n is number of configuration items
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ConfigurationLoader {
    
    private static final Logger logger = Logger.getLogger(ConfigurationLoader.class.getName());
    
    // Configuration file paths
    private static final String PARTS_KEYWORDS_FILE = "parts_keywords.txt";
    private static final String CREATE_KEYWORDS_FILE = "create_keywords.txt";
    private static final String SPELL_CORRECTIONS_FILE = "spell_corrections.txt";
    
    // Cached configuration data for O(1) access
    private static Set<String> partsKeywords = null;
    private static Set<String> createKeywords = null;
    private static Map<String, String> spellCorrections = null;
    
    // Singleton pattern for configuration caching
    private static ConfigurationLoader instance = null;
    
    /**
     * Private constructor for singleton pattern
     */
    private ConfigurationLoader() {
        loadAllConfigurations();
    }
    
    /**
     * Get singleton instance with lazy initialization
     * Time Complexity: O(1) after first call
     */
    public static ConfigurationLoader getInstance() {
        if (instance == null) {
            synchronized (ConfigurationLoader.class) {
                if (instance == null) {
                    instance = new ConfigurationLoader();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load all configuration files
     * Time Complexity: O(n) where n is total config items
     * Space Complexity: O(n) for storing configurations
     */
    private void loadAllConfigurations() {
        try {
            partsKeywords = loadKeywordsFromFile(PARTS_KEYWORDS_FILE);
            createKeywords = loadKeywordsFromFile(CREATE_KEYWORDS_FILE);
            spellCorrections = loadSpellCorrectionsFromFile(SPELL_CORRECTIONS_FILE);
            
            logger.info("Configuration loaded successfully - Parts: " + partsKeywords.size() + 
                       ", Create: " + createKeywords.size() + 
                       ", Spell: " + spellCorrections.size());
                       
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load configurations", e);
            // Initialize with empty collections to prevent null pointer exceptions
            partsKeywords = new HashSet<>();
            createKeywords = new HashSet<>();
            spellCorrections = new HashMap<>();
        }
    }
    
    /**
     * Load keywords from file into HashSet for O(1) lookup
     * Time Complexity: O(n) where n is number of lines
     * Space Complexity: O(n) for HashSet storage
     */
    private Set<String> loadKeywordsFromFile(String filename) {
        Set<String> keywords = new HashSet<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(filename)))) {
            
            if (reader == null) {
                // Try loading from current directory
                try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
                    return loadKeywordsFromReader(fileReader);
                }
            } else {
                return loadKeywordsFromReader(reader);
            }
            
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load keywords from " + filename, e);
        }
        
        return keywords;
    }
    
    /**
     * Load keywords from BufferedReader
     */
    private Set<String> loadKeywordsFromReader(BufferedReader reader) throws IOException {
        Set<String> keywords = new HashSet<>();
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim().toLowerCase();
            if (!line.isEmpty() && !line.startsWith("#")) {
                keywords.add(line);
            }
        }
        
        return keywords;
    }
    
    /**
     * Load spell corrections from file into HashMap for O(1) lookup
     * Time Complexity: O(n) where n is number of lines
     * Space Complexity: O(n) for HashMap storage
     */
    private Map<String, String> loadSpellCorrectionsFromFile(String filename) {
        Map<String, String> corrections = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(filename)))) {
            
            if (reader == null) {
                // Try loading from current directory
                try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
                    return loadCorrectionsFromReader(fileReader);
                }
            } else {
                return loadCorrectionsFromReader(reader);
            }
            
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load spell corrections from " + filename, e);
        }
        
        return corrections;
    }
    
    /**
     * Load corrections from BufferedReader
     */
    private Map<String, String> loadCorrectionsFromReader(BufferedReader reader) throws IOException {
        Map<String, String> corrections = new HashMap<>();
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    corrections.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
                }
            }
        }
        
        return corrections;
    }
    
    /**
     * Check if input contains parts keywords
     * Time Complexity: O(w) where w is number of words in input
     * Space Complexity: O(1) additional space
     */
    public boolean containsPartsKeywords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String[] words = input.toLowerCase().split("\\s+");
        
        // O(w) iteration where w is number of words
        // Each contains() call is O(1) due to HashSet
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (partsKeywords.contains(cleanWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if input contains create keywords
     * Time Complexity: O(w) where w is number of words in input
     * Space Complexity: O(1) additional space
     */
    public boolean containsCreateKeywords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String[] words = input.toLowerCase().split("\\s+");
        
        // O(w) iteration where w is number of words
        // Each contains() call is O(1) due to HashSet
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (createKeywords.contains(cleanWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Perform spell correction on input
     * Time Complexity: O(w) where w is number of words in input
     * Space Complexity: O(w) for StringBuilder
     */
    public String performSpellCorrection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        // O(w) iteration where w is number of words
        // Each get() call is O(1) due to HashMap
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            
            if (spellCorrections.containsKey(cleanWord)) {
                String replacement = spellCorrections.get(cleanWord);
                // Preserve original casing
                if (word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
                    replacement = Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
                }
                corrected.append(replacement).append(" ");
            } else {
                corrected.append(word).append(" ");
            }
        }
        
        return corrected.toString().trim();
    }
    
    /**
     * Get all parts keywords (for debugging/monitoring)
     * Time Complexity: O(1)
     */
    public Set<String> getPartsKeywords() {
        return new HashSet<>(partsKeywords); // Return copy for immutability
    }
    
    /**
     * Get all create keywords (for debugging/monitoring)
     * Time Complexity: O(1)
     */
    public Set<String> getCreateKeywords() {
        return new HashSet<>(createKeywords); // Return copy for immutability
    }
    
    /**
     * Get configuration statistics
     * Time Complexity: O(1)
     */
    public Map<String, Integer> getConfigurationStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("partsKeywords", partsKeywords.size());
        stats.put("createKeywords", createKeywords.size());
        stats.put("spellCorrections", spellCorrections.size());
        return stats;
    }
    
    /**
     * Reload configurations from files
     * Time Complexity: O(n) where n is total config items
     */
    public void reloadConfigurations() {
        logger.info("Reloading configurations...");
        loadAllConfigurations();
    }
    
    /**
     * Add new parts keyword at runtime
     * Time Complexity: O(1)
     */
    public void addPartsKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            partsKeywords.add(keyword.toLowerCase().trim());
        }
    }
    
    /**
     * Add new create keyword at runtime
     * Time Complexity: O(1)
     */
    public void addCreateKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            createKeywords.add(keyword.toLowerCase().trim());
        }
    }
    
    /**
     * Add new spell correction at runtime
     * Time Complexity: O(1)
     */
    public void addSpellCorrection(String incorrect, String correct) {
        if (incorrect != null && correct != null && 
            !incorrect.trim().isEmpty() && !correct.trim().isEmpty()) {
            spellCorrections.put(incorrect.toLowerCase().trim(), correct.toLowerCase().trim());
        }
    }
}
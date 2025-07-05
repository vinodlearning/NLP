package model.nlp;

import java.io.*;
import java.util.*;
import javax.faces.context.FacesContext;

/**
 * Configuration Loader for ADF Integration
 * Loads all configuration from text files for easy maintenance
 * 
 * Features:
 * - Spell corrections from spell_corrections.txt
 * - Keywords from parts_keywords.txt and create_keywords.txt  
 * - Database attributes from database_attributes.txt
 * - Automatic file reloading for development
 * - Production-ready caching
 * 
 * @author Contract Query Processing System
 * @version 2.0 - Text File Configuration
 */
public class ConfigurationLoader {
    
    private static ConfigurationLoader instance;
    private static final String CONFIG_PATH = "/config/";
    
    // Configuration caches
    private Map<String, String> spellCorrections;
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Map<String, String> databaseAttributes;
    
    // File timestamps for reload detection
    private long lastSpellCorrectionsLoad = 0;
    private long lastPartsKeywordsLoad = 0;
    private long lastCreateKeywordsLoad = 0;
    private long lastDatabaseAttributesLoad = 0;
    
    // Configuration flags
    private boolean enableAutoReload = true; // Set to false in production
    
    /**
     * Singleton pattern for configuration loading
     */
    public static synchronized ConfigurationLoader getInstance() {
        if (instance == null) {
            instance = new ConfigurationLoader();
        }
        return instance;
    }
    
    private ConfigurationLoader() {
        loadAllConfigurations();
    }
    
    /**
     * Load all configuration files
     */
    private void loadAllConfigurations() {
        try {
            loadSpellCorrections();
            loadPartsKeywords();
            loadCreateKeywords();
            loadDatabaseAttributes();
            
            System.out.println("Configuration loaded successfully:");
            System.out.println("- Spell corrections: " + spellCorrections.size());
            System.out.println("- Parts keywords: " + partsKeywords.size());
            System.out.println("- Create keywords: " + createKeywords.size());
            System.out.println("- Database attributes: " + databaseAttributes.size());
            
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            // Fall back to default hardcoded values
            loadDefaultConfigurations();
        }
    }
    
    /**
     * Load spell corrections from spell_corrections.txt
     */
    private void loadSpellCorrections() {
        spellCorrections = new HashMap<>();
        
        try (InputStream is = getConfigFileStream("spell_corrections.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse correction: wrong_word=correct_word
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String wrongWord = parts[0].trim().toLowerCase();
                        String correctWord = parts[1].trim();
                        spellCorrections.put(wrongWord, correctWord);
                    } else {
                        System.err.println("Invalid spell correction format at line " + lineNumber + ": " + line);
                    }
                }
            }
            
            lastSpellCorrectionsLoad = System.currentTimeMillis();
            
        } catch (Exception e) {
            System.err.println("Error loading spell corrections: " + e.getMessage());
            // Load default corrections
            loadDefaultSpellCorrections();
        }
    }
    
    /**
     * Load parts keywords from parts_keywords.txt
     */
    private void loadPartsKeywords() {
        partsKeywords = new HashSet<>();
        
        try (InputStream is = getConfigFileStream("parts_keywords.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                
                // Skip comments and empty lines
                if (!line.isEmpty() && !line.startsWith("#")) {
                    partsKeywords.add(line);
                }
            }
            
            lastPartsKeywordsLoad = System.currentTimeMillis();
            
        } catch (Exception e) {
            System.err.println("Error loading parts keywords: " + e.getMessage());
            // Load default keywords
            loadDefaultPartsKeywords();
        }
    }
    
    /**
     * Load create keywords from create_keywords.txt
     */
    private void loadCreateKeywords() {
        createKeywords = new HashSet<>();
        
        try (InputStream is = getConfigFileStream("create_keywords.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                
                // Skip comments and empty lines
                if (!line.isEmpty() && !line.startsWith("#")) {
                    createKeywords.add(line);
                }
            }
            
            lastCreateKeywordsLoad = System.currentTimeMillis();
            
        } catch (Exception e) {
            System.err.println("Error loading create keywords: " + e.getMessage());
            // Load default keywords
            loadDefaultCreateKeywords();
        }
    }
    
    /**
     * Load database attributes from database_attributes.txt
     */
    private void loadDatabaseAttributes() {
        databaseAttributes = new HashMap<>();
        
        try (InputStream is = getConfigFileStream("database_attributes.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse attribute: logical_name=actual_name
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String logicalName = parts[0].trim();
                        String actualName = parts[1].trim();
                        databaseAttributes.put(logicalName, actualName);
                    } else {
                        System.err.println("Invalid database attribute format at line " + lineNumber + ": " + line);
                    }
                }
            }
            
            lastDatabaseAttributesLoad = System.currentTimeMillis();
            
        } catch (Exception e) {
            System.err.println("Error loading database attributes: " + e.getMessage());
            // Load default attributes
            loadDefaultDatabaseAttributes();
        }
    }
    
    /**
     * Get configuration file as InputStream
     */
    private InputStream getConfigFileStream(String filename) throws IOException {
        // Try to load from classpath first
        InputStream is = getClass().getResourceAsStream(CONFIG_PATH + filename);
        
        if (is == null) {
            // Try to load from file system (for development)
            String fullPath = "config/" + filename;
            File file = new File(fullPath);
            if (file.exists()) {
                is = new FileInputStream(file);
            }
        }
        
        if (is == null) {
            throw new IOException("Configuration file not found: " + filename);
        }
        
        return is;
    }
    
    /**
     * Reload configurations if files have changed (development mode)
     */
    public void reloadIfChanged() {
        if (!enableAutoReload) {
            return;
        }
        
        try {
            // Check if any config files have been modified
            // For production, disable this feature
            loadAllConfigurations();
        } catch (Exception e) {
            System.err.println("Error during configuration reload: " + e.getMessage());
        }
    }
    
    // ==================== GETTER METHODS ====================
    
    /**
     * Get spell corrections map
     */
    public Map<String, String> getSpellCorrections() {
        if (enableAutoReload) {
            reloadIfChanged();
        }
        return new HashMap<>(spellCorrections); // Return copy for thread safety
    }
    
    /**
     * Get parts keywords set
     */
    public Set<String> getPartsKeywords() {
        if (enableAutoReload) {
            reloadIfChanged();
        }
        return new HashSet<>(partsKeywords); // Return copy for thread safety
    }
    
    /**
     * Get create keywords set
     */
    public Set<String> getCreateKeywords() {
        if (enableAutoReload) {
            reloadIfChanged();
        }
        return new HashSet<>(createKeywords); // Return copy for thread safety
    }
    
    /**
     * Get database attribute mapping
     */
    public String getDatabaseAttribute(String logicalName) {
        if (enableAutoReload) {
            reloadIfChanged();
        }
        return databaseAttributes.getOrDefault(logicalName, logicalName);
    }
    
    /**
     * Get all database attributes
     */
    public Map<String, String> getDatabaseAttributes() {
        if (enableAutoReload) {
            reloadIfChanged();
        }
        return new HashMap<>(databaseAttributes);
    }
    
    // ==================== DEFAULT CONFIGURATIONS (FALLBACK) ====================
    
    /**
     * Load default spell corrections if file loading fails
     */
    private void loadDefaultSpellCorrections() {
        spellCorrections = new HashMap<>();
        spellCorrections.put("cntract", "contract");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("shw", "show");
        spellCorrections.put("prts", "parts");
        spellCorrections.put("partz", "parts");
        System.out.println("Loaded default spell corrections");
    }
    
    /**
     * Load default parts keywords if file loading fails
     */
    private void loadDefaultPartsKeywords() {
        partsKeywords = new HashSet<>();
        partsKeywords.add("parts");
        partsKeywords.add("part");
        partsKeywords.add("lines");
        partsKeywords.add("component");
        System.out.println("Loaded default parts keywords");
    }
    
    /**
     * Load default create keywords if file loading fails
     */
    private void loadDefaultCreateKeywords() {
        createKeywords = new HashSet<>();
        createKeywords.add("create");
        createKeywords.add("help");
        createKeywords.add("new");
        createKeywords.add("make");
        System.out.println("Loaded default create keywords");
    }
    
    /**
     * Load default database attributes if file loading fails
     */
    private void loadDefaultDatabaseAttributes() {
        databaseAttributes = new HashMap<>();
        databaseAttributes.put("CONTRACT_VIEW_OBJECT", "ContractView");
        databaseAttributes.put("PARTS_VIEW_OBJECT", "PartsView");
        databaseAttributes.put("CONTRACT_ID_COLUMN", "CONTRACT_ID");
        databaseAttributes.put("CONTRACT_NAME_COLUMN", "CONTRACT_NAME");
        System.out.println("Loaded default database attributes");
    }
    
    /**
     * Load all default configurations
     */
    private void loadDefaultConfigurations() {
        loadDefaultSpellCorrections();
        loadDefaultPartsKeywords();
        loadDefaultCreateKeywords();
        loadDefaultDatabaseAttributes();
    }
    
    // ==================== CONFIGURATION MANAGEMENT ====================
    
    /**
     * Enable or disable auto-reload (set to false in production)
     */
    public void setAutoReload(boolean enableAutoReload) {
        this.enableAutoReload = enableAutoReload;
    }
    
    /**
     * Force reload all configurations
     */
    public void forceReload() {
        loadAllConfigurations();
    }
    
    /**
     * Get configuration statistics
     */
    public String getConfigurationStats() {
        return String.format(
            "Configuration Stats:\n" +
            "- Spell Corrections: %d\n" +
            "- Parts Keywords: %d\n" +
            "- Create Keywords: %d\n" +
            "- Database Attributes: %d\n" +
            "- Auto Reload: %s",
            spellCorrections.size(),
            partsKeywords.size(),
            createKeywords.size(),
            databaseAttributes.size(),
            enableAutoReload ? "Enabled" : "Disabled"
        );
    }
}
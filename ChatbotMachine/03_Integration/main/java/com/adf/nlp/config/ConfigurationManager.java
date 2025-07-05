package com.adf.nlp.config;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Centralized Configuration Manager for ADF NLP System
 * Loads all configuration from external txt files for easy maintenance
 * Thread-safe singleton implementation with lazy loading
 */
public class ConfigurationManager {
    private static final Logger logger = Logger.getLogger(ConfigurationManager.class.getName());
    private static volatile ConfigurationManager instance;
    
    // Configuration file paths
    private static final String CONFIG_BASE_PATH = "adfintegration/config/";
    private static final String CONTRACT_COLUMNS_FILE = CONFIG_BASE_PATH + "contract_columns.txt";
    private static final String PARTS_COLUMNS_FILE = CONFIG_BASE_PATH + "parts_columns.txt";
    private static final String ROUTING_KEYWORDS_FILE = CONFIG_BASE_PATH + "routing_keywords.txt";
    private static final String RESPONSE_TEMPLATES_FILE = CONFIG_BASE_PATH + "response_templates.txt";
    private static final String DATABASE_QUERIES_FILE = CONFIG_BASE_PATH + "database_queries.txt";
    private static final String SPELL_CORRECTIONS_FILE = CONFIG_BASE_PATH + "spell_corrections.txt";
    
    // Configuration maps - thread-safe
    private final Map<String, String> contractColumns = new ConcurrentHashMap<>();
    private final Map<String, String> partsColumns = new ConcurrentHashMap<>();
    private final Map<String, String> responseTemplates = new ConcurrentHashMap<>();
    private final Map<String, String> databaseQueries = new ConcurrentHashMap<>();
    private final Map<String, String> spellCorrections = new ConcurrentHashMap<>();
    
    // Keyword sets - thread-safe
    private final Set<String> partsKeywords = ConcurrentHashMap.newKeySet();
    private final Set<String> createKeywords = ConcurrentHashMap.newKeySet();
    private final Set<String> contractKeywords = ConcurrentHashMap.newKeySet();
    
    // Configuration loaded flag
    private volatile boolean configurationLoaded = false;
    
    /**
     * Private constructor for singleton pattern
     */
    private ConfigurationManager() {
        loadConfiguration();
    }
    
    /**
     * Get singleton instance with double-checked locking
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Load all configuration from txt files
     */
    private void loadConfiguration() {
        try {
            loadContractColumns();
            loadPartsColumns();
            loadRoutingKeywords();
            loadResponseTemplates();
            loadDatabaseQueries();
            loadSpellCorrections();
            
            configurationLoaded = true;
            logger.info("Configuration loaded successfully from txt files");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading configuration", e);
            throw new RuntimeException("Failed to load system configuration", e);
        }
    }
    
    /**
     * Load contract column mappings
     */
    private void loadContractColumns() throws IOException {
        loadKeyValueFile(CONTRACT_COLUMNS_FILE, contractColumns, "Contract columns");
    }
    
    /**
     * Load parts column mappings
     */
    private void loadPartsColumns() throws IOException {
        loadKeyValueFile(PARTS_COLUMNS_FILE, partsColumns, "Parts columns");
    }
    
    /**
     * Load routing keywords
     */
    private void loadRoutingKeywords() throws IOException {
        Properties props = loadPropertiesFile(ROUTING_KEYWORDS_FILE);
        
        // Load parts keywords
        String partsKeywordsStr = props.getProperty("parts_keywords", "");
        if (!partsKeywordsStr.trim().isEmpty()) {
            String[] keywords = partsKeywordsStr.split(",");
            for (String keyword : keywords) {
                partsKeywords.add(keyword.trim().toLowerCase());
            }
        }
        
        // Load create keywords
        String createKeywordsStr = props.getProperty("create_keywords", "");
        if (!createKeywordsStr.trim().isEmpty()) {
            String[] keywords = createKeywordsStr.split(",");
            for (String keyword : keywords) {
                createKeywords.add(keyword.trim().toLowerCase());
            }
        }
        
        // Load contract keywords
        String contractKeywordsStr = props.getProperty("contract_keywords", "");
        if (!contractKeywordsStr.trim().isEmpty()) {
            String[] keywords = contractKeywordsStr.split(",");
            for (String keyword : keywords) {
                contractKeywords.add(keyword.trim().toLowerCase());
            }
        }
        
        logger.info("Loaded routing keywords: Parts=" + partsKeywords.size() + 
                   ", Create=" + createKeywords.size() + ", Contract=" + contractKeywords.size());
    }
    
    /**
     * Load response templates
     */
    private void loadResponseTemplates() throws IOException {
        loadKeyValueFile(RESPONSE_TEMPLATES_FILE, responseTemplates, "Response templates");
    }
    
    /**
     * Load database queries
     */
    private void loadDatabaseQueries() throws IOException {
        loadKeyValueFile(DATABASE_QUERIES_FILE, databaseQueries, "Database queries");
    }
    
    /**
     * Load spell corrections
     */
    private void loadSpellCorrections() throws IOException {
        loadKeyValueFile(SPELL_CORRECTIONS_FILE, spellCorrections, "Spell corrections");
    }
    
    /**
     * Generic method to load key-value files
     */
    private void loadKeyValueFile(String filePath, Map<String, String> targetMap, String configType) throws IOException {
        Properties props = loadPropertiesFile(filePath);
        for (String key : props.stringPropertyNames()) {
            targetMap.put(key, props.getProperty(key));
        }
        logger.info("Loaded " + targetMap.size() + " " + configType + " from " + filePath);
    }
    
    /**
     * Load properties file with comment support
     */
    private Properties loadPropertiesFile(String filePath) throws IOException {
        Properties props = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (input != null) {
                props.load(input);
            } else {
                // Try file system path
                try (FileInputStream fileInput = new FileInputStream(filePath)) {
                    props.load(fileInput);
                }
            }
        }
        
        return props;
    }
    
    // Getter methods for configuration access
    
    /**
     * Get contract database column for user term
     */
    public String getContractColumn(String userTerm) {
        return contractColumns.get(userTerm.toLowerCase());
    }
    
    /**
     * Get parts database column for user term
     */
    public String getPartsColumn(String userTerm) {
        return partsColumns.get(userTerm.toLowerCase());
    }
    
    /**
     * Check if term is a parts keyword
     */
    public boolean isPartsKeyword(String term) {
        return partsKeywords.contains(term.toLowerCase());
    }
    
    /**
     * Check if term is a create keyword
     */
    public boolean isCreateKeyword(String term) {
        return createKeywords.contains(term.toLowerCase());
    }
    
    /**
     * Check if term is a contract keyword
     */
    public boolean isContractKeyword(String term) {
        return contractKeywords.contains(term.toLowerCase());
    }
    
    /**
     * Get all parts keywords
     */
    public Set<String> getPartsKeywords() {
        return new HashSet<>(partsKeywords);
    }
    
    /**
     * Get all create keywords
     */
    public Set<String> getCreateKeywords() {
        return new HashSet<>(createKeywords);
    }
    
    /**
     * Get all contract keywords
     */
    public Set<String> getContractKeywords() {
        return new HashSet<>(contractKeywords);
    }
    
    /**
     * Get response template
     */
    public String getResponseTemplate(String templateName) {
        return responseTemplates.get(templateName);
    }
    
    /**
     * Get database query
     */
    public String getDatabaseQuery(String queryName) {
        return databaseQueries.get(queryName);
    }
    
    /**
     * Get spell correction
     */
    public String getSpellCorrection(String misspelledWord) {
        return spellCorrections.get(misspelledWord.toLowerCase());
    }
    
    /**
     * Get all spell corrections
     */
    public Map<String, String> getAllSpellCorrections() {
        return new HashMap<>(spellCorrections);
    }
    
    /**
     * Format response template with parameters
     */
    public String formatResponseTemplate(String templateName, Map<String, Object> parameters) {
        String template = getResponseTemplate(templateName);
        if (template == null) {
            return "Template not found: " + templateName;
        }
        
        String result = template;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
    
    /**
     * Format database query with parameters
     */
    public String formatDatabaseQuery(String queryName, Map<String, Object> parameters) {
        String query = getDatabaseQuery(queryName);
        if (query == null) {
            return null;
        }
        
        String result = query;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
    
    /**
     * Reload configuration from files
     */
    public synchronized void reloadConfiguration() {
        contractColumns.clear();
        partsColumns.clear();
        responseTemplates.clear();
        databaseQueries.clear();
        spellCorrections.clear();
        partsKeywords.clear();
        createKeywords.clear();
        contractKeywords.clear();
        
        configurationLoaded = false;
        loadConfiguration();
        
        logger.info("Configuration reloaded successfully");
    }
    
    /**
     * Check if configuration is loaded
     */
    public boolean isConfigurationLoaded() {
        return configurationLoaded;
    }
    
    /**
     * Get configuration summary
     */
    public String getConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Configuration Summary:\n");
        summary.append("- Contract Columns: ").append(contractColumns.size()).append("\n");
        summary.append("- Parts Columns: ").append(partsColumns.size()).append("\n");
        summary.append("- Response Templates: ").append(responseTemplates.size()).append("\n");
        summary.append("- Database Queries: ").append(databaseQueries.size()).append("\n");
        summary.append("- Spell Corrections: ").append(spellCorrections.size()).append("\n");
        summary.append("- Parts Keywords: ").append(partsKeywords.size()).append("\n");
        summary.append("- Create Keywords: ").append(createKeywords.size()).append("\n");
        summary.append("- Contract Keywords: ").append(contractKeywords.size()).append("\n");
        summary.append("- Configuration Loaded: ").append(configurationLoaded);
        
        return summary.toString();
    }
}
package com.company.contracts.service;

import opennlp.tools.doccat.DoccatModel;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * OpenNLP Model Manager
 * 
 * This class manages the loading, caching, and reloading of Apache OpenNLP
 * .bin model files for the Contract Portal chatbot system.
 * 
 * Features:
 * - Load .bin model files from classpath or file system
 * - Model caching for performance
 * - Hot reloading of models without restart
 * - Model validation and error handling
 * - Integration with Oracle ADF applications
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class OpenNLPModelManager {
    
    private static final Logger logger = Logger.getLogger(OpenNLPModelManager.class.getName());
    
    // Model cache
    private Map<String, DoccatModel> modelCache;
    private Map<String, Long> modelLoadTimes;
    
    // Configuration
    private String modelBasePath;
    private boolean enableModelCaching;
    private long modelCacheTimeout;
    
    // Model file names
    private static final String CONTRACT_MODEL_FILE = "contract_intent_model.bin";
    private static final String PARTS_MODEL_FILE = "parts_intent_model.bin";
    private static final String HELP_MODEL_FILE = "help_intent_model.bin";
    
    /**
     * Constructor
     */
    public OpenNLPModelManager() {
        initializeManager();
    }
    
    /**
     * Constructor with custom model path
     */
    public OpenNLPModelManager(String modelBasePath) {
        this.modelBasePath = modelBasePath;
        initializeManager();
    }
    
    /**
     * Initialize the model manager
     */
    private void initializeManager() {
        // Initialize model cache
        modelCache = new HashMap<>();
        modelLoadTimes = new HashMap<>();
        
        // Set default configuration
        if (modelBasePath == null) {
            modelBasePath = "models/"; // Default path in classpath
        }
        enableModelCaching = true;
        modelCacheTimeout = 3600000; // 1 hour in milliseconds
        
        // Load all models at startup
        loadAllModels();
        
        logger.info("OpenNLPModelManager initialized with model path: " + modelBasePath);
    }
    
    /**
     * Load all models at startup
     */
    private void loadAllModels() {
        try {
            // Load contract model
            loadContractModel();
            
            // Load parts model
            loadPartsModel();
            
            // Load help model
            loadHelpModel();
            
            logger.info("All OpenNLP models loaded successfully");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading OpenNLP models", e);
        }
    }
    
    /**
     * Get contract model
     */
    public DoccatModel getContractModel() {
        return getModel("CONTRACT", CONTRACT_MODEL_FILE);
    }
    
    /**
     * Get parts model
     */
    public DoccatModel getPartsModel() {
        return getModel("PARTS", PARTS_MODEL_FILE);
    }
    
    /**
     * Get help model
     */
    public DoccatModel getHelpModel() {
        return getModel("HELP", HELP_MODEL_FILE);
    }
    
    /**
     * Generic method to get a model with caching
     */
    private DoccatModel getModel(String modelKey, String modelFileName) {
        try {
            // Check if model is cached and not expired
            if (enableModelCaching && modelCache.containsKey(modelKey)) {
                long loadTime = modelLoadTimes.get(modelKey);
                if (System.currentTimeMillis() - loadTime < modelCacheTimeout) {
                    return modelCache.get(modelKey);
                }
            }
            
            // Load model from file
            DoccatModel model = loadModelFromFile(modelFileName);
            
            // Cache the model
            if (enableModelCaching && model != null) {
                modelCache.put(modelKey, model);
                modelLoadTimes.put(modelKey, System.currentTimeMillis());
            }
            
            return model;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading model: " + modelKey, e);
            return null;
        }
    }
    
    /**
     * Load model from .bin file
     */
    private DoccatModel loadModelFromFile(String modelFileName) throws IOException {
        String fullPath = modelBasePath + modelFileName;
        
        try {
            // Try to load from classpath first
            InputStream modelStream = getClass().getClassLoader().getResourceAsStream(fullPath);
            
            if (modelStream == null) {
                // Try to load from file system
                File modelFile = new File(fullPath);
                if (modelFile.exists()) {
                    modelStream = new FileInputStream(modelFile);
                } else {
                    logger.warning("Model file not found: " + fullPath);
                    return null;
                }
            }
            
            try (InputStream is = modelStream) {
                DoccatModel model = new DoccatModel(is);
                logger.info("Successfully loaded model: " + modelFileName);
                return model;
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading model file: " + modelFileName, e);
            throw e;
        }
    }
    
    /**
     * Load contract model specifically
     */
    private void loadContractModel() {
        try {
            DoccatModel model = loadModelFromFile(CONTRACT_MODEL_FILE);
            if (model != null) {
                modelCache.put("CONTRACT", model);
                modelLoadTimes.put("CONTRACT", System.currentTimeMillis());
                logger.info("Contract model loaded successfully");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load contract model", e);
        }
    }
    
    /**
     * Load parts model specifically
     */
    private void loadPartsModel() {
        try {
            DoccatModel model = loadModelFromFile(PARTS_MODEL_FILE);
            if (model != null) {
                modelCache.put("PARTS", model);
                modelLoadTimes.put("PARTS", System.currentTimeMillis());
                logger.info("Parts model loaded successfully");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load parts model", e);
        }
    }
    
    /**
     * Load help model specifically
     */
    private void loadHelpModel() {
        try {
            DoccatModel model = loadModelFromFile(HELP_MODEL_FILE);
            if (model != null) {
                modelCache.put("HELP", model);
                modelLoadTimes.put("HELP", System.currentTimeMillis());
                logger.info("Help model loaded successfully");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load help model", e);
        }
    }
    
    /**
     * Reload all models - useful for hot deployment
     */
    public void reloadModels() {
        logger.info("Reloading all OpenNLP models...");
        
        // Clear cache
        modelCache.clear();
        modelLoadTimes.clear();
        
        // Reload all models
        loadAllModels();
        
        logger.info("All models reloaded successfully");
    }
    
    /**
     * Reload specific model
     */
    public void reloadModel(String modelKey) {
        logger.info("Reloading model: " + modelKey);
        
        // Remove from cache
        modelCache.remove(modelKey);
        modelLoadTimes.remove(modelKey);
        
        // Reload specific model
        switch (modelKey.toUpperCase()) {
            case "CONTRACT":
                loadContractModel();
                break;
            case "PARTS":
                loadPartsModel();
                break;
            case "HELP":
                loadHelpModel();
                break;
            default:
                logger.warning("Unknown model key: " + modelKey);
        }
    }
    
    /**
     * Get model information for debugging/monitoring
     */
    public Map<String, Object> getModelInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("modelBasePath", modelBasePath);
        info.put("enableModelCaching", enableModelCaching);
        info.put("modelCacheTimeout", modelCacheTimeout);
        info.put("loadedModelsCount", modelCache.size());
        
        Map<String, Object> modelStatus = new HashMap<>();
        modelStatus.put("CONTRACT", modelCache.containsKey("CONTRACT"));
        modelStatus.put("PARTS", modelCache.containsKey("PARTS"));
        modelStatus.put("HELP", modelCache.containsKey("HELP"));
        info.put("modelStatus", modelStatus);
        
        Map<String, Long> loadTimes = new HashMap<>();
        for (Map.Entry<String, Long> entry : modelLoadTimes.entrySet()) {
            loadTimes.put(entry.getKey(), System.currentTimeMillis() - entry.getValue());
        }
        info.put("modelAges", loadTimes);
        
        return info;
    }
    
    /**
     * Get count of loaded models
     */
    public int getLoadedModelsCount() {
        return modelCache.size();
    }
    
    /**
     * Check if all required models are loaded
     */
    public boolean areAllModelsLoaded() {
        return modelCache.containsKey("CONTRACT") && 
               modelCache.containsKey("PARTS") && 
               modelCache.containsKey("HELP");
    }
    
    /**
     * Check if a specific model is loaded
     */
    public boolean isModelLoaded(String modelKey) {
        return modelCache.containsKey(modelKey.toUpperCase());
    }
    
    /**
     * Get list of loaded model keys
     */
    public Set<String> getLoadedModelKeys() {
        return new HashSet<>(modelCache.keySet());
    }
    
    /**
     * Validate model files exist
     */
    public Map<String, Boolean> validateModelFiles() {
        Map<String, Boolean> validation = new HashMap<>();
        
        validation.put("CONTRACT", modelFileExists(CONTRACT_MODEL_FILE));
        validation.put("PARTS", modelFileExists(PARTS_MODEL_FILE));
        validation.put("HELP", modelFileExists(HELP_MODEL_FILE));
        
        return validation;
    }
    
    /**
     * Check if model file exists
     */
    private boolean modelFileExists(String modelFileName) {
        String fullPath = modelBasePath + modelFileName;
        
        // Check classpath
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fullPath);
        if (stream != null) {
            try {
                stream.close();
                return true;
            } catch (IOException e) {
                // Ignore
            }
        }
        
        // Check file system
        File file = new File(fullPath);
        return file.exists();
    }
    
    /**
     * Clear model cache
     */
    public void clearCache() {
        modelCache.clear();
        modelLoadTimes.clear();
        logger.info("Model cache cleared");
    }
    
    /**
     * Set model cache timeout
     */
    public void setModelCacheTimeout(long timeoutMs) {
        this.modelCacheTimeout = timeoutMs;
        logger.info("Model cache timeout set to: " + timeoutMs + "ms");
    }
    
    /**
     * Enable or disable model caching
     */
    public void setModelCaching(boolean enabled) {
        this.enableModelCaching = enabled;
        if (!enabled) {
            clearCache();
        }
        logger.info("Model caching " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Set model base path
     */
    public void setModelBasePath(String basePath) {
        this.modelBasePath = basePath;
        logger.info("Model base path set to: " + basePath);
    }
    
    /**
     * Get memory usage information
     */
    public Map<String, Object> getMemoryInfo() {
        Map<String, Object> memInfo = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        memInfo.put("totalMemory", runtime.totalMemory());
        memInfo.put("freeMemory", runtime.freeMemory());
        memInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        memInfo.put("maxMemory", runtime.maxMemory());
        
        // Estimate model memory usage (rough calculation)
        long estimatedModelMemory = modelCache.size() * 1024 * 1024; // Assume 1MB per model
        memInfo.put("estimatedModelMemory", estimatedModelMemory);
        
        return memInfo;
    }
    
    /**
     * Health check for all models
     */
    public Map<String, Object> performHealthCheck() {
        Map<String, Object> healthCheck = new HashMap<>();
        
        healthCheck.put("timestamp", new Date());
        healthCheck.put("managerStatus", "RUNNING");
        healthCheck.put("modelsLoaded", getLoadedModelsCount());
        healthCheck.put("allModelsLoaded", areAllModelsLoaded());
        healthCheck.put("modelValidation", validateModelFiles());
        healthCheck.put("memoryInfo", getMemoryInfo());
        healthCheck.put("modelInfo", getModelInfo());
        
        return healthCheck;
    }
}
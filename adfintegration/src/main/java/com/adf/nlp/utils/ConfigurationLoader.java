package com.adf.nlp.utils;

import java.util.*;

/**
 * Simple Configuration Loader for NLP processing
 * Loads keywords and configuration from hardcoded values
 */
public class ConfigurationLoader {
    
    private static ConfigurationLoader instance;
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    
    private ConfigurationLoader() {
        loadConfiguration();
    }
    
    public static ConfigurationLoader getInstance() {
        if (instance == null) {
            instance = new ConfigurationLoader();
        }
        return instance;
    }
    
    private void loadConfiguration() {
        // Parts keywords
        partsKeywords = new HashSet<>();
        partsKeywords.add("parts");
        partsKeywords.add("part");
        partsKeywords.add("components");
        partsKeywords.add("component");
        partsKeywords.add("items");
        partsKeywords.add("item");
        partsKeywords.add("inventory");
        partsKeywords.add("stock");
        partsKeywords.add("specifications");
        partsKeywords.add("specs");
        
        // Create keywords
        createKeywords = new HashSet<>();
        createKeywords.add("create");
        createKeywords.add("new");
        createKeywords.add("add");
        createKeywords.add("generate");
        createKeywords.add("make");
        createKeywords.add("build");
        createKeywords.add("setup");
        createKeywords.add("establish");
        createKeywords.add("help");
        createKeywords.add("guide");
        createKeywords.add("steps");
        createKeywords.add("how");
        createKeywords.add("process");
        createKeywords.add("workflow");
    }
    
    public boolean hasPartsKeywords(Set<String> queryWords) {
        for (String word : queryWords) {
            if (partsKeywords.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasCreateKeywords(Set<String> queryWords) {
        for (String word : queryWords) {
            if (createKeywords.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    public Set<String> getPartsKeywords() {
        return new HashSet<>(partsKeywords);
    }
    
    public Set<String> getCreateKeywords() {
        return new HashSet<>(createKeywords);
    }
}
package com.company.contracts.nlp;

import java.util.*;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Enhanced NLP Processor with advanced text processing capabilities
 * Implements tokenization, POS tagging, spell checking, and lemmatization
 * Following clean separation of concerns architecture
 */
public class EnhancedNLPProcessor {
    
    // ====================================
    // CORE COMPONENTS
    // ====================================
    
    private final Tokenizer tokenizer;
    private final POSTagger posTagger;
    private final SpellChecker spellChecker;
    private final Lemmatizer lemmatizer;
    private final ConfigurationManager configManager;
    
    // ====================================
    // CONSTRUCTOR
    // ====================================
    
    public EnhancedNLPProcessor() {
        this.tokenizer = new Tokenizer();
        this.posTagger = new POSTagger();
        this.spellChecker = new SpellChecker();
        this.lemmatizer = new Lemmatizer();
        this.configManager = ConfigurationManager.getInstance();
    }
    
    // ====================================
    // MAIN PROCESSING METHOD
    // ====================================
    
    /**
     * Process user input through complete NLP pipeline
     * @param userInput Raw user input text
     * @return ProcessedQuery object with all NLP analysis
     */
    public ProcessedQuery processQuery(String userInput) {
        ProcessedQuery.Builder builder = new ProcessedQuery.Builder(userInput);
        
        try {
            // 1. Input preprocessing
            String cleanedInput = preprocessInput(userInput);
            builder.setCleanedInput(cleanedInput);
            
            // 2. Tokenization
            List<String> tokens = tokenizer.tokenize(cleanedInput);
            builder.setTokens(tokens);
            
            // 3. Spell correction
            List<String> correctedTokens = spellChecker.correctTokens(tokens);
            builder.setCorrectedTokens(correctedTokens);
            
            // 4. POS tagging
            List<POSTag> posTags = posTagger.tagTokens(correctedTokens);
            builder.setPosTags(posTags);
            
            // 5. Lemmatization
            List<String> lemmatizedTokens = lemmatizer.lemmatizeTokens(correctedTokens, posTags);
            builder.setLemmatizedTokens(lemmatizedTokens);
            
            // 6. Entity extraction
            List<Entity> entities = extractEntities(correctedTokens, posTags);
            builder.setEntities(entities);
            
            // 7. Intent classification
            Intent intent = classifyIntent(lemmatizedTokens, entities);
            builder.setIntent(intent);
            
            // 8. Validation
            ValidationResult validation = validateQuery(builder);
            builder.setValidation(validation);
            
            return builder.build();
            
        } catch (Exception e) {
            return builder.setError("NLP processing failed: " + e.getMessage()).build();
        }
    }
    
    // ====================================
    // INPUT PREPROCESSING
    // ====================================
    
    /**
     * Clean and preprocess input text
     */
    private String preprocessInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        return input.trim()
                   .toLowerCase()
                   .replaceAll("\\s+", " ")  // Normalize whitespace
                   .replaceAll("[^a-zA-Z0-9\\s\\-_]", " ")  // Remove special chars
                   .trim();
    }
    
    // ====================================
    // ENTITY EXTRACTION
    // ====================================
    
    /**
     * Extract entities from tokens using POS tags
     */
    private List<Entity> extractEntities(List<String> tokens, List<POSTag> posTags) {
        List<Entity> entities = new ArrayList<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            POSTag posTag = i < posTags.size() ? posTags.get(i) : POSTag.UNKNOWN;
            
            // Contract number patterns
            if (isContractNumber(token)) {
                entities.add(new Entity(EntityType.CONTRACT_NUMBER, token, i));
            }
            // Part number patterns
            else if (isPartNumber(token)) {
                entities.add(new Entity(EntityType.PART_NUMBER, token, i));
            }
            // Date patterns
            else if (isDate(token)) {
                entities.add(new Entity(EntityType.DATE, token, i));
            }
            // Number patterns
            else if (isNumber(token)) {
                entities.add(new Entity(EntityType.NUMBER, token, i));
            }
            // Proper nouns (potential customer/vendor names)
            else if (posTag == POSTag.PROPER_NOUN) {
                entities.add(new Entity(EntityType.PROPER_NOUN, token, i));
            }
        }
        
        return entities;
    }
    
    // ====================================
    // INTENT CLASSIFICATION
    // ====================================
    
    /**
     * Classify user intent based on lemmatized tokens and entities
     */
    private Intent classifyIntent(List<String> lemmatizedTokens, List<Entity> entities) {
        Set<String> tokenSet = new HashSet<>(lemmatizedTokens);
        
        // Check for failed parts intent
        if (hasFailedPartsKeywords(tokenSet)) {
            return new Intent(IntentType.FAILED_PARTS, calculateConfidence(tokenSet, "failed_parts"));
        }
        
        // Check for parts intent
        if (hasPartsKeywords(tokenSet)) {
            return new Intent(IntentType.PARTS, calculateConfidence(tokenSet, "parts"));
        }
        
        // Check for help/create intent
        if (hasCreateKeywords(tokenSet)) {
            return new Intent(IntentType.HELP, calculateConfidence(tokenSet, "help"));
        }
        
        // Default to contract intent
        return new Intent(IntentType.CONTRACT, calculateConfidence(tokenSet, "contract"));
    }
    
    // ====================================
    // KEYWORD DETECTION METHODS
    // ====================================
    
    private boolean hasFailedPartsKeywords(Set<String> tokens) {
        Set<String> failedKeywords = configManager.getFailedPartsKeywords();
        Set<String> partsKeywords = configManager.getPartsKeywords();
        
        boolean hasFailedTerms = tokens.stream().anyMatch(failedKeywords::contains);
        boolean hasPartsTerms = tokens.stream().anyMatch(partsKeywords::contains);
        
        return hasFailedTerms && hasPartsTerms;
    }
    
    private boolean hasPartsKeywords(Set<String> tokens) {
        Set<String> partsKeywords = configManager.getPartsKeywords();
        return tokens.stream().anyMatch(partsKeywords::contains);
    }
    
    private boolean hasCreateKeywords(Set<String> tokens) {
        Set<String> createKeywords = configManager.getCreateKeywords();
        return tokens.stream().anyMatch(createKeywords::contains);
    }
    
    // ====================================
    // CONFIDENCE CALCULATION
    // ====================================
    
    private double calculateConfidence(Set<String> tokens, String category) {
        Set<String> categoryKeywords = getCategoryKeywords(category);
        long matchCount = tokens.stream().mapToLong(token -> 
            categoryKeywords.contains(token) ? 1 : 0).sum();
        
        if (categoryKeywords.isEmpty()) return 0.5;
        
        double baseConfidence = (double) matchCount / Math.max(tokens.size(), 1);
        return Math.min(0.95, Math.max(0.1, baseConfidence));
    }
    
    private Set<String> getCategoryKeywords(String category) {
        switch (category) {
            case "failed_parts": return configManager.getFailedPartsKeywords();
            case "parts": return configManager.getPartsKeywords();
            case "help": return configManager.getCreateKeywords();
            case "contract": return configManager.getContractKeywords();
            default: return new HashSet<>();
        }
    }
    
    // ====================================
    // VALIDATION
    // ====================================
    
    private ValidationResult validateQuery(ProcessedQuery.Builder builder) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Check if query is too short
        if (builder.getTokens().size() < 2) {
            warnings.add("Query might be too short for accurate processing");
        }
        
        // Check if query is too long
        if (builder.getTokens().size() > 50) {
            warnings.add("Query might be too long, consider simplifying");
        }
        
        // Check if no entities found for specific intents
        if (builder.getIntent().getType() == IntentType.CONTRACT && 
            builder.getEntities().stream().noneMatch(e -> e.getType() == EntityType.CONTRACT_NUMBER)) {
            warnings.add("No contract number detected in contract query");
        }
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    // ====================================
    // PATTERN MATCHING METHODS
    // ====================================
    
    private boolean isContractNumber(String token) {
        // Contract patterns: numbers, alphanumeric with dashes
        return Pattern.matches("\\d{4,10}|[A-Z]{1,3}-\\d{3,6}|[A-Z]{2,4}\\d{3,6}", token.toUpperCase());
    }
    
    private boolean isPartNumber(String token) {
        // Part patterns: AE125, PN7890, etc.
        return Pattern.matches("[A-Z]{1,3}\\d{3,6}|PN\\d{4,6}|[A-Z]{2}\\d{3}", token.toUpperCase());
    }
    
    private boolean isDate(String token) {
        // Date patterns: YYYY-MM-DD, MM/DD/YYYY, etc.
        return Pattern.matches("\\d{4}-\\d{2}-\\d{2}|\\d{2}/\\d{2}/\\d{4}|\\d{1,2}-\\d{1,2}-\\d{4}", token);
    }
    
    private boolean isNumber(String token) {
        return Pattern.matches("\\d+(\\.\\d+)?", token);
    }
}

// ====================================
// TOKENIZER CLASS
// ====================================

/**
 * Advanced tokenizer with multiple tokenization strategies
 */
class Tokenizer {
    
    public List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Split on whitespace and common delimiters
        String[] tokens = text.split("\\s+|[,;:!?.]");
        
        return Arrays.stream(tokens)
                    .filter(token -> !token.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toList());
    }
}

// ====================================
// POS TAGGER CLASS
// ====================================

/**
 * Part-of-Speech tagger for grammatical analysis
 */
class POSTagger {
    
    private final Map<String, POSTag> posMap;
    
    public POSTagger() {
        this.posMap = loadPOSMappings();
    }
    
    public List<POSTag> tagTokens(List<String> tokens) {
        return tokens.stream()
                    .map(this::tagToken)
                    .collect(Collectors.toList());
    }
    
    private POSTag tagToken(String token) {
        // Simple rule-based POS tagging
        if (posMap.containsKey(token.toLowerCase())) {
            return posMap.get(token.toLowerCase());
        }
        
        // Pattern-based tagging
        if (token.matches("\\d+")) return POSTag.NUMBER;
        if (token.matches("[A-Z][a-z]+")) return POSTag.PROPER_NOUN;
        if (token.endsWith("ing")) return POSTag.VERB;
        if (token.endsWith("ed")) return POSTag.VERB;
        if (token.endsWith("ly")) return POSTag.ADVERB;
        
        return POSTag.NOUN; // Default
    }
    
    private Map<String, POSTag> loadPOSMappings() {
        Map<String, POSTag> map = new HashMap<>();
        
        // Common verbs
        map.put("show", POSTag.VERB);
        map.put("display", POSTag.VERB);
        map.put("list", POSTag.VERB);
        map.put("find", POSTag.VERB);
        map.put("get", POSTag.VERB);
        map.put("create", POSTag.VERB);
        
        // Common nouns
        map.put("contract", POSTag.NOUN);
        map.put("part", POSTag.NOUN);
        map.put("parts", POSTag.NOUN);
        map.put("failure", POSTag.NOUN);
        map.put("error", POSTag.NOUN);
        map.put("message", POSTag.NOUN);
        map.put("reason", POSTag.NOUN);
        
        // Common adjectives
        map.put("failed", POSTag.ADJECTIVE);
        map.put("broken", POSTag.ADJECTIVE);
        map.put("damaged", POSTag.ADJECTIVE);
        
        return map;
    }
}

// ====================================
// SPELL CHECKER CLASS
// ====================================

/**
 * Advanced spell checker with domain-specific corrections
 */
class SpellChecker {
    
    private final Map<String, String> corrections;
    
    public SpellChecker() {
        this.corrections = loadSpellCorrections();
    }
    
    public List<String> correctTokens(List<String> tokens) {
        return tokens.stream()
                    .map(this::correctToken)
                    .collect(Collectors.toList());
    }
    
    private String correctToken(String token) {
        String lowerToken = token.toLowerCase();
        return corrections.getOrDefault(lowerToken, token);
    }
    
    private Map<String, String> loadSpellCorrections() {
        Map<String, String> map = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(
                new FileReader("enhanced_spell_corrections.txt"))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("=") && !line.startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        map.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            // Load default corrections if file not found
            loadDefaultCorrections(map);
        }
        
        return map;
    }
    
    private void loadDefaultCorrections(Map<String, String> map) {
        // Core corrections
        map.put("faild", "failed");
        map.put("prts", "parts");
        map.put("contrct", "contract");
        map.put("reasn", "reason");
        map.put("mesage", "message");
        map.put("eror", "error");
        map.put("shw", "show");
        map.put("lst", "list");
        map.put("whch", "which");
        map.put("wht", "what");
    }
}

// ====================================
// LEMMATIZER CLASS
// ====================================

/**
 * Lemmatizer for reducing words to their root forms
 */
class Lemmatizer {
    
    private final Map<String, String> lemmaMap;
    
    public Lemmatizer() {
        this.lemmaMap = loadLemmaMappings();
    }
    
    public List<String> lemmatizeTokens(List<String> tokens, List<POSTag> posTags) {
        List<String> lemmatized = new ArrayList<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            POSTag pos = i < posTags.size() ? posTags.get(i) : POSTag.UNKNOWN;
            
            lemmatized.add(lemmatizeToken(token, pos));
        }
        
        return lemmatized;
    }
    
    private String lemmatizeToken(String token, POSTag pos) {
        String key = token.toLowerCase() + "_" + pos.name();
        
        if (lemmaMap.containsKey(key)) {
            return lemmaMap.get(key);
        }
        
        // Simple rule-based lemmatization
        switch (pos) {
            case VERB:
                if (token.endsWith("ing")) {
                    return token.substring(0, token.length() - 3);
                }
                if (token.endsWith("ed")) {
                    return token.substring(0, token.length() - 2);
                }
                break;
            case NOUN:
                if (token.endsWith("s") && token.length() > 3) {
                    return token.substring(0, token.length() - 1);
                }
                break;
        }
        
        return token;
    }
    
    private Map<String, String> loadLemmaMappings() {
        Map<String, String> map = new HashMap<>();
        
        // Common verb lemmas
        map.put("showing_VERB", "show");
        map.put("displayed_VERB", "display");
        map.put("listing_VERB", "list");
        map.put("finding_VERB", "find");
        map.put("getting_VERB", "get");
        map.put("creating_VERB", "create");
        
        // Common noun lemmas
        map.put("contracts_NOUN", "contract");
        map.put("parts_NOUN", "part");
        map.put("failures_NOUN", "failure");
        map.put("errors_NOUN", "error");
        map.put("messages_NOUN", "message");
        map.put("reasons_NOUN", "reason");
        
        return map;
    }
}

// ====================================
// CONFIGURATION MANAGER
// ====================================

/**
 * Singleton configuration manager for loading keywords and settings
 */
class ConfigurationManager {
    
    private static ConfigurationManager instance;
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Set<String> failedPartsKeywords;
    private Set<String> contractKeywords;
    
    private ConfigurationManager() {
        loadConfiguration();
    }
    
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
    
    private void loadConfiguration() {
        this.partsKeywords = loadKeywordsFromFile("parts_keywords.txt");
        this.createKeywords = loadKeywordsFromFile("create_keywords.txt");
        this.failedPartsKeywords = loadKeywordsFromFile("failed_parts_keywords.txt");
        this.contractKeywords = loadDefaultContractKeywords();
    }
    
    private Set<String> loadKeywordsFromFile(String filename) {
        Set<String> keywords = new HashSet<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    keywords.add(line.toLowerCase());
                }
            }
        } catch (IOException e) {
            // Load defaults if file not found
            loadDefaultKeywords(keywords, filename);
        }
        
        return keywords;
    }
    
    private void loadDefaultKeywords(Set<String> keywords, String filename) {
        switch (filename) {
            case "parts_keywords.txt":
                keywords.addAll(Arrays.asList("parts", "part", "line", "lines", "components"));
                break;
            case "create_keywords.txt":
                keywords.addAll(Arrays.asList("create", "new", "add", "make", "generate"));
                break;
            case "failed_parts_keywords.txt":
                keywords.addAll(Arrays.asList("failed", "failure", "error", "broken", "defective"));
                break;
        }
    }
    
    private Set<String> loadDefaultContractKeywords() {
        return new HashSet<>(Arrays.asList(
            "contract", "agreement", "award", "deal", "terms", "conditions"
        ));
    }
    
    // Getters
    public Set<String> getPartsKeywords() { return new HashSet<>(partsKeywords); }
    public Set<String> getCreateKeywords() { return new HashSet<>(createKeywords); }
    public Set<String> getFailedPartsKeywords() { return new HashSet<>(failedPartsKeywords); }
    public Set<String> getContractKeywords() { return new HashSet<>(contractKeywords); }
}

// ====================================
// SUPPORTING ENUMS AND CLASSES
// ====================================

enum POSTag {
    NOUN, VERB, ADJECTIVE, ADVERB, PROPER_NOUN, NUMBER, PREPOSITION, CONJUNCTION, UNKNOWN
}

enum EntityType {
    CONTRACT_NUMBER, PART_NUMBER, DATE, NUMBER, PROPER_NOUN, CURRENCY, PERCENTAGE
}

enum IntentType {
    CONTRACT, PARTS, FAILED_PARTS, HELP, UNKNOWN
}

/**
 * Represents a processed query with all NLP analysis
 */
class ProcessedQuery {
    private final String originalInput;
    private final String cleanedInput;
    private final List<String> tokens;
    private final List<String> correctedTokens;
    private final List<POSTag> posTags;
    private final List<String> lemmatizedTokens;
    private final List<Entity> entities;
    private final Intent intent;
    private final ValidationResult validation;
    private final String error;
    
    private ProcessedQuery(Builder builder) {
        this.originalInput = builder.originalInput;
        this.cleanedInput = builder.cleanedInput;
        this.tokens = builder.tokens;
        this.correctedTokens = builder.correctedTokens;
        this.posTags = builder.posTags;
        this.lemmatizedTokens = builder.lemmatizedTokens;
        this.entities = builder.entities;
        this.intent = builder.intent;
        this.validation = builder.validation;
        this.error = builder.error;
    }
    
    // Getters
    public String getOriginalInput() { return originalInput; }
    public String getCleanedInput() { return cleanedInput; }
    public List<String> getTokens() { return new ArrayList<>(tokens); }
    public List<String> getCorrectedTokens() { return new ArrayList<>(correctedTokens); }
    public List<POSTag> getPosTags() { return new ArrayList<>(posTags); }
    public List<String> getLemmatizedTokens() { return new ArrayList<>(lemmatizedTokens); }
    public List<Entity> getEntities() { return new ArrayList<>(entities); }
    public Intent getIntent() { return intent; }
    public ValidationResult getValidation() { return validation; }
    public String getError() { return error; }
    public boolean hasError() { return error != null; }
    
    // Builder pattern
    public static class Builder {
        private final String originalInput;
        private String cleanedInput;
        private List<String> tokens = new ArrayList<>();
        private List<String> correctedTokens = new ArrayList<>();
        private List<POSTag> posTags = new ArrayList<>();
        private List<String> lemmatizedTokens = new ArrayList<>();
        private List<Entity> entities = new ArrayList<>();
        private Intent intent;
        private ValidationResult validation;
        private String error;
        
        public Builder(String originalInput) {
            this.originalInput = originalInput;
        }
        
        public Builder setCleanedInput(String cleanedInput) {
            this.cleanedInput = cleanedInput;
            return this;
        }
        
        public Builder setTokens(List<String> tokens) {
            this.tokens = new ArrayList<>(tokens);
            return this;
        }
        
        public Builder setCorrectedTokens(List<String> correctedTokens) {
            this.correctedTokens = new ArrayList<>(correctedTokens);
            return this;
        }
        
        public Builder setPosTags(List<POSTag> posTags) {
            this.posTags = new ArrayList<>(posTags);
            return this;
        }
        
        public Builder setLemmatizedTokens(List<String> lemmatizedTokens) {
            this.lemmatizedTokens = new ArrayList<>(lemmatizedTokens);
            return this;
        }
        
        public Builder setEntities(List<Entity> entities) {
            this.entities = new ArrayList<>(entities);
            return this;
        }
        
        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }
        
        public Builder setValidation(ValidationResult validation) {
            this.validation = validation;
            return this;
        }
        
        public Builder setError(String error) {
            this.error = error;
            return this;
        }
        
        public ProcessedQuery build() {
            return new ProcessedQuery(this);
        }
        
        // Getters for validation
        public List<String> getTokens() { return tokens; }
        public Intent getIntent() { return intent; }
        public List<Entity> getEntities() { return entities; }
    }
}

/**
 * Represents an extracted entity
 */
class Entity {
    private final EntityType type;
    private final String value;
    private final int position;
    
    public Entity(EntityType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }
    
    public EntityType getType() { return type; }
    public String getValue() { return value; }
    public int getPosition() { return position; }
    
    @Override
    public String toString() {
        return String.format("Entity{type=%s, value='%s', position=%d}", type, value, position);
    }
}

/**
 * Represents classified intent
 */
class Intent {
    private final IntentType type;
    private final double confidence;
    
    public Intent(IntentType type, double confidence) {
        this.type = type;
        this.confidence = confidence;
    }
    
    public IntentType getType() { return type; }
    public double getConfidence() { return confidence; }
    
    @Override
    public String toString() {
        return String.format("Intent{type=%s, confidence=%.2f}", type, confidence);
    }
}

/**
 * Represents validation results
 */
class ValidationResult {
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;
    
    public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors);
        this.warnings = new ArrayList<>(warnings);
    }
    
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return new ArrayList<>(errors); }
    public List<String> getWarnings() { return new ArrayList<>(warnings); }
    public boolean hasWarnings() { return !warnings.isEmpty(); }
    public boolean hasErrors() { return !errors.isEmpty(); }
}
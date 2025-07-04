package com.company.contracts.service;

import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DoccatModel;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Contract NLP Service using Apache OpenNLP
 * 
 * This service processes natural language queries related to contracts
 * using trained Apache OpenNLP models and returns structured responses.
 * 
 * Features:
 * - Intent classification using OpenNLP models
 * - Entity extraction from queries
 * - Spell correction using .txt configuration files
 * - Confidence scoring
 * - Response template system
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class ContractNLPService {
    
    private static final Logger logger = Logger.getLogger(ContractNLPService.class.getName());
    
    // Model Manager
    private OpenNLPModelManager modelManager;
    
    // Document Categorizers for each domain
    private DocumentCategorizerME contractCategorizer;
    private DocumentCategorizerME partsCategorizer;
    private DocumentCategorizerME helpCategorizer;
    
    // Configuration loaded from .txt files
    private Map<String, String> spellCorrections;
    private Set<String> contractKeywords;
    private Set<String> partsKeywords;
    private Set<String> helpKeywords;
    private Properties responseTemplates;
    
    // Entity extraction patterns
    private Map<String, Pattern> entityPatterns;
    
    /**
     * Constructor
     */
    public ContractNLPService(OpenNLPModelManager modelManager) throws Exception {
        this.modelManager = modelManager;
        initializeService();
    }
    
    /**
     * Initialize the NLP service
     */
    private void initializeService() throws Exception {
        // Load models
        loadOpenNLPModels();
        
        // Load configuration from .txt files
        loadConfigurationFromFiles();
        
        // Initialize entity patterns
        initializeEntityPatterns();
        
        logger.info("ContractNLPService initialized successfully");
    }
    
    /**
     * Load Apache OpenNLP models
     */
    private void loadOpenNLPModels() throws Exception {
        try {
            // Load contract intent model
            DoccatModel contractModel = modelManager.getContractModel();
            if (contractModel != null) {
                contractCategorizer = new DocumentCategorizerME(contractModel);
                logger.info("Contract categorizer loaded");
            }
            
            // Load parts intent model
            DoccatModel partsModel = modelManager.getPartsModel();
            if (partsModel != null) {
                partsCategorizer = new DocumentCategorizerME(partsModel);
                logger.info("Parts categorizer loaded");
            }
            
            // Load help intent model
            DoccatModel helpModel = modelManager.getHelpModel();
            if (helpModel != null) {
                helpCategorizer = new DocumentCategorizerME(helpModel);
                logger.info("Help categorizer loaded");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading OpenNLP models", e);
            throw new Exception("Failed to load OpenNLP models: " + e.getMessage());
        }
    }
    
    /**
     * Load configuration from .txt files for easy updates
     */
    private void loadConfigurationFromFiles() throws IOException {
        // Load spell corrections
        spellCorrections = loadSpellCorrectionsFromFile("config/spell_corrections.txt");
        
        // Load keywords for domain classification
        contractKeywords = loadKeywordsFromFile("config/contract_keywords.txt");
        partsKeywords = loadKeywordsFromFile("config/parts_keywords.txt");
        helpKeywords = loadKeywordsFromFile("config/help_keywords.txt");
        
        // Load response templates
        responseTemplates = loadResponseTemplatesFromFile("config/response_templates.txt");
        
        logger.info("Configuration loaded from .txt files");
    }
    
    /**
     * Load spell corrections from .txt file
     */
    private Map<String, String> loadSpellCorrectionsFromFile(String filename) {
        Map<String, String> corrections = new HashMap<>();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
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
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load spell corrections from " + filename, e);
            // Use default corrections
            corrections.put("contrct", "contract");
            corrections.put("effectuve", "effective");
            corrections.put("exipraion", "expiration");
        }
        
        return corrections;
    }
    
    /**
     * Load keywords from .txt file
     */
    private Set<String> loadKeywordsFromFile(String filename) {
        Set<String> keywords = new HashSet<>();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    keywords.add(line);
                }
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load keywords from " + filename, e);
        }
        
        return keywords;
    }
    
    /**
     * Load response templates from .txt file
     */
    private Properties loadResponseTemplatesFromFile(String filename) {
        Properties templates = new Properties();
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            templates.load(is);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load response templates from " + filename, e);
            // Use default templates
            templates.setProperty("contract_found", "Contract {contractNumber} found successfully");
            templates.setProperty("contract_not_found", "Contract {contractNumber} not found");
            templates.setProperty("help_general", "I can help you with contracts, parts, and system navigation");
        }
        
        return templates;
    }
    
    /**
     * Initialize entity extraction patterns
     */
    private void initializeEntityPatterns() {
        entityPatterns = new HashMap<>();
        
        // Contract number patterns
        entityPatterns.put("contractNumber", Pattern.compile("\\b(?:contract\\s+(?:number\\s+)?)?([A-Z0-9]{3,}-?[A-Z0-9]{3,}|\\d{6,})\\b", Pattern.CASE_INSENSITIVE));
        
        // Part number patterns
        entityPatterns.put("partNumber", Pattern.compile("\\b(?:part\\s+(?:number\\s+)?)?P?([A-Z0-9]{5,})\\b", Pattern.CASE_INSENSITIVE));
        
        // Customer patterns
        entityPatterns.put("customer", Pattern.compile("\\b(?:customer|client)\\s+([A-Z][a-zA-Z\\s]+)\\b", Pattern.CASE_INSENSITIVE));
        
        // Date patterns
        entityPatterns.put("date", Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2}|\\d{2}/\\d{2}/\\d{4})\\b"));
        
        // Account patterns
        entityPatterns.put("accountNumber", Pattern.compile("\\b(?:account\\s+(?:number\\s+)?)?([A-Z0-9]{6,12})\\b", Pattern.CASE_INSENSITIVE));
    }
    
    /**
     * Main method to process user query
     */
    public NLPResponse processQuery(String userQuery) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Step 1: Apply spell correction
            String correctedQuery = applySpellCorrection(userQuery);
            
            // Step 2: Determine primary domain
            String primaryDomain = determinePrimaryDomain(correctedQuery);
            
            // Step 3: Classify intent using appropriate OpenNLP model
            IntentClassificationResult intentResult = classifyIntent(correctedQuery, primaryDomain);
            
            // Step 4: Extract entities
            Map<String, String> entities = extractEntities(correctedQuery);
            
            // Step 5: Generate response
            String responseText = generateResponse(intentResult, entities);
            
            // Step 6: Build NLP response
            NLPResponse response = new NLPResponse();
            response.setOriginalQuery(userQuery);
            response.setCorrectedQuery(correctedQuery);
            response.setIntent(intentResult.getIntent());
            response.setConfidence(intentResult.getConfidence());
            response.setDomain(primaryDomain);
            response.setEntities(entities);
            response.setResponseText(responseText);
            response.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            response.setSuccess(true);
            
            logger.info("Query processed successfully: " + userQuery + " -> " + intentResult.getIntent() + " (" + intentResult.getConfidence() + ")");
            
            return response;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing query: " + userQuery, e);
            
            // Return error response
            NLPResponse errorResponse = new NLPResponse();
            errorResponse.setOriginalQuery(userQuery);
            errorResponse.setIntent("ERROR");
            errorResponse.setConfidence(0.0);
            errorResponse.setResponseText("I encountered an error processing your request. Please try again.");
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage(e.getMessage());
            
            return errorResponse;
        }
    }
    
    /**
     * Apply spell correction using .txt file configuration
     */
    private String applySpellCorrection(String query) {
        String corrected = query.toLowerCase();
        
        for (Map.Entry<String, String> correction : spellCorrections.entrySet()) {
            corrected = corrected.replaceAll("\\b" + correction.getKey() + "\\b", correction.getValue());
        }
        
        return corrected;
    }
    
    /**
     * Determine primary domain based on keywords
     */
    private String determinePrimaryDomain(String query) {
        String lowerQuery = query.toLowerCase();
        
        int contractScore = 0;
        int partsScore = 0;
        int helpScore = 0;
        
        // Score each domain based on keyword presence
        for (String keyword : contractKeywords) {
            if (lowerQuery.contains(keyword)) {
                contractScore++;
            }
        }
        
        for (String keyword : partsKeywords) {
            if (lowerQuery.contains(keyword)) {
                partsScore++;
            }
        }
        
        for (String keyword : helpKeywords) {
            if (lowerQuery.contains(keyword)) {
                helpScore++;
            }
        }
        
        // Return domain with highest score
        if (partsScore > contractScore && partsScore > helpScore) {
            return "PARTS";
        } else if (helpScore > contractScore && helpScore > partsScore) {
            return "HELP";
        } else {
            return "CONTRACT";
        }
    }
    
    /**
     * Classify intent using appropriate OpenNLP model
     */
    private IntentClassificationResult classifyIntent(String query, String domain) {
        try {
            DocumentCategorizerME categorizer = null;
            
            switch (domain) {
                case "CONTRACT":
                    categorizer = contractCategorizer;
                    break;
                case "PARTS":
                    categorizer = partsCategorizer;
                    break;
                case "HELP":
                    categorizer = helpCategorizer;
                    break;
                default:
                    categorizer = contractCategorizer; // Default to contract
            }
            
            if (categorizer != null) {
                double[] outcomes = categorizer.categorize(query);
                String bestCategory = categorizer.getBestCategory(outcomes);
                double confidence = categorizer.getScore(outcomes, bestCategory);
                
                return new IntentClassificationResult(bestCategory, confidence);
                
            } else {
                // Fallback to rule-based classification
                return classifyIntentRuleBased(query, domain);
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in OpenNLP classification, falling back to rules", e);
            return classifyIntentRuleBased(query, domain);
        }
    }
    
    /**
     * Fallback rule-based intent classification
     */
    private IntentClassificationResult classifyIntentRuleBased(String query, String domain) {
        String lowerQuery = query.toLowerCase();
        
        if (domain.equals("PARTS")) {
            if (lowerQuery.contains("how many") || lowerQuery.contains("count")) {
                return new IntentClassificationResult("PARTS_COUNT", 0.8);
            } else if (lowerQuery.contains("details") || lowerQuery.contains("specifications")) {
                return new IntentClassificationResult("PARTS_DETAILS", 0.8);
            } else {
                return new IntentClassificationResult("PARTS_LOOKUP", 0.7);
            }
        } else if (domain.equals("HELP")) {
            if (lowerQuery.contains("create") || lowerQuery.contains("how to")) {
                return new IntentClassificationResult("HELP_CREATE", 0.8);
            } else {
                return new IntentClassificationResult("HELP_GENERAL", 0.7);
            }
        } else {
            if (lowerQuery.contains("effective") || lowerQuery.contains("expiration")) {
                return new IntentClassificationResult("CONTRACT_DATES", 0.8);
            } else if (lowerQuery.contains("customer") || lowerQuery.contains("vendor")) {
                return new IntentClassificationResult("CONTRACT_CUSTOMER", 0.8);
            } else {
                return new IntentClassificationResult("CONTRACT_LOOKUP", 0.7);
            }
        }
    }
    
    /**
     * Extract entities from query using regex patterns
     */
    private Map<String, String> extractEntities(String query) {
        Map<String, String> entities = new HashMap<>();
        
        for (Map.Entry<String, Pattern> entry : entityPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(query);
            if (matcher.find()) {
                String entityValue = matcher.groupCount() > 0 ? matcher.group(1) : matcher.group();
                entities.put(entry.getKey(), entityValue.trim());
            }
        }
        
        return entities;
    }
    
    /**
     * Generate response based on intent and entities
     */
    private String generateResponse(IntentClassificationResult intentResult, Map<String, String> entities) {
        String intent = intentResult.getIntent();
        
        // Get response template based on intent
        String template = responseTemplates.getProperty(intent.toLowerCase(), "I understand your request about " + intent);
        
        // Replace placeholders with entity values
        String response = template;
        for (Map.Entry<String, String> entity : entities.entrySet()) {
            String placeholder = "{" + entity.getKey() + "}";
            response = response.replace(placeholder, entity.getValue());
        }
        
        // Add confidence information if low
        if (intentResult.getConfidence() < 0.6) {
            response += " (I'm not entirely sure about this - please provide more details if needed)";
        }
        
        return response;
    }
    
    /**
     * Reload configuration from .txt files
     */
    public void reloadConfiguration() throws IOException {
        loadConfigurationFromFiles();
        logger.info("Configuration reloaded from .txt files");
    }
    
    /**
     * Inner class for intent classification results
     */
    public static class IntentClassificationResult {
        private String intent;
        private double confidence;
        
        public IntentClassificationResult(String intent, double confidence) {
            this.intent = intent;
            this.confidence = confidence;
        }
        
        public String getIntent() { return intent; }
        public double getConfidence() { return confidence; }
    }
}
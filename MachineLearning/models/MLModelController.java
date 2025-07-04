package MachineLearning.models;

import MachineLearning.models.ContractMLModel.ContractResponse;
import MachineLearning.models.PartsMLModel.PartsResponse;
import MachineLearning.models.HelpMLModel.HelpResponse;

import java.io.*;
import java.util.*;

/**
 * Main ML Model Controller using Apache OpenNLP
 * 
 * This controller orchestrates all three ML models:
 * - ContractMLModel: Handles contract-related queries
 * - PartsMLModel: Handles parts-related queries
 * - HelpMLModel: Handles help/creation-related queries
 * 
 * Provides a unified interface for natural language processing and routing
 * Can be integrated into any Contract Portal application
 */
public class MLModelController {
    
    // ML Models
    private ContractMLModel contractModel;
    private PartsMLModel partsModel;
    private HelpMLModel helpModel;
    
    // Routing keywords (loaded from configuration files for easy maintenance)
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Map<String, String> spellCorrections;
    
    // Model state
    private boolean isInitialized = false;
    private String baseModelPath = "MachineLearning/binaries/";
    
    /**
     * Initialize the ML Model Controller
     */
    public MLModelController() {
        try {
            initializeModels();
            loadKeywordConfigurations();
            this.isInitialized = true;
            System.out.println("MLModelController initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing MLModelController: " + e.getMessage());
            this.isInitialized = false;
        }
    }
    
    /**
     * Main method to process user input - THIS IS THE METHOD TO CALL
     */
    public String processUserInput(String userInput) {
        if (!isInitialized) {
            return generateErrorResponse("ML Model Controller not properly initialized");
        }
        
        if (userInput == null || userInput.trim().isEmpty()) {
            return generateErrorResponse("Empty input provided");
        }
        
        try {
            long startTime = System.nanoTime();
            
            // 1. Perform spell correction
            String correctedInput = performSpellCorrection(userInput.trim());
            
            // 2. Determine routing based on keywords and business rules
            ModelRoute route = determineRoute(correctedInput);
            
            // 3. Process with appropriate ML model
            String response = routeToModel(route, correctedInput);
            
            // 4. Add routing metadata
            response = addRoutingMetadata(response, route, userInput, correctedInput, 
                                        (System.nanoTime() - startTime) / 1000.0);
            
            return response;
            
        } catch (Exception e) {
            return generateErrorResponse("Error processing input: " + e.getMessage());
        }
    }
    
    /**
     * Load pre-trained models from binary files
     */
    public void loadPreTrainedModels() throws IOException {
        System.out.println("Loading pre-trained ML models...");
        
        contractModel.loadFromBinary(baseModelPath + "contract_model.bin");
        partsModel.loadFromBinary(baseModelPath + "parts_model.bin");
        helpModel.loadFromBinary(baseModelPath + "help_model.bin");
        
        System.out.println("All ML models loaded successfully!");
    }
    
    /**
     * Train all models with provided training data
     */
    public void trainAllModels() throws IOException {
        System.out.println("Training all ML models...");
        
        // Train individual models
        contractModel.trainModel("MachineLearning/training/contract_training_data.txt");
        partsModel.trainModel("MachineLearning/training/parts_training_data.txt");
        helpModel.trainModel("MachineLearning/training/help_training_data.txt");
        
        System.out.println("All models trained successfully!");
    }
    
    /**
     * Export all trained models to binary files
     */
    public void exportAllModels() throws IOException {
        System.out.println("Exporting all ML models...");
        
        contractModel.exportToBinary(baseModelPath + "contract_model.bin");
        partsModel.exportToBinary(baseModelPath + "parts_model.bin");
        helpModel.exportToBinary(baseModelPath + "help_model.bin");
        
        System.out.println("All models exported successfully!");
    }
    
    /**
     * Initialize all ML models
     */
    private void initializeModels() {
        contractModel = new ContractMLModel();
        partsModel = new PartsMLModel();
        helpModel = new HelpMLModel();
    }
    
    /**
     * Load keyword configurations from text files
     */
    private void loadKeywordConfigurations() throws IOException {
        // Load parts keywords
        partsKeywords = loadKeywordsFromFile("parts_keywords.txt");
        
        // Load create keywords
        createKeywords = loadKeywordsFromFile("create_keywords.txt");
        
        // Load spell corrections
        spellCorrections = loadSpellCorrectionsFromFile("spell_corrections.txt");
        
        System.out.println("Keyword configurations loaded:");
        System.out.println("- Parts keywords: " + partsKeywords.size());
        System.out.println("- Create keywords: " + createKeywords.size());
        System.out.println("- Spell corrections: " + spellCorrections.size());
    }
    
    /**
     * Load keywords from text file
     */
    private Set<String> loadKeywordsFromFile(String fileName) throws IOException {
        Set<String> keywords = new HashSet<>();
        String filePath = "MachineLearning/training/" + fileName;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    keywords.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            // Create default keywords if file not found
            if (fileName.contains("parts")) {
                keywords.addAll(Arrays.asList("parts", "part", "lines", "line", "components", "component"));
            } else if (fileName.contains("create")) {
                keywords.addAll(Arrays.asList("create", "creating", "make", "new", "add", "generate"));
            }
        }
        
        return keywords;
    }
    
    /**
     * Load spell corrections from text file
     */
    private Map<String, String> loadSpellCorrectionsFromFile(String fileName) throws IOException {
        Map<String, String> corrections = new HashMap<>();
        String filePath = "MachineLearning/training/" + fileName;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
        } catch (FileNotFoundException e) {
            // Create default corrections if file not found
            corrections.put("effectuve", "effective");
            corrections.put("exipraion", "expiration");
            corrections.put("contrct", "contract");
        }
        
        return corrections;
    }
    
    /**
     * Perform spell correction on input
     */
    private String performSpellCorrection(String input) {
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String correctedWord = spellCorrections.getOrDefault(cleanWord, word);
            corrected.append(correctedWord).append(" ");
        }
        
        return corrected.toString().trim();
    }
    
    /**
     * Determine routing based on business rules and keywords
     */
    private ModelRoute determineRoute(String input) {
        String lowercaseInput = input.toLowerCase();
        
        // Check for keyword presence
        boolean hasPartsKeywords = containsAnyKeyword(lowercaseInput, partsKeywords);
        boolean hasCreateKeywords = containsAnyKeyword(lowercaseInput, createKeywords);
        
        // Extract contract ID
        String contractId = extractContractId(input);
        boolean hasContractId = !contractId.equals("N/A");
        
        // Business rule 1: Parts creation error
        if (hasPartsKeywords && hasCreateKeywords) {
            return new ModelRoute(ModelType.PARTS_CREATE_ERROR, 
                "Parts creation not supported - parts are loaded from Excel files", 
                "BUSINESS_RULE_VIOLATION");
        }
        
        // Business rule 2: Parts queries
        if (hasPartsKeywords) {
            return new ModelRoute(ModelType.PARTS, 
                "Input contains parts-related keywords", 
                "PARTS_QUERY");
        }
        
        // Business rule 3: Help/Creation queries
        if (hasCreateKeywords) {
            return new ModelRoute(ModelType.HELP, 
                "Input contains creation/help keywords", 
                "HELP_REQUEST");
        }
        
        // Business rule 4: Contract queries (default)
        return new ModelRoute(ModelType.CONTRACT, 
            "Default routing to contract model" + (hasContractId ? " (Contract ID detected)" : ""), 
            hasContractId ? "CONTRACT_ID_QUERY" : "GENERAL_CONTRACT_QUERY");
    }
    
    /**
     * Route to appropriate ML model
     */
    private String routeToModel(ModelRoute route, String input) {
        switch (route.getModelType()) {
            case CONTRACT:
                ContractResponse contractResponse = contractModel.processContractQuery(input);
                return contractResponse.toJson();
                
            case PARTS:
                PartsResponse partsResponse = partsModel.processPartsQuery(input);
                return partsResponse.toJson();
                
            case HELP:
                HelpResponse helpResponse = helpModel.processHelpQuery(input);
                return helpResponse.toJson();
                
            case PARTS_CREATE_ERROR:
                return generatePartsCreateErrorResponse();
                
            default:
                return generateErrorResponse("Unknown model route: " + route.getModelType());
        }
    }
    
    /**
     * Check if input contains any keyword from the set
     */
    private boolean containsAnyKeyword(String input, Set<String> keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extract contract ID from input
     */
    private String extractContractId(String input) {
        String[] words = input.split("\\s+");
        
        // Pattern 1: 6-digit numbers
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        
        // Pattern 2: Contract format ABC-123, XYZ456
        for (String word : words) {
            if (word.matches("[A-Z]{3}-?\\d{3,6}")) {
                return word;
            }
        }
        
        return "N/A";
    }
    
    /**
     * Generate parts creation error response
     */
    private String generatePartsCreateErrorResponse() {
        return "{\n" +
               "  \"responseType\": \"PARTS_CREATE_ERROR\",\n" +
               "  \"message\": \"Parts creation is not supported\",\n" +
               "  \"explanation\": \"Parts are loaded from Excel files and cannot be created through this system\",\n" +
               "  \"alternatives\": [\n" +
               "    \"View existing parts: Say 'show parts for contract 123456'\",\n" +
               "    \"Search parts: Say 'list all parts'\",\n" +
               "    \"Parts count: Say 'how many parts for contract 123456'\"\n" +
               "  ],\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    /**
     * Generate error response
     */
    private String generateErrorResponse(String errorMessage) {
        return "{\n" +
               "  \"responseType\": \"ERROR\",\n" +
               "  \"message\": \"" + errorMessage + "\",\n" +
               "  \"timestamp\": " + System.currentTimeMillis() + "\n" +
               "}";
    }
    
    /**
     * Add routing metadata to response
     */
    private String addRoutingMetadata(String response, ModelRoute route, String originalInput, 
                                     String correctedInput, double processingTime) {
        // Find the last closing brace and insert metadata before it
        int lastBrace = response.lastIndexOf("}");
        if (lastBrace == -1) return response;
        
        String responseWithoutBrace = response.substring(0, lastBrace);
        
        String metadata = ",\n" +
                         "  \"routingInfo\": {\n" +
                         "    \"targetModel\": \"" + route.getModelType() + "\",\n" +
                         "    \"routingReason\": \"" + route.getReason() + "\",\n" +
                         "    \"intentType\": \"" + route.getIntentType() + "\",\n" +
                         "    \"originalInput\": \"" + originalInput + "\",\n" +
                         "    \"correctedInput\": \"" + correctedInput + "\",\n" +
                         "    \"processingTimeMs\": " + processingTime + ",\n" +
                         "    \"mlFramework\": \"Apache OpenNLP\",\n" +
                         "    \"timestamp\": \"" + new Date() + "\"\n" +
                         "  }\n" +
                         "}";
        
        return responseWithoutBrace + metadata;
    }
    
    /**
     * Get information about all loaded models
     */
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("systemName", "MLModelController");
        info.put("version", "1.0.0");
        info.put("framework", "Apache OpenNLP");
        info.put("isInitialized", isInitialized);
        
        if (isInitialized) {
            info.put("contractModel", contractModel.getModelInfo());
            info.put("partsModel", partsModel.getModelInfo());
            info.put("helpModel", helpModel.getModelInfo());
            info.put("keywordCounts", Map.of(
                "partsKeywords", partsKeywords.size(),
                "createKeywords", createKeywords.size(),
                "spellCorrections", spellCorrections.size()
            ));
        }
        
        return info;
    }
    
    /**
     * Model Route class
     */
    private static class ModelRoute {
        private final ModelType modelType;
        private final String reason;
        private final String intentType;
        
        public ModelRoute(ModelType modelType, String reason, String intentType) {
            this.modelType = modelType;
            this.reason = reason;
            this.intentType = intentType;
        }
        
        public ModelType getModelType() { return modelType; }
        public String getReason() { return reason; }
        public String getIntentType() { return intentType; }
    }
    
    /**
     * Model Type enum
     */
    private enum ModelType {
        CONTRACT, PARTS, HELP, PARTS_CREATE_ERROR
    }
}
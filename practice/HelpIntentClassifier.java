package view.practice;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
public class HelpIntentClassifier {
    
    private static final Logger logger = Logger.getLogger(HelpIntentClassifier.class.getName());
    private DocumentCategorizerME categorizer;
    private DoccatModel model; // Keep reference to model for accessing categories
    private HelpHintsManager hintsManager;
    private TextPreprocessor preprocessor;
    private boolean isModelLoaded = false;
    
    // Intent confidence threshold
    private static final double CONFIDENCE_THRESHOLD = 0.6;
    
    // Predefined categories since we can't get them from the model directly
    private static final String[] HELP_CATEGORIES = {
        "help_create_contract",
        "help_load_parts", 
        "help_validate_parts",
        "help_check_errors",
        "help_export_data",
        "help_import_data",
        "help_generate_report",
        "help_manage_users",
        "help_backup_data",
        "unknown"
    };
    
    public HelpIntentClassifier() {
        try {
            initializeComponents();
        } catch (Exception e) {
            logger.severe("? Error initializing HelpIntentClassifier: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() throws IOException {
        logger.info("? Initializing Help Intent Classifier components...");
        
        // Initialize preprocessor
        preprocessor = new TextPreprocessor();
        
        // Initialize help hints manager
        hintsManager = new HelpHintsManager("Help_Hints.txt");
        
        // Load the trained model
        loadModel();
        
        logger.info("? Help Intent Classifier initialized successfully");
    }
    
    private void loadModel() throws IOException {
        try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream("help-intent-model.bin")) {
            if (modelIn == null) {
                throw new IOException("Help intent model file not found in resources");
            }
            
            model = new DoccatModel(modelIn);
            categorizer = new DocumentCategorizerME(model);
            isModelLoaded = true;
            
            logger.info("? Help Intent Model loaded successfully");
            
        } catch (IOException e) {
            logger.severe("? Error loading Help Intent Model: " + e.getMessage());
            throw e;
        }
    }
    
    public IntentResult classifyIntent(String userInput) {
        if (!isModelLoaded) {
            logger.warning("?? Model not loaded, returning unknown intent");
            return new IntentResult("unknown", 0.0, "Model not available");
        }
        
        if (userInput == null || userInput.trim().isEmpty()) {
            return new IntentResult("unknown", 0.0, "Empty input");
        }
        
        try {
            // Preprocess the input
            String processedInput = preprocessor.preprocessText(userInput);
            logger.info("? Processing: '" + userInput + "' -> '" + processedInput + "'");
            
            // Classify intent
            String[] tokens = processedInput.split("\\s+");
            double[] outcomes = categorizer.categorize(tokens);
            String bestCategory = categorizer.getBestCategory(outcomes);
            
            // Get confidence for the best category
            double confidence = getBestCategoryConfidence(outcomes, bestCategory);
            
            // Apply confidence threshold
            if (confidence < CONFIDENCE_THRESHOLD) {
                logger.info("?? Low confidence (" + String.format("%.2f", confidence * 100) + "%), treating as unknown");
                bestCategory = "unknown";
            }
            
            // Get help response if it's a help intent
            String response = getHelpResponse(bestCategory, confidence);
            
            logger.info(String.format("? Intent: %s, Confidence: %.2f%%, Response length: %d",
                bestCategory, confidence * 100, response.length()));
            
            return new IntentResult(bestCategory, confidence, response);
            
        } catch (Exception e) {
            logger.severe("? Error classifying intent: " + e.getMessage());
            e.printStackTrace();
            return new IntentResult("unknown", 0.0, "Error processing request");
        }
    }
    
    /**
     * Get confidence for the best category by finding the highest score in outcomes
     */
    private double getBestCategoryConfidence(double[] outcomes, String bestCategory) {
        if (outcomes == null || outcomes.length == 0) {
            return 0.0;
        }
        
        // Find the maximum confidence score
        double maxConfidence = 0.0;
        for (double outcome : outcomes) {
            if (outcome > maxConfidence) {
                maxConfidence = outcome;
            }
        }
        
        return maxConfidence;
    }
    
    /**
     * Create score map using predefined categories
     */
    private Map<String, Double> createScoreMap(double[] outcomes) {
        Map<String, Double> scoreMap = new HashMap<>();
        
        try {
            // Use predefined categories since we can't get them from the model
            String[] categories = getAvailableCategories();
            
            // Map each category to its outcome score
            for (int i = 0; i < categories.length && i < outcomes.length; i++) {
                scoreMap.put(categories[i], outcomes[i]);
            }
            
        } catch (Exception e) {
            logger.warning("Error creating score map: " + e.getMessage());
            // Return empty map if there's an issue
        }
        
        return scoreMap;
    }
    
    private String getHelpResponse(String intent, double confidence) {
        if (intent.startsWith("help_")) {
            String helpText = hintsManager.getHelpText(intent);
            
            // Format the response nicely
            StringBuilder response = new StringBuilder();
            response.append("? **Help Guide**\n\n");
            
            // Add confidence indicator for high confidence
            if (confidence > 0.8) {
                response.append("Here are the steps:\n\n");
            } else {
                response.append("I think you're asking about this topic. Here are the steps:\n\n");
            }
            
            // Format the help text
            String[] steps = helpText.split("?");
            for (int i = 0; i < steps.length; i++) {
                response.append("**Step ").append(i + 1).append(":** ")
                        .append(steps[i].trim()).append("\n\n");
            }
            
            // Add footer
            response.append("? *Need more help? Please contact support or check the user manual.*");
            
            return response.toString();
            
        } else if ("unknown".equals(intent)) {
            return "I'm not sure how to help with that specific request. " +
                   "I can help you with:\n" +
                   "• Creating contracts\n" +
                   "• Loading parts\n" +
                   "• Validating parts\n" +
                   "• Checking errors\n" +
                   "• And other system operations\n\n" +
                   "Please try rephrasing your question or contact support for assistance.";
        }
        
        return "I understand you need help, but I don't have specific guidance for that topic.";
    }
    
    public boolean isHelpIntent(String intent) {
        return intent != null && intent.startsWith("help_");
    }
    
    public Set<String> getSupportedIntents() {
        // Use HashSet constructor instead of Set.of() for Java 8 compatibility
        Set<String> intents = new HashSet<>();
        intents.add("help_create_contract");
        intents.add("help_load_parts");
        intents.add("help_validate_parts");
        intents.add("help_check_errors");
        intents.add("help_export_data");
        intents.add("help_import_data");
        intents.add("help_generate_report");
        intents.add("help_manage_users");
        intents.add("help_backup_data");
        return intents;
    }
    
    /**
     * Get all available categories using predefined list
     */
    public String[] getAvailableCategories() {
        return HELP_CATEGORIES.clone(); // Return a copy to prevent modification
    }
    
    /**
     * Get detailed classification results for debugging
     */
    public Map<String, Double> getAllCategoryScores(String userInput) {
        if (!isModelLoaded || userInput == null || userInput.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            String processedInput = preprocessor.preprocessText(userInput);
            String[] tokens = processedInput.split("\\s+");
            double[] outcomes = categorizer.categorize(tokens);
            
            return createScoreMap(outcomes);
            
        } catch (Exception e) {
            logger.warning("Error getting category scores: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Get the number of categories in the model
     */
    public int getNumberOfCategories() {
        return HELP_CATEGORIES.length;
    }
    
    /**
     * Check if a category is supported
     */
    public boolean isSupportedCategory(String category) {
        if (category == null) return false;
        
        for (String supportedCategory : HELP_CATEGORIES) {
            if (supportedCategory.equals(category)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if the model is properly loaded
     */
    public boolean isModelLoaded() {
        return isModelLoaded;
    }
    
    /**
     * Reload the model (useful for updates)
     */
    public void reloadModel() {
        try {
            loadModel();
            logger.info("? Help Intent Model reloaded successfully");
        } catch (IOException e) {
            logger.severe("? Error reloading Help Intent Model: " + e.getMessage());
            isModelLoaded = false;
        }
    }
    
    /**
     * Get model information for debugging
     */
    public String getModelInfo() {
        if (!isModelLoaded || model == null) {
            return "Model not loaded";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Help Intent Model Information:\n");
        info.append("- Model loaded: ").append(isModelLoaded).append("\n");
        info.append("- Available categories: ").append(HELP_CATEGORIES.length).append("\n");
        info.append("- Confidence threshold: ").append(CONFIDENCE_THRESHOLD).append("\n");
        
        return info.toString();
    }
    
    // Inner class for intent results
    public static class IntentResult {
        private final String intent;
        private final double confidence;
        private final String response;
        
        public IntentResult(String intent, double confidence, String response) {
            this.intent = intent;
            this.confidence = confidence;
            this.response = response;
        }
        
        public String getIntent() { return intent; }
        public double getConfidence() { return confidence; }
        public String getResponse() { return response; }
        
        public boolean isHelpIntent() {
            return intent != null && intent.startsWith("help_");
        }
        
        public boolean hasHighConfidence() {
            return confidence >= 0.7;
        }
        
        public boolean hasLowConfidence() {
            return confidence < 0.5;
        }
        
        public String getConfidenceLevel() {
            if (confidence >= 0.8) return "High";
            if (confidence >= 0.6) return "Medium";
            if (confidence >= 0.4) return "Low";
            return "Very Low";
        }
        
        @Override
        public String toString() {
            return String.format("IntentResult{intent='%s', confidence=%.2f, confidenceLevel='%s'}", 
                intent, confidence, getConfidenceLevel());
        }
    }
}

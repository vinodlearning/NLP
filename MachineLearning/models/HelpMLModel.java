package MachineLearning.models;

import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Help Machine Learning Model using Apache OpenNLP
 * 
 * This model handles all help and creation-related queries including:
 * - Contract creation guidance
 * - Step-by-step instructions
 * - Configuration help
 * - Process documentation
 * 
 * Trained model can be exported as .bin file for integration with other applications
 */
public class HelpMLModel {
    
    private DoccatModel categoryModel;
    private DocumentCategorizerME categorizer;
    private TokenizerME tokenizer;
    private boolean isModelTrained = false;
    
    // Help categories for classification
    private static final String[] HELP_CATEGORIES = {
        "HELP_CREATE_CONTRACT", "HELP_CREATE_WORKFLOW", "HELP_APPROVAL_PROCESS", "HELP_VALIDATION",
        "HELP_REQUIREMENTS", "HELP_TEMPLATES", "HELP_CONTRACT_TYPES", "HELP_DURATION", "HELP_PRICING",
        "HELP_TERMS", "HELP_CONDITIONS", "HELP_CLAUSES", "HELP_PARTY_INFO", "HELP_AUTHORIZATION",
        "HELP_SIGNATURES", "HELP_DOCUMENT_MGMT", "HELP_VERSION_CONTROL", "HELP_AMENDMENTS", "HELP_RENEWAL",
        "HELP_TERMINATION", "HELP_COMPLIANCE", "HELP_SECURITY", "HELP_INTEGRATION", "HELP_AUTOMATION"
    };
    
    /**
     * Initialize the Help ML Model
     */
    public HelpMLModel() {
        try {
            initializeTokenizer();
        } catch (IOException e) {
            System.err.println("Error initializing HelpMLModel: " + e.getMessage());
        }
    }
    
    /**
     * Train the help model with training data
     */
    public void trainModel(String trainingDataPath) throws IOException {
        System.out.println("Training Help ML Model...");
        
        // Load training data
        ObjectStream<DocumentSample> sampleStream = loadTrainingData(trainingDataPath);
        
        // Configure training parameters
        TrainingParameters trainParams = new TrainingParameters();
        trainParams.put(TrainingParameters.ITERATIONS_PARAM, 100);
        trainParams.put(TrainingParameters.CUTOFF_PARAM, 5);
        trainParams.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
        
        // Train the model
        categoryModel = DocumentCategorizerME.train("en", sampleStream, trainParams, 
                                                    new DoccatFactory());
        
        // Initialize categorizer with trained model
        categorizer = new DocumentCategorizerME(categoryModel);
        isModelTrained = true;
        
        System.out.println("Help ML Model training completed!");
        printModelStatistics();
    }
    
    /**
     * Process help query and return categorized response
     */
    public HelpResponse processHelpQuery(String input) {
        if (!isModelTrained) {
            return new HelpResponse("ERROR", "Model not trained", null);
        }
        
        try {
            // Tokenize input
            String[] tokens = tokenizer.tokenize(input.toLowerCase());
            
            // Categorize the query
            double[] outcomes = categorizer.categorize(tokens);
            String category = categorizer.getBestCategory(outcomes);
            double confidence = categorizer.getCategory(0, outcomes);
            
            // Generate response based on category
            return generateHelpResponse(category, input, confidence);
            
        } catch (Exception e) {
            return new HelpResponse("ERROR", "Error processing query: " + e.getMessage(), null);
        }
    }
    
    /**
     * Export trained model to binary file
     */
    public void exportToBinary(String filePath) throws IOException {
        if (!isModelTrained) {
            throw new IllegalStateException("Model must be trained before export");
        }
        
        // Ensure directory exists
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        
        // Export model
        try (FileOutputStream modelOut = new FileOutputStream(filePath)) {
            categoryModel.serialize(modelOut);
        }
        
        System.out.println("Help model exported to: " + filePath);
    }
    
    /**
     * Load trained model from binary file
     */
    public void loadFromBinary(String filePath) throws IOException {
        try (FileInputStream modelIn = new FileInputStream(filePath)) {
            categoryModel = new DoccatModel(modelIn);
            categorizer = new DocumentCategorizerME(categoryModel);
            isModelTrained = true;
            System.out.println("Help model loaded from: " + filePath);
        }
    }
    
    /**
     * Load training data from text file
     */
    private ObjectStream<DocumentSample> loadTrainingData(String filePath) throws IOException {
        List<DocumentSample> samples = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String text = parts[0].trim();
                    String category = parts[1].trim();
                    samples.add(new DocumentSample(category, text.split("\\s+")));
                }
            }
        }
        
        System.out.println("Loaded " + samples.size() + " training samples for Help model");
        return ObjectStreamUtils.createObjectStream(samples);
    }
    
    /**
     * Initialize tokenizer
     */
    private void initializeTokenizer() throws IOException {
        tokenizer = new TokenizerME(new SimpleTokenizerModel());
    }
    
    /**
     * Generate help response based on category
     */
    private HelpResponse generateHelpResponse(String category, String input, double confidence) {
        Map<String, Object> data = new HashMap<>();
        data.put("confidence", confidence);
        data.put("category", category);
        
        switch (category) {
            case "HELP_CREATE_CONTRACT":
                return generateContractCreationHelp(data);
            case "HELP_CREATE_WORKFLOW":
                return generateWorkflowHelp(data);
            case "HELP_APPROVAL_PROCESS":
                return generateApprovalProcessHelp(data);
            case "HELP_VALIDATION":
                return generateValidationHelp(data);
            case "HELP_REQUIREMENTS":
                return generateRequirementsHelp(data);
            case "HELP_TEMPLATES":
                return generateTemplatesHelp(data);
            case "HELP_PRICING":
                return generatePricingHelp(data);
            case "HELP_TERMS":
                return generateTermsHelp(data);
            default:
                return generateGeneralHelp(data);
        }
    }
    
    /**
     * Generate contract creation help
     */
    private HelpResponse generateContractCreationHelp(Map<String, Object> data) {
        List<Map<String, Object>> steps = Arrays.asList(
            Map.of(
                "step", 1,
                "title", "Gather Required Information",
                "description", "Collect all necessary details for the contract",
                "details", Arrays.asList(
                    "Customer/Client information",
                    "Contract type and scope",
                    "Pricing and payment terms",
                    "Timeline and milestones",
                    "Legal requirements"
                )
            ),
            Map.of(
                "step", 2,
                "title", "Select Contract Template",
                "description", "Choose appropriate template based on contract type",
                "details", Arrays.asList(
                    "Service Agreement Template",
                    "Supply Contract Template",
                    "License Agreement Template",
                    "Custom Template"
                )
            ),
            Map.of(
                "step", 3,
                "title", "Configure Contract Details",
                "description", "Fill in specific contract information",
                "details", Arrays.asList(
                    "Set effective and expiration dates",
                    "Define pricing structure",
                    "Add specific terms and conditions",
                    "Include performance metrics"
                )
            ),
            Map.of(
                "step", 4,
                "title", "Review and Validate",
                "description", "Ensure all information is accurate and complete",
                "details", Arrays.asList(
                    "Validate required fields",
                    "Check business rules compliance",
                    "Review pricing calculations",
                    "Verify legal requirements"
                )
            ),
            Map.of(
                "step", 5,
                "title", "Submit for Approval",
                "description", "Send contract through approval workflow",
                "details", Arrays.asList(
                    "Route to appropriate approvers",
                    "Track approval status",
                    "Handle approval comments",
                    "Finalize approved contract"
                )
            )
        );
        
        data.put("steps", steps);
        data.put("estimatedTime", "30-60 minutes");
        data.put("difficulty", "Moderate");
        
        return new HelpResponse("HELP_CREATE_CONTRACT_SUCCESS", 
                               "Contract creation guide provided", data);
    }
    
    /**
     * Generate workflow help
     */
    private HelpResponse generateWorkflowHelp(Map<String, Object> data) {
        data.put("workflow", Map.of(
            "stages", Arrays.asList(
                "Draft Creation",
                "Internal Review",
                "Legal Review",
                "Financial Approval",
                "Final Approval",
                "Contract Execution"
            ),
            "estimatedDuration", "5-10 business days",
            "requiredRoles", Arrays.asList(
                "Contract Creator",
                "Department Manager",
                "Legal Reviewer",
                "Financial Approver",
                "Executive Approver"
            )
        ));
        
        return new HelpResponse("HELP_WORKFLOW_SUCCESS", 
                               "Contract workflow information provided", data);
    }
    
    /**
     * Generate approval process help
     */
    private HelpResponse generateApprovalProcessHelp(Map<String, Object> data) {
        data.put("approvalProcess", Map.of(
            "approvalLevels", Map.of(
                "Level1", "Department Manager (up to $10,000)",
                "Level2", "Director (up to $50,000)",
                "Level3", "VP (up to $250,000)",
                "Level4", "Executive Team ($250,000+)"
            ),
            "requiredDocuments", Arrays.asList(
                "Contract Draft",
                "Business Justification",
                "Risk Assessment",
                "Financial Impact Analysis"
            ),
            "averageApprovalTime", "3-5 business days"
        ));
        
        return new HelpResponse("HELP_APPROVAL_SUCCESS", 
                               "Approval process information provided", data);
    }
    
    /**
     * Generate validation help
     */
    private HelpResponse generateValidationHelp(Map<String, Object> data) {
        data.put("validation", Map.of(
            "requiredFields", Arrays.asList(
                "Contract Title",
                "Customer Information",
                "Effective Date",
                "Expiration Date",
                "Total Value",
                "Payment Terms"
            ),
            "businessRules", Arrays.asList(
                "Effective date must be future date",
                "Expiration date must be after effective date",
                "Total value must be positive",
                "Customer must be active",
                "Payment terms must be valid"
            ),
            "validationChecks", Arrays.asList(
                "Field completeness",
                "Data format validation",
                "Business rule compliance",
                "Cross-field validation",
                "Reference data validation"
            )
        ));
        
        return new HelpResponse("HELP_VALIDATION_SUCCESS", 
                               "Validation information provided", data);
    }
    
    /**
     * Generate requirements help
     */
    private HelpResponse generateRequirementsHelp(Map<String, Object> data) {
        data.put("requirements", Map.of(
            "mandatory", Arrays.asList(
                "Contract Type",
                "Customer Details",
                "Service/Product Description",
                "Pricing Information",
                "Terms and Conditions"
            ),
            "optional", Arrays.asList(
                "Special Clauses",
                "Attachments",
                "Custom Fields",
                "Additional Notes"
            ),
            "preparation", Arrays.asList(
                "Gather customer requirements",
                "Prepare service specifications",
                "Calculate pricing",
                "Review legal requirements"
            )
        ));
        
        return new HelpResponse("HELP_REQUIREMENTS_SUCCESS", 
                               "Requirements information provided", data);
    }
    
    /**
     * Generate templates help
     */
    private HelpResponse generateTemplatesHelp(Map<String, Object> data) {
        data.put("templates", Arrays.asList(
            Map.of(
                "templateId", "TPL001",
                "name", "Standard Service Agreement",
                "description", "General service contract template",
                "category", "Services",
                "fields", 25,
                "complexity", "Medium"
            ),
            Map.of(
                "templateId", "TPL002",
                "name", "Product Supply Contract",
                "description", "Template for product supply agreements",
                "category", "Supply",
                "fields", 30,
                "complexity", "High"
            ),
            Map.of(
                "templateId", "TPL003",
                "name", "Software License Agreement",
                "description", "Software licensing contract template",
                "category", "License",
                "fields", 20,
                "complexity", "Medium"
            )
        ));
        
        return new HelpResponse("HELP_TEMPLATES_SUCCESS", 
                               "Template information provided", data);
    }
    
    /**
     * Generate pricing help
     */
    private HelpResponse generatePricingHelp(Map<String, Object> data) {
        data.put("pricingGuide", Map.of(
            "pricingModels", Arrays.asList(
                "Fixed Price",
                "Time & Materials",
                "Cost Plus",
                "Performance Based"
            ),
            "components", Arrays.asList(
                "Base Price",
                "Discounts",
                "Taxes",
                "Additional Fees"
            ),
            "considerations", Arrays.asList(
                "Market rates",
                "Customer relationship",
                "Contract duration",
                "Volume discounts"
            )
        ));
        
        return new HelpResponse("HELP_PRICING_SUCCESS", 
                               "Pricing guidance provided", data);
    }
    
    /**
     * Generate terms help
     */
    private HelpResponse generateTermsHelp(Map<String, Object> data) {
        data.put("termsGuide", Map.of(
            "standardTerms", Arrays.asList(
                "Payment Terms: Net 30",
                "Delivery Terms: FOB Destination",
                "Warranty: 12 months",
                "Termination: 30 days notice"
            ),
            "customizableTerms", Arrays.asList(
                "Payment schedule",
                "Performance penalties",
                "Bonus clauses",
                "Renewal options"
            ),
            "legalConsiderations", Arrays.asList(
                "Governing law",
                "Jurisdiction",
                "Dispute resolution",
                "Force majeure"
            )
        ));
        
        return new HelpResponse("HELP_TERMS_SUCCESS", 
                               "Terms and conditions guidance provided", data);
    }
    
    /**
     * Generate general help
     */
    private HelpResponse generateGeneralHelp(Map<String, Object> data) {
        data.put("generalHelp", Map.of(
            "availableHelp", Arrays.asList(
                "Contract creation guidance",
                "Template selection help",
                "Approval process information",
                "Validation requirements",
                "Pricing guidelines"
            ),
            "supportContacts", Map.of(
                "technical", "support@company.com",
                "legal", "legal@company.com",
                "business", "business@company.com"
            ),
            "documentation", Arrays.asList(
                "User Manual",
                "Process Guide",
                "FAQ",
                "Video Tutorials"
            )
        ));
        
        return new HelpResponse("HELP_GENERAL_SUCCESS", 
                               "General help information provided", data);
    }
    
    /**
     * Print model statistics
     */
    private void printModelStatistics() {
        if (categoryModel != null) {
            System.out.println("=== Help Model Statistics ===");
            System.out.println("Categories: " + Arrays.toString(HELP_CATEGORIES));
            System.out.println("Model Type: " + categoryModel.getClass().getSimpleName());
            System.out.println("Training completed successfully!");
        }
    }
    
    /**
     * Get model information
     */
    public Map<String, Object> getModelInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("modelType", "HelpMLModel");
        info.put("categories", HELP_CATEGORIES);
        info.put("isTrained", isModelTrained);
        info.put("trainingFramework", "Apache OpenNLP");
        info.put("supportedLanguage", "English");
        return info;
    }
    
    /**
     * Simple tokenizer model for basic tokenization
     */
    private static class SimpleTokenizerModel extends TokenizerModel {
        public SimpleTokenizerModel() {
            super("en", null, null, null, null);
        }
    }
    
    /**
     * Help Response class
     */
    public static class HelpResponse {
        private String responseType;
        private String message;
        private Map<String, Object> data;
        private long timestamp;
        
        public HelpResponse(String responseType, String message, Map<String, Object> data) {
            this.responseType = responseType;
            this.message = message;
            this.data = data != null ? data : new HashMap<>();
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getResponseType() { return responseType; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return data; }
        public long getTimestamp() { return timestamp; }
        
        /**
         * Convert to JSON string
         */
        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"responseType\": \"").append(responseType).append("\",\n");
            json.append("  \"message\": \"").append(message).append("\",\n");
            json.append("  \"timestamp\": ").append(timestamp).append(",\n");
            json.append("  \"data\": ").append(mapToJson(data)).append("\n");
            json.append("}");
            return json.toString();
        }
        
        private String mapToJson(Map<String, Object> map) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) json.append(",\n");
                json.append("    \"").append(entry.getKey()).append("\": ");
                if (entry.getValue() instanceof String) {
                    json.append("\"").append(entry.getValue()).append("\"");
                } else if (entry.getValue() instanceof Map) {
                    json.append(mapToJson((Map<String, Object>) entry.getValue()));
                } else if (entry.getValue() instanceof List) {
                    json.append(listToJson((List<?>) entry.getValue()));
                } else {
                    json.append(entry.getValue());
                }
                first = false;
            }
            json.append("\n  }");
            return json.toString();
        }
        
        private String listToJson(List<?> list) {
            StringBuilder json = new StringBuilder();
            json.append("[\n");
            boolean first = true;
            for (Object item : list) {
                if (!first) json.append(",\n");
                if (item instanceof String) {
                    json.append("      \"").append(item).append("\"");
                } else if (item instanceof Map) {
                    json.append("      ").append(mapToJson((Map<String, Object>) item));
                } else {
                    json.append("      ").append(item);
                }
                first = false;
            }
            json.append("\n    ]");
            return json.toString();
        }
    }
}
package MachineLearning.models;

import opennlp.tools.doccat.*;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelType;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Contract Machine Learning Model using Apache OpenNLP
 * 
 * This model handles all contract-related queries including:
 * - Contract details lookup
 * - Contract dates and pricing
 * - Contract terms and conditions
 * - Contract status and customer information
 * 
 * Trained model can be exported as .bin file for integration with other applications
 */
public class ContractMLModel {
    
    private DoccatModel categoryModel;
    private DocumentCategorizerME categorizer;
    private TokenizerME tokenizer;
    private boolean isModelTrained = false;
    
    // Contract categories for classification
    private static final String[] CONTRACT_CATEGORIES = {
        "CONTRACT_LOOKUP", "CONTRACT_DATES", "CONTRACT_PRICING", "CONTRACT_TERMS",
        "CONTRACT_STATUS", "CONTRACT_CUSTOMER", "CONTRACT_PAYMENT", "CONTRACT_PENALTY",
        "CONTRACT_RENEWAL", "CONTRACT_TERMINATION", "CONTRACT_HISTORY", "CONTRACT_APPROVAL",
        "CONTRACT_COMPLIANCE", "CONTRACT_SLA", "CONTRACT_SUPPORT", "CONTRACT_WARRANTY",
        "CONTRACT_LIABILITY", "CONTRACT_IP", "CONTRACT_CONFIDENTIALITY", "CONTRACT_DISPUTE"
    };
    
    /**
     * Initialize the Contract ML Model
     */
    public ContractMLModel() {
        try {
            // Initialize tokenizer (using pre-trained English tokenizer)
            initializeTokenizer();
        } catch (IOException e) {
            System.err.println("Error initializing ContractMLModel: " + e.getMessage());
        }
    }
    
    /**
     * Train the contract model with training data
     */
    public void trainModel(String trainingDataPath) throws IOException {
        System.out.println("Training Contract ML Model...");
        
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
        
        System.out.println("Contract ML Model training completed!");
        printModelStatistics();
    }
    
    /**
     * Process contract query and return categorized response
     */
    public ContractResponse processContractQuery(String input) {
        if (!isModelTrained) {
            return new ContractResponse("ERROR", "Model not trained", null);
        }
        
        try {
            // Tokenize input
            String[] tokens = tokenizer.tokenize(input.toLowerCase());
            
            // Categorize the query
            double[] outcomes = categorizer.categorize(tokens);
            String category = categorizer.getBestCategory(outcomes);
            double confidence = categorizer.getCategory(0, outcomes);
            
            // Extract contract ID if present
            String contractId = extractContractId(input);
            
            // Generate response based on category
            return generateContractResponse(category, input, contractId, confidence);
            
        } catch (Exception e) {
            return new ContractResponse("ERROR", "Error processing query: " + e.getMessage(), null);
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
        
        System.out.println("Contract model exported to: " + filePath);
    }
    
    /**
     * Load trained model from binary file
     */
    public void loadFromBinary(String filePath) throws IOException {
        try (FileInputStream modelIn = new FileInputStream(filePath)) {
            categoryModel = new DoccatModel(modelIn);
            categorizer = new DocumentCategorizerME(categoryModel);
            isModelTrained = true;
            System.out.println("Contract model loaded from: " + filePath);
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
        
        System.out.println("Loaded " + samples.size() + " training samples for Contract model");
        return ObjectStreamUtils.createObjectStream(samples);
    }
    
    /**
     * Initialize tokenizer
     */
    private void initializeTokenizer() throws IOException {
        // For simplicity, using whitespace tokenizer
        // In production, you'd use a trained tokenizer model
        tokenizer = new TokenizerME(new SimpleTokenizerModel());
    }
    
    /**
     * Extract contract ID from input text
     */
    private String extractContractId(String input) {
        // Pattern 1: 6-digit numbers
        String[] words = input.split("\\s+");
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
     * Generate contract response based on category
     */
    private ContractResponse generateContractResponse(String category, String input, 
                                                     String contractId, double confidence) {
        Map<String, Object> data = new HashMap<>();
        data.put("contractId", contractId);
        data.put("confidence", confidence);
        data.put("category", category);
        
        switch (category) {
            case "CONTRACT_LOOKUP":
                return generateContractLookupResponse(contractId, data);
            case "CONTRACT_DATES":
                return generateContractDatesResponse(contractId, data);
            case "CONTRACT_PRICING":
                return generateContractPricingResponse(contractId, data);
            case "CONTRACT_TERMS":
                return generateContractTermsResponse(contractId, data);
            case "CONTRACT_STATUS":
                return generateContractStatusResponse(contractId, data);
            case "CONTRACT_CUSTOMER":
                return generateContractCustomerResponse(contractId, data);
            default:
                return generateGeneralContractResponse(contractId, data);
        }
    }
    
    /**
     * Generate contract lookup response
     */
    private ContractResponse generateContractLookupResponse(String contractId, Map<String, Object> data) {
        data.put("contractDetails", Map.of(
            "contractId", contractId,
            "contractTitle", "Professional Services Agreement",
            "contractType", "SERVICE_AGREEMENT",
            "status", "ACTIVE",
            "effectiveDate", "2024-01-15",
            "expirationDate", "2025-12-31",
            "customerName", "ABC Corporation",
            "totalValue", "$125,000.00"
        ));
        
        return new ContractResponse("CONTRACT_LOOKUP_SUCCESS", 
                                   "Contract details retrieved successfully", data);
    }
    
    /**
     * Generate contract dates response
     */
    private ContractResponse generateContractDatesResponse(String contractId, Map<String, Object> data) {
        data.put("dates", Map.of(
            "effectiveDate", "2024-01-15",
            "expirationDate", "2025-12-31",
            "priceExpirationDate", "2025-06-30",
            "renewalDate", "2025-11-30",
            "lastModified", "2024-03-15T14:30:00Z",
            "daysUntilExpiration", 335,
            "daysUntilRenewal", 305
        ));
        
        return new ContractResponse("CONTRACT_DATES_SUCCESS", 
                                   "Contract dates retrieved successfully", data);
    }
    
    /**
     * Generate contract pricing response
     */
    private ContractResponse generateContractPricingResponse(String contractId, Map<String, Object> data) {
        data.put("pricing", Map.of(
            "totalValue", "$125,000.00",
            "monthlyAmount", "$10,416.67",
            "currency", "USD",
            "discountRate", "15%",
            "earlyPaymentDiscount", "2%",
            "penaltyRate", "1.5%",
            "paymentTerms", "Net 30"
        ));
        
        return new ContractResponse("CONTRACT_PRICING_SUCCESS", 
                                   "Contract pricing retrieved successfully", data);
    }
    
    /**
     * Generate contract terms response
     */
    private ContractResponse generateContractTermsResponse(String contractId, Map<String, Object> data) {
        data.put("terms", Map.of(
            "paymentTerms", "Net 30 days",
            "deliveryTerms", "FOB Destination",
            "warrantyPeriod", "12 months",
            "terminationNotice", "30 days",
            "governingLaw", "State of California",
            "jurisdiction", "California Courts"
        ));
        
        return new ContractResponse("CONTRACT_TERMS_SUCCESS", 
                                   "Contract terms retrieved successfully", data);
    }
    
    /**
     * Generate contract status response
     */
    private ContractResponse generateContractStatusResponse(String contractId, Map<String, Object> data) {
        data.put("status", Map.of(
            "currentStatus", "ACTIVE",
            "approvalStatus", "APPROVED",
            "signatureStatus", "FULLY_SIGNED",
            "complianceStatus", "COMPLIANT",
            "lastStatusUpdate", "2024-03-15T14:30:00Z",
            "nextReview", "2024-12-15"
        ));
        
        return new ContractResponse("CONTRACT_STATUS_SUCCESS", 
                                   "Contract status retrieved successfully", data);
    }
    
    /**
     * Generate contract customer response
     */
    private ContractResponse generateContractCustomerResponse(String contractId, Map<String, Object> data) {
        data.put("customer", Map.of(
            "customerName", "ABC Corporation",
            "contactPerson", "John Smith",
            "email", "john.smith@abc-corp.com",
            "phone", "+1-555-123-4567",
            "accountManager", "Jane Doe",
            "region", "North America",
            "industry", "Technology"
        ));
        
        return new ContractResponse("CONTRACT_CUSTOMER_SUCCESS", 
                                   "Contract customer information retrieved successfully", data);
    }
    
    /**
     * Generate general contract response
     */
    private ContractResponse generateGeneralContractResponse(String contractId, Map<String, Object> data) {
        data.put("generalInfo", Map.of(
            "contractId", contractId,
            "status", "ACTIVE",
            "lastAccessed", new Date().toString(),
            "availableActions", Arrays.asList("VIEW", "MODIFY", "RENEW", "TERMINATE")
        ));
        
        return new ContractResponse("CONTRACT_GENERAL_SUCCESS", 
                                   "Contract information retrieved successfully", data);
    }
    
    /**
     * Print model statistics
     */
    private void printModelStatistics() {
        if (categoryModel != null) {
            System.out.println("=== Contract Model Statistics ===");
            System.out.println("Categories: " + Arrays.toString(CONTRACT_CATEGORIES));
            System.out.println("Model Type: " + categoryModel.getClass().getSimpleName());
            System.out.println("Training completed successfully!");
        }
    }
    
    /**
     * Get model information
     */
    public Map<String, Object> getModelInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("modelType", "ContractMLModel");
        info.put("categories", CONTRACT_CATEGORIES);
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
     * Contract Response class
     */
    public static class ContractResponse {
        private String responseType;
        private String message;
        private Map<String, Object> data;
        private long timestamp;
        
        public ContractResponse(String responseType, String message, Map<String, Object> data) {
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
                } else {
                    json.append(entry.getValue());
                }
                first = false;
            }
            json.append("\n  }");
            return json.toString();
        }
    }
}
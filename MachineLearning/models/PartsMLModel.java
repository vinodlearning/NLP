package MachineLearning.models;

import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Parts Machine Learning Model using Apache OpenNLP
 * 
 * This model handles all parts-related queries including:
 * - Parts lookup and inventory
 * - Parts specifications and compatibility
 * - Parts availability and ordering
 * - Parts maintenance and lifecycle
 * 
 * Trained model can be exported as .bin file for integration with other applications
 */
public class PartsMLModel {
    
    private DoccatModel categoryModel;
    private DocumentCategorizerME categorizer;
    private TokenizerME tokenizer;
    private boolean isModelTrained = false;
    
    // Parts categories for classification
    private static final String[] PARTS_CATEGORIES = {
        "PARTS_LOOKUP", "PARTS_COUNT", "PARTS_SPECS", "PARTS_INVENTORY", "PARTS_AVAILABILITY",
        "PARTS_ORDERING", "PARTS_SUPPLIER", "PARTS_MANUFACTURER", "PARTS_WARRANTY", "PARTS_PRICING",
        "PARTS_DELIVERY", "PARTS_QUALITY", "PARTS_TESTING", "PARTS_CERTIFICATION", "PARTS_COMPLIANCE",
        "PARTS_MAINTENANCE", "PARTS_REPLACEMENT", "PARTS_LIFECYCLE", "PARTS_ALTERNATIVES", "PARTS_DOCUMENTATION"
    };
    
    /**
     * Initialize the Parts ML Model
     */
    public PartsMLModel() {
        try {
            initializeTokenizer();
        } catch (IOException e) {
            System.err.println("Error initializing PartsMLModel: " + e.getMessage());
        }
    }
    
    /**
     * Train the parts model with training data
     */
    public void trainModel(String trainingDataPath) throws IOException {
        System.out.println("Training Parts ML Model...");
        
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
        
        System.out.println("Parts ML Model training completed!");
        printModelStatistics();
    }
    
    /**
     * Process parts query and return categorized response
     */
    public PartsResponse processPartsQuery(String input) {
        if (!isModelTrained) {
            return new PartsResponse("ERROR", "Model not trained", null);
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
            return generatePartsResponse(category, input, contractId, confidence);
            
        } catch (Exception e) {
            return new PartsResponse("ERROR", "Error processing query: " + e.getMessage(), null);
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
        
        System.out.println("Parts model exported to: " + filePath);
    }
    
    /**
     * Load trained model from binary file
     */
    public void loadFromBinary(String filePath) throws IOException {
        try (FileInputStream modelIn = new FileInputStream(filePath)) {
            categoryModel = new DoccatModel(modelIn);
            categorizer = new DocumentCategorizerME(categoryModel);
            isModelTrained = true;
            System.out.println("Parts model loaded from: " + filePath);
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
        
        System.out.println("Loaded " + samples.size() + " training samples for Parts model");
        return ObjectStreamUtils.createObjectStream(samples);
    }
    
    /**
     * Initialize tokenizer
     */
    private void initializeTokenizer() throws IOException {
        tokenizer = new TokenizerME(new SimpleTokenizerModel());
    }
    
    /**
     * Extract contract ID from input text
     */
    private String extractContractId(String input) {
        String[] words = input.split("\\s+");
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        for (String word : words) {
            if (word.matches("[A-Z]{3}-?\\d{3,6}")) {
                return word;
            }
        }
        return "N/A";
    }
    
    /**
     * Generate parts response based on category
     */
    private PartsResponse generatePartsResponse(String category, String input, 
                                               String contractId, double confidence) {
        Map<String, Object> data = new HashMap<>();
        data.put("contractId", contractId);
        data.put("confidence", confidence);
        data.put("category", category);
        
        switch (category) {
            case "PARTS_LOOKUP":
                return generatePartsLookupResponse(contractId, data);
            case "PARTS_COUNT":
                return generatePartsCountResponse(contractId, data);
            case "PARTS_SPECS":
                return generatePartsSpecsResponse(contractId, data);
            case "PARTS_INVENTORY":
                return generatePartsInventoryResponse(contractId, data);
            case "PARTS_AVAILABILITY":
                return generatePartsAvailabilityResponse(contractId, data);
            case "PARTS_PRICING":
                return generatePartsPricingResponse(contractId, data);
            default:
                return generateGeneralPartsResponse(contractId, data);
        }
    }
    
    /**
     * Generate parts lookup response
     */
    private PartsResponse generatePartsLookupResponse(String contractId, Map<String, Object> data) {
        List<Map<String, Object>> partsList = Arrays.asList(
            Map.of(
                "partId", "P001",
                "partName", "Control Unit Module",
                "partNumber", "CU-2024-001",
                "category", "Electronics",
                "quantity", 25,
                "unitPrice", "$150.00",
                "supplier", "TechParts Inc",
                "status", "AVAILABLE"
            ),
            Map.of(
                "partId", "P002",
                "partName", "Power Supply Assembly",
                "partNumber", "PS-2024-002",
                "category", "Power",
                "quantity", 15,
                "unitPrice", "$275.00",
                "supplier", "PowerTech Ltd",
                "status", "AVAILABLE"
            ),
            Map.of(
                "partId", "P003",
                "partName", "Sensor Array",
                "partNumber", "SA-2024-003",
                "category", "Sensors",
                "quantity", 40,
                "unitPrice", "$85.00",
                "supplier", "SensorPro Corp",
                "status", "LOW_STOCK"
            )
        );
        
        data.put("parts", partsList);
        data.put("totalParts", partsList.size());
        
        return new PartsResponse("PARTS_LOOKUP_SUCCESS", 
                                "Parts list retrieved successfully", data);
    }
    
    /**
     * Generate parts count response
     */
    private PartsResponse generatePartsCountResponse(String contractId, Map<String, Object> data) {
        data.put("count", Map.of(
            "totalParts", 80,
            "availableParts", 65,
            "lowStockParts", 12,
            "outOfStockParts", 3,
            "categoriesCount", 5,
            "suppliersCount", 8
        ));
        
        return new PartsResponse("PARTS_COUNT_SUCCESS", 
                                "Parts count retrieved successfully", data);
    }
    
    /**
     * Generate parts specifications response
     */
    private PartsResponse generatePartsSpecsResponse(String contractId, Map<String, Object> data) {
        data.put("specifications", Map.of(
            "technicalSpecs", Map.of(
                "operatingVoltage", "12V-24V DC",
                "temperatureRange", "-20°C to +85°C",
                "dimensions", "150mm x 100mm x 50mm",
                "weight", "0.5 kg",
                "certification", "CE, FCC, RoHS"
            ),
            "performanceSpecs", Map.of(
                "efficiency", "95%",
                "reliability", "99.9%",
                "mtbf", "50,000 hours",
                "warranty", "24 months"
            )
        ));
        
        return new PartsResponse("PARTS_SPECS_SUCCESS", 
                                "Parts specifications retrieved successfully", data);
    }
    
    /**
     * Generate parts inventory response
     */
    private PartsResponse generatePartsInventoryResponse(String contractId, Map<String, Object> data) {
        data.put("inventory", Map.of(
            "currentStock", 480,
            "minimumStock", 100,
            "maximumStock", 1000,
            "reorderPoint", 150,
            "reorderQuantity", 200,
            "averageUsage", "25 units/month",
            "lastRestocked", "2024-03-01",
            "nextReorder", "2024-04-15"
        ));
        
        return new PartsResponse("PARTS_INVENTORY_SUCCESS", 
                                "Parts inventory information retrieved successfully", data);
    }
    
    /**
     * Generate parts availability response
     */
    private PartsResponse generatePartsAvailabilityResponse(String contractId, Map<String, Object> data) {
        data.put("availability", Map.of(
            "status", "AVAILABLE",
            "quantityAvailable", 65,
            "reservedQuantity", 15,
            "expectedDelivery", "2024-04-01",
            "leadTime", "7-10 business days",
            "alternativeParts", Arrays.asList("P004", "P005"),
            "supplierConfirmation", "CONFIRMED"
        ));
        
        return new PartsResponse("PARTS_AVAILABILITY_SUCCESS", 
                                "Parts availability information retrieved successfully", data);
    }
    
    /**
     * Generate parts pricing response
     */
    private PartsResponse generatePartsPricingResponse(String contractId, Map<String, Object> data) {
        data.put("pricing", Map.of(
            "unitPrice", "$150.00",
            "bulkPricing", Map.of(
                "qty10Plus", "$145.00",
                "qty50Plus", "$140.00",
                "qty100Plus", "$135.00"
            ),
            "currency", "USD",
            "priceValidUntil", "2024-06-30",
            "discountAvailable", "5% for early payment",
            "shippingCost", "$25.00",
            "taxes", "8.5%"
        ));
        
        return new PartsResponse("PARTS_PRICING_SUCCESS", 
                                "Parts pricing information retrieved successfully", data);
    }
    
    /**
     * Generate general parts response
     */
    private PartsResponse generateGeneralPartsResponse(String contractId, Map<String, Object> data) {
        data.put("generalInfo", Map.of(
            "contractId", contractId,
            "totalPartsCount", 80,
            "lastUpdated", new Date().toString(),
            "availableActions", Arrays.asList("VIEW", "ORDER", "CHECK_AVAILABILITY", "GET_SPECS")
        ));
        
        return new PartsResponse("PARTS_GENERAL_SUCCESS", 
                                "Parts information retrieved successfully", data);
    }
    
    /**
     * Print model statistics
     */
    private void printModelStatistics() {
        if (categoryModel != null) {
            System.out.println("=== Parts Model Statistics ===");
            System.out.println("Categories: " + Arrays.toString(PARTS_CATEGORIES));
            System.out.println("Model Type: " + categoryModel.getClass().getSimpleName());
            System.out.println("Training completed successfully!");
        }
    }
    
    /**
     * Get model information
     */
    public Map<String, Object> getModelInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("modelType", "PartsMLModel");
        info.put("categories", PARTS_CATEGORIES);
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
     * Parts Response class
     */
    public static class PartsResponse {
        private String responseType;
        private String message;
        private Map<String, Object> data;
        private long timestamp;
        
        public PartsResponse(String responseType, String message, Map<String, Object> data) {
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
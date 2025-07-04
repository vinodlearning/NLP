package MachineLearning.training;

import MachineLearning.models.ContractMLModel;
import MachineLearning.models.PartsMLModel;
import MachineLearning.models.HelpMLModel;
import MachineLearning.models.MLModelController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Model Trainer for Contract Portal ML System
 * 
 * This class handles training of all ML models:
 * - ContractMLModel: Contract-related queries
 * - PartsMLModel: Parts-related queries  
 * - HelpMLModel: Help/creation-related queries
 * 
 * Models are trained using Apache OpenNLP and exported as .bin files
 * for integration with other applications
 */
public class ModelTrainer {
    
    private static final String TRAINING_DATA_PATH = "MachineLearning/training/";
    private static final String BINARIES_PATH = "MachineLearning/binaries/";
    
    public static void main(String[] args) {
        System.out.println("=== Contract Portal ML Model Trainer ===");
        System.out.println("Training all ML models using Apache OpenNLP...\n");
        
        ModelTrainer trainer = new ModelTrainer();
        
        try {
            // Ensure directories exist
            trainer.createDirectories();
            
            // Train all models
            trainer.trainAllModels();
            
            // Test the trained models
            trainer.testTrainedModels();
            
            System.out.println("\n🎉 All models trained and exported successfully!");
            System.out.println("Models are ready for integration into other applications.");
            
        } catch (Exception e) {
            System.err.println("❌ Error during training: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Train all ML models individually
     */
    public void trainAllModels() throws IOException {
        trainContractModel();
        trainPartsModel();
        trainHelpModel();
    }
    
    /**
     * Train Contract ML Model
     */
    public void trainContractModel() throws IOException {
        System.out.println("🔄 Training Contract ML Model...");
        
        ContractMLModel contractModel = new ContractMLModel();
        
        // Train with contract training data
        contractModel.trainModel(TRAINING_DATA_PATH + "contract_training_data.txt");
        
        // Export to binary file
        contractModel.exportToBinary(BINARIES_PATH + "contract_model.bin");
        
        System.out.println("✅ Contract model trained and exported!");
        printTrainingStats("Contract", TRAINING_DATA_PATH + "contract_training_data.txt");
    }
    
    /**
     * Train Parts ML Model
     */
    public void trainPartsModel() throws IOException {
        System.out.println("\n🔄 Training Parts ML Model...");
        
        PartsMLModel partsModel = new PartsMLModel();
        
        // Train with parts training data
        partsModel.trainModel(TRAINING_DATA_PATH + "parts_training_data.txt");
        
        // Export to binary file
        partsModel.exportToBinary(BINARIES_PATH + "parts_model.bin");
        
        System.out.println("✅ Parts model trained and exported!");
        printTrainingStats("Parts", TRAINING_DATA_PATH + "parts_training_data.txt");
    }
    
    /**
     * Train Help ML Model
     */
    public void trainHelpModel() throws IOException {
        System.out.println("\n🔄 Training Help ML Model...");
        
        HelpMLModel helpModel = new HelpMLModel();
        
        // Train with help training data
        helpModel.trainModel(TRAINING_DATA_PATH + "help_training_data.txt");
        
        // Export to binary file
        helpModel.exportToBinary(BINARIES_PATH + "help_model.bin");
        
        System.out.println("✅ Help model trained and exported!");
        printTrainingStats("Help", TRAINING_DATA_PATH + "help_training_data.txt");
    }
    
    /**
     * Test the trained models with sample inputs
     */
    public void testTrainedModels() throws IOException {
        System.out.println("\n🧪 Testing trained models...");
        
        // Create ML controller and load trained models
        MLModelController controller = new MLModelController();
        controller.loadPreTrainedModels();
        
        // Test inputs
        String[] testInputs = {
            "what is the expiration date for contract 123456",  // Contract model
            "show me parts for contract 123456",                // Parts model
            "how to create a new contract",                     // Help model
            "create parts for contract 123456"                  // Parts creation error
        };
        
        System.out.println("Testing with sample inputs:");
        for (String input : testInputs) {
            System.out.println("\n📝 Input: \"" + input + "\"");
            
            long startTime = System.nanoTime();
            String response = controller.processUserInput(input);
            long processingTime = (System.nanoTime() - startTime) / 1000;
            
            // Extract response type for summary
            String responseType = extractResponseType(response);
            System.out.println("📋 Response Type: " + responseType);
            System.out.println("⏱️  Processing Time: " + processingTime + "μs");
        }
    }
    
    /**
     * Create necessary directories
     */
    private void createDirectories() throws IOException {
        Files.createDirectories(Paths.get(BINARIES_PATH));
        System.out.println("📁 Created directories for model binaries");
    }
    
    /**
     * Print training statistics
     */
    private void printTrainingStats(String modelName, String trainingFile) {
        try {
            long lineCount = Files.lines(Paths.get(trainingFile))
                    .filter(line -> !line.trim().isEmpty() && !line.startsWith("#"))
                    .count();
            System.out.println("   📊 Training samples: " + lineCount);
            
            long fileSize = Files.size(Paths.get(trainingFile));
            System.out.println("   📏 Training data size: " + formatFileSize(fileSize));
            
        } catch (IOException e) {
            System.out.println("   ⚠️  Could not read training statistics");
        }
    }
    
    /**
     * Format file size for display
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
    
    /**
     * Extract response type from JSON response
     */
    private String extractResponseType(String response) {
        if (response.contains("\"responseType\": \"")) {
            int start = response.indexOf("\"responseType\": \"") + 17;
            int end = response.indexOf("\"", start);
            if (end > start) {
                return response.substring(start, end);
            }
        }
        return "UNKNOWN";
    }
    
    /**
     * Train models with custom training data paths
     */
    public void trainModelsWithCustomPaths(String contractDataPath, 
                                          String partsDataPath, 
                                          String helpDataPath) throws IOException {
        System.out.println("🔄 Training models with custom data paths...");
        
        // Train Contract Model
        if (contractDataPath != null && Files.exists(Paths.get(contractDataPath))) {
            ContractMLModel contractModel = new ContractMLModel();
            contractModel.trainModel(contractDataPath);
            contractModel.exportToBinary(BINARIES_PATH + "contract_model.bin");
            System.out.println("✅ Contract model trained with custom data");
        }
        
        // Train Parts Model
        if (partsDataPath != null && Files.exists(Paths.get(partsDataPath))) {
            PartsMLModel partsModel = new PartsMLModel();
            partsModel.trainModel(partsDataPath);
            partsModel.exportToBinary(BINARIES_PATH + "parts_model.bin");
            System.out.println("✅ Parts model trained with custom data");
        }
        
        // Train Help Model
        if (helpDataPath != null && Files.exists(Paths.get(helpDataPath))) {
            HelpMLModel helpModel = new HelpMLModel();
            helpModel.trainModel(helpDataPath);
            helpModel.exportToBinary(BINARIES_PATH + "help_model.bin");
            System.out.println("✅ Help model trained with custom data");
        }
    }
    
    /**
     * Validate training data files
     */
    public boolean validateTrainingData() {
        System.out.println("🔍 Validating training data files...");
        
        boolean allValid = true;
        
        // Check contract training data
        allValid &= validateTrainingFile(TRAINING_DATA_PATH + "contract_training_data.txt", "Contract");
        
        // Check parts training data
        allValid &= validateTrainingFile(TRAINING_DATA_PATH + "parts_training_data.txt", "Parts");
        
        // Check help training data
        allValid &= validateTrainingFile(TRAINING_DATA_PATH + "help_training_data.txt", "Help");
        
        if (allValid) {
            System.out.println("✅ All training data files are valid");
        } else {
            System.out.println("❌ Some training data files have issues");
        }
        
        return allValid;
    }
    
    /**
     * Validate individual training file
     */
    private boolean validateTrainingFile(String filePath, String modelName) {
        try {
            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("❌ " + modelName + " training file not found: " + filePath);
                return false;
            }
            
            long validLines = Files.lines(Paths.get(filePath))
                    .filter(line -> {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) return false;
                        String[] parts = line.split("\\|");
                        return parts.length >= 3; // Minimum format: text|category|response_type
                    })
                    .count();
            
            if (validLines < 10) {
                System.out.println("⚠️  " + modelName + " training file has too few valid samples: " + validLines);
                return false;
            }
            
            System.out.println("✅ " + modelName + " training file valid (" + validLines + " samples)");
            return true;
            
        } catch (IOException e) {
            System.out.println("❌ Error validating " + modelName + " training file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate performance report
     */
    public void generatePerformanceReport() throws IOException {
        System.out.println("\n📊 Generating Performance Report...");
        
        MLModelController controller = new MLModelController();
        controller.loadPreTrainedModels();
        
        // Test performance with various input types
        String[] performanceTests = {
            "show contract 123456",                    // Simple contract query
            "what are the dates for contract 123456", // Date-specific query
            "list parts for contract 123456",         // Simple parts query
            "how many parts in contract 123456",      // Count-specific query
            "how to create contract",                  // Simple help query
            "steps for contract creation",            // Process help query
            "create parts for contract 123456"        // Error case
        };
        
        long totalTime = 0;
        int successCount = 0;
        
        for (String test : performanceTests) {
            long startTime = System.nanoTime();
            String response = controller.processUserInput(test);
            long processingTime = (System.nanoTime() - startTime) / 1000;
            
            totalTime += processingTime;
            
            if (!response.contains("ERROR")) {
                successCount++;
            }
            
            System.out.println("  " + test + " -> " + processingTime + "μs");
        }
        
        double averageTime = (double) totalTime / performanceTests.length;
        double successRate = (double) successCount / performanceTests.length * 100;
        
        System.out.println("\n📈 Performance Summary:");
        System.out.println("  Average Processing Time: " + String.format("%.2f", averageTime) + "μs");
        System.out.println("  Success Rate: " + String.format("%.1f", successRate) + "%");
        System.out.println("  Total Tests: " + performanceTests.length);
        System.out.println("  Framework: Apache OpenNLP");
    }
}
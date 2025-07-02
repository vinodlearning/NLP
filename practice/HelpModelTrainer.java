package view.practice;
import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HelpModelTrainer {
    
    public static void main(String[] args) {
        trainHelpModel();
    }
    
    public static void trainHelpModel() {
        try {
            System.out.println("? Starting Help model training...");
            
            // Create training data if it doesn't exist
            File trainingFile = new File("training/help-training-data.txt");
            if (!trainingFile.exists()) {
                System.out.println("? Generating training data...");
                HelpTrainingDataGenerator.generateHelpTrainingData();
            }
            
            // Load training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(trainingFile);
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, StandardCharsets.UTF_8);
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            
            // Set training parameters
            TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM, 1);
            params.put(TrainingParameters.ITERATIONS_PARAM, 100);
            params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");
            
            // Train the model
            System.out.println("? Training Help classification model...");
            DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            
            // Create models directory if it doesn't exist
            File modelsDir = new File("models");
            if (!modelsDir.exists()) {
                modelsDir.mkdirs();
                System.out.println("? Created models directory");
            }
            
            // Save the model
            File modelFile = new File("models/en-help.bin");
            try (FileOutputStream modelOut = new FileOutputStream(modelFile)) {
                model.serialize(modelOut);
                System.out.println("? Help model saved successfully!");
                System.out.println("? Model file: " + modelFile.getAbsolutePath());
            }
            
            // Test the model
            testHelpModel(model);
            
        } catch (Exception e) {
            System.err.println("? Error training Help model: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testHelpModel(DoccatModel model) {
        System.out.println("\n? Testing Help model...");
        
        DocumentCategorizer categorizer = new DocumentCategorizerME(model);
        
        String[] testQueries = {
            "help",
            "what can you do",
            "how to search contracts",
            "parts help",
            "contract number format",
            "show me examples",
            "not working"
        };
        
        for (String query : testQueries) {
            String[] tokens = query.split("\\s+");
            double[] outcomes = categorizer.categorize(tokens);
            String category = categorizer.getBestCategory(outcomes);
            double confidence = getMaxConfidence(outcomes);
            
            System.out.println(String.format("Query: %-25s -> Category: %-20s (%.2f)", 
                query, category, confidence));
        }
    }
    
    private static double getMaxConfidence(double[] outcomes) {
        double max = 0.0;
        for (double outcome : outcomes) {
            if (outcome > max) {
                max = outcome;
            }
        }
        return max;
    }
}
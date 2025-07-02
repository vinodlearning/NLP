package view.practice;

import opennlp.tools.doccat.*;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class HelpIntentModelTrainer {
    
    private static final Logger logger = Logger.getLogger(HelpIntentModelTrainer.class.getName());
    
    public void trainHelpIntentModel() {
        try {
            logger.info("? Starting Help Intent Model Training...");
            
            // Load training data
            InputStream trainingDataStream = getClass().getClassLoader().getResourceAsStream("help-training-data.txt");
            if (trainingDataStream == null) {
                throw new IOException("Training data file 'help-training-data.txt' not found in resources");
            }
            
            InputStreamFactory inputStreamFactory = new InputStreamFactory() {
                @Override
                public InputStream createInputStream() throws IOException {
                    return getClass().getClassLoader().getResourceAsStream("C:\\JDeveloper\\mywork\\MYChatTest\\ViewController\\src\\view\\practice\\help-training-data.txt");
                }
            };
            
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            
            // Configure training parameters
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, 100);
            params.put(TrainingParameters.CUTOFF_PARAM, 1);
            params.put(AbstractTrainer.ALGORITHM_PARAM, "MAXENT");
            
            // Train the model
            logger.info("? Training model with parameters...");
            DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            
            // Save the model to resources directory
            String resourcesPath = "src/main/resources/help-intent-model.bin";
            File modelFile = new File(resourcesPath);
            
            // Create directories if they don't exist
            modelFile.getParentFile().mkdirs();
            
            try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile))) {
                model.serialize(modelOut);
                logger.info("? Help Intent Model saved to: " + resourcesPath);
            }
            
            // Test the model
            testModel(model);
            
        } catch (Exception e) {
            logger.severe("? Error training Help Intent Model: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testModel(DoccatModel model) {
        logger.info("? Testing trained model...");
        
        DocumentCategorizerME categorizer = new DocumentCategorizerME(model);
        
        String[] testQueries = {
            "how to create a contract",
            "load parts help", 
            "validate parts steps",
            "check error messages",
            "help me create contract",
            "how do I load parts",
            "steps to validate parts",
            "what is the weather" // This should be classified as unknown
        };
        
        for (String query : testQueries) {
            String[] tokens = query.split(" ");
            double[] outcomes = categorizer.categorize(tokens);
            String category = categorizer.getBestCategory(outcomes);
            
            // Get confidence - find the max value in outcomes
            double confidence = 0.0;
            for (double outcome : outcomes) {
                if (outcome > confidence) {
                    confidence = outcome;
                }
            }
            
            logger.info(String.format("Query: '%s' -> Intent: %s (%.2f%%)",
                query, category, confidence * 100));
        }
    }
    
    /**
     * Create sample training data if file doesn't exist
     */
    public void createSampleTrainingData() {
        try {
            String trainingData = 
                "help_create_contract how to create a new contract\n" +
                "help_create_contract steps to create contract\n" +
                "help_create_contract create new contract help\n" +
                "help_create_contract contract creation guide\n" +
                "help_load_parts how to load parts\n" +
                "help_load_parts load parts help\n" +
                "help_load_parts steps to load parts\n" +
                "help_load_parts parts loading guide\n" +
                "help_validate_parts validate parts steps\n" +
                "help_validate_parts how to validate parts\n" +
                "help_validate_parts parts validation help\n" +
                "help_check_errors check error messages\n" +
                "help_check_errors error checking help\n" +
                "help_check_errors how to check errors\n" +
                "unknown what is the weather\n" +
                "unknown hello world\n" +
                "unknown random question\n";
            
            File trainingFile = new File("src/main/resources/help-training-data.txt");
            trainingFile.getParentFile().mkdirs();
            
            try (FileWriter writer = new FileWriter(trainingFile)) {
                writer.write(trainingData);
                logger.info("? Sample training data created at: " + trainingFile.getPath());
            }
            
        } catch (IOException e) {
            logger.severe("? Error creating sample training data: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        HelpIntentModelTrainer trainer = new HelpIntentModelTrainer();
        
        // Create sample training data first
        trainer.createSampleTrainingData();
        
        // Then train the model
        trainer.trainHelpIntentModel();
    }
}

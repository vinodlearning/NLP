package view.practice;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelpTrainingDataGenerator {
    
    public static void main(String[] args) {
        generateHelpTrainingData();
    }
    
    public static void generateHelpTrainingData() {
        List<String> trainingData = new ArrayList<>();
        
        // General help requests
        trainingData.add("help\tgeneral_help");
        trainingData.add("what can you do\tgeneral_help");
        trainingData.add("what can you help me with\tgeneral_help");
        trainingData.add("how do I use this\tgeneral_help");
        trainingData.add("show me help\tgeneral_help");
        trainingData.add("I need help\tgeneral_help");
        trainingData.add("assistance\tgeneral_help");
        trainingData.add("guide me\tgeneral_help");
        trainingData.add("instructions\tgeneral_help");
        trainingData.add("how to use\tgeneral_help");
        
        // Contract help
        trainingData.add("how to search contracts\tcontract_help");
        trainingData.add("contract help\tcontract_help");
        trainingData.add("how to find contract info\tcontract_help");
        trainingData.add("contract commands\tcontract_help");
        trainingData.add("what contract queries can I make\tcontract_help");
        trainingData.add("help with contracts\tcontract_help");
        trainingData.add("contract search help\tcontract_help");
        trainingData.add("how to check contract expiration\tcontract_help");
        trainingData.add("contract status help\tcontract_help");
        trainingData.add("find contracts by customer help\tcontract_help");
        
        // Parts help
        trainingData.add("how to search parts\tparts_help");
        trainingData.add("parts help\tparts_help");
        trainingData.add("how to find part info\tparts_help");
        trainingData.add("parts commands\tparts_help");
        trainingData.add("what parts queries can I make\tparts_help");
        trainingData.add("help with parts\tparts_help");
        trainingData.add("parts search help\tparts_help");
        trainingData.add("how to check failed parts\tparts_help");
        trainingData.add("how to see loaded parts\tparts_help");
        trainingData.add("validation status help\tparts_help");
        
        // Format help
        trainingData.add("what format for contract number\tformat_help");
        trainingData.add("contract number format\tformat_help");
        trainingData.add("part number format\tformat_help");
        trainingData.add("how to format queries\tformat_help");
        trainingData.add("query format help\tformat_help");
        trainingData.add("input format\tformat_help");
        trainingData.add("number format help\tformat_help");
        trainingData.add("correct format\tformat_help");
        
        // Examples help
        trainingData.add("show me examples\texamples_help");
        trainingData.add("query examples\texamples_help");
        trainingData.add("example queries\texamples_help");
        trainingData.add("sample commands\texamples_help");
        trainingData.add("examples\texamples_help");
        trainingData.add("sample queries\texamples_help");
        trainingData.add("show examples\texamples_help");
        
        // Troubleshooting help
        trainingData.add("not working\ttroubleshooting_help");
        trainingData.add("error help\ttroubleshooting_help");
        trainingData.add("troubleshoot\ttroubleshooting_help");
        trainingData.add("fix issue\ttroubleshooting_help");
        trainingData.add("problem\ttroubleshooting_help");
        trainingData.add("not finding results\ttroubleshooting_help");
        trainingData.add("no results\ttroubleshooting_help");
        
        try {
            FileWriter writer = new FileWriter("training/help-training-data.txt");
            for (String line : trainingData) {
                writer.write(line + "\n");
            }
            writer.close();
            System.out.println("? Help training data generated successfully!");
            System.out.println("? File: training/help-training-data.txt");
            System.out.println("? Total samples: " + trainingData.size());
        } catch (IOException e) {
            System.err.println("? Error generating training data: " + e.getMessage());
        }
    }
}
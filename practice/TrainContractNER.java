package view.practice;
import opennlp.tools.namefind.*;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;
import java.io.*;

public class TrainContractNER {
    public static void main(String[] args) throws Exception {
        // Prepare training data
        InputStreamFactory in = new MarkableFileInputStreamFactory(
            new File("C:\\JDeveloper\\mywork\\MYChatTest\\ViewController\\public_html\\models\\contract_ner_train.txt"));
        
        ObjectStream<String> lineStream = new PlainTextByLineStream(in, "UTF-8");
        ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
        
        // Enhanced training parameters
        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.ALGORITHM_PARAM, "MAXENT");
        params.put(TrainingParameters.CUTOFF_PARAM, "1"); // Reduced cutoff
        params.put(TrainingParameters.ITERATIONS_PARAM, "70");
        
        // Train with multiple entity types
        TokenNameFinderModel model = NameFinderME.train(
            "en", 
            null, // null for multiple entity types
            sampleStream, 
            params, 
            new TokenNameFinderFactory()
        );
        
        // Save the model
        try (OutputStream modelOut = new BufferedOutputStream(
            new FileOutputStream("contract-ner-model.bin"))) {
            model.serialize(modelOut);
        }
        
        System.out.println("Model successfully trained and saved to contract-ner-model.bin");
    }
}
package view.practice;



import opennlp.tools.doccat.*;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnhancedHelpModelTrainer extends HelpModelTrainer {
    private static final Logger logger = Logger.getLogger(EnhancedHelpModelTrainer.class.getName());
    
   
    protected Map<String, List<String>> getOriginalTrainingData() {
        Map<String, List<String>> data = new HashMap<>();
        
        // Show Steps Intent - User wants to learn how to create contract
        data.put("show_contract_steps", Arrays.asList(
            "show me steps to create contract",
            "show steps to create contract", 
            "steps to create contract",
            "how to create a contract step by step",
            "contract creation steps",
            "guide me through contract creation",
            "what are the steps for contract creation",
            "step by step contract creation",
            "contract creation process",
            "show contract creation procedure",
            "display contract creation steps",
            "list contract creation steps"
        ));
        
        // Create Contract Intent - User wants system to create contract for them
        data.put("create_contract_request", Arrays.asList(
            "create a contract",
            "help to create contract",
            "please create a contract for me",
            "create a contract account number 1012345679",
            "create a contract account 1012345679",
            "make a contract for me",
            "generate a contract",
            "I need you to create a contract",
            "can you create a contract",
            "create contract for account",
            "new contract creation",
            "please make a contract",
            "I want a new contract",
            "set up a contract for me",
            "create contract for customer"
        ));
        
        // General Help
        data.put("general_help", Arrays.asList(
            "help me", "what can you do", "assistance needed", "I need help", "guide me",
            "instructions", "how to use", "what are your capabilities", "show me options",
            "available commands"
        ));
        
        // Contract Help (general)
        data.put("contract_help", Arrays.asList(
            "help with contracts", "contract assistance", "how to search contracts",
            "contract format", "find contract help", "contract number format",
            "contract search guide", "how to find contract status", "contract history help",
            "customer contracts help", "contract details help", "contract examples"
        ));
        
        // Other existing categories...
        data.put("parts_help", Arrays.asList(
            "help with parts", "parts assistance", "how to search parts", "part number format",
            "find parts help", "parts availability help", "parts pricing help"
        ));
        
        data.put("unknown", Arrays.asList(
            "find contract 123456", "search part AB12345-12345678", "contract status",
            "part availability", "customer information", "hello", "thank you", "good morning",
            "weather today", "random question"
        ));
        
        return data;
    }
    
    
    protected String extractEnhancedFeatures(String text) {
      
        StringBuilder features = new StringBuilder(null);
        
        String lowerText = text.toLowerCase();
        
        // Enhanced pattern detection for contract creation intents
        if (lowerText.matches(".*\\bshow\\b.*\\bsteps?\\b.*")) {
            features.append("SHOW_STEPS_PATTERN ");
        }
        
        if (lowerText.matches(".*\\bsteps?\\b.*\\bcreate\\b.*\\bcontract\\b.*")) {
            features.append("STEPS_CREATE_CONTRACT ");
        }
        
        if (lowerText.matches(".*\\bcreate\\b.*\\bcontract\\b.*")) {
            features.append("CREATE_CONTRACT_PATTERN ");
        }
        
        if (lowerText.contains("for me") || lowerText.contains("please")) {
            features.append("REQUEST_ACTION ");
        }
        
        // Account number detection (10 digits)
        if (lowerText.matches(".*\\b\\d{10}\\b.*")) {
            features.append("HAS_ACCOUNT_NUMBER ");
        }
        
        return features.toString().trim();
    }
}

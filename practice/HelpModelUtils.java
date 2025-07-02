package view.practice;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class HelpModelUtils {
    
    // Keywords for different types of requests
    private static final List<String> HOW_KEYWORDS = Arrays.asList(
        "how", "steps", "step", "guide", "instruction", "tutorial", "process"
    );
    
    private static final List<String> CONTRACT_KEYWORDS = Arrays.asList(
        "contract", "agreement", "deal", "terms"
    );
    
    private static final List<String> PARTS_KEYWORDS = Arrays.asList(
        "part", "parts", "component", "load", "inventory"
    );
    
    private static final List<String> CREATION_KEYWORDS = Arrays.asList(
        "create", "make", "generate", "build", "setup", "new", "add"
    );
    
    /**
     * Enhanced detection for steps requests
     */
    public static boolean isStepsRequest(String input) {
        String lowerInput = input.toLowerCase();
        
        boolean hasHowKeyword = HOW_KEYWORDS.stream()
            .anyMatch(lowerInput::contains);
            
        boolean hasSubjectKeyword = CONTRACT_KEYWORDS.stream()
            .anyMatch(lowerInput::contains) || 
            PARTS_KEYWORDS.stream()
            .anyMatch(lowerInput::contains);
            
        return hasHowKeyword && hasSubjectKeyword;
    }
    
    /**
     * Enhanced detection for creation requests
     */
    public static boolean isCreationRequest(String input) {
        String lowerInput = input.toLowerCase();
        
        boolean hasCreationKeyword = CREATION_KEYWORDS.stream()
            .anyMatch(lowerInput::contains);
            
        boolean hasContractKeyword = CONTRACT_KEYWORDS.stream()
            .anyMatch(lowerInput::contains);
            
        // Check for imperative patterns
        Pattern imperativePattern = Pattern.compile("(create|make|generate|build)\\s+(a\\s+)?(contract|agreement)");
        boolean hasImperativePattern = imperativePattern.matcher(lowerInput).find();
        
        return (hasCreationKeyword && hasContractKeyword) || hasImperativePattern;
    }
    
    /**
     * Extract subject from user input
     */
    public static String extractSubject(String input) {
        String lowerInput = input.toLowerCase();
        
        if (CONTRACT_KEYWORDS.stream().anyMatch(lowerInput::contains)) {
            return "contract";
        } else if (PARTS_KEYWORDS.stream().anyMatch(lowerInput::contains)) {
            return "parts";
        }
        
        return "general";
    }
    
    /**
     * Calculate confidence score for classification
     */
    public static double calculateConfidence(String input, String classification) {
        String lowerInput = input.toLowerCase();
        int matchCount = 0;
        int totalKeywords = 0;
        
        List<String> relevantKeywords;
        switch (classification) {
            case "steps":
                relevantKeywords = HOW_KEYWORDS;
                break;
            case "creation":
                relevantKeywords = CREATION_KEYWORDS;
                break;
            case "contract":
                relevantKeywords = CONTRACT_KEYWORDS;
                break;
            case "parts":
                relevantKeywords = PARTS_KEYWORDS;
                break;
            default:
                return 0.5; // Default confidence
        }
        
        totalKeywords = relevantKeywords.size();
        for (String keyword : relevantKeywords) {
            if (lowerInput.contains(keyword)) {
                matchCount++;
            }
        }
        
        return Math.min(0.95, 0.3 + (double) matchCount / totalKeywords * 0.7);
    }
}

package view.practice;
import java.util.*;
import java.util.regex.Pattern;

public class TextPreprocessor {
    
    private final Map<String, String> commonTypos;
    private final Set<String> stopWords;
    private final Pattern specialCharsPattern;
    
    public TextPreprocessor() {
        this.commonTypos = initializeTypoCorrections();
        this.stopWords = initializeStopWords();
        this.specialCharsPattern = Pattern.compile("[^a-zA-Z0-9\\s]");
    }
    
    private Map<String, String> initializeTypoCorrections() {
        Map<String, String> typos = new HashMap<>();
        
        // Contract related typos
        typos.put("crate", "create");
        typos.put("cntrs", "contract");
        typos.put("cntract", "contract");
        typos.put("contrct", "contract");
        typos.put("contrat", "contract");
        typos.put("conract", "contract");
        
        // Parts related typos
        typos.put("prats", "parts");
        typos.put("part", "parts");
        typos.put("uplod", "upload");
        typos.put("uplaod", "upload");
        typos.put("upoad", "upload");
        typos.put("lod", "load");
        typos.put("laod", "load");
        
        // Action related typos
        typos.put("validat", "validate");
        typos.put("valdate", "validate");
        typos.put("chek", "check");
        typos.put("cheack", "check");
        typos.put("creat", "create");
        
        // Help related typos
        typos.put("halp", "help");
        typos.put("hlep", "help");
        typos.put("guid", "guide");
        typos.put("gide", "guide");
        typos.put("step", "steps");
        typos.put("stpes", "steps");
        
        return typos;
    }
    
    private Set<String> initializeStopWords() {
        return new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "will", "with", "me", "my", "i", "you", "your"
        ));
    }
    
    public String preprocessText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        // Convert to lowercase and trim
        String processed = input.toLowerCase().trim();
        
        // Remove special characters except spaces
        processed = specialCharsPattern.matcher(processed).replaceAll(" ");
        
        // Normalize multiple spaces to single space
        processed = processed.replaceAll("\\s+", " ");
        
        // Split into words and process each
        String[] words = processed.split("\\s+");
        List<String> processedWords = new ArrayList<>();
        
        for (String word : words) {
            if (word != null && !word.trim().isEmpty()) {
                // Apply typo corrections
                String correctedWord = commonTypos.getOrDefault(word.trim(), word.trim());
                
                // Keep important words (not stop words for intent classification)
                if (!stopWords.contains(correctedWord) || isImportantWord(correctedWord)) {
                    processedWords.add(correctedWord);
                }
            }
        }
        
        return String.join(" ", processedWords);
    }
    
    private boolean isImportantWord(String word) {
        // Keep certain stop words that might be important for intent classification
        Set<String> importantStopWords = new HashSet<>(Arrays.asList("how", "to", "with"));
        return importantStopWords.contains(word);
    }
    
    public double calculateSimilarity(String text1, String text2) {
        return calculateLevenshteinSimilarity(text1, text2);
    }
    
    private double calculateLevenshteinSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }
        
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) {
            return 1.0;
        }
        
        int distance = calculateLevenshteinDistance(s1, s2);
        return 1.0 - (double) distance / maxLength;
    }
    
    private int calculateLevenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        
        // Create a matrix to store distances
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        // Initialize first row and column
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        // Fill the matrix
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        
        return dp[len1][len2];
    }
    
    public String correctTypos(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        String[] words = input.toLowerCase().split("\\s+");
        List<String> correctedWords = new ArrayList<>();
        
        for (String word : words) {
            String cleanWord = specialCharsPattern.matcher(word).replaceAll("");
            String correctedWord = commonTypos.getOrDefault(cleanWord, word);
            correctedWords.add(correctedWord);
        }
        
        return String.join(" ", correctedWords);
    }
    
    public boolean containsHelpKeywords(String input) {
        if (input == null) return false;
        
        String processed = input.toLowerCase();
        Set<String> helpKeywords = new HashSet<>(Arrays.asList(
            "help", "how", "guide", "steps", "assist", "support", 
            "instruction", "tutorial", "show", "explain", "tell"
        ));
        
        return helpKeywords.stream().anyMatch(processed::contains);
    }
    
    public boolean containsActionKeywords(String input) {
        if (input == null) return false;
        
        String processed = input.toLowerCase();
        Set<String> actionKeywords = new HashSet<>(Arrays.asList(
            "create", "load", "upload", "validate", "check", "export", 
            "import", "generate", "manage", "backup", "contract", "parts"
        ));
        
        return actionKeywords.stream().anyMatch(processed::contains);
    }
    
    public Map<String, Integer> extractKeywordFrequency(String input) {
        Map<String, Integer> frequency = new HashMap<>();
        
        if (input == null || input.trim().isEmpty()) {
            return frequency;
        }
        
        String processed = preprocessText(input);
        String[] words = processed.split("\\s+");
        
        for (String word : words) {
            if (!word.trim().isEmpty()) {
                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
            }
        }
        
        return frequency;
    }
    
    public List<String> extractImportantTerms(String input) {
        List<String> importantTerms = new ArrayList<>();
        
        if (input == null || input.trim().isEmpty()) {
            return importantTerms;
        }
        
        Set<String> domainTerms = new HashSet<>(Arrays.asList(
            "contract", "parts", "validate", "load", "create", "check", 
            "error", "upload", "export", "import", "report", "user", "backup"
        ));
        
        String processed = preprocessText(input);
        String[] words = processed.split("\\s+");
        
        for (String word : words) {
            if (domainTerms.contains(word) && !importantTerms.contains(word)) {
                importantTerms.add(word);
            }
        }
        
        return importantTerms;
    }
}


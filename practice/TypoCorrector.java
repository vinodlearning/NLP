package view.practice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class TypoCorrector {
    private final Map<String, String> commonTypos;
    private final Set<String> validWords;
    private final double SIMILARITY_THRESHOLD = 0.7;

    public TypoCorrector() {
        this.commonTypos = loadCommonTypos();
        this.validWords = loadValidWords();
    }

    public String correct(String input) {
        // Step 1: Apply known common typos
        String corrected = applyKnownTypos(input.toLowerCase());
        
        // Step 2: Tokenize and correct individual words
        String[] words = corrected.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = correctWord(words[i]);
        }
        
        return String.join(" ", words);
    }

    private String applyKnownTypos(String text) {
        for (Map.Entry<String, String> entry : commonTypos.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        return text;
    }

    private String correctWord(String word) {
        // Skip numbers and special terms
        if (word.matches("\\d+") || word.matches(".*\\d.*")) {
            return word;
        }
        
        // Check if word is already valid
        if (validWords.contains(word)) {
            return word;
        }
        
        // Find closest match using Levenshtein distance
        String closest = null;
        double closestScore = 0;
        
        for (String validWord : validWords) {
            double similarity = calculateSimilarity(word, validWord);
            if (similarity > closestScore && similarity >= SIMILARITY_THRESHOLD) {
                closestScore = similarity;
                closest = validWord;
            }
        }
        
        return closest != null ? closest : word;
    }
    
    private double calculateSimilarity(String a, String b) {
        // Implement Levenshtein distance or use a library
        int distance = LevenshteinDistance.getDefaultInstance().apply(a, b);
        return 1 - (double) distance / Math.max(a.length(), b.length());
    }
    
    // Initialize with domain-specific terms
    private Map<String, String> loadCommonTypos() {
        Map<String, String> typos = new HashMap<>();
        typos.put("contrct", "contract");
        typos.put("custmer", "customer");
        typos.put("acount", "account");
        typos.put("shwo", "show");
        typos.put("detials", "details");
        // Add more based on your domain
        return typos;
    }
    
    private Set<String> loadValidWords() {
        Set<String> words = new HashSet<>();
        // Add all valid words in your domain
        words.add("contract");
        words.add("customer");
        words.add("account");
        words.add("show");
        words.add("details");
        // Add more based on your vocabulary
        return words;
    }
}
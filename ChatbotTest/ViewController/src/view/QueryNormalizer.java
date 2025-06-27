package view;

import java.util.*;

import java.util.Set;

import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;

public class QueryNormalizer {
    private PorterStemmer stemmer;
    private SimpleTokenizer tokenizer;
    private Set<String> stopWords;
    
    public QueryNormalizer() {
        initializeNormalizer();
    }
    
    private void initializeNormalizer() {
        try {
            stemmer = new PorterStemmer();
            tokenizer = SimpleTokenizer.INSTANCE;
            initializeStopWords();
            System.out.println("OpenNLP Query Normalizer initialized");
        } catch (Exception e) {
            System.out.println("Warning: OpenNLP not available, using basic normalization");
            stemmer = null;
            tokenizer = null;
        }
    }
    
    private void initializeStopWords() {
        stopWords = new HashSet<>();
        String[] words = {"the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by", "me", "my", "i", "you", "your"};
        Collections.addAll(stopWords, words);
    }
    
    public String normalize(String query) {
        try {
            String normalized = query.toLowerCase().trim();
            
            // Remove extra spaces
            normalized = normalized.replaceAll("\\s+", " ");
            
            // Expand contractions
            normalized = expandContractions(normalized);
            
            // Standardize query patterns
            normalized = standardizePatterns(normalized);
            
            // Apply stemming if available
            if (stemmer != null && tokenizer != null) {
                normalized = applyStemming(normalized);
            }
            
            System.out.println("Normalized query: " + normalized);
            return normalized;
            
        } catch (Exception e) {
            System.out.println("Error in normalization: " + e.getMessage());
            return query.toLowerCase().trim();
        }
    }
    
    private String expandContractions(String text) {
        text = text.replaceAll("can't", "cannot");
        text = text.replaceAll("won't", "will not");
        text = text.replaceAll("n't", " not");
        text = text.replaceAll("'re", " are");
        text = text.replaceAll("'ve", " have");
        text = text.replaceAll("'ll", " will");
        text = text.replaceAll("'d", " would");
        text = text.replaceAll("'m", " am");
        return text;
    }
    
    private String standardizePatterns(String text) {
        // Standardize common query patterns
        text = text.replaceAll("show me", "show");
        text = text.replaceAll("give me", "show");
        text = text.replaceAll("tell me", "show");
        text = text.replaceAll("i want to see", "show");
        text = text.replaceAll("i need", "show");
        text = text.replaceAll("can you", "");
        text = text.replaceAll("please", "");
        text = text.replaceAll("could you", "");
        text = text.replaceAll("would you", "");
        
        // Standardize question words
        text = text.replaceAll("what are", "show");
        text = text.replaceAll("what is", "show");
        text = text.replaceAll("who is", "show");
        text = text.replaceAll("where is", "show");
        text = text.replaceAll("how many", "count");
        
        return text.trim();
    }
    
    private String applyStemming(String text) {
        String[] tokens = tokenizer.tokenize(text);
        StringBuilder stemmed = new StringBuilder();
        
        for (String token : tokens) {
            // Skip stop words and preserve numbers/codes
            if (!stopWords.contains(token) && !token.matches("\\d+") && !token.matches("[A-Z]{2}\\d{3}")) {
                String stemmedToken = stemmer.stem(token);
                stemmed.append(stemmedToken).append(" ");
            } else {
                stemmed.append(token).append(" ");
            }
        }
        
        return stemmed.toString().trim();
    }
}


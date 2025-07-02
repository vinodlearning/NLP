package view.practice;
import opennlp.tools.util.Span;
import opennlp.tools.util.Span;

/**
 * Result of OpenNLP processing pipeline
 */
public class NLPProcessingResult {
    private String originalSentence;
    private String[] tokens;
    private String[] posTags;
    private Span[] entitySpans;
    
    public NLPProcessingResult(String originalSentence, String[] tokens, 
                              String[] posTags, Span[] entitySpans) {
        this.originalSentence = originalSentence;
        this.tokens = tokens;
        this.posTags = posTags;
        this.entitySpans = entitySpans;
    }
    
    // Getters and setters
    public String getOriginalSentence() { return originalSentence; }
    public void setOriginalSentence(String originalSentence) { this.originalSentence = originalSentence; }
    
    public String[] getTokens() { return tokens; }
    public void setTokens(String[] tokens) { this.tokens = tokens; }
    
    public String[] getPosTags() { return posTags; }
    public void setPosTags(String[] posTags) { this.posTags = posTags; }
    
    public Span[] getEntitySpans() { return entitySpans; }
    public void setEntitySpans(Span[] entitySpans) { this.entitySpans = entitySpans; }
    
    /**
     * Get tokens within entity spans
     */
    public String[] getEntityTokens() {
        if (entitySpans == null || entitySpans.length == 0) {
            return new String[0];
        }
        
        java.util.List<String> entityTokens = new java.util.ArrayList<>();
        for (Span span : entitySpans) {
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                if (i < tokens.length) {
                    entityTokens.add(tokens[i]);
                }
            }
        }
        
        return entityTokens.toArray(new String[0]);
    }
}
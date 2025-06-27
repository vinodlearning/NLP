package view;
import java.util.*;

import java.util.Map;

public class ProcessedQuery {
    private Intent intent;
    private Map<String, Object> entities;
    private String normalizedQuery;
    private String originalQuery;
    private String username;
    private double confidenceScore;
    
    public ProcessedQuery(Intent intent, Map<String, Object> entities, 
                         String normalizedQuery, String originalQuery, String username) {
        this.intent = intent;
        this.entities = entities;
        this.normalizedQuery = normalizedQuery;
        this.originalQuery = originalQuery;
        this.username = username;
        this.confidenceScore = 0.0;
    }
    
    // Getters and setters
    public Intent getIntent() { return intent; }
    public void setIntent(Intent intent) { this.intent = intent; }
    
    public Map<String, Object> getEntities() { return entities; }
    public void setEntities(Map<String, Object> entities) { this.entities = entities; }
    
    public String getNormalizedQuery() { return normalizedQuery; }
    public void setNormalizedQuery(String normalizedQuery) { this.normalizedQuery = normalizedQuery; }
    
    public String getOriginalQuery() { return originalQuery; }
    public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
}

package view;



public class Intent {
    private QueryType queryType;
    private double confidence;
    private String intentName;
    
    public Intent(QueryType queryType, double confidence) {
        this.queryType = queryType;
        this.confidence = confidence;
        this.intentName = queryType != null ? queryType.name() : "UNKNOWN";
    }
    
    public Intent(String intentName, double confidence) {
        this.intentName = intentName;
        this.confidence = confidence;
        this.queryType = parseQueryType(intentName);
    }
    
    private QueryType parseQueryType(String intentName) {
        try {
            return QueryType.valueOf(intentName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return QueryType.UNKNOWN;
        }
    }
    
    // Getters - THESE WERE MISSING
    public QueryType getQueryType() {
        return queryType;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public String getIntentName() {
        return intentName;
    }
    
    // Setters
    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
        this.intentName = queryType != null ? queryType.name() : "UNKNOWN";
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public void setIntentName(String intentName) {
        this.intentName = intentName;
        this.queryType = parseQueryType(intentName);
    }
    
    @Override
    public String toString() {
        return "Intent{" +
                "queryType=" + queryType +
                ", confidence=" + confidence +
                ", intentName='" + intentName + '\'' +
                '}';
    }
}
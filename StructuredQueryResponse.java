import java.util.*;

/**
 * Structured Query Response - The specific JSON structure for query processing
 * 
 * This class represents the structured response from query processing systems
 * and can be used independently in other projects for contract portal functionality.
 * 
 * @author Contract Management System
 * @version 1.0
 */
public class StructuredQueryResponse {
    private String originalInput;
    private String correctedInput;
    private StructuredQueryProcessor.QueryType queryType;
    private StructuredQueryProcessor.ActionType actionType;
    private Map<StructuredQueryProcessor.MainProperty, String> mainProperties;
    private List<EntityAttribute> entities;
    private long timestamp;
    private double processingTimeMs;
    
    // Constructors
    public StructuredQueryResponse() {
        this.mainProperties = new HashMap<>();
        this.entities = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public StructuredQueryResponse(String originalInput, String correctedInput) {
        this();
        this.originalInput = originalInput;
        this.correctedInput = correctedInput;
    }
    
    // Getters and Setters
    public String getOriginalInput() { return originalInput; }
    public void setOriginalInput(String originalInput) { this.originalInput = originalInput; }
    
    public String getCorrectedInput() { return correctedInput; }
    public void setCorrectedInput(String correctedInput) { this.correctedInput = correctedInput; }
    
    public StructuredQueryProcessor.QueryType getQueryType() { return queryType; }
    public void setQueryType(StructuredQueryProcessor.QueryType queryType) { this.queryType = queryType; }
    
    public StructuredQueryProcessor.ActionType getActionType() { return actionType; }
    public void setActionType(StructuredQueryProcessor.ActionType actionType) { this.actionType = actionType; }
    
    public Map<StructuredQueryProcessor.MainProperty, String> getMainProperties() { return mainProperties; }
    public void setMainProperties(Map<StructuredQueryProcessor.MainProperty, String> mainProperties) { this.mainProperties = mainProperties; }
    
    public List<EntityAttribute> getEntities() { return entities; }
    public void setEntities(List<EntityAttribute> entities) { this.entities = entities; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public double getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(double processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    // Utility methods
    public void addMainProperty(StructuredQueryProcessor.MainProperty property, String value) {
        this.mainProperties.put(property, value);
    }
    
    public void addEntity(EntityAttribute entity) {
        this.entities.add(entity);
    }
    
    public boolean hasMainProperty(StructuredQueryProcessor.MainProperty property) {
        return this.mainProperties.containsKey(property);
    }
    
    public String getMainPropertyValue(StructuredQueryProcessor.MainProperty property) {
        return this.mainProperties.get(property);
    }
    
    public boolean hasEntities() {
        return !this.entities.isEmpty();
    }
    
    public int getEntityCount() {
        return this.entities.size();
    }
    
    // JSON conversion methods
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"originalInput\": \"").append(escapeJson(originalInput)).append("\",\n");
        json.append("  \"correctedInput\": \"").append(escapeJson(correctedInput)).append("\",\n");
        json.append("  \"queryType\": \"").append(queryType).append("\",\n");
        json.append("  \"actionType\": \"").append(actionType).append("\",\n");
        
        // Main Properties
        json.append("  \"mainProperties\": {\n");
        if (mainProperties.isEmpty()) {
            json.append("    // No main properties detected\n");
        } else {
            int propCount = 0;
            for (Map.Entry<StructuredQueryProcessor.MainProperty, String> entry : mainProperties.entrySet()) {
                String comma = (propCount < mainProperties.size() - 1) ? "," : "";
                json.append("    \"").append(entry.getKey().getPropertyName()).append("\": \"")
                   .append(escapeJson(entry.getValue())).append("\"").append(comma).append("\n");
                propCount++;
            }
        }
        json.append("  },\n");
        
        // Entities
        json.append("  \"entities\": [\n");
        if (entities.isEmpty()) {
            json.append("    // No entities detected\n");
        } else {
            for (int i = 0; i < entities.size(); i++) {
                EntityAttribute entity = entities.get(i);
                String comma = (i < entities.size() - 1) ? "," : "";
                
                json.append("    {\n");
                json.append("      \"attributeName\": \"").append(escapeJson(entity.getAttributeName())).append("\",\n");
                json.append("      \"originalName\": \"").append(escapeJson(entity.getOriginalName())).append("\",\n");
                json.append("      \"operator\": \"").append(entity.getOperator()).append("\",\n");
                json.append("      \"value\": \"").append(escapeJson(entity.getValue())).append("\"\n");
                json.append("    }").append(comma).append("\n");
            }
        }
        json.append("  ],\n");
        
        json.append("  \"timestamp\": ").append(timestamp).append(",\n");
        json.append("  \"processingTimeMs\": ").append(processingTimeMs).append("\n");
        json.append("}");
        
        return json.toString();
    }
    
    private String escapeJson(String input) {
        if (input == null) return "null";
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
    
    // Analysis methods
    public String getAnalysisSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Query Analysis Summary:\n");
        summary.append("- Query Type: ").append(queryType).append("\n");
        summary.append("- Action Type: ").append(actionType).append("\n");
        summary.append("- Main Properties: ").append(mainProperties.size()).append(" detected\n");
        summary.append("- Entities: ").append(entities.size()).append(" attributes with operators\n");
        summary.append("- Processing Time: ").append(processingTimeMs).append("ms\n");
        
        if (!mainProperties.isEmpty()) {
            summary.append("\nMain Properties Detected:\n");
            for (Map.Entry<StructuredQueryProcessor.MainProperty, String> entry : mainProperties.entrySet()) {
                summary.append("  - ").append(entry.getKey().getPropertyName()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        if (!entities.isEmpty()) {
            summary.append("\nEntities Detected:\n");
            for (EntityAttribute entity : entities) {
                summary.append("  - ").append(entity.getAttributeName()).append(" (").append(entity.getOperator()).append(")\n");
            }
        }
        
        return summary.toString();
    }
    
    @Override
    public String toString() {
        return "StructuredQueryResponse{" +
                "originalInput='" + originalInput + '\'' +
                ", correctedInput='" + correctedInput + '\'' +
                ", queryType=" + queryType +
                ", actionType=" + actionType +
                ", mainProperties=" + mainProperties.size() +
                ", entities=" + entities.size() +
                ", timestamp=" + timestamp +
                ", processingTimeMs=" + processingTimeMs +
                '}';
    }
}
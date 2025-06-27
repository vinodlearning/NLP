package view;
import java.util.*;
public class ConversationContext {
    private List<ContextInteraction> interactions;
    private String lastContractNumber;
    private String lastPartNumber;
    private String lastUsername;
    private String lastMentionedEntity;
    private Intent lastIntent;
    private Map<String, Object> lastEntities;
    private long lastUpdateTime;
    
    public ConversationContext() {
        this.interactions = new ArrayList<>();
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public void addInteraction(Intent intent, Map<String, Object> entities) {
        ContextInteraction interaction = new ContextInteraction(intent, entities, System.currentTimeMillis());
        interactions.add(interaction);
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public void removeOldestInteraction() {
        if (!interactions.isEmpty()) {
            interactions.remove(0);
        }
    }
    
    public int getInteractionCount() {
        return interactions.size();
    }
    
    public String getLastMentionedEntity() {
        return lastMentionedEntity;
    }
    
    public void setLastMentionedEntity(String entity) {
        this.lastMentionedEntity = entity;
    }
    
    // Getters and setters
    public String getLastContractNumber() { return lastContractNumber; }
    public void setLastContractNumber(String contractNumber) { this.lastContractNumber = contractNumber; }
    
    public String getLastPartNumber() { return lastPartNumber; }
    public void setLastPartNumber(String partNumber) { this.lastPartNumber = partNumber; }
    
    public String getLastUsername() { return lastUsername; }
    public void setLastUsername(String username) { this.lastUsername = username; }
    
    public Intent getLastIntent() { return lastIntent; }
    public void setLastIntent(Intent intent) { this.lastIntent = intent; }
    
    public Map<String, Object> getLastEntities() { return lastEntities; }
    public void setLastEntities(Map<String, Object> entities) { this.lastEntities = entities; }
    
    public long getLastUpdateTime() { return lastUpdateTime; }
    
    private static class ContextInteraction {
        private Intent intent;
        private Map<String, Object> entities;
        private long timestamp;
        
        public ContextInteraction(Intent intent, Map<String, Object> entities, long timestamp) {
            this.intent = intent;
            this.entities = new HashMap<>(entities);
            this.timestamp = timestamp;
        }
        
        // Getters
        public Intent getIntent() { return intent; }
        public Map<String, Object> getEntities() { return entities; }
        public long getTimestamp() { return timestamp; }
    }
}

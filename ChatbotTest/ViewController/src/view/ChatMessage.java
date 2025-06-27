package view;



import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String message;
    private String sender; // "user", "bot", "system"
    private Date timestamp;
    private String intent;
    private double confidence;
    private Map<String, String> entities;
    
    public ChatMessage() {
        this.timestamp = new Date();
    }
    
    public ChatMessage(String message, String sender) {
        this();
        this.message = message;
        this.sender = sender;
    }
        // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }


    public void setEntities(Map<String, String> entities) {
        this.entities = entities;
    }

    public Map<String, String> getEntities() {
        return entities;
    }
    // Utility methods
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(timestamp);
    }
    
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        return sdf.format(timestamp);
    }
    
    public String getFormattedConfidence() {
        return String.format("%.1f%%", confidence * 100);
    }
    
    public boolean isUserMessage() {
        return "user".equals(sender);
    }
    
    public boolean isBotMessage() {
        return "bot".equals(sender);
    }
    
    public boolean isSystemMessage() {
        return "system".equals(sender);
    }
    
    public boolean hasHighConfidence() {
        return confidence >= 0.7;
    }
    
    public boolean hasEntities() {
        return entities != null && entities.values().stream()
            .anyMatch(list -> list != null && !list.isEmpty());
    }
    
    public String getSenderDisplayName() {
        switch (sender) {
            case "user": return "You";
            case "bot": return "AI Assistant";
            case "system": return "System";
            default: return "Unknown";
        }
    }
    
    public String getCssClass() {
        switch (sender) {
            case "user": return "chat-message-user";
            case "bot": return "chat-message-bot";
            case "system": return "chat-message-system";
            default: return "chat-message-default";
        }
    }
}
 // Get

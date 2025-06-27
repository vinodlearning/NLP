package view;

import java.util.*;

public class User {
    private String username;
    private String sessionId;
    private List<ChatMessage> chatMessages;
    private Map<String, Object> userPreferences;
    private Date lastActivity;
    
    public User() {
        this.chatMessages = new ArrayList<>();
        this.userPreferences = new HashMap<>();
        this.lastActivity = new Date();
    }
    
    public User(String username) {
        this();
        this.username = username;
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public List<ChatMessage> getChatMessages() { return chatMessages; }
    public void setChatMessages(List<ChatMessage> chatMessages) { this.chatMessages = chatMessages; }
    
    public Map<String, Object> getUserPreferences() { return userPreferences; }
    public void setUserPreferences(Map<String, Object> userPreferences) { this.userPreferences = userPreferences; }
    
    public Date getLastActivity() { return lastActivity; }
    public void setLastActivity(Date lastActivity) { this.lastActivity = lastActivity; }
    
    // Helper methods
    public void addChatMessage(ChatMessage message) {
        this.chatMessages.add(message);
        this.lastActivity = new Date();
    }
    
    public void clearChatHistory() {
        this.chatMessages.clear();
    }
}
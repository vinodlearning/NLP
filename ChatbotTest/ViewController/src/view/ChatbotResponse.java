package view;

import java.util.*;

public class ChatbotResponse {
    private boolean success;
    private String message;
    private double confidence;
    private Map<String, Object> data;
    private List<String> suggestions;
    private String errorMessage;
    private String originalQuery;
    
    // Private constructor to enforce factory method usage
    private ChatbotResponse() {
        this.data = new HashMap<>();
        this.suggestions = new ArrayList<>();
    }
    
    // Factory method for successful responses
    public static ChatbotResponse success(String message, double confidence) {
        ChatbotResponse response = new ChatbotResponse();
        response.success = true;
        response.message = message;
        response.confidence = confidence;
        return response;
    }
    
    // Factory method for error responses
    public static ChatbotResponse error(String errorMessage) {
        ChatbotResponse response = new ChatbotResponse();
        response.success = false;
        response.errorMessage = errorMessage;
        response.message = "Error: " + errorMessage;
        response.confidence = 0.0;
        return response;
    }
    
    // Factory method for unknown query responses
    public static ChatbotResponse unknown(String originalQuery) {
        ChatbotResponse response = new ChatbotResponse();
        response.success = false;
        response.originalQuery = originalQuery;
        response.message = "I'm sorry, I don't understand that query. Could you please rephrase it?";
        response.confidence = 0.0;
        return response;
    }
    
    // Method to add data to the response
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
    
    // Method to add suggestions
    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getOriginalQuery() {
        return originalQuery;
    }
    
    // Setters (if needed)
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    @Override
    public String toString() {
        return "ChatbotResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", confidence=" + confidence +
                ", data=" + data +
                ", suggestions=" + suggestions +
                '}';
    }
}
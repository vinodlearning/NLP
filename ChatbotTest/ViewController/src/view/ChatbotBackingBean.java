package view;


import java.util.*;
import javax.faces.context.FacesContext;
import java.io.Serializable;

public class ChatbotBackingBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String currentInput;
    private boolean isProcessing = false;
    private EnhancedNLPProcessor nlpProcessor;
    private QueryOrchestrator queryOrchestrator;
    private String sessionId;
    private ChatModelServiceImpl chatModelService;
    private List<ChatMessage> chatMessages; // Using your ChatMessage class
    private String currentUser;
    
    public ChatbotBackingBean() {
        initializeComponents();
        this.sessionId = null;//FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
        this.chatModelService = new ChatModelServiceImpl();
        this.chatMessages = new ArrayList<>();
        this.currentUser = getCurrentUserFromSession(); // Get from session/security context
        
        // Add welcome message
        addSystemMessage("Welcome! I'm your AI assistant. How can I help you today?");
    }
    
    private void initializeComponents() {
        try {
            this.nlpProcessor = new EnhancedNLPProcessor();
            this.queryOrchestrator = new QueryOrchestrator();
            System.out.println("Chatbot components initialized successfully");
        } catch (Exception e) {
            System.out.println("Error initializing chatbot components: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendMessage() {
        if (currentInput == null || currentInput.trim().isEmpty() || isProcessing) {
            return;
        }
        
        isProcessing = true;
        String userInput = currentInput.trim();
        currentInput = ""; // Clear input immediately
        
        try {
            System.out.println("Processing user input: " + userInput);
            
            // Add user message to chat using your ChatMessage structure
            ChatMessage userMessage = new ChatMessage(userInput, "user");
            userMessage.setTimestamp(new Date());
            chatMessages.add(userMessage);
            
            // Process with NLP pipeline
            ParsedQuery parsedQuery = nlpProcessor.processQuery(userInput);
            
            // Get response from query orchestrator
            ChatbotResponse response = queryOrchestrator.processQuery(parsedQuery);
            
            // Add bot response to chat using your ChatMessage structure
            ChatMessage botMessage = new ChatMessage(response.getMessage(), "bot");
            botMessage.setTimestamp(new Date());
            botMessage.setIntent(parsedQuery.getQueryType().toString());
            botMessage.setConfidence(response.getConfidence());
            botMessage.setEntities(parsedQuery.getQueryParameters());
            chatMessages.add(botMessage);
            
            // Log interaction for audit
            logInteraction(userInput, response, parsedQuery);
            
        } catch (Exception e) {
            System.out.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message to chat using your ChatMessage structure
            ChatMessage errorMessage = new ChatMessage(
                "I'm sorry, I encountered an error processing your request. Please try again.", 
                "system"
            );
            errorMessage.setTimestamp(new Date());
            errorMessage.setIntent("ERROR");
            errorMessage.setConfidence(0.0);
            chatMessages.add(errorMessage);
            
        } finally {
            isProcessing = false;
        }
    }
    
    private void logInteraction(String userInput, ChatbotResponse response, ParsedQuery parsedQuery) {
        try {
            System.out.println("=== CHATBOT INTERACTION LOG ===");
            System.out.println("Session: " + sessionId);
            System.out.println("User: " + currentUser);
            System.out.println("Input: " + userInput);
            System.out.println("Query Type: " + parsedQuery.getQueryType());
            System.out.println("Action Type: " + parsedQuery.getActionType());
            System.out.println("Contract: " + parsedQuery.getContractNumber());
            System.out.println("Part: " + parsedQuery.getPartNumber());
            System.out.println("Account: " + parsedQuery.getAccountNumner());
            System.out.println("User Name: " + parsedQuery.getUserName());
            System.out.println("Confidence: " + response.getConfidence());
            System.out.println("Success: " + response.isSuccess());
            System.out.println("Response Type: " + response.getMessage());
            System.out.println("Entities: " + parsedQuery.getQueryParameters());
            System.out.println("Timestamp: " + new Date());
            System.out.println("===============================");
        } catch (Exception e) {
            System.out.println("Error logging interaction: " + e.getMessage());
        }
    }
    
    // Helper method to add system messages
    private void addSystemMessage(String message) {
        ChatMessage systemMessage = new ChatMessage(message, "system");
        systemMessage.setTimestamp(new Date());
        systemMessage.setIntent("SYSTEM");
        systemMessage.setConfidence(1.0);
        chatMessages.add(systemMessage);
    }
    
    // Method to clear chat history
    public void clearChat() {
        try {
            chatMessages.clear();
            addSystemMessage("Chat history cleared. How can I help you?");
            System.out.println("Chat history cleared for session: " + sessionId);
        } catch (Exception e) {
            System.out.println("Error clearing chat: " + e.getMessage());
        }
    }
    
    // Method to get all chat messages
    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }
    
    // Method to get recent chat messages (last 50)
    public List<ChatMessage> getRecentChatMessages() {
        if (chatMessages.size() <= 50) {
            return chatMessages;
        }
        return chatMessages.subList(chatMessages.size() - 50, chatMessages.size());
    }
    
    // Method to check if there are any messages
    public boolean hasMessages() {
        return !chatMessages.isEmpty();
    }
    
    // Method to get message count
    public int getMessageCount() {
        return chatMessages.size();
    }
    
    // Method to get user message count
    public int getUserMessageCount() {
        return (int) chatMessages.stream().filter(ChatMessage::isUserMessage).count();
    }
    
    // Method to get bot message count
    public int getBotMessageCount() {
        return (int) chatMessages.stream().filter(ChatMessage::isBotMessage).count();
    }
    
    // Method to get last bot message confidence
    public double getLastBotConfidence() {
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ChatMessage msg = chatMessages.get(i);
            if (msg.isBotMessage()) {
                return msg.getConfidence();
            }
        }
        return 0.0;
    }
    
    // Method to get last bot message confidence formatted
    public String getLastBotConfidenceFormatted() {
        for (int i = chatMessages.size() - 1; i >= 0; i--) {
            ChatMessage msg = chatMessages.get(i);
            if (msg.isBotMessage()) {
                return msg.getFormattedConfidence();
            }
        }
        return "0.0%";
    }
    
    // Method to get messages with high confidence
    public List<ChatMessage> getHighConfidenceMessages() {
        return chatMessages.stream()
            .filter(ChatMessage::hasHighConfidence)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Method to get messages with entities
    public List<ChatMessage> getMessagesWithEntities() {
        return chatMessages.stream()
            .filter(ChatMessage::hasEntities)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Method to export chat history
    public String exportChatHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chat History Export\n");
        sb.append("Session: ").append(sessionId).append("\n");
        sb.append("User: ").append(currentUser).append("\n");
        sb.append("Export Date: ").append(new Date()).append("\n");
        sb.append("Total Messages: ").append(getMessageCount()).append("\n");
        sb.append("User Messages: ").append(getUserMessageCount()).append("\n");
        sb.append("Bot Messages: ").append(getBotMessageCount()).append("\n");
        sb.append("=================================\n\n");
        
        for (ChatMessage message : chatMessages) {
            sb.append("[").append(message.getSenderDisplayName()).append("] ");
            sb.append(message.getFormattedDate()).append("\n");
            sb.append(message.getMessage()).append("\n");
            
            if (message.getIntent() != null) {
                sb.append("Intent: ").append(message.getIntent()).append("\n");
            }
            
            if (message.getConfidence() > 0) {
                sb.append("Confidence: ").append(message.getFormattedConfidence()).append("\n");
            }
            
            if (message.hasEntities()) {
                sb.append("Entities: ").append(message.getEntities()).append("\n");
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    // Method to get current user from session/security context
    private String getCurrentUserFromSession() {
        try {
            // Try to get from JSF session
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                Object user = context.getExternalContext().getSessionMap().get("currentUser");
                if (user != null) {
                    return user.toString();
                }
            }
            
            // Fallback to default
            return "defaultUser";
            
        } catch (Exception e) {
            System.out.println("Error getting current user: " + e.getMessage());
            return "anonymousUser";
        }
    }
    
    // Method to restart conversation
    public void restartConversation() {
        clearChat();
        addSystemMessage("Conversation restarted. What would you like to know?");
    }
    
    // Method to get conversation statistics
    public Map<String, Object> getConversationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", getMessageCount());
        stats.put("userMessages", getUserMessageCount());
        stats.put("botMessages", getBotMessageCount());
        stats.put("systemMessages", chatMessages.stream().filter(ChatMessage::isSystemMessage).count());
        stats.put("highConfidenceMessages", getHighConfidenceMessages().size());
        stats.put("messagesWithEntities", getMessagesWithEntities().size());
        stats.put("averageConfidence", chatMessages.stream()
            .filter(ChatMessage::isBotMessage)
            .mapToDouble(ChatMessage::getConfidence)
            .average().orElse(0.0));
        stats.put("sessionDuration", System.currentTimeMillis() - 
            (chatMessages.isEmpty() ? System.currentTimeMillis() : chatMessages.get(0).getTimestamp().getTime()));
        
        return stats;
    }
    
    // Getters and Setters
    public String getCurrentInput() { 
        return currentInput; 
    }
    
    public void setCurrentInput(String currentInput) { 
        this.currentInput = currentInput; 
    }
    
    public boolean isProcessing() { 
        return isProcessing; 
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
    
    public EnhancedNLPProcessor getNlpProcessor() {
        return nlpProcessor;
    }
    
    public QueryOrchestrator getQueryOrchestrator() {
        return queryOrchestrator;
    }
    
    public ChatModelServiceImpl getChatModelService() {
        return chatModelService;
    }
}
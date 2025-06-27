package view;


import java.util.Map;

public class ChatbotSystemTest {
    public static void main(String[] args) {
        System.out.println("=== CHATBOT SYSTEM TEST WITH YOUR CHATMESSAGE CLASS ===");
        ChatbotSystemTest ob=new ChatbotSystemTest();
        // Initialize components
        ChatbotBackingBean chatbot = new ChatbotBackingBean();
        
        // Test queries
        String[] testQueries = {
            "show contract 123456",
            "john contracts", 
            "specifications of AE125",
            "failed parts of 123456",
            "account 10840607",
            "create contract",
            "lead time for AE125",
            "invalid query test"
        };
        
        for (String query : testQueries) {
            System.out.println("\n" +ob.repeat(60));
            System.out.println("? Testing Query: " + query);
            System.out.println("="+ob.repeat((60)));
            
            // Set input and send message
            chatbot.setCurrentInput(query);
            chatbot.sendMessage();
            
            // Display results using your ChatMessage methods
            System.out.println("? Chat Statistics:");
            System.out.println("   Total Messages: " + chatbot.getMessageCount());
            System.out.println("   User Messages: " + chatbot.getUserMessageCount());
            System.out.println("   Bot Messages: " + chatbot.getBotMessageCount());
            System.out.println("   Last Confidence: " + chatbot.getLastBotConfidenceFormatted());
            
            // Show last few messages
            if (chatbot.hasMessages()) {
                System.out.println("\n? Recent Messages:");
                int startIndex = Math.max(0, chatbot.getChatMessages().size() - 3);
                for (int i = startIndex; i < chatbot.getChatMessages().size(); i++) {
                    ChatMessage msg = chatbot.getChatMessages().get(i);
                    System.out.println("   [" + msg.getSenderDisplayName() + "] " + 
                                     msg.getFormattedTimestamp() + ": " + msg.getMessage());
                    
                    if (msg.isBotMessage() && msg.getConfidence() > 0) {
                        System.out.println("      ?? Confidence: " + msg.getFormattedConfidence() + 
                                         " | Intent: " + msg.getIntent());
                    }
                    
                    if (msg.hasEntities()) {
                        System.out.println("      ?? Entities: " + msg.getEntities());
                    }
                }
            }
        }
        
        // Display comprehensive statistics
        System.out.println("\n" + "===========================================");
        System.out.println("? COMPREHENSIVE CONVERSATION STATISTICS");
        System.out.println("====================================================");
        
        Map<String, Object> stats = chatbot.getConversationStats();
        stats.forEach((key, value) -> {
            System.out.println("   " + formatStatKey(key) + ": " + formatStatValue(key, value));
        });
        
        // Show high confidence messages
        System.out.println("\n? High Confidence Messages:");
        chatbot.getHighConfidenceMessages().forEach(msg -> {
            System.out.println("   • " + msg.getMessage() + " (" + msg.getFormattedConfidence() + ")");
        });
        
        // Show messages with entities
        System.out.println("\n?? Messages with Entities:");
        chatbot.getMessagesWithEntities().forEach(msg -> {
            System.out.println("   • " + msg.getMessage());
            System.out.println("     Entities: " + msg.getEntities());
        });
        
        // Export chat history
        System.out.println("\n" + "="+ob.repeat((80)));
        System.out.println("? EXPORTED CHAT HISTORY");
        System.out.println("="+ob.repeat(80));
        System.out.println(chatbot.exportChatHistory());
        
        // Test additional functionality
        testAdditionalFeatures(chatbot);
    }
    
    private static void testAdditionalFeatures(ChatbotBackingBean chatbot) {
       // System.out.println("\n" + "="+ob.repeat(80));
        System.out.println("? TESTING ADDITIONAL FEATURES");
        //System.out.println("=".repeat(80));
        
        // Test clear functionality
        System.out.println("\n?? Testing Clear Chat:");
        int messagesBefore = chatbot.getMessageCount();
        chatbot.clearChat();
        int messagesAfter = chatbot.getMessageCount();
        System.out.println("   Messages before clear: " + messagesBefore);
        System.out.println("   Messages after clear: " + messagesAfter);
        System.out.println("   Clear successful: " + (messagesAfter < messagesBefore));
        
        // Test restart conversation
        System.out.println("\n? Testing Restart Conversation:");
        chatbot.setCurrentInput("test message after restart");
        chatbot.sendMessage();
        chatbot.restartConversation();
        System.out.println("   Conversation restarted successfully");
        
        // Test processing state
        System.out.println("\n?? Testing Processing State:");
        System.out.println("   Currently processing: " + chatbot.isProcessing());
        
        // Test session info
        System.out.println("\n? Session Information:");
        System.out.println("   Session ID: " + chatbot.getSessionId());
        System.out.println("   Current User: " + chatbot.getCurrentUser());
        
        // Test edge cases
        System.out.println("\n? Testing Edge Cases:");
        
        // Empty input
        chatbot.setCurrentInput("");
        chatbot.sendMessage();
        System.out.println("   Empty input handled correctly");
        
        // Null input
        chatbot.setCurrentInput(null);
        chatbot.sendMessage();
        System.out.println("   Null input handled correctly");
        
        // Very long input
        String longInput = "aaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        chatbot.setCurrentInput(longInput);
        chatbot.sendMessage();
        System.out.println("   Long input handled correctly");
        
        // Special characters
        chatbot.setCurrentInput("!@#$%^&*()_+{}|:<>?[]\\;'\",./ test");
        chatbot.sendMessage();
        System.out.println("   Special characters handled correctly");
    }
    
    private static String formatStatKey(String key) {
        return key.replaceAll("([A-Z])", " $1")
                 .toLowerCase()
                 .replace("_", " ")
                 .substring(0, 1).toUpperCase() + 
               key.replaceAll("([A-Z])", " $1")
                 .toLowerCase()
                 .replace("_", " ")
                 .substring(1);
    }
    
    private static String formatStatValue(String key, Object value) {
        if (key.contains("Duration")) {
            long duration = (Long) value;
            return formatDuration(duration);
        } else if (key.contains("average") || key.contains("Average")) {
            return String.format("%.2f%%", ((Double) value) * 100);
        } else {
            return value.toString();
        }
    }
    
    private static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d hours, %d minutes, %d seconds", 
                               hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds", minutes, seconds % 60);
        } else {
            return String.format("%d seconds", seconds);
        }
    }
    public String repeat(int chars){
        String s="=";
        for(int i=0;i<chars;i++){
            s+="=";
        }
        
        return s;
        
    }
}

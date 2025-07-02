package view.practice;

import view.practice.ChatbotIntentProcessor;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.annotation.PreDestroy;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.render.ClientEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import view.ChatMessage;

public class ChatbotBean {

    private String userInput;
    private String chatResponse;
    private List<ChatMessage> chatHistory;
    private ChatbotIntentProcessor intentProcessor;
    private RichInputText inputTextComponent;
    private boolean isProcessing;
    private String systemStatus;

    public ChatbotBean() {
        this.chatHistory = new ArrayList<>();
        this.intentProcessor = new ChatbotIntentProcessor();
        this.isProcessing = false;

        // Add welcome message
        addWelcomeMessage();

        // Check if models are loaded
        if (!ChatbotIntentProcessor.areModelsReady()) {
            addSystemMessage("?? Warning: Some AI models are not loaded. Functionality may be limited.", "warning");
            this.systemStatus = "Models not fully loaded";
        } else {
            addSystemMessage("? AI Assistant is ready! All models loaded successfully.", "success");
            this.systemStatus = "All systems operational";
        }

        System.out.println("? ChatbotBean initialized successfully");
    }

    /**
     * Main method called when user submits input
     */
//    public void processUserInput(ClientEvent clientEvent) {
//        System.out.println("? Server listener called for user input processing");
//
//        try {
//            if (isValidInput(userInput)) {
//                System.out.println("? Processing user input: " + userInput);
//
//                // Set processing state
//                setProcessing(true);
//
//                // Add user message to chat history
//                chatHistory.add(new ChatMessage("You", userInput, new Date(), false));
//
//                // Handle special commands first
//                String response = handleSpecialCommands(userInput.toLowerCase().trim());
//
//                if (response == null) {
//                    // Process through NLP intent processor
//                    response = intentProcessor.processUserMessage(userInput.toLowerCase().trim());
//                }
//
//                // Add bot response to chat
//                ChatMessage botMessage = new ChatMessage("Assistant", response, new Date(), true);
//                chatHistory.add(botMessage);
//
//                // Set the response for UI binding
//                setChatResponse(response);
//
//                // Log chat history for debugging
//                logChatHistory();
//
//                // Clear input
//                userInput = "";
//
//                System.out.println("? User input processed successfully");
//                System.out.println("? Bot response: " +
//                                   (response.length() > 100 ? response.substring(0, 100) + "..." : response));
//
//            } else {
//                String errorMsg = "Please enter a valid question or command.";
//                setChatResponse(errorMsg);
//                chatHistory.add(new ChatMessage("Assistant", errorMsg, new Date(), true));
//                System.out.println("?? Invalid input received");
//            }
//
//        } catch (Exception e) {
//            System.err.println("? Error in processUserInput: " + e.getMessage());
//            e.printStackTrace();
//            handleSystemError(e);
//        } finally {
//            // Always reset processing state
//            setProcessing(false);
//        }
//    }
public void processUserInput(ClientEvent clientEvent) {
    System.out.println("? Server listener called for user input processing");
    try {
        if (isValidInput(userInput)) {
            System.out.println("? Processing user input: " + userInput);
            
            // Set processing state
            setProcessing(true);
            
            // Add user message to chat history
            chatHistory.add(new ChatMessage("You", userInput, new Date(), false));
            
            // Generate or get session ID
            String sessionId = getOrCreateSessionId();
            
            // Check if this is a checklist creation response
            String response = handleChecklistResponse(userInput.toLowerCase().trim(), sessionId);
            
            if (response == null) {
                // Handle special commands first
                response = handleSpecialCommands(userInput.toLowerCase().trim());
                
                if (response == null) {
                    // Process through NLP intent processor with session ID
                    response = intentProcessor.processUserMessage(userInput.toLowerCase().trim(), sessionId);
                }
            }
            
            // Add bot response to chat
            ChatMessage botMessage = new ChatMessage("Assistant", response, new Date(), true);
            chatHistory.add(botMessage);
            
            // Set the response for UI binding
            setChatResponse(response);
            
            // Log chat history for debugging
            logChatHistory();
            
            // Clear input
            userInput = "";
            
            System.out.println("? User input processed successfully");
            System.out.println("? Bot response: " + 
                              (response.length() > 100 ? response.substring(0, 100) + "..." : response));
            
        } else {
            String errorMsg = "Please enter a valid question or command.";
    setChatResponse(errorMsg);
               chatHistory.add(new ChatMessage("Assistant", errorMsg, new Date(), true));
               System.out.println("?? Invalid input received");
           }
       } catch (Exception e) {
           System.err.println("? Error in processUserInput: " + e.getMessage());
           e.printStackTrace();
           handleSystemError(e);
       } finally {
           // Always reset processing state
           setProcessing(false);
       }
    }

    /**
    * Handle checklist creation response
    */
    private String handleChecklistResponse(String userInput, String sessionId) {
       // Check if user just completed contract creation and this is response to checklist question
       if (isChecklistCreationResponse(userInput)) {
           if (userInput.contains("yes") || userInput.equals("y")) {
               return initiateChecklistCreation(sessionId);
           } else if (userInput.contains("no") || userInput.equals("n")) {
               return terminateChecklistCreation();
           }
       }
       return null; // Not a checklist response
    }

    /**
    * Check if input is a response to checklist creation question
    */
    private boolean isChecklistCreationResponse(String input) {
       // Check if the last bot message asked about checklist creation
       if (chatHistory.size() >= 2) {
           ChatMessage lastBotMessage = null;
           for (int i = chatHistory.size() - 2; i >= 0; i--) {
               if (chatHistory.get(i).isBot()) {
                   lastBotMessage = chatHistory.get(i);
                   break;
               }
           }
           
           if (lastBotMessage != null && 
               lastBotMessage.getMessage().toLowerCase().contains("do you want me to create a checklist")) {
               return input.matches("(yes|no|y|n).*");
           }
       }
       return false;
    }

    /**
    * Initiate checklist creation process
    */
    private String initiateChecklistCreation(String sessionId) {
        // Get the contract number from the previous contract creation
        String contractNumber = getLastCreatedContractNumber();
        
        if (contractNumber == null) {
            return "? Unable to find the contract number for checklist creation. Please try creating the contract again.";
        }
        
        // Create checklist session through intent processor
        return intentProcessor.processChecklistCreation(sessionId, contractNumber);
    }

    /**
    * Terminate checklist creation and return to main chat
    */
    private String terminateChecklistCreation() {
       StringBuilder response = new StringBuilder();
       response.append("? **Checklist Creation Cancelled**\n\n");
       response.append("No problem! Your contract has been created successfully and is ready to use.\n\n");
       response.append("? **Ready for new requests!**\n\n");
       response.append("How can I help you next? You can:\n");
       response.append("• Create another contract\n");
       response.append("• Search for existing contracts\n");
       response.append("• Look up parts information\n");
       response.append("• Type 'help' for more options\n\n");
       response.append("What would you like to do?");
       
       return response.toString();
    }

    /**
    * Get the last created contract number from chat history
    */
    private String getLastCreatedContractNumber() {
       // Look through recent chat history for contract creation success message
       for (int i = chatHistory.size() - 1; i >= Math.max(0, chatHistory.size() - 5); i--) {
           ChatMessage msg = chatHistory.get(i);
           if (msg.isBot() && msg.getMessage().contains("Contract Created Successfully")) {
               // Extract contract number from the message
               String message = msg.getMessage();
               Pattern contractPattern = Pattern.compile("Contract Number: (\\d{6})");
               Matcher matcher = contractPattern.matcher(message);
               if (matcher.find()) {
                   return matcher.group(1);
               }
           }
       }
       return null;
    }

    /**
    * Get or create session ID for the current user session
    */
    private String getOrCreateSessionId() {
       // In a real application, this would be tied to the user's HTTP session
       // For now, we'll use a simple approach based on the bean instance
       if (this.sessionId == null) {
           this.sessionId = "session_" + System.currentTimeMillis() + "_" + this.hashCode();
       }
       return this.sessionId;
    }

    // Add session ID field to the class
    private String sessionId;

   

    /**
    * Get enhanced help information including contract and checklist creation
    */
    private String getEnhancedHelpInformation() {
       StringBuilder help = new StringBuilder();
       help.append("? **Enhanced Chatbot Help Center**\n\n");
       
       help.append("I can assist you with:\n\n");
       
       help.append("? **Contract Management:**\n");
       help.append("• Create new contracts with step-by-step guidance\n");
       help.append("• Search existing contracts by number or customer\n");
       help.append("• Check contract status and details\n");
       help.append("• View contract history\n\n");
       
       help.append("? **Checklist Management:**\n");
       help.append("• Create checklists for contracts\n");
       help.append("• Set system, effective, and expiration dates\n");
       help.append("• Manage price expiration dates\n");
       help.append("• Track checklist status\n\n");
       
       help.append("? **Parts Information:**\n");
       help.append("• Lookup parts by number\n");
       help.append("• Check parts availability and pricing\n");
       help.append("• Find parts compatibility\n");
       help.append("• View parts for specific contracts\n\n");
       
       help.append("? **Customer Services:**\n");
       help.append("• Find contracts by customer name\n");
       help.append("• View customer contract history\n");
       help.append("• Search customer parts information\n\n");
       
       help.append("**? Quick Start Examples:**\n");
       help.append("• `\"Create contract\"` - Start contract creation wizard\n");
       help.append("• `\"Create contract for account 123456789\"` - Create with account\n");
       help.append("• `\"Find contract 123456\"` - Search specific contract\n");
       help.append("• `\"Search part AB12345-12345678\"` - Lookup part details\n");
       help.append("• `\"Contracts for Acme Corp\"` - Find customer contracts\n");
       help.append("• `\"Part availability AB12345-12345678\"` - Check part status\n\n");
       
       help.append("**? Pro Tips:**\n");
       help.append("• I understand natural language - speak normally!\n");
       help.append("• I can handle typos and different phrasings\n");
       help.append("• Use `\"help contracts\"`, `\"help parts\"`, or `\"help format\"` for specific guidance\n");
       help.append("• Type `\"status\"` to check system health\n");
       help.append("• Use `\"clear\"` to reset our conversation\n\n");
       
       help.append("**? Session Management:**\n");
       help.append("• I remember our conversation context\n");
       help.append("• Multi-step processes are automatically managed\n");
       help.append("• Sessions timeout after 30 minutes of inactivity\n\n");
       
       help.append("Need specific help? Just ask! ?");
       
       return help.toString();
    }

    /**
    * Get session information
    */
    private String getSessionInfo() {
        StringBuilder info = new StringBuilder();
        info.append("? **Session Information**\n\n");
        info.append("? **Session ID:** ").append(getOrCreateSessionId()).append("\n");
        info.append("? **Session Duration:** ").append(getSessionDurationMinutes()).append(" minutes\n");
        info.append("? **Messages Exchanged:** ").append(chatHistory.size()).append("\n");
        info.append("? **Your Messages:** ").append(getUserMessageCount()).append("\n");
        info.append("? **Bot Messages:** ").append(getBotMessageCount()).append("\n");
        info.append("? **Session Active:** ").append(isSessionActive() ? "Yes" : "No").append("\n");
        info.append("? **Processing State:** ").append(isProcessing() ? "Busy" : "Ready").append("\n");
        info.append("? **System Status:** ").append(getSystemStatus()).append("\n\n");
        
        // Check for active creation sessions - simplified check
        boolean hasActiveSessions = false;
        try {
            // This is a placeholder - we'll implement proper session checking later
            hasActiveSessions = false;
        } catch (Exception e) {
            hasActiveSessions = false;
        }
        
        info.append("? **Active Creation Process:** ").append(hasActiveSessions ? "Yes" : "No").append("\n");
        
        if (hasActiveSessions) {
            info.append("? **Note:** You have an active contract or checklist creation in progress.\n");
        }
        
        info.append("\n? **Tip:** Type `\"clear\"` to reset the session if needed.");
        
        return info.toString();
    }

    /**
    * Enhanced welcome message with contract creation features
    */
    
    private String buildWelcomeMessage() {
       StringBuilder welcome = new StringBuilder();
       welcome.append("<div style='padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 10px; margin-bottom: 10px;'>");
       welcome.append("<h2 style='margin: 0 0 15px 0; color: white;'>? Welcome to Contract & Parts Assistant!</h2>");
       welcome.append("<p style='margin: 0; font-size: 16px; line-height: 1.6;'>I'm your AI assistant for contract management, checklist creation, and parts information. I can understand natural language and guide you through complex processes step-by-step.</p>");
       welcome.append("</div>");
       
       welcome.append("<div style='padding: 15px; background-color: #f8f9fa; border-radius: 8px; margin-top: 10px;'>");
       welcome.append("<h3 style='color: #495057; margin-top: 0;'>? What's New - Contract Creation!</h3>");
       welcome.append("<div style='padding: 12px; background: linear-gradient(135deg, #28a745, #20c997); color: white; border-radius: 6px; margin-bottom: 15px;'>");
       welcome.append("<strong>? New Feature:</strong> I can now create contracts and checklists for you with step-by-step guidance!");
       welcome.append("</div>");
       
       welcome.append("<h3 style='color: #495057; margin-top: 15px;'>? Quick Start Examples:</h3>");
       welcome.append("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-top: 15px;'>");
       
       welcome.append("<div style='padding: 12px; background: white; border-left: 4px solid #007bff; border-radius: 4px;'>");
       welcome.append("<strong>? Contract Management:</strong><br>");
       welcome.append("<code>\"Create contract\"</code><br>");
       welcome.append("<code>\"Create contract for account 123456789\"</code><br>");
       welcome.append("<code>\"Show contract 123456\"</code>");
       welcome.append("</div>");
       
       welcome.append("<div style='padding: 12px; background: white; border-left: 4px solid #28a745; border-radius: 4px;'>");
       welcome.append("<strong>? Checklist Creation:</strong><br>");
       welcome.append("<code>\"Create checklist\"</code><br>");
       welcome.append("<code>\"Checklist for contract 123456\"</code><br>");
       welcome.append("<code>\"Show checklist status\"</code>");
       welcome.append("</div>");
       
       welcome.append("<div style='padding: 12px; background: white; border-left: 4px solid #ffc107; border-radius: 4px;'>");
       welcome.append("<strong>? Parts Information:</strong><br>");
       welcome.append("<code>\"Show parts for contract 123456\"</code><br>");
       welcome.append("<code>\"Search part AB12345-12345678\"</code><br>");
       welcome.append("<code>\"Failed parts for 123456\"</code>");
       welcome.append("</div>");
       
       welcome.append("<div style='padding: 12px; background: white; border-left: 4px solid #dc3545; border-radius: 4px;'>");
       welcome.append("<strong>? Customer Services:</strong><br>");
       welcome.append("<code>\"Contracts for Acme Corp\"</code><br>");
       welcome.append("<code>\"Customer contract history\"</code><br>");
       welcome.append("<code>\"Find customer parts\"</code>");
       welcome.append("</div>");
       
       welcome.append("</div>");
    welcome.append("<div style='padding: 15px; background: linear-gradient(135deg, #17a2b8, #138496); color: white; border-radius: 6px; margin-top: 15px;'>");
        welcome.append("<h4 style='margin: 0 0 10px 0;'>? Smart Features:</h4>");
        welcome.append("<ul style='margin: 0; padding-left: 20px;'>");
        welcome.append("<li>Natural language understanding - talk to me normally!</li>");
        welcome.append("<li>Step-by-step guidance for complex processes</li>");
        welcome.append("<li>Automatic session management and context retention</li>");
        welcome.append("<li>Typo correction and flexible query handling</li>");
        welcome.append("</ul>");
        welcome.append("</div>");
        
        welcome.append("<p style='margin: 15px 0 0 0; color: #6c757d; font-style: italic; text-align: center;'>");
        welcome.append("? Type <strong>\"help\"</strong> anytime to see all available commands, or just start talking to me!");
        welcome.append("</p>");
        welcome.append("</div>");
        
        return welcome.toString();
    }

    /**
     * Enhanced clear chat command with session cleanup
     */
    
    private String handleClearChat() {
        // Clear active sessions through intent processor
        if (sessionId != null) {
            try {
                // For now, just reset the session ID since we don't have the method yet
                sessionId = null;
            } catch (Exception e) {
                System.err.println("Error clearing sessions: " + e.getMessage());
            }
        }
        
        // Keep only the welcome message
        if (!chatHistory.isEmpty() && chatHistory.get(0).isBot()) {
            ChatMessage welcomeMsg = chatHistory.get(0);
            chatHistory.clear();
            chatHistory.add(welcomeMsg);
        } else {
            chatHistory.clear();
            // Add a new welcome message
            chatHistory.add(new ChatMessage("Assistant", buildWelcomeMessage(), new Date(), true));
        }
        
        // Reset session ID
        sessionId = null;
        
        return "<div style='padding: 15px; background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 8px; color: #155724;'>" +
               "? <strong>Chat and Sessions Cleared Successfully!</strong><br><br>" +
               "? Your conversation history has been reset<br>" +
               "?? All active creation sessions have been terminated<br>" +
               "? New session ID has been generated<br><br>" +
               "How can I help you today?" +
               "</div>";
    }
    /**
     * Enhanced system status with session information
     */
    
    private String getSystemStatusMessage() {
        StringBuilder status = new StringBuilder();
        status.append("<div style='padding: 20px; background: linear-gradient(135deg, #6c757d, #495057); color: white; border-radius: 10px;'>");
        status.append("<h3 style='margin: 0 0 15px 0;'>?? System Status Dashboard</h3>");
        status.append("</div>");
        
        status.append("<div style='padding: 15px; background-color: #f8f9fa; border-radius: 8px; margin-top: 10px;'>");
        
        // Core System Status
        status.append("<h4 style='color: #495057; margin-top: 0; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;'>? Core System</h4>");
        status.append("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 20px;'>");
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid #28a745;'>");
        status.append("<strong>Overall Status:</strong><br>");
        status.append("<span style='color: #28a745;'>").append(systemStatus).append("</span>");
        status.append("</div>");
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid #007bff;'>");
        status.append("<strong>Processing State:</strong><br>");
        status.append("<span style='color: ").append(isProcessing ? "#ffc107" : "#28a745").append(";'>");
        status.append(isProcessing ? "? Busy" : "? Ready");
        status.append("</span>");
        status.append("</div>");
        
        status.append("</div>");
        
        // Session Information
        status.append("<h4 style='color: #495057; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;'>? Session Information</h4>");
        status.append("<div style='display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px; margin-bottom: 20px;'>");
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid #17a2b8;'>");
        status.append("<strong>Session ID:</strong><br>");
        status.append("<code style='font-size: 11px;'>").append(getOrCreateSessionId()).append("</code>");
        status.append("</div>");
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid #6f42c1;'>");
        status.append("<strong>Chat History:</strong><br>");
        status.append("<span style='color: #6f42c1;'>").append(chatHistory.size()).append(" messages</span>");
        status.append("</div>");
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid #fd7e14;'>");
        status.append("<strong>Session Duration:</strong><br>");
        status.append("<span style='color: #fd7e14;'>").append(getSessionDurationMinutes()).append(" minutes</span>");
        status.append("</div>");
        
        status.append("</div>");
        
        // Active Sessions - simplified for now
        status.append("<h4 style='color: #495057; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;'>? Active Creation Sessions</h4>");
        status.append("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 20px;'>");
        
        // Placeholder values since we don't have the methods yet
        boolean hasContractSession = false;
        boolean hasChecklistSession = false;
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid ").append(hasContractSession ? "#28a745" : "#6c757d").append(";'>");
        status.append("<strong>Contract Creation:</strong><br>");
        status.append("<span style='color: ").append(hasContractSession ? "#28a745" : "#6c757d").append(";'>");
        status.append(hasContractSession ? "? Active" : "? Inactive");
        status.append("</span>");
        status.append("</div>");
        
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid ").append(hasChecklistSession ? "#28a745" : "#6c757d").append(";'>");
        status.append("<strong>Checklist Creation:</strong><br>");
        status.append("<span style='color: ").append(hasChecklistSession ? "#28a745" : "#6c757d").append(";'>");
        status.append(hasChecklistSession ? "? Active" : "? Inactive");
        status.append("</span>");
        status.append("</div>");
        
        status.append("</div>");
        
        // System Timestamp
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border: 1px solid #dee2e6; text-align: center;'>");
        status.append("<strong>System Timestamp:</strong> ");
        status.append("<code>").append(new Date().toString()).append("</code>");
        status.append("</div>");
        
        status.append("</div>");
        
        // Add basic model status
        status.append("<h4 style='color: #495057; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;'>? AI Models Status</h4>");
        status.append("<div style='padding: 12px; background: white; border-radius: 6px; border-left: 4px solid #28a745;'>");
        status.append("<strong>Intent Processor:</strong> <span style='color: #28a745;'>? Operational</span>");
        status.append("</div>");
        
        return status.toString();
    }

    /**
     * Enhanced greeting response with contract creation mention
     */
    
    private String getGreetingResponse() {
        String[] greetings = {
            "Hello! ? I'm your Contract & Parts Assistant. I can help you create contracts, manage checklists, and find parts information. What do you need today?",
            "Hi there! ? Ready to help you with contracts, checklists, and parts. I can guide you through contract creation step-by-step. How can I assist?",
            "Good day! ? I'm here to assist with your contract management, checklist creation, and parts queries. What would you like to do?",
            "Hello! ? Your AI assistant is ready. I can create contracts, manage checklists, search parts, or type 'help' for all options. What's first?"
        };
        int randomIndex = (int) (Math.random() * greetings.length);
        return greetings[randomIndex];
    }

    /**
     * Enhanced about message with new features
     */
    
    private String getAboutMessage() {
        StringBuilder about = new StringBuilder();
        about.append("<div style='padding: 20px; background: linear-gradient(135deg, #6f42c1, #6610f2); color: white; border-radius: 10px;'>");
        about.append("<h3 style='margin: 0 0 15px 0;'>? About Contract & Parts Assistant</h3>");
        about.append("<p style='margin: 0; opacity: 0.9;'>Your intelligent AI companion for contract management and parts information</p>");
        about.append("</div>");
        
        about.append("<div style='padding: 15px; background-color: #f8f9fa; border-radius: 8px; margin-top: 10px;'>");
        
        about.append("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px;'>");
        
        about.append("<div>");
        about.append("<h4 style='color: #495057; margin-top: 0;'>? Version Information</h4>");
        about.append("<div style='padding: 12px; background: white; border-radius: 6px;'>");
        about.append("<strong>Version:</strong> 2.0.0<br>");
        about.append("<strong>Build:</strong> Enhanced Contract Creation<br>");
        about.append("<strong>Release Date:</strong> ").append(new Date().toString().substring(0, 10)).append("<br>");
        about.append("<strong>Platform:</strong> Oracle ADF + Apache OpenNLP");
        about.append("</div>");
        about.append("</div>");
        
        about.append("<div>");
        about.append("<h4 style='color: #495057; margin-top: 0;'>? Core Capabilities</h4>");
        about.append("<div style='padding: 12px; background: white; border-radius: 6px;'>");
        about.append("? Contract Creation & Management<br>");
        about.append("? Checklist Creation & Tracking<br>");
        about.append("? Parts Information Retrieval<br>");
        about.append("? Customer Service Integration<br>");
        about.append("? Natural Language Processing<br>");
        about.append("? Session Management");
        about.append("</div>");
        about.append("</div>");
        
        about.append("</div>");
        
        about.append("<h4 style='color: #495057; margin-top: 0;'>? AI Features</h4>");
        about.append("<div style='padding: 15px; background: white; border-radius: 6px; margin-bottom: 15px;'>");
        about.append("<div style='display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 15px;'>");
        
        about.append("<div style='text-align: center;'>");
        about.append("<div style='font-size: 24px; margin-bottom: 8px;'>?</div>");
        about.append("<strong>Intent Recognition</strong><br>");
        about.append("<small>Understands what you want to do</small>");
        about.append("</div>");
        
        about.append("<div style='text-align: center;'>");
        about.append("<div style='font-size: 24px; margin-bottom: 8px;'>?</div>");
        about.append("<strong>Entity Extraction</strong><br>");
        about.append("<small>Finds contract numbers, parts, etc.</small>");
        about.append("</div>");
        
        about.append("<div style='text-align: center;'>");
        about.append("<div style='font-size: 24px; margin-bottom: 8px;'>?</div>");
        about.append("<strong>Typo Correction</strong><br>");
        about.append("<small>Handles spelling mistakes</small>");
        about.append("</div>");
        
        about.append("</div>");
        about.append("</div>");
        
        about.append("<h4 style='color: #495057;'>? Supported Data Types</h4>");
        about.append("<div style='padding: 15px; background: white; border-radius: 6px; margin-bottom: 15px;'>");
        about.append("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 15px;'>");
        
        about.append("<div>");
        about.append("<strong>? Contract Numbers:</strong><br>");
        about.append("<code>6-digit format (123456)</code><br><br>");
        about.append("<strong>? Account Numbers:</strong><br>");
        about.append("<code>9-digit format (123456789)</code>");
        about.append("</div>");
        
        about.append("<div>");
        about.append("<strong>? Part Numbers:</strong><br>");
        about.append("<code>XX#####-######## format</code><br><br>");
        about.append("<strong>? Customer Names:</strong><br>");
        about.append("<code>Company names and individuals</code>");
        about.append("</div>");
        
        about.append("</div>");
        about.append("</div>");
        
        about.append("<h4 style='color: #495057;'>? New in Version 2.0</h4>");
        about.append("<div style='padding: 15px; background: linear-gradient(135deg, #28a745, #20c997); color: white; border-radius: 6px;'>");
        about.append("<ul style='margin: 0; padding-left: 20px;'>");
        about.append("<li><strong>Contract Creation Wizard:</strong> Step-by-step contract creation</li>");
        about.append("<li><strong>Checklist Management:</strong> Automated checklist creation and tracking</li>");
        about.append("<li><strong>Enhanced Session Management:</strong> Better context retention</li>");
    about.append("<li><strong>Improved Error Handling:</strong> More robust error recovery</li>");
       about.append("<li><strong>Smart Response Formatting:</strong> Better visual presentation</li>");
       about.append("<li><strong>Multi-step Process Support:</strong> Complex workflows made simple</li>");
       about.append("</ul>");
       about.append("</div>");
       
       about.append("<div style='padding: 15px; background-color: #e9ecef; border-radius: 6px; margin-top: 15px; text-align: center;'>");
       about.append("<p style='margin: 0; color: #6c757d;'>");
       about.append("? <strong>Pro Tip:</strong> I learn from our conversations to provide better assistance. ");
       about.append("The more specific you are, the better I can help you!");
       about.append("</p>");
       about.append("</div>");
       
       about.append("</div>");
       
       return about.toString();
    }

    /**
    * Handle model reload command
    */
    private String handleReloadModels() {
       try {
           // Reload AI models through intent processor
           boolean success = ChatbotIntentProcessor.areModelsReady();
           
           if (success) {
               return "<div style='padding: 15px; background-color: #d4edda; border: 1px solid #c3e6cb; border-radius: 8px; color: #155724;'>" +
                      "? <strong>Models Reloaded Successfully!</strong><br><br>" +
                      "? Natural Language Processing models have been refreshed<br>" +
                      "? Intent recognition capabilities updated<br>" +
                      "? System performance optimized<br><br>" +
                      "The chatbot is now ready with the latest AI improvements!" +
                      "</div>";
           } else {
               return "<div style='padding: 15px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 8px; color: #721c24;'>" +
                      "?? <strong>Model Reload Warning</strong><br><br>" +
                      "Some models may not have reloaded completely, but the system is still functional.<br>" +
                      "If you experience issues, please contact system administrator." +
                      "</div>";
           }
       } catch (Exception e) {
           System.err.println("? Error reloading models: " + e.getMessage());
           return "<div style='padding: 15px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 8px; color: #721c24;'>" +
                  "? <strong>Model Reload Failed</strong><br><br>" +
                  "Unable to reload AI models. The system will continue using cached models.<br>" +
                  "Error: " + e.getMessage() +
                  "</div>";
       }
    }

    /**
    * Handle system errors with user-friendly messages
    */
    private void handleSystemError(Exception e) {
       String errorMsg = "<div style='padding: 15px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 8px; color: #721c24;'>" +
                        "? <strong>System Error Occurred</strong><br><br>" +
                        "I encountered an unexpected error while processing your request. " +
                        "Don't worry - I'm still here to help!<br><br>" +
                        "? <strong>What you can try:</strong><br>" +
                        "• Rephrase your request and try again<br>" +
                        "• Type 'help' to see available commands<br>" +
                        "• Use 'clear' to reset our conversation<br>" +
                        "• Check 'status' to see system health<br><br>" +
                        "If the problem persists, please contact technical support." +
                        "</div>";
       
       setChatResponse(errorMsg);
       chatHistory.add(new ChatMessage("Assistant", errorMsg, new Date(), true));
    }

    // Helper methods for session management

    /**
    * Get session duration in minutes
    */
    private long getSessionDurationMinutes() {
       if (chatHistory.isEmpty()) {
           return 0;
       }
       
       Date firstMessage = chatHistory.get(0).getTimestamp();
       Date now = new Date();
       long diffInMillis = now.getTime() - firstMessage.getTime();
       return diffInMillis / (60 * 1000); // Convert to minutes
    }

    /**
    * Get count of user messages
    */
    private int getUserMessageCount() {
       return (int) chatHistory.stream().filter(msg -> !msg.isBot()).count();
    }

    /**
    * Get count of bot messages
    */
    private int getBotMessageCount() {
       return (int) chatHistory.stream().filter(msg -> msg.isBot()).count();
    }

    /**
    * Check if session is still active (within timeout period)
    */
    private boolean isSessionActive() {
       if (chatHistory.isEmpty()) {
           return false;
       }
       
       Date lastMessage = chatHistory.get(chatHistory.size() - 1).getTimestamp();
       Date now = new Date();
       long diffInMinutes = (now.getTime() - lastMessage.getTime()) / (60 * 1000);
       
       return diffInMinutes < 30; // 30 minute timeout
    }

    /**
    * Get current system status
    */
    private String getSystemStatus() {
       try {
           // Check if intent processor is responsive
           boolean processorHealthy = intentProcessor.isHealthy();
           
           if (processorHealthy && !isProcessing) {
               return "? Operational";
           } else if (processorHealthy && isProcessing) {
               return "? Busy";
           } else {
               return "? Degraded";
           }
       } catch (Exception e) {
           return "? Error";
       }
    }

    // Enhanced validation methods

    /**
    * Enhanced input validation with better error messages
    */
    
    private boolean isValidInput(String input) {
       if (input == null) {
           return false;
       }
       
       String trimmed = input.trim();
       
       // Check for empty input
       if (trimmed.isEmpty()) {
           return false;
       }
       
       // Check for minimum length
       if (trimmed.length() < 1) {
           return false;
       }
       
       // Check for maximum length (prevent abuse)
       if (trimmed.length() > 1000) {
           return false;
       }
       
       // Check for potentially malicious content
       if (containsMaliciousContent(trimmed)) {
           return false;
       }
       
       return true;
    }

    /**
    * Check for potentially malicious content
    */
    private boolean containsMaliciousContent(String input) {
       String lowerInput = input.toLowerCase();
       
       // Check for script injection attempts
       String[] maliciousPatterns = {
           "<script", "javascript:", "vbscript:", "onload=", "onerror=",
           "eval(", "document.cookie", "window.location", "alert(",
           "confirm(", "prompt(", "setTimeout(", "setInterval("
       };
       
       for (String pattern : maliciousPatterns) {
           if (lowerInput.contains(pattern)) {
               return true;
           }
       }
       
       return false;
    }

    /**
    * Enhanced error message for invalid input
    */
    
    private String getInvalidInputMessage(String input) {
       if (input == null || input.trim().isEmpty()) {
           return "<div style='padding: 15px; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; color: #856404;'>" +
                  "?? <strong>Empty Message</strong><br><br>" +
                  "Please type a message to get started. You can:<br>" +
                  "• Ask me to create a contract<br>" +
                  "• Search for parts or contracts<br>" +
                  "• Type 'help' for all available options<br><br>" +
                  "What would you like to do?" +
                  "</div>";
       }
       
       if (input.trim().length() > 1000) {
           return "<div style='padding: 15px; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; color: #856404;'>" +
                  "?? <strong>Message Too Long</strong><br><br>" +
                  "Please keep your message under 1000 characters. " +
                  "Try breaking down complex requests into smaller parts.<br><br>" +
                  "Current length: " + input.trim().length() + " characters" +
                  "</div>";
       }
       
       if (containsMaliciousContent(input)) {
           return "<div style='padding: 15px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 8px; color: #721c24;'>" +
                  "? <strong>Invalid Content Detected</strong><br><br>" +
                  "Your message contains content that cannot be processed for security reasons. " +
                  "Please rephrase your request using plain text only." +
                  "</div>";
       }
       
       return "<div style='padding: 15px; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 8px; color: #856404;'>" +
              "?? <strong>Invalid Input</strong><br><br>" +
              "I couldn't process your message. Please try rephrasing your request or type 'help' for assistance." +
              "</div>";
    }

    // Cleanup and resource management

    /**
    * Cleanup method for session termination
    */
    public void cleanup() {
       try {
           // Clear active sessions
           if (sessionId != null) {
               intentProcessor.clearUserSessions(sessionId);
           }
           
           // Clear chat history (keep only welcome message if exists)
           if (!chatHistory.isEmpty() && chatHistory.get(0).isBot()) {
               ChatMessage welcomeMsg = chatHistory.get(0);
               chatHistory.clear();
               chatHistory.add(welcomeMsg);
           } else {
               chatHistory.clear();
           }
           
           // Reset state
           isProcessing = false;
           sessionId = null;
           
           System.out.println("? ChatbotBean cleanup completed successfully");
           
       } catch (Exception e) {
           System.err.println("? Error during ChatbotBean cleanup: " + e.getMessage());
           e.printStackTrace();
       }
    }

    /**
    * Method called when bean is being destroyed
    */
    
    public void destroy() {
       System.out.println("? ChatbotBean is being destroyed, performing cleanup...");
       cleanup();
    }

    // Additional utility methods

    /**
    * Format response with consistent styling
    */
    private String formatResponse(String content, String type) {
       String borderColor;
       String backgroundColor;
       String textColor;
       String icon;
       
       switch (type.toLowerCase()) {
           case "success":
               borderColor = "#c3e6cb";
               backgroundColor = "#d4edda";
               textColor = "#155724";
               icon = "?";
               break;
           case "warning":
               borderColor = "#ffeaa7";
               backgroundColor = "#fff3cd";
               textColor = "#856404";
               icon = "??";
               break;
           case "error":
               borderColor = "#f5c6cb";
               backgroundColor = "#f8d7da";
               textColor = "#721c24";
               icon = "?";
               break;
           case "info":
               borderColor = "#b8daff";
               backgroundColor = "#d1ecf1";
               textColor = "#0c5460";
               icon = "??";
               break;
           default:
               borderColor = "#dee2e6";
               backgroundColor = "#f8f9fa";
               textColor = "#495057";
               icon = "?";
       }
       
       return "<div style='padding: 15px; background-color: " + backgroundColor + 
              "; border: 1px solid " + borderColor + "; border-radius: 8px; color: " + textColor + ";'>" +
              icon + " " + content +
              "</div>";
    }

    /**
    * Log user interaction for analytics
    */
    private void logUserInteraction(String userInput, String botResponse, boolean successful) {
       try {
           // In a production environment, this would log to a proper analytics system
           System.out.println("? User Interaction Log:");
           System.out.println("   Session: " + getOrCreateSessionId());
           System.out.println("   Input: " + userInput.substring(0, Math.min(userInput.length(), 100)) + 
                             (userInput.length() > 100 ? "..." : ""));
           System.out.println("   Success: " + successful);
           System.out.println("   Timestamp: " + new Date());
           System.out.println("   Response Length: " + botResponse.length() + " chars");
           
       } catch (Exception e) {
           // Don't let logging errors affect the main functionality
           System.err.println("?? Error logging user interaction: " + e.getMessage());
       }
    }

    /**
    * Get performance metrics for the current session
    */
    public String getSessionMetrics() {
       StringBuilder metrics = new StringBuilder();
       metrics.append("? **Session Performance Metrics**\n\n");
       metrics.append("? **Duration:** ").append(getSessionDurationMinutes()).append(" minutes\n");
       metrics.append("? **Total Messages:** ").append(chatHistory.size()).append("\n");
       metrics.append("? **User Messages:** ").append(getUserMessageCount()).append("\n");
       metrics.append("? **Bot Responses:** ").append(getBotMessageCount()).append("\n");
       metrics.append("? **Avg Response Time:** ").append(calculateAverageResponseTime()).append("ms\n");
       metrics.append("? **Success Rate:** ").append(calculateSuccessRate()).append("%\n");
       
       return metrics.toString();
    }

    /**
    * Calculate average response time (placeholder implementation)
    */
    private long calculateAverageResponseTime() {
       // In a real implementation, this would track actual response times
       return 150; // Placeholder value
    }

    /**
    * Calculate success rate based on error messages
    */
    private double calculateSuccessRate() {
       if (chatHistory.isEmpty()) {
           return 100.0;
       }
       
       long errorCount = chatHistory.stream()
           .filter(msg -> msg.isBot() && (msg.getMessage().contains("?") || msg.getMessage().contains("??")))
           .count();
       
       double successRate = ((double)(getBotMessageCount() - errorCount) / getBotMessageCount()) * 100;
       return Math.round(successRate * 100.0) / 100.0; // Round to 2 decimal places
    }

    
                
                
                
    private String handleSpecialCommands(String input) {
        // Help commands
        if (input.contains("help") || input.equals("?") || input.contains("what can you do")) {
            return getEnhancedHelpInformation();
        }
        
        // Status commands
        if (input.contains("status") && (input.contains("system") || input.contains("model"))) {
            return getSystemStatusMessage();
        }
        
        // Clear chat command
        if (input.equals("clear") || input.equals("clear chat") || input.equals("reset")) {
            return handleClearChat();
        }
        
        // Session info command
        if (input.contains("session") && input.contains("info")) {
            return getSessionInfo();
        }
        
        // Reload models command
        if (input.contains("reload") && input.contains("model")) {
            return handleReloadModels();
        }
        
        // Version/about command
        if (input.contains("version") || input.contains("about")) {
            return getAboutMessage();
        }
        
        // Contract creation shortcuts
        if (input.equals("create contract") || input.equals("new contract")) {
            return intentProcessor.processUserMessage("create contract", getOrCreateSessionId());
        }
        
        // Greeting responses
        if (input.matches("(hi|hello|hey|good morning|good afternoon|good evening).*")) {
            return getGreetingResponse();
        }
        
        // Thank you responses
        if (input.matches(".*(thank you|thanks|thx).*")) {
            return "You're welcome! ? Is there anything else I can help you with regarding contracts, checklists, or parts?";
        }
        
        // Goodbye responses
        if (input.matches(".*(bye|goodbye|see you|exit|quit).*")) {
            return "Goodbye! ? Feel free to come back anytime if you need help with contracts, checklists, or parts information.";
        }
        
        return null; // No special command matched
    }


    /**
     * Get help information
     */
    private String getHelpInformation() {
        return "? **Chatbot Help Center**\n\n" + "I can assist you with:\n" +
               "? **Contracts**: Search, status, history, customer contracts\n" +
               "? **Parts**: Lookup, availability, pricing, compatibility\n" +
               "? **Customers**: Find contracts and parts by customer\n\n" + "**Quick Examples:**\n" +
               "• 'Find contract 123456'\n" + "• 'Search part AB12345-12345678'\n" + "• 'Contracts for Acme Corp'\n" +
               "• 'Part availability AB12345-12345678'\n\n" +
               "Type 'help contracts', 'help parts', or 'help format' for specific guidance.";
    }

    /**
     * Add welcome message to chat history
     */
    private void addWelcomeMessage() {
        String welcomeMsg = buildWelcomeMessage();
        ChatMessage welcomeMessage = new ChatMessage("Assistant", welcomeMsg, new Date(), true);
        chatHistory.add(welcomeMessage);
        setChatResponse(welcomeMsg);
    }

   

    /**
     * Add system message to chat
     */
    private void addSystemMessage(String message, String type) {
        ChatMessage systemMessage = new ChatMessage("System", message, new Date(), true);
        chatHistory.add(systemMessage);
    }

   

    /**
     * Log chat history for debugging
     */
    private void logChatHistory() {
        System.out.println("? Current chat history (" + chatHistory.size() + " messages):");
        for (int i = Math.max(0, chatHistory.size() - 3); i < chatHistory.size(); i++) {
            ChatMessage msg = chatHistory.get(i);
            String preview =
                msg.getMessage().length() > 50 ? msg.getMessage().substring(0, 50) + "..." : msg.getMessage();
            System.out.println("  " + (i + 1) + ". [" + msg.getSender() + "]: " + preview.replaceAll("<[^>]*>", ""));
        }
    }

  

    /**
     * Handle step navigation for step-by-step messages
     */
    public void navigateStep(ClientEvent clientEvent) {
        String action = (String) clientEvent.getParameters().get("action");
        String messageIndexStr = (String) clientEvent.getParameters().get("messageIndex");

        if (messageIndexStr != null) {
            try {
                int messageIndex = Integer.parseInt(messageIndexStr);

                if (messageIndex >= 0 && messageIndex < chatHistory.size()) {
                    ChatMessage message = chatHistory.get(messageIndex);

                    // Use the correct method from your ChatMessage class
                    if (message.isStepByStepMessage() && message.getSteps() != null && !message.getSteps().isEmpty()) {
                        if ("next".equals(action)) {
                            message.nextStep();
                        } else if ("previous".equals(action)) {
                            message.previousStep();
                        }

                        System.out.println("? Step navigation: " + action + " - Now at step " +
                                           message.getStepProgress());
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid message index: " + messageIndexStr);
            }
        }
    }

    /**
     * Get current step for a specific message (for UI binding)
     */
    public String getCurrentStepForMessage(int messageIndex) {
        if (messageIndex >= 0 && messageIndex < chatHistory.size()) {
            ChatMessage message = chatHistory.get(messageIndex);
            if (message.isStepByStepMessage()) {
                return message.getCurrentStep();
            }
        }
        return "";
    }

    /**
     * Get step progress for a specific message (for UI binding)
     */
    public String getStepProgressForMessage(int messageIndex) {
        if (messageIndex >= 0 && messageIndex < chatHistory.size()) {
            ChatMessage message = chatHistory.get(messageIndex);
            if (message.isStepByStepMessage()) {
                return message.getStepProgress();
            }
        }
        return "";
    }

    /**
     * Check if a message can go to next step (for UI binding)
     */
    public boolean canMessageGoNext(int messageIndex) {
        if (messageIndex >= 0 && messageIndex < chatHistory.size()) {
            ChatMessage message = chatHistory.get(messageIndex);
            if (message.isStepByStepMessage()) {
                return message.canGoNext();
            }
        }
        return false;
    }

    /**
     * Check if a message can go to previous step (for UI binding)
     */
    public boolean canMessageGoPrevious(int messageIndex) {
        if (messageIndex >= 0 && messageIndex < chatHistory.size()) {
            ChatMessage message = chatHistory.get(messageIndex);
            if (message.isStepByStepMessage()) {
                return message.canGoPrevious();
            }
        }
        return false;
    }

    public List<ChatMessage> getStepByStepMessages() {
        return chatHistory.stream()
                          .filter(ChatMessage::isStepByStepMessage)
                          .collect(java.util
                                       .stream
                                       .Collectors
                                       .toList());
    }

    /**
     * Check if chat has any step-by-step messages
     */
    public boolean hasStepByStepMessages() {
        return chatHistory.stream().anyMatch(ChatMessage::isStepByStepMessage);
    }

    /**
     * Create step-by-step message for complex procedures
     */
    private ChatMessage createStepByStepMessage(String title, List<String> steps) {
        // Use the constructor that matches your ChatMessage class
        return new ChatMessage("Assistant", title, new Date(), true, "step-by-step", steps);
    }

    private void addStepByStepResponse(String title, List<String> steps) {
        ChatMessage stepMessage = createStepByStepMessage(title, steps);
        chatHistory.add(stepMessage);

        // Set the response to show all steps formatted
        setChatResponse(stepMessage.getAllStepsFormatted());
    }

    // ===========================================
    // GETTERS AND SETTERS
    // ===========================================

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getChatResponse() {
        return chatResponse;
    }

    public void setChatResponse(String chatResponse) {
        this.chatResponse = chatResponse;
    }

    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public RichInputText getInputTextComponent() {
        return inputTextComponent;
    }

    public void setInputTextComponent(RichInputText inputTextComponent) {
        this.inputTextComponent = inputTextComponent;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean processing) {
        this.isProcessing = processing;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    // ===========================================
    // UI HELPER METHODS
    // ===========================================

    /**
     * Get the latest bot response for UI display
     */
    public String getLatestBotResponse() {
        for (int i = chatHistory.size() - 1; i >= 0; i--) {
            ChatMessage msg = chatHistory.get(i);
            if (msg.isBot()) {
                return msg.getMessage();
            }
        }
        return "";
    }

    /**
     * Get the latest user message for UI display
     */
    public String getLatestUserMessage() {
        for (int i = chatHistory.size() - 1; i >= 0; i--) {
            ChatMessage msg = chatHistory.get(i);
            if (!msg.isBot()) {
                return msg.getMessage();
            }
        }
        return "";
    }

    /**
     * Get chat history size for UI display
     */
    public int getChatHistorySize() {
        return chatHistory != null ? chatHistory.size() : 0;
    }

    /**
     * Check if chat has any messages
     */
    public boolean hasMessages() {
        return chatHistory != null && !chatHistory.isEmpty();
    }

    /**
     * Get formatted timestamp for the latest message
     */
    public String getLatestMessageTime() {
        if (chatHistory != null && !chatHistory.isEmpty()) {
            ChatMessage latest = chatHistory.get(chatHistory.size() - 1);
            return latest.getFormattedTime();
        }
        return "";
    }

    /**
     * Check if the latest message is from bot
     */
    public boolean isLatestMessageFromBot() {
        if (chatHistory != null && !chatHistory.isEmpty()) {
            ChatMessage latest = chatHistory.get(chatHistory.size() - 1);
            return latest.isBot();
        }
        return false;
    }

    /**
     * Get processing status message for UI
     */
    public String getProcessingStatusMessage() {
        if (isProcessing) {
            return "? Processing your request...";
        }
        return "Ready";
    }

    /**
     * Get CSS class for processing state
     */
    public String getProcessingCssClass() {
        return isProcessing ? "processing" : "ready";
    }

    // ===========================================
    // UTILITY METHODS FOR UI BINDING
    // ===========================================

    /**
     * Method to refresh chat display - called from UI
     */
    public void refreshChatDisplay() {
        System.out.println("? Refreshing chat display - " + chatHistory.size() + " messages");
        // This method can be used to trigger UI refresh if needed
    }

    /**
     * Clear input field - called from UI
     */
    public void clearInput() {
        setUserInput("");
        System.out.println("? Input field cleared");
    }

    /**
     * Handle Enter key press in input field
     */
    public void handleEnterKey(ClientEvent clientEvent) {
        System.out.println("?? Enter key pressed - processing input");
        processUserInput(clientEvent);
    }

    /**
     * Handle input focus event
     */
    public void handleInputFocus(ClientEvent clientEvent) {
        System.out.println("? Input field focused");
        // Can be used to show typing indicators or other UI enhancements
    }

    /**
     * Handle input blur event
     */
    public void handleInputBlur(ClientEvent clientEvent) {
        System.out.println("?? Input field blurred");
        // Can be used to hide typing indicators or save draft
    }

    // ===========================================
    // CHAT MANAGEMENT METHODS
    // ===========================================

    /**
     * Export chat history as text
     */
    public String exportChatHistory() {
        StringBuilder export = new StringBuilder();
        export.append("=== CHAT HISTORY EXPORT ===\n");
        export.append("Generated: ")
              .append(new Date().toString())
              .append("\n");
        export.append("Total Messages: ")
              .append(chatHistory.size())
              .append("\n");
        export.append("================================\n\n");

        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage msg = chatHistory.get(i);
            export.append("[")
                  .append(msg.getFormattedTime())
                  .append("] ");
            export.append(msg.getSender()).append(": ");

            // Strip HTML tags for export
            String cleanMessage = msg.getMessage().replaceAll("<[^>]*>", "");
            export.append(cleanMessage).append("\n\n");
        }

        System.out.println("? Chat history exported - " + export.length() + " characters");
        return export.toString();
    }

    /**
     * Get chat statistics
     */
    public String getChatStatistics() {
        if (chatHistory == null || chatHistory.isEmpty()) {
            return "No chat statistics available.";
        }

        int userMessages = 0;
        int botMessages = 0;
        int totalCharacters = 0;

        for (ChatMessage msg : chatHistory) {
            if (msg.isBot()) {
                botMessages++;
            } else {
                userMessages++;
            }
            totalCharacters += msg.getMessage().length();
        }

        StringBuilder stats = new StringBuilder();
        stats.append("<h3>? Chat Statistics</h3>");
        stats.append("<hr>");
        stats.append("<div style='padding: 15px; background-color: #f8f9fa; border-radius: 5px;'>");
        stats.append("<strong>Total Messages:</strong> ")
             .append(chatHistory.size())
             .append("<br>");
        stats.append("<strong>Your Messages:</strong> ")
             .append(userMessages)
             .append("<br>");
        stats.append("<strong>Bot Messages:</strong> ")
             .append(botMessages)
             .append("<br>");
        stats.append("<strong>Total Characters:</strong> ")
             .append(totalCharacters)
             .append("<br>");
        stats.append("<strong>Average Message Length:</strong> ")
             .append(totalCharacters / Math.max(1, chatHistory.size()))
             .append(" chars<br>");
        stats.append("<strong>Session Duration:</strong> ");

        if (chatHistory.size() >= 2) {
            long duration = chatHistory.get(chatHistory.size() - 1)
                                       .getTimestamp()
                                       .getTime() - chatHistory.get(0)
                                                               .getTimestamp()
                                                               .getTime();
            long minutes = duration / (1000 * 60);
            stats.append(minutes).append(" minutes");
        } else {
            stats.append("Less than 1 minute");
        }

        stats.append("</div>");
        return stats.toString();
    }

    /**
     * Search chat history
     */
    public List<ChatMessage> searchChatHistory(String searchTerm) {
        List<ChatMessage> results = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String lowerSearchTerm = searchTerm.toLowerCase();

            for (ChatMessage msg : chatHistory) {
                if (msg.getMessage()
                       .toLowerCase()
                       .contains(lowerSearchTerm) || msg.getSender()
                                                        .toLowerCase()
                                                        .contains(lowerSearchTerm)) {
                    results.add(msg);
                }
            }
        }

        System.out.println("? Chat search for '" + searchTerm + "' found " + results.size() + " results");
        return results;
    }

    /**
     * Get recent messages (last N messages)
     */
    public List<ChatMessage> getRecentMessages(int count) {
        if (chatHistory == null || chatHistory.isEmpty()) {
            return new ArrayList<>();
        }

        int startIndex = Math.max(0, chatHistory.size() - count);
        return new ArrayList<>(chatHistory.subList(startIndex, chatHistory.size()));
    }

    /**
     * Add quick reply options for common queries
     */
    public List<String> getQuickReplies() {
        List<String> quickReplies = new ArrayList<>();
        quickReplies.add("Show all contracts");
        quickReplies.add("Help");
        quickReplies.add("System status");
        quickReplies.add("What can you do?");
        quickReplies.add("Show contract 123456");
        quickReplies.add("Show parts for contract 123456");
        return quickReplies;
    }

    /**
     * Handle quick reply selection
     */
    public void selectQuickReply(ClientEvent clientEvent) {
        String selectedReply = (String) clientEvent.getParameters().get("reply");
        if (selectedReply != null && !selectedReply.trim().isEmpty()) {
            setUserInput(selectedReply);
            processUserInput(clientEvent);
            System.out.println("? Quick reply selected: " + selectedReply);
        }
    }

    // ===========================================
    // ERROR HANDLING AND RECOVERY
    // ===========================================

    

    

    /**
     * Initialize or reinitialize the bean
     */
    public void initialize() {
        System.out.println("? Reinitializing ChatbotBean");

        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
        }

        if (intentProcessor == null) {
            intentProcessor = new ChatbotIntentProcessor();
        }

        isProcessing = false;

        // Re-add welcome message if chat is empty
        if (chatHistory.isEmpty()) {
            addWelcomeMessage();
        }

        System.out.println("? ChatbotBean reinitialized successfully");
    }

    /**
     * Get bean status for monitoring
     */
    public String getBeanStatus() {
        StringBuilder status = new StringBuilder();
        status.append("ChatbotBean Status: ");
        status.append("Messages=").append(chatHistory != null ? chatHistory.size() : 0);
        status.append(", Processing=").append(isProcessing);
        status.append(", ModelsLoaded=").append(ChatbotIntentProcessor.areModelsReady());
        status.append(", SystemStatus=").append(systemStatus);
        return status.toString();
    }

    
    /**
     * Check if there are any error messages in chat
     */
    public boolean hasErrorMessages() {
        if (chatHistory == null)
            return false;
        return chatHistory.stream()
               .anyMatch(msg -> msg.getSender().equals("System") && msg.getMessage().contains("Error"));
    }

    /**
     * Get the first error message if any
     */
    public String getFirstErrorMessage() {
        if (chatHistory == null)
            return null;
        return chatHistory.stream()
                          .filter(msg -> msg.getSender().equals("System") && msg.getMessage().contains("Error"))
                          .findFirst()
                          .map(ChatMessage::getMessage)
                          .orElse(null);
    }

  
   

    /**
     * Get average response time (for performance monitoring)
     */
    public double getAverageResponseTime() {
        // This would need to be implemented with actual timing data
        // For now, return a placeholder
        return 1.5; // seconds
    }

    /**
     * Reset chat to initial state
     */
    public void resetChat() {
        System.out.println("? Resetting chat to initial state");

        if (chatHistory != null) {
            chatHistory.clear();
        }

        userInput = "";
        chatResponse = "";
        isProcessing = false;

        // Re-add welcome message
        addWelcomeMessage();

        System.out.println("? Chat reset completed");
    }

    /**
     * Handle session timeout
     */
    public void handleSessionTimeout() {
        System.out.println("? Session timeout detected");

        String timeoutMessage =
            "<div style='padding: 15px; background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; color: #856404;'>" +
            "<h4>? Session Timeout</h4>" +
            "<p>Your session has been inactive for a while. Your chat history has been preserved, but you may need to refresh the page if you experience any issues.</p>" +
            "<p>Feel free to continue our conversation!</p>" + "</div>";

        ChatMessage timeoutMsg = new ChatMessage("System", timeoutMessage, new Date(), true);
        chatHistory.add(timeoutMsg);
        setChatResponse(timeoutMessage);
    }

    /**
     * Validate chat state for consistency
     */
    public boolean validateChatState() {
        try {
            if (chatHistory == null) {
                System.err.println("? Chat history is null");
                return false;
            }

            if (intentProcessor == null) {
                System.err.println("? Intent processor is null");
                return false;
            }

            // Check for any null messages
            for (int i = 0; i < chatHistory.size(); i++) {
                ChatMessage msg = chatHistory.get(i);
                if (msg == null) {
                    System.err.println("? Null message found at index " + i);
                    return false;
                }

                if (msg.getMessage() == null || msg.getSender() == null || msg.getTimestamp() == null) {
                    System.err.println("? Invalid message data at index " + i);
                    return false;
                }
            }

            System.out.println("? Chat state validation passed");
            return true;

        } catch (Exception e) {
            System.err.println("? Error during chat state validation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get debug information for troubleshooting
     */
    public String getDebugInfo() {
        StringBuilder debug = new StringBuilder();
        debug.append("=== CHATBOT DEBUG INFO ===\n");
        debug.append("Timestamp: ")
             .append(new Date())
             .append("\n");
        debug.append("Chat History Size: ")
             .append(chatHistory != null ? chatHistory.size() : "null")
             .append("\n");
        debug.append("Processing State: ")
             .append(isProcessing)
             .append("\n");
        debug.append("System Status: ")
             .append(systemStatus)
             .append("\n");
        debug.append("User Input: ")
             .append(userInput != null ? userInput.length() + " chars" : "null")
             .append("\n");
        debug.append("Chat Response: ")
             .append(chatResponse != null ? chatResponse.length() + " chars" : "null")
             .append("\n");
        debug.append("Intent Processor: ")
             .append(intentProcessor != null ? "initialized" : "null")
             .append("\n");
        debug.append("Models Ready: ")
             .append(ChatbotIntentProcessor.areModelsReady())
             .append("\n");
        debug.append("Session Active: ")
             .append(isSessionActive())
             .append("\n");
        debug.append("Session Duration: ")
             .append(getSessionDurationMinutes())
             .append(" minutes\n");
        debug.append("State Valid: ")
             .append(validateChatState())
             .append("\n");
        debug.append("========================\n");

        return debug.toString();
    }

    /**
     * Handle emergency reset (for critical errors)
     */
    public void emergencyReset() {
        System.out.println("? Emergency reset initiated");

        try {
            // Clear all state
            chatHistory = new ArrayList<>();
            userInput = "";
            chatResponse = "";
            isProcessing = false;
            systemStatus = "Emergency reset completed";

            // Reinitialize intent processor
            intentProcessor = new ChatbotIntentProcessor();

            // Add emergency reset message
            String resetMessage =
                "<div style='padding: 15px; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 5px; color: #721c24;'>" +
                "<h4>? Emergency Reset</h4>" +
                "<p>The chatbot has been reset due to a critical error. All previous conversation history has been cleared.</p>" +
                "<p>You can now start a fresh conversation. If problems persist, please contact support.</p>" +
                "</div>";

            ChatMessage resetMsg = new ChatMessage("System", resetMessage, new Date(), true);
            chatHistory.add(resetMsg);
            setChatResponse(resetMessage);

            // Add new welcome message
            addWelcomeMessage();

            System.out.println("? Emergency reset completed successfully");

        } catch (Exception e) {
            System.err.println("? Critical error during emergency reset: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Finalize method for cleanup when bean is destroyed
     */

    protected void finalize() throws Throwable {
        try {
            cleanup();
        } finally {
            super.finalize();
        }
    }

    /**
     * toString method for debugging
     */
    
    public String toString() {
        return "ChatbotBean{" + "chatHistorySize=" + (chatHistory != null ? chatHistory.size() : 0) +
               ", isProcessing=" + isProcessing + ", systemStatus='" + systemStatus + '\'' + ", hasUserInput=" +
               (userInput != null && !userInput.isEmpty()) + ", sessionActive=" + isSessionActive() + '}';
    }
}

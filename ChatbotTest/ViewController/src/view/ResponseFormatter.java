package view;

import java.util.*;

public class ResponseFormatter {
    private Map<QueryType, String> responseTemplates;
    
    public ResponseFormatter() {
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        responseTemplates = new HashMap<>();
        
        // Contract response templates
        responseTemplates.put(QueryType.CONTRACT_INFO, "Here are the contract details:");
        responseTemplates.put(QueryType.USER_CONTRACT_QUERY, "Here are the contracts for user:");
        
        // Parts response templates
        responseTemplates.put(QueryType.PARTS_BY_CONTRACT, "Here are the parts for this contract:");
        responseTemplates.put(QueryType.PARTS_INFO, "Here are the part specifications:");
        
        // Failed parts templates
        responseTemplates.put(QueryType.FAILED_PARTS_BY_CONTRACT, "Here are the failed parts for this contract:");
        
        // Account templates
        responseTemplates.put(QueryType.ACCOUNT_INFO, "Here is the account information:");
        
        // Help templates
        responseTemplates.put(QueryType.HELP_CREATE_CONTRACT, "Here's how to create a contract:");
    }
    
    public ChatbotResponse formatResponse(String rawResult, QueryType queryType, double confidence) {
        try {
            if (rawResult == null || rawResult.trim().isEmpty()) {
                return formatNoDataFound(queryType);
            }
            
            String template = responseTemplates.getOrDefault(queryType, "Here is the information you requested:");
            String formattedMessage = formatWithTemplate(template, rawResult, queryType);
            
            return ChatbotResponse.success(formattedMessage, confidence);
            
        } catch (Exception e) {
            System.out.println("Error formatting response: " + e.getMessage());
            return ChatbotResponse.error("I encountered an error while formatting the response.");
        }
    }
    
    // Overloaded method to accept Intent object and extract QueryType
    public ChatbotResponse formatResponse(String rawResult, Intent intent, double confidence) {
        return formatResponse(rawResult, intent.getQueryType(), confidence);
    }
    
    private String formatWithTemplate(String template, String rawResult, QueryType queryType) {
        StringBuilder formatted = new StringBuilder();
        formatted.append(template).append("\n\n");
        
        // Format based on query type
        switch (queryType) {
            case CONTRACT_INFO:
            case USER_CONTRACT_QUERY:
                formatted.append(formatContractData(rawResult));
                break;
                
            case PARTS_BY_CONTRACT:
            case PARTS_INFO:
                formatted.append(formatPartsData(rawResult));
                break;
                
            case FAILED_PARTS_BY_CONTRACT:
                formatted.append(formatFailedPartsData(rawResult));
                break;
                
            case ACCOUNT_INFO:
                formatted.append(formatAccountData(rawResult));
                break;
                
            case HELP_CREATE_CONTRACT:
                formatted.append(formatHelpData(rawResult));
                break;
                
            default:
                formatted.append(rawResult);
        }
        
        return formatted.toString();
    }
    
    private String formatContractData(String rawData) {
        if (rawData.contains("No contracts found") || rawData.contains("No data")) {
            return "No contracts were found matching your criteria.";
        }
        
        // Basic formatting - you can enhance this based on your data structure
        String[] lines = rawData.split("\n");
        StringBuilder formatted = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().length() > 0) {
                formatted.append("• ").append(line.trim()).append("\n");
            }
        }
        
        return formatted.toString();
    }
    
    private String formatPartsData(String rawData) {
        if (rawData.contains("No parts found") || rawData.contains("No data")) {
            return "No parts were found matching your criteria.";
        }
        
        String[] lines = rawData.split("\n");
        StringBuilder formatted = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().length() > 0) {
                formatted.append("• ").append(line.trim()).append("\n");
            }
        }
        
        return formatted.toString();
    }
    
    private String formatFailedPartsData(String rawData) {
        if (rawData.contains("No failed parts") || rawData.contains("No data")) {
            return "No failed parts were found for your query.";
        }
        
        String[] lines = rawData.split("\n");
        StringBuilder formatted = new StringBuilder();
        formatted.append("?? Failed Parts Information:\n");
        
        for (String line : lines) {
            if (line.trim().length() > 0) {
                formatted.append("• ").append(line.trim()).append("\n");
            }
        }
        
        return formatted.toString();
    }
    
    private String formatAccountData(String rawData) {
        if (rawData.contains("No account found") || rawData.contains("No data")) {
            return "No account information was found matching your criteria.";
        }
        
        String[] lines = rawData.split("\n");
        StringBuilder formatted = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().length() > 0) {
                formatted.append("• ").append(line.trim()).append("\n");
            }
        }
        
        return formatted.toString();
    }
    
    private String formatHelpData(String rawData) {
        String[] steps = rawData.split("\n");
        StringBuilder formatted = new StringBuilder();
        
        int stepNumber = 1;
        for (String step : steps) {
            if (step.trim().length() > 0) {
                formatted.append(stepNumber).append(". ").append(step.trim()).append("\n");
                stepNumber++;
            }
        }
        
        return formatted.toString();
    }
    
    private ChatbotResponse formatNoDataFound(QueryType queryType) {
        String message;
        switch (queryType) {
            case CONTRACT_INFO:
                message = "I couldn't find any contracts matching your criteria. Please check the contract number and try again.";
                break;
            case PARTS_BY_CONTRACT:
                message = "No parts were found for the specified contract.";
                break;
            case FAILED_PARTS_BY_CONTRACT:
                message = "No failed parts were found for the specified contract.";
                break;
            case USER_CONTRACT_QUERY:
                message = "No contracts were found for the specified user.";
                break;
            case PARTS_INFO:
                message = "No parts information was found for the specified part number.";
                break;
            case ACCOUNT_INFO:
                message = "No account information was found for the specified account.";
                break;
            default:
                message = "No data was found for your request. Please try rephrasing your question.";
        }
        
        return ChatbotResponse.success(message, 0.8);
    }
    
    public ChatbotResponse formatError(String errorMessage) {
        return ChatbotResponse.error(
            "I'm sorry, I encountered an error while processing your request: " + errorMessage
        );
    }
    
    public ChatbotResponse formatUnknownIntent() {
        return ChatbotResponse.success(
            "I'm not sure I understand your request. Could you please rephrase it? " +
            "I can help you with contracts, parts, failed parts, account information, and contract creation guidance.",
            0.5
        );
    }
}
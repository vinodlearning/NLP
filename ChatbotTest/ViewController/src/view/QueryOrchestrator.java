package view;

import java.util.*;

public class QueryOrchestrator {
    private SecurityService securityService;
    private ChatModelServiceImpl chatModelService;
    
    public QueryOrchestrator() {
        this.securityService = new SecurityService();
        this.chatModelService = new ChatModelServiceImpl();
    }
    
    public ChatbotResponse processQuery(ParsedQuery parsedQuery) {
        try {
            System.out.println("Processing query: " + parsedQuery.getQueryType());
            
            // Security check
            String username = parsedQuery.getQueryParameters().get("requestingUser");
            if (!securityService.hasAccess(username, parsedQuery.getQueryType())) {
                return ChatbotResponse.error("Access denied for this query type");
            }
            
            // Route to appropriate handler based on query type
            switch (parsedQuery.getQueryType()) {
                case CONTRACT_INFO:
                    return handleContractInfo(parsedQuery);
                case USER_CONTRACT_QUERY:
                    return handleUserContractQuery(parsedQuery);
                case PARTS_INFO:
                    return handlePartsInfo(parsedQuery);
                case PARTS_BY_CONTRACT:
                    return handlePartsByContract(parsedQuery);
                case FAILED_PARTS_BY_CONTRACT:
                    return handleFailedPartsByContract(parsedQuery);
                case ACCOUNT_INFO:
                    return handleAccountInfo(parsedQuery);
                case HELP_CREATE_CONTRACT:
                    return handleHelpCreateContract(parsedQuery);
                default:
                    return ChatbotResponse.unknown(parsedQuery.getOriginalQuery());
            }
            
        } catch (Exception e) {
            System.out.println("Error in query orchestrator: " + e.getMessage());
            return ChatbotResponse.error(e.getMessage());
        }
    }
    
    private ChatbotResponse handleContractInfo(ParsedQuery parsedQuery) {
        String contractNumber = parsedQuery.getContractNumber();
        if (contractNumber == null) {
            return ChatbotResponse.error("Contract number not found in query");
        }
        
        // Simulate contract lookup
        String message = String.format("Contract %s Details:\n" +
            "Status: Active\n" +
            "Customer: Boeing\n" +
            "Start Date: 2024-01-15\n" +
            "End Date: 2024-12-31\n" +
            "Total Value: $2,500,000", contractNumber);
            
        ChatbotResponse response = ChatbotResponse.success(message, parsedQuery.getConfidence());
        response.addData("contractNumber", contractNumber);
        response.addData("status", "Active");
        return response;
    }
    
    private ChatbotResponse handleUserContractQuery(ParsedQuery parsedQuery) {
        String userName = parsedQuery.getUserName();
        if (userName == null) {
            return ChatbotResponse.error("User name not found in query");
        }
        
        String message = String.format("Contracts for %s:\n" +
            "1. Contract 123456 - Boeing (Active)\n" +
            "2. Contract 789012 - Honeywell (Completed)\n" +
            "3. Contract 345678 - Airbus (In Progress)", userName);
            
        return ChatbotResponse.success(message, parsedQuery.getConfidence());
    }
    
    private ChatbotResponse handlePartsInfo(ParsedQuery parsedQuery) {
        String partNumber = parsedQuery.getPartNumber();
        if (partNumber == null) {
            return ChatbotResponse.error("Part number not found in query");
        }
        
        String message = String.format("Part %s Information:\n" +
            "Description: Aerospace Component\n" +
            "Status: Active\n" +
            "Manufacturer: Honeywell\n" +
            "Lead Time: 6-8 weeks\n" +
            "Stock: Available", partNumber);
            
        return ChatbotResponse.success(message, parsedQuery.getConfidence());
    }
    
    private ChatbotResponse handlePartsByContract(ParsedQuery parsedQuery) {
        String contractNumber = parsedQuery.getContractNumber();
        if (contractNumber == null) {
            return ChatbotResponse.error("Contract number not found in query");
        }
        
        String message = String.format("Parts for Contract %s:\n" +
            "1. AE125 - Aerospace Component (Qty: 50)\n" +
            "2. BF230 - Navigation System (Qty: 25)\n" +
            "3. CG445 - Control Unit (Qty: 10)\n" +
            "Total Parts: 85", contractNumber);
            
        return ChatbotResponse.success(message, parsedQuery.getConfidence());
    }
    
    private ChatbotResponse handleFailedPartsByContract(ParsedQuery parsedQuery) {
        String contractNumber = parsedQuery.getContractNumber();
        if (contractNumber == null) {
            return ChatbotResponse.error("Contract number not found in query");
        }
        
        String message = String.format("Failed Parts for Contract %s:\n" +
            "1. AE125 - Failed during testing (Qty: 2)\n" +
            "2. BF230 - Manufacturing defect (Qty: 1)\n" +
            "Total Failed Parts: 3\n" +
            "Failure Rate: 3.5%%", contractNumber);
            
        return ChatbotResponse.success(message, parsedQuery.getConfidence());
    }
    
    private ChatbotResponse handleAccountInfo(ParsedQuery parsedQuery) {
        String accountNumber = parsedQuery.getAccountNumner();
        if (accountNumber == null) {
            return ChatbotResponse.error("Account number not found in query");
        }
        
        String message = String.format("Account %s Information:\n" +
            "Customer: Boeing Defense\n" +
            "Active Contracts: 5\n" +
            "Total Value: $12,500,000\n" +
            "Status: Active", accountNumber);
            
        return ChatbotResponse.success(message, parsedQuery.getConfidence());
    }
    
    private ChatbotResponse handleHelpCreateContract(ParsedQuery parsedQuery) {
        String message = "To create a new contract, you'll need:\n" +
            "1. Customer information\n" +
            "2. Contract terms and conditions\n" +
            "3. Parts list and quantities\n" +
            "4. Pricing information\n" +
            "5. Delivery schedule\n\n" +
            "Would you like me to guide you through the process?";
            
        ChatbotResponse response = ChatbotResponse.success(message, 0.95);
        response.addSuggestion("Yes, guide me through contract creation");
        response.addSuggestion("Show me contract templates");
        return response;
    }
}
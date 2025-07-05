package com.adf.nlp.models;

import com.adf.nlp.config.ConfigurationManager;
import com.adf.nlp.utils.InputAnalyzer;
import com.adf.nlp.database.DatabaseManager;
import java.util.*;
import java.util.logging.Logger;

/**
 * Contract Model using Configuration-Driven Architecture
 * All database queries and response templates are externalized in txt files
 */
public class ContractModel {
    private static final Logger logger = Logger.getLogger(ContractModel.class.getName());
    
    private final ConfigurationManager configManager;
    private final DatabaseManager databaseManager;
    
    /**
     * Constructor
     */
    public ContractModel() {
        this.configManager = ConfigurationManager.getInstance();
        this.databaseManager = new DatabaseManager(configManager);
    }
    
    /**
     * Process contract-related query
     */
    public String processQuery(String input, InputAnalyzer.AnalysisResult analysis) {
        try {
            // Determine query type based on input analysis
            QueryType queryType = determineQueryType(input, analysis);
            
            // Execute appropriate query
            switch (queryType) {
                case CONTRACT_BY_NUMBER:
                    return processContractByNumber(analysis);
                    
                case CONTRACT_BY_CUSTOMER:
                    return processContractByCustomer(input, analysis);
                    
                case CONTRACT_DATE_INFO:
                    return processContractDateInfo(analysis);
                    
                case CONTRACT_FINANCIAL_INFO:
                    return processContractFinancialInfo(analysis);
                    
                case CONTRACT_SEARCH:
                    return processContractSearch(input, analysis);
                    
                case CONTRACT_STATUS:
                    return processContractStatus(input, analysis);
                    
                default:
                    return processGeneralContractQuery(input, analysis);
            }
            
        } catch (Exception e) {
            logger.severe("Error processing contract query: " + e.getMessage());
            return configManager.getResponseTemplate("error_system_busy");
        }
    }
    
    /**
     * Determine query type based on input
     */
    private QueryType determineQueryType(String input, InputAnalyzer.AnalysisResult analysis) {
        String lowerInput = input.toLowerCase();
        
        // Check for specific identifier
        if (analysis.hasIdentifiers()) {
            if (containsDateKeywords(lowerInput)) {
                return QueryType.CONTRACT_DATE_INFO;
            }
            if (containsFinancialKeywords(lowerInput)) {
                return QueryType.CONTRACT_FINANCIAL_INFO;
            }
            return QueryType.CONTRACT_BY_NUMBER;
        }
        
        // Check for customer queries
        if (containsCustomerKeywords(lowerInput)) {
            return QueryType.CONTRACT_BY_CUSTOMER;
        }
        
        // Check for status queries
        if (containsStatusKeywords(lowerInput)) {
            return QueryType.CONTRACT_STATUS;
        }
        
        // Default to general search
        return QueryType.CONTRACT_SEARCH;
    }
    
    /**
     * Process contract by number query
     */
    private String processContractByNumber(InputAnalyzer.AnalysisResult analysis) {
        String contractNumber = analysis.getPrimaryIdentifier();
        if (contractNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("contract_number", contractNumber);
        
        String query = configManager.formatDatabaseQuery("contract_by_number", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("contract_number", contractNumber);
            return configManager.formatResponseTemplate("contract_not_found", responseParams);
        }
        
        Map<String, Object> contract = results.get(0);
        return formatContractResponse("contract_single_result", contract);
    }
    
    /**
     * Process contract by customer query
     */
    private String processContractByCustomer(String input, InputAnalyzer.AnalysisResult analysis) {
        String customerName = extractCustomerName(input);
        if (customerName == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("customer_name", customerName);
        
        String query = configManager.formatDatabaseQuery("contract_by_customer", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("contract_not_found");
        }
        
        if (results.size() == 1) {
            return formatContractResponse("contract_single_result", results.get(0));
        } else {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("count", results.size());
            responseParams.putAll(results.get(0));
            return configManager.formatResponseTemplate("contract_multiple_results", responseParams);
        }
    }
    
    /**
     * Process contract date information query
     */
    private String processContractDateInfo(InputAnalyzer.AnalysisResult analysis) {
        String contractNumber = analysis.getPrimaryIdentifier();
        if (contractNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("contract_number", contractNumber);
        
        String query = configManager.formatDatabaseQuery("contract_by_number", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("contract_not_found");
        }
        
        return formatContractResponse("contract_date_info", results.get(0));
    }
    
    /**
     * Process contract financial information query
     */
    private String processContractFinancialInfo(InputAnalyzer.AnalysisResult analysis) {
        String contractNumber = analysis.getPrimaryIdentifier();
        if (contractNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("contract_number", contractNumber);
        
        String query = configManager.formatDatabaseQuery("contract_by_number", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("contract_not_found");
        }
        
        return formatContractResponse("contract_financial_info", results.get(0));
    }
    
    /**
     * Process general contract search
     */
    private String processContractSearch(String input, InputAnalyzer.AnalysisResult analysis) {
        String searchTerm = extractSearchTerm(input);
        if (searchTerm == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("search_term", searchTerm);
        
        String query = configManager.formatDatabaseQuery("contract_search_all", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("no_results_found");
        }
        
        if (results.size() == 1) {
            return formatContractResponse("contract_single_result", results.get(0));
        } else {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("count", results.size());
            responseParams.putAll(results.get(0));
            return configManager.formatResponseTemplate("contract_multiple_results", responseParams);
        }
    }
    
    /**
     * Process contract status query
     */
    private String processContractStatus(String input, InputAnalyzer.AnalysisResult analysis) {
        String status = extractStatus(input);
        if (status == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        
        String query = configManager.formatDatabaseQuery("contract_by_status", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("no_results_found");
        }
        
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("count", results.size());
        responseParams.put("status", status);
        responseParams.putAll(results.get(0));
        
        return configManager.formatResponseTemplate("contract_multiple_results", responseParams);
    }
    
    /**
     * Process general contract query
     */
    private String processGeneralContractQuery(String input, InputAnalyzer.AnalysisResult analysis) {
        // Default to contract search
        return processContractSearch(input, analysis);
    }
    
    /**
     * Format contract response using template
     */
    private String formatContractResponse(String templateName, Map<String, Object> contractData) {
        Map<String, Object> responseParams = new HashMap<>();
        
        // Map database columns to response parameters
        responseParams.put("contract_number", contractData.get("AWARD_NUMBER"));
        responseParams.put("contract_name", contractData.get("CONTRACT_NAME"));
        responseParams.put("customer_name", contractData.get("CUSTOMER_NAME"));
        responseParams.put("customer_number", contractData.get("CUSTOMER_NUMBER"));
        responseParams.put("effective_date", contractData.get("EFFECTIVE_DATE"));
        responseParams.put("expiration_date", contractData.get("EXPIRATION_DATE"));
        responseParams.put("created_date", contractData.get("CREATED_DATE"));
        responseParams.put("status", contractData.get("STATUS"));
        responseParams.put("contract_type", contractData.get("CONTRACT_TYPE"));
        responseParams.put("amount", contractData.get("AMOUNT"));
        responseParams.put("price_list", contractData.get("PRICE_LIST"));
        responseParams.put("organization", contractData.get("ORGANIZATION"));
        responseParams.put("created_by", contractData.get("CREATED_BY"));
        
        return configManager.formatResponseTemplate(templateName, responseParams);
    }
    
    // Helper methods for keyword detection
    private boolean containsDateKeywords(String input) {
        return input.contains("date") || input.contains("effective") || input.contains("expiration") || 
               input.contains("expire") || input.contains("created");
    }
    
    private boolean containsFinancialKeywords(String input) {
        return input.contains("price") || input.contains("amount") || input.contains("cost") || 
               input.contains("value") || input.contains("financial");
    }
    
    private boolean containsCustomerKeywords(String input) {
        return input.contains("customer") || input.contains("client") || input.contains("by");
    }
    
    private boolean containsStatusKeywords(String input) {
        return input.contains("status") || input.contains("active") || input.contains("inactive") || 
               input.contains("pending") || input.contains("completed");
    }
    
    // Helper methods for data extraction
    private String extractCustomerName(String input) {
        // Simple extraction - look for words after "by", "customer", "client"
        String[] words = input.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word = words[i].toLowerCase();
            if (word.equals("by") || word.equals("customer") || word.equals("client")) {
                return words[i + 1];
            }
        }
        return null;
    }
    
    private String extractSearchTerm(String input) {
        // Extract meaningful terms, excluding common words
        String[] words = input.split("\\s+");
        List<String> meaningfulWords = new ArrayList<>();
        
        Set<String> commonWords = new HashSet<>(Arrays.asList(
            "show", "display", "get", "find", "search", "for", "the", "a", "an", "is", "are", "was", "were"
        ));
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (!commonWords.contains(cleanWord) && cleanWord.length() > 2) {
                meaningfulWords.add(cleanWord);
            }
        }
        
        return meaningfulWords.isEmpty() ? null : String.join(" ", meaningfulWords);
    }
    
    private String extractStatus(String input) {
        String lowerInput = input.toLowerCase();
        if (lowerInput.contains("active")) return "ACTIVE";
        if (lowerInput.contains("inactive")) return "INACTIVE";
        if (lowerInput.contains("pending")) return "PENDING";
        if (lowerInput.contains("completed")) return "COMPLETED";
        return null;
    }
    
    /**
     * Query type enumeration
     */
    private enum QueryType {
        CONTRACT_BY_NUMBER,
        CONTRACT_BY_CUSTOMER,
        CONTRACT_DATE_INFO,
        CONTRACT_FINANCIAL_INFO,
        CONTRACT_SEARCH,
        CONTRACT_STATUS
    }
}
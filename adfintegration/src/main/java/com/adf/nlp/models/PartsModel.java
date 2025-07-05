package com.adf.nlp.models;

import com.adf.nlp.config.ConfigurationManager;
import com.adf.nlp.utils.InputAnalyzer;
import com.adf.nlp.database.DatabaseManager;
import java.util.*;
import java.util.logging.Logger;

/**
 * Parts Model using Configuration-Driven Architecture
 * All database queries and response templates are externalized in txt files
 */
public class PartsModel {
    private static final Logger logger = Logger.getLogger(PartsModel.class.getName());
    
    private final ConfigurationManager configManager;
    private final DatabaseManager databaseManager;
    
    /**
     * Constructor
     */
    public PartsModel() {
        this.configManager = ConfigurationManager.getInstance();
        this.databaseManager = new DatabaseManager(configManager);
    }
    
    /**
     * Process parts-related query
     */
    public String processQuery(String input, InputAnalyzer.AnalysisResult analysis) {
        try {
            // Determine query type based on input analysis
            QueryType queryType = determineQueryType(input, analysis);
            
            // Execute appropriate query
            switch (queryType) {
                case PARTS_BY_CONTRACT:
                    return processPartsByContract(analysis);
                    
                case PARTS_BY_NUMBER:
                    return processPartsByNumber(analysis);
                    
                case PARTS_COUNT:
                    return processPartsCount(analysis);
                    
                case PARTS_AVAILABILITY:
                    return processPartsAvailability(input, analysis);
                    
                case PARTS_SUPPLIER:
                    return processPartsSupplier(input, analysis);
                    
                case PARTS_SEARCH:
                    return processPartsSearch(input, analysis);
                    
                default:
                    return processGeneralPartsQuery(input, analysis);
            }
            
        } catch (Exception e) {
            logger.severe("Error processing parts query: " + e.getMessage());
            return configManager.getResponseTemplate("error_system_busy");
        }
    }
    
    /**
     * Determine query type based on input
     */
    private QueryType determineQueryType(String input, InputAnalyzer.AnalysisResult analysis) {
        String lowerInput = input.toLowerCase();
        
        // Check for count queries
        if (containsCountKeywords(lowerInput)) {
            return QueryType.PARTS_COUNT;
        }
        
        // Check for availability queries
        if (containsAvailabilityKeywords(lowerInput)) {
            return QueryType.PARTS_AVAILABILITY;
        }
        
        // Check for supplier queries
        if (containsSupplierKeywords(lowerInput)) {
            return QueryType.PARTS_SUPPLIER;
        }
        
        // Check if we have identifiers
        if (analysis.hasIdentifiers()) {
            String identifier = analysis.getPrimaryIdentifier();
            if (isPartNumber(identifier)) {
                return QueryType.PARTS_BY_NUMBER;
            } else {
                return QueryType.PARTS_BY_CONTRACT;
            }
        }
        
        // Default to general search
        return QueryType.PARTS_SEARCH;
    }
    
    /**
     * Process parts by contract query
     */
    private String processPartsByContract(InputAnalyzer.AnalysisResult analysis) {
        String contractNumber = analysis.getPrimaryIdentifier();
        if (contractNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("contract_number", contractNumber);
        
        String query = configManager.formatDatabaseQuery("parts_by_contract", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("contract_number", contractNumber);
            return configManager.formatResponseTemplate("parts_not_found", responseParams);
        }
        
        if (results.size() == 1) {
            return formatPartsResponse("parts_single_result", results.get(0));
        } else {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("count", results.size());
            responseParams.put("contract_number", contractNumber);
            responseParams.putAll(results.get(0));
            return configManager.formatResponseTemplate("parts_multiple_results", responseParams);
        }
    }
    
    /**
     * Process parts by number query
     */
    private String processPartsByNumber(InputAnalyzer.AnalysisResult analysis) {
        String partNumber = analysis.getPrimaryIdentifier();
        if (partNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("part_number", partNumber);
        
        String query = configManager.formatDatabaseQuery("parts_by_number", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("parts_not_found");
        }
        
        return formatPartsResponse("parts_single_result", results.get(0));
    }
    
    /**
     * Process parts count query
     */
    private String processPartsCount(InputAnalyzer.AnalysisResult analysis) {
        String contractNumber = analysis.getPrimaryIdentifier();
        if (contractNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("contract_number", contractNumber);
        
        String query = configManager.formatDatabaseQuery("parts_count_by_contract", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("parts_not_found");
        }
        
        Map<String, Object> countData = results.get(0);
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("contract_number", contractNumber);
        responseParams.put("count", countData.get("PARTS_COUNT"));
        responseParams.put("total_value", countData.get("TOTAL_VALUE"));
        
        return configManager.formatResponseTemplate("parts_count_info", responseParams);
    }
    
    /**
     * Process parts availability query
     */
    private String processPartsAvailability(String input, InputAnalyzer.AnalysisResult analysis) {
        String partNumber = analysis.getPrimaryIdentifier();
        if (partNumber == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("part_number", partNumber);
        
        String query = configManager.formatDatabaseQuery("parts_by_number", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("parts_not_found");
        }
        
        return formatPartsResponse("parts_availability_info", results.get(0));
    }
    
    /**
     * Process parts supplier query
     */
    private String processPartsSupplier(String input, InputAnalyzer.AnalysisResult analysis) {
        String supplierName = extractSupplierName(input);
        if (supplierName != null) {
            return processPartsBySupplier(supplierName);
        }
        
        String partNumber = analysis.getPrimaryIdentifier();
        if (partNumber != null) {
            return processPartsSupplierInfo(partNumber);
        }
        
        return configManager.getResponseTemplate("error_invalid_input");
    }
    
    /**
     * Process parts by supplier
     */
    private String processPartsBySupplier(String supplierName) {
        Map<String, Object> params = new HashMap<>();
        params.put("supplier", supplierName);
        
        String query = configManager.formatDatabaseQuery("parts_by_supplier", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("parts_not_found");
        }
        
        if (results.size() == 1) {
            return formatPartsResponse("parts_single_result", results.get(0));
        } else {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("count", results.size());
            responseParams.put("supplier", supplierName);
            responseParams.putAll(results.get(0));
            return configManager.formatResponseTemplate("parts_multiple_results", responseParams);
        }
    }
    
    /**
     * Process parts supplier info
     */
    private String processPartsSupplierInfo(String partNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put("part_number", partNumber);
        
        String query = configManager.formatDatabaseQuery("parts_by_number", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("parts_not_found");
        }
        
        return formatPartsResponse("parts_supplier_info", results.get(0));
    }
    
    /**
     * Process general parts search
     */
    private String processPartsSearch(String input, InputAnalyzer.AnalysisResult analysis) {
        String searchTerm = extractSearchTerm(input);
        if (searchTerm == null) {
            return configManager.getResponseTemplate("error_invalid_input");
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("search_term", searchTerm);
        
        String query = configManager.formatDatabaseQuery("parts_search_all", params);
        List<Map<String, Object>> results = databaseManager.executeQuery(query);
        
        if (results.isEmpty()) {
            return configManager.getResponseTemplate("parts_not_found");
        }
        
        if (results.size() == 1) {
            return formatPartsResponse("parts_single_result", results.get(0));
        } else {
            Map<String, Object> responseParams = new HashMap<>();
            responseParams.put("count", results.size());
            responseParams.putAll(results.get(0));
            return configManager.formatResponseTemplate("parts_multiple_results", responseParams);
        }
    }
    
    /**
     * Process general parts query
     */
    private String processGeneralPartsQuery(String input, InputAnalyzer.AnalysisResult analysis) {
        // Default to parts search
        return processPartsSearch(input, analysis);
    }
    
    /**
     * Format parts response using template
     */
    private String formatPartsResponse(String templateName, Map<String, Object> partsData) {
        Map<String, Object> responseParams = new HashMap<>();
        
        // Map database columns to response parameters
        responseParams.put("part_number", partsData.get("PART_NUMBER"));
        responseParams.put("part_name", partsData.get("PART_NAME"));
        responseParams.put("contract_number", partsData.get("CONTRACT_NUMBER"));
        responseParams.put("line_number", partsData.get("LINE_NUMBER"));
        responseParams.put("quantity", partsData.get("QUANTITY"));
        responseParams.put("unit_price", partsData.get("UNIT_PRICE"));
        responseParams.put("total_price", partsData.get("TOTAL_PRICE"));
        responseParams.put("unit", partsData.get("UNIT"));
        responseParams.put("status", partsData.get("STATUS"));
        responseParams.put("availability", partsData.get("AVAILABILITY"));
        responseParams.put("delivery_date", partsData.get("DELIVERY_DATE"));
        responseParams.put("supplier", partsData.get("SUPPLIER"));
        responseParams.put("manufacturer", partsData.get("MANUFACTURER"));
        responseParams.put("description", partsData.get("DESCRIPTION"));
        responseParams.put("specification", partsData.get("SPECIFICATION"));
        
        return configManager.formatResponseTemplate(templateName, responseParams);
    }
    
    // Helper methods for keyword detection
    private boolean containsCountKeywords(String input) {
        return input.contains("count") || input.contains("how many") || input.contains("number of") || 
               input.contains("total") || input.contains("quantity");
    }
    
    private boolean containsAvailabilityKeywords(String input) {
        return input.contains("available") || input.contains("availability") || input.contains("stock") || 
               input.contains("inventory") || input.contains("delivery");
    }
    
    private boolean containsSupplierKeywords(String input) {
        return input.contains("supplier") || input.contains("vendor") || input.contains("manufacturer") || 
               input.contains("from") || input.contains("supplied by");
    }
    
    // Helper methods for data extraction
    private boolean isPartNumber(String identifier) {
        // Simple heuristic: part numbers often contain letters and numbers
        return identifier.matches(".*[a-zA-Z].*") && identifier.matches(".*[0-9].*");
    }
    
    private String extractSupplierName(String input) {
        // Look for supplier name after keywords
        String[] words = input.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word = words[i].toLowerCase();
            if (word.equals("supplier") || word.equals("vendor") || word.equals("from")) {
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
            "show", "display", "get", "find", "search", "for", "the", "a", "an", "is", "are", "was", "were",
            "part", "parts", "item", "items", "component", "components"
        ));
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            if (!commonWords.contains(cleanWord) && cleanWord.length() > 2) {
                meaningfulWords.add(cleanWord);
            }
        }
        
        return meaningfulWords.isEmpty() ? null : String.join(" ", meaningfulWords);
    }
    
    /**
     * Query type enumeration
     */
    private enum QueryType {
        PARTS_BY_CONTRACT,
        PARTS_BY_NUMBER,
        PARTS_COUNT,
        PARTS_AVAILABILITY,
        PARTS_SUPPLIER,
        PARTS_SEARCH
    }
}
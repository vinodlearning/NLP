package com.adf.nlp.controller;

import com.adf.nlp.response.NLPResponse;
import com.adf.nlp.response.NLPResponse.Header;
import com.adf.nlp.response.NLPResponse.QueryMetadata;
import com.adf.nlp.utils.ConfigurationLoader;
import com.adf.nlp.utils.SpellCorrector;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Enhanced NLP Controller that returns structured JSON objects
 * Implements the exact flowchart logic for ADF integration
 */
public class EnhancedNLPController {
    
    private static final ConfigurationLoader config = ConfigurationLoader.getInstance();
    private static final SpellCorrector spellCorrector = new SpellCorrector();
    
    // Compiled patterns for performance
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("\\b\\d{6,12}\\b");
    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile("\\b[A-Z]{2}\\d{3,6}\\b");
    private static final Pattern CUSTOMER_NUMBER_PATTERN = Pattern.compile("\\b\\d{4,8}\\b");
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\b");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("\\$?\\d+(?:,\\d{3})*(?:\\.\\d{2})?");
    
    // Status tracking
    private String lastProcessedQuery;
    private NLPResponse lastResponse;
    
    /**
     * Main entry point for processing natural language queries
     * @param userQuery The user's natural language query
     * @return NLPResponse object with structured JSON format
     */
    public NLPResponse processQuery(String userQuery) {
        long startTime = System.currentTimeMillis();
        
        // Validate input
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return createErrorResponse("INVALID_INPUT", "Query cannot be empty", startTime);
        }
        
        // Store for tracking
        this.lastProcessedQuery = userQuery;
        
        // Apply spell correction
        String correctedQuery = spellCorrector.correctSpelling(userQuery.toLowerCase());
        
        // Determine query type using flowchart logic
        String queryType = determineQueryType(correctedQuery);
        
        // Process based on query type
        NLPResponse response;
        switch (queryType) {
            case "CONTRACT_QUERY":
                response = processContractQuery(correctedQuery, startTime);
                break;
            case "PARTS_QUERY":
                response = processPartsQuery(correctedQuery, startTime);
                break;
            case "HELP_QUERY":
                response = processHelpQuery(correctedQuery, startTime);
                break;
            case "PARTS_CREATE_ERROR":
                response = createPartsCreateErrorResponse(correctedQuery, startTime);
                break;
            default:
                response = createErrorResponse("UNKNOWN_QUERY_TYPE", 
                    "Unable to determine query type", startTime);
        }
        
        // Store last response
        this.lastResponse = response;
        
        return response;
    }
    
    /**
     * Determine query type using flowchart logic
     */
    private String determineQueryType(String query) {
        Set<String> queryWords = new HashSet<>(Arrays.asList(query.split("\\s+")));
        
        boolean hasPartsKeywords = config.hasPartsKeywords(queryWords);
        boolean hasCreateKeywords = config.hasCreateKeywords(queryWords);
        
        // Flowchart logic implementation
        if (hasPartsKeywords && hasCreateKeywords) {
            return "PARTS_CREATE_ERROR";
        } else if (hasPartsKeywords) {
            return "PARTS_QUERY";
        } else if (hasCreateKeywords) {
            return "HELP_QUERY";
        } else {
            return "CONTRACT_QUERY";
        }
    }
    
    /**
     * Process contract-related queries
     */
    private NLPResponse processContractQuery(String query, long startTime) {
        NLPResponse response = new NLPResponse();
        
        // Set query metadata
        response.setQueryMetadata(new QueryMetadata("CONTRACT", "QUERY", 
            System.currentTimeMillis() - startTime));
        
        // Extract contract information
        Header header = extractContractHeader(query);
        response.setHeader(header);
        
        // Determine action type and add entities
        String actionType = determineContractActionType(query);
        response.getQueryMetadata().setActionType(actionType);
        
        // Add entities based on action type
        addContractEntities(response, query, actionType);
        
        // Add display entities
        addContractDisplayEntities(response, actionType);
        
        return response;
    }
    
    /**
     * Process parts-related queries
     */
    private NLPResponse processPartsQuery(String query, long startTime) {
        NLPResponse response = new NLPResponse();
        
        // Set query metadata
        response.setQueryMetadata(new QueryMetadata("PARTS", "QUERY", 
            System.currentTimeMillis() - startTime));
        
        // Extract parts information
        Header header = extractPartsHeader(query);
        response.setHeader(header);
        
        // Determine action type
        String actionType = determinePartsActionType(query);
        response.getQueryMetadata().setActionType(actionType);
        
        // Add entities based on action type
        addPartsEntities(response, query, actionType);
        
        // Add display entities
        addPartsDisplayEntities(response, actionType);
        
        return response;
    }
    
    /**
     * Process help/creation queries
     */
    private NLPResponse processHelpQuery(String query, long startTime) {
        NLPResponse response = new NLPResponse();
        
        // Set query metadata
        response.setQueryMetadata(new QueryMetadata("HELP", "GUIDE", 
            System.currentTimeMillis() - startTime));
        
        // Help queries don't typically have header information
        // but we can extract if there are any identifiers
        Header header = extractHelpHeader(query);
        response.setHeader(header);
        
        // Add display entities for help
        addHelpDisplayEntities(response, query);
        
        return response;
    }
    
    /**
     * Create error response for parts creation attempts
     */
    private NLPResponse createPartsCreateErrorResponse(String query, long startTime) {
        NLPResponse response = new NLPResponse();
        
        // Set query metadata
        response.setQueryMetadata(new QueryMetadata("PARTS", "CREATE_ERROR", 
            System.currentTimeMillis() - startTime));
        
        // Add error
        response.addError("PARTS_CREATE_NOT_SUPPORTED", 
            "Parts cannot be created through this system. Parts are loaded from Excel files. " +
            "You can view existing parts or get help with contract creation.");
        
        // Add display entities to guide user
        response.addDisplayEntity("Available Actions");
        response.addDisplayEntity("View Parts");
        response.addDisplayEntity("Contract Creation Help");
        
        return response;
    }
    
    /**
     * Extract contract header information
     */
    private Header extractContractHeader(String query) {
        Header header = new Header();
        
        // Extract contract number
        String contractNumber = extractPattern(query, CONTRACT_NUMBER_PATTERN);
        if (contractNumber != null) {
            header.setContractNumber(contractNumber);
        }
        
        // Extract customer information
        String customerNumber = extractCustomerNumber(query);
        if (customerNumber != null) {
            header.setCustomerNumber(customerNumber);
        }
        
        // Extract created by information
        String createdBy = extractCreatedBy(query);
        if (createdBy != null) {
            header.setCreatedBy(createdBy);
        }
        
        return header;
    }
    
    /**
     * Extract parts header information
     */
    private Header extractPartsHeader(String query) {
        Header header = new Header();
        
        // Extract part number
        String partNumber = extractPattern(query, PART_NUMBER_PATTERN);
        if (partNumber != null) {
            header.setPartNumber(partNumber);
        }
        
        // Extract contract number (parts are associated with contracts)
        String contractNumber = extractPattern(query, CONTRACT_NUMBER_PATTERN);
        if (contractNumber != null) {
            header.setContractNumber(contractNumber);
        }
        
        return header;
    }
    
    /**
     * Extract help header information
     */
    private Header extractHelpHeader(String query) {
        Header header = new Header();
        
        // Help queries might reference contract numbers for context
        String contractNumber = extractPattern(query, CONTRACT_NUMBER_PATTERN);
        if (contractNumber != null) {
            header.setContractNumber(contractNumber);
        }
        
        return header;
    }
    
    /**
     * Determine contract action type
     */
    private String determineContractActionType(String query) {
        if (query.contains("show") || query.contains("display") || query.contains("view")) {
            return "VIEW";
        } else if (query.contains("effective") || query.contains("expiration") || query.contains("date")) {
            return "DATE_INQUIRY";
        } else if (query.contains("price") || query.contains("amount") || query.contains("cost")) {
            return "PRICING_INQUIRY";
        } else if (query.contains("status") || query.contains("active") || query.contains("inactive")) {
            return "STATUS_INQUIRY";
        } else if (query.contains("customer") || query.contains("account")) {
            return "CUSTOMER_INQUIRY";
        } else {
            return "GENERAL_INQUIRY";
        }
    }
    
    /**
     * Determine parts action type
     */
    private String determinePartsActionType(String query) {
        if (query.contains("list") || query.contains("show") || query.contains("display")) {
            return "LIST";
        } else if (query.contains("count") || query.contains("how many") || query.contains("number")) {
            return "COUNT";
        } else if (query.contains("price") || query.contains("cost") || query.contains("amount")) {
            return "PRICING";
        } else if (query.contains("available") || query.contains("inventory") || query.contains("stock")) {
            return "AVAILABILITY";
        } else {
            return "GENERAL";
        }
    }
    
    /**
     * Add contract entities based on action type
     */
    private void addContractEntities(NLPResponse response, String query, String actionType) {
        switch (actionType) {
            case "DATE_INQUIRY":
                if (query.contains("effective")) {
                    response.addEntity("effectiveDate", "IS_NOT_NULL", "true");
                }
                if (query.contains("expiration")) {
                    response.addEntity("expirationDate", "IS_NOT_NULL", "true");
                }
                break;
            case "PRICING_INQUIRY":
                response.addEntity("contractAmount", "IS_NOT_NULL", "true");
                break;
            case "STATUS_INQUIRY":
                response.addEntity("contractStatus", "IS_NOT_NULL", "true");
                break;
            case "CUSTOMER_INQUIRY":
                response.addEntity("customerName", "IS_NOT_NULL", "true");
                response.addEntity("customerNumber", "IS_NOT_NULL", "true");
                break;
        }
        
        // Add date range filters if dates are mentioned
        String dateValue = extractPattern(query, DATE_PATTERN);
        if (dateValue != null) {
            response.addEntity("dateFilter", "EQUALS", dateValue);
        }
    }
    
    /**
     * Add parts entities based on action type
     */
    private void addPartsEntities(NLPResponse response, String query, String actionType) {
        switch (actionType) {
            case "PRICING":
                response.addEntity("partPrice", "IS_NOT_NULL", "true");
                break;
            case "AVAILABILITY":
                response.addEntity("availableQuantity", "GREATER_THAN", "0");
                break;
            case "COUNT":
                response.addEntity("partCount", "COUNT", "*");
                break;
        }
        
        // Add contract filter if contract number is present
        if (response.getHeader().getContractNumber() != null) {
            response.addEntity("contractNumber", "EQUALS", response.getHeader().getContractNumber());
        }
    }
    
    /**
     * Add contract display entities
     */
    private void addContractDisplayEntities(NLPResponse response, String actionType) {
        response.addDisplayEntity("Contract Number");
        response.addDisplayEntity("Customer Name");
        
        switch (actionType) {
            case "DATE_INQUIRY":
                response.addDisplayEntity("Effective Date");
                response.addDisplayEntity("Expiration Date");
                break;
            case "PRICING_INQUIRY":
                response.addDisplayEntity("Contract Amount");
                break;
            case "STATUS_INQUIRY":
                response.addDisplayEntity("Contract Status");
                break;
            case "CUSTOMER_INQUIRY":
                response.addDisplayEntity("Customer Number");
                response.addDisplayEntity("Account Manager");
                break;
            default:
                response.addDisplayEntity("Contract Status");
                response.addDisplayEntity("Effective Date");
                response.addDisplayEntity("Contract Amount");
        }
    }
    
    /**
     * Add parts display entities
     */
    private void addPartsDisplayEntities(NLPResponse response, String actionType) {
        response.addDisplayEntity("Part Number");
        response.addDisplayEntity("Part Description");
        
        switch (actionType) {
            case "PRICING":
                response.addDisplayEntity("Part Price");
                response.addDisplayEntity("Unit Cost");
                break;
            case "AVAILABILITY":
                response.addDisplayEntity("Available Quantity");
                response.addDisplayEntity("Inventory Status");
                break;
            case "COUNT":
                response.addDisplayEntity("Total Parts Count");
                break;
            default:
                response.addDisplayEntity("Part Price");
                response.addDisplayEntity("Available Quantity");
        }
    }
    
    /**
     * Add help display entities
     */
    private void addHelpDisplayEntities(NLPResponse response, String query) {
        response.addDisplayEntity("Contract Creation Steps");
        response.addDisplayEntity("Required Information");
        response.addDisplayEntity("Validation Rules");
        
        if (query.contains("approval") || query.contains("workflow")) {
            response.addDisplayEntity("Approval Process");
        }
        
        if (query.contains("template") || query.contains("format")) {
            response.addDisplayEntity("Contract Templates");
        }
    }
    
    /**
     * Extract pattern from query
     */
    private String extractPattern(String query, Pattern pattern) {
        java.util.regex.Matcher matcher = pattern.matcher(query);
        return matcher.find() ? matcher.group() : null;
    }
    
    /**
     * Extract customer number with business logic
     */
    private String extractCustomerNumber(String query) {
        // Look for customer/account keywords followed by numbers
        String[] words = query.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].matches("customer|account|client")) {
                String nextWord = words[i + 1];
                if (nextWord.matches("\\d{4,8}")) {
                    return nextWord;
                }
            }
        }
        return null;
    }
    
    /**
     * Extract created by information
     */
    private String extractCreatedBy(String query) {
        // Look for "created by" or "by" followed by names
        String[] words = query.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equals("by") || (words[i].equals("created") && i < words.length - 2 && words[i + 1].equals("by"))) {
                int nameIndex = words[i].equals("by") ? i + 1 : i + 2;
                if (nameIndex < words.length && words[nameIndex].matches("[a-zA-Z]+")) {
                    return words[nameIndex];
                }
            }
        }
        return null;
    }
    
    /**
     * Create error response
     */
    private NLPResponse createErrorResponse(String errorCode, String errorMessage, long startTime) {
        NLPResponse response = new NLPResponse();
        response.setQueryMetadata(new QueryMetadata("ERROR", "ERROR", 
            System.currentTimeMillis() - startTime));
        response.addError(errorCode, errorMessage);
        return response;
    }
    
    // Getters for JSF integration
    public String getLastProcessedQuery() {
        return lastProcessedQuery;
    }
    
    public NLPResponse getLastResponse() {
        return lastResponse;
    }
    
    public String getLastResponseJson() {
        return lastResponse != null ? lastResponse.toString() : "{}";
    }
    
    /**
     * Test method for direct invocation
     */
    public String testQuery(String query) {
        NLPResponse response = processQuery(query);
        return response.toString();
    }
}
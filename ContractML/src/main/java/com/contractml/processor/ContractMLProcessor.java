package com.contractml.processor;

import com.contractml.response.ContractMLResponse;
import com.contractml.response.ContractMLResponse.Header;
import com.contractml.response.ContractMLResponse.QueryMetadata;
import com.contractml.utils.SpellCorrector;
import com.contractml.utils.KeywordMatcher;
import com.contractml.utils.EntityExtractor;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * ContractML Processor - Main business logic class
 * 
 * Handles:
 * - Query preprocessing (spell correction, normalization)
 * - Entity extraction (contract numbers, part numbers, customer info)
 * - Intent classification (contracts vs parts queries)
 * - Response generation with proper JSON structure
 */
public class ContractMLProcessor {
    
    private final SpellCorrector spellCorrector;
    private final KeywordMatcher keywordMatcher;
    private final EntityExtractor entityExtractor;
    
    // Contract query patterns
    private static final String[] CONTRACT_KEYWORDS = {
        "contract", "agreement", "deal", "effective", "expiration", "price", 
        "customer", "terms", "status", "created", "signed", "renewed"
    };
    
    // Parts query patterns
    private static final String[] PARTS_KEYWORDS = {
        "part", "parts", "component", "item", "inventory", "stock", 
        "quantity", "available", "specification", "model"
    };
    
    // Display field mappings
    private static final Map<String, String> DISPLAY_FIELD_MAPPINGS = new HashMap<>();
    static {
        DISPLAY_FIELD_MAPPINGS.put("effective", "EFFECTIVE_DATE");
        DISPLAY_FIELD_MAPPINGS.put("expiration", "EXPIRATION_DATE");
        DISPLAY_FIELD_MAPPINGS.put("price", "PRICE");
        DISPLAY_FIELD_MAPPINGS.put("customer", "CUSTOMER_NAME");
        DISPLAY_FIELD_MAPPINGS.put("terms", "TERMS");
        DISPLAY_FIELD_MAPPINGS.put("status", "STATUS");
        DISPLAY_FIELD_MAPPINGS.put("created", "CREATED_DATE");
        DISPLAY_FIELD_MAPPINGS.put("quantity", "QUANTITY");
        DISPLAY_FIELD_MAPPINGS.put("available", "AVAILABLE_QUANTITY");
        DISPLAY_FIELD_MAPPINGS.put("specification", "SPECIFICATION");
        DISPLAY_FIELD_MAPPINGS.put("model", "MODEL");
    }
    
    /**
     * Constructor
     */
    public ContractMLProcessor() {
        this.spellCorrector = new SpellCorrector();
        this.keywordMatcher = new KeywordMatcher();
        this.entityExtractor = new EntityExtractor();
    }
    
    /**
     * Main processing method
     */
    public ContractMLResponse processQuery(String query) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Step 1: Preprocess query
            String correctedQuery = spellCorrector.correctSpelling(query);
            String normalizedQuery = normalizeQuery(correctedQuery);
            
            // Step 2: Extract entities
            Map<String, String> entities = entityExtractor.extractEntities(normalizedQuery);
            
            // Step 3: Classify intent
            String queryType = classifyIntent(normalizedQuery);
            
            // Step 4: Generate response
            ContractMLResponse response = generateResponse(
                normalizedQuery, entities, queryType, startTime
            );
            
            return response;
            
        } catch (Exception e) {
            // Error handling
            ContractMLResponse errorResponse = new ContractMLResponse();
            errorResponse.addError("PROCESSING_ERROR", "Failed to process query: " + e.getMessage());
            errorResponse.setQueryMetadata(new QueryMetadata("UNKNOWN", "error", 
                System.currentTimeMillis() - startTime));
            return errorResponse;
        }
    }
    
    /**
     * Normalize query - lowercase, remove extra spaces
     */
    private String normalizeQuery(String query) {
        if (query == null) return "";
        return query.toLowerCase().trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Classify intent - determine if query is about contracts or parts
     */
    private String classifyIntent(String query) {
        int contractScore = 0;
        int partsScore = 0;
        
        // Score based on keywords
        for (String keyword : CONTRACT_KEYWORDS) {
            if (query.contains(keyword)) {
                contractScore++;
            }
        }
        
        for (String keyword : PARTS_KEYWORDS) {
            if (query.contains(keyword)) {
                partsScore++;
            }
        }
        
        // Special handling for ambiguous cases
        if (partsScore > 0 && contractScore > 0) {
            // If both present, check for specific patterns
            if (query.contains("part") && (query.contains("contract") || 
                entityExtractor.hasContractNumber(query))) {
                return "PARTS"; // Parts within a contract context
            }
        }
        
        return partsScore > contractScore ? "PARTS" : "CONTRACTS";
    }
    
    /**
     * Generate response based on processed query
     */
    private ContractMLResponse generateResponse(String query, Map<String, String> entities, 
                                               String queryType, long startTime) {
        ContractMLResponse response = new ContractMLResponse();
        
        // Set header information
        Header header = response.getHeader();
        if (entities.containsKey("contractNumber")) {
            header.setContractNumber(entities.get("contractNumber"));
        }
        if (entities.containsKey("partNumber")) {
            header.setPartNumber(entities.get("partNumber"));
        }
        if (entities.containsKey("customerNumber")) {
            header.setCustomerNumber(entities.get("customerNumber"));
        }
        if (entities.containsKey("customerName")) {
            header.setCustomerName(entities.get("customerName"));
        }
        if (entities.containsKey("createdBy")) {
            header.setCreatedBy(entities.get("createdBy"));
        }
        
        // Set query metadata
        String actionType = determineActionType(query, queryType, entities);
        long processingTime = System.currentTimeMillis() - startTime;
        response.setQueryMetadata(new QueryMetadata(queryType, actionType, processingTime));
        
        // Add entity filters
        addEntityFilters(response, entities);
        
        // Add display entities
        addDisplayEntities(response, query);
        
        // Validate response
        if (!response.isValid()) {
            response.addError("INVALID_QUERY", "Query does not contain valid contract or part identifiers");
        }
        
        return response;
    }
    
    /**
     * Determine action type based on query analysis
     */
    private String determineActionType(String query, String queryType, Map<String, String> entities) {
        if ("CONTRACTS".equals(queryType)) {
            if (entities.containsKey("contractNumber")) {
                if (query.contains("effective") || query.contains("expiration")) {
                    return "contracts_by_contractNumber_dates";
                } else if (query.contains("price") || query.contains("cost")) {
                    return "contracts_by_contractNumber_pricing";
                } else if (query.contains("customer")) {
                    return "contracts_by_contractNumber_customer";
                } else {
                    return "contracts_by_contractNumber";
                }
            } else if (entities.containsKey("customerName") || entities.containsKey("customerNumber")) {
                return "contracts_by_customer";
            } else if (entities.containsKey("createdBy")) {
                return "contracts_by_createdBy";
            } else {
                return "contracts_general";
            }
        } else { // PARTS
            if (entities.containsKey("partNumber")) {
                if (entities.containsKey("contractNumber")) {
                    return "parts_by_partNumber_contractNumber";
                } else {
                    return "parts_by_partNumber";
                }
            } else if (entities.containsKey("contractNumber")) {
                return "parts_by_contractNumber";
            } else {
                return "parts_general";
            }
        }
    }
    
    /**
     * Add entity filters to response
     */
    private void addEntityFilters(ContractMLResponse response, Map<String, String> entities) {
        for (Map.Entry<String, String> entry : entities.entrySet()) {
            String attribute = entry.getKey();
            String value = entry.getValue();
            
            // Map internal attribute names to response attribute names
            String responseAttribute = mapAttributeName(attribute);
            response.addEntity(responseAttribute, "equals", value);
        }
    }
    
    /**
     * Map internal attribute names to response format
     */
    private String mapAttributeName(String internalName) {
        switch (internalName) {
            case "contractNumber": return "contractNumber";
            case "partNumber": return "partNumber";
            case "customerNumber": return "customerNumber";
            case "customerName": return "customerName";
            case "createdBy": return "createdBy";
            default: return internalName;
        }
    }
    
    /**
     * Add display entities based on query content
     */
    private void addDisplayEntities(ContractMLResponse response, String query) {
        // Analyze query for display requirements
        for (Map.Entry<String, String> entry : DISPLAY_FIELD_MAPPINGS.entrySet()) {
            String keyword = entry.getKey();
            String fieldName = entry.getValue();
            
            if (query.contains(keyword)) {
                response.addDisplayEntity(fieldName);
            }
        }
        
        // Default display entities based on query type
        if (response.getDisplayEntities().isEmpty()) {
            if ("CONTRACTS".equals(response.getQueryMetadata().getQueryType())) {
                response.addDisplayEntity("CONTRACT_NUMBER");
                response.addDisplayEntity("CUSTOMER_NAME");
                response.addDisplayEntity("STATUS");
            } else {
                response.addDisplayEntity("PART_NUMBER");
                response.addDisplayEntity("QUANTITY");
                response.addDisplayEntity("AVAILABLE_QUANTITY");
            }
        }
    }
    
    /**
     * Get processing statistics
     */
    public Map<String, Object> getProcessingStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("spellCorrections", spellCorrector.getCorrectionCount());
        stats.put("entitiesExtracted", entityExtractor.getExtractionCount());
        stats.put("queriesProcessed", getQueryCount());
        return stats;
    }
    
    // Simple query counter
    private static int queryCount = 0;
    private int getQueryCount() {
        return ++queryCount;
    }
}
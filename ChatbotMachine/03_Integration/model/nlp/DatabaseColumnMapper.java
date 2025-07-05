package model.nlp;

import java.util.*;

/**
 * Database Column Mapper for Oracle ADF Integration
 * Maps user-friendly query terms to actual database column names
 * 
 * Handles mapping between:
 * - User terms (contract, award, part, customer, etc.)
 * - Database columns (AWARD_NUMBER, CUSTOMER_NAME, etc.)
 * 
 * @author Contract Query Processing System
 * @version 1.0 - Database Schema Integration
 */
public class DatabaseColumnMapper {
    
    // Contract table column mappings
    private static final Map<String, String> CONTRACT_COLUMN_MAPPINGS = new HashMap<>();
    
    // Parts table column mappings  
    private static final Map<String, String> PARTS_COLUMN_MAPPINGS = new HashMap<>();
    
    // User-friendly term mappings
    private static final Map<String, String> USER_TERM_MAPPINGS = new HashMap<>();
    
    static {
        initializeContractMappings();
        initializePartsMappings();
        initializeUserTermMappings();
    }
    
    /**
     * Initialize contract table column mappings
     */
    private static void initializeContractMappings() {
        // Core contract identification
        CONTRACT_COLUMN_MAPPINGS.put("contract_number", "AWARD_NUMBER");
        CONTRACT_COLUMN_MAPPINGS.put("award_number", "AWARD_NUMBER");
        CONTRACT_COLUMN_MAPPINGS.put("contract_name", "CONTRACT_NAME");
        CONTRACT_COLUMN_MAPPINGS.put("external_contract_number", "EXTERNAL_CONTRACT_NUMBER");
        
        // Customer information
        CONTRACT_COLUMN_MAPPINGS.put("customer_name", "CUSTOMER_NAME");
        CONTRACT_COLUMN_MAPPINGS.put("customer_number", "CUSTOMER_NUMBER");
        CONTRACT_COLUMN_MAPPINGS.put("alternate_customers", "ALTERNATE_CUSTOMERS");
        
        // Dates
        CONTRACT_COLUMN_MAPPINGS.put("effective_date", "EFFECTIVE_DATE");
        CONTRACT_COLUMN_MAPPINGS.put("expiration_date", "EXPIRATION_DATE");
        CONTRACT_COLUMN_MAPPINGS.put("price_expiration_date", "PRICE_EXPIRATION_DATE");
        CONTRACT_COLUMN_MAPPINGS.put("create_date", "CREATE_DATE");
        CONTRACT_COLUMN_MAPPINGS.put("updated_date", "UPDATED_DATE");
        
        // User tracking
        CONTRACT_COLUMN_MAPPINGS.put("created_by", "CREATED_BY");
        CONTRACT_COLUMN_MAPPINGS.put("updated_by", "UPDATED_BY");
        
        // Contract details
        CONTRACT_COLUMN_MAPPINGS.put("status", "STATUS");
        CONTRACT_COLUMN_MAPPINGS.put("contract_type", "CONTRACT_TYPE");
        CONTRACT_COLUMN_MAPPINGS.put("project_type", "PROJECT_TYPE");
        CONTRACT_COLUMN_MAPPINGS.put("price_list", "PRICE_LIST");
        CONTRACT_COLUMN_MAPPINGS.put("opportunity_number", "OPPORTUNITY_NUMBER");
        
        // Business terms
        CONTRACT_COLUMN_MAPPINGS.put("payment_terms", "PAYMENT_TERMS");
        CONTRACT_COLUMN_MAPPINGS.put("currency", "CURRENCY");
        CONTRACT_COLUMN_MAPPINGS.put("contract_length", "CONTRACT_LENGTH");
        
        // Flags and indicators
        CONTRACT_COLUMN_MAPPINGS.put("is_program", "IS_PROGRAM");
        CONTRACT_COLUMN_MAPPINGS.put("is_hpp_unpriced_contract", "IS_HPP_UNPRICED_CONTRACT");
        CONTRACT_COLUMN_MAPPINGS.put("service_fee_applies", "SERVICE_FEE_APPLIES");
        
        // Additional fields
        CONTRACT_COLUMN_MAPPINGS.put("title", "TITLE");
        CONTRACT_COLUMN_MAPPINGS.put("description", "DESCRIPTION");
        CONTRACT_COLUMN_MAPPINGS.put("comments", "COMMENTS");
        CONTRACT_COLUMN_MAPPINGS.put("summary", "SUMMARY");
        CONTRACT_COLUMN_MAPPINGS.put("target_margin", "TARGET_MARGIN");
        CONTRACT_COLUMN_MAPPINGS.put("total_part_count", "TOTAL_PART_COUNT");
    }
    
    /**
     * Initialize parts table column mappings
     */
    private static void initializePartsMappings() {
        // Core part identification
        PARTS_COLUMN_MAPPINGS.put("award_number", "AWARD_NUMBER");
        PARTS_COLUMN_MAPPINGS.put("contract_number", "AWARD_NUMBER");  // Map to AWARD_NUMBER
        PARTS_COLUMN_MAPPINGS.put("line_no", "LINE_NO");
        PARTS_COLUMN_MAPPINGS.put("part_number", "INVOICE_PART_NUMBER");
        PARTS_COLUMN_MAPPINGS.put("nsn_part_number", "NSN_PART_NUMBER");
        PARTS_COLUMN_MAPPINGS.put("sap_number", "SAP_NUMBER");
        
        // Pricing information
        PARTS_COLUMN_MAPPINGS.put("price", "PRICE");
        PARTS_COLUMN_MAPPINGS.put("future_price", "FUTURE_PRICE");
        PARTS_COLUMN_MAPPINGS.put("future_price2", "FUTURE_PRICE2");
        PARTS_COLUMN_MAPPINGS.put("future_price3", "FUTURE_PRICE3");
        PARTS_COLUMN_MAPPINGS.put("prev_price", "PREV_PRICE");
        PARTS_COLUMN_MAPPINGS.put("quote_cost", "QUOTE_COST");
        
        // Dates
        PARTS_COLUMN_MAPPINGS.put("effective_date", "EFFECTIVE_DATE");
        PARTS_COLUMN_MAPPINGS.put("part_expiration_date", "PART_EXPIRATION_DATE");
        PARTS_COLUMN_MAPPINGS.put("f_price_effective_date", "F_PRICE_EFFECTIVE_DATE");
        PARTS_COLUMN_MAPPINGS.put("date_loaded", "DATE_LOADED");
        PARTS_COLUMN_MAPPINGS.put("creation_date", "CREATION_DATE");
        PARTS_COLUMN_MAPPINGS.put("last_update_date", "LAST_UPDATE_DATE");
        
        // User tracking
        PARTS_COLUMN_MAPPINGS.put("created_by", "CREATED_BY");
        PARTS_COLUMN_MAPPINGS.put("last_updated_by", "LAST_UPDATED_BY");
        
        // Status and classification
        PARTS_COLUMN_MAPPINGS.put("status", "STATUS");
        PARTS_COLUMN_MAPPINGS.put("csm_status", "CSM_STATUS");
        PARTS_COLUMN_MAPPINGS.put("item_classification", "ITEM_CLASSIFICATION");
        
        // Quantities and specifications
        PARTS_COLUMN_MAPPINGS.put("moq", "MOQ");
        PARTS_COLUMN_MAPPINGS.put("eau", "EAU");
        PARTS_COLUMN_MAPPINGS.put("uom", "UOM");
        PARTS_COLUMN_MAPPINGS.put("lead_time", "LEAD_TIME");
        PARTS_COLUMN_MAPPINGS.put("tot_con_qty_req", "TOT_CON_QTY_REQ");
        
        // Comments and references
        PARTS_COLUMN_MAPPINGS.put("comments", "COMMENTS");
        PARTS_COLUMN_MAPPINGS.put("award_rep_comments", "AWARD_REP_COMMENTS");
        PARTS_COLUMN_MAPPINGS.put("purchase_comments", "PURCHASE_COMMENTS");
        PARTS_COLUMN_MAPPINGS.put("sales_comments", "SALES_COMMENTS");
        PARTS_COLUMN_MAPPINGS.put("planning_comments", "PLANNING_COMMENTS");
        PARTS_COLUMN_MAPPINGS.put("customer_response", "CUSTOMER_RESPONSE");
        PARTS_COLUMN_MAPPINGS.put("customer_reference", "CUSTOMER_REFERENCE");
        
        // External references
        PARTS_COLUMN_MAPPINGS.put("external_contract_no", "EXTERNAL_CONTRACT_NO");
        PARTS_COLUMN_MAPPINGS.put("external_line_no", "EXTERNAL_LINE_NO");
        PARTS_COLUMN_MAPPINGS.put("opportunity_number", "OPPORTUNITY_NUMBER");
        
        // Additional fields
        PARTS_COLUMN_MAPPINGS.put("plant", "PLANT");
        PARTS_COLUMN_MAPPINGS.put("prime", "PRIME");
        PARTS_COLUMN_MAPPINGS.put("award_tags", "AWARD_TAGS");
        PARTS_COLUMN_MAPPINGS.put("applicable_contract", "APPLICABLE_CONTRACT");
    }
    
    /**
     * Initialize user-friendly term mappings
     */
    private static void initializeUserTermMappings() {
        // Contract/Award synonyms
        USER_TERM_MAPPINGS.put("contract", "award_number");
        USER_TERM_MAPPINGS.put("award", "award_number");
        USER_TERM_MAPPINGS.put("contract number", "award_number");
        USER_TERM_MAPPINGS.put("award number", "award_number");
        USER_TERM_MAPPINGS.put("contract id", "award_number");
        USER_TERM_MAPPINGS.put("award id", "award_number");
        
        // Part synonyms
        USER_TERM_MAPPINGS.put("part", "part_number");
        USER_TERM_MAPPINGS.put("part number", "part_number");
        USER_TERM_MAPPINGS.put("invoice part", "part_number");
        USER_TERM_MAPPINGS.put("line", "line_no");
        USER_TERM_MAPPINGS.put("line number", "line_no");
        
        // Customer synonyms
        USER_TERM_MAPPINGS.put("customer", "customer_name");
        USER_TERM_MAPPINGS.put("client", "customer_name");
        USER_TERM_MAPPINGS.put("customer name", "customer_name");
        USER_TERM_MAPPINGS.put("customer number", "customer_number");
        
        // Date synonyms
        USER_TERM_MAPPINGS.put("effective", "effective_date");
        USER_TERM_MAPPINGS.put("expiration", "expiration_date");
        USER_TERM_MAPPINGS.put("expires", "expiration_date");
        USER_TERM_MAPPINGS.put("created", "create_date");
        USER_TERM_MAPPINGS.put("updated", "updated_date");
        
        // Status synonyms
        USER_TERM_MAPPINGS.put("active", "status");
        USER_TERM_MAPPINGS.put("expired", "status");
        USER_TERM_MAPPINGS.put("pending", "status");
        
        // Price synonyms
        USER_TERM_MAPPINGS.put("cost", "price");
        USER_TERM_MAPPINGS.put("pricing", "price");
        USER_TERM_MAPPINGS.put("amount", "price");
    }
    
    /**
     * Get database column name for contract table
     */
    public static String getContractColumn(String userTerm) {
        String normalizedTerm = normalizeUserTerm(userTerm);
        return CONTRACT_COLUMN_MAPPINGS.getOrDefault(normalizedTerm, normalizedTerm.toUpperCase());
    }
    
    /**
     * Get database column name for parts table
     */
    public static String getPartsColumn(String userTerm) {
        String normalizedTerm = normalizeUserTerm(userTerm);
        return PARTS_COLUMN_MAPPINGS.getOrDefault(normalizedTerm, normalizedTerm.toUpperCase());
    }
    
    /**
     * Normalize user term to standard format
     */
    private static String normalizeUserTerm(String userTerm) {
        if (userTerm == null) return "";
        
        String normalized = userTerm.toLowerCase().trim();
        
        // Check if user term has a direct mapping
        if (USER_TERM_MAPPINGS.containsKey(normalized)) {
            return USER_TERM_MAPPINGS.get(normalized);
        }
        
        return normalized.replace(" ", "_");
    }
    
    /**
     * Get all contract columns
     */
    public static Set<String> getAllContractColumns() {
        return new HashSet<>(CONTRACT_COLUMN_MAPPINGS.values());
    }
    
    /**
     * Get all parts columns
     */
    public static Set<String> getAllPartsColumns() {
        return new HashSet<>(PARTS_COLUMN_MAPPINGS.values());
    }
    
    /**
     * Check if term maps to contract table
     */
    public static boolean isContractTerm(String userTerm) {
        String normalizedTerm = normalizeUserTerm(userTerm);
        return CONTRACT_COLUMN_MAPPINGS.containsKey(normalizedTerm);
    }
    
    /**
     * Check if term maps to parts table
     */
    public static boolean isPartsTerm(String userTerm) {
        String normalizedTerm = normalizeUserTerm(userTerm);
        return PARTS_COLUMN_MAPPINGS.containsKey(normalizedTerm);
    }
    
    /**
     * Get suggested column names for a user term
     */
    public static List<String> getSuggestedColumns(String userTerm) {
        List<String> suggestions = new ArrayList<>();
        String lowerTerm = userTerm.toLowerCase();
        
        // Check contract columns
        for (Map.Entry<String, String> entry : CONTRACT_COLUMN_MAPPINGS.entrySet()) {
            if (entry.getKey().contains(lowerTerm) || entry.getValue().toLowerCase().contains(lowerTerm)) {
                suggestions.add(entry.getValue());
            }
        }
        
        // Check parts columns
        for (Map.Entry<String, String> entry : PARTS_COLUMN_MAPPINGS.entrySet()) {
            if (entry.getKey().contains(lowerTerm) || entry.getValue().toLowerCase().contains(lowerTerm)) {
                suggestions.add(entry.getValue());
            }
        }
        
        return suggestions;
    }
}
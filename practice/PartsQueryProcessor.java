package view.practice;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.logging.Logger;

/**
 * Enhanced Parts Query Processing System
 * 
 * Integrates with PartsModelTrainer.java to provide intelligent parts query processing
 * with stage-aware database routing (cc_part_loaded, cct_parts_staging, cct_error_parts)
 * 
 * Features:
 * - Advanced entity extraction for part numbers, contract numbers, manufacturers
 * - Stage-aware processing with automatic table routing
 * - Error type detection and categorization
 * - Enhanced confidence scoring
 * - JSON output optimized for PartsModelTrainer integration
 * - Comprehensive spell correction for parts domain
 * 
 * @author Parts Management System
 * @version 2.0
 */
public class PartsQueryProcessor {
    
    private static final Logger logger = Logger.getLogger(PartsQueryProcessor.class.getName());
    
    // Parts table schema and routing
    private static final Map<String, String> STAGE_ROUTING = new HashMap<String, String>() {{
        put("loaded", "cc_part_loaded");
        put("success", "cc_part_loaded");
        put("successful", "cc_part_loaded");
        put("valid", "cc_part_loaded");
        put("approved", "cc_part_loaded");
        put("final", "cc_part_loaded");
        put("processed", "cc_part_loaded");
        put("validated", "cc_part_loaded");
        
        put("staging", "cct_parts_staging");
        put("upload", "cct_parts_staging");
        put("pending", "cct_parts_staging");
        put("unprocessed", "cct_parts_staging");
        put("waiting", "cct_parts_staging");
        
        put("failed", "cct_error_parts");
        put("error", "cct_error_parts");
        put("rejected", "cct_error_parts");
        put("invalid", "cct_error_parts");
        put("faild", "cct_error_parts");
        put("errors", "cct_error_parts");
    }};
    
    // Error type classifications for cct_error_parts
    private static final Map<String, String> ERROR_TYPE_MAPPING = new HashMap<String, String>() {{
        put("price", "price_error");
        put("cost", "price_error");
        put("pricing", "price_error");
        put("amount", "price_error");
        
        put("validation", "validation_failed");
        put("validate", "validation_failed");
        put("invalid", "validation_failed");
        put("format", "validation_failed");
        
        put("missing", "missing_data");
        put("incomplete", "missing_data");
        put("absent", "missing_data");
        put("empty", "missing_data");
        
        put("master", "master_data_error");
        put("reference", "master_data_error");
        put("lookup", "master_data_error");
        
        put("duplicate", "duplicate_error");
        put("duplicated", "duplicate_error");
        put("repeated", "duplicate_error");
    }};
    
    // Parts-specific attributes and synonyms
    private static final Set<String> PARTS_ATTRIBUTES = new HashSet<>(Arrays.asList(
        "part_number", "description", "manufacturer", "specifications", "technical_drawings",
        "price", "lead_time", "availability", "status", "validation_status", "error_message",
        "contract_number", "customer_name", "upload_date", "validation_date", "created_date",
        "quantity", "unit_of_measure", "category", "subcategory", "revision", "supplier"
    ));
    
    private static final Map<String, String> PARTS_SYNONYMS = new HashMap<String, String>() {{
        // Part identifiers
        put("part", "part_number");
        put("parts", "part_number");
        put("component", "part_number");
        put("item", "part_number");
        put("product", "part_number");
        put("pn", "part_number");
        
        // Specifications and details
        put("spec", "specifications");
        put("specs", "specifications");
        put("specification", "specifications");
        put("details", "specifications");
        put("info", "specifications");
        put("information", "specifications");
        put("datasheet", "specifications");
        put("drawing", "technical_drawings");
        put("drawings", "technical_drawings");
        put("blueprint", "technical_drawings");
        
        // Status and validation
        put("state", "status");
        put("condition", "status");
        put("validation", "validation_status");
        put("valid", "validation_status");
        put("error", "error_message");
        put("errors", "error_message");
        put("message", "error_message");
        put("reason", "error_message");
        
        // Time and dates
        put("date", "created_date");
        put("created", "created_date");
        put("uploaded", "upload_date");
        put("validated", "validation_date");
        
        // Commercial
        put("cost", "price");
        put("pricing", "price");
        put("amount", "price");
        put("leadtime", "lead_time");
        put("lede_time", "lead_time");
        put("delivery", "lead_time");
        
        // Quantity and measures
        put("qty", "quantity");
        put("count", "quantity");
        put("amount_qty", "quantity");
        put("uom", "unit_of_measure");
        put("unit", "unit_of_measure");
        
        // Organization
        put("maker", "manufacturer");
        put("vendor", "manufacturer");
        put("supplier", "manufacturer");
        put("brand", "manufacturer");
        put("type", "category");
        put("class", "category");
        put("group", "category");
    }};
    
    public static void main(String[] args) {
        try {
            PartsQueryProcessor processor = new PartsQueryProcessor();
            
            // Test queries demonstrating all functionality
            String[] testQueries = {
                "specs of AE125",
                "is AE125 active?",
                "parts for contract 123456",
                "failed parts in 123456",
                "parts loaded today",
                "lst out contrcts with part numbr AE125",
                "faield prts of 123456",
                "specifcations of prduct AE125",
                "show all staging parts for contract 789012",
                "get error messages for failed parts in contract 123456",
                "what is the price and lead time for part BC98765",
                "parts validation status for contract 456789",
                "show duplicate error parts",
                "get technical drawings for AE125",
                "list all parts uploaded this week",
                "find parts by manufacturer Honeywell",
                "show validation failed parts with missing data",
                "get all metadata for part XY54321",
                "contracts using part AE125 with price errors",
                "show unprocessed parts waiting for validation"
            };
            
            System.out.println("🚀 Parts Query Processing System Demo");
            System.out.println("📊 Processing " + testQueries.length + " test queries");
            System.out.println("=".repeat(80));
            
            for (int i = 0; i < testQueries.length; i++) {
                String query = testQueries[i];
                System.out.println("\n[" + (i + 1) + "] INPUT: \"" + query + "\"");
                
                String jsonResult = processor.processPartsQuery(query);
                System.out.println("JSON OUTPUT:");
                System.out.println(jsonResult);
                
                // Extract key information for display
                processor.displayProcessingAnalysis(jsonResult);
                System.out.println("-".repeat(60));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main processing method - converts natural language parts query to structured JSON
     */
    public String processPartsQuery(String originalQuery) {
        try {
            // Step 1: Input validation
            if (originalQuery == null || originalQuery.trim().isEmpty()) {
                return createErrorJson("Empty query provided", 0.0);
            }
            
            // Step 2: Enhanced spell correction for parts domain
            String correctedQuery = performPartsSpellCorrection(originalQuery);
            String normalizedQuery = normalizePartsQuery(correctedQuery);
            
            // Step 3: Extract entities (parts, contracts, manufacturers)
            Map<String, String> entities = extractPartsEntities(normalizedQuery, originalQuery);
            
            // Step 4: Determine intent using PartsModelTrainer logic
            String intent = determinePartsIntent(normalizedQuery, entities);
            
            // Step 5: Extract requested attributes
            List<String> requestedAttributes = extractRequestedAttributes(normalizedQuery, intent);
            
            // Step 6: Determine stage filters and database routing
            Map<String, Object> stageFilters = determineStageFilters(normalizedQuery, intent);
            
            // Step 7: Extract context (timeframes, validation status, etc.)
            Map<String, Object> context = extractPartsContext(normalizedQuery);
            
            // Step 8: Calculate enhanced confidence score
            double confidence = calculatePartsConfidence(originalQuery, correctedQuery, entities, 
                                                        requestedAttributes, stageFilters, intent);
            
            // Step 9: Generate structured JSON
            return generatePartsJson(originalQuery, normalizedQuery, intent, confidence, 
                                   entities, requestedAttributes, stageFilters, context);
            
        } catch (Exception e) {
            logger.severe("Error processing parts query: " + originalQuery + " - " + e.getMessage());
            return createErrorJson("Processing error: " + e.getMessage(), 0.0);
        }
    }
    
    /**
     * Enhanced spell correction for parts domain
     */
    private String performPartsSpellCorrection(String query) {
        Map<String, String> partsCorrections = new HashMap<String, String>() {{
            // Parts domain specific
            put("prts", "parts");
            put("aprts", "parts");
            put("prats", "parts");
            put("aprts", "parts");
            put("pasd", "passed");
            put("faield", "failed");
            put("failded", "failed");
            put("faild", "failed");
            put("faled", "failed");
            
            // Specifications and details
            put("specifcations", "specifications");
            put("specfications", "specifications");
            put("spefications", "specifications");
            put("specs", "specifications");
            put("detials", "details");
            put("detals", "details");
            put("infro", "info");
            put("infromation", "information");
            put("informaton", "information");
            put("infomation", "information");
            
            // Status and validation
            put("statuss", "status");
            put("statuz", "status");
            put("validaton", "validation");
            put("validaion", "validation");
            put("valdiation", "validation");
            put("loadded", "loaded");
            put("loaeded", "loaded");
            put("loded", "loaded");
            
            // Contract and numbers
            put("contrct", "contract");
            put("contarct", "contract");
            put("contrcts", "contracts");
            put("numbr", "number");
            put("numer", "number");
            put("numebr", "number");
            
            // Actions
            put("shwo", "show");
            put("sho", "show");
            put("lst", "list");
            put("serach", "search");
            put("finde", "find");
            put("get", "get");
            
            // Time
            put("todya", "today");
            put("yestarday", "yesterday");
            put("wek", "week");
            put("mnth", "month");
            
            // Error types
            put("eror", "error");
            put("erros", "errors");
            put("mesage", "message");
            put("mesages", "messages");
            put("messge", "message");
            
            // Manufacturers
            put("honeywel", "honeywell");
            put("boieng", "boeing");
            put("microsft", "microsoft");
            
            // Technical terms
            put("drawng", "drawing");
            put("drwaing", "drawing");
            put("drawigns", "drawings");
            put("manufactur", "manufacturer");
            put("manufaturer", "manufacturer");
            put("suuplier", "supplier");
            put("suplier", "supplier");
            
            // Commercial
            put("pric", "price");
            put("prcie", "price");
            put("cots", "cost");
            put("coist", "cost");
            put("lede", "lead");
            put("leade", "lead");
            put("quantiy", "quantity");
            put("quanity", "quantity");
        }};
        
        String corrected = query.toLowerCase();
        for (Map.Entry<String, String> correction : partsCorrections.entrySet()) {
            corrected = corrected.replace(correction.getKey(), correction.getValue());
        }
        return corrected;
    }
    
    /**
     * Normalize parts query text
     */
    private String normalizePartsQuery(String query) {
        return query.toLowerCase()
                   .replaceAll("\\s+", " ")
                   .replaceAll("[^a-zA-Z0-9\\s-]", " ")
                   .trim();
    }
    
    /**
     * Extract parts-specific entities
     */
    private Map<String, String> extractPartsEntities(String query, String originalQuery) {
        Map<String, String> entities = new HashMap<>();
        
        // Extract part numbers (enhanced patterns)
        extractPartNumber(entities, query);
        
        // Extract contract numbers
        extractContractNumber(entities, query);
        
        // Extract manufacturer names
        extractManufacturer(entities, query);
        
        // Extract customer names
        extractCustomerName(entities, query);
        
        return entities;
    }
    
    /**
     * Enhanced part number extraction
     */
    private void extractPartNumber(Map<String, String> entities, String query) {
        List<Pattern> partPatterns = Arrays.asList(
            // Complex part numbers (AE13246-46485659, BC98765-12345678)
            Pattern.compile("\\b([A-Z]{1,4}[0-9]{3,8}-[0-9]{6,12})\\b", Pattern.CASE_INSENSITIVE),
            // Simple part numbers (AE125, BC98765)
            Pattern.compile("\\b([A-Z]{1,4}[0-9]{3,8})\\b", Pattern.CASE_INSENSITIVE),
            // Part with prefix
            Pattern.compile("\\bpart\\s+(?:number\\s+)?([A-Z0-9-]{3,20})\\b", Pattern.CASE_INSENSITIVE),
            // Generic alphanumeric parts
            Pattern.compile("\\b([A-Z]{2,4}[0-9]{2,6})\\b", Pattern.CASE_INSENSITIVE)
        );
        
        for (Pattern pattern : partPatterns) {
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                String partNumber = matcher.group(1).toUpperCase();
                if (!isCommonWord(partNumber)) {
                    entities.put("partNumber", partNumber);
                    break;
                }
            }
        }
    }
    
    /**
     * Extract contract numbers
     */
    private void extractContractNumber(Map<String, String> entities, String query) {
        List<Pattern> contractPatterns = Arrays.asList(
            Pattern.compile("\\bcontract\\s+([0-9]{4,12})\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bfor\\s+contract\\s+([0-9]{4,12})\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bin\\s+([0-9]{4,12})\\b"),
            Pattern.compile("\\bof\\s+([0-9]{4,12})\\b")
        );
        
        for (Pattern pattern : contractPatterns) {
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                entities.put("contractNumber", matcher.group(1));
                break;
            }
        }
    }
    
    /**
     * Extract manufacturer names
     */
    private void extractManufacturer(Map<String, String> entities, String query) {
        String[] manufacturers = {
            "honeywell", "boeing", "microsoft", "apple", "google", "amazon", 
            "oracle", "ibm", "cisco", "intel", "siemens", "ge", "lockheed",
            "raytheon", "northrop", "caterpillar", "john deere"
        };
        
        for (String manufacturer : manufacturers) {
            if (query.toLowerCase().contains(manufacturer)) {
                entities.put("manufacturer", capitalizeFirst(manufacturer));
                break;
            }
        }
        
        // Pattern-based extraction
        Pattern manufacturerPattern = Pattern.compile("\\b(?:by|from|manufacturer)\\s+([a-zA-Z][a-zA-Z\\s&.-]{1,25})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = manufacturerPattern.matcher(query);
        if (matcher.find()) {
            entities.put("manufacturer", capitalizeFirst(matcher.group(1).trim()));
        }
    }
    
    /**
     * Extract customer names
     */
    private void extractCustomerName(Map<String, String> entities, String query) {
        Pattern customerPattern = Pattern.compile("\\b(?:customer|client)\\s+([a-zA-Z][a-zA-Z\\s.-]{1,20})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = customerPattern.matcher(query);
        if (matcher.find()) {
            entities.put("customerName", capitalizeFirst(matcher.group(1).trim()));
        }
    }
    
    /**
     * Determine parts intent based on PartsModelTrainer logic
     */
    private String determinePartsIntent(String query, Map<String, String> entities) {
        // Map to PartsModelTrainer intents
        if (entities.containsKey("partNumber") && 
            (query.contains("spec") || query.contains("info") || query.contains("detail"))) {
            return "get_part_details";
        }
        
        if (query.contains("failed") || query.contains("error") || query.contains("rejected")) {
            if (query.contains("message") || query.contains("reason")) {
                return "get_error_messages";
            } else {
                return "show_failed_parts";
            }
        }
        
        if (query.contains("loaded") || query.contains("successful") || query.contains("processed")) {
            return "show_loaded_parts";
        }
        
        if (query.contains("staging") || query.contains("upload") || query.contains("pending")) {
            return "show_loaded_parts"; // Staging is considered "loaded" in context
        }
        
        if (query.contains("final") || query.contains("validated") || query.contains("approved")) {
            return "show_final_parts";
        }
        
        if (query.contains("validation") && query.contains("status")) {
            return "get_validation_status";
        }
        
        if (query.contains("find") || query.contains("search")) {
            if (entities.containsKey("customerName")) {
                return "search_parts_by_customer";
            } else {
                return "search_parts_by_contract";
            }
        }
        
        if (query.contains("type") || query.contains("category") || query.contains("group")) {
            return "list_parts_by_type";
        }
        
        // Default to show all parts
        return "show_all_parts";
    }
    
    /**
     * Extract requested attributes based on query and intent
     */
    private List<String> extractRequestedAttributes(String query, String intent) {
        Set<String> attributes = new HashSet<>();
        
        // Intent-based defaults
        switch (intent) {
            case "get_part_details":
                attributes.addAll(Arrays.asList("specifications", "technical_drawings", "price", "lead_time", "manufacturer"));
                break;
            case "show_failed_parts":
                attributes.addAll(Arrays.asList("part_number", "error_message", "validation_status"));
                break;
            case "get_error_messages":
                attributes.addAll(Arrays.asList("error_message", "validation_date"));
                break;
            case "show_loaded_parts":
                attributes.addAll(Arrays.asList("part_number", "status", "upload_date"));
                break;
            case "show_final_parts":
                attributes.addAll(Arrays.asList("part_number", "status", "validation_date"));
                break;
            case "get_validation_status":
                attributes.addAll(Arrays.asList("validation_status", "validation_date"));
                break;
        }
        
        // Query-specific attribute extraction
        if (query.contains("spec") || query.contains("specification")) attributes.add("specifications");
        if (query.contains("drawing") || query.contains("blueprint")) attributes.add("technical_drawings");
        if (query.contains("price") || query.contains("cost")) attributes.add("price");
        if (query.contains("lead") || query.contains("delivery")) attributes.add("lead_time");
        if (query.contains("status")) attributes.add("status");
        if (query.contains("error") || query.contains("message")) attributes.add("error_message");
        if (query.contains("validation")) attributes.add("validation_status");
        if (query.contains("manufacturer") || query.contains("vendor")) attributes.add("manufacturer");
        if (query.contains("quantity") || query.contains("qty")) attributes.add("quantity");
        if (query.contains("description")) attributes.add("description");
        if (query.contains("metadata") || query.contains("all")) {
            attributes.addAll(Arrays.asList("specifications", "price", "lead_time", "manufacturer", "status"));
        }
        
        // Normalize attribute names using synonyms
        Set<String> normalizedAttributes = new HashSet<>();
        for (String attr : attributes) {
            String normalized = PARTS_SYNONYMS.getOrDefault(attr, attr);
            if (PARTS_ATTRIBUTES.contains(normalized)) {
                normalizedAttributes.add(normalized);
            }
        }
        
        return new ArrayList<>(normalizedAttributes);
    }
    
    /**
     * Determine stage filters and database routing
     */
    private Map<String, Object> determineStageFilters(String query, String intent) {
        Map<String, Object> stageFilters = new HashMap<>();
        
        // Determine target stage based on query content
        String targetStage = null;
        
        // Check for stage keywords
        for (Map.Entry<String, String> entry : STAGE_ROUTING.entrySet()) {
            if (query.contains(entry.getKey())) {
                targetStage = entry.getValue();
                break;
            }
        }
        
        // Intent-based routing if no explicit stage found
        if (targetStage == null) {
            switch (intent) {
                case "show_failed_parts":
                case "get_error_messages":
                    targetStage = "cct_error_parts";
                    break;
                case "show_loaded_parts":
                    if (query.contains("staging") || query.contains("upload")) {
                        targetStage = "cct_parts_staging";
                    } else {
                        targetStage = "cc_part_loaded";
                    }
                    break;
                case "show_final_parts":
                case "get_part_details":
                    targetStage = "cc_part_loaded";
                    break;
                default:
                    targetStage = "cc_part_loaded"; // Default to loaded parts
            }
        }
        
        stageFilters.put("targetStage", targetStage);
        
        // Determine error type for error parts
        if ("cct_error_parts".equals(targetStage)) {
            String errorType = null;
            for (Map.Entry<String, String> entry : ERROR_TYPE_MAPPING.entrySet()) {
                if (query.contains(entry.getKey())) {
                    errorType = entry.getValue();
                    break;
                }
            }
            if (errorType != null) {
                stageFilters.put("errorType", errorType);
            }
        }
        
        return stageFilters;
    }
    
    /**
     * Extract parts context information
     */
    private Map<String, Object> extractPartsContext(String query) {
        Map<String, Object> context = new HashMap<>();
        
        // Validation status context
        if (query.contains("validation")) {
            if (query.contains("passed") || query.contains("success")) {
                context.put("validationStatus", "passed");
            } else if (query.contains("failed") || query.contains("error")) {
                context.put("validationStatus", "failed");
            }
        }
        
        // Timeframe extraction
        if (query.contains("today")) {
            context.put("timeframe", "today");
        } else if (query.contains("yesterday")) {
            context.put("timeframe", "yesterday");
        } else if (query.contains("this week") || query.contains("week")) {
            context.put("timeframe", "this_week");
        } else if (query.contains("this month") || query.contains("month")) {
            context.put("timeframe", "this_month");
        } else if (query.contains("last week")) {
            context.put("timeframe", "last_week");
        } else if (query.contains("last month")) {
            context.put("timeframe", "last_month");
        }
        
        // Operational context
        if (query.contains("duplicate")) {
            context.put("operationalIssue", "duplicate");
        } else if (query.contains("missing")) {
            context.put("operationalIssue", "missing_data");
        } else if (query.contains("incomplete")) {
            context.put("operationalIssue", "incomplete");
        }
        
        return context;
    }
    
    /**
     * Calculate enhanced confidence score for parts queries
     */
    private double calculatePartsConfidence(String original, String corrected, 
                                          Map<String, String> entities, 
                                          List<String> requestedAttributes,
                                          Map<String, Object> stageFilters, 
                                          String intent) {
        double confidence = 0.65; // Higher base for parts domain
        
        // Entity boost - parts are critical
        if (entities.containsKey("partNumber")) confidence += 0.30; // Major boost for part numbers
        if (entities.containsKey("contractNumber")) confidence += 0.20; // Good boost for contracts
        if (entities.containsKey("manufacturer")) confidence += 0.15; // Moderate boost for manufacturers
        if (entities.containsKey("customerName")) confidence += 0.10; // Minor boost for customers
        
        // Attribute boost
        if (!requestedAttributes.isEmpty()) {
            confidence += Math.min(0.15, requestedAttributes.size() * 0.03);
        }
        
        // Stage routing boost (shows system understands data flow)
        if (stageFilters.containsKey("targetStage")) {
            confidence += 0.10;
        }
        if (stageFilters.containsKey("errorType")) {
            confidence += 0.05;
        }
        
        // Intent-specific boosts
        if ("get_part_details".equals(intent) && entities.containsKey("partNumber")) {
            confidence += 0.10; // High confidence for specific part queries
        }
        if ("show_failed_parts".equals(intent) || "get_error_messages".equals(intent)) {
            confidence += 0.08; // Good confidence for error queries
        }
        
        // Spell correction penalty (gentler for parts domain)
        double similarity = calculateSimilarity(original.toLowerCase(), corrected);
        if (similarity < 0.9) {
            confidence *= (0.90 + (similarity * 0.10)); // Very gentle penalty
        }
        
        // Domain familiarity boost
        if (hasPartsDomainTerms(original)) confidence += 0.08;
        
        // Query structure boost
        if (hasGoodPartsStructure(original)) confidence += 0.05;
        
        return Math.min(0.98, Math.max(0.40, confidence));
    }
    
    /**
     * Generate structured JSON for parts queries
     */
    private String generatePartsJson(String originalQuery, String normalizedQuery, String intent, 
                                   double confidence, Map<String, String> entities, 
                                   List<String> requestedAttributes, Map<String, Object> stageFilters,
                                   Map<String, Object> context) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // Metadata
        json.append("  \"metadata\": {\n");
        json.append("    \"originalQuery\": \"").append(escapeJson(originalQuery)).append("\",\n");
        json.append("    \"normalizedQuery\": \"").append(escapeJson(normalizedQuery)).append("\",\n");
        json.append("    \"confidence\": ").append(String.format("%.3f", confidence)).append(",\n");
        json.append("    \"timestamp\": \"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
        json.append("    \"intent\": \"").append(intent).append("\"\n");
        json.append("  },\n");
        
        // Entities
        json.append("  \"entities\": {\n");
        boolean firstEntity = true;
        for (Map.Entry<String, String> entity : entities.entrySet()) {
            if (!firstEntity) json.append(",\n");
            json.append("    \"").append(entity.getKey()).append("\": \"").append(escapeJson(entity.getValue())).append("\"");
            firstEntity = false;
        }
        
        // Add null entities for completeness
        if (!entities.containsKey("partNumber")) {
            if (!firstEntity) json.append(",\n");
            json.append("    \"partNumber\": null");
            firstEntity = false;
        }
        if (!entities.containsKey("contractNumber")) {
            if (!firstEntity) json.append(",\n");
            json.append("    \"contractNumber\": null");
            firstEntity = false;
        }
        if (!entities.containsKey("manufacturer")) {
            if (!firstEntity) json.append(",\n");
            json.append("    \"manufacturer\": null");
        }
        json.append("\n  },\n");
        
        // Requested attributes
        json.append("  \"requestedAttributes\": [");
        for (int i = 0; i < requestedAttributes.size(); i++) {
            if (i > 0) json.append(", ");
            json.append("\"").append(requestedAttributes.get(i)).append("\"");
        }
        json.append("],\n");
        
        // Stage filters
        json.append("  \"stageFilters\": {\n");
        boolean firstFilter = true;
        for (Map.Entry<String, Object> filter : stageFilters.entrySet()) {
            if (!firstFilter) json.append(",\n");
            json.append("    \"").append(filter.getKey()).append("\": ");
            if (filter.getValue() == null) {
                json.append("null");
            } else {
                json.append("\"").append(filter.getValue().toString()).append("\"");
            }
            firstFilter = false;
        }
        
        // Add null error type if not present
        if (!stageFilters.containsKey("errorType")) {
            if (!firstFilter) json.append(",\n");
            json.append("    \"errorType\": null");
        }
        json.append("\n  },\n");
        
        // Context
        json.append("  \"context\": {\n");
        boolean firstContext = true;
        for (Map.Entry<String, Object> ctx : context.entrySet()) {
            if (!firstContext) json.append(",\n");
            json.append("    \"").append(ctx.getKey()).append("\": ");
            if (ctx.getValue() == null) {
                json.append("null");
            } else {
                json.append("\"").append(ctx.getValue().toString()).append("\"");
            }
            firstContext = false;
        }
        
        // Add null fields for completeness
        if (!context.containsKey("validationStatus")) {
            if (!firstContext) json.append(",\n");
            json.append("    \"validationStatus\": null");
            firstContext = false;
        }
        if (!context.containsKey("timeframe")) {
            if (!firstContext) json.append(",\n");
            json.append("    \"timeframe\": null");
        }
        json.append("\n  }\n");
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Display processing analysis for demo purposes
     */
    private void displayProcessingAnalysis(String jsonResult) {
        System.out.println("ANALYSIS:");
        
        // Extract key information
        String partNumber = extractJsonField(jsonResult, "partNumber");
        if (!partNumber.equals("not found") && !partNumber.equals("null")) {
            System.out.println("  📦 Part Number: " + partNumber);
        }
        
        String contractNumber = extractJsonField(jsonResult, "contractNumber");
        if (!contractNumber.equals("not found") && !contractNumber.equals("null")) {
            System.out.println("  📋 Contract Number: " + contractNumber);
        }
        
        String manufacturer = extractJsonField(jsonResult, "manufacturer");
        if (!manufacturer.equals("not found") && !manufacturer.equals("null")) {
            System.out.println("  🏭 Manufacturer: " + manufacturer);
        }
        
        String targetStage = extractJsonField(jsonResult, "targetStage");
        if (!targetStage.equals("not found")) {
            System.out.println("  🎯 Target Table: " + targetStage);
        }
        
        String errorType = extractJsonField(jsonResult, "errorType");
        if (!errorType.equals("not found") && !errorType.equals("null")) {
            System.out.println("  ⚠️ Error Type: " + errorType);
        }
        
        String intent = extractJsonField(jsonResult, "intent");
        if (!intent.equals("not found")) {
            System.out.println("  🎯 Intent: " + intent);
        }
        
        double confidence = extractConfidenceFromJson(jsonResult);
        System.out.println("  📊 Confidence: " + String.format("%.3f", confidence));
        
        if (confidence >= 0.90) {
            System.out.println("  ✅ Assessment: HIGH CONFIDENCE - Auto-execute");
        } else if (confidence >= 0.70) {
            System.out.println("  ⚠️ Assessment: MEDIUM CONFIDENCE - Verify with user");
        } else {
            System.out.println("  ❌ Assessment: LOW CONFIDENCE - Request clarification");
        }
        
        int attrCount = countArrayElements(jsonResult, "requestedAttributes");
        if (attrCount > 0) {
            System.out.println("  📋 Requested Attributes: " + attrCount + " attributes found");
        }
    }
    
    // Helper methods
    private boolean isCommonWord(String word) {
        Set<String> commonWords = new HashSet<>(Arrays.asList(
            "parts", "part", "info", "details", "for", "by", "the", "and", "or", "from", "to", "in", "on", "at",
            "show", "get", "find", "list", "display", "view", "contract", "contracts"
        ));
        return commonWords.contains(word.toLowerCase());
    }
    
    private boolean hasPartsDomainTerms(String query) {
        String[] domainTerms = {"part", "parts", "component", "specification", "manufacturer", "validation", "error", "contract"};
        for (String term : domainTerms) {
            if (query.toLowerCase().contains(term)) return true;
        }
        return false;
    }
    
    private boolean hasGoodPartsStructure(String query) {
        return query.contains("part") || query.contains("parts") || 
               query.contains("spec") || query.contains("error") || 
               query.contains("contract") || query.contains("get") || 
               query.contains("show") || query.contains("find");
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return (maxLen - editDistance(s1, s2)) / (double) maxLen;
    }
    
    private int editDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j], Math.min(dp[i][j-1], dp[i-1][j-1]));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private String createErrorJson(String error, double confidence) {
        return "{\n" +
               "  \"metadata\": {\n" +
               "    \"originalQuery\": \"\",\n" +
               "    \"confidence\": " + confidence + ",\n" +
               "    \"intent\": \"error\",\n" +
               "    \"error\": \"" + escapeJson(error) + "\"\n" +
               "  },\n" +
               "  \"entities\": {\n" +
               "    \"partNumber\": null,\n" +
               "    \"contractNumber\": null,\n" +
               "    \"manufacturer\": null\n" +
               "  },\n" +
               "  \"requestedAttributes\": [],\n" +
               "  \"stageFilters\": {\n" +
               "    \"targetStage\": null,\n" +
               "    \"errorType\": null\n" +
               "  },\n" +
               "  \"context\": {\n" +
               "    \"validationStatus\": null,\n" +
               "    \"timeframe\": null\n" +
               "  }\n" +
               "}";
    }
    
    private String extractJsonField(String json, String fieldName) {
        String searchFor = "\"" + fieldName + "\": \"";
        int start = json.indexOf(searchFor);
        if (start == -1) {
            // Try without quotes (for null values)
            searchFor = "\"" + fieldName + "\": ";
            start = json.indexOf(searchFor);
            if (start == -1) return "not found";
            start += searchFor.length();
            int end = Math.min(
                json.indexOf(",", start) != -1 ? json.indexOf(",", start) : json.length(),
                json.indexOf("\n", start) != -1 ? json.indexOf("\n", start) : json.length()
            );
            return json.substring(start, end).trim();
        }
        
        start += searchFor.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return "not found";
        return json.substring(start, end);
    }
    
    private double extractConfidenceFromJson(String json) {
        String searchFor = "\"confidence\": ";
        int start = json.indexOf(searchFor);
        if (start == -1) return 0.0;
        
        start += searchFor.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("\n", start);
        try {
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private int countArrayElements(String json, String arrayName) {
        String searchFor = "\"" + arrayName + "\": [";
        int start = json.indexOf(searchFor);
        if (start == -1) return 0;
        
        start += searchFor.length();
        int end = json.indexOf("]", start);
        if (end == -1) return 0;
        
        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return 0;
        
        return arrayContent.split(",").length;
    }
}
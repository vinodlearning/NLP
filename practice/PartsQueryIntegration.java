package view.practice;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Parts Query Integration Utility
 * 
 * Demonstrates how to integrate PartsQueryProcessor JSON output 
 * with existing PartsModelTrainer.java system and database operations
 * 
 * This class shows the mapping between JSON and Java objects for:
 * - Database routing (cc_part_loaded, cct_parts_staging, cct_error_parts)
 * - Entity extraction and validation
 * - Query execution and result processing
 * 
 * @author Parts Integration System
 * @version 1.0
 */
public class PartsQueryIntegration {
    
    /**
     * Parts Query Data Transfer Object
     * Maps directly to JSON structure from PartsQueryProcessor
     */
    public static class PartsQuery {
        // Metadata
        private String originalQuery;
        private String normalizedQuery;
        private double confidence;
        private String timestamp;
        private String intent;
        
        // Entities
        private String partNumber;
        private String contractNumber;
        private String manufacturer;
        private String customerName;
        
        // Requested attributes
        private List<String> requestedAttributes = new ArrayList<>();
        
        // Stage filters for database routing
        private String targetStage;
        private String errorType;
        
        // Context
        private String validationStatus;
        private String timeframe;
        private String operationalIssue;
        
        // Getters and setters
        public String getOriginalQuery() { return originalQuery; }
        public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }
        
        public String getNormalizedQuery() { return normalizedQuery; }
        public void setNormalizedQuery(String normalizedQuery) { this.normalizedQuery = normalizedQuery; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        
        public String getPartNumber() { return partNumber; }
        public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
        
        public String getContractNumber() { return contractNumber; }
        public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
        
        public String getManufacturer() { return manufacturer; }
        public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public List<String> getRequestedAttributes() { return requestedAttributes; }
        public void setRequestedAttributes(List<String> requestedAttributes) { this.requestedAttributes = requestedAttributes; }
        
        public String getTargetStage() { return targetStage; }
        public void setTargetStage(String targetStage) { this.targetStage = targetStage; }
        
        public String getErrorType() { return errorType; }
        public void setErrorType(String errorType) { this.errorType = errorType; }
        
        public String getValidationStatus() { return validationStatus; }
        public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
        
        public String getTimeframe() { return timeframe; }
        public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
        
        public String getOperationalIssue() { return operationalIssue; }
        public void setOperationalIssue(String operationalIssue) { this.operationalIssue = operationalIssue; }
        
        /**
         * Check if query should be auto-executed based on confidence
         */
        public boolean shouldAutoExecute() {
            return confidence >= 0.90;
        }
        
        /**
         * Check if query needs user verification
         */
        public boolean needsVerification() {
            return confidence >= 0.70 && confidence < 0.90;
        }
        
        /**
         * Check if query needs clarification
         */
        public boolean needsClarification() {
            return confidence < 0.70;
        }
    }
    
    /**
     * Simple JSON parser for PartsQueryProcessor output
     * In production, use Jackson or Gson for robust JSON parsing
     */
    public static PartsQuery parseJsonToPartsQuery(String json) {
        PartsQuery query = new PartsQuery();
        
        try {
            // Extract metadata
            query.setOriginalQuery(extractJsonValue(json, "originalQuery"));
            query.setNormalizedQuery(extractJsonValue(json, "normalizedQuery"));
            query.setConfidence(Double.parseDouble(extractJsonValue(json, "confidence")));
            query.setTimestamp(extractJsonValue(json, "timestamp"));
            query.setIntent(extractJsonValue(json, "intent"));
            
            // Extract entities
            query.setPartNumber(extractJsonValue(json, "partNumber"));
            query.setContractNumber(extractJsonValue(json, "contractNumber"));
            query.setManufacturer(extractJsonValue(json, "manufacturer"));
            query.setCustomerName(extractJsonValue(json, "customerName"));
            
            // Extract requested attributes
            query.setRequestedAttributes(extractJsonArray(json, "requestedAttributes"));
            
            // Extract stage filters
            query.setTargetStage(extractJsonValue(json, "targetStage"));
            query.setErrorType(extractJsonValue(json, "errorType"));
            
            // Extract context
            query.setValidationStatus(extractJsonValue(json, "validationStatus"));
            query.setTimeframe(extractJsonValue(json, "timeframe"));
            query.setOperationalIssue(extractJsonValue(json, "operationalIssue"));
            
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
        
        return query;
    }
    
    /**
     * Database routing logic based on targetStage
     * Routes queries to appropriate tables: cc_part_loaded, cct_parts_staging, cct_error_parts
     */
    public static String getTargetTable(PartsQuery query) {
        String targetStage = query.getTargetStage();
        
        if (targetStage == null) {
            return "cc_part_loaded"; // Default table
        }
        
        switch (targetStage) {
            case "cc_part_loaded":
                return "cc_part_loaded";
            case "cct_parts_staging":
                return "cct_parts_staging";
            case "cct_error_parts":
                return "cct_error_parts";
            default:
                return "cc_part_loaded";
        }
    }
    
    /**
     * Generate SQL query based on PartsQuery object
     * This demonstrates how to convert the structured query to database operations
     */
    public static String generateSqlQuery(PartsQuery query) {
        StringBuilder sql = new StringBuilder();
        String tableName = getTargetTable(query);
        
        // Build SELECT clause
        sql.append("SELECT ");
        if (query.getRequestedAttributes().isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", query.getRequestedAttributes()));
        }
        
        sql.append(" FROM ").append(tableName);
        
        // Build WHERE clause
        List<String> conditions = new ArrayList<>();
        
        // Part number filter
        if (query.getPartNumber() != null && !query.getPartNumber().equals("null")) {
            conditions.add("part_number = '" + query.getPartNumber() + "'");
        }
        
        // Contract number filter
        if (query.getContractNumber() != null && !query.getContractNumber().equals("null")) {
            conditions.add("contract_number = '" + query.getContractNumber() + "'");
        }
        
        // Manufacturer filter
        if (query.getManufacturer() != null && !query.getManufacturer().equals("null")) {
            conditions.add("manufacturer = '" + query.getManufacturer() + "'");
        }
        
        // Error type filter (for error parts table)
        if ("cct_error_parts".equals(tableName) && query.getErrorType() != null && !query.getErrorType().equals("null")) {
            conditions.add("error_type = '" + query.getErrorType() + "'");
        }
        
        // Validation status filter
        if (query.getValidationStatus() != null && !query.getValidationStatus().equals("null")) {
            conditions.add("validation_status = '" + query.getValidationStatus() + "'");
        }
        
        // Timeframe filter
        if (query.getTimeframe() != null && !query.getTimeframe().equals("null")) {
            String timeCondition = buildTimeCondition(query.getTimeframe());
            if (timeCondition != null) {
                conditions.add(timeCondition);
            }
        }
        
        // Add WHERE clause if conditions exist
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        return sql.toString();
    }
    
    /**
     * Build time condition for SQL based on timeframe
     */
    private static String buildTimeCondition(String timeframe) {
        switch (timeframe) {
            case "today":
                return "DATE(created_date) = CURDATE()";
            case "yesterday":
                return "DATE(created_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
            case "this_week":
                return "YEARWEEK(created_date) = YEARWEEK(NOW())";
            case "last_week":
                return "YEARWEEK(created_date) = YEARWEEK(NOW()) - 1";
            case "this_month":
                return "YEAR(created_date) = YEAR(NOW()) AND MONTH(created_date) = MONTH(NOW())";
            case "last_month":
                return "YEAR(created_date) = YEAR(NOW()) AND MONTH(created_date) = MONTH(NOW()) - 1";
            default:
                return null;
        }
    }
    
    /**
     * Integration with PartsModelTrainer intents
     * Maps our intents to PartsModelTrainer actions
     */
    public static String getPartsModelTrainerAction(PartsQuery query) {
        String intent = query.getIntent();
        
        switch (intent) {
            case "get_part_details":
                return "GET_PART_DETAILS";
            case "show_all_parts":
                return "SHOW_ALL_PARTS";
            case "show_loaded_parts":
                return "SHOW_LOADED_PARTS";
            case "show_failed_parts":
                return "SHOW_FAILED_PARTS";
            case "get_error_messages":
                return "GET_ERROR_MESSAGES";
            case "show_final_parts":
                return "SHOW_FINAL_PARTS";
            case "search_parts_by_contract":
                return "SEARCH_PARTS_BY_CONTRACT";
            case "search_parts_by_customer":
                return "SEARCH_PARTS_BY_CUSTOMER";
            case "get_validation_status":
                return "GET_VALIDATION_STATUS";
            case "list_parts_by_type":
                return "LIST_PARTS_BY_TYPE";
            default:
                return "SHOW_ALL_PARTS";
        }
    }
    
    /**
     * Demonstration of complete integration workflow
     */
    public static void demonstrateIntegration() {
        System.out.println("🔧 Parts Query Integration Demo");
        System.out.println("=".repeat(80));
        
        // Initialize processors
        PartsQueryProcessor processor = new PartsQueryProcessor();
        
        // Test queries
        String[] testQueries = {
            "faield prts of 123456",
            "specifcations of prduct AE125",
            "show duplicate error parts",
            "parts loaded today",
            "get all metadata for part XY54321"
        };
        
        for (String testQuery : testQueries) {
            System.out.println("\n🔍 Query: \"" + testQuery + "\"");
            
            // Step 1: Process natural language to JSON
            String jsonResult = processor.processPartsQuery(testQuery);
            
            // Step 2: Parse JSON to PartsQuery object
            PartsQuery partsQuery = parseJsonToPartsQuery(jsonResult);
            
            // Step 3: Display routing information
            System.out.println("📊 Confidence: " + String.format("%.3f", partsQuery.getConfidence()));
            System.out.println("🎯 Target Table: " + getTargetTable(partsQuery));
            System.out.println("🔧 Action: " + getPartsModelTrainerAction(partsQuery));
            
            // Step 4: Generate SQL
            String sql = generateSqlQuery(partsQuery);
            System.out.println("📝 Generated SQL:");
            System.out.println("   " + sql);
            
            // Step 5: Execution decision
            if (partsQuery.shouldAutoExecute()) {
                System.out.println("✅ EXECUTE: High confidence - auto-execute query");
            } else if (partsQuery.needsVerification()) {
                System.out.println("⚠️ VERIFY: Medium confidence - verify with user");
            } else {
                System.out.println("❌ CLARIFY: Low confidence - request clarification");
            }
            
            System.out.println("-".repeat(60));
        }
    }
    
    /**
     * Example repository pattern for database integration
     */
    public static class PartsRepository {
        
        /**
         * Execute parts query and return results
         * This method would integrate with your actual database layer
         */
        public List<Map<String, Object>> executePartsQuery(PartsQuery query) {
            // In real implementation, this would:
            // 1. Get target table using getTargetTable(query)
            // 2. Generate SQL using generateSqlQuery(query)
            // 3. Execute query against database
            // 4. Return results
            
            System.out.println("🗄️ Executing query against: " + getTargetTable(query));
            
            // Mock results for demonstration
            List<Map<String, Object>> results = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();
            result.put("part_number", query.getPartNumber());
            result.put("status", "LOADED");
            result.put("manufacturer", query.getManufacturer());
            results.add(result);
            
            return results;
        }
        
        /**
         * Route query to appropriate table based on stage filters
         */
        public String routeQuery(PartsQuery query) {
            String targetStage = query.getTargetStage();
            
            if ("cct_error_parts".equals(targetStage)) {
                return "SELECT * FROM cct_error_parts WHERE " + buildErrorConditions(query);
            } else if ("cct_parts_staging".equals(targetStage)) {
                return "SELECT * FROM cct_parts_staging WHERE " + buildStagingConditions(query);
            } else {
                return "SELECT * FROM cc_part_loaded WHERE " + buildLoadedConditions(query);
            }
        }
        
        private String buildErrorConditions(PartsQuery query) {
            List<String> conditions = new ArrayList<>();
            if (query.getContractNumber() != null) {
                conditions.add("contract_number = '" + query.getContractNumber() + "'");
            }
            if (query.getErrorType() != null) {
                conditions.add("error_type = '" + query.getErrorType() + "'");
            }
            return conditions.isEmpty() ? "1=1" : String.join(" AND ", conditions);
        }
        
        private String buildStagingConditions(PartsQuery query) {
            List<String> conditions = new ArrayList<>();
            if (query.getContractNumber() != null) {
                conditions.add("contract_number = '" + query.getContractNumber() + "'");
            }
            conditions.add("status = 'STAGING'");
            return String.join(" AND ", conditions);
        }
        
        private String buildLoadedConditions(PartsQuery query) {
            List<String> conditions = new ArrayList<>();
            if (query.getPartNumber() != null) {
                conditions.add("part_number = '" + query.getPartNumber() + "'");
            }
            if (query.getContractNumber() != null) {
                conditions.add("contract_number = '" + query.getContractNumber() + "'");
            }
            conditions.add("status = 'LOADED'");
            return String.join(" AND ", conditions);
        }
    }
    
    // Helper methods for JSON parsing
    private static String extractJsonValue(String json, String key) {
        String searchPattern = "\"" + key + "\": \"";
        int start = json.indexOf(searchPattern);
        if (start == -1) {
            // Try without quotes for null values
            searchPattern = "\"" + key + "\": ";
            start = json.indexOf(searchPattern);
            if (start == -1) return null;
            
            start += searchPattern.length();
            int end = Math.min(
                json.indexOf(",", start) != -1 ? json.indexOf(",", start) : json.length(),
                json.indexOf("\n", start) != -1 ? json.indexOf("\n", start) : json.length()
            );
            String value = json.substring(start, end).trim();
            return "null".equals(value) ? null : value;
        }
        
        start += searchPattern.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }
    
    private static List<String> extractJsonArray(String json, String key) {
        List<String> result = new ArrayList<>();
        String searchPattern = "\"" + key + "\": [";
        int start = json.indexOf(searchPattern);
        if (start == -1) return result;
        
        start += searchPattern.length();
        int end = json.indexOf("]", start);
        if (end == -1) return result;
        
        String arrayContent = json.substring(start, end);
        if (arrayContent.trim().isEmpty()) return result;
        
        // Simple parsing - in production use proper JSON parser
        String[] items = arrayContent.split(",");
        for (String item : items) {
            String cleaned = item.trim().replaceAll("\"", "");
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }
        
        return result;
    }
    
    /**
     * Main method for integration demonstration
     */
    public static void main(String[] args) {
        System.out.println("🚀 Parts Query Processing Integration Demo");
        System.out.println("📋 Demonstrating JSON-to-Java mapping and database routing");
        System.out.println();
        
        demonstrateIntegration();
        
        System.out.println("\n📚 Integration Summary:");
        System.out.println("✅ JSON parsing and object mapping");
        System.out.println("✅ Database table routing");
        System.out.println("✅ SQL generation");
        System.out.println("✅ Confidence-based execution decisions");
        System.out.println("✅ PartsModelTrainer action mapping");
        System.out.println("✅ Repository pattern implementation");
        
        System.out.println("\n🔗 Integration Points:");
        System.out.println("• PartsQueryProcessor.processPartsQuery() → JSON");
        System.out.println("• PartsQueryIntegration.parseJsonToPartsQuery() → PartsQuery object");
        System.out.println("• PartsQueryIntegration.generateSqlQuery() → Database query");
        System.out.println("• PartsRepository.executePartsQuery() → Results");
    }
}
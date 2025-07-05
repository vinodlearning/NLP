package view.practice;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Comprehensive Parts Query Processing System
 * 
 * This system integrates comprehensive spell correction, stage-aware routing,
 * and entity extraction to process natural language queries about parts.
 * 
 * Features:
 * - Enhanced spell correction with 100+ domain-specific corrections
 * - Stage-aware database routing (cc_part_loaded, cct_parts_staging, cct_error_parts)
 * - Error type detection and classification
 * - Comprehensive entity extraction (part numbers, contracts, manufacturers)
 * - Confidence-based processing with auto-execute capabilities
 * - Integration with existing PartsModelTrainer system
 * 
 * @author Parts Management System
 * @version 1.0
 */
public class PartsQueryProcessor {
    
    private static final Logger logger = Logger.getLogger(PartsQueryProcessor.class.getName());
    
    // Core components
    private SpellChecker spellChecker;
    
    // Configuration
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.3;
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.90;
    private static final double MEDIUM_CONFIDENCE_THRESHOLD = 0.70;
    
    // Parts table schema and routing
    private static final Map<String, String> STAGE_ROUTING = new HashMap<String, String>() {{
        put("loaded", "cc_part_loaded");
        put("success", "cc_part_loaded");
        put("successful", "cc_part_loaded");
        put("passed", "cc_part_loaded");
        put("pasd", "cc_part_loaded");
        put("active", "cc_part_loaded");
        
        put("staging", "cct_parts_staging");
        put("upload", "cct_parts_staging");
        put("pending", "cct_parts_staging");
        put("uploading", "cct_parts_staging");
        
        put("failed", "cct_error_parts");
        put("faild", "cct_error_parts");
        put("filde", "cct_error_parts");
        put("error", "cct_error_parts");
        put("errors", "cct_error_parts");
        put("rejected", "cct_error_parts");
        put("issues", "cct_error_parts");
        put("defect", "cct_error_parts");
        put("defects", "cct_error_parts");
        put("problem", "cct_error_parts");
        put("problems", "cct_error_parts");
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
    
    // Processing statistics
    private ProcessingStats stats;
    
    /**
     * Initialize the parts query processor
     */
    public PartsQueryProcessor() {
        try {
            // Initialize enhanced spell checker
            this.spellChecker = new SpellChecker();
            
            // Initialize statistics
            this.stats = new ProcessingStats();
            
            logger.info("PartsQueryProcessor initialized successfully with enhanced spell correction");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize PartsQueryProcessor", e);
            throw new RuntimeException("Initialization failed", e);
        }
    }
    
    /**
     * Main processing method - accepts natural language query and returns JSON
     */
    public String processQuery(String originalQuery) {
        long startTime = System.nanoTime();
        
        try {
            // Input validation
            if (originalQuery == null || originalQuery.trim().isEmpty()) {
                return generateErrorJson("Empty query provided", 0.0);
            }
            
            // Step 1: Comprehensive spell correction
            String correctedQuery = performComprehensiveSpellCorrection(originalQuery);
            String normalizedQuery = normalizeQuery(correctedQuery);
            
            // Step 2: Entity extraction
            Map<String, String> entities = extractEntities(normalizedQuery);
            
            // Step 3: Intent determination
            String intent = determineIntent(normalizedQuery, entities);
            
            // Step 4: Stage and error type determination
            String targetStage = determineTargetStage(normalizedQuery, intent);
            String errorType = determineErrorType(normalizedQuery);
            
            // Step 5: Attribute extraction
            List<String> attributes = extractAttributes(normalizedQuery, intent);
            
            // Step 6: Context extraction
            Map<String, String> context = extractContext(normalizedQuery);
            
            // Step 7: Enhanced confidence calculation
            double confidence = calculateEnhancedConfidence(originalQuery, correctedQuery, entities, intent, targetStage);
            
            // Step 8: Generate comprehensive JSON
            String jsonResult = generateComprehensiveJSON(originalQuery, normalizedQuery, confidence, intent, 
                                                        entities, attributes, targetStage, errorType, context);
            
            // Update statistics
            long processingTime = System.nanoTime() - startTime;
            stats.recordProcessing(true, processingTime, confidence);
            
            logger.info(String.format("Successfully processed parts query: '%s' -> confidence: %.3f", 
                                     originalQuery, confidence));
            
            return jsonResult;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error processing parts query: " + originalQuery, e);
            long processingTime = System.nanoTime() - startTime;
            stats.recordProcessing(false, processingTime, 0.0);
            
            return generateErrorJson("Processing error: " + e.getMessage(), 0.0);
        }
    }
    
    /**
     * Perform comprehensive spell correction using enhanced SpellChecker
     */
    private String performComprehensiveSpellCorrection(String originalQuery) {
        try {
            // Use the enhanced comprehensive spell checking
            String correctedQuery = spellChecker.performComprehensiveSpellCheck(originalQuery);
            
            // Log corrections if any were made
            if (!originalQuery.equals(correctedQuery)) {
                logger.info(String.format("Parts spell correction: '%s' -> '%s'", 
                                         originalQuery, correctedQuery));
            }
            
            return correctedQuery;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Spell correction failed, using original query", e);
            return originalQuery;
        }
    }
    
    /**
     * Normalize query text for consistent processing
     */
    private String normalizeQuery(String query) {
        if (query == null) return "";
        
        return query.toLowerCase()
                   .trim()
                   .replaceAll("\\s+", " ")
                   .replaceAll("[^a-zA-Z0-9\\s-]", " ")
                   .trim();
    }
    
    /**
     * Extract entities from the query
     */
    private Map<String, String> extractEntities(String query) {
        Map<String, String> entities = new HashMap<>();
        
        // Extract part numbers with enhanced patterns
        Pattern partPattern = Pattern.compile("\\b(ae\\d+|AE\\d+|[A-Z]{2,3}\\d{3,6})\\b");
        Matcher partMatcher = partPattern.matcher(query);
        if (partMatcher.find()) {
            entities.put("partNumber", partMatcher.group(1).toUpperCase());
        }
        
        // Extract contract numbers
        Pattern contractPattern = Pattern.compile("\\b(\\d{6}|contract\\s+(\\d{4,8}))\\b");
        Matcher contractMatcher = contractPattern.matcher(query);
        if (contractMatcher.find()) {
            String contractNumber = contractMatcher.group(1);
            if (contractNumber.startsWith("contract")) {
                contractNumber = contractMatcher.group(2);
            }
            entities.put("contractNumber", contractNumber);
        }
        
        // Extract manufacturer names
        String[] manufacturers = {"boeing", "honeywell", "siemens", "ge", "ford", "gm", "caterpillar"};
        for (String manufacturer : manufacturers) {
            if (query.contains(manufacturer)) {
                entities.put("manufacturer", manufacturer.toUpperCase());
                break;
            }
        }
        
        return entities;
    }
    
    /**
     * Determine intent based on query analysis
     */
    private String determineIntent(String query, Map<String, String> entities) {
        // Prioritize based on context clues
        if (query.contains("specification") || query.contains("datasheet") || query.contains("details")) {
            return "get_part_details";
        } else if (query.contains("failed") || query.contains("error") || query.contains("rejected")) {
            if (query.contains("why") || query.contains("reason") || query.contains("because")) {
                return "get_error_messages";
            } else {
                return "show_failed_parts";
            }
        } else if (query.contains("loaded") || query.contains("successful") || query.contains("passed")) {
            return "show_loaded_parts";
        } else if (query.contains("contract") && entities.containsKey("contractNumber")) {
            return "search_parts_by_contract";
        } else if (query.contains("list") || query.contains("show") || query.contains("get")) {
            return "show_all_parts";
        } else if (query.contains("available") || query.contains("stock")) {
            return "check_part_availability";
        } else if (query.contains("price") || query.contains("cost")) {
            return "get_part_pricing";
        } else if (query.contains("lead") && query.contains("time")) {
            return "get_lead_time";
        } else if (query.contains("manufacturer")) {
            return "get_manufacturer_info";
        } else {
            return "get_part_details";
        }
    }
    
    /**
     * Determine target database stage
     */
    private String determineTargetStage(String query, String intent) {
        // Check for explicit stage keywords
        for (Map.Entry<String, String> entry : STAGE_ROUTING.entrySet()) {
            if (query.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Intent-based routing
        switch (intent) {
            case "show_failed_parts":
            case "get_error_messages":
                return "cct_error_parts";
            case "show_loaded_parts":
            case "check_part_availability":
                return "cc_part_loaded";
            default:
                return "cc_part_loaded";
        }
    }
    
    /**
     * Determine error type
     */
    private String determineErrorType(String query) {
        for (Map.Entry<String, String> entry : ERROR_TYPE_MAPPING.entrySet()) {
            if (query.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    /**
     * Extract attributes based on intent
     */
    private List<String> extractAttributes(String query, String intent) {
        List<String> attributes = new ArrayList<>();
        
        switch (intent) {
            case "get_part_details":
                attributes.addAll(Arrays.asList("specifications", "price", "lead_time", "manufacturer"));
                break;
            case "show_failed_parts":
                attributes.addAll(Arrays.asList("part_number", "error_message", "validation_status"));
                break;
            case "get_error_messages":
                attributes.addAll(Arrays.asList("error_message", "validation_date"));
                break;
            case "check_part_availability":
                attributes.addAll(Arrays.asList("availability_status", "stock_quantity"));
                break;
            case "get_part_pricing":
                attributes.addAll(Arrays.asList("price", "currency", "effective_date"));
                break;
            case "get_lead_time":
                attributes.addAll(Arrays.asList("lead_time", "delivery_date"));
                break;
            case "get_manufacturer_info":
                attributes.addAll(Arrays.asList("manufacturer", "contact_info"));
                break;
            default:
                attributes.add("part_number");
        }
        
        return attributes;
    }
    
    /**
     * Extract context information
     */
    private Map<String, String> extractContext(String query) {
        Map<String, String> context = new HashMap<>();
        
        if (query.contains("today")) {
            context.put("timeframe", "today");
        }
        if (query.contains("stock")) {
            context.put("availability", "stock_check");
        }
        if (query.contains("warranty")) {
            context.put("warranty", "warranty_check");
        }
        if (query.contains("urgent")) {
            context.put("priority", "urgent");
        }
        if (query.contains("discontinued")) {
            context.put("status", "discontinued");
        }
        
        return context;
    }
    
    /**
     * Calculate enhanced confidence score
     */
    private double calculateEnhancedConfidence(String original, String corrected, Map<String, String> entities, 
                                             String intent, String targetStage) {
        double confidence = 0.70; // Higher base confidence
        
        // Entity extraction boosts
        if (entities.containsKey("partNumber")) {
            confidence += 0.25; // Strong boost for part numbers
        }
        if (entities.containsKey("contractNumber")) {
            confidence += 0.20; // Good boost for contract numbers
        }
        if (entities.containsKey("manufacturer")) {
            confidence += 0.10; // Moderate boost for manufacturers
        }
        
        // Intent clarity boost
        if (!intent.equals("get_part_details")) {
            confidence += 0.15; // Boost for specific intents
        }
        
        // Stage routing confidence
        if (targetStage != null) {
            confidence += 0.10;
        }
        
        // Multiple entities boost
        if (entities.size() > 1) {
            confidence += 0.05;
        }
        
        // Spell correction impact (minimal penalty)
        double similarity = calculateSimilarity(original.toLowerCase(), corrected.toLowerCase());
        confidence *= (0.90 + (similarity * 0.10)); // Gentle penalty
        
        // Domain-specific boosts
        if (corrected.contains("specifications") || corrected.contains("details")) {
            confidence += 0.05;
        }
        if (corrected.contains("failed") || corrected.contains("error")) {
            confidence += 0.05;
        }
        
        return Math.min(0.98, Math.max(0.40, confidence));
    }
    
    /**
     * Generate comprehensive JSON response
     */
    private String generateComprehensiveJSON(String originalQuery, String normalizedQuery, double confidence,
                                           String intent, Map<String, String> entities, List<String> attributes,
                                           String targetStage, String errorType, Map<String, String> context) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // Metadata section
        json.append("  \"metadata\": {\n");
        json.append("    \"originalQuery\": \"").append(escapeJson(originalQuery)).append("\",\n");
        json.append("    \"normalizedQuery\": \"").append(escapeJson(normalizedQuery)).append("\",\n");
        json.append("    \"confidence\": ").append(String.format("%.3f", confidence)).append(",\n");
        json.append("    \"intent\": \"").append(intent).append("\",\n");
        json.append("    \"processingTime\": \"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
        json.append("    \"processor\": \"PartsQueryProcessor\",\n");
        json.append("    \"version\": \"1.0\"\n");
        json.append("  },\n");
        
        // Entities section
        json.append("  \"entities\": {\n");
        json.append("    \"partNumber\": ").append(entities.containsKey("partNumber") ? "\"" + entities.get("partNumber") + "\"" : "null").append(",\n");
        json.append("    \"contractNumber\": ").append(entities.containsKey("contractNumber") ? "\"" + entities.get("contractNumber") + "\"" : "null").append(",\n");
        json.append("    \"manufacturer\": ").append(entities.containsKey("manufacturer") ? "\"" + entities.get("manufacturer") + "\"" : "null").append("\n");
        json.append("  },\n");
        
        // Requested attributes section
        json.append("  \"requestedAttributes\": [");
        for (int i = 0; i < attributes.size(); i++) {
            if (i > 0) json.append(", ");
            json.append("\"").append(attributes.get(i)).append("\"");
        }
        json.append("],\n");
        
        // Stage filters section
        json.append("  \"stageFilters\": {\n");
        json.append("    \"targetStage\": \"").append(targetStage).append("\",\n");
        json.append("    \"errorType\": ").append(errorType != null ? "\"" + errorType + "\"" : "null").append("\n");
        json.append("  },\n");
        
        // Context section
        json.append("  \"context\": {\n");
        boolean first = true;
        for (Map.Entry<String, String> entry : context.entrySet()) {
            if (!first) json.append(",\n");
            json.append("    \"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\"");
            first = false;
        }
        if (first) {
            json.append("    \"validationStatus\": null,\n");
            json.append("    \"timeframe\": null");
        }
        json.append("\n  },\n");
        
        // Processing instructions section
        json.append("  \"processingInstructions\": {\n");
        json.append("    \"autoExecute\": ").append(confidence >= HIGH_CONFIDENCE_THRESHOLD ? "true" : "false").append(",\n");
        json.append("    \"requiresVerification\": ").append(confidence >= MEDIUM_CONFIDENCE_THRESHOLD && confidence < HIGH_CONFIDENCE_THRESHOLD ? "true" : "false").append(",\n");
        json.append("    \"recommendedAction\": \"");
        if (confidence >= HIGH_CONFIDENCE_THRESHOLD) {
            json.append("AUTO_EXECUTE");
        } else if (confidence >= MEDIUM_CONFIDENCE_THRESHOLD) {
            json.append("VERIFY_WITH_USER");
        } else {
            json.append("REQUEST_CLARIFICATION");
        }
        json.append("\"\n");
        json.append("  }\n");
        
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Generate error JSON response
     */
    private String generateErrorJson(String error, double confidence) {
        return "{\n" +
               "  \"metadata\": {\n" +
               "    \"error\": \"" + escapeJson(error) + "\",\n" +
               "    \"confidence\": " + String.format("%.3f", confidence) + ",\n" +
               "    \"timestamp\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\"\n" +
               "  },\n" +
               "  \"entities\": {},\n" +
               "  \"requestedAttributes\": [],\n" +
               "  \"stageFilters\": {},\n" +
               "  \"context\": {},\n" +
               "  \"processingInstructions\": {\n" +
               "    \"autoExecute\": false,\n" +
               "    \"requiresVerification\": false,\n" +
               "    \"recommendedAction\": \"REQUEST_CLARIFICATION\"\n" +
               "  }\n" +
               "}";
    }
    
    /**
     * Helper methods
     */
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
    
    /**
     * Processing statistics
     */
    public ProcessingStats getProcessingStatistics() {
        return stats;
    }
    
    public void resetStatistics() {
        stats = new ProcessingStats();
    }
    
    /**
     * Processing statistics class
     */
    public static class ProcessingStats {
        private int totalQueries = 0;
        private int successfulQueries = 0;
        private long totalProcessingTime = 0;
        private double averageConfidence = 0.0;
        private List<Double> confidenceScores = new ArrayList<>();
        
        public void recordProcessing(boolean successful, long processingTimeNanos, double confidence) {
            totalQueries++;
            if (successful) {
                successfulQueries++;
                confidenceScores.add(confidence);
                
                // Update average confidence
                double sum = 0.0;
                for (Double score : confidenceScores) {
                    sum += score;
                }
                averageConfidence = sum / confidenceScores.size();
            }
            totalProcessingTime += processingTimeNanos;
        }
        
        public int getTotalQueries() { return totalQueries; }
        public int getSuccessfulQueries() { return successfulQueries; }
        public double getSuccessRate() { 
            return totalQueries > 0 ? (double) successfulQueries / totalQueries : 0.0;
        }
        public double getAverageProcessingTimeMs() { 
            return totalQueries > 0 ? (totalProcessingTime / 1_000_000.0) / totalQueries : 0.0;
        }
        public double getAverageConfidence() { return averageConfidence; }
        
        @Override
        public String toString() {
            return String.format("ProcessingStats{totalQueries=%d, successfulQueries=%d, " +
                               "successRate=%.2f%%, avgProcessingTime=%.2fms, avgConfidence=%.3f}",
                               totalQueries, successfulQueries, getSuccessRate() * 100,
                               getAverageProcessingTimeMs(), averageConfidence);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        PartsQueryProcessor processor = new PartsQueryProcessor();
        
        // Test queries with heavy spell errors
        String[] testQueries = {
            "lst out contrcts with part numbr AE125",
            "whats the specifcations of prduct AE125",
            "shwo mee parts 123456",
            "faield prts of 123456",
            "ae125 faild becasue no cost data"
        };
        
        System.out.println("🚀 Testing Parts Query Processor with Enhanced Spell Correction");
        System.out.println("==============================================================");
        
        for (String query : testQueries) {
            System.out.println("\n📝 Query: \"" + query + "\"");
            String result = processor.processQuery(query);
            System.out.println("📋 Result: " + result);
        }
        
        System.out.println("\n📊 Processing Statistics:");
        System.out.println(processor.getProcessingStatistics().toString());
    }
}
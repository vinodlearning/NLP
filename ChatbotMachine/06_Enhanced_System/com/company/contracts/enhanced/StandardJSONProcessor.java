package com.company.contracts.enhanced;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Standard JSON Processor
 * Follows JSON_DESIGN.md standards and architecture_design.md requirements
 * Returns only the required JSON format with inputTracking
 */
public class StandardJSONProcessor {
    
    // Business rule patterns
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("\\d{6,}");
    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile("[A-Za-z0-9]{3,}");
    private static final Pattern CUSTOMER_NUMBER_PATTERN = Pattern.compile("\\d{4,8}");
    
    // Command words to filter out
    private static final Set<String> COMMAND_WORDS = Set.of(
        "show", "get", "list", "find", "display", "fetch", "retrieve", "give", "provide",
        "what", "how", "why", "when", "where", "which", "who", "is", "are", "can", "will",
        "the", "of", "for", "in", "on", "at", "by", "with", "from", "to", "and", "or",
        "contract", "contracts", "part", "parts", "customer", "account", "info", "details",
        "status", "data", "all", "any", "some", "many", "much", "more", "most", "less",
        "created", "expired", "active", "inactive", "failed", "passed", "loaded", "missing"
    );
    
    // Customer context words
    private static final Set<String> CUSTOMER_CONTEXT_WORDS = Set.of(
        "customer", "customers", "client", "clients", "account", "accounts"
    );
    
    // Spell corrections (basic implementation)
    private static final Map<String, String> SPELL_CORRECTIONS = Map.of(
        "contrct", "contract",
        "contrcts", "contracts", 
        "contrat", "contract",
        "custmer", "customer",
        "custmers", "customers",
        "prt", "part",
        "prts", "parts"
    );
    
    /**
     * Process query and return JSON string following JSON_DESIGN.md standards
     */
    public String processQuery(String originalInput) {
        long startTime = System.nanoTime();
        
        try {
            // Step 1: Input tracking and spell correction
            InputTrackingResult inputTracking = processInputTracking(originalInput);
            String processedInput = inputTracking.correctedInput != null ? 
                                  inputTracking.correctedInput : originalInput;
            
            // Step 2: Header analysis
            HeaderResult headerResult = analyzeHeaders(processedInput);
            
            // Step 3: Entity extraction
            List<EntityFilter> entities = extractEntities(processedInput);
            
            // Step 4: Validation
            List<ValidationError> errors = validateInput(headerResult, entities, processedInput);
            
            // Step 5: Query metadata
            QueryMetadata metadata = determineQueryMetadata(headerResult, entities, errors);
            
            // Step 6: Display entities
            List<String> displayEntities = determineDisplayEntities(processedInput, headerResult, entities, metadata);
            
            // Calculate processing time
            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0;
            metadata.processingTimeMs = processingTime;
            
            // Step 7: Generate JSON according to JSON_DESIGN.md
            return generateStandardJSON(originalInput, inputTracking, headerResult, metadata, entities, displayEntities, errors);
            
        } catch (Exception e) {
            // Error fallback
            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0;
            
            return generateErrorJSON(originalInput, e.getMessage(), processingTime);
        }
    }
    
    /**
     * Process input tracking with spell correction
     */
    private InputTrackingResult processInputTracking(String originalInput) {
        String[] words = originalInput.toLowerCase().split("\\s+");
        StringBuilder correctedBuilder = new StringBuilder();
        boolean hasCorrectionss = false;
        int totalWords = words.length;
        int correctedWords = 0;
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (SPELL_CORRECTIONS.containsKey(word)) {
                correctedBuilder.append(SPELL_CORRECTIONS.get(word));
                hasCorrectionss = true;
                correctedWords++;
            } else {
                correctedBuilder.append(word);
            }
            
            if (i < words.length - 1) {
                correctedBuilder.append(" ");
            }
        }
        
        String correctedInput = hasCorrectionss ? correctedBuilder.toString() : null;
        double confidence = totalWords > 0 ? (double) correctedWords / totalWords : 0.0;
        
        return new InputTrackingResult(originalInput, correctedInput, confidence);
    }
    
    /**
     * Analyze headers following the fixed parsing logic
     */
    private HeaderResult analyzeHeaders(String input) {
        Header header = new Header();
        List<String> issues = new ArrayList<>();
        
        String cleanInput = input.toLowerCase().trim();
        String[] tokens = cleanInput.split("[;\\s,]+");
        
        // Check for customer context
        boolean hasCustomerContext = Arrays.stream(tokens)
            .anyMatch(CUSTOMER_CONTEXT_WORDS::contains);
        
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty() || COMMAND_WORDS.contains(token)) {
                continue;
            }
            
            // Explicit prefixes
            if (token.startsWith("contract")) {
                String numberPart = token.substring("contract".length());
                if (!numberPart.isEmpty() && CONTRACT_NUMBER_PATTERN.matcher(numberPart).matches()) {
                    header.contractNumber = numberPart;
                } else if (!numberPart.isEmpty()) {
                    issues.add("Contract number '" + numberPart + "' must be 6+ digits");
                }
            } else if (token.startsWith("part")) {
                String numberPart = token.substring("part".length());
                if (!numberPart.isEmpty() && PART_NUMBER_PATTERN.matcher(numberPart).matches()) {
                    header.partNumber = numberPart.toUpperCase();
                } else if (!numberPart.isEmpty()) {
                    issues.add("Part number '" + numberPart + "' must be 3+ alphanumeric characters");
                }
            } else if (token.startsWith("customer")) {
                String numberPart = token.substring("customer".length());
                if (!numberPart.isEmpty() && CUSTOMER_NUMBER_PATTERN.matcher(numberPart).matches()) {
                    header.customerNumber = numberPart;
                } else if (!numberPart.isEmpty()) {
                    issues.add("Customer number '" + numberPart + "' must be 4-8 digits");
                }
            }
            // Standalone numbers
            else if (token.matches("\\d+")) {
                if (hasCustomerContext && CUSTOMER_NUMBER_PATTERN.matcher(token).matches()) {
                    header.customerNumber = token;
                } else if (token.length() >= 6) {
                    header.contractNumber = token;
                } else {
                    issues.add("Number '" + token + "' too short for contract number (need 6+ digits)");
                }
            }
            // Alphanumeric tokens (potential part numbers)
            else if (token.matches("[A-Za-z0-9]+") && token.length() >= 3) {
                if (containsLettersAndNumbers(token) || token.equals(token.toUpperCase())) {
                    header.partNumber = token.toUpperCase();
                }
            }
        }
        
        return new HeaderResult(header, issues);
    }
    
    private boolean containsLettersAndNumbers(String token) {
        boolean hasLetter = false, hasNumber = false;
        for (char c : token.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (hasLetter && hasNumber) return true;
        }
        return false;
    }
    
    /**
     * Extract entities (filters)
     */
    private List<EntityFilter> extractEntities(String input) {
        List<EntityFilter> entities = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        // Date filters
        if (lowerInput.contains("created in")) {
            String year = extractYear(lowerInput);
            if (year != null) {
                entities.add(new EntityFilter("CREATED_DATE", "=", year, "user_input"));
            }
        }
        
        // Status filters
        if (lowerInput.contains("status")) {
            String status = extractStatus(lowerInput);
            if (status != null) {
                entities.add(new EntityFilter("STATUS", "=", status, "user_input"));
            }
        }
        
        return entities;
    }
    
    private String extractYear(String input) {
        Pattern yearPattern = Pattern.compile("\\b(20\\d{2})\\b");
        java.util.regex.Matcher matcher = yearPattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }
    
    private String extractStatus(String input) {
        if (input.contains("active")) return "ACTIVE";
        if (input.contains("inactive")) return "INACTIVE";
        if (input.contains("pending")) return "PENDING";
        return null;
    }
    
    /**
     * Validate input
     */
    private List<ValidationError> validateInput(HeaderResult headerResult, List<EntityFilter> entities, String input) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Add header issues
        headerResult.issues.forEach(issue -> 
            errors.add(new ValidationError("INVALID_HEADER", issue, "BLOCKER"))
        );
        
        // Check if we have at least one header or entity
        Header header = headerResult.header;
        boolean hasValidHeader = header.contractNumber != null || 
                                header.partNumber != null || 
                                header.customerNumber != null || 
                                header.customerName != null || 
                                header.createdBy != null;
        
        // Allow general queries
        String lowerInput = input.toLowerCase();
        boolean isGeneralQuery = lowerInput.contains("all") || 
                                lowerInput.contains("list") || 
                                lowerInput.contains("show") ||
                                lowerInput.contains("status") ||
                                lowerInput.contains("details");
        
        if (!hasValidHeader && entities.isEmpty() && !isGeneralQuery) {
            errors.add(new ValidationError("MISSING_HEADER", 
                "Provide at least one identifier (contract/part/customer) or filter (date/status)", 
                "BLOCKER"));
        }
        
        return errors;
    }
    
    /**
     * Determine query metadata
     */
    private QueryMetadata determineQueryMetadata(HeaderResult headerResult, List<EntityFilter> entities, List<ValidationError> errors) {
        if (!errors.isEmpty() && errors.stream().anyMatch(e -> "BLOCKER".equals(e.severity))) {
            return new QueryMetadata("CONTRACTS", "error", 0);
        }
        
        Header header = headerResult.header;
        
        // Determine query type
        String queryType = "CONTRACTS"; // Default
        if (header.partNumber != null && header.contractNumber == null) {
            queryType = "PARTS";
        }
        
        // Determine action type
        String actionType = determineActionType(header, entities);
        
        return new QueryMetadata(queryType, actionType, 0);
    }
    
    private String determineActionType(Header header, List<EntityFilter> entities) {
        if (header.contractNumber != null) return "contracts_by_contractNumber";
        if (header.partNumber != null) return "parts_by_partNumber";
        if (header.customerNumber != null) return "contracts_by_customerNumber";
        if (header.customerName != null) return "contracts_by_customerName";
        if (header.createdBy != null) return "contracts_by_createdBy";
        
        if (!entities.isEmpty()) {
            EntityFilter firstEntity = entities.get(0);
            switch (firstEntity.attribute) {
                case "CREATED_DATE": return "contracts_by_date";
                case "STATUS": return "contracts_by_status";
                default: return "contracts_by_filter";
            }
        }
        
        return "general_query";
    }
    
    /**
     * Determine display entities
     */
    private List<String> determineDisplayEntities(String input, HeaderResult headerResult, 
                                                 List<EntityFilter> entities, QueryMetadata metadata) {
        List<String> displayEntities = new ArrayList<>();
        
        // Add defaults based on query type
        if ("CONTRACTS".equals(metadata.queryType)) {
            displayEntities.add("CONTRACT_NUMBER");
            displayEntities.add("CUSTOMER_NAME");
        } else if ("PARTS".equals(metadata.queryType)) {
            displayEntities.add("PART_NUMBER");
            displayEntities.add("DESCRIPTION");
        }
        
        // Auto-add filtered fields
        entities.forEach(entity -> {
            if (!displayEntities.contains(entity.attribute)) {
                displayEntities.add(entity.attribute);
            }
        });
        
        // Add user-requested fields
        String lowerInput = input.toLowerCase();
        if (lowerInput.contains("effective date") && !displayEntities.contains("EFFECTIVE_DATE")) {
            displayEntities.add("EFFECTIVE_DATE");
        }
        if (lowerInput.contains("status") && !displayEntities.contains("STATUS")) {
            displayEntities.add("STATUS");
        }
        if ((lowerInput.contains("expiration") || lowerInput.contains("expiry")) && !displayEntities.contains("EXPIRATION_DATE")) {
            displayEntities.add("EXPIRATION_DATE");
        }
        if ((lowerInput.contains("price") || lowerInput.contains("cost")) && !displayEntities.contains("TOTAL_VALUE")) {
            displayEntities.add("TOTAL_VALUE");
        }
        
        return displayEntities;
    }
    
    /**
     * Generate standard JSON following JSON_DESIGN.md exactly
     */
    private String generateStandardJSON(String originalInput, InputTrackingResult inputTracking, 
                                       HeaderResult headerResult, QueryMetadata metadata, 
                                       List<EntityFilter> entities, List<String> displayEntities, 
                                       List<ValidationError> errors) {
        StringBuilder json = new StringBuilder();
        
        json.append("{\n");
        
        // Header section with inputTracking
        json.append("  \"header\": {\n");
        json.append("    \"contractNumber\": ").append(quote(headerResult.header.contractNumber)).append(",\n");
        json.append("    \"partNumber\": ").append(quote(headerResult.header.partNumber)).append(",\n");
        json.append("    \"customerNumber\": ").append(quote(headerResult.header.customerNumber)).append(",\n");
        json.append("    \"customerName\": ").append(quote(headerResult.header.customerName)).append(",\n");
        json.append("    \"createdBy\": ").append(quote(headerResult.header.createdBy)).append(",\n");
        
        // InputTracking section (NEW as per JSON_DESIGN.md)
        json.append("    \"inputTracking\": {\n");
        json.append("      \"originalInput\": ").append(quote(inputTracking.originalInput)).append(",\n");
        json.append("      \"correctedInput\": ").append(quote(inputTracking.correctedInput)).append(",\n");
        json.append("      \"correctionConfidence\": ").append(inputTracking.correctionConfidence).append("\n");
        json.append("    }\n");
        json.append("  },\n");
        
        // QueryMetadata section
        json.append("  \"queryMetadata\": {\n");
        json.append("    \"queryType\": ").append(quote(metadata.queryType)).append(",\n");
        json.append("    \"actionType\": ").append(quote(metadata.actionType)).append(",\n");
        json.append("    \"processingTimeMs\": ").append(String.format("%.3f", metadata.processingTimeMs)).append("\n");
        json.append("  },\n");
        
        // Entities section
        json.append("  \"entities\": [\n");
        for (int i = 0; i < entities.size(); i++) {
            EntityFilter entity = entities.get(i);
            json.append("    {\n");
            json.append("      \"attribute\": ").append(quote(entity.attribute)).append(",\n");
            json.append("      \"operation\": ").append(quote(entity.operation)).append(",\n");
            json.append("      \"value\": ").append(quote(entity.value)).append(",\n");
            json.append("      \"source\": ").append(quote(entity.source)).append("\n");
            json.append("    }").append(i < entities.size() - 1 ? "," : "").append("\n");
        }
        json.append("  ],\n");
        
        // DisplayEntities section
        json.append("  \"displayEntities\": [\n");
        for (int i = 0; i < displayEntities.size(); i++) {
            json.append("    ").append(quote(displayEntities.get(i)));
            json.append(i < displayEntities.size() - 1 ? "," : "").append("\n");
        }
        json.append("  ],\n");
        
        // Errors section
        json.append("  \"errors\": [\n");
        for (int i = 0; i < errors.size(); i++) {
            ValidationError error = errors.get(i);
            json.append("    {\n");
            json.append("      \"code\": ").append(quote(error.code)).append(",\n");
            json.append("      \"message\": ").append(quote(error.message)).append(",\n");
            json.append("      \"severity\": ").append(quote(error.severity)).append("\n");
            json.append("    }").append(i < errors.size() - 1 ? "," : "").append("\n");
        }
        json.append("  ]\n");
        
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Generate error JSON
     */
    private String generateErrorJSON(String originalInput, String errorMessage, double processingTime) {
        return String.format(
            "{\n" +
            "  \"header\": {\n" +
            "    \"contractNumber\": null,\n" +
            "    \"partNumber\": null,\n" +
            "    \"customerNumber\": null,\n" +
            "    \"customerName\": null,\n" +
            "    \"createdBy\": null,\n" +
            "    \"inputTracking\": {\n" +
            "      \"originalInput\": %s,\n" +
            "      \"correctedInput\": null,\n" +
            "      \"correctionConfidence\": 0\n" +
            "    }\n" +
            "  },\n" +
            "  \"queryMetadata\": {\n" +
            "    \"queryType\": \"CONTRACTS\",\n" +
            "    \"actionType\": \"error\",\n" +
            "    \"processingTimeMs\": %.3f\n" +
            "  },\n" +
            "  \"entities\": [],\n" +
            "  \"displayEntities\": [],\n" +
            "  \"errors\": [\n" +
            "    {\n" +
            "      \"code\": \"PROCESSING_ERROR\",\n" +
            "      \"message\": %s,\n" +
            "      \"severity\": \"BLOCKER\"\n" +
            "    }\n" +
            "  ]\n" +
            "}",
            quote(originalInput), processingTime, quote(errorMessage)
        );
    }
    
    private String quote(String value) {
        return value == null ? "null" : "\"" + value.replace("\"", "\\\"") + "\"";
    }
    
    // Data classes
    private static class InputTrackingResult {
        final String originalInput;
        final String correctedInput;
        final double correctionConfidence;
        
        InputTrackingResult(String originalInput, String correctedInput, double correctionConfidence) {
            this.originalInput = originalInput;
            this.correctedInput = correctedInput;
            this.correctionConfidence = correctionConfidence;
        }
    }
    
    private static class Header {
        String contractNumber;
        String partNumber;
        String customerNumber;
        String customerName;
        String createdBy;
    }
    
    private static class HeaderResult {
        final Header header;
        final List<String> issues;
        
        HeaderResult(Header header, List<String> issues) {
            this.header = header;
            this.issues = issues;
        }
    }
    
    private static class QueryMetadata {
        final String queryType;
        final String actionType;
        double processingTimeMs;
        
        QueryMetadata(String queryType, String actionType, double processingTimeMs) {
            this.queryType = queryType;
            this.actionType = actionType;
            this.processingTimeMs = processingTimeMs;
        }
    }
    
    private static class EntityFilter {
        final String attribute;
        final String operation;
        final String value;
        final String source;
        
        EntityFilter(String attribute, String operation, String value, String source) {
            this.attribute = attribute;
            this.operation = operation;
            this.value = value;
            this.source = source;
        }
    }
    
    private static class ValidationError {
        final String code;
        final String message;
        final String severity;
        
        ValidationError(String code, String message, String severity) {
            this.code = code;
            this.message = message;
            this.severity = severity;
        }
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        String[] testInputs = {
            "show contract 123456",
            "contrct details 789012",  // With spell error
            "get part info AE125",
            "show customer 12345678 contracts"
        };
        
        for (String input : testInputs) {
            System.out.printf("\n🔍 INPUT: \"%s\"\n", input);
            System.out.println("=".repeat(60));
            String jsonResponse = processor.processQuery(input);
            System.out.println(jsonResponse);
            System.out.println("=".repeat(60));
        }
    }
}
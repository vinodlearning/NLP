package com.company.contracts.enhanced;

import java.util.*;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced Chatbot Processor
 * Follows exact JSON structure and business rules:
 * - Contract numbers: 6+ digits minimum
 * - Part numbers: 3+ characters (alphanumeric)
 * - Proper header/entity validation
 * - Dynamic field handling
 */
public class EnhancedChatbotProcessor {
    
    // Business rule patterns
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("\\d{6,}"); // 6+ digits
    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile("[A-Za-z0-9]{3,}"); // 3+ alphanumeric
    private static final Pattern CUSTOMER_NUMBER_PATTERN = Pattern.compile("\\d{4,8}"); // 4-8 digits
    
    // Default display entities
    private static final List<String> DEFAULT_CONTRACT_ENTITIES = Arrays.asList(
        "CONTRACT_NUMBER", "CUSTOMER_NAME"
    );
    private static final List<String> DEFAULT_PARTS_ENTITIES = Arrays.asList(
        "PART_NUMBER", "DESCRIPTION"
    );
    
    public static void main(String[] args) {
        EnhancedChatbotProcessor processor = new EnhancedChatbotProcessor();
        
        System.out.println("=".repeat(80));
        System.out.println("🤖 ENHANCED CHATBOT PROCESSOR - BUSINESS RULES VALIDATION");
        System.out.println("=".repeat(80));
        
        // Test the problematic input
        String testInput = "contract123;parts456";
        System.out.printf("📝 INPUT: \"%s\"\n", testInput);
        System.out.println("=".repeat(50));
        
        // Process and show results
        QueryResponse response = processor.processQuery(testInput);
        
        System.out.println("📊 PROCESSING ANALYSIS:");
        System.out.println("-".repeat(30));
        processor.showProcessingSteps(testInput);
        
        System.out.println("\n🎯 FINAL JSON RESPONSE:");
        System.out.println("=".repeat(50));
        System.out.println(processor.generateJSONResponse(response));
        
        System.out.println("\n✨ FORMATTED JSON RESPONSE:");
        System.out.println("=".repeat(50));
        processor.showFormattedJSON(response);
        
        System.out.println("\n✅ Enhanced processing completed!");
    }
    
    /**
     * Main processing method following the flowchart logic
     */
    public QueryResponse processQuery(String userInput) {
        long startTime = System.nanoTime();
        
        try {
            // Step 1: Check if input has headers (contract/part numbers)
            HeaderAnalysis headerAnalysis = analyzeHeaders(userInput);
            
            // Step 2: Check if input has entities (filters)
            List<EntityFilter> entities = extractEntities(userInput);
            
            // Step 3: Validate business rules
            List<ValidationError> errors = validateInput(headerAnalysis, entities);
            
            // Step 4: Determine query type and action
            QueryMetadata metadata = determineQueryMetadata(headerAnalysis, entities, errors);
            
            // Step 5: Determine display entities
            List<String> displayEntities = determineDisplayEntities(userInput, headerAnalysis, entities, metadata);
            
            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0;
            metadata.setProcessingTimeMs(processingTime);
            
            return new QueryResponse(headerAnalysis.getHeader(), metadata, entities, displayEntities, errors);
            
        } catch (Exception e) {
            long endTime = System.nanoTime();
            double processingTime = (endTime - startTime) / 1_000_000.0;
            
            List<ValidationError> errors = Arrays.asList(
                new ValidationError("PROCESSING_ERROR", "Error processing query: " + e.getMessage(), "BLOCKER")
            );
            
            QueryMetadata metadata = new QueryMetadata("UNKNOWN", "error", processingTime);
            return new QueryResponse(new Header(), metadata, new ArrayList<>(), new ArrayList<>(), errors);
        }
    }
    
    /**
     * Analyze headers (contract/part/customer numbers) in the input
     */
    private HeaderAnalysis analyzeHeaders(String userInput) {
        Header header = new Header();
        List<String> issues = new ArrayList<>();
        
        // Clean and tokenize input
        String cleanInput = userInput.toLowerCase().trim();
        String[] tokens = cleanInput.split("[;\\s,]+");
        
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;
            
            // Check for contract number patterns
            if (token.contains("contract")) {
                String numberPart = extractNumber(token, "contract");
                if (numberPart != null) {
                    if (CONTRACT_NUMBER_PATTERN.matcher(numberPart).matches()) {
                        header.setContractNumber(numberPart);
                    } else {
                        issues.add("Contract number '" + numberPart + "' must be 6+ digits (found: " + numberPart.length() + " digits)");
                    }
                }
            }
            // Check for part number patterns
            else if (token.contains("part")) {
                String numberPart = extractNumber(token, "part");
                if (numberPart != null) {
                    if (PART_NUMBER_PATTERN.matcher(numberPart).matches()) {
                        header.setPartNumber(numberPart);
                    } else {
                        issues.add("Part number '" + numberPart + "' must be 3+ alphanumeric characters (found: " + numberPart.length() + " characters)");
                    }
                }
            }
            // Check for customer number patterns
            else if (token.contains("customer")) {
                String numberPart = extractNumber(token, "customer");
                if (numberPart != null) {
                    if (CUSTOMER_NUMBER_PATTERN.matcher(numberPart).matches()) {
                        header.setCustomerNumber(numberPart);
                    } else {
                        issues.add("Customer number '" + numberPart + "' must be 4-8 digits");
                    }
                }
            }
            // Check for standalone numbers that might be contract/part numbers
            else if (token.matches("\\d+")) {
                // If it's 6+ digits, likely a contract number
                if (token.length() >= 6) {
                    header.setContractNumber(token);
                } else {
                    issues.add("Number '" + token + "' too short for contract number (need 6+ digits)");
                }
            }
            // Check for alphanumeric tokens that might be part numbers
            else if (token.matches("[A-Za-z0-9]+") && token.length() >= 3) {
                // Could be a part number
                header.setPartNumber(token.toUpperCase());
            }
        }
        
        return new HeaderAnalysis(header, issues);
    }
    
    /**
     * Extract number from token like "contract123" -> "123"
     */
    private String extractNumber(String token, String prefix) {
        if (token.startsWith(prefix)) {
            String numberPart = token.substring(prefix.length());
            return numberPart.isEmpty() ? null : numberPart;
        }
        return null;
    }
    
    /**
     * Extract entities (filters) from user input
     */
    private List<EntityFilter> extractEntities(String userInput) {
        List<EntityFilter> entities = new ArrayList<>();
        String lowerInput = userInput.toLowerCase();
        
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
        
        // More filters can be added here
        
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
     * Validate input according to business rules
     */
    private List<ValidationError> validateInput(HeaderAnalysis headerAnalysis, List<EntityFilter> entities) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Add header validation issues
        headerAnalysis.getIssues().forEach(issue -> 
            errors.add(new ValidationError("INVALID_HEADER", issue, "BLOCKER"))
        );
        
        // Check if we have at least one header or entity
        Header header = headerAnalysis.getHeader();
        boolean hasValidHeader = header.getContractNumber() != null || 
                                header.getPartNumber() != null || 
                                header.getCustomerNumber() != null || 
                                header.getCustomerName() != null || 
                                header.getCreatedBy() != null;
        
        if (!hasValidHeader && entities.isEmpty()) {
            errors.add(new ValidationError("MISSING_HEADER", 
                "Provide at least one identifier (contract/part/customer) or filter (date/status)", 
                "BLOCKER"));
        }
        
        return errors;
    }
    
    /**
     * Determine query metadata based on headers and entities
     */
    private QueryMetadata determineQueryMetadata(HeaderAnalysis headerAnalysis, 
                                                List<EntityFilter> entities, 
                                                List<ValidationError> errors) {
        
        if (!errors.isEmpty() && errors.stream().anyMatch(e -> "BLOCKER".equals(e.getSeverity()))) {
            return new QueryMetadata("UNKNOWN", "error", 0);
        }
        
        Header header = headerAnalysis.getHeader();
        
        // Determine query type
        String queryType = "CONTRACTS"; // Default
        if (header.getPartNumber() != null && header.getContractNumber() == null) {
            queryType = "PARTS";
        }
        
        // Determine action type
        String actionType = determineActionType(header, entities);
        
        return new QueryMetadata(queryType, actionType, 0);
    }
    
    private String determineActionType(Header header, List<EntityFilter> entities) {
        // Header-based actions
        if (header.getContractNumber() != null) {
            return "contracts_by_contractNumber";
        }
        if (header.getPartNumber() != null) {
            return "parts_by_partNumber";
        }
        if (header.getCustomerNumber() != null) {
            return "contracts_by_customerNumber";
        }
        if (header.getCustomerName() != null) {
            return "contracts_by_customerName";
        }
        if (header.getCreatedBy() != null) {
            return "contracts_by_createdBy";
        }
        
        // Entity-based actions
        if (!entities.isEmpty()) {
            EntityFilter firstEntity = entities.get(0);
            switch (firstEntity.getAttribute()) {
                case "CREATED_DATE":
                    return "contracts_by_date";
                case "STATUS":
                    return "contracts_by_status";
                default:
                    return "contracts_by_filter";
            }
        }
        
        return "unknown";
    }
    
    /**
     * Determine display entities based on user input and query type
     */
    private List<String> determineDisplayEntities(String userInput, HeaderAnalysis headerAnalysis, 
                                                 List<EntityFilter> entities, QueryMetadata metadata) {
        
        List<String> displayEntities = new ArrayList<>();
        
        // Add defaults based on query type
        if ("CONTRACTS".equals(metadata.getQueryType())) {
            displayEntities.addAll(DEFAULT_CONTRACT_ENTITIES);
        } else if ("PARTS".equals(metadata.getQueryType())) {
            displayEntities.addAll(DEFAULT_PARTS_ENTITIES);
        }
        
        // Auto-add filtered fields
        entities.forEach(entity -> {
            if (!displayEntities.contains(entity.getAttribute())) {
                displayEntities.add(entity.getAttribute());
            }
        });
        
        // Add user-requested fields
        String lowerInput = userInput.toLowerCase();
        if (lowerInput.contains("effective date")) {
            displayEntities.add("EFFECTIVE_DATE");
        }
        if (lowerInput.contains("status")) {
            displayEntities.add("STATUS");
        }
        if (lowerInput.contains("expiration") || lowerInput.contains("expiry")) {
            displayEntities.add("EXPIRATION_DATE");
        }
        if (lowerInput.contains("price") || lowerInput.contains("cost")) {
            displayEntities.add("TOTAL_VALUE");
        }
        
        // Handle "only show" overrides
        if (lowerInput.contains("only show")) {
            return extractExplicitFields(userInput);
        }
        
        return displayEntities;
    }
    
    private List<String> extractExplicitFields(String userInput) {
        List<String> explicitFields = new ArrayList<>();
        String lowerInput = userInput.toLowerCase();
        
        // Extract fields after "only show"
        if (lowerInput.contains("contract")) explicitFields.add("CONTRACT_NUMBER");
        if (lowerInput.contains("customer")) explicitFields.add("CUSTOMER_NAME");
        if (lowerInput.contains("status")) explicitFields.add("STATUS");
        if (lowerInput.contains("date")) explicitFields.add("CREATED_DATE");
        
        return explicitFields.isEmpty() ? DEFAULT_CONTRACT_ENTITIES : explicitFields;
    }
    
    /**
     * Show processing steps for debugging
     */
    public void showProcessingSteps(String input) {
        System.out.printf("1. Original Input: \"%s\"\n", input);
        
        HeaderAnalysis headerAnalysis = analyzeHeaders(input);
        System.out.printf("2. Header Analysis:\n");
        System.out.printf("   - Contract Number: %s\n", headerAnalysis.getHeader().getContractNumber());
        System.out.printf("   - Part Number: %s\n", headerAnalysis.getHeader().getPartNumber());
        System.out.printf("   - Issues: %s\n", headerAnalysis.getIssues());
        
        List<EntityFilter> entities = extractEntities(input);
        System.out.printf("3. Entities: %d found\n", entities.size());
        
        List<ValidationError> errors = validateInput(headerAnalysis, entities);
        System.out.printf("4. Validation Errors: %d found\n", errors.size());
        errors.forEach(error -> System.out.printf("   - %s: %s\n", error.getCode(), error.getMessage()));
        
        QueryMetadata metadata = determineQueryMetadata(headerAnalysis, entities, errors);
        System.out.printf("5. Query Type: %s\n", metadata.getQueryType());
        System.out.printf("6. Action Type: %s\n", metadata.getActionType());
    }
    
    /**
     * Generate JSON response string
     */
    public String generateJSONResponse(QueryResponse response) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        // Header
        json.append("\"header\":{");
        Header header = response.getHeader();
        json.append("\"contractNumber\":").append(quote(header.getContractNumber())).append(",");
        json.append("\"partNumber\":").append(quote(header.getPartNumber())).append(",");
        json.append("\"customerNumber\":").append(quote(header.getCustomerNumber())).append(",");
        json.append("\"customerName\":").append(quote(header.getCustomerName())).append(",");
        json.append("\"createdBy\":").append(quote(header.getCreatedBy()));
        json.append("},");
        
        // Query Metadata
        json.append("\"queryMetadata\":{");
        QueryMetadata metadata = response.getQueryMetadata();
        json.append("\"queryType\":").append(quote(metadata.getQueryType())).append(",");
        json.append("\"actionType\":").append(quote(metadata.getActionType())).append(",");
        json.append("\"processingTimeMs\":").append(metadata.getProcessingTimeMs());
        json.append("},");
        
        // Entities
        json.append("\"entities\":[");
        List<EntityFilter> entities = response.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            EntityFilter entity = entities.get(i);
            json.append("{");
            json.append("\"attribute\":").append(quote(entity.getAttribute())).append(",");
            json.append("\"operation\":").append(quote(entity.getOperation())).append(",");
            json.append("\"value\":").append(quote(entity.getValue())).append(",");
            json.append("\"source\":").append(quote(entity.getSource()));
            json.append("}");
            if (i < entities.size() - 1) json.append(",");
        }
        json.append("],");
        
        // Display Entities
        json.append("\"displayEntities\":[");
        List<String> displayEntities = response.getDisplayEntities();
        for (int i = 0; i < displayEntities.size(); i++) {
            json.append(quote(displayEntities.get(i)));
            if (i < displayEntities.size() - 1) json.append(",");
        }
        json.append("],");
        
        // Errors
        json.append("\"errors\":[");
        List<ValidationError> errors = response.getErrors();
        for (int i = 0; i < errors.size(); i++) {
            ValidationError error = errors.get(i);
            json.append("{");
            json.append("\"code\":").append(quote(error.getCode())).append(",");
            json.append("\"message\":").append(quote(error.getMessage())).append(",");
            json.append("\"severity\":").append(quote(error.getSeverity()));
            json.append("}");
            if (i < errors.size() - 1) json.append(",");
        }
        json.append("]");
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Show formatted JSON response
     */
    public void showFormattedJSON(QueryResponse response) {
        System.out.println("{");
        
        // Header
        System.out.println("  \"header\": {");
        Header header = response.getHeader();
        System.out.printf("    \"contractNumber\": %s,\n", quote(header.getContractNumber()));
        System.out.printf("    \"partNumber\": %s,\n", quote(header.getPartNumber()));
        System.out.printf("    \"customerNumber\": %s,\n", quote(header.getCustomerNumber()));
        System.out.printf("    \"customerName\": %s,\n", quote(header.getCustomerName()));
        System.out.printf("    \"createdBy\": %s\n", quote(header.getCreatedBy()));
        System.out.println("  },");
        
        // Query Metadata
        System.out.println("  \"queryMetadata\": {");
        QueryMetadata metadata = response.getQueryMetadata();
        System.out.printf("    \"queryType\": %s,\n", quote(metadata.getQueryType()));
        System.out.printf("    \"actionType\": %s,\n", quote(metadata.getActionType()));
        System.out.printf("    \"processingTimeMs\": %.3f\n", metadata.getProcessingTimeMs());
        System.out.println("  },");
        
        // Entities
        System.out.println("  \"entities\": [");
        List<EntityFilter> entities = response.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            EntityFilter entity = entities.get(i);
            System.out.println("    {");
            System.out.printf("      \"attribute\": %s,\n", quote(entity.getAttribute()));
            System.out.printf("      \"operation\": %s,\n", quote(entity.getOperation()));
            System.out.printf("      \"value\": %s,\n", quote(entity.getValue()));
            System.out.printf("      \"source\": %s\n", quote(entity.getSource()));
            System.out.printf("    }%s\n", i < entities.size() - 1 ? "," : "");
        }
        System.out.println("  ],");
        
        // Display Entities
        System.out.println("  \"displayEntities\": [");
        List<String> displayEntities = response.getDisplayEntities();
        for (int i = 0; i < displayEntities.size(); i++) {
            System.out.printf("    %s%s\n", quote(displayEntities.get(i)), 
                            i < displayEntities.size() - 1 ? "," : "");
        }
        System.out.println("  ],");
        
        // Errors
        System.out.println("  \"errors\": [");
        List<ValidationError> errors = response.getErrors();
        for (int i = 0; i < errors.size(); i++) {
            ValidationError error = errors.get(i);
            System.out.println("    {");
            System.out.printf("      \"code\": %s,\n", quote(error.getCode()));
            System.out.printf("      \"message\": %s,\n", quote(error.getMessage()));
            System.out.printf("      \"severity\": %s\n", quote(error.getSeverity()));
            System.out.printf("    }%s\n", i < errors.size() - 1 ? "," : "");
        }
        System.out.println("  ]");
        
        System.out.println("}");
    }
    
    private String quote(String value) {
        return value == null ? "null" : "\"" + value + "\"";
    }
    
    // Supporting Classes
    public static class Header {
        private String contractNumber;
        private String partNumber;
        private String customerNumber;
        private String customerName;
        private String createdBy;
        
        // Getters and setters
        public String getContractNumber() { return contractNumber; }
        public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
        public String getPartNumber() { return partNumber; }
        public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
        public String getCustomerNumber() { return customerNumber; }
        public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
    
    public static class HeaderAnalysis {
        private final Header header;
        private final List<String> issues;
        
        public HeaderAnalysis(Header header, List<String> issues) {
            this.header = header;
            this.issues = issues;
        }
        
        public Header getHeader() { return header; }
        public List<String> getIssues() { return issues; }
    }
    
    public static class QueryMetadata {
        private String queryType;
        private String actionType;
        private double processingTimeMs;
        
        public QueryMetadata(String queryType, String actionType, double processingTimeMs) {
            this.queryType = queryType;
            this.actionType = actionType;
            this.processingTimeMs = processingTimeMs;
        }
        
        public String getQueryType() { return queryType; }
        public String getActionType() { return actionType; }
        public double getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(double processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    }
    
    public static class EntityFilter {
        private String attribute;
        private String operation;
        private String value;
        private String source;
        
        public EntityFilter(String attribute, String operation, String value, String source) {
            this.attribute = attribute;
            this.operation = operation;
            this.value = value;
            this.source = source;
        }
        
        public String getAttribute() { return attribute; }
        public String getOperation() { return operation; }
        public String getValue() { return value; }
        public String getSource() { return source; }
    }
    
    public static class ValidationError {
        private String code;
        private String message;
        private String severity;
        
        public ValidationError(String code, String message, String severity) {
            this.code = code;
            this.message = message;
            this.severity = severity;
        }
        
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public String getSeverity() { return severity; }
    }
    
    public static class QueryResponse {
        private Header header;
        private QueryMetadata queryMetadata;
        private List<EntityFilter> entities;
        private List<String> displayEntities;
        private List<ValidationError> errors;
        
        public QueryResponse(Header header, QueryMetadata queryMetadata, 
                           List<EntityFilter> entities, List<String> displayEntities, 
                           List<ValidationError> errors) {
            this.header = header;
            this.queryMetadata = queryMetadata;
            this.entities = entities;
            this.displayEntities = displayEntities;
            this.errors = errors;
        }
        
        public Header getHeader() { return header; }
        public QueryMetadata getQueryMetadata() { return queryMetadata; }
        public List<EntityFilter> getEntities() { return entities; }
        public List<String> getDisplayEntities() { return displayEntities; }
        public List<ValidationError> getErrors() { return errors; }
    }
}
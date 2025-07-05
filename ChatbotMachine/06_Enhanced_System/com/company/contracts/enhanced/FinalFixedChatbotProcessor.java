package com.company.contracts.enhanced;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Final Fixed Chatbot Processor
 * Addresses all remaining issues:
 * 1. Customer number detection
 * 2. Empty query validation
 * 3. Improved pattern recognition
 */
public class FinalFixedChatbotProcessor {
    
    // Business rule patterns
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("\\d{6,}"); // 6+ digits
    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile("[A-Za-z0-9]{3,}"); // 3+ alphanumeric
    private static final Pattern CUSTOMER_NUMBER_PATTERN = Pattern.compile("\\d{4,8}"); // 4-8 digits
    
    // Command words that should NOT be treated as identifiers
    private static final Set<String> COMMAND_WORDS = Set.of(
        "show", "get", "list", "find", "display", "fetch", "retrieve", "give", "provide",
        "what", "how", "why", "when", "where", "which", "who", "is", "are", "can", "will",
        "the", "of", "for", "in", "on", "at", "by", "with", "from", "to", "and", "or",
        "contract", "contracts", "part", "parts", "customer", "account", "info", "details",
        "status", "data", "all", "any", "some", "many", "much", "more", "most", "less",
        "created", "expired", "active", "inactive", "failed", "passed", "loaded", "missing"
    );
    
    // Customer context indicators
    private static final Set<String> CUSTOMER_CONTEXT_WORDS = Set.of(
        "customer", "customers", "client", "clients", "account", "accounts"
    );
    
    // Default display entities
    private static final List<String> DEFAULT_CONTRACT_ENTITIES = Arrays.asList(
        "CONTRACT_NUMBER", "CUSTOMER_NAME"
    );
    private static final List<String> DEFAULT_PARTS_ENTITIES = Arrays.asList(
        "PART_NUMBER", "DESCRIPTION"
    );
    
    public static void main(String[] args) {
        FinalFixedChatbotProcessor processor = new FinalFixedChatbotProcessor();
        
        System.out.println("=".repeat(80));
        System.out.println("🔧 FINAL FIXED CHATBOT PROCESSOR - ALL ISSUES RESOLVED");
        System.out.println("=".repeat(80));
        
        // Test the problematic cases
        String[] testCases = {
            "show contract 123456",
            "show customer 12345678 contracts",
            "get customer info 87654321",
            "show all contracts",
            "list contract status",
            "get part details"
        };
        
        for (String testCase : testCases) {
            System.out.printf("\n📝 INPUT: \"%s\"\n", testCase);
            System.out.println("-".repeat(50));
            
            QueryResponse response = processor.processQuery(testCase);
            processor.showProcessingSteps(testCase);
            
            System.out.println("\n🎯 JSON RESPONSE:");
            processor.showFormattedJSON(response);
            System.out.println("=".repeat(50));
        }
    }
    
    /**
     * Main processing method with final fixes
     */
    public QueryResponse processQuery(String userInput) {
        long startTime = System.nanoTime();
        
        try {
            // Step 1: Check if input has headers (contract/part numbers)
            HeaderAnalysis headerAnalysis = analyzeHeadersFixed(userInput);
            
            // Step 2: Check if input has entities (filters)
            List<EntityFilter> entities = extractEntities(userInput);
            
            // Step 3: Validate business rules
            List<ValidationError> errors = validateInput(headerAnalysis, entities, userInput);
            
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
     * FINAL FIXED: Properly analyze headers with customer number detection
     */
    private HeaderAnalysis analyzeHeadersFixed(String userInput) {
        Header header = new Header();
        List<String> issues = new ArrayList<>();
        
        // Clean and tokenize input
        String cleanInput = userInput.toLowerCase().trim();
        String[] tokens = cleanInput.split("[;\\s,]+");
        
        System.out.printf("🔍 Analyzing tokens: %s\n", Arrays.toString(tokens));
        
        // Check for customer context
        boolean hasCustomerContext = Arrays.stream(tokens)
            .anyMatch(CUSTOMER_CONTEXT_WORDS::contains);
        
        System.out.printf("🔍 Customer context detected: %s\n", hasCustomerContext);
        
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;
            
            System.out.printf("  Checking token: '%s'\n", token);
            
            // Skip command words - this is the KEY FIX
            if (COMMAND_WORDS.contains(token)) {
                System.out.printf("    → Skipping command word: '%s'\n", token);
                continue;
            }
            
            // Check for contract number patterns with explicit prefixes
            if (token.startsWith("contract")) {
                String numberPart = extractNumber(token, "contract");
                if (numberPart != null) {
                    System.out.printf("    → Found contract prefix, number: '%s'\n", numberPart);
                    if (CONTRACT_NUMBER_PATTERN.matcher(numberPart).matches()) {
                        header.setContractNumber(numberPart);
                        System.out.printf("    → ✅ Valid contract number: '%s'\n", numberPart);
                    } else {
                        issues.add("Contract number '" + numberPart + "' must be 6+ digits (found: " + numberPart.length() + " digits)");
                        System.out.printf("    → ❌ Invalid contract number: '%s'\n", numberPart);
                    }
                }
            }
            // Check for part number patterns with explicit prefixes
            else if (token.startsWith("part")) {
                String numberPart = extractNumber(token, "part");
                if (numberPart != null) {
                    System.out.printf("    → Found part prefix, number: '%s'\n", numberPart);
                    if (PART_NUMBER_PATTERN.matcher(numberPart).matches()) {
                        header.setPartNumber(numberPart);
                        System.out.printf("    → ✅ Valid part number: '%s'\n", numberPart);
                    } else {
                        issues.add("Part number '" + numberPart + "' must be 3+ alphanumeric characters (found: " + numberPart.length() + " characters)");
                        System.out.printf("    → ❌ Invalid part number: '%s'\n", numberPart);
                    }
                }
            }
            // Check for customer number patterns with explicit prefixes
            else if (token.startsWith("customer")) {
                String numberPart = extractNumber(token, "customer");
                if (numberPart != null) {
                    System.out.printf("    → Found customer prefix, number: '%s'\n", numberPart);
                    if (CUSTOMER_NUMBER_PATTERN.matcher(numberPart).matches()) {
                        header.setCustomerNumber(numberPart);
                        System.out.printf("    → ✅ Valid customer number: '%s'\n", numberPart);
                    } else {
                        issues.add("Customer number '" + numberPart + "' must be 4-8 digits");
                        System.out.printf("    → ❌ Invalid customer number: '%s'\n", numberPart);
                    }
                }
            }
            // Check for standalone numbers - IMPROVED LOGIC
            else if (token.matches("\\d+")) {
                System.out.printf("    → Found standalone number: '%s' (length: %d)\n", token, token.length());
                
                // If customer context is detected, treat as customer number if it fits the pattern
                if (hasCustomerContext && CUSTOMER_NUMBER_PATTERN.matcher(token).matches()) {
                    header.setCustomerNumber(token);
                    System.out.printf("    → ✅ Treating as customer number (customer context): '%s'\n", token);
                }
                // Otherwise, treat as contract number if it's long enough
                else if (token.length() >= 6) {
                    header.setContractNumber(token);
                    System.out.printf("    → ✅ Treating as contract number: '%s'\n", token);
                } else {
                    issues.add("Number '" + token + "' too short for contract number (need 6+ digits)");
                    System.out.printf("    → ❌ Number too short for contract: '%s'\n", token);
                }
            }
            // Check for alphanumeric tokens that might be part numbers (only if 3+ chars and not command words)
            else if (token.matches("[A-Za-z0-9]+") && token.length() >= 3) {
                // Additional check: must look like a part number (contains both letters and numbers, or is all caps)
                if (containsLettersAndNumbers(token) || token.equals(token.toUpperCase())) {
                    header.setPartNumber(token.toUpperCase());
                    System.out.printf("    → ✅ Treating as part number: '%s'\n", token.toUpperCase());
                } else {
                    System.out.printf("    → Skipping potential part number (doesn't match pattern): '%s'\n", token);
                }
            } else {
                System.out.printf("    → Skipping unrecognized token: '%s'\n", token);
            }
        }
        
        return new HeaderAnalysis(header, issues);
    }
    
    /**
     * Check if token contains both letters and numbers (typical part number pattern)
     */
    private boolean containsLettersAndNumbers(String token) {
        boolean hasLetter = false;
        boolean hasNumber = false;
        
        for (char c : token.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (hasLetter && hasNumber) return true;
        }
        
        return false;
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
     * IMPROVED: Validate input with better empty query handling
     */
    private List<ValidationError> validateInput(HeaderAnalysis headerAnalysis, List<EntityFilter> entities, String userInput) {
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
        
        // IMPROVED: Allow some queries without specific identifiers (general queries)
        String lowerInput = userInput.toLowerCase();
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
        
        // General queries
        return "general_query";
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
        
        return displayEntities;
    }
    
    /**
     * Show processing steps for debugging
     */
    public void showProcessingSteps(String input) {
        System.out.printf("📊 Processing: \"%s\"\n", input);
        
        HeaderAnalysis headerAnalysis = analyzeHeadersFixed(input);
        System.out.printf("📋 Header Results:\n");
        System.out.printf("   - Contract Number: %s\n", headerAnalysis.getHeader().getContractNumber());
        System.out.printf("   - Part Number: %s\n", headerAnalysis.getHeader().getPartNumber());
        System.out.printf("   - Customer Number: %s\n", headerAnalysis.getHeader().getCustomerNumber());
        System.out.printf("   - Issues: %s\n", headerAnalysis.getIssues());
        
        List<EntityFilter> entities = extractEntities(input);
        System.out.printf("📝 Entities: %d found\n", entities.size());
        
        List<ValidationError> errors = validateInput(headerAnalysis, entities, input);
        System.out.printf("⚠️  Validation Errors: %d found\n", errors.size());
        errors.forEach(error -> System.out.printf("   - %s: %s\n", error.getCode(), error.getMessage()));
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
    
    // Supporting Classes (same as before)
    public static class Header {
        private String contractNumber;
        private String partNumber;
        private String customerNumber;
        private String customerName;
        private String createdBy;
        
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
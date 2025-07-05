package model.nlp;

import java.util.*;

/**
 * Complete ML Response for Oracle ADF Integration
 * Contains ALL required attributes and entity operations as specified
 * 
 * Required Attributes (Always Present):
 * - contract_number
 * - part_number 
 * - customer_name
 * - account_number
 * - created_by
 * - queryType (CONTRACT/PARTS/HELP)
 * - actionType (specific action types)
 * - entities (with operations)
 * 
 * @author Contract Query Processing System
 * @version 2.0 - Complete Implementation
 */
public class CompleteMLResponse {
    
    // REQUIRED ATTRIBUTES (Always present in JSON)
    private String contract_number;
    private String part_number;
    private String customer_name;
    private String account_number;
    private String created_by;
    
    // REQUIRED QUERY INFORMATION
    private String queryType;    // CONTRACT/PARTS/HELP
    private String actionType;   // Specific action type
    
    // REQUIRED ENTITIES WITH OPERATIONS
    private List<EntityOperation> entities;
    
    // Additional response metadata
    private String route;
    private String reason;
    private String intentType;
    private String originalInput;
    private String correctedInput;
    private boolean hasSpellCorrections;
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private String businessRuleViolation;
    private String enhancementApplied;
    private double contextScore;
    private double processingTime;
    private String timestamp;
    
    /**
     * Constructor - Automatically extracts all required attributes
     */
    public CompleteMLResponse(String route, String reason, String intentType,
                             String originalInput, String correctedInput, boolean hasSpellCorrections,
                             String contractId, Set<String> partsKeywords, Set<String> createKeywords,
                             String businessRuleViolation, String enhancementApplied,
                             double contextScore, double processingTime) {
        
        // Initialize ALL required attributes to null (will be populated)
        this.contract_number = null;
        this.part_number = null;
        this.customer_name = null;
        this.account_number = null;
        this.created_by = null;
        
        // Set basic response data
        this.route = route;
        this.reason = reason;
        this.intentType = intentType;
        this.originalInput = originalInput;
        this.correctedInput = correctedInput;
        this.hasSpellCorrections = hasSpellCorrections;
        this.partsKeywords = partsKeywords != null ? partsKeywords : new HashSet<>();
        this.createKeywords = createKeywords != null ? createKeywords : new HashSet<>();
        this.businessRuleViolation = businessRuleViolation != null ? businessRuleViolation : "N/A";
        this.enhancementApplied = enhancementApplied != null ? enhancementApplied : "N/A";
        this.contextScore = contextScore;
        this.processingTime = processingTime;
        this.timestamp = new Date().toString();
        
        // Determine queryType and actionType
        this.queryType = determineQueryType(route);
        this.actionType = determineActionType(route, correctedInput);
        
        // Extract entities with operations
        this.entities = new ArrayList<>();
        extractAllAttributesAndEntities(correctedInput, contractId);
    }
    
    /**
     * Determine QueryType from route
     */
    private String determineQueryType(String route) {
        if (route == null) return "CONTRACT";
        
        switch (route.toUpperCase()) {
            case "PARTS":
            case "PARTS_CREATE_ERROR":
                return "PARTS";
            case "HELP":
                return "HELP";
            case "CONTRACT":
            default:
                return "CONTRACT";
        }
    }
    
    /**
     * Determine specific ActionType based on analysis
     */
    private String determineActionType(String route, String input) {
        if (input == null) return "unknown";
        
        String lowerInput = input.toLowerCase();
        
        // For PARTS queries
        if ("PARTS".equals(queryType)) {
            if (lowerInput.contains("created by") || lowerInput.matches(".*\\bby\\s+\\w+.*")) {
                return "parts_by_user";
            } else if (lowerInput.contains("contract") || contract_number != null) {
                return "parts_by_contract";
            } else if (lowerInput.matches(".*\\b[a-z]{2}\\d+.*")) {
                return "parts_by_partNumber";
            } else if (lowerInput.contains("customer") || lowerInput.contains("client")) {
                return "parts_by_customer";
            }
            return "parts_by_contract";
        }
        
        // For CONTRACT queries
        if ("CONTRACT".equals(queryType)) {
            if (lowerInput.contains("created by") || lowerInput.matches(".*\\bby\\s+\\w+.*")) {
                return "contracts_by_user";
            } else if (customer_name != null || lowerInput.contains("customer") || 
                      lowerInput.contains("client") || lowerInput.contains("account name")) {
                return "contracts_by_customerName";
            } else if (contract_number != null) {
                return "contracts_by_contractNumber";
            } else if (lowerInput.contains("account") && account_number != null) {
                return "contracts_by_accountNumber";
            } else if (lowerInput.contains("part") || lowerInput.contains("component")) {
                return "contracts_by_parts";
            }
            return "contracts_by_contractNumber";
        }
        
        // For HELP queries
        return "help_contract_creation";
    }
    
    /**
     * Extract ALL attributes and entities with operations from input
     */
    private void extractAllAttributesAndEntities(String input, String contractId) {
        if (input == null) return;
        
        String lowerInput = input.toLowerCase();
        
        // 1. CONTRACT_NUMBER
        if (contractId != null) {
            this.contract_number = contractId;
            entities.add(new EntityOperation("contract_number", "=", contractId));
        }
        
        // 2. PART_NUMBER
        String partNumber = extractPartNumber(input);
        if (partNumber != null) {
            this.part_number = partNumber;
            entities.add(new EntityOperation("part_number", "=", partNumber));
        }
        
        // 3. CUSTOMER_NAME
        String customerName = extractCustomerName(lowerInput);
        if (customerName != null) {
            this.customer_name = customerName;
            entities.add(new EntityOperation("customer_name", "=", customerName));
        }
        
        // 4. ACCOUNT_NUMBER
        String accountNumber = extractAccountNumber(lowerInput);
        if (accountNumber != null) {
            this.account_number = accountNumber;
            entities.add(new EntityOperation("account_number", "=", accountNumber));
        }
        
        // 5. CREATED_BY
        String createdBy = extractCreatedBy(lowerInput);
        if (createdBy != null) {
            this.created_by = createdBy;
            entities.add(new EntityOperation("created_by", "=", createdBy));
        }
        
        // 6. DATE OPERATIONS
        extractDateOperations(lowerInput);
        
        // 7. STATUS OPERATIONS
        extractStatusOperations(lowerInput);
        
        // 8. PROJECT TYPE OPERATIONS (Example: project type = "new")
        extractProjectTypeOperations(lowerInput);
        
        // 9. OTHER OPERATIONS
        extractOtherOperations(lowerInput);
    }
    
    /**
     * Extract part number (format: AE12345, XY123, etc.)
     */
    private String extractPartNumber(String input) {
        // Pattern 1: Standard format AE12345
        if (input.matches(".*\\b[A-Z]{2}\\d{3,6}\\b.*")) {
            return input.replaceAll(".*\\b([A-Z]{2}\\d{3,6})\\b.*", "$1");
        }
        
        // Pattern 2: Case insensitive ae125, AE125
        if (input.toLowerCase().matches(".*\\b[a-z]{2}\\d{3,6}\\b.*")) {
            String part = input.toLowerCase().replaceAll(".*\\b([a-z]{2}\\d{3,6})\\b.*", "$1");
            return part.toUpperCase();
        }
        
        return null;
    }
    
    /**
     * Extract customer name
     */
    private String extractCustomerName(String lowerInput) {
        // Pattern 1: "customer ABC Corp"
        if (lowerInput.contains("customer ")) {
            String[] parts = lowerInput.split("customer\\s+");
            if (parts.length > 1) {
                String[] words = parts[1].split("\\s+");
                StringBuilder customer = new StringBuilder();
                for (int i = 0; i < Math.min(3, words.length); i++) {
                    if (words[i].matches("[a-zA-Z]+")) {
                        customer.append(words[i]).append(" ");
                    } else {
                        break;
                    }
                }
                String result = customer.toString().trim();
                return result.isEmpty() ? null : result;
            }
        }
        
        // Pattern 2: "account name 'Siemens'" or "account name Siemens"
        if (lowerInput.contains("account name")) {
            String[] parts = lowerInput.split("account name\\s+");
            if (parts.length > 1) {
                String namesPart = parts[1];
                // Remove quotes if present
                namesPart = namesPart.replaceAll("['\"]", "");
                String[] words = namesPart.split("\\s+");
                if (words.length > 0 && words[0].matches("[a-zA-Z]+")) {
                    return words[0];
                }
            }
        }
        
        // Pattern 3: Look for known company names
        String[] companyNames = {"siemens", "microsoft", "google", "apple", "amazon", "oracle", "ibm"};
        for (String company : companyNames) {
            if (lowerInput.contains(company)) {
                return company;
            }
        }
        
        return null;
    }
    
    /**
     * Extract account number
     */
    private String extractAccountNumber(String lowerInput) {
        if (lowerInput.contains("account ")) {
            String[] parts = lowerInput.split("account\\s+");
            if (parts.length > 1) {
                String[] words = parts[1].split("\\s+");
                for (String word : words) {
                    if (word.matches("\\d{6,12}")) {
                        return word;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Extract created by user
     */
    private String extractCreatedBy(String lowerInput) {
        if (lowerInput.contains("created by ")) {
            String[] parts = lowerInput.split("created by\\s+");
            if (parts.length > 1) {
                String user = parts[1].split("\\s+")[0];
                return user.replaceAll("[^a-zA-Z0-9]", "");
            }
        } else if (lowerInput.matches(".*\\bby\\s+\\w+.*")) {
            String[] parts = lowerInput.split("\\bby\\s+");
            if (parts.length > 1) {
                String user = parts[1].split("\\s+")[0];
                return user.replaceAll("[^a-zA-Z0-9]", "");
            }
        }
        return null;
    }
    
    /**
     * Extract date operations (after, before, between)
     */
    private void extractDateOperations(String lowerInput) {
        // After date
        if (lowerInput.contains("after ")) {
            String[] parts = lowerInput.split("after\\s+");
            if (parts.length > 1) {
                String year = extractYear(parts[1]);
                if (year != null) {
                    entities.add(new EntityOperation("created_date", ">", year + "-01-01"));
                }
            }
        }
        
        // Before date
        if (lowerInput.contains("before ")) {
            String[] parts = lowerInput.split("before\\s+");
            if (parts.length > 1) {
                String year = extractYear(parts[1]);
                if (year != null) {
                    entities.add(new EntityOperation("created_date", "<", year + "-12-31"));
                }
            }
        }
        
        // Between dates
        if (lowerInput.contains("between ")) {
            String[] parts = lowerInput.split("between\\s+");
            if (parts.length > 1) {
                String[] years = parts[1].split("\\s+and\\s+");
                if (years.length == 2) {
                    String startYear = extractYear(years[0]);
                    String endYear = extractYear(years[1]);
                    if (startYear != null && endYear != null) {
                        entities.add(new EntityOperation("created_date", "BETWEEN", startYear + " AND " + endYear));
                    }
                }
            }
        }
    }
    
    /**
     * Extract status operations
     */
    private void extractStatusOperations(String lowerInput) {
        String[] statusValues = {"active", "expired", "pending", "cancelled", "new", "draft"};
        for (String status : statusValues) {
            if (lowerInput.contains("status " + status) || 
                (lowerInput.contains(status) && lowerInput.contains("status")) ||
                (status.equals("expired") && lowerInput.contains("expired"))) {
                entities.add(new EntityOperation("status", "=", status));
                break;
            }
        }
        
        // Additional status patterns
        if (lowerInput.contains("not loaded") || lowerInput.contains("not loadded")) {
            entities.add(new EntityOperation("load_status", "=", "not_loaded"));
        } else if (lowerInput.contains("loaded") || lowerInput.contains("loadded")) {
            entities.add(new EntityOperation("load_status", "=", "loaded"));
        }
        
        if (lowerInput.contains("during loading") || lowerInput.contains("during loadding")) {
            entities.add(new EntityOperation("process_status", "=", "loading"));
        }
    }
    
    /**
     * Extract project type operations (example: project type = "new")
     */
    private void extractProjectTypeOperations(String lowerInput) {
        if (lowerInput.contains("project type")) {
            String[] projectTypes = {"new", "existing", "upgrade", "maintenance"};
            for (String type : projectTypes) {
                if (lowerInput.contains(type)) {
                    entities.add(new EntityOperation("project_type", "=", type));
                    break;
                }
            }
        }
    }
    
    /**
     * Extract other operations (priority, category, etc.)
     */
    private void extractOtherOperations(String lowerInput) {
        // Priority operations
        if (lowerInput.contains("priority")) {
            String[] priorities = {"high", "medium", "low", "critical"};
            for (String priority : priorities) {
                if (lowerInput.contains(priority)) {
                    entities.add(new EntityOperation("priority", "=", priority));
                    break;
                }
            }
        }
        
        // Category operations
        if (lowerInput.contains("category")) {
            String[] categories = {"hardware", "software", "service", "maintenance"};
            for (String category : categories) {
                if (lowerInput.contains(category)) {
                    entities.add(new EntityOperation("category", "=", category));
                    break;
                }
            }
        }
    }
    
    /**
     * Extract year from text
     */
    private String extractYear(String text) {
        if (text.matches(".*\\b(19|20)\\d{2}\\b.*")) {
            return text.replaceAll(".*\\b((19|20)\\d{2})\\b.*", "$1");
        }
        return null;
    }
    
    // GETTERS for all required attributes
    public String getContract_number() { return contract_number; }
    public String getPart_number() { return part_number; }
    public String getCustomer_name() { return customer_name; }
    public String getAccount_number() { return account_number; }
    public String getCreated_by() { return created_by; }
    public String getQueryType() { return queryType; }
    public String getActionType() { return actionType; }
    public List<EntityOperation> getEntities() { return entities; }
    
    // GETTERS for other fields
    public String getRoute() { return route; }
    public String getReason() { return reason; }
    public String getIntentType() { return intentType; }
    public String getOriginalInput() { return originalInput; }
    public String getCorrectedInput() { return correctedInput; }
    public boolean isHasSpellCorrections() { return hasSpellCorrections; }
    public Set<String> getPartsKeywords() { return partsKeywords; }
    public Set<String> getCreateKeywords() { return createKeywords; }
    public String getBusinessRuleViolation() { return businessRuleViolation; }
    public String getEnhancementApplied() { return enhancementApplied; }
    public double getContextScore() { return contextScore; }
    public double getProcessingTime() { return processingTime; }
    public String getTimestamp() { return timestamp; }
    
    /**
     * Convert to complete JSON with ALL required attributes
     */
    public String toCompleteJSON() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // REQUIRED ATTRIBUTES (Always present)
        json.append("  \"contract_number\": ").append(contract_number != null ? "\"" + contract_number + "\"" : "null").append(",\n");
        json.append("  \"part_number\": ").append(part_number != null ? "\"" + part_number + "\"" : "null").append(",\n");
        json.append("  \"customer_name\": ").append(customer_name != null ? "\"" + customer_name + "\"" : "null").append(",\n");
        json.append("  \"account_number\": ").append(account_number != null ? "\"" + account_number + "\"" : "null").append(",\n");
        json.append("  \"created_by\": ").append(created_by != null ? "\"" + created_by + "\"" : "null").append(",\n");
        
        // QUERY INFORMATION
        json.append("  \"queryType\": \"").append(queryType).append("\",\n");
        json.append("  \"actionType\": \"").append(actionType).append("\",\n");
        
        // ENTITIES WITH OPERATIONS
        json.append("  \"entities\": [\n");
        for (int i = 0; i < entities.size(); i++) {
            EntityOperation entity = entities.get(i);
            json.append("    {\n");
            json.append("      \"attribute\": \"").append(entity.getAttribute()).append("\",\n");
            json.append("      \"operation\": \"").append(entity.getOperation()).append("\",\n");
            json.append("      \"value\": \"").append(entity.getValue()).append("\"\n");
            json.append("    }");
            if (i < entities.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");
        
        // ADDITIONAL METADATA
        json.append("  \"route\": \"").append(route).append("\",\n");
        json.append("  \"reason\": \"").append(reason).append("\",\n");
        json.append("  \"intentType\": \"").append(intentType).append("\",\n");
        json.append("  \"originalInput\": \"").append(originalInput).append("\",\n");
        json.append("  \"correctedInput\": \"").append(correctedInput).append("\",\n");
        json.append("  \"hasSpellCorrections\": ").append(hasSpellCorrections).append(",\n");
        json.append("  \"processingTime\": ").append(processingTime).append(",\n");
        json.append("  \"contextScore\": ").append(contextScore).append(",\n");
        json.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        json.append("  \"partsKeywords\": ").append(partsKeywords).append(",\n");
        json.append("  \"createKeywords\": ").append(createKeywords).append(",\n");
        json.append("  \"businessRuleViolation\": \"").append(businessRuleViolation).append("\",\n");
        json.append("  \"enhancementApplied\": \"").append(enhancementApplied).append("\"\n");
        json.append("}");
        
        return json.toString();
    }
    
    @Override
    public String toString() {
        return toCompleteJSON();
    }
}


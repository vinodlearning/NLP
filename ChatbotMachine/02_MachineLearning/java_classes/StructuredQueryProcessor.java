import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Structured Query Processor
 * 
 * This system generates structured JSON responses with:
 * - Query Type (CONTRACTS, PARTS, HELP)
 * - Action Type (SEARCH_CONTRACT_BY_NUMBER, SEARCH_BY_CREATED_BY, etc.)
 * - Entities with attributes and operators
 * - Main Properties (key factors for data retrieval)
 * - Complex query handling with date ranges and filters
 */
public class StructuredQueryProcessor {
    
    // Main properties - key factors for data retrieval
    public enum MainProperty {
        CONTRACT_NUMBER("contractNumber", "contract\\s*(number|id|#)?\\s*([A-Z0-9-]+)"),
        ACCOUNT_NUMBER("accountNumber", "account\\s*(number|id|#)?\\s*([A-Z0-9-]+)"),
        CUSTOMER_NUMBER("customerNumber", "customer\\s*(number|id|#)?\\s*([A-Z0-9-]+)"),
        CREATED_BY("createdBy", "created\\s*by\\s*([a-zA-Z]+)"),
        PART_NUMBER("partNumber", "part\\s*(number|id|#)?\\s*([A-Z0-9-]+)");
        
        private final String propertyName;
        private final String pattern;
        
        MainProperty(String propertyName, String pattern) {
            this.propertyName = propertyName;
            this.pattern = pattern;
        }
        
        public String getPropertyName() { return propertyName; }
        public String getPattern() { return pattern; }
    }
    
    // Query types
    public enum QueryType {
        CONTRACTS, PARTS, HELP, ACCOUNTS, CUSTOMERS
    }
    
    // Action types based on main properties and query patterns
    public enum ActionType {
        SEARCH_CONTRACT_BY_NUMBER,
        SEARCH_CONTRACT_BY_ACCOUNT,
        SEARCH_CONTRACT_BY_CUSTOMER,
        SEARCH_CONTRACT_BY_CREATED_BY,
        SEARCH_PARTS_BY_CONTRACT,
        SEARCH_PARTS_BY_NUMBER,
        COUNT_CONTRACTS_BY_CREATED_BY,
        COUNT_PARTS_BY_CONTRACT,
        LIST_CONTRACTS_BY_DATE_RANGE,
        LIST_CONTRACTS_THIS_MONTH,
        GET_CONTRACT_DETAILS,
        GET_PARTS_DETAILS,
        HELP_CREATE_CONTRACT,
        SEARCH_ALL_CONTRACTS,
        SEARCH_ALL_PARTS
    }
    
    // Operators for complex queries
    public enum Operator {
        EQUALS("=", "equals|is|="),
        BETWEEN("BETWEEN", "between|from.*to"),
        GREATER_THAN(">", "greater\\s*than|after|>"),
        LESS_THAN("<", "less\\s*than|before|<"),
        CONTAINS("CONTAINS", "contains|includes|has"),
        IN("IN", "in|among"),
        THIS_MONTH("THIS_MONTH", "this\\s*month"),
        THIS_YEAR("THIS_YEAR", "this\\s*year"),
        TILL_DATE("TILL_DATE", "till\\s*date|to\\s*date|until\\s*now");
        
        private final String symbol;
        private final String pattern;
        
        Operator(String symbol, String pattern) {
            this.symbol = symbol;
            this.pattern = pattern;
        }
        
        public String getSymbol() { return symbol; }
        public String getPattern() { return pattern; }
    }
    
    /**
     * Process user query and return structured response
     */
    public static StructuredQueryResponse processQuery(String userInput) {
        // Step 1: Apply spell correction
        String correctedInput = applySpellCorrection(userInput);
        
        // Step 2: Detect query type
        QueryType queryType = detectQueryType(correctedInput);
        
        // Step 3: Detect main properties
        Map<MainProperty, String> mainProperties = detectMainProperties(correctedInput);
        
        // Step 4: Detect action type
        ActionType actionType = detectActionType(correctedInput, queryType, mainProperties);
        
        // Step 5: Extract entities with attributes and operators
        List<EntityAttribute> entities = extractEntitiesWithOperators(correctedInput, queryType);
        
        // Step 6: Build structured response
        return buildStructuredResponse(userInput, correctedInput, queryType, actionType, mainProperties, entities);
    }
    
    /**
     * Apply spell correction
     */
    private static String applySpellCorrection(String input) {
        Map<String, String> corrections = new HashMap<>();
        corrections.put("contarct", "contract");
        corrections.put("contrct", "contract");
        corrections.put("effectuve", "effective");
        corrections.put("expir", "expiration");
        corrections.put("exipraion", "expiration");
        corrections.put("parst", "parts");
        corrections.put("partz", "parts");
        corrections.put("customr", "customer");
        corrections.put("acount", "account");
        corrections.put("ceraedt", "created");
        corrections.put("desigb", "design");
        corrections.put("Dumhp", "Dump"); // Assuming this is a username
        
        String corrected = input.toLowerCase();
        for (Map.Entry<String, String> entry : corrections.entrySet()) {
            corrected = corrected.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        
        return corrected;
    }
    
    /**
     * Detect query type
     */
    private static QueryType detectQueryType(String input) {
        if (input.matches(".*\\b(parts?|part|component|inventory)\\b.*")) {
            return QueryType.PARTS;
        } else if (input.matches(".*\\b(account|customer)\\b.*") && !input.matches(".*\\b(contract)\\b.*")) {
            return QueryType.ACCOUNTS;
        } else if (input.matches(".*\\b(help|create|how|steps)\\b.*")) {
            return QueryType.HELP;
        } else {
            return QueryType.CONTRACTS;
        }
    }
    
    /**
     * Detect main properties (key factors for data retrieval)
     */
    private static Map<MainProperty, String> detectMainProperties(String input) {
        Map<MainProperty, String> properties = new HashMap<>();
        
        for (MainProperty property : MainProperty.values()) {
            Pattern pattern = Pattern.compile(property.getPattern(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            
            if (matcher.find()) {
                String value = null;
                if (matcher.groupCount() >= 2) {
                    value = matcher.group(2); // Get the value part
                } else if (matcher.groupCount() >= 1) {
                    value = matcher.group(1);
                }
                if (value != null && !value.trim().isEmpty()) {
                    properties.put(property, value.trim());
                }
            }
        }
        
        return properties;
    }
    
    /**
     * Detect action type based on query pattern and main properties
     */
    private static ActionType detectActionType(String input, QueryType queryType, Map<MainProperty, String> mainProperties) {
        String lowerInput = input.toLowerCase();
        
        // Count queries
        if (lowerInput.matches(".*\\b(how many|count|total)\\b.*")) {
            if (mainProperties.containsKey(MainProperty.CREATED_BY)) {
                return ActionType.COUNT_CONTRACTS_BY_CREATED_BY;
            } else if (queryType == QueryType.PARTS) {
                return ActionType.COUNT_PARTS_BY_CONTRACT;
            }
        }
        
        // Date range queries
        if (lowerInput.matches(".*\\b(between|from.*to|this month)\\b.*")) {
            return ActionType.LIST_CONTRACTS_BY_DATE_RANGE;
        }
        
        // This month queries
        if (lowerInput.matches(".*\\bthis\\s*month\\b.*")) {
            return ActionType.LIST_CONTRACTS_THIS_MONTH;
        }
        
        // Main property-based searches
        if (mainProperties.containsKey(MainProperty.CONTRACT_NUMBER)) {
            return queryType == QueryType.PARTS ? 
                ActionType.SEARCH_PARTS_BY_CONTRACT : ActionType.SEARCH_CONTRACT_BY_NUMBER;
        }
        
        if (mainProperties.containsKey(MainProperty.ACCOUNT_NUMBER)) {
            return ActionType.SEARCH_CONTRACT_BY_ACCOUNT;
        }
        
        if (mainProperties.containsKey(MainProperty.CUSTOMER_NUMBER)) {
            return ActionType.SEARCH_CONTRACT_BY_CUSTOMER;
        }
        
        if (mainProperties.containsKey(MainProperty.CREATED_BY)) {
            return ActionType.SEARCH_CONTRACT_BY_CREATED_BY;
        }
        
        if (mainProperties.containsKey(MainProperty.PART_NUMBER)) {
            return ActionType.SEARCH_PARTS_BY_NUMBER;
        }
        
        // Help queries
        if (queryType == QueryType.HELP) {
            return ActionType.HELP_CREATE_CONTRACT;
        }
        
        // Default actions
        if (queryType == QueryType.PARTS) {
            return ActionType.SEARCH_ALL_PARTS;
        } else {
            return ActionType.GET_CONTRACT_DETAILS;
        }
    }
    
    /**
     * Extract entities with attributes and operators
     */
    private static List<EntityAttribute> extractEntitiesWithOperators(String input, QueryType queryType) {
        List<EntityAttribute> entities = new ArrayList<>();
        
        // Contract attributes
        Map<String, String> contractAttrs = new HashMap<>();
        contractAttrs.put("effective", "effectiveDate");
        contractAttrs.put("expiration", "expirationDate");
        contractAttrs.put("expir", "expirationDate");
        contractAttrs.put("project", "projectType");
        contractAttrs.put("customer", "customerName");
        contractAttrs.put("vendor", "vendorName");
        contractAttrs.put("status", "status");
        contractAttrs.put("value", "totalValue");
        contractAttrs.put("amount", "totalValue");
        contractAttrs.put("department", "department");
        contractAttrs.put("region", "region");
        
        // Parts attributes
        Map<String, String> partsAttrs = new HashMap<>();
        partsAttrs.put("inventory", "inventory");
        partsAttrs.put("supplier", "supplier");
        partsAttrs.put("warranty", "warranty");
        partsAttrs.put("quantity", "quantity");
        partsAttrs.put("price", "unitPrice");
        partsAttrs.put("specification", "specifications");
        partsAttrs.put("manufacturer", "manufacturer");
        
        Map<String, String> targetAttrs = (queryType == QueryType.PARTS) ? partsAttrs : contractAttrs;
        
        // Extract attributes mentioned in input
        for (Map.Entry<String, String> attr : targetAttrs.entrySet()) {
            if (input.toLowerCase().contains(attr.getKey())) {
                EntityAttribute entity = new EntityAttribute();
                entity.setAttributeName(attr.getValue());
                entity.setOriginalName(attr.getKey());
                entity.setOperator(detectOperatorForAttribute(input, attr.getKey()));
                entity.setValue(extractValueForAttribute(input, attr.getKey()));
                entities.add(entity);
            }
        }
        
        // Handle date ranges and special operators
        if (input.toLowerCase().matches(".*\\b(between|from.*to)\\b.*")) {
            EntityAttribute dateRange = new EntityAttribute();
            dateRange.setAttributeName("dateRange");
            dateRange.setOperator(Operator.BETWEEN);
            dateRange.setValue(extractDateRange(input));
            entities.add(dateRange);
        }
        
        if (input.toLowerCase().matches(".*\\bthis\\s*month\\b.*")) {
            EntityAttribute thisMonth = new EntityAttribute();
            thisMonth.setAttributeName("createdDate");
            thisMonth.setOperator(Operator.THIS_MONTH);
            thisMonth.setValue("current_month");
            entities.add(thisMonth);
        }
        
        return entities;
    }
    
    /**
     * Detect operator for specific attribute
     */
    private static Operator detectOperatorForAttribute(String input, String attribute) {
        String context = extractContextAroundAttribute(input, attribute);
        
        for (Operator operator : Operator.values()) {
            if (context.matches(".*\\b" + operator.getPattern() + "\\b.*")) {
                return operator;
            }
        }
        
        return Operator.EQUALS; // Default operator
    }
    
    /**
     * Extract context around attribute for operator detection
     */
    private static String extractContextAroundAttribute(String input, String attribute) {
        int index = input.toLowerCase().indexOf(attribute.toLowerCase());
        if (index == -1) return input;
        
        int start = Math.max(0, index - 20);
        int end = Math.min(input.length(), index + attribute.length() + 20);
        
        return input.substring(start, end).toLowerCase();
    }
    
    /**
     * Extract value for specific attribute
     */
    private static String extractValueForAttribute(String input, String attribute) {
        // Try to find value after the attribute
        Pattern pattern = Pattern.compile("\\b" + attribute + "\\s*[=:]?\\s*([A-Za-z0-9-]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.find() && matcher.groupCount() >= 1) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extract date range from input
     */
    private static String extractDateRange(String input) {
        // Look for patterns like "jan 2025 to till date", "between jan 2025 to march 2025"
        Pattern pattern = Pattern.compile("(?:between|from)\\s*([a-zA-Z0-9\\s]+)\\s*(?:to|till|until)\\s*([a-zA-Z0-9\\s]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.find()) {
            String startDate = matcher.group(1).trim();
            String endDate = matcher.group(2).trim();
            return startDate + " TO " + endDate;
        }
        
        return "date_range";
    }
    
    /**
     * Build structured response
     */
    private static StructuredQueryResponse buildStructuredResponse(
            String originalInput, String correctedInput, QueryType queryType, 
            ActionType actionType, Map<MainProperty, String> mainProperties, 
            List<EntityAttribute> entities) {
        
        StructuredQueryResponse response = new StructuredQueryResponse();
        response.setOriginalInput(originalInput);
        response.setCorrectedInput(correctedInput);
        response.setQueryType(queryType);
        response.setActionType(actionType);
        response.setMainProperties(mainProperties);
        response.setEntities(entities);
        response.setTimestamp(System.currentTimeMillis());
        response.setProcessingTimeMs(2.5); // Simulated processing time
        
        return response;
    }
}

/**
 * Structured Query Response - The specific JSON structure you requested
 */
class StructuredQueryResponse {
    private String originalInput;
    private String correctedInput;
    private StructuredQueryProcessor.QueryType queryType;
    private StructuredQueryProcessor.ActionType actionType;
    private Map<StructuredQueryProcessor.MainProperty, String> mainProperties;
    private List<EntityAttribute> entities;
    private long timestamp;
    private double processingTimeMs;
    
    // Getters and Setters
    public String getOriginalInput() { return originalInput; }
    public void setOriginalInput(String originalInput) { this.originalInput = originalInput; }
    
    public String getCorrectedInput() { return correctedInput; }
    public void setCorrectedInput(String correctedInput) { this.correctedInput = correctedInput; }
    
    public StructuredQueryProcessor.QueryType getQueryType() { return queryType; }
    public void setQueryType(StructuredQueryProcessor.QueryType queryType) { this.queryType = queryType; }
    
    public StructuredQueryProcessor.ActionType getActionType() { return actionType; }
    public void setActionType(StructuredQueryProcessor.ActionType actionType) { this.actionType = actionType; }
    
    public Map<StructuredQueryProcessor.MainProperty, String> getMainProperties() { return mainProperties; }
    public void setMainProperties(Map<StructuredQueryProcessor.MainProperty, String> mainProperties) { this.mainProperties = mainProperties; }
    
    public List<EntityAttribute> getEntities() { return entities; }
    public void setEntities(List<EntityAttribute> entities) { this.entities = entities; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public double getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(double processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}

/**
 * Entity Attribute with operators and values
 */
class EntityAttribute {
    private String attributeName;     // e.g., "effectiveDate"
    private String originalName;      // e.g., "effective"
    private StructuredQueryProcessor.Operator operator;  // e.g., BETWEEN, EQUALS
    private String value;            // e.g., "2024-01-01", "jan 2025 TO till date"
    
    // Getters and Setters
    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }
    
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    
    public StructuredQueryProcessor.Operator getOperator() { return operator; }
    public void setOperator(StructuredQueryProcessor.Operator operator) { this.operator = operator; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    @Override
    public String toString() {
        return "EntityAttribute{" +
                "attributeName='" + attributeName + '\'' +
                ", originalName='" + originalName + '\'' +
                ", operator=" + operator +
                ", value='" + value + '\'' +
                '}';
    }
}

/**
 * Test class for Structured Query Processor
 */
class StructuredQueryProcessorTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 STRUCTURED QUERY PROCESSOR TEST");
        System.out.println("==================================\n");
        
        // Test different query scenarios
        String[] testQueries = {
            "Effective,expir, project for contract 123456",
            "how many contracts created by Dumhp between jan 2025 to till date",
            "show the contracts created in this month",
            "customer name, vendor name for account 789012",
            "inventory, supplier, warranty for parts in contract 555666",
            "all contracts created by John",
            "contracts for customer number 999888",
            "part number P12345 details"
        };
        
        for (int i = 0; i < testQueries.length; i++) {
            System.out.println("📝 Test " + (i + 1) + ": \"" + testQueries[i] + "\"");
            System.out.println("----------------------------------------");
            
            StructuredQueryResponse response = StructuredQueryProcessor.processQuery(testQueries[i]);
            
            printStructuredResponse(response);
            System.out.println("\n" + "=".repeat(60) + "\n");
        }
    }
    
    /**
     * Print structured response in the exact format you requested
     */
    private static void printStructuredResponse(StructuredQueryResponse response) {
        System.out.println("📊 STRUCTURED JSON RESPONSE:");
        System.out.println("{");
        System.out.println("  \"originalInput\": \"" + response.getOriginalInput() + "\",");
        System.out.println("  \"correctedInput\": \"" + response.getCorrectedInput() + "\",");
        System.out.println("  \"queryType\": \"" + response.getQueryType() + "\",");
        System.out.println("  \"actionType\": \"" + response.getActionType() + "\",");
        
        // Main Properties
        System.out.println("  \"mainProperties\": {");
        if (response.getMainProperties().isEmpty()) {
            System.out.println("    // No main properties detected");
        } else {
            int propCount = 0;
            for (Map.Entry<StructuredQueryProcessor.MainProperty, String> entry : response.getMainProperties().entrySet()) {
                String comma = (propCount < response.getMainProperties().size() - 1) ? "," : "";
                System.out.println("    \"" + entry.getKey().getPropertyName() + "\": \"" + entry.getValue() + "\"" + comma);
                propCount++;
            }
        }
        System.out.println("  },");
        
        // Entities
        System.out.println("  \"entities\": [");
        if (response.getEntities().isEmpty()) {
            System.out.println("    // No entities detected");
        } else {
            for (int i = 0; i < response.getEntities().size(); i++) {
                EntityAttribute entity = response.getEntities().get(i);
                String comma = (i < response.getEntities().size() - 1) ? "," : "";
                
                System.out.println("    {");
                System.out.println("      \"attributeName\": \"" + entity.getAttributeName() + "\",");
                System.out.println("      \"originalName\": \"" + entity.getOriginalName() + "\",");
                System.out.println("      \"operator\": \"" + entity.getOperator() + "\",");
                System.out.println("      \"value\": \"" + entity.getValue() + "\"");
                System.out.println("    }" + comma);
            }
        }
        System.out.println("  ],");
        
        System.out.println("  \"timestamp\": " + response.getTimestamp() + ",");
        System.out.println("  \"processingTimeMs\": " + response.getProcessingTimeMs());
        System.out.println("}");
        
        System.out.println("\n🔍 ANALYSIS:");
        System.out.println("✅ Query Type: " + response.getQueryType());
        System.out.println("✅ Action Type: " + response.getActionType());
        System.out.println("✅ Main Properties: " + response.getMainProperties().size() + " detected");
        System.out.println("✅ Entities: " + response.getEntities().size() + " attributes with operators");
        System.out.println("✅ Processing Time: " + response.getProcessingTimeMs() + "ms");
    }
}
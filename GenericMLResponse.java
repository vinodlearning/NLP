import java.util.*;

/**
 * Generic ML Response System
 * 
 * This system handles dynamic attributes for:
 * - Contract: Any combination of 30+ columns
 * - Parts: Different parts-specific attributes  
 * - Help: Variable help content
 * 
 * The structure adapts to whatever attributes the user requests
 */

// Main Generic Response Class
public class GenericMLResponse {
    private String responseType;
    private String message;
    private boolean success;
    private long timestamp;
    private RequestInfo request;
    private Map<String, Object> data;           // Generic data container
    private ProcessingInfo processing;
    private ActionInfo actions;
    
    // Constructors
    public GenericMLResponse() {
        this.data = new HashMap<>();
    }
    
    // Getters and Setters
    public String getResponseType() { return responseType; }
    public void setResponseType(String responseType) { this.responseType = responseType; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public RequestInfo getRequest() { return request; }
    public void setRequest(RequestInfo request) { this.request = request; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    
    public ProcessingInfo getProcessing() { return processing; }
    public void setProcessing(ProcessingInfo processing) { this.processing = processing; }
    
    public ActionInfo getActions() { return actions; }
    public void setActions(ActionInfo actions) { this.actions = actions; }
    
    // Generic attribute access methods
    public Object getAttribute(String attributeName) {
        return data != null ? data.get(attributeName) : null;
    }
    
    public String getAttributeAsString(String attributeName) {
        Object value = getAttribute(attributeName);
        return value != null ? value.toString() : null;
    }
    
    public Integer getAttributeAsInteger(String attributeName) {
        Object value = getAttribute(attributeName);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    public Double getAttributeAsDouble(String attributeName) {
        Object value = getAttribute(attributeName);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    // Get all available attributes
    public Set<String> getAvailableAttributes() {
        return data != null ? data.keySet() : new HashSet<>();
    }
    
    // Check if specific attribute exists
    public boolean hasAttribute(String attributeName) {
        return data != null && data.containsKey(attributeName);
    }
    
    // Get nested object (for complex attributes)
    @SuppressWarnings("unchecked")
    public Map<String, Object> getNestedObject(String objectName) {
        Object value = getAttribute(objectName);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }
    
    // Get list attribute
    @SuppressWarnings("unchecked")
    public List<Object> getAttributeAsList(String attributeName) {
        Object value = getAttribute(attributeName);
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return null;
    }
}

// Request Information (flexible for any model)
class RequestInfo {
    private String originalInput;
    private String correctedInput;
    private String entityId;                    // Contract ID, Parts ID, etc.
    private String entityType;                  // "CONTRACT", "PARTS", "HELP"
    private List<String> requestedAttributes;
    private Map<String, Object> detectedEntities; // Any detected entities
    
    // Constructors
    public RequestInfo() {
        this.requestedAttributes = new ArrayList<>();
        this.detectedEntities = new HashMap<>();
    }
    
    // Getters and Setters
    public String getOriginalInput() { return originalInput; }
    public void setOriginalInput(String originalInput) { this.originalInput = originalInput; }
    
    public String getCorrectedInput() { return correctedInput; }
    public void setCorrectedInput(String correctedInput) { this.correctedInput = correctedInput; }
    
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public List<String> getRequestedAttributes() { return requestedAttributes; }
    public void setRequestedAttributes(List<String> requestedAttributes) { this.requestedAttributes = requestedAttributes; }
    
    public Map<String, Object> getDetectedEntities() { return detectedEntities; }
    public void setDetectedEntities(Map<String, Object> detectedEntities) { this.detectedEntities = detectedEntities; }
}

// Processing Information
class ProcessingInfo {
    private String modelUsed;
    private Double processingTimeMs;
    private List<String> spellCorrectionsApplied;
    private Double confidence;
    private int attributesFound;
    private String dataSource;
    private Map<String, Object> additionalInfo;
    
    // Constructors
    public ProcessingInfo() {
        this.spellCorrectionsApplied = new ArrayList<>();
        this.additionalInfo = new HashMap<>();
    }
    
    // Getters and Setters
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    
    public Double getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Double processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public List<String> getSpellCorrectionsApplied() { return spellCorrectionsApplied; }
    public void setSpellCorrectionsApplied(List<String> spellCorrectionsApplied) { this.spellCorrectionsApplied = spellCorrectionsApplied; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public int getAttributesFound() { return attributesFound; }
    public void setAttributesFound(int attributesFound) { this.attributesFound = attributesFound; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    
    public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }
}

// Action Information
class ActionInfo {
    private String recommendedUIAction;
    private String navigationTarget;
    private List<String> availableActions;
    private Map<String, Object> actionParameters;
    
    // Constructors
    public ActionInfo() {
        this.availableActions = new ArrayList<>();
        this.actionParameters = new HashMap<>();
    }
    
    // Getters and Setters
    public String getRecommendedUIAction() { return recommendedUIAction; }
    public void setRecommendedUIAction(String recommendedUIAction) { this.recommendedUIAction = recommendedUIAction; }
    
    public String getNavigationTarget() { return navigationTarget; }
    public void setNavigationTarget(String navigationTarget) { this.navigationTarget = navigationTarget; }
    
    public List<String> getAvailableActions() { return availableActions; }
    public void setAvailableActions(List<String> availableActions) { this.availableActions = availableActions; }
    
    public Map<String, Object> getActionParameters() { return actionParameters; }
    public void setActionParameters(Map<String, Object> actionParameters) { this.actionParameters = actionParameters; }
}

/**
 * Helper class for building responses dynamically
 */
class GenericResponseBuilder {
    private GenericMLResponse response;
    
    public GenericResponseBuilder() {
        this.response = new GenericMLResponse();
        this.response.setTimestamp(System.currentTimeMillis());
    }
    
    public GenericResponseBuilder setResponseType(String responseType) {
        response.setResponseType(responseType);
        return this;
    }
    
    public GenericResponseBuilder setMessage(String message) {
        response.setMessage(message);
        return this;
    }
    
    public GenericResponseBuilder setSuccess(boolean success) {
        response.setSuccess(success);
        return this;
    }
    
    // Add any attribute dynamically
    public GenericResponseBuilder addAttribute(String name, Object value) {
        response.getData().put(name, value);
        return this;
    }
    
    // Add multiple attributes at once
    public GenericResponseBuilder addAttributes(Map<String, Object> attributes) {
        response.getData().putAll(attributes);
        return this;
    }
    
    // Add nested object
    public GenericResponseBuilder addNestedObject(String name, Map<String, Object> nestedObject) {
        response.getData().put(name, nestedObject);
        return this;
    }
    
    // Add list attribute
    public GenericResponseBuilder addList(String name, List<Object> list) {
        response.getData().put(name, list);
        return this;
    }
    
    public GenericResponseBuilder setRequest(RequestInfo request) {
        response.setRequest(request);
        return this;
    }
    
    public GenericResponseBuilder setProcessing(ProcessingInfo processing) {
        response.setProcessing(processing);
        return this;
    }
    
    public GenericResponseBuilder setActions(ActionInfo actions) {
        response.setActions(actions);
        return this;
    }
    
    public GenericMLResponse build() {
        return response;
    }
}

/**
 * Usage Examples
 */
class GenericResponseExamples {
    
    public static void main(String[] args) {
        System.out.println("🧪 Generic ML Response Examples");
        System.out.println("===============================\n");
        
        // Example 1: Contract with user's requested attributes
        demonstrateContractResponse();
        
        // Example 2: Parts response with different attributes
        demonstratePartsResponse();
        
        // Example 3: How to handle any user query dynamically
        demonstrateDynamicResponse();
    }
    
    public static void demonstrateContractResponse() {
        System.out.println("📋 Example 1: Contract Response (Your Input)");
        System.out.println("--------------------------------------------");
        
        // User input: "effective date,expiration,price expiration,projecttype for contarct 124563"
        String[] userRequestedAttributes = {"effectiveDate", "expirationDate", "priceExpirationDate", "projectType"};
        
        // Build response dynamically based on user request
        GenericMLResponse response = new GenericResponseBuilder()
            .setResponseType("CONTRACT_DETAILS_SUCCESS")
            .setMessage("Contract details retrieved successfully")
            .setSuccess(true)
            // Add only the attributes user requested
            .addAttribute("contractId", "124563")
            .addAttribute("effectiveDate", "2024-01-15")
            .addAttribute("expirationDate", "2025-12-31")
            .addAttribute("priceExpirationDate", "2025-06-30")
            .addAttribute("projectType", "Software Development")
            // Add additional attributes that might be useful
            .addAttribute("status", "ACTIVE")
            .addAttribute("totalValue", 125000.00)
            .build();
        
        System.out.println("✅ User requested attributes:");
        for (String attr : userRequestedAttributes) {
            Object value = response.getAttribute(attr);
            System.out.println("   " + attr + ": " + value);
        }
        
        System.out.println("\n📊 All available attributes:");
        for (String attr : response.getAvailableAttributes()) {
            System.out.println("   " + attr + ": " + response.getAttribute(attr));
        }
        System.out.println();
    }
    
    public static void demonstratePartsResponse() {
        System.out.println("🔧 Example 2: Parts Response (Different Attributes)");
        System.out.println("---------------------------------------------------");
        
        // User might ask: "show inventory, supplier, warranty for parts in contract 123456"
        GenericMLResponse response = new GenericResponseBuilder()
            .setResponseType("PARTS_DETAILS_SUCCESS")
            .setMessage("Parts details retrieved successfully")
            .setSuccess(true)
            // Parts-specific attributes (completely different from contract)
            .addAttribute("contractId", "123456")
            .addAttribute("totalPartsCount", 75)
            .addAttribute("availablePartsCount", 62)
            .addAttribute("lowStockPartsCount", 10)
            // Add parts list with nested objects
            .addList("partsList", Arrays.asList(
                Map.of(
                    "partId", "P001",
                    "partName", "Control Module", 
                    "inventory", 25,
                    "supplier", "TechSupplier Inc",
                    "warranty", "24 months",
                    "unitPrice", 150.00
                ),
                Map.of(
                    "partId", "P002",
                    "partName", "Power Supply",
                    "inventory", 15,
                    "supplier", "PowerTech Ltd", 
                    "warranty", "36 months",
                    "unitPrice", 275.00
                )
            ))
            .build();
        
        System.out.println("✅ Parts attributes:");
        System.out.println("   Contract ID: " + response.getAttribute("contractId"));
        System.out.println("   Total Parts: " + response.getAttribute("totalPartsCount"));
        System.out.println("   Available: " + response.getAttribute("availablePartsCount"));
        
        // Access parts list
        List<Object> partsList = response.getAttributeAsList("partsList");
        if (partsList != null) {
            System.out.println("   Parts Details:");
            for (Object part : partsList) {
                @SuppressWarnings("unchecked")
                Map<String, Object> partMap = (Map<String, Object>) part;
                System.out.println("     - " + partMap.get("partName") + 
                                 " (Inventory: " + partMap.get("inventory") + 
                                 ", Supplier: " + partMap.get("supplier") + ")");
            }
        }
        System.out.println();
    }
    
    public static void demonstrateDynamicResponse() {
        System.out.println("🎯 Example 3: Dynamic Response Builder");
        System.out.println("--------------------------------------");
        
        // Simulate different user queries and build responses dynamically
        String[] differentQueries = {
            "show customer, status, value for contract 123456",
            "get partName, quantity, price for all parts", 
            "show creation steps, timeline, requirements"
        };
        
        for (String query : differentQueries) {
            System.out.println("📝 Query: \"" + query + "\"");
            GenericMLResponse response = buildResponseForQuery(query);
            
            System.out.println("   📋 Response Type: " + response.getResponseType());
            System.out.println("   📊 Attributes: " + response.getAvailableAttributes().size());
            System.out.println("   🎯 Available: " + response.getAvailableAttributes());
            System.out.println();
        }
    }
    
    // Helper method to build response based on query type
    private static GenericMLResponse buildResponseForQuery(String query) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setSuccess(true);
        
        if (query.contains("contract")) {
            return builder
                .setResponseType("CONTRACT_DETAILS_SUCCESS")
                .setMessage("Contract details retrieved")
                .addAttribute("customerId", "CUST001")
                .addAttribute("customerName", "ABC Corp")
                .addAttribute("status", "ACTIVE")
                .addAttribute("totalValue", 125000.00)
                .addAttribute("currency", "USD")
                .build();
        } else if (query.contains("parts")) {
            return builder
                .setResponseType("PARTS_DETAILS_SUCCESS")
                .setMessage("Parts details retrieved")
                .addAttribute("totalParts", 50)
                .addList("parts", Arrays.asList(
                    Map.of("partName", "Module A", "quantity", 10, "price", 100.00),
                    Map.of("partName", "Module B", "quantity", 20, "price", 150.00)
                ))
                .build();
        } else {
            return builder
                .setResponseType("HELP_DETAILS_SUCCESS")
                .setMessage("Help information provided")
                .addList("steps", Arrays.asList("Step 1", "Step 2", "Step 3"))
                .addAttribute("estimatedTime", "30 minutes")
                .addAttribute("difficulty", "Medium")
                .build();
        }
    }
}
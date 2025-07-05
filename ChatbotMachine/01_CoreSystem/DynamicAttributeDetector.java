import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Dynamic Attribute Detector
 * 
 * This class analyzes user queries and determines:
 * - What attributes they want from any table
 * - Entity IDs (contract IDs, part IDs, etc.)
 * - Query type (contract, parts, help)
 * - Spell corrections needed
 * 
 * Supports 30+ contract columns and unlimited parts attributes
 */
public class DynamicAttributeDetector {
    
    // Contract table schema (30+ columns)
    private static final Map<String, String> CONTRACT_ATTRIBUTES = new HashMap<>();
    static {
        // Basic contract information
        CONTRACT_ATTRIBUTES.put("contractid", "contractId");
        CONTRACT_ATTRIBUTES.put("contractnumber", "contractNumber");
        CONTRACT_ATTRIBUTES.put("contractname", "contractName");
        CONTRACT_ATTRIBUTES.put("contracttype", "contractType");
        CONTRACT_ATTRIBUTES.put("projecttype", "projectType");
        CONTRACT_ATTRIBUTES.put("status", "status");
        CONTRACT_ATTRIBUTES.put("phase", "phase");
        CONTRACT_ATTRIBUTES.put("priority", "priority");
        
        // Date attributes
        CONTRACT_ATTRIBUTES.put("effectivedate", "effectiveDate");
        CONTRACT_ATTRIBUTES.put("expirationdate", "expirationDate");
        CONTRACT_ATTRIBUTES.put("startdate", "startDate");
        CONTRACT_ATTRIBUTES.put("enddate", "endDate");
        CONTRACT_ATTRIBUTES.put("createddate", "createdDate");
        CONTRACT_ATTRIBUTES.put("modifieddate", "modifiedDate");
        CONTRACT_ATTRIBUTES.put("signeddate", "signedDate");
        CONTRACT_ATTRIBUTES.put("renewaldate", "renewalDate");
        CONTRACT_ATTRIBUTES.put("priceexpirationdate", "priceExpirationDate");
        CONTRACT_ATTRIBUTES.put("deliverydate", "deliveryDate");
        
        // Financial attributes
        CONTRACT_ATTRIBUTES.put("totalvalue", "totalValue");
        CONTRACT_ATTRIBUTES.put("contractvalue", "contractValue");
        CONTRACT_ATTRIBUTES.put("netvalue", "netValue");
        CONTRACT_ATTRIBUTES.put("grossvalue", "grossValue");
        CONTRACT_ATTRIBUTES.put("taxamount", "taxAmount");
        CONTRACT_ATTRIBUTES.put("discountamount", "discountAmount");
        CONTRACT_ATTRIBUTES.put("penaltyamount", "penaltyAmount");
        CONTRACT_ATTRIBUTES.put("bonusamount", "bonusAmount");
        CONTRACT_ATTRIBUTES.put("currency", "currency");
        CONTRACT_ATTRIBUTES.put("exchangerate", "exchangeRate");
        
        // Customer/Vendor attributes
        CONTRACT_ATTRIBUTES.put("customerid", "customerId");
        CONTRACT_ATTRIBUTES.put("customername", "customerName");
        CONTRACT_ATTRIBUTES.put("vendorid", "vendorId");
        CONTRACT_ATTRIBUTES.put("vendorname", "vendorName");
        CONTRACT_ATTRIBUTES.put("accountid", "accountId");
        CONTRACT_ATTRIBUTES.put("accountname", "accountName");
        CONTRACT_ATTRIBUTES.put("contactperson", "contactPerson");
        CONTRACT_ATTRIBUTES.put("contactemail", "contactEmail");
        CONTRACT_ATTRIBUTES.put("contactphone", "contactPhone");
        
        // Business attributes
        CONTRACT_ATTRIBUTES.put("department", "department");
        CONTRACT_ATTRIBUTES.put("division", "division");
        CONTRACT_ATTRIBUTES.put("region", "region");
        CONTRACT_ATTRIBUTES.put("territory", "territory");
        CONTRACT_ATTRIBUTES.put("businessunit", "businessUnit");
        CONTRACT_ATTRIBUTES.put("costcenter", "costCenter");
        CONTRACT_ATTRIBUTES.put("profitcenter", "profitCenter");
        
        // Technical attributes
        CONTRACT_ATTRIBUTES.put("description", "description");
        CONTRACT_ATTRIBUTES.put("comments", "comments");
        CONTRACT_ATTRIBUTES.put("notes", "notes");
        CONTRACT_ATTRIBUTES.put("terms", "terms");
        CONTRACT_ATTRIBUTES.put("conditions", "conditions");
        CONTRACT_ATTRIBUTES.put("specifications", "specifications");
        CONTRACT_ATTRIBUTES.put("deliverables", "deliverables");
        CONTRACT_ATTRIBUTES.put("milestones", "milestones");
        CONTRACT_ATTRIBUTES.put("risklevel", "riskLevel");
        CONTRACT_ATTRIBUTES.put("compliancestatus", "complianceStatus");
        CONTRACT_ATTRIBUTES.put("approvalstatus", "approvalStatus");
        CONTRACT_ATTRIBUTES.put("documentpath", "documentPath");
        CONTRACT_ATTRIBUTES.put("attachments", "attachments");
    }
    
    // Parts table schema (different attributes)
    private static final Map<String, String> PARTS_ATTRIBUTES = new HashMap<>();
    static {
        PARTS_ATTRIBUTES.put("partid", "partId");
        PARTS_ATTRIBUTES.put("partname", "partName");
        PARTS_ATTRIBUTES.put("partnumber", "partNumber");
        PARTS_ATTRIBUTES.put("parttype", "partType");
        PARTS_ATTRIBUTES.put("category", "category");
        PARTS_ATTRIBUTES.put("subcategory", "subCategory");
        PARTS_ATTRIBUTES.put("description", "description");
        PARTS_ATTRIBUTES.put("specifications", "specifications");
        PARTS_ATTRIBUTES.put("quantity", "quantity");
        PARTS_ATTRIBUTES.put("availablequantity", "availableQuantity");
        PARTS_ATTRIBUTES.put("reservedquantity", "reservedQuantity");
        PARTS_ATTRIBUTES.put("inventory", "inventory");
        PARTS_ATTRIBUTES.put("stock", "stock");
        PARTS_ATTRIBUTES.put("stocklevel", "stockLevel");
        PARTS_ATTRIBUTES.put("unitprice", "unitPrice");
        PARTS_ATTRIBUTES.put("totalprice", "totalPrice");
        PARTS_ATTRIBUTES.put("cost", "cost");
        PARTS_ATTRIBUTES.put("supplier", "supplier");
        PARTS_ATTRIBUTES.put("supplierid", "supplierId");
        PARTS_ATTRIBUTES.put("suppliername", "supplierName");
        PARTS_ATTRIBUTES.put("manufacturer", "manufacturer");
        PARTS_ATTRIBUTES.put("brand", "brand");
        PARTS_ATTRIBUTES.put("model", "model");
        PARTS_ATTRIBUTES.put("version", "version");
        PARTS_ATTRIBUTES.put("warranty", "warranty");
        PARTS_ATTRIBUTES.put("warrantyperiod", "warrantyPeriod");
        PARTS_ATTRIBUTES.put("leadtime", "leadTime");
        PARTS_ATTRIBUTES.put("deliverydate", "deliveryDate");
        PARTS_ATTRIBUTES.put("location", "location");
        PARTS_ATTRIBUTES.put("warehouse", "warehouse");
        PARTS_ATTRIBUTES.put("bin", "bin");
        PARTS_ATTRIBUTES.put("weight", "weight");
        PARTS_ATTRIBUTES.put("dimensions", "dimensions");
        PARTS_ATTRIBUTES.put("color", "color");
        PARTS_ATTRIBUTES.put("material", "material");
        PARTS_ATTRIBUTES.put("status", "status");
        PARTS_ATTRIBUTES.put("condition", "condition");
        PARTS_ATTRIBUTES.put("serialnumber", "serialNumber");
        PARTS_ATTRIBUTES.put("barcode", "barCode");
    }
    
    // Common spell corrections
    private static final Map<String, String> SPELL_CORRECTIONS = new HashMap<>();
    static {
        SPELL_CORRECTIONS.put("effectuve", "effective");
        SPELL_CORRECTIONS.put("expiration", "expiration");
        SPELL_CORRECTIONS.put("contarct", "contract");
        SPELL_CORRECTIONS.put("contrct", "contract");
        SPELL_CORRECTIONS.put("parst", "parts");
        SPELL_CORRECTIONS.put("partz", "parts");
        SPELL_CORRECTIONS.put("customr", "customer");
        SPELL_CORRECTIONS.put("vender", "vendor");
        SPELL_CORRECTIONS.put("prise", "price");
        SPELL_CORRECTIONS.put("dat", "date");
        SPELL_CORRECTIONS.put("staus", "status");
        SPELL_CORRECTIONS.put("creat", "create");
        SPELL_CORRECTIONS.put("shw", "show");
        SPELL_CORRECTIONS.put("shwo", "show");
        SPELL_CORRECTIONS.put("dispaly", "display");
        SPELL_CORRECTIONS.put("gte", "get");
        SPELL_CORRECTIONS.put("gt", "get");
        SPELL_CORRECTIONS.put("acount", "account");
        SPELL_CORRECTIONS.put("acont", "account");
        SPELL_CORRECTIONS.put("exipraion", "expiration");
        SPELL_CORRECTIONS.put("warenty", "warranty");
        SPELL_CORRECTIONS.put("quantiy", "quantity");
        SPELL_CORRECTIONS.put("quanity", "quantity");
        SPELL_CORRECTIONS.put("prise", "price");
        SPELL_CORRECTIONS.put("costumer", "customer");
        SPELL_CORRECTIONS.put("suppler", "supplier");
        SPELL_CORRECTIONS.put("suply", "supply");
        SPELL_CORRECTIONS.put("inventry", "inventory");
        SPELL_CORRECTIONS.put("invntory", "inventory");
        SPELL_CORRECTIONS.put("delivry", "delivery");
        SPELL_CORRECTIONS.put("delyvery", "delivery");
        SPELL_CORRECTIONS.put("manfacturer", "manufacturer");
        SPELL_CORRECTIONS.put("manufacurer", "manufacturer");
        SPELL_CORRECTIONS.put("specfications", "specifications");
        SPELL_CORRECTIONS.put("specifcations", "specifications");
    }
    
    // Entity ID patterns
    private static final Pattern CONTRACT_ID_PATTERN = Pattern.compile("\\b(?:contract|contractid|id)\\s*[#:=]?\\s*([A-Z0-9-]+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SIMPLE_ID_PATTERN = Pattern.compile("\\b([A-Z0-9-]{3,})\\b");
    
    /**
     * Analyzes user query and returns detected attributes and metadata
     */
    public static QueryAnalysis analyzeQuery(String userInput) {
        QueryAnalysis analysis = new QueryAnalysis();
        analysis.setOriginalInput(userInput);
        
        // Step 1: Apply spell corrections
        String correctedInput = applySpellCorrections(userInput);
        analysis.setCorrectedInput(correctedInput);
        
        // Step 2: Detect entity type and routing
        String entityType = detectEntityType(correctedInput);
        analysis.setEntityType(entityType);
        
        // Step 3: Extract entity IDs
        String entityId = extractEntityId(correctedInput);
        analysis.setEntityId(entityId);
        
        // Step 4: Detect requested attributes based on entity type
        List<String> requestedAttributes = detectRequestedAttributes(correctedInput, entityType);
        analysis.setRequestedAttributes(requestedAttributes);
        
        // Step 5: Detect query intent
        String queryIntent = detectQueryIntent(correctedInput);
        analysis.setQueryIntent(queryIntent);
        
        // Step 6: Extract additional entities
        Map<String, Object> additionalEntities = extractAdditionalEntities(correctedInput);
        analysis.setAdditionalEntities(additionalEntities);
        
        return analysis;
    }
    
    /**
     * Apply spell corrections to user input
     */
    private static String applySpellCorrections(String input) {
        String corrected = input.toLowerCase();
        
        for (Map.Entry<String, String> correction : SPELL_CORRECTIONS.entrySet()) {
            corrected = corrected.replaceAll("\\b" + correction.getKey() + "\\b", correction.getValue());
        }
        
        return corrected;
    }
    
    /**
     * Detect entity type (CONTRACT, PARTS, HELP)
     */
    private static String detectEntityType(String input) {
        String lowerInput = input.toLowerCase();
        
        // Check for parts keywords
        if (lowerInput.matches(".*\\b(parts?|part|components?|component|inventory|stock)\\b.*")) {
            if (lowerInput.matches(".*\\b(create|add|insert|new)\\b.*")) {
                return "PARTS_CREATE_ERROR";
            }
            return "PARTS";
        }
        
        // Check for help/creation keywords
        if (lowerInput.matches(".*\\b(help|create|how|steps|guide|tutorial|instructions)\\b.*")) {
            return "HELP";
        }
        
        // Default to contract
        return "CONTRACT";
    }
    
    /**
     * Extract entity ID from input
     */
    private static String extractEntityId(String input) {
        // Try contract ID pattern first
        Matcher contractMatcher = CONTRACT_ID_PATTERN.matcher(input);
        if (contractMatcher.find()) {
            return contractMatcher.group(1);
        }
        
        // Try simple ID pattern
        Matcher simpleMatcher = SIMPLE_ID_PATTERN.matcher(input);
        if (simpleMatcher.find()) {
            return simpleMatcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Detect requested attributes based on entity type
     */
    private static List<String> detectRequestedAttributes(String input, String entityType) {
        List<String> attributes = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        
        Map<String, String> schemaMap = entityType.equals("PARTS") ? PARTS_ATTRIBUTES : CONTRACT_ATTRIBUTES;
        
        for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Check if the attribute is mentioned in the input
            if (lowerInput.contains(key) || lowerInput.contains(value.toLowerCase())) {
                attributes.add(value);
            }
        }
        
        // If no specific attributes found, check for common query patterns
        if (attributes.isEmpty()) {
            if (lowerInput.matches(".*\\b(all|everything|details|info|information)\\b.*")) {
                // User wants all attributes
                attributes.addAll(schemaMap.values());
            } else if (lowerInput.matches(".*\\b(show|display|get|list|view)\\b.*")) {
                // User wants basic attributes
                if (entityType.equals("PARTS")) {
                    attributes.addAll(Arrays.asList("partId", "partName", "quantity", "unitPrice", "supplier"));
                } else {
                    attributes.addAll(Arrays.asList("contractId", "contractName", "status", "effectiveDate", "totalValue"));
                }
            }
        }
        
        return attributes;
    }
    
    /**
     * Detect query intent
     */
    private static String detectQueryIntent(String input) {
        String lowerInput = input.toLowerCase();
        
        if (lowerInput.matches(".*\\b(show|display|get|list|view|what|which)\\b.*")) {
            return "RETRIEVE";
        } else if (lowerInput.matches(".*\\b(create|add|insert|new)\\b.*")) {
            return "CREATE";
        } else if (lowerInput.matches(".*\\b(update|modify|change|edit)\\b.*")) {
            return "UPDATE";
        } else if (lowerInput.matches(".*\\b(delete|remove)\\b.*")) {
            return "DELETE";
        } else if (lowerInput.matches(".*\\b(count|how many|total)\\b.*")) {
            return "COUNT";
        } else if (lowerInput.matches(".*\\b(help|how|steps|guide)\\b.*")) {
            return "HELP";
        } else {
            return "QUERY";
        }
    }
    
    /**
     * Extract additional entities (customer names, dates, etc.)
     */
    private static Map<String, Object> extractAdditionalEntities(String input) {
        Map<String, Object> entities = new HashMap<>();
        String lowerInput = input.toLowerCase();
        
        // Extract customer names
        Pattern customerPattern = Pattern.compile("\\b(?:customer|client)\\s+([a-zA-Z\\s]+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher customerMatcher = customerPattern.matcher(input);
        if (customerMatcher.find()) {
            entities.put("customerName", customerMatcher.group(1).trim());
        }
        
        // Extract vendor names
        Pattern vendorPattern = Pattern.compile("\\b(?:vendor|supplier)\\s+([a-zA-Z\\s]+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher vendorMatcher = vendorPattern.matcher(input);
        if (vendorMatcher.find()) {
            entities.put("vendorName", vendorMatcher.group(1).trim());
        }
        
        // Extract date ranges
        if (lowerInput.contains("from") && lowerInput.contains("to")) {
            entities.put("hasDateRange", true);
        }
        
        // Extract numeric values
        Pattern numberPattern = Pattern.compile("\\b(\\d+(?:\\.\\d+)?)\\b");
        Matcher numberMatcher = numberPattern.matcher(input);
        List<String> numbers = new ArrayList<>();
        while (numberMatcher.find()) {
            numbers.add(numberMatcher.group(1));
        }
        if (!numbers.isEmpty()) {
            entities.put("numbers", numbers);
        }
        
        return entities;
    }
}

/**
 * Query Analysis Result
 */
class QueryAnalysis {
    private String originalInput;
    private String correctedInput;
    private String entityType;        // CONTRACT, PARTS, HELP, PARTS_CREATE_ERROR
    private String entityId;          // Contract ID, Parts ID, etc.
    private List<String> requestedAttributes;
    private String queryIntent;       // RETRIEVE, CREATE, UPDATE, DELETE, COUNT, HELP
    private Map<String, Object> additionalEntities;
    
    // Constructors
    public QueryAnalysis() {
        this.requestedAttributes = new ArrayList<>();
        this.additionalEntities = new HashMap<>();
    }
    
    // Getters and Setters
    public String getOriginalInput() { return originalInput; }
    public void setOriginalInput(String originalInput) { this.originalInput = originalInput; }
    
    public String getCorrectedInput() { return correctedInput; }
    public void setCorrectedInput(String correctedInput) { this.correctedInput = correctedInput; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    
    public List<String> getRequestedAttributes() { return requestedAttributes; }
    public void setRequestedAttributes(List<String> requestedAttributes) { this.requestedAttributes = requestedAttributes; }
    
    public String getQueryIntent() { return queryIntent; }
    public void setQueryIntent(String queryIntent) { this.queryIntent = queryIntent; }
    
    public Map<String, Object> getAdditionalEntities() { return additionalEntities; }
    public void setAdditionalEntities(Map<String, Object> additionalEntities) { this.additionalEntities = additionalEntities; }
    
    @Override
    public String toString() {
        return "QueryAnalysis{" +
                "originalInput='" + originalInput + '\'' +
                ", correctedInput='" + correctedInput + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", requestedAttributes=" + requestedAttributes +
                ", queryIntent='" + queryIntent + '\'' +
                ", additionalEntities=" + additionalEntities +
                '}';
    }
}

/**
 * Test the Dynamic Attribute Detector
 */
class DynamicAttributeDetectorTest {
    public static void main(String[] args) {
        System.out.println("🔍 Dynamic Attribute Detector Test");
        System.out.println("===================================\n");
        
        // Test different user queries
        String[] testQueries = {
            "effective date,expiration,price expiration,projecttype for contarct 124563",
            "show all parts for contract 123456",
            "get inventory, supplier, warranty for parts in contract ABC-789",
            "what is the customer name, status, total value for contract 555",
            "how many parst are available for contract 999",
            "display customr, vendorname, deliverydate for contract XYZ-123",
            "help me create a contract",
            "create parts for contract 123",
            "show specifications, manufacturer, cost for part P001",
            "get all details for contract 12345"
        };
        
        for (String query : testQueries) {
            System.out.println("📝 Query: \"" + query + "\"");
            QueryAnalysis analysis = DynamicAttributeDetector.analyzeQuery(query);
            
            System.out.println("   🔧 Corrected: " + analysis.getCorrectedInput());
            System.out.println("   🏷️  Entity Type: " + analysis.getEntityType());
            System.out.println("   🆔 Entity ID: " + analysis.getEntityId());
            System.out.println("   📋 Requested Attributes: " + analysis.getRequestedAttributes());
            System.out.println("   🎯 Query Intent: " + analysis.getQueryIntent());
            if (!analysis.getAdditionalEntities().isEmpty()) {
                System.out.println("   📊 Additional Entities: " + analysis.getAdditionalEntities());
            }
            System.out.println();
        }
    }
}
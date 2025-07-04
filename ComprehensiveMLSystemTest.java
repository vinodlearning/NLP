import java.util.*;

/**
 * Comprehensive ML System Test
 * 
 * This test demonstrates the complete generic ML system that can handle:
 * - Any combination of attributes from 30+ contract columns  
 * - Different parts attributes dynamically
 * - Spell correction and entity detection
 * - Flexible JSON response generation
 */
public class ComprehensiveMLSystemTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Comprehensive ML System Test");
        System.out.println("================================\n");
        
        // Test the complete system with various queries
        testCompleteSystem();
        
        System.out.println("\n🎯 Testing Your Specific Input");
        System.out.println("==============================");
        testSpecificUserInput();
        
        System.out.println("\n🔀 Testing Dynamic Attribute Scenarios");
        System.out.println("======================================");
        testDynamicAttributeScenarios();
    }
    
    /**
     * Test the complete system integration
     */
    private static void testCompleteSystem() {
        System.out.println("🔍 Testing Complete System Integration");
        System.out.println("-------------------------------------\n");
        
        // Simulate different user queries
        String[] queries = {
            "effective date,expiration,price expiration,projecttype for contarct 124563",
            "show customer name, vendor name, total value for contract 124563",
            "get inventory, supplier, warranty for parts in contract 124563",
            "help me create a contract",
            "create parts for contract 123",
            "show all details for contract 124563"
        };
        
        for (String query : queries) {
            System.out.println("📝 Query: \"" + query + "\"");
            
            // Step 1: Analyze the query
            QueryAnalysis analysis = DynamicAttributeDetector.analyzeQuery(query);
            System.out.println("   🔧 Corrected: " + analysis.getCorrectedInput());
            System.out.println("   🏷️  Entity Type: " + analysis.getEntityType());
            System.out.println("   🆔 Entity ID: " + analysis.getEntityId());
            System.out.println("   📋 Requested Attributes: " + analysis.getRequestedAttributes());
            System.out.println("   🎯 Query Intent: " + analysis.getQueryIntent());
            
            // Step 2: Build dynamic response
            GenericMLResponse response = buildDynamicResponse(analysis);
            System.out.println("   ✅ Response Type: " + response.getResponseType());
            System.out.println("   📊 Available Attributes: " + response.getAvailableAttributes().size());
            
            // Step 3: Show attribute values
            if (response.getAvailableAttributes().size() > 0) {
                System.out.println("   🎯 Sample Attributes:");
                int count = 0;
                for (String attr : response.getAvailableAttributes()) {
                    if (count < 3) {
                        Object value = response.getAttribute(attr);
                        System.out.println("     - " + attr + ": " + value);
                        count++;
                    }
                }
                if (response.getAvailableAttributes().size() > 3) {
                    System.out.println("     ... and " + (response.getAvailableAttributes().size() - 3) + " more");
                }
            }
            
            System.out.println();
        }
    }
    
    /**
     * Test your specific user input
     */
    private static void testSpecificUserInput() {
        String userInput = "effective date,expiration,price expiration,projecttype for contarct 124563";
        
        System.out.println("📝 Your Input: \"" + userInput + "\"");
        System.out.println("------------------------------------------------\n");
        
        // Analyze the query
        QueryAnalysis analysis = DynamicAttributeDetector.analyzeQuery(userInput);
        
        System.out.println("🔍 Query Analysis:");
        System.out.println("   Original: " + analysis.getOriginalInput());
        System.out.println("   Corrected: " + analysis.getCorrectedInput());
        System.out.println("   Entity Type: " + analysis.getEntityType());
        System.out.println("   Entity ID: " + analysis.getEntityId());
        System.out.println("   Requested Attributes: " + analysis.getRequestedAttributes());
        System.out.println("   Query Intent: " + analysis.getQueryIntent());
        
        // Build response
        GenericMLResponse response = buildDynamicResponse(analysis);
        
        System.out.println("\n📋 Dynamic Response:");
        System.out.println("   Response Type: " + response.getResponseType());
        System.out.println("   Success: " + response.isSuccess());
        System.out.println("   Message: " + response.getMessage());
        System.out.println("   Total Attributes: " + response.getAvailableAttributes().size());
        
        System.out.println("\n🎯 Your Requested Attributes:");
        for (String attr : analysis.getRequestedAttributes()) {
            Object value = response.getAttribute(attr);
            System.out.println("   ✅ " + attr + ": " + value);
        }
        
        System.out.println("\n📊 Complete Response JSON Structure:");
        System.out.println("   {");
        System.out.println("     \"responseType\": \"" + response.getResponseType() + "\",");
        System.out.println("     \"success\": " + response.isSuccess() + ",");
        System.out.println("     \"message\": \"" + response.getMessage() + "\",");
        System.out.println("     \"timestamp\": " + response.getTimestamp() + ",");
        System.out.println("     \"data\": {");
        for (String attr : response.getAvailableAttributes()) {
            Object value = response.getAttribute(attr);
            if (value instanceof String) {
                System.out.println("       \"" + attr + "\": \"" + value + "\",");
            } else {
                System.out.println("       \"" + attr + "\": " + value + ",");
            }
        }
        System.out.println("     }");
        System.out.println("   }");
    }
    
    /**
     * Test dynamic attribute scenarios
     */
    private static void testDynamicAttributeScenarios() {
        System.out.println("🔄 Testing Different Attribute Combinations");
        System.out.println("------------------------------------------\n");
        
        // Test different attribute combinations
        String[] scenarios = {
            "show only customer name and status for contract 124563",
            "get parts inventory and supplier for contract 124563", 
            "what is the total value, currency, department for contract 124563",
            "display all financial details for contract 124563",
            "show parts specifications and warranty for contract 124563"
        };
        
        for (String scenario : scenarios) {
            System.out.println("📝 Scenario: \"" + scenario + "\"");
            
            QueryAnalysis analysis = DynamicAttributeDetector.analyzeQuery(scenario);
            GenericMLResponse response = buildDynamicResponse(analysis);
            
            System.out.println("   🏷️  Entity Type: " + analysis.getEntityType());
            System.out.println("   📋 Detected Attributes: " + analysis.getRequestedAttributes());
            System.out.println("   ✅ Response Success: " + response.isSuccess());
            System.out.println("   📊 Returned Attributes: " + response.getAvailableAttributes().size());
            
            // Show the actual attribute values
            if (response.isSuccess() && !response.getAvailableAttributes().isEmpty()) {
                System.out.println("   🎯 Attribute Values:");
                for (String attr : response.getAvailableAttributes()) {
                    Object value = response.getAttribute(attr);
                    System.out.println("     - " + attr + ": " + value);
                }
            }
            
            System.out.println();
        }
    }
    
    /**
     * Build dynamic response based on analysis
     */
    private static GenericMLResponse buildDynamicResponse(QueryAnalysis analysis) {
        // Simulate database data
        Map<String, Object> contractData = createSampleContractData();
        List<Map<String, Object>> partsData = createSamplePartsData();
        
        String entityType = analysis.getEntityType();
        
        switch (entityType) {
            case "CONTRACT":
                return buildContractResponse(analysis, contractData);
            case "PARTS":
                return buildPartsResponse(analysis, partsData);
            case "HELP":
                return buildHelpResponse(analysis);
            case "PARTS_CREATE_ERROR":
                return buildErrorResponse(analysis, "Parts cannot be created");
            default:
                return buildDefaultResponse(analysis);
        }
    }
    
    /**
     * Build contract response with dynamic attributes
     */
    private static GenericMLResponse buildContractResponse(QueryAnalysis analysis, Map<String, Object> contractData) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("CONTRACT_DETAILS_SUCCESS")
            .setMessage("Contract details retrieved successfully")
            .setSuccess(true);
        
        List<String> requestedAttributes = analysis.getRequestedAttributes();
        
        if (requestedAttributes.isEmpty()) {
            // Return basic attributes
            builder.addAttribute("contractId", contractData.get("contractId"))
                   .addAttribute("contractName", contractData.get("contractName"))
                   .addAttribute("status", contractData.get("status"))
                   .addAttribute("totalValue", contractData.get("totalValue"));
        } else {
            // Return requested attributes
            for (String attr : requestedAttributes) {
                if (contractData.containsKey(attr)) {
                    builder.addAttribute(attr, contractData.get(attr));
                }
            }
        }
        
        return builder.build();
    }
    
    /**
     * Build parts response with dynamic attributes
     */
    private static GenericMLResponse buildPartsResponse(QueryAnalysis analysis, List<Map<String, Object>> partsData) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("PARTS_DETAILS_SUCCESS")
            .setMessage("Parts details retrieved successfully")
            .setSuccess(true);
        
        builder.addAttribute("contractId", analysis.getEntityId())
               .addAttribute("totalPartsCount", partsData.size());
        
        List<String> requestedAttributes = analysis.getRequestedAttributes();
        
        // Filter parts data based on requested attributes
        List<Map<String, Object>> filteredParts = new ArrayList<>();
        for (Map<String, Object> part : partsData) {
            Map<String, Object> filteredPart = new HashMap<>();
            
            if (requestedAttributes.isEmpty()) {
                // Default attributes
                filteredPart.put("partId", part.get("partId"));
                filteredPart.put("partName", part.get("partName"));
                filteredPart.put("quantity", part.get("quantity"));
                filteredPart.put("unitPrice", part.get("unitPrice"));
            } else {
                // Specific attributes
                for (String attr : requestedAttributes) {
                    if (part.containsKey(attr)) {
                        filteredPart.put(attr, part.get(attr));
                    }
                }
            }
            
            filteredParts.add(filteredPart);
        }
        
        builder.addList("partsList", new ArrayList<>(filteredParts));
        
        return builder.build();
    }
    
    /**
     * Build help response
     */
    private static GenericMLResponse buildHelpResponse(QueryAnalysis analysis) {
        return new GenericResponseBuilder()
            .setResponseType("HELP_DETAILS_SUCCESS")
            .setMessage("Help information provided")
            .setSuccess(true)
            .addList("steps", Arrays.asList("Step 1", "Step 2", "Step 3"))
            .addAttribute("estimatedTime", "30 minutes")
            .build();
    }
    
    /**
     * Build error response
     */
    private static GenericMLResponse buildErrorResponse(QueryAnalysis analysis, String error) {
        return new GenericResponseBuilder()
            .setResponseType("ERROR")
            .setMessage(error)
            .setSuccess(false)
            .addAttribute("errorType", "OPERATION_NOT_SUPPORTED")
            .build();
    }
    
    /**
     * Build default response
     */
    private static GenericMLResponse buildDefaultResponse(QueryAnalysis analysis) {
        return new GenericResponseBuilder()
            .setResponseType("DEFAULT_RESPONSE")
            .setMessage("Query processed")
            .setSuccess(true)
            .addAttribute("detectedIntent", analysis.getQueryIntent())
            .build();
    }
    
    /**
     * Create sample contract data with 30+ attributes
     */
    private static Map<String, Object> createSampleContractData() {
        Map<String, Object> contract = new HashMap<>();
        
        // Your requested attributes
        contract.put("contractId", "124563");
        contract.put("effectiveDate", "2024-01-15");
        contract.put("expirationDate", "2025-12-31");
        contract.put("priceExpirationDate", "2025-06-30");
        contract.put("projectType", "Software Development");
        
        // Additional attributes (showing we can handle 30+ columns)
        contract.put("contractName", "Software Development Agreement");
        contract.put("contractType", "Service Agreement");
        contract.put("status", "ACTIVE");
        contract.put("totalValue", 125000.00);
        contract.put("currency", "USD");
        contract.put("customerId", "CUST001");
        contract.put("customerName", "ABC Corporation");
        contract.put("vendorId", "VEND001");
        contract.put("vendorName", "TechSoft Solutions");
        contract.put("accountId", "ACC001");
        contract.put("accountName", "Main Account");
        contract.put("department", "IT");
        contract.put("region", "North America");
        contract.put("startDate", "2024-01-15");
        contract.put("endDate", "2025-12-31");
        contract.put("createdDate", "2023-12-01");
        contract.put("modifiedDate", "2024-01-10");
        contract.put("signedDate", "2024-01-12");
        contract.put("renewalDate", "2025-10-31");
        contract.put("deliveryDate", "2024-06-30");
        contract.put("contractValue", 125000.00);
        contract.put("netValue", 120000.00);
        contract.put("grossValue", 125000.00);
        contract.put("taxAmount", 5000.00);
        contract.put("discountAmount", 0.00);
        contract.put("penaltyAmount", 0.00);
        contract.put("bonusAmount", 2500.00);
        contract.put("exchangeRate", 1.0);
        contract.put("division", "Software Division");
        contract.put("territory", "Americas");
        contract.put("businessUnit", "Technology Solutions");
        contract.put("costCenter", "CC-IT-001");
        contract.put("profitCenter", "PC-SW-001");
        contract.put("contactPerson", "John Smith");
        contract.put("contactEmail", "john.smith@abccorp.com");
        contract.put("contactPhone", "+1-555-123-4567");
        contract.put("description", "Comprehensive software development services");
        contract.put("terms", "Net 30 payment terms");
        contract.put("conditions", "Standard T&C apply");
        contract.put("specifications", "Custom software development");
        contract.put("deliverables", "Software application and documentation");
        contract.put("milestones", "Design, Development, Testing, Deployment");
        contract.put("riskLevel", "Medium");
        contract.put("complianceStatus", "COMPLIANT");
        contract.put("approvalStatus", "APPROVED");
        contract.put("documentPath", "/contracts/124563/contract.pdf");
        contract.put("attachments", "SOW, Technical Specs, Budget");
        contract.put("priority", "High");
        contract.put("phase", "Execution");
        contract.put("comments", "Project on track");
        contract.put("notes", "Customer satisfied with progress");
        
        return contract;
    }
    
    /**
     * Create sample parts data
     */
    private static List<Map<String, Object>> createSamplePartsData() {
        List<Map<String, Object>> parts = new ArrayList<>();
        
        // Part 1
        Map<String, Object> part1 = new HashMap<>();
        part1.put("partId", "P001");
        part1.put("partName", "Control Module");
        part1.put("partNumber", "CM-2024-001");
        part1.put("quantity", 25);
        part1.put("unitPrice", 150.00);
        part1.put("supplier", "TechSupplier Inc");
        part1.put("inventory", 100);
        part1.put("warranty", "24 months");
        part1.put("specifications", "High-precision control module");
        part1.put("manufacturer", "ControlTech");
        part1.put("category", "Controls");
        part1.put("status", "AVAILABLE");
        parts.add(part1);
        
        // Part 2
        Map<String, Object> part2 = new HashMap<>();
        part2.put("partId", "P002");
        part2.put("partName", "Power Supply");
        part2.put("partNumber", "PS-2024-002");
        part2.put("quantity", 15);
        part2.put("unitPrice", 275.00);
        part2.put("supplier", "PowerTech Ltd");
        part2.put("inventory", 50);
        part2.put("warranty", "36 months");
        part2.put("specifications", "High-efficiency power supply");
        part2.put("manufacturer", "PowerMax");
        part2.put("category", "Power");
        part2.put("status", "AVAILABLE");
        parts.add(part2);
        
        return parts;
    }
}
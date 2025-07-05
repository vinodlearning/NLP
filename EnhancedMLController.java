import java.util.*;

/**
 * Enhanced ML Controller
 * 
 * This controller integrates:
 * - Dynamic Attribute Detector (analyzes user queries)
 * - Generic ML Response (builds flexible responses)
 * - Database simulation (gets actual data)
 * - Business logic (handles routing and validation)
 * 
 * Supports any combination of attributes from 30+ contract columns
 * and different parts attributes dynamically
 */
public class EnhancedMLController {
    
    // Database simulation
    private DatabaseSimulator database;
    
    // Performance tracking
    private long processingStartTime;
    
    public EnhancedMLController() {
        this.database = new DatabaseSimulator();
    }
    
    /**
     * Main method to process user queries
     */
    public GenericMLResponse processQuery(String userInput) {
        processingStartTime = System.currentTimeMillis();
        
        try {
            // Step 1: Analyze user query
            QueryAnalysis analysis = DynamicAttributeDetector.analyzeQuery(userInput);
            
            // Step 2: Build response based on analysis
            GenericMLResponse response = buildResponse(analysis);
            
            // Step 3: Add processing information
            addProcessingInfo(response, analysis);
            
            return response;
            
        } catch (Exception e) {
            return buildErrorResponse(userInput, e);
        }
    }
    
    /**
     * Main method to process user queries and return JSON
     * This method returns a properly formatted JSON string
     */
    public String processQueryJSON(String userInput) {
        GenericMLResponse response = processQuery(userInput);
        return response.processQueryJSON();
    }
    
    /**
     * Build response based on query analysis
     */
    private GenericMLResponse buildResponse(QueryAnalysis analysis) {
        String entityType = analysis.getEntityType();
        
        switch (entityType) {
            case "CONTRACT":
                return buildContractResponse(analysis);
            case "PARTS":
                return buildPartsResponse(analysis);
            case "HELP":
                return buildHelpResponse(analysis);
            case "PARTS_CREATE_ERROR":
                return buildPartsCreateErrorResponse(analysis);
            default:
                return buildDefaultResponse(analysis);
        }
    }
    
    /**
     * Build contract response with dynamic attributes
     */
    private GenericMLResponse buildContractResponse(QueryAnalysis analysis) {
        String contractId = analysis.getEntityId();
        List<String> requestedAttributes = analysis.getRequestedAttributes();
        
        // Get contract data from database
        Map<String, Object> contractData = database.getContractData(contractId);
        
        if (contractData == null) {
            return buildNotFoundResponse(analysis, "Contract not found: " + contractId);
        }
        
        // Build response with requested attributes
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("CONTRACT_DETAILS_SUCCESS")
            .setMessage("Contract details retrieved successfully")
            .setSuccess(true);
        
        // Add all requested attributes
        if (requestedAttributes.isEmpty()) {
            // No specific attributes requested, return basic info
            builder.addAttribute("contractId", contractData.get("contractId"))
                   .addAttribute("contractName", contractData.get("contractName"))
                   .addAttribute("status", contractData.get("status"))
                   .addAttribute("effectiveDate", contractData.get("effectiveDate"))
                   .addAttribute("totalValue", contractData.get("totalValue"));
        } else {
            // Add specific requested attributes
            for (String attribute : requestedAttributes) {
                if (contractData.containsKey(attribute)) {
                    builder.addAttribute(attribute, contractData.get(attribute));
                }
            }
        }
        
        // Add request information
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setOriginalInput(analysis.getOriginalInput());
        requestInfo.setCorrectedInput(analysis.getCorrectedInput());
        requestInfo.setEntityId(contractId);
        requestInfo.setEntityType("CONTRACT");
        requestInfo.setRequestedAttributes(requestedAttributes);
        requestInfo.setDetectedEntities(analysis.getAdditionalEntities());
        
        builder.setRequest(requestInfo);
        
        // Add actions
        ActionInfo actions = new ActionInfo();
        actions.setRecommendedUIAction("DISPLAY_CONTRACT_DETAILS");
        actions.setNavigationTarget("/contract/details/" + contractId);
        actions.setAvailableActions(Arrays.asList("VIEW_DETAILS", "EDIT_CONTRACT", "VIEW_PARTS", "DOWNLOAD_PDF"));
        builder.setActions(actions);
        
        return builder.build();
    }
    
    /**
     * Build parts response with dynamic attributes
     */
    private GenericMLResponse buildPartsResponse(QueryAnalysis analysis) {
        String contractId = analysis.getEntityId();
        List<String> requestedAttributes = analysis.getRequestedAttributes();
        
        // Get parts data from database
        List<Map<String, Object>> partsData = database.getPartsData(contractId);
        
        if (partsData == null || partsData.isEmpty()) {
            return buildNotFoundResponse(analysis, "No parts found for contract: " + contractId);
        }
        
        // Build response with requested attributes
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("PARTS_DETAILS_SUCCESS")
            .setMessage("Parts details retrieved successfully")
            .setSuccess(true);
        
        // Add summary information
        builder.addAttribute("contractId", contractId)
               .addAttribute("totalPartsCount", partsData.size())
               .addAttribute("availablePartsCount", partsData.size());
        
        // Process parts data
        if (analysis.getQueryIntent().equals("COUNT")) {
            // User wants count information
            builder.addAttribute("partsCount", partsData.size());
            
            // Add breakdown by category
            Map<String, Integer> categoryBreakdown = new HashMap<>();
            for (Map<String, Object> part : partsData) {
                String category = (String) part.get("category");
                categoryBreakdown.put(category, categoryBreakdown.getOrDefault(category, 0) + 1);
            }
            builder.addAttribute("categoryBreakdown", categoryBreakdown);
        } else {
            // User wants detailed parts information
            List<Map<String, Object>> filteredParts = new ArrayList<>();
            
            for (Map<String, Object> part : partsData) {
                Map<String, Object> filteredPart = new HashMap<>();
                
                if (requestedAttributes.isEmpty()) {
                    // Default attributes
                    filteredPart.put("partId", part.get("partId"));
                    filteredPart.put("partName", part.get("partName"));
                    filteredPart.put("quantity", part.get("quantity"));
                    filteredPart.put("unitPrice", part.get("unitPrice"));
                    filteredPart.put("supplier", part.get("supplier"));
                } else {
                    // Specific requested attributes
                    for (String attribute : requestedAttributes) {
                        if (part.containsKey(attribute)) {
                            filteredPart.put(attribute, part.get(attribute));
                        }
                    }
                }
                
                filteredParts.add(filteredPart);
            }
            
            builder.addList("partsList", new ArrayList<>(filteredParts));
        }
        
        // Add request information
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setOriginalInput(analysis.getOriginalInput());
        requestInfo.setCorrectedInput(analysis.getCorrectedInput());
        requestInfo.setEntityId(contractId);
        requestInfo.setEntityType("PARTS");
        requestInfo.setRequestedAttributes(requestedAttributes);
        
        builder.setRequest(requestInfo);
        
        // Add actions
        ActionInfo actions = new ActionInfo();
        actions.setRecommendedUIAction("DISPLAY_PARTS_LIST");
        actions.setNavigationTarget("/parts/list/" + contractId);
        actions.setAvailableActions(Arrays.asList("VIEW_PARTS_LIST", "EXPORT_PARTS", "VIEW_INVENTORY", "CHECK_AVAILABILITY"));
        builder.setActions(actions);
        
        return builder.build();
    }
    
    /**
     * Build help response
     */
    private GenericMLResponse buildHelpResponse(QueryAnalysis analysis) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("HELP_DETAILS_SUCCESS")
            .setMessage("Help information provided")
            .setSuccess(true);
        
        // Add help content
        if (analysis.getCorrectedInput().contains("create")) {
            builder.addList("steps", Arrays.asList(
                "1. Gather contract requirements and specifications",
                "2. Identify stakeholders and obtain necessary approvals",
                "3. Fill out contract basic information (name, type, dates)",
                "4. Add financial details (value, currency, payment terms)",
                "5. Specify customer and vendor information",
                "6. Review and submit for approval"
            ));
            builder.addAttribute("estimatedTime", "45-60 minutes");
            builder.addAttribute("difficulty", "Medium");
            builder.addAttribute("requiredDocuments", Arrays.asList("SOW", "Budget Approval", "Vendor Details"));
        } else {
            builder.addList("availableCommands", Arrays.asList(
                "show contract [ID] - View contract details",
                "get parts for contract [ID] - View parts list",
                "create contract - Get contract creation help",
                "help - Show this help message"
            ));
        }
        
        // Add request information
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setOriginalInput(analysis.getOriginalInput());
        requestInfo.setCorrectedInput(analysis.getCorrectedInput());
        requestInfo.setEntityType("HELP");
        
        builder.setRequest(requestInfo);
        
        // Add actions
        ActionInfo actions = new ActionInfo();
        actions.setRecommendedUIAction("DISPLAY_HELP");
        actions.setNavigationTarget("/help");
        actions.setAvailableActions(Arrays.asList("VIEW_HELP", "START_CONTRACT_CREATION", "VIEW_TUTORIALS"));
        builder.setActions(actions);
        
        return builder.build();
    }
    
    /**
     * Build parts creation error response
     */
    private GenericMLResponse buildPartsCreateErrorResponse(QueryAnalysis analysis) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("PARTS_CREATE_ERROR")
            .setMessage("Parts cannot be created through this system")
            .setSuccess(false);
        
        builder.addAttribute("errorType", "OPERATION_NOT_SUPPORTED")
               .addAttribute("explanation", "Parts are loaded from Excel files and cannot be created manually")
               .addAttribute("suggestion", "Use 'show parts for contract [ID]' to view existing parts")
               .addList("alternativeActions", Arrays.asList(
                   "View existing parts for a contract",
                   "Check parts inventory",
                   "Create a new contract (which can have parts assigned)"
               ));
        
        // Add request information
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setOriginalInput(analysis.getOriginalInput());
        requestInfo.setCorrectedInput(analysis.getCorrectedInput());
        requestInfo.setEntityType("PARTS_CREATE_ERROR");
        
        builder.setRequest(requestInfo);
        
        return builder.build();
    }
    
    /**
     * Build not found response
     */
    private GenericMLResponse buildNotFoundResponse(QueryAnalysis analysis, String message) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("NOT_FOUND")
            .setMessage(message)
            .setSuccess(false);
        
        builder.addAttribute("errorType", "ENTITY_NOT_FOUND")
               .addAttribute("entityId", analysis.getEntityId())
               .addAttribute("entityType", analysis.getEntityType())
               .addList("suggestions", Arrays.asList(
                   "Check the ID format and try again",
                   "Use 'show all contracts' to see available contracts",
                   "Contact support if the issue persists"
               ));
        
        return builder.build();
    }
    
    /**
     * Build default response
     */
    private GenericMLResponse buildDefaultResponse(QueryAnalysis analysis) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("DEFAULT_RESPONSE")
            .setMessage("I understand you're looking for information. Let me help you.")
            .setSuccess(true);
        
        builder.addAttribute("detectedIntent", analysis.getQueryIntent())
               .addList("availableCommands", Arrays.asList(
                   "show contract [ID] - View contract details",
                   "get parts for contract [ID] - View parts",
                   "create contract - Get help with contract creation",
                   "help - View all available commands"
               ));
        
        return builder.build();
    }
    
    /**
     * Build error response
     */
    private GenericMLResponse buildErrorResponse(String userInput, Exception e) {
        GenericResponseBuilder builder = new GenericResponseBuilder()
            .setResponseType("PROCESSING_ERROR")
            .setMessage("An error occurred while processing your request")
            .setSuccess(false);
        
        builder.addAttribute("errorType", "PROCESSING_ERROR")
               .addAttribute("errorMessage", e.getMessage())
               .addAttribute("originalInput", userInput)
               .addAttribute("suggestion", "Please try rephrasing your query or contact support");
        
        return builder.build();
    }
    
    /**
     * Add processing information to response
     */
    private void addProcessingInfo(GenericMLResponse response, QueryAnalysis analysis) {
        ProcessingInfo processing = new ProcessingInfo();
        processing.setModelUsed("EnhancedMLController");
        processing.setProcessingTimeMs((double) (System.currentTimeMillis() - processingStartTime));
        processing.setDataSource("DatabaseSimulator");
        processing.setAttributesFound(response.getAvailableAttributes().size());
        processing.setConfidence(0.95); // High confidence for rule-based system
        
        // Add spell corrections applied
        if (!analysis.getOriginalInput().equals(analysis.getCorrectedInput())) {
            processing.getSpellCorrectionsApplied().add("Applied spell corrections");
        }
        
        response.setProcessing(processing);
    }
}

/**
 * Database Simulator
 * 
 * Simulates database operations for testing
 */
class DatabaseSimulator {
    
    private Map<String, Map<String, Object>> contractDatabase;
    private Map<String, List<Map<String, Object>>> partsDatabase;
    
    public DatabaseSimulator() {
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        contractDatabase = new HashMap<>();
        partsDatabase = new HashMap<>();
        
        // Sample contract data
        Map<String, Object> contract1 = new HashMap<>();
        contract1.put("contractId", "124563");
        contract1.put("contractName", "Software Development Agreement");
        contract1.put("contractType", "Service Agreement");
        contract1.put("projectType", "Software Development");
        contract1.put("status", "ACTIVE");
        contract1.put("effectiveDate", "2024-01-15");
        contract1.put("expirationDate", "2025-12-31");
        contract1.put("priceExpirationDate", "2025-06-30");
        contract1.put("totalValue", 125000.00);
        contract1.put("currency", "USD");
        contract1.put("customerId", "CUST001");
        contract1.put("customerName", "ABC Corporation");
        contract1.put("vendorId", "VEND001");
        contract1.put("vendorName", "TechSoft Solutions");
        contract1.put("accountId", "ACC001");
        contract1.put("accountName", "Main Account");
        contract1.put("department", "IT");
        contract1.put("region", "North America");
        contract1.put("description", "Comprehensive software development services");
        contract1.put("terms", "Net 30 payment terms");
        contract1.put("riskLevel", "Medium");
        contract1.put("approvalStatus", "APPROVED");
        contractDatabase.put("124563", contract1);
        
        // Sample contract 2
        Map<String, Object> contract2 = new HashMap<>();
        contract2.put("contractId", "123456");
        contract2.put("contractName", "Hardware Supply Agreement");
        contract2.put("contractType", "Supply Agreement");
        contract2.put("projectType", "Hardware Procurement");
        contract2.put("status", "ACTIVE");
        contract2.put("effectiveDate", "2024-03-01");
        contract2.put("expirationDate", "2025-02-28");
        contract2.put("totalValue", 85000.00);
        contract2.put("currency", "USD");
        contract2.put("customerName", "XYZ Industries");
        contract2.put("vendorName", "Hardware Plus");
        contractDatabase.put("123456", contract2);
        
        // Sample parts data for contract 124563
        List<Map<String, Object>> parts1 = new ArrayList<>();
        
        Map<String, Object> part1 = new HashMap<>();
        part1.put("partId", "P001");
        part1.put("partName", "Control Module");
        part1.put("partNumber", "CM-2024-001");
        part1.put("partType", "Electronic Component");
        part1.put("category", "Controls");
        part1.put("quantity", 25);
        part1.put("availableQuantity", 25);
        part1.put("unitPrice", 150.00);
        part1.put("totalPrice", 3750.00);
        part1.put("supplier", "TechSupplier Inc");
        part1.put("manufacturer", "ControlTech");
        part1.put("warranty", "24 months");
        part1.put("inventory", 100);
        part1.put("specifications", "High-precision control module");
        part1.put("status", "AVAILABLE");
        parts1.add(part1);
        
        Map<String, Object> part2 = new HashMap<>();
        part2.put("partId", "P002");
        part2.put("partName", "Power Supply");
        part2.put("partNumber", "PS-2024-002");
        part2.put("partType", "Power Component");
        part2.put("category", "Power");
        part2.put("quantity", 15);
        part2.put("availableQuantity", 15);
        part2.put("unitPrice", 275.00);
        part2.put("totalPrice", 4125.00);
        part2.put("supplier", "PowerTech Ltd");
        part2.put("manufacturer", "PowerMax");
        part2.put("warranty", "36 months");
        part2.put("inventory", 50);
        part2.put("specifications", "High-efficiency power supply");
        part2.put("status", "AVAILABLE");
        parts1.add(part2);
        
        partsDatabase.put("124563", parts1);
        partsDatabase.put("123456", parts1); // Same parts for demo
    }
    
    public Map<String, Object> getContractData(String contractId) {
        return contractDatabase.get(contractId);
    }
    
    public List<Map<String, Object>> getPartsData(String contractId) {
        return partsDatabase.get(contractId);
    }
}

/**
 * Enhanced ML Controller Test
 */
class EnhancedMLControllerTest {
    
    public static void main(String[] args) {
        System.out.println("🚀 Enhanced ML Controller Test");
        System.out.println("===============================\n");
        
        EnhancedMLController controller = new EnhancedMLController();
        
        // Test different user queries
        String[] testQueries = {
            "effective date,expiration,price expiration,projecttype for contarct 124563",
            "show all parts for contract 123456",
            "get inventory, supplier, warranty for parts in contract 124563",
            "what is the customer name, status, total value for contract 124563",
            "help me create a contract",
            "create parts for contract 123",
            "show contract 999999",  // Non-existent contract
            "get all details for contract 124563"
        };
        
        for (String query : testQueries) {
            System.out.println("📝 Query: \"" + query + "\"");
            GenericMLResponse response = controller.processQuery(query);
            
            System.out.println("   ✅ Response Type: " + response.getResponseType());
            System.out.println("   📋 Success: " + response.isSuccess());
            System.out.println("   💬 Message: " + response.getMessage());
            System.out.println("   📊 Attributes Count: " + response.getAvailableAttributes().size());
            
            if (response.isSuccess() && !response.getAvailableAttributes().isEmpty()) {
                System.out.println("   🎯 Available Attributes: " + response.getAvailableAttributes());
                
                // Show some sample attribute values
                int count = 0;
                for (String attr : response.getAvailableAttributes()) {
                    if (count < 3) { // Show first 3 attributes
                        Object value = response.getAttribute(attr);
                        System.out.println("     - " + attr + ": " + value);
                        count++;
                    }
                }
                if (response.getAvailableAttributes().size() > 3) {
                    System.out.println("     ... and " + (response.getAvailableAttributes().size() - 3) + " more");
                }
            }
            
            if (response.getProcessing() != null) {
                System.out.println("   ⚡ Processing Time: " + response.getProcessing().getProcessingTimeMs() + "ms");
            }
            
            System.out.println();
        }
    }
}
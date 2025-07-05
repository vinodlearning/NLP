package nlp.handler;

import nlp.core.QueryStructure;
import nlp.core.QueryStructure.ActionType;
import nlp.core.QueryStructure.QueryOperator;
import nlp.engine.NLPEngine;

import java.util.*;

/**
 * Request Handler - Analyzes action types and constructs responses
 * 
 * Responsibilities:
 * - Analyze action type from QueryStructure
 * - Call appropriate private method with operators/entities
 * - Construct HTML response with tabular data
 * - Support pagination for large result sets
 * - Highlight corrected terms in results
 * - Handle errors gracefully
 * 
 * @author Contract/Parts Search System
 * @version 1.0
 */
public class RequestHandler {
    
    private NLPEngine nlpEngine;
    private static RequestHandler instance;
    
    // Configuration
    private int defaultPageSize = 25;
    private int maxPageSize = 100;
    private boolean highlightCorrections = true;
    
    // Response templates
    private String htmlTemplate;
    private String tableRowTemplate;
    private String errorTemplate;
    private String helpTemplate;
    
    /**
     * Singleton pattern with lazy initialization
     */
    public static synchronized RequestHandler getInstance() {
        if (instance == null) {
            instance = new RequestHandler();
        }
        return instance;
    }
    
    /**
     * Private constructor - initializes templates and dependencies
     */
    private RequestHandler() {
        this.nlpEngine = NLPEngine.getInstance();
        initializeTemplates();
        System.out.println("Request Handler initialized successfully");
    }
    
    /**
     * MAIN METHOD: Process user query and return HTML response
     * 
     * @param userQuery Raw user input text
     * @return HTML response string
     */
    public String handleRequest(String userQuery) {
        return handleRequest(userQuery, 1, defaultPageSize);
    }
    
    /**
     * Process user query with pagination and return HTML response
     * 
     * @param userQuery Raw user input text
     * @param page Page number (1-based)
     * @param pageSize Number of results per page
     * @return HTML response string
     */
    public String handleRequest(String userQuery, int page, int pageSize) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return constructErrorResponse("Please enter a search query", null);
        }
        
        try {
            // Step 1: Process query through NLP engine
            QueryStructure structure = nlpEngine.processQuery(userQuery);
            
            // Step 2: Handle errors
            if (structure.isHasErrors()) {
                return constructErrorResponse(
                    "Query processing failed: " + String.join(", ", structure.getErrors()),
                    structure.getSuggestions()
                );
            }
            
            // Step 3: Route to appropriate handler based on action type
            return routeToActionHandler(structure, page, pageSize);
            
        } catch (Exception e) {
            return constructErrorResponse("System error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Route to appropriate action handler based on action type
     */
    private String routeToActionHandler(QueryStructure structure, int page, int pageSize) {
        ActionType actionType = structure.getActionType();
        
        switch (actionType) {
            // Contract actions
            case CONTRACTS_BY_USER:
                return handleContractsByUser(structure, page, pageSize);
            case CONTRACTS_BY_CONTRACT_NUMBER:
                return handleContractsByContractNumber(structure, page, pageSize);
            case CONTRACTS_BY_ACCOUNT_NUMBER:
                return handleContractsByAccountNumber(structure, page, pageSize);
            case CONTRACTS_BY_CUSTOMER_NAME:
                return handleContractsByCustomerName(structure, page, pageSize);
            case CONTRACTS_BY_PARTS:
                return handleContractsByParts(structure, page, pageSize);
                
            // Parts actions
            case PARTS_BY_USER:
                return handlePartsByUser(structure, page, pageSize);
            case PARTS_BY_CONTRACT:
                return handlePartsByContract(structure, page, pageSize);
            case PARTS_BY_PART_NUMBER:
                return handlePartsByPartNumber(structure, page, pageSize);
            case PARTS_BY_CUSTOMER:
                return handlePartsByCustomer(structure, page, pageSize);
                
            // Help actions
            case HELP_CONTRACT_CREATION:
                return handleHelpContractCreation(structure);
            case HELP_PARTS_SEARCH:
                return handleHelpPartsSearch(structure);
            case HELP_GENERAL:
                return handleHelpGeneral(structure);
                
            default:
                return constructErrorResponse(
                    "Unknown action type: " + actionType,
                    Arrays.asList("Try using keywords like 'show contract', 'list parts', or 'help'")
                );
        }
    }
    
    /**
     * Handle contracts by user queries
     */
    private String handleContractsByUser(QueryStructure structure, int page, int pageSize) {
        String user = (String) structure.getExtractedEntityValue("created_by");
        if (user == null) {
            return constructErrorResponse("User name not found in query", 
                Arrays.asList("Try: 'show contracts created by john'"));
        }
        
        // Simulate data retrieval
        List<Map<String, Object>> contractData = retrieveContractsByUser(user, structure.getOperators(), page, pageSize);
        
        String title = "Contracts Created by " + user;
        if (highlightCorrections && structure.isHasSpellCorrections()) {
            title += " <span class='spell-corrected'>(Spell corrected)</span>";
        }
        
        return constructDataResponse(title, contractData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle contracts by contract number queries
     */
    private String handleContractsByContractNumber(QueryStructure structure, int page, int pageSize) {
        String contractNumber = (String) structure.getExtractedEntityValue("contract_number");
        if (contractNumber == null) {
            return constructErrorResponse("Contract number not found in query", 
                Arrays.asList("Try: 'show contract 123456'"));
        }
        
        List<Map<String, Object>> contractData = retrieveContractsByNumber(contractNumber, structure.getOperators(), page, pageSize);
        
        String title = "Contract " + contractNumber + " Details";
        return constructDataResponse(title, contractData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle contracts by account number queries
     */
    private String handleContractsByAccountNumber(QueryStructure structure, int page, int pageSize) {
        String accountNumber = (String) structure.getExtractedEntityValue("account_number");
        if (accountNumber == null) {
            return constructErrorResponse("Account number not found in query", 
                Arrays.asList("Try: 'show contracts for account 123456789'"));
        }
        
        List<Map<String, Object>> contractData = retrieveContractsByAccount(accountNumber, structure.getOperators(), page, pageSize);
        
        String title = "Contracts for Account " + accountNumber;
        return constructDataResponse(title, contractData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle contracts by customer name queries
     */
    private String handleContractsByCustomerName(QueryStructure structure, int page, int pageSize) {
        String customerName = (String) structure.getExtractedEntityValue("customer_name");
        if (customerName == null) {
            return constructErrorResponse("Customer name not found in query", 
                Arrays.asList("Try: 'show contracts for customer ABC Corp'"));
        }
        
        List<Map<String, Object>> contractData = retrieveContractsByCustomer(customerName, structure.getOperators(), page, pageSize);
        
        String title = "Contracts for Customer " + customerName;
        return constructDataResponse(title, contractData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle contracts by parts queries
     */
    private String handleContractsByParts(QueryStructure structure, int page, int pageSize) {
        String partNumber = (String) structure.getExtractedEntityValue("part_number");
        if (partNumber == null) {
            return constructErrorResponse("Part number not found in query", 
                Arrays.asList("Try: 'show contracts containing part AE12345'"));
        }
        
        List<Map<String, Object>> contractData = retrieveContractsByPart(partNumber, structure.getOperators(), page, pageSize);
        
        String title = "Contracts Containing Part " + partNumber;
        return constructDataResponse(title, contractData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle parts by user queries
     */
    private String handlePartsByUser(QueryStructure structure, int page, int pageSize) {
        String user = (String) structure.getExtractedEntityValue("created_by");
        if (user == null) {
            return constructErrorResponse("User name not found in query", 
                Arrays.asList("Try: 'show parts created by jane'"));
        }
        
        List<Map<String, Object>> partsData = retrievePartsByUser(user, structure.getOperators(), page, pageSize);
        
        String title = "Parts Created by " + user;
        return constructDataResponse(title, partsData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle parts by contract queries
     */
    private String handlePartsByContract(QueryStructure structure, int page, int pageSize) {
        String contractNumber = (String) structure.getExtractedEntityValue("contract_number");
        if (contractNumber == null) {
            return constructErrorResponse("Contract number not found in query", 
                Arrays.asList("Try: 'show parts for contract 123456'"));
        }
        
        List<Map<String, Object>> partsData = retrievePartsByContract(contractNumber, structure.getOperators(), page, pageSize);
        
        String title = "Parts in Contract " + contractNumber;
        return constructDataResponse(title, partsData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle parts by part number queries
     */
    private String handlePartsByPartNumber(QueryStructure structure, int page, int pageSize) {
        String partNumber = (String) structure.getExtractedEntityValue("part_number");
        if (partNumber == null) {
            return constructErrorResponse("Part number not found in query", 
                Arrays.asList("Try: 'show part AE12345'"));
        }
        
        List<Map<String, Object>> partsData = retrievePartsByPartNumber(partNumber, structure.getOperators(), page, pageSize);
        
        String title = "Part " + partNumber + " Details";
        return constructDataResponse(title, partsData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle parts by customer queries
     */
    private String handlePartsByCustomer(QueryStructure structure, int page, int pageSize) {
        String customerName = (String) structure.getExtractedEntityValue("customer_name");
        if (customerName == null) {
            return constructErrorResponse("Customer name not found in query", 
                Arrays.asList("Try: 'show parts for customer XYZ Inc'"));
        }
        
        List<Map<String, Object>> partsData = retrievePartsByCustomer(customerName, structure.getOperators(), page, pageSize);
        
        String title = "Parts for Customer " + customerName;
        return constructDataResponse(title, partsData, structure.getRequestedEntities(), 
                                   structure, page, pageSize);
    }
    
    /**
     * Handle help for contract creation
     */
    private String handleHelpContractCreation(QueryStructure structure) {
        String helpContent = generateContractCreationHelp();
        return constructHelpResponse("How to Create a Contract", helpContent, structure);
    }
    
    /**
     * Handle help for parts search
     */
    private String handleHelpPartsSearch(QueryStructure structure) {
        String helpContent = generatePartsSearchHelp();
        return constructHelpResponse("How to Search Parts", helpContent, structure);
    }
    
    /**
     * Handle general help
     */
    private String handleHelpGeneral(QueryStructure structure) {
        String helpContent = generateGeneralHelp();
        return constructHelpResponse("General Help", helpContent, structure);
    }
    
    // ==================== DATA RETRIEVAL SIMULATION METHODS ====================
    
    /**
     * Simulate retrieving contracts by user (replace with actual database call)
     */
    private List<Map<String, Object>> retrieveContractsByUser(String user, List<QueryOperator> operators, int page, int pageSize) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Simulate data - replace with actual database query
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> contract = new HashMap<>();
            contract.put("contract_number", "CTR" + (100000 + i));
            contract.put("customer_name", "Customer " + i);
            contract.put("account_number", "ACC" + (200000 + i));
            contract.put("created_by", user);
            contract.put("created_date", "2023-0" + (i % 9 + 1) + "-15");
            contract.put("effective_date", "2023-0" + (i % 9 + 1) + "-20");
            contract.put("expiration_date", "2024-0" + (i % 9 + 1) + "-20");
            contract.put("status", i % 3 == 0 ? "expired" : "active");
            contract.put("amount", "$" + (10000 + i * 1000));
            results.add(contract);
        }
        
        // Apply operators (simplified simulation)
        results = applyOperators(results, operators);
        
        // Apply pagination
        return applyPagination(results, page, pageSize);
    }
    
    /**
     * Simulate retrieving contracts by contract number
     */
    private List<Map<String, Object>> retrieveContractsByNumber(String contractNumber, List<QueryOperator> operators, int page, int pageSize) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Simulate single contract data
        Map<String, Object> contract = new HashMap<>();
        contract.put("contract_number", contractNumber);
        contract.put("customer_name", "ABC Corporation");
        contract.put("account_number", "ACC789012");
        contract.put("created_by", "john.smith");
        contract.put("created_date", "2023-06-15");
        contract.put("effective_date", "2023-07-01");
        contract.put("expiration_date", "2024-07-01");
        contract.put("status", "active");
        contract.put("amount", "$25,000");
        contract.put("description", "Software licensing agreement");
        results.add(contract);
        
        return applyPagination(results, page, pageSize);
    }
    
    /**
     * Simulate additional retrieval methods (similar pattern)
     */
    private List<Map<String, Object>> retrieveContractsByAccount(String accountNumber, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    private List<Map<String, Object>> retrieveContractsByCustomer(String customerName, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    private List<Map<String, Object>> retrieveContractsByPart(String partNumber, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    private List<Map<String, Object>> retrievePartsByUser(String user, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    private List<Map<String, Object>> retrievePartsByContract(String contractNumber, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    private List<Map<String, Object>> retrievePartsByPartNumber(String partNumber, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    private List<Map<String, Object>> retrievePartsByCustomer(String customerName, List<QueryOperator> operators, int page, int pageSize) {
        // Simulation - replace with actual implementation
        return new ArrayList<>();
    }
    
    /**
     * Apply query operators to filter results
     */
    private List<Map<String, Object>> applyOperators(List<Map<String, Object>> data, List<QueryOperator> operators) {
        List<Map<String, Object>> filtered = new ArrayList<>(data);
        
        for (QueryOperator operator : operators) {
            // Simplified operator application - enhance as needed
            String field = operator.getField();
            Object value = operator.getValue();
            
            switch (operator.getOperator()) {
                case EQUALS:
                    filtered.removeIf(item -> !Objects.equals(item.get(field), value));
                    break;
                case AFTER:
                    // Date comparison logic here
                    break;
                case BEFORE:
                    // Date comparison logic here
                    break;
                // Add more operator types as needed
            }
        }
        
        return filtered;
    }
    
    /**
     * Apply pagination to results
     */
    private List<Map<String, Object>> applyPagination(List<Map<String, Object>> data, int page, int pageSize) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, data.size());
        
        if (startIndex >= data.size()) {
            return new ArrayList<>();
        }
        
        return data.subList(startIndex, endIndex);
    }
    
    // ==================== RESPONSE CONSTRUCTION METHODS ====================
    
    /**
     * Construct data response with tabular format
     */
    private String constructDataResponse(String title, List<Map<String, Object>> data, 
                                       Set<String> requestedFields, QueryStructure structure, 
                                       int page, int pageSize) {
        if (data.isEmpty()) {
            return constructErrorResponse("No results found", 
                Arrays.asList("Try broadening your search criteria"));
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<div class='search-results'>");
        html.append("<h2>").append(title).append("</h2>");
        
        // Add query info
        if (structure.isHasSpellCorrections()) {
            html.append("<div class='spell-correction-info'>");
            html.append("Query corrected: ").append(structure.getOriginalQuery())
                .append(" → ").append(structure.getCorrectedQuery());
            html.append("</div>");
        }
        
        // Add results table
        html.append(constructTable(data, requestedFields));
        
        // Add pagination if needed
        html.append(constructPagination(data.size(), page, pageSize));
        
        // Add suggestions if available
        if (!structure.getSuggestions().isEmpty()) {
            html.append(constructSuggestions(structure.getSuggestions()));
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    /**
     * Construct HTML table from data
     */
    private String constructTable(List<Map<String, Object>> data, Set<String> requestedFields) {
        if (data.isEmpty()) {
            return "<p>No data to display</p>";
        }
        
        StringBuilder table = new StringBuilder();
        table.append("<table class='results-table'>");
        
        // Table header
        table.append("<thead><tr>");
        Set<String> fields = requestedFields.isEmpty() ? data.get(0).keySet() : requestedFields;
        for (String field : fields) {
            table.append("<th>").append(formatFieldName(field)).append("</th>");
        }
        table.append("</tr></thead>");
        
        // Table body
        table.append("<tbody>");
        for (Map<String, Object> row : data) {
            table.append("<tr>");
            for (String field : fields) {
                Object value = row.get(field);
                table.append("<td>").append(value != null ? value.toString() : "").append("</td>");
            }
            table.append("</tr>");
        }
        table.append("</tbody>");
        
        table.append("</table>");
        return table.toString();
    }
    
    /**
     * Construct pagination controls
     */
    private String constructPagination(int totalResults, int currentPage, int pageSize) {
        if (totalResults <= pageSize) {
            return "";
        }
        
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);
        StringBuilder pagination = new StringBuilder();
        
        pagination.append("<div class='pagination'>");
        pagination.append("Page ").append(currentPage).append(" of ").append(totalPages);
        pagination.append(" (").append(totalResults).append(" total results)");
        // Add actual pagination controls here
        pagination.append("</div>");
        
        return pagination.toString();
    }
    
    /**
     * Construct error response
     */
    private String constructErrorResponse(String errorMessage, List<String> suggestions) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='error-response'>");
        html.append("<h2>Error</h2>");
        html.append("<p class='error-message'>").append(errorMessage).append("</p>");
        
        if (suggestions != null && !suggestions.isEmpty()) {
            html.append(constructSuggestions(suggestions));
        }
        
        html.append("</div>");
        return html.toString();
    }
    
    /**
     * Construct help response
     */
    private String constructHelpResponse(String title, String content, QueryStructure structure) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='help-response'>");
        html.append("<h2>").append(title).append("</h2>");
        html.append("<div class='help-content'>").append(content).append("</div>");
        html.append("</div>");
        return html.toString();
    }
    
    /**
     * Construct suggestions section
     */
    private String constructSuggestions(List<String> suggestions) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='suggestions'>");
        html.append("<h3>Suggestions:</h3>");
        html.append("<ul>");
        for (String suggestion : suggestions) {
            html.append("<li>").append(suggestion).append("</li>");
        }
        html.append("</ul>");
        html.append("</div>");
        return html.toString();
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Format field names for display
     */
    private String formatFieldName(String fieldName) {
        return fieldName.replace("_", " ")
                       .substring(0, 1).toUpperCase() + 
                       fieldName.replace("_", " ").substring(1).toLowerCase();
    }
    
    /**
     * Generate contract creation help content
     */
    private String generateContractCreationHelp() {
        return "<ol>" +
               "<li>Navigate to Contract Creation page</li>" +
               "<li>Fill in required fields: Customer Name, Account Number</li>" +
               "<li>Set effective and expiration dates</li>" +
               "<li>Add contract terms and conditions</li>" +
               "<li>Submit for approval</li>" +
               "</ol>";
    }
    
    /**
     * Generate parts search help content
     */
    private String generatePartsSearchHelp() {
        return "<ul>" +
               "<li>Search by part number: 'show part AE12345'</li>" +
               "<li>Search by contract: 'list parts for contract 123456'</li>" +
               "<li>Search by user: 'parts created by jane'</li>" +
               "<li>Search by customer: 'parts for customer ABC Corp'</li>" +
               "</ul>";
    }
    
    /**
     * Generate general help content
     */
    private String generateGeneralHelp() {
        return "<h3>Available Commands:</h3>" +
               "<ul>" +
               "<li><strong>Contracts:</strong> show contract 123456, contracts by user john</li>" +
               "<li><strong>Parts:</strong> list parts for contract 123456, part AE12345</li>" +
               "<li><strong>Help:</strong> help create contract, how to search parts</li>" +
               "</ul>";
    }
    
    /**
     * Initialize HTML templates
     */
    private void initializeTemplates() {
        // Initialize templates for consistent formatting
        htmlTemplate = "<!DOCTYPE html><html><body>%s</body></html>";
        tableRowTemplate = "<tr>%s</tr>";
        errorTemplate = "<div class='error'>%s</div>";
        helpTemplate = "<div class='help'>%s</div>";
    }
    
    // Configuration getters and setters
    public int getDefaultPageSize() { return defaultPageSize; }
    public void setDefaultPageSize(int defaultPageSize) { this.defaultPageSize = defaultPageSize; }
    
    public boolean isHighlightCorrections() { return highlightCorrections; }
    public void setHighlightCorrections(boolean highlightCorrections) { this.highlightCorrections = highlightCorrections; }
}
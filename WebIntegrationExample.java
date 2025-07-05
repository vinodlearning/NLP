import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Web Integration Example
 * 
 * This example demonstrates how to integrate the processQueryJSON functionality
 * into a web application using servlets, REST APIs, or other web frameworks.
 */
public class WebIntegrationExample {
    
    /**
     * Example 1: Simple Servlet Integration
     */
    public static class ContractQueryServlet extends HttpServlet {
        private EnhancedMLController controller;
        
        @Override
        public void init() throws ServletException {
            controller = new EnhancedMLController();
        }
        
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {
            
            // Set response content type
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Get user query from request
            String userQuery = request.getParameter("query");
            
            if (userQuery == null || userQuery.trim().isEmpty()) {
                // Return error JSON
                response.getWriter().write("{\"error\": \"Query parameter is required\"}");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            try {
                // Process query and get JSON response
                String jsonResponse = controller.processQueryJSON(userQuery);
                
                // Write JSON response
                response.getWriter().write(jsonResponse);
                response.setStatus(HttpServletResponse.SC_OK);
                
            } catch (Exception e) {
                // Handle errors
                String errorJson = "{\"error\": \"Processing failed: " + e.getMessage() + "\"}";
                response.getWriter().write(errorJson);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
    
    /**
     * Example 2: REST API Controller (Spring Boot style)
     */
    public static class ContractQueryRestController {
        private EnhancedMLController controller = new EnhancedMLController();
        
        // @PostMapping("/api/query")
        // @ResponseBody
        public String processQuery(String query) {
            try {
                return controller.processQueryJSON(query);
            } catch (Exception e) {
                return "{\"error\": \"Processing failed: " + e.getMessage() + "\"}";
            }
        }
        
        // @GetMapping("/api/contract/{id}")
        // @ResponseBody
        public String getContract(String contractId) {
            String query = "show contract " + contractId;
            return processQuery(query);
        }
        
        // @GetMapping("/api/parts/{contractId}")
        // @ResponseBody
        public String getPartsForContract(String contractId) {
            String query = "show all parts for contract " + contractId;
            return processQuery(query);
        }
        
        // @GetMapping("/api/help")
        // @ResponseBody
        public String getHelp() {
            String query = "help me create a contract";
            return processQuery(query);
        }
    }
    
    /**
     * Example 3: JAX-RS REST Service
     */
    public static class ContractQueryResource {
        private EnhancedMLController controller = new EnhancedMLController();
        
        // @POST
        // @Path("/query")
        // @Consumes(MediaType.APPLICATION_JSON)
        // @Produces(MediaType.APPLICATION_JSON)
        public String processQuery(QueryRequest request) {
            return controller.processQueryJSON(request.getQuery());
        }
        
        // @GET
        // @Path("/contract/{id}")
        // @Produces(MediaType.APPLICATION_JSON)
        public String getContract(String id) {
            return controller.processQueryJSON("show contract " + id);
        }
    }
    
    /**
     * Example 4: WebSocket Integration
     */
    public static class ContractQueryWebSocket {
        private EnhancedMLController controller = new EnhancedMLController();
        
        // @OnMessage
        public void onMessage(String message) {
            try {
                // Parse incoming message (could be JSON)
                String query = parseQuery(message);
                
                // Process query
                String jsonResponse = controller.processQueryJSON(query);
                
                // Send response back to client
                sendResponse(jsonResponse);
                
            } catch (Exception e) {
                sendError("Processing failed: " + e.getMessage());
            }
        }
        
        private String parseQuery(String message) {
            // Simple parsing - in real implementation, parse JSON
            return message;
        }
        
        private void sendResponse(String response) {
            // Send JSON response to WebSocket client
            System.out.println("Sending: " + response);
        }
        
        private void sendError(String error) {
            String errorJson = "{\"error\": \"" + error + "\"}";
            System.out.println("Error: " + errorJson);
        }
    }
    
    /**
     * Example 5: Microservice Integration
     */
    public static class ContractQueryMicroservice {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Process query and return JSON for microservice communication
         */
        public String processServiceQuery(String query, String serviceId) {
            try {
                String jsonResponse = controller.processQueryJSON(query);
                
                // Add service metadata
                jsonResponse = addServiceMetadata(jsonResponse, serviceId);
                
                return jsonResponse;
                
            } catch (Exception e) {
                return createServiceError(e.getMessage(), serviceId);
            }
        }
        
        private String addServiceMetadata(String jsonResponse, String serviceId) {
            // Add service-specific metadata to JSON
            // This is a simplified example - in real implementation, parse and modify JSON
            return jsonResponse.replace("}", 
                ", \"serviceMetadata\": {" +
                "\"serviceId\": \"" + serviceId + "\"," +
                "\"version\": \"1.0\"," +
                "\"processedAt\": " + System.currentTimeMillis() +
                "}}");
        }
        
        private String createServiceError(String error, String serviceId) {
            return "{" +
                "\"error\": \"" + error + "\"," +
                "\"serviceMetadata\": {" +
                "\"serviceId\": \"" + serviceId + "\"," +
                "\"version\": \"1.0\"," +
                "\"processedAt\": " + System.currentTimeMillis() +
                "}}";
        }
    }
    
    /**
     * Example 6: Frontend Integration Helper
     */
    public static class FrontendIntegrationHelper {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Process query and return formatted JSON for frontend consumption
         */
        public String processForFrontend(String query) {
            String jsonResponse = controller.processQueryJSON(query);
            
            // Add frontend-specific enhancements
            return enhanceForFrontend(jsonResponse);
        }
        
        private String enhanceForFrontend(String jsonResponse) {
            // Add frontend-specific metadata
            // This could include UI hints, display formatting, etc.
            return jsonResponse.replace("}", 
                ", \"ui\": {" +
                "\"displayFormat\": \"table\"," +
                "\"sortable\": true," +
                "\"exportable\": true," +
                "\"refreshable\": true" +
                "}}");
        }
    }
    
    /**
     * Supporting classes for examples
     */
    public static class QueryRequest {
        private String query;
        
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
    }
    
    /**
     * Main method to demonstrate usage
     */
    public static void main(String[] args) {
        System.out.println("🌐 Web Integration Examples");
        System.out.println("==========================\n");
        
        // Example 1: REST Controller
        System.out.println("📡 REST API Example:");
        ContractQueryRestController restController = new ContractQueryRestController();
        String contractResponse = restController.getContract("124563");
        System.out.println("Contract API Response: " + contractResponse.substring(0, 100) + "...");
        
        // Example 2: Microservice
        System.out.println("\n🔧 Microservice Example:");
        ContractQueryMicroservice microservice = new ContractQueryMicroservice();
        String serviceResponse = microservice.processServiceQuery("show contract 124563", "contract-service-1");
        System.out.println("Service Response: " + serviceResponse.substring(0, 100) + "...");
        
        // Example 3: Frontend Helper
        System.out.println("\n💻 Frontend Integration Example:");
        FrontendIntegrationHelper frontendHelper = new FrontendIntegrationHelper();
        String frontendResponse = frontendHelper.processForFrontend("show all parts for contract 123456");
        System.out.println("Frontend Response: " + frontendResponse.substring(0, 100) + "...");
        
        System.out.println("\n✅ All integration examples completed successfully!");
    }
}

/**
 * Usage Instructions:
 * 
 * 1. Servlet Integration:
 *    - Deploy ContractQueryServlet in your web application
 *    - Send POST requests to the servlet with 'query' parameter
 *    - Receive JSON responses directly
 * 
 * 2. Spring Boot Integration:
 *    - Add @RestController annotation to ContractQueryRestController
 *    - Use @PostMapping, @GetMapping annotations as shown in comments
 *    - Spring Boot will automatically handle JSON serialization
 * 
 * 3. JAX-RS Integration:
 *    - Add JAX-RS annotations as shown in comments
 *    - Deploy in a JAX-RS compatible container
 *    - Access via REST endpoints
 * 
 * 4. WebSocket Integration:
 *    - Add @ServerEndpoint annotation to ContractQueryWebSocket
 *    - Handle real-time queries and responses
 *    - Useful for interactive applications
 * 
 * 5. Microservice Integration:
 *    - Use ContractQueryMicroservice for service-to-service communication
 *    - Add service metadata for tracing and monitoring
 *    - Integrate with service discovery and load balancing
 * 
 * 6. Frontend Integration:
 *    - Use FrontendIntegrationHelper for UI-specific enhancements
 *    - Add display hints and formatting information
 *    - Optimize for frontend consumption
 */
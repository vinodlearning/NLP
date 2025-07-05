/**
 * Simple Web Integration Example
 * 
 * This example demonstrates how to integrate the processQueryJSON functionality
 * into web applications without external dependencies.
 */
public class SimpleWebIntegrationExample {
    
    /**
     * Example 1: Simple HTTP Handler
     */
    public static class ContractQueryHandler {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Handle HTTP request and return JSON response
         */
        public String handleRequest(String query, String method) {
            try {
                // Validate input
                if (query == null || query.trim().isEmpty()) {
                    return createErrorResponse("Query parameter is required", 400);
                }
                
                // Process query
                String jsonResponse = controller.processQueryJSON(query);
                
                // Add HTTP metadata
                return addHttpMetadata(jsonResponse, 200, "OK");
                
            } catch (Exception e) {
                return createErrorResponse("Processing failed: " + e.getMessage(), 500);
            }
        }
        
        private String createErrorResponse(String error, int statusCode) {
            return "{" +
                "\"error\": \"" + error + "\"," +
                "\"statusCode\": " + statusCode + "," +
                "\"timestamp\": " + System.currentTimeMillis() +
                "}";
        }
        
        private String addHttpMetadata(String jsonResponse, int statusCode, String status) {
            // Add HTTP metadata to JSON response
            return jsonResponse.replace("}", 
                ", \"httpMetadata\": {" +
                "\"statusCode\": " + statusCode + "," +
                "\"status\": \"" + status + "\"," +
                "\"contentType\": \"application/json\"" +
                "}}");
        }
    }
    
    /**
     * Example 2: REST API Simulator
     */
    public static class RestAPISimulator {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Simulate GET /api/contract/{id}
         */
        public String getContract(String contractId) {
            String query = "show contract " + contractId;
            return processAPIRequest(query, "GET", "/api/contract/" + contractId);
        }
        
        /**
         * Simulate GET /api/parts/{contractId}
         */
        public String getPartsForContract(String contractId) {
            String query = "show all parts for contract " + contractId;
            return processAPIRequest(query, "GET", "/api/parts/" + contractId);
        }
        
        /**
         * Simulate POST /api/query
         */
        public String processQuery(String query) {
            return processAPIRequest(query, "POST", "/api/query");
        }
        
        /**
         * Simulate GET /api/help
         */
        public String getHelp() {
            String query = "help me create a contract";
            return processAPIRequest(query, "GET", "/api/help");
        }
        
        private String processAPIRequest(String query, String method, String endpoint) {
            try {
                String jsonResponse = controller.processQueryJSON(query);
                
                // Add API metadata
                return addAPIMetadata(jsonResponse, method, endpoint);
                
            } catch (Exception e) {
                return createAPIError(e.getMessage(), method, endpoint);
            }
        }
        
        private String addAPIMetadata(String jsonResponse, String method, String endpoint) {
            return jsonResponse.replace("}", 
                ", \"apiMetadata\": {" +
                "\"method\": \"" + method + "\"," +
                "\"endpoint\": \"" + endpoint + "\"," +
                "\"version\": \"v1\"," +
                "\"requestId\": \"req-" + System.currentTimeMillis() + "\"" +
                "}}");
        }
        
        private String createAPIError(String error, String method, String endpoint) {
            return "{" +
                "\"error\": \"" + error + "\"," +
                "\"apiMetadata\": {" +
                "\"method\": \"" + method + "\"," +
                "\"endpoint\": \"" + endpoint + "\"," +
                "\"version\": \"v1\"," +
                "\"requestId\": \"req-" + System.currentTimeMillis() + "\"" +
                "}}";
        }
    }
    
    /**
     * Example 3: WebSocket Message Handler
     */
    public static class WebSocketMessageHandler {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Handle WebSocket message and return JSON response
         */
        public String handleMessage(String message, String sessionId) {
            try {
                // Parse message (could be JSON or plain text)
                String query = parseMessage(message);
                
                // Process query
                String jsonResponse = controller.processQueryJSON(query);
                
                // Add WebSocket metadata
                return addWebSocketMetadata(jsonResponse, sessionId);
                
            } catch (Exception e) {
                return createWebSocketError(e.getMessage(), sessionId);
            }
        }
        
        private String parseMessage(String message) {
            // Simple parsing - in real implementation, could parse JSON
            if (message.startsWith("{") && message.endsWith("}")) {
                // Assume JSON format: {"query": "show contract 123"}
                int start = message.indexOf("\"query\":");
                if (start != -1) {
                    start = message.indexOf("\"", start + 8);
                    int end = message.indexOf("\"", start + 1);
                    if (start != -1 && end != -1) {
                        return message.substring(start + 1, end);
                    }
                }
            }
            
            // Return as-is if not JSON
            return message;
        }
        
        private String addWebSocketMetadata(String jsonResponse, String sessionId) {
            return jsonResponse.replace("}", 
                ", \"websocketMetadata\": {" +
                "\"sessionId\": \"" + sessionId + "\"," +
                "\"messageType\": \"response\"," +
                "\"timestamp\": " + System.currentTimeMillis() +
                "}}");
        }
        
        private String createWebSocketError(String error, String sessionId) {
            return "{" +
                "\"error\": \"" + error + "\"," +
                "\"websocketMetadata\": {" +
                "\"sessionId\": \"" + sessionId + "\"," +
                "\"messageType\": \"error\"," +
                "\"timestamp\": " + System.currentTimeMillis() +
                "}}";
        }
    }
    
    /**
     * Example 4: Microservice Communication
     */
    public static class MicroserviceCommunicator {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Process request from another microservice
         */
        public String processServiceRequest(String query, String sourceService, String targetService) {
            try {
                String jsonResponse = controller.processQueryJSON(query);
                
                // Add microservice metadata
                return addMicroserviceMetadata(jsonResponse, sourceService, targetService);
                
            } catch (Exception e) {
                return createMicroserviceError(e.getMessage(), sourceService, targetService);
            }
        }
        
        private String addMicroserviceMetadata(String jsonResponse, String sourceService, String targetService) {
            return jsonResponse.replace("}", 
                ", \"microserviceMetadata\": {" +
                "\"sourceService\": \"" + sourceService + "\"," +
                "\"targetService\": \"" + targetService + "\"," +
                "\"correlationId\": \"corr-" + System.currentTimeMillis() + "\"," +
                "\"version\": \"1.0\"," +
                "\"processedAt\": " + System.currentTimeMillis() +
                "}}");
        }
        
        private String createMicroserviceError(String error, String sourceService, String targetService) {
            return "{" +
                "\"error\": \"" + error + "\"," +
                "\"microserviceMetadata\": {" +
                "\"sourceService\": \"" + sourceService + "\"," +
                "\"targetService\": \"" + targetService + "\"," +
                "\"correlationId\": \"corr-" + System.currentTimeMillis() + "\"," +
                "\"version\": \"1.0\"," +
                "\"processedAt\": " + System.currentTimeMillis() +
                "}}";
        }
    }
    
    /**
     * Example 5: Frontend API Helper
     */
    public static class FrontendAPIHelper {
        private EnhancedMLController controller = new EnhancedMLController();
        
        /**
         * Process query and return frontend-optimized JSON
         */
        public String processForFrontend(String query, String userId, String sessionId) {
            try {
                String jsonResponse = controller.processQueryJSON(query);
                
                // Add frontend-specific enhancements
                return enhanceForFrontend(jsonResponse, userId, sessionId);
                
            } catch (Exception e) {
                return createFrontendError(e.getMessage(), userId, sessionId);
            }
        }
        
        private String enhanceForFrontend(String jsonResponse, String userId, String sessionId) {
            return jsonResponse.replace("}", 
                ", \"frontendMetadata\": {" +
                "\"userId\": \"" + userId + "\"," +
                "\"sessionId\": \"" + sessionId + "\"," +
                "\"displayHints\": {" +
                "\"format\": \"table\"," +
                "\"sortable\": true," +
                "\"filterable\": true," +
                "\"exportable\": true" +
                "}," +
                "\"uiActions\": [\"refresh\", \"export\", \"share\"]" +
                "}}");
        }
        
        private String createFrontendError(String error, String userId, String sessionId) {
            return "{" +
                "\"error\": \"" + error + "\"," +
                "\"frontendMetadata\": {" +
                "\"userId\": \"" + userId + "\"," +
                "\"sessionId\": \"" + sessionId + "\"," +
                "\"displayHints\": {" +
                "\"showError\": true," +
                "\"errorType\": \"processing\"" +
                "}" +
                "}}";
        }
    }
    
    /**
     * Main method to demonstrate all integration examples
     */
    public static void main(String[] args) {
        System.out.println("🌐 Simple Web Integration Examples");
        System.out.println("==================================\n");
        
        // Example 1: HTTP Handler
        System.out.println("🔧 HTTP Handler Example:");
        ContractQueryHandler httpHandler = new ContractQueryHandler();
        String httpResponse = httpHandler.handleRequest("show contract 124563", "GET");
        System.out.println("HTTP Response: " + httpResponse.substring(0, 150) + "...\n");
        
        // Example 2: REST API Simulator
        System.out.println("📡 REST API Simulator Example:");
        RestAPISimulator restAPI = new RestAPISimulator();
        String contractResponse = restAPI.getContract("124563");
        System.out.println("Contract API: " + contractResponse.substring(0, 150) + "...");
        
        String partsResponse = restAPI.getPartsForContract("123456");
        System.out.println("Parts API: " + partsResponse.substring(0, 150) + "...\n");
        
        // Example 3: WebSocket Handler
        System.out.println("🔌 WebSocket Handler Example:");
        WebSocketMessageHandler wsHandler = new WebSocketMessageHandler();
        String wsResponse = wsHandler.handleMessage("help me create a contract", "session-123");
        System.out.println("WebSocket Response: " + wsResponse.substring(0, 150) + "...\n");
        
        // Example 4: Microservice Communication
        System.out.println("🔧 Microservice Communication Example:");
        MicroserviceCommunicator microservice = new MicroserviceCommunicator();
        String serviceResponse = microservice.processServiceRequest(
            "show all parts for contract 123456", 
            "frontend-service", 
            "contract-service"
        );
        System.out.println("Microservice Response: " + serviceResponse.substring(0, 150) + "...\n");
        
        // Example 5: Frontend API Helper
        System.out.println("💻 Frontend API Helper Example:");
        FrontendAPIHelper frontendAPI = new FrontendAPIHelper();
        String frontendResponse = frontendAPI.processForFrontend(
            "effective date,expiration,price expiration,projecttype for contract 124563",
            "user-456",
            "session-789"
        );
        System.out.println("Frontend Response: " + frontendResponse.substring(0, 150) + "...\n");
        
        System.out.println("✅ All integration examples completed successfully!");
        
        // Demonstrate error handling
        System.out.println("\n❌ Error Handling Examples:");
        String errorResponse = httpHandler.handleRequest("", "GET");
        System.out.println("Empty Query Error: " + errorResponse);
        
        String notFoundResponse = restAPI.getContract("999999");
        System.out.println("Not Found Error: " + notFoundResponse.substring(0, 150) + "...");
    }
}

/**
 * Integration Guide:
 * 
 * 1. HTTP Handler:
 *    - Use ContractQueryHandler for simple HTTP request processing
 *    - Handles validation, error responses, and metadata
 *    - Can be integrated into any HTTP server framework
 * 
 * 2. REST API Simulator:
 *    - Demonstrates RESTful endpoint patterns
 *    - Shows how to create resource-specific methods
 *    - Includes API metadata for monitoring and debugging
 * 
 * 3. WebSocket Handler:
 *    - Handles real-time message processing
 *    - Supports both JSON and plain text messages
 *    - Includes session management capabilities
 * 
 * 4. Microservice Communication:
 *    - Service-to-service communication patterns
 *    - Includes correlation IDs for distributed tracing
 *    - Supports request routing and load balancing
 * 
 * 5. Frontend API Helper:
 *    - Optimized for frontend consumption
 *    - Includes UI hints and display metadata
 *    - Supports user context and session management
 */
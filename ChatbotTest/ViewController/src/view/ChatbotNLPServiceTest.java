package view;



public class ChatbotNLPServiceTest {
    
    public static void main(String[] args) {
       // ChatbotNLPService chatbot = new ChatbotNLPService();
       EnhancedNLPProcessor processor = new EnhancedNLPProcessor();
               SecurityService security = new SecurityService();
               
        // Test various queries
        String[] testQueries = {
            // Contract queries
            "show contract 123456",
            "contract details 123456", 
            "get contract info 123456",
            "find contract 123456",
            "john contracts",
            "contracts by smith",
            "contracts created by mary",
            
            // Status queries
            "status of 123456",
            "expired contracts",
            "active contracts",
            
            // Customer queries
            "boeing contracts",
            "customer honeywell",
            "account 10840607",
            
            // Parts queries
            "List out the contracts associated with part number AE125",
            "What are the specifications of product ID AE125?",
            "Is part number AE125 still active or discontinued?",
            "Can you provide the datasheet for AE125?",
            "What are the compatible parts for AE125?",
            "Is AE125 available in stock?",
            "What is the lead time for part AE125?",
            "Who is the manufacturer of AE125?",
            "Are there any known issues or defects with AE125?",
            "What is the warranty period for AE125?",
            
            // Parts by contract
            "show me the parts 123456",
            "list the parts of 123456",
            "how many parts for 123456",
            
            // Failed parts queries
            "failed parts of 123456",
            "filed parts 123456",
            "failed parts",
            
            // Help queries
            "create contract",
            "show me how to create contract",
            "I want to create a contract",
            "help create contract",
            
            // Typo examples
            "cntrs 123456 shw me",
            "contarct 123456",
            "pasrt AE125 info",
            "filed parts of 123456"
        };
        
        System.out.println("=== ChatBot NLP Service Test ===\n");
        
        for (String query : testQueries) {
            System.out.println("Query: " + query);
            // Process the query
            
            // Parse the query
            //ParsedQuery parsed = chatbot.parseQuery(query);
            //System.out.println("Parsed: " + parsed);
            ParsedQuery result = processor.processQuery(query);
            System.out.println("Query Type: " + result.getQueryType());
            System.out.println("Action Type: " + result.getActionType());
            System.out.println("Confidence: " + result.getConfidence());
            System.out.println("Contract: " + result.getContractNumber());
            System.out.println("Part: " + result.getPartNumber());
            System.out.println("Account: " + result.getAccountNumner());
            System.out.println("User: " + result.getUserName());
            // Test security
                      boolean hasAccess = security.hasAccess("testuser", result.getQueryType());
                      System.out.println("Access Granted: " + hasAccess);
            
//            String response = chatbot.processQuery(query);
//            System.out.println("Response: " + response);
//            System.out.println("Confidence: " + String.format("%.2f", parsed.getConfidence()));
//            System.out.println("---");
        }
    }
}



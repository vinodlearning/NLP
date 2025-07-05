public class BasicIntegrationExample {
    public static void main(String[] args) {
        // Test queries
        String[] queries = {
            "show contract 123456",
            "list parts for contract 789012",
            "create new contract",
            "effective date for contract ABC-123"
        };
        
        for (String query : queries) {
            System.out.println("Query: " + query);
            
            // Process with Structured Query Processor
            StructuredQueryResponse response = StructuredQueryProcessor.processQuery(query);
            
            System.out.println("Response: " + response.toJson());
            System.out.println("Analysis: " + response.getAnalysisSummary());
            System.out.println("---");
        }
    }
}

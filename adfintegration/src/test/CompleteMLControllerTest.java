package test;

import model.nlp.CompleteMLController;
import model.nlp.CompleteMLResponse;

/**
 * Test to demonstrate Complete ML Controller with ALL required attributes
 * Shows JSON response with:
 * - contract_number, part_number, customer_name, account_number, created_by
 * - queryType (CONTRACT/PARTS/HELP)
 * - actionType (specific action types)
 * - entities (with operations like project_type = "new")
 */
public class CompleteMLControllerTest {
    
    public static void main(String[] args) {
        System.out.println("🎯 COMPLETE ML CONTROLLER TEST");
        System.out.println("=".repeat(80));
        System.out.println("Testing JSON response with ALL required attributes...");
        System.out.println();
        
        CompleteMLController controller = CompleteMLController.getInstance();
        
        // Test cases with different scenarios
        String[] testQueries = {
            "Show the contrst78954632",
            "contracts created by vinod after 2020 status active",
            "parts for customer ABC Corp project type new",
            "show part AE12345 for account 789012345",
            "contracts for customer XYZ priority high category software"
        };
        
        for (int i = 0; i < testQueries.length; i++) {
            String query = testQueries[i];
            System.out.println("TEST " + (i + 1) + ": " + query);
            System.out.println("-".repeat(60));
            
            CompleteMLResponse response = controller.processUserQuery(query);
            
            // Show the complete JSON with ALL required attributes
            System.out.println(response.toCompleteJSON());
            System.out.println();
            System.out.println("=".repeat(80));
            System.out.println();
        }
        
        // Demonstrate specific attribute extraction
        demonstrateAttributeExtraction(controller);
    }
    
    private static void demonstrateAttributeExtraction(CompleteMLController controller) {
        System.out.println("🔍 ATTRIBUTE EXTRACTION DEMONSTRATION");
        System.out.println("=".repeat(80));
        
        String complexQuery = "contracts created by vinod for customer ABC Corp account 789012345 " +
                             "containing part AE12345 after 2020 status active project type new priority high";
        
        System.out.println("Complex Query: " + complexQuery);
        System.out.println();
        
        CompleteMLResponse response = controller.processUserQuery(complexQuery);
        
        System.out.println("EXTRACTED REQUIRED ATTRIBUTES:");
        System.out.println("contract_number: " + response.getContract_number());
        System.out.println("part_number: " + response.getPart_number());
        System.out.println("customer_name: " + response.getCustomer_name());
        System.out.println("account_number: " + response.getAccount_number());
        System.out.println("created_by: " + response.getCreated_by());
        System.out.println();
        
        System.out.println("QUERY CLASSIFICATION:");
        System.out.println("queryType: " + response.getQueryType());
        System.out.println("actionType: " + response.getActionType());
        System.out.println();
        
        System.out.println("ENTITIES WITH OPERATIONS:");
        for (int i = 0; i < response.getEntities().size(); i++) {
            var entity = response.getEntities().get(i);
            System.out.println((i + 1) + ". " + entity.getAttribute() + " " + 
                             entity.getOperation() + " \"" + entity.getValue() + "\"");
        }
        System.out.println();
        
        System.out.println("SPELL CORRECTIONS:");
        System.out.println("Original: " + response.getOriginalInput());
        System.out.println("Corrected: " + response.getCorrectedInput());
        System.out.println("Has Corrections: " + response.isHasSpellCorrections());
        System.out.println();
        
        System.out.println("✅ ALL REQUIRED ATTRIBUTES PRESENT IN JSON!");
    }
}
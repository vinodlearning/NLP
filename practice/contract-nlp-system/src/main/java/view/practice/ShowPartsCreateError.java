import java.util.*;

/**
 * Demo the complete JSON response for parts creation error
 */
public class ShowPartsCreateError {
    
    public static void main(String[] args) {
        System.out.println("=== PARTS CREATION ERROR RESPONSE DEMO ===\n");
        
        String userInput = "create pasrt";  // With typo to show spell correction
        
        System.out.println("🔍 USER INPUT: \"" + userInput + "\"");
        System.out.println("✏️  SPELL CORRECTED: \"create part\"");
        System.out.println("🎯 ROUTING DECISION: PARTS_CREATE_ERROR");
        System.out.println("💡 BUSINESS RULE: Parts creation not supported\n");
        
        System.out.println("📋 COMPLETE JSON RESPONSE:");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        String response = generatePartsCreateErrorResponse("create part");
        System.out.println(response);
        
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    private static String generatePartsCreateErrorResponse(String input) {
        return "{\n" +
               "  \"responseType\": \"BUSINESS_RULE_ERROR\",\n" +
               "  \"message\": \"Parts creation is not supported by the system\",\n" +
               "  \"query\": \"" + input + "\",\n" +
               "  \"errorDetails\": {\n" +
               "    \"reason\": \"Parts are managed through bulk loading process\",\n" +
               "    \"explanation\": \"The system does not support individual parts creation. Parts data is loaded in bulk from Excel files into database tables.\",\n" +
               "    \"supportedOperations\": [\n" +
               "      \"View existing parts\",\n" +
               "      \"Search parts by contract\",\n" +
               "      \"Check parts status\",\n" +
               "      \"Generate parts reports\"\n" +
               "    ],\n" +
               "    \"alternativeActions\": [\n" +
               "      {\n" +
               "        \"action\": \"Load Parts from Excel\",\n" +
               "        \"description\": \"Use the Parts Loading screen to upload Excel files\",\n" +
               "        \"screen\": \"Parts Management → Load from Excel\",\n" +
               "        \"instructions\": \"Navigate to Parts screen and use the Excel upload feature\"\n" +
               "      },\n" +
               "      {\n" +
               "        \"action\": \"Create Contract Instead\",\n" +
               "        \"description\": \"If you need to create a contract, use the contract creation workflow\",\n" +
               "        \"screen\": \"Contract Management → Create New Contract\",\n" +
               "        \"instructions\": \"Use 'create contract' or 'help me create contract' commands\"\n" +
               "      },\n" +
               "      {\n" +
               "        \"action\": \"View Existing Parts\",\n" +
               "        \"description\": \"Browse or search existing parts in the system\",\n" +
               "        \"screen\": \"Parts Management → View Parts\",\n" +
               "        \"instructions\": \"Use queries like 'show parts for contract [ID]' or 'list all parts'\"\n" +
               "      }\n" +
               "    ]\n" +
               "  },\n" +
               "  \"suggestedQueries\": [\n" +
               "    \"show parts for contract [contract_id]\",\n" +
               "    \"list all parts\",\n" +
               "    \"how to create contract\",\n" +
               "    \"parts loading process\",\n" +
               "    \"help me create contract\"\n" +
               "  ],\n" +
               "  \"routingInfo\": {\n" +
               "    \"targetModel\": \"PARTS_CREATE_ERROR\",\n" +
               "    \"intentType\": \"BUSINESS_RULE_VIOLATION\",\n" +
               "    \"routingReason\": \"Parts creation not supported - parts are loaded from Excel files\",\n" +
               "    \"businessRule\": \"Only contracts can be created. Parts are bulk-loaded from Excel.\",\n" +
               "    \"timestamp\": \"" + new Date().toString() + "\"\n" +
               "  }\n" +
               "}";
    }
}
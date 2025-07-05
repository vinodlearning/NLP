package com.company.contracts.enhanced;

/**
 * Test specific failed cases from Failed_test_cases.md
 */
public class TestFailedCases {
    
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        
        System.out.println("🧪 TESTING SPECIFIC FAILED CASES FROM Failed_test_cases.md");
        System.out.println("=" + "=".repeat(70));
        
        // Test the specific failed cases mentioned in the document
        String[] failedCases = {
            "acc number 1084",
            "AE125_validation-fail", 
            "customer897654contracts",
            "contractSiemensunderaccount",
            "customernumber123456contract",
            "contractAE125parts",
            "contract456789status",
            "AE125_valid-fail",
            "contract123;parts456",
            "contract 123?parts=AE125",
            "AE125|contract123",
            "contract123sumry",
            "AE125...contract123..."
        };
        
        for (String testCase : failedCases) {
            System.out.printf("\n🔍 TESTING: \"%s\"\n", testCase);
            System.out.println("-".repeat(50));
            
            String result = processor.processQuery(testCase);
            
            // Check if the query now processes without blocker errors
            boolean hasBlockerError = result.contains("\"severity\": \"BLOCKER\"");
            boolean hasValidData = result.contains("\"contractNumber\":") || 
                                  result.contains("\"partNumber\":") || 
                                  result.contains("\"customerNumber\":");
            
            if (!hasBlockerError) {
                System.out.println("✅ NO BLOCKER ERRORS - Query processed successfully");
                
                // Show extracted data
                if (result.contains("\"contractNumber\": \"")) {
                    String contractNum = extractJsonValue(result, "contractNumber");
                    System.out.println("📋 Contract Number: " + contractNum);
                }
                if (result.contains("\"partNumber\": \"")) {
                    String partNum = extractJsonValue(result, "partNumber");
                    System.out.println("📋 Part Number: " + partNum);
                }
                if (result.contains("\"customerNumber\": \"")) {
                    String customerNum = extractJsonValue(result, "customerNumber");
                    System.out.println("📋 Customer Number: " + customerNum);
                }
                if (result.contains("\"correctedInput\": \"")) {
                    String corrected = extractJsonValue(result, "correctedInput");
                    System.out.println("📋 Spell Corrected: " + corrected);
                }
            } else {
                System.out.println("❌ STILL HAS BLOCKER ERRORS");
                // Show the error
                if (result.contains("\"message\":")) {
                    String errorMsg = extractJsonValue(result, "message");
                    System.out.println("📋 Error: " + errorMsg);
                }
            }
        }
        
        System.out.println("\n🎉 TESTING COMPLETE!");
        System.out.println("Fixed cases should now process without blocker errors.");
    }
    
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\": ";
        int start = json.indexOf(pattern);
        if (start == -1) return "null";
        
        start += pattern.length();
        if (json.charAt(start) == 'n') return "null";
        
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("\n", start);
        if (end == -1) end = json.length();
        
        String value = json.substring(start, end).trim();
        return value.replace("\"", "");
    }
}
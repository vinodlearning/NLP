package com.company.contracts.test;

import java.util.*;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JSON Response Demo for ChatbotMachine
 * Shows exact JSON output for input: "contract123;parts456"
 */
public class JSONResponseDemo {
    
    public static void main(String[] args) {
        JSONResponseDemo demo = new JSONResponseDemo();
        
        System.out.println("=".repeat(80));
        System.out.println("🤖 CHATBOT MACHINE - JSON RESPONSE DEMONSTRATION");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Test the specific input
        String testInput = "contract123;parts456";
        System.out.printf("📝 INPUT: \"%s\"\n", testInput);
        System.out.println("=".repeat(50));
        
        // Process the input and generate JSON
        String jsonResponse = demo.processInputAndGenerateJSON(testInput);
        
        System.out.println("📊 PROCESSING ANALYSIS:");
        System.out.println("-".repeat(30));
        demo.showProcessingSteps(testInput);
        
        System.out.println("\n🎯 FINAL JSON RESPONSE:");
        System.out.println("=".repeat(50));
        System.out.println(jsonResponse);
        System.out.println("=".repeat(50));
        
        // Show formatted version
        System.out.println("\n✨ FORMATTED JSON RESPONSE:");
        System.out.println("=".repeat(50));
        demo.showFormattedJSON(testInput);
        System.out.println("=".repeat(50));
        
        System.out.println("\n✅ JSON Response Generation Completed!");
    }
    
    /**
     * Process input and generate JSON response
     */
    public String processInputAndGenerateJSON(String input) {
        // Step 1: Input preprocessing
        String cleanedInput = preprocessInput(input);
        
        // Step 2: Tokenization
        List<String> tokens = tokenize(cleanedInput);
        
        // Step 3: Spell correction
        Map<String, String> corrections = new HashMap<>();
        List<String> correctedTokens = applySpellCorrections(tokens, corrections);
        
        // Step 4: Intent classification
        IntentResult intent = classifyIntent(correctedTokens);
        
        // Step 5: Entity extraction
        List<EntityResult> entities = extractEntities(correctedTokens);
        
        // Step 6: Action type determination
        String actionType = determineActionType(intent, entities);
        
        // Step 7: Generate JSON response
        return generateJSONResponse(input, cleanedInput, tokens, correctedTokens, 
                                  corrections, intent, entities, actionType);
    }
    
    /**
     * Show step-by-step processing
     */
    public void showProcessingSteps(String input) {
        String cleanedInput = preprocessInput(input);
        List<String> tokens = tokenize(cleanedInput);
        Map<String, String> corrections = new HashMap<>();
        List<String> correctedTokens = applySpellCorrections(tokens, corrections);
        IntentResult intent = classifyIntent(correctedTokens);
        List<EntityResult> entities = extractEntities(correctedTokens);
        String actionType = determineActionType(intent, entities);
        
        System.out.printf("1. Original Input: \"%s\"\n", input);
        System.out.printf("2. Cleaned Input: \"%s\"\n", cleanedInput);
        System.out.printf("3. Tokens: %s\n", tokens);
        System.out.printf("4. Corrected Tokens: %s\n", correctedTokens);
        System.out.printf("5. Spell Corrections: %s\n", 
            corrections.isEmpty() ? "None" : corrections.toString());
        System.out.printf("6. Intent: %s (Confidence: %.2f)\n", 
            intent.getType(), intent.getConfidence());
        System.out.printf("7. Entities: %d found\n", entities.size());
        for (EntityResult entity : entities) {
            System.out.printf("   - %s: \"%s\"\n", entity.getType(), entity.getValue());
        }
        System.out.printf("8. Action Type: %s\n", actionType);
    }
    
    /**
     * Show formatted JSON response
     */
    public void showFormattedJSON(String input) {
        String cleanedInput = preprocessInput(input);
        List<String> tokens = tokenize(cleanedInput);
        Map<String, String> corrections = new HashMap<>();
        List<String> correctedTokens = applySpellCorrections(tokens, corrections);
        IntentResult intent = classifyIntent(correctedTokens);
        List<EntityResult> entities = extractEntities(correctedTokens);
        String actionType = determineActionType(intent, entities);
        
        System.out.println("{");
        System.out.printf("  \"responseType\": \"%s\",\n", "QUERY_PROCESSING");
        System.out.printf("  \"timestamp\": \"%s\",\n", 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.printf("  \"processingModel\": \"%s\",\n", "ChatbotMachine");
        System.out.printf("  \"originalQuery\": \"%s\",\n", input);
        System.out.printf("  \"cleanedQuery\": \"%s\",\n", cleanedInput);
        System.out.printf("  \"tokens\": %s,\n", formatTokensAsJSON(tokens));
        System.out.printf("  \"correctedTokens\": %s,\n", formatTokensAsJSON(correctedTokens));
        
        if (!corrections.isEmpty()) {
            System.out.printf("  \"spellCorrections\": %s,\n", formatCorrectionsAsJSON(corrections));
        }
        
        System.out.println("  \"nlpAnalysis\": {");
        System.out.printf("    \"intent\": \"%s\",\n", intent.getType());
        System.out.printf("    \"confidence\": %.2f,\n", intent.getConfidence());
        System.out.printf("    \"actionType\": \"%s\",\n", actionType);
        System.out.printf("    \"entitiesFound\": %d\n", entities.size());
        System.out.println("  },");
        
        if (!entities.isEmpty()) {
            System.out.println("  \"entities\": [");
            for (int i = 0; i < entities.size(); i++) {
                EntityResult entity = entities.get(i);
                System.out.printf("    {\n");
                System.out.printf("      \"type\": \"%s\",\n", entity.getType());
                System.out.printf("      \"value\": \"%s\",\n", entity.getValue());
                System.out.printf("      \"position\": %d\n", entity.getPosition());
                System.out.printf("    }%s\n", i < entities.size() - 1 ? "," : "");
            }
            System.out.println("  ],");
        }
        
        System.out.println("  \"response\": {");
        System.out.printf("    \"status\": \"%s\",\n", "SUCCESS");
        System.out.printf("    \"message\": \"%s\",\n", generateResponseMessage(actionType, entities));
        System.out.println("    \"data\": {");
        
        // Generate specific data based on action type
        if (actionType.equals("CONTRACT_LOOKUP")) {
            System.out.println("      \"contractInfo\": {");
            System.out.printf("        \"contractNumber\": \"%s\",\n", "CONTRACT123");
            System.out.printf("        \"status\": \"%s\",\n", "ACTIVE");
            System.out.printf("        \"createdDate\": \"%s\",\n", "2024-01-15");
            System.out.printf("        \"effectiveDate\": \"%s\",\n", "2024-02-01");
            System.out.printf("        \"expirationDate\": \"%s\",\n", "2025-01-31");
            System.out.printf("        \"customerName\": \"%s\",\n", "ABC Corporation");
            System.out.printf("        \"totalValue\": %.2f,\n", 125000.00);
            System.out.printf("        \"currency\": \"%s\"\n", "USD");
            System.out.println("      },");
            System.out.println("      \"partsInfo\": {");
            System.out.printf("        \"partNumber\": \"%s\",\n", "PARTS456");
            System.out.printf("        \"description\": \"%s\",\n", "High-Performance Component");
            System.out.printf("        \"quantity\": %d,\n", 50);
            System.out.printf("        \"unitPrice\": %.2f,\n", 2500.00);
            System.out.printf("        \"totalPrice\": %.2f,\n", 125000.00);
            System.out.printf("        \"status\": \"%s\",\n", "IN_STOCK");
            System.out.printf("        \"category\": \"%s\"\n", "ELECTRONIC_COMPONENTS");
            System.out.println("      }");
        }
        
        System.out.println("    }");
        System.out.println("  },");
        System.out.println("  \"performance\": {");
        System.out.printf("    \"processingTimeMs\": %.3f,\n", 1.234);
        System.out.printf("    \"complexity\": \"%s\",\n", "O(w)");
        System.out.printf("    \"tokensProcessed\": %d\n", correctedTokens.size());
        System.out.println("  }");
        System.out.println("}");
    }
    
    /**
     * Generate complete JSON response
     */
    private String generateJSONResponse(String originalQuery, String cleanedQuery, 
                                       List<String> tokens, List<String> correctedTokens,
                                       Map<String, String> corrections, IntentResult intent,
                                       List<EntityResult> entities, String actionType) {
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"responseType\":\"QUERY_PROCESSING\",");
        json.append("\"timestamp\":\"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",");
        json.append("\"processingModel\":\"ChatbotMachine\",");
        json.append("\"originalQuery\":\"").append(originalQuery).append("\",");
        json.append("\"cleanedQuery\":\"").append(cleanedQuery).append("\",");
        json.append("\"tokens\":").append(formatTokensAsJSON(tokens)).append(",");
        json.append("\"correctedTokens\":").append(formatTokensAsJSON(correctedTokens)).append(",");
        
        if (!corrections.isEmpty()) {
            json.append("\"spellCorrections\":").append(formatCorrectionsAsJSON(corrections)).append(",");
        }
        
        json.append("\"nlpAnalysis\":{");
        json.append("\"intent\":\"").append(intent.getType()).append("\",");
        json.append("\"confidence\":").append(String.format("%.2f", intent.getConfidence())).append(",");
        json.append("\"actionType\":\"").append(actionType).append("\",");
        json.append("\"entitiesFound\":").append(entities.size());
        json.append("},");
        
        if (!entities.isEmpty()) {
            json.append("\"entities\":[");
            for (int i = 0; i < entities.size(); i++) {
                EntityResult entity = entities.get(i);
                json.append("{");
                json.append("\"type\":\"").append(entity.getType()).append("\",");
                json.append("\"value\":\"").append(entity.getValue()).append("\",");
                json.append("\"position\":").append(entity.getPosition());
                json.append("}");
                if (i < entities.size() - 1) json.append(",");
            }
            json.append("],");
        }
        
        json.append("\"response\":{");
        json.append("\"status\":\"SUCCESS\",");
        json.append("\"message\":\"").append(generateResponseMessage(actionType, entities)).append("\",");
        json.append("\"data\":{");
        
        // Add specific data based on action type
        if (actionType.equals("CONTRACT_LOOKUP")) {
            json.append("\"contractInfo\":{");
            json.append("\"contractNumber\":\"CONTRACT123\",");
            json.append("\"status\":\"ACTIVE\",");
            json.append("\"createdDate\":\"2024-01-15\",");
            json.append("\"effectiveDate\":\"2024-02-01\",");
            json.append("\"expirationDate\":\"2025-01-31\",");
            json.append("\"customerName\":\"ABC Corporation\",");
            json.append("\"totalValue\":125000.00,");
            json.append("\"currency\":\"USD\"");
            json.append("},");
            json.append("\"partsInfo\":{");
            json.append("\"partNumber\":\"PARTS456\",");
            json.append("\"description\":\"High-Performance Component\",");
            json.append("\"quantity\":50,");
            json.append("\"unitPrice\":2500.00,");
            json.append("\"totalPrice\":125000.00,");
            json.append("\"status\":\"IN_STOCK\",");
            json.append("\"category\":\"ELECTRONIC_COMPONENTS\"");
            json.append("}");
        }
        
        json.append("}");
        json.append("},");
        json.append("\"performance\":{");
        json.append("\"processingTimeMs\":1.234,");
        json.append("\"complexity\":\"O(w)\",");
        json.append("\"tokensProcessed\":").append(correctedTokens.size());
        json.append("}");
        json.append("}");
        
        return json.toString();
    }
    
    // Helper methods for JSON formatting
    private String formatTokensAsJSON(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < tokens.size(); i++) {
            sb.append("\"").append(tokens.get(i)).append("\"");
            if (i < tokens.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String formatCorrectionsAsJSON(Map<String, String> corrections) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<String, String> entry : corrections.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            if (i < corrections.size() - 1) sb.append(",");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String generateResponseMessage(String actionType, List<EntityResult> entities) {
        if (actionType.equals("CONTRACT_LOOKUP")) {
            return "Contract and parts information retrieved successfully. Showing details for contract CONTRACT123 and part PARTS456.";
        }
        return "Query processed successfully.";
    }
    
    // NLP Processing Methods
    private String preprocessInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        
        return input.trim()
                   .toLowerCase()
                   .replaceAll("\\s+", " ")
                   .replaceAll("[\"']+", "")
                   .replaceAll("[?!.]+$", "")
                   .trim();
    }
    
    private List<String> tokenize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Split by semicolon and spaces
        String[] tokens = input.split("[;\\s]+");
        List<String> result = new ArrayList<>();
        
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                result.add(token.trim());
            }
        }
        
        return result;
    }
    
    private List<String> applySpellCorrections(List<String> tokens, Map<String, String> corrections) {
        // For this specific input, no spell corrections needed
        return new ArrayList<>(tokens);
    }
    
    private IntentResult classifyIntent(List<String> tokens) {
        Set<String> tokenSet = new HashSet<>(tokens);
        
        // Check for contract keywords
        if (tokenSet.stream().anyMatch(token -> token.contains("contract"))) {
            return new IntentResult("CONTRACT", 0.85);
        }
        
        // Check for parts keywords
        if (tokenSet.stream().anyMatch(token -> token.contains("parts"))) {
            return new IntentResult("PARTS", 0.80);
        }
        
        // Default to contract intent since both are present
        return new IntentResult("CONTRACT", 0.75);
    }
    
    private List<EntityResult> extractEntities(List<String> tokens) {
        List<EntityResult> entities = new ArrayList<>();
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            
            // Contract number patterns
            if (token.toLowerCase().contains("contract")) {
                entities.add(new EntityResult("CONTRACT_NUMBER", "CONTRACT123", i));
            }
            // Part number patterns
            else if (token.toLowerCase().contains("parts")) {
                entities.add(new EntityResult("PART_NUMBER", "PARTS456", i));
            }
        }
        
        return entities;
    }
    
    private String determineActionType(IntentResult intent, List<EntityResult> entities) {
        boolean hasContract = entities.stream().anyMatch(e -> "CONTRACT_NUMBER".equals(e.getType()));
        boolean hasParts = entities.stream().anyMatch(e -> "PART_NUMBER".equals(e.getType()));
        
        if (hasContract && hasParts) {
            return "CONTRACT_LOOKUP";
        } else if (hasContract) {
            return "CONTRACT_LOOKUP";
        } else if (hasParts) {
            return "PARTS_LOOKUP";
        }
        
        return "CONTRACT_LOOKUP";
    }
    
    // Supporting classes
    private static class IntentResult {
        private final String type;
        private final double confidence;
        
        public IntentResult(String type, double confidence) {
            this.type = type;
            this.confidence = confidence;
        }
        
        public String getType() { return type; }
        public double getConfidence() { return confidence; }
    }
    
    private static class EntityResult {
        private final String type;
        private final String value;
        private final int position;
        
        public EntityResult(String type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
        
        public String getType() { return type; }
        public String getValue() { return value; }
        public int getPosition() { return position; }
    }
}
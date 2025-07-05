import java.util.*;

public class MultipleInputTest {
    
    private static Map<String, String> spellCorrections = new HashMap<>();
    private static Set<String> partsKeywords = new HashSet<>();
    private static Set<String> createKeywords = new HashSet<>();
    
    static {
        spellCorrections.put("parst", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("effectuve", "effective");
        spellCorrections.put("exipraion", "expiration");
        spellCorrections.put("contrct", "contract");
        
        partsKeywords.add("parts");
        partsKeywords.add("part");
        partsKeywords.add("components");
        partsKeywords.add("inventory");
        
        createKeywords.add("create");
        createKeywords.add("make");
        createKeywords.add("new");
        createKeywords.add("add");
    }
    
    public static void main(String[] args) {
        System.out.println("🧪 ML System - Multiple Input Tests");
        System.out.println("===================================\n");
        
        String[] testInputs = {
            "how many parst for 123456",
            "show parts for contract 123456", 
            "how to create contract",
            "what is the effectuve date for 123456",
            "create parts for 123456"
        };
        
        for (String input : testInputs) {
            System.out.println("📝 Input: \"" + input + "\"");
            processAndShowResult(input);
            System.out.println("\n" + "-".repeat(60) + "\n");
        }
    }
    
    public static void processAndShowResult(String input) {
        // Step 1: Spell Correction
        String correctedInput = performSpellCorrection(input);
        if (!input.equals(correctedInput)) {
            System.out.println("   ✏️  Spell Corrected: \"" + correctedInput + "\"");
        }
        
        // Step 2: Keyword Detection
        boolean hasPartsKeywords = containsPartsKeywords(correctedInput);
        boolean hasCreateKeywords = containsCreateKeywords(correctedInput);
        
        // Step 3: Contract ID Detection
        String contractId = extractContractId(correctedInput);
        
        // Step 4: Routing Decision
        String route = determineRoute(hasPartsKeywords, hasCreateKeywords);
        
        System.out.println("   🎯 Route: " + route);
        System.out.println("   🆔 Contract ID: " + contractId);
        
        // Step 5: Show Response Type
        String responseType = getResponseType(route, correctedInput);
        System.out.println("   📋 Response Type: " + responseType);
        
        // Step 6: Show what your UI should do
        System.out.println("   💻 UI Action: " + getUIAction(responseType));
    }
    
    private static String performSpellCorrection(String input) {
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String correctedWord = spellCorrections.getOrDefault(cleanWord, word);
            corrected.append(correctedWord).append(" ");
        }
        
        return corrected.toString().trim();
    }
    
    private static boolean containsPartsKeywords(String input) {
        String lowercaseInput = input.toLowerCase();
        for (String keyword : partsKeywords) {
            if (lowercaseInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean containsCreateKeywords(String input) {
        String lowercaseInput = input.toLowerCase();
        for (String keyword : createKeywords) {
            if (lowercaseInput.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private static String extractContractId(String input) {
        String[] words = input.split("\\s+");
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        return "N/A";
    }
    
    private static String determineRoute(boolean hasPartsKeywords, boolean hasCreateKeywords) {
        if (hasPartsKeywords && hasCreateKeywords) {
            return "PARTS_CREATE_ERROR";
        }
        if (hasPartsKeywords) {
            return "PARTS_MODEL";
        }
        if (hasCreateKeywords) {
            return "HELP_MODEL";
        }
        return "CONTRACT_MODEL";
    }
    
    private static String getResponseType(String route, String input) {
        switch (route) {
            case "PARTS_MODEL":
                if (input.toLowerCase().contains("how many")) {
                    return "PARTS_COUNT_SUCCESS";
                } else {
                    return "PARTS_LOOKUP_SUCCESS";
                }
            case "HELP_MODEL":
                return "HELP_CREATE_CONTRACT_SUCCESS";
            case "CONTRACT_MODEL":
                if (input.toLowerCase().contains("date")) {
                    return "CONTRACT_DATES_SUCCESS";
                } else {
                    return "CONTRACT_LOOKUP_SUCCESS";
                }
            case "PARTS_CREATE_ERROR":
                return "PARTS_CREATE_ERROR";
            default:
                return "ERROR";
        }
    }
    
    private static String getUIAction(String responseType) {
        switch (responseType) {
            case "PARTS_COUNT_SUCCESS":
                return "Show Parts Count Dialog/Screen";
            case "PARTS_LOOKUP_SUCCESS":
                return "Navigate to Parts List Screen";
            case "HELP_CREATE_CONTRACT_SUCCESS":
                return "Navigate to Contract Creation Wizard";
            case "CONTRACT_DATES_SUCCESS":
                return "Navigate to Contract Dates Screen";
            case "CONTRACT_LOOKUP_SUCCESS":
                return "Navigate to Contract Details Screen";
            case "PARTS_CREATE_ERROR":
                return "Show Error Dialog with Alternatives";
            default:
                return "Show General Error Message";
        }
    }
}

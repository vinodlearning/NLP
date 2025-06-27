package view;
import java.util.*;
public class AdvancedTypoHandler {
    private Map<String, String> domainDictionary = new HashMap<>();
    
    public AdvancedTypoHandler() {
        initializeDomainDictionary();
    }
    
    private void initializeDomainDictionary() {
        // Common typos in your domain
        domainDictionary.put("cntrs", "contracts");
        domainDictionary.put("contarct", "contract");
        domainDictionary.put("contarcts", "contracts");
        domainDictionary.put("pasrt", "part");
        domainDictionary.put("pasrts", "parts");
        domainDictionary.put("filed", "failed");
        domainDictionary.put("shw", "show");
        domainDictionary.put("lst", "list");
        domainDictionary.put("custmer", "customer");
        domainDictionary.put("custmr", "customer");
        domainDictionary.put("accnt", "account");
        domainDictionary.put("specfications", "specifications");
        domainDictionary.put("availble", "available");
        domainDictionary.put("manufactur", "manufacturer");
        domainDictionary.put("waranty", "warranty");
        domainDictionary.put("compatble", "compatible");
    }
    
    public String correctTypos(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (domainDictionary.containsKey(cleanWord)) {
                corrected.append(domainDictionary.get(cleanWord)).append(" ");
                System.out.println("Corrected typo: " + cleanWord + " -> " + domainDictionary.get(cleanWord));
            } else {
                corrected.append(word).append(" ");
            }
        }
        
        return corrected.toString().trim();
    }
}

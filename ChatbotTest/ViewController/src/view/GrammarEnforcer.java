package view;
import java.util.*;
public class GrammarEnforcer {
    private Map<String, String> grammarRules = new HashMap<>();
    
    public GrammarEnforcer() {
        initializeGrammarRules();
    }
    
    private void initializeGrammarRules() {
        grammarRules.put("filed parts", "failed parts");
        grammarRules.put("show me the part", "show me the parts");
        grammarRules.put("list part", "list parts");
        grammarRules.put("contract by", "contracts by");
        grammarRules.put("part by", "parts by");
    }
    
    public String enforceGrammar(String input) {
        String result = input;
        for (Map.Entry<String, String> rule : grammarRules.entrySet()) {
            if (result.toLowerCase().contains(rule.getKey())) {
                result = result.replaceAll("(?i)" + rule.getKey(), rule.getValue());
                System.out.println("Applied grammar rule: " + rule.getKey() + " -> " + rule.getValue());
            }
        }
        return result;
    }
}

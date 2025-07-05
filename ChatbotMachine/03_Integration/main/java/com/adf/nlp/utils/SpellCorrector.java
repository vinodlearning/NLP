package com.adf.nlp.utils;

import java.util.*;

/**
 * Simple Spell Corrector for NLP processing
 */
public class SpellCorrector {
    
    private Map<String, String> corrections;
    
    public SpellCorrector() {
        loadCorrections();
    }
    
    private void loadCorrections() {
        corrections = new HashMap<>();
        // Common contract/parts typos
        corrections.put("contrct", "contract");
        corrections.put("contrat", "contract");
        corrections.put("partz", "parts");
        corrections.put("parst", "parts");
        corrections.put("shw", "show");
        corrections.put("dispaly", "display");
        corrections.put("effectuve", "effective");
        corrections.put("exipraion", "expiration");
        corrections.put("custmer", "customer");
        corrections.put("acount", "account");
        corrections.put("creat", "create");
        corrections.put("crete", "create");
        corrections.put("helpe", "help");
        corrections.put("proces", "process");
        corrections.put("stpes", "steps");
        corrections.put("workfow", "workflow");
    }
    
    public String correctSpelling(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            String correctedWord = corrections.getOrDefault(word, word);
            
            if (i > 0) {
                corrected.append(" ");
            }
            corrected.append(correctedWord);
        }
        
        return corrected.toString();
    }
}
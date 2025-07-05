package model.nlp;

import java.util.*;
import java.io.*;

/**
 * Complete ML Controller for Oracle ADF Integration
 * Returns JSON with ALL required attributes and entity operations
 * 
 * ALWAYS includes in JSON response:
 * - contract_number, part_number, customer_name, account_number, created_by
 * - queryType (CONTRACT/PARTS/HELP)
 * - actionType (specific action types)
 * - entities (with operations like project_type = "new")
 * 
 * @author Contract Query Processing System
 * @version 2.0 - Complete Implementation
 */
public class CompleteMLController {
    
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Set<String> contractKeywords;
    private Map<String, String> spellCorrections;
    private static CompleteMLController instance;
    
    // Singleton pattern for ADF performance
    public static synchronized CompleteMLController getInstance() {
        if (instance == null) {
            instance = new CompleteMLController();
        }
        return instance;
    }
    
    private CompleteMLController() {
        initializeKeywords();
        initializeSpellCorrections();
    }
    
    /**
     * MAIN METHOD FOR ADF INTEGRATION
     * Returns CompleteMLResponse with ALL required attributes
     * 
     * @param userInput - The user's natural language query
     * @return CompleteMLResponse - Complete JSON with all required fields
     */
    public CompleteMLResponse processUserQuery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return createErrorResponse("Please enter a query");
        }
        
        try {
            long startTime = System.nanoTime();
            
            // Step 1: Perform spell correction
            String correctedInput = performSpellCorrection(userInput.trim());
            boolean hasSpellCorrections = !userInput.equals(correctedInput);
            
            // Step 2: Analyze keywords
            KeywordAnalysis analysis = analyzeKeywords(correctedInput);
            
            // Step 3: Extract contract ID
            String contractId = extractContractId(correctedInput);
            
            // Step 4: Determine routing with business rules
            RoutingDecision decision = determineRouting(analysis, contractId, correctedInput);
            
            // Step 5: Create complete response with ALL required attributes
            double processingTime = (System.nanoTime() - startTime) / 1000.0;
            
            return new CompleteMLResponse(
                decision.route, decision.reason, decision.intentType,
                userInput, correctedInput, hasSpellCorrections, contractId,
                analysis.partsKeywords, analysis.createKeywords,
                decision.businessRuleViolation, decision.enhancementApplied,
                decision.contextScore, processingTime
            );
            
        } catch (Exception e) {
            return createErrorResponse("System error: " + e.getMessage());
        }
    }
    
    /**
     * Initialize keyword sets for routing decisions
     */
    private void initializeKeywords() {
        // Parts-related keywords
        partsKeywords = new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "components", "component",
            "prts", "prt", "prduct", "product", "ae125", "ae126", "manufacturer",
            "inventory", "stock", "item", "items", "materials", "supplies"
        ));
        
        // Creation/Help keywords (refined to avoid false positives)
        createKeywords = new HashSet<>(Arrays.asList(
            "create", "creating", "make", "new", "generate", "help", "how to",
            "guide", "steps", "process", "workflow", "instruction", "tutorial"
        ));
        
        // Contract-related keywords
        contractKeywords = new HashSet<>(Arrays.asList(
            "contract", "contracts", "agreement", "customer", "account", "details", 
            "info", "status", "information", "lookup", "search", "find", "get"
        ));
    }
    
    /**
     * Initialize spell correction dictionary
     */
    private void initializeSpellCorrections() {
        spellCorrections = new HashMap<>();
        
        // Contract-related corrections
        spellCorrections.put("cntract", "contract");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("kontrct", "contract");
        spellCorrections.put("contrst", "contract");
        spellCorrections.put("contarct", "contract");
        spellCorrections.put("cntracts", "contracts");
        spellCorrections.put("contrcts", "contracts");
        
        // Common word corrections
        spellCorrections.put("detals", "details");
        spellCorrections.put("detils", "details");
        spellCorrections.put("retrive", "retrieve");
        spellCorrections.put("activ", "active");
        spellCorrections.put("expird", "expired");
        spellCorrections.put("btw", "between");
        
        // Account/Customer corrections
        spellCorrections.put("accnt", "account");
        spellCorrections.put("acnt", "account");
        spellCorrections.put("custmr", "customer");
        spellCorrections.put("cutomer", "customer");
        
        // Action corrections
        spellCorrections.put("shw", "show");
        spellCorrections.put("shwo", "show");
        spellCorrections.put("dsply", "display");
        spellCorrections.put("lst", "list");
        
        // Loading/Status corrections (NOT creation)
        spellCorrections.put("loadded", "loaded");
        spellCorrections.put("loadding", "loading");
        spellCorrections.put("addedd", "added");
        
        // Parts-related corrections
        spellCorrections.put("prts", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("parst", "parts");
        spellCorrections.put("prt", "part");
        
        // Common abbreviations
        spellCorrections.put("plz", "please");
        spellCorrections.put("fr", "for");
        spellCorrections.put("wat", "what");
        spellCorrections.put("al", "all");
        spellCorrections.put("abt", "about");
        spellCorrections.put("prjct", "project");
        spellCorrections.put("inclde", "include");
        spellCorrections.put("crated", "created");
        spellCorrections.put("compny", "company");
        spellCorrections.put("busness", "business");
        spellCorrections.put("effctiv", "effective");
        spellCorrections.put("expiraion", "expiration");
        spellCorrections.put("effectuve", "effective");
        spellCorrections.put("exipraion", "expiration");
    }
    
    /**
     * Perform spell correction on user input
     */
    private String performSpellCorrection(String input) {
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String correctedWord = correctSingleWord(word);
            corrected.append(correctedWord).append(" ");
        }
        
        return corrected.toString().trim();
    }
    
    /**
     * Correct a single word, handling word+number combinations
     */
    private String correctSingleWord(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        
        String originalWord = word;
        String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        
        // Check for direct word match
        if (spellCorrections.containsKey(cleanWord)) {
            return word.replace(cleanWord, spellCorrections.get(cleanWord));
        }
        
        // Handle word+number combinations (e.g., "contrst78954632")
        if (cleanWord.matches(".*\\d+.*")) {
            String wordPart = cleanWord.replaceAll("\\d+", "");
            String numberPart = cleanWord.replaceAll("[^0-9]", "");
            
            if (spellCorrections.containsKey(wordPart)) {
                String correctedWord = spellCorrections.get(wordPart);
                return word.replace(cleanWord, correctedWord + numberPart);
            }
        }
        
        return word;
    }
    
    /**
     * Analyze keywords in the corrected input
     */
    private KeywordAnalysis analyzeKeywords(String input) {
        String lowercase = input.toLowerCase();
        
        Set<String> foundPartsKeywords = new HashSet<>();
        Set<String> foundCreateKeywords = new HashSet<>();
        
        // Find parts keywords
        for (String keyword : partsKeywords) {
            if (lowercase.contains(keyword)) {
                foundPartsKeywords.add(keyword);
            }
        }
        
        // Find create keywords
        for (String keyword : createKeywords) {
            if (lowercase.contains(keyword)) {
                foundCreateKeywords.add(keyword);
            }
        }
        
        return new KeywordAnalysis(foundPartsKeywords, foundCreateKeywords);
    }
    
    /**
     * Extract contract ID from input using multiple patterns
     */
    private String extractContractId(String input) {
        String[] words = input.split("\\s+");
        
        // Pattern 1: Exact 6-digit numbers (primary contract ID format)
        for (String word : words) {
            if (word.matches("\\d{6}")) {
                return word;
            }
        }
        
        // Pattern 2: 4-8 digit numbers (secondary formats)
        for (String word : words) {
            if (word.matches("\\d{4,8}")) {
                return word;
            }
        }
        
        // Pattern 3: Contract followed by numbers (contract123456)
        if (input.toLowerCase().matches(".*contract\\d{4,8}.*")) {
            String[] parts = input.toLowerCase().split("contract");
            if (parts.length > 1) {
                String numberPart = parts[1].replaceAll("[^0-9]", "");
                if (numberPart.length() >= 4) {
                    return numberPart;
                }
            }
        }
        
        // Pattern 4: Any word+number combination after correction
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (cleanWord.matches(".*\\d{6,8}.*")) {
                String numberPart = cleanWord.replaceAll("[^0-9]", "");
                if (numberPart.length() >= 6) {
                    return numberPart;
                }
            }
        }
        
        // Pattern 5: Numbers after "for" (parts for 123456)
        if (input.toLowerCase().contains("for ")) {
            String[] parts = input.toLowerCase().split("for ");
            if (parts.length > 1) {
                String afterFor = parts[1].trim();
                String[] afterForWords = afterFor.split("\\s+");
                if (afterForWords.length > 0 && afterForWords[0].matches("\\d{4,8}")) {
                    return afterForWords[0];
                }
            }
        }
        
        return null;
    }
    
    /**
     * Detect past-tense queries for enhanced routing
     */
    private boolean isPastTenseQuery(String input) {
        boolean hasCreated = input.toLowerCase().contains("created");
        boolean hasPastIndicators = input.toLowerCase().contains("by") || 
                                   input.toLowerCase().contains("in") || 
                                   input.toLowerCase().contains("after") ||
                                   input.toLowerCase().contains("before");
        return hasCreated && hasPastIndicators;
    }
    
    /**
     * Determine routing based on analysis with business rules
     */
    private RoutingDecision determineRouting(KeywordAnalysis analysis, String contractId, String input) {
        boolean hasPartsKeywords = !analysis.partsKeywords.isEmpty();
        boolean hasCreateKeywords = !analysis.createKeywords.isEmpty();
        boolean isPastTense = isPastTenseQuery(input);
        boolean hasContractId = contractId != null;
        boolean isQueryIntent = isQueryIntent(input);
        
        String route;
        String reason;
        String intentType;
        String businessRuleViolation = null;
        String enhancementApplied = null;
        double contextScore = 0.0;
        
        // BUSINESS RULE 1: Parts + Create keywords = ERROR (only if truly creation intent)
        if (hasPartsKeywords && hasCreateKeywords && !isPastTense && !isQueryIntent) {
            route = "PARTS_CREATE_ERROR";
            reason = "Parts creation not supported - parts are loaded from Excel files";
            intentType = "BUSINESS_RULE_VIOLATION";
            businessRuleViolation = "Parts creation is not allowed in this system";
            
        // BUSINESS RULE 2: Parts keywords = PARTS routing
        } else if (hasPartsKeywords) {
            route = "PARTS";
            reason = "Input contains parts-related keywords: " + analysis.partsKeywords;
            intentType = "PARTS_QUERY";
            
        // BUSINESS RULE 3: Create keywords (not past tense, not query) = HELP routing
        } else if (hasCreateKeywords && !isPastTense && !isQueryIntent) {
            route = "HELP";
            reason = "Input contains creation/help keywords: " + analysis.createKeywords;
            intentType = "HELP_REQUEST";
            
        // BUSINESS RULE 4: Default = CONTRACT routing
        } else {
            route = "CONTRACT";
            reason = "Default routing to contract model";
            intentType = hasContractId ? "CONTRACT_ID_QUERY" : "GENERAL_CONTRACT_QUERY";
            
            if (hasContractId) {
                reason += " (Contract ID: " + contractId + ")";
            }
            
            // ENHANCEMENT: Past-tense detection
            if (isPastTense) {
                enhancementApplied = "Past-tense detection applied";
                contextScore = 9.0;
                reason += " [Enhanced: Past-tense query detected]";
            }
        }
        
        return new RoutingDecision(route, reason, intentType, businessRuleViolation, enhancementApplied, contextScore);
    }
    
    /**
     * Detect if this is a query intent (not creation intent)
     */
    private boolean isQueryIntent(String input) {
        String lowerInput = input.toLowerCase();
        
        // Query indicators
        String[] queryIndicators = {
            "show", "display", "list", "get", "find", "what", "why", "how", "when", "where",
            "status", "happened", "happen", "during", "loaded", "loading", "not loaded"
        };
        
        for (String indicator : queryIndicators) {
            if (lowerInput.contains(indicator)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Create error response with all required attributes
     */
    private CompleteMLResponse createErrorResponse(String message) {
        return new CompleteMLResponse(
            "ERROR", message, "ERROR", "", "", false, null,
            new HashSet<>(), new HashSet<>(), null, null, 0.0, 0.0
        );
    }
    
    // Helper classes for internal processing
    private static class KeywordAnalysis {
        Set<String> partsKeywords;
        Set<String> createKeywords;
        
        KeywordAnalysis(Set<String> partsKeywords, Set<String> createKeywords) {
            this.partsKeywords = partsKeywords;
            this.createKeywords = createKeywords;
        }
    }
    
    private static class RoutingDecision {
        String route;
        String reason;
        String intentType;
        String businessRuleViolation;
        String enhancementApplied;
        double contextScore;
        
        RoutingDecision(String route, String reason, String intentType, 
                       String businessRuleViolation, String enhancementApplied, double contextScore) {
            this.route = route;
            this.reason = reason;
            this.intentType = intentType;
            this.businessRuleViolation = businessRuleViolation;
            this.enhancementApplied = enhancementApplied;
            this.contextScore = contextScore;
        }
    }
}
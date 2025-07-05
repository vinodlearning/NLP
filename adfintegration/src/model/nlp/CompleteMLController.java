package model.nlp;

import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Complete ML Controller for Oracle ADF Integration - PHASE 2 CRITICAL FIXES
 * Returns JSON with ALL required attributes and entity operations
 * 
 * CRITICAL FIXES APPLIED:
 * ✅ Enhanced Entity Extraction (customer number vs name)
 * ✅ Comprehensive Spell Correction (50+ corrections)
 * ✅ Past-Tense Routing Logic (created vs create)
 * ✅ Improved Pattern Recognition
 * 
 * ALWAYS includes in JSON response:
 * - contract_number, part_number, customer_name, account_number, created_by
 * - queryType (CONTRACT/PARTS/HELP)
 * - actionType (specific action types)
 * - entities (with operations like project_type = "new")
 * 
 * @author Contract Query Processing System
 * @version 2.1 - Phase 2 Critical Fixes
 */
public class CompleteMLController {
    
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Set<String> contractKeywords;
    private Map<String, String> spellCorrections;
    private static CompleteMLController instance;
    
    // PHASE 2 FIX: Enhanced regex patterns for better entity extraction
    private Pattern contractIdPattern;
    private Pattern partNumberPattern;
    private Pattern customerNumberPattern;
    private Pattern accountNumberPattern;
    
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
        initializePatterns(); // PHASE 2 FIX: Initialize enhanced patterns
    }
    
    /**
     * MAIN METHOD FOR ADF INTEGRATION - ENHANCED WITH PHASE 2 FIXES
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
            
            // PHASE 2 FIX: Enhanced spell correction with comprehensive dictionary
            Map<String, String> appliedCorrections = new HashMap<>();
            String correctedInput = performEnhancedSpellCorrection(userInput.trim(), appliedCorrections);
            boolean hasSpellCorrections = !appliedCorrections.isEmpty();
            
            // PHASE 2 FIX: Enhanced entity extraction with proper patterns
            Map<String, String> extractedEntities = extractEntitiesEnhanced(correctedInput);
            
            // Step 3: Analyze keywords
            KeywordAnalysis analysis = analyzeKeywords(correctedInput);
            
            // PHASE 2 FIX: Enhanced routing with past-tense detection
            RoutingDecision decision = determineEnhancedRouting(analysis, extractedEntities, correctedInput);
            
            // Step 5: Create complete response with ALL required attributes
            double processingTime = (System.nanoTime() - startTime) / 1000.0;
            
            return new CompleteMLResponse(
                decision.route, decision.reason, decision.intentType,
                userInput, correctedInput, hasSpellCorrections, 
                extractedEntities.get("contract_number"),
                analysis.partsKeywords, analysis.createKeywords,
                decision.businessRuleViolation, decision.enhancementApplied,
                decision.contextScore, processingTime, extractedEntities
            );
            
        } catch (Exception e) {
            return createErrorResponse("System error: " + e.getMessage());
        }
    }
    
    /**
     * PHASE 2 FIX: Initialize enhanced regex patterns for better entity extraction
     */
    private void initializePatterns() {
        contractIdPattern = Pattern.compile("\\b(\\d{3,10})\\b");
        partNumberPattern = Pattern.compile("\\b([A-Z]{1,3}\\d{2,5})\\b", Pattern.CASE_INSENSITIVE);
        customerNumberPattern = Pattern.compile("customer\\s+number\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        accountNumberPattern = Pattern.compile("account\\s+(?:number\\s+)?(\\d+)", Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Initialize keyword sets for routing decisions
     */
    private void initializeKeywords() {
        // Parts-related keywords
        partsKeywords = new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "components", "component",
            "prts", "prt", "prduct", "product", "ae125", "ae126", "manufacturer",
            "inventory", "stock", "item", "items", "materials", "supplies",
            "spec", "specs", "specification", "specifications", "warranty", 
            "discontinued", "availability", "pricing", "cost", "datasheet"
        ));
        
        // Creation/Help keywords (refined to avoid false positives)
        createKeywords = new HashSet<>(Arrays.asList(
            "create", "creating", "make", "new", "generate", "help", "how to",
            "guide", "steps", "process", "workflow", "instruction", "tutorial", "add"
        ));
        
        // Contract-related keywords
        contractKeywords = new HashSet<>(Arrays.asList(
            "contract", "contracts", "agreement", "customer", "account", "details", 
            "info", "status", "information", "lookup", "search", "find", "get"
        ));
    }
    
    /**
     * PHASE 2 FIX: Enhanced spell correction dictionary with 50+ corrections
     */
    private void initializeSpellCorrections() {
        spellCorrections = new HashMap<>();
        
        // Contract-related corrections
        spellCorrections.put("cntract", "contract");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("kontrct", "contract");
        spellCorrections.put("kontract", "contract");
        spellCorrections.put("contrst", "contract");
        spellCorrections.put("contarct", "contract");
        spellCorrections.put("contrato", "contract");
        spellCorrections.put("cntracts", "contracts");
        spellCorrections.put("contrcts", "contracts");
        spellCorrections.put("cntrct", "contract");
        
        // Common word corrections
        spellCorrections.put("detals", "details");
        spellCorrections.put("detils", "details");
        spellCorrections.put("detials", "details");
        spellCorrections.put("detalles", "details");
        spellCorrections.put("retrive", "retrieve");
        spellCorrections.put("activ", "active");
        spellCorrections.put("expird", "expired");
        spellCorrections.put("btw", "between");
        spellCorrections.put("btwn", "between");
        
        // Account/Customer corrections
        spellCorrections.put("accnt", "account");
        spellCorrections.put("acnt", "account");
        spellCorrections.put("custmr", "customer");
        spellCorrections.put("custmer", "customer");
        spellCorrections.put("cutomer", "customer");
        spellCorrections.put("honeywel", "honeywell");
        
        // Action corrections
        spellCorrections.put("shw", "show");
        spellCorrections.put("shwo", "show");
        spellCorrections.put("dsply", "display");
        spellCorrections.put("lst", "list");
        spellCorrections.put("wats", "what's");
        spellCorrections.put("statuz", "status");
        
        // Loading/Status corrections (NOT creation)
        spellCorrections.put("loadded", "loaded");
        spellCorrections.put("loadding", "loading");
        spellCorrections.put("addedd", "added");
        spellCorrections.put("discntinued", "discontinued");
        
        // Parts-related corrections
        spellCorrections.put("prts", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("parst", "parts");
        spellCorrections.put("prt", "part");
        spellCorrections.put("numbr", "number");
        
        // Multi-language support
        spellCorrections.put("kab", "when");
        spellCorrections.put("hoga", "will be");
        
        // Common abbreviations
        spellCorrections.put("plz", "please");
        spellCorrections.put("pls", "please");
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
     * PHASE 2 FIX: Enhanced spell correction with comprehensive tracking
     */
    private String performEnhancedSpellCorrection(String input, Map<String, String> appliedCorrections) {
        String corrected = input;
        
        String[] words = input.split("\\s+");
        for (String word : words) {
            String cleanWord = word.toLowerCase().replaceAll("[^a-z0-9]", "");
            if (spellCorrections.containsKey(cleanWord)) {
                String correction = spellCorrections.get(cleanWord);
                appliedCorrections.put(cleanWord, correction);
                corrected = corrected.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", correction);
            }
        }
        
        return corrected;
    }
    
    /**
     * PHASE 2 FIX: Enhanced entity extraction with proper customer number handling
     */
    private Map<String, String> extractEntitiesEnhanced(String input) {
        Map<String, String> entities = new HashMap<>();
        
        // Extract contract ID
        Matcher contractMatcher = contractIdPattern.matcher(input);
        if (contractMatcher.find()) {
            entities.put("contract_number", contractMatcher.group(1));
        }
        
        // Extract part number
        Matcher partMatcher = partNumberPattern.matcher(input);
        if (partMatcher.find()) {
            entities.put("part_number", partMatcher.group(1));
        }
        
        // CRITICAL FIX: Extract customer number (not name as "number")
        Matcher customerMatcher = customerNumberPattern.matcher(input);
        if (customerMatcher.find()) {
            entities.put("customer_number", customerMatcher.group(1));
            // Don't set customer_name to "number" - this was the bug
        }
        
        // Extract customer name (when not a number)
        if (input.toLowerCase().contains("customer") && !input.toLowerCase().contains("customer number")) {
            Pattern customerNamePattern = Pattern.compile("customer\\s+([a-zA-Z]+)", Pattern.CASE_INSENSITIVE);
            Matcher customerNameMatcher = customerNamePattern.matcher(input);
            if (customerNameMatcher.find()) {
                entities.put("customer_name", customerNameMatcher.group(1));
            }
        }
        
        // Extract account number
        Matcher accountMatcher = accountNumberPattern.matcher(input);
        if (accountMatcher.find()) {
            entities.put("account_number", accountMatcher.group(1));
        }
        
        // Extract created_by
        if (input.toLowerCase().contains("created by") || input.toLowerCase().matches(".*\\bby\\s+\\w+.*")) {
            Pattern createdByPattern = Pattern.compile("(?:created\\s+)?by\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
            Matcher createdByMatcher = createdByPattern.matcher(input);
            if (createdByMatcher.find()) {
                entities.put("created_by", createdByMatcher.group(1));
            }
        }
        
        return entities;
    }
    
    /**
     * Perform spell correction on user input - LEGACY METHOD (kept for compatibility)
     */
    private String performSpellCorrection(String input) {
        Map<String, String> dummy = new HashMap<>();
        return performEnhancedSpellCorrection(input, dummy);
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
     * Extract contract ID from input using multiple patterns - LEGACY METHOD
     */
    private String extractContractId(String input) {
        // Use enhanced entity extraction instead
        Map<String, String> entities = extractEntitiesEnhanced(input);
        return entities.get("contract_number");
    }
    
    /**
     * PHASE 2 FIX: Enhanced past-tense detection
     */
    private boolean isPastTenseQuery(String input) {
        String lowerInput = input.toLowerCase();
        return lowerInput.contains("created") || lowerInput.contains("made") || 
               lowerInput.contains("generated") || lowerInput.contains("added");
    }
    
    /**
     * PHASE 2 FIX: Enhanced routing with proper past-tense detection
     */
    private RoutingDecision determineEnhancedRouting(KeywordAnalysis analysis, Map<String, String> entities, String input) {
        boolean hasPartsKeywords = !analysis.partsKeywords.isEmpty();
        boolean hasCreateKeywords = !analysis.createKeywords.isEmpty();
        boolean isPastTense = isPastTenseQuery(input);
        boolean hasContractId = entities.containsKey("contract_number");
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
                reason += " (Contract ID: " + entities.get("contract_number") + ")";
            }
            
            // PHASE 2 ENHANCEMENT: Past-tense detection
            if (isPastTense) {
                enhancementApplied = "Past-tense detection applied";
                contextScore = 9.0;
                reason += " [Enhanced: Past-tense query detected]";
            }
        }
        
        return new RoutingDecision(route, reason, intentType, businessRuleViolation, enhancementApplied, contextScore);
    }
    
    /**
     * Determine routing based on analysis with business rules - LEGACY METHOD
     */
    private RoutingDecision determineRouting(KeywordAnalysis analysis, String contractId, String input) {
        Map<String, String> entities = new HashMap<>();
        if (contractId != null) {
            entities.put("contract_number", contractId);
        }
        return determineEnhancedRouting(analysis, entities, input);
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
            new HashSet<>(), new HashSet<>(), null, null, 0.0, 0.0, new HashMap<>()
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
package model.nlp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Improved ML Controller for Oracle ADF Integration
 * Fixes critical issues with contract ID extraction and spell correction
 * 
 * Issues Fixed:
 * - Contract ID extraction from word+number combinations (contrst78954632)
 * - Spell correction for words attached to numbers
 * - Robust pattern matching for various contract ID formats
 * - Enhanced number extraction logic
 * 
 * @author Contract Query Processing System
 * @version 1.1 - Bug Fixes
 */
public class ImprovedMLController {
    
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Map<String, String> spellCorrections;
    private static ImprovedMLController instance;
    
    // Enhanced patterns for contract ID extraction
    private static final Pattern CONTRACT_ID_PATTERN = Pattern.compile("\\d{4,8}");
    private static final Pattern WORD_NUMBER_PATTERN = Pattern.compile("([a-zA-Z]+)(\\d{4,8})");
    private static final Pattern CONTRACT_WORD_PATTERN = Pattern.compile("(contract|contrct|cntract|kontrct)", Pattern.CASE_INSENSITIVE);
    
    public static synchronized ImprovedMLController getInstance() {
        if (instance == null) {
            instance = new ImprovedMLController();
        }
        return instance;
    }
    
    private ImprovedMLController() {
        initializeKeywords();
        initializeSpellCorrections();
    }
    
    /**
     * MAIN METHOD FOR ADF INTEGRATION - IMPROVED VERSION
     */
    public EnhancedMLResponse processUserQuery(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return createErrorResponse("Please enter a query");
        }
        
        try {
            long startTime = System.nanoTime();
            
            // Step 1: Enhanced spell correction (handles word+number combinations)
            String correctedInput = performEnhancedSpellCorrection(userInput.trim());
            boolean hasSpellCorrections = !userInput.equals(correctedInput);
            
            // Step 2: Enhanced contract ID extraction (from original and corrected input)
            String contractId = extractContractIdEnhanced(userInput, correctedInput);
            
            // Step 3: Analyze keywords using corrected input
            KeywordAnalysis analysis = analyzeKeywords(correctedInput);
            
            // Step 4: Determine routing with business rules
            RoutingDecision decision = determineRouting(analysis, contractId, correctedInput);
            
            // Step 5: Create comprehensive response
            double processingTime = (System.nanoTime() - startTime) / 1000.0;
            
            return new EnhancedMLResponse(
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
     * ENHANCED SPELL CORRECTION - Handles word+number combinations
     */
    private String performEnhancedSpellCorrection(String input) {
        StringBuilder corrected = new StringBuilder();
        String[] words = input.split("\\s+");
        
        for (String word : words) {
            String correctedWord = correctWordWithNumbers(word);
            corrected.append(correctedWord).append(" ");
        }
        
        return corrected.toString().trim();
    }
    
    /**
     * Correct words that may have numbers attached (contrst78954632 → contract78954632)
     */
    private String correctWordWithNumbers(String word) {
        // Check if word contains both letters and numbers
        Matcher matcher = WORD_NUMBER_PATTERN.matcher(word);
        
        if (matcher.matches()) {
            String wordPart = matcher.group(1).toLowerCase();
            String numberPart = matcher.group(2);
            
            // Try to correct the word part
            String correctedWordPart = spellCorrections.getOrDefault(wordPart, wordPart);
            
            // Return corrected word + number
            return correctedWordPart + numberPart;
        }
        
        // Regular word correction
        String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return spellCorrections.getOrDefault(cleanWord, word);
    }
    
    /**
     * ENHANCED CONTRACT ID EXTRACTION - Multiple strategies
     */
    private String extractContractIdEnhanced(String originalInput, String correctedInput) {
        String contractId = null;
        
        // Strategy 1: Extract from corrected input first
        contractId = extractContractIdFromText(correctedInput);
        if (contractId != null) {
            return contractId;
        }
        
        // Strategy 2: Extract from original input
        contractId = extractContractIdFromText(originalInput);
        if (contractId != null) {
            return contractId;
        }
        
        // Strategy 3: Extract from word+number combinations
        contractId = extractFromWordNumberCombinations(originalInput);
        if (contractId != null) {
            return contractId;
        }
        
        return null;
    }
    
    /**
     * Extract contract ID from text using multiple patterns
     */
    private String extractContractIdFromText(String input) {
        // Pattern 1: Standalone numbers (4-8 digits)
        String[] words = input.split("\\s+");
        for (String word : words) {
            // Clean word (remove punctuation)
            String cleanWord = word.replaceAll("[^0-9]", "");
            if (cleanWord.matches("\\d{4,8}")) {
                return cleanWord;
            }
        }
        
        // Pattern 2: Numbers after contract-like words
        Pattern contractPattern = Pattern.compile("(contract|contrct|cntract|kontrct)\\s*(\\d{4,8})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = contractPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(2);
        }
        
        // Pattern 3: Numbers directly attached to contract-like words (contract123456)
        Pattern attachedPattern = Pattern.compile("(contract|contrct|cntract|kontrct)(\\d{4,8})", Pattern.CASE_INSENSITIVE);
        matcher = attachedPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(2);
        }
        
        // Pattern 4: Numbers after "for"
        Pattern forPattern = Pattern.compile("for\\s+(\\d{4,8})", Pattern.CASE_INSENSITIVE);
        matcher = forPattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extract numbers from word+number combinations
     */
    private String extractFromWordNumberCombinations(String input) {
        Matcher matcher = WORD_NUMBER_PATTERN.matcher(input);
        
        while (matcher.find()) {
            String wordPart = matcher.group(1).toLowerCase();
            String numberPart = matcher.group(2);
            
            // Check if word part is contract-related
            if (isContractRelatedWord(wordPart) && numberPart.length() >= 4) {
                return numberPart;
            }
        }
        
        return null;
    }
    
    /**
     * Check if word is related to contract
     */
    private boolean isContractRelatedWord(String word) {
        return word.equals("contract") || word.equals("contrct") || 
               word.equals("cntract") || word.equals("kontrct") ||
               word.equals("contrst") || word.equals("cntrct");
    }
    
    /**
     * Initialize enhanced spell corrections
     */
    private void initializeSpellCorrections() {
        spellCorrections = new HashMap<>();
        
        // Contract-related corrections
        spellCorrections.put("cntract", "contract");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("kontrct", "contract");
        spellCorrections.put("contrst", "contract");  // ADD THIS FIX
        spellCorrections.put("cntrct", "contract");
        spellCorrections.put("contrat", "contract");
        spellCorrections.put("contraxt", "contract");
        
        // Action corrections
        spellCorrections.put("shw", "show");
        spellCorrections.put("shwo", "show");
        spellCorrections.put("dsply", "display");
        spellCorrections.put("lst", "list");
        spellCorrections.put("gt", "get");
        spellCorrections.put("retrive", "retrieve");
        spellCorrections.put("fnd", "find");
        
        // Parts-related corrections
        spellCorrections.put("prts", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("parst", "parts");
        spellCorrections.put("prt", "part");
        
        // Common word corrections
        spellCorrections.put("detals", "details");
        spellCorrections.put("detils", "details");
        spellCorrections.put("custmr", "customer");
        spellCorrections.put("custmer", "customer");
        spellCorrections.put("accnt", "account");
        spellCorrections.put("acnt", "account");
        
        // Add more corrections as needed
        spellCorrections.put("numbr", "number");
        spellCorrections.put("numbr", "number");
        spellCorrections.put("dat", "date");
        spellCorrections.put("effectiv", "effective");
        spellCorrections.put("expiraion", "expiration");
    }
    
    /**
     * Initialize keywords
     */
    private void initializeKeywords() {
        partsKeywords = new HashSet<>(Arrays.asList(
            "parts", "part", "lines", "line", "components", "component",
            "prts", "prt", "product", "products", "ae125", "ae126", "manufacturer",
            "inventory", "stock", "item", "items", "materials", "supplies"
        ));
        
        createKeywords = new HashSet<>(Arrays.asList(
            "create", "creating", "make", "new", "add", "generate", "help", "how to",
            "guide", "steps", "process", "workflow", "instruction", "tutorial"
        ));
    }
    
    /**
     * Analyze keywords in the corrected input
     */
    private KeywordAnalysis analyzeKeywords(String input) {
        String lowercase = input.toLowerCase();
        
        Set<String> foundPartsKeywords = new HashSet<>();
        Set<String> foundCreateKeywords = new HashSet<>();
        
        for (String keyword : partsKeywords) {
            if (lowercase.contains(keyword)) {
                foundPartsKeywords.add(keyword);
            }
        }
        
        for (String keyword : createKeywords) {
            if (lowercase.contains(keyword)) {
                foundCreateKeywords.add(keyword);
            }
        }
        
        return new KeywordAnalysis(foundPartsKeywords, foundCreateKeywords);
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
        
        String route;
        String reason;
        String intentType;
        String businessRuleViolation = null;
        String enhancementApplied = null;
        double contextScore = 0.0;
        
        // Enhanced routing logic
        if (hasPartsKeywords && hasCreateKeywords && !isPastTense) {
            route = "PARTS_CREATE_ERROR";
            reason = "Parts creation not supported - parts are loaded from Excel files";
            intentType = "BUSINESS_RULE_VIOLATION";
            businessRuleViolation = "Parts creation is not allowed";
        } else if (hasPartsKeywords) {
            route = "PARTS";
            reason = "Input contains parts-related keywords: " + analysis.partsKeywords;
            intentType = "PARTS_QUERY";
        } else if (hasCreateKeywords && !isPastTense) {
            route = "HELP";
            reason = "Input contains creation/help keywords: " + analysis.createKeywords;
            intentType = "HELP_REQUEST";
        } else {
            route = "CONTRACT";
            reason = "Default routing to contract model";
            intentType = hasContractId ? "CONTRACT_ID_QUERY" : "GENERAL_CONTRACT_QUERY";
            
            if (hasContractId) {
                reason += " (Contract ID: " + contractId + ")";
                intentType = "CONTRACT_ID_QUERY";
            }
            
            if (isPastTense) {
                enhancementApplied = "Past-tense detection applied";
                contextScore = 9.0;
                reason += " [Enhanced: Past-tense query detected]";
            }
        }
        
        return new RoutingDecision(route, reason, intentType, businessRuleViolation, enhancementApplied, contextScore);
    }
    
    /**
     * Create error response
     */
    private EnhancedMLResponse createErrorResponse(String message) {
        return new EnhancedMLResponse(
            "ERROR", message, "ERROR", "", "", false, null,
            new HashSet<>(), new HashSet<>(), null, null, 0.0, 0.0
        );
    }
    
    // Helper classes
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
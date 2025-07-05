package nlp.correction;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Spell Checker with External Configuration
 * 
 * Maintains external text file for typo-to-correct-word mappings
 * Maps corrected terms to actual database column names
 * 
 * @author Contract/Parts Search System
 * @version 1.0
 */
public class SpellChecker {
    
    private Map<String, String> spellCorrections;
    private Map<String, String> databaseColumnMappings;
    private Map<String, String> contextualCorrections;
    private Set<String> contractTerms;
    private Set<String> partsTerms;
    private Set<String> actionTerms;
    
    private static final String SPELL_CORRECTIONS_FILE = "spell_corrections.txt";
    private static final String DATABASE_MAPPINGS_FILE = "database_mappings.txt";
    private static final String CONTEXTUAL_CORRECTIONS_FILE = "contextual_corrections.txt";
    
    private static SpellChecker instance;
    private long lastModified;
    
    /**
     * Singleton pattern with lazy initialization
     */
    public static synchronized SpellChecker getInstance() {
        if (instance == null) {
            instance = new SpellChecker();
        }
        return instance;
    }
    
    /**
     * Private constructor - loads all correction files
     */
    private SpellChecker() {
        this.spellCorrections = new HashMap<>();
        this.databaseColumnMappings = new HashMap<>();
        this.contextualCorrections = new HashMap<>();
        this.contractTerms = new HashSet<>();
        this.partsTerms = new HashSet<>();
        this.actionTerms = new HashSet<>();
        
        loadAllCorrectionFiles();
        initializeDomainTerms();
    }
    
    /**
     * Load all correction files
     */
    private void loadAllCorrectionFiles() {
        loadSpellCorrections();
        loadDatabaseMappings();
        loadContextualCorrections();
    }
    
    /**
     * Load spell corrections from external file
     */
    private void loadSpellCorrections() {
        spellCorrections.clear();
        
        // Try to load from file first
        if (loadCorrectionsFromFile(SPELL_CORRECTIONS_FILE, spellCorrections)) {
            System.out.println("Loaded spell corrections from file: " + SPELL_CORRECTIONS_FILE);
        } else {
            // Fallback to hardcoded corrections
            loadDefaultSpellCorrections();
            System.out.println("Using default spell corrections (file not found)");
        }
    }
    
    /**
     * Load database column mappings from external file
     */
    private void loadDatabaseMappings() {
        databaseColumnMappings.clear();
        
        if (loadCorrectionsFromFile(DATABASE_MAPPINGS_FILE, databaseColumnMappings)) {
            System.out.println("Loaded database mappings from file: " + DATABASE_MAPPINGS_FILE);
        } else {
            loadDefaultDatabaseMappings();
            System.out.println("Using default database mappings (file not found)");
        }
    }
    
    /**
     * Load contextual corrections from external file
     */
    private void loadContextualCorrections() {
        contextualCorrections.clear();
        
        if (loadCorrectionsFromFile(CONTEXTUAL_CORRECTIONS_FILE, contextualCorrections)) {
            System.out.println("Loaded contextual corrections from file: " + CONTEXTUAL_CORRECTIONS_FILE);
        } else {
            loadDefaultContextualCorrections();
            System.out.println("Using default contextual corrections (file not found)");
        }
    }
    
    /**
     * Generic method to load corrections from file
     */
    private boolean loadCorrectionsFromFile(String fileName, Map<String, String> targetMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineCount = 0;
            
            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse correction mappings (format: wrong_word → correct_word)
                if (line.contains("→") || line.contains("->")) {
                    String[] parts = line.split("→|->", 2);
                    if (parts.length == 2) {
                        String wrong = parts[0].trim().toLowerCase();
                        String correct = parts[1].trim().toLowerCase();
                        targetMap.put(wrong, correct);
                    }
                }
                // Parse simple mappings (format: wrong_word = correct_word)
                else if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String wrong = parts[0].trim().toLowerCase();
                        String correct = parts[1].trim().toLowerCase();
                        targetMap.put(wrong, correct);
                    }
                }
            }
            
            System.out.println("Loaded " + targetMap.size() + " corrections from " + fileName + " (" + lineCount + " lines processed)");
            return true;
            
        } catch (IOException e) {
            System.err.println("Could not load corrections from file: " + fileName + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load default spell corrections (fallback)
     */
    private void loadDefaultSpellCorrections() {
        // Contract-related corrections
        spellCorrections.put("contarct", "contract");
        spellCorrections.put("contrct", "contract");
        spellCorrections.put("cntract", "contract");
        spellCorrections.put("kontrct", "contract");
        spellCorrections.put("contrst", "contract");
        spellCorrections.put("cntrct", "contract");
        spellCorrections.put("contrat", "contract");
        spellCorrections.put("contraxt", "contract");
        
        // Customer-related corrections
        spellCorrections.put("custmr", "customer");
        spellCorrections.put("custmer", "customer");
        spellCorrections.put("customar", "customer");
        spellCorrections.put("custemer", "customer");
        
        // Account-related corrections
        spellCorrections.put("accnt", "account");
        spellCorrections.put("acnt", "account");
        spellCorrections.put("acount", "account");
        spellCorrections.put("accout", "account");
        
        // Parts-related corrections
        spellCorrections.put("prts", "parts");
        spellCorrections.put("partz", "parts");
        spellCorrections.put("parst", "parts");
        spellCorrections.put("prt", "part");
        spellCorrections.put("partes", "parts");
        
        // Action-related corrections
        spellCorrections.put("shw", "show");
        spellCorrections.put("shwo", "show");
        spellCorrections.put("sho", "show");
        spellCorrections.put("dsply", "display");
        spellCorrections.put("disply", "display");
        spellCorrections.put("lst", "list");
        spellCorrections.put("lsit", "list");
        spellCorrections.put("gt", "get");
        spellCorrections.put("retrive", "retrieve");
        spellCorrections.put("retreive", "retrieve");
        spellCorrections.put("fnd", "find");
        spellCorrections.put("serch", "search");
        spellCorrections.put("seach", "search");
        
        // Common word corrections
        spellCorrections.put("detals", "details");
        spellCorrections.put("detils", "details");
        spellCorrections.put("numbr", "number");
        spellCorrections.put("nubmer", "number");
        spellCorrections.put("dat", "date");
        spellCorrections.put("effectiv", "effective");
        spellCorrections.put("expiraion", "expiration");
        spellCorrections.put("expirasion", "expiration");
        spellCorrections.put("staus", "status");
        spellCorrections.put("statys", "status");
        spellCorrections.put("crete", "create");
        spellCorrections.put("creat", "create");
        spellCorrections.put("mak", "make");
        spellCorrections.put("aftr", "after");
        spellCorrections.put("befor", "before");
        spellCorrections.put("beetwen", "between");
        spellCorrections.put("betwen", "between");
    }
    
    /**
     * Load default database mappings (fallback)
     */
    private void loadDefaultDatabaseMappings() {
        // Contract table mappings
        databaseColumnMappings.put("contract", "contract_number");
        databaseColumnMappings.put("contract_number", "contract_number");
        databaseColumnMappings.put("contract_id", "contract_number");
        databaseColumnMappings.put("id", "contract_number");
        
        // Customer mappings
        databaseColumnMappings.put("customer", "customer_name");
        databaseColumnMappings.put("customer_name", "customer_name");
        databaseColumnMappings.put("client", "customer_name");
        databaseColumnMappings.put("client_name", "customer_name");
        
        // Account mappings
        databaseColumnMappings.put("account", "account_number");
        databaseColumnMappings.put("account_number", "account_number");
        databaseColumnMappings.put("account_id", "account_number");
        databaseColumnMappings.put("customer_number", "account_number");
        
        // Parts mappings
        databaseColumnMappings.put("part", "part_number");
        databaseColumnMappings.put("part_number", "part_number");
        databaseColumnMappings.put("part_id", "part_number");
        databaseColumnMappings.put("parts", "part_number");
        
        // User mappings
        databaseColumnMappings.put("user", "created_by");
        databaseColumnMappings.put("created_by", "created_by");
        databaseColumnMappings.put("author", "created_by");
        databaseColumnMappings.put("creator", "created_by");
        
        // Date mappings
        databaseColumnMappings.put("date", "created_date");
        databaseColumnMappings.put("created_date", "created_date");
        databaseColumnMappings.put("creation_date", "created_date");
        databaseColumnMappings.put("effective_date", "effective_date");
        databaseColumnMappings.put("expiration_date", "expiration_date");
        databaseColumnMappings.put("expire_date", "expiration_date");
        
        // Status mappings
        databaseColumnMappings.put("status", "status");
        databaseColumnMappings.put("state", "status");
        databaseColumnMappings.put("condition", "status");
    }
    
    /**
     * Load default contextual corrections (fallback)
     */
    private void loadDefaultContextualCorrections() {
        // Context-specific corrections
        contextualCorrections.put("pull contracts", "get contracts");
        contextualCorrections.put("fetch contracts", "get contracts");
        contextualCorrections.put("bring contracts", "get contracts");
        contextualCorrections.put("grab contracts", "get contracts");
        
        contextualCorrections.put("pull parts", "get parts");
        contextualCorrections.put("fetch parts", "get parts");
        contextualCorrections.put("bring parts", "get parts");
        
        contextualCorrections.put("show me", "show");
        contextualCorrections.put("display me", "display");
        contextualCorrections.put("list me", "list");
        
        contextualCorrections.put("help me", "help");
        contextualCorrections.put("assist me", "help");
        contextualCorrections.put("guide me", "help");
    }
    
    /**
     * Initialize domain-specific terms
     */
    private void initializeDomainTerms() {
        // Contract terms
        contractTerms.addAll(Arrays.asList(
            "contract", "contracts", "agreement", "agreements", "deal", "deals"
        ));
        
        // Parts terms
        partsTerms.addAll(Arrays.asList(
            "part", "parts", "component", "components", "item", "items", 
            "product", "products", "material", "materials", "supply", "supplies"
        ));
        
        // Action terms
        actionTerms.addAll(Arrays.asList(
            "show", "display", "list", "get", "find", "search", "retrieve",
            "pull", "fetch", "bring", "grab", "create", "make", "add", "help"
        ));
    }
    
    /**
     * MAIN METHOD: Apply spell corrections to input text
     */
    public SpellCorrectionResult correctSpelling(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new SpellCorrectionResult(input, input, false, new ArrayList<>());
        }
        
        String originalInput = input;
        String correctedInput = input;
        List<SpellCorrection> corrections = new ArrayList<>();
        
        // Apply contextual corrections first
        correctedInput = applyContextualCorrections(correctedInput, corrections);
        
        // Apply word-level corrections
        correctedInput = applyWordLevelCorrections(correctedInput, corrections);
        
        // Apply database column mappings
        correctedInput = applyDatabaseMappings(correctedInput, corrections);
        
        boolean hasCorrections = !corrections.isEmpty();
        
        return new SpellCorrectionResult(originalInput, correctedInput, hasCorrections, corrections);
    }
    
    /**
     * Apply contextual corrections (phrase-level)
     */
    private String applyContextualCorrections(String input, List<SpellCorrection> corrections) {
        String result = input;
        
        for (Map.Entry<String, String> entry : contextualCorrections.entrySet()) {
            String wrong = entry.getKey();
            String correct = entry.getValue();
            
            if (result.toLowerCase().contains(wrong.toLowerCase())) {
                result = result.replaceAll("(?i)" + Pattern.quote(wrong), correct);
                corrections.add(new SpellCorrection(wrong, correct, "contextual"));
            }
        }
        
        return result;
    }
    
    /**
     * Apply word-level corrections
     */
    private String applyWordLevelCorrections(String input, List<SpellCorrection> corrections) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            String correctedWord = correctSingleWord(word, corrections);
            result.append(correctedWord).append(" ");
        }
        
        return result.toString().trim();
    }
    
    /**
     * Correct a single word (including word+number combinations)
     */
    private String correctSingleWord(String word, List<SpellCorrection> corrections) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        
        String originalWord = word;
        String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        
        // Check for direct match
        if (spellCorrections.containsKey(cleanWord)) {
            String correction = spellCorrections.get(cleanWord);
            corrections.add(new SpellCorrection(originalWord, correction, "direct"));
            return word.replace(cleanWord, correction);
        }
        
        // Check for word+number combinations
        if (cleanWord.matches(".*\\d+.*")) {
            String wordPart = cleanWord.replaceAll("\\d+", "");
            String numberPart = cleanWord.replaceAll("[^0-9]", "");
            
            if (spellCorrections.containsKey(wordPart)) {
                String correction = spellCorrections.get(wordPart) + numberPart;
                corrections.add(new SpellCorrection(originalWord, correction, "word+number"));
                return word.replace(cleanWord, correction);
            }
        }
        
        return word;
    }
    
    /**
     * Apply database column mappings
     */
    private String applyDatabaseMappings(String input, List<SpellCorrection> corrections) {
        String result = input;
        
        for (Map.Entry<String, String> entry : databaseColumnMappings.entrySet()) {
            String fieldName = entry.getKey();
            String columnName = entry.getValue();
            
            if (result.toLowerCase().contains(fieldName.toLowerCase()) && !fieldName.equals(columnName)) {
                result = result.replaceAll("(?i)\\b" + Pattern.quote(fieldName) + "\\b", columnName);
                corrections.add(new SpellCorrection(fieldName, columnName, "database_mapping"));
            }
        }
        
        return result;
    }
    
    /**
     * Get suggestions for unrecognized terms
     */
    public List<String> getSuggestions(String term) {
        List<String> suggestions = new ArrayList<>();
        String lowerTerm = term.toLowerCase();
        
        // Find close matches using edit distance
        for (String correction : spellCorrections.keySet()) {
            if (calculateEditDistance(lowerTerm, correction) <= 2) {
                suggestions.add(spellCorrections.get(correction));
            }
        }
        
        // Add domain-specific suggestions
        if (lowerTerm.contains("contract") || lowerTerm.contains("contrct")) {
            suggestions.addAll(contractTerms);
        }
        
        if (lowerTerm.contains("part") || lowerTerm.contains("prt")) {
            suggestions.addAll(partsTerms);
        }
        
        // Remove duplicates and limit results
        return suggestions.stream()
                .distinct()
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Calculate edit distance between two strings
     */
    private int calculateEditDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Check if term is a domain-specific term
     */
    public boolean isDomainTerm(String term) {
        String lowerTerm = term.toLowerCase();
        return contractTerms.contains(lowerTerm) || 
               partsTerms.contains(lowerTerm) || 
               actionTerms.contains(lowerTerm);
    }
    
    /**
     * Get database column name for a field
     */
    public String getDatabaseColumnName(String fieldName) {
        return databaseColumnMappings.getOrDefault(fieldName.toLowerCase(), fieldName);
    }
    
    /**
     * Reload corrections from files (for hot-reload functionality)
     */
    public void reloadCorrections() {
        loadAllCorrectionFiles();
        System.out.println("Spell corrections reloaded from files");
    }
    
    /**
     * Spell Correction Result class
     */
    public static class SpellCorrectionResult {
        private String originalText;
        private String correctedText;
        private boolean hasCorrections;
        private List<SpellCorrection> corrections;
        
        public SpellCorrectionResult(String originalText, String correctedText, 
                                   boolean hasCorrections, List<SpellCorrection> corrections) {
            this.originalText = originalText;
            this.correctedText = correctedText;
            this.hasCorrections = hasCorrections;
            this.corrections = corrections;
        }
        
        // Getters
        public String getOriginalText() { return originalText; }
        public String getCorrectedText() { return correctedText; }
        public boolean isHasCorrections() { return hasCorrections; }
        public List<SpellCorrection> getCorrections() { return corrections; }
        
        @Override
        public String toString() {
            return String.format("SpellCorrectionResult{original='%s', corrected='%s', hasCorrections=%s, corrections=%d}", 
                               originalText, correctedText, hasCorrections, corrections.size());
        }
    }
    
    /**
     * Individual Spell Correction class
     */
    public static class SpellCorrection {
        private String original;
        private String corrected;
        private String type;
        
        public SpellCorrection(String original, String corrected, String type) {
            this.original = original;
            this.corrected = corrected;
            this.type = type;
        }
        
        // Getters
        public String getOriginal() { return original; }
        public String getCorrected() { return corrected; }
        public String getType() { return type; }
        
        @Override
        public String toString() {
            return String.format("%s → %s (%s)", original, corrected, type);
        }
    }
}
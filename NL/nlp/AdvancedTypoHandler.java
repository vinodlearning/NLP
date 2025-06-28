package view.nlp;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Advanced Typo Handler for Enhanced NLP Processing
 * Provides sophisticated spell checking, domain-specific corrections,
 * and contextual typo detection for natural language processing
 */
public class AdvancedTypoHandler {

    // Core dictionaries and data structures
    private Map<String, String> domainDictionary;
    private Map<String, List<String>> commonTypos;
    private Set<String> validWords;
    private Map<String, String> abbreviations;
    private Map<String, String> contextualCorrections;
    private Map<String, Double> wordFrequency;

    // Configuration constants
    private static final int MAX_EDIT_DISTANCE = 2;
    private static final double SIMILARITY_THRESHOLD = 0.75;
    private static final int MAX_SUGGESTIONS = 5;

    // Pattern matching for special cases
    private static final Pattern CONTRACT_PATTERN =
        Pattern.compile("\\b(contract|cont)\\s*#?\\s*(\\d+)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+\\b");

    /**
     * Constructor initializes all dictionaries and correction mechanisms
     */
    public AdvancedTypoHandler() {
        initializeDomainDictionary();
        initializeCommonTypos();
        initializeAbbreviations();
        initializeValidWords();
        initializeContextualCorrections();
        initializeWordFrequency();
    }

    /**
     * Initialize domain-specific dictionary for business and contract terms
     */
    private void initializeDomainDictionary() {
        domainDictionary = new HashMap<>();

        // Contract-related terms
        domainDictionary.put("contarct", "contract");
        domainDictionary.put("contrct", "contract");
        domainDictionary.put("contrat", "contract");
        domainDictionary.put("contraact", "contract");
        domainDictionary.put("contractt", "contract");
        domainDictionary.put("contracct", "contract");

        // Customer/Client terms
        domainDictionary.put("custmer", "customer");
        domainDictionary.put("cusotmer", "customer");
        domainDictionary.put("customr", "customer");
        domainDictionary.put("customar", "customer");
        domainDictionary.put("clinet", "client");
        domainDictionary.put("cleint", "client");
        domainDictionary.put("cilent", "client");

        // Action verbs
        domainDictionary.put("retreive", "retrieve");
        domainDictionary.put("retrive", "retrieve");
        domainDictionary.put("retreave", "retrieve");
        domainDictionary.put("serach", "search");
        domainDictionary.put("seach", "search");
        domainDictionary.put("searh", "search");
        domainDictionary.put("updat", "update");
        domainDictionary.put("updaet", "update");
        domainDictionary.put("delet", "delete");
        domainDictionary.put("deleet", "delete");

        // Status terms
        domainDictionary.put("activ", "active");
        domainDictionary.put("actve", "active");
        domainDictionary.put("inactiv", "inactive");
        domainDictionary.put("inactve", "inactive");
        domainDictionary.put("expird", "expired");
        domainDictionary.put("expir", "expire");
        domainDictionary.put("expiry", "expire");

        // Business terms
        domainDictionary.put("invoic", "invoice");
        domainDictionary.put("invioce", "invoice");
        domainDictionary.put("paymet", "payment");
        domainDictionary.put("paymnt", "payment");
        domainDictionary.put("paymetn", "payment");
        domainDictionary.put("accont", "account");
        domainDictionary.put("acount", "account");
        domainDictionary.put("accout", "account");
        domainDictionary.put("addres", "address");
        domainDictionary.put("adress", "address");
        domainDictionary.put("adres", "address");
    }

    /**
     * Initialize common typing mistakes and their corrections
     */
    private void initializeCommonTypos() {
        commonTypos = new HashMap<>();

        // Common letter transpositions
        commonTypos.put("teh", Arrays.asList("the"));
        commonTypos.put("adn", Arrays.asList("and"));
        commonTypos.put("fo", Arrays.asList("of", "for"));
        commonTypos.put("ot", Arrays.asList("to", "of"));
        commonTypos.put("si", Arrays.asList("is"));
        commonTypos.put("cna", Arrays.asList("can"));
        commonTypos.put("yuo", Arrays.asList("you"));
        commonTypos.put("taht", Arrays.asList("that"));
        commonTypos.put("whta", Arrays.asList("what"));
        commonTypos.put("hwo", Arrays.asList("how"));
        commonTypos.put("whne", Arrays.asList("when"));
        commonTypos.put("whre", Arrays.asList("where"));
        commonTypos.put("wihch", Arrays.asList("which"));
        commonTypos.put("thier", Arrays.asList("their"));
        commonTypos.put("recieve", Arrays.asList("receive"));
        commonTypos.put("seperate", Arrays.asList("separate"));
        commonTypos.put("occured", Arrays.asList("occurred"));
        commonTypos.put("definately", Arrays.asList("definitely"));
        commonTypos.put("neccessary", Arrays.asList("necessary"));

        // Business-specific common typos
        commonTypos.put("managment", Arrays.asList("management"));
        commonTypos.put("buisness", Arrays.asList("business"));
        commonTypos.put("reccomend", Arrays.asList("recommend"));
        commonTypos.put("proffesional", Arrays.asList("professional"));
        commonTypos.put("sucessful", Arrays.asList("successful"));
    }

    /**
     * Initialize abbreviations and their full forms
     */
    private void initializeAbbreviations() {
        abbreviations = new HashMap<>();

        // Business abbreviations
        abbreviations.put("cont", "contract");
        abbreviations.put("cust", "customer");
        abbreviations.put("info", "information");
        abbreviations.put("num", "number");
        abbreviations.put("addr", "address");
        abbreviations.put("tel", "telephone");
        abbreviations.put("ph", "phone");
        abbreviations.put("acct", "account");
        abbreviations.put("inv", "invoice");
        abbreviations.put("qty", "quantity");
        abbreviations.put("amt", "amount");
        abbreviations.put("desc", "description");
        abbreviations.put("stat", "status");
        abbreviations.put("exp", "expire");
        abbreviations.put("upd", "update");
        abbreviations.put("del", "delete");
        abbreviations.put("mod", "modify");
        abbreviations.put("mgmt", "management");
        abbreviations.put("dept", "department");
        abbreviations.put("org", "organization");
        abbreviations.put("corp", "corporation");
        abbreviations.put("ltd", "limited");
        abbreviations.put("inc", "incorporated");
    }

    /**
     * Initialize valid words dictionary
     */
    private void initializeValidWords() {
        validWords = new HashSet<>();

        // Business and contract terms
        validWords.addAll(Arrays.asList("contract", "customer", "client", "account", "invoice", "payment", "active",
                                        "inactive", "expired", "pending", "approved", "rejected", "cancelled",
                                        "terminated", "renewed", "extended", "modified", "search", "find", "get",
                                        "retrieve", "update", "delete", "add", "modify", "create", "remove", "change",
                                        "edit", "view", "display", "show", "information", "details", "status", "number",
                                        "address", "phone", "email", "date", "amount", "quantity", "description",
                                        "name", "id", "type", "category", "department", "organization", "company",
                                        "business", "management", "manager", "employee", "staff", "team", "group",
                                        "project", "task", "assignment", "deadline", "schedule", "meeting", "report",
                                        "document", "file", "record", "database", "system"));

        // Common words
        validWords.addAll(Arrays.asList("show", "display", "list", "all", "by", "with", "for", "from", "to", "what",
                                        "when", "where", "how", "who", "which", "why", "can", "could", "would",
                                        "should", "will", "shall", "may", "might", "must", "need", "want", "like",
                                        "have", "has", "had", "is", "are", "was", "were", "been", "being", "do", "does",
                                        "did", "done", "doing", "go", "goes", "went", "gone", "going", "come", "comes",
                                        "came", "coming", "see", "saw", "seen", "seeing", "know", "knew", "known",
                                        "knowing", "think", "thought", "thinking", "say", "said", "saying", "tell",
                                        "told", "telling", "ask", "asked", "asking", "give", "gave", "given", "giving",
                                        "take", "took", "taken", "taking", "make", "made", "making", "work", "worked",
                                        "working", "help", "helped", "helping", "use", "used", "using", "the", "a",
                                        "an", "and", "or", "but", "if", "then", "else", "not", "no", "yes", "ok",
                                        "okay", "please", "thank", "thanks", "sorry", "excuse", "hello", "hi", "bye",
                                        "goodbye", "good", "bad", "best", "better", "worse", "worst", "new", "old",
                                        "first", "last", "next", "previous", "current", "recent", "latest", "early",
                                        "late", "now", "today", "tomorrow", "yesterday", "week", "month", "year", "day",
                                        "time", "hour", "minute", "second", "morning", "afternoon", "evening", "night",
                                        "here", "there", "everywhere", "somewhere", "nowhere", "this", "that", "these",
                                        "those", "my", "your", "his", "her", "its", "our", "their", "me", "you", "him",
                                        "her", "us", "them", "myself", "yourself", "himself", "herself", "itself",
                                        "ourselves", "themselves"));
    }

    /**
     * Initialize contextual corrections based on surrounding words
     */
    private void initializeContextualCorrections() {
        contextualCorrections = new HashMap<>();

        // Context-dependent corrections
        contextualCorrections.put("contract_number", "contract number");
        contextualCorrections.put("customer_info", "customer information");
        contextualCorrections.put("payment_status", "payment status");
        contextualCorrections.put("account_details", "account details");
        contextualCorrections.put("invoice_date", "invoice date");
        contextualCorrections.put("due_date", "due date");
        contextualCorrections.put("expiry_date", "expiry date");
        contextualCorrections.put("start_date", "start date");
        contextualCorrections.put("end_date", "end date");
    }

    /**
     * Initialize word frequency for better correction suggestions
     */
    private void initializeWordFrequency() {
        wordFrequency = new HashMap<>();

        // High frequency business terms
        wordFrequency.put("contract", 0.95);
        wordFrequency.put("customer", 0.90);
        wordFrequency.put("account", 0.85);
        wordFrequency.put("payment", 0.80);
        wordFrequency.put("invoice", 0.75);
        wordFrequency.put("information", 0.70);
        wordFrequency.put("status", 0.65);
        wordFrequency.put("number", 0.60);
        wordFrequency.put("search", 0.55);
        wordFrequency.put("update", 0.50);

        // Medium frequency terms
        wordFrequency.put("active", 0.45);
        wordFrequency.put("expired", 0.40);
        wordFrequency.put("pending", 0.35);
        wordFrequency.put("approved", 0.30);
        wordFrequency.put("details", 0.25);

        // Common words
        wordFrequency.put("the", 1.0);
        wordFrequency.put("and", 0.98);
        wordFrequency.put("of", 0.96);
        wordFrequency.put("to", 0.94);
        wordFrequency.put("a", 0.92);
        wordFrequency.put("is", 0.90);
        wordFrequency.put("for", 0.88);
        wordFrequency.put("with", 0.86);
        wordFrequency.put("by", 0.84);
        wordFrequency.put("from", 0.82);
    }

    /**
     * Main method to correct typos in input text
     */
    public String correctTypos(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // Preserve special patterns (emails, phones, numbers, etc.)
        Map<String, String> preservedPatterns = extractAndPreservePatterns(input);
        String workingText = input;

        // Replace preserved patterns with placeholders
        for (Map.Entry<String, String> entry : preservedPatterns.entrySet()) {
            workingText = workingText.replace(entry.getValue(), entry.getKey());
        }

        // Process words
        String[] words = workingText.split("\\s+");
        List<String> correctedWords = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String cleanWord = cleanWord(word);
            String correctedWord = correctSingleWord(cleanWord, getContext(words, i));

            // Preserve original casing and punctuation
            if (!cleanWord.equals(correctedWord)) {
                correctedWord = preserveCaseAndPunctuation(word, correctedWord);
            } else {
                correctedWord = word; // Keep original if no correction
            }

            correctedWords.add(correctedWord);
        }

        String result = String.join(" ", correctedWords);

        // Restore preserved patterns
        for (Map.Entry<String, String> entry : preservedPatterns.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Extract and preserve special patterns that shouldn't be corrected
     */
    private Map<String, String> extractAndPreservePatterns(String input) {
        Map<String, String> patterns = new HashMap<>();
        int counter = 0;

        // Preserve email addresses
        java.util.regex.Matcher emailMatcher = EMAIL_PATTERN.matcher(input);
        while (emailMatcher.find()) {
            String placeholder = "__EMAIL_" + (counter++) + "__";
            patterns.put(placeholder, emailMatcher.group());
        }

        // Preserve phone numbers
        java.util.regex.Matcher phoneMatcher = PHONE_PATTERN.matcher(input);
        while (phoneMatcher.find()) {
            String placeholder = "__PHONE_" + (counter++) + "__";
            patterns.put(placeholder, phoneMatcher.group());
        }

        // Preserve contract numbers
        java.util.regex.Matcher contractMatcher = CONTRACT_PATTERN.matcher(input);
        while (contractMatcher.find()) {
            String placeholder = "__CONTRACT_" + (counter++) + "__";
            patterns.put(placeholder, contractMatcher.group());
        }

        return patterns;
    }

    /**
     * Get context words around the current word for contextual correction
     */
    private String[] getContext(String[] words, int currentIndex) {
        List<String> context = new ArrayList<>();

        // Add previous word
        if (currentIndex > 0) {
            context.add(cleanWord(words[currentIndex - 1]).toLowerCase());
        }

        // Add next word
        if (currentIndex < words.length - 1) {
            context.add(cleanWord(words[currentIndex + 1]).toLowerCase());
        }

        return context.toArray(new String[0]);
    }

    /**
     * Correct a single word using various strategies with context
     */
    private String correctSingleWord(String word, String[] context) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        String lowerWord = word.toLowerCase();

        // Check if word is already valid
        if (validWords.contains(lowerWord)) {
            return word;
        }

        // Check domain dictionary first (highest priority)
        if (domainDictionary.containsKey(lowerWord)) {
            return domainDictionary.get(lowerWord);
        }

        // Check common typos
        if (commonTypos.containsKey(lowerWord)) {
            List<String> suggestions = commonTypos.get(lowerWord);
            return selectBestSuggestion(suggestions, context);
        }

        // Check abbreviations
        if (abbreviations.containsKey(lowerWord)) {
            return abbreviations.get(lowerWord);
        }

        // Try contextual corrections
        String contextualCorrection = findContextualCorrection(lowerWord, context);
        if (contextualCorrection != null) {
            return contextualCorrection;
        }

        // Try edit distance correction
        String editDistanceCorrection = findBestEditDistanceMatch(lowerWord);
        if (editDistanceCorrection != null) {
            return editDistanceCorrection;
        }

        // Try phonetic similarity
        String phoneticCorrection = findPhoneticMatch(lowerWord);
        if (phoneticCorrection != null) {
            return phoneticCorrection;
        }

        // Try keyboard proximity correction
        String keyboardCorrection = findKeyboardProximityMatch(lowerWord);
        if (keyboardCorrection != null) {
            return keyboardCorrection;
        }

        // Return original if no correction found
        return word;
    }

    /**
     * Select best suggestion based on context
     */
    private String selectBestSuggestion(List<String> suggestions, String[] context) {
        if (suggestions.isEmpty()) {
            return suggestions.get(0);
        }

        if (context.length == 0) {
            return suggestions.get(0);
        }

        // Score suggestions based on context and frequency
        Map<String, Double> scores = new HashMap<>();
        for (String suggestion : suggestions) {
            double score = wordFrequency.getOrDefault(suggestion, 0.1);

            // Boost score if suggestion appears in context
            for (String contextWord : context) {
                if (isRelated(suggestion, contextWord)) {
                    score += 0.3;
                }
            }

            scores.put(suggestion, score);
        }

        return scores.entrySet()
                     .stream()
                     .max(Map.Entry.comparingByValue())
                     .map(Map.Entry::getKey)
                     .orElse(suggestions.get(0));
    }

    /**
     * Check if two words are semantically related
     */
    private boolean isRelated(String word1, String word2) {
        // Simple semantic relationship check
        Set<String> contractTerms = new HashSet<>(Arrays.asList("contract", "agreement", "deal", "number", "id"));
        Set<String> customerTerms = new HashSet<>(Arrays.asList("customer", "client", "account", "user", "person"));
        Set<String> paymentTerms = new HashSet<>(Arrays.asList("payment", "invoice", "bill", "amount", "money"));
        Set<String> statusTerms = new HashSet<>(Arrays.asList("status", "state", "condition", "active", "inactive"));

        return (contractTerms.contains(word1) && contractTerms.contains(word2)) ||
               (customerTerms.contains(word1) && customerTerms.contains(word2)) ||
               (paymentTerms.contains(word1) && paymentTerms.contains(word2)) ||
               (statusTerms.contains(word1) && statusTerms.contains(word2));
    }

    /**
     * Find contextual correction based on surrounding words
     */
    private String findContextualCorrection(String word, String[] context) {
        for (String contextWord : context) {
            String key = contextWord + "_" + word;
            if (contextualCorrections.containsKey(key)) {
                return contextualCorrections.get(key);
            }

            key = word + "_" + contextWord;
            if (contextualCorrections.containsKey(key)) {
                return contextualCorrections.get(key);
            }
        }
        return null;
    }

    /**
     * Find best match using edit distance with frequency weighting
     */
    private String findBestEditDistanceMatch(String word) {
        String bestMatch = null;
        double bestScore = 0.0;

        for (String validWord : validWords) {
            int distance = calculateEditDistance(word, validWord);
            if (distance <= MAX_EDIT_DISTANCE) {
                double similarity = 1.0 - (double) distance / Math.max(word.length(), validWord.length());
                double frequency = wordFrequency.getOrDefault(validWord, 0.1);
                double score = similarity * 0.7 + frequency * 0.3;

                if (score > bestScore && score >= SIMILARITY_THRESHOLD) {
                    bestScore = score;
                    bestMatch = validWord;
                }
            }
        }

        return bestMatch;
    }

    /**
     * Calculate Levenshtein distance between two strings
     */
    private int calculateEditDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Find phonetic match using improved soundex algorithm
     */
    private String findPhoneticMatch(String word) {
        String wordSoundex = generateSoundex(word);

        for (String validWord : validWords) {
            if (generateSoundex(validWord).equals(wordSoundex) && Math.abs(word.length() - validWord.length()) <= 2) {
                return validWord;
            }
        }

        return null;
    }

    /**
     * Find match based on keyboard proximity (common typing errors)
     */
    private String findKeyboardProximityMatch(String word) {
        Map<Character, String> keyboardMap = getKeyboardProximityMap();

        for (String validWord : validWords) {
            if (isKeyboardProximityMatch(word, validWord, keyboardMap)) {
                return validWord;
            }
        }

        return null;
    }

    /**
     * Get keyboard proximity mapping for common typing errors
     */
    private Map<Character, String> getKeyboardProximityMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('q', "wa");
        map.put('w', "qeas");
        map.put('e', "wrds");
        map.put('r', "etdf");
        map.put('t', "ryfg");
        map.put('y', "tugh");
        map.put('u', "yihj");
        map.put('i', "uojk");
        map.put('o', "ipkl");
        map.put('p', "ol");
        map.put('a', "qwsz");
        map.put('s', "awedxz");
        map.put('d', "serfcx");
        map.put('f', "drtgvc");
        map.put('g', "ftyhbv");
        map.put('h', "gyujnb");
        map.put('j', "huikmn");
        map.put('k', "jiolm");
        map.put('l', "kop");
        map.put('z', "asx");
        map.put('x', "zsdc");
        map.put('c', "xdfv");
        map.put('v', "cfgb");
        map.put('b', "vghn");
        map.put('n', "bhjm");
        map.put('m', "njk");

        return map;
    }

    /**
     * Check if two words match based on keyboard proximity
     */
    private boolean isKeyboardProximityMatch(String word1, String word2, Map<Character, String> keyboardMap) {
        if (Math.abs(word1.length() - word2.length()) > 1) {
            return false;
        }

        int differences = 0;
        int minLength = Math.min(word1.length(), word2.length());

        for (int i = 0; i < minLength; i++) {
            char c1 = word1.charAt(i);
            char c2 = word2.charAt(i);

            if (c1 != c2) {
                String proximityChars = keyboardMap.get(c1);
                if (proximityChars == null || !proximityChars.contains(String.valueOf(c2))) {
                    differences++;
                    if (differences > 1) {
                        return false;
                    }
                }
            }
        }

        return differences <= 1;
    }

    /**
     * Generate improved soundex code for phonetic matching
     */
    private String generateSoundex(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        word = word.toLowerCase().replaceAll("[^a-z]", "");
        if (word.isEmpty()) {
            return "";
        }

        StringBuilder soundex = new StringBuilder();
        soundex.append(Character.toUpperCase(word.charAt(0)));

        String previousCode = getSoundexCode(word.charAt(0));

        for (int i = 1; i < word.length() && soundex.length() < 4; i++) {
            String code = getSoundexCode(word.charAt(i));
            if (!code.isEmpty() && !code.equals(previousCode)) {
                soundex.append(code);
                previousCode = code;
            }
        }

        // Pad with zeros if necessary
        while (soundex.length() < 4) {
            soundex.append('0');
        }

        return soundex.toString();
    }

    /**
     * Get soundex code for a character
     */
    private String getSoundexCode(char c) {
        switch (c) {
        case 'b':
        case 'f':
        case 'p':
        case 'v':
            return "1";
        case 'c':
        case 'g':
        case 'j':
        case 'k':
        case 'q':
        case 's':
        case 'x':
        case 'z':
            return "2";
        case 'd':
        case 't':
            return "3";
        case 'l':
            return "4";
        case 'm':
        case 'n':
            return "5";
        case 'r':
            return "6";
        default:
            return "";
        }
    }

    /**
     * Clean word by removing punctuation while preserving structure
     */
    private String cleanWord(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        // Remove leading and trailing punctuation but keep internal structure
        return word.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
    }

    /**
     * Preserve original case pattern and punctuation when applying correction
     */
    private String preserveCaseAndPunctuation(String original, String corrected) {
        if (original == null || original.isEmpty() || corrected == null || corrected.isEmpty()) {
            return corrected;
        }

        // Extract leading and trailing punctuation
        String leadingPunct = "";
        String trailingPunct = "";
        String cleanOriginal = original;

        // Extract leading punctuation
        int start = 0;
        while (start < original.length() && !Character.isLetterOrDigit(original.charAt(start))) {
            leadingPunct += original.charAt(start);
            start++;
        }

        // Extract trailing punctuation
        int end = original.length() - 1;
        while (end >= start && !Character.isLetterOrDigit(original.charAt(end))) {
            trailingPunct = original.charAt(end) + trailingPunct;
            end--;
        }

        if (start <= end) {
            cleanOriginal = original.substring(start, end + 1);
        }

        // Apply case pattern
        String caseCorrected = applyCasePattern(cleanOriginal, corrected);

        return leadingPunct + caseCorrected + trailingPunct;
    }

    /**
     * Apply the case pattern from original word to corrected word
     */
    private String applyCasePattern(String original, String corrected) {
        if (original.isEmpty() || corrected.isEmpty()) {
            return corrected;
        }

        StringBuilder result = new StringBuilder();

        // Check if original is all uppercase
        if (original.equals(original.toUpperCase())) {
            return corrected.toUpperCase();
        }

        // Check if original is all lowercase
        if (original.equals(original.toLowerCase())) {
            return corrected.toLowerCase();
        }

        // Check if original is title case (first letter uppercase)
        if (Character.isUpperCase(original.charAt(0)) &&
            original.substring(1).equals(original.substring(1).toLowerCase())) {
            return Character.toUpperCase(corrected.charAt(0)) +
                   (corrected.length() > 1 ? corrected.substring(1).toLowerCase() : "");
        }

        // Apply character-by-character case pattern
        for (int i = 0; i < corrected.length(); i++) {
            char correctedChar = corrected.charAt(i);

            if (i < original.length()) {
                char originalChar = original.charAt(i);
                if (Character.isUpperCase(originalChar)) {
                    result.append(Character.toUpperCase(correctedChar));
                } else {
                    result.append(Character.toLowerCase(correctedChar));
                }
            } else {
                // For extra characters, use lowercase
                result.append(Character.toLowerCase(correctedChar));
            }
        }

        return result.toString();
    }

    /**
     * Get multiple suggestions for a word with confidence scores
     */
    public List<TypoSuggestion> getSuggestions(String word) {
        List<TypoSuggestion> suggestions = new ArrayList<>();

        if (word == null || word.trim().isEmpty()) {
            return suggestions;
        }

        String cleanWord = cleanWord(word.toLowerCase());

        // Add domain dictionary suggestions
        if (domainDictionary.containsKey(cleanWord)) {
            suggestions.add(new TypoSuggestion(domainDictionary.get(cleanWord), 0.95, "Domain Dictionary"));
        }

        // Add common typo suggestions
        if (commonTypos.containsKey(cleanWord)) {
            List<String> typoSuggestions = commonTypos.get(cleanWord);
            for (int i = 0; i < typoSuggestions.size() && i < 3; i++) {
                suggestions.add(new TypoSuggestion(typoSuggestions.get(i), 0.90 - i * 0.1, "Common Typos"));
            }
        }

        // Add abbreviation suggestions
        if (abbreviations.containsKey(cleanWord)) {
            suggestions.add(new TypoSuggestion(abbreviations.get(cleanWord), 0.85, "Abbreviation"));
        }

        // Add edit distance suggestions
        validWords.stream()
                  .filter(validWord -> {
            int distance = calculateEditDistance(cleanWord, validWord);
            return distance > 0 && distance <= MAX_EDIT_DISTANCE;
        }).sorted((w1, w2) -> {
            int d1 = calculateEditDistance(cleanWord, w1);
            int d2 = calculateEditDistance(cleanWord, w2);
            if (d1 != d2)
                return Integer.compare(d1, d2);
            return Double.compare(wordFrequency.getOrDefault(w2, 0.1), wordFrequency.getOrDefault(w1, 0.1));
        }).limit(MAX_SUGGESTIONS - suggestions.size())
            .forEach(validWord -> {
            int distance = calculateEditDistance(cleanWord, validWord);
            double confidence = 0.8 - (distance * 0.2);
            suggestions.add(new TypoSuggestion(validWord, confidence, "Edit Distance"));
        });

        // Sort by confidence and return top suggestions
        return suggestions.stream()
                          .sorted((s1, s2) -> Double.compare(s2.getConfidence(), s1.getConfidence()))
                          .limit(MAX_SUGGESTIONS)
                          .collect(Collectors.toList());
    }

    /**
     * Check if a word needs correction
     */
    public boolean needsCorrection(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        String cleanWord = cleanWord(word.toLowerCase());

        // Don't correct numbers, emails, phones, etc.
        if (NUMBER_PATTERN.matcher(word).matches() || EMAIL_PATTERN.matcher(word).matches() ||
            PHONE_PATTERN.matcher(word).matches()) {
            return false;
        }

        return !validWords.contains(cleanWord) &&
               (domainDictionary.containsKey(cleanWord) || commonTypos.containsKey(cleanWord) ||
                abbreviations.containsKey(cleanWord) || findBestEditDistanceMatch(cleanWord) != null);
    }

    /**
     * Get correction confidence for a word
     */
    public double getCorrectionConfidence(String original, String corrected) {
        if (original == null || corrected == null || original.equals(corrected)) {
            return 1.0;
        }

        String cleanOriginal = cleanWord(original.toLowerCase());
        String cleanCorrected = corrected.toLowerCase();

        // High confidence for domain dictionary
        if (domainDictionary.containsKey(cleanOriginal) && domainDictionary.get(cleanOriginal).equals(cleanCorrected)) {
            return 0.95;
        }

        // High confidence for common typos
        if (commonTypos.containsKey(cleanOriginal) && commonTypos.get(cleanOriginal).contains(cleanCorrected)) {
            return 0.90;
        }

        // Medium confidence for abbreviations
        if (abbreviations.containsKey(cleanOriginal) && abbreviations.get(cleanOriginal).equals(cleanCorrected)) {
            return 0.85;
        }

        // Calculate confidence based on edit distance and frequency
        int distance = calculateEditDistance(cleanOriginal, cleanCorrected);
        if (distance <= MAX_EDIT_DISTANCE) {
            double similarity = 1.0 - (double) distance / Math.max(cleanOriginal.length(), cleanCorrected.length());
            double frequency = wordFrequency.getOrDefault(cleanCorrected, 0.1);
            return similarity * 0.7 + frequency * 0.3;
        }

        return 0.0;
    }

    /**
     * Add custom word to valid words dictionary
     */
    public void addValidWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            validWords.add(word.toLowerCase().trim());
        }
    }

    /**
     * Add custom typo correction
     */
    public void addTypoCorrection(String typo, String correction) {
        if (typo != null && correction != null && !typo.trim().isEmpty() && !correction.trim().isEmpty()) {
            domainDictionary.put(typo.toLowerCase().trim(), correction.toLowerCase().trim());
        }
    }

    /**
     * Remove word from valid words dictionary
     */
    public void removeValidWord(String word) {
        if (word != null && !word.trim().isEmpty()) {
            validWords.remove(word.toLowerCase().trim());
        }
    }

    /**
     * Get statistics about the typo handler
     */
    public TypoHandlerStats getStats() {
        return new TypoHandlerStats(validWords.size(), domainDictionary.size(), commonTypos.size(),
                                    abbreviations.size(), contextualCorrections.size());
    }

    /**
     * Inner class for typo suggestions with confidence scores
     */
    public static class TypoSuggestion {
        private final String suggestion;
        private final double confidence;
        private final String source;

        public TypoSuggestion(String suggestion, double confidence, String source) {
            this.suggestion = suggestion;
            this.confidence = confidence;
            this.source = source;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getSource() {
            return source;
        }

        @Override
        public String toString() {
            return String.format("TypoSuggestion{suggestion='%s', confidence=%.2f, source='%s'}", suggestion,
                                 confidence, source);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            TypoSuggestion that = (TypoSuggestion) obj;
            return Objects.equals(suggestion, that.suggestion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(suggestion);
        }
    }

    /**
     * Inner class for typo handler statistics
     */
    public static class TypoHandlerStats {
        private final int validWordsCount;
        private final int domainDictionaryCount;
        private final int commonTyposCount;
        private final int abbreviationsCount;
        private final int contextualCorrectionsCount;

        public TypoHandlerStats(int validWordsCount, int domainDictionaryCount, int commonTyposCount,
                                int abbreviationsCount, int contextualCorrectionsCount) {
            this.validWordsCount = validWordsCount;
            this.domainDictionaryCount = domainDictionaryCount;
            this.commonTyposCount = commonTyposCount;
            this.abbreviationsCount = abbreviationsCount;
            this.contextualCorrectionsCount = contextualCorrectionsCount;
        }

        public int getValidWordsCount() {
            return validWordsCount;
        }

        public int getDomainDictionaryCount() {
            return domainDictionaryCount;
        }

        public int getCommonTyposCount() {
            return commonTyposCount;
        }

        public int getAbbreviationsCount() {
            return abbreviationsCount;
        }

        public int getContextualCorrectionsCount() {
            return contextualCorrectionsCount;
        }

        @Override
        public String toString() {
            return String.format("TypoHandlerStats{validWords=%d, domainDict=%d, commonTypos=%d, abbreviations=%d, contextual=%d}",
                                 validWordsCount, domainDictionaryCount, commonTyposCount, abbreviationsCount,
                                 contextualCorrectionsCount);
        }
    }
}

package view.nlp;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.text.Normalizer;

/**
 * QueryNormalizer - Advanced query preprocessing and normalization for chatbot applications
 * Handles text cleaning, standardization, expansion, and preparation for NLP processing
 */
public class QueryNormalizer {
    
    // Normalization configuration
    private static final boolean ENABLE_SPELL_CORRECTION = true;
    private static final boolean ENABLE_ABBREVIATION_EXPANSION = true;
    private static final boolean ENABLE_SYNONYM_REPLACEMENT = true;
    private static final boolean ENABLE_STEMMING = true;
    private static final boolean ENABLE_STOPWORD_REMOVAL = false; // Keep for chatbot context
    private static final boolean ENABLE_PROFANITY_FILTERING = true;
    private static final boolean ENABLE_EMOJI_PROCESSING = true;
    
    // Regex patterns for various normalizations
    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "\\b(?:\\+?1[-\\s]?)?\\(?([0-9]{3})\\)?[-\\s]?([0-9]{3})[-\\s]?([0-9]{4})\\b");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^\\w\\s\\-.,!?@#$%&*()+=\\[\\]{}|;:'\"/\\\\<>]");
    private static final Pattern REPEATED_CHARS = Pattern.compile("(.)\\1{2,}");
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>");
    private static final Pattern MARKDOWN_PATTERN = Pattern.compile("\\*\\*([^*]+)\\*\\*|\\*([^*]+)\\*|__([^_]+)__|_([^_]+)_");
    
    // Dictionaries and lookup tables
    private final Map<String, String> abbreviationMap;
    private final Map<String, String> synonymMap;
    private final Map<String, String> contractionMap;
    private final Map<String, String> slangMap;
    private final Map<String, String> businessTermMap;
    private final Map<String, String> emojiMap;
    private final Set<String> stopWords;
    private final Set<String> profanityWords;
    private final Set<String> businessStopWords;
    private final Map<String, String> commonMisspellings;
    private final Map<String, String> domainSpecificTerms;
    
    // Statistics
    private QueryNormalizationStats stats;
    
    // Configuration
    private boolean preserveCase = false;
    private boolean preservePunctuation = true;
    private boolean preserveNumbers = true;
    private boolean preserveEmails = true;
    private boolean preserveUrls = true;
    private boolean preservePhones = true;
    private int maxQueryLength = 1000;
    private int minQueryLength = 1;
    
    /**
     * Constructor
     */
    public QueryNormalizer() {
        this.abbreviationMap = new HashMap<>();
        this.synonymMap = new HashMap<>();
        this.contractionMap = new HashMap<>();
        this.slangMap = new HashMap<>();
        this.businessTermMap = new HashMap<>();
        this.emojiMap = new HashMap<>();
        this.stopWords = new HashSet<>();
        this.profanityWords = new HashSet<>();
        this.businessStopWords = new HashSet<>();
        this.commonMisspellings = new HashMap<>();
        this.domainSpecificTerms = new HashMap<>();
        this.stats = new QueryNormalizationStats();
        
        initializeDictionaries();
    }
    
    /**
     * Initialize all dictionaries and lookup tables
     */
    private void initializeDictionaries() {
        initializeContractions();
        initializeAbbreviations();
        initializeSynonyms();
        initializeSlang();
        initializeBusinessTerms();
        initializeEmojis();
        initializeStopWords();
        initializeProfanityWords();
        initializeCommonMisspellings();
        initializeDomainSpecificTerms();
    }
    
    /**
     * Initialize contraction mappings
     */
    private void initializeContractions() {
        contractionMap.put("ain't", "am not");
        contractionMap.put("aren't", "are not");
        contractionMap.put("can't", "cannot");
        contractionMap.put("couldn't", "could not");
        contractionMap.put("didn't", "did not");
        contractionMap.put("doesn't", "does not");
        contractionMap.put("don't", "do not");
        contractionMap.put("hadn't", "had not");
        contractionMap.put("hasn't", "has not");
        contractionMap.put("haven't", "have not");
        contractionMap.put("he'd", "he would");
        contractionMap.put("he'll", "he will");
        contractionMap.put("he's", "he is");
        contractionMap.put("i'd", "i would");
        contractionMap.put("i'll", "i will");
        contractionMap.put("i'm", "i am");
        contractionMap.put("i've", "i have");
        contractionMap.put("isn't", "is not");
        contractionMap.put("it'd", "it would");
        contractionMap.put("it'll", "it will");
        contractionMap.put("it's", "it is");
        contractionMap.put("let's", "let us");
        contractionMap.put("shouldn't", "should not");
        contractionMap.put("that's", "that is");
        contractionMap.put("there's", "there is");
        contractionMap.put("they'd", "they would");
        contractionMap.put("they'll", "they will");
        contractionMap.put("they're", "they are");
        contractionMap.put("they've", "they have");
        contractionMap.put("wasn't", "was not");
        contractionMap.put("we'd", "we would");
        contractionMap.put("we'll", "we will");
        contractionMap.put("we're", "we are");
        contractionMap.put("we've", "we have");
        contractionMap.put("weren't", "were not");
        contractionMap.put("what's", "what is");
        contractionMap.put("where's", "where is");
        contractionMap.put("who's", "who is");
        contractionMap.put("won't", "will not");
        contractionMap.put("wouldn't", "would not");
        contractionMap.put("you'd", "you would");
        contractionMap.put("you'll", "you will");
        contractionMap.put("you're", "you are");
        contractionMap.put("you've", "you have");
    }
    
    /**
     * Initialize abbreviation mappings
     */
    private void initializeAbbreviations() {
        // Business abbreviations
        abbreviationMap.put("acct", "account");
        abbreviationMap.put("addr", "address");
        abbreviationMap.put("amt", "amount");
        abbreviationMap.put("appt", "appointment");
        abbreviationMap.put("bal", "balance");
        abbreviationMap.put("biz", "business");
        abbreviationMap.put("co", "company");
        abbreviationMap.put("corp", "corporation");
        abbreviationMap.put("cust", "customer");
        abbreviationMap.put("dept", "department");
        abbreviationMap.put("doc", "document");
        abbreviationMap.put("emp", "employee");
        abbreviationMap.put("info", "information");
        abbreviationMap.put("inv", "invoice");
        abbreviationMap.put("mgmt", "management");
        abbreviationMap.put("num", "number");
        abbreviationMap.put("org", "organization");
        abbreviationMap.put("pmt", "payment");
        abbreviationMap.put("qty", "quantity");
        abbreviationMap.put("ref", "reference");
        abbreviationMap.put("req", "request");
        abbreviationMap.put("svc", "service");
        abbreviationMap.put("txn", "transaction");
        
        // Time abbreviations
        abbreviationMap.put("asap", "as soon as possible");
        abbreviationMap.put("eod", "end of day");
        abbreviationMap.put("eta", "estimated time of arrival");
        abbreviationMap.put("fyi", "for your information");
        abbreviationMap.put("tbd", "to be determined");
        abbreviationMap.put("tba", "to be announced");
        
        // Common abbreviations
        abbreviationMap.put("etc", "et cetera");
        abbreviationMap.put("ie", "that is");
        abbreviationMap.put("eg", "for example");
        abbreviationMap.put("vs", "versus");
        abbreviationMap.put("w/", "with");
        abbreviationMap.put("w/o", "without");
        abbreviationMap.put("b/c", "because");
        abbreviationMap.put("thru", "through");
        abbreviationMap.put("pls", "please");
        abbreviationMap.put("thx", "thanks");
        abbreviationMap.put("ur", "your");
        abbreviationMap.put("u", "you");
        abbreviationMap.put("r", "are");
    }
    
    /**
     * Initialize synonym mappings
     */
    private void initializeSynonyms() {
        // Business synonyms
        synonymMap.put("purchase", "buy");
        synonymMap.put("acquire", "buy");
        synonymMap.put("obtain", "get");
        synonymMap.put("receive", "get");
        synonymMap.put("assist", "help");
        synonymMap.put("support", "help");
        synonymMap.put("resolve", "fix");
        synonymMap.put("repair", "fix");
        synonymMap.put("issue", "problem");
        synonymMap.put("concern", "problem");
        synonymMap.put("matter", "issue");
        synonymMap.put("inquiry", "question");
        synonymMap.put("query", "question");
        synonymMap.put("request", "ask");
        synonymMap.put("require", "need");
        synonymMap.put("desire", "want");
        synonymMap.put("wish", "want");
        synonymMap.put("locate", "find");
        synonymMap.put("search", "find");
        synonymMap.put("discover", "find");
        synonymMap.put("provide", "give");
        synonymMap.put("supply", "give");
        synonymMap.put("deliver", "give");
        synonymMap.put("complete", "finish");
        synonymMap.put("finalize", "finish");
        synonymMap.put("conclude", "finish");
        synonymMap.put("commence", "start");
        synonymMap.put("begin", "start");
        synonymMap.put("initiate", "start");
        synonymMap.put("terminate", "end");
        synonymMap.put("cease", "stop");
        synonymMap.put("halt", "stop");
    }
    
    /**
     * Initialize slang mappings
     */
    private void initializeSlang() {
        slangMap.put("gonna", "going to");
        slangMap.put("wanna", "want to");
        slangMap.put("gotta", "got to");
        slangMap.put("hafta", "have to");
        slangMap.put("kinda", "kind of");
        slangMap.put("sorta", "sort of");
        slangMap.put("dunno", "do not know");
        slangMap.put("lemme", "let me");
        slangMap.put("gimme", "give me");
        slangMap.put("whatcha", "what are you");
        slangMap.put("betcha", "bet you");
        slangMap.put("gotcha", "got you");
        slangMap.put("shoulda", "should have");
        slangMap.put("coulda", "could have");
        slangMap.put("woulda", "would have");
        slangMap.put("mighta", "might have");
        slangMap.put("oughta", "ought to");
        slangMap.put("lotta", "lot of");
        slangMap.put("outta", "out of");
        slangMap.put("cuz", "because");
        slangMap.put("bout", "about");
        slangMap.put("em", "them");
        slangMap.put("ya", "you");
        slangMap.put("yep", "yes");
        slangMap.put("nope", "no");
        slangMap.put("yeah", "yes");
        slangMap.put("nah", "no");
        slangMap.put("ok", "okay");
        slangMap.put("sup", "what is up");
        slangMap.put("wassup", "what is up");
    }
    
    /**
     * Initialize business term mappings
     */
    private void initializeBusinessTerms() {
        businessTermMap.put("roi", "return on investment");
        businessTermMap.put("kpi", "key performance indicator");
        businessTermMap.put("sla", "service level agreement");
        businessTermMap.put("crm", "customer relationship management");
        businessTermMap.put("erp", "enterprise resource planning");
        businessTermMap.put("hr", "human resources");
        businessTermMap.put("it", "information technology");
        businessTermMap.put("qa", "quality assurance");
        businessTermMap.put("qc", "quality control");
        businessTermMap.put("r&d", "research and development");
        businessTermMap.put("ceo", "chief executive officer");
        businessTermMap.put("cfo", "chief financial officer");
        businessTermMap.put("cto", "chief technology officer");
        businessTermMap.put("coo", "chief operating officer");
        businessTermMap.put("vp", "vice president");
        businessTermMap.put("mgr", "manager");
        businessTermMap.put("dir", "director");
        businessTermMap.put("svp", "senior vice president");
        businessTermMap.put("evp", "executive vice president");
        businessTermMap.put("b2b", "business to business");
        businessTermMap.put("b2c", "business to consumer");
        businessTermMap.put("p&l", "profit and loss");
        businessTermMap.put("ipo", "initial public offering");
        businessTermMap.put("llc", "limited liability company");
        businessTermMap.put("inc", "incorporated");
        businessTermMap.put("ltd", "limited");
    }
    
    /**
     * Initialize emoji mappings
     */
    private void initializeEmojis() {
        emojiMap.put("?", "happy");
        emojiMap.put("?", "happy");
        emojiMap.put("?", "happy");
        emojiMap.put("?", "happy");
        emojiMap.put("?", "happy");
        emojiMap.put("?", "laughing");
        emojiMap.put("?", "laughing");
 emojiMap.put("?", "laughing");
        emojiMap.put("?", "laughing");
        emojiMap.put("?", "sad");
        emojiMap.put("?", "crying");
        emojiMap.put("?", "angry");
        emojiMap.put("?", "angry");
        emojiMap.put("?", "frustrated");
        emojiMap.put("?", "confused");
        emojiMap.put("?", "worried");
        emojiMap.put("?", "scared");
        emojiMap.put("?", "anxious");
        emojiMap.put("?", "thinking");
        emojiMap.put("?", "neutral");
        emojiMap.put("?", "expressionless");
        emojiMap.put("?", "eye roll");
        emojiMap.put("?", "unamused");
        emojiMap.put("?", "cool");
        emojiMap.put("?", "hugging");
        emojiMap.put("?", "handshake");
        emojiMap.put("?", "thumbs up");
        emojiMap.put("?", "thumbs down");
        emojiMap.put("?", "okay");
        emojiMap.put("?", "check mark");
        emojiMap.put("?", "cross mark");
        emojiMap.put("?", "star");
        emojiMap.put("?", "hundred percent");
        emojiMap.put("?", "fire");
        emojiMap.put("?", "money");
        emojiMap.put("?", "credit card");
        emojiMap.put("?", "phone");
        emojiMap.put("?", "email");
        emojiMap.put("?", "calendar");
        emojiMap.put("?", "alarm clock");
        emojiMap.put("?", "office building");
        emojiMap.put("?", "convenience store");
        emojiMap.put("?", "bank");
        emojiMap.put("?", "car");
        emojiMap.put("??", "airplane");
        emojiMap.put("?", "house");
        emojiMap.put("?", "question mark");
        emojiMap.put("?", "exclamation mark");
    }
    
    /**
     * Initialize stop words
     */
    private void initializeStopWords() {
        String[] commonStopWords = {
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "has", "he", "in", "is", "it", "its", "of", "on", "that", "the",
            "to", "was", "will", "with", "the", "this", "but", "they", "have",
            "had", "what", "said", "each", "which", "she", "do", "how", "their",
            "if", "up", "out", "many", "then", "them", "these", "so", "some",
            "her", "would", "make", "like", "into", "him", "time", "two", "more",
            "go", "no", "way", "could", "my", "than", "first", "been", "call",
            "who", "oil", "sit", "now", "find", "down", "day", "did", "get",
            "come", "made", "may", "part"
        };
        stopWords.addAll(Arrays.asList(commonStopWords));
        
        // Business-specific stop words that should be preserved in chatbot context
        String[] businessStopWords = {
            "please", "thank", "thanks", "hello", "hi", "hey", "goodbye", "bye",
            "yes", "no", "okay", "ok", "sure", "maybe", "perhaps", "actually",
            "really", "very", "quite", "rather", "pretty", "fairly", "somewhat",
            "definitely", "certainly", "absolutely", "exactly", "precisely"
        };
        this.businessStopWords.addAll(Arrays.asList(businessStopWords));
    }
    
    /**
     * Initialize profanity words (basic list for demonstration)
     */
    private void initializeProfanityWords() {
        // Add common profanity words - keeping minimal for professional context
        String[] profanity = {
            "damn", "hell", "crap", "stupid", "idiot", "moron", "dumb", "suck",
            "sucks", "hate", "hated", "awful", "terrible", "horrible", "worst"
        };
        profanityWords.addAll(Arrays.asList(profanity));
    }
    
    /**
     * Initialize common misspellings
     */
    private void initializeCommonMisspellings() {
        commonMisspellings.put("recieve", "receive");
        commonMisspellings.put("seperate", "separate");
        commonMisspellings.put("definately", "definitely");
        commonMisspellings.put("occured", "occurred");
        commonMisspellings.put("begining", "beginning");
        commonMisspellings.put("beleive", "believe");
        commonMisspellings.put("acheive", "achieve");
        commonMisspellings.put("neccessary", "necessary");
        commonMisspellings.put("accomodate", "accommodate");
        commonMisspellings.put("embarass", "embarrass");
        commonMisspellings.put("existance", "existence");
        commonMisspellings.put("maintainance", "maintenance");
        commonMisspellings.put("occassion", "occasion");
        commonMisspellings.put("priviledge", "privilege");
        commonMisspellings.put("recomend", "recommend");
        commonMisspellings.put("succesful", "successful");
        commonMisspellings.put("tommorow", "tomorrow");
        commonMisspellings.put("untill", "until");
        commonMisspellings.put("wierd", "weird");
        commonMisspellings.put("thier", "their");
        commonMisspellings.put("freind", "friend");
        commonMisspellings.put("buisness", "business");
        commonMisspellings.put("adress", "address");
        commonMisspellings.put("calender", "calendar");
        commonMisspellings.put("commitee", "committee");
        commonMisspellings.put("enviroment", "environment");
        commonMisspellings.put("goverment", "government");
        commonMisspellings.put("independant", "independent");
        commonMisspellings.put("managment", "management");
        commonMisspellings.put("personel", "personnel");
    }
    
    /**
     * Initialize domain-specific terms
     */
    private void initializeDomainSpecificTerms() {
        // Financial terms
        domainSpecificTerms.put("apr", "annual percentage rate");
        domainSpecificTerms.put("apy", "annual percentage yield");
        domainSpecificTerms.put("cd", "certificate of deposit");
        domainSpecificTerms.put("ach", "automated clearing house");
        domainSpecificTerms.put("eft", "electronic funds transfer");
        domainSpecificTerms.put("atm", "automated teller machine");
        domainSpecificTerms.put("pin", "personal identification number");
        domainSpecificTerms.put("ssn", "social security number");
        domainSpecificTerms.put("ein", "employer identification number");
        domainSpecificTerms.put("ira", "individual retirement account");
        domainSpecificTerms.put("401k", "401k retirement plan");
        
        // Technology terms
        domainSpecificTerms.put("api", "application programming interface");
        domainSpecificTerms.put("url", "uniform resource locator");
        domainSpecificTerms.put("html", "hypertext markup language");
        domainSpecificTerms.put("css", "cascading style sheets");
        domainSpecificTerms.put("sql", "structured query language");
        domainSpecificTerms.put("xml", "extensible markup language");
        domainSpecificTerms.put("json", "javascript object notation");
        domainSpecificTerms.put("http", "hypertext transfer protocol");
        domainSpecificTerms.put("https", "hypertext transfer protocol secure");
        domainSpecificTerms.put("ftp", "file transfer protocol");
        domainSpecificTerms.put("ssh", "secure shell");
        domainSpecificTerms.put("vpn", "virtual private network");
        domainSpecificTerms.put("dns", "domain name system");
        domainSpecificTerms.put("ip", "internet protocol");
        domainSpecificTerms.put("tcp", "transmission control protocol");
        domainSpecificTerms.put("udp", "user datagram protocol");
    }
    
    /**
     * Main normalization method
     */
    public QueryNormalizationResult normalize(String query) {
        if (query == null) {
            return new QueryNormalizationResult("", "", 0.0, "Null input");
        }
        
        long startTime = System.currentTimeMillis();
        stats.incrementProcessedQueries();
        
        try {
            String originalQuery = query;
            List<String> appliedTransformations = new ArrayList<>();
            
            // Step 1: Basic validation and length check
            if (query.trim().isEmpty()) {
                return new QueryNormalizationResult(originalQuery, "", 0.0, "Empty query");
            }
            
            if (query.length() > maxQueryLength) {
                query = query.substring(0, maxQueryLength);
                appliedTransformations.add("truncated");
            }
            
            if (query.length() < minQueryLength) {
                return new QueryNormalizationResult(originalQuery, query, 0.5, "Query too short");
            }
            
            // Step 2: Preserve important entities before normalization
            Map<String, String> preservedEntities = preserveEntities(query);
            query = replaceEntitiesWithPlaceholders(query, preservedEntities);
            appliedTransformations.add("entity_preservation");
            
            // Step 3: Unicode normalization
            query = Normalizer.normalize(query, Normalizer.Form.NFKC);
            appliedTransformations.add("unicode_normalization");
            
            // Step 4: HTML/Markdown cleanup
            if (HTML_TAGS.matcher(query).find()) {
                query = HTML_TAGS.matcher(query).replaceAll(" ");
                appliedTransformations.add("html_cleanup");
            }
            
            if (MARKDOWN_PATTERN.matcher(query).find()) {
                query = MARKDOWN_PATTERN.matcher(query).replaceAll("$1$2$3$4");
                appliedTransformations.add("markdown_cleanup");
            }
            
            // Step 5: Emoji processing
            if (ENABLE_EMOJI_PROCESSING) {
                String emojiProcessed = processEmojis(query);
                if (!emojiProcessed.equals(query)) {
                    query = emojiProcessed;
                    appliedTransformations.add("emoji_processing");
                }
            }
            
            // Step 6: Profanity filtering
            if (ENABLE_PROFANITY_FILTERING) {
                String profanityFiltered = filterProfanity(query);
                if (!profanityFiltered.equals(query)) {
                    query = profanityFiltered;
                    appliedTransformations.add("profanity_filtering");
                }
            }
            
            // Step 7: Contraction expansion
            String contractionsExpanded = expandContractions(query);
            if (!contractionsExpanded.equals(query)) {
                query = contractionsExpanded;
                appliedTransformations.add("contraction_expansion");
            }
            
            // Step 8: Slang normalization
            String slangNormalized = normalizeSlang(query);
            if (!slangNormalized.equals(query)) {
                query = slangNormalized;
                appliedTransformations.add("slang_normalization");
            }
            
            // Step 9: Abbreviation expansion
            if (ENABLE_ABBREVIATION_EXPANSION) {
                String abbreviationsExpanded = expandAbbreviations(query);
                if (!abbreviationsExpanded.equals(query)) {
                    query = abbreviationsExpanded;
                    appliedTransformations.add("abbreviation_expansion");
                }
            }
            
            // Step 10: Domain-specific term expansion
            String domainTermsExpanded = expandDomainSpecificTerms(query);
            if (!domainTermsExpanded.equals(query)) {
                query = domainTermsExpanded;
                appliedTransformations.add("domain_term_expansion");
            }
            
            // Step 11: Business term expansion
            String businessTermsExpanded = expandBusinessTerms(query);
            if (!businessTermsExpanded.equals(query)) {
                query = businessTermsExpanded;
                appliedTransformations.add("business_term_expansion");
            }
            
            // Step 12: Spell correction
            if (ENABLE_SPELL_CORRECTION) {
                String spellCorrected = correctSpelling(query);
                if (!spellCorrected.equals(query)) {
                    query = spellCorrected;
                    appliedTransformations.add("spell_correction");
                }
            }
            
            // Step 13: Synonym replacement
            if (ENABLE_SYNONYM_REPLACEMENT) {
                String synonymsReplaced = replaceSynonyms(query);
                if (!synonymsReplaced.equals(query)) {
                    query = synonymsReplaced;
                    appliedTransformations.add("synonym_replacement");
                }
            }
            
            // Step 14: Repeated character normalization
            String repeatedCharsNormalized = normalizeRepeatedCharacters(query);
            if (!repeatedCharsNormalized.equals(query)) {
                query = repeatedCharsNormalized;
                appliedTransformations.add("repeated_char_normalization");
            }
            
            // Step 15: Special character handling
            if (!preservePunctuation) {
                String specialCharsRemoved = SPECIAL_CHARS.matcher(query).replaceAll(" ");
                if (!specialCharsRemoved.equals(query)) {
                    query = specialCharsRemoved;
                    appliedTransformations.add("special_char_removal");
                }
            }
            
            // Step 16: Case normalization
            if (!preserveCase) {
                query = query.toLowerCase();
                appliedTransformations.add("case_normalization");
            }
            
            // Step 17: Whitespace normalization
            String whitespaceNormalized = MULTIPLE_SPACES.matcher(query).replaceAll(" ").trim();
            if (!whitespaceNormalized.equals(query)) {
                query = whitespaceNormalized;
                appliedTransformations.add("whitespace_normalization");
            }
            
            // Step 18: Stop word removal (optional for chatbots)
            if (ENABLE_STOPWORD_REMOVAL) {
                String stopWordsRemoved = removeStopWords(query);
                if (!stopWordsRemoved.equals(query)) {
                    query = stopWordsRemoved;
                    appliedTransformations.add("stopword_removal");
                }
            }
            
            // Step 19: Restore preserved entities
            query = restoreEntitiesFromPlaceholders(query, preservedEntities);
            appliedTransformations.add("entity_restoration");
            
            // Step 20: Final validation and cleanup
            query = query.trim();
            if (query.isEmpty()) {
                return new QueryNormalizationResult(originalQuery, originalQuery, 0.3, "Normalization resulted in empty query");
            }
            
            // Calculate confidence score
            double confidence = calculateNormalizationConfidence(originalQuery, query, appliedTransformations);
            
            // Update statistics
            long processingTime = System.currentTimeMillis() - startTime;
            stats.addProcessingTime(processingTime);
            stats.addTransformationCount(appliedTransformations.size());
            
            return new QueryNormalizationResult(
                originalQuery, 
                query, 
                confidence, 
                "Successfully normalized with " + appliedTransformations.size() + " transformations",
                appliedTransformations,
                processingTime
            );
            
        } catch (Exception e) {
            stats.incrementErrors();
            return new QueryNormalizationResult(
                query, 
                query, 
                0.1, 
                "Error during normalization: " + e.getMessage()
            );
        }
    }
    
    /**
     * Preserve important entities before normalization
     */
    private Map<String, String> preserveEntities(String query) {
        Map<String, String> preservedEntities = new HashMap<>();
        int placeholderIndex = 0;
        
        // Preserve URLs
        if (preserveUrls) {
            Matcher urlMatcher = URL_PATTERN.matcher(query);
            while (urlMatcher.find()) {
                String placeholder = "URL_PLACEHOLDER_" + placeholderIndex++;
                preservedEntities.put(placeholder, urlMatcher.group());
            }
        }
        
        // Preserve emails
        if (preserveEmails) {
            Matcher emailMatcher = EMAIL_PATTERN.matcher(query);
            while (emailMatcher.find()) {
                String placeholder = "EMAIL_PLACEHOLDER_" + placeholderIndex++;
                preservedEntities.put(placeholder, emailMatcher.group());
            }
        }
        
        // Preserve phone numbers
        if (preservePhones) {
            Matcher phoneMatcher = PHONE_PATTERN.matcher(query);
            while (phoneMatcher.find()) {
                String placeholder = "PHONE_PLACEHOLDER_" + placeholderIndex++;
                preservedEntities.put(placeholder, phoneMatcher.group());
            }
        }
        
        // Preserve numbers if configured
        if (preserveNumbers) {
            Pattern numberPattern = Pattern.compile("\\b\\d+(?:\\.\\d+)?\\b");
            Matcher numberMatcher = numberPattern.matcher(query);
            while (numberMatcher.find()) {
                String placeholder = "NUMBER_PLACEHOLDER_" + placeholderIndex++;
                preservedEntities.put(placeholder, numberMatcher.group());
            }
        }
        
        return preservedEntities;
    }
    
    /**
     * Replace entities with placeholders
     */
    private String replaceEntitiesWithPlaceholders(String query, Map<String, String> preservedEntities) {
        String result = query;
        for (Map.Entry<String, String> entry : preservedEntities.entrySet()) {
            result = result.replace(entry.getValue(), entry.getKey());
        }
        return result;
    }
    
    /**
     * Restore entities from placeholders
     */
    private String restoreEntitiesFromPlaceholders(String query, Map<String, String> preservedEntities) {
        String result = query;
        for (Map.Entry<String, String> entry : preservedEntities.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * Process emojis
     */
    private String processEmojis(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            result = result.replace(entry.getKey(), " " + entry.getValue() + " ");
        }
        return result;
    }
    
    /**
     * Filter profanity
     */
    private String filterProfanity(String query) {
        String[] words = query.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.toLowerCase().replaceAll("[^a-z]", "");
            if (profanityWords.contains(cleanWord)) {
                result.append("[filtered] ");
            } else {
                result.append(word).append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * Expand contractions
     */
    private String expandContractions(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : contractionMap.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Normalize slang
     */
    private String normalizeSlang(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : slangMap.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Expand abbreviations
     */
    private String expandAbbreviations(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : abbreviationMap.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Expand domain-specific terms
     */
    private String expandDomainSpecificTerms(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : domainSpecificTerms.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Expand business terms
     */
    private String expandBusinessTerms(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : businessTermMap.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Correct spelling
     */
    private String correctSpelling(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : commonMisspellings.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Replace synonyms
     */
    private String replaceSynonyms(String query) {
        String result = query;
        for (Map.Entry<String, String> entry : synonymMap.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }
        return result;
    }
    
    /**
     * Normalize repeated characters
     */
    private String normalizeRepeatedCharacters(String query) {
        return REPEATED_CHARS.matcher(query).replaceAll("$1$1");
    }
    
    /**
     * Remove stop words
     */
    private String removeStopWords(String query) {
        String[] words = query.split("\\s+");
        List<String> filteredWords = new ArrayList<>();
        
        for (String word : words) {
            String cleanWord = word.toLowerCase().replaceAll("[^a-z]", "");
            if (!stopWords.contains(cleanWord) || businessStopWords.contains(cleanWord)) {
                filteredWords.add(word);
            }
        }
        
        return String.join(" ", filteredWords);
    }
    
    /**
     * Calculate normalization confidence
     */
    private double calculateNormalizationConfidence(String original, String normalized, List<String> transformations) {
        double baseConfidence = 1.0;
        
        // Reduce confidence based on number of transformations
        double transformationPenalty = transformations.size() * 0.02;
        baseConfidence -= transformationPenalty;
        
        // Reduce confidence based on length change
        double lengthRatio = (double) normalized.length() / original.length();
        if (lengthRatio < 0.5 || lengthRatio > 2.0) {
            baseConfidence -= 0.2;
        } else if (lengthRatio < 0.7 || lengthRatio > 1.5) {
            baseConfidence -= 0.1;
        }
        
        // Boost confidence for successful transformations
        if (transformations.contains("spell_correction")) {
            baseConfidence += 0.05;
        }
        if (transformations.contains("contraction_expansion")) {
            baseConfidence += 0.03;
        }
        if (transformations.contains("abbreviation_expansion")) {
            baseConfidence += 0.03;
        }
        
        // Ensure confidence is within bounds
        return Math.max(0.0, Math.min(1.0, baseConfidence));
    }
    
    /**
     * Batch normalize multiple queries
     */
    public List<QueryNormalizationResult> batchNormalize(List<String> queries) {
        if (queries == null || queries.isEmpty()) {
            return new ArrayList<>();
        }
        
        return queries.stream()
                     .map(this::normalize)
                     .collect(Collectors.toList());
    }
    
    /**
     * Add custom abbreviation
     */
    public void addAbbreviation(String abbreviation, String expansion) {
        if (abbreviation != null && expansion != null && 
            !abbreviation.trim().isEmpty() && !expansion.trim().isEmpty()) {
            abbreviationMap.put(abbreviation.toLowerCase().trim(), expansion.trim());
        }
    }
    
    /**
     * Add custom synonym
     */
    public void addSynonym(String word, String synonym) {
        if (word != null && synonym != null && 
            !word.trim().isEmpty() && !synonym.trim().isEmpty()) {
            synonymMap.put(word.toLowerCase().trim(), synonym.trim());
        }
    }
    
    /**
     * Add custom business term
     */
    public void addBusinessTerm(String term, String expansion) {
        if (term != null && expansion != null && 
            !term.trim().isEmpty() && !expansion.trim().isEmpty()) {
            businessTermMap.put(term.toLowerCase().trim(), expansion.trim());
        }
    }
    
    /**
     * Add custom misspelling correction
     */
    public void addMisspellingCorrection(String misspelling, String correction) {
        if (misspelling != null && correction != null && 
            !misspelling.trim().isEmpty() && !correction.trim().isEmpty()) {
            commonMisspellings.put(misspelling.toLowerCase().trim(), correction.trim());
        }
    }
    
    /**
     * Configuration methods
     */
    public void setPreserveCase(boolean preserveCase) {
        this.preserveCase = preserveCase;
    }
    
    public void setPreservePunctuation(boolean preservePunctuation) {
        this.preservePunctuation = preservePunctuation;
    }
    
    public void setPreserveNumbers(boolean preserveNumbers) {
        this.preserveNumbers = preserveNumbers;
    }
    
    public void setPreserveEmails(boolean preserveEmails) {
        this.preserveEmails = preserveEmails;
    }
    
    public void setPreserveUrls(boolean preserveUrls) {
        this.preserveUrls = preserveUrls;
    }
    
    public void setPreservePhones(boolean preservePhones) {
        this.preservePhones = preservePhones;
    }
    
    public void setMaxQueryLength(int maxQueryLength) {
        this.maxQueryLength = Math.max(1, maxQueryLength);
    }
    
    public void setMinQueryLength(int minQueryLength) {
        this.minQueryLength = Math.max(0, minQueryLength);
    }
    
    /**
     * Get statistics
     */
    public QueryNormalizationStats getStats() {
        return stats;
    }
    
    /**
     * Reset statistics
     */
    public void resetStats() {
        this.stats = new QueryNormalizationStats();
    }
    
    /**
     * Export configuration
     */
    public Map<String, Object> exportConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("abbreviationMap", new HashMap<>(abbreviationMap));
        config.put("synonymMap", new HashMap<>(synonymMap));
        config.put("businessTermMap", new HashMap<>(businessTermMap));
        config.put("commonMisspellings", new HashMap<>(commonMisspellings));
        config.put("domainSpecificTerms", new HashMap<>(domainSpecificTerms));
        config.put("preserveCase", preserveCase);
        config.put("preservePunctuation", preservePunctuation);
        config.put("preserveNumbers", preserveNumbers);
        config.put("preserveEmails", preserveEmails);
        config.put("preserveUrls", preserveUrls);
        config.put("preservePhones", preservePhones);
        config.put("maxQueryLength", maxQueryLength);
        config.put("minQueryLength", minQueryLength);
        return config;
    }
    
    /**
     * Import configuration
     */
    @SuppressWarnings("unchecked")
    public void importConfiguration(Map<String, Object> config) {
        if (config == null) return;
        
        try {
            if (config.containsKey("abbreviationMap")) {
                Map<String, String> abbrevs = (Map<String, String>) config.get("abbreviationMap");
                abbreviationMap.putAll(abbrevs);
            }
            
            if (config.containsKey("synonymMap")) {
                Map<String, String> synonyms = (Map<String, String>) config.get("synonymMap");
                synonymMap.putAll(synonyms);
            }
            
            if (config.containsKey("businessTermMap")) {
                Map<String, String> businessTerms = (Map<String, String>) config.get("businessTermMap");
                businessTermMap.putAll(businessTerms);
            }
            
            if (config.containsKey("commonMisspellings")) {
                Map<String, String> misspellings = (Map<String, String>) config.get("commonMisspellings");
                commonMisspellings.putAll(misspellings);
            }
            
            if (config.containsKey("domainSpecificTerms")) {
                Map<String, String> domainTerms = (Map<String, String>) config.get("domainSpecificTerms");
                domainSpecificTerms.putAll(domainTerms);
            }
            
            if (config.containsKey("preserveCase")) {
                preserveCase = (Boolean) config.get("preserveCase");
            }
            
            if (config.containsKey("preservePunctuation")) {
                preservePunctuation = (Boolean) config.get("preservePunctuation");
            }
            
            if (config.containsKey("preserveNumbers")) {
                preserveNumbers = (Boolean) config.get("preserveNumbers");
            }
            
            if (config.containsKey("preserveEmails")) {
                preserveEmails = (Boolean) config.get("preserveEmails");
            }
            
            if (config.containsKey("preserveUrls")) {
                preserveUrls = (Boolean) config.get("preserveUrls");
            }
            
            if (config.containsKey("preservePhones")) {
                preservePhones = (Boolean) config.get("preservePhones");
            }
            
            if (config.containsKey("maxQueryLength")) {
                maxQueryLength = (Integer) config.get("maxQueryLength");
            }
            
            if (config.containsKey("minQueryLength")) {
                minQueryLength = (Integer) config.get("minQueryLength");
            }
            
        } catch (ClassCastException e) {
            System.err.println("Error importing configuration: Invalid data type - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error importing configuration: " + e.getMessage());
        }
    }
    
    /**
     * Validate configuration
     */
    public boolean validateConfiguration() {
        try {
            if (maxQueryLength <= 0) {
                System.err.println("Error: maxQueryLength must be positive");
                return false;
            }
            
            if (minQueryLength < 0) {
                System.err.println("Error: minQueryLength cannot be negative");
                return false;
            }
            
            if (minQueryLength >= maxQueryLength) {
                System.err.println("Error: minQueryLength must be less than maxQueryLength");
                return false;
            }
            
            if (abbreviationMap.isEmpty()) {
                System.err.println("Warning: No abbreviations loaded");
            }
            
            if (synonymMap.isEmpty()) {
                System.err.println("Warning: No synonyms loaded");
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error validating configuration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test query normalization
     */
    public void testQueryNormalization() {
        System.out.println("=== Query Normalizer Test ===");
        
        String[] testQueries = {
            "Hi! Can u help me w/ my acct balance pls? ?",
            "I can't find my inv #12345 - it's really important!!!",
            "What's the status of contract CT-2024-001? I need info ASAP.",
            "Please help me w/ payment issues. My email is john@company.com",
            "I wanna check my 401k balance and ira info",
            "Can't login to the system - getting error messages :(",
            "Need to update my addr and phone num (555) 123-4567",
            "The API isn't working properly - returns 404 errors",
            "I'd like to schedule an appt for next week if possible",
            "What's the ROI on our recent investment? Need P&L data",
            "Can you recieve my payment? I beleive it was processed",
            "The mgmt team wants a report on Q4 performance",
            "I'm gonna need help w/ the new CRM system setup",
            "Please provide info about our SLA and support options",
            "The server at 192.168.1.100 is down - critical issue!",
            "Visit https://company.com/portal for more details",
            "I dunno why the system keeps crashing - very frustrating ?",
            "Thx for ur help! The issue has been resolved successfully",
            "What r the requirements for opening a new biz account?",
            "I shoulda contacted support earlier about this problem"
        };
        
        for (String query : testQueries) {
            System.out.println("\nOriginal: " + query);
            QueryNormalizationResult result = normalize(query);
            
            System.out.println("Normalized: " + result.getNormalizedQuery());
            System.out.println("Confidence: " + String.format("%.2f", result.getConfidence()));
            System.out.println("Transformations: " + result.getAppliedTransformations().size());
            System.out.println("Processing time: " + result.getProcessingTime() + "ms");
            
            if (!result.getAppliedTransformations().isEmpty()) {
                System.out.println("Applied: " + String.join(", ", result.getAppliedTransformations()));
            }
        }
        
        System.out.println("\n=== Statistics ===");
        System.out.println(getStats());
        
        System.out.println("\n=== Configuration Validation ===");
        System.out.println("Configuration valid: " + validateConfiguration());
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        QueryNormalizer normalizer = new QueryNormalizer();
        
        if (args.length > 0) {
            // Process command line arguments
            String inputQuery = String.join(" ", args);
            System.out.println("Input: " + inputQuery);
            
            QueryNormalizationResult result = normalizer.normalize(inputQuery);
            System.out.println("Normalized: " + result.getNormalizedQuery());
            System.out.println("Confidence: " + String.format("%.2f", result.getConfidence()));
            System.out.println("Transformations: " + result.getAppliedTransformations());
            System.out.println("Processing time: " + result.getProcessingTime() + "ms");
            
        } else {
            // Run test suite
            normalizer.testQueryNormalization();
        }
    }
    
    /**
     * Inner class for normalization result
     */
    public static class QueryNormalizationResult {
        private final String originalQuery;
        private final String normalizedQuery;
        private final double confidence;
        private final String message;
        private final List<String> appliedTransformations;
        private final long processingTime;
        private final long timestamp;
        
        public QueryNormalizationResult(String originalQuery, String normalizedQuery, 
                                      double confidence, String message) {
            this(originalQuery, normalizedQuery, confidence, message, new ArrayList<>(), 0L);
        }
        
        public QueryNormalizationResult(String originalQuery, String normalizedQuery, 
                                      double confidence, String message, 
                                      List<String> appliedTransformations, long processingTime) {
            this.originalQuery = originalQuery;
            this.normalizedQuery = normalizedQuery;
            this.confidence = confidence;
            this.message = message;
            this.appliedTransformations = appliedTransformations != null ? 
                new ArrayList<>(appliedTransformations) : new ArrayList<>();
            this.processingTime = processingTime;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getOriginalQuery() { return originalQuery; }
        public String getNormalizedQuery() { return normalizedQuery; }
        public double getConfidence() { return confidence; }
        public String getMessage() { return message; }
        public List<String> getAppliedTransformations() { return new ArrayList<>(appliedTransformations); }
        public long getProcessingTime() { return processingTime; }
        public long getTimestamp() { return timestamp; }
        
        public boolean wasTransformed() { 
            return !originalQuery.equals(normalizedQuery); 
        }
        
        public int getTransformationCount() { 
            return appliedTransformations.size(); 
        }
        
        public boolean hasTransformation(String transformation) {
            return appliedTransformations.contains(transformation);
        }
        
        @Override
        public String toString() {
            return String.format("QueryNormalizationResult{original='%s', normalized='%s', confidence=%.2f, transformations=%d}", 
                               originalQuery, normalizedQuery, confidence, appliedTransformations.size());
        }
    }
    
    /**
     * Inner class for normalization statistics
     */
    public static class QueryNormalizationStats {
        private int processedQueries = 0;
        private int errors = 0;
        private long totalProcessingTime = 0;
        private int totalTransformations = 0;
        private final Map<String, Integer> transformationCounts = new HashMap<>();
        private final long startTime = System.currentTimeMillis();
        
        public void incrementProcessedQueries() {
            processedQueries++;
        }
        
        public void incrementErrors() {
            errors++;
        }
        
        public void addProcessingTime(long time) {
            totalProcessingTime += time;
        }
        
        public void addTransformationCount(int count) {
            totalTransformations += count;
        }
        
        public void addTransformation(String transformation) {
            transformationCounts.merge(transformation, 1, Integer::sum);
        }
        
        // Getters
        public int getProcessedQueries() { return processedQueries; }
        public int getErrors() { return errors; }
        public long getTotalProcessingTime() { return totalProcessingTime; }
        public int getTotalTransformations() { return totalTransformations; }
        public Map<String, Integer> getTransformationCounts() { return new HashMap<>(transformationCounts); }
        public long getStartTime() { return startTime; }
        
        public double getAverageProcessingTime() {
            return processedQueries > 0 ? (double) totalProcessingTime / processedQueries : 0.0;
        }
        
        public double getAverageTransformationsPerQuery() {
            return processedQueries > 0 ? (double) totalTransformations / processedQueries : 0.0;
        }
        
        public double getErrorRate() {
            return processedQueries > 0 ? (double) errors / processedQueries : 0.0;
        }
        
        public long getUptime() {
            return System.currentTimeMillis() - startTime;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("QueryNormalizationStats{\n");
            sb.append("  Processed queries: ").append(processedQueries).append("\n");
            sb.append("  Errors: ").append(errors).append("\n");
            sb.append("  Total processing time: ").append(totalProcessingTime).append(" ms\n");
            sb.append("  Total transformations: ").append(totalTransformations).append("\n");
            sb.append("  Average processing time: ").append(String.format("%.2f", getAverageProcessingTime())).append(" ms\n");
            sb.append("  Average transformations per query: ").append(String.format("%.2f", getAverageTransformationsPerQuery())).append("\n");
            sb.append("  Error rate: ").append(String.format("%.2f%%", getErrorRate() * 100)).append("\n");
            sb.append("  Uptime: ").append(getUptime()).append(" ms\n");
            
            if (!transformationCounts.isEmpty()) {
                sb.append("  Transformation counts:\n");
                transformationCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
            }
            
            sb.append("}");
            return sb.toString();
        }
    }
}
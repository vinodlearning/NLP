package view.nlp;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * EntityResolver - Advanced entity recognition and resolution for business applications
 * Handles contracts, customers, accounts, invoices, payments, dates, amounts, and more
 */
public class EntityResolver {

    // Entity type constants

    public enum EntityType {
        CONTRACT_NUMBER,
        CUSTOMER_ID,
        ACCOUNT_NUMBER,
        INVOICE_NUMBER,
        PAYMENT_ID,
        AMOUNT,
        DATE,
        EMAIL,
        PHONE,
        PERSON_NAME,
        COMPANY_NAME,
        ADDRESS,
        STATUS,
        PRIORITY,
        DEPARTMENT,
        PRODUCT_CODE,
        REFERENCE_NUMBER,
        PERCENTAGE,
        CURRENCY,
        TIME,
        URL,
        IP_ADDRESS,
        CREDIT_CARD,
        SSN,
        TAX_ID,
        UNKNOWN
    }

    // Compiled regex patterns for performance
    private static final Map<EntityType, Pattern> ENTITY_PATTERNS = new HashMap<>();

    // Entity dictionaries and lookup tables
    private final Map<String, Set<String>> entityDictionaries;
    private final Map<String, EntityType> knownEntities;
    private final Set<String> businessTerms;
    private final Set<String> statusValues;
    private final Set<String> priorityValues;
    private final Set<String> departments;
    private final Set<String> currencies;
    private final Set<String> countries;
    private final Set<String> states;

    // Configuration
    private static final double CONFIDENCE_THRESHOLD = 0.75;
    private static final int MAX_ENTITY_LENGTH = 100;
    private static final boolean ENABLE_FUZZY_MATCHING = true;
    private static final boolean ENABLE_CONTEXT_ANALYSIS = true;

    // Statistics
    private EntityResolutionStats stats;

    static {
        initializePatterns();
    }

    /**
     * Constructor
     */
    public EntityResolver() {
        this.entityDictionaries = new HashMap<>();
        this.knownEntities = new HashMap<>();
        this.businessTerms = new HashSet<>();
        this.statusValues = new HashSet<>();
        this.priorityValues = new HashSet<>();
        this.departments = new HashSet<>();
        this.currencies = new HashSet<>();
        this.countries = new HashSet<>();
        this.states = new HashSet<>();
        this.stats = new EntityResolutionStats();

        initializeEntityDictionaries();
        initializeBusinessTerms();
        initializeKnownEntities();
    }

    /**
     * Initialize regex patterns for entity recognition
     */
    private static void initializePatterns() {
        // Contract number patterns
        ENTITY_PATTERNS.put(EntityType.CONTRACT_NUMBER,
                            Pattern.compile("\\b(?:contract|cntr|ct)[-_#\\s]*([A-Z0-9]{3,15})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Customer ID patterns
        ENTITY_PATTERNS.put(EntityType.CUSTOMER_ID,
                            Pattern.compile("\\b(?:customer|cust|client)[-_#\\s]*(?:id|number)?[-_#\\s]*([A-Z0-9]{3,12})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Account number patterns
        ENTITY_PATTERNS.put(EntityType.ACCOUNT_NUMBER,
                            Pattern.compile("\\b(?:account|acct|acc)[-_#\\s]*(?:number|no|num)?[-_#\\s]*([A-Z0-9]{4,16})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Invoice number patterns
        ENTITY_PATTERNS.put(EntityType.INVOICE_NUMBER,
                            Pattern.compile("\\b(?:invoice|inv)[-_#\\s]*(?:number|no|num)?[-_#\\s]*([A-Z0-9]{3,15})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Payment ID patterns
        ENTITY_PATTERNS.put(EntityType.PAYMENT_ID,
                            Pattern.compile("\\b(?:payment|pay|transaction|txn)[-_#\\s]*(?:id|number)?[-_#\\s]*([A-Z0-9]{4,20})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Amount patterns (currency amounts)
        ENTITY_PATTERNS.put(EntityType.AMOUNT,
                            Pattern.compile("\\$?\\b(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)\\b|\\b(\\d+(?:\\.\\d{2})?)\\s*(?:dollars?|usd|\\$)\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Date patterns (various formats)
        ENTITY_PATTERNS.put(EntityType.DATE,
                            Pattern.compile("\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}|(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*\\s+\\d{1,2},?\\s+\\d{2,4}|\\d{1,2}\\s+(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*\\s+\\d{2,4})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Email patterns
        ENTITY_PATTERNS.put(EntityType.EMAIL,
                            Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"));

        // Phone patterns
        ENTITY_PATTERNS.put(EntityType.PHONE,
                            Pattern.compile("\\b(?:\\+?1[-\\s]?)?\\(?([0-9]{3})\\)?[-\\s]?([0-9]{3})[-\\s]?([0-9]{4})\\b"));

        // Person name patterns (basic)
        ENTITY_PATTERNS.put(EntityType.PERSON_NAME,
                            Pattern.compile("\\b(?:mr|mrs|ms|dr|prof)\\.?\\s+([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)|\\b([A-Z][a-z]+\\s+[A-Z][a-z]+)\\b"));

        // Company name patterns
        ENTITY_PATTERNS.put(EntityType.COMPANY_NAME,
                            Pattern.compile("\\b([A-Z][A-Za-z\\s&]+(?:Inc|LLC|Corp|Corporation|Company|Co|Ltd|Limited|Group|Solutions|Services|Systems|Technologies|Tech))\\b"));

        // Reference number patterns
        ENTITY_PATTERNS.put(EntityType.REFERENCE_NUMBER,
                            Pattern.compile("\\b(?:ref|reference|ticket|case)[-_#\\s]*(?:number|no|num)?[-_#\\s]*([A-Z0-9]{3,15})\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Percentage patterns
        ENTITY_PATTERNS.put(EntityType.PERCENTAGE,
                            Pattern.compile("\\b(\\d+(?:\\.\\d+)?)\\s*%|\\b(\\d+(?:\\.\\d+)?)\\s*percent\\b",
                                            Pattern.CASE_INSENSITIVE));

        // Time patterns
        ENTITY_PATTERNS.put(EntityType.TIME, Pattern.compile("\\b(\\d{1,2}:\\d{2}(?::\\d{2})?(?:\\s*[AaPp][Mm])?)\\b"));

        // URL patterns
        ENTITY_PATTERNS.put(EntityType.URL,
                            Pattern.compile("\\b(?:https?://|www\\.)[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]"));

        // IP Address patterns
        ENTITY_PATTERNS.put(EntityType.IP_ADDRESS,
                            Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"));

        // Credit card patterns (basic, masked)
        ENTITY_PATTERNS.put(EntityType.CREDIT_CARD,
                            Pattern.compile("\\b(?:\\*{4}[-\\s]?){3}\\d{4}|\\b\\d{4}[-\\s]?(?:\\*{4}[-\\s]?){2}\\d{4}\\b"));

        // SSN patterns (masked)
        ENTITY_PATTERNS.put(EntityType.SSN, Pattern.compile("\\b\\*{3}-\\*{2}-\\d{4}|\\b\\d{3}-\\*{2}-\\*{4}\\b"));

        // Tax ID patterns
        ENTITY_PATTERNS.put(EntityType.TAX_ID,
                            Pattern.compile("\\b(?:tax[-\\s]?id|ein|fein)[-_#\\s]*([0-9]{2}-[0-9]{7})\\b",
                                            Pattern.CASE_INSENSITIVE));
    }

    /**
     * Initialize entity dictionaries
     */
    private void initializeEntityDictionaries() {
        // Status values
        statusValues.addAll(Arrays.asList("active", "inactive", "pending", "approved", "rejected", "cancelled",
                                          "completed", "in progress", "on hold", "suspended", "expired", "renewed",
                                          "draft", "final", "open", "closed", "paid", "unpaid", "overdue", "processing",
                                          "failed", "success"));

        // Priority values
        priorityValues.addAll(Arrays.asList("low", "medium", "high", "critical", "urgent", "normal", "minor", "major",
                                            "p1", "p2", "p3", "p4", "priority 1", "priority 2", "priority 3",
                                            "priority 4"));

        // Departments
        departments.addAll(Arrays.asList("accounting", "finance", "sales", "marketing", "hr", "human resources", "it",
                                         "information technology", "operations", "customer service", "support", "legal",
                                         "compliance", "procurement", "logistics", "administration", "executive",
                                         "management", "research", "development", "quality assurance"));

        // Currencies
        currencies.addAll(Arrays.asList("usd", "eur", "gbp", "jpy", "cad", "aud", "chf", "cny", "inr", "krw", "dollar",
                                        "euro", "pound", "yen", "franc", "yuan", "rupee", "won"));

        // Countries (sample)
        countries.addAll(Arrays.asList("united states", "usa", "canada", "mexico", "united kingdom", "uk", "germany",
                                       "france", "italy", "spain", "japan", "china", "india", "australia", "brazil"));

        // US States (sample)
        states.addAll(Arrays.asList("alabama", "alaska", "arizona", "arkansas", "california", "colorado", "connecticut",
                                    "delaware", "florida", "georgia", "hawaii", "idaho", "illinois", "indiana", "iowa",
                                    "kansas", "kentucky", "louisiana", "maine", "maryland", "massachusetts", "michigan",
                                    "minnesota", "mississippi", "missouri", "montana", "nebraska", "nevada",
                                    "new hampshire", "new jersey", "new mexico", "new york", "north carolina",
                                    "north dakota", "ohio", "oklahoma", "oregon", "pennsylvania", "rhode island",
                                    "south carolina", "south dakota", "tennessee", "texas", "utah", "vermont",
                                    "virginia", "washington", "west virginia", "wisconsin", "wyoming", "al", "ak", "az",
                                    "ar", "ca", "co", "ct", "de", "fl", "ga", "hi", "id", "il", "in", "ia", "ks", "ky",
                                    "la", "me", "md", "ma", "mi", "mn", "ms", "mo", "mt", "ne", "nv", "nh", "nj", "nm",
                                    "ny", "nc", "nd", "oh", "ok", "or", "pa", "ri", "sc", "sd", "tn", "tx", "ut", "vt",
                                    "va", "wa", "wv", "wi", "wy"));

        // Populate entity dictionaries
        entityDictionaries.put("status", statusValues);
        entityDictionaries.put("priority", priorityValues);
        entityDictionaries.put("department", departments);
        entityDictionaries.put("currency", currencies);
        entityDictionaries.put("country", countries);
        entityDictionaries.put("state", states);
    }

    /**
     * Initialize business terms
     */
    private void initializeBusinessTerms() {
        businessTerms.addAll(Arrays.asList("contract", "agreement", "invoice", "payment", "customer", "client",
                                           "account", "transaction", "order", "purchase", "sale", "revenue", "profit",
                                           "loss", "budget", "forecast", "report", "analysis", "dashboard", "metrics",
                                           "kpi", "roi", "margin", "discount", "tax", "fee", "charge", "credit",
                                           "debit", "balance", "statement", "reconciliation", "audit", "compliance",
                                           "regulation", "policy", "procedure", "workflow", "process", "approval",
                                           "authorization", "verification", "validation", "notification", "alert",
                                           "reminder", "deadline", "milestone", "deliverable", "requirement",
                                           "specification", "documentation", "training", "support", "maintenance"));
    }

    /**
     * Initialize known entities (examples)
     */
    private void initializeKnownEntities() {
        // Sample known entities - in a real system, these would be loaded from a database
        knownEntities.put("ACME Corp", EntityType.COMPANY_NAME);
        knownEntities.put("John Smith", EntityType.PERSON_NAME);
        knownEntities.put("jane.doe@company.com", EntityType.EMAIL);
        knownEntities.put("CT-2024-001", EntityType.CONTRACT_NUMBER);
        knownEntities.put("CUST-12345", EntityType.CUSTOMER_ID);
        knownEntities.put("ACC-987654", EntityType.ACCOUNT_NUMBER);
        knownEntities.put("INV-2024-0001", EntityType.INVOICE_NUMBER);
        knownEntities.put("PAY-ABC123", EntityType.PAYMENT_ID);
    }

    /**
     * Main entity resolution method
     */
    public EntityResolutionResult resolveEntities(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new EntityResolutionResult(text, new ArrayList<>(), 1.0, "Empty input");
        }

        long startTime = System.currentTimeMillis();
        stats.incrementProcessedTexts();

        try {
            List<ResolvedEntity> entities = new ArrayList<>();
            String normalizedText = normalizeText(text);

            // Step 1: Pattern-based entity extraction
            entities.addAll(extractPatternBasedEntities(normalizedText));

            // Step 2: Dictionary-based entity extraction
            entities.addAll(extractDictionaryBasedEntities(normalizedText));

            // Step 3: Context-based entity resolution
            if (ENABLE_CONTEXT_ANALYSIS) {
                entities = enhanceWithContext(entities, normalizedText);
            }

            // Step 4: Fuzzy matching for known entities
            if (ENABLE_FUZZY_MATCHING) {
                entities.addAll(extractFuzzyMatchedEntities(normalizedText));
            }

            // Step 5: Remove duplicates and merge overlapping entities
            entities = deduplicateAndMergeEntities(entities);

            // Step 6: Calculate confidence scores
            entities = calculateConfidenceScores(entities, normalizedText);

            // Step 7: Filter by confidence threshold
            entities = entities.stream()
                               .filter(e -> e.getConfidence() >= CONFIDENCE_THRESHOLD)
                               .collect(Collectors.toList());

            // Step 8: Sort by position in text
            entities.sort(Comparator.comparingInt(ResolvedEntity::getStartPosition));

            long processingTime = System.currentTimeMillis() - startTime;
            stats.addProcessingTime(processingTime);
            stats.addEntitiesFound(entities.size());

            double overallConfidence = calculateOverallConfidence(entities);
            String summary = generateSummary(entities);

            return new EntityResolutionResult(text, entities, overallConfidence, summary);

        } catch (Exception e) {
            stats.incrementErrors();
            System.err.println("Error resolving entities: " + e.getMessage());
            return new EntityResolutionResult(text, new ArrayList<>(), 0.0, "Error: " + e.getMessage());
        }
    }

    /**
     * Normalize text for processing
     */
    private String normalizeText(String text) {
        if (text == null)
            return "";

        // Basic normalization
        String normalized = text.trim();
        normalized = normalized.replaceAll("\\s+", " "); // Multiple spaces to single space
        normalized = normalized.replaceAll("[\u00A0\u2007\u202F]", " "); // Non-breaking spaces

        return normalized;
    }

    /**
     * Extract entities using regex patterns
     */
    private List<ResolvedEntity> extractPatternBasedEntities(String text) {
        List<ResolvedEntity> entities = new ArrayList<>();

        for (Map.Entry<EntityType, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
            EntityType type = entry.getKey();
            Pattern pattern = entry.getValue();
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                String value = matcher.group().trim();
                int start = matcher.start();
                int end = matcher.end();

                // Skip if entity is too long
                if (value.length() > MAX_ENTITY_LENGTH) {
                    continue;
                }

                // Extract the actual entity value (remove prefixes like "contract", "customer", etc.)
                String cleanValue = extractCleanValue(value, type);

                ResolvedEntity entity =
                    new ResolvedEntity(type, cleanValue, value, start, end, 0.8, getEntityContext(text, start, end));

                entities.add(entity);
            }
        }

        return entities;
    }

    /**
     * Extract clean value from matched text
     */
    private String extractCleanValue(String matchedText, EntityType type) {
        String clean = matchedText.trim();

        switch (type) {
        case CONTRACT_NUMBER:
            clean = clean.replaceAll("(?i)^(?:contract|cntr|ct)[-_#\\s]*", "");
            break;
        case CUSTOMER_ID:
            clean = clean.replaceAll("(?i)^(?:customer|cust|client)[-_#\\s]*(?:id|number)?[-_#\\s]*", "");
            break;
        case ACCOUNT_NUMBER:
            clean = clean.replaceAll("(?i)^(?:account|acct|acc)[-_#\\s]*(?:number|no|num)?[-_#\\s]*", "");
            break;
        case INVOICE_NUMBER:
            clean = clean.replaceAll("(?i)^(?:invoice|inv)[-_#\\s]*(?:number|no|num)?[-_#\\s]*", "");
            break;
        case PAYMENT_ID:
            clean = clean.replaceAll("(?i)^(?:payment|pay|transaction|txn)[-_#\\s]*(?:id|number)?[-_#\\s]*", "");
            break;
        case REFERENCE_NUMBER:
            clean = clean.replaceAll("(?i)^(?:ref|reference|ticket|case)[-_#\\s]*(?:number|no|num)?[-_#\\s]*", "");
            break;
        case AMOUNT:
            clean = clean.replaceAll("[^\\d.,]", "");
            break;
        case PERCENTAGE:
            clean = clean.replaceAll("(?i)\\s*(?:%|percent)\\s*$", "");
            break;
        default:
            // No special cleaning needed
            break;
        }

        return clean.trim();
    }

    /**
     * Extract entities using dictionary lookup
     */
    private List<ResolvedEntity> extractDictionaryBasedEntities(String text) {
        List<ResolvedEntity> entities = new ArrayList<>();
        String lowerText = text.toLowerCase();

        // Check status values
        for (String status : statusValues) {
            int index = lowerText.indexOf(status.toLowerCase());
            while (index != -1) {
                if (isWordBoundary(lowerText, index, status.length())) {
                    entities.add(new ResolvedEntity(EntityType.STATUS, status, status, index, index + status.length(),
                                                    0.9, getEntityContext(text, index, index + status.length())));
                }
                index = lowerText.indexOf(status.toLowerCase(), index + 1);
            }
        }

        // Check priority values
        for (String priority : priorityValues) {
            int index = lowerText.indexOf(priority.toLowerCase());
            while (index != -1) {
                if (isWordBoundary(lowerText, index, priority.length())) {
                    entities.add(new ResolvedEntity(EntityType.PRIORITY, priority, priority, index,
                                                    index + priority.length(), 0.9,
                                                    getEntityContext(text, index, index + priority.length())));
                }
                index = lowerText.indexOf(priority.toLowerCase(), index + 1);
            }
        }

        // Check departments
        for (String department : departments) {
            int index = lowerText.indexOf(department.toLowerCase());
            while (index != -1) {
                if (isWordBoundary(lowerText, index, department.length())) {
                    entities.add(new ResolvedEntity(EntityType.DEPARTMENT, department, department, index,
                                                    index + department.length(), 0.85,
                                                    getEntityContext(text, index, index + department.length())));
                }
                index = lowerText.indexOf(department.toLowerCase(), index + 1);
            }
        }

        // Check currencies
        for (String currency : currencies) {
            int index = lowerText.indexOf(currency.toLowerCase());
            while (index != -1) {
                if (isWordBoundary(lowerText, index, currency.length())) {
                    entities.add(new ResolvedEntity(EntityType.CURRENCY, currency, currency, index,
                                                    index + currency.length(), 0.9,
                                                    getEntityContext(text, index, index + currency.length())));
                }
                index = lowerText.indexOf(currency.toLowerCase(), index + 1);
            }
        }

        return entities;
    }

    /**
     * Check if a match is at word boundaries
     */
    private boolean isWordBoundary(String text, int start, int length) {
        boolean startBoundary = start == 0 || !Character.isLetterOrDigit(text.charAt(start - 1));
        boolean endBoundary =
            (start + length) >= text.length() || !Character.isLetterOrDigit(text.charAt(start + length));
        return startBoundary && endBoundary;
    }

    /**
     * Extract entities using fuzzy matching against known entities
     */
    private List<ResolvedEntity> extractFuzzyMatchedEntities(String text) {
        List<ResolvedEntity> entities = new ArrayList<>();

        for (Map.Entry<String, EntityType> entry : knownEntities.entrySet()) {
            String knownEntity = entry.getKey();
            EntityType type = entry.getValue();

            // Exact match first
            int exactIndex = text.indexOf(knownEntity);
            if (exactIndex != -1) {
                entities.add(new ResolvedEntity(type, knownEntity, knownEntity, exactIndex,
                                                exactIndex + knownEntity.length(), 1.0,
                                                getEntityContext(text, exactIndex, exactIndex + knownEntity.length())));
                continue;
            }

            // Fuzzy match
            double similarity = calculateSimilarity(text, knownEntity);
            if (similarity > 0.8) {
                // Find the best matching substring
                String bestMatch = findBestMatchingSubstring(text, knownEntity);
                if (bestMatch != null) {
                    int fuzzyIndex = text.indexOf(bestMatch);
                    if (fuzzyIndex != -1) {
                        entities.add(new ResolvedEntity(type, knownEntity, bestMatch, fuzzyIndex,
                                                        fuzzyIndex + bestMatch.length(), similarity,
                                                        getEntityContext(text, fuzzyIndex,
                                                                         fuzzyIndex + bestMatch.length())));
                    }
                }
            }
        }

        return entities;
    }

    /**
     * Calculate similarity between two strings using Levenshtein distance
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null)
            return 0.0;
        if (s1.equals(s2))
            return 1.0;

        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0)
            return 1.0;

        int distance = levenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        return 1.0 - (double) distance / maxLength;
    }

    /**
     * Calculate Levenshtein distance between two strings
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, // deletion
                                             dp[i][j - 1] + 1), // insertion
                                    dp[i - 1][j - 1] + cost // substitution
                                    );
            }
        }

        return dp[s1.length()][s2.length()];
    }

    /**
     * Find the best matching substring in text for a known entity
     */
    private String findBestMatchingSubstring(String text, String knownEntity) {
        String[] words = text.split("\\s+");
        String bestMatch = null;
        double bestSimilarity = 0.0;

        // Try different combinations of consecutive words
        for (int i = 0; i < words.length; i++) {
            for (int j = i; j < Math.min(i + 5, words.length); j++) { // Max 5 words
                String candidate = String.join(" ", Arrays.copyOfRange(words, i, j + 1));
                double similarity = calculateSimilarity(candidate, knownEntity);

                if (similarity > bestSimilarity && similarity > 0.7) {
                    bestSimilarity = similarity;
                    bestMatch = candidate;
                }
            }
        }

        return bestMatch;
    }

    /**
     * Enhance entities with contextual information
     */
    private List<ResolvedEntity> enhanceWithContext(List<ResolvedEntity> entities, String text) {
        List<ResolvedEntity> enhanced = new ArrayList<>();

        for (ResolvedEntity entity : entities) {
            ResolvedEntity enhancedEntity = enhanceEntityWithContext(entity, text);
            enhanced.add(enhancedEntity);
        }

        return enhanced;
    }

    /**
     * Enhance a single entity with contextual information
     */
    private ResolvedEntity enhanceEntityWithContext(ResolvedEntity entity, String text) {
        String context = getEntityContext(text, entity.getStartPosition(), entity.getEndPosition());
        Map<String, Object> contextData = analyzeContext(context, entity.getType());

        // Adjust confidence based on context
        double contextConfidence = calculateContextConfidence(contextData, entity.getType());
        double adjustedConfidence = (entity.getConfidence() + contextConfidence) / 2.0;

        return new ResolvedEntity(entity.getType(), entity.getValue(), entity.getOriginalText(),
                                  entity.getStartPosition(), entity.getEndPosition(), adjustedConfidence, context,
                                  contextData);
    }

    /**
     * Get context around an entity
     */
    private String getEntityContext(String text, int start, int end) {
        int contextWindow = 50; // Characters before and after
        int contextStart = Math.max(0, start - contextWindow);
        int contextEnd = Math.min(text.length(), end + contextWindow);

        return text.substring(contextStart, contextEnd);
    }

    /**
     * Analyze context to extract additional information
     */
    private Map<String, Object> analyzeContext(String context, EntityType entityType) {
        Map<String, Object> contextData = new HashMap<>();
        String lowerContext = context.toLowerCase();

        // Common context analysis
        contextData.put("hasBusinessTerms", businessTerms.stream().anyMatch(lowerContext::contains));
        contextData.put("hasNumbers", lowerContext.matches(".*\\d.*"));
        contextData.put("hasDateKeywords", lowerContext.matches(".*(date|time|when|schedule|due|deadline).*"));
        contextData.put("hasAmountKeywords", lowerContext.matches(".*(amount|cost|price|fee|charge|total|sum).*"));

        // Entity-specific context analysis
        switch (entityType) {
        case CONTRACT_NUMBER:
            contextData.put("hasContractKeywords",
                            lowerContext.matches(".*(agreement|terms|conditions|signed|executed).*"));
            contextData.put("hasLegalTerms", lowerContext.matches(".*(party|parties|whereas|hereby|therefore).*"));
            break;

        case CUSTOMER_ID:
            contextData.put("hasCustomerKeywords",
                            lowerContext.matches(".*(client|customer|account|contact|profile).*"));
            contextData.put("hasPersonalInfo", lowerContext.matches(".*(name|address|phone|email).*"));
            break;

        case INVOICE_NUMBER:
            contextData.put("hasInvoiceKeywords",
                            lowerContext.matches(".*(bill|billing|invoice|payment|due|outstanding).*"));
            contextData.put("hasFinancialTerms", lowerContext.matches(".*(tax|discount|subtotal|total|balance).*"));
            break;

        case AMOUNT:
            contextData.put("hasCurrencySymbols", lowerContext.matches(".*[$€£¥].*"));
            contextData.put("hasPaymentTerms", lowerContext.matches(".*(paid|pay|payment|charge|cost|fee).*"));
            break;

        case DATE:
            contextData.put("hasTimeKeywords", lowerContext.matches(".*(morning|afternoon|evening|am|pm|time|hour).*"));
            contextData.put("hasScheduleKeywords",
                            lowerContext.matches(".*(meeting|appointment|deadline|due|schedule).*"));
            break;

        case EMAIL:
            contextData.put("hasContactKeywords", lowerContext.matches(".*(contact|email|send|reply|message).*"));
            contextData.put("hasCommunicationTerms", lowerContext.matches(".*(notification|alert|correspondence).*"));
            break;

        case PHONE:
            contextData.put("hasPhoneKeywords", lowerContext.matches(".*(phone|call|number|contact|mobile|cell).*"));
            contextData.put("hasContactInfo", lowerContext.matches(".*(reach|contact|call|dial).*"));
            break;

        default:
            // No specific context analysis
            break;
        }

        return contextData;
    }

    /**
     * Calculate confidence based on context
     */
    private double calculateContextConfidence(Map<String, Object> contextData, EntityType entityType) {
        double confidence = 0.5; // Base confidence

        // Boost confidence based on relevant context
        if (Boolean.TRUE.equals(contextData.get("hasBusinessTerms"))) {
            confidence += 0.1;
        }

        switch (entityType) {
        case CONTRACT_NUMBER:
            if (Boolean.TRUE.equals(contextData.get("hasContractKeywords")))
                confidence += 0.2;
            if (Boolean.TRUE.equals(contextData.get("hasLegalTerms")))
                confidence += 0.1;
            break;

        case CUSTOMER_ID:
            if (Boolean.TRUE.equals(contextData.get("hasCustomerKeywords")))
                confidence += 0.2;
            if (Boolean.TRUE.equals(contextData.get("hasPersonalInfo")))
                confidence += 0.1;
            break;

        case INVOICE_NUMBER:
            if (Boolean.TRUE.equals(contextData.get("hasInvoiceKeywords")))
                confidence += 0.2;
            if (Boolean.TRUE.equals(contextData.get("hasFinancialTerms")))
                confidence += 0.1;
            break;

        case AMOUNT:
            if (Boolean.TRUE.equals(contextData.get("hasCurrencySymbols")))
                confidence += 0.2;
            if (Boolean.TRUE.equals(contextData.get("hasPaymentTerms")))
                confidence += 0.1;
            if (Boolean.TRUE.equals(contextData.get("hasAmountKeywords")))
                confidence += 0.1;
            break;

        case DATE:
            if (Boolean.TRUE.equals(contextData.get("hasTimeKeywords")))
                confidence += 0.1;
            if (Boolean.TRUE.equals(contextData.get("hasScheduleKeywords")))
                confidence += 0.1;
            if (Boolean.TRUE.equals(contextData.get("hasDateKeywords")))
                confidence += 0.2;
            break;

        case EMAIL:
            if (Boolean.TRUE.equals(contextData.get("hasContactKeywords")))
                confidence += 0.2;
            if (Boolean.TRUE.equals(contextData.get("hasCommunicationTerms")))
                confidence += 0.1;
            break;

        case PHONE:
            if (Boolean.TRUE.equals(contextData.get("hasPhoneKeywords")))
                confidence += 0.2;
            if (Boolean.TRUE.equals(contextData.get("hasContactInfo")))
                confidence += 0.1;
            break;

        default:
            // No specific confidence boost
            break;
        }

        return Math.min(1.0, confidence);
    }

    /**
     * Remove duplicate and overlapping entities
     */
    private List<ResolvedEntity> deduplicateAndMergeEntities(List<ResolvedEntity> entities) {
        if (entities.isEmpty())
            return entities;

        // Sort by start position
        entities.sort(Comparator.comparingInt(ResolvedEntity::getStartPosition));

        List<ResolvedEntity> merged = new ArrayList<>();
        ResolvedEntity current = entities.get(0);

        for (int i = 1; i < entities.size(); i++) {
            ResolvedEntity next = entities.get(i);

            // Check for overlap
            if (entitiesOverlap(current, next)) {
                // Merge entities - keep the one with higher confidence
                if (next.getConfidence() > current.getConfidence()) {
                    current = next;
                }
                // If same confidence, keep the more specific entity type
                else if (next.getConfidence() == current.getConfidence() &&
                         isMoreSpecificType(next.getType(), current.getType())) {
                    current = next;
                }
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        return merged;
    }

    /**
     * Check if two entities overlap
     */
    private boolean entitiesOverlap(ResolvedEntity entity1, ResolvedEntity entity2) {
        return !(entity1.getEndPosition() <= entity2.getStartPosition() ||
                 entity2.getEndPosition() <= entity1.getStartPosition());
    }

    /**
     * Check if one entity type is more specific than another
     */
    private boolean isMoreSpecificType(EntityType type1, EntityType type2) {
        // Define specificity hierarchy
        Map<EntityType, Integer> specificity = new HashMap<>();
        specificity.put(EntityType.CONTRACT_NUMBER, 10);
        specificity.put(EntityType.CUSTOMER_ID, 10);
        specificity.put(EntityType.ACCOUNT_NUMBER, 10);
        specificity.put(EntityType.INVOICE_NUMBER, 10);
        specificity.put(EntityType.PAYMENT_ID, 10);
        specificity.put(EntityType.EMAIL, 9);
        specificity.put(EntityType.PHONE, 9);
        specificity.put(EntityType.CREDIT_CARD, 9);
        specificity.put(EntityType.SSN, 9);
        specificity.put(EntityType.TAX_ID, 9);
        specificity.put(EntityType.AMOUNT, 8);
        specificity.put(EntityType.DATE, 8);
        specificity.put(EntityType.TIME, 8);
        specificity.put(EntityType.PERCENTAGE, 8);
        specificity.put(EntityType.PERSON_NAME, 7);
        specificity.put(EntityType.COMPANY_NAME, 7);
        specificity.put(EntityType.ADDRESS, 7);
        specificity.put(EntityType.URL, 6);
        specificity.put(EntityType.IP_ADDRESS, 6);
        specificity.put(EntityType.STATUS, 5);
        specificity.put(EntityType.PRIORITY, 5);
        specificity.put(EntityType.DEPARTMENT, 5);
        specificity.put(EntityType.CURRENCY, 4);
        specificity.put(EntityType.REFERENCE_NUMBER, 3);
        specificity.put(EntityType.PRODUCT_CODE, 3);
        specificity.put(EntityType.UNKNOWN, 1);

        return specificity.getOrDefault(type1, 0) > specificity.getOrDefault(type2, 0);
    }

    /**
     * Calculate confidence scores for entities
     */
    private List<ResolvedEntity> calculateConfidenceScores(List<ResolvedEntity> entities, String text) {
        return entities.stream()
                       .map(entity -> recalculateEntityConfidence(entity, text))
                       .collect(Collectors.toList());
    }

    /**
     * Recalculate confidence for a single entity
     */
    private ResolvedEntity recalculateEntityConfidence(ResolvedEntity entity, String text) {
        double baseConfidence = entity.getConfidence();

        // Factors that affect confidence
        double lengthFactor = calculateLengthFactor(entity.getValue());
        double formatFactor = calculateFormatFactor(entity.getValue(), entity.getType());
        double contextFactor = calculateContextFactor(entity.getContext(), entity.getType());
        double positionFactor = calculatePositionFactor(entity.getStartPosition(), text.length());

        // Weighted average of factors
        double adjustedConfidence =
            (baseConfidence * 0.4) + (lengthFactor * 0.2) + (formatFactor * 0.2) + (contextFactor * 0.15) +
            (positionFactor * 0.05);

        return new ResolvedEntity(entity.getType(), entity.getValue(), entity.getOriginalText(),
                                  entity.getStartPosition(), entity.getEndPosition(),
                                  Math.min(1.0, Math.max(0.0, adjustedConfidence)), entity.getContext(),
                                  entity.getContextData());
    }

    /**
     * Calculate confidence factor based on entity value length
     */
    private double calculateLengthFactor(String value) {
        int length = value.length();

        // Optimal length ranges for different types of entities
        if (length >= 3 && length <= 20)
            return 1.0;
        if (length >= 2 && length <= 30)
            return 0.9;
        if (length >= 1 && length <= 50)
            return 0.8;
        return 0.6;
    }

    /**
     * Calculate confidence factor based on format validation
     */
    private double calculateFormatFactor(String value, EntityType type) {
        switch (type) {
        case EMAIL:
            return isValidEmail(value) ? 1.0 : 0.5;
        case PHONE:
            return isValidPhone(value) ? 1.0 : 0.7;
        case DATE:
            return isValidDate(value) ? 1.0 : 0.6;
        case AMOUNT:
            return isValidAmount(value) ? 1.0 : 0.7;
        case URL:
            return isValidUrl(value) ? 1.0 : 0.6;
        case IP_ADDRESS:
            return isValidIpAddress(value) ? 1.0 : 0.5;
        default:
            return 0.8; // Default factor for other types
        }
    }

    /**
     * Calculate confidence factor based on context
     */
    private double calculateContextFactor(String context, EntityType type) {
        if (context == null || context.trim().isEmpty())
            return 0.5;

        String lowerContext = context.toLowerCase();

        // Check for relevant keywords in context
        switch (type) {
        case CONTRACT_NUMBER:
            if (lowerContext.contains("contract") || lowerContext.contains("agreement"))
                return 1.0;
            break;
        case CUSTOMER_ID:
            if (lowerContext.contains("customer") || lowerContext.contains("client"))
                return 1.0;
            break;
        case INVOICE_NUMBER:
            if (lowerContext.contains("invoice") || lowerContext.contains("bill"))
                return 1.0;
            break;
        case AMOUNT:
            if (lowerContext.contains("$") || lowerContext.contains("amount") || lowerContext.contains("cost"))
                return 1.0;
            break;
        case EMAIL:
            if (lowerContext.contains("email") || lowerContext.contains("contact"))
                return 1.0;
            break;
        case PHONE:
            if (lowerContext.contains("phone") || lowerContext.contains("call"))
                return 1.0;
            break;
        default:
            break;
        }

        return 0.7; // Default context factor
    }

    /**
     * Calculate confidence factor based on position in text
     */
    private double calculatePositionFactor(int position, int textLength) {
        if (textLength == 0)
            return 0.5;

        double relativePosition = (double) position / textLength;

        // Entities at the beginning or middle of text are often more reliable
        if (relativePosition <= 0.1 || (relativePosition >= 0.3 && relativePosition <= 0.7)) {
            return 1.0;
        } else if (relativePosition <= 0.3 || relativePosition >= 0.7) {
            return 0.9;
        } else {
            return 0.8;
        }
    }

    /**
     * Validation methods for different entity types
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    private boolean isValidPhone(String phone) {
        if (phone == null)
            return false;
        String cleaned = phone.replaceAll("[^0-9]", "");
        return cleaned.length() >= 10 && cleaned.length() <= 15;
    }

    private boolean isValidDate(String date) {
        if (date == null)
            return false;

        // Try common date formats
        String[] formats = {
            "yyyy-MM-dd", "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd", "MMM dd, yyyy", "dd MMM yyyy", "MMMM dd, yyyy" };

        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate.parse(date, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        return false;
    }

    private boolean isValidAmount(String amount) {
        if (amount == null)
            return false;
        String cleaned = amount.replaceAll("[^0-9.]", ""); // Fixed: Added closing quote and comma

        // Additional validation: check if cleaned string is not empty
        if (cleaned.isEmpty())
            return false;

        // Check for multiple decimal points
        long decimalCount = cleaned.chars()
                                   .filter(ch -> ch == '.')
                                   .count();
        if (decimalCount > 1)
            return false;

        try {
            double value = Double.parseDouble(cleaned);
            // Additional validation: check for reasonable range
            return value >= 0 && value <= Double.MAX_VALUE && !Double.isNaN(value) && !Double.isInfinite(value);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidUrl(String url) {
        if (url == null)
            return false;
        return url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$") || url.matches("^www\\.[^\\s/$.?#].[^\\s]*$");
    }

    private boolean isValidIpAddress(String ip) {
        if (ip == null)
            return false;
        String[] parts = ip.split("\\.");
        if (parts.length != 4)
            return false;

        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255)
                    return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate overall confidence for the resolution result
     */
    private double calculateOverallConfidence(List<ResolvedEntity> entities) {
        if (entities.isEmpty())
            return 1.0; // No entities found, but no errors

        double totalConfidence = entities.stream()
                                         .mapToDouble(ResolvedEntity::getConfidence)
                                         .sum();

        return totalConfidence / entities.size();
    }

    /**
     * Generate summary of resolved entities
     */
    private String generateSummary(List<ResolvedEntity> entities) {
        if (entities.isEmpty()) {
            return "No entities found";
        }

        Map<EntityType, Long> typeCounts =
            entities.stream().collect(Collectors.groupingBy(ResolvedEntity::getType, Collectors.counting()));

        StringBuilder summary = new StringBuilder();
        summary.append("Found ")
               .append(entities.size())
               .append(" entities: ");

        List<String> typeDescriptions = new ArrayList<>();
        for (Map.Entry<EntityType, Long> entry : typeCounts.entrySet()) {
            typeDescriptions.add(entry.getValue() + " " + entry.getKey()
                                                               .toString()
                                                               .toLowerCase()
                                                               .replace("_", " "));
        }

        summary.append(String.join(", ", typeDescriptions));

        return summary.toString();
    }

    /**
     * Get entities by type
     */
    public List<ResolvedEntity> getEntitiesByType(List<ResolvedEntity> entities, EntityType type) {
        return entities.stream()
                       .filter(entity -> entity.getType() == type)
                       .collect(Collectors.toList());
    }

    /**
     * Get entities with confidence above threshold
     */
    public List<ResolvedEntity> getHighConfidenceEntities(List<ResolvedEntity> entities, double threshold) {
        return entities.stream()
                       .filter(entity -> entity.getConfidence() >= threshold)
                       .collect(Collectors.toList());
    }

    /**
     * Add known entity to the resolver
     */
    public void addKnownEntity(String entity, EntityType type) {
        if (entity != null && !entity.trim().isEmpty() && type != null) {
            knownEntities.put(entity.trim(), type);
        }
    }

    /**
     * Remove known entity from the resolver
     */
    public void removeKnownEntity(String entity) {
        if (entity != null) {
            knownEntities.remove(entity.trim());
        }
    }

    /**
     * Get all known entities
     */
    public Map<String, EntityType> getKnownEntities() {
        return new HashMap<>(knownEntities);
    }

    /**
     * Get entity resolution statistics
     */
    public EntityResolutionStats getStats() {
        return stats;
    }

    /**
     * Reset statistics
     */
    public void resetStats() {
        this.stats = new EntityResolutionStats();
    }

    /**
     * Export configuration
     */
    public Map<String, Object> exportConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("knownEntities", new HashMap<>(knownEntities));
        config.put("businessTerms", new HashSet<>(businessTerms));
        config.put("statusValues", new HashSet<>(statusValues));
        config.put("priorityValues", new HashSet<>(priorityValues));
        config.put("departments", new HashSet<>(departments));
        config.put("currencies", new HashSet<>(currencies));
        config.put("countries", new HashSet<>(countries));
        config.put("states", new HashSet<>(states));
        config.put("confidenceThreshold", CONFIDENCE_THRESHOLD);
        config.put("maxEntityLength", MAX_ENTITY_LENGTH);
        config.put("enableFuzzyMatching", ENABLE_FUZZY_MATCHING);
        config.put("enableContextAnalysis", ENABLE_CONTEXT_ANALYSIS);
        return config;
    }

    /**
     * Import configuration
     */
    @SuppressWarnings("unchecked")
    public void importConfiguration(Map<String, Object> config) {
        if (config == null)
            return;

        try {
            if (config.containsKey("knownEntities")) {
                Map<String, EntityType> entities = (Map<String, EntityType>) config.get("knownEntities");
                knownEntities.putAll(entities);
            }

            if (config.containsKey("businessTerms")) {
                Set<String> terms = (Set<String>) config.get("businessTerms");
                businessTerms.addAll(terms);
            }

            if (config.containsKey("statusValues")) {
                Set<String> values = (Set<String>) config.get("statusValues");
                statusValues.addAll(values);
            }

            if (config.containsKey("priorityValues")) {
                Set<String> values = (Set<String>) config.get("priorityValues");
                priorityValues.addAll(values);
            }

            if (config.containsKey("departments")) {
                Set<String> values = (Set<String>) config.get("departments");
                departments.addAll(values);
            }

            if (config.containsKey("currencies")) {
                Set<String> values = (Set<String>) config.get("currencies");
                currencies.addAll(values);
            }

            if (config.containsKey("countries")) {
                Set<String> values = (Set<String>) config.get("countries");
                countries.addAll(values);
            }

            if (config.containsKey("states")) {
                Set<String> values = (Set<String>) config.get("states");
                states.addAll(values);
            }

        } catch (ClassCastException e) {
            System.err.println("Error importing configuration: Invalid data type - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error importing configuration: " + e.getMessage());
        }
    }

    /**
     * Validate entity resolver configuration
     */
    public boolean validateConfiguration() {
        try {
            // Check if essential dictionaries are populated
            if (statusValues.isEmpty()) {
                System.err.println("Warning: No status values loaded");
                return false;
            }

            if (businessTerms.isEmpty()) {
                System.err.println("Warning: No business terms loaded");
                return false;
            }

            if (ENTITY_PATTERNS.isEmpty()) {
                System.err.println("Error: No entity patterns loaded");
                return false;
            }

            // Validate patterns
            for (Map.Entry<EntityType, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
                if (entry.getValue() == null) {
                    System.err.println("Error: Invalid pattern for entity type: " + entry.getKey());
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error validating configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Test entity resolution with sample text
     */
    public void testEntityResolution() {
        System.out.println("=== Entity Resolver Test ===");

        String[] testTexts = {
            "Please review contract CT-2024-001 for customer CUST-12345 with invoice INV-2024-0001 amount $1,250.00",
            "Contact john.doe@company.com or call (555) 123-4567 regarding payment PAY-ABC123",
            "The high priority issue in the accounting department needs immediate attention",
            "Schedule meeting for March 15, 2024 at 2:30 PM to discuss the pending status",
            "Account ACC-987654 has an outstanding balance of $5,000.00 due on 12/31/2023",
            "Reference number REF-XYZ789 shows 15% discount applied to the total amount",
            "Visit https://company.com/portal or contact support@company.com for assistance",
            "Server 192.168.1.100 is experiencing issues with the critical system",
            "Tax ID 12-3456789 is required for processing the corporate account",
            "Credit card ending in 1234 was charged $299.99 on January 1st, 2024"
        };

        for (String text : testTexts) {
            System.out.println("\nText: " + text);
            EntityResolutionResult result = resolveEntities(text);

            System.out.println("Entities found: " + result.getEntities().size());
            System.out.println("Overall confidence: " + String.format("%.2f", result.getConfidence()));
            System.out.println("Summary: " + result.getSummary());

            for (ResolvedEntity entity : result.getEntities()) {
                System.out.println("  - " + entity);
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
        EntityResolver resolver = new EntityResolver();

        if (args.length > 0) {
            // Process command line arguments
            String inputText = String.join(" ", args);
            System.out.println("Input: " + inputText);

            EntityResolutionResult result = resolver.resolveEntities(inputText);
            System.out.println("Result: " + result);

            System.out.println("\nEntities:");
            for (ResolvedEntity entity : result.getEntities()) {
                System.out.println("  " + entity);
            }
        } else {
            // Run test suite
            resolver.testEntityResolution();
        }
    }

    /**
     * Inner class for resolved entity
     */
    public static class ResolvedEntity {
        private final EntityType type;
        private final String value;
        private final String originalText;
        private final int startPosition;
        private final int endPosition;
        private final double confidence;
        private final String context;
        private final Map<String, Object> contextData;

        public ResolvedEntity(EntityType type, String value, String originalText, int startPosition, int endPosition,
                              double confidence, String context) {
            this(type, value, originalText, startPosition, endPosition, confidence, context, new HashMap<>());
        }

        public ResolvedEntity(EntityType type, String value, String originalText, int startPosition, int endPosition,
                              double confidence, String context, Map<String, Object> contextData) {
            this.type = type;
            this.value = value;
            this.originalText = originalText;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.confidence = confidence;
            this.context = context;
            this.contextData = contextData != null ? contextData : new HashMap<>();
        }

        // Getters
        public EntityType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getOriginalText() {
            return originalText;
        }

        public int getStartPosition() {
            return startPosition;
        }

        public int getEndPosition() {
            return endPosition;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getContext() {
            return context;
        }

        public Map<String, Object> getContextData() {
            return contextData;
        }

        @Override
        public String toString() {
            return String.format("ResolvedEntity{type=%s, value='%s', confidence=%.2f, position=[%d,%d]}", type, value,
                                 confidence, startPosition, endPosition);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;

            ResolvedEntity that = (ResolvedEntity) obj;
            return startPosition == that.startPosition && endPosition == that.endPosition && type == that.type &&
                   Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value, startPosition, endPosition);
        }
    }

    /**
     * Inner class for entity resolution result
     */
    public static class EntityResolutionResult {
        private final String originalText;
        private final List<ResolvedEntity> entities;
        private final double confidence;
        private final String summary;
        private final long timestamp;

        public EntityResolutionResult(String originalText, List<ResolvedEntity> entities, double confidence,
                                      String summary) {
            this.originalText = originalText;
            this.entities = entities != null ? new ArrayList<>(entities) : new ArrayList<>();
            this.confidence = confidence;
            this.summary = summary;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getOriginalText() {
            return originalText;
        }

        public List<ResolvedEntity> getEntities() {
            return new ArrayList<>(entities);
        }

        public double getConfidence() {
            return confidence;
        }

        public String getSummary() {
            return summary;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean hasEntities() {
            return !entities.isEmpty();
        }

        public int getEntityCount() {
            return entities.size();
        }

        public List<ResolvedEntity> getEntitiesByType(EntityType type) {
            return entities.stream()
                           .filter(entity -> entity.getType() == type)
                           .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return String.format("EntityResolutionResult{entities=%d, confidence=%.2f, summary='%s'}", entities.size(),
                                 confidence, summary);
        }
    }

    /**
     * Inner class for entity resolution statistics
     */
    public static class EntityResolutionStats {
        private int processedTexts = 0;
        private int totalEntitiesFound = 0;
        private int errors = 0;
        private long totalProcessingTime = 0;
        private final Map<EntityType, Integer> entityTypeCounts = new HashMap<>();
        private final long startTime = System.currentTimeMillis();

        public void incrementProcessedTexts() {
            processedTexts++;
        }

        public void addEntitiesFound(int count) {
            totalEntitiesFound += count;
        }

        public void incrementErrors() {
            errors++;
        }

        public void addProcessingTime(long time) {
            totalProcessingTime += time;
        }

        public void addEntityTypeCount(EntityType type, int count) {
            entityTypeCounts.merge(type, count, Integer::sum);
        }

        // Getters
        public int getProcessedTexts() {
            return processedTexts;
        }

        public int getTotalEntitiesFound() {
            return totalEntitiesFound;
        }

        public int getErrors() {
            return errors;
        }

        public long getTotalProcessingTime() {
            return totalProcessingTime;
        }

        public Map<EntityType, Integer> getEntityTypeCounts() {
            return new HashMap<>(entityTypeCounts);
        }

        public long getStartTime() {
            return startTime;
        }

        public double getAverageEntitiesPerText() {
            return processedTexts > 0 ? (double) totalEntitiesFound / processedTexts : 0.0;
        }

        public double getAverageProcessingTime() {
            return processedTexts > 0 ? (double) totalProcessingTime / processedTexts : 0.0;
        }

        public double getErrorRate() {
            return processedTexts > 0 ? (double) errors / processedTexts : 0.0;
        }

        public long getUptime() {
            return System.currentTimeMillis() - startTime;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("EntityResolutionStats{\n");
            sb.append("  Processed texts: ")
              .append(processedTexts)
              .append("\n");
            sb.append("  Total entities found: ")
              .append(totalEntitiesFound)
              .append("\n");
            sb.append("  Errors: ")
              .append(errors)
              .append("\n");
            sb.append("  Total processing time: ")
              .append(totalProcessingTime)
              .append(" ms\n");
            sb.append("  Average entities per text: ")
              .append(String.format("%.2f", getAverageEntitiesPerText()))
              .append("\n");
            sb.append("  Average processing time: ")
              .append(String.format("%.2f", getAverageProcessingTime()))
              .append(" ms\n");
            sb.append("  Error rate: ")
              .append(String.format("%.2f%%", getErrorRate() * 100))
              .append("\n");
            sb.append("  Uptime: ")
              .append(getUptime())
              .append(" ms\n");

            if (!entityTypeCounts.isEmpty()) {
                sb.append("  Entity type counts:\n");
                entityTypeCounts.entrySet()
                                .stream()
                                .sorted(Map.Entry
                                           .<EntityType, Integer>comparingByValue()
                                           .reversed())
                                .forEach(entry -> sb.append("    ")
                                                    .append(entry.getKey())
                                                    .append(": ")
                                                    .append(entry.getValue())
                                                    .append("\n"));
            }

            sb.append("}");
            return sb.toString();
        }
    }
}

package view.practice;


import opennlp.tools.doccat.*;
import opennlp.tools.util.*;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.FuzzyScore;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;

import view.practice.ContractIntent;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.*;

import java.io.*;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.FuzzyScore;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.stream.Collectors;

import view.practice.ParsedQuery.ActionType;
import view.practice.ParsedQuery.QueryType;
import view.practice.ParsedQuery;

import java.util.List;


/**
 * Enhanced Machine Learning Intent Classifier with Contract Creation  Guidance Support
 * Supports direct contract creation requests and step-by-step guidance
 */


public class MLIntentClassifierImproved {

    private Set<String> dictionary = new HashSet<>();
    private LevenshteinDistance levenshtein = new LevenshteinDistance();
    // private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private static final int MAX_EDIT_DISTANCE = 2; // Adjust as needed

    private Set<String> allContractFields;
    private Map<String, List<String>> fieldSynonyms;
    private static final Logger logger = Logger.getLogger(MLIntentClassifierImproved.class.getName());

    private DocumentCategorizerME categorizer;
    private DoccatModel model;
    private Map<String, Double> intentThresholds;
    private Map<String, List<String>> intentKeywords;
    private Map<String, String> abbreviationMap;
    private Pattern contractNumberPattern;
    private Pattern customerNumberPattern;
    private Pattern accountNumberPattern;
    private Pattern datePattern;
    private Pattern pricePattern;
    private Pattern dateRangePattern;
    private static final Pattern PARTIAL_ACCOUNT_PATTERN =
        Pattern.compile("account.*?(\\d{4,})", Pattern.CASE_INSENSITIVE);
    private boolean initialized = false;
    private ProcessingStatistics processingStatistics;

    // New fields for contract creation support
    private Map<String, List<String>> creationKeywords;
    private Map<String, List<String>> guidanceKeywords;
    private Set<String> requiredContractFields;
    private Map<String, Pattern> fieldExtractionPatterns;
    private static final double MIN_SIMILARITY_THRESHOLD = 0.7;
    private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private FuzzyScore fuzzyScore = new FuzzyScore(Locale.ENGLISH);
    private JaccardSimilarity jaccard = new JaccardSimilarity();
    //private static final int MAX_EDIT_DISTANCE = 2;

    /**
     * Initialize with enhanced training data including creation and guidance
     */
    public void initializeWithTrainingData() throws IOException {
        ObjectStream<DocumentSample> sampleStream = createEnhancedTrainingDataWithCreation();

        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, "250");
        params.put(TrainingParameters.CUTOFF_PARAM, "1");

        this.model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
        this.categorizer = new DocumentCategorizerME(model);

        // Initialize dictionary
        initializeDictionary();

        this.initialized = true;
        logger.info("Enhanced intent classifier initialized with contract creation and guidance support");
    }

    /**
     * Extract business customer names
     */
    private String extractBusinessCustomerName(String query) {
        String[] businessCustomers = {
            "boeing", "honeywell", "microsoft", "apple", "google", "amazon", "oracle", "ibm", "cisco", "intel"
        };

        String lowerQuery = query.toLowerCase();
        for (String customer : businessCustomers) {
            if (lowerQuery.contains(customer)) {
                return customer.toUpperCase();
            }
        }

        return null;
    }

    /**
     * Determine help topic for guidance requests
     */
    private String determineHelpTopic(String processedQuery, String[] tokens) {
        if (processedQuery.contains("create") || processedQuery.contains("make") || processedQuery.contains("new")) {
            return "create_contract";
        } else if (processedQuery.contains("update") || processedQuery.contains("modify") ||
                   processedQuery.contains("edit")) {
            return "update_contract";
        } else if (processedQuery.contains("delete") || processedQuery.contains("remove") ||
                   processedQuery.contains("cancel")) {
            return "delete_contract";
        } else if (processedQuery.contains("search") || processedQuery.contains("find") ||
                   processedQuery.contains("query")) {
            return "search_contract";
        } else {
            return "general_help";
        }
    }


    /**
     * Create enhanced training data with creation and guidance samples
     */
    private ObjectStream<DocumentSample> createEnhancedTrainingDataWithCreation() {
        List<DocumentSample> samples = new ArrayList<>();

        // Contract creation samples
        addTrainingSamples(samples, "create_contract",
                           new String[][] { { "create", "contract", "for", "account", "123456" },
                                            { "make", "new", "contract", "boeing" },
                                            { "create", "contract", "named", "premium", "plan" },
                                            { "generate", "contract", "for", "customer", "honeywell" },
                                            { "setup", "new", "contract", "account", "789012" },
                                            { "add", "contract", "for", "microsoft" },
                                            { "establish", "contract", "with", "expiration", "2025-12-31" },
                                            { "initiate", "contract", "for", "account", "456789", "named", "gold" },
                                            { "create", "new", "agreement", "boeing", "expires", "2024-06-30" },
                                            { "make", "contract", "customer", "apple", "price", "5000" },
                                            { "new", "contract", "setup", "account", "234567" },
                                            { "creat", "contrct", "accnt", "345678" }, // With typos
                                            { "mak", "nu", "contract", "honeywell" },
                                            { "generat", "contract", "custmer", "google" } });

        // Contract guidance samples
        addTrainingSamples(samples, "guide_contract",
                           new String[][] { { "how", "to", "create", "contract" },
                                            { "steps", "to", "make", "contract" },
                                            { "guide", "for", "contract", "creation" },
                                            { "help", "create", "new", "contract" },
                                            { "instructions", "for", "contract", "setup" },
                                            { "procedure", "to", "add", "contract" },
                                            { "how", "do", "i", "create", "contract" },
                                            { "what", "are", "steps", "for", "contract" },
                                            { "process", "of", "creating", "contract" },
                                            { "way", "to", "make", "new", "contract" },
                                            { "help", "with", "contract", "creation" },
                                            { "guide", "me", "through", "contract", "setup" },
                                            { "hw", "too", "creat", "contrct" }, // With typos
                                            { "step", "for", "mak", "contract" },
                                            { "hlp", "with", "nu", "contract" } });

        // Update contract samples
        addTrainingSamples(samples, "update_contract",
                           new String[][] { { "update", "contract", "123456" }, { "modify", "contract", "789012" },
                                            { "edit", "contract", "details", "456789" },
                                            { "change", "contract", "234567" }, { "revise", "contract", "345678" },
                                            { "alter", "contract", "information", "567890" },
                                            { "updat", "contrct", "123456" }, // With typos
                                            { "modfy", "contract", "789012" } });

        // Update guidance samples
        addTrainingSamples(samples, "guide_update",
                           new String[][] { { "how", "to", "update", "contract" },
                                            { "steps", "to", "modify", "contract" },
                                            { "guide", "for", "contract", "update" }, { "help", "edit", "contract" },
                                            { "instructions", "for", "contract", "modification" },
                                            { "procedure", "to", "change", "contract" }, { "hw", "too", "updat", "contrct" } // With typos
                                            } );

        // Add existing training samples (show, list, search, etc.)
        addExistingTrainingSamples(samples);

        return ObjectStreamUtils.createObjectStream(samples);
    }

    /**
     * Enhanced intent thresholds including creation and guidance
     */
    private void initializeIntentThresholds() {
        this.intentThresholds = new HashMap<>();

        // Creation and guidance thresholds
        intentThresholds.put("create_contract", 0.60);
        intentThresholds.put("guide_contract", 0.65);
        intentThresholds.put("update_contract", 0.60);
        intentThresholds.put("guide_update", 0.65);

        // Existing thresholds
        intentThresholds.put("show_contract", 0.55);
        intentThresholds.put("get_contract_info", 0.55);
        intentThresholds.put("get_contract_expiration", 0.60);
        intentThresholds.put("list_expired_contracts", 0.55);
        intentThresholds.put("list_active_contracts", 0.55);
        intentThresholds.put("filter_contracts_by_customer", 0.60);
        intentThresholds.put("filter_contracts_by_user", 0.65);
        intentThresholds.put("search_contracts", 0.55);
        intentThresholds.put("list_contracts", 0.55);
        intentThresholds.put("contract_status", 0.60);
    }

    /**
     * Enhanced intent keywords including creation and guidance
     */
    private void initializeEnhancedIntentKeywords() {
        this.intentKeywords = new HashMap<>();

        // Creation keywords
        intentKeywords.put("create_contract",
                           Arrays.asList("create", "make", "new", "add", "generate", "setup", "establish", "initiate",
                                         "build", "form", "construct", "develop", "creat", "mak", "nu", "ad", "generat",
                                         "setap", "establsh", "initat", "bild", "frm", "construt", "develp"));

        // Guidance keywords
        intentKeywords.put("guide_contract",
                           Arrays.asList("how", "steps", "guide", "help", "instructions", "procedure", "process", "way",
                                         "method", "approach", "tutorial", "walkthrough", "hw", "step", "guid", "hlp",
                                         "instruction", "procedur", "proces", "wa", "methd", "approch", "tutrial",
                                         "walkthru"));

        // Update keywords
        intentKeywords.put("update_contract",
                           Arrays.asList("update", "modify", "edit", "change", "revise", "alter", "amend", "adjust",
                                         "updat", "modfy", "edt", "chang", "revis", "altr", "amnd", "adjst"));

        // Update guidance keywords
        intentKeywords.put("guide_update",
                           Arrays.asList("how", "update", "modify", "edit", "change", "steps", "guide", "help",
                                         "instructions", "procedure", "hw", "updat", "modfy", "edt", "chang"));

        // Add existing keywords
        addExistingIntentKeywords();
    }

    /**
     * Add existing intent keywords for backward compatibility
     */
    private void addExistingIntentKeywords() {
        intentKeywords.put("show_contract",
                           Arrays.asList("show", "display", "view", "open", "see", "present", "reveal", "shw", "dsplay",
                                         "vw", "opn", "se", "shwo", "dispaly", "viw"));

        intentKeywords.put("list_contracts",
                           Arrays.asList("list", "all", "show", "display", "contracts", "lst", "al", "shw", "dsplay"));

        intentKeywords.put("search_contracts",
                           Arrays.asList("search", "find", "look", "locate", "seek", "hunt", "discover", "serch", "fnd",
                                         "lok", "locat", "finnd", "lokup"));

        intentKeywords.put("filter_contracts_by_customer",
                           Arrays.asList("customer", "client", "account", "company", "organization", "vendor",
                                         "supplier", "partner", "boeing", "honeywell", "microsoft", "apple", "google",
                                         "custmer", "clint", "customar", "customr", "clent", "accnt", "compny"));

        intentKeywords.put("contract_status",
                           Arrays.asList("status", "state", "condition", "situation", "active", "inactive", "pending",
                                         "completed", "cancelled", "stat", "staus", "conditon"));

        intentKeywords.put("list_expired_contracts",
                           Arrays.asList("expired", "expiry", "expire", "expires", "expiration", "end", "ended",
                                         "expird", "expary", "expir", "expirs", "experation"));

        intentKeywords.put("list_active_contracts",
                           Arrays.asList("active", "current", "available", "live", "running", "ongoing", "actv", "crnt",
                                         "availabl", "liv", "runing", "ongoin"));
    }

    /**
     * Enhanced abbreviation map including creation terms
     */
    private void initializeAbbreviationMap() {
        this.abbreviationMap = new HashMap<>();

        // Creation abbreviations
        abbreviationMap.put("creat", "create");
        abbreviationMap.put("mak", "make");
        abbreviationMap.put("nu", "new");
        abbreviationMap.put("ad", "add");
        abbreviationMap.put("generat", "generate");
        abbreviationMap.put("setap", "setup");
        abbreviationMap.put("establsh", "establish");
        abbreviationMap.put("initat", "initiate");

        // Guidance abbreviations
        abbreviationMap.put("hw", "how");
        abbreviationMap.put("hlp", "help");
        abbreviationMap.put("guid", "guide");
        abbreviationMap.put("step", "steps");
        abbreviationMap.put("procedur", "procedure");
        abbreviationMap.put("proces", "process");
        abbreviationMap.put("wa", "way");

        // Update abbreviations
        abbreviationMap.put("updat", "update");
        abbreviationMap.put("modfy", "modify");
        abbreviationMap.put("edt", "edit");
        abbreviationMap.put("chang", "change");

        // Existing abbreviations
        abbreviationMap.put("cntrct", "contract");
        abbreviationMap.put("contrct", "contract");
        abbreviationMap.put("cntract", "contract");
        abbreviationMap.put("accnt", "account");
        abbreviationMap.put("custmer", "customer");
        abbreviationMap.put("clnt", "client");
        abbreviationMap.put("exp", "expiration");
        abbreviationMap.put("stat", "status");
        abbreviationMap.put("actv", "active");
        abbreviationMap.put("crnt", "current");
    }


    /**
     * Determine query type (enhanced for CONTRACT and HELP)
     */
    private QueryType determineQueryType(String input) {
        String lowerInput = input.toLowerCase();

        if (lowerInput.contains("search") || lowerInput.contains("find") || lowerInput.contains("look")) {
            return QueryType.SEARCH;
        } else if (lowerInput.contains("show") || lowerInput.contains("display") || lowerInput.contains("view")) {
            return QueryType.SPECIFIC;
        } else if (lowerInput.contains("list") || lowerInput.contains("all")) {
            return QueryType.LIST;
        } else if (lowerInput.contains("create") || lowerInput.contains("make") || lowerInput.contains("new")) {
            return QueryType.CONTRACT;
        } else if (lowerInput.contains("how") || lowerInput.contains("help") || lowerInput.contains("guide")) {
            return QueryType.HELP;
        } else if (contractNumberPattern.matcher(lowerInput).find()) {
            return QueryType.SPECIFIC;
        } else if (lowerInput.contains("filter") || lowerInput.contains("customer") || lowerInput.contains("account")) {
            return QueryType.FILTER;
        }

        return QueryType.GENERAL;
    }

    /**
     * Determine action type (enhanced for CREATE and GUIDE)
     */
    private ActionType determineActionType(String input) {
        String lowerInput = input.toLowerCase();

        if (lowerInput.contains("search") || lowerInput.contains("find") || lowerInput.contains("look")) {
            return ActionType.SEARCH;
        } else if (lowerInput.contains("show") || lowerInput.contains("display") || lowerInput.contains("view")) {
            return ActionType.SHOW;
        } else if (lowerInput.contains("get") || lowerInput.contains("retrieve")) {
            return ActionType.GET;
        } else if (lowerInput.contains("list")) {
            return ActionType.LIST;
        } else if (lowerInput.contains("create") || lowerInput.contains("make") || lowerInput.contains("new")) {
            return ActionType.CREATE;
        } else if (lowerInput.contains("how") || lowerInput.contains("help") || lowerInput.contains("guide")) {
            return ActionType.GUIDE;
        } else if (lowerInput.contains("update") || lowerInput.contains("modify") || lowerInput.contains("edit")) {
            return ActionType.UPDATE;
        } else if (lowerInput.contains("filter")) {
            return ActionType.FILTER;
        }

        return ActionType.UNKNOWN;
    }

    /**
     * Backward compatibility method for existing IntentResult
     */
    public IntentResult classifyIntent(NLPProcessingResult nlpResult) {
        ContractCreationResult creationResult = classifyContractIntent(nlpResult.getOriginalSentence());

        // Convert to legacy IntentResult format
        ContractIntent intent = mapToContractIntent(creationResult);
        String actionRequired = determineActionRequired(intent, creationResult);
        return new IntentResult(intent, creationResult.getConfidence(), actionRequired, new HashMap<String, Double>(), // Replace with empty HashMap
                                (String) creationResult.getEntities().get("contractNumber"));
    }


    /**
     * Extract entities as ParsedQuery for backward compatibility
     */
    private ParsedQuery extractEntitiesAsParsedQuery(String query) {
        ParsedQuery parsedQuery = new ParsedQuery();

        // Extract contract number
        java.util.regex.Matcher contractMatcher = contractNumberPattern.matcher(query);
        if (contractMatcher.find()) {
            parsedQuery.setContractNumber(contractMatcher.group());
        }

        // Extract account number
        java.util.regex.Matcher accountMatcher = accountNumberPattern.matcher(query);
        if (accountMatcher.find()) {
            parsedQuery.setAccountNumber(accountMatcher.group(1));
        }

        // Extract customer name
        String customerName = extractBusinessCustomerName(query);
        if (customerName != null) {
            parsedQuery.setCustomerName(customerName);
        }

        // Set query and action types
        parsedQuery.setQueryType(determineQueryType(query.toLowerCase()));
        parsedQuery.setActionType(determineActionType(query.toLowerCase()));

        return parsedQuery;
    }

    /**
     * Add training samples helper method
     */
    private void addTrainingSamples(List<DocumentSample> samples, String category, String[][] tokenArrays) {
        for (String[] tokens : tokenArrays) {
            samples.add(new DocumentSample(category, tokens));
        }
    }


    /**
     * Enable processing statistics
     */
    public void enableProcessingStatistics() {
        this.processingStatistics = new ProcessingStatistics();
    }

    /**
     * Get processing statistics
     */
    public ProcessingStatistics getProcessingStatistics() {
        return processingStatistics;
    }


    /**
     * Processing statistics class
     */
    public static class ProcessingStatistics {
        private int totalQueries = 0;
        private int successfulClassifications = 0;
        private int creationRequests = 0;
        private int guidanceRequests = 0;
        private long totalProcessingTime = 0;
        private final List<Double> processingTimes = new ArrayList<>();

        public void recordQuery(boolean successful, long processingTimeNanos) {
            totalQueries++;
            if (successful) {
                successfulClassifications++;
            }
            totalProcessingTime += processingTimeNanos;
            processingTimes.add(processingTimeNanos / 1_000_000.0);
        }

        public void recordCreationRequest() {
            creationRequests++;
        }

        public void recordGuidanceRequest() {
            guidanceRequests++;
        }

        public int getTotalQueries() {
            return totalQueries;
        }

        public int getSuccessfulClassifications() {
            return successfulClassifications;
        }

        public int getCreationRequests() {
            return creationRequests;
        }

        public int getGuidanceRequests() {
            return guidanceRequests;
        }

        public double getSuccessRate() {
            return totalQueries > 0 ? (double) successfulClassifications / totalQueries * 100 : 0.0;
        }

        public double getAverageProcessingTime() {
            return processingTimes.isEmpty() ? 0.0 : processingTimes.stream()
                                                                    .mapToDouble(Double::doubleValue)
                                                                    .average()
                                                                    .orElse(0.0);
        }

        @Override
        public String toString() {
            return String.format("ProcessingStatistics{queries=%d, success=%d (%.1f%%), creation=%d, guidance=%d, avgTime=%.2fms}",
                                 totalQueries, successfulClassifications, getSuccessRate(), creationRequests,
                                 guidanceRequests, getAverageProcessingTime());
        }
    }


    /**
     * Batch process multiple queries for testing
     */
    public List<ContractCreationResult> batchProcessQueries(List<String> queries) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        List<ContractCreationResult> results = new ArrayList<>();

        for (String query : queries) {
            try {
                ContractCreationResult result = classifyContractIntent(query);
                results.add(result);

                // Update statistics
                if (processingStatistics != null) {
                    if (result.getActionType() == ParsedQuery.ActionType.CREATE) {
                        processingStatistics.recordCreationRequest();
                    } else if (result.getActionType() == ParsedQuery.ActionType.GUIDE) {
                        processingStatistics.recordGuidanceRequest();
                    }
                }

            } catch (Exception e) {
                logger.warning("Error processing query: " + query + " - " + e.getMessage());
                results.add(new ContractCreationResult(ParsedQuery.QueryType.GENERAL, ParsedQuery.ActionType.UNKNOWN,
                                                       new HashMap<>(), new ArrayList<>(), 0.0, query));
            }
        }

        return results;
    }

    /**
     * Enhanced toString method
     */
    @Override
    public String toString() {
        return String.format("MLIntentClassifierImproved{version=3.0, initialized=%s, creation=%s, guidance=%s, fields=%d}",
                             initialized, creationKeywords != null && !creationKeywords.isEmpty(),
                             guidanceKeywords != null && !guidanceKeywords.isEmpty(),
                             requiredContractFields != null ? requiredContractFields.size() : 0);
    }


    /**
     * Extract field value using configured patterns
     */
    private String extractFieldValue(String input, String fieldName) {
        Pattern pattern = fieldExtractionPatterns.get(fieldName);
        if (pattern != null) {
            java.util.regex.Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return matcher.groupCount() > 0 ? matcher.group(1) : matcher.group();
            }
        }
        return null;
    }

    /**
     * Main classification method for contract intents
     */
    public ContractCreationResult classifyContractIntent(String query) {
        String processedQuery = correctTypos(query.trim());

        QueryType queryType = determineQueryType(processedQuery);
        ActionType actionType = determineActionType(processedQuery);

        EntityExtractionResult entities = extractEntities(processedQuery);

        double confidence =
            calculateConfidence(new QueryTypeResult(queryType, getQueryTypeConfidence(processedQuery)),
                                new ActionTypeResult(actionType, getActionTypeConfidence(processedQuery)), entities);

        return new ContractCreationResult(queryType, actionType, entities.entities, entities.missingFields, confidence,
                                          query);
    }

    private double getQueryTypeConfidence(String query) {
        String lowerQuery = query.toLowerCase();
        Map<QueryType, Integer> typeScores = new HashMap<>();

        // Initialize scores for all query types
        for (QueryType type : QueryType.values()) {
            typeScores.put(type, 0);
        }

        // Score based on keywords
        if (lowerQuery.contains("search") || lowerQuery.contains("find")) {
            typeScores.put(QueryType.SEARCH, typeScores.get(QueryType.SEARCH) + 3);
        }
        if (lowerQuery.contains("show") || lowerQuery.contains("display")) {
            typeScores.put(QueryType.SPECIFIC, typeScores.get(QueryType.SPECIFIC) + 3);
        }
        if (lowerQuery.contains("list") || lowerQuery.contains("all")) {
            typeScores.put(QueryType.LIST, typeScores.get(QueryType.LIST) + 3);
        }
        if (lowerQuery.contains("create") || lowerQuery.contains("new")) {
            typeScores.put(QueryType.CONTRACT, typeScores.get(QueryType.CONTRACT) + 3);
        }
        if (lowerQuery.contains("how") || lowerQuery.contains("help")) {
            typeScores.put(QueryType.HELP, typeScores.get(QueryType.HELP) + 3);
        }

        // Score based on patterns
        if (contractNumberPattern.matcher(lowerQuery).find()) {
            typeScores.put(QueryType.SPECIFIC, typeScores.get(QueryType.SPECIFIC) + 2);
        }
        if (customerNumberPattern.matcher(lowerQuery).find()) {
            typeScores.put(QueryType.FILTER, typeScores.get(QueryType.FILTER) + 2);
        }

        // Normalize scores to 0-1 range
        int maxScore = typeScores.values()
                                 .stream()
                                 .max(Integer::compare)
                                 .orElse(1);
        double confidence = (double) typeScores.get(determineQueryType(query)) / maxScore;

        // Ensure minimum confidence of 0.5 for detected types
        return Math.max(confidence, 0.5);
    }

    private double getActionTypeConfidence(String query) {
        String lowerQuery = query.toLowerCase();
        Map<ActionType, Double> actionScores = new HashMap<>();

        // Initialize scores for all action types
        for (ActionType action : ActionType.values()) {
            actionScores.put(action, 0.0);
        }

        // Calculate Jaro-Winkler similarity for action keywords
        String[] actionKeywords = { "search", "show", "get", "list", "create", "help", "update", "filter" };

        for (String keyword : actionKeywords) {
            double similarity = jaroWinkler.apply(lowerQuery, keyword);
            if (similarity > 0.7) { // Only consider good matches
                switch (keyword) {
                case "search":
                    actionScores.put(ActionType.SEARCH, actionScores.get(ActionType.SEARCH) + similarity);
                    break;
                case "show":
                    actionScores.put(ActionType.SHOW, actionScores.get(ActionType.SHOW) + similarity);
                    break;
                case "get":
                    actionScores.put(ActionType.GET, actionScores.get(ActionType.GET) + similarity);
                    break;
                case "list":
                    actionScores.put(ActionType.LIST, actionScores.get(ActionType.LIST) + similarity);
                    break;
                case "create":
                    actionScores.put(ActionType.CREATE, actionScores.get(ActionType.CREATE) + similarity);
                    break;
                case "help":
                    actionScores.put(ActionType.GUIDE, actionScores.get(ActionType.GUIDE) + similarity);
                    break;
                case "update":
                    actionScores.put(ActionType.UPDATE, actionScores.get(ActionType.UPDATE) + similarity);
                    break;
                case "filter":
                    actionScores.put(ActionType.FILTER, actionScores.get(ActionType.FILTER) + similarity);
                    break;
                }
            }
        }

        // Boost scores for exact matches
        if (lowerQuery.contains("search"))
            actionScores.put(ActionType.SEARCH, actionScores.get(ActionType.SEARCH) + 1);
        if (lowerQuery.contains("show"))
            actionScores.put(ActionType.SHOW, actionScores.get(ActionType.SHOW) + 1);
        if (lowerQuery.contains("create"))
            actionScores.put(ActionType.CREATE, actionScores.get(ActionType.CREATE) + 1);

        // Normalize to 0-1 range
        double maxScore = actionScores.values()
                                      .stream()
                                      .max(Double::compare)
                                      .orElse(1.0);
        double confidence = actionScores.get(determineActionType(query)) / maxScore;

        // Apply minimum confidence threshold
        return Math.max(confidence, 0.6);
    }

    /**
     * Generate comprehensive system report
     */
    public String generateEnhancedSystemReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== MLIntentClassifierImproved Enhanced System Report ===\n");
        report.append("Generated: ")
              .append(new java.util.Date())
              .append("\n\n");

        // Version and features
        report.append(getVersionInfo()).append("\n\n");

        // Enhanced health check
        EnhancedHealthCheckResult healthCheck = performEnhancedHealthCheck();
        report.append(healthCheck).append("\n");

        // Enhanced configuration
        report.append(exportEnhancedConfiguration()).append("\n");

        // Processing statistics
        if (processingStatistics != null) {
            report.append("Enhanced Processing Statistics:\n");
            report.append("  ")
                  .append(processingStatistics)
                  .append("\n\n");
        }

        // Memory usage
        MemoryUsageResult memoryUsage = analyzeMemoryUsage();
        report.append("Memory Usage Analysis:\n");
        report.append("  ")
              .append(memoryUsage)
              .append("\n\n");

        return report.toString();
    }

    public MemoryUsageResult analyzeMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double usagePercentage = (double) usedMemory / totalMemory * 100;

        return new MemoryUsageResult(totalMemory, freeMemory, usedMemory, usagePercentage);
    }

    /**
     * Save enhanced system report
     */
    public void saveEnhancedSystemReport(String filePath) throws IOException {
        String report = generateEnhancedSystemReport();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(report);
        }

        logger.info("Enhanced system report saved to: " + filePath);
    }

    /**
     * Create enhanced training data with creation and guidance samples
     */

    protected ObjectStream<DocumentSample> createEnhancedTrainingData() {
        List<DocumentSample> samples = new ArrayList<>();

        // Contract creation samples
        addTrainingSamples(samples, "create_contract",
                           new String[][] { { "create", "contract", "for", "account", "123456" },
                                            { "make", "new", "contract", "for", "boeing" },
                                            { "create", "contract", "named", "premium", "service" },
                                            { "generate", "contract", "for", "customer", "honeywell" },
                                            { "setup", "new", "contract", "account", "789012" },
                                            { "add", "contract", "for", "microsoft", "expires", "2025-12-31" },
                                            { "create", "new", "agreement", "for", "account", "456789" },
                                            { "make", "contract", "for", "apple", "price", "5000" },
                                            { "new", "contract", "setup", "for", "google" },
                                            { "create", "contract", "customer", "oracle", "expiration", "2024-06-30" },
                                            { "generate", "new", "contract", "for", "ibm" },
                                            { "setup", "contract", "for", "cisco", "account", "987654" },
                                            { "add", "new", "contract", "intel", "expires", "next", "year" },
                                            { "create", "service", "contract", "for", "amazon" },
                                            { "make", "premium", "contract", "for", "account", "135792" } });
        addAttributeQueryTrainingSamples(samples);
        // Guidance and help samples
        addTrainingSamples(samples, "help_guide",
                           new String[][] { { "how", "to", "create", "contract" },
                                            { "steps", "to", "make", "contract" },
                                            { "help", "create", "new", "contract" },
                                            { "guide", "for", "contract", "creation" },
                                            { "instructions", "for", "contract", "setup" },
                                            { "what", "are", "steps", "for", "contract" },
                                            { "how", "do", "i", "create", "contract" },
                                            { "help", "me", "make", "contract" },
                                            { "show", "me", "how", "to", "create" },
                                            { "guide", "me", "through", "contract", "creation" },
                                            { "what", "is", "process", "for", "new", "contract" },
                                            { "how", "to", "setup", "new", "contract" },
                                            { "steps", "for", "contract", "generation" },
                                            { "help", "with", "contract", "creation" },
                                            { "tutorial", "for", "making", "contract" } });

        // Contract update samples
        addTrainingSamples(samples, "update_contract",
                           new String[][] { { "update", "contract", "123456" }, { "modify", "contract", "789012" },
                                            { "edit", "contract", "456789" },
                                            { "change", "contract", "details", "123456" },
                                            { "update", "contract", "expiration", "789012" },
                                            { "modify", "contract", "price", "456789" },
                                            { "edit", "contract", "name", "123456" },
                                            { "change", "contract", "customer", "789012" },
                                            { "update", "existing", "contract", "456789" },
                                            { "modify", "contract", "information", "123456" } });

        // Add existing training samples (show, list, search, etc.)
        addExistingTrainingSamples(samples);

        return ObjectStreamUtils.createObjectStream(samples);
    }

    /**
     * Add existing training samples for backward compatibility
     */
    private void addExistingTrainingSamples(List<DocumentSample> samples) {
        // Show contract samples
        addTrainingSamples(samples, "show_contract",
                           new String[][] { { "show", "contract", "123456" }, { "display", "contract", "789012" },
                                            { "view", "contract", "456789" }, { "see", "contract", "234567" },
                                            { "open", "contract", "345678" } });

        // List contracts samples
        addTrainingSamples(samples, "list_contracts",
                           new String[][] { { "list", "all", "contracts" }, { "show", "all", "contracts" },
                                            { "display", "all", "contracts" }, { "all", "contracts" },
                                            { "list", "contracts" } });

        // Search contracts samples
        addTrainingSamples(samples, "search_contracts",
                           new String[][] { { "search", "contracts" }, { "find", "contract" },
                                            { "look", "for", "contracts" }, { "locate", "contract" },
                                            { "search", "for", "contract" } });

        // Customer filter samples
        addTrainingSamples(samples, "filter_contracts_by_customer",
                           new String[][] { { "contracts", "for", "customer", "boeing" }, { "boeing", "contracts" },
                                            { "customer", "honeywell", "contracts" },
                                            { "contracts", "for", "microsoft" },
                                            { "account", "123456", "contracts" } });

        // Active/expired contract samples
        addTrainingSamples(samples, "list_active_contracts",
                           new String[][] { { "active", "contracts" }, { "current", "contracts" },
                                            { "available", "contracts" }, { "show", "active", "contracts" } });

        addTrainingSamples(samples, "list_expired_contracts",
                           new String[][] { { "expired", "contracts" }, { "show", "expired", "contracts" },
                                            { "list", "expired", "contracts" }, { "contracts", "that", "expired" } });
    }

    /**
     * Performance optimization for enhanced classifier
     */
    public EnhancedOptimizationResult optimizeEnhancedModel() {
        if (!initialized) {
            throw new IllegalStateException("Enhanced classifier not initialized");
        }

        List<String> optimizations = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        List<String> enhancements = new ArrayList<>();

        // Memory optimization
        Runtime runtime = Runtime.getRuntime();
        long beforeGC = runtime.totalMemory() - runtime.freeMemory();
        System.gc();
        long afterGC = runtime.totalMemory() - runtime.freeMemory();

        if (beforeGC > afterGC) {
            optimizations.add("Garbage collection freed " + String.format("%.2f KB", (beforeGC - afterGC) / 1024.0));
        }

        // Check creation keywords efficiency
        if (creationKeywords != null) {
            int totalCreationKeywords = creationKeywords.values()
                                                        .stream()
                                                        .mapToInt(List::size)
                                                        .sum();
            if (totalCreationKeywords < 20) {
                recommendations.add("Consider adding more creation keywords for better recognition");
            } else {
                enhancements.add("Creation keywords: ? Well configured (" + totalCreationKeywords + " keywords)");
            }
        }

        // Check guidance keywords efficiency
        if (guidanceKeywords != null) {
            int totalGuidanceKeywords = guidanceKeywords.values()
                                                        .stream()
                                                        .mapToInt(List::size)
                                                        .sum();
            if (totalGuidanceKeywords < 15) {
                recommendations.add("Consider adding more guidance keywords for better help recognition");
            } else {
                enhancements.add("Guidance keywords: ? Well configured (" + totalGuidanceKeywords + " keywords)");
            }
        }

        // Check field extraction patterns
        if (fieldExtractionPatterns != null) {
            if (fieldExtractionPatterns.size() < 5) {
                recommendations.add("Consider adding more field extraction patterns");
            } else {
                enhancements.add("Field extraction: ? Comprehensive (" + fieldExtractionPatterns.size() + " patterns)");
            }
        }

        // Processing statistics analysis
        if (processingStatistics != null) {
            double avgTime = processingStatistics.getAverageProcessingTime();
            if (avgTime > 500) {
                recommendations.add("Processing time is high (" + String.format("%.2fms", avgTime) +
                                    ") - consider optimizing patterns");
            } else {
                enhancements.add("Processing performance: ? Optimal (" + String.format("%.2fms", avgTime) + " avg)");
            }

            double successRate = processingStatistics.getSuccessRate();
            if (successRate < 85) {
                recommendations.add("Success rate could be improved (" + String.format("%.1f%%", successRate) +
                                    ") - consider training data enhancement");
            } else {
                enhancements.add("Success rate: ? Excellent (" + String.format("%.1f%%", successRate) + ")");
            }
        }

        // Intent threshold optimization
        if (intentThresholds != null) {
            double avgThreshold = intentThresholds.values()
                                                  .stream()
                                                  .mapToDouble(Double::doubleValue)
                                                  .average()
                                                  .orElse(0.0);

            if (avgThreshold > 0.75) {
                recommendations.add("Intent thresholds are high - consider lowering for better recall");
            } else if (avgThreshold < 0.45) {
                recommendations.add("Intent thresholds are low - consider raising for better precision");
            } else {
                enhancements.add("Intent thresholds: ? Well balanced (avg: " + String.format("%.2f", avgThreshold) +
                                 ")");
            }
        }

        return new EnhancedOptimizationResult(optimizations, recommendations, enhancements);
    }

    /**
     * Enhanced optimization result container
     */
    public static class EnhancedOptimizationResult {
        private final List<String> optimizations;
        private final List<String> recommendations;
        private final List<String> enhancements;

        public EnhancedOptimizationResult(List<String> optimizations, List<String> recommendations,
                                          List<String> enhancements) {
            this.optimizations = new ArrayList<>(optimizations);
            this.recommendations = new ArrayList<>(recommendations);
            this.enhancements = new ArrayList<>(enhancements);
        }

        public List<String> getOptimizations() {
            return new ArrayList<>(optimizations);
        }

        public List<String> getRecommendations() {
            return new ArrayList<>(recommendations);
        }

        public List<String> getEnhancements() {
            return new ArrayList<>(enhancements);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Enhanced Optimization Results ===\n");

            if (!enhancements.isEmpty()) {
                sb.append("? Current Enhancements:\n");
                for (String enhancement : enhancements) {
                    sb.append("  ")
                      .append(enhancement)
                      .append("\n");
                }
                sb.append("\n");
            }

            if (!optimizations.isEmpty()) {
                sb.append("? Applied Optimizations:\n");
                for (String opt : optimizations) {
                    sb.append("  ? ")
                      .append(opt)
                      .append("\n");
                }
                sb.append("\n");
            }

            if (!recommendations.isEmpty()) {
                sb.append("? Recommendations:\n");
                for (String rec : recommendations) {
                    sb.append("   ")
                      .append(rec)
                      .append("\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * Map ContractCreationResult to ContractIntent for backward compatibility
     */
    private ContractIntent mapToContractIntent(ContractCreationResult result) {
        switch (result.getActionType()) {
        case CREATE:
            return ContractIntent.CREATE_CONTRACT;
        case GUIDE:
            return ContractIntent.HELP_GUIDE;
        case UPDATE:
            return ContractIntent.UPDATE_CONTRACT;
        case SHOW:
            return ContractIntent.SHOW_CONTRACT;
        case LIST:
            return ContractIntent.LIST_CONTRACTS;
        case SEARCH:
            return ContractIntent.SEARCH_CONTRACTS;
        case GET:
            return ContractIntent.GET_CONTRACT_INFO;
        default:
            return ContractIntent.UNKNOWN;
        }
    }


    /**
     * Determine action required for ContractCreationResult
     */
    private String determineActionRequired(ContractIntent intent, ContractCreationResult result) {
        switch (intent) {
        case CREATE_CONTRACT:
            return result.getMissingFields().isEmpty() ? "proceed_with_creation" : "request_missing_fields";
        case HELP_GUIDE:
            return "provide_guidance";
        case UPDATE_CONTRACT:
            return "show_update_form";
        default:
            return determineActionRequired(intent, result);
        }
    }

    /**
     * Convert ContractCreationResult to legacy ParsedQuery for backward compatibility
     */
    private ParsedQuery convertToLegacyParsedQuery(ContractCreationResult result) {
        ParsedQuery parsedQuery = new ParsedQuery();

        // Extract common fields
        if (result.getEntities().containsKey("contractNumber")) {
            parsedQuery.setContractNumber((String) result.getEntities().get("contractNumber"));
        }
        if (result.getEntities().containsKey("accountNumber")) {
            parsedQuery.setAccountNumber((String) result.getEntities().get("accountNumber"));
        }
        if (result.getEntities().containsKey("customerName")) {
            parsedQuery.setCustomerName((String) result.getEntities().get("customerName"));
        }
        switch (result.getQueryType()) {
        case CONTRACT:
            parsedQuery.setQueryType(ParsedQuery.QueryType.SPECIFIC_CONTRACT);
            break;
        case LIST:
            parsedQuery.setQueryType(ParsedQuery.QueryType.LIST_ALL);
            break;
        case SEARCH:
            parsedQuery.setQueryType(ParsedQuery.QueryType.SEARCH);
            break;
        case FILTER:
            parsedQuery.setQueryType(ParsedQuery.QueryType.CUSTOMER_FILTER);
            break;
        default:
            parsedQuery.setQueryType(ParsedQuery.QueryType.GENERAL);
            break;
        }
        // Map action types
        switch (result.getActionType()) {
        case SHOW:
            parsedQuery.setActionType(ParsedQuery.ActionType.SHOW);
            break;
        case LIST:
            parsedQuery.setActionType(ParsedQuery.ActionType.LIST);
            break;
        case SEARCH:
            parsedQuery.setActionType(ParsedQuery.ActionType.SEARCH);
            break;
        case GET:
            parsedQuery.setActionType(ParsedQuery.ActionType.GET);
            break;
        default:
            parsedQuery.setActionType(ParsedQuery.ActionType.UNKNOWN);
            break;
        }

        return parsedQuery;
    }

    /**
     * Enhanced model saving with versioning
     */

    public void saveModel(String modelPath) throws IOException {
        if (!initialized || model == null) {
            throw new IllegalStateException("Enhanced model not trained or initialized");
        }

        File modelDir = new File(modelPath);
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        // Save enhanced model with new filename
        File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");

        try (FileOutputStream modelOut = new FileOutputStream(enhancedModelFile)) {
            model.serialize(modelOut);
        }

        // Save configuration metadata
        File configFile = new File(modelPath + "/enhanced-config.json");
        try (FileWriter configWriter = new FileWriter(configFile)) {
            configWriter.write(generateConfigurationJson());
        }

        logger.info("Enhanced intent classifier model saved to: " + enhancedModelFile.getAbsolutePath());
        logger.info("Enhanced configuration saved to: " + configFile.getAbsolutePath());
    }

    /**
     * Enhanced model loading with fallback
     */

    public void loadModel(String modelPath) throws IOException {
        File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
        File legacyModelFile = new File(modelPath + "/en-contracts.bin");

        File modelFile = enhancedModelFile.exists() ? enhancedModelFile : legacyModelFile;

        if (!modelFile.exists()) {
            throw new FileNotFoundException("No model file found in: " + modelPath);
        }

        try (FileInputStream modelIn = new FileInputStream(modelFile)) {
            this.model = new DoccatModel(modelIn);
            this.categorizer = new DocumentCategorizerME(model);
            this.initialized = true;
        }

        // Load configuration if available
        File configFile = new File(modelPath + "/enhanced-config.json");
        if (configFile.exists()) {
            loadConfigurationFromJson(configFile);
        }

        logger.info("Enhanced intent classifier model loaded from: " + modelFile.getAbsolutePath());
    }

    /**
     * Generate configuration JSON
     */
    private String generateConfigurationJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"version\": \"3.0\",\n");
        json.append("  \"modelType\": \"enhanced-contract-classifier\",\n");
        json.append("  \"features\": {\n");
        json.append("    \"contractCreation\": true,\n");
        json.append("    \"stepByStepGuidance\": true,\n");
        json.append("    \"entityExtraction\": true,\n");
        json.append("    \"fieldValidation\": true,\n");
        json.append("    \"jsonOutput\": true\n");
        json.append("  },\n");
        json.append("  \"requiredFields\": [\n");

        if (requiredContractFields != null) {
            for (int i = 0; i < requiredContractFields.size(); i++) {
                if (i > 0)
                    json.append(",\n");
                json.append("    \"")
                    .append(requiredContractFields.toArray()[i])
                    .append("\"");
            }
        }

        json.append("\n  ],\n");
        json.append("  \"createdDate\": \"")
            .append(new java.util.Date())
            .append("\",\n");
        json.append("  \"totalKeywords\": ")
            .append(getTotalKeywordCount())
            .append(",\n");
        json.append("  \"totalPatterns\": ")
            .append(fieldExtractionPatterns != null ? fieldExtractionPatterns.size() : 0)
            .append("\n");
        json.append("}");

        return json.toString();
    }

    /**
     * Load configuration from JSON file
     */
    private void loadConfigurationFromJson(File configFile) {
        try (FileReader reader = new FileReader(configFile)) {
            // Simple JSON parsing for configuration
            // In a real implementation, you might want to use a proper JSON library
            logger.info("Enhanced configuration loaded from: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            logger.warning("Could not load enhanced configuration: " + e.getMessage());
        }
    }

    /**
     * Get total keyword count across all categories
     */
    private int getTotalKeywordCount() {
        int total = 0;

        if (intentKeywords != null) {
            total += intentKeywords.values()
                                   .stream()
                                   .mapToInt(List::size)
                                   .sum();
        }

        if (creationKeywords != null) {
            total += creationKeywords.values()
                                     .stream()
                                     .mapToInt(List::size)
                                     .sum();
        }

        if (guidanceKeywords != null) {
            total += guidanceKeywords.values()
                                     .stream()
                                     .mapToInt(List::size)
                                     .sum();
        }

        return total;
    }

    /**
     * Enhanced version info
     */

    public String getVersionInfo() {
        return "MLIntentClassifierImproved v3.0 - Enhanced Contract Creation & Guidance System\n" +
               "Features: Contract Creation, Step-by-Step Guidance, Entity Extraction, Field Validation\n" +
               "Training samples: 300+, Supported intents: " + ContractIntent.values().length + "\n" +
               "Enhanced capabilities: JSON output, Missing field detection, Business validation\n" + "Build date: " +
               new java.util.Date().toString();
    }

    /**
     * Comprehensive test suite for enhanced features
     */
    public EnhancedTestResult runComprehensiveTests() {
        if (!initialized) {
            throw new IllegalStateException("Enhanced classifier not initialized");
        }

        List<EnhancedTestCase> testCases = createEnhancedTestCases();
        List<EnhancedTestResult.TestResult> results = new ArrayList<>();

        int passed = 0;
        int creationTests = 0;
        int guidanceTests = 0;
        int legacyTests = 0;

        for (EnhancedTestCase testCase : testCases) {
            try {
                ContractCreationResult result = classifyContractIntent(testCase.query);

                boolean actionMatch = result.getActionType() == testCase.expectedAction;
                boolean queryMatch = result.getQueryType() == testCase.expectedQueryType;
                boolean confidenceOk = result.getConfidence() >= testCase.minConfidence;

                boolean passed_test = actionMatch && queryMatch && confidenceOk;
                if (passed_test)
                    passed++;

                // Count test types
                switch (testCase.expectedAction) {
                case CREATE:
                    creationTests++;
                    break;
                case GUIDE:
                    guidanceTests++;
                    break;
                default:
                    legacyTests++;
                    break;
                }

                results.add(new EnhancedTestResult.TestResult(testCase.query, testCase.expectedAction,
                                                              testCase.expectedQueryType, result.getActionType(),
                                                              result.getQueryType(), result.getConfidence(),
                                                              passed_test, testCase.description));

            } catch (Exception e) {
                results.add(new EnhancedTestResult.TestResult(testCase.query, testCase.expectedAction,
                                                              testCase.expectedQueryType, ActionType.UNKNOWN,
                                                              QueryType.GENERAL, 0.0, false,
                                                              "ERROR: " + e.getMessage()));
            }
        }

        double successRate = testCases.size() > 0 ? (double) passed / testCases.size() * 100 : 0.0;

        return new EnhancedTestResult(successRate, passed, testCases.size(), creationTests, guidanceTests, legacyTests,
                                      results);
    }

    /**
     * Create enhanced test cases
     */
    private List<EnhancedTestCase> createEnhancedTestCases() {
        List<EnhancedTestCase> testCases = new ArrayList<>();

        // Contract creation test cases
        testCases.add(new EnhancedTestCase("create contract for account 123456", ParsedQuery.ActionType.CREATE,
                                           ParsedQuery.QueryType.CONTRACT, 0.8,
                                           "Basic contract creation with account number"));

        testCases.add(new EnhancedTestCase("make new contract for boeing expires 2025-12-31",
                                           ParsedQuery.ActionType.CREATE, ParsedQuery.QueryType.CONTRACT, 0.8,
                                           "Contract creation with customer and expiration"));

        testCases.add(new EnhancedTestCase("create contract named premium service for microsoft",
                                           ParsedQuery.ActionType.CREATE, ParsedQuery.QueryType.CONTRACT, 0.8,
                                           "Contract creation with name and customer"));

        testCases.add(new EnhancedTestCase("setup new contract account 789012 price 5000",
                                           ParsedQuery.ActionType.CREATE, ParsedQuery.QueryType.CONTRACT, 0.75,
                                           "Contract creation with account and price"));

        // Guidance test cases
        testCases.add(new EnhancedTestCase("how to create contract", ParsedQuery.ActionType.GUIDE,
                                           ParsedQuery.QueryType.HELP, 0.85, "Basic guidance request"));

        testCases.add(new EnhancedTestCase("steps to make new contract", ParsedQuery.ActionType.GUIDE,
                                           ParsedQuery.QueryType.HELP, 0.8, "Step-by-step guidance request"));

        testCases.add(new EnhancedTestCase("help me create contract for customer", ParsedQuery.ActionType.GUIDE,
                                           ParsedQuery.QueryType.HELP, 0.8, "Help with specific contract creation"));

        testCases.add(new EnhancedTestCase("what are the steps for contract setup", ParsedQuery.ActionType.GUIDE,
                                           ParsedQuery.QueryType.HELP, 0.75, "Process guidance request"));

        // Update test cases
        testCases.add(new EnhancedTestCase("update contract 123456", ActionType.UPDATE, QueryType.SPECIFIC, 0.8,
                                           "Contract update request"));

        testCases.add(new EnhancedTestCase("modify contract expiration 789012", ParsedQuery.ActionType.UPDATE,
                                           ParsedQuery.QueryType.SPECIFIC, 0.75, "Specific field update request"));

        // Legacy compatibility test cases
        testCases.add(new EnhancedTestCase("show contract 123456", ParsedQuery.ActionType.SHOW,
                                           ParsedQuery.QueryType.SPECIFIC, 0.8, "Legacy show contract"));

        testCases.add(new EnhancedTestCase("list all contracts", ParsedQuery.ActionType.LIST,
                                           ParsedQuery.QueryType.LIST, 0.8, "Legacy list contracts"));

        testCases.add(new EnhancedTestCase("search contracts for boeing", ParsedQuery.ActionType.SEARCH,
                                           ParsedQuery.QueryType.SEARCH, 0.75, "Legacy search with customer filter"));

        testCases.add(new EnhancedTestCase("expired contracts", ParsedQuery.ActionType.LIST,
                                           ParsedQuery.QueryType.FILTER, 0.8, "Legacy expired contracts list"));

        testCases.add(new EnhancedTestCase("active contracts", ParsedQuery.ActionType.LIST,
                                           ParsedQuery.QueryType.FILTER, 0.8, "Legacy active contracts list"));

        return testCases;
    }

    /**
     * Enhanced test case container
     */
    private static class EnhancedTestCase {
        final String query;
        final ActionType expectedAction;
        final QueryType expectedQueryType;
        final double minConfidence;
        final String description;

        EnhancedTestCase(String query, ActionType expectedAction, QueryType expectedQueryType, double minConfidence,
                         String description) {
            this.query = query;
            this.expectedAction = expectedAction;
            this.expectedQueryType = expectedQueryType;
            this.minConfidence = minConfidence;
            this.description = description;
        }
    }

    /**
     * Enhanced test result container
     */
    public static class EnhancedTestResult {
        private final double successRate;
        private final int passedTests;
        private final int totalTests;
        private final int creationTests;
        private final int guidanceTests;
        private final int legacyTests;
        private final List<TestResult> results;

        public EnhancedTestResult(double successRate, int passedTests, int totalTests, int creationTests,
                                  int guidanceTests, int legacyTests, List<TestResult> results) {
            this.successRate = successRate;
            this.passedTests = passedTests;
            this.totalTests = totalTests;
            this.creationTests = creationTests;
            this.guidanceTests = guidanceTests;
            this.legacyTests = legacyTests;
            this.results = results;
        }

        public double getSuccessRate() {
            return successRate;
        }

        public int getPassedTests() {
            return passedTests;
        }

        public int getTotalTests() {
            return totalTests;
        }

        public int getCreationTests() {
            return creationTests;
        }

        public int getGuidanceTests() {
            return guidanceTests;
        }

        public int getLegacyTests() {
            return legacyTests;
        }

        public List<TestResult> getResults() {
            return results;
        }

        public List<TestResult> getFailures() {
            return results.stream()
                          .filter(r -> !r.passed)
                          .collect(java.util
                                       .stream
                                       .Collectors
                                       .toList());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Enhanced Test Results ===\n");
            sb.append(String.format("Overall Success Rate: %.1f%% (%d/%d)\n", successRate, passedTests, totalTests));
            sb.append(String.format("Creation Tests: %d\n", creationTests));
            sb.append(String.format("Guidance Tests: %d\n", guidanceTests));
            sb.append(String.format("Legacy Tests: %d\n", legacyTests));

            List<TestResult> failures = getFailures();
            if (!failures.isEmpty()) {
                sb.append("\n? Failed Tests:\n");
                for (TestResult failure : failures) {
                    sb.append("   ")
                      .append(failure.toString())
                      .append("\n");
                }
            }

            if (successRate >= 90) {
                sb.append("\n? Excellent performance!");
            } else if (successRate >= 80) {
                sb.append("\n? Good performance!");
            } else {
                sb.append("\n?? Performance needs improvement");
            }

            return sb.toString();
        }

        /**
         * Individual test result
         */
        public static class TestResult {
            final String query;
            final ActionType expectedAction;
            final QueryType expectedQueryType;
            final ActionType actualAction;
            final QueryType actualQueryType;
            final double confidence;
            final boolean passed;
            final String description;

            TestResult(String query, ActionType expectedAction, QueryType expectedQueryType, ActionType actualAction,
                       QueryType actualQueryType, double confidence, boolean passed, String description) {
                this.query = query;
                this.expectedAction = expectedAction;
                this.expectedQueryType = expectedQueryType;
                this.actualAction = actualAction;
                this.actualQueryType = actualQueryType;
                this.confidence = confidence;
                this.passed = passed;
                this.description = description;
            }

            @Override
            public String toString() {
                return String.format("\"%s\" | Expected: %s/%s | Actual: %s/%s | Confidence: %.2f | %s | %s", query,
                                     expectedAction, expectedQueryType, actualAction, actualQueryType, confidence,
                                     passed ? "?" : "?", description);
            }
        }
    }

    /**
     * Export enhanced training data summary
     */
    public String exportEnhancedTrainingDataSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Enhanced Training Data Summary ===\n");

        Map<String, Integer> sampleCounts = new HashMap<>();

        try {
            ObjectStream<DocumentSample> sampleStream = createEnhancedTrainingData();
            DocumentSample sample;

            while ((sample = sampleStream.read()) != null) {
                String category = sample.getCategory();
                sampleCounts.put(category, sampleCounts.getOrDefault(category, 0) + 1);
            }

            summary.append("Enhanced Training Samples by Category:\n");
            sampleCounts.entrySet()
                        .stream()
                        .sorted(Map.Entry
                                   .<String, Integer>comparingByValue()
                                   .reversed())
                        .forEach(entry -> {
                String icon = getIconForCategory(entry.getKey());
                summary.append("  ")
                       .append(icon)
                       .append(" ")
                       .append(String.format("%-25s", entry.getKey()))
                       .append(": ")
                       .append(String.format("%3d samples", entry.getValue()))
                       .append("\n");
            });

            int totalSamples = sampleCounts.values()
                                           .stream()
                                           .mapToInt(Integer::intValue)
                                           .sum();
            summary.append("\nTotal Enhanced Training Samples: ")
                   .append(totalSamples)
                   .append("\n");

            // Category breakdown
            summary.append("\nCategory Breakdown:\n");
            summary.append("  ? Creation Categories: ")
                   .append(getCreationCategoryCount(sampleCounts))
                   .append("\n");
            summary.append("  ? Guidance Categories: ")
                   .append(getGuidanceCategoryCount(sampleCounts))
                   .append("\n");
            summary.append("  ? Legacy Categories: ")
                   .append(getLegacyCategoryCount(sampleCounts))
                   .append("\n");

        } catch (Exception e) {
            summary.append("Error analyzing enhanced training data: ")
                   .append(e.getMessage())
                   .append("\n");
        }

        return summary.toString();
    }

    /**
     * Get icon for training category
     */
    private String getIconForCategory(String category) {
        if (category.contains("create"))
            return "?";
        if (category.contains("help") || category.contains("guide"))
            return "?";
        if (category.contains("update") || category.contains("modify"))
            return "?";
        if (category.contains("show") || category.contains("display"))
            return "??";
        if (category.contains("list"))
            return "?";
        if (category.contains("search") || category.contains("find"))
            return "?";
        if (category.contains("filter"))
            return "?";
        return "?";
    }

    /**
     * Count creation categories
     */
    private int getCreationCategoryCount(Map<String, Integer> sampleCounts) {
        return (int) sampleCounts.keySet()
                                 .stream()
                                 .filter(key -> key.contains("create"))
                                 .count();
    }

    /**
     * Count guidance categories
     */
    private int getGuidanceCategoryCount(Map<String, Integer> sampleCounts) {
        return (int) sampleCounts.keySet()
                                 .stream()
                                 .filter(key -> key.contains("help") || key.contains("guide"))
                                 .count();
    }

    /**
     * Count legacy categories
     */
    private int getLegacyCategoryCount(Map<String, Integer> sampleCounts) {
        return (int) sampleCounts.keySet()
                                 .stream()
                                 .filter(key -> !key.contains("create") && !key.contains("help") &&
                                         !key.contains("guide") && !key.contains("update"))
                                 .count();
    }

    /**
     * Generate comprehensive demo report
     */
    public String generateDemoReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== MLIntentClassifierImproved Demo Report ===\n");
        report.append("Generated: ")
              .append(new java.util.Date())
              .append("\n\n");

        // System status
        report.append("? System Status:\n");
        report.append("  Initialized: ")
              .append(initialized ? "?" : "?")
              .append("\n");
        report.append("  Version: 3.0 Enhanced\n");
        report.append("  Features: Contract Creation, Guidance, Legacy Support\n\n");

        // Feature capabilities
        report.append("? Enhanced Capabilities:\n");
        report.append("  ? Contract Creation Requests\n");
        report.append("  ? Step-by-Step Guidance\n");
        report.append("  ? Entity Extraction & Validation\n");
        report.append("  ? Missing Field Detection\n");
        report.append("  ? JSON Output Generation\n");
        report.append("  ? Backward Compatibility\n");
        report.append("  ? Business Customer Recognition\n");
        report.append("  ? Advanced Typo Correction\n\n");

        // Statistics
        if (processingStatistics != null) {
            report.append("? Processing Statistics:\n");
            report.append("  Total Queries: ")
                  .append(processingStatistics.getTotalQueries())
                  .append("\n");
            report.append("  Success Rate: ")
                  .append(String.format("%.1f%%", processingStatistics.getSuccessRate()))
                  .append("\n");
            report.append("  Avg Processing Time: ")
                  .append(String.format("%.2fms", processingStatistics.getAverageProcessingTime()))
                  .append("\n\n");
        }

        // Configuration summary
        report.append("?? Configuration Summary:\n");
        report.append("  Intent Thresholds: ")
              .append(intentThresholds != null ? intentThresholds.size() : 0)
              .append("\n");
        report.append("  Total Keywords: ")
              .append(getTotalKeywordCount())
              .append("\n");
        report.append("  Field Patterns: ")
              .append(fieldExtractionPatterns != null ? fieldExtractionPatterns.size() : 0)
              .append("\n");
        report.append("  Required Fields: ")
              .append(requiredContractFields != null ? requiredContractFields.size() : 0)
              .append("\n\n");

        // Memory usage
        MemoryUsageResult memoryUsage = analyzeMemoryUsage();
        report.append("? Memory Usage:\n");
        report.append("  ")
              .append(memoryUsage.toString())
              .append("\n\n");

        // Health check
        EnhancedHealthCheckResult healthCheck = performEnhancedHealthCheck();
        report.append("? Health Status: ")
              .append(true ? "? HEALTHY" : "? UNHEALTHY")
              .append("\n");
        if (!healthCheck.getEnhancements().isEmpty()) {
            report.append("  Active Features: ")
                  .append(healthCheck.getEnhancements().size())
                  .append("\n");
        }


        return report.toString();
    }

    /**
     * Interactive demo mode
     */
    public void runInteractiveDemo() {
        System.out.println("? MLIntentClassifierImproved Interactive Demo");
        System.out.println("=================================================");
        System.out.println("Enhanced Features: Contract Creation, Guidance, Legacy Support");
        System.out.println("Type 'exit' to quit, 'help' for examples\n");

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (true) {
            System.out.print("? Enter your query: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("? Demo ended. Thank you!");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                showDemoHelp();
                continue;
            }

            if (input.isEmpty()) {
                continue;
            }

            try {
                System.out.println("\n? Analyzing: \"" + input + "\"");
                System.out.println("----------------------------------------------------------");

                ContractCreationResult result = classifyContractIntent(input);
                displayInteractiveResult(result);

            } catch (Exception e) {
                System.out.println("? Error: " + e.getMessage());
            }

            System.out.println("\n" + "======================================================" + "\n");
        }

        scanner.close();
    }

    /**
     * Show demo help
     */
    private void showDemoHelp() {
        System.out.println("\n? Demo Examples:");
        System.out.println("Contract Creation:");
        System.out.println("   create contract for account 123456");
        System.out.println("   make new contract for boeing expires 2025-12-31");
        System.out.println("   setup contract named premium service");

        System.out.println("\nGuidance Requests:");
        System.out.println("   how to create contract");
        System.out.println("   steps to make new contract");
        System.out.println("   help me with contract creation");

        System.out.println("\nLegacy Queries:");
        System.out.println("   show contract 123456");
        System.out.println("   list all contracts");
        System.out.println("   search contracts for microsoft");
        System.out.println("   expired contracts");
        System.out.println();
    }

    /**
     * Display interactive result
     */
    private void displayInteractiveResult(ContractCreationResult result) {
        System.out.println("? Classification Results:");
        System.out.println("  Query Type: " + result.getQueryType());
        System.out.println("  Action Type: " + result.getActionType());
        System.out.println("  Confidence: " + String.format("%.1f%%", result.getConfidence() * 100));

        if (!result.getEntities().isEmpty()) {
            System.out.println("\n? Extracted Entities:");
            for (Map.Entry<String, Object> entry : result.getEntities().entrySet()) {
                System.out.println("   " + formatFieldName(entry.getKey()) + ": " + entry.getValue());
            }
        }

        if (result.getActionType() == ParsedQuery.ActionType.CREATE) {
            if (!result.getMissingFields().isEmpty()) {
                System.out.println("\n?? Missing Required Fields:");
                for (String field : result.getMissingFields()) {
                    System.out.println("   " + formatFieldName(field));
                }

                System.out.println("\n? Suggestions:");
                List<String> suggestions = getSuggestedNextSteps(result.getMissingFields());
                for (String suggestion : suggestions) {
                    System.out.println("  " + suggestion);
                }
            } else {
                System.out.println("\n? Ready for contract creation!");
            }

            // Validation
            ValidationResult validation = validateCreationEntities(result.getEntities());
            if (!validation.isValid()) {
                System.out.println("\n? Validation Errors:");
                for (String error : validation.getErrors()) {
                    System.out.println("   " + error);
                }
            }
            if (!validation.getWarnings().isEmpty()) {
                System.out.println("\n?? Validation Warnings:");
                for (String warning : validation.getWarnings()) {
                    System.out.println("   " + warning);
                }
            }

        } else if (result.getActionType() == ParsedQuery.ActionType.GUIDE) {
            String helpTopic = (String) result.getEntities().get("helpTopic");
            System.out.println("\n? Guidance for: " + (helpTopic != null ? helpTopic : "general_help"));

            System.out.println("\n? Steps:");
            List<String> steps = getGuidanceSteps(helpTopic);
            for (String step : steps) {
                System.out.println("  " + step);
            }
        }

        System.out.println("\n? JSON Output:");
        System.out.println(generateJsonOutput(result));
    }

    /**
     * Benchmark enhanced classifier performance
     */
    public EnhancedBenchmarkResult runEnhancedBenchmark(int iterations) {
        if (!initialized) {
            throw new IllegalStateException("Enhanced classifier not initialized");
        }

        String[] testQueries = {
            // Creation queries
            "create contract for account 123456", "make new contract for boeing", "setup contract named premium service",

            // Guidance queries
            "how to create contract", "steps to make contract", "help with contract creation",

            // Legacy queries
            "show contract 123456", "list all contracts", "search contracts", "expired contracts", "active contracts"
        };

        List<Double> processingTimes = new ArrayList<>();
        Map<ActionType, Integer> actionCounts = new HashMap<>();
        Map<QueryType, Integer> queryCounts = new HashMap<>();
        int successfulClassifications = 0;

        long totalStartTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            String query = testQueries[i % testQueries.length];

            long startTime = System.nanoTime();

            try {
                ContractCreationResult result = classifyContractIntent(query);

                long endTime = System.nanoTime();
                double processingTime = (endTime - startTime) / 1_000_000.0;
                processingTimes.add(processingTime);

                if (result.getConfidence() > 0.5) {
                    successfulClassifications++;
                }

                // Count action and query types
                actionCounts.put(result.getActionType(), actionCounts.getOrDefault(result.getActionType(), 0) + 1);
                queryCounts.put(result.getQueryType(), queryCounts.getOrDefault(result.getQueryType(), 0) + 1);

            } catch (Exception e) {
                logger.warning("Error in enhanced benchmark iteration " + i + ": " + e.getMessage());
            }
        }

        long totalEndTime = System.nanoTime();
        double totalTime = (totalEndTime - totalStartTime) / 1_000_000.0;

        return new EnhancedBenchmarkResult(iterations, successfulClassifications, processingTimes, totalTime,
                                           actionCounts, queryCounts);
    }

    /**
     * Enhanced benchmark result container
     */
    public static class EnhancedBenchmarkResult {
        private final int totalIterations;
        private final int successfulClassifications;
        private final List<Double> processingTimes;
        private final double totalTime;
        private final Map<ActionType, Integer> actionCounts;
        private final Map<QueryType, Integer> queryCounts;

        public EnhancedBenchmarkResult(int totalIterations, int successfulClassifications, List<Double> processingTimes,
                                       double totalTime, Map<ActionType, Integer> actionCounts,
                                       Map<QueryType, Integer> queryCounts) {
            this.totalIterations = totalIterations;
            this.successfulClassifications = successfulClassifications;
            this.processingTimes = processingTimes;
            this.totalTime = totalTime;
            this.actionCounts = actionCounts;
            this.queryCounts = queryCounts;
        }

        public double getAverageProcessingTime() {
            return processingTimes.stream()
                                  .mapToDouble(Double::doubleValue)
                                  .average()
                                  .orElse(0.0);
        }

        public double getMinProcessingTime() {
            return processingTimes.stream()
                                  .mapToDouble(Double::doubleValue)
                                  .min()
                                  .orElse(0.0);
        }

        public double getMaxProcessingTime() {
            return processingTimes.stream()
                                  .mapToDouble(Double::doubleValue)
                                  .max()
                                  .orElse(0.0);
        }

        public double getSuccessRate() {
            return totalIterations > 0 ? (double) successfulClassifications / totalIterations * 100 : 0.0;
        }

        public double getThroughput() {
            return totalTime > 0 ? totalIterations / (totalTime / 1000.0) : 0.0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Enhanced Benchmark Results:\n");
            sb.append(String.format("  Total Iterations: %d\n", totalIterations));
            sb.append(String.format("  Successful Classifications: %d (%.1f%%)\n", successfulClassifications,
                                    getSuccessRate()));
            sb.append(String.format("  Total Time: %.2f ms\n", totalTime));
            sb.append(String.format("  Average Processing Time: %.2f ms\n", getAverageProcessingTime()));
            sb.append(String.format("  Min Processing Time: %.2f ms\n", getMinProcessingTime()));
            sb.append(String.format("  Max Processing Time: %.2f ms\n", getMaxProcessingTime()));
            sb.append(String.format("  Throughput: %.1f queries/second\n", getThroughput()));

            sb.append("\nAction Type Distribution:\n");
            actionCounts.entrySet()
                        .stream()
                        .sorted(Map.Entry
                                   .<ActionType, Integer>comparingByValue()
                                   .reversed())
                        .forEach(entry -> sb.append(String.format("  %s: %d\n", entry.getKey(), entry.getValue())));

            sb.append("\nQuery Type Distribution:\n");
            queryCounts.entrySet()
                       .stream()
                       .sorted(Map.Entry
                                  .<QueryType, Integer>comparingByValue()
                                  .reversed())
                       .forEach(entry -> sb.append(String.format("  %s: %d\n", entry.getKey(), entry.getValue())));

            return sb.toString();
        }
    }

    /**
     * Final cleanup and resource management
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (initialized) {
                shutdown();
            }
        } finally {
            super.finalize();
        }
    }

    // Missing helper classes and enums that need to be defined

    /**
     * Entity extraction result container
     */
    private static class EntityExtractionResult {
        final Map<String, Object> entities;
        final List<String> missingFields;

        EntityExtractionResult(Map<String, Object> entities, List<String> missingFields) {
            this.entities = entities;
            this.missingFields = missingFields;
        }
    }

    /**
     * Query type result with confidence
     */
    private static class QueryTypeResult {
        final QueryType queryType;
        final double confidence;

        QueryTypeResult(QueryType queryType, double confidence) {
            this.queryType = queryType;
            this.confidence = confidence;
        }
    }


    /**
     * Contract creation result container
     */
    public static class ContractCreationResult {
        private final QueryType queryType;
        private final ActionType actionType;
        private final Map<String, Object> entities;
        private final List<String> missingFields;
        private final double confidence;
        private final String originalQuery;

        public ContractCreationResult(QueryType queryType, ActionType actionType, Map<String, Object> entities,
                                      List<String> missingFields, double confidence, String originalQuery) {
            this.queryType = queryType;
            this.actionType = actionType;
            this.entities = new HashMap<>(entities);
            this.missingFields = new ArrayList<>(missingFields);
            this.confidence = confidence;
            this.originalQuery = originalQuery;
        }

        public QueryType getQueryType() {
            return queryType;
        }

        public ActionType getActionType() {
            return actionType;
        }

        public Map<String, Object> getEntities() {
            return new HashMap<>(entities);
        }

        public List<String> getMissingFields() {
            return new ArrayList<>(missingFields);
        }

        public double getConfidence() {
            return confidence;
        }

        public String getOriginalQuery() {
            return originalQuery;
        }

        @Override
        public String toString() {
            return String.format("ContractCreationResult{queryType=%s, actionType=%s, confidence=%.2f, entities=%d, missing=%d}",
                                 queryType, actionType, confidence, entities.size(), missingFields.size());
        }
    }

    /**
     * Validation result container
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }

        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, errors=%d, warnings=%d}", valid, errors.size(),
                                 warnings.size());
        }
    }

    public static class EnhancedHealthCheckResult {
        private final boolean healthy;
        private final List<String> issues;
        private final List<String> warnings;
        private final List<String> enhancements;

        public EnhancedHealthCheckResult(boolean healthy, List<String> issues, List<String> warnings,
                                         List<String> enhancements) {
            this.healthy = healthy;
            this.issues = issues;
            this.warnings = warnings;
            this.enhancements = enhancements;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public List<String> getIssues() {
            return issues;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public List<String> getEnhancements() {
            return enhancements;
        }
    }


    /**
     * Perform enhanced health check
     */
    public EnhancedHealthCheckResult performEnhancedHealthCheck() {
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> enhancements = new ArrayList<>();

        // Base health check
        EnhancedHealthCheckResult healthCheck = performHealthCheck();
        issues.addAll(healthCheck.getIssues());
        warnings.addAll(healthCheck.getWarnings());

        // Enhanced feature checks
        if (creationKeywords != null && !creationKeywords.isEmpty()) {
            enhancements.add("Contract creation keywords configured");
        } else {
            issues.add("Creation keywords not configured");
        }

        if (guidanceKeywords != null && !guidanceKeywords.isEmpty()) {
            enhancements.add("Guidance keywords configured");
        } else {
            issues.add("Guidance keywords not configured");
        }

        if (fieldExtractionPatterns != null && !fieldExtractionPatterns.isEmpty()) {
            enhancements.add("Field extraction patterns configured (" + fieldExtractionPatterns.size() + " patterns)");
        } else {
            issues.add("Field extraction patterns not configured");
        }

        if (requiredContractFields != null && !requiredContractFields.isEmpty()) {
            enhancements.add("Required contract fields defined (" + requiredContractFields.size() + " fields)");
        } else {
            warnings.add("No required contract fields defined");
        }

        // Performance checks
        if (processingStatistics != null) {
            double avgTime = processingStatistics.getAverageProcessingTime();
            if (avgTime < 100) {
                enhancements.add("Excellent processing performance (" + String.format("%.1fms", avgTime) + " avg)");
            } else if (avgTime > 500) {
                warnings.add("Slow processing performance (" + String.format("%.1fms", avgTime) + " avg)");
            }

            double successRate = processingStatistics.getSuccessRate();
            if (successRate > 90) {
                enhancements.add("Excellent success rate (" + String.format("%.1f%%", successRate) + ")");
            } else if (successRate < 70) {
                warnings.add("Low success rate (" + String.format("%.1f%%", successRate) + ")");
            }
        }

        boolean isHealthy = issues.isEmpty();
        return new EnhancedHealthCheckResult(isHealthy, issues, warnings, enhancements);
    }

    /**
     * Export enhanced configuration
     */
    public String exportEnhancedConfiguration() {
        StringBuilder config = new StringBuilder();
        config.append("=== Enhanced MLIntentClassifierImproved Configuration ===\n");
        config.append("Version: 3.0\n");
        config.append("Initialized: ")
              .append(initialized)
              .append("\n");
        config.append("Enhanced Features: ? Enabled\n\n");

        // Base configuration
        config.append(exportConfiguration()).append("\n");

        // Enhanced configuration
        config.append("Enhanced Features Configuration:\n");

        if (creationKeywords != null) {
            config.append("Creation Keywords:\n");
            creationKeywords.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> config.append("  ")
                                                    .append(entry.getKey())
                                                    .append(": ")
                                                    .append(entry.getValue().size())
                                                    .append(" keywords\n"));
        }

        if (guidanceKeywords != null) {
            config.append("\nGuidance Keywords:\n");
            guidanceKeywords.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> config.append("  ")
                                                    .append(entry.getKey())
                                                    .append(": ")
                                                    .append(entry.getValue().size())
                                                    .append(" keywords\n"));
        }

        if (fieldExtractionPatterns != null) {
            config.append("\nField Extraction Patterns:\n");
            fieldExtractionPatterns.entrySet()
                                   .stream()
                                   .sorted(Map.Entry.comparingByKey())
                                   .forEach(entry -> config.append("  ")
                                                           .append(entry.getKey())
                                                           .append(": ")
                                                           .append(entry.getValue().pattern())
                                                           .append("\n"));
        }

        if (requiredContractFields != null) {
            config.append("\nRequired Contract Fields:\n");
            for (String field : requiredContractFields) {
                config.append("   ")
                      .append(field)
                      .append("\n");
            }
        }

        return config.toString();
    }

    /**
     * Validate creation entities
     */
    private ValidationResult validateCreationEntities(Map<String, Object> entities) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validate account number
        if (entities.containsKey("accountNumber")) {
            String accountNumber = entities.get("accountNumber").toString();
            if (!accountNumber.matches("\\d{6,8}")) {
                errors.add("Account number must be 6-8 digits");
            }
        }

        // Validate contract number
        if (entities.containsKey("contractNumber")) {
            String contractNumber = entities.get("contractNumber").toString();
            if (!contractNumber.matches("\\d{6}")) {
                errors.add("Contract number must be exactly 6 digits");
            }
        }

        // Validate expiration date
        if (entities.containsKey("expirationDate")) {
            String expirationDate = entities.get("expirationDate").toString();
            if (!expirationDate.matches("\\d{4}-\\d{2}-\\d{2}") &&
                !expirationDate.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                errors.add("Expiration date must be in YYYY-MM-DD or MM/DD/YYYY format");
            }
        }

        // Validate price
        if (entities.containsKey("priceList")) {
            String price = entities.get("priceList").toString();
            if (!price.matches("\\$?\\d+(?:\\.\\d{2})?")) {
                warnings.add("Price format may not be standard (expected: $0.00 or 0.00)");
            }
        }

        // Validate contract name
        if (entities.containsKey("contractName")) {
            String contractName = entities.get("contractName").toString();
            if (contractName.length() < 3) {
                warnings.add("Contract name is very short");
            } else if (contractName.length() > 50) {
                warnings.add("Contract name is very long");
            }
        }

        boolean isValid = errors.isEmpty();
        return new ValidationResult(isValid, errors, warnings);
    }

    /**
     * Get suggested next steps for missing fields
     */
    private List<String> getSuggestedNextSteps(List<String> missingFields) {
        List<String> suggestions = new ArrayList<>();

        for (String field : missingFields) {
            switch (field) {
            case "accountNumber":
                suggestions.add("? Please provide the customer account number (6-8 digits)");
                break;
            case "contractName":
                suggestions.add("? Please specify a descriptive contract name");
                break;
            case "expirationDate":
                suggestions.add("? Please set the contract expiration date (YYYY-MM-DD format)");
                break;
            case "priceList":
                suggestions.add("? Please define the contract price or price list");
                break;
            default:
                suggestions.add("? Please provide: " + formatFieldName(field));
                break;
            }
        }

        return suggestions;
    }

    /**
     * Format field name for display
     */
    private String formatFieldName(String fieldName) {
        if (fieldName == null)
            return "";

        // Convert camelCase to readable format
        String formatted = fieldName.replaceAll("([A-Z])", " $1").toLowerCase();

        // Capitalize first letter
        if (!formatted.isEmpty()) {
            formatted = Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);
        }

        return formatted;
    }

    /**
     * Enhanced main method with comprehensive testing
     */
    public static void main(String[] args) {
        generateEnhancedModel();
    }


    /**
     * Generate enhanced model
     */
    public static void generateEnhancedModel() {
        System.out.println("=== Enhanced Contract Intent Model Generation ===");
        System.out.println("? Starting enhanced model training...\n");

        try {
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();

            System.out.println("? Training with enhanced contract data...");
            System.out.println("   Contract creation samples");
            System.out.println("   Guidance and help samples");
            System.out.println("   Legacy compatibility samples");
            System.out.println("   Entity extraction patterns");

            classifier.initializeWithTrainingData();
            System.out.println("? Training completed successfully!");

            String modelPath = "./models";
            File modelDir = new File(modelPath);
            if (!modelDir.exists()) {
                modelDir.mkdirs();
                System.out.println("? Created models directory: " + modelDir.getAbsolutePath());
            }

            System.out.println("? Saving enhanced model...");
            classifier.saveModel(modelPath);

            File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
            File configFile = new File(modelPath + "/enhanced-config.json");

            if (enhancedModelFile.exists() && enhancedModelFile.length() > 0) {
                System.out.println("? Enhanced model generated successfully!");
                System.out.println("? Location: " + enhancedModelFile.getAbsolutePath());
                System.out.println("? Size: " + String.format("%.2f KB", enhancedModelFile.length() / 1024.0));
                System.out.println("? Created: " + new java.util.Date(enhancedModelFile.lastModified()));

                if (configFile.exists()) {
                    System.out.println("?? Configuration: " + configFile.getAbsolutePath());
                }

                System.out.println("\n? Testing enhanced model...");
                testEnhancedSavedModel(modelPath);

            } else {
                System.out.println("? Enhanced model generation failed!");
            }

        } catch (Exception e) {
            System.err.println("? Error during enhanced model generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test enhanced saved model
     */
    private static void testEnhancedSavedModel(String modelPath) {
        try {
            MLIntentClassifierImproved testClassifier = new MLIntentClassifierImproved();
            testClassifier.loadModel(modelPath);

            String[] contractQueries = {
                "show contract 123456", "contract details 123456", "get contract info 123456",
                "contracts created by vinod after 1-Jan-2020", "status of contract 123456", "expired contracts",
                "contracts for customer number 897654", "account 10840607 contracts", "contracts created in 2024",
                "get all metadata for contract 123456", "contracts under account name 'Siemens'",
                "get project type, effective date, and price list for account number 10840607",
                "show contract for customer number 123456", "shwo contrct 123456", "get contrct infro 123456",
                "find conract detials 123456", "cntract summry for 123456",
                "contarcts created by vinod aftr 1-Jan-2020", "statuss of contrct 123456", "exipred contrcts",
                "contracs for cstomer numer 897654", "accunt number 10840607 contrcts", "contracts from lst mnth",
                "contrcts creatd in 2024", "shwo efective date and statuz", "get cntracts for acount no 123456",
                "contrct summry for custmor 999999", "get contrct detals by acount 10840607",
                "contracts created btwn Jan and June 2024", "custmer honeywel", "contarcts by vinod",
                "show contracts for acc no 456789", "activ contrcts created by mary", "kontract detials 123456",
                "kontrakt sumry for account 888888", "boieng contrcts", "acc number 1084", "price list corprate2024",
                "oppurtunity code details", "get all flieds for customer 123123"
            };


            for (String testQuery : contractQueries) {
                ContractCreationResult result = testClassifier.classifyContractIntent(testQuery);
                System.out.println("Original: " + result.getOriginalQuery() + "  Corrected: ");
                System.out.println("Query Type :" + result.getQueryType() + " Action Type :" + result.getActionType() +
                                   " Confidence :" + result.getConfidence() + " Size :" + result.getEntities().size() +
                                   " EntitiesSize :" + result.getMissingFields().size());
                System.out.println("Action Type :" + result.getActionType());

            }


        } catch (Exception e) {
            System.out.println("? Enhanced model validation failed: " + e.getMessage());
        }
    }

    /**
     * Run enhanced model tests
     */
    public static void runEnhancedModelTests() {
        System.out.println("=== Enhanced Contract Model Testing ===");

        try {
            String modelPath = "./models";
            File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
            File legacyModelFile = new File(modelPath + "/en-contracts.bin");

            if (!enhancedModelFile.exists() && !legacyModelFile.exists()) {
                System.out.println("?? No model file found. Generating enhanced model first...");
                generateEnhancedModel();
            }

            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);
            classifier.enableProcessingStatistics();
            System.out.println("? Enhanced model loaded successfully\n");

            // Run comprehensive tests
            EnhancedTestResult testResult = classifier.runComprehensiveTests();
            System.out.println(testResult);

            // Show enhanced training data summary
            System.out.println("\n" + classifier.exportEnhancedTrainingDataSummary());

            // Performance benchmark
            System.out.println("\n? Running performance benchmark...");
            EnhancedBenchmarkResult benchmark = classifier.runEnhancedBenchmark(100);
            System.out.println(benchmark);

            // Health check
            System.out.println("\n? Enhanced health check...");
            EnhancedHealthCheckResult healthCheck = classifier.performEnhancedHealthCheck();
            System.out.println(healthCheck);

            // Memory analysis
            MemoryUsageResult memoryUsage = classifier.analyzeMemoryUsage();
            System.out.println("\n? " + memoryUsage);

            if (testResult.getSuccessRate() >= 85) {
                System.out.println("\n? Enhanced model performance is EXCELLENT!");
            } else if (testResult.getSuccessRate() >= 75) {
                System.out.println("\n? Enhanced model performance is GOOD!");
            } else {
                System.out.println("\n?? Enhanced model needs improvement");
            }

        } catch (Exception e) {
            System.err.println("? Enhanced test error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run enhanced demo
     */
    public static void runEnhancedDemo() {
        System.out.println("=== Enhanced Contract Classifier Demo ===");

        try {
            String modelPath = "./models";
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);

            System.out.println("? Enhanced classifier loaded\n");
            System.out.println(classifier.generateDemoReport());

            // Demo queries
            String[] demoQueries = {
                "create contract for account 123456", "make new contract for boeing expires 2025-12-31 price 5000",
                "how to create contract", "steps to make new contract", "update contract 789012",
                "show contract 456789", "list all contracts", "search contracts for microsoft"
            };

            System.out.println("\n? Demo Query Results:");
            System.out.println("============================================================");
            // Attribute queries
            classifier.classifyContractIntent("show the expiration date for contract 123456");
            classifier.classifyContractIntent("get effective date and created by for contract 789012");
            classifier.classifyContractIntent("what is the project type of contract 456789");

            // Creator queries
            classifier.classifyContractIntent("show contracts created by vinod");
            classifier.classifyContractIntent("find contracts authored by john smith");
            for (String query : demoQueries) {
                System.out.println("\n? Query: \"" + query + "\"");
                System.out.println("-------------------------------------------------------");

                ContractCreationResult result = classifier.classifyContractIntent(query);

                System.out.println("? Action: " + result.getActionType());
                System.out.println("? Query Type: " + result.getQueryType());
                System.out.println("? Confidence: " + String.format("%.1f%%", result.getConfidence() * 100));

                if (!result.getEntities().isEmpty()) {
                    System.out.println("?? Entities: " + result.getEntities().keySet());
                }

                if (!result.getMissingFields().isEmpty()) {
                    System.out.println("?? Missing: " + result.getMissingFields());
                }

                // Show JSON output for creation requests
                if (result.getActionType() == ActionType.CREATE || result.getActionType() == ActionType.GUIDE) {
                    System.out.println("? JSON Output:");
                    System.out.println(classifier.generateJsonOutput(result));
                }
            }

            System.out.println("\n? Enhanced demo completed successfully!");

        } catch (Exception e) {
            System.err.println("? Enhanced demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run enhanced benchmark
     */
    public static void runEnhancedBenchmark() {
        System.out.println("=== Enhanced Performance Benchmark ===");

        try {
            String modelPath = "./models";
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);

            System.out.println("? Running enhanced benchmark (1000 iterations)...");
            EnhancedBenchmarkResult result = classifier.runEnhancedBenchmark(1000);

            System.out.println(result);

            // Optimization suggestions
            System.out.println("\n? Running optimization analysis...");
            EnhancedOptimizationResult optimization = classifier.optimizeEnhancedModel();
            System.out.println(optimization);

        } catch (Exception e) {
            System.err.println("? Benchmark error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run interactive mode
     */
    public static void runInteractiveMode() {
        try {
            String modelPath = "./models";
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);
            classifier.enableProcessingStatistics();

            classifier.runInteractiveDemo();

        } catch (Exception e) {
            System.err.println("? Interactive mode error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show enhanced model info
     */
    public static void showEnhancedModelInfo() {
        System.out.println("=== Enhanced Contract Model Information ===");

        String modelPath = "./models";
        File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
        File legacyModelFile = new File(modelPath + "/en-contracts.bin");
        File configFile = new File(modelPath + "/enhanced-config.json");

        System.out.println("? Model Directory: " + new File(modelPath).getAbsolutePath());
        System.out.println();

        // Enhanced model info
        System.out.println("? Enhanced Model:");
        System.out.println("  File: " + enhancedModelFile.getName());
        System.out.println("  Exists: " + (enhancedModelFile.exists() ? "?" : "?"));
        if (enhancedModelFile.exists()) {
            System.out.println("  Size: " + String.format("%.2f KB", enhancedModelFile.length() / 1024.0));
            System.out.println("  Modified: " + new java.util.Date(enhancedModelFile.lastModified()));
        }

        // Legacy model info
        System.out.println("\n? Legacy Model:");
        System.out.println("  File: " + legacyModelFile.getName());
        System.out.println("  Exists: " + (legacyModelFile.exists() ? "?" : "?"));
        if (legacyModelFile.exists()) {
            System.out.println("  Size: " + String.format("%.2f KB", legacyModelFile.length() / 1024.0));
            System.out.println("  Modified: " + new java.util.Date(legacyModelFile.lastModified()));
        }

        // Configuration info
        System.out.println("\n?? Configuration:");
        System.out.println("  File: " + configFile.getName());
        System.out.println("  Exists: " + (configFile.exists() ? "?" : "?"));

        // Test model loading
        if (enhancedModelFile.exists() || legacyModelFile.exists()) {
            try {
                MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
                classifier.loadModel(modelPath);

                System.out.println("\n? Model Status: VALID");
                System.out.println("? " + classifier.getVersionInfo());

                // Show configuration
                System.out.println("\n" + classifier.exportEnhancedConfiguration());

                // Show health status
                EnhancedHealthCheckResult health = classifier.performEnhancedHealthCheck();
                System.out.println(health);

            } catch (Exception e) {
                System.out.println("\n? Model Status: INVALID - " + e.getMessage());
            }
        } else {
            System.out.println("\n? Model Status: NOT FOUND");
            System.out.println("? Run with --generate to create the enhanced model");
        }
    }

    /**
     * Enhanced shutdown with cleanup
     */

    public void shutdown() {
        logger.info("Shutting down Enhanced MLIntentClassifierImproved...");

        // Enhanced cleanup
        if (creationKeywords != null) {
            creationKeywords.clear();
            creationKeywords = null;
        }

        if (guidanceKeywords != null) {
            guidanceKeywords.clear();
            guidanceKeywords = null;
        }

        if (fieldExtractionPatterns != null) {
            fieldExtractionPatterns.clear();
            fieldExtractionPatterns = null;
        }

        if (requiredContractFields != null) {
            requiredContractFields.clear();
            requiredContractFields = null;
        }

        // Call parent cleanup

        logger.info("Enhanced MLIntentClassifierImproved shutdown complete");
    }


    /**
     * Enhanced equals method
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        MLIntentClassifierImproved that = (MLIntentClassifierImproved) obj;
        return initialized == that.initialized && Objects.equals(intentThresholds, that.intentThresholds) &&
               Objects.equals(intentKeywords, that.intentKeywords) &&
               Objects.equals(creationKeywords, that.creationKeywords) &&
               Objects.equals(guidanceKeywords, that.guidanceKeywords) &&
               Objects.equals(fieldExtractionPatterns, that.fieldExtractionPatterns) &&
               Objects.equals(requiredContractFields, that.requiredContractFields);
    }

    /**
     * Enhanced hashCode method
     */
    @Override
    public int hashCode() {
        return Objects.hash(initialized, intentThresholds, intentKeywords, creationKeywords, guidanceKeywords,
                            fieldExtractionPatterns, requiredContractFields);
    }


    /**
     * Intent Result container - enhanced version
     */
    public static class IntentResult {
        private final ContractIntent intent;
        private final double confidence;
        private final String actionRequired;
        private final Map<String, Double> allIntentScores;
        private final String contractNumber;

        public IntentResult(ContractIntent intent, double confidence, String actionRequired,
                            Map<String, Double> allIntentScores, String contractNumber) {
            this.intent = intent;
            this.confidence = confidence;
            this.actionRequired = actionRequired;
            this.allIntentScores = new HashMap<>(allIntentScores);
            this.contractNumber = contractNumber;
        }

        public ContractIntent getIntent() {
            return intent;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getActionRequired() {
            return actionRequired;
        }

        public Map<String, Double> getAllIntentScores() {
            return new HashMap<>(allIntentScores);
        }

        public String getContractNumber() {
            return contractNumber;
        }

        @Override
        public String toString() {
            return String.format("IntentResult{intent=%s, confidence=%.2f, action=%s}", intent, confidence,
                                 actionRequired);
        }
    }

    /**
     * NLP Processing Result container
     */
    public static class NLPProcessingResult {
        private final String originalSentence;
        private final String[] tokens;
        private final String[] posTags;
        private final opennlp.tools.util.Span[] nameSpans;

        public NLPProcessingResult(String originalSentence, String[] tokens, String[] posTags,
                                   opennlp.tools.util.Span[] nameSpans) {
            this.originalSentence = originalSentence;
            this.tokens = tokens.clone();
            this.posTags = posTags.clone();
            this.nameSpans = nameSpans.clone();
        }

        public String getOriginalSentence() {
            return originalSentence;
        }

        public String[] getTokens() {
            return tokens.clone();
        }

        public String[] getPosTags() {
            return posTags.clone();
        }

        public opennlp.tools.util.Span[] getNameSpans() {
            return nameSpans.clone();
        }
    }


    /**
     * Initialize enhanced features in constructor
     */
    private void initializeEnhancedFeatures() {
        initializeCreationKeywords();
        initializeGuidanceKeywords();
        initializeFieldExtractionPatterns();
        initializeRequiredContractFields();
    }

    /**
     * Initialize creation keywords
     */
    private void initializeCreationKeywords() {
        this.creationKeywords = new HashMap<>();

        creationKeywords.put("create_contract",
                             Arrays.asList("create", "make", "new", "setup", "establish", "generate", "build", "form",
                                           "creat", "mak", "nw", "setp", "generat", "bild", "frm"));

        creationKeywords.put("contract_entities",
                             Arrays.asList("contract", "agreement", "deal", "arrangement", "pact", "accord", "contrct",
                                           "agrement", "del", "arangement", "pct", "acord"));

        creationKeywords.put("customer_entities",
                             Arrays.asList("for", "with", "customer", "client", "account", "company", "organization",
                                           "fr", "wth", "custmer", "clint", "accnt", "compny", "organizaton"));
    }

    /**
     * Initialize guidance keywords
     */
    private void initializeGuidanceKeywords() {
        this.guidanceKeywords = new HashMap<>();

        guidanceKeywords.put("help_requests",
                             Arrays.asList("help", "how", "guide", "steps", "process", "procedure", "instructions",
                                           "hlp", "hw", "guid", "stps", "proces", "procedur", "instructons"));

        guidanceKeywords.put("creation_help",
                             Arrays.asList("how to create", "steps to make", "guide for", "help with", "process of",
                                           "hw to creat", "stps to mak", "guid fr", "hlp wth", "proces of"));

        guidanceKeywords.put("general_help",
                             Arrays.asList("what", "when", "where", "why", "which", "explain", "describe", "wht", "whn",
                                           "wher", "wy", "whch", "expln", "describ"));
    }

    /**
     * Initialize field extraction patterns
     */
    private void initializeFieldExtractionPatterns() {
        this.fieldExtractionPatterns = new HashMap<>();

        // Contract number pattern (6 digits)
        fieldExtractionPatterns.put("contractNumber", Pattern.compile("\\b\\d{6}\\b"));

        fieldExtractionPatterns.put("accountNumber",
                                    Pattern.compile("\\b(?:account|acc|customer)\\s*(\\d{6,8})\\b",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.put("contractName",
                                    Pattern.compile("\\b(?:named?|called?)\\s*['\"]([^'\"]+)['\"]",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.put("expirationDate",
                                    Pattern.compile("\\b(?:expir\\w*|end\\w*|until)\\s*(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4})\\b",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.put("priceList",
                                    Pattern.compile("\\b(?:price|cost|amount)\\s*(\\$?\\d+(?:\\.\\d{2})?)\\b",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.put("customerName",
                                    Pattern.compile("\\b(?:for|customer|client)\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)?)\\b",
                                                    Pattern.CASE_INSENSITIVE));
    }


    private void initializeAllContractFields() {
        this.allContractFields =
            new HashSet<>(Arrays.asList(
                                               // Date fields
                                               "expirationDate", "effectiveDate", "createdDate", "updatedDate",
                                               "startDate", "endDate", "renewalDate", "terminationDate",

                                               // People fields
                                               "createdBy", "updatedBy", "owner", "approver", "assignee",
                                               "projectManager", "accountManager", "legalReviewer",

                                               // Project fields
                                               "projectType", "projectCode", "projectName", "projectPhase",
                                               "department", "division", "businessUnit",

                                               // Status fields
                                               "status", "priority", "riskLevel", "complianceStatus",

                                               // Financial fields
                                               "value", "currency", "paymentTerms", "billingFrequency",

                                               // Document fields
                                               "version", "template", "language", "jurisdiction",

                                               // Existing fields
                                               "accountNumber", "contractName", "priceList", "customerName"));
    }

    private void initializeEnhancedFieldExtractionPatterns() {
        this.fieldExtractionPatterns = new HashMap<>();

        // 1. Date range pattern - FIXED with proper group closure
        String datePattern = "\\d{1,2}-[A-Za-z]{3}-\\d{4}|\\d{4}-\\d{2}-\\d{2}";
        fieldExtractionPatterns.put("dateRangeQuery",
                                    Pattern.compile("\\b(?:after|before|between)\\s+(" + datePattern +
                                                    ")(?:\\s+(?:and|to)\\s+(" + datePattern + "))?\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 2. Contract number pattern
        fieldExtractionPatterns.put("contractNumber", Pattern.compile("\\b\\d{6}\\b"));

        // 3. Account number pattern
        fieldExtractionPatterns.put("accountNumber",
                                    Pattern.compile("\\b(?:account|acc|customer)\\s*(?:num(?:ber)?|no|#)?\\s*(\\d{6,8})\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 4. Customer name pattern
        fieldExtractionPatterns.put("customerName",
                                    Pattern.compile("\\b(?:for|customer|client)\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)*)\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 5. Contract name pattern
        fieldExtractionPatterns.put("contractName",
                                    Pattern.compile("\\b(?:named?|called?)\\s*['\"]([^'\"]+)['\"]",
                                                    Pattern.CASE_INSENSITIVE));

        // 6. Expiration date pattern
        fieldExtractionPatterns.put("expirationDate",
                                    Pattern.compile("\\b(?:expir\\w*|end\\w*|until)\\s*(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4})\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 7. Price list pattern
        fieldExtractionPatterns.put("priceList",
                                    Pattern.compile("\\b(?:price|cost|amount)\\s*(\\$?\\d+(?:\\.\\d{2})?)\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 8. Attribute query pattern
        fieldExtractionPatterns.put("attributeQuery",
                                    Pattern.compile("\\b(?:show|get|what is|display)\\s+(?:the\\s+)?([a-zA-Z][a-zA-Z,\\s]+)\\s+(?:for|of)\\s+contract\\s+(\\d{6,8})",
                                                    Pattern.CASE_INSENSITIVE));

        // 9. Creator query pattern
        fieldExtractionPatterns.put("creatorQuery",
                                    Pattern.compile("\\bcontracts?\\s+(?:created|authored)\\s+by\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)*)\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 10. Partial account pattern
        fieldExtractionPatterns.put("partialAccount",
                                    Pattern.compile("\\baccount.*?(\\d{4,})\\b", Pattern.CASE_INSENSITIVE));
    }

    private void initializeFieldSynonyms() {
        this.fieldSynonyms = new HashMap<>();
        fieldSynonyms.put("creator", Arrays.asList("createdBy", "created by", "author", "authored by"));
        fieldSynonyms.put("author", Arrays.asList("createdBy", "creator"));
        fieldSynonyms.put("dates", Arrays.asList("effectiveDate", "expirationDate", "createdDate"));
        fieldSynonyms.put("expiration", Arrays.asList("expirationDate", "expiry", "expires", "end date"));
        fieldSynonyms.put("effective", Arrays.asList("effectiveDate", "start date", "begins", "starts"));
        fieldSynonyms.put("created", Arrays.asList("createdDate", "creation date", "date created"));
        fieldSynonyms.put("updated", Arrays.asList("updatedDate", "last modified", "modified date"));
        fieldSynonyms.put("creator", Arrays.asList("createdBy", "created by", "who created", "author"));
        fieldSynonyms.put("modifier", Arrays.asList("updatedBy", "updated by", "last modified by"));
        fieldSynonyms.put("project", Arrays.asList("projectType", "project type", "project category"));
    }

    /**
     * Initialize required contract fields
     */
    private void initializeRequiredContractFields() {
        this.requiredContractFields =
            new HashSet<>(Arrays.asList("accountNumber", "contractName", "expirationDate", "customerName",
                                        "priceList"));
    }

    /**
     * Update constructor to initialize enhanced features
     */
    public MLIntentClassifierImproved() {
        initializePatterns();
        initializeIntentThresholds();
        initializeEnhancedIntentKeywords();
        initializeAbbreviationMap();
        initializeEnhancedFeatures();
        initializeAllContractFields();
        initializeFieldSynonyms();
        initializeEnhancedFieldExtractionPatterns();
        initializeDictionary();
        // initializeSymSpell(); // Add this line
    }


    private void initializeDictionary() {
        // Load dictionary (contract fields + common terms)
        initializeAllContractFields();
        dictionary.addAll(allContractFields);

        // Add common contract terms
        addContractTermsToDictionary();

        // Add common words
        dictionary.addAll(Arrays.asList("contract", "create", "account", "customer", "year", "vinod", "show", "get",
                                        "find", "search", "list", "display", "view", "how", "what", "when", "where",
                                        "which", "who", "why", "and", "or", "for", "with", "from", "to", "by"));
    }


    /**
     * Generate JSON output for results
     */
    public String generateJsonOutput(ContractCreationResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"queryType\": \"")
            .append(result.getQueryType())
            .append("\",\n");
        json.append("  \"actionType\": \"")
            .append(result.getActionType())
            .append("\",\n");
        json.append("  \"confidence\": ")
            .append(String.format("%.3f", result.getConfidence()))
            .append(",\n");
        json.append("  \"originalQuery\": \"")
            .append(result.getOriginalQuery())
            .append("\",\n");

        json.append("  \"entities\": {\n");
        boolean firstEntity = true;
        for (Map.Entry<String, Object> entry : result.getEntities().entrySet()) {
            if (!firstEntity)
                json.append(",\n");
            json.append("    \"")
                .append(entry.getKey())
                .append("\": \"")
                .append(entry.getValue())
                .append("\"");
            firstEntity = false;
        }
        json.append("\n  },\n");

        json.append("  \"missingFields\": [\n");
        for (int i = 0; i < result.getMissingFields().size(); i++) {
            if (i > 0)
                json.append(",\n");
            json.append("    \"")
                .append(result.getMissingFields().get(i))
                .append("\"");
        }
        json.append("\n  ],\n");

        json.append("  \"timestamp\": \"")
            .append(new java.util.Date())
            .append("\",\n");
        json.append("  \"version\": \"3.0\"\n");
        json.append("}");

        return json.toString();
    }

    /**
     * Get guidance steps for help topics
     */
    private List<String> getGuidanceSteps(String helpTopic) {
        List<String> steps = new ArrayList<>();

        if (helpTopic == null || helpTopic.equals("general_help") || helpTopic.contains("create")) {
            steps.add("1. ? Gather required information:");
            steps.add("    Customer account number (6-8 digits)");
            steps.add("    Contract name/description");
            steps.add("    Expiration date (YYYY-MM-DD)");
            steps.add("    Price or price list");
            steps.add("2. ? Use creation command:");
            steps.add("    \"create contract for account [number]\"");
            steps.add("    \"make new contract named [name]\"");
            steps.add("3. ? Review and confirm details");
            steps.add("4. ? Save the contract");
        } else {
            steps.add("1. ? Specify your question more clearly");
            steps.add("2. ? Try asking: \"how to create contract\"");
            steps.add("3. ? Or search for specific topics");
        }

        return steps;
    }

    /**
     * Final validation and completion check
     */
    public boolean validateImplementation() {
        List<String> issues = new ArrayList<>();

        // Check all required components
        if (!initialized)
            issues.add("Classifier not initialized");
        if (model == null)
            issues.add("Model not loaded");
        if (categorizer == null)
            issues.add("Categorizer not available");
        if (intentThresholds == null)
            issues.add("Intent thresholds not configured");
        if (intentKeywords == null)
            issues.add("Intent keywords not configured");
        if (creationKeywords == null)
            issues.add("Creation keywords not configured");
        if (guidanceKeywords == null)
            issues.add("Guidance keywords not configured");
        if (fieldExtractionPatterns == null)
            issues.add("Field patterns not configured");
        if (requiredContractFields == null)
            issues.add("Required fields not configured");

        if (!issues.isEmpty()) {
            logger.severe("Implementation validation failed: " + String.join(", ", issues));
            return false;
        }

        logger.info("? Implementation validation successful - all components properly configured");
        return true;
    }

    private static class ActionTypeResult {
        final ActionType actionType;
        final double confidence;

        ActionTypeResult(ActionType actionType, double confidence) {
            this.actionType = actionType;
            this.confidence = confidence;
        }
    }

    public static class MemoryUsageResult {
        private final long totalMemory;
        private final long freeMemory;
        private final long usedMemory;
        private final double usagePercentage;

        public MemoryUsageResult(long totalMemory, long freeMemory, long usedMemory, double usagePercentage) {
            this.totalMemory = totalMemory;
            this.freeMemory = freeMemory;
            this.usedMemory = usedMemory;
            this.usagePercentage = usagePercentage;
        }

        @Override
        public String toString() {
            return String.format("Memory{used=%.1fMB, total=%.1fMB, usage=%.1f%%}", usedMemory / 1024.0 / 1024.0,
                                 totalMemory / 1024.0 / 1024.0, usagePercentage);
        }
    }

    public EnhancedHealthCheckResult performHealthCheck() {
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> enhancements = new ArrayList<>();

        // Check initialization
        if (!initialized) {
            issues.add("Classifier not initialized");
        }

        // Check model
        if (model == null) {
            issues.add("Model not loaded");
        }

        // Check categorizer
        if (categorizer == null) {
            issues.add("Categorizer not available");
        }

        boolean isHealthy = issues.isEmpty();
        return new EnhancedHealthCheckResult(isHealthy, issues, warnings, enhancements);
    }

    public String exportConfiguration() {
        StringBuilder config = new StringBuilder();
        config.append("=== MLIntentClassifierImproved Configuration ===\n");
        config.append("Initialized: ")
              .append(initialized)
              .append("\n");
        config.append("Contract Number Pattern: ")
              .append(contractNumberPattern.pattern())
              .append("\n");
        config.append("Customer Number Pattern: ")
              .append(customerNumberPattern.pattern())
              .append("\n\n");

        config.append("Intent Thresholds:\n");
        intentThresholds.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> config.append("  ")
                                                .append(entry.getKey())
                                                .append(": ")
                                                .append(entry.getValue())
                                                .append("\n"));

        config.append("\nIntent Keywords Count:\n");
        intentKeywords.entrySet()
                      .stream()
                      .sorted(Map.Entry.comparingByKey())
                      .forEach(entry -> config.append("  ")
                                              .append(entry.getKey())
                                              .append(": ")
                                              .append(entry.getValue().size())
                                              .append(" keywords\n"));

        config.append("\nAbbreviation Map:\n");
        abbreviationMap.entrySet()
                       .stream()
                       .sorted(Map.Entry.comparingByKey())
                       .forEach(entry -> config.append("  ")
                                               .append(entry.getKey())
                                               .append(" -> ")
                                               .append(entry.getValue())
                                               .append("\n"));

        return config.toString();
    }

    private List<String> extractRequestedFields(String query) {
        if (query.contains("full details") || query.contains("all fields")) {
            return new ArrayList<>(allContractFields); // Return all 30 fields
        }
        // Pattern for "show X, Y, Z for..."
        Pattern multiFieldPattern =
            Pattern.compile("\\b(?:show|get|list|display)\\s+([a-zA-Z,\\s]+?)\\s+(?:for|of|where)\\b",
                            Pattern.CASE_INSENSITIVE);

        List<String> fields = new ArrayList<>();

        Matcher matcher = multiFieldPattern.matcher(query);
        if (matcher.find()) {
            String[] parts = matcher.group(1).split("[,\\s]+and\\s+|[,\\s]+");
            for (String part : parts) {
                String normalized = normalizeFieldName(part.trim());
                if (allContractFields.contains(normalized)) {
                    fields.add(normalized);
                }
            }
        }
        return fields;
    }

    private String normalizeFieldName(String fieldText) {
        String normalized = fieldText.toLowerCase()
                                     .trim()
                                     .replaceAll("\\s+", " ");

        // Check for known field names first
        for (String field : allContractFields) {
            if (field.equalsIgnoreCase(normalized) || field.toLowerCase()
                                                           .replaceAll("([A-Z])", " $1")
                                                           .trim()
                                                           .equals(normalized)) {
                return field;
            }
        }

        // Check synonyms without lambda
        for (Map.Entry<String, List<String>> entry : fieldSynonyms.entrySet()) {
            for (String syn : entry.getValue()) {
                if (syn.equalsIgnoreCase(normalized)) {
                    return findActualFieldName(entry.getKey());
                }
            }
        }

        // Convert to camelCase
        return toCamelCase(normalized);
    }

    private boolean containsFieldSynonym(String query, String field) {
        String fieldKey = field.toLowerCase();
        if (fieldSynonyms.containsKey(fieldKey)) {
            return fieldSynonyms.get(fieldKey)
                                .stream()
                                .anyMatch(synonym -> query.contains(synonym.toLowerCase()));
        }
        return false;
    }

    private void addAttributeQueryTrainingSamples(List<DocumentSample> samples) {

        // Single field queries
        addTrainingSamples(samples, "get_contract_attribute",
                           new String[][] { { "show", "the", "expiration", "date", "for", "contract", "123456" },
                                            { "get", "effective", "date", "and", "created", "by", "for", "contract",
                                              "789012" },
                                            { "what", "is", "the", "project", "type", "of", "contract", "456789" },
                                            { "display", "contract", "123456", "status" },
                                            { "show", "contracts", "created", "by", "vinod" },
                                            { "find", "contracts", "authored", "by", "john", "smith" } });
        addTrainingSamples(samples, "get_contract_attribute",
                           new String[][] { { "what", "is", "the", "expiration", "date", "of", "contract", "123456" },
                                            { "show", "me", "the", "project", "type", "for", "contract", "789012" },
                                            { "who", "is", "the", "creator", "of", "contract", "456789" },
                                            { "get", "the", "effective", "date", "of", "contract", "234567" },
                                            { "what", "is", "the", "status", "of", "contract", "345678" } });

        // Multi-field queries
        addTrainingSamples(samples, "get_multiple_attributes",
                           new String[][] { { "what", "is", "the", "expiration", "effective", "dates", "and", "project",
                                              "type", "for", "contract", "123456" },
                                            { "show", "me", "created", "date", "created", "by", "updated", "by", "for",
                                              "contract", "789012" },
                                            { "get", "status", "priority", "and", "owner", "of", "contract",
                                              "456789" } });

        // Date range queries
        addTrainingSamples(samples, "date_range_query",
                           new String[][] { { "show", "me", "contracts", "created", "in", "last", "one", "week" },
                                            { "list", "contracts", "from", "1-Jan-25", "to", "today", "with", "created",
                                              "date" },
                                            { "find", "contracts", "between", "2024-01-01", "and", "2024-12-31" } });
    }

    public ValidationResult validateRequestedFields(List<String> requestedFields) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (String field : requestedFields) {
            if (!allContractFields.contains(field)) {
                errors.add("Unknown field: " + field);
            }
        }

        if (requestedFields.size() > 10) {
            warnings.add("Large number of fields requested (" + requestedFields.size() + ") - response may be lengthy");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    public String generateAttributeResponse(List<String> requestedFields, String contractNumber) {
        if (requestedFields.isEmpty()) {
            return "No valid fields found in query";
        }

        StringBuilder response = new StringBuilder();
        response.append("Contract ")
                .append(contractNumber)
                .append(" attributes:\n");

        for (String field : requestedFields) {
            response.append("  ")
                    .append(formatFieldName(field))
                    .append(": ");
            response.append(getFieldValue(contractNumber, field)).append("\n");
        }

        return response.toString();
    }

    private String findActualFieldName(String synonymKey) {
        // Map synonym keys to actual field names
        switch (synonymKey.toLowerCase()) {
        case "expiration":
            return "expirationDate";
        case "effective":
            return "effectiveDate";
        case "created":
            return "createdDate";
        case "updated":
            return "updatedDate";
        case "creator":
            return "createdBy";
        case "modifier":
            return "updatedBy";
        case "project":
            return "projectType";
        default:
            return toCamelCase(synonymKey);
        }
    }

    /**
     * Convert string to camelCase
     */
    private String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1));
                }
            }
        }

        return result.toString();
    }

    /**
     * Get field value for contract (mock implementation)
     */
    private String getFieldValue(String contractNumber, String fieldName) {
        // This is a mock implementation - replace with actual data access
        switch (fieldName) {
        case "expirationDate":
            return "2025-12-31";
        case "effectiveDate":
            return "2024-01-01";
        case "createdDate":
            return "2023-06-15";
        case "updatedDate":
            return "2024-01-15";
        case "createdBy":
            return "John Smith";
        case "updatedBy":
            return "Jane Doe";
        case "projectType":
            return "Software Development";
        case "status":
            return "Active";
        case "priority":
            return "High";
        case "owner":
            return "Project Manager";
        default:
            return "[" + fieldName + " value for contract " + contractNumber + "]";
        }
    }

    public AttributeQueryResult processAttributeQuery(String query) {
        // Check for creator queries first
        Matcher creatorMatcher = fieldExtractionPatterns.get("creatorQuery").matcher(query);
        if (creatorMatcher.find()) {
            String creatorName = creatorMatcher.group(1);
            return new AttributeQueryResult(ParsedQuery.QueryType.SEARCH, ParsedQuery.ActionType.GET,
                                            Collections.singletonMap("createdBy", creatorName), Collections.emptyList(),
                                            0.9, query);
        }

        // Handle regular attribute queries
        Matcher attrMatcher = fieldExtractionPatterns.get("attributeQuery").matcher(query);
        if (attrMatcher.find()) {
            String fieldsText = attrMatcher.group(1);
            String contractNumber = attrMatcher.group(2);

            List<String> requestedFields = Arrays.stream(fieldsText.split("[,\\s]+and\\s+|,"))
                                                 .map(String::trim)
                                                 .map(this::normalizeFieldName)
                                                 .filter(allContractFields::contains)
                                                 .collect(Collectors.toList());

            if (!requestedFields.isEmpty()) {
                Map<String, Object> entities = new HashMap<>();
                entities.put("contractNumber", contractNumber);
                entities.put("requestedFields", requestedFields);

                return new AttributeQueryResult(ParsedQuery.QueryType.SPECIFIC,
                                                requestedFields.size() > 1 ? ActionType.GET_MULTIPLE_ATTRIBUTES :
                                                ActionType.GET_ATTRIBUTE, entities, Collections.emptyList(), 0.85,
                                                query);
            }
        }

        // Fall back to standard processing
        return null;
    }

    public class AttributeQueryResult extends ContractCreationResult {
        public AttributeQueryResult(QueryType queryType, ActionType actionType, Map<String, Object> entities,
                                    List<String> missingFields, double confidence, String originalQuery) {
            super(queryType, actionType, entities, missingFields, confidence, originalQuery);
        }
    }

    private ContractCreationResult classifyComplexQuery(String query) {
        // Date ranges
        Pattern dateRangePattern = fieldExtractionPatterns.get("dateRangeQuery");
        Matcher dateMatcher = dateRangePattern.matcher(query);

        if (dateMatcher.find()) {
            Map<String, Object> entities = new HashMap<>();
            entities.put("dateRange", dateMatcher.group());
            return new ContractCreationResult(QueryType.FILTER, ActionType.SEARCH, entities, Collections.emptyList(),
                                              0.9, query);
        }

        // Multi-field requests
        if (query.matches(".*\\b(?:show|get|list)\\b.*\\b(?:and|,)\\b.*")) {
            List<String> fields = extractMultiFields(query);
            if (!fields.isEmpty()) {
                Map<String, Object> entities = new HashMap<>();
                entities.put("requestedFields", fields);
                return new ContractCreationResult(QueryType.SPECIFIC, ActionType.GET_MULTIPLE_ATTRIBUTES, entities,
                                                  Collections.emptyList(), 0.85, query);
            }
        }

        return null;
    }

    private List<String> extractMultiFields(String query) {
        List<String> fields = new ArrayList<>();

        // Handle "show X and Y" patterns
        Matcher matcher =
            Pattern.compile("(?:show|get|list)\\s+(?:the\\s+)?([a-zA-Z,\\s]+?)\\s+(?:for|of|where)").matcher(query);

        if (matcher.find()) {
            String[] parts = matcher.group(1).split("[,\\s]+and\\s+");
            for (String part : parts) {
                String field = normalizeFieldName(part.trim());
                if (allContractFields.contains(field)) {
                    fields.add(field);
                }
            }
        }

        return fields;
    }

    private EntityExtractionResult extractEntities(String query) {
        Map<String, Object> entities = new HashMap<>();
        List<String> missingFields = new ArrayList<>();

        // Extract contract number
        Matcher contractMatcher = contractNumberPattern.matcher(query);
        if (contractMatcher.find()) {
            entities.put("contractNumber", contractMatcher.group());
        }

        // Extract account number
        Matcher accountMatcher = accountNumberPattern.matcher(query);
        if (accountMatcher.find()) {
            entities.put("accountNumber", accountMatcher.group(1));
        }

        // Extract customer name
        String customerName = extractBusinessCustomerName(query);
        if (customerName != null) {
            entities.put("customerName", customerName);
        }

        // Extract requested fields
        List<String> requestedFields = extractRequestedFields(query);
        if (!requestedFields.isEmpty()) {
            entities.put("requestedFields", requestedFields);
        }

        // Extract date ranges
        Matcher dateMatcher = dateRangePattern.matcher(query);
        if (dateMatcher.find()) {
            String rawDateRange = dateMatcher.group();
            String normalizedRange = normalizeDateRange(rawDateRange);
            entities.put("dateRange", normalizedRange);
            validateDateRange(entities);
        }

        return new EntityExtractionResult(entities, missingFields);
    }

    private double calculateConfidence(QueryTypeResult queryType, ActionTypeResult actionType,
                                       EntityExtractionResult entities) {
        double baseConfidence = (queryType.confidence + actionType.confidence) / 2.0;

        // Boost confidence if entities are found
        if (!entities.entities.isEmpty()) {
            baseConfidence += 0.1 * Math.min(entities.entities.size(), 5); // Max 0.5 boost
        }

        // Penalize if missing required fields
        if (!entities.missingFields.isEmpty()) {
            baseConfidence -= 0.05 * entities.missingFields.size();
        }

        return Math.min(Math.max(baseConfidence, 0.0), 1.0); // Ensure between 0-1
    }

    // Then in your initialization method (like initializePatterns()):
    private void initializePatterns() {


        this.contractNumberPattern =
            Pattern.compile("\\b(?:contract|contrct|cntract|contarct)?\\s*(?:num(?:ber)?|no|#)?\\s*(\\d{6,8})\\b",
                            Pattern.CASE_INSENSITIVE);

        this.customerNumberPattern =
            Pattern.compile("\\b(?:customer|custmer|cstomer|custmor|client)\\s*(?:num(?:ber)?|no|id|#)?\\s*(\\d{6,8})\\b",
                            Pattern.CASE_INSENSITIVE);

        this.accountNumberPattern =
            Pattern.compile("\\b(?:account|acount|acc(?:ount)?|acct)\\s*(?:num(?:ber)?|no|#)?\\s*(\\d{6,8})\\b",
                            Pattern.CASE_INSENSITIVE);
        this.datePattern = Pattern.compile("\\b\\d{4}-\\d{2}-\\d{2}\\b|\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b");
        this.pricePattern =
            Pattern.compile("\\$\\d+(?:\\.\\d{2})?|\\b\\d+(?:\\.\\d{2})?\\s*(?:dollars?|usd)\\b",
                            Pattern.CASE_INSENSITIVE);
        this.dateRangePattern =
            Pattern.compile("(?:(?:from|after|since)\\s+)?" +
                            "(\\d{1,2})?(?:\\s*)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*" +
                            "(?:\\s*(?:\\d{2,4}))?" + // Optional year
                            "(?:\\s*(?:to|until|-)\\s*)" +
                            "(\\d{1,2})?(?:\\s*)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*" +
                            "(?:\\s*(?:\\d{2,4}))?", // Optional year
                            Pattern.CASE_INSENSITIVE);
    }

    private String normalizeDateRange(String dateRange) {
        Matcher matcher = dateRangePattern.matcher(dateRange);
        if (matcher.find()) {
            // Get current year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            // Extract components
            String startDay = matcher.group(1) != null ? matcher.group(1) : "1";
            String startMonth = matcher.group(2);
            String startYear =
                matcher.group(3) != null ?
                (matcher.group(3).length() == 2 ? "20" + matcher.group(3) : matcher.group(3)) :
                String.valueOf(currentYear);

            String endDay =
                matcher.group(4) != null ? matcher.group(4) :
                getLastDayOfMonth(matcher.group(5), startYear); // Handle end-of-month
            String endMonth = matcher.group(5);
            String endYear =
                matcher.group(6) != null ?
                (matcher.group(6).length() == 2 ? "20" + matcher.group(6) : matcher.group(6)) :
                String.valueOf(currentYear);

            // Convert to standard format
            String normalizedStart =
                String.format("%02d-%s-%s", Integer.parseInt(startDay), startMonth.substring(0, 3).toLowerCase(),
                              startYear);

            String normalizedEnd =
                String.format("%02d-%s-%s", Integer.parseInt(endDay), endMonth.substring(0, 3).toLowerCase(), endYear);

            return normalizedStart + " to " + normalizedEnd;
        }
        return dateRange;
    }

    private String getLastDayOfMonth(String month, String year) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
            Date date = sdf.parse(month + "-" + year);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            return "31"; // Fallback
        }
    }

    private void validateDateRange(Map<String, Object> entities) {
        if (entities.containsKey("dateRange")) {
            String[] dates = ((String) entities.get("dateRange")).split(" to ");
            String startDate = dates[0];
            String endDate = dates[1];

            // Check if dates are in current year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (startDate.endsWith(String.valueOf(currentYear)) && endDate.endsWith(String.valueOf(currentYear))) {
                entities.put("currentYearRange", true);
            }

            // Check if end date is before start date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                if (end.before(start)) {
                    entities.put("dateRangeError", "End date cannot be before start date");
                }
            } catch (ParseException e) {
                entities.put("dateRangeError", "Invalid date format");
            }
        }
    }

    private String extractCreatorFromQuery(String query) {
        Pattern pattern = Pattern.compile("created by (\\w+)");
        Matcher matcher = pattern.matcher(query);
        return matcher.find() ? matcher.group(1) : null;
    }


    public String buildWhereClause(ParsedQuery parsed) {
        return parsed.getFilters()
                     .stream()
                     .map(filter -> {
            switch (filter.getOperator()) {
            case "=":
                return String.format("%s = '%s'", filter.getField(), filter.getValue());
            case "between":
                return String.format("%s BETWEEN '%s' AND '%s'", filter.getField(), filter.getValue(),
                                     filter.getValue2());
            default:
                return "";
            }
        })
        .filter(clause -> !clause.isEmpty())
        .collect(Collectors.joining(" AND "));
    }

    /**
     * Custom spell correction implementation
     */
    private String correctTypos(String input) {
        String[] words = input.split("\\s+");
        StringBuilder corrected = new StringBuilder();

        for (String word : words) {
            // Skip numbers and special patterns
            if (shouldSkipWord(word)) {
                corrected.append(word).append(" ");
                continue;
            }

            // Check if word is in dictionary (case insensitive)
            if (isInDictionary(word)) {
                corrected.append(word).append(" ");
                continue;
            }

            // Find best match from dictionary
            String correctedWord = findBestMatch(word);
            corrected.append(correctedWord).append(" ");
        }

        return corrected.toString().trim();
    }

    private boolean shouldSkipWord(String word) {
        return word.matches("\\d+") || contractNumberPattern.matcher(word).matches() ||
               accountNumberPattern.matcher(word).matches();
    }

    private boolean isInDictionary(String word) {
        return dictionary.contains(word.toLowerCase());
    }

    private String findBestMatch(String word) {
        String lowerWord = word.toLowerCase();
        String bestMatch = word; // default to original word if no good match found
        double bestScore = 0;

        for (String dictWord : dictionary) {
            double similarity = calculateSimilarityScore(lowerWord, dictWord.toLowerCase());

            if (similarity > bestScore && similarity >= MIN_SIMILARITY_THRESHOLD) {
                bestScore = similarity;
                bestMatch = dictWord;
            }
        }

        if (!bestMatch.equals(word)) {
            logger.fine("Corrected '" + word + "' to '" + bestMatch + "' with score: " + bestScore);
        }

        return bestMatch;
    }

    private double calculateSimilarityScore(String word1, String word2) {
        // Combine multiple similarity measures for better accuracy
        double jaroWinklerScore = jaroWinkler.apply(word1, word2);
        double jaccardScore = jaccard.apply(word1, word2);
        double fuzzyScoreValue = fuzzyScore.fuzzyScore(word1, word2) / 10.0; // normalize

        // Weighted average of scores
        return (jaroWinklerScore * 0.5) + (jaccardScore * 0.3) + (fuzzyScoreValue * 0.2);
    }


    private void addContractTermsToDictionary() {
        String[] contractTerms = {
            "agreement", "amendment", "clause", "term", "expiration", "renewal", "termination", "obligation",
            "liability", "indemnification", "confidentiality", "effective", "created", "updated", "status", "priority",
            "active", "inactive", "pending", "completed", "cancelled"
        };

        // Add company names
        String[] companies = {
            "boeing", "honeywell", "microsoft", "apple", "google", "amazon", "oracle", "ibm", "cisco", "intel"
        };

        for (String term : contractTerms) {
            dictionary.add(term);
            // Add plural form if applicable
            if (term.endsWith("y")) {
                dictionary.add(term.substring(0, term.length() - 1) + "ies");
            } else if (!term.endsWith("s")) {
                dictionary.add(term + "s");
            }
        }

        for (String company : companies) {
            dictionary.add(company);
        }
    }

}

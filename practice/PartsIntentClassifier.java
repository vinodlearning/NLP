package view.practice;

import opennlp.tools.doccat.*;
import opennlp.tools.util.*;

import java.io.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class PartsIntentClassifier {
    private static final Logger logger = Logger.getLogger(PartsIntentClassifier.class.getName());

    // Parts-specific intents

    public enum PartsIntent {
        SHOW_LOADED_PARTS,
        SHOW_FAILED_PARTS,
        SHOW_FINAL_PARTS,
        SHOW_ALL_PARTS,
        GET_PART_DETAILS,
        GET_ERROR_MESSAGES,
        SEARCH_PARTS_BY_CONTRACT,
        SEARCH_PARTS_BY_CUSTOMER,
        GET_VALIDATION_STATUS,
        LIST_PARTS_BY_TYPE,
        UNKNOWN
    }

    private DoccatModel model;
    private DocumentCategorizer categorizer;
    private boolean initialized = false;

    // Patterns for entity extraction
    private final Pattern contractNumberPattern = Pattern.compile("\\b\\d{6}\\b");
    private final Pattern partNumberPattern = Pattern.compile("\\b[A-Z]{2}\\d{5}-\\d{8}\\b");
    private final Pattern invoicePattern = Pattern.compile("\\b\\d{4,8}-[A-Z]+\\b");

    // Typo correction and synonyms
    private Map<String, String> partsTypoMap;
    private Map<String, String> partsSynonymMap;
    private Map<PartsIntent, Double> intentThresholds;

    public PartsIntentClassifier() {
        initializeTypoCorrection();
        initializeSynonyms();
        initializeThresholds();
    }

    private void initializeTypoCorrection() {
        partsTypoMap = new HashMap<>();
        // Contract typos
        partsTypoMap.put("contarst", "contract");
        partsTypoMap.put("contrct", "contract");
        partsTypoMap.put("cntract", "contract");
        partsTypoMap.put("contarct", "contract");

        // Parts typos
        partsTypoMap.put("prats", "parts");
        partsTypoMap.put("aprts", "parts");
        partsTypoMap.put("partss", "parts");
        partsTypoMap.put("prts", "parts");

        // Status typos
        partsTypoMap.put("faild", "failed");
        partsTypoMap.put("failded", "failed");
        partsTypoMap.put("loadded", "loaded");
        partsTypoMap.put("loaed", "loaded");
        partsTypoMap.put("finall", "final");

        // Action typos
        partsTypoMap.put("shwo", "show");
        partsTypoMap.put("sho", "show");
        partsTypoMap.put("gte", "get");
        partsTypoMap.put("finde", "find");
        partsTypoMap.put("serach", "search");
        partsTypoMap.put("infro", "info");
        partsTypoMap.put("detials", "details");
        partsTypoMap.put("mesage", "message");
        partsTypoMap.put("messge", "message");
        partsTypoMap.put("reson", "reason");
        partsTypoMap.put("resons", "reasons");
    }

    private void initializeSynonyms() {
        partsSynonymMap = new HashMap<>();
        // Parts synonyms
        partsSynonymMap.put("line", "parts");
        partsSynonymMap.put("lines", "parts");
        partsSynonymMap.put("part", "parts");
        partsSynonymMap.put("item", "parts");
        partsSynonymMap.put("items", "parts");
        partsSynonymMap.put("component", "parts");
        partsSynonymMap.put("components", "parts");

        // Status synonyms
        partsSynonymMap.put("error", "failed");
        partsSynonymMap.put("errors", "failed");
        partsSynonymMap.put("rejected", "failed");
        partsSynonymMap.put("invalid", "failed");
        partsSynonymMap.put("processed", "final");
        partsSynonymMap.put("validated", "final");
        partsSynonymMap.put("approved", "final");
        partsSynonymMap.put("pending", "loaded");

        // Action synonyms
        partsSynonymMap.put("display", "show");
        partsSynonymMap.put("view", "show");
        partsSynonymMap.put("list", "show");
        partsSynonymMap.put("retrieve", "get");
        partsSynonymMap.put("fetch", "get");
        partsSynonymMap.put("find", "search");
        partsSynonymMap.put("locate", "search");
    }

    private void initializeThresholds() {
        intentThresholds = new HashMap<>();
        intentThresholds.put(PartsIntent.SHOW_LOADED_PARTS, 0.6);
        intentThresholds.put(PartsIntent.SHOW_FAILED_PARTS, 0.6);
        intentThresholds.put(PartsIntent.SHOW_FINAL_PARTS, 0.6);
        intentThresholds.put(PartsIntent.SHOW_ALL_PARTS, 0.5);
        intentThresholds.put(PartsIntent.GET_PART_DETAILS, 0.7);
        intentThresholds.put(PartsIntent.GET_ERROR_MESSAGES, 0.6);
        intentThresholds.put(PartsIntent.SEARCH_PARTS_BY_CONTRACT, 0.5);
        intentThresholds.put(PartsIntent.SEARCH_PARTS_BY_CUSTOMER, 0.6);
        intentThresholds.put(PartsIntent.GET_VALIDATION_STATUS, 0.6);
        intentThresholds.put(PartsIntent.LIST_PARTS_BY_TYPE, 0.5);
    }

    public void initializeWithTrainingData() throws IOException {
        ObjectStream<DocumentSample> samples = createPartsTrainingData();

        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, 200);
        params.put(TrainingParameters.CUTOFF_PARAM, 1);

        this.model = DocumentCategorizerME.train("en", samples, params, new DoccatFactory());
        this.categorizer = new DocumentCategorizerME(model);
        this.initialized = true;

        logger.info("Parts Intent Classifier initialized successfully");
        logger.info("Supported intents: " + Arrays.toString(PartsIntent.values()));
    }

    private ObjectStream<DocumentSample> createPartsTrainingData() {
        List<DocumentSample> samples = new ArrayList<>();

        // SHOW_LOADED_PARTS
        addTrainingSamples(samples, "show_loaded_parts",
                           new String[][] { { "show", "loaded", "parts", "contract", "123456" },
                                            { "display", "loaded", "parts", "123456" },
                                            { "view", "loaded", "parts", "for", "contract", "123456" },
                                            { "list", "loaded", "parts", "123456" },
                                            { "get", "loaded", "parts", "contract", "123456" },
                                            { "loaded", "parts", "123456" }, { "show", "pending", "parts", "123456" },
                                            { "display", "pending", "parts", "contract", "123456" },
                                            { "loaded", "parts", "for", "123456" },
                                            { "show", "parts", "loaded", "123456" },
                                            { "parts", "loaded", "contract", "123456" } });

        // SHOW_FAILED_PARTS
        addTrainingSamples(samples, "show_failed_parts",
                           new String[][] { { "show", "failed", "parts", "contract", "123456" },
                                            { "display", "failed", "parts", "123456" },
                                            { "view", "failed", "parts", "for", "contract", "123456" },
                                            { "list", "failed", "parts", "123456" },
                                            { "get", "failed", "parts", "contract", "123456" },
                                            { "failed", "parts", "123456" }, { "show", "error", "parts", "123456" },
                                            { "display", "rejected", "parts", "contract", "123456" },
                                            { "failed", "parts", "for", "123456" },
                                            { "show", "parts", "failed", "123456" },
                                            { "parts", "failed", "contract", "123456" }, { "error", "parts", "123456" },
                                            { "rejected", "parts", "123456" } });

        // SHOW_FINAL_PARTS
        addTrainingSamples(samples, "show_final_parts",
                           new String[][] { { "show", "final", "parts", "contract", "123456" },
                                            { "display", "final", "parts", "123456" },
                                            { "view", "final", "parts", "for", "contract", "123456" },
                                            { "list", "final", "parts", "123456" },
                                            { "get", "final", "parts", "contract", "123456" },
                                            { "final", "parts", "123456" }, { "show", "processed", "parts", "123456" },
                                            { "display", "validated", "parts", "contract", "123456" },
                                            { "final", "parts", "for", "123456" },
                                            { "show", "parts", "final", "123456" },
                                            { "parts", "final", "contract", "123456" },
                                            { "processed", "parts", "123456" }, { "validated", "parts", "123456" },
                                            { "approved", "parts", "123456" } });

        // SHOW_ALL_PARTS
        addTrainingSamples(samples, "show_all_parts",
                           new String[][] { { "show", "all", "parts", "contract", "123456" },
                                            { "display", "all", "parts", "123456" },
                                            { "view", "all", "parts", "for", "contract", "123456" },
                                            { "list", "all", "parts", "123456" },
                                            { "get", "all", "parts", "contract", "123456" },
                                            { "all", "parts", "123456" }, { "show", "parts", "123456" },
                                            { "parts", "123456" }, { "parts", "for", "contract", "123456" },
                                            { "contract", "123456", "parts" }, { "parts", "contract", "123456" } });

        // GET_PART_DETAILS
        addTrainingSamples(samples, "get_part_details",
                           new String[][] { { "part", "details", "AE13246-46485659" },
                                            { "get", "part", "info", "AE13246-46485659" },
                                            { "show", "part", "AE13246-46485659" },
                                            { "part", "information", "AE13246-46485659" },
                                            { "details", "for", "part", "AE13246-46485659" },
                                            { "part", "AE13246-46485659", "details" },
                                            { "part", "AE13246-46485659", "information" },
                                            { "info", "AE13246-46485659" }, { "AE13246-46485659", "details" },
                                            { "AE13246-46485659", "information" }, { "show", "AE13246-46485659" } });

        // GET_ERROR_MESSAGES
        addTrainingSamples(samples, "get_error_messages",
                           new String[][] { { "show", "error", "messages", "123456" },
                                            { "get", "error", "messages", "contract", "123456" },
                                            { "error", "messages", "123456" },
                                            { "show", "failed", "reasons", "123456" },
                                            { "get", "failure", "reasons", "123456" }, { "error", "reasons", "123456" },
                                            { "show", "errors", "123456" }, { "failure", "messages", "123456" },
                                            { "validation", "errors", "123456" },
                                            { "show", "validation", "failures", "123456" },
                                            { "error", "details", "123456" }, { "failure", "details", "123456" } });

        // SEARCH_PARTS_BY_CONTRACT
        addTrainingSamples(samples, "search_parts_by_contract",
                           new String[][] { { "find", "parts", "contract", "123456" }, { "search", "parts", "123456" },
                                            { "locate", "parts", "contract", "123456" },
                                            { "find", "parts", "for", "123456" },
                                            { "search", "contract", "123456", "parts" },
                                            { "parts", "for", "contract", "123456" }, { "contract", "parts", "123456" },
                                            { "find", "contract", "123456", "parts" } });

        // SEARCH_PARTS_BY_CUSTOMER
        addTrainingSamples(samples, "search_parts_by_customer",
                           new String[][] { { "parts", "for", "smith" }, { "find", "parts", "customer", "smith" },
                                            { "search", "parts", "smith" }, { "parts", "smith" },
                                            { "customer", "smith", "parts" }, { "smith", "parts" },
                                            { "find", "smith", "parts" }, { "search", "customer", "parts", "smith" } });

        return new CollectionObjectStream<>(samples);
    }

    private void addTrainingSamples(List<DocumentSample> samples, String intent, String[][] phrases) {
        for (String[] phrase : phrases) {
            samples.add(new DocumentSample(intent, phrase));
        }
    }

    public PartsIntentResult classifyIntent(String input) {
        if (!initialized) {
            throw new IllegalStateException("Parts Intent Classifier not initialized");
        }

        try {
            // Apply typo correction and synonym mapping
            String correctedInput = correctTyposAndSynonyms(input);

            // Tokenize and classify
            String[] tokens = correctedInput.toLowerCase().split("\\s+");
            double[] outcomes = categorizer.categorize(tokens);
            String[] categories = null;//categorizer.getCategories();

            String bestCategory = categorizer.getBestCategory(outcomes);
            double confidence = getMaxConfidence(outcomes);

            // Apply keyword boosting
            PartsIntentConfidencePair boosted = applyKeywordBoosting(correctedInput, bestCategory, confidence);

            // Apply threshold filtering
            PartsIntent finalIntent = applyThresholdFiltering(boosted.intent, boosted.confidence);

            // Extract entities
            String contractNumber = extractContractNumber(input);
            String partNumber = extractPartNumber(input);
            String customerName = extractCustomerName(correctedInput);
            String invoiceNumber = extractInvoiceNumber(input);

            String actionRequired = determineActionRequired(finalIntent, contractNumber, partNumber);

            return new PartsIntentResult(finalIntent, boosted.confidence, actionRequired, contractNumber, partNumber,
                                         customerName, invoiceNumber, createConfidenceMap(outcomes, categories));

        } catch (Exception e) {
            logger.severe("Error classifying parts intent: " + e.getMessage());
            return new PartsIntentResult(PartsIntent.UNKNOWN, 0.0, "error", null, null, null, null, new HashMap<>());
        }
    }

    private String correctTyposAndSynonyms(String input) {
        String corrected = input.toLowerCase();

        // Apply typo corrections
        for (Map.Entry<String, String> entry : partsTypoMap.entrySet()) {
            String typo = entry.getKey();
            String correct = entry.getValue();
            corrected = corrected.replaceAll("\\b" + typo + "\\b", correct);
        }

        // Apply synonym mapping
        for (Map.Entry<String, String> entry : partsSynonymMap.entrySet()) {
            String synonym = entry.getKey();
            String standard = entry.getValue();
            corrected = corrected.replaceAll("\\b" + synonym + "\\b", standard);
        }

        return corrected;
    }

    private double getMaxConfidence(double[] outcomes) {
        double max = 0.0;
        for (double outcome : outcomes) {
            if (outcome > max) {
                max = outcome;
            }
        }
        return max;
    }

    private PartsIntentConfidencePair applyKeywordBoosting(String input, String category, double confidence) {
        PartsIntent intent = mapCategoryToIntent(category);
        double boostedConfidence = confidence;

        // Keyword boosting rules
        if (input.contains("failed") || input.contains("error") || input.contains("rejected")) {
            if (intent == PartsIntent.SHOW_FAILED_PARTS || intent == PartsIntent.GET_ERROR_MESSAGES) {
                boostedConfidence += 0.3;
            }
        }

        if (input.contains("loaded") || input.contains("pending")) {
            if (intent == PartsIntent.SHOW_LOADED_PARTS) {
                boostedConfidence += 0.3;
            }
        }

        if (input.contains("final") || input.contains("processed") || input.contains("validated")) {
            if (intent == PartsIntent.SHOW_FINAL_PARTS) {
                boostedConfidence += 0.3;
            }
        }

        if (input.contains("message") || input.contains("reason")) {
            if (intent == PartsIntent.GET_ERROR_MESSAGES) {
                boostedConfidence += 0.4;
            }
        }

        // Part number pattern boost
        if (partNumberPattern.matcher(input).find()) {
            if (intent == PartsIntent.GET_PART_DETAILS) {
                boostedConfidence += 0.5;
            }
        }

        // Customer name boost
        if (containsCustomerName(input)) {
            if (intent == PartsIntent.SEARCH_PARTS_BY_CUSTOMER) {
                boostedConfidence += 0.4;
            }
        }

        return new PartsIntentConfidencePair(intent, boostedConfidence);
    }

    private PartsIntent mapCategoryToIntent(String category) {
        switch (category) {
        case "show_loaded_parts":
            return PartsIntent.SHOW_LOADED_PARTS;
        case "show_failed_parts":
            return PartsIntent.SHOW_FAILED_PARTS;
        case "show_final_parts":
            return PartsIntent.SHOW_FINAL_PARTS;
        case "show_all_parts":
            return PartsIntent.SHOW_ALL_PARTS;
        case "get_part_details":
            return PartsIntent.GET_PART_DETAILS;
        case "get_error_messages":
            return PartsIntent.GET_ERROR_MESSAGES;
        case "search_parts_by_contract":
            return PartsIntent.SEARCH_PARTS_BY_CONTRACT;
        case "search_parts_by_customer":
            return PartsIntent.SEARCH_PARTS_BY_CUSTOMER;
        case "get_validation_status":
            return PartsIntent.GET_VALIDATION_STATUS;
        case "list_parts_by_type":
            return PartsIntent.LIST_PARTS_BY_TYPE;
        default:
            return PartsIntent.UNKNOWN;
        }
    }

    private PartsIntent applyThresholdFiltering(PartsIntent intent, double confidence) {
        Double threshold = intentThresholds.get(intent);
        if (threshold != null && confidence < threshold) {
            return PartsIntent.UNKNOWN;
        }
        return intent;
    }

    private String extractContractNumber(String input) {
        java.util.regex.Matcher matcher = contractNumberPattern.matcher(input);
        return matcher.find() ? matcher.group() : null;
    }

    private String extractPartNumber(String input) {
        java.util.regex.Matcher matcher = partNumberPattern.matcher(input);
        return matcher.find() ? matcher.group() : null;
    }

    private String extractCustomerName(String input) {
        // Simple customer name extraction - looks for common names after "for" or standalone
        String[] commonNames = {
            "smith", "johnson", "williams", "brown", "jones", "garcia", "miller", "davis", "rodriguez", "martinez"
        };
        String lowerInput = input.toLowerCase();

        for (String name : commonNames) {
            if (lowerInput.contains(name)) {
                return name;
            }
        }
        return null;
    }

    private String extractInvoiceNumber(String input) {
        java.util.regex.Matcher matcher = invoicePattern.matcher(input);
        return matcher.find() ? matcher.group() : null;
    }

    private boolean containsCustomerName(String input) {
        return extractCustomerName(input) != null;
    }

    private String determineActionRequired(PartsIntent intent, String contractNumber, String partNumber) {
        switch (intent) {
        case SHOW_LOADED_PARTS:
            return contractNumber != null ? "show_loaded_parts_table" : "request_contract_number";
        case SHOW_FAILED_PARTS:
            return contractNumber != null ? "show_failed_parts_table" : "request_contract_number";
        case SHOW_FINAL_PARTS:
            return contractNumber != null ? "show_final_parts_table" : "request_contract_number";
        case SHOW_ALL_PARTS:
            return contractNumber != null ? "show_all_parts_table" : "request_contract_number";
        case GET_PART_DETAILS:
            return partNumber != null ? "show_part_details" : "request_part_number";
        case GET_ERROR_MESSAGES:
            return contractNumber != null ? "show_error_messages" : "request_contract_number";
        case SEARCH_PARTS_BY_CONTRACT:
            return contractNumber != null ? "search_parts_by_contract" : "request_contract_number";
        case SEARCH_PARTS_BY_CUSTOMER:
            return "search_parts_by_customer";
        case GET_VALIDATION_STATUS:
            return contractNumber != null ? "show_validation_status" : "request_contract_number";
        case LIST_PARTS_BY_TYPE:
            return "show_parts_type_selector";
        default:
            return "clarify_parts_intent";
        }
    }

    private Map<String, Double> createConfidenceMap(double[] outcomes, String[] categories) {
        Map<String, Double> confidenceMap = new HashMap<>();
        for (int i = 0; i < categories.length && i < outcomes.length; i++) {
            confidenceMap.put(categories[i], outcomes[i]);
        }
        return confidenceMap;
    }

    public void saveModel(String modelPath) throws IOException {
        if (model == null) {
            throw new IllegalStateException("No model to save. Train the model first.");
        }

        try (FileOutputStream fos = new FileOutputStream(modelPath)) {
            model.serialize(fos);
            logger.info("Parts intent classifier model saved to: " + new File(modelPath).getAbsolutePath());
        }
    }

    public void loadModel(String modelPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(modelPath)) {
            this.model = new DoccatModel(fis);
            this.categorizer = new DocumentCategorizerME(model);
            this.initialized = true;
            logger.info("Parts intent classifier model loaded from: " + modelPath);
        }
    }

    // Helper classes

    private static class PartsIntentConfidencePair {
        final PartsIntent intent;
        final double confidence;

        PartsIntentConfidencePair(PartsIntent intent, double confidence) {
            this.intent = intent;
            this.confidence = confidence;
        }
    }

    public static class PartsIntentResult {
        private final PartsIntent intent;
        private final double confidence;
        private final String actionRequired;
        private final String contractNumber;
        private final String partNumber;
        private final String customerName;
        private final String invoiceNumber;
        private final Map<String, Double> allConfidences;

        public PartsIntentResult(PartsIntent intent, double confidence, String actionRequired, String contractNumber,
                                 String partNumber, String customerName, String invoiceNumber,
                                 Map<String, Double> allConfidences) {
            this.intent = intent;
            this.confidence = confidence;
            this.actionRequired = actionRequired;
            this.contractNumber = contractNumber;
            this.partNumber = partNumber;
            this.customerName = customerName;
            this.invoiceNumber = invoiceNumber;
            this.allConfidences = allConfidences;
        }

        // Getters
        public PartsIntent getIntent() {
            return intent;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getActionRequired() {
            return actionRequired;
        }

        public String getContractNumber() {
            return contractNumber;
        }

        public String getPartNumber() {
            return partNumber;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public Map<String, Double> getAllConfidences() {
            return allConfidences;
        }

        @Override
        public String toString() {
            return String.format("Intent: %s, Confidence: %.2f, Action: %s, Contract: %s, Part: %s, Customer: %s",
                                 intent, confidence, actionRequired, contractNumber, partNumber, customerName);
        }
    }
}

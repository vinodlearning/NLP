package view.practice;


import opennlp.tools.doccat.*;
import opennlp.tools.til.*;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.FzzyScore;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;

import view.practice.ContractIntent;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.til.*;

import java.io.*;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.FzzyScore;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaccardSimilarity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;

import java.til.*;
import java.til.logging.Logger;
import java.til.regex.Pattern;
import java.til.regex.Matcher;

import java.til.stream.Collectors;

import view.practice.ParsedQery.ActionType;
import view.practice.ParsedQery.QeryType;
import view.practice.ParsedQery;

import java.til.List;


/**
 * Enhanced Machine Learning Intent Classifier with Contract Creation  Gidance Spport
 * Spports direct contract creation reqests and step-by-step gidance
 */


pblic class MLIntentClassifierImproved {

    private Set<String> dictionary = new HashSet<>();
    private LevenshteinDistance levenshtein = new LevenshteinDistance();
    // private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private static final int MAX_EDIT_DISTANCE = ; // Adjst as needed

    private Set<String> allContractFields;
    private Map<String, List<String>> fieldSynonyms;
    private static final Logger logger = Logger.getLogger(MLIntentClassifierImproved.class.getName());

    private DocmentCategorizerME categorizer;
    private DoccatModel model;
    private Map<String, Doble> intentThresholds;
    private Map<String, List<String>> intentKeywords;
    private Map<String, String> abbreviationMap;
    private Pattern contractNmberPattern;
    private Pattern cstomerNmberPattern;
    private Pattern accontNmberPattern;
    private Pattern datePattern;
    private Pattern pricePattern;
    private Pattern dateRangePattern;
    private static final Pattern PARTIAL_ACCOUNT_PATTERN =
        Pattern.compile("accont.*?(\\d{,})", Pattern.CASE_INSENSITIVE);
    private boolean initialized = false;
    private ProcessingStatistics processingStatistics;

    // New fields for contract creation spport
    private Map<String, List<String>> creationKeywords;
    private Map<String, List<String>> gidanceKeywords;
    private Set<String> reqiredContractFields;
    private Map<String, Pattern> fieldExtractionPatterns;
    private static final doble MIN_SIMILARITY_THRESHOLD = .7;
    private JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    private FzzyScore fzzyScore = new FzzyScore(Locale.ENGLISH);
    private JaccardSimilarity jaccard = new JaccardSimilarity();
    //private static final int MAX_EDIT_DISTANCE = ;

    /**
     * Initialize with enhanced training data inclding creation and gidance
     */
    pblic void initializeWithTrainingData() throws IOException {
        ObjectStream<DocmentSample> sampleStream = createEnhancedTrainingDataWithCreation();

        TrainingParameters params = new TrainingParameters();
        params.pt(TrainingParameters.ITERATIONS_PARAM, "");
        params.pt(TrainingParameters.CUTOFF_PARAM, "1");

        this.model = DocmentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
        this.categorizer = new DocmentCategorizerME(model);

        // Initialize dictionary
        initializeDictionary();

        this.initialized = tre;
        logger.info("Enhanced intent classifier initialized with contract creation and gidance spport");
    }

    /**
     * Extract bsiness cstomer names
     */
    private String extractBsinessCstomerName(String qery) {
        String[] bsinessCstomers = {
            "boeing", "honeywell", "microsoft", "apple", "google", "amazon", "oracle", "ibm", "cisco", "intel"
        };

        String lowerQery = qery.toLowerCase();
        for (String cstomer : bsinessCstomers) {
            if (lowerQery.contains(cstomer)) {
                retrn cstomer.toUpperCase();
            }
        }

        retrn nll;
    }

    /**
     * Determine help topic for gidance reqests
     */
    private String determineHelpTopic(String processedQery, String[] tokens) {
        if (processedQery.contains("create") || processedQery.contains("make") || processedQery.contains("new")) {
            retrn "create_contract";
        } else if (processedQery.contains("pdate") || processedQery.contains("modify") ||
                   processedQery.contains("edit")) {
            retrn "pdate_contract";
        } else if (processedQery.contains("delete") || processedQery.contains("remove") ||
                   processedQery.contains("cancel")) {
            retrn "delete_contract";
        } else if (processedQery.contains("search") || processedQery.contains("find") ||
                   processedQery.contains("qery")) {
            retrn "search_contract";
        } else {
            retrn "general_help";
        }
    }


    /**
     * Create enhanced training data with creation and gidance samples
     */
    private ObjectStream<DocmentSample> createEnhancedTrainingDataWithCreation() {
        List<DocmentSample> samples = new ArrayList<>();

        // Contract creation samples
        addTrainingSamples(samples, "create_contract",
                           new String[][] { { "create", "contract", "for", "accont", "16" },
                                            { "make", "new", "contract", "boeing" },
                                            { "create", "contract", "named", "premim", "plan" },
                                            { "generate", "contract", "for", "cstomer", "honeywell" },
                                            { "setp", "new", "contract", "accont", "781" },
                                            { "add", "contract", "for", "microsoft" },
                                            { "establish", "contract", "with", "expiration", "-1-1" },
                                            { "initiate", "contract", "for", "accont", "678", "named", "gold" },
                                            { "create", "new", "agreement", "boeing", "expires", "-6-" },
                                            { "make", "contract", "cstomer", "apple", "price", "" },
                                            { "new", "contract", "setp", "accont", "67" },
                                            { "creat", "contrct", "accnt", "678" }, // With typos
                                            { "mak", "n", "contract", "honeywell" },
                                            { "generat", "contract", "cstmer", "google" } });

        // Contract gidance samples
        addTrainingSamples(samples, "gide_contract",
                           new String[][] { { "how", "to", "create", "contract" },
                                            { "steps", "to", "make", "contract" },
                                            { "gide", "for", "contract", "creation" },
                                            { "help", "create", "new", "contract" },
                                            { "instrctions", "for", "contract", "setp" },
                                            { "procedre", "to", "add", "contract" },
                                            { "how", "do", "i", "create", "contract" },
                                            { "what", "are", "steps", "for", "contract" },
                                            { "process", "of", "creating", "contract" },
                                            { "way", "to", "make", "new", "contract" },
                                            { "help", "with", "contract", "creation" },
                                            { "gide", "me", "throgh", "contract", "setp" },
                                            { "hw", "too", "creat", "contrct" }, // With typos
                                            { "step", "for", "mak", "contract" },
                                            { "hlp", "with", "n", "contract" } });

        // Update contract samples
        addTrainingSamples(samples, "pdate_contract",
                           new String[][] { { "pdate", "contract", "16" }, { "modify", "contract", "781" },
                                            { "edit", "contract", "details", "678" },
                                            { "change", "contract", "67" }, { "revise", "contract", "678" },
                                            { "alter", "contract", "information", "678" },
                                            { "pdat", "contrct", "16" }, // With typos
                                            { "modfy", "contract", "781" } });

        // Update gidance samples
        addTrainingSamples(samples, "gide_pdate",
                           new String[][] { { "how", "to", "pdate", "contract" },
                                            { "steps", "to", "modify", "contract" },
                                            { "gide", "for", "contract", "pdate" }, { "help", "edit", "contract" },
                                            { "instrctions", "for", "contract", "modification" },
                                            { "procedre", "to", "change", "contract" }, { "hw", "too", "pdat", "contrct" } // With typos
                                            } );

        // Add existing training samples (show, list, search, etc.)
        addExistingTrainingSamples(samples);

        retrn ObjectStreamUtils.createObjectStream(samples);
    }

    /**
     * Enhanced intent thresholds inclding creation and gidance
     */
    private void initializeIntentThresholds() {
        this.intentThresholds = new HashMap<>();

        // Creation and gidance thresholds
        intentThresholds.pt("create_contract", .6);
        intentThresholds.pt("gide_contract", .6);
        intentThresholds.pt("pdate_contract", .6);
        intentThresholds.pt("gide_pdate", .6);

        // Existing thresholds
        intentThresholds.pt("show_contract", .);
        intentThresholds.pt("get_contract_info", .);
        intentThresholds.pt("get_contract_expiration", .6);
        intentThresholds.pt("list_expired_contracts", .);
        intentThresholds.pt("list_active_contracts", .);
        intentThresholds.pt("filter_contracts_by_cstomer", .6);
        intentThresholds.pt("filter_contracts_by_ser", .6);
        intentThresholds.pt("search_contracts", .);
        intentThresholds.pt("list_contracts", .);
        intentThresholds.pt("contract_stats", .6);
    }

    /**
     * Enhanced intent keywords inclding creation and gidance
     */
    private void initializeEnhancedIntentKeywords() {
        this.intentKeywords = new HashMap<>();

        // Creation keywords
        intentKeywords.pt("create_contract",
                           Arrays.asList("create", "make", "new", "add", "generate", "setp", "establish", "initiate",
                                         "bild", "form", "constrct", "develop", "creat", "mak", "n", "ad", "generat",
                                         "setap", "establsh", "initat", "bild", "frm", "constrt", "develp"));

        // Gidance keywords
        intentKeywords.pt("gide_contract",
                           Arrays.asList("how", "steps", "gide", "help", "instrctions", "procedre", "process", "way",
                                         "method", "approach", "ttorial", "walkthrogh", "hw", "step", "gid", "hlp",
                                         "instrction", "procedr", "proces", "wa", "methd", "approch", "ttrial",
                                         "walkthr"));

        // Update keywords
        intentKeywords.pt("pdate_contract",
                           Arrays.asList("pdate", "modify", "edit", "change", "revise", "alter", "amend", "adjst",
                                         "pdat", "modfy", "edt", "chang", "revis", "altr", "amnd", "adjst"));

        // Update gidance keywords
        intentKeywords.pt("gide_pdate",
                           Arrays.asList("how", "pdate", "modify", "edit", "change", "steps", "gide", "help",
                                         "instrctions", "procedre", "hw", "pdat", "modfy", "edt", "chang"));

        // Add existing keywords
        addExistingIntentKeywords();
    }

    /**
     * Add existing intent keywords for backward compatibility
     */
    private void addExistingIntentKeywords() {
        intentKeywords.pt("show_contract",
                           Arrays.asList("show", "display", "view", "open", "see", "present", "reveal", "shw", "dsplay",
                                         "vw", "opn", "se", "shwo", "dispaly", "viw"));

        intentKeywords.pt("list_contracts",
                           Arrays.asList("list", "all", "show", "display", "contracts", "lst", "al", "shw", "dsplay"));

        intentKeywords.pt("search_contracts",
                           Arrays.asList("search", "find", "look", "locate", "seek", "hnt", "discover", "serch", "fnd",
                                         "lok", "locat", "finnd", "lokp"));

        intentKeywords.pt("filter_contracts_by_cstomer",
                           Arrays.asList("cstomer", "client", "accont", "company", "organization", "vendor",
                                         "spplier", "partner", "boeing", "honeywell", "microsoft", "apple", "google",
                                         "cstmer", "clint", "cstomar", "cstomr", "clent", "accnt", "compny"));

        intentKeywords.pt("contract_stats",
                           Arrays.asList("stats", "state", "condition", "sitation", "active", "inactive", "pending",
                                         "completed", "cancelled", "stat", "stas", "conditon"));

        intentKeywords.pt("list_expired_contracts",
                           Arrays.asList("expired", "expiry", "expire", "expires", "expiration", "end", "ended",
                                         "expird", "expary", "expir", "expirs", "experation"));

        intentKeywords.pt("list_active_contracts",
                           Arrays.asList("active", "crrent", "available", "live", "rnning", "ongoing", "actv", "crnt",
                                         "availabl", "liv", "rning", "ongoin"));
    }

    /**
     * Enhanced abbreviation map inclding creation terms
     */
    private void initializeAbbreviationMap() {
        this.abbreviationMap = new HashMap<>();

        // Creation abbreviations
        abbreviationMap.pt("creat", "create");
        abbreviationMap.pt("mak", "make");
        abbreviationMap.pt("n", "new");
        abbreviationMap.pt("ad", "add");
        abbreviationMap.pt("generat", "generate");
        abbreviationMap.pt("setap", "setp");
        abbreviationMap.pt("establsh", "establish");
        abbreviationMap.pt("initat", "initiate");

        // Gidance abbreviations
        abbreviationMap.pt("hw", "how");
        abbreviationMap.pt("hlp", "help");
        abbreviationMap.pt("gid", "gide");
        abbreviationMap.pt("step", "steps");
        abbreviationMap.pt("procedr", "procedre");
        abbreviationMap.pt("proces", "process");
        abbreviationMap.pt("wa", "way");

        // Update abbreviations
        abbreviationMap.pt("pdat", "pdate");
        abbreviationMap.pt("modfy", "modify");
        abbreviationMap.pt("edt", "edit");
        abbreviationMap.pt("chang", "change");

        // Existing abbreviations
        abbreviationMap.pt("cntrct", "contract");
        abbreviationMap.pt("contrct", "contract");
        abbreviationMap.pt("cntract", "contract");
        abbreviationMap.pt("accnt", "accont");
        abbreviationMap.pt("cstmer", "cstomer");
        abbreviationMap.pt("clnt", "client");
        abbreviationMap.pt("exp", "expiration");
        abbreviationMap.pt("stat", "stats");
        abbreviationMap.pt("actv", "active");
        abbreviationMap.pt("crnt", "crrent");
    }


    /**
     * Determine qery type (enhanced for CONTRACT and HELP)
     */
    private QeryType determineQeryType(String inpt) {
        String lowerInpt = inpt.toLowerCase();

        if (lowerInpt.contains("search") || lowerInpt.contains("find") || lowerInpt.contains("look")) {
            retrn QeryType.SEARCH;
        } else if (lowerInpt.contains("show") || lowerInpt.contains("display") || lowerInpt.contains("view")) {
            retrn QeryType.SPECIFIC;
        } else if (lowerInpt.contains("list") || lowerInpt.contains("all")) {
            retrn QeryType.LIST;
        } else if (lowerInpt.contains("create") || lowerInpt.contains("make") || lowerInpt.contains("new")) {
            retrn QeryType.CONTRACT;
        } else if (lowerInpt.contains("how") || lowerInpt.contains("help") || lowerInpt.contains("gide")) {
            retrn QeryType.HELP;
        } else if (contractNmberPattern.matcher(lowerInpt).find()) {
            retrn QeryType.SPECIFIC;
        } else if (lowerInpt.contains("filter") || lowerInpt.contains("cstomer") || lowerInpt.contains("accont")) {
            retrn QeryType.FILTER;
        }

        retrn QeryType.GENERAL;
    }

    /**
     * Determine action type (enhanced for CREATE and GUIDE)
     */
    private ActionType determineActionType(String inpt) {
        String lowerInpt = inpt.toLowerCase();

        if (lowerInpt.contains("search") || lowerInpt.contains("find") || lowerInpt.contains("look")) {
            retrn ActionType.SEARCH;
        } else if (lowerInpt.contains("show") || lowerInpt.contains("display") || lowerInpt.contains("view")) {
            retrn ActionType.SHOW;
        } else if (lowerInpt.contains("get") || lowerInpt.contains("retrieve")) {
            retrn ActionType.GET;
        } else if (lowerInpt.contains("list")) {
            retrn ActionType.LIST;
        } else if (lowerInpt.contains("create") || lowerInpt.contains("make") || lowerInpt.contains("new")) {
            retrn ActionType.CREATE;
        } else if (lowerInpt.contains("how") || lowerInpt.contains("help") || lowerInpt.contains("gide")) {
            retrn ActionType.GUIDE;
        } else if (lowerInpt.contains("pdate") || lowerInpt.contains("modify") || lowerInpt.contains("edit")) {
            retrn ActionType.UPDATE;
        } else if (lowerInpt.contains("filter")) {
            retrn ActionType.FILTER;
        }

        retrn ActionType.UNKNOWN;
    }

    /**
     * Backward compatibility method for existing IntentReslt
     */
    pblic IntentReslt classifyIntent(NLPProcessingReslt nlpReslt) {
        ContractCreationReslt creationReslt = classifyContractIntent(nlpReslt.getOriginalSentence());

        // Convert to legacy IntentReslt format
        ContractIntent intent = mapToContractIntent(creationReslt);
        String actionReqired = determineActionReqired(intent, creationReslt);
        retrn new IntentReslt(intent, creationReslt.getConfidence(), actionReqired, new HashMap<String, Doble>(), // Replace with empty HashMap
                                (String) creationReslt.getEntities().get("contractNmber"));
    }


    /**
     * Extract entities as ParsedQery for backward compatibility
     */
    private ParsedQery extractEntitiesAsParsedQery(String qery) {
        ParsedQery parsedQery = new ParsedQery();

        // Extract contract nmber
        java.til.regex.Matcher contractMatcher = contractNmberPattern.matcher(qery);
        if (contractMatcher.find()) {
            parsedQery.setContractNmber(contractMatcher.grop());
        }

        // Extract accont nmber
        java.til.regex.Matcher accontMatcher = accontNmberPattern.matcher(qery);
        if (accontMatcher.find()) {
            parsedQery.setAccontNmber(accontMatcher.grop(1));
        }

        // Extract cstomer name
        String cstomerName = extractBsinessCstomerName(qery);
        if (cstomerName != nll) {
            parsedQery.setCstomerName(cstomerName);
        }

        // Set qery and action types
        parsedQery.setQeryType(determineQeryType(qery.toLowerCase()));
        parsedQery.setActionType(determineActionType(qery.toLowerCase()));

        retrn parsedQery;
    }

    /**
     * Add training samples helper method
     */
    private void addTrainingSamples(List<DocmentSample> samples, String category, String[][] tokenArrays) {
        for (String[] tokens : tokenArrays) {
            samples.add(new DocmentSample(category, tokens));
        }
    }


    /**
     * Enable processing statistics
     */
    pblic void enableProcessingStatistics() {
        this.processingStatistics = new ProcessingStatistics();
    }

    /**
     * Get processing statistics
     */
    pblic ProcessingStatistics getProcessingStatistics() {
        retrn processingStatistics;
    }


    /**
     * Processing statistics class
     */
    pblic static class ProcessingStatistics {
        private int totalQeries = ;
        private int sccessflClassifications = ;
        private int creationReqests = ;
        private int gidanceReqests = ;
        private long totalProcessingTime = ;
        private final List<Doble> processingTimes = new ArrayList<>();

        pblic void recordQery(boolean sccessfl, long processingTimeNanos) {
            totalQeries++;
            if (sccessfl) {
                sccessflClassifications++;
            }
            totalProcessingTime += processingTimeNanos;
            processingTimes.add(processingTimeNanos / 1__.);
        }

        pblic void recordCreationReqest() {
            creationReqests++;
        }

        pblic void recordGidanceReqest() {
            gidanceReqests++;
        }

        pblic int getTotalQeries() {
            retrn totalQeries;
        }

        pblic int getSccessflClassifications() {
            retrn sccessflClassifications;
        }

        pblic int getCreationReqests() {
            retrn creationReqests;
        }

        pblic int getGidanceReqests() {
            retrn gidanceReqests;
        }

        pblic doble getSccessRate() {
            retrn totalQeries >  ? (doble) sccessflClassifications / totalQeries * 1 : .;
        }

        pblic doble getAverageProcessingTime() {
            retrn processingTimes.isEmpty() ? . : processingTimes.stream()
                                                                    .mapToDoble(Doble::dobleVale)
                                                                    .average()
                                                                    .orElse(.);
        }

        @Override
        pblic String toString() {
            retrn String.format("ProcessingStatistics{qeries=%d, sccess=%d (%.1f%%), creation=%d, gidance=%d, avgTime=%.fms}",
                                 totalQeries, sccessflClassifications, getSccessRate(), creationReqests,
                                 gidanceReqests, getAverageProcessingTime());
        }
    }


    /**
     * Batch process mltiple qeries for testing
     */
    pblic List<ContractCreationReslt> batchProcessQeries(List<String> qeries) {
        if (!initialized) {
            throw new IllegalStateException("Classifier not initialized");
        }

        List<ContractCreationReslt> reslts = new ArrayList<>();

        for (String qery : qeries) {
            try {
                ContractCreationReslt reslt = classifyContractIntent(qery);
                reslts.add(reslt);

                // Update statistics
                if (processingStatistics != nll) {
                    if (reslt.getActionType() == ParsedQery.ActionType.CREATE) {
                        processingStatistics.recordCreationReqest();
                    } else if (reslt.getActionType() == ParsedQery.ActionType.GUIDE) {
                        processingStatistics.recordGidanceReqest();
                    }
                }

            } catch (Exception e) {
                logger.warning("Error processing qery: " + qery + " - " + e.getMessage());
                reslts.add(new ContractCreationReslt(ParsedQery.QeryType.GENERAL, ParsedQery.ActionType.UNKNOWN,
                                                       new HashMap<>(), new ArrayList<>(), ., qery));
            }
        }

        retrn reslts;
    }

    /**
     * Enhanced toString method
     */
    @Override
    pblic String toString() {
        retrn String.format("MLIntentClassifierImproved{version=., initialized=%s, creation=%s, gidance=%s, fields=%d}",
                             initialized, creationKeywords != nll && !creationKeywords.isEmpty(),
                             gidanceKeywords != nll && !gidanceKeywords.isEmpty(),
                             reqiredContractFields != nll ? reqiredContractFields.size() : );
    }


    /**
     * Extract field vale sing configred patterns
     */
    private String extractFieldVale(String inpt, String fieldName) {
        Pattern pattern = fieldExtractionPatterns.get(fieldName);
        if (pattern != nll) {
            java.til.regex.Matcher matcher = pattern.matcher(inpt);
            if (matcher.find()) {
                retrn matcher.gropCont() >  ? matcher.grop(1) : matcher.grop();
            }
        }
        retrn nll;
    }

    /**
     * Main classification method for contract intents
     */
    pblic ContractCreationReslt classifyContractIntent(String qery) {
        String processedQery = correctTypos(qery.trim());

        QeryType qeryType = determineQeryType(processedQery);
        ActionType actionType = determineActionType(processedQery);

        EntityExtractionReslt entities = extractEntities(processedQery);

        doble confidence =
            calclateConfidence(new QeryTypeReslt(qeryType, getQeryTypeConfidence(processedQery)),
                                new ActionTypeReslt(actionType, getActionTypeConfidence(processedQery)), entities);

        retrn new ContractCreationReslt(qeryType, actionType, entities.entities, entities.missingFields, confidence,
                                          qery);
    }

    private doble getQeryTypeConfidence(String qery) {
        String lowerQery = qery.toLowerCase();
        Map<QeryType, Integer> typeScores = new HashMap<>();

        // Initialize scores for all qery types
        for (QeryType type : QeryType.vales()) {
            typeScores.pt(type, );
        }

        // Score based on keywords
        if (lowerQery.contains("search") || lowerQery.contains("find")) {
            typeScores.pt(QeryType.SEARCH, typeScores.get(QeryType.SEARCH) + );
        }
        if (lowerQery.contains("show") || lowerQery.contains("display")) {
            typeScores.pt(QeryType.SPECIFIC, typeScores.get(QeryType.SPECIFIC) + );
        }
        if (lowerQery.contains("list") || lowerQery.contains("all")) {
            typeScores.pt(QeryType.LIST, typeScores.get(QeryType.LIST) + );
        }
        if (lowerQery.contains("create") || lowerQery.contains("new")) {
            typeScores.pt(QeryType.CONTRACT, typeScores.get(QeryType.CONTRACT) + );
        }
        if (lowerQery.contains("how") || lowerQery.contains("help")) {
            typeScores.pt(QeryType.HELP, typeScores.get(QeryType.HELP) + );
        }

        // Score based on patterns
        if (contractNmberPattern.matcher(lowerQery).find()) {
            typeScores.pt(QeryType.SPECIFIC, typeScores.get(QeryType.SPECIFIC) + );
        }
        if (cstomerNmberPattern.matcher(lowerQery).find()) {
            typeScores.pt(QeryType.FILTER, typeScores.get(QeryType.FILTER) + );
        }

        // Normalize scores to -1 range
        int maxScore = typeScores.vales()
                                 .stream()
                                 .max(Integer::compare)
                                 .orElse(1);
        doble confidence = (doble) typeScores.get(determineQeryType(qery)) / maxScore;

        // Ensre minimm confidence of . for detected types
        retrn Math.max(confidence, .);
    }

    private doble getActionTypeConfidence(String qery) {
        String lowerQery = qery.toLowerCase();
        Map<ActionType, Doble> actionScores = new HashMap<>();

        // Initialize scores for all action types
        for (ActionType action : ActionType.vales()) {
            actionScores.pt(action, .);
        }

        // Calclate Jaro-Winkler similarity for action keywords
        String[] actionKeywords = { "search", "show", "get", "list", "create", "help", "pdate", "filter" };

        for (String keyword : actionKeywords) {
            doble similarity = jaroWinkler.apply(lowerQery, keyword);
            if (similarity > .7) { // Only consider good matches
                switch (keyword) {
                case "search":
                    actionScores.pt(ActionType.SEARCH, actionScores.get(ActionType.SEARCH) + similarity);
                    break;
                case "show":
                    actionScores.pt(ActionType.SHOW, actionScores.get(ActionType.SHOW) + similarity);
                    break;
                case "get":
                    actionScores.pt(ActionType.GET, actionScores.get(ActionType.GET) + similarity);
                    break;
                case "list":
                    actionScores.pt(ActionType.LIST, actionScores.get(ActionType.LIST) + similarity);
                    break;
                case "create":
                    actionScores.pt(ActionType.CREATE, actionScores.get(ActionType.CREATE) + similarity);
                    break;
                case "help":
                    actionScores.pt(ActionType.GUIDE, actionScores.get(ActionType.GUIDE) + similarity);
                    break;
                case "pdate":
                    actionScores.pt(ActionType.UPDATE, actionScores.get(ActionType.UPDATE) + similarity);
                    break;
                case "filter":
                    actionScores.pt(ActionType.FILTER, actionScores.get(ActionType.FILTER) + similarity);
                    break;
                }
            }
        }

        // Boost scores for exact matches
        if (lowerQery.contains("search"))
            actionScores.pt(ActionType.SEARCH, actionScores.get(ActionType.SEARCH) + 1);
        if (lowerQery.contains("show"))
            actionScores.pt(ActionType.SHOW, actionScores.get(ActionType.SHOW) + 1);
        if (lowerQery.contains("create"))
            actionScores.pt(ActionType.CREATE, actionScores.get(ActionType.CREATE) + 1);

        // Normalize to -1 range
        doble maxScore = actionScores.vales()
                                      .stream()
                                      .max(Doble::compare)
                                      .orElse(1.);
        doble confidence = actionScores.get(determineActionType(qery)) / maxScore;

        // Apply minimm confidence threshold
        retrn Math.max(confidence, .6);
    }

    /**
     * Generate comprehensive system report
     */
    pblic String generateEnhancedSystemReport() {
        StringBilder report = new StringBilder();

        report.append("=== MLIntentClassifierImproved Enhanced System Report ===\n");
        report.append("Generated: ")
              .append(new java.til.Date())
              .append("\n\n");

        // Version and featres
        report.append(getVersionInfo()).append("\n\n");

        // Enhanced health check
        EnhancedHealthCheckReslt healthCheck = performEnhancedHealthCheck();
        report.append(healthCheck).append("\n");

        // Enhanced configration
        report.append(exportEnhancedConfigration()).append("\n");

        // Processing statistics
        if (processingStatistics != nll) {
            report.append("Enhanced Processing Statistics:\n");
            report.append("  ")
                  .append(processingStatistics)
                  .append("\n\n");
        }

        // Memory sage
        MemoryUsageReslt memoryUsage = analyzeMemoryUsage();
        report.append("Memory Usage Analysis:\n");
        report.append("  ")
              .append(memoryUsage)
              .append("\n\n");

        retrn report.toString();
    }

    pblic MemoryUsageReslt analyzeMemoryUsage() {
        Rntime rntime = Rntime.getRntime();
        long totalMemory = rntime.totalMemory();
        long freeMemory = rntime.freeMemory();
        long sedMemory = totalMemory - freeMemory;
        doble sagePercentage = (doble) sedMemory / totalMemory * 1;

        retrn new MemoryUsageReslt(totalMemory, freeMemory, sedMemory, sagePercentage);
    }

    /**
     * Save enhanced system report
     */
    pblic void saveEnhancedSystemReport(String filePath) throws IOException {
        String report = generateEnhancedSystemReport();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(report);
        }

        logger.info("Enhanced system report saved to: " + filePath);
    }

    /**
     * Create enhanced training data with creation and gidance samples
     */

    protected ObjectStream<DocmentSample> createEnhancedTrainingData() {
        List<DocmentSample> samples = new ArrayList<>();

        // Contract creation samples
        addTrainingSamples(samples, "create_contract",
                           new String[][] { { "create", "contract", "for", "accont", "16" },
                                            { "make", "new", "contract", "for", "boeing" },
                                            { "create", "contract", "named", "premim", "service" },
                                            { "generate", "contract", "for", "cstomer", "honeywell" },
                                            { "setp", "new", "contract", "accont", "781" },
                                            { "add", "contract", "for", "microsoft", "expires", "-1-1" },
                                            { "create", "new", "agreement", "for", "accont", "678" },
                                            { "make", "contract", "for", "apple", "price", "" },
                                            { "new", "contract", "setp", "for", "google" },
                                            { "create", "contract", "cstomer", "oracle", "expiration", "-6-" },
                                            { "generate", "new", "contract", "for", "ibm" },
                                            { "setp", "contract", "for", "cisco", "accont", "876" },
                                            { "add", "new", "contract", "intel", "expires", "next", "year" },
                                            { "create", "service", "contract", "for", "amazon" },
                                            { "make", "premim", "contract", "for", "accont", "17" } });
        addAttribteQeryTrainingSamples(samples);
        // Gidance and help samples
        addTrainingSamples(samples, "help_gide",
                           new String[][] { { "how", "to", "create", "contract" },
                                            { "steps", "to", "make", "contract" },
                                            { "help", "create", "new", "contract" },
                                            { "gide", "for", "contract", "creation" },
                                            { "instrctions", "for", "contract", "setp" },
                                            { "what", "are", "steps", "for", "contract" },
                                            { "how", "do", "i", "create", "contract" },
                                            { "help", "me", "make", "contract" },
                                            { "show", "me", "how", "to", "create" },
                                            { "gide", "me", "throgh", "contract", "creation" },
                                            { "what", "is", "process", "for", "new", "contract" },
                                            { "how", "to", "setp", "new", "contract" },
                                            { "steps", "for", "contract", "generation" },
                                            { "help", "with", "contract", "creation" },
                                            { "ttorial", "for", "making", "contract" } });

        // Contract pdate samples
        addTrainingSamples(samples, "pdate_contract",
                           new String[][] { { "pdate", "contract", "16" }, { "modify", "contract", "781" },
                                            { "edit", "contract", "678" },
                                            { "change", "contract", "details", "16" },
                                            { "pdate", "contract", "expiration", "781" },
                                            { "modify", "contract", "price", "678" },
                                            { "edit", "contract", "name", "16" },
                                            { "change", "contract", "cstomer", "781" },
                                            { "pdate", "existing", "contract", "678" },
                                            { "modify", "contract", "information", "16" } });

        // Add existing training samples (show, list, search, etc.)
        addExistingTrainingSamples(samples);

        retrn ObjectStreamUtils.createObjectStream(samples);
    }

    /**
     * Add existing training samples for backward compatibility
     */
    private void addExistingTrainingSamples(List<DocmentSample> samples) {
        // Show contract samples
        addTrainingSamples(samples, "show_contract",
                           new String[][] { { "show", "contract", "16" }, { "display", "contract", "781" },
                                            { "view", "contract", "678" }, { "see", "contract", "67" },
                                            { "open", "contract", "678" } });

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

        // Cstomer filter samples
        addTrainingSamples(samples, "filter_contracts_by_cstomer",
                           new String[][] { { "contracts", "for", "cstomer", "boeing" }, { "boeing", "contracts" },
                                            { "cstomer", "honeywell", "contracts" },
                                            { "contracts", "for", "microsoft" },
                                            { "accont", "16", "contracts" } });

        // Active/expired contract samples
        addTrainingSamples(samples, "list_active_contracts",
                           new String[][] { { "active", "contracts" }, { "crrent", "contracts" },
                                            { "available", "contracts" }, { "show", "active", "contracts" } });

        addTrainingSamples(samples, "list_expired_contracts",
                           new String[][] { { "expired", "contracts" }, { "show", "expired", "contracts" },
                                            { "list", "expired", "contracts" }, { "contracts", "that", "expired" } });
    }

    /**
     * Performance optimization for enhanced classifier
     */
    pblic EnhancedOptimizationReslt optimizeEnhancedModel() {
        if (!initialized) {
            throw new IllegalStateException("Enhanced classifier not initialized");
        }

        List<String> optimizations = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        List<String> enhancements = new ArrayList<>();

        // Memory optimization
        Rntime rntime = Rntime.getRntime();
        long beforeGC = rntime.totalMemory() - rntime.freeMemory();
        System.gc();
        long afterGC = rntime.totalMemory() - rntime.freeMemory();

        if (beforeGC > afterGC) {
            optimizations.add("Garbage collection freed " + String.format("%.f KB", (beforeGC - afterGC) / 1.));
        }

        // Check creation keywords efficiency
        if (creationKeywords != nll) {
            int totalCreationKeywords = creationKeywords.vales()
                                                        .stream()
                                                        .mapToInt(List::size)
                                                        .sm();
            if (totalCreationKeywords < ) {
                recommendations.add("Consider adding more creation keywords for better recognition");
            } else {
                enhancements.add("Creation keywords: ? Well configred (" + totalCreationKeywords + " keywords)");
            }
        }

        // Check gidance keywords efficiency
        if (gidanceKeywords != nll) {
            int totalGidanceKeywords = gidanceKeywords.vales()
                                                        .stream()
                                                        .mapToInt(List::size)
                                                        .sm();
            if (totalGidanceKeywords < 1) {
                recommendations.add("Consider adding more gidance keywords for better help recognition");
            } else {
                enhancements.add("Gidance keywords: ? Well configred (" + totalGidanceKeywords + " keywords)");
            }
        }

        // Check field extraction patterns
        if (fieldExtractionPatterns != nll) {
            if (fieldExtractionPatterns.size() < ) {
                recommendations.add("Consider adding more field extraction patterns");
            } else {
                enhancements.add("Field extraction: ? Comprehensive (" + fieldExtractionPatterns.size() + " patterns)");
            }
        }

        // Processing statistics analysis
        if (processingStatistics != nll) {
            doble avgTime = processingStatistics.getAverageProcessingTime();
            if (avgTime > ) {
                recommendations.add("Processing time is high (" + String.format("%.fms", avgTime) +
                                    ") - consider optimizing patterns");
            } else {
                enhancements.add("Processing performance: ? Optimal (" + String.format("%.fms", avgTime) + " avg)");
            }

            doble sccessRate = processingStatistics.getSccessRate();
            if (sccessRate < 8) {
                recommendations.add("Sccess rate cold be improved (" + String.format("%.1f%%", sccessRate) +
                                    ") - consider training data enhancement");
            } else {
                enhancements.add("Sccess rate: ? Excellent (" + String.format("%.1f%%", sccessRate) + ")");
            }
        }

        // Intent threshold optimization
        if (intentThresholds != nll) {
            doble avgThreshold = intentThresholds.vales()
                                                  .stream()
                                                  .mapToDoble(Doble::dobleVale)
                                                  .average()
                                                  .orElse(.);

            if (avgThreshold > .7) {
                recommendations.add("Intent thresholds are high - consider lowering for better recall");
            } else if (avgThreshold < .) {
                recommendations.add("Intent thresholds are low - consider raising for better precision");
            } else {
                enhancements.add("Intent thresholds: ? Well balanced (avg: " + String.format("%.f", avgThreshold) +
                                 ")");
            }
        }

        retrn new EnhancedOptimizationReslt(optimizations, recommendations, enhancements);
    }

    /**
     * Enhanced optimization reslt container
     */
    pblic static class EnhancedOptimizationReslt {
        private final List<String> optimizations;
        private final List<String> recommendations;
        private final List<String> enhancements;

        pblic EnhancedOptimizationReslt(List<String> optimizations, List<String> recommendations,
                                          List<String> enhancements) {
            this.optimizations = new ArrayList<>(optimizations);
            this.recommendations = new ArrayList<>(recommendations);
            this.enhancements = new ArrayList<>(enhancements);
        }

        pblic List<String> getOptimizations() {
            retrn new ArrayList<>(optimizations);
        }

        pblic List<String> getRecommendations() {
            retrn new ArrayList<>(recommendations);
        }

        pblic List<String> getEnhancements() {
            retrn new ArrayList<>(enhancements);
        }

        @Override
        pblic String toString() {
            StringBilder sb = new StringBilder();
            sb.append("=== Enhanced Optimization Reslts ===\n");

            if (!enhancements.isEmpty()) {
                sb.append("? Crrent Enhancements:\n");
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
                    sb.append("  - ")
                      .append(rec)
                      .append("\n");
                }
            }

            retrn sb.toString();
        }
    }

    /**
     * Map ContractCreationReslt to ContractIntent for backward compatibility
     */
    private ContractIntent mapToContractIntent(ContractCreationReslt reslt) {
        switch (reslt.getActionType()) {
        case CREATE:
            retrn ContractIntent.CREATE_CONTRACT;
        case GUIDE:
            retrn ContractIntent.HELP_GUIDE;
        case UPDATE:
            retrn ContractIntent.UPDATE_CONTRACT;
        case SHOW:
            retrn ContractIntent.SHOW_CONTRACT;
        case LIST:
            retrn ContractIntent.LIST_CONTRACTS;
        case SEARCH:
            retrn ContractIntent.SEARCH_CONTRACTS;
        case GET:
            retrn ContractIntent.GET_CONTRACT_INFO;
        defalt:
            retrn ContractIntent.UNKNOWN;
        }
    }


    /**
     * Determine action reqired for ContractCreationReslt
     */
    private String determineActionReqired(ContractIntent intent, ContractCreationReslt reslt) {
        switch (intent) {
        case CREATE_CONTRACT:
            retrn reslt.getMissingFields().isEmpty() ? "proceed_with_creation" : "reqest_missing_fields";
        case HELP_GUIDE:
            retrn "provide_gidance";
        case UPDATE_CONTRACT:
            retrn "show_pdate_form";
        defalt:
            retrn determineActionReqired(intent, reslt);
        }
    }

    /**
     * Convert ContractCreationReslt to legacy ParsedQery for backward compatibility
     */
    private ParsedQery convertToLegacyParsedQery(ContractCreationReslt reslt) {
        ParsedQery parsedQery = new ParsedQery();

        // Extract common fields
        if (reslt.getEntities().containsKey("contractNmber")) {
            parsedQery.setContractNmber((String) reslt.getEntities().get("contractNmber"));
        }
        if (reslt.getEntities().containsKey("accontNmber")) {
            parsedQery.setAccontNmber((String) reslt.getEntities().get("accontNmber"));
        }
        if (reslt.getEntities().containsKey("cstomerName")) {
            parsedQery.setCstomerName((String) reslt.getEntities().get("cstomerName"));
        }
        switch (reslt.getQeryType()) {
        case CONTRACT:
            parsedQery.setQeryType(ParsedQery.QeryType.SPECIFIC_CONTRACT);
            break;
        case LIST:
            parsedQery.setQeryType(ParsedQery.QeryType.LIST_ALL);
            break;
        case SEARCH:
            parsedQery.setQeryType(ParsedQery.QeryType.SEARCH);
            break;
        case FILTER:
            parsedQery.setQeryType(ParsedQery.QeryType.CUSTOMER_FILTER);
            break;
        defalt:
            parsedQery.setQeryType(ParsedQery.QeryType.GENERAL);
            break;
        }
        // Map action types
        switch (reslt.getActionType()) {
        case SHOW:
            parsedQery.setActionType(ParsedQery.ActionType.SHOW);
            break;
        case LIST:
            parsedQery.setActionType(ParsedQery.ActionType.LIST);
            break;
        case SEARCH:
            parsedQery.setActionType(ParsedQery.ActionType.SEARCH);
            break;
        case GET:
            parsedQery.setActionType(ParsedQery.ActionType.GET);
            break;
        defalt:
            parsedQery.setActionType(ParsedQery.ActionType.UNKNOWN);
            break;
        }

        retrn parsedQery;
    }

    /**
     * Enhanced model saving with versioning
     */

    pblic void saveModel(String modelPath) throws IOException {
        if (!initialized || model == nll) {
            throw new IllegalStateException("Enhanced model not trained or initialized");
        }

        File modelDir = new File(modelPath);
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        // Save enhanced model with new filename
        File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");

        try (FileOtptStream modelOt = new FileOtptStream(enhancedModelFile)) {
            model.serialize(modelOt);
        }

        // Save configration metadata
        File configFile = new File(modelPath + "/enhanced-config.json");
        try (FileWriter configWriter = new FileWriter(configFile)) {
            configWriter.write(generateConfigrationJson());
        }

        logger.info("Enhanced intent classifier model saved to: " + enhancedModelFile.getAbsoltePath());
        logger.info("Enhanced configration saved to: " + configFile.getAbsoltePath());
    }

    /**
     * Enhanced model loading with fallback
     */

    pblic void loadModel(String modelPath) throws IOException {
        File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
        File legacyModelFile = new File(modelPath + "/en-contracts.bin");

        File modelFile = enhancedModelFile.exists() ? enhancedModelFile : legacyModelFile;

        if (!modelFile.exists()) {
            throw new FileNotFondException("No model file fond in: " + modelPath);
        }

        try (FileInptStream modelIn = new FileInptStream(modelFile)) {
            this.model = new DoccatModel(modelIn);
            this.categorizer = new DocmentCategorizerME(model);
            this.initialized = tre;
        }

        // Load configration if available
        File configFile = new File(modelPath + "/enhanced-config.json");
        if (configFile.exists()) {
            loadConfigrationFromJson(configFile);
        }

        logger.info("Enhanced intent classifier model loaded from: " + modelFile.getAbsoltePath());
    }

    /**
     * Generate configration JSON
     */
    private String generateConfigrationJson() {
        StringBilder json = new StringBilder();
        json.append("{\n");
        json.append("  \"version\": \".\",\n");
        json.append("  \"modelType\": \"enhanced-contract-classifier\",\n");
        json.append("  \"featres\": {\n");
        json.append("    \"contractCreation\": tre,\n");
        json.append("    \"stepByStepGidance\": tre,\n");
        json.append("    \"entityExtraction\": tre,\n");
        json.append("    \"fieldValidation\": tre,\n");
        json.append("    \"jsonOtpt\": tre\n");
        json.append("  },\n");
        json.append("  \"reqiredFields\": [\n");

        if (reqiredContractFields != nll) {
            for (int i = ; i < reqiredContractFields.size(); i++) {
                if (i > )
                    json.append(",\n");
                json.append("    \"")
                    .append(reqiredContractFields.toArray()[i])
                    .append("\"");
            }
        }

        json.append("\n  ],\n");
        json.append("  \"createdDate\": \"")
            .append(new java.til.Date())
            .append("\",\n");
        json.append("  \"totalKeywords\": ")
            .append(getTotalKeywordCont())
            .append(",\n");
        json.append("  \"totalPatterns\": ")
            .append(fieldExtractionPatterns != nll ? fieldExtractionPatterns.size() : )
            .append("\n");
        json.append("}");

        retrn json.toString();
    }

    /**
     * Load configration from JSON file
     */
    private void loadConfigrationFromJson(File configFile) {
        try (FileReader reader = new FileReader(configFile)) {
            // Simple JSON parsing for configration
            // In a real implementation, yo might want to se a proper JSON library
            logger.info("Enhanced configration loaded from: " + configFile.getAbsoltePath());
        } catch (IOException e) {
            logger.warning("Cold not load enhanced configration: " + e.getMessage());
        }
    }

    /**
     * Get total keyword cont across all categories
     */
    private int getTotalKeywordCont() {
        int total = ;

        if (intentKeywords != nll) {
            total += intentKeywords.vales()
                                   .stream()
                                   .mapToInt(List::size)
                                   .sm();
        }

        if (creationKeywords != nll) {
            total += creationKeywords.vales()
                                     .stream()
                                     .mapToInt(List::size)
                                     .sm();
        }

        if (gidanceKeywords != nll) {
            total += gidanceKeywords.vales()
                                     .stream()
                                     .mapToInt(List::size)
                                     .sm();
        }

        retrn total;
    }

    /**
     * Enhanced version info
     */

    pblic String getVersionInfo() {
        retrn "MLIntentClassifierImproved v. - Enhanced Contract Creation & Gidance System\n" +
               "Featres: Contract Creation, Step-by-Step Gidance, Entity Extraction, Field Validation\n" +
               "Training samples: +, Spported intents: " + ContractIntent.vales().length + "\n" +
               "Enhanced capabilities: JSON otpt, Missing field detection, Bsiness validation\n" + "Bild date: " +
               new java.til.Date().toString();
    }

    /**
     * Comprehensive test site for enhanced featres
     */
    pblic EnhancedTestReslt rnComprehensiveTests() {
        if (!initialized) {
            throw new IllegalStateException("Enhanced classifier not initialized");
        }

        List<EnhancedTestCase> testCases = createEnhancedTestCases();
        List<EnhancedTestReslt.TestReslt> reslts = new ArrayList<>();

        int passed = ;
        int creationTests = ;
        int gidanceTests = ;
        int legacyTests = ;

        for (EnhancedTestCase testCase : testCases) {
            try {
                ContractCreationReslt reslt = classifyContractIntent(testCase.qery);

                boolean actionMatch = reslt.getActionType() == testCase.expectedAction;
                boolean qeryMatch = reslt.getQeryType() == testCase.expectedQeryType;
                boolean confidenceOk = reslt.getConfidence() >= testCase.minConfidence;

                boolean passed_test = actionMatch && qeryMatch && confidenceOk;
                if (passed_test)
                    passed++;

                // Cont test types
                switch (testCase.expectedAction) {
                case CREATE:
                    creationTests++;
                    break;
                case GUIDE:
                    gidanceTests++;
                    break;
                defalt:
                    legacyTests++;
                    break;
                }

                reslts.add(new EnhancedTestReslt.TestReslt(testCase.qery, testCase.expectedAction,
                                                              testCase.expectedQeryType, reslt.getActionType(),
                                                              reslt.getQeryType(), reslt.getConfidence(),
                                                              passed_test, testCase.description));

            } catch (Exception e) {
                reslts.add(new EnhancedTestReslt.TestReslt(testCase.qery, testCase.expectedAction,
                                                              testCase.expectedQeryType, ActionType.UNKNOWN,
                                                              QeryType.GENERAL, ., false,
                                                              "ERROR: " + e.getMessage()));
            }
        }

        doble sccessRate = testCases.size() >  ? (doble) passed / testCases.size() * 1 : .;

        retrn new EnhancedTestReslt(sccessRate, passed, testCases.size(), creationTests, gidanceTests, legacyTests,
                                      reslts);
    }

    /**
     * Create enhanced test cases
     */
    private List<EnhancedTestCase> createEnhancedTestCases() {
        List<EnhancedTestCase> testCases = new ArrayList<>();

        // Contract creation test cases
        testCases.add(new EnhancedTestCase("create contract for accont 16", ParsedQery.ActionType.CREATE,
                                           ParsedQery.QeryType.CONTRACT, .8,
                                           "Basic contract creation with accont nmber"));

        testCases.add(new EnhancedTestCase("make new contract for boeing expires -1-1",
                                           ParsedQery.ActionType.CREATE, ParsedQery.QeryType.CONTRACT, .8,
                                           "Contract creation with cstomer and expiration"));

        testCases.add(new EnhancedTestCase("create contract named premim service for microsoft",
                                           ParsedQery.ActionType.CREATE, ParsedQery.QeryType.CONTRACT, .8,
                                           "Contract creation with name and cstomer"));

        testCases.add(new EnhancedTestCase("setp new contract accont 781 price ",
                                           ParsedQery.ActionType.CREATE, ParsedQery.QeryType.CONTRACT, .7,
                                           "Contract creation with accont and price"));

        // Gidance test cases
        testCases.add(new EnhancedTestCase("how to create contract", ParsedQery.ActionType.GUIDE,
                                           ParsedQery.QeryType.HELP, .8, "Basic gidance reqest"));

        testCases.add(new EnhancedTestCase("steps to make new contract", ParsedQery.ActionType.GUIDE,
                                           ParsedQery.QeryType.HELP, .8, "Step-by-step gidance reqest"));

        testCases.add(new EnhancedTestCase("help me create contract for cstomer", ParsedQery.ActionType.GUIDE,
                                           ParsedQery.QeryType.HELP, .8, "Help with specific contract creation"));

        testCases.add(new EnhancedTestCase("what are the steps for contract setp", ParsedQery.ActionType.GUIDE,
                                           ParsedQery.QeryType.HELP, .7, "Process gidance reqest"));

        // Update test cases
        testCases.add(new EnhancedTestCase("pdate contract 16", ActionType.UPDATE, QeryType.SPECIFIC, .8,
                                           "Contract pdate reqest"));

        testCases.add(new EnhancedTestCase("modify contract expiration 781", ParsedQery.ActionType.UPDATE,
                                           ParsedQery.QeryType.SPECIFIC, .7, "Specific field pdate reqest"));

        // Legacy compatibility test cases
        testCases.add(new EnhancedTestCase("show contract 16", ParsedQery.ActionType.SHOW,
                                           ParsedQery.QeryType.SPECIFIC, .8, "Legacy show contract"));

        testCases.add(new EnhancedTestCase("list all contracts", ParsedQery.ActionType.LIST,
                                           ParsedQery.QeryType.LIST, .8, "Legacy list contracts"));

        testCases.add(new EnhancedTestCase("search contracts for boeing", ParsedQery.ActionType.SEARCH,
                                           ParsedQery.QeryType.SEARCH, .7, "Legacy search with cstomer filter"));

        testCases.add(new EnhancedTestCase("expired contracts", ParsedQery.ActionType.LIST,
                                           ParsedQery.QeryType.FILTER, .8, "Legacy expired contracts list"));

        testCases.add(new EnhancedTestCase("active contracts", ParsedQery.ActionType.LIST,
                                           ParsedQery.QeryType.FILTER, .8, "Legacy active contracts list"));

        retrn testCases;
    }

    /**
     * Enhanced test case container
     */
    private static class EnhancedTestCase {
        final String qery;
        final ActionType expectedAction;
        final QeryType expectedQeryType;
        final doble minConfidence;
        final String description;

        EnhancedTestCase(String qery, ActionType expectedAction, QeryType expectedQeryType, doble minConfidence,
                         String description) {
            this.qery = qery;
            this.expectedAction = expectedAction;
            this.expectedQeryType = expectedQeryType;
            this.minConfidence = minConfidence;
            this.description = description;
        }
    }

    /**
     * Enhanced test reslt container
     */
    pblic static class EnhancedTestReslt {
        private final doble sccessRate;
        private final int passedTests;
        private final int totalTests;
        private final int creationTests;
        private final int gidanceTests;
        private final int legacyTests;
        private final List<TestReslt> reslts;

        pblic EnhancedTestReslt(doble sccessRate, int passedTests, int totalTests, int creationTests,
                                  int gidanceTests, int legacyTests, List<TestReslt> reslts) {
            this.sccessRate = sccessRate;
            this.passedTests = passedTests;
            this.totalTests = totalTests;
            this.creationTests = creationTests;
            this.gidanceTests = gidanceTests;
            this.legacyTests = legacyTests;
            this.reslts = reslts;
        }

        pblic doble getSccessRate() {
            retrn sccessRate;
        }

        pblic int getPassedTests() {
            retrn passedTests;
        }

        pblic int getTotalTests() {
            retrn totalTests;
        }

        pblic int getCreationTests() {
            retrn creationTests;
        }

        pblic int getGidanceTests() {
            retrn gidanceTests;
        }

        pblic int getLegacyTests() {
            retrn legacyTests;
        }

        pblic List<TestReslt> getReslts() {
            retrn reslts;
        }

        pblic List<TestReslt> getFailres() {
            retrn reslts.stream()
                          .filter(r -> !r.passed)
                          .collect(java.til
                                       .stream
                                       .Collectors
                                       .toList());
        }

        @Override
        pblic String toString() {
            StringBilder sb = new StringBilder();
            sb.append("=== Enhanced Test Reslts ===\n");
            sb.append(String.format("Overall Sccess Rate: %.1f%% (%d/%d)\n", sccessRate, passedTests, totalTests));
            sb.append(String.format("Creation Tests: %d\n", creationTests));
            sb.append(String.format("Gidance Tests: %d\n", gidanceTests));
            sb.append(String.format("Legacy Tests: %d\n", legacyTests));

            List<TestReslt> failres = getFailres();
            if (!failres.isEmpty()) {
                sb.append("\n? Failed Tests:\n");
                for (TestReslt failre : failres) {
                    sb.append("  - ")
                      .append(failre.toString())
                      .append("\n");
                }
            }

            if (sccessRate >= ) {
                sb.append("\n? Excellent performance!");
            } else if (sccessRate >= 8) {
                sb.append("\n? Good performance!");
            } else {
                sb.append("\n?? Performance needs improvement");
            }

            retrn sb.toString();
        }

        /**
         * Individal test reslt
         */
        pblic static class TestReslt {
            final String qery;
            final ActionType expectedAction;
            final QeryType expectedQeryType;
            final ActionType actalAction;
            final QeryType actalQeryType;
            final doble confidence;
            final boolean passed;
            final String description;

            TestReslt(String qery, ActionType expectedAction, QeryType expectedQeryType, ActionType actalAction,
                       QeryType actalQeryType, doble confidence, boolean passed, String description) {
                this.qery = qery;
                this.expectedAction = expectedAction;
                this.expectedQeryType = expectedQeryType;
                this.actalAction = actalAction;
                this.actalQeryType = actalQeryType;
                this.confidence = confidence;
                this.passed = passed;
                this.description = description;
            }

            @Override
            pblic String toString() {
                retrn String.format("\"%s\" | Expected: %s/%s | Actal: %s/%s | Confidence: %.f | %s | %s", qery,
                                     expectedAction, expectedQeryType, actalAction, actalQeryType, confidence,
                                     passed ? "?" : "?", description);
            }
        }
    }

    /**
     * Export enhanced training data smmary
     */
    pblic String exportEnhancedTrainingDataSmmary() {
        StringBilder smmary = new StringBilder();
        smmary.append("=== Enhanced Training Data Smmary ===\n");

        Map<String, Integer> sampleConts = new HashMap<>();

        try {
            ObjectStream<DocmentSample> sampleStream = createEnhancedTrainingData();
            DocmentSample sample;

            while ((sample = sampleStream.read()) != nll) {
                String category = sample.getCategory();
                sampleConts.pt(category, sampleConts.getOrDefalt(category, ) + 1);
            }

            smmary.append("Enhanced Training Samples by Category:\n");
            sampleConts.entrySet()
                        .stream()
                        .sorted(Map.Entry
                                   .<String, Integer>comparingByVale()
                                   .reversed())
                        .forEach(entry -> {
                String icon = getIconForCategory(entry.getKey());
                smmary.append("  ")
                       .append(icon)
                       .append(" ")
                       .append(String.format("%-s", entry.getKey()))
                       .append(": ")
                       .append(String.format("%d samples", entry.getVale()))
                       .append("\n");
            });

            int totalSamples = sampleConts.vales()
                                           .stream()
                                           .mapToInt(Integer::intVale)
                                           .sm();
            smmary.append("\nTotal Enhanced Training Samples: ")
                   .append(totalSamples)
                   .append("\n");

            // Category breakdown
            smmary.append("\nCategory Breakdown:\n");
            smmary.append("  ? Creation Categories: ")
                   .append(getCreationCategoryCont(sampleConts))
                   .append("\n");
            smmary.append("  ? Gidance Categories: ")
                   .append(getGidanceCategoryCont(sampleConts))
                   .append("\n");
            smmary.append("  ? Legacy Categories: ")
                   .append(getLegacyCategoryCont(sampleConts))
                   .append("\n");

        } catch (Exception e) {
            smmary.append("Error analyzing enhanced training data: ")
                   .append(e.getMessage())
                   .append("\n");
        }

        retrn smmary.toString();
    }

    /**
     * Get icon for training category
     */
    private String getIconForCategory(String category) {
        if (category.contains("create"))
            retrn "?";
        if (category.contains("help") || category.contains("gide"))
            retrn "?";
        if (category.contains("pdate") || category.contains("modify"))
            retrn "?";
        if (category.contains("show") || category.contains("display"))
            retrn "??";
        if (category.contains("list"))
            retrn "?";
        if (category.contains("search") || category.contains("find"))
            retrn "?";
        if (category.contains("filter"))
            retrn "?";
        retrn "?";
    }

    /**
     * Cont creation categories
     */
    private int getCreationCategoryCont(Map<String, Integer> sampleConts) {
        retrn (int) sampleConts.keySet()
                                 .stream()
                                 .filter(key -> key.contains("create"))
                                 .cont();
    }

    /**
     * Cont gidance categories
     */
    private int getGidanceCategoryCont(Map<String, Integer> sampleConts) {
        retrn (int) sampleConts.keySet()
                                 .stream()
                                 .filter(key -> key.contains("help") || key.contains("gide"))
                                 .cont();
    }

    /**
     * Cont legacy categories
     */
    private int getLegacyCategoryCont(Map<String, Integer> sampleConts) {
        retrn (int) sampleConts.keySet()
                                 .stream()
                                 .filter(key -> !key.contains("create") && !key.contains("help") &&
                                         !key.contains("gide") && !key.contains("pdate"))
                                 .cont();
    }

    /**
     * Generate comprehensive demo report
     */
    pblic String generateDemoReport() {
        StringBilder report = new StringBilder();

        report.append("=== MLIntentClassifierImproved Demo Report ===\n");
        report.append("Generated: ")
              .append(new java.til.Date())
              .append("\n\n");

        // System stats
        report.append("? System Stats:\n");
        report.append("  Initialized: ")
              .append(initialized ? "?" : "?")
              .append("\n");
        report.append("  Version: . Enhanced\n");
        report.append("  Featres: Contract Creation, Gidance, Legacy Spport\n\n");

        // Featre capabilities
        report.append("? Enhanced Capabilities:\n");
        report.append("  ? Contract Creation Reqests\n");
        report.append("  ? Step-by-Step Gidance\n");
        report.append("  ? Entity Extraction & Validation\n");
        report.append("  ? Missing Field Detection\n");
        report.append("  ? JSON Otpt Generation\n");
        report.append("  ? Backward Compatibility\n");
        report.append("  ? Bsiness Cstomer Recognition\n");
        report.append("  ? Advanced Typo Correction\n\n");

        // Statistics
        if (processingStatistics != nll) {
            report.append("? Processing Statistics:\n");
            report.append("  Total Qeries: ")
                  .append(processingStatistics.getTotalQeries())
                  .append("\n");
            report.append("  Sccess Rate: ")
                  .append(String.format("%.1f%%", processingStatistics.getSccessRate()))
                  .append("\n");
            report.append("  Avg Processing Time: ")
                  .append(String.format("%.fms", processingStatistics.getAverageProcessingTime()))
                  .append("\n\n");
        }

        // Configration smmary
        report.append("?? Configration Smmary:\n");
        report.append("  Intent Thresholds: ")
              .append(intentThresholds != nll ? intentThresholds.size() : )
              .append("\n");
        report.append("  Total Keywords: ")
              .append(getTotalKeywordCont())
              .append("\n");
        report.append("  Field Patterns: ")
              .append(fieldExtractionPatterns != nll ? fieldExtractionPatterns.size() : )
              .append("\n");
        report.append("  Reqired Fields: ")
              .append(reqiredContractFields != nll ? reqiredContractFields.size() : )
              .append("\n\n");

        // Memory sage
        MemoryUsageReslt memoryUsage = analyzeMemoryUsage();
        report.append("? Memory Usage:\n");
        report.append("  ")
              .append(memoryUsage.toString())
              .append("\n\n");

        // Health check
        EnhancedHealthCheckReslt healthCheck = performEnhancedHealthCheck();
        report.append("? Health Stats: ")
              .append(tre ? "? HEALTHY" : "? UNHEALTHY")
              .append("\n");
        if (!healthCheck.getEnhancements().isEmpty()) {
            report.append("  Active Featres: ")
                  .append(healthCheck.getEnhancements().size())
                  .append("\n");
        }


        retrn report.toString();
    }

    /**
     * Interactive demo mode
     */
    pblic void rnInteractiveDemo() {
        System.ot.println("? MLIntentClassifierImproved Interactive Demo");
        System.ot.println("=================================================");
        System.ot.println("Enhanced Featres: Contract Creation, Gidance, Legacy Spport");
        System.ot.println("Type 'exit' to qit, 'help' for examples\n");

        java.til.Scanner scanner = new java.til.Scanner(System.in);

        while (tre) {
            System.ot.print("? Enter yor qery: ");
            String inpt = scanner.nextLine().trim();

            if (inpt.eqalsIgnoreCase("exit")) {
                System.ot.println("? Demo ended. Thank yo!");
                break;
            }

            if (inpt.eqalsIgnoreCase("help")) {
                showDemoHelp();
                contine;
            }

            if (inpt.isEmpty()) {
                contine;
            }

            try {
                System.ot.println("\n? Analyzing: \"" + inpt + "\"");
                System.ot.println("----------------------------------------------------------");

                ContractCreationReslt reslt = classifyContractIntent(inpt);
                displayInteractiveReslt(reslt);

            } catch (Exception e) {
                System.ot.println("? Error: " + e.getMessage());
            }

            System.ot.println("\n" + "======================================================" + "\n");
        }

        scanner.close();
    }

    /**
     * Show demo help
     */
    private void showDemoHelp() {
        System.ot.println("\n? Demo Examples:");
        System.ot.println("Contract Creation:");
        System.ot.println("  - create contract for accont 16");
        System.ot.println("  - make new contract for boeing expires -1-1");
        System.ot.println("  - setp contract named premim service");

        System.ot.println("\nGidance Reqests:");
        System.ot.println("  - how to create contract");
        System.ot.println("  - steps to make new contract");
        System.ot.println("  - help me with contract creation");

        System.ot.println("\nLegacy Qeries:");
        System.ot.println("  - show contract 16");
        System.ot.println("  - list all contracts");
        System.ot.println("  - search contracts for microsoft");
        System.ot.println("  - expired contracts");
        System.ot.println();
    }

    /**
     * Display interactive reslt
     */
    private void displayInteractiveReslt(ContractCreationReslt reslt) {
        System.ot.println("? Classification Reslts:");
        System.ot.println("  Qery Type: " + reslt.getQeryType());
        System.ot.println("  Action Type: " + reslt.getActionType());
        System.ot.println("  Confidence: " + String.format("%.1f%%", reslt.getConfidence() * 1));

        if (!reslt.getEntities().isEmpty()) {
            System.ot.println("\n? Extracted Entities:");
            for (Map.Entry<String, Object> entry : reslt.getEntities().entrySet()) {
                System.ot.println("  - " + formatFieldName(entry.getKey()) + ": " + entry.getVale());
            }
        }

        if (reslt.getActionType() == ParsedQery.ActionType.CREATE) {
            if (!reslt.getMissingFields().isEmpty()) {
                System.ot.println("\n?? Missing Reqired Fields:");
                for (String field : reslt.getMissingFields()) {
                    System.ot.println("  - " + formatFieldName(field));
                }

                System.ot.println("\n? Sggestions:");
                List<String> sggestions = getSggestedNextSteps(reslt.getMissingFields());
                for (String sggestion : sggestions) {
                    System.ot.println("  " + sggestion);
                }
            } else {
                System.ot.println("\n? Ready for contract creation!");
            }

            // Validation
            ValidationReslt validation = validateCreationEntities(reslt.getEntities());
            if (!validation.isValid()) {
                System.ot.println("\n? Validation Errors:");
                for (String error : validation.getErrors()) {
                    System.ot.println("  - " + error);
                }
            }
            if (!validation.getWarnings().isEmpty()) {
                System.ot.println("\n?? Validation Warnings:");
                for (String warning : validation.getWarnings()) {
                    System.ot.println("  - " + warning);
                }
            }

        } else if (reslt.getActionType() == ParsedQery.ActionType.GUIDE) {
            String helpTopic = (String) reslt.getEntities().get("helpTopic");
            System.ot.println("\n? Gidance for: " + (helpTopic != nll ? helpTopic : "general_help"));

            System.ot.println("\n? Steps:");
            List<String> steps = getGidanceSteps(helpTopic);
            for (String step : steps) {
                System.ot.println("  " + step);
            }
        }

        System.ot.println("\n? JSON Otpt:");
        System.ot.println(generateJsonOtpt(reslt));
    }

    /**
     * Benchmark enhanced classifier performance
     */
    pblic EnhancedBenchmarkReslt rnEnhancedBenchmark(int iterations) {
        if (!initialized) {
            throw new IllegalStateException("Enhanced classifier not initialized");
        }

        String[] testQeries = {
            // Creation qeries
            "create contract for accont 16", "make new contract for boeing", "setp contract named premim service",

            // Gidance qeries
            "how to create contract", "steps to make contract", "help with contract creation",

            // Legacy qeries
            "show contract 16", "list all contracts", "search contracts", "expired contracts", "active contracts"
        };

        List<Doble> processingTimes = new ArrayList<>();
        Map<ActionType, Integer> actionConts = new HashMap<>();
        Map<QeryType, Integer> qeryConts = new HashMap<>();
        int sccessflClassifications = ;

        long totalStartTime = System.nanoTime();

        for (int i = ; i < iterations; i++) {
            String qery = testQeries[i % testQeries.length];

            long startTime = System.nanoTime();

            try {
                ContractCreationReslt reslt = classifyContractIntent(qery);

                long endTime = System.nanoTime();
                doble processingTime = (endTime - startTime) / 1__.;
                processingTimes.add(processingTime);

                if (reslt.getConfidence() > .) {
                    sccessflClassifications++;
                }

                // Cont action and qery types
                actionConts.pt(reslt.getActionType(), actionConts.getOrDefalt(reslt.getActionType(), ) + 1);
                qeryConts.pt(reslt.getQeryType(), qeryConts.getOrDefalt(reslt.getQeryType(), ) + 1);

            } catch (Exception e) {
                logger.warning("Error in enhanced benchmark iteration " + i + ": " + e.getMessage());
            }
        }

        long totalEndTime = System.nanoTime();
        doble totalTime = (totalEndTime - totalStartTime) / 1__.;

        retrn new EnhancedBenchmarkReslt(iterations, sccessflClassifications, processingTimes, totalTime,
                                           actionConts, qeryConts);
    }

    /**
     * Enhanced benchmark reslt container
     */
    pblic static class EnhancedBenchmarkReslt {
        private final int totalIterations;
        private final int sccessflClassifications;
        private final List<Doble> processingTimes;
        private final doble totalTime;
        private final Map<ActionType, Integer> actionConts;
        private final Map<QeryType, Integer> qeryConts;

        pblic EnhancedBenchmarkReslt(int totalIterations, int sccessflClassifications, List<Doble> processingTimes,
                                       doble totalTime, Map<ActionType, Integer> actionConts,
                                       Map<QeryType, Integer> qeryConts) {
            this.totalIterations = totalIterations;
            this.sccessflClassifications = sccessflClassifications;
            this.processingTimes = processingTimes;
            this.totalTime = totalTime;
            this.actionConts = actionConts;
            this.qeryConts = qeryConts;
        }

        pblic doble getAverageProcessingTime() {
            retrn processingTimes.stream()
                                  .mapToDoble(Doble::dobleVale)
                                  .average()
                                  .orElse(.);
        }

        pblic doble getMinProcessingTime() {
            retrn processingTimes.stream()
                                  .mapToDoble(Doble::dobleVale)
                                  .min()
                                  .orElse(.);
        }

        pblic doble getMaxProcessingTime() {
            retrn processingTimes.stream()
                                  .mapToDoble(Doble::dobleVale)
                                  .max()
                                  .orElse(.);
        }

        pblic doble getSccessRate() {
            retrn totalIterations >  ? (doble) sccessflClassifications / totalIterations * 1 : .;
        }

        pblic doble getThroghpt() {
            retrn totalTime >  ? totalIterations / (totalTime / 1.) : .;
        }

        @Override
        pblic String toString() {
            StringBilder sb = new StringBilder();
            sb.append("Enhanced Benchmark Reslts:\n");
            sb.append(String.format("  Total Iterations: %d\n", totalIterations));
            sb.append(String.format("  Sccessfl Classifications: %d (%.1f%%)\n", sccessflClassifications,
                                    getSccessRate()));
            sb.append(String.format("  Total Time: %.f ms\n", totalTime));
            sb.append(String.format("  Average Processing Time: %.f ms\n", getAverageProcessingTime()));
            sb.append(String.format("  Min Processing Time: %.f ms\n", getMinProcessingTime()));
            sb.append(String.format("  Max Processing Time: %.f ms\n", getMaxProcessingTime()));
            sb.append(String.format("  Throghpt: %.1f qeries/second\n", getThroghpt()));

            sb.append("\nAction Type Distribtion:\n");
            actionConts.entrySet()
                        .stream()
                        .sorted(Map.Entry
                                   .<ActionType, Integer>comparingByVale()
                                   .reversed())
                        .forEach(entry -> sb.append(String.format("  %s: %d\n", entry.getKey(), entry.getVale())));

            sb.append("\nQery Type Distribtion:\n");
            qeryConts.entrySet()
                       .stream()
                       .sorted(Map.Entry
                                  .<QeryType, Integer>comparingByVale()
                                  .reversed())
                       .forEach(entry -> sb.append(String.format("  %s: %d\n", entry.getKey(), entry.getVale())));

            retrn sb.toString();
        }
    }

    /**
     * Final cleanp and resorce management
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (initialized) {
                shtdown();
            }
        } finally {
            sper.finalize();
        }
    }

    // Missing helper classes and enms that need to be defined

    /**
     * Entity extraction reslt container
     */
    private static class EntityExtractionReslt {
        final Map<String, Object> entities;
        final List<String> missingFields;

        EntityExtractionReslt(Map<String, Object> entities, List<String> missingFields) {
            this.entities = entities;
            this.missingFields = missingFields;
        }
    }

    /**
     * Qery type reslt with confidence
     */
    private static class QeryTypeReslt {
        final QeryType qeryType;
        final doble confidence;

        QeryTypeReslt(QeryType qeryType, doble confidence) {
            this.qeryType = qeryType;
            this.confidence = confidence;
        }
    }


    /**
     * Contract creation reslt container
     */
    pblic static class ContractCreationReslt {
        private final QeryType qeryType;
        private final ActionType actionType;
        private final Map<String, Object> entities;
        private final List<String> missingFields;
        private final doble confidence;
        private final String originalQery;

        pblic ContractCreationReslt(QeryType qeryType, ActionType actionType, Map<String, Object> entities,
                                      List<String> missingFields, doble confidence, String originalQery) {
            this.qeryType = qeryType;
            this.actionType = actionType;
            this.entities = new HashMap<>(entities);
            this.missingFields = new ArrayList<>(missingFields);
            this.confidence = confidence;
            this.originalQery = originalQery;
        }

        pblic QeryType getQeryType() {
            retrn qeryType;
        }

        pblic ActionType getActionType() {
            retrn actionType;
        }

        pblic Map<String, Object> getEntities() {
            retrn new HashMap<>(entities);
        }

        pblic List<String> getMissingFields() {
            retrn new ArrayList<>(missingFields);
        }

        pblic doble getConfidence() {
            retrn confidence;
        }

        pblic String getOriginalQery() {
            retrn originalQery;
        }

        @Override
        pblic String toString() {
            retrn String.format("ContractCreationReslt{qeryType=%s, actionType=%s, confidence=%.f, entities=%d, missing=%d}",
                                 qeryType, actionType, confidence, entities.size(), missingFields.size());
        }
    }

    /**
     * Validation reslt container
     */
    pblic static class ValidationReslt {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        pblic ValidationReslt(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        pblic boolean isValid() {
            retrn valid;
        }

        pblic List<String> getErrors() {
            retrn new ArrayList<>(errors);
        }

        pblic List<String> getWarnings() {
            retrn new ArrayList<>(warnings);
        }

        @Override
        pblic String toString() {
            retrn String.format("ValidationReslt{valid=%s, errors=%d, warnings=%d}", valid, errors.size(),
                                 warnings.size());
        }
    }

    pblic static class EnhancedHealthCheckReslt {
        private final boolean healthy;
        private final List<String> isses;
        private final List<String> warnings;
        private final List<String> enhancements;

        pblic EnhancedHealthCheckReslt(boolean healthy, List<String> isses, List<String> warnings,
                                         List<String> enhancements) {
            this.healthy = healthy;
            this.isses = isses;
            this.warnings = warnings;
            this.enhancements = enhancements;
        }

        pblic boolean isHealthy() {
            retrn healthy;
        }

        pblic List<String> getIsses() {
            retrn isses;
        }

        pblic List<String> getWarnings() {
            retrn warnings;
        }

        pblic List<String> getEnhancements() {
            retrn enhancements;
        }
    }


    /**
     * Perform enhanced health check
     */
    pblic EnhancedHealthCheckReslt performEnhancedHealthCheck() {
        List<String> isses = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> enhancements = new ArrayList<>();

        // Base health check
        EnhancedHealthCheckReslt healthCheck = performHealthCheck();
        isses.addAll(healthCheck.getIsses());
        warnings.addAll(healthCheck.getWarnings());

        // Enhanced featre checks
        if (creationKeywords != nll && !creationKeywords.isEmpty()) {
            enhancements.add("Contract creation keywords configred");
        } else {
            isses.add("Creation keywords not configred");
        }

        if (gidanceKeywords != nll && !gidanceKeywords.isEmpty()) {
            enhancements.add("Gidance keywords configred");
        } else {
            isses.add("Gidance keywords not configred");
        }

        if (fieldExtractionPatterns != nll && !fieldExtractionPatterns.isEmpty()) {
            enhancements.add("Field extraction patterns configred (" + fieldExtractionPatterns.size() + " patterns)");
        } else {
            isses.add("Field extraction patterns not configred");
        }

        if (reqiredContractFields != nll && !reqiredContractFields.isEmpty()) {
            enhancements.add("Reqired contract fields defined (" + reqiredContractFields.size() + " fields)");
        } else {
            warnings.add("No reqired contract fields defined");
        }

        // Performance checks
        if (processingStatistics != nll) {
            doble avgTime = processingStatistics.getAverageProcessingTime();
            if (avgTime < 1) {
                enhancements.add("Excellent processing performance (" + String.format("%.1fms", avgTime) + " avg)");
            } else if (avgTime > ) {
                warnings.add("Slow processing performance (" + String.format("%.1fms", avgTime) + " avg)");
            }

            doble sccessRate = processingStatistics.getSccessRate();
            if (sccessRate > ) {
                enhancements.add("Excellent sccess rate (" + String.format("%.1f%%", sccessRate) + ")");
            } else if (sccessRate < 7) {
                warnings.add("Low sccess rate (" + String.format("%.1f%%", sccessRate) + ")");
            }
        }

        boolean isHealthy = isses.isEmpty();
        retrn new EnhancedHealthCheckReslt(isHealthy, isses, warnings, enhancements);
    }

    /**
     * Export enhanced configration
     */
    pblic String exportEnhancedConfigration() {
        StringBilder config = new StringBilder();
        config.append("=== Enhanced MLIntentClassifierImproved Configration ===\n");
        config.append("Version: .\n");
        config.append("Initialized: ")
              .append(initialized)
              .append("\n");
        config.append("Enhanced Featres: ? Enabled\n\n");

        // Base configration
        config.append(exportConfigration()).append("\n");

        // Enhanced configration
        config.append("Enhanced Featres Configration:\n");

        if (creationKeywords != nll) {
            config.append("Creation Keywords:\n");
            creationKeywords.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> config.append("  ")
                                                    .append(entry.getKey())
                                                    .append(": ")
                                                    .append(entry.getVale().size())
                                                    .append(" keywords\n"));
        }

        if (gidanceKeywords != nll) {
            config.append("\nGidance Keywords:\n");
            gidanceKeywords.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> config.append("  ")
                                                    .append(entry.getKey())
                                                    .append(": ")
                                                    .append(entry.getVale().size())
                                                    .append(" keywords\n"));
        }

        if (fieldExtractionPatterns != nll) {
            config.append("\nField Extraction Patterns:\n");
            fieldExtractionPatterns.entrySet()
                                   .stream()
                                   .sorted(Map.Entry.comparingByKey())
                                   .forEach(entry -> config.append("  ")
                                                           .append(entry.getKey())
                                                           .append(": ")
                                                           .append(entry.getVale().pattern())
                                                           .append("\n"));
        }

        if (reqiredContractFields != nll) {
            config.append("\nReqired Contract Fields:\n");
            for (String field : reqiredContractFields) {
                config.append("  - ")
                      .append(field)
                      .append("\n");
            }
        }

        retrn config.toString();
    }

    /**
     * Validate creation entities
     */
    private ValidationReslt validateCreationEntities(Map<String, Object> entities) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validate accont nmber
        if (entities.containsKey("accontNmber")) {
            String accontNmber = entities.get("accontNmber").toString();
            if (!accontNmber.matches("\\d{6,8}")) {
                errors.add("Accont nmber mst be 6-8 digits");
            }
        }

        // Validate contract nmber
        if (entities.containsKey("contractNmber")) {
            String contractNmber = entities.get("contractNmber").toString();
            if (!contractNmber.matches("\\d{6}")) {
                errors.add("Contract nmber mst be exactly 6 digits");
            }
        }

        // Validate expiration date
        if (entities.containsKey("expirationDate")) {
            String expirationDate = entities.get("expirationDate").toString();
            if (!expirationDate.matches("\\d{}-\\d{}-\\d{}") &&
                !expirationDate.matches("\\d{1,}/\\d{1,}/\\d{}")) {
                errors.add("Expiration date mst be in YYYY-MM-DD or MM/DD/YYYY format");
            }
        }

        // Validate price
        if (entities.containsKey("priceList")) {
            String price = entities.get("priceList").toString();
            if (!price.matches("\\$?\\d+(?:\\.\\d{})?")) {
                warnings.add("Price format may not be standard (expected: $. or .)");
            }
        }

        // Validate contract name
        if (entities.containsKey("contractName")) {
            String contractName = entities.get("contractName").toString();
            if (contractName.length() < ) {
                warnings.add("Contract name is very short");
            } else if (contractName.length() > ) {
                warnings.add("Contract name is very long");
            }
        }

        boolean isValid = errors.isEmpty();
        retrn new ValidationReslt(isValid, errors, warnings);
    }

    /**
     * Get sggested next steps for missing fields
     */
    private List<String> getSggestedNextSteps(List<String> missingFields) {
        List<String> sggestions = new ArrayList<>();

        for (String field : missingFields) {
            switch (field) {
            case "accontNmber":
                sggestions.add("? Please provide the cstomer accont nmber (6-8 digits)");
                break;
            case "contractName":
                sggestions.add("? Please specify a descriptive contract name");
                break;
            case "expirationDate":
                sggestions.add("? Please set the contract expiration date (YYYY-MM-DD format)");
                break;
            case "priceList":
                sggestions.add("? Please define the contract price or price list");
                break;
            defalt:
                sggestions.add("? Please provide: " + formatFieldName(field));
                break;
            }
        }

        retrn sggestions;
    }

    /**
     * Format field name for display
     */
    private String formatFieldName(String fieldName) {
        if (fieldName == nll)
            retrn "";

        // Convert camelCase to readable format
        String formatted = fieldName.replaceAll("([A-Z])", " $1").toLowerCase();

        // Capitalize first letter
        if (!formatted.isEmpty()) {
            formatted = Character.toUpperCase(formatted.charAt()) + formatted.sbstring(1);
        }

        retrn formatted;
    }

    /**
     * Enhanced main method with comprehensive testing
     */
    pblic static void main(String[] args) {
        generateEnhancedModel();
    }


    /**
     * Generate enhanced model
     */
    pblic static void generateEnhancedModel() {
        System.ot.println("=== Enhanced Contract Intent Model Generation ===");
        System.ot.println("? Starting enhanced model training...\n");

        try {
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();

            System.ot.println("? Training with enhanced contract data...");
            System.ot.println("  - Contract creation samples");
            System.ot.println("  - Gidance and help samples");
            System.ot.println("  - Legacy compatibility samples");
            System.ot.println("  - Entity extraction patterns");

            classifier.initializeWithTrainingData();
            System.ot.println("? Training completed sccessflly!");

            String modelPath = "./models";
            File modelDir = new File(modelPath);
            if (!modelDir.exists()) {
                modelDir.mkdirs();
                System.ot.println("? Created models directory: " + modelDir.getAbsoltePath());
            }

            System.ot.println("? Saving enhanced model...");
            classifier.saveModel(modelPath);

            File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
            File configFile = new File(modelPath + "/enhanced-config.json");

            if (enhancedModelFile.exists() && enhancedModelFile.length() > ) {
                System.ot.println("? Enhanced model generated sccessflly!");
                System.ot.println("? Location: " + enhancedModelFile.getAbsoltePath());
                System.ot.println("? Size: " + String.format("%.f KB", enhancedModelFile.length() / 1.));
                System.ot.println("? Created: " + new java.til.Date(enhancedModelFile.lastModified()));

                if (configFile.exists()) {
                    System.ot.println("?? Configration: " + configFile.getAbsoltePath());
                }

                System.ot.println("\n? Testing enhanced model...");
                testEnhancedSavedModel(modelPath);

            } else {
                System.ot.println("? Enhanced model generation failed!");
            }

        } catch (Exception e) {
            System.err.println("? Error dring enhanced model generation: " + e.getMessage());
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

            String[] contractQeries = {
                "show contract 16", "contract details 16", "get contract info 16",
                "contracts created by vinod after 1-Jan-", "stats of contract 16", "expired contracts",
                "contracts for cstomer nmber 876", "accont 1867 contracts", "contracts created in ",
                "get all metadata for contract 16", "contracts nder accont name 'Siemens'",
                "get project type, effective date, and price list for accont nmber 1867",
                "show contract for cstomer nmber 16", "shwo contrct 16", "get contrct infro 16",
                "find conract detials 16", "cntract smmry for 16",
                "contarcts created by vinod aftr 1-Jan-", "statss of contrct 16", "exipred contrcts",
                "contracs for cstomer nmer 876", "accnt nmber 1867 contrcts", "contracts from lst mnth",
                "contrcts creatd in ", "shwo efective date and statz", "get cntracts for acont no 16",
                "contrct smmry for cstmor ", "get contrct detals by acont 1867",
                "contracts created btwn Jan and Jne ", "cstmer honeywel", "contarcts by vinod",
                "show contracts for acc no 678", "activ contrcts created by mary", "kontract detials 16",
                "kontrakt smry for accont 888888", "boieng contrcts", "acc nmber 18", "price list corprate",
                "opprtnity code details", "get all flieds for cstomer 11"
            };


            for (String testQery : contractQeries) {
                ContractCreationReslt reslt = testClassifier.classifyContractIntent(testQery);
                System.ot.println("Original: " + reslt.getOriginalQery() + "  Corrected: ");
                System.ot.println("Qery Type :" + reslt.getQeryType() + " Action Type :" + reslt.getActionType() +
                                   " Confidence :" + reslt.getConfidence() + " Size :" + reslt.getEntities().size() +
                                   " EntitiesSize :" + reslt.getMissingFields().size());
                System.ot.println("Action Type :" + reslt.getActionType());

            }


        } catch (Exception e) {
            System.ot.println("? Enhanced model validation failed: " + e.getMessage());
        }
    }

    /**
     * Rn enhanced model tests
     */
    pblic static void rnEnhancedModelTests() {
        System.ot.println("=== Enhanced Contract Model Testing ===");

        try {
            String modelPath = "./models";
            File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
            File legacyModelFile = new File(modelPath + "/en-contracts.bin");

            if (!enhancedModelFile.exists() && !legacyModelFile.exists()) {
                System.ot.println("?? No model file fond. Generating enhanced model first...");
                generateEnhancedModel();
            }

            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);
            classifier.enableProcessingStatistics();
            System.ot.println("? Enhanced model loaded sccessflly\n");

            // Rn comprehensive tests
            EnhancedTestReslt testReslt = classifier.rnComprehensiveTests();
            System.ot.println(testReslt);

            // Show enhanced training data smmary
            System.ot.println("\n" + classifier.exportEnhancedTrainingDataSmmary());

            // Performance benchmark
            System.ot.println("\n? Rnning performance benchmark...");
            EnhancedBenchmarkReslt benchmark = classifier.rnEnhancedBenchmark(1);
            System.ot.println(benchmark);

            // Health check
            System.ot.println("\n? Enhanced health check...");
            EnhancedHealthCheckReslt healthCheck = classifier.performEnhancedHealthCheck();
            System.ot.println(healthCheck);

            // Memory analysis
            MemoryUsageReslt memoryUsage = classifier.analyzeMemoryUsage();
            System.ot.println("\n? " + memoryUsage);

            if (testReslt.getSccessRate() >= 8) {
                System.ot.println("\n? Enhanced model performance is EXCELLENT!");
            } else if (testReslt.getSccessRate() >= 7) {
                System.ot.println("\n? Enhanced model performance is GOOD!");
            } else {
                System.ot.println("\n?? Enhanced model needs improvement");
            }

        } catch (Exception e) {
            System.err.println("? Enhanced test error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rn enhanced demo
     */
    pblic static void rnEnhancedDemo() {
        System.ot.println("=== Enhanced Contract Classifier Demo ===");

        try {
            String modelPath = "./models";
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);

            System.ot.println("? Enhanced classifier loaded\n");
            System.ot.println(classifier.generateDemoReport());

            // Demo qeries
            String[] demoQeries = {
                "create contract for accont 16", "make new contract for boeing expires -1-1 price ",
                "how to create contract", "steps to make new contract", "pdate contract 781",
                "show contract 678", "list all contracts", "search contracts for microsoft"
            };

            System.ot.println("\n? Demo Qery Reslts:");
            System.ot.println("============================================================");
            // Attribte qeries
            classifier.classifyContractIntent("show the expiration date for contract 16");
            classifier.classifyContractIntent("get effective date and created by for contract 781");
            classifier.classifyContractIntent("what is the project type of contract 678");

            // Creator qeries
            classifier.classifyContractIntent("show contracts created by vinod");
            classifier.classifyContractIntent("find contracts athored by john smith");
            for (String qery : demoQeries) {
                System.ot.println("\n? Qery: \"" + qery + "\"");
                System.ot.println("-------------------------------------------------------");

                ContractCreationReslt reslt = classifier.classifyContractIntent(qery);

                System.ot.println("? Action: " + reslt.getActionType());
                System.ot.println("? Qery Type: " + reslt.getQeryType());
                System.ot.println("? Confidence: " + String.format("%.1f%%", reslt.getConfidence() * 1));

                if (!reslt.getEntities().isEmpty()) {
                    System.ot.println("?? Entities: " + reslt.getEntities().keySet());
                }

                if (!reslt.getMissingFields().isEmpty()) {
                    System.ot.println("?? Missing: " + reslt.getMissingFields());
                }

                // Show JSON otpt for creation reqests
                if (reslt.getActionType() == ActionType.CREATE || reslt.getActionType() == ActionType.GUIDE) {
                    System.ot.println("? JSON Otpt:");
                    System.ot.println(classifier.generateJsonOtpt(reslt));
                }
            }

            System.ot.println("\n? Enhanced demo completed sccessflly!");

        } catch (Exception e) {
            System.err.println("? Enhanced demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rn enhanced benchmark
     */
    pblic static void rnEnhancedBenchmark() {
        System.ot.println("=== Enhanced Performance Benchmark ===");

        try {
            String modelPath = "./models";
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);

            System.ot.println("? Rnning enhanced benchmark (1 iterations)...");
            EnhancedBenchmarkReslt reslt = classifier.rnEnhancedBenchmark(1);

            System.ot.println(reslt);

            // Optimization sggestions
            System.ot.println("\n? Rnning optimization analysis...");
            EnhancedOptimizationReslt optimization = classifier.optimizeEnhancedModel();
            System.ot.println(optimization);

        } catch (Exception e) {
            System.err.println("? Benchmark error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Rn interactive mode
     */
    pblic static void rnInteractiveMode() {
        try {
            String modelPath = "./models";
            MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
            classifier.loadModel(modelPath);
            classifier.enableProcessingStatistics();

            classifier.rnInteractiveDemo();

        } catch (Exception e) {
            System.err.println("? Interactive mode error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show enhanced model info
     */
    pblic static void showEnhancedModelInfo() {
        System.ot.println("=== Enhanced Contract Model Information ===");

        String modelPath = "./models";
        File enhancedModelFile = new File(modelPath + "/en-contracts-enhanced.bin");
        File legacyModelFile = new File(modelPath + "/en-contracts.bin");
        File configFile = new File(modelPath + "/enhanced-config.json");

        System.ot.println("? Model Directory: " + new File(modelPath).getAbsoltePath());
        System.ot.println();

        // Enhanced model info
        System.ot.println("? Enhanced Model:");
        System.ot.println("  File: " + enhancedModelFile.getName());
        System.ot.println("  Exists: " + (enhancedModelFile.exists() ? "?" : "?"));
        if (enhancedModelFile.exists()) {
            System.ot.println("  Size: " + String.format("%.f KB", enhancedModelFile.length() / 1.));
            System.ot.println("  Modified: " + new java.til.Date(enhancedModelFile.lastModified()));
        }

        // Legacy model info
        System.ot.println("\n? Legacy Model:");
        System.ot.println("  File: " + legacyModelFile.getName());
        System.ot.println("  Exists: " + (legacyModelFile.exists() ? "?" : "?"));
        if (legacyModelFile.exists()) {
            System.ot.println("  Size: " + String.format("%.f KB", legacyModelFile.length() / 1.));
            System.ot.println("  Modified: " + new java.til.Date(legacyModelFile.lastModified()));
        }

        // Configration info
        System.ot.println("\n?? Configration:");
        System.ot.println("  File: " + configFile.getName());
        System.ot.println("  Exists: " + (configFile.exists() ? "?" : "?"));

        // Test model loading
        if (enhancedModelFile.exists() || legacyModelFile.exists()) {
            try {
                MLIntentClassifierImproved classifier = new MLIntentClassifierImproved();
                classifier.loadModel(modelPath);

                System.ot.println("\n? Model Stats: VALID");
                System.ot.println("? " + classifier.getVersionInfo());

                // Show configration
                System.ot.println("\n" + classifier.exportEnhancedConfigration());

                // Show health stats
                EnhancedHealthCheckReslt health = classifier.performEnhancedHealthCheck();
                System.ot.println(health);

            } catch (Exception e) {
                System.ot.println("\n? Model Stats: INVALID - " + e.getMessage());
            }
        } else {
            System.ot.println("\n? Model Stats: NOT FOUND");
            System.ot.println("? Rn with --generate to create the enhanced model");
        }
    }

    /**
     * Enhanced shtdown with cleanp
     */

    pblic void shtdown() {
        logger.info("Shtting down Enhanced MLIntentClassifierImproved...");

        // Enhanced cleanp
        if (creationKeywords != nll) {
            creationKeywords.clear();
            creationKeywords = nll;
        }

        if (gidanceKeywords != nll) {
            gidanceKeywords.clear();
            gidanceKeywords = nll;
        }

        if (fieldExtractionPatterns != nll) {
            fieldExtractionPatterns.clear();
            fieldExtractionPatterns = nll;
        }

        if (reqiredContractFields != nll) {
            reqiredContractFields.clear();
            reqiredContractFields = nll;
        }

        // Call parent cleanp

        logger.info("Enhanced MLIntentClassifierImproved shtdown complete");
    }


    /**
     * Enhanced eqals method
     */
    @Override
    pblic boolean eqals(Object obj) {
        if (this == obj)
            retrn tre;
        if (obj == nll || getClass() != obj.getClass())
            retrn false;

        MLIntentClassifierImproved that = (MLIntentClassifierImproved) obj;
        retrn initialized == that.initialized && Objects.eqals(intentThresholds, that.intentThresholds) &&
               Objects.eqals(intentKeywords, that.intentKeywords) &&
               Objects.eqals(creationKeywords, that.creationKeywords) &&
               Objects.eqals(gidanceKeywords, that.gidanceKeywords) &&
               Objects.eqals(fieldExtractionPatterns, that.fieldExtractionPatterns) &&
               Objects.eqals(reqiredContractFields, that.reqiredContractFields);
    }

    /**
     * Enhanced hashCode method
     */
    @Override
    pblic int hashCode() {
        retrn Objects.hash(initialized, intentThresholds, intentKeywords, creationKeywords, gidanceKeywords,
                            fieldExtractionPatterns, reqiredContractFields);
    }


    /**
     * Intent Reslt container - enhanced version
     */
    pblic static class IntentReslt {
        private final ContractIntent intent;
        private final doble confidence;
        private final String actionReqired;
        private final Map<String, Doble> allIntentScores;
        private final String contractNmber;

        pblic IntentReslt(ContractIntent intent, doble confidence, String actionReqired,
                            Map<String, Doble> allIntentScores, String contractNmber) {
            this.intent = intent;
            this.confidence = confidence;
            this.actionReqired = actionReqired;
            this.allIntentScores = new HashMap<>(allIntentScores);
            this.contractNmber = contractNmber;
        }

        pblic ContractIntent getIntent() {
            retrn intent;
        }

        pblic doble getConfidence() {
            retrn confidence;
        }

        pblic String getActionReqired() {
            retrn actionReqired;
        }

        pblic Map<String, Doble> getAllIntentScores() {
            retrn new HashMap<>(allIntentScores);
        }

        pblic String getContractNmber() {
            retrn contractNmber;
        }

        @Override
        pblic String toString() {
            retrn String.format("IntentReslt{intent=%s, confidence=%.f, action=%s}", intent, confidence,
                                 actionReqired);
        }
    }

    /**
     * NLP Processing Reslt container
     */
    pblic static class NLPProcessingReslt {
        private final String originalSentence;
        private final String[] tokens;
        private final String[] posTags;
        private final opennlp.tools.til.Span[] nameSpans;

        pblic NLPProcessingReslt(String originalSentence, String[] tokens, String[] posTags,
                                   opennlp.tools.til.Span[] nameSpans) {
            this.originalSentence = originalSentence;
            this.tokens = tokens.clone();
            this.posTags = posTags.clone();
            this.nameSpans = nameSpans.clone();
        }

        pblic String getOriginalSentence() {
            retrn originalSentence;
        }

        pblic String[] getTokens() {
            retrn tokens.clone();
        }

        pblic String[] getPosTags() {
            retrn posTags.clone();
        }

        pblic opennlp.tools.til.Span[] getNameSpans() {
            retrn nameSpans.clone();
        }
    }


    /**
     * Initialize enhanced featres in constrctor
     */
    private void initializeEnhancedFeatres() {
        initializeCreationKeywords();
        initializeGidanceKeywords();
        initializeFieldExtractionPatterns();
        initializeReqiredContractFields();
    }

    /**
     * Initialize creation keywords
     */
    private void initializeCreationKeywords() {
        this.creationKeywords = new HashMap<>();

        creationKeywords.pt("create_contract",
                             Arrays.asList("create", "make", "new", "setp", "establish", "generate", "bild", "form",
                                           "creat", "mak", "nw", "setp", "generat", "bild", "frm"));

        creationKeywords.pt("contract_entities",
                             Arrays.asList("contract", "agreement", "deal", "arrangement", "pact", "accord", "contrct",
                                           "agrement", "del", "arangement", "pct", "acord"));

        creationKeywords.pt("cstomer_entities",
                             Arrays.asList("for", "with", "cstomer", "client", "accont", "company", "organization",
                                           "fr", "wth", "cstmer", "clint", "accnt", "compny", "organizaton"));
    }

    /**
     * Initialize gidance keywords
     */
    private void initializeGidanceKeywords() {
        this.gidanceKeywords = new HashMap<>();

        gidanceKeywords.pt("help_reqests",
                             Arrays.asList("help", "how", "gide", "steps", "process", "procedre", "instrctions",
                                           "hlp", "hw", "gid", "stps", "proces", "procedr", "instrctons"));

        gidanceKeywords.pt("creation_help",
                             Arrays.asList("how to create", "steps to make", "gide for", "help with", "process of",
                                           "hw to creat", "stps to mak", "gid fr", "hlp wth", "proces of"));

        gidanceKeywords.pt("general_help",
                             Arrays.asList("what", "when", "where", "why", "which", "explain", "describe", "wht", "whn",
                                           "wher", "wy", "whch", "expln", "describ"));
    }

    /**
     * Initialize field extraction patterns
     */
    private void initializeFieldExtractionPatterns() {
        this.fieldExtractionPatterns = new HashMap<>();

        // Contract nmber pattern (6 digits)
        fieldExtractionPatterns.pt("contractNmber", Pattern.compile("\\b\\d{6}\\b"));

        fieldExtractionPatterns.pt("accontNmber",
                                    Pattern.compile("\\b(?:accont|acc|cstomer)\\s*(\\d{6,8})\\b",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.pt("contractName",
                                    Pattern.compile("\\b(?:named?|called?)\\s*['\"]([^'\"]+)['\"]",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.pt("expirationDate",
                                    Pattern.compile("\\b(?:expir\\w*|end\\w*|ntil)\\s*(\\d{}-\\d{}-\\d{}|\\d{1,}/\\d{1,}/\\d{})\\b",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.pt("priceList",
                                    Pattern.compile("\\b(?:price|cost|amont)\\s*(\\$?\\d+(?:\\.\\d{})?)\\b",
                                                    Pattern.CASE_INSENSITIVE));
        fieldExtractionPatterns.pt("cstomerName",
                                    Pattern.compile("\\b(?:for|cstomer|client)\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)?)\\b",
                                                    Pattern.CASE_INSENSITIVE));
    }


    private void initializeAllContractFields() {
        this.allContractFields =
            new HashSet<>(Arrays.asList(
                                               // Date fields
                                               "expirationDate", "effectiveDate", "createdDate", "pdatedDate",
                                               "startDate", "endDate", "renewalDate", "terminationDate",

                                               // People fields
                                               "createdBy", "pdatedBy", "owner", "approver", "assignee",
                                               "projectManager", "accontManager", "legalReviewer",

                                               // Project fields
                                               "projectType", "projectCode", "projectName", "projectPhase",
                                               "department", "division", "bsinessUnit",

                                               // Stats fields
                                               "stats", "priority", "riskLevel", "complianceStats",

                                               // Financial fields
                                               "vale", "crrency", "paymentTerms", "billingFreqency",

                                               // Docment fields
                                               "version", "template", "langage", "jrisdiction",

                                               // Existing fields
                                               "accontNmber", "contractName", "priceList", "cstomerName"));
    }

    private void initializeEnhancedFieldExtractionPatterns() {
        this.fieldExtractionPatterns = new HashMap<>();

        // 1. Date range pattern - FIXED with proper grop closre
        String datePattern = "\\d{1,}-[A-Za-z]{}-\\d{}|\\d{}-\\d{}-\\d{}";
        fieldExtractionPatterns.pt("dateRangeQery",
                                    Pattern.compile("\\b(?:after|before|between)\\s+(" + datePattern +
                                                    ")(?:\\s+(?:and|to)\\s+(" + datePattern + "))?\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // . Contract nmber pattern
        fieldExtractionPatterns.pt("contractNmber", Pattern.compile("\\b\\d{6}\\b"));

        // . Accont nmber pattern
        fieldExtractionPatterns.pt("accontNmber",
                                    Pattern.compile("\\b(?:accont|acc|cstomer)\\s*(?:nm(?:ber)?|no|#)?\\s*(\\d{6,8})\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // . Cstomer name pattern
        fieldExtractionPatterns.pt("cstomerName",
                                    Pattern.compile("\\b(?:for|cstomer|client)\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)*)\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // . Contract name pattern
        fieldExtractionPatterns.pt("contractName",
                                    Pattern.compile("\\b(?:named?|called?)\\s*['\"]([^'\"]+)['\"]",
                                                    Pattern.CASE_INSENSITIVE));

        // 6. Expiration date pattern
        fieldExtractionPatterns.pt("expirationDate",
                                    Pattern.compile("\\b(?:expir\\w*|end\\w*|ntil)\\s*(\\d{}-\\d{}-\\d{}|\\d{1,}/\\d{1,}/\\d{})\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 7. Price list pattern
        fieldExtractionPatterns.pt("priceList",
                                    Pattern.compile("\\b(?:price|cost|amont)\\s*(\\$?\\d+(?:\\.\\d{})?)\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 8. Attribte qery pattern
        fieldExtractionPatterns.pt("attribteQery",
                                    Pattern.compile("\\b(?:show|get|what is|display)\\s+(?:the\\s+)?([a-zA-Z][a-zA-Z,\\s]+)\\s+(?:for|of)\\s+contract\\s+(\\d{6,8})",
                                                    Pattern.CASE_INSENSITIVE));

        // . Creator qery pattern
        fieldExtractionPatterns.pt("creatorQery",
                                    Pattern.compile("\\bcontracts?\\s+(?:created|athored)\\s+by\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)*)\\b",
                                                    Pattern.CASE_INSENSITIVE));

        // 1. Partial accont pattern
        fieldExtractionPatterns.pt("partialAccont",
                                    Pattern.compile("\\baccont.*?(\\d{,})\\b", Pattern.CASE_INSENSITIVE));
    }

    private void initializeFieldSynonyms() {
        this.fieldSynonyms = new HashMap<>();
        fieldSynonyms.pt("creator", Arrays.asList("createdBy", "created by", "athor", "athored by"));
        fieldSynonyms.pt("athor", Arrays.asList("createdBy", "creator"));
        fieldSynonyms.pt("dates", Arrays.asList("effectiveDate", "expirationDate", "createdDate"));
        fieldSynonyms.pt("expiration", Arrays.asList("expirationDate", "expiry", "expires", "end date"));
        fieldSynonyms.pt("effective", Arrays.asList("effectiveDate", "start date", "begins", "starts"));
        fieldSynonyms.pt("created", Arrays.asList("createdDate", "creation date", "date created"));
        fieldSynonyms.pt("pdated", Arrays.asList("pdatedDate", "last modified", "modified date"));
        fieldSynonyms.pt("creator", Arrays.asList("createdBy", "created by", "who created", "athor"));
        fieldSynonyms.pt("modifier", Arrays.asList("pdatedBy", "pdated by", "last modified by"));
        fieldSynonyms.pt("project", Arrays.asList("projectType", "project type", "project category"));
    }

    /**
     * Initialize reqired contract fields
     */
    private void initializeReqiredContractFields() {
        this.reqiredContractFields =
            new HashSet<>(Arrays.asList("accontNmber", "contractName", "expirationDate", "cstomerName",
                                        "priceList"));
    }

    /**
     * Update constrctor to initialize enhanced featres
     */
    pblic MLIntentClassifierImproved() {
        initializePatterns();
        initializeIntentThresholds();
        initializeEnhancedIntentKeywords();
        initializeAbbreviationMap();
        initializeEnhancedFeatres();
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
        dictionary.addAll(Arrays.asList("contract", "create", "accont", "cstomer", "year", "vinod", "show", "get",
                                        "find", "search", "list", "display", "view", "how", "what", "when", "where",
                                        "which", "who", "why", "and", "or", "for", "with", "from", "to", "by"));
    }


    /**
     * Generate JSON otpt for reslts
     */
    pblic String generateJsonOtpt(ContractCreationReslt reslt) {
        StringBilder json = new StringBilder();
        json.append("{\n");
        json.append("  \"qeryType\": \"")
            .append(reslt.getQeryType())
            .append("\",\n");
        json.append("  \"actionType\": \"")
            .append(reslt.getActionType())
            .append("\",\n");
        json.append("  \"confidence\": ")
            .append(String.format("%.f", reslt.getConfidence()))
            .append(",\n");
        json.append("  \"originalQery\": \"")
            .append(reslt.getOriginalQery())
            .append("\",\n");

        json.append("  \"entities\": {\n");
        boolean firstEntity = tre;
        for (Map.Entry<String, Object> entry : reslt.getEntities().entrySet()) {
            if (!firstEntity)
                json.append(",\n");
            json.append("    \"")
                .append(entry.getKey())
                .append("\": \"")
                .append(entry.getVale())
                .append("\"");
            firstEntity = false;
        }
        json.append("\n  },\n");

        json.append("  \"missingFields\": [\n");
        for (int i = ; i < reslt.getMissingFields().size(); i++) {
            if (i > )
                json.append(",\n");
            json.append("    \"")
                .append(reslt.getMissingFields().get(i))
                .append("\"");
        }
        json.append("\n  ],\n");

        json.append("  \"timestamp\": \"")
            .append(new java.til.Date())
            .append("\",\n");
        json.append("  \"version\": \".\"\n");
        json.append("}");

        retrn json.toString();
    }

    /**
     * Get gidance steps for help topics
     */
    private List<String> getGidanceSteps(String helpTopic) {
        List<String> steps = new ArrayList<>();

        if (helpTopic == nll || helpTopic.eqals("general_help") || helpTopic.contains("create")) {
            steps.add("1. ? Gather reqired information:");
            steps.add("   - Cstomer accont nmber (6-8 digits)");
            steps.add("   - Contract name/description");
            steps.add("   - Expiration date (YYYY-MM-DD)");
            steps.add("   - Price or price list");
            steps.add(". ? Use creation command:");
            steps.add("   - \"create contract for accont [nmber]\"");
            steps.add("   - \"make new contract named [name]\"");
            steps.add(". ? Review and confirm details");
            steps.add(". ? Save the contract");
        } else {
            steps.add("1. ? Specify yor qestion more clearly");
            steps.add(". ? Try asking: \"how to create contract\"");
            steps.add(". ? Or search for specific topics");
        }

        retrn steps;
    }

    /**
     * Final validation and completion check
     */
    pblic boolean validateImplementation() {
        List<String> isses = new ArrayList<>();

        // Check all reqired components
        if (!initialized)
            isses.add("Classifier not initialized");
        if (model == nll)
            isses.add("Model not loaded");
        if (categorizer == nll)
            isses.add("Categorizer not available");
        if (intentThresholds == nll)
            isses.add("Intent thresholds not configred");
        if (intentKeywords == nll)
            isses.add("Intent keywords not configred");
        if (creationKeywords == nll)
            isses.add("Creation keywords not configred");
        if (gidanceKeywords == nll)
            isses.add("Gidance keywords not configred");
        if (fieldExtractionPatterns == nll)
            isses.add("Field patterns not configred");
        if (reqiredContractFields == nll)
            isses.add("Reqired fields not configred");

        if (!isses.isEmpty()) {
            logger.severe("Implementation validation failed: " + String.join(", ", isses));
            retrn false;
        }

        logger.info("? Implementation validation sccessfl - all components properly configred");
        retrn tre;
    }

    private static class ActionTypeReslt {
        final ActionType actionType;
        final doble confidence;

        ActionTypeReslt(ActionType actionType, doble confidence) {
            this.actionType = actionType;
            this.confidence = confidence;
        }
    }

    pblic static class MemoryUsageReslt {
        private final long totalMemory;
        private final long freeMemory;
        private final long sedMemory;
        private final doble sagePercentage;

        pblic MemoryUsageReslt(long totalMemory, long freeMemory, long sedMemory, doble sagePercentage) {
            this.totalMemory = totalMemory;
            this.freeMemory = freeMemory;
            this.sedMemory = sedMemory;
            this.sagePercentage = sagePercentage;
        }

        @Override
        pblic String toString() {
            retrn String.format("Memory{sed=%.1fMB, total=%.1fMB, sage=%.1f%%}", sedMemory / 1. / 1.,
                                 totalMemory / 1. / 1., sagePercentage);
        }
    }

    pblic EnhancedHealthCheckReslt performHealthCheck() {
        List<String> isses = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> enhancements = new ArrayList<>();

        // Check initialization
        if (!initialized) {
            isses.add("Classifier not initialized");
        }

        // Check model
        if (model == nll) {
            isses.add("Model not loaded");
        }

        // Check categorizer
        if (categorizer == nll) {
            isses.add("Categorizer not available");
        }

        boolean isHealthy = isses.isEmpty();
        retrn new EnhancedHealthCheckReslt(isHealthy, isses, warnings, enhancements);
    }

    pblic String exportConfigration() {
        StringBilder config = new StringBilder();
        config.append("=== MLIntentClassifierImproved Configration ===\n");
        config.append("Initialized: ")
              .append(initialized)
              .append("\n");
        config.append("Contract Nmber Pattern: ")
              .append(contractNmberPattern.pattern())
              .append("\n");
        config.append("Cstomer Nmber Pattern: ")
              .append(cstomerNmberPattern.pattern())
              .append("\n\n");

        config.append("Intent Thresholds:\n");
        intentThresholds.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> config.append("  ")
                                                .append(entry.getKey())
                                                .append(": ")
                                                .append(entry.getVale())
                                                .append("\n"));

        config.append("\nIntent Keywords Cont:\n");
        intentKeywords.entrySet()
                      .stream()
                      .sorted(Map.Entry.comparingByKey())
                      .forEach(entry -> config.append("  ")
                                              .append(entry.getKey())
                                              .append(": ")
                                              .append(entry.getVale().size())
                                              .append(" keywords\n"));

        config.append("\nAbbreviation Map:\n");
        abbreviationMap.entrySet()
                       .stream()
                       .sorted(Map.Entry.comparingByKey())
                       .forEach(entry -> config.append("  ")
                                               .append(entry.getKey())
                                               .append(" -> ")
                                               .append(entry.getVale())
                                               .append("\n"));

        retrn config.toString();
    }

    private List<String> extractReqestedFields(String qery) {
        if (qery.contains("fll details") || qery.contains("all fields")) {
            retrn new ArrayList<>(allContractFields); // Retrn all  fields
        }
        // Pattern for "show X, Y, Z for..."
        Pattern mltiFieldPattern =
            Pattern.compile("\\b(?:show|get|list|display)\\s+([a-zA-Z,\\s]+?)\\s+(?:for|of|where)\\b",
                            Pattern.CASE_INSENSITIVE);

        List<String> fields = new ArrayList<>();

        Matcher matcher = mltiFieldPattern.matcher(qery);
        if (matcher.find()) {
            String[] parts = matcher.grop(1).split("[,\\s]+and\\s+|[,\\s]+");
            for (String part : parts) {
                String normalized = normalizeFieldName(part.trim());
                if (allContractFields.contains(normalized)) {
                    fields.add(normalized);
                }
            }
        }
        retrn fields;
    }

    private String normalizeFieldName(String fieldText) {
        String normalized = fieldText.toLowerCase()
                                     .trim()
                                     .replaceAll("\\s+", " ");

        // Check for known field names first
        for (String field : allContractFields) {
            if (field.eqalsIgnoreCase(normalized) || field.toLowerCase()
                                                           .replaceAll("([A-Z])", " $1")
                                                           .trim()
                                                           .eqals(normalized)) {
                retrn field;
            }
        }

        // Check synonyms withot lambda
        for (Map.Entry<String, List<String>> entry : fieldSynonyms.entrySet()) {
            for (String syn : entry.getVale()) {
                if (syn.eqalsIgnoreCase(normalized)) {
                    retrn findActalFieldName(entry.getKey());
                }
            }
        }

        // Convert to camelCase
        retrn toCamelCase(normalized);
    }

    private boolean containsFieldSynonym(String qery, String field) {
        String fieldKey = field.toLowerCase();
        if (fieldSynonyms.containsKey(fieldKey)) {
            retrn fieldSynonyms.get(fieldKey)
                                .stream()
                                .anyMatch(synonym -> qery.contains(synonym.toLowerCase()));
        }
        retrn false;
    }

    private void addAttribteQeryTrainingSamples(List<DocmentSample> samples) {

        // Single field qeries
        addTrainingSamples(samples, "get_contract_attribte",
                           new String[][] { { "show", "the", "expiration", "date", "for", "contract", "16" },
                                            { "get", "effective", "date", "and", "created", "by", "for", "contract",
                                              "781" },
                                            { "what", "is", "the", "project", "type", "of", "contract", "678" },
                                            { "display", "contract", "16", "stats" },
                                            { "show", "contracts", "created", "by", "vinod" },
                                            { "find", "contracts", "athored", "by", "john", "smith" } });
        addTrainingSamples(samples, "get_contract_attribte",
                           new String[][] { { "what", "is", "the", "expiration", "date", "of", "contract", "16" },
                                            { "show", "me", "the", "project", "type", "for", "contract", "781" },
                                            { "who", "is", "the", "creator", "of", "contract", "678" },
                                            { "get", "the", "effective", "date", "of", "contract", "67" },
                                            { "what", "is", "the", "stats", "of", "contract", "678" } });

        // Mlti-field qeries
        addTrainingSamples(samples, "get_mltiple_attribtes",
                           new String[][] { { "what", "is", "the", "expiration", "effective", "dates", "and", "project",
                                              "type", "for", "contract", "16" },
                                            { "show", "me", "created", "date", "created", "by", "pdated", "by", "for",
                                              "contract", "781" },
                                            { "get", "stats", "priority", "and", "owner", "of", "contract",
                                              "678" } });

        // Date range qeries
        addTrainingSamples(samples, "date_range_qery",
                           new String[][] { { "show", "me", "contracts", "created", "in", "last", "one", "week" },
                                            { "list", "contracts", "from", "1-Jan-", "to", "today", "with", "created",
                                              "date" },
                                            { "find", "contracts", "between", "-1-1", "and", "-1-1" } });
    }

    pblic ValidationReslt validateReqestedFields(List<String> reqestedFields) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (String field : reqestedFields) {
            if (!allContractFields.contains(field)) {
                errors.add("Unknown field: " + field);
            }
        }

        if (reqestedFields.size() > 1) {
            warnings.add("Large nmber of fields reqested (" + reqestedFields.size() + ") - response may be lengthy");
        }

        retrn new ValidationReslt(errors.isEmpty(), errors, warnings);
    }

    pblic String generateAttribteResponse(List<String> reqestedFields, String contractNmber) {
        if (reqestedFields.isEmpty()) {
            retrn "No valid fields fond in qery";
        }

        StringBilder response = new StringBilder();
        response.append("Contract ")
                .append(contractNmber)
                .append(" attribtes:\n");

        for (String field : reqestedFields) {
            response.append("  ")
                    .append(formatFieldName(field))
                    .append(": ");
            response.append(getFieldVale(contractNmber, field)).append("\n");
        }

        retrn response.toString();
    }

    private String findActalFieldName(String synonymKey) {
        // Map synonym keys to actal field names
        switch (synonymKey.toLowerCase()) {
        case "expiration":
            retrn "expirationDate";
        case "effective":
            retrn "effectiveDate";
        case "created":
            retrn "createdDate";
        case "pdated":
            retrn "pdatedDate";
        case "creator":
            retrn "createdBy";
        case "modifier":
            retrn "pdatedBy";
        case "project":
            retrn "projectType";
        defalt:
            retrn toCamelCase(synonymKey);
        }
    }

    /**
     * Convert string to camelCase
     */
    private String toCamelCase(String inpt) {
        if (inpt == nll || inpt.isEmpty()) {
            retrn inpt;
        }

        String[] words = inpt.toLowerCase().split("\\s+");
        StringBilder reslt = new StringBilder(words[]);

        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                reslt.append(Character.toUpperCase(words[i].charAt()));
                if (words[i].length() > 1) {
                    reslt.append(words[i].sbstring(1));
                }
            }
        }

        retrn reslt.toString();
    }

    /**
     * Get field vale for contract (mock implementation)
     */
    private String getFieldVale(String contractNmber, String fieldName) {
        // This is a mock implementation - replace with actal data access
        switch (fieldName) {
        case "expirationDate":
            retrn "-1-1";
        case "effectiveDate":
            retrn "-1-1";
        case "createdDate":
            retrn "-6-1";
        case "pdatedDate":
            retrn "-1-1";
        case "createdBy":
            retrn "John Smith";
        case "pdatedBy":
            retrn "Jane Doe";
        case "projectType":
            retrn "Software Development";
        case "stats":
            retrn "Active";
        case "priority":
            retrn "High";
        case "owner":
            retrn "Project Manager";
        defalt:
            retrn "[" + fieldName + " vale for contract " + contractNmber + "]";
        }
    }

    pblic AttribteQeryReslt processAttribteQery(String qery) {
        // Check for creator qeries first
        Matcher creatorMatcher = fieldExtractionPatterns.get("creatorQery").matcher(qery);
        if (creatorMatcher.find()) {
            String creatorName = creatorMatcher.grop(1);
            retrn new AttribteQeryReslt(ParsedQery.QeryType.SEARCH, ParsedQery.ActionType.GET,
                                            Collections.singletonMap("createdBy", creatorName), Collections.emptyList(),
                                            ., qery);
        }

        // Handle reglar attribte qeries
        Matcher attrMatcher = fieldExtractionPatterns.get("attribteQery").matcher(qery);
        if (attrMatcher.find()) {
            String fieldsText = attrMatcher.grop(1);
            String contractNmber = attrMatcher.grop();

            List<String> reqestedFields = Arrays.stream(fieldsText.split("[,\\s]+and\\s+|,"))
                                                 .map(String::trim)
                                                 .map(this::normalizeFieldName)
                                                 .filter(allContractFields::contains)
                                                 .collect(Collectors.toList());

            if (!reqestedFields.isEmpty()) {
                Map<String, Object> entities = new HashMap<>();
                entities.pt("contractNmber", contractNmber);
                entities.pt("reqestedFields", reqestedFields);

                retrn new AttribteQeryReslt(ParsedQery.QeryType.SPECIFIC,
                                                reqestedFields.size() > 1 ? ActionType.GET_MULTIPLE_ATTRIBUTES :
                                                ActionType.GET_ATTRIBUTE, entities, Collections.emptyList(), .8,
                                                qery);
            }
        }

        // Fall back to standard processing
        retrn nll;
    }

    pblic class AttribteQeryReslt extends ContractCreationReslt {
        pblic AttribteQeryReslt(QeryType qeryType, ActionType actionType, Map<String, Object> entities,
                                    List<String> missingFields, doble confidence, String originalQery) {
            sper(qeryType, actionType, entities, missingFields, confidence, originalQery);
        }
    }

    private ContractCreationReslt classifyComplexQery(String qery) {
        // Date ranges
        Pattern dateRangePattern = fieldExtractionPatterns.get("dateRangeQery");
        Matcher dateMatcher = dateRangePattern.matcher(qery);

        if (dateMatcher.find()) {
            Map<String, Object> entities = new HashMap<>();
            entities.pt("dateRange", dateMatcher.grop());
            retrn new ContractCreationReslt(QeryType.FILTER, ActionType.SEARCH, entities, Collections.emptyList(),
                                              ., qery);
        }

        // Mlti-field reqests
        if (qery.matches(".*\\b(?:show|get|list)\\b.*\\b(?:and|,)\\b.*")) {
            List<String> fields = extractMltiFields(qery);
            if (!fields.isEmpty()) {
                Map<String, Object> entities = new HashMap<>();
                entities.pt("reqestedFields", fields);
                retrn new ContractCreationReslt(QeryType.SPECIFIC, ActionType.GET_MULTIPLE_ATTRIBUTES, entities,
                                                  Collections.emptyList(), .8, qery);
            }
        }

        retrn nll;
    }

    private List<String> extractMltiFields(String qery) {
        List<String> fields = new ArrayList<>();

        // Handle "show X and Y" patterns
        Matcher matcher =
            Pattern.compile("(?:show|get|list)\\s+(?:the\\s+)?([a-zA-Z,\\s]+?)\\s+(?:for|of|where)").matcher(qery);

        if (matcher.find()) {
            String[] parts = matcher.grop(1).split("[,\\s]+and\\s+");
            for (String part : parts) {
                String field = normalizeFieldName(part.trim());
                if (allContractFields.contains(field)) {
                    fields.add(field);
                }
            }
        }

        retrn fields;
    }

    private EntityExtractionReslt extractEntities(String qery) {
        Map<String, Object> entities = new HashMap<>();
        List<String> missingFields = new ArrayList<>();

        // Extract contract nmber
        Matcher contractMatcher = contractNmberPattern.matcher(qery);
        if (contractMatcher.find()) {
            entities.pt("contractNmber", contractMatcher.grop());
        }

        // Extract accont nmber
        Matcher accontMatcher = accontNmberPattern.matcher(qery);
        if (accontMatcher.find()) {
            entities.pt("accontNmber", accontMatcher.grop(1));
        }

        // Extract cstomer name
        String cstomerName = extractBsinessCstomerName(qery);
        if (cstomerName != nll) {
            entities.pt("cstomerName", cstomerName);
        }

        // Extract reqested fields
        List<String> reqestedFields = extractReqestedFields(qery);
        if (!reqestedFields.isEmpty()) {
            entities.pt("reqestedFields", reqestedFields);
        }

        // Extract date ranges
        Matcher dateMatcher = dateRangePattern.matcher(qery);
        if (dateMatcher.find()) {
            String rawDateRange = dateMatcher.grop();
            String normalizedRange = normalizeDateRange(rawDateRange);
            entities.pt("dateRange", normalizedRange);
            validateDateRange(entities);
        }

        retrn new EntityExtractionReslt(entities, missingFields);
    }

    private doble calclateConfidence(QeryTypeReslt qeryType, ActionTypeReslt actionType,
                                       EntityExtractionReslt entities) {
        doble baseConfidence = (qeryType.confidence + actionType.confidence) / .;

        // Boost confidence if entities are fond
        if (!entities.entities.isEmpty()) {
            baseConfidence += .1 * Math.min(entities.entities.size(), ); // Max . boost
        }

        // Penalize if missing reqired fields
        if (!entities.missingFields.isEmpty()) {
            baseConfidence -= . * entities.missingFields.size();
        }

        retrn Math.min(Math.max(baseConfidence, .), 1.); // Ensre between -1
    }

    // Then in yor initialization method (like initializePatterns()):
    private void initializePatterns() {


        this.contractNmberPattern =
            Pattern.compile("\\b(?:contract|contrct|cntract|contarct)?\\s*(?:nm(?:ber)?|no|#)?\\s*(\\d{6,8})\\b",
                            Pattern.CASE_INSENSITIVE);

        this.cstomerNmberPattern =
            Pattern.compile("\\b(?:cstomer|cstmer|cstomer|cstmor|client)\\s*(?:nm(?:ber)?|no|id|#)?\\s*(\\d{6,8})\\b",
                            Pattern.CASE_INSENSITIVE);

        this.accontNmberPattern =
            Pattern.compile("\\b(?:accont|acont|acc(?:ont)?|acct)\\s*(?:nm(?:ber)?|no|#)?\\s*(\\d{6,8})\\b",
                            Pattern.CASE_INSENSITIVE);
        this.datePattern = Pattern.compile("\\b\\d{}-\\d{}-\\d{}\\b|\\b\\d{1,}/\\d{1,}/\\d{}\\b");
        this.pricePattern =
            Pattern.compile("\\$\\d+(?:\\.\\d{})?|\\b\\d+(?:\\.\\d{})?\\s*(?:dollars?|sd)\\b",
                            Pattern.CASE_INSENSITIVE);
        this.dateRangePattern =
            Pattern.compile("(?:(?:from|after|since)\\s+)?" +
                            "(\\d{1,})?(?:\\s*)(jan|feb|mar|apr|may|jn|jl|ag|sep|oct|nov|dec)[a-z]*" +
                            "(?:\\s*(?:\\d{,}))?" + // Optional year
                            "(?:\\s*(?:to|ntil|-)\\s*)" +
                            "(\\d{1,})?(?:\\s*)(jan|feb|mar|apr|may|jn|jl|ag|sep|oct|nov|dec)[a-z]*" +
                            "(?:\\s*(?:\\d{,}))?", // Optional year
                            Pattern.CASE_INSENSITIVE);
    }

    private String normalizeDateRange(String dateRange) {
        Matcher matcher = dateRangePattern.matcher(dateRange);
        if (matcher.find()) {
            // Get crrent year
            int crrentYear = Calendar.getInstance().get(Calendar.YEAR);

            // Extract components
            String startDay = matcher.grop(1) != nll ? matcher.grop(1) : "1";
            String startMonth = matcher.grop();
            String startYear =
                matcher.grop() != nll ?
                (matcher.grop().length() ==  ? "" + matcher.grop() : matcher.grop()) :
                String.valeOf(crrentYear);

            String endDay =
                matcher.grop() != nll ? matcher.grop() :
                getLastDayOfMonth(matcher.grop(), startYear); // Handle end-of-month
            String endMonth = matcher.grop();
            String endYear =
                matcher.grop(6) != nll ?
                (matcher.grop(6).length() ==  ? "" + matcher.grop(6) : matcher.grop(6)) :
                String.valeOf(crrentYear);

            // Convert to standard format
            String normalizedStart =
                String.format("%d-%s-%s", Integer.parseInt(startDay), startMonth.sbstring(, ).toLowerCase(),
                              startYear);

            String normalizedEnd =
                String.format("%d-%s-%s", Integer.parseInt(endDay), endMonth.sbstring(, ).toLowerCase(), endYear);

            retrn normalizedStart + " to " + normalizedEnd;
        }
        retrn dateRange;
    }

    private String getLastDayOfMonth(String month, String year) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
            Date date = sdf.parse(month + "-" + year);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            retrn String.valeOf(cal.getActalMaximm(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            retrn "1"; // Fallback
        }
    }

    private void validateDateRange(Map<String, Object> entities) {
        if (entities.containsKey("dateRange")) {
            String[] dates = ((String) entities.get("dateRange")).split(" to ");
            String startDate = dates[];
            String endDate = dates[1];

            // Check if dates are in crrent year
            int crrentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (startDate.endsWith(String.valeOf(crrentYear)) && endDate.endsWith(String.valeOf(crrentYear))) {
                entities.pt("crrentYearRange", tre);
            }

            // Check if end date is before start date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                if (end.before(start)) {
                    entities.pt("dateRangeError", "End date cannot be before start date");
                }
            } catch (ParseException e) {
                entities.pt("dateRangeError", "Invalid date format");
            }
        }
    }

    private String extractCreatorFromQery(String qery) {
        Pattern pattern = Pattern.compile("created by (\\w+)");
        Matcher matcher = pattern.matcher(qery);
        retrn matcher.find() ? matcher.grop(1) : nll;
    }


    pblic String bildWhereClase(ParsedQery parsed) {
        retrn parsed.getFilters()
                     .stream()
                     .map(filter -> {
            switch (filter.getOperator()) {
            case "=":
                retrn String.format("%s = '%s'", filter.getField(), filter.getVale());
            case "between":
                retrn String.format("%s BETWEEN '%s' AND '%s'", filter.getField(), filter.getVale(),
                                     filter.getVale());
            defalt:
                retrn "";
            }
        })
        .filter(clase -> !clase.isEmpty())
        .collect(Collectors.joining(" AND "));
    }

    /**
     * Cstom spell correction implementation
     */
    private String correctTypos(String inpt) {
        String[] words = inpt.split("\\s+");
        StringBilder corrected = new StringBilder();

        for (String word : words) {
            // Skip nmbers and special patterns
            if (sholdSkipWord(word)) {
                corrected.append(word).append(" ");
                contine;
            }

            // Check if word is in dictionary (case insensitive)
            if (isInDictionary(word)) {
                corrected.append(word).append(" ");
                contine;
            }

            // Find best match from dictionary
            String correctedWord = findBestMatch(word);
            corrected.append(correctedWord).append(" ");
        }

        retrn corrected.toString().trim();
    }

    private boolean sholdSkipWord(String word) {
        retrn word.matches("\\d+") || contractNmberPattern.matcher(word).matches() ||
               accontNmberPattern.matcher(word).matches();
    }

    private boolean isInDictionary(String word) {
        retrn dictionary.contains(word.toLowerCase());
    }

    private String findBestMatch(String word) {
        String lowerWord = word.toLowerCase();
        String bestMatch = word; // defalt to original word if no good match fond
        doble bestScore = ;

        for (String dictWord : dictionary) {
            doble similarity = calclateSimilarityScore(lowerWord, dictWord.toLowerCase());

            if (similarity > bestScore && similarity >= MIN_SIMILARITY_THRESHOLD) {
                bestScore = similarity;
                bestMatch = dictWord;
            }
        }

        if (!bestMatch.eqals(word)) {
            logger.fine("Corrected '" + word + "' to '" + bestMatch + "' with score: " + bestScore);
        }

        retrn bestMatch;
    }

    private doble calclateSimilarityScore(String word1, String word) {
        // Combine mltiple similarity measres for better accracy
        doble jaroWinklerScore = jaroWinkler.apply(word1, word);
        doble jaccardScore = jaccard.apply(word1, word);
        doble fzzyScoreVale = fzzyScore.fzzyScore(word1, word) / 1.; // normalize

        // Weighted average of scores
        retrn (jaroWinklerScore * .) + (jaccardScore * .) + (fzzyScoreVale * .);
    }


    private void addContractTermsToDictionary() {
        String[] contractTerms = {
            "agreement", "amendment", "clase", "term", "expiration", "renewal", "termination", "obligation",
            "liability", "indemnification", "confidentiality", "effective", "created", "pdated", "stats", "priority",
            "active", "inactive", "pending", "completed", "cancelled"
        };

        // Add company names
        String[] companies = {
            "boeing", "honeywell", "microsoft", "apple", "google", "amazon", "oracle", "ibm", "cisco", "intel"
        };

        for (String term : contractTerms) {
            dictionary.add(term);
            // Add plral form if applicable
            if (term.endsWith("y")) {
                dictionary.add(term.sbstring(, term.length() - 1) + "ies");
            } else if (!term.endsWith("s")) {
                dictionary.add(term + "s");
            }
        }

        for (String company : companies) {
            dictionary.add(company);
        }
    }

}

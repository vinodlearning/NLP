//package view.practice;
//import opennlp.tools.tokenize.*;
//import opennlp.tools.namefind.*;
//import opennlp.tools.postag.*;
//import opennlp.tools.sentdetect.*;
//import opennlp.tools.util.*;
//import java.io.*;
//import java.util.*;
//import java.util.logging.Logger;
//
//import view.nlp.AdvancedTypoHandler;
//import view.nlp.EntityResolver;
//import view.nlp.GrammarEnforcer;
//import view.nlp.MLIntentClassifier;
//import view.nlp.QueryNormalizer;
//
///**
// * Main NLP processor for contract queries in Oracle ADF application
// */
//public class ContractNLPProcessor {
//    
//    private static final Logger logger = Logger.getLogger(ContractNLPProcessor.class.getName());
//    
//    // Your existing components
//    private MLIntentClassifier intentClassifier;
//    private AdvancedTypoHandler typoHandler;
//    private GrammarEnforcer grammarEnforcer;
//    private EntityResolver entityResolver;
//    private QueryNormalizer queryNormalizer;
//    
//    // OpenNLP components
//    private SentenceDetectorME sentenceDetector;
//    private TokenizerME tokenizer;
//    private POSTaggerME posTagger;
//    private NameFinderME contractEntityFinder;
//    
//    // Configuration
//    private String modelPath;
//    private boolean initialized = false;
//    
//    public ContractNLPProcessor(String modelPath) {
//        this.modelPath = modelPath;
//        initializeComponents();
//    }
//    
//    /**
//     * Initialize all NLP components
//     */
//    private void initializeComponents() {
//        try {
//            // Initialize your existing components
//            this.intentClassifier = new MLIntentClassifier();
//            this.typoHandler = new AdvancedTypoHandler();
//            this.grammarEnforcer = new GrammarEnforcer();
//            this.entityResolver = new EntityResolver();
//            this.queryNormalizer = new QueryNormalizer();
//            
//            // Initialize OpenNLP components
//            initializeOpenNLPComponents();
//            
//            this.initialized = true;
//            logger.info("ContractNLPProcessor initialized successfully");
//            
//        } catch (Exception e) {
//            logger.severe("Failed to initialize NLP components: " + e.getMessage());
//            throw new RuntimeException("NLP Initialization failed", e);
//        }
//    }
//    
//    private void initializeOpenNLPComponents() throws IOException {
//        // Initialize sentence detector
//        InputStream sentModelStream = new FileInputStream(modelPath + "/en-sent.bin");
//        SentenceModel sentModel = new SentenceModel(sentModelStream);
//        this.sentenceDetector = new SentenceDetectorME(sentModel);
//        sentModelStream.close();
//        
//        // Initialize tokenizer
//        InputStream tokenModelStream = new FileInputStream(modelPath + "/en-token.bin");
//        TokenizerModel tokenModel = new TokenizerModel(tokenModelStream);
//        this.tokenizer = new TokenizerME(tokenModel);
//        tokenModelStream.close();
//        
//        // Initialize POS tagger
//        InputStream posModelStream = new FileInputStream(modelPath + "/en-pos-maxent.bin");
//        POSModel posModel = new POSModel(posModelStream);
//        this.posTagger = new POSTaggerME(posModel);
//        posModelStream.close();
//        
//        // Initialize contract entity finder
//        InputStream entityModelStream = new FileInputStream(modelPath + "/contract-ner.bin");
//        TokenNameFinderModel entityModel = new TokenNameFinderModel(entityModelStream);
//        this.contractEntityFinder = new NameFinderME(entityModel);
//        entityModelStream.close();
//    }
//    
//    /**
//     * Main processing method for contract queries
//     */
//    public ContractQueryResult processQuery(String userQuery) {
//        if (!initialized) {
//            throw new IllegalStateException("NLP Processor not initialized");
//        }
//        
//        try {
//            // Step 1: Handle typos and normalize query
//            String correctedQuery = typoHandler.correctTypos(userQuery);
//            String normalizedQuery = queryNormalizer.normalize(correctedQuery);
//            
//            // Step 2: Grammar enforcement
//            String grammaticalQuery = grammarEnforcer.enforceGrammar(normalizedQuery);
//            
//            // Step 3: OpenNLP processing pipeline
//            NLPProcessingResult nlpResult = performNLPProcessing(grammaticalQuery);
//            
//            // Step 4: Intent classification
//            IntentResult intentResult = intentClassifier.classifyIntent(nlpResult);
//            
//            // Step 5: Entity resolution
//            EntityResult entityResult = entityResolver.resolveEntities(nlpResult, intentResult);
//            
//            // Step 6: Build final result
//            return buildContractQueryResult(userQuery, nlpResult, intentResult, entityResult);
//            
//        } catch (Exception e) {
//            logger.severe("Error processing query: " + userQuery + " - " + e.getMessage());
//            return ContractQueryResult.createErrorResult(userQuery, e.getMessage());
//        }
//    }
//    
//    private NLPProcessingResult performNLPProcessing(String query) {
//        // Sentence detection
//        String[] sentences = sentenceDetector.sentDetect(query);
//        
//        // Process first sentence (assuming single sentence queries)
//        String sentence = sentences.length > 0 ? sentences[0] : query;
//        
//        // Tokenization
//        String[] tokens = tokenizer.tokenize(sentence);
//        
//        // POS tagging
//        String[] posTags = posTagger.tag(tokens);
//        
//        // Named entity recognition
//        Span[] entitySpans = contractEntityFinder.find(tokens);
//        
//        return new NLPProcessingResult(sentence, tokens, posTags, entitySpans);
//    }
//    
//    private ContractQueryResult buildContractQueryResult(String originalQuery, 
//            NLPProcessingResult nlpResult, IntentResult intentResult, EntityResult entityResult) {
//        
//        ContractQueryResult result = new ContractQueryResult();
//        result.setOriginalQuery(originalQuery);
//        result.setProcessedTokens(nlpResult.getTokens());
//        result.setIntent(intentResult.getIntent());
//        result.setConfidence(intentResult.getConfidence());
//        result.setEntities(entityResult.getEntities());
//        result.setContractNumber(entityResult.getContractNumber());
//        result.setActionRequired(intentResult.getActionRequired());
//        result.setProcessingTimestamp(new Date());
//        
//        return result;
//    }
//    
//    // Getters for testing individual components
//    public MLIntentClassifier getIntentClassifier() { return intentClassifier; }
//    public AdvancedTypoHandler getTypoHandler() { return typoHandler; }
//    public GrammarEnforcer getGrammarEnforcer() { return grammarEnforcer; }
//    public EntityResolver getEntityResolver() { return entityResolver; }
//    public QueryNormalizer getQueryNormalizer() { return queryNormalizer; }
//    
//    public boolean isInitialized() { return initialized; }
//}
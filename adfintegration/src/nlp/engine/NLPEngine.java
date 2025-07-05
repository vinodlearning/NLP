package nlp.engine;

import nlp.core.QueryStructure;
import nlp.correction.SpellChecker;
import nlp.analysis.TextAnalyzer;

/**
 * NLP Engine - Main controller for the NLP processing pipeline
 * 
 * Orchestrates the complete flow:
 * 1. Spell correction
 * 2. Text analysis
 * 3. Entity extraction
 * 4. Operator parsing
 * 5. Response construction
 * 
 * @author Contract/Parts Search System
 * @version 1.0
 */
public class NLPEngine {
    
    private SpellChecker spellChecker;
    private TextAnalyzer textAnalyzer;
    private static NLPEngine instance;
    
    // Performance tracking
    private long totalQueries;
    private long totalProcessingTime;
    private double averageProcessingTime;
    
    /**
     * Singleton pattern with lazy initialization
     */
    public static synchronized NLPEngine getInstance() {
        if (instance == null) {
            instance = new NLPEngine();
        }
        return instance;
    }
    
    /**
     * Private constructor - initializes all components
     */
    private NLPEngine() {
        this.spellChecker = SpellChecker.getInstance();
        this.textAnalyzer = new TextAnalyzer();
        this.totalQueries = 0;
        this.totalProcessingTime = 0;
        this.averageProcessingTime = 0.0;
        
        System.out.println("NLP Engine initialized successfully");
    }
    
    /**
     * MAIN METHOD: Process user query through complete NLP pipeline
     * 
     * @param userQuery Raw user input text
     * @return Structured query object containing all extracted information
     */
    public QueryStructure processQuery(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            QueryStructure errorStructure = new QueryStructure();
            errorStructure.setOriginalQuery(userQuery);
            errorStructure.setCorrectedQuery(userQuery);
            errorStructure.addError("Empty or null user query");
            return errorStructure;
        }
        
        long pipelineStartTime = System.currentTimeMillis();
        
        try {
            // Step 1: Spell Correction
            SpellChecker.SpellCorrectionResult correctionResult = spellChecker.correctSpelling(userQuery);
            
            // Step 2: Text Analysis
            QueryStructure structure = textAnalyzer.analyzeText(
                correctionResult.getCorrectedText(), 
                correctionResult.getOriginalText()
            );
            
            // Step 3: Add spell correction metadata
            if (correctionResult.isHasCorrections()) {
                structure.setHasSpellCorrections(true);
                for (SpellChecker.SpellCorrection correction : correctionResult.getCorrections()) {
                    structure.addSuggestion("Corrected: " + correction.toString());
                }
            }
            
            // Step 4: Performance tracking
            long pipelineEndTime = System.currentTimeMillis();
            long pipelineTime = pipelineEndTime - pipelineStartTime;
            structure.setProcessingTime(pipelineTime);
            
            updatePerformanceMetrics(pipelineTime);
            
            // Step 5: Final validation
            validateFinalStructure(structure);
            
            return structure;
            
        } catch (Exception e) {
            // Error handling
            QueryStructure errorStructure = new QueryStructure();
            errorStructure.setOriginalQuery(userQuery);
            errorStructure.setCorrectedQuery(userQuery);
            errorStructure.addError("NLP Pipeline error: " + e.getMessage());
            
            long pipelineEndTime = System.currentTimeMillis();
            errorStructure.setProcessingTime(pipelineEndTime - pipelineStartTime);
            
            return errorStructure;
        }
    }
    
    /**
     * Process query with additional context information
     * 
     * @param userQuery Raw user input text
     * @param sessionContext Additional context from user session
     * @return Enhanced structured query object
     */
    public QueryStructure processQueryWithContext(String userQuery, SessionContext sessionContext) {
        QueryStructure structure = processQuery(userQuery);
        
        if (sessionContext != null) {
            // Enhance structure with session context
            enhanceWithSessionContext(structure, sessionContext);
        }
        
        return structure;
    }
    
    /**
     * Batch process multiple queries
     * 
     * @param queries List of user queries to process
     * @return List of processed query structures
     */
    public java.util.List<QueryStructure> processQueries(java.util.List<String> queries) {
        java.util.List<QueryStructure> results = new java.util.ArrayList<>();
        
        for (String query : queries) {
            results.add(processQuery(query));
        }
        
        return results;
    }
    
    /**
     * Enhance query structure with session context
     */
    private void enhanceWithSessionContext(QueryStructure structure, SessionContext sessionContext) {
        // Add default user if not extracted
        if (!structure.hasExtractedEntity("created_by") && sessionContext.getCurrentUser() != null) {
            structure.addExtractedEntity("created_by", sessionContext.getCurrentUser());
        }
        
        // Add default customer context if available
        if (!structure.hasExtractedEntity("customer_name") && sessionContext.getCurrentCustomer() != null) {
            structure.addExtractedEntity("customer_name", sessionContext.getCurrentCustomer());
        }
        
        // Add recent queries context
        if (sessionContext.getRecentQueries().size() > 0) {
            String lastQuery = sessionContext.getRecentQueries().get(0);
            if (structure.getQueryType() == QueryStructure.QueryType.UNKNOWN) {
                // Try to infer type from recent queries
                structure.addSuggestion("Based on recent queries, you might want: " + lastQuery);
            }
        }
        
        // Add user preferences
        if (sessionContext.getPreferredFields() != null && !sessionContext.getPreferredFields().isEmpty()) {
            structure.getRequestedEntities().addAll(sessionContext.getPreferredFields());
        }
    }
    
    /**
     * Final validation of the query structure
     */
    private void validateFinalStructure(QueryStructure structure) {
        // Ensure confidence is set
        if (structure.getConfidence() == 0.0) {
            structure.setConfidence(0.5); // Default confidence
        }
        
        // Ensure at least one entity is requested
        if (structure.getRequestedEntities().isEmpty() && 
            structure.getQueryType() != QueryStructure.QueryType.HELP) {
            structure.addSuggestion("No specific fields requested - returning default fields");
        }
        
        // Check for common issues
        if (structure.getExtractedEntities().isEmpty() && 
            structure.getOperators().isEmpty() &&
            structure.getQueryType() != QueryStructure.QueryType.HELP) {
            structure.addSuggestion("Query seems very general - consider being more specific");
        }
    }
    
    /**
     * Update performance tracking metrics
     */
    private void updatePerformanceMetrics(long processingTime) {
        totalQueries++;
        totalProcessingTime += processingTime;
        averageProcessingTime = (double) totalProcessingTime / totalQueries;
    }
    
    /**
     * Get suggestions for improving a query
     */
    public java.util.List<String> getQueryImprovementSuggestions(String query) {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        
        // Process the query to analyze it
        QueryStructure structure = processQuery(query);
        
        // Add specific suggestions based on analysis
        if (structure.getConfidence() < 0.5) {
            suggestions.add("Query confidence is low - try being more specific");
        }
        
        if (structure.getQueryType() == QueryStructure.QueryType.UNKNOWN) {
            suggestions.add("Add keywords like 'contract', 'parts', or 'help' to clarify intent");
        }
        
        if (structure.getExtractedEntities().isEmpty()) {
            suggestions.add("Include specific identifiers like contract numbers or user names");
        }
        
        // Add suggestions from text analyzer
        suggestions.addAll(textAnalyzer.getSimilarQueries(query));
        
        return suggestions;
    }
    
    /**
     * Reload all configurations (for hot-reload functionality)
     */
    public void reloadConfigurations() {
        spellChecker.reloadCorrections();
        System.out.println("NLP Engine configurations reloaded");
    }
    
    /**
     * Get performance statistics
     */
    public PerformanceStats getPerformanceStats() {
        return new PerformanceStats(totalQueries, totalProcessingTime, averageProcessingTime);
    }
    
    /**
     * Test the NLP engine with sample queries
     */
    public void runDiagnostics() {
        System.out.println("Running NLP Engine Diagnostics...");
        
        String[] testQueries = {
            "show contract 123456",
            "pull contracts created by vinod after 2020 before 2024 status expired",
            "list parts for contract 789012",
            "help create contract",
            "get contrst78954632", // Test spell correction
            "parts by custmr ABC Corp" // Test spell correction
        };
        
        for (String query : testQueries) {
            System.out.println("\nTesting: " + query);
            QueryStructure result = processQuery(query);
            System.out.println("Result: " + result.toString());
            System.out.println("Confidence: " + String.format("%.2f", result.getConfidence()));
            System.out.println("Processing Time: " + result.getProcessingTime() + "ms");
            
            if (result.isHasErrors()) {
                System.out.println("Errors: " + result.getErrors());
            }
            
            if (!result.getSuggestions().isEmpty()) {
                System.out.println("Suggestions: " + result.getSuggestions());
            }
        }
        
        System.out.println("\nDiagnostics completed.");
        System.out.println("Performance: " + getPerformanceStats());
    }
    
    /**
     * Session Context class for enhanced processing
     */
    public static class SessionContext {
        private String currentUser;
        private String currentCustomer;
        private java.util.List<String> recentQueries;
        private java.util.Set<String> preferredFields;
        
        public SessionContext() {
            this.recentQueries = new java.util.ArrayList<>();
            this.preferredFields = new java.util.HashSet<>();
        }
        
        public SessionContext(String currentUser, String currentCustomer) {
            this();
            this.currentUser = currentUser;
            this.currentCustomer = currentCustomer;
        }
        
        // Getters and setters
        public String getCurrentUser() { return currentUser; }
        public void setCurrentUser(String currentUser) { this.currentUser = currentUser; }
        
        public String getCurrentCustomer() { return currentCustomer; }
        public void setCurrentCustomer(String currentCustomer) { this.currentCustomer = currentCustomer; }
        
        public java.util.List<String> getRecentQueries() { return recentQueries; }
        public void addRecentQuery(String query) { 
            this.recentQueries.add(0, query); // Add to beginning
            if (this.recentQueries.size() > 10) { // Keep only last 10
                this.recentQueries.remove(10);
            }
        }
        
        public java.util.Set<String> getPreferredFields() { return preferredFields; }
        public void setPreferredFields(java.util.Set<String> preferredFields) { this.preferredFields = preferredFields; }
        public void addPreferredField(String field) { this.preferredFields.add(field); }
    }
    
    /**
     * Performance Statistics class
     */
    public static class PerformanceStats {
        private long totalQueries;
        private long totalProcessingTime;
        private double averageProcessingTime;
        
        public PerformanceStats(long totalQueries, long totalProcessingTime, double averageProcessingTime) {
            this.totalQueries = totalQueries;
            this.totalProcessingTime = totalProcessingTime;
            this.averageProcessingTime = averageProcessingTime;
        }
        
        // Getters
        public long getTotalQueries() { return totalQueries; }
        public long getTotalProcessingTime() { return totalProcessingTime; }
        public double getAverageProcessingTime() { return averageProcessingTime; }
        
        @Override
        public String toString() {
            return String.format("PerformanceStats{queries=%d, totalTime=%dms, avgTime=%.2fms}", 
                               totalQueries, totalProcessingTime, averageProcessingTime);
        }
    }
}
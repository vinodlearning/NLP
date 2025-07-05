package com.nlp.core;

import com.nlp.domain.*;
import com.nlp.models.*;
import com.nlp.processing.*;
import com.nlp.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Core NLP Engine - Main orchestrator for the modular NLP architecture
 * Handles the complete processing pipeline from raw input to structured response
 */
@Component
public class NLPEngine {
    
    @Autowired
    private DomainTokenizer tokenizer;
    
    @Autowired
    private EnhancedPOSTagger posTagger;
    
    @Autowired
    private ContextualSpellChecker spellChecker;
    
    @Autowired
    private IntelligentRouter router;
    
    @Autowired
    private List<DomainModule> domainModules;
    
    private final Map<DomainType, DomainModule> moduleRegistry = new ConcurrentHashMap<>();
    private final ProcessingMetrics metrics = new ProcessingMetrics();
    
    @PostConstruct
    public void initialize() {
        // Register all domain modules
        for (DomainModule module : domainModules) {
            moduleRegistry.put(module.getSupportedDomain(), module);
        }
        
        // Initialize router with registered modules
        router.initialize(moduleRegistry);
        
        System.out.println("NLP Engine initialized with " + moduleRegistry.size() + " domain modules");
    }
    
    /**
     * Main processing method - handles the complete NLP pipeline
     * @param userInput Raw user input string
     * @return Structured NLP response
     */
    public NLPResponse processQuery(String userInput) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate input
            if (userInput == null || userInput.trim().isEmpty()) {
                return createErrorResponse("Empty input provided", userInput);
            }
            
            // Step 1: Tokenization
            TokenizedInput tokens = tokenizer.tokenize(userInput);
            
            // Step 2: POS Tagging
            POSTaggedInput tagged = posTagger.tag(tokens);
            
            // Step 3: Spell Correction
            CorrectedInput corrected = spellChecker.correct(tagged);
            
            // Step 4: Routing Decision
            RoutingDecision routing = router.route(corrected);
            
            // Step 5: Module Processing
            DomainModule selectedModule = moduleRegistry.get(routing.getDomainType());
            if (selectedModule == null) {
                return createErrorResponse("No module found for domain: " + routing.getDomainType(), userInput);
            }
            
            ModuleResponse moduleResponse = selectedModule.process(corrected);
            
            // Step 6: Response Aggregation
            NLPResponse finalResponse = aggregateResponse(userInput, corrected, routing, moduleResponse);
            
            // Update metrics
            long processingTime = System.currentTimeMillis() - startTime;
            metrics.recordProcessing(routing.getDomainType(), processingTime, finalResponse.getConfidence());
            
            return finalResponse;
            
        } catch (Exception e) {
            System.err.println("Error processing query: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Processing error: " + e.getMessage(), userInput);
        }
    }
    
    /**
     * Aggregates all processing results into a unified response
     */
    private NLPResponse aggregateResponse(String originalInput, CorrectedInput corrected, 
                                        RoutingDecision routing, ModuleResponse moduleResponse) {
        
        NLPResponse.Builder responseBuilder = NLPResponse.builder()
            .originalInput(originalInput)
            .correctedInput(corrected.getCorrectedText())
            .correctionConfidence(corrected.getConfidence())
            .selectedModule(routing.getDomainType().toString())
            .routingConfidence(routing.getConfidence())
            .processingTimeMs(System.currentTimeMillis())
            .queryType(moduleResponse.getQueryType())
            .actionType(moduleResponse.getActionType())
            .entities(moduleResponse.getEntities())
            .displayEntities(moduleResponse.getDisplayEntities())
            .moduleSpecificData(moduleResponse.getModuleSpecificData())
            .errors(moduleResponse.getErrors())
            .confidence(calculateOverallConfidence(routing.getConfidence(), moduleResponse.getConfidence()));
        
        // Add header information
        ResponseHeader header = createResponseHeader(originalInput, corrected, moduleResponse);
        responseBuilder.header(header);
        
        return responseBuilder.build();
    }
    
    /**
     * Creates standardized response header
     */
    private ResponseHeader createResponseHeader(String originalInput, CorrectedInput corrected, 
                                              ModuleResponse moduleResponse) {
        return ResponseHeader.builder()
            .contractNumber(extractFromResponse(moduleResponse, "contractNumber"))
            .partNumber(extractFromResponse(moduleResponse, "partNumber"))
            .customerNumber(extractFromResponse(moduleResponse, "customerNumber"))
            .customerName(extractFromResponse(moduleResponse, "customerName"))
            .createdBy(extractFromResponse(moduleResponse, "createdBy"))
            .inputTracking(InputTracking.builder()
                .originalInput(originalInput)
                .correctedInput(corrected.getCorrectedText())
                .correctionConfidence(corrected.getConfidence())
                .build())
            .build();
    }
    
    /**
     * Extracts specific field from module response
     */
    private String extractFromResponse(ModuleResponse response, String fieldName) {
        if (response.getModuleSpecificData() instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) response.getModuleSpecificData();
            Object value = data.get(fieldName);
            return value != null ? value.toString() : null;
        }
        return null;
    }
    
    /**
     * Calculates overall confidence combining routing and module confidence
     */
    private double calculateOverallConfidence(double routingConfidence, double moduleConfidence) {
        // Weighted average: routing confidence (30%) + module confidence (70%)
        return (routingConfidence * 0.3) + (moduleConfidence * 0.7);
    }
    
    /**
     * Creates error response for failed processing
     */
    private NLPResponse createErrorResponse(String errorMessage, String originalInput) {
        return NLPResponse.builder()
            .originalInput(originalInput)
            .correctedInput(originalInput)
            .correctionConfidence(0.0)
            .selectedModule("ERROR")
            .routingConfidence(0.0)
            .processingTimeMs(System.currentTimeMillis())
            .queryType("ERROR")
            .actionType("error_handling")
            .entities(Collections.emptyList())
            .displayEntities(Collections.emptyList())
            .moduleSpecificData(Collections.emptyMap())
            .errors(Arrays.asList(errorMessage))
            .confidence(0.0)
            .header(ResponseHeader.builder()
                .inputTracking(InputTracking.builder()
                    .originalInput(originalInput)
                    .correctedInput(originalInput)
                    .correctionConfidence(0.0)
                    .build())
                .build())
            .build();
    }
    
    /**
     * Batch processing for multiple queries
     */
    public List<NLPResponse> processBatch(List<String> inputs) {
        List<NLPResponse> responses = new ArrayList<>();
        
        for (String input : inputs) {
            responses.add(processQuery(input));
        }
        
        return responses;
    }
    
    /**
     * Get processing metrics
     */
    public ProcessingMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Health check method
     */
    public boolean isHealthy() {
        return tokenizer != null && 
               posTagger != null && 
               spellChecker != null && 
               router != null && 
               !moduleRegistry.isEmpty();
    }
    
    /**
     * Get available domain modules
     */
    public Set<DomainType> getAvailableDomains() {
        return moduleRegistry.keySet();
    }
    
    /**
     * Internal class for tracking processing metrics
     */
    public static class ProcessingMetrics {
        private final Map<DomainType, Integer> domainCounts = new ConcurrentHashMap<>();
        private final Map<DomainType, Double> avgProcessingTimes = new ConcurrentHashMap<>();
        private final Map<DomainType, Double> avgConfidences = new ConcurrentHashMap<>();
        private int totalRequests = 0;
        
        public void recordProcessing(DomainType domain, long processingTime, double confidence) {
            totalRequests++;
            domainCounts.merge(domain, 1, Integer::sum);
            
            // Update average processing time
            avgProcessingTimes.merge(domain, (double) processingTime, 
                (oldAvg, newTime) -> (oldAvg + newTime) / 2);
            
            // Update average confidence
            avgConfidences.merge(domain, confidence, 
                (oldAvg, newConf) -> (oldAvg + newConf) / 2);
        }
        
        public Map<DomainType, Integer> getDomainCounts() {
            return new HashMap<>(domainCounts);
        }
        
        public Map<DomainType, Double> getAvgProcessingTimes() {
            return new HashMap<>(avgProcessingTimes);
        }
        
        public Map<DomainType, Double> getAvgConfidences() {
            return new HashMap<>(avgConfidences);
        }
        
        public int getTotalRequests() {
            return totalRequests;
        }
        
        public String getMetricsSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== NLP Engine Metrics ===\n");
            sb.append("Total Requests: ").append(totalRequests).append("\n");
            sb.append("Domain Distribution:\n");
            
            for (Map.Entry<DomainType, Integer> entry : domainCounts.entrySet()) {
                DomainType domain = entry.getKey();
                int count = entry.getValue();
                double avgTime = avgProcessingTimes.getOrDefault(domain, 0.0);
                double avgConf = avgConfidences.getOrDefault(domain, 0.0);
                
                sb.append(String.format("  %s: %d requests (%.2fms avg, %.2f confidence)\n", 
                    domain, count, avgTime, avgConf));
            }
            
            return sb.toString();
        }
    }
}
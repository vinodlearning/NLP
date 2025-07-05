package com.contractml.controller;

import com.contractml.processor.ContractMLProcessor;
import com.contractml.response.ContractMLResponse;
import com.contractml.utils.SpellCorrector;
import com.contractml.utils.KeywordMatcher;
import com.contractml.utils.EntityExtractor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ContractML Controller - Main entry point for the ContractML system
 * 
 * Features:
 * - Query processing orchestration
 * - Session management
 * - Caching for performance
 * - Statistics tracking
 * - Error handling and logging
 * - Thread-safe operations
 */
public class ContractMLController {
    
    private final ContractMLProcessor processor;
    private final Map<String, ContractMLResponse> responseCache;
    private final Map<String, Object> systemStats;
    
    // Configuration
    private static final int MAX_CACHE_SIZE = 1000;
    private static final long CACHE_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes
    
    /**
     * Constructor
     */
    public ContractMLController() {
        this.processor = new ContractMLProcessor();
        this.responseCache = new ConcurrentHashMap<>();
        this.systemStats = new ConcurrentHashMap<>();
        
        initializeStats();
    }
    
    /**
     * Initialize system statistics
     */
    private void initializeStats() {
        systemStats.put("totalQueries", 0);
        systemStats.put("cacheHits", 0);
        systemStats.put("cacheMisses", 0);
        systemStats.put("errors", 0);
        systemStats.put("averageProcessingTime", 0.0);
        systemStats.put("systemStartTime", System.currentTimeMillis());
    }
    
    /**
     * Main query processing method
     */
    public ContractMLResponse processQuery(String query) {
        return processQuery(query, null);
    }
    
    /**
     * Process query with session ID
     */
    public ContractMLResponse processQuery(String query, String sessionId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Input validation
            if (query == null || query.trim().isEmpty()) {
                return createErrorResponse("INVALID_INPUT", "Query cannot be null or empty");
            }
            
            // Check cache first
            String cacheKey = createCacheKey(query, sessionId);
            ContractMLResponse cachedResponse = getCachedResponse(cacheKey);
            if (cachedResponse != null) {
                incrementStat("cacheHits");
                return cachedResponse;
            }
            
            incrementStat("cacheMisses");
            
            // Process query
            ContractMLResponse response = processor.processQuery(query);
            
            // Cache response
            cacheResponse(cacheKey, response);
            
            // Update statistics
            updateProcessingStats(startTime);
            incrementStat("totalQueries");
            
            return response;
            
        } catch (Exception e) {
            incrementStat("errors");
            return createErrorResponse("SYSTEM_ERROR", "System error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Batch process multiple queries
     */
    public List<ContractMLResponse> processQueries(List<String> queries) {
        return processQueries(queries, null);
    }
    
    /**
     * Batch process multiple queries with session ID
     */
    public List<ContractMLResponse> processQueries(List<String> queries, String sessionId) {
        List<ContractMLResponse> responses = new ArrayList<>();
        
        if (queries == null || queries.isEmpty()) {
            return responses;
        }
        
        for (String query : queries) {
            ContractMLResponse response = processQuery(query, sessionId);
            responses.add(response);
        }
        
        return responses;
    }
    
    /**
     * Get cached response
     */
    private ContractMLResponse getCachedResponse(String cacheKey) {
        if (responseCache.containsKey(cacheKey)) {
            // Simple cache expiry check would go here
            return responseCache.get(cacheKey);
        }
        return null;
    }
    
    /**
     * Cache response
     */
    private void cacheResponse(String cacheKey, ContractMLResponse response) {
        if (responseCache.size() >= MAX_CACHE_SIZE) {
            // Simple cache eviction - remove oldest entries
            clearOldCacheEntries();
        }
        responseCache.put(cacheKey, response);
    }
    
    /**
     * Clear old cache entries
     */
    private void clearOldCacheEntries() {
        // Simple implementation - clear half the cache
        int entriesToRemove = responseCache.size() / 2;
        Iterator<String> iterator = responseCache.keySet().iterator();
        
        for (int i = 0; i < entriesToRemove && iterator.hasNext(); i++) {
            iterator.next();
            iterator.remove();
        }
    }
    
    /**
     * Create cache key
     */
    private String createCacheKey(String query, String sessionId) {
        String normalizedQuery = query.toLowerCase().trim();
        return sessionId != null ? sessionId + ":" + normalizedQuery : normalizedQuery;
    }
    
    /**
     * Create error response
     */
    private ContractMLResponse createErrorResponse(String errorCode, String errorMessage) {
        ContractMLResponse response = new ContractMLResponse();
        response.addError(errorCode, errorMessage);
        response.setQueryMetadata(new ContractMLResponse.QueryMetadata("ERROR", "error", 0L));
        return response;
    }
    
    /**
     * Update processing statistics
     */
    private void updateProcessingStats(long startTime) {
        long processingTime = System.currentTimeMillis() - startTime;
        
        // Update average processing time
        int totalQueries = (Integer) systemStats.get("totalQueries");
        double currentAverage = (Double) systemStats.get("averageProcessingTime");
        double newAverage = (currentAverage * totalQueries + processingTime) / (totalQueries + 1);
        
        systemStats.put("averageProcessingTime", newAverage);
    }
    
    /**
     * Increment statistic counter
     */
    private void incrementStat(String statName) {
        systemStats.put(statName, (Integer) systemStats.get(statName) + 1);
    }
    
    /**
     * Get system statistics
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>(systemStats);
        
        // Add processor statistics
        stats.putAll(processor.getProcessingStats());
        
        // Add cache statistics
        stats.put("cacheSize", responseCache.size());
        stats.put("maxCacheSize", MAX_CACHE_SIZE);
        
        // Add uptime
        long uptime = System.currentTimeMillis() - (Long) systemStats.get("systemStartTime");
        stats.put("uptimeMs", uptime);
        
        return stats;
    }
    
    /**
     * Clear cache
     */
    public void clearCache() {
        responseCache.clear();
    }
    
    /**
     * Reset statistics
     */
    public void resetStatistics() {
        initializeStats();
    }
    
    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", responseCache.size());
        stats.put("maxCacheSize", MAX_CACHE_SIZE);
        stats.put("cacheHits", systemStats.get("cacheHits"));
        stats.put("cacheMisses", systemStats.get("cacheMisses"));
        
        int hits = (Integer) systemStats.get("cacheHits");
        int misses = (Integer) systemStats.get("cacheMisses");
        double hitRate = (hits + misses) > 0 ? (double) hits / (hits + misses) * 100 : 0;
        stats.put("cacheHitRate", hitRate);
        
        return stats;
    }
    
    /**
     * Health check method
     */
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Test basic functionality
            ContractMLResponse testResponse = processQuery("test query");
            
            health.put("status", "HEALTHY");
            health.put("timestamp", System.currentTimeMillis());
            health.put("version", "1.0.0");
            health.put("uptime", System.currentTimeMillis() - (Long) systemStats.get("systemStartTime"));
            health.put("totalQueries", systemStats.get("totalQueries"));
            health.put("errors", systemStats.get("errors"));
            
        } catch (Exception e) {
            health.put("status", "UNHEALTHY");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());
        }
        
        return health;
    }
    
    /**
     * Validate query method
     */
    public Map<String, Object> validateQuery(String query) {
        Map<String, Object> validation = new HashMap<>();
        
        if (query == null) {
            validation.put("valid", false);
            validation.put("error", "Query is null");
            return validation;
        }
        
        if (query.trim().isEmpty()) {
            validation.put("valid", false);
            validation.put("error", "Query is empty");
            return validation;
        }
        
        if (query.length() > 1000) {
            validation.put("valid", false);
            validation.put("error", "Query too long (max 1000 characters)");
            return validation;
        }
        
        validation.put("valid", true);
        validation.put("length", query.length());
        validation.put("wordCount", query.split("\\s+").length);
        
        return validation;
    }
    
    /**
     * Get system configuration
     */
    public Map<String, Object> getSystemConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxCacheSize", MAX_CACHE_SIZE);
        config.put("cacheExpiryMs", CACHE_EXPIRY_MS);
        config.put("version", "1.0.0");
        config.put("features", Arrays.asList(
            "Spell Correction",
            "Entity Extraction", 
            "Intent Classification",
            "Response Caching",
            "Statistics Tracking"
        ));
        return config;
    }
    
    /**
     * Test method for the controller
     */
    public static void main(String[] args) {
        ContractMLController controller = new ContractMLController();
        
        // Test cases
        String[] testCases = {
            "show contract 12345",
            "display all parts for customer john",
            "what is the effective date for contract ABC-789",
            "list parts for account 567890",
            "show contracts created by vinod"
        };
        
        System.out.println("ContractML Controller Test:");
        System.out.println("===========================");
        
        for (String test : testCases) {
            System.out.println("Query: " + test);
            
            ContractMLResponse response = controller.processQuery(test);
            System.out.println("Response JSON:");
            System.out.println(response.toJsonString());
            System.out.println();
        }
        
        // Display statistics
        System.out.println("System Statistics:");
        System.out.println(controller.getSystemStatistics());
        
        // Health check
        System.out.println("\nHealth Check:");
        System.out.println(controller.healthCheck());
    }
}
package view;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class OpenNLPProcessor {
    
    // Change variable name to avoid conflict
    private Pattern contractNumberPattern = Pattern.compile("\\b\\d{6,}\\b|\\b[A-Z]{2,3}-\\d{4,}\\b");
    private Pattern partNumberPattern = Pattern.compile("\\b[A-Z]{2}\\d{3,}\\b|\\b[A-Z]{3}\\d{2,}\\b");
    
    public ParsedQuery processQuery(String query) {
        ParsedQuery parsedQuery = new ParsedQuery();
        parsedQuery.setOriginalQuery(query);
        
        // Simplified processing without OpenNLP for now
        String lowerQuery = query.toLowerCase();
        
        // Extract business entities
        extractBusinessEntities(parsedQuery, query);
        
        // Simple intent classification
        classifyIntent(parsedQuery, lowerQuery);
        
        // Extract parameters
        extractParameters(parsedQuery, query);
        
        return parsedQuery;
    }
    
    private void extractBusinessEntities(ParsedQuery parsedQuery, String query) {
        // Extract contract numbers
        Matcher contractMatcher = contractNumberPattern.matcher(query);
        if (contractMatcher.find()) {
            parsedQuery.setContractNumber(contractMatcher.group());
        }
        
        // Extract part numbers
        Matcher partMatcher = partNumberPattern.matcher(query);
        if (partMatcher.find()) {
            parsedQuery.setPartNumber(partMatcher.group());
        }
    }
    
    private void classifyIntent(ParsedQuery parsedQuery, String query) {
        if (query.contains("contract") && (query.contains("info") || query.contains("details"))) {
            parsedQuery.setQueryType(QueryType.CONTRACT_INFO);
            parsedQuery.setConfidence(0.8);
        } else if (query.contains("parts") && query.contains("contract") && !query.contains("failed")) {
            parsedQuery.setQueryType(QueryType.PARTS_BY_CONTRACT);
            parsedQuery.setConfidence(0.75);
        } else if (query.contains("failed") && query.contains("parts")) {
            parsedQuery.setQueryType(QueryType.FAILED_PARTS_BY_CONTRACT);
            parsedQuery.setConfidence(0.8);
        } else if (query.contains("user") && query.contains("contract")) {
            parsedQuery.setQueryType(QueryType.USER_CONTRACT_QUERY);
            parsedQuery.setConfidence(0.7);
        } else if (query.contains("parts")) {
            parsedQuery.setQueryType(QueryType.PARTS_INFO);
            parsedQuery.setConfidence(0.7);
        } else if (query.contains("account")) {
            parsedQuery.setQueryType(QueryType.ACCOUNT_INFO);
            parsedQuery.setConfidence(0.75);
        } else if (query.contains("help") && query.contains("create")) {
            parsedQuery.setQueryType(QueryType.HELP_CREATE_CONTRACT);
            parsedQuery.setConfidence(0.7);
        } else {
            parsedQuery.setQueryType(QueryType.UNKNOWN);
            parsedQuery.setConfidence(0.3);
        }
    }
    
    private void extractParameters(ParsedQuery parsedQuery, String query) {
        Map<String, String> parameters = new HashMap<>();
        
        if (parsedQuery.getContractNumber() != null) {
            parameters.put("contractNumber", parsedQuery.getContractNumber());
        }
        
        if (parsedQuery.getPartNumber() != null) {
            parameters.put("partNumber", parsedQuery.getPartNumber());
        }
        
        parameters.put("requestingUser", "current_user");
        parsedQuery.setQueryParameters(parameters);
    }
    
    public void cleanup() {
        // Cleanup method for future OpenNLP integration
    }
}
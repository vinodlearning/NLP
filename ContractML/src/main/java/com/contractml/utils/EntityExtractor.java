package com.contractml.utils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Entity Extractor - Extracts entities from natural language text
 * 
 * Features:
 * - Contract number extraction (various formats)
 * - Part number extraction (alphanumeric patterns)
 * - Customer information extraction
 * - Account number extraction
 * - Date extraction (basic patterns)
 * - Performance optimized with compiled regex patterns
 */
public class EntityExtractor {
    
    // Compiled regex patterns for performance
    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile(
        "\\b(?:contract|agreement|deal)\\s*(?:number|#|no\\.?)?\\s*:?\\s*([A-Z0-9-]+)\\b|" +
        "\\b([A-Z]{2,3}-\\d{3,6})\\b|" +
        "\\b(\\d{4,8})\\b(?=\\s*(?:contract|agreement))|" +
        "\\b(\\d{4,8})\\b(?!\\s*(?:account|customer|part))",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile(
        "\\b(?:part|component|item)\\s*(?:number|#|no\\.?)?\\s*:?\\s*([A-Z0-9-]+)\\b|" +
        "\\b([A-Z]{1,3}\\d{2,6}[A-Z]?)\\b|" +
        "\\b(P\\d{3,6})\\b|" +
        "\\b([A-Z]{2,4}-\\d{2,6})\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern CUSTOMER_NUMBER_PATTERN = Pattern.compile(
        "\\b(?:customer|client|account)\\s*(?:number|#|no\\.?)?\\s*:?\\s*(\\d{4,12})\\b|" +
        "\\b(?:for|by)\\s+(?:customer|client|account)\\s+(\\d{4,12})\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern CUSTOMER_NAME_PATTERN = Pattern.compile(
        "\\b(?:customer|client)\\s*(?:name)?\\s*:?\\s*([A-Za-z]+(?:\\s+[A-Za-z]+)*)\\b|" +
        "\\b(?:for|by)\\s+(?:customer|client)?\\s*([A-Za-z]+(?:\\s+[A-Za-z]+)*)\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile(
        "\\b(?:account|acct)\\s*(?:number|#|no\\.?)?\\s*:?\\s*(\\d{4,12})\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern CREATED_BY_PATTERN = Pattern.compile(
        "\\b(?:created|made|signed)\\s+by\\s+([A-Za-z]+(?:\\s+[A-Za-z]+)*)\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})\\b|" +
        "\\b(\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})\\b|" +
        "\\b(\\d{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+\\d{2,4})\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    // Statistics tracking
    private int extractionCount = 0;
    
    /**
     * Extract all entities from text
     */
    public Map<String, String> extractEntities(String text) {
        Map<String, String> entities = new HashMap<>();
        
        if (text == null || text.trim().isEmpty()) {
            return entities;
        }
        
        // Extract different types of entities
        String contractNumber = extractContractNumber(text);
        if (contractNumber != null) {
            entities.put("contractNumber", contractNumber);
        }
        
        String partNumber = extractPartNumber(text);
        if (partNumber != null) {
            entities.put("partNumber", partNumber);
        }
        
        String customerNumber = extractCustomerNumber(text);
        if (customerNumber != null) {
            entities.put("customerNumber", customerNumber);
        }
        
        String customerName = extractCustomerName(text);
        if (customerName != null) {
            entities.put("customerName", customerName);
        }
        
        String accountNumber = extractAccountNumber(text);
        if (accountNumber != null) {
            entities.put("accountNumber", accountNumber);
        }
        
        String createdBy = extractCreatedBy(text);
        if (createdBy != null) {
            entities.put("createdBy", createdBy);
        }
        
        List<String> dates = extractDates(text);
        if (!dates.isEmpty()) {
            entities.put("dates", String.join(", ", dates));
        }
        
        if (!entities.isEmpty()) {
            extractionCount++;
        }
        
        return entities;
    }
    
    /**
     * Extract contract number from text
     */
    public String extractContractNumber(String text) {
        if (text == null) return null;
        
        Matcher matcher = CONTRACT_NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    return match.trim().toUpperCase();
                }
            }
        }
        return null;
    }
    
    /**
     * Extract part number from text
     */
    public String extractPartNumber(String text) {
        if (text == null) return null;
        
        Matcher matcher = PART_NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    return match.trim().toUpperCase();
                }
            }
        }
        return null;
    }
    
    /**
     * Extract customer number from text
     */
    public String extractCustomerNumber(String text) {
        if (text == null) return null;
        
        Matcher matcher = CUSTOMER_NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    return match.trim();
                }
            }
        }
        return null;
    }
    
    /**
     * Extract customer name from text
     */
    public String extractCustomerName(String text) {
        if (text == null) return null;
        
        Matcher matcher = CUSTOMER_NAME_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    // Filter out common false positives
                    String name = match.trim();
                    if (!isCommonFalsePositive(name)) {
                        return capitalizeWords(name);
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Extract account number from text
     */
    public String extractAccountNumber(String text) {
        if (text == null) return null;
        
        Matcher matcher = ACCOUNT_NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    return match.trim();
                }
            }
        }
        return null;
    }
    
    /**
     * Extract created by information from text
     */
    public String extractCreatedBy(String text) {
        if (text == null) return null;
        
        Matcher matcher = CREATED_BY_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    return capitalizeWords(match.trim());
                }
            }
        }
        return null;
    }
    
    /**
     * Extract dates from text
     */
    public List<String> extractDates(String text) {
        List<String> dates = new ArrayList<>();
        if (text == null) return dates;
        
        Matcher matcher = DATE_PATTERN.matcher(text);
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);
                if (match != null && !match.trim().isEmpty()) {
                    dates.add(match.trim());
                }
            }
        }
        return dates;
    }
    
    /**
     * Check if text has a contract number
     */
    public boolean hasContractNumber(String text) {
        return extractContractNumber(text) != null;
    }
    
    /**
     * Check if text has a part number
     */
    public boolean hasPartNumber(String text) {
        return extractPartNumber(text) != null;
    }
    
    /**
     * Check if text has customer information
     */
    public boolean hasCustomerInfo(String text) {
        return extractCustomerNumber(text) != null || extractCustomerName(text) != null;
    }
    
    /**
     * Filter out common false positives for names
     */
    private boolean isCommonFalsePositive(String name) {
        String lowerName = name.toLowerCase();
        return lowerName.equals("contract") || lowerName.equals("agreement") ||
               lowerName.equals("part") || lowerName.equals("customer") ||
               lowerName.equals("account") || lowerName.equals("number") ||
               lowerName.equals("date") || lowerName.equals("price") ||
               lowerName.equals("status") || lowerName.equals("terms") ||
               lowerName.length() < 2;
    }
    
    /**
     * Capitalize words in a string
     */
    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) return text;
        
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            String word = words[i];
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * Get extraction count
     */
    public int getExtractionCount() {
        return extractionCount;
    }
    
    /**
     * Reset extraction count
     */
    public void resetExtractionCount() {
        extractionCount = 0;
    }
    
    /**
     * Get extraction statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("extractionsPerformed", extractionCount);
        stats.put("patternsSupported", 6); // contract, part, customer number, customer name, account, created by
        return stats;
    }
    
    /**
     * Test method for entity extraction
     */
    public static void main(String[] args) {
        EntityExtractor extractor = new EntityExtractor();
        
        // Test cases
        String[] testCases = {
            "show contract 12345",
            "display all parts for customer john smith",
            "what is the effective date for contract ABC-789",
            "list parts P12345 for account 567890",
            "show contracts created by vinod kumar",
            "find agreement DEF-456 for customer 123456",
            "display part XYZ-789 pricing information",
            "contracts for account 987654 created on 01/15/2024"
        };
        
        System.out.println("Entity Extraction Test:");
        System.out.println("======================");
        
        for (String test : testCases) {
            Map<String, String> entities = extractor.extractEntities(test);
            System.out.println("Query: " + test);
            System.out.println("Entities: " + entities);
            System.out.println();
        }
        
        System.out.println("Statistics: " + extractor.getStatistics());
    }
}
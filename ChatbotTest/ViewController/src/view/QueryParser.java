package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser{
    
}
//public class QueryParser {
//    
//    // Regex patterns for entity extraction
//    private static final Pattern CONTRACT_NUMBER_PATTERN = Pattern.compile("\\b\\d{6,}\\b");
//    private static final Pattern PART_NUMBER_PATTERN = Pattern.compile("\\b[A-Z]{2}\\d{3,}\\b|\\bpart\\s+(?:number\\s+)?([A-Z0-9]+)\\b", Pattern.CASE_INSENSITIVE);
//    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\baccount\\s+(\\d{8,})\\b", Pattern.CASE_INSENSITIVE);
//    private static final Pattern USER_NAME_PATTERN = Pattern.compile("\\b(john|smith|mary|[a-z]+)\\s+contracts\\b|\\bcontracts\\s+(?:by|created by)\\s+([a-z]+)\\b", Pattern.CASE_INSENSITIVE);
//    private static final Pattern CUSTOMER_NAME_PATTERN = Pattern.compile("\\b(boeing|honeywell|customer\\s+([a-z]+))\\b", Pattern.CASE_INSENSITIVE);
//
//    // Keywords for different query types
//    private static final Map<String, ParsedQuery> QUERY_TYPE_KEYWORDS = new HashMap<>();
//    private static final Map<String, ParsedQuery.ActionType> ACTION_TYPE_KEYWORDS = new HashMap<>();
//    
//    static {
//        // Contract info keywords
//        QUERY_TYPE_KEYWORDS.put("show contract", ParsedQuery.QueryType.CONTRACT_INFO);
//        QUERY_TYPE_KEYWORDS.put("contract details", ParsedQuery.QueryType.CONTRACT_INFO);
//        QUERY_TYPE_KEYWORDS.put("get contract info", ParsedQuery.QueryType.CONTRACT_INFO);
//        QUERY_TYPE_KEYWORDS.put("find contract", ParsedQuery.QueryType.CONTRACT_INFO);
//        
//        // User contract keywords
//        QUERY_TYPE_KEYWORDS.put("contracts", ParsedQuery.QueryType.USER_CONTRACT_QUERY);
//        QUERY_TYPE_KEYWORDS.put("contracts by", ParsedQuery.QueryType.USER_CONTRACT_QUERY);
//        QUERY_TYPE_KEYWORDS.put("contracts created by", ParsedQuery.QueryType.USER_CONTRACT_QUERY);
//        
//        // Status keywords
//        QUERY_TYPE_KEYWORDS.put("status of", ParsedQuery.QueryType.STATUS_CHECK);
//        QUERY_TYPE_KEYWORDS.put("expired contracts", ParsedQuery.QueryType.CONTRACT_STATUS_CHECK);
//        QUERY_TYPE_KEYWORDS.put("active contracts", ParsedQuery.QueryType.CONTRACT_STATUS_CHECK);
//        
//        // Customer keywords
//        QUERY_TYPE_KEYWORDS.put("customer", ParsedQuery.QueryType.CUSTOMER_INFO);
//        QUERY_TYPE_KEYWORDS.put("account", ParsedQuery.QueryType.ACCOUNT_INFO);
//        
//        // Parts keywords
//        QUERY_TYPE_KEYWORDS.put("specifications", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("datasheet", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("compatible parts", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("stock", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("lead time", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("manufacturer", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("warranty", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("issues", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("defects", ParsedQuery.QueryType.PARTS_INFO);
//        QUERY_TYPE_KEYWORDS.put("active or discontinued", ParsedQuery.QueryType.PARTS_INFO);
//        
//        // Parts by contract
//        QUERY_TYPE_KEYWORDS.put("parts", ParsedQuery.QueryType.PARTS_BY_CONTRACT);
//        QUERY_TYPE_KEYWORDS.put("list the parts", ParsedQuery.QueryType.PARTS_BY_CONTRACT);
//        QUERY_TYPE_KEYWORDS.put("show me the parts", ParsedQuery.QueryType.PARTS_BY_CONTRACT);
//        QUERY_TYPE_KEYWORDS.put("how many parts", ParsedQuery.QueryType.PARTS_BY_CONTRACT);
//        
//        // Contracts by part
//        QUERY_TYPE_KEYWORDS.put("contracts associated with part", ParsedQuery.QueryType.CONTRACTS_BY_PART);
//        
//        // Failed parts
//        QUERY_TYPE_KEYWORDS.put("failed parts", ParsedQuery.QueryType.FAILED_PARTS);
//        QUERY_TYPE_KEYWORDS.put("filed parts", ParsedQuery.QueryType.FAILED_PARTS); // Handle typo
//        
//        // Help
//        QUERY_TYPE_KEYWORDS.put("create contract", ParsedQuery.QueryType.HELP_CREATE_CONTRACT);
//        QUERY_TYPE_KEYWORDS.put("help create contract", ParsedQuery.QueryType.HELP_CREATE_CONTRACT);
//        QUERY_TYPE_KEYWORDS.put("how to create contract", ParsedQuery.QueryType.HELP_CREATE_CONTRACT);
//        
//        // Action type keywords
//        ACTION_TYPE_KEYWORDS.put("show", ParsedQuery.ActionType.SHOW);
//        ACTION_TYPE_KEYWORDS.put("details", ParsedQuery.ActionType.DETAILS);
//        ACTION_TYPE_KEYWORDS.put("info", ParsedQuery.ActionType.INFO);
//        ACTION_TYPE_KEYWORDS.put("find", ParsedQuery.ActionType.FIND);
//        ACTION_TYPE_KEYWORDS.put("list", ParsedQuery.ActionType.LIST);
//        ACTION_TYPE_KEYWORDS.put("create", ParsedQuery.ActionType.CREATE);
//        ACTION_TYPE_KEYWORDS.put("status", ParsedQuery.ActionType.CHECK_STATUS);
//        ACTION_TYPE_KEYWORDS.put("specifications", ParsedQuery.ActionType.GET_SPECIFICATIONS);
//        ACTION_TYPE_KEYWORDS.put("datasheet", ParsedQuery.ActionType.GET_DATASHEET);
//        ACTION_TYPE_KEYWORDS.put("compatible", ParsedQuery.ActionType.GET_COMPATIBLE);
//        ACTION_TYPE_KEYWORDS.put("stock", ParsedQuery.ActionType.CHECK_STOCK);
//        ACTION_TYPE_KEYWORDS.put("lead time", ParsedQuery.ActionType.GET_LEAD_TIME);
//        ACTION_TYPE_KEYWORDS.put("manufacturer", ParsedQuery.ActionType.GET_MANUFACTURER);
//        ACTION_TYPE_KEYWORDS.put("warranty", ParsedQuery.ActionType.GET_WARRANTY);
//        ACTION_TYPE_KEYWORDS.put("issues", ParsedQuery.ActionType.CHECK_ISSUES);
//        ACTION_TYPE_KEYWORDS.put("defects", ParsedQuery.ActionType.CHECK_ISSUES);
//        ACTION_TYPE_KEYWORDS.put("active or discontinued", ParsedQuery.ActionType.CHECK_ACTIVE);
//        ACTION_TYPE_KEYWORDS.put("how many", ParsedQuery.ActionType.COUNT_PARTS);
//        ACTION_TYPE_KEYWORDS.put("expired", ParsedQuery.ActionType.CHECK_EXPIRED);
//        ACTION_TYPE_KEYWORDS.put("active", ParsedQuery.ActionType.CHECK_ACTIVE_STATUS);
//    }
//
//    public ParsedQuery parseQuery(String query) {
//        if (query == null || query.trim().isEmpty()) {
//            return createUnknownQuery(query);
//        }
//
//        String normalizedQuery = query.toLowerCase().trim();
//        ParsedQuery parsedQuery = new ParsedQuery();
//        parsedQuery.setOriginalQuery(query);
//
//        // Extract entities first
//        extractEntities(parsedQuery, normalizedQuery);
//
//        // Determine query type and action type
//        determineQueryTypeAndAction(parsedQuery, normalizedQuery);
//
//        // Set confidence based on how well we parsed the query
//        calculateConfidence(parsedQuery, normalizedQuery);
//
//        return parsedQuery;
//    }
//
//    private void extractEntities(ParsedQuery parsedQuery, String query) {
//        List<String> entities = new ArrayList<>();
//
//        // Extract contract number
//        Matcher contractMatcher = CONTRACT_NUMBER_PATTERN.matcher(query);
//        if (contractMatcher.find()) {
//            parsedQuery.setContractNumber(contractMatcher.group());
//            entities.add("contract:" + contractMatcher.group());
//        }
//
//        // Extract part number
//        Matcher partMatcher = PART_NUMBER_PATTERN.matcher(query);
//        if (partMatcher.find()) {
//            String partNumber = partMatcher.group(1) != null ? partMatcher.group(1) : partMatcher.group();
//            parsedQuery.setPartNumber(partNumber);
//            entities.add("part:" + partNumber);
//        }
//
//        // Extract account number
//        Matcher accountMatcher = ACCOUNT_NUMBER_PATTERN.matcher(query);
//        if (accountMatcher.find()) {
//            parsedQuery.setAccountNumber(accountMatcher.group(1));
//            entities.add("account:" + accountMatcher.group(1));
//        }
//
//        // Extract user name
//        Matcher userMatcher = USER_NAME_PATTERN.matcher(query);
//        if (userMatcher.find()) {
//            String userName = userMatcher.group(1) != null ? userMatcher.group(1) : userMatcher.group(2);
//            parsedQuery.setUserName(userName);
//            entities.add("user:" + userName);
//        }
//
//        // Extract customer name
//        Matcher customerMatcher = CUSTOMER_NAME_PATTERN.matcher(query);
//        if (customerMatcher.find()) {
//            String customerName = customerMatcher.group(1);
//            if (customerName.startsWith("customer ")) {
//                customerName = customerMatcher.group(2);
//            }
//            parsedQuery.setCustomerName(customerName);
//            entities.add("customer:" + customerName);
//        }
//
//        parsedQuery.setExtractedEntities(entities);
//    }
//
//    private void determineQueryTypeAndAction(ParsedQuery parsedQuery, String query) {
//        // Check for specific patterns first
//        if (checkContractInfoQueries(parsedQuery, query)) return;
//        if (checkUserContractQueries(parsedQuery, query)) return;
//        if (checkStatusQueries(parsedQuery, query)) return;
//        if (checkCustomerQueries(parsedQuery, query)) return;
//        if (checkPartsQueries(parsedQuery, query)) return;
//        if (checkPartsByContractQueries(parsedQuery, query)) return;
//        if (checkContractsByPartQueries(parsedQuery, query)) return;
//        if (checkFailedPartsQueries(parsedQuery, query)) return;
//        if (checkHelpQueries(parsedQuery, query)) return;
//
//        // Default to unknown
//        parsedQuery.setQueryType(ParsedQuery.QueryType.UNKNOWN);
//        parsedQuery.setActionType(ParsedQuery.ActionType.INFO);
//    }
//
//    private boolean checkContractInfoQueries(ParsedQuery parsedQuery, String query) {
//        if ((query.contains("show contract") || query.contains("contract details") || 
//             query.contains("get contract info") || query.contains("find contract")) &&
//            parsedQuery.getContractNumber() != null) {
//            
//            parsedQuery.setQueryType(ParsedQuery.QueryType.CONTRACT_INFO);
//            
//            if (query.contains("details")) {
//                parsedQuery.setActionType(ParsedQuery.ActionType.DETAILS);
//            } else if (query.contains("show")) {
//                parsedQuery.setActionType(ParsedQuery.ActionType.SHOW);
//            } else if (query.contains("find")) {
//                parsedQuery.setActionType(ParsedQuery.ActionType.FIND);
//            } else {
//                parsedQuery.setActionType(ParsedQuery.ActionType.INFO);
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkUserContractQueries(ParsedQuery parsedQuery, String query) {
//        if (parsedQuery.getUserName() != null && query.contains("contracts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.USER_CONTRACT_QUERY);
//            parsedQuery.setActionType(ParsedQuery.ActionType.LIST);
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkStatusQueries(ParsedQuery parsedQuery, String query) {
//        if (query.contains("status of") && parsedQuery.getContractNumber() != null) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.STATUS_CHECK);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CHECK_STATUS);
//            return true;
//        }
//        
//        if (query.contains("expired contracts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.CONTRACT_STATUS_CHECK);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CHECK_EXPIRED);
//            parsedQuery.setStatusType("expired");
//            return true;
//        }
//        
//        if (query.contains("active contracts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.CONTRACT_STATUS_CHECK);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CHECK_ACTIVE_STATUS);
//            parsedQuery.setStatusType("active");
//            return true;
//        }
//        
//        return false;
//    }
//
//    private boolean checkCustomerQueries(ParsedQuery parsedQuery, String query) {
//        if (parsedQuery.getCustomerName() != null && query.contains("contracts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.CUSTOMER_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.LIST);
//            return true;
//        }
//        
//        if (parsedQuery.getAccountNumber() != null) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.ACCOUNT_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.INFO);
//            return true;
//        }
//        
//        return false;
//    }
//
//    private boolean checkPartsQueries(ParsedQuery parsedQuery, String query) {
//        if (parsedQuery.getPartNumber() == null) return false;
//
//        if (query.contains("specifications")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_SPECIFICATIONS);
//            return true;
//        }
//        
//        if (query.contains("active or discontinued") || query.contains("still active")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CHECK_ACTIVE);
//            return true;
//        }
//        
//        if (query.contains("datasheet")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_DATASHEET);
//            return true;
//        }
//        
//        if (query.contains("compatible parts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_COMPATIBLE);
//            return true;
//        }
//        
//        if (query.contains("stock") || query.contains("available")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CHECK_STOCK);
//            return true;
//        }
//        
//       if (query.contains("lead time")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_LEAD_TIME);
//            return true;
//        }
//        
//        if (query.contains("manufacturer")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_MANUFACTURER);
//            return true;
//        }
//        
//        if (query.contains("issues") || query.contains("defects")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CHECK_ISSUES);
//            return true;
//        }
//        
//        if (query.contains("warranty")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_INFO);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_WARRANTY);
//            return true;
//        }
//        
//        return false;
//    }
//
//    private boolean checkPartsByContractQueries(ParsedQuery parsedQuery, String query) {
//        if (parsedQuery.getContractNumber() == null) return false;
//
//        if (query.contains("show me the parts") || query.contains("list the parts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_BY_CONTRACT);
//            parsedQuery.setActionType(ParsedQuery.ActionType.LIST);
//            return true;
//        }
//        
//        if (query.contains("how many parts")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.PARTS_BY_CONTRACT);
//            parsedQuery.setActionType(ParsedQuery.ActionType.COUNT_PARTS);
//            return true;
//        }
//        
//        return false;
//    }
//
//    private boolean checkContractsByPartQueries(ParsedQuery parsedQuery, String query) {
//        if (query.contains("contracts associated with part") && parsedQuery.getPartNumber() != null) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.CONTRACTS_BY_PART);
//            parsedQuery.setActionType(ParsedQuery.ActionType.GET_CONTRACTS_BY_PART);
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkFailedPartsQueries(ParsedQuery parsedQuery, String query) {
//        if (query.contains("failed parts") || query.contains("filed parts")) {
//            if (parsedQuery.getContractNumber() != null) {
//                parsedQuery.setQueryType(ParsedQuery.QueryType.FAILED_PARTS_BY_CONTRACT);
//            } else {
//                parsedQuery.setQueryType(ParsedQuery.QueryType.FAILED_PARTS);
//            }
//            parsedQuery.setActionType(ParsedQuery.ActionType.LIST);
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkHelpQueries(ParsedQuery parsedQuery, String query) {
//        if (query.contains("create contract") || query.contains("help create contract") || 
//            query.contains("how to create contract") || query.contains("i want to create a contract")) {
//            parsedQuery.setQueryType(ParsedQuery.QueryType.HELP_CREATE_CONTRACT);
//            parsedQuery.setActionType(ParsedQuery.ActionType.CREATE);
//            return true;
//        }
//        return false;
//    }
//
//    private void calculateConfidence(ParsedQuery parsedQuery, String query) {
//        double confidence = 0.0;
//        
//        // Base confidence if we identified a query type
//        if (parsedQuery.getQueryType() != ParsedQuery.QueryType.UNKNOWN) {
//            confidence += 0.5;
//        }
//        
//        // Add confidence for extracted entities
//        if (parsedQuery.getContractNumber() != null) confidence += 0.2;
//        if (parsedQuery.getPartNumber() != null) confidence += 0.2;
//        if (parsedQuery.getUserName() != null) confidence += 0.15;
//        if (parsedQuery.getCustomerName() != null) confidence += 0.15;
//        if (parsedQuery.getAccountNumber() != null) confidence += 0.15;
//        
//        // Add confidence for clear action words
//        if (parsedQuery.getActionType() != null) {
//            confidence += 0.1;
//        }
//        
//        // Reduce confidence for ambiguous queries
//        if (parsedQuery.isAmbiguous()) {
//            confidence -= 0.2;
//        }
//        
//        parsedQuery.setConfidence(Math.min(1.0, confidence));
//    }
//
//    private ParsedQuery createUnknownQuery(String query) {
//        ParsedQuery parsedQuery = new ParsedQuery();
//        parsedQuery.setOriginalQuery(query);
//        parsedQuery.setQueryType(ParsedQuery.QueryType.UNKNOWN);
//        parsedQuery.setActionType(ParsedQuery.ActionType.INFO);
//        parsedQuery.setConfidence(0.0);
//        return parsedQuery;
//    }
//
//    // Utility method to get suggestions for ambiguous queries
//    public List<String> getSuggestions(String query) {
//        List<String> suggestions = new ArrayList<>();
//        
//        if (query == null || query.trim().isEmpty()) {
//            suggestions.add("Try: 'show contract 123456'");
//            suggestions.add("Try: 'list parts for contract 123456'");
//            suggestions.add("Try: 'get specifications for part AE125'");
//            return suggestions;
//        }
//        
//        String normalizedQuery = query.toLowerCase().trim();
//        
//        // Suggest contract queries if numbers are found
//        Matcher contractMatcher = CONTRACT_NUMBER_PATTERN.matcher(normalizedQuery);
//        if (contractMatcher.find()) {
//            String contractNum = contractMatcher.group();
//            suggestions.add("show contract " + contractNum);
//            suggestions.add("status of " + contractNum);
//            suggestions.add("list parts for " + contractNum);
//        }
//        
//        // Suggest part queries if part numbers are found
//        Matcher partMatcher = PART_NUMBER_PATTERN.matcher(normalizedQuery);
//        if (partMatcher.find()) {
//            String partNum = partMatcher.group();
//            suggestions.add("get specifications for " + partNum);
//            suggestions.add("check stock for " + partNum);
//            suggestions.add("get datasheet for " + partNum);
//        }
//        
//        return suggestions;
//    }
//}
package view.practice;

import opennlp.tools.doccat.*;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import java.text.SimpleDateFormat;

import java.util.List;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.ViewObject;
import oracle.jbo.Row;

import java.util.ArrayList;

import oracle.jbo.RowSetIterator;

public class ChatbotIntentProcessor {
    private static final Map<String, ContractCreationSession> activeContractSessions = new HashMap<>();
    private static final Map<String, ChecklistCreationSession> activeChecklistSessions = new HashMap<>();
    // Model paths - adjust based on your project structure
    private static final String CONTRACTS_MODEL_PATH = "./models/en-contracts.bin";
    private static final String PARTS_MODEL_PATH = "./models/en-parts.bin";
    private static final String HELP_MODEL_PATH = "./models/en-help.bin";

    // Cached models for performance
    private static DoccatModel contractsModel;
    private static DoccatModel partsModel;
    private static DoccatModel helpModel;
    private static DocumentCategorizer contractsClassifier;
    private static DocumentCategorizer partsClassifier;
    private static DocumentCategorizer helpClassifier;

    // Pattern matchers
    private static final Pattern CONTRACT_PATTERN = Pattern.compile("\\b\\d{6}\\b");
    private static final Pattern PART_NUMBER_PATTERN =
        Pattern.compile("\\b[A-Z]{2}\\d{5}-\\d{8}\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern CUSTOMER_PATTERN = Pattern.compile("\\b[a-zA-Z]+\\b");

    // Help keywords for quick detection
    private static final String[] HELP_KEYWORDS = {
        "help", "how", "what can", "assistance", "guide", "instructions", "examples", "format", "commands",
        "troubleshoot", "not working"
    };

    // Typo correction mappings
    private static final Map<String, String> TYPO_CORRECTIONS = new HashMap<>();

    public static void initializeHelpModel() {
        if (helpClassifier == null) {
            try {
               
                // Load the help model
                try (FileInputStream helpFis = new FileInputStream(HELP_MODEL_PATH)) {
                    helpModel = new DoccatModel(helpFis);
                    helpClassifier = new DocumentCategorizerME(helpModel);
                    System.out.println(  "Help model loaded successfully");
                }

            } catch (IOException e) {
                System.err.println(  "Error initializing help model: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    static {
        initializeModels();
        initializeTypoCorrections();
    }

    private static void initializeModels() {
        try {
            // Load contracts model
            try (FileInputStream contractsFis = new FileInputStream(CONTRACTS_MODEL_PATH)) {
                contractsModel = new DoccatModel(contractsFis);
                contractsClassifier = new DocumentCategorizerME(contractsModel);
                System.out.println(  "Contracts model loaded successfully");
            }

            // Load parts model
            try (FileInputStream partsFis = new FileInputStream(PARTS_MODEL_PATH)) {
                partsModel = new DoccatModel(partsFis);
                partsClassifier = new DocumentCategorizerME(partsModel);
                System.out.println(  "Parts model loaded successfully");
            }

            // Initialize help model
            initializeHelpModel(); // Load help model

            //            try (FileInputStream helpFis = new FileInputStream(HELP_MODEL_PATH)) {
            //                helpModel = new DoccatModel(helpFis);
            //                helpClassifier = new DocumentCategorizerME(helpModel);
            //                System.out.println(  "Help model loaded successfully");
            //            }

        } catch (IOException e) {
            System.err.println(  "Error loading models: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeTypoCorrections() {
        TYPO_CORRECTIONS.put("contarct", "contract");
        TYPO_CORRECTIONS.put("contrct", "contract");
        TYPO_CORRECTIONS.put("conract", "contract");
        TYPO_CORRECTIONS.put("part", "part");
        TYPO_CORRECTIONS.put("prts", "parts");
        TYPO_CORRECTIONS.put("hlp", "help");
        TYPO_CORRECTIONS.put("serch", "search");
        TYPO_CORRECTIONS.put("find", "find");
        TYPO_CORRECTIONS.put("lok", "look");
        TYPO_CORRECTIONS.put("custmer", "customer");
        TYPO_CORRECTIONS.put("numbr", "number");
    }

    //    /**
    //     * Main method to process user input and determine intent
    //     */
    //    public String processUserMessage(String userInput) {
    //        try {
    //            System.out.println(  "Processing user input: " + userInput);
    //
    //            // Apply typo correction
    //            String correctedInput = applyTypoCorrection(userInput.toLowerCase().trim());
    //            System.out.println(  "Corrected input: " + correctedInput);
    //
    //            // Check if this is a help request first
    //            if (isHelpRequest(correctedInput)) {
    //                return processHelpIntent(correctedInput);
    //            }
    //
    //            // Extract entities
    //            Map<String, String> entities = extractEntities(correctedInput);
    //            System.out.println(  "Extracted entities: " + entities);
    //
    //            // Determine domain (contracts vs parts)
    //            String domain = determineDomain(correctedInput, entities);
    //            System.out.println(  "Determined domain: " + domain);
    //
    //            // Process based on domain
    //            if ("contracts".equals(domain)) {
    //                return processContractsIntent(correctedInput, entities);
    //            } else if ("parts".equals(domain)) {
    //                return processPartsIntent(correctedInput, entities);
    //            } else {
    //                return handleAmbiguousQuery(correctedInput, entities);
    //            }
    //
    //        } catch (Exception e) {
    //            System.err.println(  "Error processing user message: " + e.getMessage());
    //            e.printStackTrace();
    //            return "I apologize, but I encountered an error processing your request. Please try again or rephrase your question.";
    //        }
    //    }

    /**
     * Check if the input is a help request
     */
    private boolean isHelpRequest(String input) {
        // Quick keyword check first
        for (String keyword : HELP_KEYWORDS) {
            if (input.contains(keyword)) {
                if (input.matches(".*\\b" + Pattern.quote(keyword) + "\\b.*")) {
                            return true;
                        }
            }
        }
        return false;
    }

    /**
     * Process help-related intents
     */
    private String processHelpIntent(String input) {
        if (helpClassifier == null) {
            return getHelpInformation(); // Fallback to existing help method
        }
//vinod
        String[] tokens = input.split("\\s+");
        double[] outcomes = helpClassifier.categorize(tokens);
        String intent = helpClassifier.getBestCategory(outcomes);
        double confidence = getMaxConfidence(outcomes);

        System.out.println(input+  "Help Intent: " + intent + " (confidence: " + String.format("%.2f", confidence) + ")");

        switch (intent) {
        case "general_help":
            return getGeneralHelp();
        case "contract_help":
            return getContractHelp();
        case "parts_help":
            return getPartsHelp();
        case "search_help":
            return getSearchHelp();
        case "format_help":
            return getFormatHelp();
        case "troubleshoot":
            return getTroubleshootHelp();
        default:
            return getHelpInformation();
        }
    }

    /**
     * Process contracts-related intents
     */
    private String processContractsIntent(String input, Map<String, String> entities) {
        if (contractsClassifier == null) {
            return handleContractQueryFallback(entities);
        }

        String[] tokens = input.split("\\s+");
        double[] outcomes = contractsClassifier.categorize(tokens);
        String intent = contractsClassifier.getBestCategory(outcomes);
        double confidence = getMaxConfidence(outcomes);

        System.out.println(  "Contract Intent: " + intent + " (confidence: " + String.format("%.2f", confidence) + ")");

        switch (intent) {
        case "search_contract":
            return searchContract(entities);
        case "contract_details":
            return getContractDetails(entities);
        case "contract_status":
            return getContractStatus(entities);
        case "contract_history":
            return getContractHistory(entities);
        case "contract_customer":
            return getContractsByCustomer(entities);
        default:
            return handleContractQueryFallback(entities);
        }
    }

    /**
     * Process parts-related intents
     */
    private String processPartsIntent(String input, Map<String, String> entities) {
        if (partsClassifier == null) {
            return handlePartsQueryFallback(entities);
        }

        String[] tokens = input.split("\\s+");
        double[] outcomes = partsClassifier.categorize(tokens);
        String intent = partsClassifier.getBestCategory(outcomes);
        double confidence = getMaxConfidence(outcomes);

        System.out.println(  "Parts Intent: " + intent + " (confidence: " + String.format("%.2f", confidence) + ")");

        switch (intent) {
        case "search_part":
            return searchPart(entities);
        case "part_details":
            return getPartDetails(entities);
        case "part_availability":
            return getPartAvailability(entities);
        case "part_price":
            return getPartPrice(entities);
        case "part_compatibility":
            return getPartCompatibility(entities);
        default:
            return handlePartsQueryFallback(entities);
        }
    }

    /**
     * Apply typo correction to input
     */
    private String applyTypoCorrection(String input) {
        String corrected = input;
        for (Map.Entry<String, String> correction : TYPO_CORRECTIONS.entrySet()) {
            corrected = corrected.replaceAll("\\b" + correction.getKey() + "\\b", correction.getValue());
        }
        return corrected;
    }

    /**
     * Extract entities from user input
     */
    private Map<String, String> extractEntities(String input) {
        Map<String, String> entities = new HashMap<>();

        // Extract contract numbers
        Matcher contractMatcher = CONTRACT_PATTERN.matcher(input);
        if (contractMatcher.find()) {
            entities.put("contract_number", contractMatcher.group());
        }

        // Extract part numbers
        Matcher partMatcher = PART_NUMBER_PATTERN.matcher(input);
        if (partMatcher.find()) {
            entities.put("part_number", partMatcher.group());
        }

        // Extract customer names (simplified)
        Matcher customerMatcher = CUSTOMER_PATTERN.matcher(input);
        List<String> words = new ArrayList<>();
        while (customerMatcher.find()) {
            String word = customerMatcher.group();
            if (word.length() > 2 && !isCommonWord(word)) {
                words.add(word);
            }
        }
        if (!words.isEmpty()) {
            entities.put("customer", String.join(" ", words));
        }

        return entities;
    }

    /**
     * Determine the domain (contracts or parts) based on input
     */
    private String determineDomain(String input, Map<String, String> entities) {
        int contractScore = 0;
        int partsScore = 0;

        // Check for domain-specific keywords
        if (input.contains("contract") || input.contains("agreement") || entities.containsKey("contract_number")) {
            contractScore += 3;
        }
        if (input.contains("part") || input.contains("component") || entities.containsKey("part_number")) {
            partsScore += 3;
        }

        // Check for action keywords
        if (input.contains("status") || input.contains("history")) {
            contractScore += 1;
        }
        if (input.contains("availability") || input.contains("price") || input.contains("inventory")) {
            partsScore += 1;
        }

        if (contractScore > partsScore) {
            return "contracts";
        } else if (partsScore > contractScore) {
            return "parts";
        } else {
            return "ambiguous";
        }
    }

    /**
     * Handle ambiguous queries
     */
    private String handleAmbiguousQuery(String input, Map<String, String> entities) {
        StringBuilder response = new StringBuilder();
        response.append("I'm not sure if you're asking about contracts or parts. ");

        if (entities.containsKey("contract_number")) {
            response.append("I found a contract number (")
                    .append(entities.get("contract_number"))
                    .append("). ");
            return response.toString() + searchContract(entities);
        } else if (entities.containsKey("part_number")) {
            response.append("I found a part number (")
                    .append(entities.get("part_number"))
                    .append("). ");
            return response.toString() + searchPart(entities);
        } else {
            response.append("Could you please specify if you're looking for:\n");
            response.append("• Contract information (use 'contract' + number)\n");
            response.append("• Parts information (use 'part' + part number)\n");
            response.append("• Type 'help' for more assistance");
            return response.toString();
        }
    }

    /**
     * Get maximum confidence from outcomes array
     */
    private double getMaxConfidence(double[] outcomes) {
        double max = 0.0;
        for (double outcome : outcomes) {
            if (outcome > max) {
                max = outcome;
            }
        }
        return max;
    }

    /**
     * Check if a word is a common word that shouldn't be considered as entity
     */
    private boolean isCommonWord(String word) {
        String[] commonWords = {
            "the", "and", "for", "are", "but", "not", "you", "all", "can", "had", "her", "was", "one", "our", "out",
            "day", "get", "has", "him", "his", "how", "man", "new", "now", "old", "see", "two", "way", "who", "boy",
            "did", "its", "let", "put", "say", "she", "too", "use"
        };
        for (String common : commonWords) {
            if (common.equals(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Help methods
    private String getGeneralHelp() {
        return "I can help you with:\n" + "• Contract searches and information\n" +
               "• Parts lookup and availability\n" + "• Customer information\n" + "• System troubleshooting\n\n" +
               "Try asking: 'Find contract 123456' or 'Search part AB12345-12345678'";
    }

    private String getContractHelp() {
        return "For contract queries, you can:\n" + "• Search by contract number: 'Find contract 123456'\n" +
               "• Get contract status: 'Status of contract 123456'\n" +
               "• View contract history: 'History for contract 123456'\n" +
               "• Find contracts by customer: 'Contracts for CustomerName'\n" +
               "• Contract format: 6-digit numbers (e.g., 123456)";
    }

    private String getPartsHelp() {
        return "For parts queries, you can:\n" + "• Search by part number: 'Find part AB12345-12345678'\n" +
               "• Check availability: 'Availability of part AB12345-12345678'\n" +
               "• Get part price: 'Price for part AB12345-12345678'\n" +
               "• Check compatibility: 'Compatible parts for AB12345-12345678'\n" +
               "• Part format: XX#####-######## (e.g., AB12345-12345678)";
    }

    private String getSearchHelp() {
        return "Search tips:\n" + "• Use specific numbers when possible\n" + "• Contract numbers are 6 digits\n" +
               "• Part numbers follow format: XX#####-########\n" + "• Customer names should be spelled correctly\n" +
               "• Try different keywords if first search doesn't work";
    }

    private String getFormatHelp() {
        return "Correct formats:\n" + "• Contract numbers: 6 digits (123456)\n" +
               "• Part numbers: 2 letters + 5 digits + dash + 8 digits (AB12345-12345678)\n" +
               "• Customer names: Full company or person name\n" + "• Dates: MM/DD/YYYY or DD-MM-YYYY";
    }

    private String getTroubleshootHelp() {
        return "Common issues and solutions:\n" + "• 'No results found': Check number format and spelling\n" +
               "• 'Access denied': Contact your administrator\n" + "• 'System slow': Try simpler queries first\n" +
               "• 'Wrong results': Be more specific with your search terms\n" +
               "• Still having issues? Contact IT support";
    }

    private String getHelpInformation() {
        return   "**Chatbot Help Center**\n\n" + "I can assist you with:\n" +
                 "**Contracts**: Search, status, history, customer contracts\n" +
                 "**Parts**: Lookup, availability, pricing, compatibility\n" +
                 "**Customers**: Find contracts and parts by customer\n\n" + "**Quick Examples:**\n" +
               "• 'Find contract 123456'\n" + "• 'Search part AB12345-12345678'\n" + "• 'Contracts for Acme Corp'\n" +
               "• 'Part availability AB12345-12345678'\n\n" +
               "Type 'help contracts', 'help parts', or 'help format' for specific guidance.";
    }

    // Contract-related methods
    private String searchContract(Map<String, String> entities) {
        try {
            String contractNumber = entities.get("contract_number");
            if (contractNumber == null || contractNumber.isEmpty()) {
                return "Please provide a valid 6-digit contract number. Example: 'Find contract 123456'";
            }

            // Get binding context and iterator
            DCBindingContainer bindings = getBindingContainer();
            if (bindings == null) {
                return "Unable to access contract data. Please try again later.";
            }

            DCIteratorBinding contractIter = bindings.findIteratorBinding("ContractsIterator");
            if (contractIter == null) {
                return "Contract data source not available.";
            }

            ViewObject vo = contractIter.getViewObject();
            vo.setWhereClause("CONTRACT_NUMBER = :contractNum");
            vo.defineNamedWhereClauseParam("contractNum", null, null);
            vo.setNamedWhereClauseParam("contractNum", contractNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                return formatContractResult(row);
            } else {
                return   "Contract " + contractNumber + " not found. Please verify the contract number and try again.";
            }

        } catch (Exception e) {
            System.err.println("Error searching contract: " + e.getMessage());
            return "An error occurred while searching for the contract. Please try again.";
        }
    }

    private String getContractDetails(Map<String, String> entities) {
        String contractNumber = entities.get("contract_number");
        if (contractNumber == null) {
            return "Please specify a contract number to get details.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding contractIter = bindings.findIteratorBinding("ContractsIterator");
            ViewObject vo = contractIter.getViewObject();

            vo.setWhereClause("CONTRACT_NUMBER = :contractNum");
            vo.defineNamedWhereClauseParam("contractNum", null, null);
            vo.setNamedWhereClauseParam("contractNum", contractNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                return formatDetailedContractResult(row);
            } else {
                return "Contract " + contractNumber + " not found.";
            }

        } catch (Exception e) {
            return "Error retrieving contract details: " + e.getMessage();
        }
    }

    private String getContractStatus(Map<String, String> entities) {
        String contractNumber = entities.get("contract_number");
        if (contractNumber == null) {
            return "Please specify a contract number to check status.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding contractIter = bindings.findIteratorBinding("ContractsIterator");
            ViewObject vo = contractIter.getViewObject();

            vo.setWhereClause("CONTRACT_NUMBER = :contractNum");
            vo.defineNamedWhereClauseParam("contractNum", null, null);
            vo.setNamedWhereClauseParam("contractNum", contractNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                String status = (String) row.getAttribute("Status");
                String customer = (String) row.getAttribute("CustomerName");
                Date startDate = (Date) row.getAttribute("StartDate");
                Date endDate = (Date) row.getAttribute("EndDate");

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                return   "**Contract " + contractNumber + " Status**\n" + "Status: " +
                       (status != null ? status : "Unknown") + "\n" + "Customer: " +
                       (customer != null ? customer : "Unknown") + "\n" + "Start Date: " +
                       (startDate != null ? sdf.format(startDate) : "Unknown") + "\n" + "End Date: " +
                       (endDate != null ? sdf.format(endDate) : "Unknown");
            } else {
                return "Contract " + contractNumber + " not found.";
            }

        } catch (Exception e) {
            return "Error retrieving contract status: " + e.getMessage();
        }
    }

    private String getContractHistory(Map<String, String> entities) {
        String contractNumber = entities.get("contract_number");
        if (contractNumber == null) {
            return "Please specify a contract number to view history.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding historyIter = bindings.findIteratorBinding("ContractHistoryIterator");
            ViewObject vo = historyIter.getViewObject();

            vo.setWhereClause("CONTRACT_NUMBER = :contractNum ORDER BY CHANGE_DATE DESC");
            vo.defineNamedWhereClauseParam("contractNum", null, null);
            vo.setNamedWhereClauseParam("contractNum", contractNumber);
            vo.executeQuery();

            StringBuilder history = new StringBuilder();
            history.append(  "**Contract ")
                   .append(contractNumber)
                   .append(" History**\n\n");

            int count = 0;
            while (vo.hasNext() && count < 5) { // Limit to last 5 changes
                Row row = vo.next();
                Date changeDate = (Date) row.getAttribute("ChangeDate");
                String changeType = (String) row.getAttribute("ChangeType");
                String description = (String) row.getAttribute("Description");

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                history.append("• ")
                       .append(sdf.format(changeDate))
                       .append(" - ")
                       .append(changeType)
                       .append(": ")
                       .append(description)
                       .append("\n");
                count++;
            }

            if (count == 0) {
                return "No history found for contract " + contractNumber;
            }

            return history.toString();

        } catch (Exception e) {
            return "Error retrieving contract history: " + e.getMessage();
        }
    }

    private String getContractsByCustomer(Map<String, String> entities) {
        String customer = entities.get("customer");
        if (customer == null) {
            return "Please specify a customer name to search contracts.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding contractIter = bindings.findIteratorBinding("ContractsIterator");
            ViewObject vo = contractIter.getViewObject();

            vo.setWhereClause("UPPER(CUSTOMER_NAME) LIKE UPPER(:customerName)");
            vo.defineNamedWhereClauseParam("customerName", null, null);
            vo.setNamedWhereClauseParam("customerName", "%" + customer + "%");
            vo.executeQuery();

            StringBuilder results = new StringBuilder();
            results.append(  "**Contracts for ")
                   .append(customer)
                   .append("**\n\n");

            int count = 0;
            while (vo.hasNext() && count < 10) { // Limit to 10 results
                Row row = vo.next();
                String contractNum = (String) row.getAttribute("ContractNumber");
                String status = (String) row.getAttribute("Status");
                Date startDate = (Date) row.getAttribute("StartDate");

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                results.append("• Contract ")
                       .append(contractNum)
                       .append(" - Status: ")
                       .append(status)
                       .append(" - Start: ")
                       .append(sdf.format(startDate))
                       .append("\n");
                count++;
            }

            if (count == 0) {
                return "No contracts found for customer: " + customer;
            }

            return results.toString();

        } catch (Exception e) {
            return "Error searching contracts by customer: " + e.getMessage();
        }
    }

    // Parts-related methods
    private String searchPart(Map<String, String> entities) {
        try {
            String partNumber = entities.get("part_number");
            if (partNumber == null || partNumber.isEmpty()) {
                return "Please provide a valid part number. Format: XX#####-######## (e.g., AB12345-12345678)";
            }

            DCBindingContainer bindings = getBindingContainer();
            if (bindings == null) {
                return "Unable to access parts data. Please try again later.";
            }

            DCIteratorBinding partsIter = bindings.findIteratorBinding("PartsIterator");
            if (partsIter == null) {
                return "Parts data source not available.";
            }

            ViewObject vo = partsIter.getViewObject();
            vo.setWhereClause("PART_NUMBER = :partNum");
            vo.defineNamedWhereClauseParam("partNum", null, null);
            vo.setNamedWhereClauseParam("partNum", partNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                return formatPartResult(row);
            } else {
                return   "Part " + partNumber + " not found. Please verify the part number format and try again.";
            }

        } catch (Exception e) {
            System.err.println("Error searching part: " + e.getMessage());
            return "An error occurred while searching for the part. Please try again.";
        }
    }

    private String getPartDetails(Map<String, String> entities) {
        String partNumber = entities.get("part_number");
        if (partNumber == null) {
            return "Please specify a part number to get details.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding partsIter = bindings.findIteratorBinding("PartsIterator");
            ViewObject vo = partsIter.getViewObject();

            vo.setWhereClause("PART_NUMBER = :partNum");
            vo.defineNamedWhereClauseParam("partNum", null, null);
            vo.setNamedWhereClauseParam("partNum", partNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                return formatDetailedPartResult(row);
            } else {
                return "Part " + partNumber + " not found.";
            }

        } catch (Exception e) {
            return "Error retrieving part details: " + e.getMessage();
        }
    }

    private String getPartAvailability(Map<String, String> entities) {
        String partNumber = entities.get("part_number");
        if (partNumber == null) {
            return "Please specify a part number to check availability.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding partsIter = bindings.findIteratorBinding("PartsIterator");
            ViewObject vo = partsIter.getViewObject();

            vo.setWhereClause("PART_NUMBER = :partNum");
            vo.defineNamedWhereClauseParam("partNum", null, null);
            vo.setNamedWhereClauseParam("partNum", partNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                String description = (String) row.getAttribute("Description");
                Integer quantity = (Integer) row.getAttribute("QuantityOnHand");
                String status = (String) row.getAttribute("Status");
                String location = (String) row.getAttribute("Location");

                return   "**Part " + partNumber + " Availability**\n" + "Description: " +
                       (description != null ? description : "Unknown") + "\n" + "Quantity on Hand: " +
                       (quantity != null ? quantity.toString() : "Unknown") + "\n" + "Status: " +
                       (status != null ? status : "Unknown") + "\n" + "Location: " +
                       (location != null ? location : "Unknown");
            } else {
                return "Part " + partNumber + " not found.";
            }

        } catch (Exception e) {
            return "Error checking part availability: " + e.getMessage();
        }
    }

    private String getPartPrice(Map<String, String> entities) {
        String partNumber = entities.get("part_number");
        if (partNumber == null) {
            return "Please specify a part number to get pricing information.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding partsIter = bindings.findIteratorBinding("PartsIterator");
            ViewObject vo = partsIter.getViewObject();

            vo.setWhereClause("PART_NUMBER = :partNum");
            vo.defineNamedWhereClauseParam("partNum", null, null);
            vo.setNamedWhereClauseParam("partNum", partNumber);
            vo.executeQuery();

            if (vo.hasNext()) {
                Row row = vo.next();
                String description = (String) row.getAttribute("Description");
                Double unitPrice = (Double) row.getAttribute("UnitPrice");
                Double listPrice = (Double) row.getAttribute("ListPrice");
                String currency = (String) row.getAttribute("Currency");

                StringBuilder priceInfo = new StringBuilder();
                priceInfo.append(  "**Part ")
                         .append(partNumber)
                         .append(" Pricing**\n");
                priceInfo.append("Description: ")
                         .append(description != null ? description : "Unknown")
                         .append("\n");

                if (unitPrice != null) {
                    priceInfo.append("Unit Price: ")
                             .append(currency != null ? currency : "$")
                             .append(String.format("%.2f", unitPrice))
                             .append("\n");
                }
                if (listPrice != null) {
                    priceInfo.append("List Price: ")
                             .append(currency != null ? currency : "$")
                             .append(String.format("%.2f", listPrice))
                             .append("\n");
                }

                return priceInfo.toString();
            } else {
                return "Part " + partNumber + " not found.";
            }

        } catch (Exception e) {
            return "Error retrieving part pricing: " + e.getMessage();
        }
    }

    private String getPartCompatibility(Map<String, String> entities) {
        String partNumber = entities.get("part_number");
        if (partNumber == null) {
            return "Please specify a part number to check compatibility.";
        }

        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding compatIter = bindings.findIteratorBinding("PartCompatibilityIterator");
            ViewObject vo = compatIter.getViewObject();

            vo.setWhereClause("PART_NUMBER = :partNum OR COMPATIBLE_PART = :partNum");
            vo.defineNamedWhereClauseParam("partNum", null, null);
            vo.setNamedWhereClauseParam("partNum", partNumber);
            vo.executeQuery();

            StringBuilder compatibility = new StringBuilder();
            compatibility.append(  "**Part ")
                         .append(partNumber)
                         .append(" Compatibility**\n\n");

            int count = 0;
            while (vo.hasNext() && count < 10) {
                Row row = vo.next();
                String compatiblePart = (String) row.getAttribute("CompatiblePart");
                String compatibilityType = (String) row.getAttribute("CompatibilityType");
                String notes = (String) row.getAttribute("Notes");

                compatibility.append("• ")
                             .append(compatiblePart)
                             .append(" (")
                             .append(compatibilityType != null ? compatibilityType : "Standard")
                             .append(")");
                if (notes != null && !notes.isEmpty()) {
                    compatibility.append(" - ").append(notes);
                }
                compatibility.append("\n");
                count++;
            }

            if (count == 0) {
                return "No compatibility information found for part " + partNumber;
            }

            return compatibility.toString();

        } catch (Exception e) {
            return "Error retrieving part compatibility: " + e.getMessage();
        }
    }

    // Fallback methods
    private String handleContractQueryFallback(Map<String, String> entities) {
        if (entities.containsKey("contract_number")) {
            return searchContract(entities);
        } else if (entities.containsKey("customer")) {
            return getContractsByCustomer(entities);
        } else {
            return "I can help you with contract information. Please provide:\n" +
                   "• A 6-digit contract number (e.g., 'Find contract 123456')\n" +
                   "• A customer name (e.g., 'Contracts for Acme Corp')\n" + "• Type 'help contracts' for more options";
        }
    }

    private String handlePartsQueryFallback(Map<String, String> entities) {
        if (entities.containsKey("part_number")) {
            return searchPart(entities);
        } else {
            return "I can help you with parts information. Please provide:\n" +
                   "• A part number in format XX#####-######## (e.g., 'Find part AB12345-12345678')\n" +
                   "• Type 'help parts' for more options";
        }
    }

    // Formatting methods
    private String formatContractResult(Row row) {
        try {
            String contractNumber = (String) row.getAttribute("ContractNumber");
            String customerName = (String) row.getAttribute("CustomerName");
            String status = (String) row.getAttribute("Status");
            Date startDate = (Date) row.getAttribute("StartDate");
            Date endDate = (Date) row.getAttribute("EndDate");
            Double contractValue = (Double) row.getAttribute("ContractValue");

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            StringBuilder result = new StringBuilder();
            result.append(  "**Contract Found**\n\n");
            result.append(  "Contract Number: ")
                  .append(contractNumber)
                  .append("\n");
            result.append(  "Customer: ")
                  .append(customerName != null ? customerName : "Unknown")
                  .append("\n");
            result.append(  "Status: ")
                  .append(status != null ? status : "Unknown")
                  .append("\n");

            if (startDate != null) {
                result.append(  "Start Date: ")
                      .append(sdf.format(startDate))
                      .append("\n");
            }
            if (endDate != null) {
                result.append(  "End Date: ")
                      .append(sdf.format(endDate))
                      .append("\n");
            }
            if (contractValue != null) {
                result.append(  "Value: $")
                      .append(String.format("%.2f", contractValue))
                      .append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "Error formatting contract result: " + e.getMessage();
        }
    }

    private String formatDetailedContractResult(Row row) {
        try {
            String basicInfo = formatContractResult(row);

            // Add additional details
            String description = (String) row.getAttribute("Description");
            String contactPerson = (String) row.getAttribute("ContactPerson");
            String terms = (String) row.getAttribute("Terms");
            String notes = (String) row.getAttribute("Notes");

            StringBuilder detailed = new StringBuilder(basicInfo);
            detailed.append("\n**Additional Details:**\n");

            if (description != null && !description.isEmpty()) {
                detailed.append(  "Description: ")
                        .append(description)
                        .append("\n");
            }
            if (contactPerson != null && !contactPerson.isEmpty()) {
                detailed.append(  "Contact: ")
                        .append(contactPerson)
                        .append("\n");
            }
            if (terms != null && !terms.isEmpty()) {
                detailed.append(  "Terms: ")
                        .append(terms)
                        .append("\n");
            }
            if (notes != null && !notes.isEmpty()) {
                detailed.append(  "Notes: ")
                        .append(notes)
                        .append("\n");
            }

            return detailed.toString();

        } catch (Exception e) {
            return "Error formatting detailed contract result: " + e.getMessage();
        }
    }

    private String formatPartResult(Row row) {
        try {
            String partNumber = (String) row.getAttribute("PartNumber");
            String description = (String) row.getAttribute("Description");
            String category = (String) row.getAttribute("Category");
            Integer quantityOnHand = (Integer) row.getAttribute("QuantityOnHand");
            String status = (String) row.getAttribute("Status");
            Double unitPrice = (Double) row.getAttribute("UnitPrice");

            StringBuilder result = new StringBuilder();
            result.append(  "**Part Found**\n\n");
            result.append(  "Part Number: ")
                  .append(partNumber)
                  .append("\n");
            result.append(  "Description: ")
                  .append(description != null ? description : "Unknown")
                  .append("\n");
            result.append(  "Category: ")
                  .append(category != null ? category : "Unknown")
                  .append("\n");
            result.append(  "Status: ")
                  .append(status != null ? status : "Unknown")
                  .append("\n");

            if (quantityOnHand != null) {
                result.append(  "Quantity: ")
                      .append(quantityOnHand)
                      .append("\n");
            }
            if (unitPrice != null) {
                result.append(  "Unit Price: $")
                      .append(String.format("%.2f", unitPrice))
                      .append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "Error formatting part result: " + e.getMessage();
        }
    }

    private String formatDetailedPartResult(Row row) {
        try {
            String basicInfo = formatPartResult(row);

            // Add additional details
            String manufacturer = (String) row.getAttribute("Manufacturer");
            String model = (String) row.getAttribute("Model");
            String specifications = (String) row.getAttribute("Specifications");
            String location = (String) row.getAttribute("Location");
            Date lastUpdated = (Date) row.getAttribute("LastUpdated");

            StringBuilder detailed = new StringBuilder(basicInfo);
            detailed.append("\n**Additional Details:**\n");

            if (manufacturer != null && !manufacturer.isEmpty()) {
                detailed.append(  "Manufacturer: ")
                        .append(manufacturer)
                        .append("\n");
            }
            if (model != null && !model.isEmpty()) {
                detailed.append(  "Model: ")
                        .append(model)
                        .append("\n");
            }
            if (specifications != null && !specifications.isEmpty()) {
                detailed.append(  "Specifications: ")
                        .append(specifications)
                        .append("\n");
            }
            if (location != null && !location.isEmpty()) {
                detailed.append(  "Location: ")
                        .append(location)
                        .append("\n");
            }
            if (lastUpdated != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                detailed.append(  "Last Updated: ")
                        .append(sdf.format(lastUpdated))
                        .append("\n");
            }

            return detailed.toString();

        } catch (Exception e) {
            return "Error formatting detailed part result: " + e.getMessage();
        }
    }

    // Utility methods
    private DCBindingContainer getBindingContainer() {
        try {
            BindingContext bindingContext = BindingContext.getCurrent();
            return (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
        } catch (Exception e) {
            System.err.println("Error getting binding container: " + e.getMessage());
            return null;
        }
    }

    /**
     * Public method to reload models if needed
     */
    public static void reloadModels() {
        System.out.println(  "Reloading NLP models...");
        initializeModels();
    }

    /**
     * Check if models are loaded and ready
     */
    public static boolean areModelsReady() {
        return contractsClassifier != null && partsClassifier != null && helpClassifier != null;
    }

    /**
     * Get model status information
     */
    public static String getModelStatus() {
        StringBuilder status = new StringBuilder();
        status.append(  "**Model Status**\n");
        status.append("Contracts Model: ")
              .append(contractsClassifier != null ?   "Loaded" :   "Not Loaded")
              .append("\n");
        status.append("Parts Model: ")
              .append(partsClassifier != null ?   "Loaded" :   "Not Loaded")
              .append("\n");
        status.append("Help Model: ")
              .append(helpClassifier != null ?   "Loaded" :   "Not Loaded")
              .append("\n");
        return status.toString();
    }

    /**
     * Add custom typo correction
     */
    public static void addTypoCorrection(String typo, String correction) {
        TYPO_CORRECTIONS.put(typo.toLowerCase(), correction.toLowerCase());
        System.out.println("Added typo correction: " + typo + " -> " + correction);
    }

    /**
     * Process batch messages for testing
     */
    public List<String> processBatchMessages(List<String> messages) {
        List<String> responses = new ArrayList<>();
        for (String message : messages) {
            responses.add(processUserMessage(message));
        }
        return responses;
    }

    /**
     * Get processing statistics
     */
    public String getProcessingStats() {
        return   "**Processing Statistics**\n" + "Models Status: " + (areModelsReady() ? "All Ready" : "Some Missing") +
               "\n" + "Typo Corrections: " + TYPO_CORRECTIONS.size() + " entries\n" + "Help Keywords: " +
               HELP_KEYWORDS.length + " keywords\n" + "Pattern Matchers: 3 active (Contract, Part, Customer)";
    }

    // Add these inner classes for session management

    public static class ContractCreationSession {
        private String sessionId;
        private String accountNumber;
        private String contractName;
        private String projectType;
        private String comments;
        private String description;
        private String isPrice;
        private int currentStep;
        private long lastActivity;

        public ContractCreationSession(String sessionId) {
            this.sessionId = sessionId;
            this.currentStep = 1;
            this.lastActivity = System.currentTimeMillis();
        }

        // Getters and setters
        public String getSessionId() {
            return sessionId;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getContractName() {
            return contractName;
        }

        public void setContractName(String contractName) {
            this.contractName = contractName;
        }

        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIsPrice() {
            return isPrice;
        }

        public void setIsPrice(String isPrice) {
            this.isPrice = isPrice;
        }

        public int getCurrentStep() {
            return currentStep;
        }

        public void setCurrentStep(int currentStep) {
            this.currentStep = currentStep;
        }

        public long getLastActivity() {
            return lastActivity;
        }

        public void updateActivity() {
            this.lastActivity = System.currentTimeMillis();
        }

        public boolean isComplete() {
            return accountNumber != null && contractName != null && projectType != null && comments != null &&
                   description != null && isPrice != null;
        }
    }


    // Add contract creation patterns
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("\\b\\d{9}\\b");
    private static final String[] CONTRACT_CREATION_KEYWORDS = {
        "create contract", "create a contract", "help to create", "can you create", "new contract", "make contract",
        "generate contract", "contract creation"
    };

    // Update your processUserMessage method to handle contract creation
    public String processUserMessage(String userInput) {
        return processUserMessage(userInput, generateSessionId());
    }

    public String processUserMessage(String userInput, String sessionId) {
        try {
            System.out.println(  "Processing user input: " + userInput + " (Session: " + sessionId + ")");
/**
            // Clean up old sessions (older than 30 minutes)
            cleanupOldSessions();

            // Check if user is in an active contract creation session
            if (activeContractSessions.containsKey(sessionId)) {
                return handleContractCreationFlow(userInput, sessionId);
            }

            // Check if user is in an active checklist creation session
            if (activeChecklistSessions.containsKey(sessionId)) {
                return handleChecklistCreationFlow(userInput, sessionId);
            }
*/
            // Apply typo correction
            String correctedInput = applyTypoCorrection(userInput.toLowerCase().trim());
            System.out.println("Corrected input: " + correctedInput);

            // Check if this is a contract creation request
            if (isContractCreationRequest(correctedInput)) {
                return initiateContractCreation(correctedInput, sessionId);
            }

            // Check if this is a help request
            if (isHelpRequest(correctedInput)) {
                return processHelpIntent(correctedInput);
            }

            // Extract entities
            Map<String, String> entities = extractEntities(correctedInput);
            System.out.println(  "Extracted entities: " + entities);

            // Determine domain (contracts vs parts)
            String domain = determineDomain(correctedInput, entities);
            System.out.println(  "Determined domain: " + domain);

            // Process based on domain
            if ("contracts".equals(domain)) {
                return processContractsIntent(correctedInput, entities);
            } else if ("parts".equals(domain)) {
                return processPartsIntent(correctedInput, entities);
            } else {
                return handleAmbiguousQuery(correctedInput, entities);
            }

        } catch (Exception e) {
            System.err.println(  "Error processing user message: " + e.getMessage());
            e.printStackTrace();
            return "I apologize, but I encountered an error processing your request. Please try again or rephrase your question.";
        }
    }

    /**
     * Check if the input is a contract creation request
     */
    private boolean isContractCreationRequest(String input) {
        for (String keyword : CONTRACT_CREATION_KEYWORDS) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initiate contract creation process
     */
    private String initiateContractCreation(String input, String sessionId) {
        ContractCreationSession session = new ContractCreationSession(sessionId);

        // Check if account number is provided in the initial request
        Matcher accountMatcher = ACCOUNT_NUMBER_PATTERN.matcher(input);
        if (accountMatcher.find()) {
            session.setAccountNumber(accountMatcher.group());
            session.setCurrentStep(2); // Skip account number step
        }

        activeContractSessions.put(sessionId, session);

        StringBuilder response = new StringBuilder();
        response.append(  "**Contract Creation Started**\n\n");
        response.append("I'll help you create a new contract. I need the following information:\n\n");

        if (session.getAccountNumber() != null) {
            response.append(  "Account Number: ")
                    .append(session.getAccountNumber())
                    .append("\n");
            response.append(  "Contract Name\n");
            response.append("?? Project Type\n");
            response.append(  "Comments\n");
            response.append(  "Description\n");
            response.append(  "Is Price (Yes/No)\n\n");
            response.append("Please provide the **Contract Name**:");
        } else {
            response.append(  "Account Number (9 digits)\n");
            response.append(  "Contract Name\n");
            response.append("?? Project Type\n");
            response.append(  "Comments\n");
            response.append(  "Description\n");
            response.append(  "Is Price (Yes/No)\n\n");
            response.append("Let's start! Please provide the **Account Number** (9 digits):");
        }

        return response.toString();
    }

    /**
     * Handle contract creation flow
     */
    private String handleContractCreationFlow(String userInput, String sessionId) {
        ContractCreationSession session = activeContractSessions.get(sessionId);
        session.updateActivity();

        String input = userInput.trim();

        switch (session.getCurrentStep()) {
        case 1: // Account Number
            if (isValidAccountNumber(input)) {
                session.setAccountNumber(input);
                session.setCurrentStep(2);
                return   "Account Number saved: " + input + "\n\nPlease provide the **Contract Name**:";
            } else {
                return   "Invalid account number format. Please provide a 9-digit account number:";
            }

        case 2: // Contract Name
            if (input.length() >= 3) {
                session.setContractName(input);
                session.setCurrentStep(3);
                return   "Contract Name saved: " + input + "\n\nPlease provide the **Project Type**:";
            } else {
                return   "Contract name must be at least 3 characters. Please provide the **Contract Name**:";
            }

        case 3: // Project Type
            if (input.length() >= 2) {
                session.setProjectType(input);
                session.setCurrentStep(4);
                return   "Project Type saved: " + input + "\n\nPlease provide **Comments**:";
            } else {
                return   "Project type must be at least 2 characters. Please provide the **Project Type**:";
            }

        case 4: // Comments
            session.setComments(input);
            session.setCurrentStep(5);
            return   "Comments saved: " + input + "\n\nPlease provide the **Description**:";

        case 5: // Description
            session.setDescription(input);
            session.setCurrentStep(6);
            return   "Description saved: " + input + "\n\nIs this a priced contract? Please answer **Yes** or **No**:";

        case 6: // Is Price
            String priceAnswer = input.toLowerCase();
            if (priceAnswer.equals("yes") || priceAnswer.equals("no") || priceAnswer.equals("y") ||
                priceAnswer.equals("n")) {

                session.setIsPrice(priceAnswer.startsWith("y") ? "Yes" : "No");

                // All information collected, create the contract
                String contractResult = automateContractCreate(session);

                // Remove the session
                activeContractSessions.remove(sessionId);

                return contractResult;
            } else {
                return   "Please answer **Yes** or **No** for the price question:";
            }

        default:
            activeContractSessions.remove(sessionId);
            return   "Session error. Please start over with 'create contract'.";
        }
    }

    /**
     * Validate account number format
     */
    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{9}");
    }

    /**
     * Automate contract creation
     */
    private String automateContractCreate(ContractCreationSession session) {
        try {
            System.out.println(  "Creating contract with session data...");

            // Get binding context for database operations
            DCBindingContainer bindings = getBindingContainer();
            if (bindings == null) {
                return   "Unable to access contract creation system. Please try again later.";
            }

            // Get the contracts view object for creation
            DCIteratorBinding contractIter = bindings.findIteratorBinding("ContractsIterator");
            if (contractIter == null) {
                return   "Contract creation system not available.";
            }

            ViewObject vo = contractIter.getViewObject();

            // Create new contract row
            Row newContract = vo.createRow();

            // Generate new contract number (6 digits)
            String newContractNumber = generateContractNumber();

            // Set contract attributes
            newContract.setAttribute("ContractNumber", newContractNumber);
            newContract.setAttribute("AccountNumber", session.getAccountNumber());
            newContract.setAttribute("ContractName", session.getContractName());
            newContract.setAttribute("ProjectType", session.getProjectType());
            newContract.setAttribute("Comments", session.getComments());
            newContract.setAttribute("Description", session.getDescription());
            newContract.setAttribute("IsPrice", session.getIsPrice());
            newContract.setAttribute("Status", "Draft");
            newContract.setAttribute("CreatedDate", new Date());
            newContract.setAttribute("CreatedBy", "Chatbot");

            // Insert the row
            vo.insertRow(newContract);

            // Commit the transaction
            bindings.getDataControl().commitTransaction();

            StringBuilder result = new StringBuilder();
            result.append(  "**Contract Created Successfully!**\n\n");
            result.append(  "**Contract Details:**\n");
            result.append(  "Contract Number: ")
                  .append(newContractNumber)
                  .append("\n");
            result.append(  "Account Number: ")
                  .append(session.getAccountNumber())
                  .append("\n");
            result.append(  "Contract Name: ")
                  .append(session.getContractName())
                  .append("\n");
            result.append("?? Project Type: ")
                  .append(session.getProjectType())
                  .append("\n");
            result.append(  "Comments: ")
                  .append(session.getComments())
                  .append("\n");
            result.append(  "Description: ")
                  .append(session.getDescription())
                  .append("\n");
            result.append(  "Is Price: ")
                  .append(session.getIsPrice())
                  .append("\n");
            result.append(  "Status: Draft\n\n");
            result.append(  "**Do you want me to create a Checklist for this contract?**\n");
            result.append("Please reply **Yes** or **No**");

            // Store contract number for potential checklist creation
            storeContractForChecklist(generateSessionId(), newContractNumber);

            return result.toString();

        } catch (Exception e) {
            System.err.println(  "Error creating contract: " + e.getMessage());
            e.printStackTrace();
            return   "An error occurred while creating the contract. Please contact support or try again later.";
        }
    }

    /**
     * Generate a unique 6-digit contract number
     */
    private String generateContractNumber() {
        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding contractIter = bindings.findIteratorBinding("ContractsIterator");
            ViewObject vo = contractIter.getViewObject();

            // Get the highest existing contract number
            vo.setWhereClause("CONTRACT_NUMBER IS NOT NULL");
            vo.setOrderByClause("CONTRACT_NUMBER DESC");
            vo.executeQuery();

            int maxNumber = 100000; // Start from 100000 if no contracts exist
            if (vo.hasNext()) {
                Row row = vo.next();
                Object contractNumberObj = row.getAttribute("ContractNumber");
                if (contractNumberObj != null) {
                    maxNumber = Integer.parseInt(contractNumberObj.toString());
                }
            }

            // Generate next contract number
            return String.format("%06d", maxNumber + 1);

        } catch (Exception e) {
            System.err.println("Error generating contract number: " + e.getMessage());
            // Fallback to timestamp-based number
            return String.format("%06d", (int) (System.currentTimeMillis() % 1000000));
        }
    }

    /**
     * Store contract number for potential checklist creation
     */
    private void storeContractForChecklist(String sessionId, String contractNumber) {
        // This will be used when user responds to checklist creation question
        System.out.println(  "Contract " + contractNumber + " ready for potential checklist creation");
    }

    /**
     * Handle checklist creation flow
     */
    private String handleChecklistCreationFlow(String userInput, String sessionId) {
        ChecklistCreationSession session = activeChecklistSessions.get(sessionId);
        session.updateActivity();

        String input = userInput.trim();

        switch (session.getCurrentStep()) {
        case 1: // System Date
            if (isValidDate(input)) {
                session.setSystemDate(input);
                session.setCurrentStep(2);
                return   "System Date saved: " + input +
                       "\n\nPlease provide the **Effective Date** (MM/DD/YYYY or YYYY-MM-DD):";
            } else {
                return   "Invalid date format. Please provide the **System Date** in MM/DD/YYYY or YYYY-MM-DD format:";
            }

        case 2: // Effective Date
            if (isValidDate(input)) {
                session.setEffectiveDate(input);
                session.setCurrentStep(3);
                return   "Effective Date saved: " + input +
                       "\n\nPlease provide the **Expiration Date** (MM/DD/YYYY or YYYY-MM-DD):";
            } else {
                return   "Invalid date format. Please provide the **Effective Date** in MM/DD/YYYY or YYYY-MM-DD format:";
            }

        case 3: // Expiration Date
            if (isValidDate(input)) {
                session.setExpirationDate(input);
                session.setCurrentStep(4);
                return   "Expiration Date saved: " + input +
                       "\n\nPlease provide the **Price Expiration Date** (MM/DD/YYYY or YYYY-MM-DD):";
            } else {
                return   "Invalid date format. Please provide the **Expiration Date** in MM/DD/YYYY or YYYY-MM-DD format:";
            }

        case 4: // Price Expiration Date
            if (isValidDate(input)) {
                session.setPriceExpirationDate(input);

                // All information collected, create the checklist
                String checklistResult = automateCheckList(session);

                // Remove the session
                activeChecklistSessions.remove(sessionId);

                return checklistResult;
            } else {
                return   "Invalid date format. Please provide the **Price Expiration Date** in MM/DD/YYYY or YYYY-MM-DD format:";
            }

        default:
            activeChecklistSessions.remove(sessionId);
            return   "Session error. Please start over.";
        }
    }

    /**
     * Validate date format
     */
    private boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }

        // Support multiple date formats
        String[] dateFormats = { "MM/dd/yyyy", "MM/dd/yy", "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy" };

        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                sdf.parse(dateStr.trim());
                return true;
            } catch (ParseException e) {
                // Continue to next format
            }
        }

        return false;
    }

    /**
     * Automate checklist creation
     */
    private String automateCheckList(ChecklistCreationSession session) {
        try {
            System.out.println(  "Creating checklist with session data...");

            // Get binding context for database operations
            DCBindingContainer bindings = getBindingContainer();
            if (bindings == null) {
                return   "Unable to access checklist creation system. Please try again later.";
            }

            // Get the checklist view object for creation
            DCIteratorBinding checklistIter = bindings.findIteratorBinding("ChecklistIterator");
            if (checklistIter == null) {
                return   "Checklist creation system not available.";
            }

            ViewObject vo = checklistIter.getViewObject();

            // Create new checklist row
            Row newChecklist = vo.createRow();

            // Generate new checklist ID
            String newChecklistId = generateChecklistId();

            // Parse dates to proper format
            Date systemDate = parseDate(session.getSystemDate());
            Date effectiveDate = parseDate(session.getEffectiveDate());
            Date expirationDate = parseDate(session.getExpirationDate());
            Date priceExpirationDate = parseDate(session.getPriceExpirationDate());

            // Set checklist attributes
            newChecklist.setAttribute("ChecklistId", newChecklistId);
            newChecklist.setAttribute("ContractNumber", session.getContractNumber());
            newChecklist.setAttribute("SystemDate", systemDate);
            newChecklist.setAttribute("EffectiveDate", effectiveDate);
            newChecklist.setAttribute("ExpirationDate", expirationDate);
            newChecklist.setAttribute("PriceExpirationDate", priceExpirationDate);
            newChecklist.setAttribute("Status", "Active");
            newChecklist.setAttribute("CreatedDate", new Date());
            newChecklist.setAttribute("CreatedBy", "Chatbot");

            // Insert the row
            vo.insertRow(newChecklist);

            // Commit the transaction
            bindings.getDataControl().commitTransaction();

            StringBuilder result = new StringBuilder();
            result.append(  "**Checklist Created Successfully!**\n\n");
            result.append(  "**Checklist Details:**\n");
            result.append(  "Checklist ID: ")
                  .append(newChecklistId)
                  .append("\n");
            result.append(  "Contract Number: ")
                  .append(session.getContractNumber())
                  .append("\n");
            result.append("?? System Date: ")
                  .append(session.getSystemDate())
                  .append("\n");
            result.append(  "Effective Date: ")
                  .append(session.getEffectiveDate())
                  .append("\n");
            result.append(  "Expiration Date: ")
                  .append(session.getExpirationDate())
                  .append("\n");
            result.append(  "Price Expiration Date: ")
                  .append(session.getPriceExpirationDate())
                  .append("\n");
            result.append(  "Status: Active\n\n");
            result.append(  "**Both Contract and Checklist have been created successfully!**\n\n");
            result.append(  "**Ready for new requests!** How can I help you next?");

            return result.toString();

        } catch (Exception e) {
            System.err.println(  "Error creating checklist: " + e.getMessage());
            e.printStackTrace();
            return   "An error occurred while creating the checklist. Please contact support or try again later.";
        }
    }

    /**
     * Generate a unique checklist ID
     */
    private String generateChecklistId() {
        try {
            DCBindingContainer bindings = getBindingContainer();
            DCIteratorBinding checklistIter = bindings.findIteratorBinding("ChecklistIterator");
            ViewObject vo = checklistIter.getViewObject();

            // Get the highest existing checklist ID
            vo.setWhereClause("CHECKLIST_ID IS NOT NULL");
            vo.setOrderByClause("CHECKLIST_ID DESC");
            vo.executeQuery();

            int maxId = 1000; // Start from 1000 if no checklists exist

            if (vo.hasNext()) {
                Row row = vo.next();
                Object checklistIdObj = row.getAttribute("ChecklistId");
                if (checklistIdObj != null) {
                    maxId = Integer.parseInt(checklistIdObj.toString());
                }
            }

            // Generate next checklist ID
            return String.valueOf(maxId + 1);

        } catch (Exception e) {
            System.err.println("Error generating checklist ID: " + e.getMessage());
            // Fallback to timestamp-based ID
            return String.valueOf(System.currentTimeMillis() % 100000);
        }
    }

    /**
     * Parse date string to Date object
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        String[] dateFormats = { "MM/dd/yyyy", "MM/dd/yy", "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy" };

        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.parse(dateStr.trim());
            } catch (ParseException e) {
                // Continue to next format
            }
        }

        return null;
    }

    /**
     * Generate session ID
     */
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
    }

    /**
     * Clean up old sessions (older than 30 minutes)
     */
    private void cleanupOldSessions() {
        long currentTime = System.currentTimeMillis();
        long thirtyMinutes = 30 * 60 * 1000;

        // Clean up contract sessions
        activeContractSessions.entrySet()
            .removeIf(entry -> (currentTime - entry.getValue().getLastActivity()) > thirtyMinutes);

        // Clean up checklist sessions
        activeChecklistSessions.entrySet()
            .removeIf(entry -> (currentTime - entry.getValue().getLastActivity()) > thirtyMinutes);

        System.out.println(  "Session cleanup completed. Active contract sessions: " + activeContractSessions.size() +
                           ", Active checklist sessions: " + activeChecklistSessions.size());
    }

    // Add missing fields and methods to ChecklistCreationSession class

    public static class ChecklistCreationSession {
        private String sessionId;
        private String contractNumber;
        private String systemDate;
        private String effectiveDate;
        private String expirationDate;
        private String priceExpirationDate;
        private int currentStep;
        private long lastActivity;

        public ChecklistCreationSession(String sessionId, String contractNumber) {
            this.sessionId = sessionId;
            this.contractNumber = contractNumber;
            this.currentStep = 1;
            this.lastActivity = System.currentTimeMillis();
        }

        // Getters and setters
        public String getSessionId() {
            return sessionId;
        }

        public String getContractNumber() {
            return contractNumber;
        }

        public String getSystemDate() {
            return systemDate;
        }

        public void setSystemDate(String systemDate) {
            this.systemDate = systemDate;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(String effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public String getPriceExpirationDate() {
            return priceExpirationDate;
        }

        public void setPriceExpirationDate(String priceExpirationDate) {
            this.priceExpirationDate = priceExpirationDate;
        }

        public int getCurrentStep() {
            return currentStep;
        }

        public void setCurrentStep(int currentStep) {
            this.currentStep = currentStep;
        }

        public long getLastActivity() {
            return lastActivity;
        }

        public void updateActivity() {
            this.lastActivity = System.currentTimeMillis();
        }

        public boolean isComplete() {
            return systemDate != null && effectiveDate != null && expirationDate != null && priceExpirationDate != null;
        }
    }

    public String processChecklistCreation(String sessionId, String contractNumber) {
        try {
            System.out.println(  "Starting checklist creation for contract: " + contractNumber);

            StringBuilder response = new StringBuilder();
            response.append(  "**Starting Checklist Creation**\n\n");
            response.append(  "Contract Number: ")
                    .append(contractNumber)
                    .append("\n");
            response.append(  "Initializing checklist creation process...\n\n");
            response.append("Please provide the following information:\n");
            response.append("1. System Date (YYYY-MM-DD)\n");
            response.append("2. Effective Date (YYYY-MM-DD)\n");
            response.append("3. Expiration Date (YYYY-MM-DD)\n\n");
            response.append("Let's start with the **System Date**. What date should I use?");

            return response.toString();

        } catch (Exception e) {
            System.err.println(  "Error starting checklist creation: " + e.getMessage());
            return   "Unable to start checklist creation. Please try again or contact support.";
        }
    }

    public boolean isHealthy() {
        try {
            // Check if models are loaded
            boolean modelsReady = areModelsReady();
            return modelsReady;

        } catch (Exception e) {
            System.err.println(  "Health check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear user sessions for a specific user
     */
    public void clearUserSessions(String userId) {
        try {
            System.out.println(  "Clearing sessions for user: " + userId);

            // Remove contract sessions for this user
            activeContractSessions.entrySet()
                .removeIf(entry -> entry.getKey().contains(userId) || entry.getKey().startsWith(userId));

            // Remove checklist sessions for this user
            activeChecklistSessions.entrySet()
                .removeIf(entry -> entry.getKey().contains(userId) || entry.getKey().startsWith(userId));

            System.out.println(  "Sessions cleared for user: " + userId);

        } catch (Exception e) {
            System.err.println(  "Error clearing sessions for user " + userId + ": " + e.getMessage());
        }
    }
    
    public static void main(String b[]){
        // Test various queries
        String[] testQueries = {
            // Contract queries
            "show contract 123456",
            "contract details 123456", 
            "get contract info 123456",
            "find contract 123456",
            "john contracts",
            "contracts by smith",
            "contracts created by mary",
            
            // Status queries
            "status of 123456",
            "expired contracts",
            "active contracts",
            
            // Customer queries
            "boeing contracts",
            "customer honeywell",
            "account 10840607",
            
            // Parts queries
            "List out the contracts associated with part number AE125",
            "What are the specifications of product ID AE125?",
            "Is part number AE125 still active or discontinued?",
            "Can you provide the datasheet for AE125?",
            "What are the compatible parts for AE125?",
            "Is AE125 available in stock?",
            "What is the lead time for part AE125?",
            "Who is the manufacturer of AE125?",
            "Are there any known issues or defects with AE125?",
            "What is the warranty period for AE125?",
            
            // Parts by contract
            "show me the parts 123456",
            "list the parts of 123456",
            "how many parts for 123456",
            
            // Failed parts queries
            "failed parts of 123456",
            "filed parts 123456",
            "failed parts",
            
//            // Help queries
//            "create contract",--> user is asking to create a contract so user not provided the account number so ask user to provdie account numbre, contract name, expiarstion date effective date, pricelist (yes/no) title.desc. 
//            "show me how to create contract",--> user asking steps tpo create contacrt only.
//            "I want to create a contract",--> user is asking to create a contract so user not provided the account number so ask user to provdie account numbre, contract name, expiarstion date effective date, pricelist (yes/no) title.desc.
//            "help create contract1023456789",--> user is asking to create a contract so user  provided the account number so ask user to provdie contract name, expiarstion date effective date, pricelist (yes/no) title.desc.
            
            // Typo examples
            "cntrs 123456 shw me",
            "contarct 123456",
            "pasrt AE125 info",
            "filed parts of 123456"
        };
        ChatbotIntentProcessor ob=new ChatbotIntentProcessor();
        for(String s:testQueries){
        String Response=ob.processUserMessage(s,"12" );
            System.out.println("'''''''''''''''''''''''''"+Response+"''''''''''''''''''''''''''");
        }
    }

}


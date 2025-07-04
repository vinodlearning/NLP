package view.practice;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Enhanced Helper class for Contract Management System
 * 
 * Features:
 * - Account number validation with business rules
 * - Date validation with range checking
 * - Contract data validation
 * - ValidationResult wrapper for detailed error reporting
 * - Integration with existing query processing systems
 * 
 * @author Contract Management System
 * @version 2.0
 */
public class Helper {
    
    private static final Logger logger = Logger.getLogger(Helper.class.getName());
    
    // Validation patterns
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{6,12}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    
    // Business rules
    private static final int MIN_ACCOUNT_NUMBER = 100000;
    private static final int MAX_ACCOUNT_NUMBER = 999999999;
    private static final int MAX_FUTURE_YEARS = 10;
    
    // Valid account prefixes (simulated customer master data)
    private static final Set<String> VALID_ACCOUNT_PREFIXES = new HashSet<>(Arrays.asList(
        "123", "147", "234", "345", "456", "567", "678", "789", "890", "901"
    ));
    
    /**
     * ValidationResult class for detailed validation feedback
     */
    public static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private String errorCode;
        private Map<String, Object> additionalInfo;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.additionalInfo = new HashMap<>();
        }
        
        public ValidationResult(boolean valid, String errorMessage, String errorCode) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
            this.additionalInfo = new HashMap<>();
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public Map<String, Object> getAdditionalInfo() {
            return additionalInfo;
        }
        
        public void addInfo(String key, Object value) {
            additionalInfo.put(key, value);
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message);
        }
        
        public static ValidationResult failure(String message, String code) {
            return new ValidationResult(false, message, code);
        }
    }
    
    /**
     * Validate account number with comprehensive business rules
     */
    public ValidationResult validateAccount(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return ValidationResult.failure("Account number cannot be empty", "EMPTY_ACCOUNT");
            }
            
            String normalizedAccount = accountNumber.trim();
            
            // Check format
            if (!ACCOUNT_NUMBER_PATTERN.matcher(normalizedAccount).matches()) {
                return ValidationResult.failure(
                    "Account number must be 6-12 digits", 
                    "INVALID_FORMAT"
                );
            }
            
            // Check range
            long accountLong = Long.parseLong(normalizedAccount);
            if (accountLong < MIN_ACCOUNT_NUMBER || accountLong > MAX_ACCOUNT_NUMBER) {
                return ValidationResult.failure(
                    "Account number out of valid range", 
                    "OUT_OF_RANGE"
                );
            }
            
            // Check prefix against customer master (simulated)
            String prefix = normalizedAccount.substring(0, 3);
            if (!VALID_ACCOUNT_PREFIXES.contains(prefix)) {
                return ValidationResult.failure(
                    "Account number not found in customer master", 
                    "NOT_IN_MASTER"
                );
            }
            
            // Additional business rule checks
            if (isDeactivatedAccount(normalizedAccount)) {
                return ValidationResult.failure(
                    "Account is deactivated", 
                    "ACCOUNT_DEACTIVATED"
                );
            }
            
            if (isBlockedAccount(normalizedAccount)) {
                return ValidationResult.failure(
                    "Account is blocked for contract creation", 
                    "ACCOUNT_BLOCKED"
                );
            }
            
            // Success
            ValidationResult result = ValidationResult.success();
            result.addInfo("accountNumber", normalizedAccount);
            result.addInfo("customerType", getCustomerType(normalizedAccount));
            result.addInfo("region", getAccountRegion(normalizedAccount));
            
            logger.info("Account validation successful for: " + normalizedAccount);
            return result;
            
        } catch (NumberFormatException e) {
            return ValidationResult.failure(
                "Account number must contain only digits", 
                "NON_NUMERIC"
            );
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error validating account: " + accountNumber, e);
            return ValidationResult.failure(
                "Validation error: " + e.getMessage(), 
                "VALIDATION_ERROR"
            );
        }
    }
    
    /**
     * Validate dates with business rules
     */
    public ValidationResult validateDates(String effectiveDate, String expirationDate) {
        try {
            // Check if both dates are provided
            if (effectiveDate == null && expirationDate == null) {
                return ValidationResult.success(); // Optional dates
            }
            
            LocalDate effective = null;
            LocalDate expiration = null;
            
            // Validate effective date
            if (effectiveDate != null && !effectiveDate.trim().isEmpty()) {
                ValidationResult effectiveValidation = validateSingleDate(effectiveDate, "effective");
                if (!effectiveValidation.isValid()) {
                    return effectiveValidation;
                }
                effective = LocalDate.parse(effectiveDate.trim());
            }
            
            // Validate expiration date
            if (expirationDate != null && !expirationDate.trim().isEmpty()) {
                ValidationResult expirationValidation = validateSingleDate(expirationDate, "expiration");
                if (!expirationValidation.isValid()) {
                    return expirationValidation;
                }
                expiration = LocalDate.parse(expirationDate.trim());
            }
            
            // Cross-validate dates
            if (effective != null && expiration != null) {
                if (effective.isAfter(expiration)) {
                    return ValidationResult.failure(
                        "Effective date cannot be after expiration date", 
                        "INVALID_DATE_ORDER"
                    );
                }
                
                if (effective.equals(expiration)) {
                    return ValidationResult.failure(
                        "Effective date and expiration date cannot be the same", 
                        "SAME_DATES"
                    );
                }
            }
            
            // Success
            ValidationResult result = ValidationResult.success();
            if (effective != null) {
                result.addInfo("effectiveDate", effective.toString());
            }
            if (expiration != null) {
                result.addInfo("expirationDate", expiration.toString());
            }
            
            logger.info("Date validation successful");
            return result;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error validating dates", e);
            return ValidationResult.failure(
                "Date validation error: " + e.getMessage(), 
                "VALIDATION_ERROR"
            );
        }
    }
    
    /**
     * Validate a single date
     */
    private ValidationResult validateSingleDate(String dateString, String dateType) {
        try {
            String normalizedDate = dateString.trim();
            
            // Check format
            if (!DATE_PATTERN.matcher(normalizedDate).matches()) {
                return ValidationResult.failure(
                    dateType + " date must be in YYYY-MM-DD format", 
                    "INVALID_DATE_FORMAT"
                );
            }
            
            // Parse date
            LocalDate date = LocalDate.parse(normalizedDate);
            LocalDate today = LocalDate.now();
            
            // Business rule: dates cannot be too far in the past
            if (date.isBefore(today.minusYears(1))) {
                return ValidationResult.failure(
                    dateType + " date cannot be more than 1 year in the past", 
                    "DATE_TOO_OLD"
                );
            }
            
            // Business rule: dates cannot be too far in the future
            if (date.isAfter(today.plusYears(MAX_FUTURE_YEARS))) {
                return ValidationResult.failure(
                    dateType + " date cannot be more than " + MAX_FUTURE_YEARS + " years in the future", 
                    "DATE_TOO_FUTURE"
                );
            }
            
            return ValidationResult.success();
            
        } catch (DateTimeParseException e) {
            return ValidationResult.failure(
                "Invalid " + dateType + " date: " + e.getMessage(), 
                "PARSE_ERROR"
            );
        }
    }
    
    /**
     * Check if account is deactivated (simulated business rule)
     */
    private boolean isDeactivatedAccount(String accountNumber) {
        // Simulate deactivated accounts ending with 000
        return accountNumber.endsWith("000");
    }
    
    /**
     * Check if account is blocked for contract creation (simulated business rule)
     */
    private boolean isBlockedAccount(String accountNumber) {
        // Simulate blocked accounts ending with 999
        return accountNumber.endsWith("999");
    }
    
    /**
     * Get customer type based on account number (simulated)
     */
    private String getCustomerType(String accountNumber) {
        String prefix = accountNumber.substring(0, 3);
        switch (prefix) {
            case "123":
            case "147":
            case "234":
                return "ENTERPRISE";
            case "345":
            case "456":
            case "567":
                return "BUSINESS";
            default:
                return "STANDARD";
        }
    }
    
    /**
     * Get account region based on account number (simulated)
     */
    private String getAccountRegion(String accountNumber) {
        String prefix = accountNumber.substring(0, 3);
        switch (prefix) {
            case "123":
            case "147":
                return "NORTH";
            case "234":
            case "345":
                return "SOUTH";
            case "456":
            case "567":
                return "EAST";
            default:
                return "WEST";
        }
    }
    
    /**
     * Validate contract name
     */
    public ValidationResult validateContractName(String contractName) {
        try {
            if (contractName == null || contractName.trim().isEmpty()) {
                return ValidationResult.failure("Contract name cannot be empty", "EMPTY_NAME");
            }
            
            String normalized = contractName.trim();
            
            if (normalized.length() < 3) {
                return ValidationResult.failure("Contract name must be at least 3 characters", "TOO_SHORT");
            }
            
            if (normalized.length() > 100) {
                return ValidationResult.failure("Contract name cannot exceed 100 characters", "TOO_LONG");
            }
            
            // Check for invalid characters
            if (!normalized.matches("^[a-zA-Z0-9\\s\\-_]+$")) {
                return ValidationResult.failure("Contract name contains invalid characters", "INVALID_CHARS");
            }
            
            ValidationResult result = ValidationResult.success();
            result.addInfo("contractName", normalized);
            return result;
            
        } catch (Exception e) {
            return ValidationResult.failure("Contract name validation error: " + e.getMessage(), "VALIDATION_ERROR");
        }
    }
    
    /**
     * Validate price list
     */
    public ValidationResult validatePriceList(String priceList) {
        try {
            if (priceList == null || priceList.trim().isEmpty()) {
                return ValidationResult.failure("Price list cannot be empty", "EMPTY_PRICE_LIST");
            }
            
            String normalized = priceList.trim();
            
            if (normalized.length() < 2) {
                return ValidationResult.failure("Price list must be at least 2 characters", "TOO_SHORT");
            }
            
            if (normalized.length() > 50) {
                return ValidationResult.failure("Price list cannot exceed 50 characters", "TOO_LONG");
            }
            
            ValidationResult result = ValidationResult.success();
            result.addInfo("priceList", normalized);
            return result;
            
        } catch (Exception e) {
            return ValidationResult.failure("Price list validation error: " + e.getMessage(), "VALIDATION_ERROR");
        }
    }
    
    /**
     * Validate contract title
     */
    public ValidationResult validateTitle(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return ValidationResult.failure("Title cannot be empty", "EMPTY_TITLE");
            }
            
            String normalized = title.trim();
            
            if (normalized.length() < 3) {
                return ValidationResult.failure("Title must be at least 3 characters", "TOO_SHORT");
            }
            
            if (normalized.length() > 200) {
                return ValidationResult.failure("Title cannot exceed 200 characters", "TOO_LONG");
            }
            
            ValidationResult result = ValidationResult.success();
            result.addInfo("title", normalized);
            return result;
            
        } catch (Exception e) {
            return ValidationResult.failure("Title validation error: " + e.getMessage(), "VALIDATION_ERROR");
        }
    }
    
    /**
     * Validate contract description
     */
    public ValidationResult validateDescription(String description) {
        try {
            if (description == null || description.trim().isEmpty()) {
                return ValidationResult.failure("Description cannot be empty", "EMPTY_DESCRIPTION");
            }
            
            String normalized = description.trim();
            
            if (normalized.length() < 5) {
                return ValidationResult.failure("Description must be at least 5 characters", "TOO_SHORT");
            }
            
            if (normalized.length() > 500) {
                return ValidationResult.failure("Description cannot exceed 500 characters", "TOO_LONG");
            }
            
            ValidationResult result = ValidationResult.success();
            result.addInfo("description", normalized);
            return result;
            
        } catch (Exception e) {
            return ValidationResult.failure("Description validation error: " + e.getMessage(), "VALIDATION_ERROR");
        }
    }
    
    /**
     * Comprehensive contract data validation
     */
    public ValidationResult validateContractData(Map<String, Object> contractData) {
        try {
            List<String> errors = new ArrayList<>();
            
            // Validate each field
            if (contractData.containsKey("accountNumber")) {
                ValidationResult accountResult = validateAccount(contractData.get("accountNumber").toString());
                if (!accountResult.isValid()) {
                    errors.add("Account: " + accountResult.getErrorMessage());
                }
            }
            
            if (contractData.containsKey("contractName")) {
                ValidationResult nameResult = validateContractName(contractData.get("contractName").toString());
                if (!nameResult.isValid()) {
                    errors.add("Name: " + nameResult.getErrorMessage());
                }
            }
            
            if (contractData.containsKey("priceList")) {
                ValidationResult priceResult = validatePriceList(contractData.get("priceList").toString());
                if (!priceResult.isValid()) {
                    errors.add("Price List: " + priceResult.getErrorMessage());
                }
            }
            
            if (contractData.containsKey("title")) {
                ValidationResult titleResult = validateTitle(contractData.get("title").toString());
                if (!titleResult.isValid()) {
                    errors.add("Title: " + titleResult.getErrorMessage());
                }
            }
            
            if (contractData.containsKey("description")) {
                ValidationResult descResult = validateDescription(contractData.get("description").toString());
                if (!descResult.isValid()) {
                    errors.add("Description: " + descResult.getErrorMessage());
                }
            }
            
            // Validate dates if present
            if (contractData.containsKey("effectiveDate") || contractData.containsKey("expirationDate")) {
                String effectiveDate = contractData.containsKey("effectiveDate") ? 
                    contractData.get("effectiveDate").toString() : null;
                String expirationDate = contractData.containsKey("expirationDate") ? 
                    contractData.get("expirationDate").toString() : null;
                
                ValidationResult dateResult = validateDates(effectiveDate, expirationDate);
                if (!dateResult.isValid()) {
                    errors.add("Dates: " + dateResult.getErrorMessage());
                }
            }
            
            if (errors.isEmpty()) {
                return ValidationResult.success();
            } else {
                return ValidationResult.failure(
                    "Validation failed: " + String.join(", ", errors), 
                    "MULTIPLE_ERRORS"
                );
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in comprehensive validation", e);
            return ValidationResult.failure(
                "Comprehensive validation error: " + e.getMessage(), 
                "VALIDATION_ERROR"
            );
        }
    }
    
    /**
     * Get validation summary for debugging
     */
    public String getValidationSummary() {
        return String.format(
            "Helper Validation Rules:\n" +
            "- Account Numbers: %d-%d digits, valid prefixes: %s\n" +
            "- Dates: YYYY-MM-DD format, within 1 year past to %d years future\n" +
            "- Contract Names: 3-100 characters, alphanumeric only\n" +
            "- Price Lists: 2-50 characters\n" +
            "- Titles: 3-200 characters\n" +
            "- Descriptions: 5-500 characters",
            MIN_ACCOUNT_NUMBER, MAX_ACCOUNT_NUMBER, 
            VALID_ACCOUNT_PREFIXES.toString(),
            MAX_FUTURE_YEARS
        );
    }
}
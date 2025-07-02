package view.practice;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class ContractCreationHandler {
    private static final Logger logger = Logger.getLogger(ContractCreationHandler.class.getName());
    private Scanner scanner;
    
    public ContractCreationHandler() {
        this.scanner = new Scanner(System.in);
    }
    
    public void handleContractCreation(String userInput) {
        System.out.println("? I'll help you create a contract. Let me gather some details...\n");
        
        ContractDetails details = new ContractDetails();
        
        // Extract account number if provided
        String accountNumber = extractAccountNumber(userInput);
        if (accountNumber != null) {
            details.setAccountNumber(accountNumber);
            System.out.println("? Account Number: " + accountNumber + " (detected from your request)");
        } else {
            details.setAccountNumber(promptForAccountNumber());
        }
        
        // Collect other required details
        details.setContractName(promptForContractName());
        details.setTitle(promptForTitle());
        details.setComments(promptForComments());
        details.setDescription(promptForDescription());
        details.setPriceListRequired(promptForPriceList());
        
        // Create the contract
        createContract(details);
    }
    
    private String extractAccountNumber(String input) {
        Pattern pattern = Pattern.compile("\\b(\\d{10})\\b");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private String promptForAccountNumber() {
        System.out.print("? Please enter Account Number (10 digits): ");
        String accountNumber = scanner.nextLine().trim();
        
        while (!isValidAccountNumber(accountNumber)) {
            System.out.print("? Invalid account number. Please enter 10 digits: ");
            accountNumber = scanner.nextLine().trim();
        }
        
        return accountNumber;
    }
    
    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{10}");
    }
    
    private String promptForContractName() {
        System.out.print("? Contract Name: ");
        String name = scanner.nextLine().trim();
        
        while (name.isEmpty()) {
            System.out.print("? Contract name is required. Please enter: ");
            name = scanner.nextLine().trim();
        }
        
        return name;
    }
    
    private String promptForTitle() {
        System.out.print("? Contract Title: ");
        String title = scanner.nextLine().trim();
        
        while (title.isEmpty()) {
            System.out.print("? Contract title is required. Please enter: ");
            title = scanner.nextLine().trim();
        }
        
        return title;
    }
    
    private String promptForComments() {
        System.out.print("? Comments (optional, press Enter to skip): ");
        return scanner.nextLine().trim();
    }
    
    private String promptForDescription() {
        System.out.print("? Description: ");
        String description = scanner.nextLine().trim();
        
        while (description.isEmpty()) {
            System.out.print("? Description is required. Please enter: ");
            description = scanner.nextLine().trim();
        }
        
        return description;
    }
    
    private boolean promptForPriceList() {
        System.out.print("? Include Price List? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        while (!response.equals("yes") && !response.equals("no") && 
               !response.equals("y") && !response.equals("n")) {
            System.out.print("? Please enter 'yes' or 'no': ");
            response = scanner.nextLine().trim().toLowerCase();
        }
        
        return response.equals("yes") || response.equals("y");
    }
    
    private void createContract(ContractDetails details) {
        System.out.println("\n? Creating contract with the following details:");
        System.out.println("===========================================================" );
        
        System.out.println("Account Number: " + details.getAccountNumber());
        System.out.println("Contract Name: " + details.getContractName());
        System.out.println("Title: " + details.getTitle());
        System.out.println("Comments: " + (details.getComments().isEmpty() ? "None" : details.getComments()));
        System.out.println("Description: " + details.getDescription());
        System.out.println("Price List: " + (details.isPriceListRequired() ? "Yes" : "No"));
        System.out.println("===========================================================" );
        
        // Call your existing private method here
        boolean success = callExistingContractCreationMethod(details);
        
        if (success) {
            System.out.println("? Contract created successfully!");
            System.out.println("? You will receive a confirmation email shortly.");
        } else {
            System.out.println("? Failed to create contract. Please try again or contact support.");
        }
    }
    
    // This method should call your existing private contract creation method
    private boolean callExistingContractCreationMethod(ContractDetails details) {
        try {
            // Replace this with your actual contract creation logic
            logger.info("Calling existing contract creation method with details: " + details);
            
            // Example: return yourExistingMethod(details);
            // For now, simulating success
            Thread.sleep(1000); // Simulate processing time
            return true;
            
        } catch (Exception e) {
            logger.severe("Error creating contract: " + e.getMessage());
            return false;
        }
    }
    
    // Inner class to hold contract details
    public static class ContractDetails {
        private String accountNumber;
        private String contractName;
        private String title;
        private String comments;
        private String description;
        private boolean priceListRequired;
        
        // Getters and setters
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        
        public String getContractName() { return contractName; }
        public void setContractName(String contractName) { this.contractName = contractName; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public boolean isPriceListRequired() { return priceListRequired; }
        public void setPriceListRequired(boolean priceListRequired) { this.priceListRequired = priceListRequired; }
        
        @Override
        public String toString() {
            return String.format("ContractDetails{accountNumber='%s', contractName='%s', title='%s', comments='%s', description='%s', priceListRequired=%s}",
                    accountNumber, contractName, title, comments, description, priceListRequired);
        }
    }
}

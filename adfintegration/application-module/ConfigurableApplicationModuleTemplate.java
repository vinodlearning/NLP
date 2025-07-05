package model;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;
import oracle.jbo.server.ApplicationModuleImpl;
import model.nlp.ConfigurableMLController;

/**
 * Configurable Application Module Template for Contract Query Processing
 * Version 2.0 - Uses text file configuration for database attributes
 * 
 * INTEGRATION INSTRUCTIONS:
 * 1. Add these method operations to your existing ApplicationModuleImpl
 * 2. The ViewObject names and column names are loaded from database_attributes.txt
 * 3. Update the database_attributes.txt file instead of changing Java code
 * 4. Add these methods to your ApplicationModule interface
 * 5. Regenerate your ADF bindings after adding these methods
 * 
 * Configuration File: database_attributes.txt
 * 
 * @author Contract Query Processing System
 * @version 2.0 - Text File Configuration
 */
public class ConfigurableApplicationModuleTemplate extends ApplicationModuleImpl {
    
    private ConfigurableMLController mlController;
    
    /**
     * Initialize the configurable ML controller
     */
    public ConfigurableApplicationModuleTemplate() {
        super();
        this.mlController = ConfigurableMLController.getInstance();
    }
    
    // ==================== CONTRACT OPERATIONS ====================
    
    /**
     * Get specific contract details by contract ID
     * ViewObject and column names loaded from database_attributes.txt
     * 
     * @param contractId - The contract ID to lookup
     */
    public void getContractDetails(String contractId) {
        try {
            // Get ViewObject name from configuration file
            String voName = mlController.getContractViewObject();
            ViewObject contractVO = this.findViewObject(voName);
            
            if (contractVO == null) {
                throw new RuntimeException("ViewObject not found: " + voName + 
                    ". Check CONTRACT_VIEW_OBJECT in database_attributes.txt");
            }
            
            // Clear any existing WHERE clause
            contractVO.setWhereClause(null);
            contractVO.setWhereClauseParams(null);
            
            // Get column name from configuration file
            String columnName = mlController.getContractIdColumn();
            
            // Set WHERE clause using configured column name
            contractVO.setWhereClause(columnName + " = :contractId");
            contractVO.defineNamedWhereClauseParam("contractId", contractId, null);
            
            // Execute query
            contractVO.executeQuery();
            
            // Log for monitoring
            System.out.println("Contract lookup executed for ID: " + contractId);
            System.out.println("Using ViewObject: " + voName + ", Column: " + columnName);
            System.out.println("Result count: " + contractVO.getEstimatedRowCount());
            
        } catch (Exception e) {
            System.err.println("Error in getContractDetails: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve contract details for ID: " + contractId, e);
        }
    }
    
    /**
     * Search contracts based on search criteria
     * ViewObject and column names loaded from database_attributes.txt
     * 
     * @param searchCriteria - The search text to match against
     */
    public void searchContracts(String searchCriteria) {
        try {
            // Get ViewObject name from configuration file
            String voName = mlController.getContractViewObject();
            ViewObject contractVO = this.findViewObject(voName);
            
            if (contractVO == null) {
                throw new RuntimeException("ViewObject not found: " + voName + 
                    ". Check CONTRACT_VIEW_OBJECT in database_attributes.txt");
            }
            
            // Clear any existing WHERE clause
            contractVO.setWhereClause(null);
            contractVO.setWhereClauseParams(null);
            
            // Get column names from configuration file
            String contractIdCol = mlController.getContractIdColumn();
            String contractNameCol = mlController.getContractNameColumn();
            String customerNameCol = mlController.getCustomerNameColumn();
            String descriptionCol = mlController.getDatabaseAttribute("DESCRIPTION_COLUMN");
            String statusCol = mlController.getDatabaseAttribute("STATUS_COLUMN");
            
            // Build comprehensive search WHERE clause using configured column names
            String whereClause = String.format(
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "%s = :searchCriteria",
                contractNameCol, customerNameCol, descriptionCol, statusCol, contractIdCol
            );
            
            contractVO.setWhereClause(whereClause);
            contractVO.defineNamedWhereClauseParam("searchCriteria", searchCriteria, null);
            
            // Execute query
            contractVO.executeQuery();
            
            // Log for monitoring
            System.out.println("Contract search executed for criteria: " + searchCriteria);
            System.out.println("Using ViewObject: " + voName);
            System.out.println("Search columns: " + contractNameCol + ", " + customerNameCol + 
                             ", " + descriptionCol + ", " + statusCol + ", " + contractIdCol);
            System.out.println("Result count: " + contractVO.getEstimatedRowCount());
            
        } catch (Exception e) {
            System.err.println("Error in searchContracts: " + e.getMessage());
            throw new RuntimeException("Failed to search contracts with criteria: " + searchCriteria, e);
        }
    }
    
    // ==================== PARTS OPERATIONS ====================
    
    /**
     * Get parts for a specific contract
     * ViewObject and column names loaded from database_attributes.txt
     * 
     * @param contractId - The contract ID to get parts for
     */
    public void getContractParts(String contractId) {
        try {
            // Get ViewObject name from configuration file
            String voName = mlController.getPartsViewObject();
            ViewObject partsVO = this.findViewObject(voName);
            
            if (partsVO == null) {
                throw new RuntimeException("ViewObject not found: " + voName + 
                    ". Check PARTS_VIEW_OBJECT in database_attributes.txt");
            }
            
            // Clear any existing WHERE clause
            partsVO.setWhereClause(null);
            partsVO.setWhereClauseParams(null);
            
            // Get foreign key column name from configuration file
            String fkColumnName = mlController.getDatabaseAttribute("CONTRACT_FK_COLUMN");
            
            // Set WHERE clause using configured foreign key column
            partsVO.setWhereClause(fkColumnName + " = :contractId");
            partsVO.defineNamedWhereClauseParam("contractId", contractId, null);
            
            // Execute query
            partsVO.executeQuery();
            
            // Log for monitoring
            System.out.println("Parts lookup executed for contract: " + contractId);
            System.out.println("Using ViewObject: " + voName + ", FK Column: " + fkColumnName);
            System.out.println("Result count: " + partsVO.getEstimatedRowCount());
            
        } catch (Exception e) {
            System.err.println("Error in getContractParts: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve parts for contract ID: " + contractId, e);
        }
    }
    
    /**
     * Search parts based on search criteria
     * ViewObject and column names loaded from database_attributes.txt
     * 
     * @param searchCriteria - The search text to match against
     */
    public void searchParts(String searchCriteria) {
        try {
            // Get ViewObject name from configuration file
            String voName = mlController.getPartsViewObject();
            ViewObject partsVO = this.findViewObject(voName);
            
            if (partsVO == null) {
                throw new RuntimeException("ViewObject not found: " + voName + 
                    ". Check PARTS_VIEW_OBJECT in database_attributes.txt");
            }
            
            // Clear any existing WHERE clause
            partsVO.setWhereClause(null);
            partsVO.setWhereClauseParams(null);
            
            // Get column names from configuration file
            String partNameCol = mlController.getPartNameColumn();
            String partNumberCol = mlController.getPartNumberColumn();
            String descriptionCol = mlController.getDatabaseAttribute("PART_DESCRIPTION_COLUMN");
            String manufacturerCol = mlController.getDatabaseAttribute("MANUFACTURER_COLUMN");
            String categoryCol = mlController.getDatabaseAttribute("CATEGORY_COLUMN");
            
            // Build comprehensive search WHERE clause using configured column names
            String whereClause = String.format(
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%') OR " +
                "UPPER(%s) LIKE UPPER('%%' || :searchCriteria || '%%')",
                partNameCol, partNumberCol, descriptionCol, manufacturerCol, categoryCol
            );
            
            partsVO.setWhereClause(whereClause);
            partsVO.defineNamedWhereClauseParam("searchCriteria", searchCriteria, null);
            
            // Execute query
            partsVO.executeQuery();
            
            // Log for monitoring
            System.out.println("Parts search executed for criteria: " + searchCriteria);
            System.out.println("Using ViewObject: " + voName);
            System.out.println("Search columns: " + partNameCol + ", " + partNumberCol + 
                             ", " + descriptionCol + ", " + manufacturerCol + ", " + categoryCol);
            System.out.println("Result count: " + partsVO.getEstimatedRowCount());
            
        } catch (Exception e) {
            System.err.println("Error in searchParts: " + e.getMessage());
            throw new RuntimeException("Failed to search parts with criteria: " + searchCriteria, e);
        }
    }
    
    // ==================== CONFIGURATION MANAGEMENT ====================
    
    /**
     * Reload configurations from text files
     * Useful for development or when configuration files are updated
     */
    public void reloadConfiguration() {
        try {
            mlController.forceReloadConfigurations();
            System.out.println("Configuration reloaded from text files");
        } catch (Exception e) {
            System.err.println("Error reloading configuration: " + e.getMessage());
            throw new RuntimeException("Failed to reload configuration", e);
        }
    }
    
    /**
     * Get current configuration information
     * Useful for debugging and monitoring
     */
    public String getConfigurationInfo() {
        return mlController.getConfigurationInfo();
    }
    
    /**
     * Validate that all required ViewObjects exist
     * Call this method during startup to verify configuration
     */
    public boolean validateViewObjects() {
        try {
            // Check contract ViewObject
            String contractVOName = mlController.getContractViewObject();
            ViewObject contractVO = this.findViewObject(contractVOName);
            if (contractVO == null) {
                System.err.println("Contract ViewObject not found: " + contractVOName);
                return false;
            }
            
            // Check parts ViewObject
            String partsVOName = mlController.getPartsViewObject();
            ViewObject partsVO = this.findViewObject(partsVOName);
            if (partsVO == null) {
                System.err.println("Parts ViewObject not found: " + partsVOName);
                return false;
            }
            
            System.out.println("ViewObject validation successful:");
            System.out.println("- Contract VO: " + contractVOName);
            System.out.println("- Parts VO: " + partsVOName);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error validating ViewObjects: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get configured database attribute
     * Helper method for custom operations
     */
    public String getDatabaseAttribute(String logicalName) {
        return mlController.getDatabaseAttribute(logicalName);
    }
    
    // ==================== HELPER OPERATIONS ====================
    
    /**
     * Show help information (optional)
     */
    public void showHelp() {
        System.out.println("Contract Query Processing Help displayed");
    }
    
    /**
     * Handle error scenarios (optional)
     */
    public void handleError() {
        System.out.println("Error handling invoked");
    }
    
    // ==================== VALIDATION & UTILITY OPERATIONS ====================
    
    /**
     * Validate contract ID exists using configured attributes
     */
    public boolean validateContractExists(String contractId) {
        try {
            String voName = mlController.getContractViewObject();
            ViewObject contractVO = this.findViewObject(voName);
            
            if (contractVO == null) {
                return false;
            }
            
            String columnName = mlController.getContractIdColumn();
            
            // Create a temporary query to check existence
            contractVO.setWhereClause(columnName + " = :contractId");
            contractVO.defineNamedWhereClauseParam("contractId", contractId, null);
            contractVO.executeQuery();
            
            boolean exists = contractVO.first() != null;
            
            // Reset the ViewObject
            contractVO.setWhereClause(null);
            contractVO.setWhereClauseParams(null);
            contractVO.executeQuery();
            
            return exists;
            
        } catch (Exception e) {
            System.err.println("Error validating contract: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get contract count using configured attributes
     */
    public int getContractCount() {
        try {
            String voName = mlController.getContractViewObject();
            ViewObject contractVO = this.findViewObject(voName);
            
            if (contractVO == null) {
                return 0;
            }
            
            contractVO.setWhereClause(null);
            contractVO.setWhereClauseParams(null);
            contractVO.executeQuery();
            
            return contractVO.getEstimatedRowCount();
            
        } catch (Exception e) {
            System.err.println("Error getting contract count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get parts count using configured attributes
     */
    public int getPartsCount() {
        try {
            String voName = mlController.getPartsViewObject();
            ViewObject partsVO = this.findViewObject(voName);
            
            if (partsVO == null) {
                return 0;
            }
            
            partsVO.setWhereClause(null);
            partsVO.setWhereClauseParams(null);
            partsVO.executeQuery();
            
            return partsVO.getEstimatedRowCount();
            
        } catch (Exception e) {
            System.err.println("Error getting parts count: " + e.getMessage());
            return 0;
        }
    }
    
    // ==================== CONFIGURATION NOTES ====================
    
    /*
     * TEXT FILE CONFIGURATION:
     * 
     * All database-specific settings are loaded from config/database_attributes.txt
     * 
     * To customize for your database:
     * 1. Edit config/database_attributes.txt
     * 2. Update ViewObject names: CONTRACT_VIEW_OBJECT=YourContractVO
     * 3. Update column names: CONTRACT_ID_COLUMN=YOUR_CONTRACT_ID
     * 4. Restart application or call reloadConfiguration()
     * 
     * Example database_attributes.txt:
     * CONTRACT_VIEW_OBJECT=MyContractView
     * PARTS_VIEW_OBJECT=MyPartsView  
     * CONTRACT_ID_COLUMN=CONTRACT_NUMBER
     * CONTRACT_NAME_COLUMN=CONTRACT_TITLE
     * CUSTOMER_NAME_COLUMN=CLIENT_NAME
     * 
     * Benefits:
     * - No Java code changes needed
     * - Easy to maintain across environments
     * - Configuration can be updated without recompilation
     * - Version control friendly
     */
}
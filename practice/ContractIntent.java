package view.practice;
/**
 * Contract Intent enumeration for business operations
 */
public enum ContractIntent {
    SHOW_CONTRACT("show_contract", "Display contract details"),
    GET_CONTRACT_INFO("get_contract_info", "Get contract information"),
    GET_CONTRACT_EXPIRATION("get_contract_expiration", "Get contract expiration date"),
    FILTER_CONTRACTS_BY_USER("filter_contracts_by_user", "Filter contracts by creator/user"),
    FILTER_CONTRACTS_BY_CUSTOMER("filter_contracts_by_customer", "Filter contracts by customer"),
    SEARCH_CONTRACTS("search_contracts", "Search contracts"),
    LIST_CONTRACTS("list_contracts", "List all contracts"),
    CONTRACT_STATUS("contract_status", "Get contract status"),
    UNKNOWN("unknown", "Unknown intent");
    
    private final String value;
    private final String description;
    
    ContractIntent(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public String getValue() { return value; }
    public String getDescription() { return description; }
    
    public static ContractIntent fromString(String text) {
        for (ContractIntent intent : ContractIntent.values()) {
            if (intent.value.equalsIgnoreCase(text)) {
                return intent;
            }
        }
        return UNKNOWN;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
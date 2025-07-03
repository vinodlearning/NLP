package view.practice;


/**
 * Contract Intent enumeration for business operations
 */
public enum ContractIntent {
    UPDATE_CONTRACT("updaet_contract"),
    CREATE_CONTRACT("create_contract"),
    HELP_GUIDE("help_guide"),
    SHOW_CONTRACT("show_contract"),
    GET_CONTRACT_INFO("get_contract_info"),
    GET_CONTRACT_EXPIRATION("get_contract_expiration"),
    
    // ADD THESE NEW INTENTS
    LIST_EXPIRED_CONTRACTS("list_expired_contracts"),
    LIST_ACTIVE_CONTRACTS("list_active_contracts"),
    FILTER_CONTRACTS_BY_ACCOUNT("filter_contracts_by_account"),
    
    FILTER_CONTRACTS_BY_USER("filter_contracts_by_user"),
    FILTER_CONTRACTS_BY_CUSTOMER("filter_contracts_by_customer"),
    SEARCH_CONTRACTS("search_contracts"),
    LIST_CONTRACTS("list_contracts"),
    CONTRACT_STATUS("contract_status"),
    UNKNOWN("unknown");

    private final String value;

    ContractIntent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContractIntent fromString(String text) {
        for (ContractIntent intent : ContractIntent.values()) {
            if (intent.value.equalsIgnoreCase(text)) {
                return intent;
            }
        }
        return UNKNOWN;
    }
}
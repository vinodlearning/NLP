import java.util.List;

/**
 * Complete Java Object Mapping for Contract JSON Response
 * 
 * These classes can be used with JSON parsing libraries like:
 * - Jackson: ObjectMapper mapper = new ObjectMapper(); ContractResponse response = mapper.readValue(jsonString, ContractResponse.class);
 * - Gson: Gson gson = new Gson(); ContractResponse response = gson.fromJson(jsonString, ContractResponse.class);
 */

// Main Response Class
public class ContractResponse {
    private String responseType;
    private String message;
    private boolean success;
    private long timestamp;
    private RequestInfo request;
    private ContractData data;
    private ProcessingInfo processing;
    private ActionInfo actions;
    
    // Constructors
    public ContractResponse() {}
    
    // Getters and Setters
    public String getResponseType() { return responseType; }
    public void setResponseType(String responseType) { this.responseType = responseType; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public RequestInfo getRequest() { return request; }
    public void setRequest(RequestInfo request) { this.request = request; }
    
    public ContractData getData() { return data; }
    public void setData(ContractData data) { this.data = data; }
    
    public ProcessingInfo getProcessing() { return processing; }
    public void setProcessing(ProcessingInfo processing) { this.processing = processing; }
    
    public ActionInfo getActions() { return actions; }
    public void setActions(ActionInfo actions) { this.actions = actions; }
    
    // Convenience methods for easy access to specific attributes
    public String getContractId() {
        return data != null ? data.getContractId() : null;
    }
    
    public String getEffectiveDate() {
        return data != null && data.getDates() != null ? data.getDates().getEffectiveDate() : null;
    }
    
    public String getExpirationDate() {
        return data != null && data.getDates() != null ? data.getDates().getExpirationDate() : null;
    }
    
    public String getPriceExpirationDate() {
        return data != null && data.getDates() != null ? data.getDates().getPriceExpirationDate() : null;
    }
    
    public String getProjectType() {
        return data != null && data.getProject() != null ? data.getProject().getProjectType() : null;
    }
    
    public String getCustomerName() {
        return data != null && data.getCustomer() != null ? data.getCustomer().getCustomerName() : null;
    }
    
    public Double getTotalValue() {
        return data != null && data.getPricing() != null ? data.getPricing().getTotalValue() : null;
    }
}

// Request Information Class
class RequestInfo {
    private String originalInput;
    private String correctedInput;
    private String contractId;
    private List<String> requestedAttributes;
    
    // Constructors
    public RequestInfo() {}
    
    // Getters and Setters
    public String getOriginalInput() { return originalInput; }
    public void setOriginalInput(String originalInput) { this.originalInput = originalInput; }
    
    public String getCorrectedInput() { return correctedInput; }
    public void setCorrectedInput(String correctedInput) { this.correctedInput = correctedInput; }
    
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    
    public List<String> getRequestedAttributes() { return requestedAttributes; }
    public void setRequestedAttributes(List<String> requestedAttributes) { this.requestedAttributes = requestedAttributes; }
}

// Contract Data Class
class ContractData {
    private String contractId;
    private String contractTitle;
    private String effectiveDate;
    private String expirationDate;
    private String priceExpirationDate;
    private String projectType;
    private String status;
    private String totalValue;
    private String currency;
    private CustomerInfo customer;
    private DateInfo dates;
    private PricingInfo pricing;
    private ProjectInfo project;
    
    // Constructors
    public ContractData() {}
    
    // Getters and Setters
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    
    public String getContractTitle() { return contractTitle; }
    public void setContractTitle(String contractTitle) { this.contractTitle = contractTitle; }
    
    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }
    
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    
    public String getPriceExpirationDate() { return priceExpirationDate; }
    public void setPriceExpirationDate(String priceExpirationDate) { this.priceExpirationDate = priceExpirationDate; }
    
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTotalValue() { return totalValue; }
    public void setTotalValue(String totalValue) { this.totalValue = totalValue; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public CustomerInfo getCustomer() { return customer; }
    public void setCustomer(CustomerInfo customer) { this.customer = customer; }
    
    public DateInfo getDates() { return dates; }
    public void setDates(DateInfo dates) { this.dates = dates; }
    
    public PricingInfo getPricing() { return pricing; }
    public void setPricing(PricingInfo pricing) { this.pricing = pricing; }
    
    public ProjectInfo getProject() { return project; }
    public void setProject(ProjectInfo project) { this.project = project; }
}

// Customer Information Class
class CustomerInfo {
    private String customerId;
    private String customerName;
    private String contactPerson;
    private String email;
    private String phone;
    
    // Constructors
    public CustomerInfo() {}
    
    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

// Date Information Class
class DateInfo {
    private String effectiveDate;
    private String expirationDate;
    private String priceExpirationDate;
    private String renewalDate;
    private String lastModified;
    private int daysUntilExpiration;
    private int daysUntilPriceExpiration;
    
    // Constructors
    public DateInfo() {}
    
    // Getters and Setters
    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }
    
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    
    public String getPriceExpirationDate() { return priceExpirationDate; }
    public void setPriceExpirationDate(String priceExpirationDate) { this.priceExpirationDate = priceExpirationDate; }
    
    public String getRenewalDate() { return renewalDate; }
    public void setRenewalDate(String renewalDate) { this.renewalDate = renewalDate; }
    
    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    
    public int getDaysUntilExpiration() { return daysUntilExpiration; }
    public void setDaysUntilExpiration(int daysUntilExpiration) { this.daysUntilExpiration = daysUntilExpiration; }
    
    public int getDaysUntilPriceExpiration() { return daysUntilPriceExpiration; }
    public void setDaysUntilPriceExpiration(int daysUntilPriceExpiration) { this.daysUntilPriceExpiration = daysUntilPriceExpiration; }
}

// Pricing Information Class
class PricingInfo {
    private Double totalValue;
    private String currency;
    private Double monthlyAmount;
    private Double discountRate;
    private String priceValidUntil;
    
    // Constructors
    public PricingInfo() {}
    
    // Getters and Setters
    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Double getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(Double monthlyAmount) { this.monthlyAmount = monthlyAmount; }
    
    public Double getDiscountRate() { return discountRate; }
    public void setDiscountRate(Double discountRate) { this.discountRate = discountRate; }
    
    public String getPriceValidUntil() { return priceValidUntil; }
    public void setPriceValidUntil(String priceValidUntil) { this.priceValidUntil = priceValidUntil; }
}

// Project Information Class
class ProjectInfo {
    private String projectType;
    private String projectCategory;
    private String deliveryMethod;
    private String estimatedDuration;
    private String projectManager;
    
    // Constructors
    public ProjectInfo() {}
    
    // Getters and Setters
    public String getProjectType() { return projectType; }
    public void setProjectType(String projectType) { this.projectType = projectType; }
    
    public String getProjectCategory() { return projectCategory; }
    public void setProjectCategory(String projectCategory) { this.projectCategory = projectCategory; }
    
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    
    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }
}

// Processing Information Class
class ProcessingInfo {
    private String modelUsed;
    private Double processingTimeMs;
    private List<String> spellCorrectionsApplied;
    private Double confidence;
    private int attributesFound;
    private String dataSource;
    
    // Constructors
    public ProcessingInfo() {}
    
    // Getters and Setters
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    
    public Double getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Double processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public List<String> getSpellCorrectionsApplied() { return spellCorrectionsApplied; }
    public void setSpellCorrectionsApplied(List<String> spellCorrectionsApplied) { this.spellCorrectionsApplied = spellCorrectionsApplied; }
    
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    public int getAttributesFound() { return attributesFound; }
    public void setAttributesFound(int attributesFound) { this.attributesFound = attributesFound; }
    
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
}

// Action Information Class
class ActionInfo {
    private String recommendedUIAction;
    private String navigationTarget;
    private List<String> availableActions;
    
    // Constructors
    public ActionInfo() {}
    
    // Getters and Setters
    public String getRecommendedUIAction() { return recommendedUIAction; }
    public void setRecommendedUIAction(String recommendedUIAction) { this.recommendedUIAction = recommendedUIAction; }
    
    public String getNavigationTarget() { return navigationTarget; }
    public void setNavigationTarget(String navigationTarget) { this.navigationTarget = navigationTarget; }
    
    public List<String> getAvailableActions() { return availableActions; }
    public void setAvailableActions(List<String> availableActions) { this.availableActions = availableActions; }
}
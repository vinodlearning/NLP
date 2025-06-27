package view;

import java.util.HashMap;
import java.util.Map;


public class ParsedQuery {
    private String originalQuery;
    private String correctedQuery;
    private QueryType queryType;
    private ActionType actionType;
    private String customerName;
    private String statusType;
    private String partNumber;
    private String contractNumber;
    private double confidence;
    private String userName;
    private String accountNumner;
    private Map<String, String> queryParameters;
    // Constructors
       public ParsedQuery() {
           this.queryType = QueryType.UNKNOWN;
           this.actionType = ActionType.UNKNOWN;
           this.confidence = 0.0;
           this.queryParameters = new HashMap<>();
       }
           
       public ParsedQuery(String originalQuery) {
           this();
           this.originalQuery = originalQuery;
       }
           
       public ParsedQuery(String originalQuery, QueryType queryType) {
           this(originalQuery);
           this.queryType = queryType;
       }
           
       public ParsedQuery(String originalQuery, QueryType queryType, ActionType actionType) {
           this(originalQuery, queryType);
           this.actionType = actionType;
       }

       // Getters and Setters
       public String getOriginalQuery() {
           return originalQuery;
       }
           
       public void setOriginalQuery(String originalQuery) {
           this.originalQuery = originalQuery;
       }
           
       public String getCorrectedQuery() {
           return correctedQuery;
       }
           
       public void setCorrectedQuery(String correctedQuery) {
           this.correctedQuery = correctedQuery;
       }
           
       public QueryType getQueryType() {
           return queryType;
       }
           
       public void setQueryType(QueryType queryType) {
           this.queryType = queryType;
       }
           
       public ActionType getActionType() {
           return actionType;
       }
           
       public void setActionType(ActionType actionType) {
           this.actionType = actionType;
       }
           
       public String getCustomerName() {
           return customerName;
       }
           
       public void setCustomerName(String customerName) {
           this.customerName = customerName;
       }
           
       public String getStatusType() {
           return statusType;
       }
           
       public void setStatusType(String statusType) {
           this.statusType = statusType;
       }
           
       public String getPartNumber() {
           return partNumber;
       }
           
       public void setPartNumber(String partNumber) {
           this.partNumber = partNumber;
       }
           
       public String getContractNumber() {
           return contractNumber;
       }
           
       public void setContractNumber(String contractNumber) {
           this.contractNumber = contractNumber;
       }
           
       public double getConfidence() {
           return confidence;
       }
           
       public void setConfidence(double confidence) {
           this.confidence = confidence;
       }

       public void setAccountNumner(String accountNumner) {
           this.accountNumner = accountNumner;
       }

       public String getAccountNumner() {
           return accountNumner;
       }

       public void setUserName(String userName) {
           this.userName = userName;
       }

       public String getUserName() {
           return userName;
       }

       public Map<String, String> getQueryParameters() {
           return queryParameters;
       }

       public void setQueryParameters(Map<String, String> queryParameters) {
           this.queryParameters = queryParameters != null ? queryParameters : new HashMap<>();
       }

       // Utility methods for query parameters
       public void addQueryParameter(String key, String value) {
           if (this.queryParameters == null) {
               this.queryParameters = new HashMap<>();
           }
           this.queryParameters.put(key, value);
       }

       public String getQueryParameter(String key) {
           return this.queryParameters != null ? this.queryParameters.get(key) : null;
       }

       public boolean hasQueryParameter(String key) {
           return this.queryParameters != null && this.queryParameters.containsKey(key);
       }

       public void removeQueryParameter(String key) {
           if (this.queryParameters != null) {
               this.queryParameters.remove(key);
           }
       }

       public void clearQueryParameters() {
           if (this.queryParameters != null) {
               this.queryParameters.clear();
           }
       }

       @Override
       public String toString() {
           return "ParsedQuery{" +
                  "originalQuery='" + originalQuery + '\'' +
                  ", correctedQuery='" + correctedQuery + '\'' +
                  ", queryType=" + queryType +
                  ", actionType=" + actionType +
                  ", customerName='" + customerName + '\'' +
                  ", statusType='" + statusType + '\'' +
                  ", partNumber='" + partNumber + '\'' +
                  ", contractNumber='" + contractNumber + '\'' +
                  ", accountNumner='" + accountNumner + '\'' +
                  ", username='" + userName + '\'' +
                  ", confidence=" + confidence +
                  ", queryParameters=" + queryParameters +
                  '}';
       }
}
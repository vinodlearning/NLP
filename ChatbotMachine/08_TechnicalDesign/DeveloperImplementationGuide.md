# Developer Implementation Guide
## Contract Portal ML Query Processing System

### Quick Start Integration

---

## 🚀 **5-Minute Integration**

### Step 1: Copy Core File
```bash
# Copy the main processor file to your project
cp StructuredQueryProcessor.java src/main/java/com/yourcompany/contracts/
```

### Step 2: Basic Integration
```java
// In your controller/service class
import com.yourcompany.contracts.StructuredQueryProcessor;

public class ContractController {
    
    public String processUserQuery(String userInput) {
        // Main integration point - ONE LINE!
        StructuredQueryResponse response = StructuredQueryProcessor.processQuery(userInput);
        
        // Route based on action type
        return routeToBusinessLogic(response);
    }
}
```

### Step 3: Business Logic Routing
```java
private String routeToBusinessLogic(StructuredQueryResponse response) {
    switch(response.getActionType()) {
        case SEARCH_CONTRACT_BY_NUMBER:
            String contractId = response.getMainProperties().get(MainProperty.CONTRACT_NUMBER);
            return contractService.findById(contractId);
            
        case SEARCH_CONTRACT_BY_ACCOUNT:
            String accountId = response.getMainProperties().get(MainProperty.ACCOUNT_NUMBER);
            return contractService.findByAccount(accountId);
            
        case SEARCH_PARTS_BY_CONTRACT:
            String contractNum = response.getMainProperties().get(MainProperty.CONTRACT_NUMBER);
            return partsService.findByContract(contractNum);
            
        default:
            return "Query processed successfully";
    }
}
```

---

## 📋 **JSF Integration (Complete Example)**

### JSF Managed Bean
```java
@ManagedBean(name = "contractQueryBean")
@ViewScoped
public class ContractQueryBean implements Serializable {
    
    private String userInput;
    private StructuredQueryResponse queryResponse;
    private List<Contract> results;
    private String message;
    
    // Process query action
    public void processQuery() {
        try {
            // Step 1: Process user input
            queryResponse = StructuredQueryProcessor.processQuery(userInput);
            
            // Step 2: Execute business logic
            results = executeQuery(queryResponse);
            
            // Step 3: Set success message
            message = "Found " + results.size() + " results";
            
        } catch (Exception e) {
            message = "Error: " + e.getMessage();
            results = new ArrayList<>();
        }
    }
    
    private List<Contract> executeQuery(StructuredQueryResponse response) {
        switch(response.getActionType()) {
            case SEARCH_CONTRACT_BY_NUMBER:
                return contractDAO.findByNumber(
                    response.getMainProperties().get(MainProperty.CONTRACT_NUMBER));
                    
            case SEARCH_CONTRACT_BY_ACCOUNT:
                return contractDAO.findByAccount(
                    response.getMainProperties().get(MainProperty.ACCOUNT_NUMBER));
                    
            case SEARCH_CONTRACT_BY_CREATED_BY:
                return contractDAO.findByCreatedBy(
                    response.getMainProperties().get(MainProperty.CREATED_BY));
                    
            default:
                return contractDAO.findAll();
        }
    }
    
    // Getters and setters
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    
    public List<Contract> getResults() { return results; }
    public String getMessage() { return message; }
    
    public StructuredQueryResponse getQueryResponse() { return queryResponse; }
}
```

### JSF Page (XHTML)
```xml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:p="http://primefaces.org/ui">

<h:head>
    <title>Contract Query System</title>
</h:head>

<h:body>
    <h:form id="queryForm">
        <p:panel header="Smart Contract Query" style="margin-bottom:20px">
            
            <!-- Query Input -->
            <p:inputTextarea value="#{contractQueryBean.userInput}" 
                           rows="3" cols="80"
                           placeholder="Enter your query: e.g., 'show contract 123456' or 'parts for contract ABC-789'"/>
            <br/><br/>
            
            <!-- Process Button -->
            <p:commandButton value="Process Query" 
                           action="#{contractQueryBean.processQuery}"
                           update="resultsPanel"
                           styleClass="ui-button-success"/>
        </p:panel>
        
        <!-- Results Panel -->
        <p:panel id="resultsPanel" header="Query Results" rendered="#{not empty contractQueryBean.message}">
            
            <!-- Message -->
            <p:messages/>
            <h:outputText value="#{contractQueryBean.message}" style="font-weight:bold;"/>
            <br/><br/>
            
            <!-- Query Analysis -->
            <p:panel header="Query Analysis" rendered="#{not empty contractQueryBean.queryResponse}">
                <p:panelGrid columns="2">
                    <h:outputText value="Original Input:"/>
                    <h:outputText value="#{contractQueryBean.queryResponse.originalInput}"/>
                    
                    <h:outputText value="Corrected Input:"/>
                    <h:outputText value="#{contractQueryBean.queryResponse.correctedInput}"/>
                    
                    <h:outputText value="Query Type:"/>
                    <h:outputText value="#{contractQueryBean.queryResponse.queryType}"/>
                    
                    <h:outputText value="Action Type:"/>
                    <h:outputText value="#{contractQueryBean.queryResponse.actionType}"/>
                    
                    <h:outputText value="Processing Time:"/>
                    <h:outputText value="#{contractQueryBean.queryResponse.processingTimeMs}ms"/>
                </p:panelGrid>
            </p:panel>
            
            <!-- Results Table -->
            <p:dataTable value="#{contractQueryBean.results}" var="contract" 
                        rendered="#{not empty contractQueryBean.results}">
                <p:column headerText="Contract ID">
                    <h:outputText value="#{contract.contractId}"/>
                </p:column>
                <p:column headerText="Contract Name">
                    <h:outputText value="#{contract.contractName}"/>
                </p:column>
                <p:column headerText="Status">
                    <h:outputText value="#{contract.status}"/>
                </p:column>
                <p:column headerText="Total Value">
                    <h:outputText value="#{contract.totalValue}">
                        <f:convertNumber type="currency"/>
                    </h:outputText>
                </p:column>
            </p:dataTable>
            
        </p:panel>
    </h:form>
</h:body>
</html>
```

---

## 🌐 **REST API Integration**

### REST Controller
```java
@RestController
@RequestMapping("/api/contract-query")
@CrossOrigin(origins = "*")
public class ContractQueryRestController {
    
    @Autowired
    private ContractService contractService;
    
    @PostMapping("/process")
    public ResponseEntity<QueryResultResponse> processQuery(@RequestBody QueryRequest request) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Step 1: Process query
            StructuredQueryResponse analysis = StructuredQueryProcessor.processQuery(request.getInput());
            
            // Step 2: Execute business logic
            Object results = executeBusinessLogic(analysis);
            
            // Step 3: Build response
            QueryResultResponse response = QueryResultResponse.builder()
                .success(true)
                .queryAnalysis(analysis)
                .results(results)
                .resultCount(getResultCount(results))
                .executionTimeMs(System.currentTimeMillis() - startTime)
                .build();
                
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(QueryResultResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build());
        }
    }
    
    private Object executeBusinessLogic(StructuredQueryResponse analysis) {
        switch(analysis.getActionType()) {
            case SEARCH_CONTRACT_BY_NUMBER:
                return contractService.findByNumber(
                    analysis.getMainProperties().get(MainProperty.CONTRACT_NUMBER));
                    
            case SEARCH_PARTS_BY_CONTRACT:
                return contractService.getPartsForContract(
                    analysis.getMainProperties().get(MainProperty.CONTRACT_NUMBER));
                    
            case COUNT_CONTRACTS_BY_CREATED_BY:
                return contractService.countByCreatedBy(
                    analysis.getMainProperties().get(MainProperty.CREATED_BY));
                    
            default:
                return Collections.emptyList();
        }
    }
}
```

### Request/Response DTOs
```java
// Request DTO
public class QueryRequest {
    private String input;
    private String userId;
    private String sessionId;
    
    // Getters and setters
}

// Response DTO
@Builder
public class QueryResultResponse {
    private boolean success;
    private StructuredQueryResponse queryAnalysis;
    private Object results;
    private int resultCount;
    private long executionTimeMs;
    private String errorMessage;
    
    // Getters and setters
}
```

---

## 🗄️ **Database Integration**

### SQL Query Generation
```java
@Service
public class QueryExecutionService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<Map<String, Object>> executeQuery(StructuredQueryResponse response) {
        
        // Generate SQL based on response
        String sql = generateSQL(response);
        List<Object> parameters = extractParameters(response);
        
        // Execute query
        return jdbcTemplate.queryForList(sql, parameters.toArray());
    }
    
    private String generateSQL(StructuredQueryResponse response) {
        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        if (response.getEntities().isEmpty()) {
            sql.append("SELECT * ");
        } else {
            sql.append("SELECT ");
            sql.append(response.getEntities().stream()
                .map(EntityAttribute::getAttributeName)
                .collect(Collectors.joining(", ")));
            sql.append(" ");
        }
        
        // FROM clause
        sql.append("FROM ");
        switch(response.getQueryType()) {
            case CONTRACTS:
                sql.append("contracts ");
                break;
            case PARTS:
                sql.append("parts ");
                break;
            case ACCOUNTS:
                sql.append("accounts a JOIN contracts c ON a.account_number = c.account_number ");
                break;
        }
        
        // WHERE clause
        List<String> conditions = new ArrayList<>();
        
        // Add main property conditions
        if (response.getMainProperties().containsKey(MainProperty.CONTRACT_NUMBER)) {
            conditions.add("contract_number = ?");
        }
        if (response.getMainProperties().containsKey(MainProperty.ACCOUNT_NUMBER)) {
            conditions.add("account_number = ?");
        }
        if (response.getMainProperties().containsKey(MainProperty.CREATED_BY)) {
            conditions.add("created_by = ?");
        }
        
        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions));
        }
        
        return sql.toString();
    }
    
    private List<Object> extractParameters(StructuredQueryResponse response) {
        List<Object> params = new ArrayList<>();
        
        // Add main property values
        for (String value : response.getMainProperties().values()) {
            params.add(value);
        }
        
        return params;
    }
}
```

---

## ⚙️ **Configuration & Setup**

### Application Properties
```properties
# application.properties

# Query Processing Configuration
contract.query.max-input-length=500
contract.query.cache-enabled=true
contract.query.cache-ttl=3600

# Database Configuration  
spring.datasource.url=jdbc:postgresql://localhost:5432/contracts
spring.datasource.username=contracts_user
spring.datasource.password=contracts_pass

# Cache Configuration
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379

# Logging Configuration
logging.level.com.yourcompany.contracts=DEBUG
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### Maven Dependencies
```xml
<dependencies>
    <!-- Core Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Cache -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- JSF (if using JSF) -->
    <dependency>
        <groupId>org.apache.myfaces.core</groupId>
        <artifactId>myfaces-impl</artifactId>
    </dependency>
    <dependency>
        <groupId>org.primefaces</groupId>
        <artifactId>primefaces</artifactId>
        <version>10.0.0</version>
    </dependency>
</dependencies>
```

---

## 🧪 **Testing Examples**

### Unit Test
```java
@ExtendWith(MockitoExtension.class)
class StructuredQueryProcessorTest {
    
    @Test
    void testContractQueryProcessing() {
        // Given
        String userInput = "get project type, effective date for contract 123456";
        
        // When
        StructuredQueryResponse response = StructuredQueryProcessor.processQuery(userInput);
        
        // Then
        assertThat(response.getQueryType()).isEqualTo(QueryType.CONTRACTS);
        assertThat(response.getActionType()).isEqualTo(ActionType.SEARCH_CONTRACT_BY_NUMBER);
        assertThat(response.getMainProperties().get(MainProperty.CONTRACT_NUMBER)).isEqualTo("123456");
        assertThat(response.getEntities()).hasSize(2);
    }
    
    @Test
    void testPartsQueryProcessing() {
        // Given
        String userInput = "show parts for contract ABC-789";
        
        // When
        StructuredQueryResponse response = StructuredQueryProcessor.processQuery(userInput);
        
        // Then
        assertThat(response.getQueryType()).isEqualTo(QueryType.PARTS);
        assertThat(response.getActionType()).isEqualTo(ActionType.SEARCH_PARTS_BY_CONTRACT);
        assertThat(response.getMainProperties().get(MainProperty.CONTRACT_NUMBER)).isEqualTo("ABC-789");
    }
}
```

### Integration Test
```java
@SpringBootTest
@AutoConfigureTestDatabase
class ContractQueryIntegrationTest {
    
    @Autowired
    private ContractQueryRestController controller;
    
    @Test
    void testFullQueryProcessing() {
        // Given
        QueryRequest request = new QueryRequest();
        request.setInput("get customer name, status for contract 123456");
        
        // When
        ResponseEntity<QueryResultResponse> response = controller.processQuery(request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getResultCount()).isGreaterThan(0);
    }
}
```

---

## 🎯 **Quick Reference**

### Common Query Patterns
```java
// Contract searches
"show contract 123456"                    → SEARCH_CONTRACT_BY_NUMBER
"get customer name for contract 123456"   → SEARCH_CONTRACT_BY_NUMBER
"contracts for account 789012"           → SEARCH_CONTRACT_BY_ACCOUNT
"contracts created by john"               → SEARCH_CONTRACT_BY_CREATED_BY

// Parts searches  
"show parts for contract 123456"         → SEARCH_PARTS_BY_CONTRACT
"get inventory for contract 123456"      → SEARCH_PARTS_BY_CONTRACT
"parts details for part P12345"          → SEARCH_PARTS_BY_NUMBER

// Count queries
"how many contracts by john"              → COUNT_CONTRACTS_BY_CREATED_BY
"total parts for contract 123456"        → COUNT_PARTS_BY_CONTRACT
```

### Response Structure Access
```java
StructuredQueryResponse response = StructuredQueryProcessor.processQuery(input);

// Access basic info
String queryType = response.getQueryType().toString();
String actionType = response.getActionType().toString();

// Access main properties (key factors)
String contractId = response.getMainProperties().get(MainProperty.CONTRACT_NUMBER);
String accountId = response.getMainProperties().get(MainProperty.ACCOUNT_NUMBER);
String createdBy = response.getMainProperties().get(MainProperty.CREATED_BY);

// Access requested attributes
List<String> attributes = response.getEntities().stream()
    .map(EntityAttribute::getAttributeName)
    .collect(Collectors.toList());

// Access processing info
double processingTime = response.getProcessingTimeMs();
long timestamp = response.getTimestamp();
```

---

## 🚀 **Production Deployment Checklist**

### ✅ Pre-Deployment
- [ ] Copy `StructuredQueryProcessor.java` to project
- [ ] Implement business logic routing
- [ ] Add database integration
- [ ] Configure application properties
- [ ] Add necessary Maven dependencies
- [ ] Write unit tests
- [ ] Write integration tests

### ✅ Deployment
- [ ] Deploy to staging environment
- [ ] Run performance tests
- [ ] Verify query processing accuracy  
- [ ] Test error handling
- [ ] Monitor resource usage
- [ ] Deploy to production

### ✅ Post-Deployment
- [ ] Monitor application logs
- [ ] Track query processing metrics
- [ ] Monitor database performance
- [ ] Set up alerts for errors
- [ ] Document common issues
- [ ] Train support team

**This guide provides everything needed to integrate the Contract Portal ML Query Processing System into your application!** 🎉
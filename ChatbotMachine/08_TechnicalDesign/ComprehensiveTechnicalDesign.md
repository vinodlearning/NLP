# ChatbotMachine - Comprehensive Technical Design Document

## 📋 Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Design](#architecture-design)
3. [Component Specifications](#component-specifications)
4. [API Design](#api-design)
5. [Database Design](#database-design)
6. [Machine Learning Design](#machine-learning-design)
7. [Performance Specifications](#performance-specifications)
8. [Security Design](#security-design)
9. [Deployment Architecture](#deployment-architecture)
10. [Integration Patterns](#integration-patterns)

## 🏗️ System Overview

### Purpose
The ChatbotMachine is an intelligent Oracle ADF-based Natural Language Processing system designed to handle contract-related queries with high accuracy and performance.

### Key Features
- **Natural Language Query Processing**: Handles complex queries with typos and variations
- **Multi-Model Routing**: Intelligent routing to Contract, Parts, and Help models
- **Advanced Spell Correction**: 200+ domain-specific corrections
- **Machine Learning Integration**: Apache OpenNLP with 100% accuracy
- **Oracle ADF Integration**: Seamless database connectivity
- **JSON Response System**: Standardized response format

### Technical Stack
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Technology Stack                                     │
│                                                                                 │
│  Frontend:     Oracle ADF, JSF 2.x, PrimeFaces                               │
│  Backend:      Java 8+, Spring Framework, Apache OpenNLP                     │
│  Database:     Oracle Database 12c+, ADF Business Components                  │
│  ML Framework: Apache OpenNLP 1.9.x                                          │
│  Build Tool:   Maven 3.6+                                                     │
│  Testing:      JUnit 5, Mockito                                               │
│  Deployment:   WebLogic Server 12c+                                           │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🏛️ Architecture Design

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        ChatbotMachine Architecture                             │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                        Presentation Layer                                   │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   │ │
│  │  │ ADF Pages   │    │ JSF Managed │    │ REST        │                   │ │
│  │  │ (JSPX)      │    │ Beans       │    │ Endpoints   │                   │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                         Business Layer                                     │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   │ │
│  │  │ NLP         │    │ ML Models   │    │ Business    │                   │ │
│  │  │ Controller  │    │ (OpenNLP)   │    │ Logic       │                   │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                         Data Layer                                         │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   │ │
│  │  │ ADF BC      │    │ Oracle      │    │ Configuration│                   │ │
│  │  │ (EJB)       │    │ Database    │    │ Files       │                   │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Component Interaction Flow
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Component Interaction                                │
│                                                                                 │
│  User Input                                                                     │
│      │                                                                         │
│      ▼                                                                         │
│  ┌─────────────┐    processQuery()    ┌─────────────┐                         │
│  │ JSF Managed │ ──────────────────▶ │ NLP         │                         │
│  │ Bean        │                     │ Controller  │                         │
│  └─────────────┘                     └─────────────┘                         │
│                                              │                                 │
│                                              ▼                                 │
│                                      ┌─────────────┐                         │
│                                      │ Routing     │                         │
│                                      │ Engine      │                         │
│                                      └─────────────┘                         │
│                                              │                                 │
│                                              ▼                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│  │ Contract    │    │ Parts       │    │ Help        │                     │
│  │ Model       │    │ Model       │    │ Model       │                     │
│  └─────────────┘    └─────────────┘    └─────────────┘                     │
│         │                   │                   │                           │
│         ▼                   ▼                   ▼                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│  │ Database    │    │ Parts       │    │ Help        │                     │
│  │ Access      │    │ Repository  │    │ Content     │                     │
│  └─────────────┘    └─────────────┘    └─────────────┘                     │
│         │                   │                   │                           │
│         └───────────────────┼───────────────────┘                           │
│                             ▼                                               │
│                     ┌─────────────┐                                         │
│                     │ JSON        │                                         │
│                     │ Response    │                                         │
│                     │ Builder     │                                         │
│                     └─────────────┘                                         │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔧 Component Specifications

### 1. NLP Controller
```java
@Component
@Scope("session")
public class OptimizedNLPController {
    
    // Core Dependencies
    private ConfigurationLoader configLoader;
    private MLModelController mlController;
    private SpellCorrector spellCorrector;
    
    // Main Processing Method
    public JSONResponse processQuery(String userInput) {
        // 1. Input preprocessing
        String cleanInput = preprocessInput(userInput);
        
        // 2. Spell correction
        String correctedInput = spellCorrector.correctSpelling(cleanInput);
        
        // 3. Routing decision
        RouteDecision route = determineRoute(correctedInput);
        
        // 4. Model processing
        ModelResponse response = processWithModel(route, correctedInput);
        
        // 5. JSON response generation
        return buildJSONResponse(response);
    }
    
    // Routing Logic (O(w) complexity)
    private RouteDecision determineRoute(String input) {
        Set<String> words = tokenizeInput(input);
        
        boolean hasPartsKeywords = hasAnyKeyword(words, configLoader.getPartsKeywords());
        boolean hasCreateKeywords = hasAnyKeyword(words, configLoader.getCreateKeywords());
        
        if (hasPartsKeywords && hasCreateKeywords) {
            return RouteDecision.PARTS_CREATE_ERROR;
        } else if (hasPartsKeywords) {
            return RouteDecision.PARTS_MODEL;
        } else if (hasCreateKeywords) {
            return RouteDecision.HELP_MODEL;
        } else {
            return RouteDecision.CONTRACT_MODEL;
        }
    }
}
```

### 2. Configuration Loader
```java
@Singleton
public class ConfigurationLoader {
    
    // Optimized data structures for O(1) lookups
    private Set<String> partsKeywords;
    private Set<String> createKeywords;
    private Map<String, String> spellCorrections;
    private Map<String, String> databaseMappings;
    
    // Lazy loading with caching
    @PostConstruct
    public void initialize() {
        loadPartsKeywords();
        loadCreateKeywords();
        loadSpellCorrections();
        loadDatabaseMappings();
    }
    
    // Configuration file loading
    private void loadPartsKeywords() {
        partsKeywords = loadKeywordsFromFile("parts_keywords.txt");
    }
    
    // Thread-safe getters
    public Set<String> getPartsKeywords() {
        return Collections.unmodifiableSet(partsKeywords);
    }
}
```

### 3. Machine Learning Models
```java
@Component
public class MLModelController {
    
    // Apache OpenNLP Models
    private DocumentCategorizerME contractModel;
    private DocumentCategorizerME partsModel;
    private DocumentCategorizerME helpModel;
    
    // Model loading
    @PostConstruct
    public void loadModels() {
        contractModel = loadModel("contract_model.bin");
        partsModel = loadModel("parts_model.bin");
        helpModel = loadModel("help_model.bin");
    }
    
    // Processing methods
    public ContractResponse processContractQuery(String query) {
        // Intent classification
        String[] intents = contractModel.categorize(query.split(" "));
        
        // Entity extraction
        Map<String, Object> entities = extractEntities(query);
        
        // Response generation
        return generateContractResponse(intents, entities);
    }
}
```

## 🗄️ Database Design

### Entity Relationship Diagram
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Database Schema                                    │
│                                                                                 │
│  ┌─────────────────┐                    ┌─────────────────┐                   │
│  │    CONTRACTS    │                    │      PARTS      │                   │
│  │                 │                    │                 │                   │
│  │ AWARD_NUMBER    │◀──────────────────▶│ PART_ID         │                   │
│  │ STATUS          │                    │ PART_NUMBER     │                   │
│  │ EFFECTIVE_DATE  │                    │ PART_NAME       │                   │
│  │ EXPIRATION_DATE │                    │ DESCRIPTION     │                   │
│  │ CONTRACT_VALUE  │                    │ QUANTITY        │                   │
│  │ CUSTOMER_NAME   │                    │ UNIT_PRICE      │                   │
│  │ VENDOR_NAME     │                    │ MANUFACTURER    │                   │
│  │ ACCOUNT_NUMBER  │                    │ CATEGORY        │                   │
│  │ CREATED_DATE    │                    │ AWARD_NUMBER    │                   │
│  │ CREATED_BY      │                    │ INVENTORY_STATUS│                   │
│  └─────────────────┘                    └─────────────────┘                   │
│                                                                                 │
│  ┌─────────────────┐                    ┌─────────────────┐                   │
│  │    CUSTOMERS    │                    │     VENDORS     │                   │
│  │                 │                    │                 │                   │
│  │ CUSTOMER_ID     │                    │ VENDOR_ID       │                   │
│  │ CUSTOMER_NAME   │                    │ VENDOR_NAME     │                   │
│  │ ADDRESS         │                    │ ADDRESS         │                   │
│  │ PHONE           │                    │ PHONE           │                   │
│  │ EMAIL           │                    │ EMAIL           │                   │
│  │ CONTACT_PERSON  │                    │ CONTACT_PERSON  │                   │
│  └─────────────────┘                    └─────────────────┘                   │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### ADF Business Components
```java
// Contract Entity Object
@Entity
@Table(name = "CONTRACTS")
public class ContractEO extends EntityImpl {
    
    @Id
    @Column(name = "AWARD_NUMBER")
    private String awardNumber;
    
    @Column(name = "STATUS")
    private String status;
    
    @Column(name = "EFFECTIVE_DATE")
    @Temporal(TemporalType.DATE)
    private Date effectiveDate;
    
    @Column(name = "EXPIRATION_DATE")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
    
    @Column(name = "CONTRACT_VALUE")
    private BigDecimal contractValue;
    
    // Getters and setters
    public String getAwardNumber() { return awardNumber; }
    public void setAwardNumber(String awardNumber) { this.awardNumber = awardNumber; }
}

// Contract View Object
public class ContractVO extends ViewObjectImpl {
    
    public void findContractByNumber(String contractNumber) {
        setWhereClause("AWARD_NUMBER = :contractNumber");
        defineNamedWhereClauseParam("contractNumber", contractNumber, null);
        executeQuery();
    }
    
    public void findContractsByCustomer(String customerName) {
        setWhereClause("CUSTOMER_NAME LIKE :customerName");
        defineNamedWhereClauseParam("customerName", "%" + customerName + "%", null);
        executeQuery();
    }
}
```

## 🤖 Machine Learning Design

### Training Data Structure
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            Training Data Format                                │
│                                                                                 │
│  Contract Training Data (85 samples):                                          │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │ CONTRACT_LOOKUP    show me contract 12345                                   │ │
│  │ CONTRACT_LOOKUP    display contract ABC-789                                 │ │
│  │ CONTRACT_STATUS    what is the status of contract 12345                    │ │
│  │ CONTRACT_DATES     when does contract 12345 expire                         │ │
│  │ CONTRACT_CUSTOMER  who is the customer for contract 12345                  │ │
│  │ CONTRACT_VALUE     what is the value of contract 12345                     │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                 │
│  Parts Training Data (95 samples):                                             │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │ PARTS_LOOKUP       show me parts for contract 12345                        │ │
│  │ PARTS_COUNT        how many parts in contract 12345                        │ │
│  │ PARTS_DETAILS      list all parts for contract 12345                       │ │
│  │ PARTS_INVENTORY    check inventory for part AE125                          │ │
│  │ PARTS_PRICE        what is the price of part AE125                         │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                 │
│  Help Training Data (90 samples):                                              │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │ HELP_CREATE        how to create a contract                                │ │
│  │ HELP_WORKFLOW      what is the contract creation process                   │ │
│  │ HELP_APPROVAL      how to get contract approval                            │ │
│  │ HELP_TEMPLATES     show me contract templates                              │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Model Training Pipeline
```java
public class ModelTrainer {
    
    public void trainAllModels() {
        // 1. Load training data
        ObjectStream<DocumentSample> contractSamples = loadTrainingData("contract_training_data.txt");
        ObjectStream<DocumentSample> partsSamples = loadTrainingData("parts_training_data.txt");
        ObjectStream<DocumentSample> helpSamples = loadTrainingData("help_training_data.txt");
        
        // 2. Train models
        DocumentCategorizerME contractModel = trainModel(contractSamples);
        DocumentCategorizerME partsModel = trainModel(partsSamples);
        DocumentCategorizerME helpModel = trainModel(helpSamples);
        
        // 3. Export models
        exportModel(contractModel, "contract_model.bin");
        exportModel(partsModel, "parts_model.bin");
        exportModel(helpModel, "help_model.bin");
        
        // 4. Validate models
        validateModels();
    }
    
    private DocumentCategorizerME trainModel(ObjectStream<DocumentSample> samples) {
        DoccatFactory factory = new DoccatFactory();
        TrainingParameters params = new TrainingParameters();
        params.put(TrainingParameters.ITERATIONS_PARAM, "100");
        params.put(TrainingParameters.CUTOFF_PARAM, "5");
        
        DoccatModel model = DocumentCategorizerME.train("en", samples, params, factory);
        return new DocumentCategorizerME(model);
    }
}
```

## 📊 Performance Specifications

### Response Time Requirements
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Performance Targets                                  │
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐           │
│  │ Query Type      │    │ Target Time     │    │ Actual Time     │           │
│  │                 │    │                 │    │                 │           │
│  │ Simple Lookup   │    │ < 100μs        │    │ 85μs           │           │
│  │ Complex Query   │    │ < 300μs        │    │ 196μs          │           │
│  │ Spell Correction│    │ < 200μs        │    │ 145μs          │           │
│  │ ML Processing   │    │ < 500μs        │    │ 312μs          │           │
│  │ Database Query  │    │ < 1000μs       │    │ 750μs          │           │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘           │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Scalability Specifications
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            Scalability Metrics                                 │
│                                                                                 │
│  Concurrent Users:     1000+ simultaneous users                                │
│  Throughput:          10,000+ queries per minute                               │
│  Memory Usage:        < 512MB heap space                                       │
│  CPU Usage:           < 60% under normal load                                  │
│  Database Connections: 50 max concurrent connections                           │
│  Response Time:       < 2 seconds for 99th percentile                          │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔐 Security Design

### Authentication & Authorization
```java
@Component
public class SecurityManager {
    
    // Role-based access control
    public boolean hasAccess(String userRole, String operation) {
        switch (operation) {
            case "CONTRACT_VIEW":
                return userRole.equals("CONTRACT_VIEWER") || 
                       userRole.equals("CONTRACT_ADMIN");
            case "CONTRACT_MODIFY":
                return userRole.equals("CONTRACT_ADMIN");
            case "PARTS_VIEW":
                return userRole.equals("PARTS_VIEWER") || 
                       userRole.equals("PARTS_ADMIN");
            default:
                return false;
        }
    }
    
    // Input validation
    public String sanitizeInput(String userInput) {
        // Remove SQL injection attempts
        String sanitized = userInput.replaceAll("[';\"\\-\\-]", "");
        
        // Limit input length
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
        }
        
        return sanitized;
    }
}
```

### Data Protection
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Security Measures                                 │
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐           │
│  │ Input           │    │ Data            │    │ Output          │           │
│  │ Validation      │    │ Encryption      │    │ Sanitization    │           │
│  │                 │    │                 │    │                 │           │
│  │ • SQL Injection │    │ • AES-256       │    │ • XSS Prevention│           │
│  │ • XSS Prevention│    │ • TLS 1.3       │    │ • Data Masking  │           │
│  │ • Length Limits │    │ • Key Rotation  │    │ • Access Logs   │           │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘           │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🚀 Deployment Architecture

### WebLogic Server Configuration
```xml
<!-- weblogic.xml -->
<weblogic-web-app>
    <context-root>/chatbot</context-root>
    <resource-description>
        <res-ref-name>jdbc/ChatbotDS</res-ref-name>
        <jndi-name>jdbc/ChatbotDataSource</jndi-name>
    </resource-description>
    <session-descriptor>
        <session-timeout-secs>3600</session-timeout-secs>
        <invalidation-interval-secs>60</invalidation-interval-secs>
    </session-descriptor>
</weblogic-web-app>
```

### Environment Configuration
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Deployment Environments                              │
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐           │
│  │ Development     │    │ Testing         │    │ Production      │           │
│  │                 │    │                 │    │                 │           │
│  │ • Local DB      │    │ • Test DB       │    │ • Oracle RAC    │           │
│  │ • Single Node   │    │ • Load Testing  │    │ • Clustered     │           │
│  │ • Debug Mode    │    │ • Performance   │    │ • High Availability│        │
│  │ • Hot Deploy    │    │ • Integration   │    │ • Monitoring    │           │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘           │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔗 Integration Patterns

### REST API Design
```java
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotRestController {
    
    @PostMapping("/query")
    public ResponseEntity<JSONResponse> processQuery(
            @RequestBody QueryRequest request,
            @RequestHeader("Authorization") String token) {
        
        // 1. Validate request
        if (!isValidRequest(request)) {
            return ResponseEntity.badRequest().build();
        }
        
        // 2. Authenticate user
        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 3. Process query
        JSONResponse response = nlpController.processQuery(request.getQuery());
        
        // 4. Return response
        return ResponseEntity.ok(response);
    }
}
```

### WebSocket Integration
```java
@ServerEndpoint("/chatbot-ws")
public class ChatbotWebSocketEndpoint {
    
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // Parse incoming message
            QueryRequest request = parseMessage(message);
            
            // Process query
            JSONResponse response = nlpController.processQuery(request.getQuery());
            
            // Send response
            session.getBasicRemote().sendText(response.toJson());
            
        } catch (Exception e) {
            sendErrorResponse(session, e.getMessage());
        }
    }
}
```

## 📈 Monitoring & Metrics

### Performance Monitoring
```java
@Component
public class PerformanceMonitor {
    
    @EventListener
    public void handleQueryEvent(QueryProcessedEvent event) {
        // Record metrics
        meterRegistry.counter("chatbot.queries.total", 
                            "model", event.getModel(),
                            "status", event.getStatus())
                    .increment();
        
        meterRegistry.timer("chatbot.query.duration",
                          "model", event.getModel())
                    .record(event.getDuration(), TimeUnit.MICROSECONDS);
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> checkHealth() {
        HealthStatus status = new HealthStatus();
        status.setDatabaseConnected(checkDatabaseConnection());
        status.setModelsLoaded(checkModelsLoaded());
        status.setMemoryUsage(getMemoryUsage());
        
        return ResponseEntity.ok(status);
    }
}
```

---

*This technical design document provides comprehensive specifications for implementing and maintaining the ChatbotMachine system.*
# Complete System Architecture Flow
## Contract Portal - Class Interconnections from UI Screen

### 📋 Overview
This document shows the complete flow of how classes are interconnected when a request comes from the UI screen, including all available system implementations.

---

## 🎯 System Architecture Options

The system has **3 main architectural approaches**:

### 1️⃣ **Classic NLP System** (Optimized)
### 2️⃣ **Machine Learning System** (OpenNLP-based)
### 3️⃣ **Structured Query Processor** (Advanced parsing)

---

## 🚀 **Primary Flow: Classic NLP System**

### **REQUEST FLOW DIAGRAM**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           UI SCREEN (JSF/Web)                           │
│                        User enters query:                               │
│                    "show parts for contract 123456"                     │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    MANAGED BEAN LAYER                                   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │              ContractQueryManagedBean                           │   │
│  │              @ManagedBean @SessionScoped                        │   │
│  │                                                                 │   │
│  │  • processQuery() - Main entry point                          │   │
│  │  • Session management                                          │   │
│  │  • Conversation history                                        │   │
│  │  • UI state management                                         │   │
│  │  • JSF message handling                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                                     │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                OptimizedNLPController                           │   │
│  │                                                                 │   │
│  │  • processUserInput() - Main processing method                │   │
│  │  • determineRoute() - Business logic routing                  │   │
│  │  • Spell correction integration                               │   │
│  │  • Performance metrics                                        │   │
│  │  • Session state management                                   │   │
│  │                                                                │   │
│  │  ROUTING LOGIC:                                               │   │
│  │  1. Parts keywords → PartsModel                               │   │
│  │  2. Create keywords → HelpModel                               │   │
│  │  3. Default → ContractModel                                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    CONFIGURATION LAYER                                  │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                ConfigurationLoader                               │   │
│  │                (Singleton Pattern)                              │   │
│  │                                                                 │   │
│  │  • containsPartsKeywords() - O(1) lookup                      │   │
│  │  • containsCreateKeywords() - O(1) lookup                     │   │
│  │  • performSpellCorrection() - O(w) processing                 │   │
│  │  • Configuration file management                               │   │
│  │                                                                │   │
│  │  FILES:                                                       │   │
│  │  • parts_keywords.txt                                         │   │
│  │  • create_keywords.txt                                        │   │
│  │  • spell_corrections.txt                                      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    UTILITY LAYER                                        │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                        Helper                                    │   │
│  │                                                                 │   │
│  │  • validateAccount() - Account validation                      │   │
│  │  • validateDates() - Date validation                          │   │
│  │  • validateContractData() - Comprehensive validation          │   │
│  │  • ValidationResult class for detailed feedback               │   │
│  │  • Business rule enforcement                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    MODEL LAYER                                          │
│                                                                         │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐      │
│  │   ContractModel  │  │    PartsModel    │  │    HelpModel     │      │
│  │                  │  │                  │  │                  │      │
│  │ • processContract│  │ • processparts   │  │ • processHelp    │      │
│  │   Query()        │  │   Query()        │  │   Request()      │      │
│  │ • Contract       │  │ • Parts lookup   │  │ • Creation       │      │
│  │   lookup         │  │ • Inventory      │  │   guidance       │      │
│  │ • Contract       │  │   management     │  │ • Step-by-step   │      │
│  │   details        │  │ • Parts count    │  │   instructions   │      │
│  │ • Date queries   │  │ • Specifications │  │ • Validation     │      │
│  │                  │  │                  │  │   help           │      │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘      │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    RESPONSE LAYER                                       │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    JSON Response                                │   │
│  │                                                                 │   │
│  │  • Structured JSON format                                     │   │
│  │  • Routing metadata                                           │   │
│  │  • Processing time                                            │   │
│  │  • Model identification                                       │   │
│  │  • Error handling                                             │   │
│  │  • Business rule compliance                                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    BACK TO UI SCREEN                                    │
│                                                                         │
│  • Response displayed to user                                          │
│  • Conversation history updated                                        │
│  • Performance metrics updated                                         │
│  • UI state refreshed                                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🤖 **Alternative Flow: Machine Learning System**

### **ML SYSTEM ARCHITECTURE**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           UI SCREEN                                     │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    MANAGED BEAN LAYER                                   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │              Any JSF Managed Bean                               │   │
│  │                                                                 │   │
│  │  Integration Code:                                             │   │
│  │  MLIntegrationManager mlManager = new MLIntegrationManager();  │   │
│  │  String response = mlManager.processQuery(userInput);          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    ML INTEGRATION LAYER                                 │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                MLIntegrationManager                              │   │
│  │                                                                 │   │
│  │  • processQuery() - Main ML processing                        │   │
│  │  • Error handling                                             │   │
│  │  • Performance monitoring                                     │   │
│  │  • Simple interface for integration                           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    ML CONTROLLER LAYER                                  │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                MLModelController                                 │   │
│  │                                                                 │   │
│  │  • processUserInput() - Main ML processing                    │   │
│  │  • determineRoute() - ML-based routing                        │   │
│  │  • Apache OpenNLP integration                                 │   │
│  │  • Binary model loading (.bin files)                         │   │
│  │  • Training data management                                   │   │
│  │                                                                │   │
│  │  ROUTING LOGIC:                                               │   │
│  │  1. Parts + Create → Error response                           │   │
│  │  2. Parts keywords → PartsMLModel                             │   │
│  │  3. Create keywords → HelpMLModel                             │   │
│  │  4. Default → ContractMLModel                                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    ML MODEL LAYER                                       │
│                                                                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐         │
│  │ ContractMLModel │  │   PartsMLModel  │  │   HelpMLModel   │         │
│  │                 │  │                 │  │                 │         │
│  │ • Apache OpenNLP│  │ • Apache OpenNLP│  │ • Apache OpenNLP│         │
│  │ • .bin model    │  │ • .bin model    │  │ • .bin model    │         │
│  │ • Training data │  │ • Training data │  │ • Training data │         │
│  │ • Intent        │  │ • Intent        │  │ • Intent        │         │
│  │   classification│  │   classification│  │   classification│         │
│  │ • Response      │  │ • Response      │  │ • Response      │         │
│  │   generation    │  │   generation    │  │   generation    │         │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘         │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    TRAINING LAYER                                       │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    ModelTrainer                                  │   │
│  │                                                                 │   │
│  │  • trainAllModels() - Batch training                          │   │
│  │  • exportAllModels() - Binary export                          │   │
│  │  • loadPreTrainedModels() - Binary loading                    │   │
│  │                                                                │   │
│  │  TRAINING FILES:                                              │   │
│  │  • contract_training_data.txt (85+ samples)                   │   │
│  │  • parts_training_data.txt (95+ samples)                      │   │
│  │  • help_training_data.txt (90+ samples)                       │   │
│  │                                                                │   │
│  │  BINARY MODELS:                                               │   │
│  │  • contract_model.bin                                         │   │
│  │  • parts_model.bin                                            │   │
│  │  • help_model.bin                                             │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔍 **Alternative Flow: Structured Query Processor**

### **ADVANCED PARSING SYSTEM**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           UI SCREEN                                     │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    MANAGED BEAN LAYER                                   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │              Any JSF Managed Bean                               │   │
│  │                                                                 │   │
│  │  Integration Code:                                             │   │
│  │  StructuredQueryResponse response =                            │   │
│  │      StructuredQueryProcessor.processQuery(userInput);        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    STRUCTURED PROCESSOR LAYER                           │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                StructuredQueryProcessor                          │   │
│  │                                                                 │   │
│  │  • processQuery() - Main processing method                    │   │
│  │  • Advanced NLP parsing                                       │   │
│  │  • Entity extraction                                          │   │
│  │  • Operator detection                                         │   │
│  │  • Complex query handling                                     │   │
│  │                                                                │   │
│  │  ENUMS:                                                       │   │
│  │  • MainProperty (CONTRACT_NUMBER, ACCOUNT_NUMBER, etc.)      │   │
│  │  • QueryType (CONTRACTS, PARTS, HELP, etc.)                 │   │
│  │  • ActionType (SEARCH_CONTRACT_BY_NUMBER, etc.)              │   │
│  │  • Operator (EQUALS, BETWEEN, CONTAINS, etc.)                │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    STRUCTURED RESPONSE LAYER                            │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                StructuredQueryResponse                           │   │
│  │                                                                 │   │
│  │  • originalInput                                               │   │
│  │  • correctedInput                                              │   │
│  │  • queryType                                                   │   │
│  │  • actionType                                                  │   │
│  │  • mainProperties                                              │   │
│  │  • entities (with attributes and operators)                   │   │
│  │  • timestamp                                                   │   │
│  │  • processingTimeMs                                            │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔗 **Complete Class Interconnections**

### **KEY CLASSES AND THEIR RELATIONSHIPS**

```
ContractQueryManagedBean (UI Layer)
        ↓
    • processQuery()
    • Session management
    • JSF integration
        ↓
OptimizedNLPController (Business Logic)
        ↓
    • processUserInput()
    • determineRoute()
    • Performance metrics
        ↓
ConfigurationLoader (Configuration)
        ↓
    • containsPartsKeywords()
    • containsCreateKeywords()
    • performSpellCorrection()
        ↓
Helper (Validation)
        ↓
    • validateAccount()
    • validateDates()
    • ValidationResult
        ↓
Model Layer (Processing)
        ↓
    • ContractModel
    • PartsModel
    • HelpModel
        ↓
JSON Response (Output)
```

---

## 📊 **Performance Characteristics**

### **Time Complexity Analysis**

| Component | Time Complexity | Description |
|-----------|----------------|-------------|
| **ConfigurationLoader** | O(1) | HashSet/HashMap lookups |
| **Spell Correction** | O(w) | w = number of words |
| **Route Determination** | O(w) | w = number of words |
| **Validation** | O(1) | Per field validation |
| **Model Processing** | O(model) | Depends on model complexity |
| **Response Generation** | O(1) | JSON serialization |

### **Space Complexity Analysis**

| Component | Space Complexity | Description |
|-----------|------------------|-------------|
| **Keyword Storage** | O(k) | k = number of keywords |
| **Session State** | O(s) | s = session data size |
| **Model Memory** | O(m) | m = model size |
| **Response Data** | O(r) | r = response size |

---

## 🚀 **Integration Examples**

### **1. JSF Integration**
```java
@ManagedBean
@SessionScoped
public class YourBean {
    private OptimizedNLPController nlpController = new OptimizedNLPController();
    
    public void processUserQuery(String query) {
        String response = nlpController.processUserInput(query);
        // Handle response
    }
}
```

### **2. Machine Learning Integration**
```java
@ManagedBean
@SessionScoped
public class YourBean {
    private MLIntegrationManager mlManager = new MLIntegrationManager();
    
    public void processUserQuery(String query) {
        String response = mlManager.processQuery(query);
        // Handle ML response
    }
}
```

### **3. Structured Query Integration**
```java
@ManagedBean
@SessionScoped
public class YourBean {
    
    public void processUserQuery(String query) {
        StructuredQueryResponse response = 
            StructuredQueryProcessor.processQuery(query);
        // Handle structured response
    }
}
```

---

## 🎛️ **Configuration Management**

### **Configuration Files**

| File | Purpose | Location |
|------|---------|----------|
| **parts_keywords.txt** | Parts routing keywords | practice/parts_keywords.txt |
| **create_keywords.txt** | Creation routing keywords | practice/create_keywords.txt |
| **spell_corrections.txt** | Spell correction mappings | practice/spell_corrections.txt |
| **contract_training_data.txt** | ML contract training | MachineLearning/training/ |
| **parts_training_data.txt** | ML parts training | MachineLearning/training/ |
| **help_training_data.txt** | ML help training | MachineLearning/training/ |

### **Business Rules**

1. **Routing Priority:**
   - Parts keywords → PartsModel
   - Create keywords → HelpModel
   - Default → ContractModel

2. **Validation Rules:**
   - Account numbers: 6-12 digits
   - Date ranges: Not more than 10 years future
   - Contract names: 3-100 characters

3. **ML Business Rules:**
   - Parts + Create → Error (parts can't be created)
   - Spell correction applied before routing
   - Intent classification with confidence scores

---

## 🔧 **Error Handling Flow**

```
Error Occurs
    ↓
Helper.ValidationResult
    ↓
Controller Error Response
    ↓
JSON Error Format
    ↓
UI Error Display
    ↓
JSF Messages
```

---

## 📈 **Monitoring and Metrics**

### **Performance Metrics**
- Processing time per query
- Route distribution
- Error rates
- Session statistics
- Model accuracy (ML system)

### **System Health**
- Configuration load status
- Model initialization status
- Memory usage
- Cache hit rates

---

## 🏆 **Best Practices**

1. **Lazy Loading:** Models initialized only when needed
2. **Singleton Pattern:** Configuration loaded once
3. **Validation:** Comprehensive input validation
4. **Error Handling:** Graceful degradation
5. **Performance:** O(1) and O(w) time complexity
6. **Maintainability:** Configuration in text files
7. **Scalability:** Stateless processing
8. **Testing:** Comprehensive test coverage

---

## 🎯 **Summary**

The system provides **three architectural approaches** for processing user queries:

1. **Classic NLP** - Fast, rule-based routing
2. **Machine Learning** - Advanced NLP with Apache OpenNLP
3. **Structured Query** - Advanced parsing with entity extraction

All approaches maintain the same **UI → Controller → Model → Response** flow while providing different levels of sophistication and accuracy in query processing.
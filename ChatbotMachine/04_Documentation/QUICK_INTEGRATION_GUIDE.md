# Quick Integration Guide
## How to Use the Contract Portal Systems

### 🎯 **Choose Your Architecture**

You have **3 system options** to choose from:

| System | Best For | Complexity | Performance |
|--------|----------|------------|-------------|
| **Classic NLP** | Simple routing, fast responses | Low | High |
| **Machine Learning** | Advanced NLP, learning capability | Medium | Medium |
| **Structured Query** | Complex parsing, entity extraction | High | Medium |

---

## 🚀 **Option 1: Classic NLP System (Recommended)**

### **Quick Start - 3 Steps**

#### **Step 1: Add to your JSF Managed Bean**
```java
import view.practice.OptimizedNLPController;

@ManagedBean
@SessionScoped
public class YourBean {
    private OptimizedNLPController nlpController = new OptimizedNLPController();
    private String userInput;
    private String systemResponse;
    
    public void processUserQuery() {
        systemResponse = nlpController.processUserInput(userInput);
    }
    
    // getters and setters...
}
```

#### **Step 2: Add to your JSF Page**
```xml
<h:form>
    <h:inputText value="#{yourBean.userInput}" placeholder="Enter your query"/>
    <h:commandButton value="Process" action="#{yourBean.processUserQuery}"/>
    <h:outputText value="#{yourBean.systemResponse}" escape="false"/>
</h:form>
```

#### **Step 3: Configure Keywords (Optional)**
- Edit `practice/parts_keywords.txt` - Add parts-related keywords
- Edit `practice/create_keywords.txt` - Add creation-related keywords  
- Edit `practice/spell_corrections.txt` - Add spell corrections

### **Sample Input/Output**
```
Input: "show parts for contract 123456"
Output: {
  "responseType": "PARTS_RESULT",
  "message": "Parts query processed",
  "routingInfo": {
    "targetModel": "PartsModel",
    "processingTime": 2.3
  }
}
```

---

## 🤖 **Option 2: Machine Learning System**

### **Quick Start - 2 Steps**

#### **Step 1: Add to your JSF Managed Bean**
```java
import MachineLearning.integration.MLIntegrationManager;

@ManagedBean
@SessionScoped
public class YourBean {
    private MLIntegrationManager mlManager = new MLIntegrationManager();
    private String userInput;
    private String mlResponse;
    
    public void processWithML() {
        mlResponse = mlManager.processQuery(userInput);
    }
    
    // getters and setters...
}
```

#### **Step 2: Train Models (One-time)**
```java
// Run this once to train models
MLModelController controller = new MLModelController();
controller.trainAllModels();
controller.exportAllModels();
```

### **Sample Input/Output**
```
Input: "how many parts for contract 123456"
Output: {
  "responseType": "PARTS_COUNT_SUCCESS",
  "totalParts": 45,
  "routingInfo": {
    "targetModel": "PARTS",
    "mlFramework": "Apache OpenNLP",
    "processingTimeMs": 15.2
  }
}
```

---

## 🔍 **Option 3: Structured Query Processor**

### **Quick Start - 1 Step**

#### **Single Integration**
```java
import StructuredQueryProcessor;
import StructuredQueryResponse;

@ManagedBean
@SessionScoped
public class YourBean {
    private String userInput;
    private StructuredQueryResponse structuredResponse;
    
    public void processStructuredQuery() {
        structuredResponse = StructuredQueryProcessor.processQuery(userInput);
    }
    
    // getters and setters...
}
```

### **Sample Input/Output**
```
Input: "effective,expir,project for contract 123456"
Output: {
  "originalInput": "effective,expir,project for contract 123456",
  "correctedInput": "effective,expiration,project for contract 123456",
  "queryType": "CONTRACTS",
  "actionType": "SEARCH_CONTRACT_BY_NUMBER",
  "mainProperties": {
    "CONTRACT_NUMBER": "123456"
  },
  "entities": [
    {
      "attributeName": "effectiveDate",
      "operator": "EQUALS"
    },
    {
      "attributeName": "expirationDate", 
      "operator": "EQUALS"
    }
  ]
}
```

---

## 📋 **Common Integration Patterns**

### **Pattern 1: Direct Integration**
```java
// Direct controller call
String response = nlpController.processUserInput("show contract 123456");
```

### **Pattern 2: Service Layer**
```java
// Create a service layer
@Service
public class QueryService {
    private OptimizedNLPController nlpController = new OptimizedNLPController();
    
    public String processQuery(String input) {
        return nlpController.processUserInput(input);
    }
}
```

### **Pattern 3: REST API**
```java
@RestController
@RequestMapping("/api/query")
public class QueryController {
    private OptimizedNLPController nlpController = new OptimizedNLPController();
    
    @PostMapping("/process")
    public ResponseEntity<String> processQuery(@RequestBody String query) {
        String response = nlpController.processUserInput(query);
        return ResponseEntity.ok(response);
    }
}
```

---

## 🎛️ **Configuration Quick Reference**

### **Routing Keywords**
```
Parts Keywords: parts, part, lines, line, components, component
Create Keywords: create, creating, make, new, add, generate
```

### **Business Rules**
1. **Parts keywords** → Routes to PartsModel
2. **Create keywords** → Routes to HelpModel
3. **Default** → Routes to ContractModel
4. **Parts + Create** → Error (parts can't be created)

### **Validation Rules**
- Account numbers: 6-12 digits
- Contract names: 3-100 characters
- Dates: Not more than 10 years in future

---

## 🔧 **Troubleshooting**

### **Common Issues**

| Issue | Solution |
|-------|----------|
| **NullPointerException** | Check if controller is properly initialized |
| **Wrong routing** | Verify keywords in configuration files |
| **Slow performance** | Use Classic NLP system for best speed |
| **ML models not loading** | Run training process first |
| **Configuration not loading** | Check file paths and permissions |

### **Performance Tips**
1. **Use Classic NLP** for high-volume applications
2. **Cache responses** for repeated queries
3. **Lazy load models** to save memory
4. **Monitor processing times** and optimize bottlenecks

---

## 🧪 **Testing Your Integration**

### **Test Queries**
```java
// Test different routing scenarios
String[] testQueries = {
    "show parts for contract 123456",     // → PartsModel
    "create a new contract",              // → HelpModel  
    "display contract details for ABC789", // → ContractModel
    "how many parts for contract 555666", // → PartsModel
    "steps to create contract",           // → HelpModel
    "show contract 12345 for account 567890" // → ContractModel
};

for (String query : testQueries) {
    String response = nlpController.processUserInput(query);
    System.out.println("Query: " + query);
    System.out.println("Response: " + response);
    System.out.println("---");
}
```

### **Performance Testing**
```java
// Test processing time
long startTime = System.currentTimeMillis();
String response = nlpController.processUserInput("test query");
long processingTime = System.currentTimeMillis() - startTime;
System.out.println("Processing time: " + processingTime + "ms");
```

---

## 📊 **System Comparison**

### **When to Use Each System**

#### **Classic NLP System**
✅ **Use when:**
- Need fast responses (< 5ms)
- Simple routing requirements
- High-volume applications
- Limited complexity

#### **Machine Learning System**  
✅ **Use when:**
- Need advanced NLP capabilities
- Want learning/training capability
- Complex intent classification
- Can accept slower responses (10-50ms)

#### **Structured Query Processor**
✅ **Use when:**
- Need complex entity extraction
- Advanced parsing requirements
- Multiple operators (BETWEEN, CONTAINS, etc.)
- Complex query analysis

---

## 🏆 **Best Practices**

### **Integration Best Practices**
1. **Always validate input** before processing
2. **Handle errors gracefully** with try-catch blocks
3. **Log processing times** for performance monitoring
4. **Use appropriate system** for your complexity needs
5. **Test with various inputs** including edge cases

### **Performance Best Practices**
1. **Initialize controllers once** (don't recreate per request)
2. **Use session scoped beans** for stateful operations
3. **Cache frequently used responses** if applicable
4. **Monitor memory usage** with ML models
5. **Profile your application** to identify bottlenecks

---

## 📞 **Support**

### **Getting Help**
- Review `COMPLETE_SYSTEM_ARCHITECTURE_FLOW.md` for detailed architecture
- Check test classes for usage examples
- Review configuration files for customization options
- Use the built-in error handling and logging

### **Key Files to Review**
- `practice/ContractQueryManagedBean.java` - Full JSF integration example
- `practice/OptimizedNLPController.java` - Main controller implementation
- `StructuredQueryProcessor.java` - Advanced parsing system
- `MachineLearning/integration/MLIntegrationManager.java` - ML integration

---

## 🎯 **Summary**

**For most applications, start with the Classic NLP System** - it's fast, simple, and handles 90% of use cases effectively. Upgrade to ML or Structured Query systems only when you need their advanced capabilities.

**Choose based on your needs:**
- **Speed** → Classic NLP
- **Intelligence** → Machine Learning  
- **Complexity** → Structured Query Processor
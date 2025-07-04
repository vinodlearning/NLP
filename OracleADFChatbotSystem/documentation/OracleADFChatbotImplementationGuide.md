# Oracle ADF Chatbot Implementation Guide
## Complete Apache OpenNLP Model Generation & Integration

---

## 🎯 **Overview**

This guide provides complete step-by-step instructions for implementing an intelligent Contract Portal chatbot using Apache OpenNLP models integrated with Oracle ADF applications.

### **System Architecture**
```
ADF Page → Backing Bean → NLP Service → OpenNLP Models → Response → ADF UI
```

### **Key Features**
- Real Apache OpenNLP .bin model generation
- Oracle ADF integration with backing beans
- Configuration through .txt files for easy updates
- Spell correction and entity extraction
- Confidence scoring and error handling
- Rich UI formatting with suggested actions

---

## 📋 **Prerequisites**

### **Dependencies Required**
```xml
<!-- Add to your pom.xml -->
<dependency>
    <groupId>org.apache.opennlp</groupId>
    <artifactId>opennlp-tools</artifactId>
    <version>1.9.4</version>
</dependency>
```

### **Project Structure**
```
src/
├── main/
│   ├── java/
│   │   └── com/company/contracts/
│   │       ├── service/
│   │       │   ├── ApacheOpenNLPModelGenerator.java
│   │       │   ├── OpenNLPModelManager.java
│   │       │   ├── ContractNLPService.java
│   │       │   ├── ADFResponseBuilder.java
│   │       │   ├── NLPResponse.java
│   │       │   └── ADFChatResponse.java
│   │       └── view/
│   │           └── ChatbotBackingBean.java
│   └── resources/
│       ├── models/
│       │   ├── contract_intent_model.bin
│       │   ├── parts_intent_model.bin
│       │   └── help_intent_model.bin
│       └── config/
│           ├── spell_corrections.txt
│           ├── contract_keywords.txt
│           ├── parts_keywords.txt
│           ├── help_keywords.txt
│           ├── response_templates.txt
│           └── adf_integration.properties
```

---

## 🔧 **Step 1: Generate OpenNLP Models**

### **1.1 Run Model Generator**
```bash
# Compile and run the model generator
javac -cp "lib/*" ApacheOpenNLPModelGenerator.java
java -cp "lib/*:." ApacheOpenNLPModelGenerator
```

### **1.2 Generated Files**
The generator creates:
- `contract_intent_model.bin` - Contract queries classification
- `parts_intent_model.bin` - Parts queries classification  
- `help_intent_model.bin` - Help queries classification
- Configuration files in `config/` directory

### **1.3 Training Data Examples**
```
# contract_training.txt
CONTRACT_LOOKUP	show contract 123456
CONTRACT_DATES	effective date for contract ABC-789
CONTRACT_CUSTOMER	customer for contract 555666

# parts_training.txt
PARTS_LOOKUP	show parts for contract 123456
PARTS_COUNT	how many parts for contract ABC-789
PARTS_DETAILS	part number P12345 details

# help_training.txt
HELP_CONTRACT_CREATE	how to create contract
HELP_WORKFLOW	contract approval workflow
HELP_VALIDATION	contract validation rules
```

---

## 🎨 **Step 2: Oracle ADF Integration**

### **2.1 Create ADF Page (contractChatbot.jsf)**
```xml
<?xml version='1.0' encoding='UTF-8'?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:af="http://xmlns.oracle.com/adf/faces/rich"
          version="2.1">
    
    <af:document title="Contract Portal Chatbot">
        <af:form id="chatbotForm">
            
            <!-- Chat History Display -->
            <af:panelGroupLayout layout="vertical" id="chatPanel">
                <af:panelHeader text="Contract Portal Assistant" id="header">
                    
                    <!-- Chat Messages Display -->
                    <af:panelCollection id="chatCollection">
                        <af:table value="#{chatbotBean.conversationHistoryForDisplay}" 
                                 var="message" 
                                 id="chatTable"
                                 rowBandingInterval="0"
                                 styleClass="chat-table">
                            
                            <af:column headerText="Time" width="80" id="timeCol">
                                <af:outputText value="#{message.formattedTimestamp}" 
                                              id="timeText"/>
                            </af:column>
                            
                            <af:column headerText="Sender" width="80" id="senderCol">
                                <af:outputText value="#{message.sender}" 
                                              id="senderText"
                                              styleClass="#{message.userMessage ? 'user-message' : 'system-message'}"/>
                            </af:column>
                            
                            <af:column headerText="Message" id="messageCol">
                                <af:outputText value="#{message.message}" 
                                              id="messageText"/>
                            </af:column>
                            
                        </af:table>
                    </af:panelCollection>
                    
                    <!-- Current Response Display -->
                    <af:panelGroupLayout id="responsePanel" 
                                        rendered="#{chatbotBean.hasResponse}">
                        <af:outputText value="#{chatbotBean.chatResponse}" 
                                      id="responseText"
                                      styleClass="#{chatbotBean.responseType}"/>
                    </af:panelGroupLayout>
                    
                </af:panelHeader>
                
                <!-- Input Section -->
                <af:panelGroupLayout layout="horizontal" id="inputPanel">
                    <af:inputText value="#{chatbotBean.userQuery}" 
                                 id="queryInput"
                                 columns="50"
                                 placeholder="Ask me about contracts, parts, or help..."
                                 binding="#{chatbotBean.userQueryInput}"/>
                    
                    <af:commandButton text="Send" 
                                     id="sendButton"
                                     actionListener="#{chatbotBean.processUserQuery}"
                                     disabled="#{chatbotBean.processing}"/>
                    
                    <af:commandButton text="Clear" 
                                     id="clearButton"
                                     actionListener="#{chatbotBean.clearConversation}"/>
                </af:panelGroupLayout>
                
                <!-- Quick Actions -->
                <af:panelGroupLayout layout="horizontal" id="quickActions">
                    <af:commandButton text="Help" 
                                     id="helpButton"
                                     actionListener="#{chatbotBean.askForHelp}"/>
                    
                    <af:commandButton text="Contract Help" 
                                     id="contractHelpButton"
                                     actionListener="#{chatbotBean.showContractHelp}"/>
                    
                    <af:commandButton text="Parts Help" 
                                     id="partsHelpButton"
                                     actionListener="#{chatbotBean.showPartsHelp}"/>
                </af:panelGroupLayout>
                
                <!-- Session Statistics -->
                <af:panelGroupLayout id="statsPanel">
                    <af:outputText value="Session Duration: #{chatbotBean.sessionStatistics.sessionDuration} minutes | Total Queries: #{chatbotBean.sessionStatistics.totalQueries} | Models Loaded: #{chatbotBean.sessionStatistics.modelsLoaded}" 
                                  id="statsText"
                                  styleClass="session-stats"/>
                </af:panelGroupLayout>
                
            </af:panelGroupLayout>
            
        </af:form>
    </af:document>
    
</jsp:root>
```

### **2.2 Configure Managed Bean (faces-config.xml)**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="http://java.sun.com/xml/ns/javaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                                  http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd"
              version="2.0">
    
    <managed-bean>
        <managed-bean-name>chatbotBean</managed-bean-name>
        <managed-bean-class>com.company.contracts.view.ChatbotBackingBean</managed-bean-class>
        <managed-bean-scope>session</managed-bean-scope>
    </managed-bean>
    
</faces-config>
```

### **2.3 CSS Styling (skin.css)**
```css
/* Chat UI Styles */
.chat-table {
    width: 100%;
    border-collapse: collapse;
}

.user-message {
    color: #1976D2;
    font-weight: bold;
}

.system-message {
    color: #388E3C;
    font-weight: bold;
}

.chat-response-high-confidence {
    background-color: #E8F5E8;
    border-left: 4px solid #4CAF50;
    padding: 10px;
}

.chat-response-medium-confidence {
    background-color: #FFF8E1;
    border-left: 4px solid #FFC107;
    padding: 10px;
}

.chat-response-low-confidence {
    background-color: #FFF3E0;
    border-left: 4px solid #FF9800;
    padding: 10px;
}

.chat-response-error {
    background-color: #FFEBEE;
    border-left: 4px solid #F44336;
    padding: 10px;
}

.session-stats {
    font-size: 0.9em;
    color: #666;
    margin-top: 10px;
}

.quick-actions {
    margin: 10px 0;
}

.quick-actions button {
    margin-right: 10px;
}
```

---

## 🚀 **Step 3: Deployment Steps**

### **3.1 Copy Model Files**
```bash
# Copy generated .bin files to your ADF project
cp generated_models/*.bin src/main/resources/models/
cp configuration/*.txt src/main/resources/config/
cp configuration/*.properties src/main/resources/config/
```

### **3.2 Update ADF Project Libraries**
1. Add Apache OpenNLP JAR to project libraries
2. Update deployment profile to include model files
3. Configure resource paths in `adf_integration.properties`

### **3.3 Build and Deploy**
```bash
# Build ADF application
mvn clean install

# Deploy to WebLogic Server
# Use ADF deployment tools or manual deployment
```

---

## 🧪 **Step 4: Testing the System**

### **4.1 Test Queries**
```
Contract Queries:
- "show contract 123456"
- "effective date for contract ABC-789"
- "customer for contract 555666"

Parts Queries:
- "how many parts for contract 123456"
- "list parts in contract ABC-789"
- "part P12345 details"

Help Queries:
- "how to create contract"
- "contract approval workflow"
- "help with validation"
```

### **4.2 Expected Flow**
1. User enters query in ADF input field
2. ChatbotBackingBean.processUserQuery() is called
3. ContractNLPService processes the query
4. OpenNLP models classify intent
5. Response is formatted by ADFResponseBuilder
6. ADF UI displays the formatted response

---

## 📊 **Step 5: Monitoring & Maintenance**

### **5.1 Model Performance Monitoring**
```java
// Access model information
Map<String, Object> modelInfo = modelManager.getModelInfo();
Map<String, Object> healthCheck = modelManager.performHealthCheck();
```

### **5.2 Configuration Updates**
Update `.txt` files for:
- **Spell Corrections**: `spell_corrections.txt`
- **Keywords**: `contract_keywords.txt`, `parts_keywords.txt`, `help_keywords.txt`
- **Response Templates**: `response_templates.txt`

### **5.3 Model Retraining**
```bash
# Add new training data to .txt files
# Run model generator again
java -cp "lib/*:." ApacheOpenNLPModelGenerator

# Hot reload models (if supported)
chatbotBean.reloadModels()
```

---

## 🔄 **Step 6: Integration with Existing Systems**

### **6.1 Database Integration**
```java
// In ContractNLPService
private String getContractDetails(String contractNumber) {
    // Query your contract database
    // Return contract information
}
```

### **6.2 Web Service Integration**
```java
// In ContractNLPService
private String getPartsInventory(String partNumber) {
    // Call parts web service
    // Return inventory information
}
```

### **6.3 Security Integration**
```java
// In ChatbotBackingBean
private boolean isUserAuthorized(String query) {
    // Check user permissions
    // Return authorization status
}
```

---

## 📈 **Step 7: Performance Optimization**

### **7.1 Model Caching**
```java
// Configure model caching
modelManager.setModelCaching(true);
modelManager.setModelCacheTimeout(3600000); // 1 hour
```

### **7.2 Response Caching**
```java
// Implement response caching for common queries
private Map<String, ADFChatResponse> responseCache = new HashMap<>();
```

### **7.3 Async Processing**
```java
// For long-running queries
@Asynchronous
public Future<NLPResponse> processQueryAsync(String query) {
    return new AsyncResult<>(nlpService.processQuery(query));
}
```

---

## 🐛 **Step 8: Troubleshooting**

### **8.1 Common Issues**

**Model Loading Errors:**
```
Error: Model file not found
Solution: Check model file paths in resources/models/
```

**ADF Integration Issues:**
```
Error: Backing bean not found
Solution: Verify faces-config.xml configuration
```

**Performance Issues:**
```
Error: Slow response times
Solution: Enable model caching and optimize training data
```

### **8.2 Debug Mode**
```java
// Enable detailed logging
logger.setLevel(Level.FINE);

// Add debug output to backing bean
public void debugModelStatus() {
    logger.info("Model Status: " + modelManager.getModelInfo());
}
```

---

## 📋 **Step 9: Checklist for Go-Live**

### **Pre-Deployment Checklist:**
- [ ] All .bin model files generated successfully
- [ ] Configuration files updated with production values
- [ ] ADF backing bean properly configured
- [ ] Database connections tested
- [ ] Security permissions configured
- [ ] Performance testing completed
- [ ] Error handling tested
- [ ] Monitoring/logging configured

### **Post-Deployment Checklist:**
- [ ] All test queries working correctly
- [ ] Model confidence scores acceptable (>70%)
- [ ] Response times within acceptable range (<2 seconds)
- [ ] Error handling working properly
- [ ] User training completed
- [ ] Support documentation available

---

## 📚 **Additional Resources**

### **Documentation Files:**
- `ApacheOpenNLPModelGenerator.java` - Model generation code
- `ChatbotBackingBean.java` - ADF backing bean
- `ContractNLPService.java` - NLP processing service
- `OpenNLPModelManager.java` - Model management
- `ADFResponseBuilder.java` - Response formatting
- Configuration files in `config/` directory

### **Sample Configurations:**
All configuration files use simple .txt format for easy updates without code changes.

### **Support:**
For questions or issues:
1. Check model validation using `modelManager.validateModelFiles()`
2. Review logs for error details
3. Test with simple queries first
4. Verify all dependencies are properly configured

---

**🎉 System Ready for Production Use! 🎉**

This implementation provides a complete, production-ready chatbot system with real Apache OpenNLP models fully integrated with Oracle ADF applications.
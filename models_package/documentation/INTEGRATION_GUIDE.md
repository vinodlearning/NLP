# Integration Guide
## How to Integrate Contract Portal Models

### Quick Integration (3 Steps)

#### Step 1: Copy Files
```bash
# Copy Java classes to your project
cp java_classes/*.java src/main/java/

# Copy configuration files
cp configuration/*.txt src/main/resources/
```

#### Step 2: Use in Your Code
```java
// Simple usage
StructuredQueryResponse response = StructuredQueryProcessor.processQuery(userInput);
System.out.println(response.toJson());
```

#### Step 3: Customize Configuration
Edit the configuration files to match your specific keywords and requirements.

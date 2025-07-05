# Quick Data Access Guide

## How to Get All the Data You Need

### 1. Basic Setup
```java
StandardJSONProcessor processor = new StandardJSONProcessor();
StandardJSONProcessor.QueryResult result = processor.processQueryToObject(userInput);
```

### 2. Get Corrected Text
```java
// Method 1: Using convenience method (RECOMMENDED)
String correctedText = result.getCorrectedInput();

// Method 2: Direct field access
String correctedText = result.inputTracking.correctedInput;

// Method 3: Check if corrections exist
if (result.hasSpellCorrections()) {
    String correctedText = result.getCorrectedInput();
    System.out.println("Corrected: " + correctedText);
}
```

### 3. Get Original Input
```java
// Method 1: Using convenience method (RECOMMENDED)
String originalInput = result.getOriginalInput();

// Method 2: Direct field access
String originalInput = result.inputTracking.originalInput;
```

### 4. Get Query Type
```java
// Method 1: Using convenience method (RECOMMENDED)
String queryType = result.getQueryType();

// Method 2: Direct field access
String queryType = result.metadata.queryType;

// Possible values: "CONTRACTS", "PARTS"
```

### 5. Get Action Type
```java
// Method 1: Using convenience method (RECOMMENDED)
String actionType = result.getActionType();

// Method 2: Direct field access
String actionType = result.metadata.actionType;

// Possible values:
// - "contracts_by_contractNumber"
// - "contracts_by_customerNumber"
// - "contracts_by_createdBy"
// - "contracts_by_status"
// - "contracts_by_date"
// - "parts_by_partNumber"
// - "general_query"
```

### 6. Get Display Attributes
```java
// Get the list of display attributes
List<String> displayAttributes = result.displayEntities;

// Get count of attributes
int count = displayAttributes.size();

// Get specific attribute by index
String firstAttribute = displayAttributes.get(0);

// Check if specific attribute exists
boolean hasContractNumber = displayAttributes.contains("CONTRACT_NUMBER");
boolean hasCustomerName = displayAttributes.contains("CUSTOMER_NAME");
boolean hasExpirationDate = displayAttributes.contains("EXPIRATION_DATE");
boolean hasTotalValue = displayAttributes.contains("TOTAL_VALUE");
boolean hasStatus = displayAttributes.contains("STATUS");

// Convert to comma-separated string
String attributesString = String.join(", ", displayAttributes);

// Loop through all attributes
for (String attribute : displayAttributes) {
    System.out.println("Display: " + attribute);
}

// Common display attributes:
// - "CONTRACT_NUMBER"
// - "CUSTOMER_NAME"
// - "PART_NUMBER"
// - "EXPIRATION_DATE"
// - "TOTAL_VALUE"
// - "STATUS"
// - "CREATED_DATE"
// - "PROJECT_TYPE"
// - "METADATA"
```

### 7. Get Operations
```java
// Get the list of operations/filters
List<StandardJSONProcessor.EntityFilter> operations = result.entities;

// Get count of operations
int operationsCount = operations.size();

// Check if any operations exist
if (!operations.isEmpty()) {
    // Get first operation details
    StandardJSONProcessor.EntityFilter firstOp = operations.get(0);
    String attribute = firstOp.attribute;    // What field is being filtered
    String operation = firstOp.operation;    // How it's being filtered (=, between, etc.)
    String value = firstOp.value;           // The filter value
    String source = firstOp.source;         // Where the filter came from
    
    System.out.println("Operation: " + attribute + " " + operation + " " + value);
}

// Loop through all operations
for (StandardJSONProcessor.EntityFilter op : operations) {
    System.out.println("Filter: " + op.attribute + " " + op.operation + " " + op.value);
}

// Find specific operation types
for (StandardJSONProcessor.EntityFilter op : operations) {
    if ("CREATED_DATE".equals(op.attribute)) {
        System.out.println("Date filter: " + op.operation + " " + op.value);
    }
    if ("STATUS".equals(op.attribute)) {
        System.out.println("Status filter: " + op.operation + " " + op.value);
    }
}

// Common operation attributes:
// - "CREATED_DATE" (date filters)
// - "STATUS" (status filters like EXPIRED, ACTIVE)
// Common operation types:
// - "=" (equals)
// - "between" (date ranges)
```

## Complete Example

```java
public class DataAccessExample {
    public static void main(String[] args) {
        StandardJSONProcessor processor = new StandardJSONProcessor();
        String userInput = "show contrct 123456 with expired status";
        
        // Process query
        StandardJSONProcessor.QueryResult result = processor.processQueryToObject(userInput);
        
        // Get all the data you need
        String correctedText = result.getCorrectedInput();
        String originalInput = result.getOriginalInput();
        String queryType = result.getQueryType();
        String actionType = result.getActionType();
        List<String> displayAttributes = result.displayEntities;
        List<StandardJSONProcessor.EntityFilter> operations = result.entities;
        
        // Print results
        System.out.println("Original: " + originalInput);
        System.out.println("Corrected: " + correctedText);
        System.out.println("Query Type: " + queryType);
        System.out.println("Action Type: " + actionType);
        System.out.println("Display Attributes: " + displayAttributes);
        System.out.println("Operations Count: " + operations.size());
        
        // Access operations
        for (StandardJSONProcessor.EntityFilter op : operations) {
            System.out.println("Operation: " + op.attribute + " " + op.operation + " " + op.value);
        }
    }
}
```

## One-Line Access Examples

```java
// Get corrected text
String corrected = processor.processQueryToObject(input).getCorrectedInput();

// Get original input
String original = processor.processQueryToObject(input).getOriginalInput();

// Get query type
String queryType = processor.processQueryToObject(input).getQueryType();

// Get action type
String actionType = processor.processQueryToObject(input).getActionType();

// Get display attributes
List<String> displays = processor.processQueryToObject(input).displayEntities;

// Get operations count
int opsCount = processor.processQueryToObject(input).entities.size();
```

## Error Handling

```java
StandardJSONProcessor.QueryResult result = processor.processQueryToObject(userInput);

// Check for errors
if (result.hasErrors()) {
    System.out.println("Query has errors");
    for (StandardJSONProcessor.ValidationError error : result.errors) {
        System.out.println("Error: " + error.message);
    }
}

// Check for blocking errors
if (result.hasBlockingErrors()) {
    System.out.println("Query has blocking errors - cannot proceed");
}

// Check for spell corrections
if (result.hasSpellCorrections()) {
    System.out.println("Spell corrections were applied");
    System.out.println("Confidence: " + result.getCorrectionConfidence());
}
```

## Summary

**The key method to remember:**
```java
StandardJSONProcessor.QueryResult result = processor.processQueryToObject(userInput);
```

**Then access what you need:**
- **Corrected Text**: `result.getCorrectedInput()`
- **Original Input**: `result.getOriginalInput()`
- **Query Type**: `result.getQueryType()`
- **Action Type**: `result.getActionType()`
- **Display Attributes**: `result.displayEntities`
- **Operations**: `result.entities`
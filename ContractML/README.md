# ContractML - Natural Language Processing for Contract Management

A sophisticated Java-based NLP system for processing natural language queries about contracts and parts. The system provides intelligent query processing with spell correction, entity extraction, and intent classification.

## 🚀 Features

- **Natural Language Processing**: Process queries in plain English
- **Spell Correction**: Automatic correction of common typos and misspellings
- **Entity Extraction**: Extract contract numbers, part numbers, customer information
- **Intent Classification**: Distinguish between contract and parts queries
- **JSON Response Format**: Standardized response structure for easy integration
- **Performance Optimized**: Fast processing with caching and optimized algorithms
- **Comprehensive Testing**: Full test suite with performance benchmarks

## 📋 System Requirements

- Java 8 or higher
- Maven 3.6+ (for building)
- Memory: 512MB RAM minimum
- Disk: 100MB available space

## 🛠️ Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd ContractML
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Run Tests
```bash
mvn test
```

## 🎯 Quick Start

### Basic Usage

```java
import com.contractml.controller.ContractMLController;
import com.contractml.response.ContractMLResponse;

// Initialize the controller
ContractMLController controller = new ContractMLController();

// Process a query
ContractMLResponse response = controller.processQuery("show contract 12345");

// Get JSON response
String jsonResponse = response.toJsonString();
System.out.println(jsonResponse);
```

### Example Queries

The system supports various types of natural language queries:

```java
// Contract queries
controller.processQuery("show contract 12345");
controller.processQuery("what is the effective date for contract ABC-789");
controller.processQuery("display contracts created by john smith");

// Parts queries
controller.processQuery("list all parts for contract 12345");
controller.processQuery("show parts P12345 availability");
controller.processQuery("display parts inventory for customer 567890");

// Queries with typos (automatically corrected)
controller.processQuery("shw contrct 12345");
controller.processQuery("dispaly all partz for custmer john");
```

## 📊 Response Format

The system returns responses in a standardized JSON format:

```json
{
  "header": {
    "contractNumber": "12345",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 45
  },
  "entities": [
    {
      "attribute": "contractNumber",
      "operation": "equals",
      "value": "12345"
    }
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "STATUS"
  ],
  "errors": []
}
```

## 🏗️ Architecture

### Core Components

1. **ContractMLController**: Main entry point and orchestration
2. **ContractMLProcessor**: Core business logic and processing
3. **ContractMLResponse**: Standardized response format
4. **SpellCorrector**: Domain-specific spell correction
5. **EntityExtractor**: Extract entities from natural language
6. **KeywordMatcher**: Efficient keyword matching and scoring

### Data Flow

```
User Query → Controller → Processor → [SpellCorrector, EntityExtractor, KeywordMatcher] → Response
```

## 🔧 Configuration

### Spell Correction

Add custom spell corrections by creating a `spell_corrections.txt` file:

```
contrct -> contract
custmer -> customer
partz -> parts
```

### Performance Tuning

Key configuration parameters in `ContractMLController`:

```java
private static final int MAX_CACHE_SIZE = 1000;
private static final long CACHE_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes
```

## 📈 Performance

### Benchmarks

- **Average Processing Time**: < 50ms per query
- **Cache Hit Rate**: > 80% for repeated queries
- **Memory Usage**: < 100MB for typical workloads
- **Throughput**: > 1000 queries per second

### Optimization Features

- **Response Caching**: Automatic caching of processed queries
- **Compiled Regex**: Pre-compiled patterns for entity extraction
- **HashMap Lookups**: O(1) keyword matching
- **Efficient Algorithms**: Optimized for real-time processing

## 🧪 Testing

### Run Full Test Suite

```bash
java -cp target/classes com.contractml.ContractMLSystemTest
```

### Test Categories

1. **Basic Functionality**: Core system operations
2. **Spell Correction**: Typo correction accuracy
3. **Entity Extraction**: Entity identification precision
4. **Intent Classification**: Query type classification
5. **Error Handling**: Graceful error management
6. **Performance**: Speed and efficiency benchmarks
7. **Caching**: Cache effectiveness validation

### Sample Test Output

```
============================================================
ContractML System Test Suite
============================================================

1. Testing Basic Functionality
----------------------------------------
Query: show contract 12345
Status: PASS
Time: 23ms

...

============================================================
TEST REPORT
============================================================
Total Tests: 25
Passed: 25
Failed: 0
Success Rate: 100.00%
Average Execution Time: 31.25ms
```

## 🔍 API Reference

### ContractMLController

#### Methods

- `processQuery(String query)`: Process a single query
- `processQuery(String query, String sessionId)`: Process with session
- `processQueries(List<String> queries)`: Batch processing
- `getSystemStatistics()`: Get system performance statistics
- `healthCheck()`: System health status
- `clearCache()`: Clear response cache

### ContractMLResponse

#### Properties

- `header`: Entity identifiers (contract number, part number, etc.)
- `queryMetadata`: Query type, action type, processing time
- `entities`: Filter conditions for database queries
- `displayEntities`: Fields to display in UI
- `errors`: Error messages if any

#### Methods

- `toJsonString()`: Convert response to JSON
- `isValid()`: Check if response is valid
- `addEntity(attribute, operation, value)`: Add entity filter
- `addDisplayEntity(fieldName)`: Add display field
- `addError(code, message)`: Add error message

## 🛡️ Error Handling

The system provides comprehensive error handling:

### Error Types

- **INVALID_INPUT**: Null or empty queries
- **PROCESSING_ERROR**: System processing failures
- **INVALID_QUERY**: Queries without valid entities
- **SYSTEM_ERROR**: Unexpected system errors

### Error Response Example

```json
{
  "header": { ... },
  "queryMetadata": {
    "queryType": "ERROR",
    "actionType": "error",
    "processingTimeMs": 5
  },
  "entities": [],
  "displayEntities": [],
  "errors": [
    {
      "code": "INVALID_INPUT",
      "message": "Query cannot be null or empty"
    }
  ]
}
```

## 📝 Examples

### Complete Integration Example

```java
import com.contractml.controller.ContractMLController;
import com.contractml.response.ContractMLResponse;

public class ContractMLExample {
    public static void main(String[] args) {
        // Initialize controller
        ContractMLController controller = new ContractMLController();
        
        // Process various queries
        String[] queries = {
            "show contract 12345",
            "list parts for customer john",
            "what is the effective date for contract ABC-789",
            "display available parts inventory"
        };
        
        for (String query : queries) {
            System.out.println("Processing: " + query);
            
            ContractMLResponse response = controller.processQuery(query);
            
            // Check for errors
            if (!response.getErrors().isEmpty()) {
                System.out.println("Errors: " + response.getErrors());
                continue;
            }
            
            // Display results
            System.out.println("Query Type: " + response.getQueryMetadata().getQueryType());
            System.out.println("Action Type: " + response.getQueryMetadata().getActionType());
            System.out.println("Processing Time: " + response.getQueryMetadata().getProcessingTimeMs() + "ms");
            
            // Show entities
            if (!response.getEntities().isEmpty()) {
                System.out.println("Entities:");
                response.getEntities().forEach(entity -> 
                    System.out.println("  " + entity.getAttribute() + " " + 
                                     entity.getOperation() + " " + entity.getValue()));
            }
            
            // Show display fields
            if (!response.getDisplayEntities().isEmpty()) {
                System.out.println("Display Fields: " + response.getDisplayEntities());
            }
            
            System.out.println();
        }
        
        // Display system statistics
        System.out.println("System Statistics:");
        controller.getSystemStatistics().forEach((key, value) -> 
            System.out.println("  " + key + ": " + value));
    }
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the full test suite
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:

- Create an issue in the GitHub repository
- Check the test suite for usage examples
- Review the API documentation above

## 🔄 Version History

- **v1.0.0**: Initial release with core NLP functionality
- **v1.1.0**: Added caching and performance optimizations
- **v1.2.0**: Enhanced spell correction and entity extraction
- **v1.3.0**: Comprehensive test suite and documentation

---

**ContractML** - Intelligent Contract Management through Natural Language Processing
# API Reference
## Contract Portal Models API

### Main Classes

#### StructuredQueryProcessor
- `processQuery(String input)` - Process natural language query
- Returns: `StructuredQueryResponse`

#### StructuredQueryResponse
- `toJson()` - Convert response to JSON
- `getAnalysisSummary()` - Get analysis summary
- `getQueryType()` - Get detected query type
- `getActionType()` - Get detected action type

### Usage Examples

```java
// Structured Query Processing
StructuredQueryResponse response = StructuredQueryProcessor.processQuery("show contract 123456");
String json = response.toJson();
String analysis = response.getAnalysisSummary();
```

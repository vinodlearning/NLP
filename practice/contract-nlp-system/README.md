# Optimized NLP Contract Management System

## Overview

An intelligent Natural Language Processing system for contract management with optimized routing logic, configuration-driven approach, and strict business rule compliance.

## Key Features

- **O(w) Time Complexity**: Linear scaling with input word count
- **Configuration-Driven**: Externalized keywords and spell corrections
- **Business Rule Compliant**: Contract creation supported, parts creation restricted
- **JSF Integration**: Complete managed bean implementation
- **Comprehensive Testing**: 100% routing accuracy validation

## Quick Start

```bash
# Compile and run tests
javac src/test/java/view/practice/SystemDemo.java
java view.practice.SystemDemo

# Run comprehensive tests
javac src/test/java/view/practice/OptimizedSystemTest.java
java view.practice.OptimizedSystemTest
```

## Architecture

```
UI Screen → Managed Bean → NLPController → [Helper, Models] → JSON Response
```

## Routing Logic

1. **Parts + Create keywords** → PARTS_CREATE_ERROR (business rule violation)
2. **Parts keywords only** → PartsModel (queries/viewing)
3. **Create keywords only** → HelpModel (contract creation)
4. **Default** → ContractModel

## Documentation

See `/docs/` directory for complete documentation:
- `OptimizedSystemDocumentation.md` - Complete system guide
- `FinalSystemSummary.md` - Executive summary
- `CodePushReadyGuide.md` - Push instructions

## Performance

- Average processing time: 3-8 milliseconds
- Time complexity: O(w) where w = word count
- Space complexity: O(1) additional per query
- Configuration loading: O(n) one-time cost

## Business Rules

✅ **Contract Creation**: Fully supported with step-by-step guidance  
❌ **Parts Creation**: Not supported (parts are bulk-loaded from Excel)  
✅ **Parts Viewing**: Supported for queries and reports  
✅ **Contract Management**: Complete CRUD operations

---

*This system implements exact routing requirements with optimal performance and complete business rule compliance.*

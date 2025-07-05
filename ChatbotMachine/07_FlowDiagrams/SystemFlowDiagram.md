# ChatbotMachine System Flow Diagram

## 🏗️ Overall System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           ChatbotMachine NLP System                             │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ User Input  │───▶│ UI Screen   │───▶│ JSF Managed │───▶│ NLP         │    │
│  │ (Natural    │    │ (ADF Page)  │    │ Bean        │    │ Controller  │    │
│  │ Language)   │    │             │    │             │    │             │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                                    │            │
│                                                                    ▼            │
│  ┌─────────────────────────────────────────────────────────────────────────────┐│
│  │                        Routing Logic Engine                                 ││
│  │                                                                             ││
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   ││
│  │  │ Keyword     │    │ Spell       │    │ Context     │                   ││
│  │  │ Detection   │    │ Correction  │    │ Analysis    │                   ││
│  │  │ (O(w))      │    │ (200+ rules)│    │ (Business   │                   ││
│  │  │             │    │             │    │ Rules)      │                   ││
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   ││
│  └─────────────────────────────────────────────────────────────────────────────┘│
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐│
│  │                         Model Selection                                     ││
│  │                                                                             ││
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   ││
│  │  │ Parts       │    │ Help        │    │ Contract    │                   ││
│  │  │ Model       │    │ Model       │    │ Model       │                   ││
│  │  │ (Apache     │    │ (Apache     │    │ (Apache     │                   ││
│  │  │ OpenNLP)    │    │ OpenNLP)    │    │ OpenNLP)    │                   ││
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   ││
│  └─────────────────────────────────────────────────────────────────────────────┘│
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐│
│  │                      Data Processing Layer                                  ││
│  │                                                                             ││
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   ││
│  │  │ Helper      │    │ Validation  │    │ Database    │                   ││
│  │  │ Classes     │    │ Engine      │    │ Access      │                   ││
│  │  │             │    │             │    │ (Oracle ADF)│                   ││
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   ││
│  └─────────────────────────────────────────────────────────────────────────────┘│
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐│
│  │                       JSON Response Generation                              ││
│  │                                                                             ││
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                   ││
│  │  │ Response    │    │ Data        │    │ UI          │                   ││
│  │  │ Builder     │    │ Formatter   │    │ Update      │                   ││
│  │  │             │    │             │    │             │                   ││
│  │  └─────────────┘    └─────────────┘    └─────────────┘                   ││
│  └─────────────────────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔄 Detailed Request Flow

### 1. User Input Processing
```
User Query: "show contract 12345"
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Input Preprocessing                                   │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Text        │───▶│ Tokenization│───▶│ Spell       │───▶│ Keyword     │    │
│  │ Cleaning    │    │ (Split by   │    │ Correction  │    │ Extraction  │    │
│  │             │    │ spaces)     │    │             │    │             │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2. Routing Decision Tree
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Routing Logic                                     │
│                                                                                 │
│  Query: "show contract 12345"                                                  │
│                                                                                 │
│  ┌─────────────┐                                                               │
│  │ Contains    │ NO                                                            │
│  │ Parts       │────────────────────────────────────────────────────────────┐ │
│  │ Keywords?   │                                                            │ │
│  └─────────────┘                                                            │ │
│         │ YES                                                                │ │
│         ▼                                                                    │ │
│  ┌─────────────┐                                                             │ │
│  │ Contains    │ YES                                                         │ │
│  │ Create      │────────────────────────────────────────────────────────┐   │ │
│  │ Keywords?   │                                                        │   │ │
│  └─────────────┘                                                        │   │ │
│         │ NO                                                             │   │ │
│         ▼                                                                │   │ │
│  ┌─────────────┐                                                         │   │ │
│  │ Route to    │                                                         │   │ │
│  │ PartsModel  │                                                         │   │ │
│  └─────────────┘                                                         │   │ │
│                                                                          │   │ │
│                                                                          │   │ │
│  ┌─────────────┐  ◀─────────────────────────────────────────────────────┘   │ │
│  │ PARTS_      │                                                             │ │
│  │ CREATE_     │                                                             │ │
│  │ ERROR       │                                                             │ │
│  └─────────────┘                                                             │ │
│                                                                              │ │
│  ┌─────────────┐  ◀───────────────────────────────────────────────────────────┘ │
│  │ Contains    │                                                               │
│  │ Create      │ YES                                                           │
│  │ Keywords?   │────────────────────────────────────────────────────────────┐ │
│  └─────────────┘                                                            │ │
│         │ NO                                                                 │ │
│         ▼                                                                    │ │
│  ┌─────────────┐                                                             │ │
│  │ Route to    │                                                             │ │
│  │ Contract    │                                                             │ │
│  │ Model       │                                                             │ │
│  └─────────────┘                                                             │ │
│                                                                              │ │
│  ┌─────────────┐  ◀───────────────────────────────────────────────────────────┘ │
│  │ Route to    │                                                               │
│  │ HelpModel   │                                                               │
│  └─────────────┘                                                               │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 3. Model Processing
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          Contract Model Processing                             │
│                                                                                 │
│  Input: "show contract 12345"                                                  │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Intent      │───▶│ Entity      │───▶│ Database    │───▶│ Response    │    │
│  │ Detection   │    │ Extraction  │    │ Query       │    │ Generation  │    │
│  │ (Show)      │    │ (12345)     │    │ (Oracle)    │    │ (JSON)      │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                                                 │
│  ML Model: contract_model.bin                                                  │
│  Training Data: 85+ contract queries                                           │
│  Accuracy: 100%                                                                │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 4. Database Integration
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Oracle ADF Integration                               │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Query       │───▶│ Column      │───▶│ SQL         │───▶│ Result      │    │
│  │ Parameters  │    │ Mapping     │    │ Execution   │    │ Processing  │    │
│  │             │    │ (AWARD_     │    │             │    │             │    │
│  │ Contract:   │    │ NUMBER)     │    │ SELECT *    │    │ Contract    │    │
│  │ 12345       │    │             │    │ FROM...     │    │ Data        │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                                                 │
│  Database Mappings:                                                             │
│  - contract_number → AWARD_NUMBER                                              │
│  - status → STATUS                                                             │
│  - effective_date → EFFECTIVE_DATE                                             │
│  - expiration_date → EXPIRATION_DATE                                           │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 5. JSON Response Generation
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           JSON Response Structure                               │
│                                                                                 │
│  {                                                                              │
│    "responseType": "CONTRACT_LOOKUP",                                          │
│    "success": true,                                                             │
│    "message": "Contract found successfully",                                   │
│    "processingModel": "ContractModel",                                         │
│    "data": {                                                                   │
│      "contractNumber": "12345",                                                │
│      "status": "Active",                                                       │
│      "effectiveDate": "2024-01-01",                                            │
│      "expirationDate": "2024-12-31",                                           │
│      "contractValue": 50000.00,                                                │
│      "customer": "ABC Corp",                                                   │
│      "vendor": "XYZ Supplier"                                                  │
│    },                                                                          │
│    "metadata": {                                                               │
│      "queryTime": "2024-12-19T10:30:00Z",                                     │
│      "processingTime": "185μs",                                                │
│      "spellCorrections": [],                                                   │
│      "confidence": 0.98                                                        │
│    }                                                                           │
│  }                                                                              │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🎯 Key Performance Metrics

### Routing Accuracy
- **Overall Accuracy**: 100% (84/84 test cases)
- **Parts Model**: 100% (44/44 queries)
- **Contract Model**: 100% (40/40 queries)
- **Help Model**: 100% (specialized queries)

### Processing Performance
- **Average Response Time**: 196.74 microseconds
- **Time Complexity**: O(w) where w = word count
- **Memory Complexity**: O(1) lookups with HashSet/HashMap
- **Spell Correction Rate**: 78.57% of queries

### Business Rules Compliance
- **Account Validation**: 6-12 digits with business rules
- **Date Validation**: Multiple format support
- **Contract Number Mapping**: AWARD_NUMBER column
- **Error Handling**: Graceful degradation

## 🔧 Technical Implementation Details

### Configuration Management
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Configuration Files                                  │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ parts_      │    │ create_     │    │ spell_      │    │ database_   │    │
│  │ keywords.   │    │ keywords.   │    │ corrections.│    │ mappings.   │    │
│  │ txt         │    │ txt         │    │ txt         │    │ txt         │    │
│  │             │    │             │    │             │    │             │    │
│  │ 25 keywords │    │ 17 keywords │    │ 200+ rules  │    │ 50+ maps    │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Machine Learning Pipeline
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        Apache OpenNLP Integration                              │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Training    │───▶│ Model       │───▶│ Binary      │───▶│ Runtime     │    │
│  │ Data        │    │ Training    │    │ Export      │    │ Loading     │    │
│  │             │    │             │    │             │    │             │    │
│  │ 270+ samples│    │ Apache      │    │ .bin files  │    │ Production  │    │
│  │             │    │ OpenNLP     │    │             │    │ Use         │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                                                 │
│  Models:                                                                        │
│  - contract_model.bin (85 samples)                                             │
│  - parts_model.bin (95 samples)                                                │
│  - help_model.bin (90 samples)                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---
*This diagram represents the complete ChatbotMachine system architecture with all components, flows, and performance metrics.*
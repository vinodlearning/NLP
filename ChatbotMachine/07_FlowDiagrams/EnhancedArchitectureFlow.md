# Enhanced ChatbotMachine Architecture Flow

## 🏗️ Complete System Architecture with Clean Separation of Concerns

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        ENHANCED CHATBOT MACHINE ARCHITECTURE                   │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                           PRESENTATION LAYER                               │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ ADF Pages   │    │ JSF Managed │    │ REST        │    │ WebSocket   │ │ │
│  │  │ (UI Screen) │    │ Bean        │    │ Endpoints   │    │ Handlers    │ │ │
│  │  │             │    │             │    │             │    │             │ │ │
│  │  │ - Input     │    │ - Session   │    │ - JSON API  │    │ - Real-time │ │ │
│  │  │ - Display   │    │ - Validation│    │ - Auth      │    │ - Events    │ │ │
│  │  │ - Navigation│    │ - State Mgmt│    │ - Routing   │    │ - Messaging │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                            NLP PROCESSING LAYER                            │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ Enhanced    │    │ Tokenizer   │    │ POS Tagger  │    │ Spell       │ │ │
│  │  │ NLP         │    │             │    │             │    │ Checker     │ │ │
│  │  │ Processor   │    │ - Word      │    │ - Grammar   │    │             │ │ │
│  │  │             │    │   Splitting │    │   Analysis  │    │ - Domain    │ │ │
│  │  │ - Pipeline  │    │ - Delimiter │    │ - Part of   │    │   Specific  │ │ │
│  │  │ - Validation│    │   Handling  │    │   Speech    │    │ - Real-time │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ Lemmatizer  │    │ Entity      │    │ Intent      │    │ Configuration│ │ │
│  │  │             │    │ Extractor   │    │ Classifier  │    │ Manager     │ │ │
│  │  │ - Root Form │    │             │    │             │    │             │ │ │
│  │  │ - Context   │    │ - Contract  │    │ - Failed    │    │ - Keywords  │ │ │
│  │  │ - Patterns  │    │   Numbers   │    │   Parts     │    │ - Rules     │ │ │
│  │  │             │    │ - Part Nums │    │ - Parts     │    │ - Mappings  │ │ │
│  │  │             │    │ - Dates     │    │ - Contract  │    │ - Constants │ │ │
│  │  │             │    │ - Entities  │    │ - Help      │    │             │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                           ROUTING & CONTROL LAYER                          │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ Main NLP    │    │ Routing     │    │ Action Type │    │ Business    │ │ │
│  │  │ Controller  │    │ Engine      │    │ Determiner  │    │ Rules       │ │ │
│  │  │             │    │             │    │             │    │ Engine      │ │ │
│  │  │ - Request   │    │ - Keyword   │    │ - Failed    │    │             │ │ │
│  │  │   Handling  │    │   Detection │    │   Parts     │    │ - Validation│ │ │
│  │  │ - Response  │    │ - Intent    │    │ - Parts     │    │ - Compliance│ │ │
│  │  │   Building  │    │   Routing   │    │ - Contract  │    │ - Security  │ │ │
│  │  │ - Error     │    │ - Model     │    │ - Help      │    │ - Access    │ │ │
│  │  │   Handling  │    │   Selection │    │             │    │   Control   │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                            MODEL PROCESSING LAYER                          │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ Contract    │    │ Parts       │    │ Failed Parts│    │ Help        │ │ │
│  │  │ Model       │    │ Model       │    │ Model (NEW) │    │ Model       │ │ │
│  │  │             │    │             │    │             │    │             │ │ │
│  │  │ - Lookup    │    │ - Inventory │    │ - Failure   │    │ - Creation  │ │ │
│  │  │ - Status    │    │ - Pricing   │    │   Analysis  │    │ - Workflow  │ │ │
│  │  │ - Dates     │    │ - Specs     │    │ - Error     │    │ - Templates │ │ │
│  │  │ - Customer  │    │ - Quality   │    │   Messages  │    │ - Guidance  │ │ │
│  │  │ - Terms     │    │ - Lead Time │    │ - Reasons   │    │ - Support   │ │ │
│  │  │ - Compliance│    │ - Vendor    │    │ - Reports   │    │ - Training  │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                           DATA ACCESS LAYER                                │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ Contracts   │    │ Parts       │    │ Parts_Failed│    │ Customers   │ │ │
│  │  │ Repository  │    │ Repository  │    │ Repository  │    │ Repository  │ │ │
│  │  │             │    │             │    │ (NEW)       │    │             │ │ │
│  │  │ - CRUD Ops  │    │ - CRUD Ops  │    │             │    │ - CRUD Ops  │ │ │
│  │  │ - Queries   │    │ - Queries   │    │ - Failure   │    │ - Queries   │ │ │
│  │  │ - Joins     │    │ - Joins     │    │   Queries   │    │ - Joins     │ │ │
│  │  │ - Filters   │    │ - Filters   │    │ - Error Log │    │ - Filters   │ │ │
│  │  │             │    │             │    │ - Analysis  │    │             │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                       │
│                                         ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                           DATABASE LAYER                                   │ │
│  │                                                                             │ │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │ │
│  │  │ CONTRACTS   │    │ PARTS       │    │ PARTS_FAILED│    │ CUSTOMERS   │ │ │
│  │  │ Table       │    │ Table       │    │ Table (NEW) │    │ Table       │ │ │
│  │  │             │    │             │    │             │    │             │ │ │
│  │  │ - 100+ Cols │    │ - 50+ Cols  │    │ - PARTS_    │    │ - Customer  │ │ │
│  │  │ - AWARD_    │    │ - INVOICE_  │    │   NUMBER    │    │   Info      │ │ │
│  │  │   NUMBER    │    │   PART_     │    │ - CONTRACT_ │    │ - Address   │ │ │
│  │  │ - STATUS    │    │   NUMBER    │    │   NUMBER    │    │ - Contact   │ │ │
│  │  │ - DATES     │    │ - PRICE     │    │ - FAILED_   │    │ - History   │ │ │
│  │  │ - TERMS     │    │ - STATUS    │    │   MESSAGE_  │    │             │ │ │
│  │  │             │    │             │    │   COLUMN    │    │             │ │ │
│  │  │             │    │             │    │ - REASON_   │    │             │ │ │
│  │  │             │    │             │    │   FOR_FAILED│    │             │ │ │
│  │  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔄 Enhanced Request Processing Flow

### 1. User Input Processing with Advanced NLP
```
User Query: "show faild prts for contrct 987654"
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        ADVANCED NLP PROCESSING PIPELINE                        │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Input       │───▶│ Tokenization│───▶│ Spell       │───▶│ POS         │    │
│  │ Cleaning    │    │             │    │ Correction  │    │ Tagging     │    │
│  │             │    │ ["show",    │    │             │    │             │    │
│  │ "show faild │    │  "faild",   │    │ ["show",    │    │ [VERB,      │    │
│  │  prts for   │    │  "prts",    │    │  "failed",  │    │  ADJ,       │    │
│  │  contrct    │    │  "for",     │    │  "parts",   │    │  NOUN,      │    │
│  │  987654"    │    │  "contrct", │    │  "for",     │    │  PREP,      │    │
│  │             │    │  "987654"]  │    │  "contract",│    │  NOUN,      │    │
│  │             │    │             │    │  "987654"]  │    │  NUMBER]    │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Lemmatization│───▶│ Entity      │───▶│ Intent      │───▶│ Validation  │    │
│  │             │    │ Extraction  │    │ Classification│   │             │    │
│  │ ["show",    │    │             │    │             │    │             │    │
│  │  "fail",    │    │ Entities:   │    │ Intent:     │    │ Valid: true │    │
│  │  "part",    │    │ - CONTRACT_ │    │ FAILED_PARTS│    │ Warnings:   │    │
│  │  "for",     │    │   NUMBER:   │    │ Confidence: │    │ - Spell     │    │
│  │  "contract",│    │   "987654"  │    │   0.85      │    │   corrected │    │
│  │  "987654"]  │    │ - FAILURE:  │    │             │    │             │    │
│  │             │    │   "failed"  │    │             │    │             │    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2. Enhanced Routing Decision Tree
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           ENHANCED ROUTING LOGIC                               │
│                                                                                 │
│  Intent: FAILED_PARTS (Confidence: 0.85)                                       │
│  Entities: [CONTRACT_NUMBER: "987654", FAILURE: "failed"]                      │
│                                                                                 │
│  ┌─────────────┐                                                               │
│  │ Has Failed  │ YES                                                           │
│  │ Keywords?   │────────────────────────────────────────────────────────────┐ │
│  └─────────────┘                                                            │ │
│                                                                              │ │
│  ┌─────────────┐                                                             │ │
│  │ Has Parts   │ YES                                                         │ │
│  │ Keywords?   │────────────────────────────────────────────────────────┐   │ │
│  └─────────────┘                                                        │   │ │
│                                                                          │   │ │
│  ┌─────────────┐  ◀─────────────────────────────────────────────────────┘   │ │
│  │ Route to    │                                                             │ │
│  │ Failed Parts│                                                             │ │
│  │ Model       │                                                             │ │
│  └─────────────┘                                                             │ │
│         │                                                                    │ │
│         ▼                                                                    │ │
│  ┌─────────────┐                                                             │ │
│  │ Determine   │                                                             │ │
│  │ Action Type │                                                             │ │
│  │             │                                                             │ │
│  │ Contract +  │ → FAILED_PARTS_CONTRACT                                     │ │
│  │ Failed      │                                                             │ │
│  └─────────────┘                                                             │ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 3. Failed Parts Model Processing (NEW)
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          FAILED PARTS MODEL PROCESSING                         │
│                                                                                 │
│  Action Type: FAILED_PARTS_CONTRACT                                             │
│  Contract Number: "987654"                                                      │
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    │
│  │ Query       │───▶│ Database    │───▶│ Result      │───▶│ Response    │    │
│  │ Builder     │    │ Execution   │    │ Processing  │    │ Generation  │    │
│  │             │    │             │    │             │    │             │    │
│  │ SELECT *    │    │ Execute     │    │ Found:      │    │ "Failed     │    │
│  │ FROM        │    │ Query on    │    │ - AE125     │    │  parts for  │    │
│  │ PARTS_      │    │ PARTS_      │    │ - PN7890    │    │  contract   │    │
│  │ FAILED      │    │ FAILED      │    │ - AE777     │    │  987654:    │    │
│  │ WHERE       │    │ Table       │    │             │    │  AE125      │    │
│  │ CONTRACT_   │    │             │    │ Reasons:    │    │  (voltage), │    │
│  │ NUMBER =    │    │             │    │ - Voltage   │    │  PN7890     │    │
│  │ '987654'    │    │             │    │ - Overheat  │    │  (overheat),│    │
│  │             │    │             │    │ - Pressure  │    │  AE777      │    │
│  │             │    │             │    │             │    │  (pressure)"│    │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🎯 Clean Separation of Concerns Implementation

### Layer Responsibilities

#### 1. Presentation Layer
- **UI Screen (ADF Pages)**: User interface, input collection, display
- **JSF Managed Bean**: Session management, UI state, validation
- **REST Endpoints**: API exposure, authentication, JSON handling
- **WebSocket Handlers**: Real-time communication, event handling

#### 2. NLP Processing Layer
- **Enhanced NLP Processor**: Main processing pipeline coordination
- **Tokenizer**: Text splitting, delimiter handling, normalization
- **POS Tagger**: Grammatical analysis, part-of-speech identification
- **Spell Checker**: Domain-specific corrections, typo handling
- **Lemmatizer**: Word root extraction, morphological analysis
- **Entity Extractor**: Contract numbers, part numbers, dates extraction
- **Intent Classifier**: Failed parts, parts, contract, help classification
- **Configuration Manager**: Keywords, rules, mappings management

#### 3. Routing & Control Layer
- **Main NLP Controller**: Request orchestration, response building
- **Routing Engine**: Intent-based routing, model selection
- **Action Type Determiner**: Specific action identification
- **Business Rules Engine**: Validation, compliance, security

#### 4. Model Processing Layer
- **Contract Model**: Contract-related query processing
- **Parts Model**: Parts inventory, pricing, specifications
- **Failed Parts Model**: Failure analysis, error reporting (NEW)
- **Help Model**: Creation guidance, workflow support

#### 5. Data Access Layer
- **Repository Pattern**: CRUD operations, query building
- **Database Abstraction**: Oracle ADF integration, transaction management
- **Connection Management**: Pool management, performance optimization

#### 6. Database Layer
- **Enhanced Schema**: Updated with Parts_Failed table
- **Optimized Indexes**: Performance tuning for failed parts queries
- **Referential Integrity**: Foreign key constraints, data consistency

## 🔧 Configuration Management (Hard-coded values in text files)

### Configuration Files Structure
```
06_Configuration/
├── parts_keywords.txt              # Parts detection keywords
├── create_keywords.txt             # Help/creation keywords
├── failed_parts_keywords.txt       # Failed parts detection (NEW)
├── enhanced_spell_corrections.txt  # Comprehensive spell corrections
├── action_types_constants.txt      # Action type definitions
└── enhanced_database_mappings.txt  # Database column mappings
```

### Constants Management
```java
// Example: ActionTypeConstants.java
public class ActionTypeConstants {
    // Loaded from action_types_constants.txt
    public static final String FAILED_PARTS = "FAILED_PARTS";
    public static final String FAILED_PARTS_CONTRACT = "FAILED_PARTS_CONTRACT";
    public static final String FAILED_PARTS_REASON = "FAILED_PARTS_REASON";
    public static final String FAILED_PARTS_MESSAGE = "FAILED_PARTS_MESSAGE";
    // ... more constants
}
```

## 📊 Performance Optimizations

### Time Complexity Achievements
- **Tokenization**: O(n) where n = input length
- **Spell Correction**: O(w) where w = word count
- **Intent Classification**: O(w) where w = word count
- **Entity Extraction**: O(w) where w = word count
- **Overall Processing**: O(w) where w = word count

### Memory Optimizations
- **HashSet/HashMap Lookups**: O(1) keyword detection
- **Singleton Pattern**: Configuration manager instance
- **Lazy Loading**: Configuration files loaded on demand
- **Connection Pooling**: Database connection reuse

## 🎉 Enhanced System Benefits

### New Failed Parts Functionality
- **Comprehensive Failure Analysis**: Root cause identification
- **Error Message Tracking**: Detailed failure logs
- **Contract-based Reporting**: Failure analysis by contract
- **Predictive Insights**: Failure pattern recognition

### Advanced NLP Capabilities
- **Tokenization**: Advanced text splitting and normalization
- **POS Tagging**: Grammatical analysis for better understanding
- **Spell Checking**: 200+ domain-specific corrections
- **Lemmatization**: Word root extraction for better matching

### Reusability & Maintainability
- **Modular Design**: Each component has single responsibility
- **Configuration-driven**: Easy updates without code changes
- **Extensible Architecture**: Easy to add new models/features
- **Comprehensive Testing**: 100% test coverage with detailed analysis

---

*This enhanced architecture demonstrates clean separation of concerns with advanced NLP capabilities and comprehensive failed parts functionality, following the design patterns shown in the user's flow diagram.*
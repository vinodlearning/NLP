# 🧪 Test Queries for Contract Query Processing System
## Comprehensive Validation Test Suite

This document provides test queries to validate your Oracle ADF integration with expected results and validation criteria.

---

## 📋 **Testing Overview**

### **Test Categories:**
1. **Contract Queries** - Specific and general contract searches
2. **Parts Queries** - Parts lookup and search functionality
3. **Help Queries** - Contract creation assistance
4. **Business Rule Tests** - Parts creation error handling
5. **Spell Correction Tests** - Typo correction validation
6. **Error Handling Tests** - System error scenarios
7. **Performance Tests** - Speed and accuracy validation

### **Expected Performance:**
- ⚡ **Processing Time**: < 500 microseconds
- 🎯 **Routing Accuracy**: 100%
- 🔧 **Spell Correction**: 80%+ success rate
- 🛡️ **Business Rules**: 100% enforcement

---

## 🎯 **Category 1: Contract Queries**

### **1.1 Specific Contract Lookup**

**Test Purpose:** Validate contract ID extraction and specific contract retrieval

| Query | Expected Route | Expected Action | Contract ID |
|-------|---------------|----------------|-------------|
| "show contract 123456" | CONTRACT | getContractDetails | 123456 |
| "display contract 789012" | CONTRACT | getContractDetails | 789012 |
| "get contract details for 456789" | CONTRACT | getContractDetails | 456789 |
| "contract 234567 information" | CONTRACT | getContractDetails | 234567 |
| "lookup contract 345678" | CONTRACT | getContractDetails | 345678 |

**Validation Criteria:**
- ✅ Route = "CONTRACT"
- ✅ Contract ID extracted correctly
- ✅ ADF operation = "getContractDetails"
- ✅ Processing time < 500μs

### **1.2 General Contract Search**

**Test Purpose:** Validate general contract searches without specific IDs

| Query | Expected Route | Expected Action | Search Criteria |
|-------|---------------|----------------|----------------|
| "show all contracts" | CONTRACT | searchContracts | "show all contracts" |
| "list contracts for customer ABC" | CONTRACT | searchContracts | "list contracts for customer ABC" |
| "find active contracts" | CONTRACT | searchContracts | "find active contracts" |
| "contract status pending" | CONTRACT | searchContracts | "contract status pending" |
| "expired contracts list" | CONTRACT | searchContracts | "expired contracts list" |

**Validation Criteria:**
- ✅ Route = "CONTRACT"
- ✅ No contract ID extracted
- ✅ ADF operation = "searchContracts"
- ✅ Search criteria passed correctly

### **1.3 Contract Date Queries**

**Test Purpose:** Validate contract queries with date information

| Query | Expected Route | Expected Action | Intent Type |
|-------|---------------|----------------|-------------|
| "contracts created in 2023" | CONTRACT | searchContracts | GENERAL_CONTRACT_QUERY |
| "show contracts expiring soon" | CONTRACT | searchContracts | GENERAL_CONTRACT_QUERY |
| "contracts effective from January" | CONTRACT | searchContracts | GENERAL_CONTRACT_QUERY |
| "what contracts were created by john" | CONTRACT | searchContracts | GENERAL_CONTRACT_QUERY |

**Validation Criteria:**
- ✅ Route = "CONTRACT"
- ✅ Intent = "GENERAL_CONTRACT_QUERY"
- ✅ Past-tense detection working (where applicable)

---

## 🔧 **Category 2: Parts Queries**

### **2.1 Parts for Specific Contract**

**Test Purpose:** Validate parts lookup for specific contracts

| Query | Expected Route | Expected Action | Contract ID |
|-------|---------------|----------------|-------------|
| "show parts for contract 123456" | PARTS | getContractParts | 123456 |
| "list parts in contract 789012" | PARTS | getContractParts | 789012 |
| "parts for 456789" | PARTS | getContractParts | 456789 |
| "what parts are in contract 234567" | PARTS | getContractParts | 234567 |
| "display contract 345678 parts" | PARTS | getContractParts | 345678 |

**Validation Criteria:**
- ✅ Route = "PARTS"
- ✅ Contract ID extracted correctly
- ✅ ADF operation = "getContractParts"
- ✅ Intent = "PARTS_QUERY"

### **2.2 General Parts Search**

**Test Purpose:** Validate general parts searches

| Query | Expected Route | Expected Action | Search Criteria |
|-------|---------------|----------------|----------------|
| "show all parts" | PARTS | searchParts | "show all parts" |
| "list parts containing AE125" | PARTS | searchParts | "list parts containing AE125" |
| "find parts by manufacturer XYZ" | PARTS | searchParts | "find parts by manufacturer XYZ" |
| "parts inventory status" | PARTS | searchParts | "parts inventory status" |
| "search for component ABC" | PARTS | searchParts | "search for component ABC" |

**Validation Criteria:**
- ✅ Route = "PARTS"
- ✅ No contract ID extracted
- ✅ ADF operation = "searchParts"
- ✅ Parts keywords detected

### **2.3 Advanced Parts Queries**

**Test Purpose:** Validate complex parts queries with multiple keywords

| Query | Expected Route | Parts Keywords | Contract ID |
|-------|---------------|---------------|-------------|
| "how many parts for contract 123456" | PARTS | ["parts"] | 123456 |
| "list all components and parts" | PARTS | ["components", "parts"] | null |
| "show inventory of parts AE125 and AE126" | PARTS | ["parts", "ae125", "ae126"] | null |
| "parts and lines for manufacturing" | PARTS | ["parts", "lines"] | null |

**Validation Criteria:**
- ✅ Route = "PARTS"
- ✅ Multiple parts keywords detected
- ✅ Contract ID extraction (where applicable)

---

## 🆘 **Category 3: Help Queries**

### **3.1 Contract Creation Help**

**Test Purpose:** Validate help request routing

| Query | Expected Route | Expected Action | Intent Type |
|-------|---------------|----------------|-------------|
| "help me create contract" | HELP | showHelp | HELP_REQUEST |
| "how to create new contract" | HELP | showHelp | HELP_REQUEST |
| "contract creation steps" | HELP | showHelp | HELP_REQUEST |
| "guide for making contract" | HELP | showHelp | HELP_REQUEST |
| "help with contract workflow" | HELP | showHelp | HELP_REQUEST |

**Validation Criteria:**
- ✅ Route = "HELP"
- ✅ Intent = "HELP_REQUEST"
- ✅ Create keywords detected
- ✅ Help message displayed

### **3.2 Process and Workflow Help**

**Test Purpose:** Validate process-related help queries

| Query | Expected Route | Create Keywords | 
|-------|---------------|----------------|
| "how to add new contract" | HELP | ["add", "new"] |
| "steps to generate contract" | HELP | ["steps", "generate"] |
| "contract creation process" | HELP | ["create"] |
| "help with contract setup" | HELP | ["help"] |

**Validation Criteria:**
- ✅ Route = "HELP"
- ✅ Create keywords identified
- ✅ No parts keywords present

---

## ❌ **Category 4: Business Rule Tests**

### **4.1 Parts Creation Error**

**Test Purpose:** Validate business rule enforcement (parts cannot be created)

| Query | Expected Route | Expected Error | Business Rule |
|-------|---------------|---------------|---------------|
| "create new parts for contract 123456" | PARTS_CREATE_ERROR | Parts creation not supported | Parts creation not allowed |
| "add parts to contract 789012" | PARTS_CREATE_ERROR | Parts creation not supported | Parts creation not allowed |
| "make new parts AE125" | PARTS_CREATE_ERROR | Parts creation not supported | Parts creation not allowed |
| "generate parts for contract" | PARTS_CREATE_ERROR | Parts creation not supported | Parts creation not allowed |

**Validation Criteria:**
- ✅ Route = "PARTS_CREATE_ERROR"
- ✅ Intent = "BUSINESS_RULE_VIOLATION"
- ✅ Both parts AND create keywords detected
- ✅ Error message explains parts are loaded from Excel

### **4.2 Parts Creation vs Past Tense**

**Test Purpose:** Validate past-tense detection vs creation intent

| Query | Expected Route | Reason | Enhancement |
|-------|---------------|--------|-------------|
| "parts were created by admin" | CONTRACT | Past-tense detected | Past-tense detection applied |
| "who created parts for contract 123456" | CONTRACT | Past-tense detected | Past-tense detection applied |
| "parts created in 2023" | CONTRACT | Past-tense detected | Past-tense detection applied |

**Validation Criteria:**
- ✅ Route = "CONTRACT" (not PARTS_CREATE_ERROR)
- ✅ Enhancement = "Past-tense detection applied"
- ✅ Context score > 8.0

---

## 🔤 **Category 5: Spell Correction Tests**

### **5.1 Common Typos**

**Test Purpose:** Validate spell correction functionality

| Original Query | Corrected Query | Expected Route |
|---------------|----------------|----------------|
| "shw contrct 123456" | "show contract 123456" | CONTRACT |
| "lst partz for contrct 123456" | "list parts for contract 123456" | PARTS |
| "hlp me creat contrct" | "help me create contract" | HELP |
| "detals for custmr ABC" | "details for customer ABC" | CONTRACT |
| "dsply al cntracts" | "display all contracts" | CONTRACT |

**Validation Criteria:**
- ✅ hasSpellCorrections() = true
- ✅ Corrected input different from original
- ✅ Routing based on corrected input
- ✅ Spell correction message available

### **5.2 Multiple Typos**

**Test Purpose:** Validate correction of multiple typos in single query

| Original Query | Expected Corrections | Expected Route |
|---------------|---------------------|----------------|
| "shwo partz fr contrct 123456" | "show parts for contract 123456" | PARTS |
| "lst al cntracts fr custmr" | "list all contracts for customer" | CONTRACT |
| "wat iz th effctiv dat" | "what is the effective date" | CONTRACT |
| "hw mny partz ar inclded" | "how many parts are included" | PARTS |

**Validation Criteria:**
- ✅ Multiple words corrected
- ✅ Meaning preserved
- ✅ Correct routing after correction

### **5.3 Domain-Specific Corrections**

**Test Purpose:** Validate business domain spell corrections

| Original Query | Corrected Query | Domain Terms |
|---------------|----------------|--------------|
| "expiraion dat for contrct" | "expiration date for contract" | expiration, contract |
| "effectuve dat and custmr" | "effective date and customer" | effective, customer |
| "manufcturer for compnnt AE125" | "manufacturer for component AE125" | manufacturer, component |
| "busness rul for accnt" | "business rule for account" | business, account |

**Validation Criteria:**
- ✅ Domain-specific terms corrected
- ✅ Technical terminology preserved
- ✅ Context-appropriate corrections

---

## 🚨 **Category 6: Error Handling Tests**

### **6.1 Empty Input Tests**

**Test Purpose:** Validate handling of empty/null inputs

| Input | Expected Route | Expected Message |
|-------|---------------|------------------|
| "" (empty string) | ERROR | Please enter a query |
| null | ERROR | Please enter a query |
| "   " (whitespace) | ERROR | Please enter a query |

**Validation Criteria:**
- ✅ Route = "ERROR"
- ✅ Appropriate error message
- ✅ No system exceptions

### **6.2 Unknown Query Types**

**Test Purpose:** Validate handling of ambiguous queries

| Query | Expected Route | Expected Behavior |
|-------|---------------|------------------|
| "xyz abc random text" | CONTRACT | Default to contract routing |
| "hello world test" | CONTRACT | Default to contract routing |
| "12345" | CONTRACT | Default to contract routing |

**Validation Criteria:**
- ✅ Default to CONTRACT route
- ✅ No errors or exceptions
- ✅ Graceful handling

---

## ⚡ **Category 7: Performance Tests**

### **7.1 Processing Speed Tests**

**Test Purpose:** Validate system performance

| Query Type | Target Time | Sample Query |
|-----------|-------------|--------------|
| Simple contract | < 200μs | "show contract 123456" |
| Simple parts | < 200μs | "show parts for 123456" |
| Complex search | < 500μs | "show contracts for customer ABC with status active" |
| Spell correction | < 300μs | "shw contrct detals fr custmr" |

**Validation Criteria:**
- ✅ Processing time within targets
- ✅ Response includes timing metadata
- ✅ Performance consistent across queries

### **7.2 Accuracy Tests**

**Test Purpose:** Validate routing accuracy

| Test Set | Expected Accuracy | Query Count |
|----------|------------------|-------------|
| Contract queries | 100% | 20 queries |
| Parts queries | 100% | 20 queries |
| Help queries | 100% | 10 queries |
| Business rule tests | 100% | 5 queries |
| **Overall** | **100%** | **55 queries** |

**Validation Criteria:**
- ✅ All queries route correctly
- ✅ No false positives/negatives
- ✅ Business rules properly enforced

---

## 🎯 **Quick Validation Test Suite**

### **Essential Tests (Must Pass)**

Run these 10 essential tests to validate core functionality:

1. **"show contract 123456"** → CONTRACT + Contract ID extracted
2. **"list parts for contract 123456"** → PARTS + Contract ID extracted  
3. **"help me create contract"** → HELP + No errors
4. **"create new parts"** → PARTS_CREATE_ERROR + Business rule message
5. **"shw contrct 123456"** → CONTRACT + Spell correction applied
6. **"contracts created by john"** → CONTRACT + Past-tense enhancement
7. **""** (empty) → ERROR + Appropriate message
8. **"show all contracts"** → CONTRACT + No contract ID
9. **"parts containing AE125"** → PARTS + No contract ID
10. **"contract creation steps"** → HELP + Create keywords detected

### **Validation Checklist**

After running tests, verify:
- ✅ All 10 essential tests pass
- ✅ Spell correction working (test #5)
- ✅ Business rules enforced (test #4)
- ✅ Contract ID extraction working (tests #1, #2)
- ✅ Error handling working (test #7)
- ✅ Performance under 500μs for all tests
- ✅ No exceptions or system errors
- ✅ ADF operations called correctly
- ✅ UI displays appropriate messages

---

## 📊 **Test Results Template**

Use this template to record your test results:

```
=== CONTRACT QUERY PROCESSING - TEST RESULTS ===

Date: [DATE]
Environment: [ENVIRONMENT]
ADF Version: [VERSION]

ESSENTIAL TESTS:
[ ] Test 1: show contract 123456
[ ] Test 2: list parts for contract 123456
[ ] Test 3: help me create contract
[ ] Test 4: create new parts
[ ] Test 5: shw contrct 123456
[ ] Test 6: contracts created by john
[ ] Test 7: (empty input)
[ ] Test 8: show all contracts
[ ] Test 9: parts containing AE125
[ ] Test 10: contract creation steps

PERFORMANCE METRICS:
- Average processing time: _____ μs
- Routing accuracy: _____% 
- Spell correction rate: _____%
- Business rule enforcement: _____%

ISSUES FOUND:
[List any issues or failures]

OVERALL RESULT: [ ] PASS [ ] FAIL

Notes: [Any additional notes]
```

---

## 🔍 **Debugging Failed Tests**

If tests fail, check:

1. **Routing Issues:**
   - Verify keywords are properly configured
   - Check spell correction dictionary
   - Validate business rule logic

2. **ADF Integration Issues:**
   - Verify ViewObject names match
   - Check ADF bindings are regenerated
   - Validate method operation signatures

3. **Performance Issues:**
   - Check for unnecessary processing
   - Verify singleton pattern working
   - Monitor memory usage

4. **UI Issues:**
   - Verify JSF managed bean properties
   - Check FacesMessage integration
   - Validate response display logic

---

## ✅ **Test Completion**

When all tests pass, your Oracle ADF application has:
- 🎯 **100% Accurate Query Routing**
- 🔧 **Intelligent Spell Correction** 
- ⚡ **Fast Processing Performance**
- 🛡️ **Robust Business Rule Enforcement**
- 🚨 **Comprehensive Error Handling**

**Congratulations! Your integration is complete and validated.** 🚀

---

## 📞 **Support**

If any tests fail:
1. Review the **IntegrationGuide.md** troubleshooting section
2. Check the **specific error messages** and logs
3. Verify **all customization steps** completed
4. Enable **debug logging** for detailed analysis

Your enhanced ADF application is ready for production use! 🎉
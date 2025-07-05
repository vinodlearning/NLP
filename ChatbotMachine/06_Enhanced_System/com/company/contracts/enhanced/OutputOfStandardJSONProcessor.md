Test 1 :show contract 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show contract 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 53.130
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 2 :contract details 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract details 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.142
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 3 :get contract info 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get contract info 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.112
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 4 :contracts created by vinod after 1-Jan-2020
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracts created by vinod after 1-Jan-2020",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 2.044
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 5 :status of contract 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "status of contract 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.151
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "STATUS"
  ],
  "errors": [
  ]
}
===========================================================
Test 6 :expired contracts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "expired contracts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.104
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 7 :contracts for customer number 897654
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": "897654",
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracts for customer number 897654",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_customerNumber",
    "processingTimeMs": 0.594
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 8 :account 10840607 contracts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": "10840607",
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "account 10840607 contracts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_customerNumber",
    "processingTimeMs": 0.113
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 9 :contracts created in 2024
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracts created in 2024",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.342
  },
  "entities": [
    {
      "attribute": "CREATED_DATE",
      "operation": "=",
      "value": "2024",
      "source": "user_input"
    }
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "CREATED_DATE"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2024' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 10 :get all metadata for contract 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get all metadata for contract 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.388
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 11 :contracts under account name 'Siemens'
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracts under account name 'Siemens'",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.173
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 12 :get project type, effective date, and price list for account number 10840607
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": "10840607",
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get project type, effective date, and price list for account number 10840607",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_customerNumber",
    "processingTimeMs": 0.385
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "EFFECTIVE_DATE",
    "TOTAL_VALUE"
  ],
  "errors": [
  ]
}
===========================================================
Test 13 :show contract for customer number 123456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": "123456",
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show contract for customer number 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_customerNumber",
    "processingTimeMs": 0.166
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 14 :shwo contrct 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shwo contrct 123456",
      "correctedInput": "shwo contract 123456",
      "correctionConfidence": 0.3333333333333333
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.137
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 15 :get contrct infro 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get contrct infro 123456",
      "correctedInput": "get contract infro 123456",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.117
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 16 :find conract detials 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "find conract detials 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.148
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 17 :cntract summry for 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "cntract summry for 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.139
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 18 :contarcts created by vinod aftr 1-Jan-2020
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contarcts created by vinod aftr 1-Jan-2020",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.731
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 19 :statuss of contrct 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "statuss of contrct 123456",
      "correctedInput": "statuss of contract 123456",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.129
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "STATUS"
  ],
  "errors": [
  ]
}
===========================================================
Test 20 :exipred contrcts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "exipred contrcts",
      "correctedInput": "exipred contracts",
      "correctionConfidence": 0.5
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.115
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 21 :contracs for cstomer numer 897654
===========================================================
{
  "header": {
    "contractNumber": "897654",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracs for cstomer numer 897654",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.219
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 22 :accunt number 10840607 contrcts
===========================================================
{
  "header": {
    "contractNumber": "10840607",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "accunt number 10840607 contrcts",
      "correctedInput": "accunt number 10840607 contracts",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.137
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 23 :contracts from lst mnth
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracts from lst mnth",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.428
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 24 :contrcts creatd in 2024
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrcts creatd in 2024",
      "correctedInput": "contracts creatd in 2024",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.127
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2024' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 25 :shwo efective date and statuz
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shwo efective date and statuz",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.159
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 26 :get cntracts for acount no 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get cntracts for acount no 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.143
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 27 :contrct summry for custmor 999999
===========================================================
{
  "header": {
    "contractNumber": "999999",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrct summry for custmor 999999",
      "correctedInput": "contract summry for custmor 999999",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.124
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 28 :get contrct detals by acount 10840607
===========================================================
{
  "header": {
    "contractNumber": "10840607",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get contrct detals by acount 10840607",
      "correctedInput": "get contract detals by acount 10840607",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.142
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 29 :contracts created btwn Jan and June 2024
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contracts created btwn Jan and June 2024",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.191
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2024' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 30 :custmer honeywel
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "custmer honeywel",
      "correctedInput": "customer honeywel",
      "correctionConfidence": 0.5
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.093
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 31 :contarcts by vinod
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contarcts by vinod",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.102
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 32 :show contracts for acc no 456789
===========================================================
{
  "header": {
    "contractNumber": "456789",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show contracts for acc no 456789",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.114
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 33 :activ contrcts created by mary
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "activ contrcts created by mary",
      "correctedInput": "activ contracts created by mary",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.118
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 34 :kontract detials 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "kontract detials 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.105
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 35 :kontrakt sumry for account 888888
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": "888888",
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "kontrakt sumry for account 888888",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_customerNumber",
    "processingTimeMs": 1.035
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 36 :boieng contrcts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "boieng contrcts",
      "correctedInput": "boieng contracts",
      "correctionConfidence": 0.5
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.071
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 37 :acc number 1084
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "acc number 1084",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.106
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '1084' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 38 :price list corprate2024
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "CORPRATE2024",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "price list corprate2024",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.080
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION",
    "TOTAL_VALUE"
  ],
  "errors": [
  ]
}
===========================================================
Test 39 :oppurtunity code details
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "oppurtunity code details",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "general_query",
    "processingTimeMs": 0.075
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 40 :get all flieds for customer 123123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": "123123",
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get all flieds for customer 123123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_customerNumber",
    "processingTimeMs": 0.097
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 41 :lst out contrcts with part numbr AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "lst out contrcts with part numbr AE125",
      "correctedInput": "lst out contracts with part numbr ae125",
      "correctionConfidence": 0.14285714285714285
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.904
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 42 :whats the specifcations of prduct AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "whats the specifcations of prduct AE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.189
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 43 :is part AE125 actve or discontnued
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "is part AE125 actve or discontnued",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.206
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 44 :can yu provid datashet for AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "can yu provid datashet for AE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.131
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 45 :wat r compatble prts for AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "wat r compatble prts for AE125",
      "correctedInput": "wat r compatble parts for ae125",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.187
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 46 :ae125 avalable in stok?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "ae125 avalable in stok?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.167
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 47 :what is lede time part AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "what is lede time part AE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.147
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 48 :who's the manufacterer of ae125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "who's the manufacterer of ae125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.148
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 49 :any isses or defect with AE125?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "any isses or defect with AE125?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.208
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 50 :warrenty priod of AE125?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "warrenty priod of AE125?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.101
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 51 :shwo mee parts 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shwo mee parts 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.072
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 52 :how many parst for 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "how many parst for 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.132
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 53 :list the prts of 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "list the prts of 123456",
      "correctedInput": "list the parts of 123456",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.069
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 54 :parts of 123456 not showing
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts of 123456 not showing",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.112
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 55 :123456 prts failed
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "123456 prts failed",
      "correctedInput": "123456 parts failed",
      "correctionConfidence": 0.3333333333333333
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.051
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 56 :faield prts of 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "faield prts of 123456",
      "correctedInput": "faield parts of 123456",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.064
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 57 :parts failed validdation in 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts failed validdation in 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.077
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 58 :filde prts in 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "filde prts in 123456",
      "correctedInput": "filde parts in 123456",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.070
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 59 :contract 123456 parst not loadded
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract 123456 parst not loadded",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.102
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 60 :show partz faild in contrct 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show partz faild in contrct 123456",
      "correctedInput": "show partz faild in contract 123456",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.107
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number 'z' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 61 :parts misssing for 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts misssing for 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.074
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 62 :rejected partz 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "rejected partz 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.097
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number 'z' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 63 :why ae125 was not addedd in contract
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "why ae125 was not addedd in contract",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.108
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 64 :part ae125 pricng mismatch
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "part ae125 pricng mismatch",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.705
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 65 :ae125 nt in mastr data
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "ae125 nt in mastr data",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.188
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 66 :ae125 discntinued?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "ae125 discntinued?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.067
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 67 :shw successfull prts 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shw successfull prts 123456",
      "correctedInput": "shw successfull parts 123456",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.062
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 68 :get all parst that passd in 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get all parst that passd in 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.190
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 69 :what parts faild due to price error
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "what parts faild due to price error",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.110
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "TOTAL_VALUE"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 70 :chek error partz in contrct 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "chek error partz in contrct 123456",
      "correctedInput": "chek error partz in contract 123456",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.081
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number 'z' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 71 :ae125 faild becasue no cost data?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "ae125 faild becasue no cost data?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.102
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION",
    "TOTAL_VALUE"
  ],
  "errors": [
  ]
}
===========================================================
Test 72 :is ae125 loaded in contract 123456?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "is ae125 loaded in contract 123456?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.081
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 73 :ae125 skipped? why?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "ae125 skipped? why?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.079
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 74 :ae125 passd validation?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "ae125 passd validation?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.462
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 75 :parts that arnt in stock 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts that arnt in stock 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.141
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 76 :shwo failed and pasd parts 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shwo failed and pasd parts 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.112
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 77 :hw many partz failed in 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "hw many partz failed in 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.100
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number 'z' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 78 :show parts today loadded 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show parts today loadded 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.087
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 79 :show part AE126 detalis
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE126",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show part AE126 detalis",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.193
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 80 :list all AE partz for contract 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "list all AE partz for contract 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.123
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number 'z' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 81 :shwo me AE125 statuz in contrct
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shwo me AE125 statuz in contrct",
      "correctedInput": "shwo me ae125 statuz in contract",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.082
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 82 :what happen to AE125 during loadding
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "what happen to AE125 during loadding",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.086
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 83 :any issues while loading AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "any issues while loading AE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.068
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 84 :get contract123456 failed parts
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get contract123456 failed parts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 1.127
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 85 :sh0w c0ntract 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": "C0NTRACT",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "sh0w c0ntract 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.086
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 86 :kontract #123456 detais
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "kontract #123456 detais",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.093
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 87 :get al meta 4 cntrct 123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get al meta 4 cntrct 123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.079
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '4' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "INVALID_HEADER",
      "message": "Number '123' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 88 :contrcts expird in 2023
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrcts expird in 2023",
      "correctedInput": "contracts expird in 2023",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.076
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2023' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 89 :cntrct by V1N0D aftr Jan
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "V1N0D",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "cntrct by V1N0D aftr Jan",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.075
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 90 :wats the statuz of 789
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "wats the statuz of 789",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.085
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '789' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 91 :lst 10 contrcts by mary
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "lst 10 contrcts by mary",
      "correctedInput": "lst 10 contracts by mary",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.080
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '10' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 92 :h0w 2 creat a contrct?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "H0W",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "h0w 2 creat a contrct?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.156
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 93 :boeing cntrcts wth prts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "boeing cntrcts wth prts",
      "correctedInput": "boeing cntrcts wth parts",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.097
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 94 :contrct 404 not found
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrct 404 not found",
      "correctedInput": "contract 404 not found",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.074
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '404' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 95 :pls giv contrct 123 detl
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "pls giv contrct 123 detl",
      "correctedInput": "pls giv contract 123 detl",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.081
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '123' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 96 :contrato 456 detalles
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrato 456 detalles",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.101
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '456' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 97 :c0n7r4c7 123!!!
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "C0N7R4C7",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "c0n7r4c7 123!!!",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.055
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 98 :wuu2 wit cntrct 456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "WUU2",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "wuu2 wit cntrct 456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.095
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '456' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 99 :contract 999 where?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract 999 where?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.074
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '999' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 100 :prt AE125 spec pls
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "prt AE125 spec pls",
      "correctedInput": "part ae125 spec pls",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.086
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 101 :hw 2 check AE125 stok
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "hw 2 check AE125 stok",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.097
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 102 :AE125 vs AE126 diff
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE126",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 vs AE126 diff",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.074
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 103 :y is AE125 failng?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "y is AE125 failng?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.054
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 104 :AE125 datasheet.pdf?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 datasheet.pdf?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.062
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 105 :add AE125 2 cntrct
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "add AE125 2 cntrct",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.082
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '2' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 106 :AE125 replacmnt part
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 replacmnt part",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.054
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 107 :AE125 ? in 123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 ? in 123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.050
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 108 :AE125 cost @ 50% off
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 cost @ 50% off",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.059
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION",
    "TOTAL_VALUE"
  ],
  "errors": [
  ]
}
===========================================================
Test 109 :whr is AE125 mfd?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "whr is AE125 mfd?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.043
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 110 :AE125 kab load hoga?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 kab load hoga?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.053
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 111 :p@r7 AE125 $t@tus
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "p@r7 AE125 $t@tus",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.038
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 112 :AE125 zzzz broken
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 zzzz broken",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.045
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 113 :AE125_validation-fail
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125_validation-fail",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.033
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 114 :AE125 specs??
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 specs??",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.038
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 115 :contrct 123456 parts lst
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrct 123456 parts lst",
      "correctedInput": "contract 123456 parts lst",
      "correctionConfidence": 0.25
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.047
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 116 :AE125 in cntrct 789
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125 in cntrct 789",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.073
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '789' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 117 :show cntrct 123 & prts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show cntrct 123 & prts",
      "correctedInput": "show cntrct 123 & parts",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.061
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '123' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 118 :hw many prts in 123?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "hw many prts in 123?",
      "correctedInput": "hw many parts in 123?",
      "correctionConfidence": 0.2
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.051
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 119 :contract 456 + parts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract 456 + parts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.052
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Number '456' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 120 :parts/contract 789 issues
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts/contract 789 issues",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.050
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number 's/contract' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    },
    {
      "code": "INVALID_HEADER",
      "message": "Number '789' too short for contract number (need 6+ digits)",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 121 :contract123456
===========================================================
{
  "header": {
    "contractNumber": "123456",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.025
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 122 :showcontract123456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "SHOWCONTRACT123456",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "showcontract123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.027
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 123 :statusofcontract123456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "STATUSOFCONTRACT123456",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "statusofcontract123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.114
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION",
    "STATUS"
  ],
  "errors": [
  ]
}
===========================================================
Test 124 :detailsforcontract#123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "detailsforcontract#123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "general_query",
    "processingTimeMs": 0.026
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 125 :getcontract2024metadata
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "GETCONTRACT2024METADATA",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "getcontract2024metadata",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.036
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 126 :expiredcontractslist
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "expiredcontractslist",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "general_query",
    "processingTimeMs": 0.023
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 127 :customer897654contracts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "customer897654contracts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.036
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Customer number '897654contracts' must be 4-8 digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 128 :account10840607contracts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "ACCOUNT10840607CONTRACTS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "account10840607contracts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.055
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 129 :vinodcontracts2024
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "VINODCONTRACTS2024",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "vinodcontracts2024",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.044
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 130 :contractSiemensunderaccount
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contractSiemensunderaccount",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.052
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number 'siemensunderaccount' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 131 :projecttype10840607
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "PROJECTTYPE10840607",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "projecttype10840607",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.044
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 132 :customernumber123456contract
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "customernumber123456contract",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.109
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Customer number 'number123456contract' must be 4-8 digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 133 :contractAE125parts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contractAE125parts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.066
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number 'ae125parts' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 134 :failedparts123456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "FAILEDPARTS123456",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "failedparts123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.115
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 135 :contract456789status
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract456789status",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.056
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "STATUS"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '456789status' must be 6+ digits",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 136 :partAE125specs
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125SPECS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "partAE125specs",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.043
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 137 :AE125stockstatus
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125STOCKSTATUS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125stockstatus",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.042
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION",
    "STATUS"
  ],
  "errors": [
  ]
}
===========================================================
Test 138 :AE125warrantyperiod
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125WARRANTYPERIOD",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125warrantyperiod",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.040
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 139 :AE125compatibleparts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125COMPATIBLEPARTS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125compatibleparts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.151
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 140 :AE125validationfailed
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125VALIDATIONFAILED",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125validationfailed",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.042
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 141 :parts123456missing
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "S123456MISSING",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts123456missing",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.033
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 142 :parts123456failed
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "S123456FAILED",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts123456failed",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.030
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 143 :loadAE125contract123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "LOADAE125CONTRACT123",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "loadAE125contract123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.041
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 144 :AE125pricemismatch
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125PRICEMISMATCH",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125pricemismatch",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION",
    "TOTAL_VALUE"
  ],
  "errors": [
  ]
}
===========================================================
Test 145 :AE125manufacturerinfo
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125MANUFACTURERINFO",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125manufacturerinfo",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.050
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 146 :cntrct123456!!!
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "cntrct123456!!!",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.030
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 147 :shwcontrct123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "SHWCONTRCT123",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shwcontrct123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.026
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 148 :prtAE125spec??
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "prtAE125spec??",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.029
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 149 :AE125vsAE126diff
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125VSAE126DIFF",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125vsAE126diff",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.026
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 150 :w2chkAE125stok
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "W2CHKAE125STOK",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "w2chkAE125stok",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.025
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 151 :yAE125failng?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "yAE125failng?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.025
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 152 :addAE125tocntrct
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "ADDAE125TOCNTRCT",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "addAE125tocntrct",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.035
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 153 :AE125cost@50%off
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125cost@50%off",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.027
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "TOTAL_VALUE"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 154 :AE125kbloadhoga?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125kbloadhoga?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 155 :p@r7AE125$t@tus
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "p@r7AE125$t@tus",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.028
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 156 :c0n7r4c7123!!!
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "c0n7r4c7123!!!",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.045
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 157 :wuu2witcntrct456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "WUU2WITCNTRCT456",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "wuu2witcntrct456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.028
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 158 :AE125zzzzbroken
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125ZZZZBROKEN",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125zzzzbroken",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 159 :AE125_valid-fail
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125_valid-fail",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.032
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 160 :contrct123&prts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contrct123&prts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.033
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 161 :contract#123456/details
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract#123456/details",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.028
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '#123456/details' must be 6+ digits",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 162 :part-AE125/status
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "part-AE125/status",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.029
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME",
    "STATUS"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Part number '-ae125/status' must be 3+ alphanumeric characters",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 163 :contract 123456&parts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract 123456&parts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.108
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 164 :AE125_in_contract789
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125_in_contract789",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.027
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 165 :contract:123456,parts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract:123456,parts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.036
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number ':123456' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 166 :contract123;parts456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "S456",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract123;parts456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.041
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '123' must be 6+ digits",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 167 :contract 123?parts=AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract 123?parts=AE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.064
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 168 :contract@123456#parts
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract@123456#parts",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.027
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '@123456#parts' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 169 :AE125|contract123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125|contract123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.027
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 170 :contract(123)+parts(AE125)
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract(123)+parts(AE125)",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.032
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '(123)+parts(ae125)' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 171 :plsshowcntrct123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "PLSSHOWCNTRCT123",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "plsshowcntrct123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.027
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 172 :givmectrct456deets
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "GIVMECTRCT456DEETS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "givmectrct456deets",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.051
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 173 :needprtAE125infoASAP
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "NEEDPRTAE125INFOASAP",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "needprtAE125infoASAP",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.030
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 174 :AE125statpls
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125STATPLS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125statpls",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.030
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 175 :cntrct789quicklook
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "CNTRCT789QUICKLOOK",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "cntrct789quicklook",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.023
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 176 :whtspartsin123?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "whtspartsin123?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.034
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 177 :chkAE125valid
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "CHKAE125VALID",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "chkAE125valid",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.038
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 178 :contract123sumry
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "contract123sumry",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.024
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '123sumry' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 179 :AE125warrantypls
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125WARRANTYPLS",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125warrantypls",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.029
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 180 :prts4contract456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "PRTS4CONTRACT456",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "prts4contract456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.026
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 181 :c0ntrct123prts456!!!
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "c0ntrct123prts456!!!",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.028
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 182 :AE125$$$contract@@@
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125$$$contract@@@",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 183 :??123456
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "??123456",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 184 :PARTae125CONTRACT123
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125CONTRACT123",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "PARTae125CONTRACT123",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.045
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 185 :123456CONTRACTae125PART
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "123456CONTRACTAE125PART",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "123456CONTRACTae125PART",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 186 :CONTRACT/123/PARTS/AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "CONTRACT/123/PARTS/AE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.030
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "INVALID_HEADER",
      "message": "Contract number '/123/parts/ae125' must be 6+ digits",
      "severity": "BLOCKER"
    },
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 187 :AE125...contract123...
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "AE125...contract123...",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 188 :??123&partsAE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "??123&partsAE125",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.026
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 189 :??123456??
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "??123456??",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.041
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 190 :CONTRATO123PARTES
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "CONTRATO123PARTES",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "CONTRATO123PARTES",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.032
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 191 :Show all failed parts for contract 987654
===========================================================
{
  "header": {
    "contractNumber": "987654",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Show all failed parts for contract 987654",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.041
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 192 :What is the reason for failure of part AE125?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "What is the reason for failure of part AE125?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.095
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 193 :List all parts that failed under contract CN1234
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "CN1234",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "List all parts that failed under contract CN1234",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.136
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 194 :Get failure message for part number PN7890
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "PN7890",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Get failure message for part number PN7890",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.064
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 195 :Why did part PN4567 fail?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "PN4567",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Why did part PN4567 fail?",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.047
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 196 :Show failed message and reason for AE777
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE777",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Show failed message and reason for AE777",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.053
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 197 :List failed parts and their contract numbers
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "List failed parts and their contract numbers",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "general_query",
    "processingTimeMs": 0.049
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 198 :Parts failed due to voltage issues
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Parts failed due to voltage issues",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.064
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 199 :Find all parts failed with error message "Leak detected"
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Find all parts failed with error message \"Leak detected\"",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "general_query",
    "processingTimeMs": 0.053
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 200 :Which parts failed in contract 888999
===========================================================
{
  "header": {
    "contractNumber": "888999",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "Which parts failed in contract 888999",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.052
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 201 :show faild prts for contrct 987654
===========================================================
{
  "header": {
    "contractNumber": "987654",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "show faild prts for contrct 987654",
      "correctedInput": "show faild parts for contract 987654",
      "correctionConfidence": 0.3333333333333333
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.067
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 202 :reasn for failr of prt AE125
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE125",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "reasn for failr of prt AE125",
      "correctedInput": "reasn for failr of part ae125",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.064
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 203 :get falure mesage for part PN7890
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "PN7890",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "get falure mesage for part PN7890",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.073
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 204 :whch prts faild in cntract CN1234
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "CN1234",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "whch prts faild in cntract CN1234",
      "correctedInput": "whch parts faild in cntract cn1234",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.038
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 205 :list fialed prts due to ovrheating
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "list fialed prts due to ovrheating",
      "correctedInput": "list fialed parts due to ovrheating",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "general_query",
    "processingTimeMs": 0.038
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 206 :wht is the faild reasn of AE777
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE777",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "wht is the faild reasn of AE777",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.031
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 207 :parts whch hav faild n contract 888999
===========================================================
{
  "header": {
    "contractNumber": "888999",
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "parts whch hav faild n contract 888999",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "contracts_by_contractNumber",
    "processingTimeMs": 0.028
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
  ]
}
===========================================================
Test 208 :shw me mesage colum for faild part AE901
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": "AE901",
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "shw me mesage colum for faild part AE901",
      "correctedInput": null,
      "correctionConfidence": 0.0
    }
  },
  "queryMetadata": {
    "queryType": "PARTS",
    "actionType": "parts_by_partNumber",
    "processingTimeMs": 0.033
  },
  "entities": [
  ],
  "displayEntities": [
    "PART_NUMBER",
    "DESCRIPTION"
  ],
  "errors": [
  ]
}
===========================================================
Test 209 :prts faild with resn “voltag drop”
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "prts faild with resn “voltag drop”",
      "correctedInput": "parts faild with resn “voltag drop”",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 1.176
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================
Test 210 :falure rsn fr prt numbr AE456?
===========================================================
{
  "header": {
    "contractNumber": null,
    "partNumber": null,
    "customerNumber": null,
    "customerName": null,
    "createdBy": null,
    "inputTracking": {
      "originalInput": "falure rsn fr prt numbr AE456?",
      "correctedInput": "falure rsn fr part numbr ae456?",
      "correctionConfidence": 0.16666666666666666
    }
  },
  "queryMetadata": {
    "queryType": "CONTRACTS",
    "actionType": "error",
    "processingTimeMs": 0.075
  },
  "entities": [
  ],
  "displayEntities": [
    "CONTRACT_NUMBER",
    "CUSTOMER_NAME"
  ],
  "errors": [
    {
      "code": "MISSING_HEADER",
      "message": "Provide at least one identifier (contract/part/customer) or filter (date/status)",
      "severity": "BLOCKER"
    }
  ]
}
===========================================================

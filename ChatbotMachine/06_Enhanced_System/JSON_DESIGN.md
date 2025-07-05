{
  "header": {
    "contractNumber": null,      // String|null - must have at least one non-null header OR entities
    "partNumber": null,          // String|null - maps to PART_NUMBER in DB
    "customerNumber": null,      // String|null - maps to CUSTOMER_NUMBER
    "customerName": null,        // String|null - maps to CUSTOMER_NAME
    "createdBy": null,           // String|null - maps to CREATED_BY
    "inputTracking": {           // New section for original/corrected input
      "originalInput": "",       // String - raw user query (required)
      "correctedInput": "",      // String|null - spell-corrected version
      "correctionConfidence": 0  // Number [0-1] - confidence in corrections
    }
  },
  "queryMetadata": {
    "queryType": "",             // Enum: "CONTRACTS" or "PARTS" (required)
    "actionType": "",            // Auto-determined (e.g., "contracts_by_date")
    "processingTimeMs": 0        // Number - time taken for NLP processing
  },
  "entities": [                  // Array of filter objects
    {
      "attribute": "",           // String - exact DB column name (required)
      "operation": "",           // String: "=", ">", "<", "between"
      "value": "",               // String - filter value (required)
      "source": ""               // String: "user_input" or "inferred"
    }
  ],
  "displayEntities": [           // Fields to return in output
    "CONTRACT_NUMBER",           // Default field
    "CUSTOMER_NAME",             // Default field
    ""                           // User-requested/auto-added fields
  ],
  "errors": [                    // Error objects
    {
      "code": "",                // String: error type
      "message": "",             // String - human-readable
      "severity": ""             // String: "BLOCKER" or "WARNING"
    }
  ]
}

New inputTracking Section in Header:
"inputTracking": {
  "originalInput": "contrcts for boeing",  // Required
  "correctedInput": "contracts for boeing", 
  "correctionConfidence": 0.95             // 0-1 scale
}
Tracks raw input and NLP corrections

Helps debug translation issues
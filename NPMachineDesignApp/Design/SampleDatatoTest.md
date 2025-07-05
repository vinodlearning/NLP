Sample data inputs:

        String[] allTestCases = {
            // Original Contract Queries (40)
            "show contract 123456", "contract details 123456", "get contract info 123456",
            "contracts created by vinod after 1-Jan-2020", "status of contract 123456", "expired contracts",
            "contracts for customer number 897654", "account 10840607 contracts", "contracts created in 2024",
            "get all metadata for contract 123456", "contracts under account name 'Siemens'",
            "get project type, effective date, and price list for account number 10840607",
            "show contract for customer number 123456", "shwo contrct 123456", "get contrct infro 123456",
            "find conract detials 123456", "cntract summry for 123456", "contarcts created by vinod aftr 1-Jan-2020",
            "statuss of contrct 123456", "exipred contrcts", "contracs for cstomer numer 897654",
            "accunt number 10840607 contrcts", "contracts from lst mnth", "contrcts creatd in 2024",
            "shwo efective date and statuz", "get cntracts for acount no 123456", "contrct summry for custmor 999999",
            "get contrct detals by acount 10840607", "contracts created btwn Jan and June 2024", "custmer honeywel",
            "contarcts by vinod", "show contracts for acc no 456789", "activ contrcts created by mary",
            "kontract detials 123456", "kontrakt sumry for account 888888", "boieng contrcts", "acc number 1084",
            "price list corprate2024", "oppurtunity code details", "get all flieds for customer 123123",

            // Original Parts Queries (44)
            "lst out contrcts with part numbr AE125", "whats the specifcations of prduct AE125",
            "is part AE125 actve or discontnued", "can yu provid datashet for AE125", "wat r compatble prts for AE125",
            "ae125 avalable in stok?", "what is lede time part AE125", "who's the manufacterer of ae125",
            "any isses or defect with AE125?", "warrenty priod of AE125?", "shwo mee parts 123456",
            "how many parst for 123456", "list the prts of 123456", "parts of 123456 not showing", "123456 prts failed",
            "faield prts of 123456", "parts failed validdation in 123456", "filde prts in 123456",
            "contract 123456 parst not loadded", "show partz faild in contrct 123456", "parts misssing for 123456",
            "rejected partz 123456", "why ae125 was not addedd in contract", "part ae125 pricng mismatch",
            "ae125 nt in mastr data", "ae125 discntinued?", "shw successfull prts 123456",
            "get all parst that passd in 123456", "what parts faild due to price error",
            "chek error partz in contrct 123456", "ae125 faild becasue no cost data?",
            "is ae125 loaded in contract 123456?", "ae125 skipped? why?", "ae125 passd validation?",
            "parts that arnt in stock 123456", "shwo failed and pasd parts 123456", "hw many partz failed in 123456",
            "show parts today loadded 123456", "show part AE126 detalis", "list all AE partz for contract 123456",
            "shwo me AE125 statuz in contrct", "what happen to AE125 during loadding", "any issues while loading AE125", "get contract123456 failed parts",

            // Enhanced Edge Cases (50+)
            // Contract Queries
            "sh0w c0ntract 123456", "kontract #123456 detais", "get al meta 4 cntrct 123", "contrcts expird in 2023",
            "cntrct by V1N0D aftr Jan", "wats the statuz of 789", "lst 10 contrcts by mary", "h0w 2 creat a contrct?",
            "boeing cntrcts wth prts", "contrct 404 not found", "pls giv contrct 123 detl", "contrato 456 detalles",
            "c0n7r4c7 123!!!", "wuu2 wit cntrct 456", "contract 999 where?",

            // Parts Queries
            "prt AE125 spec pls", "hw 2 check AE125 stok", "AE125 vs AE126 diff", "y is AE125 failng?",
            "AE125 datasheet.pdf?", "add AE125 2 cntrct", "AE125 replacmnt part", "AE125 ❌ in 123456",
            "AE125 cost @ 50% off", "whr is AE125 mfd?", "AE125 kab load hoga?", "p@r7 AE125 $t@tus",
            "AE125 zzzz broken", "AE125_validation-fail", "AE125 specs??",

            // Mixed Queries
            "contrct 123456 parts lst", "AE125 in cntrct 789", "show cntrct 123 & prts", "hw many prts in 123?",
            "contract 456 + parts", "parts/contract 789 issues", "contract123456", "showcontract123456",
            "statusofcontract123456", "detailsforcontract#123", "getcontract2024metadata", "expiredcontractslist",
            "customer897654contracts", "account10840607contracts", "vinodcontracts2024", "contractSiemensunderaccount",
            "projecttype10840607", "customernumber123456contract", "contractAE125parts", "failedparts123456", "contract456789status",

            // Parts
            "partAE125specs", "AE125stockstatus", "AE125warrantyperiod", "AE125compatibleparts",
            "AE125validationfailed", "parts123456missing", "parts123456failed", "loadAE125contract123",
            "AE125pricemismatch", "AE125manufacturerinfo",

            //=== Extreme No-Space + Typos (30) ===//
            "cntrct123456!!!", "shwcontrct123", "prtAE125spec??", "AE125vsAE126diff", "w2chkAE125stok", "yAE125failng?",
            "addAE125tocntrct", "AE125cost@50%off", "AE125kbloadhoga?", "p@r7AE125$t@tus", "c0n7r4c7123!!!",
            "wuu2witcntrct456", "AE125zzzzbroken", "AE125_valid-fail", "contrct123&prts",

            //=== Mixed Spacing + Symbols (20) ===//
            "contract#123456/details", "part-AE125/status", "contract 123456&parts", "AE125_in_contract789",
            "contract:123456,parts", "contract123;parts456", "contract 123?parts=AE125", "contract@123456#parts",
            "AE125|contract123", "contract(123)+parts(AE125)",

            //=== Real-World Abbreviations (15) ===//
            "plsshowcntrct123", "givmectrct456deets", "needprtAE125infoASAP", "AE125statpls", "cntrct789quicklook",
            "whtspartsin123?", "chkAE125valid", "contract123sumry", "AE125warrantypls", "prts4contract456",

            //=== Stress Tests (15) ===//
            "c0ntrct123prts456!!!", "AE125$$$contract@@@", "合同123456", // Non-Latin characters
            "PARTae125CONTRACT123", "123456CONTRACTae125PART", "CONTRACT/123/PARTS/AE125", "AE125...contract123...",
            "合同123&partsAE125", "契約123456詳細", // Japanese
            "CONTRATO123PARTES",
			// ✅ Correct Queries
    "Show all failed parts for contract 987654",
    "What is the reason for failure of part AE125?",
    "List all parts that failed under contract CN1234",
    "Get failure message for part number PN7890",
    "Why did part PN4567 fail?",
    "Show failed message and reason for AE777",
    "List failed parts and their contract numbers",
    "Parts failed due to voltage issues",
    "Find all parts failed with error message \"Leak detected\"",
    "Which parts failed in contract 888999",

    // ❌ Queries with Typos
    "show faild prts for contrct 987654",
    "reasn for failr of prt AE125",
    "get falure mesage for part PN7890",
    "whch prts faild in cntract CN1234",
    "list fialed prts due to ovrheating",
    "wht is the faild reasn of AE777",
    "parts whch hav faild n contract 888999",
    "shw me mesage colum for faild part AE901",
    "prts faild with resn “voltag drop”",
    "falure rsn fr prt numbr AE456?"


        };
		


Contracts Table Columns:


EXP_NOTIF_SENT_90
EXP_NOTIF_SENT_60
EXP_NOTIF_SENT_30
EXP_NOTIF_FEEDBACK
ADDL_OPPORTUNITIES
MIN_INV_OBLIGATION
CREATED_BY
UPDATED_BY
UPDATED_DATE
IS_PROGRAM
IS_HPP_UNPRICED_CONTRACT
AWARD_NUMBER
CONTRACT_NAME
CUSTOMER_NAME
CUSTOMER_NUMBER
ALTERNATE_CUSTOMERS
EFFECTIVE_DATE
EXPIRATION_DATE
PRICE_EXPIRATION_DATE
CONTRACT_LENGTH
PAYMENT_TERMS
INCOTERMS
PROGRAM_INFORMATION
CMI
VMI
BAILMENT
CONSIGNMENT
EDI
MIN_MAX
KITTING
PL_3
PL_4
FSL_LOCATION
VENDING_MACHINES
SERVICE_FEE_APPLIES
CURRENCY
E_COMMERCE_ACCESS
ULTIMATE_DESTINATION
GT_25
PART_FILE
PMA_TSO_APPLIES
DFAR_APPLIES
ASL_APPLIES
ASL_DETAIL
STOCKING_STRATEGY
LIABILITY_ON_INVESTMENT
HPP_LANGUAGE
CSM_LANGUAGE
D_ITEM_LANGUAGE
REBATE
LINE_MIN
ORDER_MIN
EFFECTIVE_LOL
PENALTIES_DAMAGES
UNUSUAL_TITLE_TRANSFER
RIGHTS_OF_RETURN
INCENTIVES_CREDITS
CANCELLATION_PRIVILEGES
BILL_AND_HOLD
BUY_BACK
CREATE_DATE
PROCESS_FLAG
OPPORTUNITY_NUMBER
CONTRACT_TYPE
COMPLETENESS_CHECK
WAREHOUSE_INFO
PROJECT_TYPE
IS_TSO_PMA
GROUP_TYPE
PRICE_LIST
TITLE
DESCRIPTION
COMMENTS
STATUS
IMPL_CODE
S_ITEM_LANGUAGE
EXTERNAL_CONTRACT_NUMBER
ACCOUNT_TYPE
COMPETITION
EXISTING_CONTRACT_NUMBER
EXISTING_CONTRACT_TYPE
IS_FSL_REQ
IS_SITE_VISIT_REQ
LEGAL_FORMAT_TYPE
CUSTOMER_FOCUS
MOQS_AMORTIZE
PLATFORM_INFO
RETURN_PART_LIST_FORMAT
SOURCE_PRODUCTS
SUMMARY
TARGET_MARGIN
TOTAL_PART_COUNT
CRF_ID




Parts Table Columns:

FUTURE_PRICE2
F_PRICE_EFFECTIVE_DATE2
FUTURE_PRICE3
F_PRICE_EFFECTIVE_DATE3
DATE_LOADED
COMMENTS
CREATION_DATE
CREATED_BY
LAST_UPDATE_DATE
LAST_UPDATED_BY
AWARD_TAGS
PREV_PRICE
REPRICE_EFFECTIVE_DATE
EXTERNAL_CONTRACT_NO
EXTERNAL_LINE_NO
PLANT
VALUATION_TYPE
OPPORTUNITY_NUMBER
NSN_PART_NUMBER
CSM_STATUS
TEST_REPORTS_REQUIRED
INCOTERMS
INCOTERMS_LOCATION
CSM_MONITORED
AWARD_ID
LINE_NO
INVOICE_PART_NUMBER
EAU
UOM
PRICE
ITEM_CLASSIFICATION
LEAD_TIME
STATUS
AWARD_REP_COMMENTS
CUSTOMER_REFERENCE
ASL_CODES
PRIME
LOADED_CP_NUMBER
FUTURE_PRICE
F_PRICE_EFFECTIVE_DATE
EFFECTIVE_DATE
PART_EXPIRATION_DATE
MOQ
SAP_NUMBER
TOT_CON_QTY_REQ
QUOTE_COST
QUOTE_COST_SOURCE
PURCHASE_COMMENTS
SALES_COMMENTS
CUSTOMER_RESPONSE
PL4_VENDOR
APPLICABLE_CONTRACT
CUST_EXCLUDE_PN
PLANNING_COMMENTS

PASRT_ERROR table
PASRT_NUMEBR
CILUM_FAILED
CONTARCT_NUMBER
REASON

FAILED_PARTS TABLE:

PASRT_NUMEBR
ERROR_COLUMN
LAODED_CP_NUMBER 
REASON

Note: Loade_CP_number or AWARD_NUMBER or CONTARCT_NUMBER all are same.

ACCOUNT_NUMBER OR CUSTOMER_NUMBER same.


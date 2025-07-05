Read carefully and design the machine learning model to predict the test cases that failed.

Failed Test Cases:

1. Test 4: "contracts created by vinod after 1-Jan-2020"
   - Reason: Missing header. The system requires at least one identifier (contract/part/customer) or filter (date/status), but the query only specifies a creator and date range.

2. Test 6: "expired contracts"
   - Reason: Missing header. The system expects an identifier or filter, but the query is valid as the user wants a list of all expired contracts.

3. Test 9: "contracts created in 2024"
   - Reason: Invalid header. The system interprets "2024" as a contract number, which is too short (needs 6+ digits). The query is valid for filtering by creation year.

4. Test 11: "contracts under account name 'Siemens'"
   - Reason: Missing header. The system requires an identifier or filter, but the query is valid for filtering by customer name.

5. Test 18: "contarcts created by vinod aftr 1-Jan-2020"
   - Reason: Missing header. Similar to Test 4, the query lacks a required identifier or filter.

6. Test 20: "exipred contrcts"
   - Reason: Missing header. Similar to Test 6, the query is valid for listing expired contracts but lacks a required identifier.

7. Test 23: "contracts from lst mnth"
   - Reason: Missing header. The query is valid for filtering by date but lacks a required identifier or filter.

8. Test 24: "contrcts creatd in 2024"
   - Reason: Invalid header. Similar to Test 9, "2024" is interpreted as an invalid contract number.

9. Test 25: "shwo efective date and statuz"
   - Reason: Missing header. The query lacks a required identifier or filter.

10. Test 29: "contracts created btwn Jan and June 2024"
    - Reason: Invalid header. The system interprets "2024" as an invalid contract number.

11. Test 30: "custmer honeywel"
    - Reason: Missing header. The query lacks a required identifier or filter.

12. Test 31: "contarcts by vinod"
    - Reason: Missing header. The query lacks a required identifier or filter.

13. Test 33: "activ contrcts created by mary"
    - Reason: Missing header. The query lacks a required identifier or filter.

14. Test 49: "any isses or defect with AE125?"
    - Reason: Missing header. The query lacks a required identifier or filter.

15. Test 50: "warrenty priod of AE125?"
    - Reason: Missing header. The query lacks a required identifier or filter.

16. Test 60: "show partz faild in contrct 123456"
    - Reason: Invalid header. The part number "z" is too short (needs 3+ alphanumeric characters).

17. Test 62: "rejected partz 123456"
    - Reason: Invalid header. The part number "z" is too short (needs 3+ alphanumeric characters).

18. Test 77: "hw many partz failed in 123456"
    - Reason: Invalid header. The part number "z" is too short (needs 3+ alphanumeric characters).

19. Test 80: "list all AE partz for contract 123456"
    - Reason: Invalid header. The part number "z" is too short (needs 3+ alphanumeric characters).

20. Test 86: "kontract #123456 detais"
    - Reason: Missing header. The query lacks a required identifier or filter.

21. Test 87: "get al meta 4 cntrct 123"
    - Reason: Invalid header. The numbers "4" and "123" are too short for contract numbers (need 6+ digits).

22. Test 88: "contrcts expird in 2023"
    - Reason: Invalid header. "2023" is interpreted as an invalid contract number.

23. Test 90: "wats the statuz of 789"
    - Reason: Invalid header. "789" is too short for a contract number (needs 6+ digits).

24. Test 91: "lst 10 contrcts by mary"
    - Reason: Invalid header. "10" is too short for a contract number (needs 6+ digits).

25. Test 92: "h0w 2 creat a contrct?"
    - Reason: Invalid header. "2" is too short for a contract number (needs 6+ digits).

26. Test 93: "boeing cntrcts wth prts"
    - Reason: Missing header. The query lacks a required identifier or filter.

27. Test 94: "contrct 404 not found"
    - Reason: Invalid header. "404" is too short for a contract number (needs 6+ digits).

28. Test 95: "pls giv contrct 123 detl"
    - Reason: Invalid header. "123" is too short for a contract number (needs 6+ digits).

29. Test 96: "contrato 456 detalles"
    - Reason: Invalid header. "456" is too short for a contract number (needs 6+ digits).

30. Test 99: "contract 999 where?"
    - Reason: Invalid header. "999" is too short for a contract number (needs 6+ digits).

31. Test 101: "hw 2 check AE125 stok"
    - Reason: Invalid header. "2" is too short for a contract number (needs 6+ digits).

32. Test 105: "add AE125 2 cntrct"
    - Reason: Invalid header. "2" is too short for a contract number (needs 6+ digits).

33. Test 113: "AE125_validation-fail"
    - Reason: Missing header. The query lacks a required identifier or filter.

34. Test 116: "AE125 in cntrct 789"
    - Reason: Invalid header. "789" is too short for a contract number (needs 6+ digits).

35. Test 117: "show cntrct 123 & prts"
    - Reason: Invalid header. "123" is too short for a contract number (needs 6+ digits).

36. Test 118: "hw many prts in 123?"
    - Reason: Missing header. The query lacks a required identifier or filter.

37. Test 119: "contract 456 + parts"
    - Reason: Invalid header. "456" is too short for a contract number (needs 6+ digits).

38. Test 120: "parts/contract 789 issues"
    - Reason: Invalid header. "789" is too short for a contract number (needs 6+ digits).

39. Test 146: "cntrct123456!!!"
    - Reason: Missing header. The query lacks a required identifier or filter.

40. Test 148: "prtAE125spec??"
    - Reason: Missing header. The query lacks a required identifier or filter.

41. Test 151: "yAE125failng?"
    - Reason: Missing header. The query lacks a required identifier or filter.

42. Test 153: "AE125cost@50%off"
    - Reason: Missing header. The query lacks a required identifier or filter.

43. Test 154: "AE125kbloadhoga?"
    - Reason: Missing header. The query lacks a required identifier or filter.

44. Test 155: "p@r7AE125$t@tus"
    - Reason: Missing header. The query lacks a required identifier or filter.

45. Test 156: "c0n7r4c7123!!!"
    - Reason: Missing header. The query lacks a required identifier or filter.

46. Test 159: "AE125_valid-fail"
    - Reason: Missing header. The query lacks a required identifier or filter.

47. Test 160: "contrct123&prts"
    - Reason: Missing header. The query lacks a required identifier or filter.

48. Test 161: "contract#123456/details"
    - Reason: Invalid header. The contract number includes invalid characters.

49. Test 162: "part-AE125/status"
    - Reason: Invalid header. The part number includes invalid characters.

50. Test 163: "contract 123456&parts"
    - Reason: Missing header. The query lacks a required identifier or filter.

51. Test 164: "AE125_in_contract789"
    - Reason: Missing header. The query lacks a required identifier or filter.

52. Test 165: "contract:123456,parts"
    - Reason: Invalid header. The contract number includes invalid characters.

53. Test 166: "contract123;parts456"
    - Reason: Invalid header. "123" is too short for a contract number (needs 6+ digits).

54. Test 167: "contract 123?parts=AE125"
    - Reason: Missing header. The query lacks a required identifier or filter.

55. Test 168: "contract@123456#parts"
    - Reason: Invalid header. The contract number includes invalid characters.

56. Test 169: "AE125|contract123"
    - Reason: Missing header. The query lacks a required identifier or filter.

57. Test 170: "contract(123)+parts(AE125)"
    - Reason: Invalid header. "123" is too short for a contract number (needs 6+ digits).

58. Test 176: "whtspartsin123?"
    - Reason: Missing header. The query lacks a required identifier or filter.

59. Test 178: "contract123sumry"
    - Reason: Invalid header. "123" is too short for a contract number (needs 6+ digits).

60. Test 181: "c0ntrct123prts456!!!"
    - Reason: Missing header. The query lacks a required identifier or filter.

61. Test 182: "AE125$$$contract@@@"
    - Reason: Missing header. The query lacks a required identifier or filter.

62. Test 183: "??123456"
    - Reason: Missing header. The query lacks a required identifier or filter.

63. Test 186: "CONTRACT/123/PARTS/AE125"
    - Reason: Invalid header. "123" is too short for a contract number (needs 6+ digits).

64. Test 187: "AE125...contract123..."
    - Reason: Missing header. The query lacks a required identifier or filter.

65. Test 188: "??123&partsAE125"
    - Reason: Missing header. The query lacks a required identifier or filter.

66. Test 189: "??123456??"
    - Reason: Missing header. The query lacks a required identifier or filter.

67. Test 192: "What is the reason for failure of part AE125?"
    - Reason: Missing header. The query lacks a required identifier or filter.

68. Test 198: "Parts failed due to voltage issues"
    - Reason: Missing header. The query lacks a required identifier or filter.

69. Test 209: "prts faild with resn “voltag drop”"
    - Reason: Missing header. The query lacks a required identifier or filter.

70. Test 210: "falure rsn fr prt numbr AE456?"
    - Reason: Missing header. The query lacks a required identifier or filter.
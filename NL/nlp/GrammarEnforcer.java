package view.nlp;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Grammar Enforcer for Enhanced NLP Processing
 * Provides comprehensive grammar checking, correction, and enforcement
 * for business and contract-related natural language processing
 */
public class GrammarEnforcer {

    // Grammar rule sets
    private Map<String, String> subjectVerbAgreementRules;
    private Map<String, String> articleRules;
    private Map<String, String> prepositionRules;
    private Map<String, String> verbFormRules;
    private Map<String, String> pluralizationRules;
    private Set<String> irregularVerbs;
    private Set<String> irregularPlurals;
    private Map<String, String> commonGrammarErrors;

    // Business-specific grammar patterns
    private Map<String, String> businessPhraseCorrections;
    private Set<String> formalBusinessTerms;
    private Map<String, String> contractLanguageRules;

    // Sentence structure patterns
    private List<GrammarRule> grammarRules;
    private Map<String, String> sentencePatterns;

    // Regular expressions for grammar checking
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+\\s*");
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");
    private static final Pattern VERB_PATTERN =
        Pattern.compile("\\b(is|are|was|were|have|has|had|do|does|did|will|would|can|could|should|shall|may|might)\\b",
                        Pattern.CASE_INSENSITIVE);
    private static final Pattern ARTICLE_PATTERN = Pattern.compile("\\b(a|an|the)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PREPOSITION_PATTERN =
        Pattern.compile("\\b(in|on|at|by|for|with|from|to|of|about|under|over|through|during|before|after|above|below|between|among)\\b",
                        Pattern.CASE_INSENSITIVE);

    // Configuration constants
    private static final double GRAMMAR_CONFIDENCE_THRESHOLD = 0.7;
    private static final int MAX_SUGGESTIONS = 3;

    /**
     * Constructor initializes all grammar rules and patterns
     */
    public GrammarEnforcer() {
        initializeSubjectVerbAgreement();
        initializeArticleRules();
        initializePrepositionRules();
        initializeVerbFormRules();
        initializePluralizationRules();
        initializeIrregularForms();
        initializeCommonGrammarErrors();
        initializeBusinessGrammar();
        initializeGrammarRules();
        initializeSentencePatterns();
    }

    /**
     * Initialize subject-verb agreement rules
     */
    private void initializeSubjectVerbAgreement() {
        subjectVerbAgreementRules = new HashMap<>();

        // Singular subjects with plural verbs (incorrect)
        subjectVerbAgreementRules.put("contract are", "contract is");
        subjectVerbAgreementRules.put("customer are", "customer is");
        subjectVerbAgreementRules.put("account are", "account is");
        subjectVerbAgreementRules.put("invoice are", "invoice is");
        subjectVerbAgreementRules.put("payment are", "payment is");
        subjectVerbAgreementRules.put("document are", "document is");
        subjectVerbAgreementRules.put("record are", "record is");
        subjectVerbAgreementRules.put("status are", "status is");
        subjectVerbAgreementRules.put("information are", "information is");
        subjectVerbAgreementRules.put("data are", "data is"); // Business context: often treated as singular

        // Plural subjects with singular verbs (incorrect)
        subjectVerbAgreementRules.put("contracts is", "contracts are");
        subjectVerbAgreementRules.put("customers is", "customers are");
        subjectVerbAgreementRules.put("accounts is", "accounts are");
        subjectVerbAgreementRules.put("invoices is", "invoices are");
        subjectVerbAgreementRules.put("payments is", "payments are");
        subjectVerbAgreementRules.put("documents is", "documents are");
        subjectVerbAgreementRules.put("records is", "records are");

        // Have/Has agreement
        subjectVerbAgreementRules.put("contract have", "contract has");
        subjectVerbAgreementRules.put("customer have", "customer has");
        subjectVerbAgreementRules.put("contracts has", "contracts have");
        subjectVerbAgreementRules.put("customers has", "customers have");

        // Do/Does agreement
        subjectVerbAgreementRules.put("contract do", "contract does");
        subjectVerbAgreementRules.put("customer do", "customer does");
        subjectVerbAgreementRules.put("contracts does", "contracts do");
        subjectVerbAgreementRules.put("customers does", "customers do");
    }

    /**
     * Initialize article usage rules (a, an, the)
     */
    private void initializeArticleRules() {
        articleRules = new HashMap<>();

        // A vs An rules
        articleRules.put("a account", "an account");
        articleRules.put("a invoice", "an invoice");
        articleRules.put("a order", "an order");
        articleRules.put("a email", "an email");
        articleRules.put("a update", "an update");
        articleRules.put("a error", "an error");
        articleRules.put("a amount", "an amount");
        articleRules.put("a address", "an address");
        articleRules.put("a organization", "an organization");
        articleRules.put("a employee", "an employee");

        // An vs A rules
        articleRules.put("an contract", "a contract");
        articleRules.put("an customer", "a customer");
        articleRules.put("an payment", "a payment");
        articleRules.put("an document", "a document");
        articleRules.put("an business", "a business");
        articleRules.put("an company", "a company");
        articleRules.put("an manager", "a manager");
        articleRules.put("an report", "a report");
        articleRules.put("an user", "a user");
        articleRules.put("an project", "a project");

        // Missing articles
        articleRules.put("show contract", "show the contract");
        articleRules.put("find customer", "find the customer");
        articleRules.put("get payment", "get the payment");
        articleRules.put("update account", "update the account");
        articleRules.put("delete record", "delete the record");
    }

    /**
     * Initialize preposition usage rules
     */
    private void initializePrepositionRules() {
        prepositionRules = new HashMap<>();

        // Common preposition errors
        prepositionRules.put("search for contract", "search for a contract");
        prepositionRules.put("look at contract", "look at the contract");
        prepositionRules.put("work in project", "work on the project");
        prepositionRules.put("depend of", "depend on");
        prepositionRules.put("different than", "different from");
        prepositionRules.put("comply to", "comply with");
        prepositionRules.put("agree to contract", "agree to the contract");
        prepositionRules.put("responsible of", "responsible for");
        prepositionRules.put("consist in", "consist of");
        prepositionRules.put("based in", "based on");

        // Business-specific preposition rules
        prepositionRules.put("contract with number", "contract with the number");
        prepositionRules.put("payment for invoice", "payment for the invoice");
        prepositionRules.put("account of customer", "account for the customer");
        prepositionRules.put("information about contract", "information about the contract");
        prepositionRules.put("details of account", "details of the account");
    }

    /**
     * Initialize verb form rules
     */
    private void initializeVerbFormRules() {
        verbFormRules = new HashMap<>();

        // Past tense corrections
        verbFormRules.put("catched", "caught");
        verbFormRules.put("teached", "taught");
        verbFormRules.put("brang", "brought");
        verbFormRules.put("runned", "ran");
        verbFormRules.put("goed", "went");
        verbFormRules.put("comed", "came");
        verbFormRules.put("sended", "sent");
        verbFormRules.put("builded", "built");

        // Present tense corrections
        verbFormRules.put("don't got", "don't have");
        verbFormRules.put("doesn't got", "doesn't have");
        verbFormRules.put("ain't got", "don't have");
        verbFormRules.put("should of", "should have");
        verbFormRules.put("could of", "could have");
        verbFormRules.put("would of", "would have");
        verbFormRules.put("might of", "might have");

        // Business verb forms
        verbFormRules.put("contract was expired", "contract has expired");
        verbFormRules.put("payment was processed", "payment has been processed");
        verbFormRules.put("account was created", "account has been created");
        verbFormRules.put("invoice was generated", "invoice has been generated");
    }

    /**
     * Initialize pluralization rules
     */
    private void initializePluralizationRules() {
        pluralizationRules = new HashMap<>();

        // Regular pluralization
        pluralizationRules.put("contract", "contracts");
        pluralizationRules.put("customer", "customers");
        pluralizationRules.put("account", "accounts");
        pluralizationRules.put("invoice", "invoices");
        pluralizationRules.put("payment", "payments");
        pluralizationRules.put("document", "documents");
        pluralizationRules.put("record", "records");
        pluralizationRules.put("report", "reports");
        pluralizationRules.put("project", "projects");
        pluralizationRules.put("manager", "managers");

        // Words ending in 'y'
        pluralizationRules.put("company", "companies");
        pluralizationRules.put("category", "categories");
        pluralizationRules.put("query", "queries");
        pluralizationRules.put("entry", "entries");
        pluralizationRules.put("policy", "policies");

        // Words ending in 's', 'x', 'z', 'ch', 'sh'
        pluralizationRules.put("business", "businesses");
        pluralizationRules.put("address", "addresses");
        pluralizationRules.put("process", "processes");
        pluralizationRules.put("tax", "taxes");
        pluralizationRules.put("branch", "branches");
        pluralizationRules.put("approach", "approaches");
    }

    /**
     * Initialize irregular verb and plural forms
     */
    private void initializeIrregularForms() {
        irregularVerbs = new HashSet<>();
        irregularVerbs.addAll(Arrays.asList("be", "have", "do", "say", "get", "make", "go", "know", "take", "see",
                                            "come", "think", "look", "want", "give", "use", "find", "tell", "ask",
                                            "work", "seem", "feel", "try", "leave", "call", "good", "new", "first",
                                            "last", "long", "great", "little", "own", "other", "old", "right", "big",
                                            "high", "different", "small", "large", "next", "early", "young",
                                            "important", "few", "public", "bad", "same", "able"));

        irregularPlurals = new HashSet<>();
        irregularPlurals.addAll(Arrays.asList("child:children", "person:people", "man:men", "woman:women",
                                              "tooth:teeth", "foot:feet", "mouse:mice", "goose:geese",
                                              "analysis:analyses", "basis:bases", "crisis:crises", "datum:data",
                                              "phenomenon:phenomena", "criterion:criteria"));
    }

    /**
     * Initialize common grammar error corrections
     */
    private void initializeCommonGrammarErrors() {
        commonGrammarErrors = new HashMap<>();

        // Common mistakes
        commonGrammarErrors.put("your welcome", "you're welcome");
        commonGrammarErrors.put("its a", "it's a");
        commonGrammarErrors.put("there contract", "their contract");
        commonGrammarErrors.put("they're contract", "their contract");
        commonGrammarErrors.put("your contract", "your contract"); // This is actually correct
        commonGrammarErrors.put("you're contract", "your contract");
        commonGrammarErrors.put("who's contract", "whose contract");
        commonGrammarErrors.put("loose the contract", "lose the contract");
        commonGrammarErrors.put("affect the payment", "affect the payment"); // Correct
        commonGrammarErrors.put("effect the payment", "affect the payment");

        // Business-specific errors
        commonGrammarErrors.put("less contracts", "fewer contracts");
        commonGrammarErrors.put("amount of contracts", "number of contracts");
        commonGrammarErrors.put("less customers", "fewer customers");
        commonGrammarErrors.put("amount of customers", "number of customers");
        commonGrammarErrors.put("between you and I", "between you and me");
        commonGrammarErrors.put("myself will handle", "I will handle");
        commonGrammarErrors.put("irregardless", "regardless");
        commonGrammarErrors.put("could care less", "couldn't care less");

        // Redundancy corrections
        commonGrammarErrors.put("advance planning", "planning");
        commonGrammarErrors.put("future plans", "plans");
        commonGrammarErrors.put("past history", "history");
        commonGrammarErrors.put("end result", "result");
        commonGrammarErrors.put("final outcome", "outcome");
        commonGrammarErrors.put("close proximity", "proximity");
        commonGrammarErrors.put("completely finished", "finished");
    }

    /**
     * Initialize business-specific grammar rules
     */
    private void initializeBusinessGrammar() {
        businessPhraseCorrections = new HashMap<>();

        // Formal business language
        businessPhraseCorrections.put("gonna", "going to");
        businessPhraseCorrections.put("wanna", "want to");
        businessPhraseCorrections.put("gotta", "have to");
        businessPhraseCorrections.put("kinda", "kind of");
        businessPhraseCorrections.put("sorta", "sort of");
        businessPhraseCorrections.put("dunno", "don't know");
        businessPhraseCorrections.put("yeah", "yes");
        businessPhraseCorrections.put("nope", "no");
        businessPhraseCorrections.put("ok", "okay");
        businessPhraseCorrections.put("thru", "through");
        businessPhraseCorrections.put("u", "you");
        businessPhraseCorrections.put("ur", "your");
        businessPhraseCorrections.put("r", "are");
        businessPhraseCorrections.put("n", "and");
        businessPhraseCorrections.put("w/", "with");
        businessPhraseCorrections.put("w/o", "without");
        businessPhraseCorrections.put("b4", "before");
        businessPhraseCorrections.put("2", "to");
        businessPhraseCorrections.put("4", "for");

        // Business phrase improvements
        businessPhraseCorrections.put("as per", "according to");
        businessPhraseCorrections.put("at this point in time", "now");
        businessPhraseCorrections.put("due to the fact that", "because");
        businessPhraseCorrections.put("in order to", "to");
        businessPhraseCorrections.put("for the purpose of", "to");
        businessPhraseCorrections.put("with regard to", "regarding");
        businessPhraseCorrections.put("in regard to", "regarding");
        businessPhraseCorrections.put("with respect to", "regarding");
        businessPhraseCorrections.put("in the event that", "if");
        businessPhraseCorrections.put("in the near future", "soon");
        businessPhraseCorrections.put("at the present time", "now");
        businessPhraseCorrections.put("please be advised", "please note");
        businessPhraseCorrections.put("please don't hesitate", "please");

        // Initialize formal business terms
        formalBusinessTerms = new HashSet<>();
        formalBusinessTerms.addAll(Arrays.asList("contract", "agreement", "customer", "client", "account", "invoice",
                                                 "payment", "transaction", "business", "company", "organization",
                                                 "department", "management", "professional", "service", "product",
                                                 "project", "process", "procedure", "policy", "strategy", "objective",
                                                 "requirement", "specification", "documentation", "report", "analysis",
                                                 "evaluation", "assessment", "recommendation", "proposal", "solution",
                                                 "implementation", "development", "maintenance", "support", "training",
                                                 "consultation", "collaboration", "partnership", "relationship",
                                                 "communication", "presentation", "meeting", "conference", "discussion",
                                                 "negotiation", "decision", "approval", "authorization", "compliance",
                                                 "regulation", "standard", "quality", "performance", "efficiency",
                                                 "productivity", "profitability", "revenue", "expense", "budget",
                                                 "forecast", "planning", "scheduling", "deadline", "milestone",
                                                 "deliverable", "outcome", "result", "success", "achievement"));

        // Contract language rules
        contractLanguageRules = new HashMap<>();
        contractLanguageRules.put("party of the first part", "first party");
        contractLanguageRules.put("party of the second part", "second party");
        contractLanguageRules.put("whereas", "given that");
        contractLanguageRules.put("heretofore", "previously");
        contractLanguageRules.put("hereinafter", "from now on");
        contractLanguageRules.put("aforementioned", "mentioned above");
        contractLanguageRules.put("pursuant to", "according to");
        contractLanguageRules.put("notwithstanding", "despite");
        contractLanguageRules.put("in lieu of", "instead of");
        contractLanguageRules.put("prior to", "before");
        contractLanguageRules.put("subsequent to", "after");
        contractLanguageRules.put("in accordance with", "according to");
    }

    /**
     * Initialize comprehensive grammar rules
     */
    private void initializeGrammarRules() {
        grammarRules = new ArrayList<>();

        // Subject-verb agreement rules
        grammarRules.add(new GrammarRule("SUBJECT_VERB_AGREEMENT", "Check subject-verb agreement",
                                         Pattern.compile("\\b(\\w+)\\s+(is|are|was|were|have|has|do|does)\\b",
                                                         Pattern.CASE_INSENSITIVE), this::checkSubjectVerbAgreement));

        // Article usage rules
        grammarRules.add(new GrammarRule("ARTICLE_USAGE", "Check article usage (a, an, the)",
                                         Pattern.compile("\\b(a|an)\\s+(\\w+)\\b", Pattern.CASE_INSENSITIVE),
                                         this::checkArticleUsage));

        // Preposition rules
        grammarRules.add(new GrammarRule("PREPOSITION_USAGE", "Check preposition usage",
                                         Pattern.compile("\\b(\\w+)\\s+(in|on|at|by|for|with|from|to|of)\\s+(\\w+)\\b",
                                                         Pattern.CASE_INSENSITIVE), this::checkPrepositionUsage));

        // Double negative rules
        grammarRules.add(new GrammarRule("DOUBLE_NEGATIVE", "Check for double negatives",
                                         Pattern.compile("\\b(don't|doesn't|didn't|won't|wouldn't|can't|couldn't|shouldn't)\\s+\\w*\\s*(no|not|nothing|nobody|nowhere|never)\\b",
                                                         Pattern.CASE_INSENSITIVE), this::checkDoubleNegative));

        // Sentence fragment rules
        grammarRules.add(new GrammarRule("SENTENCE_FRAGMENT", "Check for sentence fragments",
                                         Pattern.compile("^[A-Z][^.!?]*[.!?]$"), this::checkSentenceFragment));

        // Run-on sentence rules
        grammarRules.add(new GrammarRule("RUN_ON_SENTENCE", "Check for run-on sentences",
                                         Pattern.compile("^[^.!?]{100,}[.!?]$"), this::checkRunOnSentence));
    }

    /**
     * Initialize sentence pattern corrections
     */
    private void initializeSentencePatterns() {
        sentencePatterns = new HashMap<>();

        // Question patterns
        sentencePatterns.put("what is contract", "What is the contract?");
        sentencePatterns.put("where is customer", "Where is the customer?");
        sentencePatterns.put("when is payment", "When is the payment?");
        sentencePatterns.put("how is account", "How is the account?");
        sentencePatterns.put("who is manager", "Who is the manager?");

        // Command patterns
        sentencePatterns.put("show me contract", "Show me the contract.");
        sentencePatterns.put("find customer", "Find the customer.");
        sentencePatterns.put("get payment", "Get the payment.");
        sentencePatterns.put("update account", "Update the account.");
        sentencePatterns.put("delete record", "Delete the record.");

        // Statement patterns
        sentencePatterns.put("contract is active", "The contract is active.");
        sentencePatterns.put("customer is new", "The customer is new.");
        sentencePatterns.put("payment is pending", "The payment is pending.");
        sentencePatterns.put("account is closed", "The account is closed.");
        sentencePatterns.put("invoice is overdue", "The invoice is overdue.");
    }

    /**
     * Main method to enforce grammar rules on input text
     */
    public String enforceGrammar(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String result = input;

        // Apply business phrase corrections
        result = applyBusinessPhraseCorrections(result);

        // Apply common grammar error corrections
        result = applyCommonGrammarCorrections(result);

        // Apply subject-verb agreement corrections
        result = applySubjectVerbAgreement(result);

        // Apply article corrections
        result = applyArticleCorrections(result);

        // Apply preposition corrections
        result = applyPrepositionCorrections(result);

        // Apply verb form corrections
        result = applyVerbFormCorrections(result);

        // Apply sentence pattern corrections
        result = applySentencePatternCorrections(result);

        // Apply capitalization rules
        result = applyCapitalizationRules(result);

        // Apply punctuation rules
        result = applyPunctuationRules(result);

        return result.trim();
    }

    /**
     * Apply business phrase corrections
     */
    private String applyBusinessPhraseCorrections(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : businessPhraseCorrections.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Apply common grammar error corrections
     */
    private String applyCommonGrammarCorrections(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : commonGrammarErrors.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Apply subject-verb agreement corrections
     */
    private String applySubjectVerbAgreement(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : subjectVerbAgreementRules.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Apply article corrections
     */
    private String applyArticleCorrections(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : articleRules.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        // Additional a/an corrections based on vowel sounds
        result = result.replaceAll("\\ba ([aeiouAEIOU])", "an $1");
        result = result.replaceAll("\\ban ([^aeiouAEIOU])", "a $1");

        return result;
    }

    /**
     * Apply preposition corrections
     */
    private String applyPrepositionCorrections(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : prepositionRules.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Apply verb form corrections
     */
    private String applyVerbFormCorrections(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : verbFormRules.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Apply sentence pattern corrections
     */
    private String applySentencePatternCorrections(String text) {
        String result = text;

        for (Map.Entry<String, String> entry : sentencePatterns.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Apply capitalization rules
     */
    private String applyCapitalizationRules(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] sentences = SENTENCE_PATTERN.split(text);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty()) {
                // Capitalize first letter of sentence
                sentence =
                    Character.toUpperCase(sentence.charAt(0)) + (sentence.length() > 1 ? sentence.substring(1) : "");

                // Capitalize proper nouns and business terms
                sentence = capitalizeProperNouns(sentence);

                result.append(sentence);

                // Add appropriate punctuation
                if (i < sentences.length - 1) {
                    if (isQuestion(sentence)) {
                        result.append("? ");
                    } else if (isExclamation(sentence)) {
                        result.append("! ");
                    } else {
                        result.append(". ");
                    }
                }
            }
        }

        return result.toString();
    }

    /**
     * Capitalize proper nouns and business terms
     */
    private String capitalizeProperNouns(String sentence) {
        String[] words = sentence.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String cleanWord = word.replaceAll("[^a-zA-Z]", "").toLowerCase();

            // Capitalize business terms that should be capitalized
            if (shouldCapitalize(cleanWord, i == 0)) {
                word = capitalizeWord(word);
            }

            result.append(word);
            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     * Check if a word should be capitalized
     */
    private boolean shouldCapitalize(String word, boolean isFirstWord) {
        if (isFirstWord) {
            return true;
        }

        // Capitalize specific business terms
        Set<String> alwaysCapitalize =
            new HashSet<>(Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
                                        "january", "february", "march", "april", "may", "june", "july", "august",
                                        "september", "october", "november", "december", "mr", "mrs", "ms", "dr", "prof",
                                        "ceo", "cfo", "cto", "vp", "president", "manager", "director", "supervisor",
                                        "coordinator", "administrator", "sql", "api", "url", "http", "https", "xml",
                                        "json", "csv", "pdf", "microsoft", "google", "apple", "amazon", "oracle",
                                        "salesforce", "contract", "invoice", "account", "customer", "client",
                                        "project"));

        return alwaysCapitalize.contains(word.toLowerCase());
    }

    /**
     * Capitalize a word while preserving punctuation
     */
    private String capitalizeWord(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        StringBuilder result = new StringBuilder();
        boolean foundLetter = false;

        for (char c : word.toCharArray()) {
            if (Character.isLetter(c) && !foundLetter) {
                result.append(Character.toUpperCase(c));
                foundLetter = true;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Apply punctuation rules
     */
    private String applyPunctuationRules(String text) {
        String result = text;

        // Fix spacing around punctuation
        result = result.replaceAll("\\s+([.!?,:;])", "$1");
        result = result.replaceAll("([.!?])([A-Z])", "$1 $2");
        result = result.replaceAll("([,:;])([A-Za-z])", "$1 $2");

        // Fix multiple punctuation marks
        result = result.replaceAll("[.]{2,}", ".");
        result = result.replaceAll("[!]{2,}", "!");
        result = result.replaceAll("[?]{2,}", "?");

        // Fix quotation marks
        result = result.replaceAll("\\s*\"\\s*", "\"");
        result = result.replaceAll("\\s*'\\s*", "'");

        // Ensure sentences end with punctuation
        result = result.replaceAll("([a-zA-Z])\\s*$", "$1.");

        return result;
    }

    /**
     * Check if sentence is a question
     */
    private boolean isQuestion(String sentence) {
        String lower = sentence.toLowerCase().trim();
        return lower.startsWith("what") || lower.startsWith("where") || lower.startsWith("when") ||
               lower.startsWith("why") || lower.startsWith("who") || lower.startsWith("how") ||
               lower.startsWith("is") || lower.startsWith("are") || lower.startsWith("do") ||
               lower.startsWith("does") || lower.startsWith("did") || lower.startsWith("will") ||
               lower.startsWith("would") || lower.startsWith("can") || lower.startsWith("could") ||
               lower.startsWith("should");
    }

    /**
     * Check if sentence is an exclamation
     */
    private boolean isExclamation(String sentence) {
        String lower = sentence.toLowerCase().trim();
        return lower.startsWith("wow") || lower.startsWith("great") || lower.startsWith("excellent") ||
               lower.startsWith("amazing") || lower.startsWith("fantastic") || lower.contains("!");
    }

    /**
     * Grammar rule checking methods
     */
    private GrammarSuggestion checkSubjectVerbAgreement(String text, Matcher matcher) {
        String subject = matcher.group(1);
        String verb = matcher.group(2);
        String fullMatch = matcher.group(0);

        // Check if subject is singular or plural
        boolean subjectIsPlural = isPlural(subject);
        boolean verbIsPlural = isPluralVerb(verb);

        if (subjectIsPlural != verbIsPlural) {
            String correction = subjectVerbAgreementRules.get(fullMatch.toLowerCase());
            if (correction != null) {
                return new GrammarSuggestion(fullMatch, correction, "Subject-verb agreement error", 0.9);
            }
        }

        return null;
    }

    private GrammarSuggestion checkArticleUsage(String text, Matcher matcher) {
        String article = matcher.group(1);
        String word = matcher.group(2);
        String fullMatch = matcher.group(0);

        boolean startsWithVowelSound = startsWithVowelSound(word);
        boolean isCorrectArticle =
            (article.equalsIgnoreCase("an") && startsWithVowelSound) ||
            (article.equalsIgnoreCase("a") && !startsWithVowelSound);

        if (!isCorrectArticle) {
            String correctArticle = startsWithVowelSound ? "an" : "a";
            String correction = correctArticle + " " + word;

            return new GrammarSuggestion(fullMatch, correction, "Incorrect article usage", 0.85);
        }

        return null;
    }

    private GrammarSuggestion checkPrepositionUsage(String text, Matcher matcher) {
        String fullMatch = matcher.group(0);
        String correction = prepositionRules.get(fullMatch.toLowerCase());

        if (correction != null) {
            return new GrammarSuggestion(fullMatch, correction, "Incorrect preposition usage", 0.8);
        }

        return null;
    }

    private GrammarSuggestion checkDoubleNegative(String text, Matcher matcher) {
        String fullMatch = matcher.group(0);

        // Simple double negative correction
        String correction = fullMatch.replaceAll("\\b(no|not|nothing|nobody|nowhere|never)\\b", "");
        correction = correction.replaceAll("\\s+", " ").trim();

        return new GrammarSuggestion(fullMatch, correction, "Double negative detected", 0.75);
    }

    private GrammarSuggestion checkSentenceFragment(String text, Matcher matcher) {
        String sentence = matcher.group(0);

        // Check if sentence has a subject and predicate
        if (!hasSubjectAndPredicate(sentence)) {
            return new GrammarSuggestion(sentence, "Consider adding a subject or predicate to complete the sentence",
                                         "Possible sentence fragment", 0.6);
        }

        return null;
    }

    private GrammarSuggestion checkRunOnSentence(String text, Matcher matcher) {
        String sentence = matcher.group(0);

        // Count conjunctions and suggest breaking up
        long conjunctionCount = Arrays.stream(sentence.split("\\s+"))
                                      .filter(word -> word.matches("(?i)\\b(and|but|or|so|yet|for|nor)\\b"))
                                      .count();

        if (conjunctionCount > 2) {
            return new GrammarSuggestion(sentence, "Consider breaking this into multiple sentences",
                                         "Possible run-on sentence", 0.65);
        }

        return null;
    }

    /**
     * Helper methods for grammar checking
     */
    private boolean isPlural(String word) {
        String lower = word.toLowerCase();

        // Check irregular plurals
        for (String irregular : irregularPlurals) {
            String[] parts = irregular.split(":");
            if (parts.length == 2 && parts[1].equals(lower)) {
                return true;
            }
        }

        // Check regular plural patterns
        return lower.endsWith("s") && !lower.endsWith("ss") && !lower.endsWith("us") && !lower.endsWith("is");
    }

    private boolean isPluralVerb(String verb) {
        String lower = verb.toLowerCase();
        return lower.equals("are") || lower.equals("were") || lower.equals("have") || lower.equals("do");
    }

    private boolean startsWithVowelSound(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        String lower = word.toLowerCase();

        // Special cases
        if (lower.startsWith("uni") || lower.startsWith("eu") || lower.startsWith("one") || lower.startsWith("use")) {
            return false;
        }

        if (lower.startsWith("hour") || lower.startsWith("honest") || lower.startsWith("honor")) {
            return true;
        }

        // General vowel check
        return "aeiou".indexOf(lower.charAt(0)) != -1;
    }

    private boolean hasSubjectAndPredicate(String sentence) {
        // Simple check for subject and predicate
        String[] words = sentence.toLowerCase().split("\\s+");

        boolean hasSubject = false;
        boolean hasPredicate = false;

        for (String word : words) {
            // Check for common subjects
            if (word.matches("\\b(i|you|he|she|it|we|they|this|that|contract|customer|account|invoice|payment)\\b")) {
                hasSubject = true;
            }

            // Check for verbs (predicates)
            if (word.matches("\\b(is|are|was|were|have|has|had|do|does|did|will|would|can|could|should|shall|may|might|get|got|make|made|go|went|come|came|see|saw|know|knew|think|thought|want|wanted|need|needed|show|find|update|delete|create|process)\\b")) {
                hasPredicate = true;
            }
        }

        return hasSubject && hasPredicate;
    }

    /**
     * Get grammar suggestions for text
     */
    public List<GrammarSuggestion> getGrammarSuggestions(String text) {
        List<GrammarSuggestion> suggestions = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return suggestions;
        }

        for (GrammarRule rule : grammarRules) {
            Matcher matcher = rule.getPattern().matcher(text);
            while (matcher.find()) {
                GrammarSuggestion suggestion = rule.getChecker().apply(text, matcher);
                if (suggestion != null && suggestion.getConfidence() >= GRAMMAR_CONFIDENCE_THRESHOLD) {
                    suggestions.add(suggestion);
                }
            }
        }

        return suggestions.stream()
                          .sorted((s1, s2) -> Double.compare(s2.getConfidence(), s1.getConfidence()))
                          .limit(MAX_SUGGESTIONS)
                          .collect(Collectors.toList());
    }

    /**
     * Check if text needs grammar correction
     */
    public boolean needsGrammarCorrection(String text) {
        return !getGrammarSuggestions(text).isEmpty();
    }

    /**
     * Get grammar correction confidence
     */
    public double getGrammarConfidence(String original, String corrected) {
        if (original == null || corrected == null || original.equals(corrected)) {
            return 1.0;
        }

        List<GrammarSuggestion> originalSuggestions = getGrammarSuggestions(original);
        List<GrammarSuggestion> correctedSuggestions = getGrammarSuggestions(corrected);

        if (originalSuggestions.isEmpty()) {
            return 1.0; // No grammar issues found
        }

        if (correctedSuggestions.size() < originalSuggestions.size()) {
            return 0.9; // Fewer grammar issues after correction
        }

        return 0.7; // Some improvement
    }

    /**
     * Convert text to formal business language
     */
    public String toFormalBusinessLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String result = text;

        // Apply business phrase corrections
        result = applyBusinessPhraseCorrections(result);

        // Apply contract language simplification if needed
        for (Map.Entry<String, String> entry : contractLanguageRules.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        // Ensure proper capitalization and punctuation
        result = applyCapitalizationRules(result);
        result = applyPunctuationRules(result);

        return result;
    }

    /**
     * Get grammar statistics
     */
    public GrammarStats getGrammarStats() {
        return new GrammarStats(subjectVerbAgreementRules.size(), articleRules.size(), prepositionRules.size(),
                                verbFormRules.size(), commonGrammarErrors.size(), businessPhraseCorrections.size(),
                                grammarRules.size());
    }

    /**
     * Inner class for grammar rules
     */
    private static class GrammarRule {
        private final String name;
        private final String description;
        private final Pattern pattern;
        private final java.util.function.BiFunction<String, Matcher, GrammarSuggestion> checker;

        public GrammarRule(String name, String description, Pattern pattern,
                           java.util.function.BiFunction<String, Matcher, GrammarSuggestion> checker) {
            this.name = name;
            this.description = description;
            this.pattern = pattern;
            this.checker = checker;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public java.util.function.BiFunction<String, Matcher, GrammarSuggestion> getChecker() {
            return checker;
        }
    }

    /**
     * Inner class for grammar suggestions
     */
    public static class GrammarSuggestion {
        private final String original;
        private final String suggestion;
        private final String reason;
        private final double confidence;

        public GrammarSuggestion(String original, String suggestion, String reason, double confidence) {
            this.original = original;
            this.suggestion = suggestion;
            this.reason = reason;
            this.confidence = confidence;
        }

        public String getOriginal() {
            return original;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public String getReason() {
            return reason;
        }

        public double getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return String.format("GrammarSuggestion{original='%s', suggestion='%s', reason='%s', confidence=%.2f}",
                                 original, suggestion, reason, confidence);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            GrammarSuggestion that = (GrammarSuggestion) obj;
            return Objects.equals(original, that.original) && Objects.equals(suggestion, that.suggestion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(original, suggestion);
        }
    }

    /**
     * Inner class for grammar statistics
     */
    public static class GrammarStats {
        private final int subjectVerbRules;
        private final int articleRules;
        private final int prepositionRules;
        private final int verbFormRules;
        private final int commonErrorRules;
        private final int businessPhraseRules;
        private final int totalGrammarRules;

        public GrammarStats(int subjectVerbRules, int articleRules, int prepositionRules, int verbFormRules,
                            int commonErrorRules, int businessPhraseRules, int totalGrammarRules) {
            this.subjectVerbRules = subjectVerbRules;
            this.articleRules = articleRules;
            this.prepositionRules = prepositionRules;
            this.verbFormRules = verbFormRules;
            this.commonErrorRules = commonErrorRules;
            this.businessPhraseRules = businessPhraseRules;
            this.totalGrammarRules = totalGrammarRules;
        }

        public int getSubjectVerbRules() {
            return subjectVerbRules;
        }

        public int getArticleRules() {
            return articleRules;
        }

        public int getPrepositionRules() {
            return prepositionRules;
        }

        public int getVerbFormRules() {
            return verbFormRules;
        }

        public int getCommonErrorRules() {
            return commonErrorRules;
        }

        public int getBusinessPhraseRules() {
            return businessPhraseRules;
        }

        public int getTotalGrammarRules() {
            return totalGrammarRules;
        }

        @Override
        public String toString() {
            return String.format("GrammarStats{subjectVerb=%d, articles=%d, prepositions=%d, verbForms=%d, commonErrors=%d, businessPhrases=%d, total=%d}",
                                 subjectVerbRules, articleRules, prepositionRules, verbFormRules, commonErrorRules,
                                 businessPhraseRules, totalGrammarRules);
        }
    }

    /**
     * Advanced grammar checking methods
     */

    /**
     * Check for passive voice and suggest active voice alternatives
     */
    public String convertPassiveToActive(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String result = text;

        // Common passive voice patterns
        Map<String, String> passiveToActive = new HashMap<>();
        passiveToActive.put("was created by", "created");
        passiveToActive.put("was processed by", "processed");
        passiveToActive.put("was updated by", "updated");
        passiveToActive.put("was deleted by", "deleted");
        passiveToActive.put("was generated by", "generated");
        passiveToActive.put("was sent by", "sent");
        passiveToActive.put("was received by", "received");
        passiveToActive.put("was approved by", "approved");
        passiveToActive.put("was rejected by", "rejected");
        passiveToActive.put("was completed by", "completed");
        passiveToActive.put("is being processed", "is processing");
        passiveToActive.put("are being reviewed", "are reviewing");
        passiveToActive.put("will be handled", "will handle");
        passiveToActive.put("can be found", "can find");
        passiveToActive.put("should be considered", "should consider");

        for (Map.Entry<String, String> entry : passiveToActive.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Check for wordiness and suggest concise alternatives
     */
    public String makeTextConcise(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String result = text;

        // Wordy phrases to concise alternatives
        Map<String, String> wordyToConcise = new HashMap<>();
        wordyToConcise.put("in order to", "to");
        wordyToConcise.put("for the purpose of", "to");
        wordyToConcise.put("with regard to", "regarding");
        wordyToConcise.put("in regard to", "regarding");
        wordyToConcise.put("with respect to", "regarding");
        wordyToConcise.put("in connection with", "regarding");
        wordyToConcise.put("in relation to", "regarding");
        wordyToConcise.put("as a result of", "because of");
        wordyToConcise.put("due to the fact that", "because");
        wordyToConcise.put("in spite of the fact that", "although");
        wordyToConcise.put("despite the fact that", "although");
        wordyToConcise.put("in the event that", "if");
        wordyToConcise.put("in the case that", "if");
        wordyToConcise.put("under circumstances in which", "when");
        wordyToConcise.put("at this point in time", "now");
        wordyToConcise.put("at the present time", "now");
        wordyToConcise.put("in the near future", "soon");
        wordyToConcise.put("at an early date", "soon");
        wordyToConcise.put("in the final analysis", "finally");
        wordyToConcise.put("it is important to note that", "note that");
        wordyToConcise.put("please be advised that", "please note");
        wordyToConcise.put("please don't hesitate to", "please");
        wordyToConcise.put("we would like to take this opportunity to", "we");
        wordyToConcise.put("I am writing to inform you that", "");
        wordyToConcise.put("this is to inform you that", "");

        for (Map.Entry<String, String> entry : wordyToConcise.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        // Remove redundant words
        result = result.replaceAll("\\s+", " "); // Multiple spaces
        result = result.replaceAll("\\b(very|really|quite|rather|somewhat|fairly)\\s+", ""); // Weak intensifiers
        result = result.replaceAll("\\b(basically|essentially|actually|literally)\\s+", ""); // Filler words

        return result.trim();
    }

    /**
     * Check for consistency in tense usage
     */
    public String ensureTenseConsistency(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String result = text;

        // Detect predominant tense in the text
        String predominantTense = detectPredominantTense(text);

        // Apply tense consistency based on predominant tense
        if ("present".equals(predominantTense)) {
            result = convertToPresentTense(result);
        } else if ("past".equals(predominantTense)) {
            result = convertToPastTense(result);
        } else if ("future".equals(predominantTense)) {
            result = convertToFutureTense(result);
        }

        return result;
    }

    /**
     * Detect the predominant tense in text
     */
    private String detectPredominantTense(String text) {
        String[] words = text.toLowerCase().split("\\s+");

        int presentCount = 0;
        int pastCount = 0;
        int futureCount = 0;

        Set<String> presentIndicators =
            new HashSet<>(Arrays.asList("is", "are", "am", "have", "has", "do", "does", "get", "gets", "make", "makes",
                                        "go", "goes", "come", "comes", "see", "sees", "know", "knows", "think",
                                        "thinks", "want", "wants", "need", "needs"));
        Set<String> pastIndicators =
            new HashSet<>(Arrays.asList("was", "were", "had", "did", "got", "made", "went", "came", "saw", "knew",
                                        "thought", "wanted", "needed", "created", "processed", "updated", "deleted",
                                        "completed", "approved", "rejected"));
        Set<String> futureIndicators =
            new HashSet<>(Arrays.asList("will", "shall", "going", "gonna", "planning", "intending", "expecting",
                                        "anticipating"));

        for (String word : words) {
            if (presentIndicators.contains(word)) {
                presentCount++;
            } else if (pastIndicators.contains(word)) {
                pastCount++;
            } else if (futureIndicators.contains(word)) {
                futureCount++;
            }
        }

        if (presentCount >= pastCount && presentCount >= futureCount) {
            return "present";
        } else if (pastCount >= futureCount) {
            return "past";
        } else {
            return "future";
        }
    }

    /**
     * Convert text to present tense
     */
    private String convertToPresentTense(String text) {
        String result = text;

        Map<String, String> pastToPresent = new HashMap<>();
        pastToPresent.put("was", "is");
        pastToPresent.put("were", "are");
        pastToPresent.put("had", "have");
        pastToPresent.put("did", "do");
        pastToPresent.put("got", "get");
        pastToPresent.put("made", "make");
        pastToPresent.put("went", "go");
        pastToPresent.put("came", "come");
        pastToPresent.put("saw", "see");
        pastToPresent.put("knew", "know");
        pastToPresent.put("thought", "think");
        pastToPresent.put("wanted", "want");
        pastToPresent.put("needed", "need");
        pastToPresent.put("created", "create");
        pastToPresent.put("processed", "process");
        pastToPresent.put("updated", "update");
        pastToPresent.put("deleted", "delete");
        pastToPresent.put("completed", "complete");
        pastToPresent.put("approved", "approve");
        pastToPresent.put("rejected", "reject");

        for (Map.Entry<String, String> entry : pastToPresent.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Convert text to past tense
     */
    private String convertToPastTense(String text) {
        String result = text;

        Map<String, String> presentToPast = new HashMap<>();
        presentToPast.put("is", "was");
        presentToPast.put("are", "were");
        presentToPast.put("have", "had");
        presentToPast.put("has", "had");
        presentToPast.put("do", "did");
        presentToPast.put("does", "did");
        presentToPast.put("get", "got");
        presentToPast.put("gets", "got");
        presentToPast.put("make", "made");
        presentToPast.put("makes", "made");
        presentToPast.put("go", "went");
        presentToPast.put("goes", "went");
        presentToPast.put("come", "came");
        presentToPast.put("comes", "came");
        presentToPast.put("see", "saw");
        presentToPast.put("sees", "saw");
        presentToPast.put("know", "knew");
        presentToPast.put("knows", "knew");
        presentToPast.put("think", "thought");
        presentToPast.put("thinks", "thought");
        presentToPast.put("want", "wanted");
        presentToPast.put("wants", "wanted");
        presentToPast.put("need", "needed");
        presentToPast.put("needs", "needed");
        presentToPast.put("create", "created");
        presentToPast.put("creates", "created");
        presentToPast.put("process", "processed");
        presentToPast.put("processes", "processed");
        presentToPast.put("update", "updated");
        presentToPast.put("updates", "updated");
        presentToPast.put("delete", "deleted");
        presentToPast.put("deletes", "deleted");
        presentToPast.put("complete", "completed");
        presentToPast.put("completes", "completed");
        presentToPast.put("approve", "approved");
        presentToPast.put("approves", "approved");
        presentToPast.put("reject", "rejected");
        presentToPast.put("rejects", "rejected");

        for (Map.Entry<String, String> entry : presentToPast.entrySet()) {
            String pattern = "\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll("(?i)" + pattern, entry.getValue());
        }

        return result;
    }

    /**
     * Convert text to future tense
     */
    private String convertToFutureTense(String text) {
        String result = text;

        // Add "will" before present tense verbs
        String[] presentVerbs = {
            "create", "process", "update", "delete", "complete", "approve", "reject", "send", "receive", "generate",
            "handle", "manage", "review", "analyze", "evaluate", "implement", "develop", "maintain", "support", "train"
        };

        for (String verb : presentVerbs) {
            String pattern = "\\b(?<!will\\s)" + Pattern.quote(verb) + "\\b";
            result = result.replaceAll("(?i)" + pattern, "will " + verb);
        }

        return result;
    }

    /**
     * Validate and correct sentence structure
     */
    public String correctSentenceStructure(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String[] sentences = SENTENCE_PATTERN.split(text);
        StringBuilder result = new StringBuilder();

        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                // Correct individual sentence structure
                sentence = correctIndividualSentence(sentence);
                result.append(sentence);

                // Add appropriate punctuation if missing
                if (!sentence.matches(".*[.!?]$")) {
                    if (isQuestion(sentence)) {
                        result.append("?");
                    } else if (isExclamation(sentence)) {
                        result.append("!");
                    } else {
                        result.append(".");
                    }
                }

                result.append(" ");
            }
        }

        return result.toString().trim();
    }

    /**
     * Correct individual sentence structure
     */
    private String correctIndividualSentence(String sentence) {
        String result = sentence.trim();

        // Fix common sentence structure issues
        result = fixDanglingModifiers(result);
        result = fixMisplacedModifiers(result);
        result = fixParallelStructure(result);
        result = fixSentenceFragments(result);
        result = fixRunOnSentences(result);

        return result;
    }

    /**
     * Fix dangling modifiers
     */
    private String fixDanglingModifiers(String sentence) {
        String result = sentence;

        // Common dangling modifier patterns
        Map<String, String> danglingModifierFixes = new HashMap<>();
        danglingModifierFixes.put("^(After|Before|While|When|Since)\\s+([^,]+),\\s*the\\s+(\\w+)", "$1 $2, the $3");
        danglingModifierFixes.put("^(Having|Being|Seeing|Knowing)\\s+([^,]+),\\s*the\\s+(\\w+)", "After $2, the $3");
        danglingModifierFixes.put("^(To\\s+\\w+)\\s+([^,]+),\\s*the\\s+(\\w+)", "$1 $2, the $3");

        for (Map.Entry<String, String> entry : danglingModifierFixes.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Fix misplaced modifiers
     */
    private String fixMisplacedModifiers(String sentence) {
        String result = sentence;

        // Common misplaced modifier patterns
        Map<String, String> misplacedModifierFixes = new HashMap<>();
        misplacedModifierFixes.put("\\b(only|just|nearly|almost|hardly|barely)\\s+(\\w+)\\s+(\\w+)", "$2 $1 $3");
        misplacedModifierFixes.put("\\b(contract|customer|account|invoice|payment)\\s+(that|which)\\s+is\\s+(\\w+)\\s+only",
                                   "$1 that is only $3");

        for (Map.Entry<String, String> entry : misplacedModifierFixes.entrySet()) {
            result = result.replaceAll("(?i)" + entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * Fix parallel structure issues
     */
    private String fixParallelStructure(String sentence) {
        String result = sentence;

        // Fix parallel structure in lists
        result =
            result.replaceAll("\\b(creating|processing|updating|deleting)\\s+and\\s+(create|process|update|delete)\\b",
                              "$1 and $2ing");
        result =
            result.replaceAll("\\b(create|process|update|delete)\\s+and\\s+(creating|processing|updating|deleting)\\b",
                              "$1 and $2");

        // Fix parallel structure with correlative conjunctions
        result = result.replaceAll("\\bnot only\\s+(\\w+ing)\\s+but also\\s+(\\w+)\\b", "not only $1 but also $2ing");
        result = result.replaceAll("\\beither\\s+(\\w+ing)\\s+or\\s+(\\w+)\\b", "either $1 or $2ing");
        result = result.replaceAll("\\bboth\\s+(\\w+ing)\\s+and\\s+(\\w+)\\b", "both $1 and $2ing");

        return result;
    }

    /**
     * Fix sentence fragments
     */
    private String fixSentenceFragments(String sentence) {
        String result = sentence;

        // Add subject to fragments that start with verbs
        if (result.matches("^(Show|Find|Get|Update|Delete|Create|Process|Generate|Send|Receive)\\s+.*")) {
            result = "Please " + result.toLowerCase();
        }

        // Add predicate to fragments that are just subjects
        if (result.matches("^(The\\s+)?(contract|customer|account|invoice|payment|document|record|report)\\s*\\.?$")) {
            result = result.replaceAll("\\.$", "") + " is available.";
        }

        // Fix subordinate clause fragments
        if (result.matches("^(Because|Since|Although|While|When|If|Unless|Until)\\s+.*")) {
            if (!result.contains(",")) {
                result = result + ", please contact support.";
            }
        }

        return result;
    }

    /**
     * Fix run-on sentences
     */
    private String fixRunOnSentences(String sentence) {
        String result = sentence;

        // Split comma splices
        result = result.replaceAll(",\\s+(however|therefore|furthermore|moreover|nevertheless|consequently)", ". $1,");
        result = result.replaceAll(",\\s+(and|but|or|so|yet)\\s+", ", $1 ");

        // Break up sentences with too many clauses
        if (sentence.length() > 150) {
            result = result.replaceAll("\\s+(and|but|or)\\s+", ". ");
            result =
                result.replaceAll("\\s+(however|therefore|furthermore|moreover|nevertheless|consequently)\\s+",
                                  ". $1, ");
        }

        return result;
    }

    /**
     * Advanced business writing enhancement
     */
    public String enhanceBusinessWriting(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String result = text;

        // Apply all grammar corrections
        result = enforceGrammar(result);

        // Convert to active voice
        result = convertPassiveToActive(result);

        // Make text concise
        result = makeTextConcise(result);

        // Ensure tense consistency
        result = ensureTenseConsistency(result);

        // Correct sentence structure
        result = correctSentenceStructure(result);

        // Convert to formal business language
        result = toFormalBusinessLanguage(result);

        // Final cleanup
        result = finalCleanup(result);

        return result;
    }

    /**
     * Final cleanup of text
     */
    private String finalCleanup(String text) {
        String result = text;

        // Remove extra whitespace
        result = result.replaceAll("\\s+", " ");
        result = result.replaceAll("\\s*([.!?,:;])\\s*", "$1 ");
        result = result.replaceAll("\\s+$", "");
        result = result.replaceAll("^\\s+", "");

        // Fix capitalization after punctuation
        result = result.replaceAll("([.!?])\\s+([a-z])", "$1 " + java.util
                                                                     .regex
                                                                     .Matcher
                                                                     .quoteReplacement("$2")
                                                                     .toUpperCase());

        // Ensure first letter is capitalized
        if (!result.isEmpty()) {
            result = Character.toUpperCase(result.charAt(0)) + (result.length() > 1 ? result.substring(1) : "");
        }

        // Remove duplicate punctuation
        result = result.replaceAll("([.!?])\\1+", "$1");
        result = result.replaceAll("([,:;])\\1+", "$1");

        return result;
    }

    /**
     * Batch process multiple texts
     */
    public List<String> batchEnforceGrammar(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        return texts.stream()
                    .map(this::enforceGrammar)
                    .collect(Collectors.toList());
    }

    /**
     * Get detailed grammar analysis
     */
    public GrammarAnalysis analyzeGrammar(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new GrammarAnalysis(text, new ArrayList<>(), 1.0, "No issues found");
        }

        List<GrammarSuggestion> suggestions = getGrammarSuggestions(text);
        double overallScore = calculateGrammarScore(text, suggestions);
        String summary = generateGrammarSummary(suggestions);

        return new GrammarAnalysis(text, suggestions, overallScore, summary);
    }

    /**
     * Calculate overall grammar score
     */
    private double calculateGrammarScore(String text, List<GrammarSuggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return 1.0;
        }

        int wordCount = text.split("\\s+").length;
        int issueCount = suggestions.size();

        // Base score calculation
        double baseScore = Math.max(0.0, 1.0 - (double) issueCount / wordCount * 10);

        // Adjust based on severity of issues
        double severityPenalty = suggestions.stream()
                                            .mapToDouble(s -> 1.0 - s.getConfidence())
                                            .average()
                                            .orElse(0.0);

        return Math.max(0.0, baseScore - severityPenalty * 0.5);
    }

    /**
     * Generate grammar summary
     */
    private String generateGrammarSummary(List<GrammarSuggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return "No grammar issues detected. Text appears to be well-written.";
        }

        Map<String, Long> issueTypes =
            suggestions.stream().collect(Collectors.groupingBy(GrammarSuggestion::getReason, Collectors.counting()));

        StringBuilder summary = new StringBuilder();
        summary.append("Found ")
               .append(suggestions.size())
               .append(" grammar issue(s): ");

        issueTypes.entrySet()
                  .stream()
                  .sorted(Map.Entry
                             .<String, Long>comparingByValue()
                             .reversed())
                  .forEach(entry -> summary.append(entry.getKey())
                                           .append(" (")
                                           .append(entry.getValue())
                                           .append("), "));

        // Remove trailing comma and space
        if (summary.length() > 2) {
            summary.setLength(summary.length() - 2);
        }

        return summary.toString();
    }

    /**
     * Inner class for comprehensive grammar analysis
     */
    public static class GrammarAnalysis {
        private final String originalText;
        private final List<GrammarSuggestion> suggestions;
        private final double grammarScore;
        private final String summary;

        public GrammarAnalysis(String originalText, List<GrammarSuggestion> suggestions, double grammarScore,
                               String summary) {
            this.originalText = originalText;
            this.suggestions = suggestions;
            this.grammarScore = grammarScore;
            this.summary = summary;
        }

        public String getOriginalText() {
            return originalText;
        }

        public List<GrammarSuggestion> getSuggestions() {
            return suggestions;
        }

        public double getGrammarScore() {
            return grammarScore;
        }

        public String getSummary() {
            return summary;
        }

        public boolean hasIssues() {
            return !suggestions.isEmpty();
        }

        public String getGradeLevel() {
            if (grammarScore >= 0.9)
                return "Excellent";
            if (grammarScore >= 0.8)
                return "Good";
            if (grammarScore >= 0.7)
                return "Fair";
            if (grammarScore >= 0.6)
                return "Poor";
            return "Needs Improvement";
        }

        @Override
        public String toString() {
            return String.format("GrammarAnalysis{score=%.2f, grade='%s', issues=%d, summary='%s'}", grammarScore,
                                 getGradeLevel(), suggestions.size(), summary);
        }
    }

    /**
     * Configuration and utility methods
     */

    /**
     * Update grammar rules configuration
     */
    public void updateConfiguration(Map<String, Object> config) {
        if (config == null)
            return;

        // Update confidence threshold
        if (config.containsKey("confidenceThreshold")) {
            // Note: GRAMMAR_CONFIDENCE_THRESHOLD is final, so this would require refactoring
            // to make it configurable if needed
        }

        // Update max suggestions
        if (config.containsKey("maxSuggestions")) {
            // Note: MAX_SUGGESTIONS is final, so this would require refactoring
            // to make it configurable if needed
        }

        // Add custom rules
        if (config.containsKey("customRules")) {
            @SuppressWarnings("unchecked")
            Map<String, String> customRules = (Map<String, String>) config.get("customRules");
            commonGrammarErrors.putAll(customRules);
        }

        // Add custom business phrases
        if (config.containsKey("customBusinessPhrases")) {
            @SuppressWarnings("unchecked")
            Map<String, String> customPhrases = (Map<String, String>) config.get("customBusinessPhrases");
            businessPhraseCorrections.putAll(customPhrases);
        }
    }

    /**
     * Reset to default configuration
     */
    public void resetToDefaults() {
        // Reinitialize all rules
        initializeSubjectVerbAgreement();
        initializeArticleRules();
        initializePrepositionRules();
        initializeVerbFormRules();
        initializePluralizationRules();
        initializeIrregularForms();
        initializeCommonGrammarErrors();
        initializeBusinessGrammar();
        initializeGrammarRules();
        initializeSentencePatterns();
    }

    /**
     * Export current configuration
     */
    public Map<String, Object> exportConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("subjectVerbAgreementRules", new HashMap<>(subjectVerbAgreementRules));
        config.put("articleRules", new HashMap<>(articleRules));
        config.put("prepositionRules", new HashMap<>(prepositionRules));
        config.put("verbFormRules", new HashMap<>(verbFormRules));
        config.put("commonGrammarErrors", new HashMap<>(commonGrammarErrors));
        config.put("businessPhraseCorrections", new HashMap<>(businessPhraseCorrections));
        config.put("contractLanguageRules", new HashMap<>(contractLanguageRules));
        config.put("sentencePatterns", new HashMap<>(sentencePatterns));
        config.put("formalBusinessTerms", new HashSet<>(formalBusinessTerms));
        config.put("irregularPlurals", new ArrayList<>(irregularPlurals));
        config.put("grammarRulesCount", grammarRules.size());
        config.put("confidenceThreshold", GRAMMAR_CONFIDENCE_THRESHOLD);
        config.put("maxSuggestions", MAX_SUGGESTIONS);

        return config;
    }

    /**
     * Import configuration from external source
     */
    public void importConfiguration(Map<String, Object> config) {
        if (config == null)
            return;

        try {
            // Import subject-verb agreement rules
            if (config.containsKey("subjectVerbAgreementRules")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("subjectVerbAgreementRules");
                subjectVerbAgreementRules.putAll(rules);
            }

            // Import article rules
            if (config.containsKey("articleRules")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("articleRules");
                articleRules.putAll(rules);
            }

            // Import preposition rules
            if (config.containsKey("prepositionRules")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("prepositionRules");
                prepositionRules.putAll(rules);
            }

            // Import verb form rules
            if (config.containsKey("verbFormRules")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("verbFormRules");
                verbFormRules.putAll(rules);
            }

            // Import common grammar errors
            if (config.containsKey("commonGrammarErrors")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("commonGrammarErrors");
                commonGrammarErrors.putAll(rules);
            }

            // Import business phrase corrections
            if (config.containsKey("businessPhraseCorrections")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("businessPhraseCorrections");
                businessPhraseCorrections.putAll(rules);
            }

            // Import contract language rules
            if (config.containsKey("contractLanguageRules")) {
                @SuppressWarnings("unchecked")
                Map<String, String> rules = (Map<String, String>) config.get("contractLanguageRules");
                contractLanguageRules.putAll(rules);
            }

            // Import sentence patterns
            if (config.containsKey("sentencePatterns")) {
                @SuppressWarnings("unchecked")
                Map<String, String> patterns = (Map<String, String>) config.get("sentencePatterns");
                sentencePatterns.putAll(patterns);
            }

            // Import formal business terms
            if (config.containsKey("formalBusinessTerms")) {
                @SuppressWarnings("unchecked")
                Set<String> terms = (Set<String>) config.get("formalBusinessTerms");
                formalBusinessTerms.addAll(terms);
            }

            // Import irregular plurals
            if (config.containsKey("irregularPlurals")) {
                @SuppressWarnings("unchecked")
                List<String> plurals = (List<String>) config.get("irregularPlurals");
                irregularPlurals.addAll(plurals);
            }

        } catch (ClassCastException e) {
            System.err.println("Error importing configuration: Invalid data type - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error importing configuration: " + e.getMessage());
        }
    }

    /**
     * Validate grammar enforcer configuration
     */
    public boolean validateConfiguration() {
        try {
            // Check if essential rules are present
            if (subjectVerbAgreementRules.isEmpty()) {
                System.err.println("Warning: No subject-verb agreement rules loaded");
                return false;
            }

            if (articleRules.isEmpty()) {
                System.err.println("Warning: No article rules loaded");
                return false;
            }

            if (commonGrammarErrors.isEmpty()) {
                System.err.println("Warning: No common grammar error rules loaded");
                return false;
            }

            if (businessPhraseCorrections.isEmpty()) {
                System.err.println("Warning: No business phrase corrections loaded");
                return false;
            }

            if (grammarRules.isEmpty()) {
                System.err.println("Warning: No grammar rules loaded");
                return false;
            }

            // Validate rule patterns
            for (GrammarRule rule : grammarRules) {
                if (rule.getPattern() == null || rule.getChecker() == null) {
                    System.err.println("Warning: Invalid grammar rule found: " + rule.getName());
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error validating configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get performance metrics
     */
    public GrammarPerformanceMetrics getPerformanceMetrics() {
        return new GrammarPerformanceMetrics(subjectVerbAgreementRules.size(), articleRules.size(),
                                             prepositionRules.size(), verbFormRules.size(), commonGrammarErrors.size(),
                                             businessPhraseCorrections.size(), contractLanguageRules.size(),
                                             sentencePatterns.size(), formalBusinessTerms.size(),
                                             irregularPlurals.size(), grammarRules.size());
    }

    /**
     * Inner class for performance metrics
     */
    public static class GrammarPerformanceMetrics {
        private final int subjectVerbRules;
        private final int articleRules;
        private final int prepositionRules;
        private final int verbFormRules;
        private final int commonErrorRules;
        private final int businessPhraseRules;
        private final int contractLanguageRules;
        private final int sentencePatterns;
        private final int businessTerms;
        private final int irregularPlurals;
        private final int totalGrammarRules;
        private final long timestamp;

        public GrammarPerformanceMetrics(int subjectVerbRules, int articleRules, int prepositionRules,
                                         int verbFormRules, int commonErrorRules, int businessPhraseRules,
                                         int contractLanguageRules, int sentencePatterns, int businessTerms,
                                         int irregularPlurals, int totalGrammarRules) {
            this.subjectVerbRules = subjectVerbRules;
            this.articleRules = articleRules;
            this.prepositionRules = prepositionRules;
            this.verbFormRules = verbFormRules;
            this.commonErrorRules = commonErrorRules;
            this.businessPhraseRules = businessPhraseRules;
            this.contractLanguageRules = contractLanguageRules;
            this.sentencePatterns = sentencePatterns;
            this.businessTerms = businessTerms;
            this.irregularPlurals = irregularPlurals;
            this.totalGrammarRules = totalGrammarRules;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public int getSubjectVerbRules() {
            return subjectVerbRules;
        }

        public int getArticleRules() {
            return articleRules;
        }

        public int getPrepositionRules() {
            return prepositionRules;
        }

        public int getVerbFormRules() {
            return verbFormRules;
        }

        public int getCommonErrorRules() {
            return commonErrorRules;
        }

        public int getBusinessPhraseRules() {
            return businessPhraseRules;
        }

        public int getContractLanguageRules() {
            return contractLanguageRules;
        }

        public int getSentencePatterns() {
            return sentencePatterns;
        }

        public int getBusinessTerms() {
            return businessTerms;
        }

        public int getIrregularPlurals() {
            return irregularPlurals;
        }

        public int getTotalGrammarRules() {
            return totalGrammarRules;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getTotalRules() {
            return subjectVerbRules + articleRules + prepositionRules + verbFormRules + commonErrorRules +
                   businessPhraseRules + contractLanguageRules + sentencePatterns + totalGrammarRules;
        }

        @Override
        public String toString() {
            return String.format("GrammarPerformanceMetrics{" +
                                 "subjectVerb=%d, articles=%d, prepositions=%d, verbForms=%d, " +
                                 "commonErrors=%d, businessPhrases=%d, contractLanguage=%d, " +
                                 "sentencePatterns=%d, businessTerms=%d, irregularPlurals=%d, " +
                                 "grammarRules=%d, totalRules=%d, timestamp=%d}", subjectVerbRules, articleRules,
                                 prepositionRules, verbFormRules, commonErrorRules, businessPhraseRules,
                                 contractLanguageRules, sentencePatterns, businessTerms, irregularPlurals,
                                 totalGrammarRules, getTotalRules(), timestamp);
        }
    }

    /**
     * Test grammar enforcement with sample text
     */
    public void testGrammarEnforcement() {
        System.out.println("=== Grammar Enforcer Test ===");

        String[] testSentences = {
            "the contract are ready for review", "a invoice was send to the customer", "customer have payed there bill",
            "we was processing the payment", "show me contract details", "gonna need to update the account",
            "the customer which is new", "creating and process the documents",
            "not only updating but also delete records", "Because the system is down",
            "The contract, however the customer is not available",
            "this is to inform you that the payment was processed by the system"
        };

        for (String sentence : testSentences) {
            System.out.println("\nOriginal: " + sentence);
            String corrected = enforceGrammar(sentence);
            System.out.println("Corrected: " + corrected);

            List<GrammarSuggestion> suggestions = getGrammarSuggestions(sentence);
            if (!suggestions.isEmpty()) {
                System.out.println("Suggestions:");
                suggestions.forEach(s -> System.out.println("  - " + s));
            }

            GrammarAnalysis analysis = analyzeGrammar(sentence);
            System.out.println("Analysis: " + analysis);
        }

        System.out.println("\n=== Performance Metrics ===");
        System.out.println(getPerformanceMetrics());

        System.out.println("\n=== Grammar Statistics ===");
        System.out.println(getGrammarStats());

        System.out.println("\n=== Configuration Validation ===");
        System.out.println("Configuration valid: " + validateConfiguration());
    }

    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        GrammarEnforcer enforcer = new GrammarEnforcer();

        if (args.length > 0) {
            // Process command line arguments
            String inputText = String.join(" ", args);
            System.out.println("Input: " + inputText);

            String corrected = enforcer.enforceGrammar(inputText);
            System.out.println("Corrected: " + corrected);

            String enhanced = enforcer.enhanceBusinessWriting(inputText);
            System.out.println("Enhanced: " + enhanced);

            GrammarAnalysis analysis = enforcer.analyzeGrammar(inputText);
            System.out.println("Analysis: " + analysis);
        } else {
            // Run test suite
            enforcer.testGrammarEnforcement();
        }
    }
}

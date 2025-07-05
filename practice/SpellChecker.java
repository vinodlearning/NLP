package view.practice;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpellChecker {
    private Map<String, Integer> dictionary;
    private static final int MAX_EDIT_DISTANCE = 2;
    
    // Comprehensive spell correction mappings for Contract and Parts domains
    private static final Map<String, String> COMPREHENSIVE_CORRECTIONS = new HashMap<String, String>() {{
        // Basic corrections
        put("lst", "list");
        put("shwo", "show");
        put("shw", "show");
        put("mee", "me");
        put("teh", "the");
        put("tehm", "them");
        put("wth", "with");
        put("waht", "what");
        put("whats", "what");
        put("wat", "what");
        put("hw", "how");
        put("yu", "you");
        put("chek", "check");
        put("gt", "get");
        put("giv", "give");
        put("provid", "provide");
        put("nt", "not");
        put("arnt", "aren't");
        put("becasue", "because");
        put("addedd", "added");
        put("happen", "happened");
        put("happend", "happened");
        
        // Contract domain corrections
        put("contrct", "contract");
        put("contrcts", "contracts");
        put("contarct", "contract");
        put("contarcts", "contracts");
        put("cntract", "contract");
        put("cntracts", "contracts");
        put("kontrakt", "contract");
        put("kontract", "contract");
        put("conract", "contract");
        put("contracs", "contracts");
        put("cntracts", "contracts");
        put("tracts", "contracts");
        put("numbr", "number");
        put("numer", "number");
        put("umber", "number");
        put("accunt", "account");
        put("acount", "account");
        put("custmer", "customer");
        put("cstomer", "customer");
        put("custmor", "customer");
        put("statuss", "status");
        put("statuz", "status");
        put("infro", "info");
        put("detials", "details");
        put("detals", "details");
        put("denials", "details");
        put("detalis", "details");
        put("summry", "summary");
        put("sumry", "summary");
        put("sumy", "summary");
        put("efective", "effective");
        put("creatd", "created");
        put("exipred", "expired");
        put("corprate", "corporate");
        put("oppurtunity", "opportunity");
        put("unity", "opportunity");
        put("flieds", "fields");
        put("flies", "fields");
        put("boieng", "boeing");
        put("honeywel", "honeywell");
        put("vino", "vinod");
        
        // Parts domain corrections
        put("prts", "parts");
        put("parst", "parts");
        put("partz", "parts");
        put("prduct", "product");
        put("prodcut", "product");
        put("specifcations", "specifications");
        put("specificatons", "specifications");
        put("specifcation", "specification");
        put("actve", "active");
        put("activ", "active");
        put("discontnued", "discontinued");
        put("discntinued", "discontinued");
        put("discountinued", "discontinued");
        put("datashet", "datasheet");
        put("dataheet", "datasheet");
        put("compatble", "compatible");
        put("compatable", "compatible");
        put("avalable", "available");
        put("availabe", "available");
        put("availble", "available");
        put("stok", "stock");
        put("sotck", "stock");
        put("lede", "lead");
        put("led", "lead");
        put("manufacterer", "manufacturer");
        put("manufacter", "manufacturer");
        put("manufactuer", "manufacturer");
        put("isses", "issues");
        put("issuse", "issues");
        put("deffect", "defect");
        put("defect", "defect");
        put("warrenty", "warranty");
        put("warrnty", "warranty");
        put("priod", "period");
        put("peroid", "period");
        put("faild", "failed");
        put("faield", "failed");
        put("filde", "failed");
        put("failded", "failed");
        put("validdation", "validation");
        put("validaion", "validation");
        put("vaildation", "validation");
        put("loadded", "loaded");
        put("looded", "loaded");
        put("lodded", "loaded");
        put("loadding", "loading");
        put("looding", "loading");
        put("misssing", "missing");
        put("mising", "missing");
        put("missig", "missing");
        put("rejeted", "rejected");
        put("rejectd", "rejected");
        put("successfull", "successful");
        put("succesful", "successful");
        put("sucessful", "successful");
        put("passd", "passed");
        put("pasd", "passed");
        put("passeed", "passed");
        put("pricng", "pricing");
        put("priceing", "pricing");
        put("picing", "pricing");
        put("mastr", "master");
        put("mater", "master");
        put("mastter", "master");
        put("skiped", "skipped");
        put("skippd", "skipped");
        put("durng", "during");
        put("durign", "during");
        put("whil", "while");
        put("whlie", "while");
        
        // Time-related corrections
        put("aftr", "after");
        put("befre", "before");
        put("befor", "before");
        put("btwn", "between");
        put("betwen", "between");
        put("beetween", "between");
        put("mth", "month");
        put("moth", "month");
        put("mnth", "month");
        put("yr", "year");
        put("yar", "year");
        put("yaer", "year");
        put("dat", "date");
        put("dte", "date");
        put("dae", "date");
        put("tody", "today");
        put("todya", "today");
        put("toady", "today");
        
        // Action corrections
        put("crate", "create");
        put("creat", "create");
        put("craete", "create");
        put("updat", "update");
        put("updaet", "update");
        put("delet", "delete");
        put("deleet", "delete");
        put("remo", "remove");
        put("remov", "remove");
        put("modfy", "modify");
        put("modifi", "modify");
        put("modigy", "modify");
        put("sav", "save");
        put("sve", "save");
        
        // Error type corrections
        put("erro", "error");
        put("eror", "error");
        put("erros", "errors");
        put("erors", "errors");
        put("mesage", "message");
        put("messag", "message");
        put("messaeg", "message");
        put("data", "data");
        put("dat", "data");
        put("dtata", "data");
        
        // Special case corrections for common patterns
        put("w/", "with");
        put("w", "with");
        put("b/w", "between");
        put("thru", "through");
        put("acc", "account");
        put("cont", "contract");
        put("spec", "specification");
        put("mfg", "manufacturer");
        put("mfr", "manufacturer");
        put("qty", "quantity");
        put("amt", "amount");
        put("desc", "description");
        put("req", "required");
        put("reqs", "requirements");
        put("info", "information");
        put("specs", "specifications");
        put("docs", "documents");
        put("doc", "document");
        put("ref", "reference");
        put("refs", "references");
        put("num", "number");
        put("id", "identifier");
        put("ids", "identifiers");
        put("val", "value");
        put("vals", "values");
        put("stat", "status");
        put("stats", "statistics");
        put("mgr", "manager");
        put("mgmt", "management");
        put("org", "organization");
        put("dept", "department");
        put("div", "division");
        put("co", "company");
        put("corp", "corporation");
        put("inc", "incorporated");
        put("llc", "limited_liability_company");
        put("ltd", "limited");
        put("mfg", "manufacturing");
        put("eng", "engineering");
        put("tech", "technology");
        put("sys", "system");
        put("proc", "process");
        put("proj", "project");
        put("prog", "program");
        put("dev", "development");
        put("impl", "implementation");
        put("config", "configuration");
        put("admin", "administration");
        put("maint", "maintenance");
        put("ops", "operations");
        put("svc", "service");
        put("svcs", "services");
        put("prod", "production");
        put("qual", "quality");
        put("cert", "certification");
        put("std", "standard");
        put("stds", "standards");
        put("temp", "temporary");
        put("perm", "permanent");
        put("temp", "temperature");
        put("max", "maximum");
        put("min", "minimum");
        put("avg", "average");
        put("est", "estimated");
        put("act", "actual");
        put("req", "request");
        put("res", "response");
        put("resp", "response");
        put("req", "requirement");
        put("reqs", "requirements");
        put("spec", "specification");
        put("specs", "specifications");
        put("std", "standard");
        put("stds", "standards");
        put("proc", "procedure");
        put("procs", "procedures");
        put("pol", "policy");
        put("pols", "policies");
        put("guid", "guideline");
        put("guids", "guidelines");
        put("instr", "instruction");
        put("instrs", "instructions");
        put("man", "manual");
        put("mans", "manuals");
        put("ref", "reference");
        put("refs", "references");
        put("doc", "document");
        put("docs", "documents");
        put("file", "file");
        put("files", "files");
        put("rec", "record");
        put("recs", "records");
        put("log", "log");
        put("logs", "logs");
        put("rpt", "report");
        put("rpts", "reports");
        put("sum", "summary");
        put("sums", "summaries");
        put("det", "detail");
        put("dets", "details");
        put("desc", "description");
        put("descs", "descriptions");
        put("note", "note");
        put("notes", "notes");
        put("comm", "comment");
        put("comms", "comments");
        put("rem", "remark");
        put("rems", "remarks");
        put("obs", "observation");
        put("obss", "observations");
        put("find", "finding");
        put("finds", "findings");
        put("rec", "recommendation");
        put("recs", "recommendations");
        put("act", "action");
        put("acts", "actions");
        put("item", "item");
        put("items", "items");
        put("task", "task");
        put("tasks", "tasks");
        put("step", "step");
        put("steps", "steps");
        put("phase", "phase");
        put("phases", "phases");
        put("stage", "stage");
        put("stages", "stages");
        put("level", "level");
        put("levels", "levels");
        put("tier", "tier");
        put("tiers", "tiers");
        put("cat", "category");
        put("cats", "categories");
        put("type", "type");
        put("types", "types");
        put("kind", "kind");
        put("kinds", "kinds");
        put("sort", "sort");
        put("sorts", "sorts");
        put("class", "class");
        put("classes", "classes");
        put("group", "group");
        put("groups", "groups");
        put("set", "set");
        put("sets", "sets");
        put("list", "list");
        put("lists", "lists");
        put("arr", "array");
        put("arrs", "arrays");
        put("col", "collection");
        put("cols", "collections");
        put("ser", "series");
        put("seq", "sequence");
        put("seqs", "sequences");
        put("ord", "order");
        put("ords", "orders");
        put("rank", "rank");
        put("ranks", "ranks");
        put("pos", "position");
        put("poss", "positions");
        put("loc", "location");
        put("locs", "locations");
        put("addr", "address");
        put("addrs", "addresses");
        put("coord", "coordinate");
        put("coords", "coordinates");
        put("pt", "point");
        put("pts", "points");
        put("line", "line");
        put("lines", "lines");
        put("path", "path");
        put("paths", "paths");
        put("route", "route");
        put("routes", "routes");
        put("dir", "direction");
        put("dirs", "directions");
        put("way", "way");
        put("ways", "ways");
        put("method", "method");
        put("methods", "methods");
        put("tech", "technique");
        put("techs", "techniques");
        put("app", "approach");
        put("apps", "approaches");
        put("str", "strategy");
        put("strs", "strategies");
        put("plan", "plan");
        put("plans", "plans");
        put("scheme", "scheme");
        put("schemes", "schemes");
        put("model", "model");
        put("models", "models");
        put("frame", "framework");
        put("frames", "frameworks");
        put("struct", "structure");
        put("structs", "structures");
        put("arch", "architecture");
        put("archs", "architectures");
        put("design", "design");
        put("designs", "designs");
        put("pattern", "pattern");
        put("patterns", "patterns");
        put("template", "template");
        put("templates", "templates");
        put("sample", "sample");
        put("samples", "samples");
        put("example", "example");
        put("examples", "examples");
        put("instance", "instance");
        put("instances", "instances");
        put("case", "case");
        put("cases", "cases");
        put("scenario", "scenario");
        put("scenarios", "scenarios");
        put("situation", "situation");
        put("situations", "situations");
        put("condition", "condition");
        put("conditions", "conditions");
        put("state", "state");
        put("states", "states");
        put("status", "status");
        put("statuses", "statuses");
        put("mode", "mode");
        put("modes", "modes");
        put("config", "configuration");
        put("configs", "configurations");
        put("setting", "setting");
        put("settings", "settings");
        put("option", "option");
        put("options", "options");
        put("choice", "choice");
        put("choices", "choices");
        put("select", "selection");
        put("selects", "selections");
        put("pick", "pick");
        put("picks", "picks");
        put("prefer", "preference");
        put("prefers", "preferences");
        put("default", "default");
        put("defaults", "defaults");
        put("custom", "custom");
        put("customs", "customs");
        put("user", "user");
        put("users", "users");
        put("client", "client");
        put("clients", "clients");
        put("customer", "customer");
        put("customers", "customers");
        put("vendor", "vendor");
        put("vendors", "vendors");
        put("supplier", "supplier");
        put("suppliers", "suppliers");
        put("partner", "partner");
        put("partners", "partners");
        put("contact", "contact");
        put("contacts", "contacts");
        put("person", "person");
        put("persons", "persons");
        put("people", "people");
        put("individual", "individual");
        put("individuals", "individuals");
        put("org", "organization");
        put("orgs", "organizations");
        put("comp", "company");
        put("comps", "companies");
        put("firm", "firm");
        put("firms", "firms");
        put("bus", "business");
        put("buss", "businesses");
        put("ent", "enterprise");
        put("ents", "enterprises");
        put("corp", "corporation");
        put("corps", "corporations");
        put("assoc", "association");
        put("assocs", "associations");
        put("inst", "institution");
        put("insts", "institutions");
        put("agency", "agency");
        put("agencies", "agencies");
        put("dept", "department");
        put("depts", "departments");
        put("div", "division");
        put("divs", "divisions");
        put("unit", "unit");
        put("units", "units");
        put("team", "team");
        put("teams", "teams");
        put("crew", "crew");
        put("crews", "crews");
        put("staff", "staff");
        put("staffs", "staffs");
        put("emp", "employee");
        put("emps", "employees");
        put("worker", "worker");
        put("workers", "workers");
        put("member", "member");
        put("members", "members");
        put("participant", "participant");
        put("participants", "participants");
        put("attendee", "attendee");
        put("attendees", "attendees");
        put("guest", "guest");
        put("guests", "guests");
        put("visitor", "visitor");
        put("visitors", "visitors");
        put("rep", "representative");
        put("reps", "representatives");
        put("agent", "agent");
        put("agents", "agents");
        put("delegate", "delegate");
        put("delegates", "delegates");
        put("ambassador", "ambassador");
        put("ambassadors", "ambassadors");
        put("liaison", "liaison");
        put("liaisons", "liaisons");
        put("coord", "coordinator");
        put("coords", "coordinators");
        put("mgr", "manager");
        put("mgrs", "managers");
        put("dir", "director");
        put("dirs", "directors");
        put("head", "head");
        put("heads", "heads");
        put("chief", "chief");
        put("chiefs", "chiefs");
        put("lead", "lead");
        put("leads", "leads");
        put("super", "supervisor");
        put("supers", "supervisors");
        put("boss", "boss");
        put("bosses", "bosses");
        put("owner", "owner");
        put("owners", "owners");
        put("exec", "executive");
        put("execs", "executives");
        put("officer", "officer");
        put("officers", "officers");
        put("president", "president");
        put("presidents", "presidents");
        put("vp", "vice_president");
        put("vps", "vice_presidents");
        put("ceo", "chief_executive_officer");
        put("ceos", "chief_executive_officers");
        put("cfo", "chief_financial_officer");
        put("cfos", "chief_financial_officers");
        put("cto", "chief_technology_officer");
        put("ctos", "chief_technology_officers");
        put("coo", "chief_operating_officer");
        put("coos", "chief_operating_officers");
        put("cmo", "chief_marketing_officer");
        put("cmos", "chief_marketing_officers");
        put("cio", "chief_information_officer");
        put("cios", "chief_information_officers");
        put("ciso", "chief_information_security_officer");
        put("cisos", "chief_information_security_officers");
        put("cpo", "chief_product_officer");
        put("cpos", "chief_product_officers");
        put("cdo", "chief_data_officer");
        put("cdos", "chief_data_officers");
        put("cso", "chief_security_officer");
        put("csos", "chief_security_officers");
        put("cco", "chief_compliance_officer");
        put("ccos", "chief_compliance_officers");
        put("cro", "chief_risk_officer");
        put("cros", "chief_risk_officers");
        put("cto", "chief_technology_officer");
        put("ctos", "chief_technology_officers");
        put("ceo", "chief_executive_officer");
        put("ceos", "chief_executive_officers");
        put("cfo", "chief_financial_officer");
        put("cfos", "chief_financial_officers");
        put("coo", "chief_operating_officer");
        put("coos", "chief_operating_officers");
        put("cmo", "chief_marketing_officer");
        put("cmos", "chief_marketing_officers");
        put("cio", "chief_information_officer");
        put("cios", "chief_information_officers");
        put("ciso", "chief_information_security_officer");
        put("cisos", "chief_information_security_officers");
        put("cpo", "chief_product_officer");
        put("cpos", "chief_product_officers");
        put("cdo", "chief_data_officer");
        put("cdos", "chief_data_officers");
        put("cso", "chief_security_officer");
        put("csos", "chief_security_officers");
        put("cco", "chief_compliance_officer");
        put("ccos", "chief_compliance_officers");
        put("cro", "chief_risk_officer");
        put("cros", "chief_risk_officers");
    }};
    
    public SpellChecker() {
        dictionary = new HashMap<>();
        loadDictionary("C:\\JDeveloper\\mywork\\MYChatTest\\lib\\frequency_dictionary_en_82_765.txt");
    }
    
    public void loadDictionary(String dictionaryPath) {
        try {
            File dictFile = new File(dictionaryPath);
            if (!dictFile.exists()) {
                System.out.println("Warning: Dictionary file not found. Creating a basic dictionary...");
                createBasicDictionary();
                return;
            }
            
            // Read the dictionary file
            dictionary = Files.lines(Paths.get(dictionaryPath))
                    .map(line -> line.split(" "))
                    .filter(tokens -> tokens.length >= 2)
                    .collect(Collectors.toMap(
                            tokens -> tokens[0].toLowerCase(),
                            tokens -> {
                                try {
                                    return Integer.parseInt(tokens[1]);
                                } catch (NumberFormatException e) {
                                    return 1;
                                }
                            },
                            Integer::sum
                    ));
            
            // Add domain-specific words after loading base dictionary
            addDomainSpecificWords();
            
        } catch (Exception e) {
            createBasicDictionary();
        }
    }
    
    private void createBasicDictionary() {
        // Create a comprehensive dictionary with high-frequency words
        String[] commonWords = {
            "the", "and", "is", "in", "to", "of", "a", "that", "it", "with",
            "for", "as", "was", "on", "are", "you", "this", "be", "at", "have",
            "or", "from", "one", "had", "but", "word", "not", "what", "all",
            "were", "they", "we", "when", "your", "can", "said", "there", "use",
            "an", "each", "which", "she", "do", "how", "their", "if", "will",
            "up", "other", "about", "out", "many", "then", "them", "these", "so",
            "some", "her", "would", "make", "like", "into", "him", "has", "two",
            "more", "very", "after", "words", "first", "where", "much", "through",
            "hello", "world", "computer", "programming", "java", "spell", "check",
            "correct", "mistake", "error", "text", "sentence", "language",
            "receive", "believe", "achieve", "piece", "their", "there", "they're",
            "separate", "definitely", "beginning", "beautiful", "necessary",
            "embarrass", "occurring", "recommend", "disappear", "tomorrow",
            "weird", "friend", "business", "really", "until", "immediately",
            "sophisticated", "my", "name", "am", "people", "person", "because",
            "between", "important", "example", "government", "company", "system",
            "program", "question", "number", "public", "information", "development",
            "i", "me", "him", "her", "us", "them", "myself", "yourself", "himself",
            "herself", "ourselves", "themselves", "document", "file", "record", "data", 
            "user", "customer", "client", "order", "invoice", "payment", "state", "date", 
            "month", "year", "created", "from", "today", "summary", "by",
            "info", "information", "metadata", "before", "under", "project", "type", 
            "price", "corporate", "opportunity", "code", "fields", "last", "part", 
            "parts", "product", "products", "specifications", "datasheet", "compatible", 
            "available", "stock", "discontinued", "active", "provide", "manufacturer", 
            "issues", "defects", "warranty", "period", "lead", "time", "validation", 
            "failed", "passed", "missing", "rejected", "loaded", "loading", "pricing", 
            "mismatch", "master", "skipped", "successful", "cost", "errors", "happened", 
            "during", "added", "showing", "due", "while", "happen", "then", "stock", 
            "skipped", "passed", "show", "get", "list", "find", "search", "display",
            "details", "status", "account", "contract", "contracts"
        };
        
        // Add all common words with base frequency
        for (String word : commonWords) {
            dictionary.put(word, 5000);
        }
        
        // Add domain-specific words
        addDomainSpecificWords();
        
        System.out.println("Basic dictionary created with " + dictionary.size() + " words.");
    }
    
    private void addDomainSpecificWords() {
        // Very high frequency business/contract/parts terms
        Map<String, Integer> domainWords = new HashMap<>();
        
        // Core business terms - VERY HIGH FREQUENCY
        domainWords.put("contract", 50000);
        domainWords.put("contracts", 48000);
        domainWords.put("show", 45000);
        domainWords.put("get", 45000);
        domainWords.put("account", 40000);
        domainWords.put("accounts", 38000);
        domainWords.put("customer", 35000);
        domainWords.put("customers", 33000);
        domainWords.put("details", 35000);
        domainWords.put("status", 28000);
        domainWords.put("info", 25000);
        domainWords.put("information", 23000);
        
        // Parts-specific terms
        domainWords.put("part", 30000);
        domainWords.put("parts", 32000);
        domainWords.put("product", 28000);
        domainWords.put("products", 26000);
        domainWords.put("specifications", 15000);
        domainWords.put("specification", 14000);
        domainWords.put("datasheet", 12000);
        domainWords.put("compatible", 10000);
        domainWords.put("available", 18000);
        domainWords.put("stock", 16000);
        domainWords.put("discontinued", 12000);
        domainWords.put("active", 20000);
        domainWords.put("manufacturer", 15000);
        domainWords.put("lead", 18000);
        domainWords.put("time", 25000);
        domainWords.put("issues", 15000);
        domainWords.put("defects", 8000);
        domainWords.put("warranty", 12000);
        domainWords.put("period", 15000);
        domainWords.put("validation", 12000);
        domainWords.put("failed", 25000);
        domainWords.put("passed", 20000);
        domainWords.put("missing", 18000);
        domainWords.put("rejected", 12000);
        domainWords.put("loaded", 15000);
        domainWords.put("loading", 14000);
        domainWords.put("pricing", 12000);
        domainWords.put("price", 20000);
        domainWords.put("master", 15000);
        domainWords.put("skipped", 10000);
        domainWords.put("successful", 12000);
        domainWords.put("cost", 18000);
        domainWords.put("errors", 16000);
        domainWords.put("error", 18000);
        
        // Action words
        domainWords.put("find", 20000);
        domainWords.put("list", 18000);
        domainWords.put("search", 16000);
        domainWords.put("display", 14000);
        domainWords.put("create", 15000);
        domainWords.put("created", 15000);
        domainWords.put("update", 12000);
        domainWords.put("delete", 12000);
        domainWords.put("add", 12000);
        domainWords.put("remove", 12000);
        domainWords.put("edit", 12000);
        domainWords.put("modify", 12000);
        domainWords.put("save", 12000);
        
        // Status and state words
        domainWords.put("inactive", 12000);
        domainWords.put("expired", 15000);
        domainWords.put("effective", 12000);
        domainWords.put("draft", 10000);
        
        // Time-related
        domainWords.put("after", 15000);
        domainWords.put("before", 15000);
        domainWords.put("between", 18000);
        domainWords.put("last", 25000);
        domainWords.put("month", 20000);
        domainWords.put("year", 12000);
        domainWords.put("date", 15000);
        domainWords.put("today", 15000);
        domainWords.put("during", 12000);
        domainWords.put("while", 12000);
        
        // Company names and proper nouns
        domainWords.put("vinod", 8000);
        domainWords.put("mary", 8000);
        domainWords.put("boeing", 8000);
        domainWords.put("siemens", 8000);
        domainWords.put("honeywell", 8000);
        
        // Add all domain words to dictionary
        for (Map.Entry<String, Integer> entry : domainWords.entrySet()) {
            dictionary.put(entry.getKey(), entry.getValue());
        }
    }
    
    // Enhanced spell correction with comprehensive mapping
    public String correctText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder correctedText = new StringBuilder();
        
        for (String word : words) {
            String originalWord = word;
            
            // Handle special contractions
            if (word.matches(".*[;].*")) {
                word = word.replaceAll(";", "'");
                if (word.toLowerCase().startsWith("i'")) {
                    word = "I" + word.substring(1);
                }
                correctedText.append(word);
                correctedText.append(" ");
                continue;
            }
            
            // Handle contractions with apostrophes - preserve them as-is
            if (word.toLowerCase().equals("who's") || word.toLowerCase().equals("it's") || 
                word.toLowerCase().equals("i'm") || word.toLowerCase().equals("don't") ||
                word.toLowerCase().equals("can't") || word.toLowerCase().equals("won't") ||
                word.toLowerCase().equals("aren't") || word.toLowerCase().equals("isn't") ||
                word.toLowerCase().equals("wasn't") || word.toLowerCase().equals("weren't") ||
                word.toLowerCase().equals("hasn't") || word.toLowerCase().equals("haven't") ||
                word.toLowerCase().equals("hadn't") || word.toLowerCase().equals("won't") ||
                word.toLowerCase().equals("wouldn't") || word.toLowerCase().equals("shouldn't") ||
                word.toLowerCase().equals("couldn't") || word.toLowerCase().equals("mustn't")) {
                correctedText.append(word);
                correctedText.append(" ");
                continue;
            }
            
            // Remove punctuation for spell checking but preserve it
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            String punctuation = word.replaceAll("[a-zA-Z0-9]", "");
            
            if (cleanWord.isEmpty()) {
                correctedText.append(word).append(" ");
                continue;
            }
            
            // Skip numbers and part numbers
            if (cleanWord.matches("^\\d+$") || cleanWord.toLowerCase().matches("^[a-z]{1,3}\\d+$")) {
                correctedText.append(word);
                correctedText.append(" ");
                continue;
            }
            
            // Apply comprehensive corrections first
            String lowerCleanWord = cleanWord.toLowerCase();
            if (COMPREHENSIVE_CORRECTIONS.containsKey(lowerCleanWord)) {
                String corrected = COMPREHENSIVE_CORRECTIONS.get(lowerCleanWord) + punctuation;
                correctedText.append(corrected);
                correctedText.append(" ");
                continue;
            }
            
            // Skip likely proper names (capitalized words that aren't at sentence start)
            if (Character.isUpperCase(cleanWord.charAt(0)) && !isFirstWordOfSentence(word, correctedText.toString())) {
                correctedText.append(word);
                correctedText.append(" ");
                continue;
            }
            
            try {
                List<Suggestion> suggestions = findSuggestions(cleanWord.toLowerCase());
                
                if (!suggestions.isEmpty()) {
                    if (suggestions.get(0).distance == 0) {
                        // Word is correct
                        correctedText.append(word);
                    } else if (suggestions.get(0).distance <= MAX_EDIT_DISTANCE) {
                        // Apply correction only if it makes sense
                        String bestSuggestion = suggestions.get(0).word;
                        
                        if (shouldApplyCorrection(cleanWord.toLowerCase(), bestSuggestion)) {
                            String corrected = bestSuggestion + punctuation;
                            correctedText.append(corrected);
                        } else {
                            correctedText.append(word);
                        }
                    } else {
                        correctedText.append(word);
                    }
                } else {
                    correctedText.append(word);
                }
                correctedText.append(" ");
            } catch (Exception e) {
                correctedText.append(word).append(" ");
            }
        }
        
        return correctedText.toString().trim();
    }
    
    // Calculate Levenshtein distance between two strings
    private int editDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        
        // Avoid matching very different length words unless they're very close
        if (Math.abs(len1 - len2) > 3) {
            return MAX_EDIT_DISTANCE + 1;
        }
        
        // dp[i][j] = minimum edits to transform s1[0..i-1] to s2[0..j-1]
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        // Initialize base cases
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        // Fill the dp table
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        
        return dp[len1][len2];
    }
    
    // Find suggestions for a word
    public List<Suggestion> findSuggestions(String word) {
        List<Suggestion> suggestions = new ArrayList<>();
        word = word.toLowerCase();
        
        // If word exists in dictionary, return it
        if (dictionary.containsKey(word)) {
            suggestions.add(new Suggestion(word, 0, dictionary.get(word)));
            return suggestions;
        }
        
        // Check comprehensive corrections first
        if (COMPREHENSIVE_CORRECTIONS.containsKey(word)) {
            String corrected = COMPREHENSIVE_CORRECTIONS.get(word);
            int frequency = dictionary.getOrDefault(corrected, 1000);
            suggestions.add(new Suggestion(corrected, 1, frequency));
            return suggestions;
        }
        
        // Find words with edit distance <= MAX_EDIT_DISTANCE
        for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
            String dictWord = entry.getKey();
            int frequency = entry.getValue();
            
            int distance = editDistance(word, dictWord);
            if (distance <= MAX_EDIT_DISTANCE) {
                suggestions.add(new Suggestion(dictWord, distance, frequency));
            }
        }
        
        // Sort suggestions by distance first, then by frequency (descending)
        suggestions.sort((a, b) -> {
            if (a.distance != b.distance) {
                return Integer.compare(a.distance, b.distance);
            }
            return Integer.compare(b.frequency, a.frequency);
        });
        
        return suggestions;
    }
    
    public void correctWord(String word) {
        try {
            List<Suggestion> suggestions = findSuggestions(word);
            
            if (suggestions.isEmpty()) {
                // No suggestions found
            } else if (suggestions.get(0).distance == 0) {
                // Word is correct
            } else {
                // Show suggestions
                for (int i = 0; i < Math.min(5, suggestions.size()); i++) {
                    Suggestion suggestion = suggestions.get(i);
                    // Process suggestion
                }
            }
        } catch (Exception e) {
            System.err.println("Error correcting word: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Additional helper method for comprehensive spell checking
    public String performComprehensiveSpellCheck(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        // First pass: Apply comprehensive corrections
        String[] words = text.split("\\s+");
        StringBuilder firstPass = new StringBuilder();
        
        for (String word : words) {
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String punctuation = word.replaceAll("[a-zA-Z0-9]", "");
            
            if (COMPREHENSIVE_CORRECTIONS.containsKey(cleanWord)) {
                firstPass.append(COMPREHENSIVE_CORRECTIONS.get(cleanWord)).append(punctuation).append(" ");
            } else {
                firstPass.append(word).append(" ");
            }
        }
        
        // Second pass: Apply dictionary-based corrections
        return correctText(firstPass.toString().trim());
    }
    
    private boolean shouldApplyCorrection(String original, String suggestion) {
        // Don't apply correction if suggestion is much shorter than original
        if (suggestion.length() < original.length() - 2) {
            return false;
        }
        
        // Don't apply correction if original is very short and suggestion is very different
        if (original.length() <= 3 && editDistance(original, suggestion) > 1) {
            return false;
        }
        
        // Apply correction for common patterns
        if (COMPREHENSIVE_CORRECTIONS.containsKey(original)) {
            return true;
        }
        
        return true;
    }
    
    private boolean isFirstWordOfSentence(String word, String previousText) {
        if (previousText.isEmpty()) return true;
        return previousText.endsWith(".") || previousText.endsWith("!") || previousText.endsWith("?");
    }
    
    private static class Suggestion {
        String word;
        int distance;
        int frequency;
        
        Suggestion(String word, int distance, int frequency) {
            this.word = word;
            this.distance = distance;
            this.frequency = frequency;
        }
    }
    
    public static void main(String[] args) {
        SpellChecker spellChecker = new SpellChecker();
        
        // Test comprehensive spell correction
        String[] testQueries = {
            "lst out contrcts with part numbr AE125",
            "whats the specifcations of prduct AE125",
            "shwo mee parts 123456",
            "faield prts of 123456",
            "ae125 faild becasue no cost data"
        };
        
        System.out.println("Testing Comprehensive Spell Correction:");
        System.out.println("==================================================");
        
        for (String query : testQueries) {
            String corrected = spellChecker.performComprehensiveSpellCheck(query);
            System.out.println("Original: " + query);
            System.out.println("Corrected: " + corrected);
            System.out.println();
        }
    }
}
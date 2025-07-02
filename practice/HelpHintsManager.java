package view.practice;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class HelpHintsManager {
    
    private static final Logger logger = Logger.getLogger(HelpHintsManager.class.getName());
    private final Map<String, String> helpHints;
    private final String hintsFilePath;
    
    public HelpHintsManager(String hintsFilePath) {
        this.hintsFilePath = hintsFilePath;
        this.helpHints = new HashMap<>();
        loadHelpHints();
    }
    
    private void loadHelpHints() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(hintsFilePath)) {
            if (inputStream == null) {
                logger.warning("?? Help hints file not found: " + hintsFilePath);
                loadDefaultHints();
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#")) {
                        continue; // Skip empty lines and comments
                    }
                    
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        helpHints.put(key, value);
                    }
                }
                
                logger.info("? Loaded " + helpHints.size() + " help hints from " + hintsFilePath);
            }
            
        } catch (IOException e) {
            logger.severe("? Error loading help hints: " + e.getMessage());
            loadDefaultHints();
        }
    }
    
    private void loadDefaultHints() {
        logger.info("? Loading default help hints...");
        
        helpHints.put("create_contract", 
            "Go to Contract Menu ? New Contract ? Fill Required Fields ? Enter Contract Details ? Save Contract ? Verify Creation");
        
        helpHints.put("load_parts", 
            "Navigate to Load Screen ? Choose File ? Select Parts File ? Validate Format ? Click Upload ? Confirm Loading");
        
        helpHints.put("validate_parts", 
            "After loading ? Click 'Validate' button ? Wait for processing ? Check validation status ? Review results");
        
        helpHints.put("check_errors", 
            "Open Failed Parts tab ? Review error messages ? Check validation issues ? Fix data problems ? Re-validate");
        
        helpHints.put("export_data", 
            "Go to Export Menu ? Select Data Type ? Choose Format ? Set Parameters ? Generate Export ? Download File");
        
        helpHints.put("import_data", 
            "Navigate to Import Screen ? Select File Type ? Browse File ? Map Fields ? Validate Data ? Import");
        
        helpHints.put("generate_report", 
            "Open Reports Menu ? Select Report Type ? Set Date Range ? Choose Parameters ? Generate ? View/Download");
        
        helpHints.put("manage_users", 
            "Go to Admin Panel ? User Management ? Add/Edit Users ? Set Permissions ? Save Changes");
        
        helpHints.put("backup_data", 
            "Access System Menu ? Backup Options ? Select Data ? Choose Location ? Start Backup ? Verify Completion");
        
        logger.info("? Default help hints loaded: " + helpHints.size() + " entries");
    }
    
    public String getHelpText(String intent) {
        // Map intent to help key
        String helpKey = mapIntentToHelpKey(intent);
        return helpHints.getOrDefault(helpKey, getDefaultHelpMessage(intent));
    }
    
    private String mapIntentToHelpKey(String intent) {
        if (intent == null) return "unknown";
        
        switch (intent) {
            case "help_create_contract":
                return "create_contract";
            case "help_load_parts":
                return "load_parts";
            case "help_validate_parts":
                return "validate_parts";
            case "help_check_errors":
                return "check_errors";
            case "help_export_data":
                return "export_data";
            case "help_import_data":
                return "import_data";
            case "help_generate_report":
                return "generate_report";
            case "help_manage_users":
                return "manage_users";
            case "help_backup_data":
                return "backup_data";
            default:
                return "unknown";
        }
    }
    
    private String getDefaultHelpMessage(String intent) {
        return "I understand you need help, but I don't have specific guidance for that topic. " +
               "Please contact support or check the user manual for detailed instructions.\n\n" +
               "I can help you with:\n" +
               "• Creating contracts\n" +
               "• Loading parts\n" +
               "• Validating parts\n" +
               "• Checking errors\n" +
               "• Exporting/importing data\n" +
               "• Generating reports\n" +
               "• Managing users\n" +
               "• Backing up data";
    }
    
    public Set<String> getAllHelpKeys() {
        return new HashSet<>(helpHints.keySet());
    }
    
    public Map<String, String> getAllHelpHints() {
        return new HashMap<>(helpHints);
    }
    
    public void reloadHints() {
        helpHints.clear();
        loadHelpHints();
        logger.info("? Help hints reloaded");
    }
    
    public boolean hasHelpForIntent(String intent) {
        String helpKey = mapIntentToHelpKey(intent);
        return helpHints.containsKey(helpKey);
    }
    
    public List<String> getAvailableHelpTopics() {
        return new ArrayList<>(Arrays.asList(
            "Creating contracts",
            "Loading parts", 
            "Validating parts",
            "Checking errors",
            "Exporting data",
            "Importing data", 
            "Generating reports",
            "Managing users",
            "Backing up data"
        ));
    }
}
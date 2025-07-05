package com.adf.nlp.models;

import com.adf.nlp.config.ConfigurationManager;
import com.adf.nlp.utils.InputAnalyzer;
import java.util.*;
import java.util.logging.Logger;

/**
 * Help Model using Configuration-Driven Architecture
 * Provides contract creation guidance and help information
 * All response templates are externalized in txt files
 */
public class HelpModel {
    private static final Logger logger = Logger.getLogger(HelpModel.class.getName());
    
    private final ConfigurationManager configManager;
    
    /**
     * Constructor
     */
    public HelpModel() {
        this.configManager = ConfigurationManager.getInstance();
    }
    
    /**
     * Process help-related query
     */
    public String processQuery(String input, InputAnalyzer.AnalysisResult analysis) {
        try {
            // Determine help type based on input analysis
            HelpType helpType = determineHelpType(input, analysis);
            
            // Provide appropriate help
            switch (helpType) {
                case CONTRACT_CREATION:
                    return provideContractCreationHelp(input);
                    
                case WORKFLOW_HELP:
                    return provideWorkflowHelp();
                    
                case REQUIRED_FIELDS:
                    return provideRequiredFieldsHelp();
                    
                case APPROVAL_PROCESS:
                    return provideApprovalProcessHelp();
                    
                case STEP_BY_STEP:
                    return provideStepByStepGuide(input);
                    
                default:
                    return provideGeneralHelp(input);
            }
            
        } catch (Exception e) {
            logger.severe("Error processing help query: " + e.getMessage());
            return configManager.getResponseTemplate("error_system_busy");
        }
    }
    
    /**
     * Determine help type based on input
     */
    private HelpType determineHelpType(String input, InputAnalyzer.AnalysisResult analysis) {
        String lowerInput = input.toLowerCase();
        
        // Check for specific help types
        if (containsWorkflowKeywords(lowerInput)) {
            return HelpType.WORKFLOW_HELP;
        }
        
        if (containsFieldsKeywords(lowerInput)) {
            return HelpType.REQUIRED_FIELDS;
        }
        
        if (containsApprovalKeywords(lowerInput)) {
            return HelpType.APPROVAL_PROCESS;
        }
        
        if (containsStepKeywords(lowerInput)) {
            return HelpType.STEP_BY_STEP;
        }
        
        // Default to contract creation help
        return HelpType.CONTRACT_CREATION;
    }
    
    /**
     * Provide contract creation help
     */
    private String provideContractCreationHelp(String input) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_input", input);
        
        String basicHelp = configManager.getResponseTemplate("help_create_contract");
        
        // Add additional context based on input
        StringBuilder detailedHelp = new StringBuilder();
        detailedHelp.append(basicHelp).append("\n\n");
        
        // Add specific guidance based on what user is asking
        if (input.toLowerCase().contains("start") || input.toLowerCase().contains("begin")) {
            detailedHelp.append("To get started:\n");
            detailedHelp.append("1. Navigate to the Contract Creation page\n");
            detailedHelp.append("2. Click 'New Contract' button\n");
            detailedHelp.append("3. Select the appropriate contract type\n");
            detailedHelp.append("4. Begin filling in the required information\n\n");
        }
        
        if (input.toLowerCase().contains("what") || input.toLowerCase().contains("information")) {
            detailedHelp.append("Required Information:\n");
            detailedHelp.append("• Customer/Client details\n");
            detailedHelp.append("• Contract dates (effective and expiration)\n");
            detailedHelp.append("• Financial terms and pricing\n");
            detailedHelp.append("• Organizational assignments\n");
            detailedHelp.append("• Legal terms and conditions\n\n");
        }
        
        detailedHelp.append("Need more specific help? Try:\n");
        detailedHelp.append("• 'What fields are required for contract creation?'\n");
        detailedHelp.append("• 'Show me the contract approval process'\n");
        detailedHelp.append("• 'What is the contract creation workflow?'");
        
        return detailedHelp.toString();
    }
    
    /**
     * Provide workflow help
     */
    private String provideWorkflowHelp() {
        String workflowTemplate = configManager.getResponseTemplate("help_contract_workflow");
        
        StringBuilder detailedWorkflow = new StringBuilder();
        detailedWorkflow.append(workflowTemplate).append("\n\n");
        
        detailedWorkflow.append("Detailed Workflow Steps:\n\n");
        
        detailedWorkflow.append("1. DRAFT Phase:\n");
        detailedWorkflow.append("   • Create initial contract document\n");
        detailedWorkflow.append("   • Fill in basic information\n");
        detailedWorkflow.append("   • Save as draft for later editing\n\n");
        
        detailedWorkflow.append("2. REVIEW Phase:\n");
        detailedWorkflow.append("   • Internal team review\n");
        detailedWorkflow.append("   • Technical specifications validation\n");
        detailedWorkflow.append("   • Financial terms verification\n\n");
        
        detailedWorkflow.append("3. APPROVAL Phase:\n");
        detailedWorkflow.append("   • Legal department approval\n");
        detailedWorkflow.append("   • Management authorization\n");
        detailedWorkflow.append("   • Budget confirmation\n\n");
        
        detailedWorkflow.append("4. ACTIVE Phase:\n");
        detailedWorkflow.append("   • Contract becomes effective\n");
        detailedWorkflow.append("   • Performance tracking begins\n");
        detailedWorkflow.append("   • Regular status monitoring\n\n");
        
        detailedWorkflow.append("5. EXECUTION Phase:\n");
        detailedWorkflow.append("   • Deliverables management\n");
        detailedWorkflow.append("   • Payment processing\n");
        detailedWorkflow.append("   • Performance evaluation\n\n");
        
        detailedWorkflow.append("6. COMPLETION/RENEWAL:\n");
        detailedWorkflow.append("   • Contract closure activities\n");
        detailedWorkflow.append("   • Final deliverables confirmation\n");
        detailedWorkflow.append("   • Renewal evaluation if applicable");
        
        return detailedWorkflow.toString();
    }
    
    /**
     * Provide required fields help
     */
    private String provideRequiredFieldsHelp() {
        String fieldsTemplate = configManager.getResponseTemplate("help_required_fields");
        
        StringBuilder detailedFields = new StringBuilder();
        detailedFields.append(fieldsTemplate).append("\n\n");
        
        detailedFields.append("Detailed Field Requirements:\n\n");
        
        detailedFields.append("MANDATORY FIELDS:\n");
        detailedFields.append("• Contract Number (Award Number) - Unique identifier\n");
        detailedFields.append("• Customer Name - Full legal name\n");
        detailedFields.append("• Effective Date - Contract start date\n");
        detailedFields.append("• Expiration Date - Contract end date\n");
        detailedFields.append("• Contract Type - Service/Product/Mixed\n");
        detailedFields.append("• Total Amount - Financial value\n");
        detailedFields.append("• Organization - Responsible department\n\n");
        
        detailedFields.append("OPTIONAL BUT RECOMMENDED:\n");
        detailedFields.append("• Contract Name/Title - Descriptive name\n");
        detailedFields.append("• Customer Number - Internal customer ID\n");
        detailedFields.append("• Price List - Applicable pricing structure\n");
        detailedFields.append("• Department - Specific department assignment\n");
        detailedFields.append("• Project Type - Classification\n");
        detailedFields.append("• Description - Contract summary\n");
        detailedFields.append("• Terms and Conditions - Legal clauses\n\n");
        
        detailedFields.append("VALIDATION RULES:\n");
        detailedFields.append("• Effective Date must be before Expiration Date\n");
        detailedFields.append("• Contract Number must be unique\n");
        detailedFields.append("• Amount must be positive number\n");
        detailedFields.append("• Customer Name cannot be empty\n");
        detailedFields.append("• Organization must be valid department");
        
        return detailedFields.toString();
    }
    
    /**
     * Provide approval process help
     */
    private String provideApprovalProcessHelp() {
        String approvalTemplate = configManager.getResponseTemplate("help_approval_process");
        
        StringBuilder detailedApproval = new StringBuilder();
        detailedApproval.append(approvalTemplate).append("\n\n");
        
        detailedApproval.append("Detailed Approval Process:\n\n");
        
        detailedApproval.append("STEP 1: Legal Review\n");
        detailedApproval.append("• Contract terms validation\n");
        detailedApproval.append("• Compliance checking\n");
        detailedApproval.append("• Risk assessment\n");
        detailedApproval.append("• Legal language verification\n");
        detailedApproval.append("• Timeline: 2-3 business days\n\n");
        
        detailedApproval.append("STEP 2: Financial Approval\n");
        detailedApproval.append("• Budget availability confirmation\n");
        detailedApproval.append("• Pricing validation\n");
        detailedApproval.append("• Cost-benefit analysis\n");
        detailedApproval.append("• Financial impact assessment\n");
        detailedApproval.append("• Timeline: 1-2 business days\n\n");
        
        detailedApproval.append("STEP 3: Management Sign-off\n");
        detailedApproval.append("• Executive approval\n");
        detailedApproval.append("• Strategic alignment check\n");
        detailedApproval.append("• Resource allocation confirmation\n");
        detailedApproval.append("• Final authorization\n");
        detailedApproval.append("• Timeline: 1-2 business days\n\n");
        
        detailedApproval.append("STEP 4: Customer Acceptance\n");
        detailedApproval.append("• Customer review and approval\n");
        detailedApproval.append("• Terms negotiation (if needed)\n");
        detailedApproval.append("• Final contract signing\n");
        detailedApproval.append("• Contract activation\n");
        detailedApproval.append("• Timeline: 3-5 business days\n\n");
        
        detailedApproval.append("TOTAL APPROVAL TIME: 7-12 business days\n");
        detailedApproval.append("(May vary based on contract complexity and value)");
        
        return detailedApproval.toString();
    }
    
    /**
     * Provide step-by-step guide
     */
    private String provideStepByStepGuide(String input) {
        StringBuilder stepGuide = new StringBuilder();
        stepGuide.append("Step-by-Step Contract Creation Guide:\n\n");
        
        stepGuide.append("STEP 1: Preparation\n");
        stepGuide.append("□ Gather all required customer information\n");
        stepGuide.append("□ Determine contract type and scope\n");
        stepGuide.append("□ Prepare financial estimates\n");
        stepGuide.append("□ Identify key stakeholders\n");
        stepGuide.append("□ Review similar contracts for reference\n\n");
        
        stepGuide.append("STEP 2: Initial Creation\n");
        stepGuide.append("□ Log into the contract management system\n");
        stepGuide.append("□ Navigate to 'Create New Contract'\n");
        stepGuide.append("□ Select appropriate contract template\n");
        stepGuide.append("□ Enter basic contract information\n");
        stepGuide.append("□ Save initial draft\n\n");
        
        stepGuide.append("STEP 3: Detailed Information Entry\n");
        stepGuide.append("□ Fill in customer details completely\n");
        stepGuide.append("□ Set effective and expiration dates\n");
        stepGuide.append("□ Enter financial terms and amounts\n");
        stepGuide.append("□ Specify organizational assignments\n");
        stepGuide.append("□ Add technical specifications\n");
        stepGuide.append("□ Include terms and conditions\n\n");
        
        stepGuide.append("STEP 4: Validation and Review\n");
        stepGuide.append("□ Run system validation checks\n");
        stepGuide.append("□ Review all entered information\n");
        stepGuide.append("□ Verify calculations and totals\n");
        stepGuide.append("□ Check for completeness\n");
        stepGuide.append("□ Correct any errors or omissions\n\n");
        
        stepGuide.append("STEP 5: Submission for Approval\n");
        stepGuide.append("□ Submit for internal review\n");
        stepGuide.append("□ Track approval status\n");
        stepGuide.append("□ Respond to review comments\n");
        stepGuide.append("□ Make necessary revisions\n");
        stepGuide.append("□ Resubmit if required\n\n");
        
        stepGuide.append("STEP 6: Final Activation\n");
        stepGuide.append("□ Obtain final approvals\n");
        stepGuide.append("□ Coordinate customer signing\n");
        stepGuide.append("□ Activate contract in system\n");
        stepGuide.append("□ Set up monitoring and tracking\n");
        stepGuide.append("□ Notify all stakeholders\n\n");
        
        stepGuide.append("TIPS FOR SUCCESS:\n");
        stepGuide.append("• Double-check all dates and amounts\n");
        stepGuide.append("• Use clear, specific language\n");
        stepGuide.append("• Keep documentation organized\n");
        stepGuide.append("• Communicate regularly with stakeholders\n");
        stepGuide.append("• Follow up on pending approvals");
        
        return stepGuide.toString();
    }
    
    /**
     * Provide general help
     */
    private String provideGeneralHelp(String input) {
        StringBuilder generalHelp = new StringBuilder();
        generalHelp.append("Contract Creation Help Center\n\n");
        
        generalHelp.append("I can help you with:\n\n");
        
        generalHelp.append("CONTRACT CREATION:\n");
        generalHelp.append("• Step-by-step creation process\n");
        generalHelp.append("• Required fields and information\n");
        generalHelp.append("• Validation rules and requirements\n");
        generalHelp.append("• Best practices and tips\n\n");
        
        generalHelp.append("WORKFLOW GUIDANCE:\n");
        generalHelp.append("• Contract lifecycle stages\n");
        generalHelp.append("• Approval process details\n");
        generalHelp.append("• Status tracking and monitoring\n");
        generalHelp.append("• Timeline expectations\n\n");
        
        generalHelp.append("COMMON QUESTIONS:\n");
        generalHelp.append("• 'How do I create a new contract?'\n");
        generalHelp.append("• 'What information do I need?'\n");
        generalHelp.append("• 'What is the approval process?'\n");
        generalHelp.append("• 'How long does approval take?'\n");
        generalHelp.append("• 'What are the required fields?'\n\n");
        
        generalHelp.append("SYSTEM FEATURES:\n");
        generalHelp.append("• Template-based creation\n");
        generalHelp.append("• Automatic validation\n");
        generalHelp.append("• Workflow management\n");
        generalHelp.append("• Status notifications\n");
        generalHelp.append("• Document management\n\n");
        
        generalHelp.append("For specific help, try asking:\n");
        generalHelp.append("• 'Show me the contract creation steps'\n");
        generalHelp.append("• 'What fields are required?'\n");
        generalHelp.append("• 'Explain the approval workflow'\n");
        generalHelp.append("• 'How do I get started?'");
        
        return generalHelp.toString();
    }
    
    // Helper methods for keyword detection
    private boolean containsWorkflowKeywords(String input) {
        return input.contains("workflow") || input.contains("process") || input.contains("steps") || 
               input.contains("procedure") || input.contains("lifecycle");
    }
    
    private boolean containsFieldsKeywords(String input) {
        return input.contains("fields") || input.contains("information") || input.contains("data") || 
               input.contains("required") || input.contains("mandatory");
    }
    
    private boolean containsApprovalKeywords(String input) {
        return input.contains("approval") || input.contains("approve") || input.contains("sign") || 
               input.contains("authorize") || input.contains("review");
    }
    
    private boolean containsStepKeywords(String input) {
        return input.contains("step") || input.contains("guide") || input.contains("tutorial") || 
               input.contains("instruction") || input.contains("how to");
    }
    
    /**
     * Help type enumeration
     */
    private enum HelpType {
        CONTRACT_CREATION,
        WORKFLOW_HELP,
        REQUIRED_FIELDS,
        APPROVAL_PROCESS,
        STEP_BY_STEP
    }
}
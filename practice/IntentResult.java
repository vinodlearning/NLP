package view.practice;
import java.util.Map;

/**
 * Result of intent classification with contract-specific information
 */
public class IntentResult {
    private ContractIntent intent;
    private double confidence;
    private String actionRequired;
    private Map<String, Double> allIntentScores;
    private String contractNumber;
    
    // Constructor with contract number (new version)
    public IntentResult(ContractIntent intent, double confidence, 
                       String actionRequired, Map<String, Double> allIntentScores, 
                       String contractNumber) {
        this.intent = intent;
        this.confidence = confidence;
        this.actionRequired = actionRequired;
        this.allIntentScores = allIntentScores;
        this.contractNumber = contractNumber;
    }
    
    // Constructor without contract number (backward compatibility)
    public IntentResult(ContractIntent intent, double confidence, 
                       String actionRequired, Map<String, Double> allIntentScores) {
        this(intent, confidence, actionRequired, allIntentScores, null);
    }
    
    // Getters and setters
    public ContractIntent getIntent() { return intent; }
    public void setIntent(ContractIntent intent) { this.intent = intent; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public String getActionRequired() { return actionRequired; }
    public void setActionRequired(String actionRequired) { this.actionRequired = actionRequired; }
    
    public Map<String, Double> getAllIntentScores() { return allIntentScores; }
    public void setAllIntentScores(Map<String, Double> allIntentScores) { this.allIntentScores = allIntentScores; }
    
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    
    @Override
    public String toString() {
        return String.format("IntentResult{intent=%s, confidence=%.2f, action=%s, contractNumber=%s}", 
                           intent, confidence, actionRequired, contractNumber);
    }
}
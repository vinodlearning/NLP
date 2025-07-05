package model.nlp;

/**
 * Entity Operation class for structured entity extraction
 * Represents database query operations like: project_type = "new"
 * 
 * @author Contract Query Processing System
 * @version 1.0
 */
public class EntityOperation {
    private String attribute;
    private String operation;  // =, >, <, >=, <=, BETWEEN, LIKE, etc.
    private String value;
    
    public EntityOperation(String attribute, String operation, String value) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
    }
    
    public String getAttribute() { return attribute; }
    public String getOperation() { return operation; }
    public String getValue() { return value; }
    
    @Override
    public String toString() {
        return attribute + " " + operation + " " + value;
    }
}
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "contractQueryBean")
@SessionScoped
public class ContractQueryBean implements Serializable {
    private String userInput;
    private StructuredQueryResponse response;
    private String jsonResponse;
    private boolean hasResponse = false;
    
    public void processQuery() {
        if (userInput != null && !userInput.trim().isEmpty()) {
            response = StructuredQueryProcessor.processQuery(userInput.trim());
            jsonResponse = response.toJson();
            hasResponse = true;
        }
    }
    
    // Getters and Setters
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }
    
    public StructuredQueryResponse getResponse() { return response; }
    public String getJsonResponse() { return jsonResponse; }
    public boolean isHasResponse() { return hasResponse; }
}

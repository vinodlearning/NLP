package view;
import java.util.*;
public class ContextManager {
    private Map<String, ConversationContext> sessionContexts = new HashMap<>();
    private final int MAX_CONTEXT_SIZE = 10;
    
    public String enrichWithContext(String userInput, String sessionId) {
        ConversationContext context = sessionContexts.get(sessionId);
        if (context == null) {
            return userInput;
        }
        
        String enriched = userInput;
        
        // Handle pronouns and references
        enriched = resolvePronouns(enriched, context);
        enriched = resolveReferences(enriched, context);
        
        System.out.println("Context enriched: " + userInput + " -> " + enriched);
        return enriched;
    }
    
    private String resolvePronouns(String input, ConversationContext context) {
        String resolved = input;
        
        // Handle "it" references
        if (resolved.contains(" it ") || resolved.startsWith("it ") || resolved.endsWith(" it")) {
            String lastEntity = context.getLastMentionedEntity();
            if (lastEntity != null) {
                resolved = resolved.replaceAll("\\bit\\b", lastEntity);
            }
        }
        
        // Handle "that" references
        if (resolved.contains(" that ") || resolved.startsWith("that ")) {
            String lastEntity = context.getLastMentionedEntity();
            if (lastEntity != null) {
                resolved = resolved.replaceAll("\\bthat\\b", lastEntity);
            }
        }
        
        return resolved;
    }
    
    private String resolveReferences(String input, ConversationContext context) {
        String resolved = input;
        
        // Handle "same" references
        if (resolved.contains("same")) {
            String lastEntity = context.getLastMentionedEntity();
            if (lastEntity != null) {
                resolved = resolved.replaceAll("same", lastEntity);
            }
        }
        
        // Handle implicit contract/part references
        if (!resolved.matches(".*\\d{6}.*") && context.getLastContractNumber() != null) {
            if (resolved.contains("contract") && !resolved.contains("contracts")) {
                resolved = resolved.replace("contract", "contract " + context.getLastContractNumber());
            }
        }
        
        if (!resolved.matches(".*[A-Z]{2}\\d{3}.*") && context.getLastPartNumber() != null) {
            if (resolved.contains("part") && !resolved.contains("parts")) {
                resolved = resolved.replace("part", "part " + context.getLastPartNumber());
            }
        }
        
        return resolved;
    }
    
    public void updateContext(String sessionId, Intent intent, Map<String, Object> entities) {
        ConversationContext context = sessionContexts.computeIfAbsent(sessionId, 
            k -> new ConversationContext());
        
        context.addInteraction(intent, entities);
        
        // Update last mentioned entities
        if (entities.containsKey("contractNumber")) {
            context.setLastContractNumber((String) entities.get("contractNumber"));
            context.setLastMentionedEntity((String) entities.get("contractNumber"));
        }
        if (entities.containsKey("partNumber")) {
            context.setLastPartNumber((String) entities.get("partNumber"));
            context.setLastMentionedEntity((String) entities.get("partNumber"));
        }
        if (entities.containsKey("username")) {
            context.setLastUsername((String) entities.get("username"));
        }
        
        context.setLastIntent(intent);
        
        // Cleanup old contexts
        if (context.getInteractionCount() > MAX_CONTEXT_SIZE) {
            context.removeOldestInteraction();
        }
        
        System.out.println("Updated context for session: " + sessionId);
    }
    
    public void clearContext(String sessionId) {
        sessionContexts.remove(sessionId);
        System.out.println("Cleared context for session: " + sessionId);
    }
}

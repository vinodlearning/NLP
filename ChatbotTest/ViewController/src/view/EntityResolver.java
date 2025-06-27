package view;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityResolver {
    private Pattern contractPattern = Pattern.compile("\\b\\d{6}\\b"); // 6-digit contract numbers
    private Pattern partPattern = Pattern.compile("\\b[A-Z]{2}\\d{3}\\b"); // AE125 format
    private Pattern accountPattern = Pattern.compile("\\b\\d{8}\\b"); // 8-digit account numbers
    private Pattern userNamePattern = Pattern.compile("\\b[A-Z][a-z]+\\b"); // Names like John, Smith, Mary
    
    // Open source NER using Stanford CoreNLP
    private StanfordCoreNLP pipeline;
    
    public EntityResolver() {
        initializeStanfordNLP();
    }
    
    private void initializeStanfordNLP() {
        try {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
            props.setProperty("ner.useSUTime", "false");
            this.pipeline = new StanfordCoreNLP(props);
            System.out.println("Stanford CoreNLP initialized successfully");
        } catch (Exception e) {
            System.out.println("Warning: Stanford CoreNLP not available, using regex-based extraction");
            this.pipeline = null;
        }
    }
    
    public Map<String, Object> extractEntities(String query, Intent intent) {
        Map<String, Object> entities = new HashMap<>();
        
        // Use Stanford NLP if available, otherwise fallback to regex
        if (pipeline != null) {
            entities.putAll(extractWithStanfordNLP(query));
        }
        
        // Always run domain-specific regex patterns
        entities.putAll(extractDomainEntities(query, intent));
        
        System.out.println("Extracted entities: " + entities);
        return entities;
    }
    
    private Map<String, Object> extractWithStanfordNLP(String query) {
        Map<String, Object> entities = new HashMap<>();
        
        try {
            CoreDocument document = new CoreDocument(query);
            pipeline.annotate(document);
            
            for (CoreEntityMention em : document.entityMentions()) {
                String entityType = em.entityType();
                String entityText = em.text();
                
                switch (entityType) {
                    case "PERSON":
                        entities.put("username", entityText.toLowerCase());
                        break;
                    case "ORGANIZATION":
                        entities.put("customerName", entityText);
                        break;
                    case "NUMBER":
                        // Will be refined by domain-specific patterns
                        break;
                    case "DATE":
                        entities.put("date", entityText);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error in Stanford NLP extraction: " + e.getMessage());
        }
        
        return entities;
    }
private Map<String, Object> extractDomainEntities(String query, Intent intent) {
        Map<String, Object> entities = new HashMap<>();
        
        // Extract contract numbers
        Matcher contractMatcher = contractPattern.matcher(query);
        if (contractMatcher.find()) {
            entities.put("contractNumber", contractMatcher.group());
        }
        
        // Extract part numbers
        Matcher partMatcher = partPattern.matcher(query);
        if (partMatcher.find()) {
            entities.put("partNumber", partMatcher.group());
        }
        
        // Extract account numbers
        Matcher accountMatcher = accountPattern.matcher(query);
        if (accountMatcher.find()) {
            entities.put("accountNumber", accountMatcher.group());
        }
        
        // Extract user names for contract queries
        if (isUserRelatedIntent(intent)) {
            Matcher userMatcher = userNamePattern.matcher(query);
            if (userMatcher.find() && !entities.containsKey("username")) {
                entities.put("username", userMatcher.group().toLowerCase());
            }
        }
        
        return entities;
    }
    
    private boolean isUserRelatedIntent(Intent intent) {
        return true;
    }
}
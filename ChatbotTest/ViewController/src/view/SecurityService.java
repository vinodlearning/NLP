package view;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

public class SecurityService {
    private Map<String, Set<QueryType>> userPermissions;
    private Set<QueryType> publicQueryTypes;
    
    public SecurityService() {
        initializePermissions();
    }
    
    private void initializePermissions() {
        publicQueryTypes = new HashSet<>();
        publicQueryTypes.add(QueryType.HELP_CREATE_CONTRACT);
        
        userPermissions = new HashMap<>();
        
        // Default permissions for all authenticated users
        Set<QueryType> defaultPermissions = new HashSet<>();
        defaultPermissions.addAll(publicQueryTypes);
        defaultPermissions.add(QueryType.CONTRACT_INFO);
        defaultPermissions.add(QueryType.USER_CONTRACT_QUERY);
        defaultPermissions.add(QueryType.PARTS_INFO);
        defaultPermissions.add(QueryType.PARTS_BY_CONTRACT);
        defaultPermissions.add(QueryType.STATUS_CHECK);
        
        // Admin permissions (all query types) - Fixed type from ParsedQuery to QueryType
        Set<QueryType> adminPermissions = new HashSet<>();
        adminPermissions.addAll(Arrays.asList(QueryType.values()));
        
        // Set default permissions
        userPermissions.put("default", defaultPermissions);
        userPermissions.put("admin", adminPermissions);
    }
    
    public boolean hasAccess(String username, QueryType queryType) {
        try {
            System.out.println("Checking access for user: " + username + ", queryType: " + queryType);
            
            // Public query types are always allowed
            if (publicQueryTypes.contains(queryType)) {
                return true;
            }
            
            // Check user-specific permissions
            Set<QueryType> userQueryTypes = userPermissions.get(username);
            if (userQueryTypes == null) {
                userQueryTypes = userPermissions.get("default");
            }
            
            boolean hasAccess = userQueryTypes != null && userQueryTypes.contains(queryType);
            System.out.println("Access " + (hasAccess ? "granted" : "denied") + " for " + username);
            
            return hasAccess;
            
        } catch (Exception e) {
            System.out.println("Error checking access: " + e.getMessage());
            return false; // Deny access on error
        }
    }
    
    public void grantPermission(String username, QueryType queryType) {
        userPermissions.computeIfAbsent(username, k -> new HashSet<>()).add(queryType);
        System.out.println("Granted permission: " + queryType + " to user: " + username);
    }
    
    // Fixed parameter type from ParsedQuery to QueryType
    public void revokePermission(String username, QueryType queryType) {
        Set<QueryType> userQueryTypes = userPermissions.get(username);
        if (userQueryTypes != null) {
            userQueryTypes.remove(queryType);
            System.out.println("Revoked permission: " + queryType + " from user: " + username);
        }
    }
    
    public Set<QueryType> getUserPermissions(String username) {
        return userPermissions.getOrDefault(username, userPermissions.get("default"));
    }
    
    // Removed duplicate hasAccess method - you had two methods with the same signature
}


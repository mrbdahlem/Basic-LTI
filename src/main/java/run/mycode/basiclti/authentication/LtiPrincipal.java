package run.mycode.basiclti.authentication;

import java.security.Principal;
import java.util.List;
import org.imsglobal.lti.launch.LtiUser;

/**
 * An authenticated user that has launched the LTI tool verified with 
 * credentials provided through a known tool consumer
 * 
 * @author bdahl
 */
public class LtiPrincipal implements Principal {
    private final String name;
    private final LtiUser user;
    
    public LtiPrincipal(LtiUser user, String name) {
        this.user = user;
        this.name = name;
    }
        
    @Override
    public String getName() {
        return name;    
    }
    
    /**
     * Get the user id provided by the tool consumer
     * 
     * @return the tool consumer's id for this user 
     */
    public String getId() {
        return user.getId();
    }
    
    /**
     * Get the list of all the roles granted to this user in this launch of the
     * tool
     * 
     * @return The list of all roles the tool consumer assigned to the user
     *         for this launch
     */
    public List<String> getRoles() {
        return user.getRoles();
    }
    
    /**
     * Determine if the tool consumer has granted the user a particular role
     * for this launch off the tool
     * 
     * @param role the role to check for
     * 
     * @return true if the tool consumer granted the user the specified role
     */
    public boolean hasRole(String role) {
        return user.getRoles().stream()
                .anyMatch(userrole -> (userrole.equalsIgnoreCase(role)));
    }
}

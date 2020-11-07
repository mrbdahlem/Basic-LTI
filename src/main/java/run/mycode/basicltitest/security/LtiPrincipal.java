package run.mycode.basicltitest.security;

import java.security.Principal;
import java.util.List;
import org.imsglobal.lti.launch.LtiUser;

/**
 *
 * @author bdahl
 */
public class LtiPrincipal implements Principal {
    private final String name;
    private final LtiUser user;
    
    public LtiPrincipal(LtiUser user, LtiLaunchData launchData) {
        this.user = user;
        
        if (launchData.get("lis_person_name_full") != null) {
            name = launchData.get("lis_person_name_full");
        }
        else {
            String fname = launchData.get("lis_person_name_given");
            String lname = launchData.get("lis_person_name_family");

            if (fname != null && lname != null) {
                name = fname + " " + lname;
            }
            else if (fname != null) {
                name = fname;
            }
            else if (lname != null) {
                name = lname;
            }
            else {
                name = null;
            }
        }
    }
    
    @Override
    public String getName() {
        return name;    
    }
    
    public String getId() {
        return user.getId();
    }
    
    public List<String> getRoles() {
        return user.getRoles();
    }
}

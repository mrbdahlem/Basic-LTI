package run.mycode.basiclti.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import run.mycode.basiclti.model.LtiLaunchData;

/**
 *
 * TODO: Move controller and related demo files to a separate project
 * @author dahlem.brian
 */
@Controller
public class LtiController {
    private static final Logger LOG = LogManager.getLogger(LtiController.class);
        
    @PostMapping(value = "/lti/test")
    public String ltiEntry(HttpServletRequest request,  
            Authentication auth, LtiLaunchData data) {
        
        // A new launch means any session we had should be invalidated
        HttpSession session = request.getSession();
        session.invalidate();
        
        // Create a new session and store the launch data in it
        session = request.getSession();
        session.setAttribute(LtiLaunchData.NAME, data);
        
        // Log the launch
        LOG.info("Launch for Context id: " + data.getContext_id() +
                " User: " + auth.getName() + 
                " Roles: " + 
                auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .reduce("", String::concat) + " ");
        
        return "success";
    }
    
}

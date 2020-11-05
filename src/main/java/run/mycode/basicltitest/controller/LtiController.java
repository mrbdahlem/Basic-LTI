package run.mycode.basicltitest.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import run.mycode.basicltitest.persistence.model.User;

/**
 *
 * @author dahlem.brian
 */
@Controller
public class LtiController {
    private static final Logger LOG = LogManager.getLogger(LtiController.class);
        
    @PostMapping(value = "/lti/test")
    public String ltiEntry(HttpServletRequest request, Authentication auth) {
        //LtiLaunch launch = result.getLtiLaunchResult();
        
        LOG.info("User: " + ((User)auth.getPrincipal()).getName());

        LOG.info("Roles: " + 
                auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .reduce("", String::concat));
        
        return "success";
    }
    
}

package run.mycode.basicltitest.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imsglobal.lti.launch.LtiLaunch;
import org.imsglobal.lti.launch.LtiOauthVerifier;
import org.imsglobal.lti.launch.LtiVerificationException;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.imsglobal.lti.launch.LtiVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import run.mycode.basicltitest.service.KeyService;

/**
 *
 * @author dahlem.brian
 */
@Controller
public class LtiController {
    private static final Logger LOG = LogManager.getLogger(LtiController.class);
    
    @Autowired
    KeyService keyService;
    
    @PostMapping(value = "/ltitest")
    public String ltiEntry(HttpServletRequest request) {
        LtiVerificationResult result;
        try {
            result = verifyLtiRequest(request);
        }
        catch (LtiVerificationException ex) {
            return "error";
        }
        
        LtiLaunch launch = result.getLtiLaunchResult();
        LOG.info("User: " + launch.getUser().getId());

        LOG.info("Roles: " + 
                launch.getUser().getRoles().stream().reduce("", String::concat));

        return "success";
    }
    
    private LtiVerificationResult verifyLtiRequest(HttpServletRequest request) 
            throws LtiVerificationException {

        LtiVerifier ltiVerifier = new LtiOauthVerifier();
        String consumerkey = request.getParameter("oauth_consumer_key");        
        String secret = keyService.getSecretForKey(consumerkey);
        
        return ltiVerifier.verify(request, secret);
    }
}

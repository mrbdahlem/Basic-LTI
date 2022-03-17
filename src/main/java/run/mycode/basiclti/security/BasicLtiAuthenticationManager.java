package run.mycode.basiclti.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import run.mycode.basiclti.authentication.LtiAuthentication;
import run.mycode.basiclti.authentication.LtiKey;
import run.mycode.basiclti.authentication.LtiPrincipal;

/**
 * A simple authentication manager that authenticates all complete tokens
 * 
 * @author dahlem.brian
 */
public class BasicLtiAuthenticationManager implements AuthenticationManager {

    private static final Logger LOG = LogManager.getLogger(BasicLtiAuthenticationManager.class);
    
    @Override
    public Authentication authenticate(Authentication a) throws AuthenticationException {
        LtiKey key = (LtiKey)a.getCredentials();
        LtiPrincipal principal = (LtiPrincipal)a.getPrincipal();
        
        if (key == null) {
            LOG.info("No LTI Credentials present");
            throw new BadCredentialsException("LTI Verification Failed");
        }
        
        if (principal == null) {
            LOG.info("No LTI Credentials present");
            throw new LtiAuthenticationException("LTI Verification Failed");
        }
        
        return new LtiAuthentication(key, principal, true);
    }
    
}

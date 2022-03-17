package run.mycode.basiclti.security;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author dahlem.brian
 */
public class LtiAuthenticationException extends AuthenticationException {
    
    public LtiAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public LtiAuthenticationException(String msg) {
        super(msg);
    }
}

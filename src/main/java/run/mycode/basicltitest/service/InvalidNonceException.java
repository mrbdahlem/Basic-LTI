package run.mycode.basicltitest.service;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown if an authentication request is rejected because the nonce has already
 * been used or does not fall within the valid time window of the current server
 * time
 * 
 * @author dahlem.brian
 */
public class InvalidNonceException extends AuthenticationException {
    /**
     * Constructs an InvalidNonceException with the specified message and root
     * cause
     * 
     * @param msg the detail message
     * @param t root cause
     */
    public InvalidNonceException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an InvalidNonceException with the specified message
     * @param msg the detail message
     */
    public InvalidNonceException(String msg) {
        super(msg);
    }
    
}

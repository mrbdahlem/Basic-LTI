package run.mycode.basiclti.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imsglobal.lti.launch.LtiLaunch;
import org.imsglobal.lti.launch.LtiOauthVerifier;
import org.imsglobal.lti.launch.LtiVerificationException;
import org.imsglobal.lti.launch.LtiVerificationResult;
import org.imsglobal.lti.launch.LtiVerifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import run.mycode.basiclti.persistence.model.LtiKey;
import run.mycode.basiclti.service.InvalidNonceException;
import run.mycode.basiclti.service.KeyService;
import run.mycode.basiclti.service.NonceService;

/**
 * LTI Authentication Processing Filter. This filter should be applied to
 * requests for resources requested from an LTI Tool Provider (learning tool) 
 * by a Tool Consumer (LMS)
 * 
 * @author dahlem.brian
 */
@Component
public class LtiAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(LtiAuthenticationProcessingFilter.class);
    private final KeyService keyService;
    private final NonceService nonceService;
    
    /**
     * Construct an LTI filter using keys verified by a provided key service
     * and nonces tracked by the provided nonce service
     * 
     * @param keyService A service that can provide consumer secrets when given
     *                  the request's consumer key
     * @param nonceService A service that can verify that nonces are not reused
     */
    public LtiAuthenticationProcessingFilter(KeyService keyService, 
            NonceService nonceService) {
        this.keyService = keyService;
        this.nonceService = nonceService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws AuthenticationException, ServletException, IOException {
                Authentication auth;
        
        LtiKey key;
        LtiLaunch launch;
        LtiVerificationResult result;
        
        // If the request has not already been authenticated
        Authentication preAuth = SecurityContextHolder.getContext().getAuthentication();
        if (preAuth == null || !preAuth.isAuthenticated()) {
            String consumerKey = request.getParameter("oauth_consumer_key");        
            
            // load the information for the consumer key associated with the
            // resource request
            key = keyService.getKeyInfo(consumerKey);

            // If the key is not known, quit with an error
            if (key == null) {
                LOG.info("Invalid LTI Consumer Key");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }
                
            try {
                // Verify the nonce in this request has not been reused when
                // paired with the timestamp to prevent replay attacks
                String nonce = request.getParameter("oauth_nonce");
                long timestamp = Long.parseLong(request.getParameter("oauth_timestamp"));
                nonceService.validateNonce(consumerKey, nonce, timestamp);

                // Verify that the LTI request has been properly signed
                LtiVerifier ltiVerifier = new LtiOauthVerifier();
                result = ltiVerifier.verify(request, key.getSecret());
            }
            
            // If an error occurrs, or the verification is not successful,
            // send an error message and quit
            catch (InvalidNonceException e) {
                LOG.info("Nonce validation failed: " + e.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }
            catch (LtiVerificationException e) {
                LOG.info("LTI Verification failed: " + e.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }
            
            if (!result.getSuccess()) {
                LOG.info("LTI Verification failed");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }

            // If the checks passed, we have a valid launch request
            launch = result.getLtiLaunchResult();
            
            // Extract the launch data from the http request
            LtiLaunchData data = new LtiLaunchData(request);
            
            // Get the user information from the launch data, build into an
            // authenticated user
            LtiPrincipal user = new LtiPrincipal(launch.getUser(), data);
            auth = new LtiAuthentication(key, user, true);
            SecurityContextHolder.getContext().setAuthentication(auth);
    
            // Add the launch data to the http session
            HttpSession session = request.getSession();
            session.setAttribute(LtiLaunchData.NAME, data);
    
            LOG.info("LTI Verification succeeded");
        }
        
        // Continue through the filter chain
        chain.doFilter(request, response);
    }
}

package run.mycode.basiclti.security;

import run.mycode.basiclti.authentication.LtiAuthentication;
import run.mycode.basiclti.authentication.LtiPrincipal;
import run.mycode.basiclti.model.LtiLaunchData;
import java.io.IOException;
import java.util.logging.Level;
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
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import run.mycode.basiclti.authentication.LtiKey;
import run.mycode.basiclti.service.InvalidNonceException;
import run.mycode.basiclti.service.NonceService;
import run.mycode.basiclti.service.LtiKeyService;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

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
    
    private final LtiKeyService keyService;
    private final NonceService nonceService;
    
    /**
     * Construct an LTI filter using keys verified by a provided key service
     * and nonces tracked by the provided nonce service
     * 
     * @param keyService A service that can provide consumer secrets when given
     *                  the request's consumer key
     * @param nonceService A service that can verify that nonces are not reused
     */
    public LtiAuthenticationProcessingFilter(LtiKeyService keyService, 
            NonceService nonceService) {
        
        if (keyService == null) {
            throw new IllegalArgumentException("KeyService must be specified");
        }
        if (nonceService == null) {
            throw new IllegalArgumentException("NonceService must be specified");
        }
        
        this.keyService = keyService;
        this.nonceService = nonceService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws AuthenticationException, ServletException, IOException {
        
        LtiLaunch launch;
        LtiVerificationResult result;
        
        
        Authentication preAuth = SecurityContextHolder.getContext().getAuthentication();
        // If the request is a POST request 
        if (HttpMethod.POST.matches(request.getMethod()) &&
                // that has not already been authenticated
                (preAuth == null || 
                !preAuth.isAuthenticated() ||
                // Or the request is a new lti launch request
                "basic-lti-launch-request".equalsIgnoreCase(request.getParameter("lti_message_type"))))
        {
            String consumerKey = request.getParameter("oauth_consumer_key");
            
            // If there is no consumer key
            if (consumerKey == null) {
                // If this is a new lti launch request, then verification has failed
                if ("basic-lti-launch-request".equalsIgnoreCase(request.getParameter("lti_message_type"))) {
                    LOG.info("Missing LTI Consumer Key");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "LTI Verification failed");
                    return;
                }
                
                // Continue through the filter chain
                chain.doFilter(request, response);
                return;
            }
            
            // load the information for the consumer key associated with the
            // resource request
            LtiKey credential = keyService.getKey(consumerKey);
            
            // If the key is not known, quit with an error
            if (credential == null) {
                LOG.info("Invalid LTI Consumer Key");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "LTI Verification failed");
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
                result = ltiVerifier.verify(request, credential.getSecret());
            }
            // If an error occurrs, or the verification is not successful,
            // send an error message and quit
            catch (InvalidNonceException e) {
                LOG.info("Nonce validation failed: " + e.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "LTI Verification failed");
                return;
            }
            catch (LtiVerificationException e) {
                LOG.info("LTI Verification failed: " + e.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "LTI Verification failed");
                return;
            }
            
            if (!result.getSuccess()) {
                LOG.info("LTI Verification failed");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }

            // If the checks passed, we have a valid launch request
            launch = result.getLtiLaunchResult();
            
            String name = LtiLaunchData.getName(request);
            
            // Get the user information from the launch data, build into an
            // authenticated user
            LtiPrincipal user = new LtiPrincipal(launch.getUser(), name);
            Authentication auth = new LtiAuthentication(credential, user, true);
            
            HttpSession session = request.getSession();
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
        
            LOG.info("LTI Verification succeeded");
        }
        
        // Continue through the filter chain
        chain.doFilter(request, response);
    }
}

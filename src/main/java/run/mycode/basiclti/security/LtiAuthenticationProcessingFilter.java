package run.mycode.basiclti.security;

import run.mycode.basiclti.authentication.LtiAuthentication;
import run.mycode.basiclti.authentication.LtiPrincipal;
import run.mycode.basiclti.model.LtiLaunchData;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;
import run.mycode.basiclti.authentication.LtiKey;
import run.mycode.basiclti.service.InvalidNonceException;
import run.mycode.basiclti.service.NonceService;
import run.mycode.basiclti.service.LtiKeyService;

/**
 * LTI Authentication Processing Filter. This filter should be applied to
 * requests for resources requested from an LTI Tool Provider (learning tool) 
 * by a Tool Consumer (LMS)
 * 
 * @author dahlem.brian
 */
@Component
public class LtiAuthenticationProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
    
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
            NonceService nonceService) 
    {
        super();
        
        if (keyService == null) {
            throw new IllegalArgumentException("KeyService must be specified");
        }
        if (nonceService == null) {
            throw new IllegalArgumentException("NonceService must be specified");
        }
        
        this.keyService = keyService;
        this.nonceService = nonceService;
        
        // The principal may change on each LTI launch
        setCheckForPrincipalChanges(true);
        setInvalidateSessionOnPrincipalChange(true);
        
    }
    
    /**
     * Retrieve or verify the LTI authentication for a given request
     * @param request the POST request to verify
     * 
     * @return An Authentication containing the LTI Principal and LTI Key credentials
     * @throws AuthenticationException if LTI Verification fails
     */
    private Authentication getAuth(HttpServletRequest request)
            throws AuthenticationException {
        
        LtiLaunch launch;
        LtiVerificationResult result;
        
        // If this request has already been authenticated
        Authentication auth = (Authentication)request.getAttribute("LTI_AUTH");
        if (auth != null) {
            // Return the authentication token
            return auth;
        }
        
        auth = SecurityContextHolder.getContext().getAuthentication();
        
        // a new LTI authentication can only happen on POST requests
        if (!HttpMethod.POST.matches(request.getMethod())) {
            setContinueFilterChainOnUnsuccessfulAuthentication(true);
            return auth;
        }
        
        // lti launch requests MUST be authenticated
        String launchType = request.getParameter("lti_message_type");
        if ("basic-lti-launch-request".equalsIgnoreCase(launchType)) {
            setContinueFilterChainOnUnsuccessfulAuthentication(false);
            SecurityContextHolder.clearContext();
        }
        
        String consumerKey = request.getParameter("oauth_consumer_key");

        // If there is no consumer key
        if (consumerKey == null) {
            throw new LtiAuthenticationException("No consumer key provided");
        }

        // load the information for the consumer key associated with the
        // resource request
        LtiKey credential = keyService.getKey(consumerKey);

        // If the key is not known, quit with an error
        if (credential == null) {
            LOG.info("Invalid LTI Consumer Key");
            throw new LtiAuthenticationException("LTI Verification Failed");
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
            throw new LtiAuthenticationException("LTI Verification Failed");
        }
        catch (LtiVerificationException e) {
            LOG.info("LTI Verification failed: " + e.getLocalizedMessage());
            throw new LtiAuthenticationException("LTI Verification Failed");
        }

        if (!result.getSuccess()) {
            LOG.info("LTI Verification failed");
            throw new LtiAuthenticationException("LTI Verification Failed");
        }

        // If the checks passed, we have a valid launch request
        launch = result.getLtiLaunchResult();

        String name = LtiLaunchData.getName(request);

        // Get the user information from the launch data, build into an
        // authenticated user
        LtiPrincipal user = new LtiPrincipal(launch.getUser(), name);
        auth = new LtiAuthentication(credential, user, true);

        request.setAttribute("LTI_AUTH", auth);
        LOG.info("LTI Verification succeeded");
        
        return auth;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) { 
        
        Authentication auth = getAuth(request);
        if (auth == null) {
            LOG.info ("NULL Authentication token " + request.getServletPath());
            return null;
        }
        if (auth.getPrincipal() == null) {
            LOG.info ("NULL Authentication principal");
            return null;
        }
        LOG.info ("Retrieved principal");
        
        return auth.getPrincipal();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {

        Authentication auth = getAuth(request);
        if (auth == null) {
            LOG.info ("NULL Authentication token " + request.getServletPath());
            return null;
        }
        if (auth.getCredentials() == null) {
            LOG.info ("NULL Authentication credentials");
            return null;
        }
        LOG.info ("Retrieved credentials");
        
        return auth.getCredentials();
    }
}

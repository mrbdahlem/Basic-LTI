package run.mycode.basicltitest.security;

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
import run.mycode.basicltitest.persistence.model.LtiKey;
import run.mycode.basicltitest.service.InvalidNonceException;
import run.mycode.basicltitest.service.KeyService;
import run.mycode.basicltitest.service.NonceService;

/**
 *
 * @author dahlem.brian
 */
@Component
public class LtiAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(LtiAuthenticationProcessingFilter.class);
    private final KeyService keyService;
    private final NonceService nonceService;
    
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

        Authentication preAuth = SecurityContextHolder.getContext().getAuthentication();
        if (preAuth == null || !preAuth.isAuthenticated()) {
            try {
                LtiVerifier ltiVerifier = new LtiOauthVerifier();
                String consumerKey = request.getParameter("oauth_consumer_key");        
                LtiKey key = keyService.getKeyInfo(consumerKey);

                if (key == null) {
                    LOG.info("Invalid LTI Consumer Key");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                    return;
                }
                
                String nonce = request.getParameter("oauth_nonce");
                long timestamp = Long.parseLong(request.getParameter("oauth_timestamp"));
                
                nonceService.validateNonce(consumerKey, nonce, timestamp);

                LtiVerificationResult result = ltiVerifier.verify(request, key.getSecret());

                if (!result.getSuccess()) {
                    LOG.info("LTI Verification failed");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                    return;
                }

                LtiLaunch launch = result.getLtiLaunchResult();

                auth = new LtiAuthentication(key, launch, true);
            }
            catch (LtiVerificationException e) {
                LOG.info("LTI Verification failed");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }
            catch (InvalidNonceException e) {
                LOG.info("Nonce validation failed: " + e.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "LTI Verification failed");
                return;
            }
            
            HttpSession session = request.getSession();
            session.setAttribute(LtiLaunchData.NAME, new LtiLaunchData(request));

            LOG.info("LTI Verification succeeded");
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        chain.doFilter(request, response);
    }
}

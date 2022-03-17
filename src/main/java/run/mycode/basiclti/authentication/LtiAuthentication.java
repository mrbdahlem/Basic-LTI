package run.mycode.basiclti.authentication;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * A Spring Authentication for LTI invocations, associating a tool user 
 * (student, teacher, etc) with the LtiKey that authorizes their use of the tool
 * 
 * @author dahlem.brian
 */
public class LtiAuthentication implements Authentication {
    private final LtiKey key;
    private final LtiPrincipal principal;
    private boolean authenticated;
    private final List<SimpleGrantedAuthority> authorities;
    
    /**
     * Construct an authentication token for an LTI launch
     * 
     * @param credential the key/secret pair that authorized the launch
     * @param principal the user (student or instructor) that launched the tool
     * @param authenticated true if the credential was valid
     */
    public LtiAuthentication(LtiKey credential, LtiPrincipal principal,
            boolean authenticated) {
        
        this.key = credential;
        this.principal = principal;
        this.authenticated = authenticated;
        this.authorities = principal.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return key;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        if (authenticated == false) {
            this.authenticated = false;
        }
        else {
            throw new IllegalArgumentException("Cannot authenticate unauthenticated token");
        }
    }

    @Override
    public String getName() {
        return principal.getName();
    }
}

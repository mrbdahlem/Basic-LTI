package run.mycode.basicltitest.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import run.mycode.basicltitest.persistence.model.LtiKey;

/**
 *
 * @author dahlem.brian
 */
public class LtiAuthentication implements Authentication {
    private final LtiKey key;
    private final LtiPrincipal principal;
    private boolean authenticated;
    private final List<SimpleGrantedAuthority> authorities;
    
    public LtiAuthentication(LtiKey key, LtiPrincipal principal,
            boolean authenticated) {
        
        this.key = key;
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
            throw new IllegalArgumentException("Cannot raise authenticate");
        }
    }

    @Override
    public String getName() {
        return principal.getName();
    }
}

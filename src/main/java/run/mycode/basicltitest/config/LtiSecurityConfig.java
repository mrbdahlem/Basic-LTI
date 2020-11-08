package run.mycode.basicltitest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import run.mycode.basicltitest.security.LtiAuthenticationProcessingFilter;
import run.mycode.basicltitest.service.KeyService;
import run.mycode.basicltitest.service.NonceService;

@Configuration
@Order(1)
public class LtiSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private KeyService keyService;
    
    @Autowired
    private NonceService nonceService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .ignoringAntMatchers("/lti/**") // LTI launches are made using HTTP POST, but won't have csrf tokens so must be ignored -- nonce should eliminate csrf
            .and()
            .antMatcher("/lti/**")
                .headers().frameOptions().disable() // Allow launches in iframes
            .and()
                .addFilterBefore(new LtiAuthenticationProcessingFilter(keyService, nonceService),
                        UsernamePasswordAuthenticationFilter.class) // Authenticate LTI launches before requiring username/password
            .authorizeRequests()
                .antMatchers("/lti/**") // Launches will be made on any of the resources below lti
                .authenticated()        // and must be authenticated (by the filter)
                ;
    }
}
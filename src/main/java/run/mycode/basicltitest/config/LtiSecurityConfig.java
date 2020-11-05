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
        http.csrf().ignoringAntMatchers("/lti/**")
            .and()
            .antMatcher("/lti/**")
                .headers().frameOptions().disable()
            .and()
                .addFilterBefore(new LtiAuthenticationProcessingFilter(keyService, nonceService),
                        UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers("/lti/**")
                .permitAll()
                ;
    }
    
//    @Bean
//    public LtiAuthenticationProcessingFilter ltiProcessingFilter() {
//        return new LtiAuthenticationProcessingFilter(keyService);        
//    }
}
package run.mycode.basiclti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class BasicLtiTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicLtiTestApplication.class, args);
    }
}

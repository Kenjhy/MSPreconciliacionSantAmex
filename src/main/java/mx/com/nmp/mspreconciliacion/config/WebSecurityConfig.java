package mx.com.nmp.mspreconciliacion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // For example: Use only Http Basic and not form login.
        http.csrf().disable().authorizeRequests().anyRequest().
                permitAll().and().logout().deleteCookies("rememberme").
                permitAll().and().rememberMe().tokenValiditySeconds(160);

    }

}

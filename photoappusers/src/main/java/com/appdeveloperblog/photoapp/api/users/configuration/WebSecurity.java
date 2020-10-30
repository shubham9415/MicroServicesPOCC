package com.appdeveloperblog.photoapp.api.users.configuration;

import com.appdeveloperblog.photoapp.api.users.service.CreateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity  extends WebSecurityConfigurerAdapter {

    Environment environment;

    CreateUserService createUserService;

    BCryptPasswordEncoder bCryptPasswordEncoder;



    @Autowired
    WebSecurity(Environment environment, CreateUserService createUserService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.environment=environment;
        this.createUserService = createUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("The API gateway is:" + environment.getProperty("spring.gateway.ipAddress"));
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/**").hasIpAddress(environment.getProperty("spring.gateway.ipAddress"))
                .and()
                /**
                 * An custom filter was created, which would
                 * help us in creating our custom login functionality
                 * so mentioning the filter which was created by
                 * using the add filter method.
                 */
        .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(createUserService, environment, authenticationManager());
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url"));
        /*authenticationFilter.setAuthenticationManager(authenticationManager());*/
        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(createUserService).passwordEncoder(bCryptPasswordEncoder);
    }
}

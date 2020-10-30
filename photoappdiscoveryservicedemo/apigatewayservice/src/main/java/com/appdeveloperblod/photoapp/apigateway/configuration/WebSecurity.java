package com.appdeveloperblod.photoapp.apigateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    Environment environment;

    @Autowired
    WebSecurity(Environment environment) {
        this.environment = environment;

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("H2-console is:" + environment.getProperty("gateway.service.h2console"));
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers(environment.getProperty("gateway.service.actuator.url")).permitAll()
                .antMatchers(environment.getProperty("gateway.service.h2console")).permitAll()
                .antMatchers(HttpMethod.POST, environment.getProperty("gateway.service.loginUrl")).permitAll()
                .antMatchers(HttpMethod.POST, environment.getProperty("gateway.service.registrationUrl")).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new AutherizationFilter(authenticationManager(), environment));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}

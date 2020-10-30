package com.appdeveloperblod.photoapp.apigateway.configuration;

import io.jsonwebtoken.Jwts;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AutherizationFilter extends BasicAuthenticationFilter {

    Environment environment;

    public AutherizationFilter(AuthenticationManager authenticationManager, Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestHeader = request.
                getHeader(environment.getProperty("gateway.service.authorization.header.value"));
        if(null == requestHeader || !requestHeader.
                startsWith(environment.getProperty("gateway.service.authorization.header.prefix"))){
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken = getAuthorizationHeader(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.
                getHeader(environment.getProperty("gateway.service.authorization.header.value"));
        if(null == authorizationHeader){
            return null;
        }
        String token = authorizationHeader.
                replace(environment.getProperty("gateway.service.authorization.header.prefix"), "");
        String userid = Jwts.parser()
                .setSigningKey(environment.getProperty("gateway.token.secret"))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        if(null == userid){
            return null;
        }
        return new UsernamePasswordAuthenticationToken(userid, null, new ArrayList<>());
    }
}

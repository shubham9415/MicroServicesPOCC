package com.appdeveloperblog.photoapp.api.users.configuration;

import com.appdeveloperblog.photoapp.api.users.model.CreateUserModel;
import com.appdeveloperblog.photoapp.api.users.model.LoginUserModel;
import com.appdeveloperblog.photoapp.api.users.service.CreateUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    CreateUserService createUserService;

    Environment environment;

    AuthenticationFilter(CreateUserService createUserService, Environment environment, AuthenticationManager authenticationManager) {
        this.createUserService = createUserService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginUserModel loginUserModel = null;
        try {
            loginUserModel = new ObjectMapper().readValue(request.getInputStream(), LoginUserModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(loginUserModel.getEmail(),
                loginUserModel.getPassword(), new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName = ((User) authResult.getPrincipal()).getUsername();
        CreateUserModel createUserModel = createUserService.getUserDetailsByEmail(userName);
        String userToken = Jwts.builder()
                .setSubject(createUserModel.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration.time"))))
                /**
                 * THis is being done here to Authorize with a particular
                 * String, yes I have signed it with this value and now I want
                 * when I will login again with this token then Can have the
                 * signing string again just to add an extra layer of security.
                 */
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("token.secret"))
                .compact();
        response.addHeader("token", userToken);
        response.addHeader("userId", createUserModel.getId().toString());
    }
}

package com.appdeveloperblog.photoapp.api.users.ui.controllers;

import com.appdeveloperblog.photoapp.api.users.configuration.AuthenticationFilter;
import com.appdeveloperblog.photoapp.api.users.model.CreateUserModel;
import com.appdeveloperblog.photoapp.api.users.model.UserResponseModel;
import com.appdeveloperblog.photoapp.api.users.service.CreateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Method;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    CreateUserService createUserService;

    @Autowired
    Environment environment;

    @GetMapping("/status")
    public String status(){
        System.out.println(environment.getProperty("token.secret"));
        System.out.println(environment.getProperty("gateway.token.secret"));
        return "Working" + environment.getProperty("token.secret");
    }

    @PostMapping
    public ResponseEntity<CreateUserModel> createUser(@Valid @RequestBody CreateUserModel createUser){

        Method []methods = AuthenticationFilter.class.getMethods();
        CreateUserModel createUserModel =createUserService.save(createUser);
        ResponseEntity<CreateUserModel> createUserModelResponseEntity = new ResponseEntity<CreateUserModel>(createUserModel, HttpStatus.CREATED);
        return  createUserModelResponseEntity;

    }

    @GetMapping("/{userId}/details")
    public ResponseEntity<UserResponseModel> getUserDetails(@PathVariable String userId){
        UserResponseModel userResponseModel =createUserService.getUserDetailsById(userId);
        return  new ResponseEntity<>(userResponseModel, HttpStatus.FOUND);
    }
}

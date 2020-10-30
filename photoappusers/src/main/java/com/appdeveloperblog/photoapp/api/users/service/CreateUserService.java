package com.appdeveloperblog.photoapp.api.users.service;

import com.appdeveloperblog.photoapp.api.users.model.CreateUserModel;
import com.appdeveloperblog.photoapp.api.users.model.UserResponseModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CreateUserService extends UserDetailsService {

     CreateUserModel save(CreateUserModel createUserModel) ;
     CreateUserModel getUserDetailsByEmail(String username);

     UserResponseModel getUserDetailsById(String userId);
}

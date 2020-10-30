package com.appdeveloperblog.photoapp.api.users.service;

import com.appdeveloperblog.photoapp.api.users.Repository.AlbumServiceClient;
import com.appdeveloperblog.photoapp.api.users.Repository.CreateUserRepository;
import com.appdeveloperblog.photoapp.api.users.model.AlbumsResponseModel;
import com.appdeveloperblog.photoapp.api.users.model.CreateUserModel;
import com.appdeveloperblog.photoapp.api.users.model.UserResponseModel;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreateUserServiceImpl implements  CreateUserService{

    @Autowired
    CreateUserRepository createUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Environment environment;

    @Autowired
    AlbumServiceClient albumServiceClient;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public CreateUserModel save(CreateUserModel inputCreateUserModel) {

        CreateUserModel createUserModel = new CreateUserModel();
        createUserModel.setFirstName(inputCreateUserModel.getFirstName());
        createUserModel.setLastName(inputCreateUserModel.getLastName());
        createUserModel.setEmail(inputCreateUserModel.getEmail());
        createUserModel.setPassword(bCryptPasswordEncoder.encode(inputCreateUserModel.getPassword()));
        return   createUserRepository.save(createUserModel);

    }

    @Override
    public CreateUserModel getUserDetailsByEmail(String username) {
        CreateUserModel createUserModel =createUserRepository.findByEmail(username);
        return  createUserModel;
    }

    @Override
    public UserResponseModel getUserDetailsById(String userId) {
        CreateUserModel createUserModel = createUserRepository.findById(Long.parseLong(userId)).get();
        if(null== createUserModel){
            throw new UsernameNotFoundException(userId);
        }
        String albumsUrl = String.format(environment.getProperty("albums.microservice.url"),userId);
        /*List<AlbumsResponseModel> albumsResponseModelList = callAlbumsMicroService(albumsUrl);*/
        List<AlbumsResponseModel> albumsResponseModelList = null;
        try{
            logger.info("Before the Albums Microservice Call");
            albumsResponseModelList = albumServiceClient.getAlbums(userId);
            logger.info("After the Albums Microservice Call");
        }
        catch (FeignException e){
            logger.error(e.getLocalizedMessage());
        }
        UserResponseModel userResponseModel = new UserResponseModel();
        userResponseModel.setEmail(createUserModel.getEmail());
        userResponseModel.setFirstName(createUserModel.getFirstName());
        userResponseModel.setLastName(createUserModel.getLastName());
        userResponseModel.setAlbumsResponseModelList(albumsResponseModelList);
        return userResponseModel;
    }

    private List<AlbumsResponseModel> callAlbumsMicroService(String albumsUrl) {
        ResponseEntity<List<AlbumsResponseModel>> listResponseEntity = restTemplate.exchange(albumsUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AlbumsResponseModel>>(){});
        return listResponseEntity.getBody();
    }

    /**
     * The method is being called by spring framework
     * itself, actually spring have created a default class User
     * which has various attributes like account is expired or not and many others to help us.
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        CreateUserModel createUserModel =createUserRepository.findByEmail(s);
        if(createUserModel == null){
            throw  new UsernameNotFoundException("User Name Doesn't Exist in DataBase, Please check!");
        }
        User user = new User(createUserModel.getEmail(), createUserModel.getPassword(),
                true,
                true,
                true,
                true,
                new ArrayList<>());
        return user;
    }
}

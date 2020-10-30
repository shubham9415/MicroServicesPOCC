package com.appdeveloperblog.photoapp.api.users.Repository;

import com.appdeveloperblog.photoapp.api.users.model.CreateUserModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CreateUserRepository extends CrudRepository<CreateUserModel,Long > {

    CreateUserModel findByEmail(String s);
    Optional<CreateUserModel> findById(Long id);
}

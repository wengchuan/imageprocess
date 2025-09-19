package com.imageprocess.service;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;

import java.util.Optional;

public interface UserService {

    public void registerUser(CreateUserDTO createUserDTO);
    public Optional<User> loginUser(CreateUserDTO createUserDTO);
    public Optional<User> findUser();
}

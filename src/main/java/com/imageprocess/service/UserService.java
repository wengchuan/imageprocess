package com.imageprocess.service;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;

public interface UserService {

    public void registerUser(CreateUserDTO createUserDTO);
    public User loginUser(CreateUserDTO createUserDTO);
}

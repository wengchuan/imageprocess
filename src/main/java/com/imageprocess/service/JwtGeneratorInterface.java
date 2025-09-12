package com.imageprocess.service;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;

import java.util.Map;

public interface JwtGeneratorInterface {

    Map<String,String> generateToken(User user);
    Map<String,String> generateToken(CreateUserDTO createUserDTO);
}

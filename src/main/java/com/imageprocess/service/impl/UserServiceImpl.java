package com.imageprocess.service.impl;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;
import com.imageprocess.repository.UserRepository;
import com.imageprocess.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(createUserDTO.getPassword());

        userRepository.save(user);
    }

    @Override
    public User loginUser(CreateUserDTO createUserDTO) {
        return  userRepository.findByUsernameAndPassword(createUserDTO.getUsername(), createUserDTO.getPassword());
    }

    @Override
    public Optional<User> findUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUsername(authentication.getName());
    }
}

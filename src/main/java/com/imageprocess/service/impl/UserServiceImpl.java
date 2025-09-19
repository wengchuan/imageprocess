package com.imageprocess.service.impl;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;
import com.imageprocess.repository.UserRepository;
import com.imageprocess.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    final private UserRepository userRepository;
    final private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    final  private AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void registerUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        userRepository.save(user);
    }

    @Override
    public Optional<User> loginUser(CreateUserDTO createUserDTO) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(createUserDTO.getUsername(),createUserDTO.getPassword()));
        if(authentication.isAuthenticated()){
            return userRepository.findByUsername(createUserDTO.getUsername());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUsername(authentication.getName());
    }
}

package com.imageprocess.controller;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;
import com.imageprocess.service.JwtService;
import com.imageprocess.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtGeneratorInterface;

    @Autowired
    public AuthenticationController(UserService userService, JwtService jwtGeneratorInterface){
        this.userService = userService;
        this.jwtGeneratorInterface = jwtGeneratorInterface;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CreateUserDTO createUserDTO){
        try {
            userService.registerUser(createUserDTO);
            return new ResponseEntity<>(jwtGeneratorInterface.generateToken(createUserDTO),HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody CreateUserDTO createUserDTO){
        User user = userService.loginUser(createUserDTO);
        if(user!=null){
            return ResponseEntity.ok().body(jwtGeneratorInterface.generateToken(createUserDTO));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}

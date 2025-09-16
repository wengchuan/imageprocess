package com.imageprocess.service;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    Map<String,String> generateToken(User user);
    Map<String,String> generateToken(CreateUserDTO createUserDTO);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    <T> T extractClaim(String token, Function<Claims,T> claimsResolver);
}

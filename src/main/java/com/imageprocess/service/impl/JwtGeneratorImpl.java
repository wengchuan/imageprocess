package com.imageprocess.service.impl;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;
import com.imageprocess.service.JwtGeneratorInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtGeneratorImpl implements JwtGeneratorInterface {

    @Value("${secretKey}")
    private String secretKey;


    @Override
    public Map<String, String> generateToken(User user) {
        String jwtToken="";
        jwtToken = Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();

        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        return jwtTokenGen;
    }

    @Override
    public Map<String, String> generateToken(CreateUserDTO createUserDTO) {
        String jwtToken="";
        jwtToken = Jwts.builder()
                .subject(createUserDTO.getUsername())
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();

        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        return jwtTokenGen;
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

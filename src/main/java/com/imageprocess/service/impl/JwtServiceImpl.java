package com.imageprocess.service.impl;

import com.imageprocess.dto.CreateUserDTO;
import com.imageprocess.model.User;
import com.imageprocess.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${secretKey}")
    private String secretKey;

    private long jwtExp=186400000L;


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
                .expiration(new Date(System.currentTimeMillis() + jwtExp))
                .signWith(getSigningKey())
                .compact();

        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        return jwtTokenGen;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return Objects.equals(userDetails.getUsername(), username)&& !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }


    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
         Claims claims = Jwts
                             .parser()
                             .verifyWith((SecretKey) getSigningKey())
                             .build()
                             .parseSignedClaims(token)
                             .getPayload();

        return claimsResolver.apply(claims);
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

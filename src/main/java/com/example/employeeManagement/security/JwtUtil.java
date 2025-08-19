package com.example.employeeManagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private byte[] getSigningKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()  //Bluprint of building token/header/holds all data until compact() called.
                .setSubject(username)
                .claim("role", role)    //eg. Admin
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(getSigningKey()), SignatureAlgorithm.HS256)
                .compact();   //final jwt string
    }

    public String extractUsername(String token) {       //works during request handeling
        return extractClaim(token, Claims::getSubject);//spring security verifies that the requesting user is authorised
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {//Claims=input T =output
        Claims claims = Jwts.parserBuilder()//for decoding the tocken
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)//pparses/verifies token is valid annd untempered
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {// if true token is expired
        Date exp = extractClaim(token, Claims::getExpiration);
        return exp.before(new Date());//if expiry is before current date then it becomes true
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}

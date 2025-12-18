package com.example.donationplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtUtil {
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("mySecretKey12345mySecretKey12345".getBytes());
    private static final long JWT_EXPIRATION = 1000L * 60 * 60 * 3;
    public static String generateToken(long userId, String nickName){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SECRET_KEY)
                .claim("nickName", nickName)
                .compact();
    }
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

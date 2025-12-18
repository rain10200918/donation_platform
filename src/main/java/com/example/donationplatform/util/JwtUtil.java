package com.example.donationplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String JWT_SECRET = "mySecretKey12345";
    private static final long JWT_EXPIRATION = 1000L*60*60*3;

    public static String generateToken(Long userId,String nickname){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .addClaims(Map.of("nickname",nickname))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256,JWT_SECRET)
                .compact();
    }

    public static Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.thacbao.codeSphere.configurations;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
@Service
public class JwtUtils {
    @Value("${jwt.secretKey}")
    private String secretKey;

    private static final String REDIS_KEY_PREFIX = "jwt:";
    private static final Long EXPIRATION_TIME = 10 * 60 * 60 * 1000L;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateToken(String username, Map<String, Object> claim){
        if(redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + username) != null){ // neu token da duoc cache-> tra ve cache da luu
            System.out.println("Caching token: " + username);
            return redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + username);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        for (Map.Entry<String, Object> entry : claim.entrySet()) {
            claims.put(entry.getKey(), entry.getValue());
        }
        String token = createToken(claims, username);
        saveToken(token, username); // cache rong -> tao moi token va push vao cache
        return token;
    }

    private void saveToken(String token, String username){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(REDIS_KEY_PREFIX + username, token, EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims getClaimsFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token){
        return getExpirationDateFromToken(token).before(new Date());
    }

    public Boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails){
        return !isTokenExpired(token) && userDetails.getUsername().equals(getUsernameFromToken(token));
    }
}

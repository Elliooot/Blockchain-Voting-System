package com.voting.spring_boot_project.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.voting.spring_boot_project.entity.User; // <-- 確保已 import

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        System.out.println("Generating token for user: " + userDetails.getUsername());


        if (userDetails instanceof User) {
            User user = (User) userDetails;

            extraClaims.put("firstName", user.getFirstName());
            extraClaims.put("lastName", user.getLastName());
            extraClaims.put("email", user.getEmail());
            extraClaims.put("gender", user.getGender());
            extraClaims.put("dateOfBirth", user.getDateOfBirth());
            extraClaims.put("userId", user.getId());
            System.out.println("Successfully put user details into extraClaims");
        }

        userDetails.getAuthorities().stream()
                .findFirst()
                .ifPresent(authority -> extraClaims.put("role", authority.getAuthority()));
        
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userdetails){
        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(userdetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3)) // 3 days
            .signWith(getSignInKey())
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenValidAndBelongToAdmin(String token, UserDetails userDetails) {
        final String role = extractUserRole(token);
        return (isTokenValid(token, userDetails) && role.equals("ElectoralAdmin"));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .verifyWith((SecretKey) getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

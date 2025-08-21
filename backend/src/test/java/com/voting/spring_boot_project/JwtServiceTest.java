package com.voting.spring_boot_project;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.service.JwtService;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        // Generate a proper HS256 key and inject as base64 into JwtService.secretKey
        String testSecret = "test-jwt-secret-key-minimum-256-bits-required-for-hs256-algorithm";
        byte[] keyBytes = testSecret.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        
        String base64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        
        Field f = JwtService.class.getDeclaredField("secretKey");
        f.setAccessible(true);
        f.set(jwtService, base64);
    }

    @Test
    void generate_and_validate_token() {
        User user = User.builder()
                .id(1)
                .email("test@example.com")
                .password("pwd")
                .role(Role.Voter)
                .build();

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
        assertEquals("Voter", jwtService.extractUserRole(token));
        assertFalse(jwtService.isTokenValidAndBelongToAdmin(token, user));
    }
}

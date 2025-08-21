package com.voting.spring_boot_project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.voting.spring_boot_project.dto.AuthenticationRequest;
import com.voting.spring_boot_project.dto.AuthenticationResponse;
import com.voting.spring_boot_project.dto.RegisterRequest;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.service.AuthenticationService;
import com.voting.spring_boot_project.service.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private BallotRepository ballotRepository;

    @InjectMocks private AuthenticationService authenticationService;

    @Test
    void register_encodes_password_and_returns_token() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("A");
        req.setLastName("B");
        req.setEmail("USER@EXAMPLE.COM");
        req.setPassword("pwd");
        req.setRole(Role.Voter);

        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        AuthenticationResponse resp = authenticationService.register(req);

        assertEquals("token", resp.getToken());
        verify(userRepository).save(argThat(u -> "user@example.com".equals(u.getEmail()) && "ENC".equals(u.getPassword())));
    }

    @Test
    void authenticate_auth_manager_called_and_token_returned() {
        AuthenticationRequest req = new AuthenticationRequest();
        req.setEmail("user@example.com");
        req.setPassword("pwd");

        User user = User.builder().id(1).email("user@example.com").role(Role.Voter).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token");

        AuthenticationResponse resp = authenticationService.authenticate(req);

        assertEquals("token", resp.getToken());
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("user@example.com", "pwd"));
    }
}

package com.voting.spring_boot_project.service;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.dto.AuthenticationRequest;
import com.voting.spring_boot_project.dto.AuthenticationResponse;
import com.voting.spring_boot_project.dto.RegisterRequest;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final BallotRepository ballotRepository;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        assignDemoBallotsToUser(user);
        
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );
        var user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow();
        
        System.out.println("🔵 [AuthService] Generating JWT token...");
        var jwtToken = jwtService.generateToken(user);
        System.out.println("🔵 [AuthService] Token generated successfully");
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void assignDemoBallotsToUser(User user) {
        System.out.println("🎯 assignDemoBallotsToUser - Starting for user: " + user.getEmail());

        List<Integer> demoIds = List.of(1502, 1503, 1504);

        System.out.println("🎯 Parsed demo ballot IDs: " + demoIds);

        for (Integer id : demoIds) {
            ballotRepository.findById(id).ifPresent(ballot -> {
                System.out.println("🎯 Processing ballot ID: " + ballot.getId() + ", Title: " + ballot.getTitle());
                var qv = ballot.getQualifiedVoters();
                boolean exists = qv.stream().anyMatch(u -> u.getId().equals(user.getId()));

                if (!exists) {
                    System.out.println("✅ Adding user " + user.getEmail() + " to ballot " + ballot.getId());
                    qv.add(user);
                    ballotRepository.save(ballot);
                } else {
                    System.out.println("⚠️ User " + user.getEmail() + " already qualified for ballot " + ballot.getId());
                }
            });
        }

        System.out.println("🎯 Demo ballot assignment completed");
    }
}

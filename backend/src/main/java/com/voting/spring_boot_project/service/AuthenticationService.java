package com.voting.spring_boot_project.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${demo.ballot-ids:}")
    private String demoBallotIdsCsv;
    
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
        if (demoBallotIdsCsv == null || demoBallotIdsCsv.isBlank()) return;

        List<Integer> ids = Arrays.stream(demoBallotIdsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        
        var ballots = ballotRepository.findAllById(ids);
        for (var ballot: ballots) {
            var qv = ballot.getQualifiedVoters();
            boolean exists = qv.stream().anyMatch(u -> u.getId().equals(user.getId()));
            if (!exists) {
                qv.add(user);
                ballotRepository.save(ballot);
            }
        }
    }
}

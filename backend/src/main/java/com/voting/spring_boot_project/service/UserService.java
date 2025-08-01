package com.voting.spring_boot_project.service;

import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BallotRepository ballotRepository;

    public void deleteAccount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ballotRepository.deleteAll(ballotRepository.findByAdmin(user));
        userRepository.delete(user);

        System.out.println("User account deleted: " + userEmail);
    }
    
}

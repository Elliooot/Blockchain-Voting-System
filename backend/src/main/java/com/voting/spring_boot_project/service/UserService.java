package com.voting.spring_boot_project.service;

import java.util.ArrayList;
import java.util.List;

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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public void deleteAccount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ballotRepository.deleteAll(ballotRepository.findByAdmin(user)); // If admin account, delete all ballots
        userRepository.delete(user); // Delete user in repo

        System.out.println("User account deleted: " + userEmail);
    }
    
}

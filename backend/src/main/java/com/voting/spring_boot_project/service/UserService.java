package com.voting.spring_boot_project.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.dto.UpdateWalletRequest;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BallotRepository ballotRepository;

    public Map<String, String> getWalletAddress(){
        System.out.println("Get Wallet Address Method Called");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String walletAddress = user.getWalletAddress();
        Map<String, String> response = new HashMap<>();
        response.put("walletAddress", walletAddress);
        return response;
    }

    public Map<String, String> updateWalletAddress(UpdateWalletRequest request){
        System.out.println("Update Wallet Address Method Called");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User's current wallet address: " + user.getWalletAddress());

        System.out.println("Updating wallet address...");
        
        if(request.getWalletAddress() == null || request.getWalletAddress().isEmpty()){
            System.out.println("Disconnect Wallet Address");
            user.setWalletAddress(null);
        } else {
            System.out.println("Update Wallet Address");
            System.out.println("New wallet address: " + request.getWalletAddress());
            user.setWalletAddress(request.getWalletAddress());
        }

        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Wallet address updated successfully");
        response.put("walletAddress", request.getWalletAddress());

        return response;
    }

    public void deleteAccount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ballotRepository.deleteAll(ballotRepository.findByAdmin(user)); // If admin account, delete all ballots
        userRepository.delete(user); // Delete user in repo

        System.out.println("User account deleted: " + userEmail);
    }
    
}

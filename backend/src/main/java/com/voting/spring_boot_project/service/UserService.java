package com.voting.spring_boot_project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting.spring_boot_project.dto.UpdateWalletRequest;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.entity.Vote;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BallotRepository ballotRepository;
    private final VoteRepository voteRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, String> getWalletAddress(){
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
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = auth.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if(request.getWalletAddress() == null || request.getWalletAddress().isEmpty()){
                user.setWalletAddress(null);
            } else {
                user.setWalletAddress(request.getWalletAddress());
            }

            User savedUser = userRepository.save(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Wallet address updated successfully");
            response.put("walletAddress", savedUser.getWalletAddress());

            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteAccount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getRole() == Role.ElectoralAdmin){
            ballotRepository.deleteAll(ballotRepository.findByAdmin(user)); // If admin account, delete all ballots
        }

        List<Ballot> ballots = ballotRepository.findAll();
        for (Ballot ballot : ballots) { // delete from all qualified ballots list
            ballot.getQualifiedVoters().removeIf(u -> u.getId().equals(user.getId()));
            ballotRepository.save(ballot);
        }

        List<Vote> votes = voteRepository.findAll();
        for (Vote vote : votes) {
            if (vote.getVoter().getId().equals(user.getId())) {
                voteRepository.delete(vote);
            }
        }

        userRepository.delete(user); // Delete user in repo
    }
    
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // authenticate current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // new password cannot be the same as the old one
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password must be different from current password");
        }
        
        // update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

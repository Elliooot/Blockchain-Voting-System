package com.voting.spring_boot_project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.OptionResponse;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BallotService {
    private final BallotRepository ballotRepository;
    private final UserRepository userRepository; // get the current user entity

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public List<BallotResponse> getAllBallots() {
        List<Ballot> ballots = ballotRepository.findAll();
        return ballots.stream()
                .map(this::convertToBallotResponse)
                .collect(Collectors.toList());
    }

    public List<BallotResponse> getBallotsForCurrentUser() {
        // --- Debug Point 8: 檢查方法開始執行 ---
        System.out.println("🚀 getBallotsForCurrentUser() method started");
        
        // --- Debug Point 9: 檢查 SecurityContext ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Current authentication: " + auth);
        System.out.println("👤 Principal: " + auth.getPrincipal());
        System.out.println("🎫 Authorities: " + auth.getAuthorities());
        
        String userEmail = auth.getName();
        System.out.println("📧 User email from SecurityContext: " + userEmail);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // --- Debug Point 10: 檢查使用者角色 ---
        System.out.println("👤 Found user: " + currentUser.getEmail());
        System.out.println("🎭 User role: " + currentUser.getRole());
        
        List<Ballot> ballots;

        if (currentUser.getRole() == Role.ElectoralAdmin) {
            System.out.println("🔧 Fetching ballots for ElectoralAdmin");
            ballots = ballotRepository.findByAdmin(currentUser);
        } else if (currentUser.getRole() == Role.Voter) {
            System.out.println("🗳️ Fetching ballots for Voter");
            ballots = ballotRepository.findBallotsForVoter(currentUser);
        } else {
            System.out.println("❓ Unknown role, returning empty list");
            ballots = new ArrayList<>();
        }
        
        System.out.println("📊 Found " + ballots.size() + " ballots");
        
        return ballots.stream()
                .map(this::convertToBallotResponse)
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public BallotResponse createBallot(CreateBallotRequest request) {
        // Get authenticated user from the secure context
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        var ballot = Ballot.builder()
                .admin(admin) // Use creditable user object
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .duration(request.getDuration())
                .options(request.getOptions())
                .build();

        // if(request.getQualifiedVoterIds() != null && !request.getQualifiedVoterIds().isEmpty()){
        //     List<User> qualifiedVoters = userRepository.findAllById(request.getQualifiedVoterIds());
        //     ballot.setQualifiedVoters(qualifiedVoters);
        // }

        Ballot createdballot = ballotRepository.save(ballot);
        return convertToBallotResponse(createdballot);
    }

    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public void deleteBallot(Integer ballotId){
        if(!ballotRepository.existsById(ballotId)){
            throw new RuntimeException("Ballot not found with id: " + ballotId);
        }
        ballotRepository.deleteById(ballotId);
    }

    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public BallotResponse updateInfo(Integer ballotId, UpdateBallotRequest request) {
        Ballot ballotToUpdate = ballotRepository.findById(ballotId)
                        .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        if(request.getTitle() != null && !request.getTitle().isBlank()) {
            ballotToUpdate.setTitle(request.getTitle());
        }

        if(request.getDescription() != null) {
            ballotToUpdate.setDescription(request.getDescription());
        }

        if(request.getStartTime() != null) { // Need to check if it can be voided
            ballotToUpdate.setStartTime(request.getStartTime());
        }

        if(request.getDuration() != null) { // Need to check if it can be voided
            ballotToUpdate.setDuration(request.getDuration());
        }

        if(request.getOptions() != null) { // Need to check if it can be voided
            ballotToUpdate.setOptions(request.getOptions());
        }

        ballotRepository.save(ballotToUpdate);

        return convertToBallotResponse(ballotToUpdate);
    }

    private BallotResponse convertToBallotResponse(Ballot ballot) {
        List<OptionResponse> optionResponses = ballot.getOptions().stream()
                .map(this::convertToOptionResponse)
                .collect(Collectors.toList());

        return BallotResponse.builder()
                .id(ballot.getId())
                .title(ballot.getTitle())
                .description(ballot.getDescription())
                .startTime(ballot.getStartTime())
                .duration(ballot.getDuration())
                .options(optionResponses)
                .status(ballot.getStatus())
                .build();
    }

    private OptionResponse convertToOptionResponse(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .description(option.getDescription())
                .voteCount(option.getVoteCount())
                .displayOrder(option.getDisplayOrder())
                .build();
    }
}

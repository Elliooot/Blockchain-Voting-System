package com.voting.spring_boot_project.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BallotService {
    private final BallotRepository ballotRepository;
    private final UserRepository userRepository; // get the current user entity
    
    // @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public BallotResponse createBallot(CreateBallotRequest request) {
        // Get authenticated user from the secure context
        // String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        // User adminUser = userRepository.findByEmail(userEmail)
        //         .orElseThrow(() -> new RuntimeException("Admin user not found"));

        var ballot = Ballot.builder()
                // .admin(adminUser) // Use creditable user object
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                // .duration(request.getDuration())
                .options(request.getOptions())
                .build();

        Ballot createdballot = ballotRepository.save(ballot);
        return BallotResponse.builder()
            .id(createdballot.getId())
            .title(createdballot.getTitle())
            .description(createdballot.getDescription())
            .startTime(createdballot.getStartTime())
            .duration(createdballot.getDuration())
            .options(createdballot.getOptions())
            .message("Ballot created successfully")
            .build(); // Need to check if it's correct
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

        // if(request.getDuration() != null) { // Need to check if it can be voided
        //     ballotToUpdate.setDuration(request.getDuration());
        // }

        if(request.getOptions() != null) { // Need to check if it can be voided
            ballotToUpdate.setOptions(request.getOptions());
        }

        ballotRepository.save(ballotToUpdate);

        return BallotResponse.builder()
            .id(ballotToUpdate.getId())
            .title(ballotToUpdate.getTitle())
            .description(ballotToUpdate.getDescription())
            .startTime(ballotToUpdate.getStartTime())
            // .duration(ballotToUpdate.getDuration())
            .options(ballotToUpdate.getOptions())
            .build();
    }
}

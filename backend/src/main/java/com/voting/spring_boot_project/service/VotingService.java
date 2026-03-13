package com.voting.spring_boot_project.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.dto.VoteRecordResponse;
import com.voting.spring_boot_project.dto.VoteRequest;
import com.voting.spring_boot_project.dto.VoteResponse;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.entity.Vote;
import com.voting.spring_boot_project.repository.OptionRepository;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotingService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;
    private final BlockchainGateway blockchainGateway;

    @PreAuthorize("hasAuthority('Voter')")
    public VoteResponse castVote(VoteRequest request){ // Contract deployer calls the contract and acts as proxy to vote using its private key and voters address

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User voter = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Voter not found"));
        if (voter.getWalletAddress() == null || voter.getWalletAddress().isBlank()) {
            throw new RuntimeException("Voter wallet address is missing");
        }

        Option selectedOption = optionRepository.findById(request.getOptionId())
            .orElseThrow(() -> new RuntimeException("Option not found"));
        if (selectedOption.getBallot().getCurrentStatus() != com.voting.spring_boot_project.entity.Status.Active) {
            throw new RuntimeException("Voting is not active for this ballot");
        }
        if (voteRepository.existsByBallotAndVoter(selectedOption.getBallot(), voter)) {
            throw new RuntimeException("Voter has already voted");
        }
        if (selectedOption.getBallot().getBlockchainBallotId() == null || selectedOption.getBlockchainOptionId() == null) {
            throw new RuntimeException("Blockchain identifiers missing for this ballot");
        }
        if (request.getBlockchainBallotId() != null
            && selectedOption.getBallot().getBlockchainBallotId() != null
            && !request.getBlockchainBallotId().equals(selectedOption.getBallot().getBlockchainBallotId().intValue())) {
            throw new RuntimeException("Blockchain ballot ID mismatch");
        }

        try {
            String txHash = blockchainGateway.castVote(
                selectedOption.getBallot().getBlockchainBallotId(),
                selectedOption.getBlockchainOptionId(),
                voter.getWalletAddress()
            );

            var newVote = Vote.builder()
                .option(selectedOption)
                .ballot(selectedOption.getBallot())
                .voter(voter)
                .timestamp(new Date())
                .transactionHash(txHash)
                .build();

            Vote savedVote = voteRepository.save(newVote);
            
            return VoteResponse.builder()
                .voteId(savedVote.getId())
                .optionId(savedVote.getOption().getId())
                .optionName(savedVote.getOption().getName())
                .timestamp(savedVote.getTimestamp())
                .txHash(txHash)
                .build();
                
        } catch (Exception e) {
            throw new RuntimeException("Failed to cast vote on the blockchain: " + e.getMessage(), e);
        }
    }

    public List<VoteRecordResponse> getVoteRecordsForCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Vote> votesList = voteRepository.findByVoter(currentUser);

        return votesList.stream()
            .map(this::convertToVoteRecordResponse)
            .toList();
    }

    private VoteRecordResponse convertToVoteRecordResponse(Vote vote) {
        return VoteRecordResponse.builder()
            .voteId(vote.getId())
            .ballotId(vote.getBallot().getId())
            .ballotTitle(vote.getBallot().getTitle())
            .optionName(vote.getOption().getName())
            .build();
    }
}

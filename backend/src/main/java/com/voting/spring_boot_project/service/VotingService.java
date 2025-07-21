package com.voting.spring_boot_project.service;

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.dto.VoteRequest;
import com.voting.spring_boot_project.dto.VoteResponse;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.Vote;
import com.voting.spring_boot_project.repository.OptionRepository;
import com.voting.spring_boot_project.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotingService {
    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;

    @PreAuthorize("hasAuthority('Voter')")
    public VoteResponse castVote(VoteRequest request){

        Option selectedOption = optionRepository.findById(request.getOptionId())
            .orElseThrow();
        
        var newVote = Vote.builder()
            .option(selectedOption)
            .ballot(selectedOption.getBallot())
            .timestamp(new Date())
            .isSuccess(true)
            .build();

        Vote savedVote = voteRepository.save(newVote);

        return VoteResponse.builder()
            .voteId(savedVote.getId())
            .optionId(savedVote.getOption().getId())
            .optionName(savedVote.getOption().getName())
            .timestamp(savedVote.getTimestamp())
            .message("Vote cast successfully")
            .build();
    }
}

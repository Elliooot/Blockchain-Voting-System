package com.voting.spring_boot_project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.voting.spring_boot_project.dto.ResultResponse;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.OptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final BallotRepository ballotRepository;
    private final OptionRepository optionRepository; // 如果我result.tsx要抓取ballot的title, description, result和option的vote count和total voter

    public ResultResponse getBallotResult(Integer ballotId){
        Ballot ballot = ballotRepository.findById(ballotId)
                .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        List<String> resultOptionNames = ballot.getResultOptionIds().stream()
                .map(optionId -> optionRepository.findById(optionId)
                    .map(option -> option.getName())
                    .orElse("Unknown"))
                .toList();

        List<Integer> voteCounts = ballot.getResultOptionIds().stream()
                .map(optionId -> optionRepository.findById(optionId)
                    .map(option -> option.getVoteCount())
                    .orElse(0))
                .toList();

        Long totalVotes = voteCounts.stream()
                .mapToLong(count -> count)
                .sum();

        return ResultResponse.builder()
                .title(ballot.getTitle())
                .description(ballot.getDescription())
                .resultOptionNames(resultOptionNames)
                .voteCounts(voteCounts)
                .totalVotes(totalVotes)
                .build();
    }
}

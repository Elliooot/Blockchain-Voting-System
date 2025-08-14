package com.voting.spring_boot_project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting.spring_boot_project.dto.VoteRecordResponse;
import com.voting.spring_boot_project.dto.VoteRequest;
import com.voting.spring_boot_project.dto.VoteResponse;
import com.voting.spring_boot_project.service.VotingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/voting")
@RequiredArgsConstructor
public class VotingController {
    private final VotingService votingService;

    @PostMapping("/vote")
    public ResponseEntity<VoteResponse> cateVote(
        @RequestBody VoteRequest request
    ) {
        return ResponseEntity.ok(votingService.castVote(request));
    }

    @GetMapping("/records")
    public ResponseEntity<List<VoteRecordResponse>> getVoteRecordsForCurrentUser() {
        return ResponseEntity.ok(votingService.getVoteRecordsForCurrentUser());
    }
}

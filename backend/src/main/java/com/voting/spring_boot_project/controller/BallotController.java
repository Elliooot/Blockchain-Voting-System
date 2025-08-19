package com.voting.spring_boot_project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.ResultResponse;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.service.BallotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ballots")
@RequiredArgsConstructor
public class BallotController {

    private final BallotService ballotService;

    @GetMapping
    public List<BallotResponse> getBallotsForCurrentUser() {
        System.out.println("🎯 BallotController - getBallotsForCurrentUser() called");
        System.out.println("🔍 BallotController - Current SecurityContext: " + 
            SecurityContextHolder.getContext().getAuthentication());
        
        return ballotService.getBallotsForCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BallotResponse> getBallotById(@PathVariable Integer id) {
        System.out.println("🎯 BallotController - getBallotById() called");
        return ResponseEntity.ok(ballotService.getBallotById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<BallotResponse> createBallot(
        @RequestBody CreateBallotRequest request
    ) {
        System.out.println("🎯 BallotController - createBallot() called");
        return ResponseEntity.ok(ballotService.createBallot(request));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<BallotResponse> updateBallot(
        @PathVariable Integer id, @RequestBody UpdateBallotRequest request
    ) {
        System.out.println("🎯 BallotController - updateBallot() called");
        return ResponseEntity.ok(ballotService.updateInfo(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteBallot(@PathVariable Integer id) {
        System.out.println("🎯 BallotController - deleteBallot() called");
        ballotService.deleteBallot(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ballot id: " + id + " delete successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/result")
    public ResponseEntity<List<ResultResponse>> getBallotResult() {
        System.out.println("🎯 BallotController - getBallotResult() called");
        ballotService.finalizeExpiredBallots();
        return ResponseEntity.ok(ballotService.getResultForCurrentUser());
    }
}

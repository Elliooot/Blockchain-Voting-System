package com.voting.spring_boot_project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.service.BallotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class BallotController {
    private final BallotService ballotService;

    @PostMapping("/create_ballot")
    public ResponseEntity<BallotResponse> createBallot(
        @RequestBody CreateBallotRequest request
    ) {
        return ResponseEntity.ok(ballotService.createBallot(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BallotResponse> updateBallot(
        @PathVariable Integer id, @RequestBody UpdateBallotRequest request
    ) {
        return ResponseEntity.ok(ballotService.updateInfo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBallot(@PathVariable Integer id) {
        ballotService.deleteBallot(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ballot id: " + id + " delete successfully");
        return ResponseEntity.ok(response);
    }
}

package com.voting.spring_boot_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting.spring_boot_project.service.ResultService;
import com.voting.spring_boot_project.dto.ResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/result")
@RequiredArgsConstructor
public class ResultController {
    
    private final ResultService resultService;

    @GetMapping("/{ballotId}")
    public ResponseEntity<ResultResponse> getBallotResult(@PathVariable Integer ballotId) {
        return ResponseEntity.ok(resultService.getBallotResult(ballotId));
    }

}

package com.voting.spring_boot_project.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BallotFinalizationScheduler {
    
    private final BallotService ballotService;

    // Execute once a minute
    // @Scheduled(fixedRate = 60000)
    public void finalizeExpiredBallots() {
        ballotService.finalizeExpiredBallots();
    }
}

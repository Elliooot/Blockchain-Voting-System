package com.voting.spring_boot_project.service;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import com.voting.spring_boot_project.contract.Voting;
import com.voting.spring_boot_project.dto.VoteRequest;
import com.voting.spring_boot_project.dto.VoteResponse;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.entity.Vote;
import com.voting.spring_boot_project.repository.BallotRepository;
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

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    @PreAuthorize("hasAuthority('Voter')")
    public VoteResponse castVote(VoteRequest request){

        System.out.println("castVote() method started");

        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // String userEmail = auth.getName();
        // User voter = userRepository.findByEmail(userEmail)
        //         .orElseThrow(() -> new RuntimeException("Voter not found"));

        Option selectedOption = optionRepository.findById(request.getOptionId())
            .orElseThrow();
        
        var newVote = Vote.builder()
            .option(selectedOption)
            .ballot(selectedOption.getBallot())
            .timestamp(new Date())
            .isSuccess(true)
            .build();

        System.out.println("Vote Option: " + newVote.getOption().getName());

        Vote savedVote = voteRepository.save(newVote);

        try {
            System.out.println("Interacting wiht smart contract...");

            BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            BigInteger gasLimit = BigInteger.valueOf(300_000L);
            ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

            // Loading the deployed contract
            Voting contract = Voting.load(contractAddress, web3j, credentials, gasProvider);

            long ballotId = request.getBlockchainBallotId();
            long optionId = request.getOptionId();

            // Calling vote() method and send
            TransactionReceipt receipt = contract.vote(BigInteger.valueOf(ballotId), BigInteger.valueOf(optionId)).send();

            System.out.println("Vote transaction hash: " + receipt.getTransactionHash());
            System.out.println("Gas used: " + receipt.getGasUsed());

            voteRepository.save(savedVote);
        } catch (Exception e) {
            System.out.println("Failed to interact with smart contract: " + e.getMessage());
            savedVote.setSuccess(false);
            voteRepository.delete(savedVote);
            throw new RuntimeException("Failed to cast vote on the blockchain");
        }

        return VoteResponse.builder()
            .voteId(savedVote.getId())
            .optionId(savedVote.getOption().getId())
            .optionName(savedVote.getOption().getName())
            .timestamp(savedVote.getTimestamp())
            .message("Vote cast successfully")
            .build();
    }
}

package com.voting.spring_boot_project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.web3j.protocol.Web3j;
import org.web3j.crypto.Credentials;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import com.voting.spring_boot_project.contract.Voting;

import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.OptionResponse;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BallotService {
    private final BallotRepository ballotRepository;
    private final UserRepository userRepository; // get the current user entity

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    // @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public List<BallotResponse> getAllBallots() {
        List<Ballot> ballots = ballotRepository.findAll();
        return ballots.stream()
                .map(this::convertToBallotResponse)
                .collect(Collectors.toList());
    }

    public List<BallotResponse> getBallotsForCurrentUser() {
        System.out.println("🚀 getBallotsForCurrentUser() method started");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 Current authentication: " + auth);
        System.out.println("👤 Principal: " + auth.getPrincipal());
        System.out.println("🎫 Authorities: " + auth.getAuthorities());
        
        String userEmail = auth.getName();
        System.out.println("📧 User email from SecurityContext: " + userEmail);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("👤 Found user: " + currentUser.getEmail());
        System.out.println("🎭 User role: " + currentUser.getRole());
        
        List<Ballot> ballots;

        if (currentUser.getRole() == Role.ElectoralAdmin) {
            System.out.println("🔧 Fetching ballots for ElectoralAdmin");
            ballots = ballotRepository.findByAdmin(currentUser);
        } else if (currentUser.getRole() == Role.Voter) {
            System.out.println("🗳️ Fetching ballots for Voter");
            ballots = ballotRepository.findBallotsForVoter(currentUser);
        } else {
            System.out.println("❓ Unknown role, returning empty list");
            ballots = new ArrayList<>();
        }
        
        System.out.println("📊 Found " + ballots.size() + " ballots");
        
        return ballots.stream()
                .map(this::convertToBallotResponse)
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public BallotResponse createBallot(CreateBallotRequest request) {
        System.out.println("🚀 createBallot() method started");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔐 [createBallot] Current authentication: " + auth);
        System.out.println("👤 [createBallot] Principal: " + auth.getPrincipal());
        System.out.println("🎫 [createBallot] Authorities: " + auth.getAuthorities());
        // Get authenticated user from the secure context
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        List<User> qualifiedVoters = new ArrayList<>();
        if(request.getQualifiedVoterIds() != null && !request.getQualifiedVoterIds().isEmpty()){
            qualifiedVoters = userRepository.findAllById(request.getQualifiedVoterIds());
        }

        var ballot = Ballot.builder()
                .admin(admin) // Use creditable user object
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .duration(request.getDuration())
                .options(request.getOptions())
                .qualifiedVoters(qualifiedVoters)
                .status(Status.Pending)
                .build();

        Ballot createdballotInDB = ballotRepository.save(ballot);

        // Calling Smart Contract
        try {
            System.out.println("Interacting with smart contract...");
            System.out.println("Contract address: " + contractAddress);
            System.out.println("Web3j URL: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
            
            BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            BigInteger gasLimit = BigInteger.valueOf(300_000L);
            ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

            // Loading the deployed contract
            Voting contract = Voting.load(contractAddress, web3j, credentials, gasProvider);

            // Converting Date and Duration in Java into uint256(sec) in Solidity
            long startTimeSeconds = request.getStartTime().toInstant().getEpochSecond();
            long durationSeconds = request.getDuration().getSeconds();

            List<String> proposalNames = request.getOptions().stream()
                .map(Option::getName)
                .collect(Collectors.toList());

            List<String> voterAddresses = qualifiedVoters.stream()
                .map(User::getWalletAddress)
                .collect(Collectors.toList());

            System.out.println("Calling createBallot with:");
            System.out.println("- title: " + request.getTitle());
            System.out.println("- startTimeSeconds: " + startTimeSeconds);
            System.out.println("- durationSeconds: " + durationSeconds);
            System.out.println("- gasLimit: " + gasLimit);
            System.out.println("- gasPrice: " + gasPrice);

            // Calling createBallot() method and send
            TransactionReceipt receipt = contract.createBallot(
                request.getTitle(), 
                BigInteger.valueOf(startTimeSeconds), 
                BigInteger.valueOf(durationSeconds),
                proposalNames,
                voterAddresses
            ).send();
            
            System.out.println("Smart Contract transaction successful. Hash: " + receipt.getTransactionHash());
            System.out.println("Gas used: " + receipt.getGasUsed());

            ballotRepository.save(createdballotInDB);
        } catch (Exception e) {
            System.out.println("Failed interacting with smart contract: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            ballotRepository.delete(createdballotInDB);
            throw new RuntimeException("Failed to create ballot on the blockchain: " + e.getMessage(), e);
        }

        return convertToBallotResponse(createdballotInDB);
    }

    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public void deleteBallot(Integer ballotId){
        System.out.println("🚀 deleteBallot() method started");

        System.out.println("Deleting ballot with id: " + ballotId);
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

        if(request.getDuration() != null) { // Need to check if it can be voided
            ballotToUpdate.setDuration(request.getDuration());
        }

        if(request.getOptions() != null) { // Need to check if it can be voided
            ballotToUpdate.setOptions(request.getOptions());
        }

        ballotRepository.save(ballotToUpdate);

        return convertToBallotResponse(ballotToUpdate);
    }

    private BallotResponse convertToBallotResponse(Ballot ballot) {
        List<OptionResponse> optionResponses = ballot.getOptions().stream()
                .map(this::convertToOptionResponse)
                .collect(Collectors.toList());

        List<Integer> qualifiedVotersId = ballot.getQualifiedVoters().stream()
                .map(User::getId)
                .toList();

        return BallotResponse.builder()
                .id(ballot.getId())
                .title(ballot.getTitle())
                .description(ballot.getDescription())
                .startTime(ballot.getStartTime())
                .duration(ballot.getDuration())
                .options(optionResponses)
                .qualifiedVotersId(qualifiedVotersId)
                .status(ballot.getCurrentStatus())
                .build();
    }

    private OptionResponse convertToOptionResponse(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .description(option.getDescription())
                .voteCount(option.getVoteCount())
                .displayOrder(option.getDisplayOrder())
                .build();
    }
}

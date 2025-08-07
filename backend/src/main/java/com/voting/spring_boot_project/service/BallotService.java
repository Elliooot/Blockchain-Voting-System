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
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;

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

    public BallotResponse getBallotById(Integer ballotId) {
        System.out.println("🚀 getBallotById() method started");
        
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // String userEmail = auth.getName();
        
        // User currentUser = userRepository.findByEmail(userEmail)
        //         .orElseThrow(() -> new RuntimeException("User not found"));

        Ballot ballot = ballotRepository.findById(ballotId)
            .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        Option[] options = ballot.getOptions().toArray(new Option[0]);
        System.out.println("XOption size: " + options.length);

        // List<OptionResponse> optionResponses = ballot.getOptions().stream()
        //         .map(this::convertToOptionResponse)
        //         .collect(Collectors.toList());

        // Check if it is okay when response structure different with frontend (BallotData)

        return convertToBallotResponse(ballot);
    }

    
    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public BallotResponse createBallot(CreateBallotRequest request) {
        System.out.println("🚀 createBallot() method started");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Get authenticated user from the secure context
        String userEmail = auth.getName();
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
                .qualifiedVoters(qualifiedVoters)
                .status(Status.Pending)
                .build();

        for(Option option : request.getOptions()) {
            option.setBallot(ballot);
        }

        ballot.setOptions(request.getOptions());

        // --- 暫時不要在這裡保存到數據庫 ---
        // Ballot createdballotInDB = ballotRepository.save(ballot);

        // Calling Smart Contract
        TransactionReceipt receipt = null; // 將 receipt 宣告在 try 外面
        try {
            System.out.println("--- PRE-FLIGHT CHECK ---");
            System.out.println("Contract Address from properties: " + contractAddress);
            if (contractAddress == null || contractAddress.isBlank() || "0x0".equals(contractAddress)) {
                throw new IllegalStateException("Contract address is invalid!");
            }

            System.out.println("Verifying contract code exists at address...");
            String code = web3j.ethGetCode(contractAddress, DefaultBlockParameterName.LATEST).send().getCode();
            if (code == null || "0x".equals(code)) {
                throw new IllegalStateException("No contract deployed at address: " + contractAddress + ". Please redeploy!");
            }
            System.out.println("Contract code found. Length: " + code.length());
            
            BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            BigInteger gasLimit = BigInteger.valueOf(1_000_000L);
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

            // --- 更嚴格的參數驗證 ---
            System.out.println("--- STRICT PARAMETER VALIDATION ---");
            System.out.println("Title: " + request.getTitle());
            if (request.getTitle() == null || request.getTitle().isBlank()) throw new IllegalStateException("Title cannot be empty.");
            
            System.out.println("Start Time (Epoch Seconds): " + startTimeSeconds);
            System.out.println("Current Time (Epoch Seconds): " + System.currentTimeMillis() / 1000);
            if (startTimeSeconds <= (System.currentTimeMillis() / 1000) + 5) throw new IllegalStateException("Start time must be at least 5 seconds in the future.");

            System.out.println("Proposal Names Count: " + proposalNames.size());
            if (proposalNames.size() < 2) throw new IllegalStateException("Must have at least two proposals.");

            System.out.println("Voter Addresses Count: " + voterAddresses.size());
            if (voterAddresses.isEmpty()) throw new IllegalStateException("Must have at least one voter.");
            for (String addr : voterAddresses) {
                if (addr == null || !addr.matches("^0x[a-fA-F0-9]{40}$")) {
                    throw new IllegalStateException("Invalid Ethereum address found: " + addr);
                }
            }
            System.out.println("All parameters seem valid. Sending transaction...");
            
            // Calling createBallot() method and send
            receipt = contract.createBallot(
                request.getTitle(), 
                BigInteger.valueOf(startTimeSeconds), 
                BigInteger.valueOf(durationSeconds),
                proposalNames,
                voterAddresses
            ).send();

            System.out.println("--- TRANSACTION MINED ---");
            System.out.println("Transaction successful. Hash: " + receipt.getTransactionHash());
            System.out.println("Gas used: " + receipt.getGasUsed());
            System.out.println("Status: " + receipt.getStatus());

            if (!receipt.isStatusOK()) {
                // 即使 Web3j 應該會拋出異常，我們也自己檢查一次
                throw new TransactionException("Transaction failed with status: " + receipt.getStatus(), receipt);
            }

            // --- 交易成功後，再保存到數據庫 ---
            Ballot createdballotInDB = ballotRepository.save(ballot);

            List<Voting.BallotCreatedEventResponse> events = contract.getBallotCreatedEvents(receipt);
            if(events.isEmpty()) throw new RuntimeException("No BallotCreated event found");
            Long blockchainBallotId = events.get(0).ballotId.longValue();
            createdballotInDB.setBlockchainBallotId(blockchainBallotId);

            List<Voting.ProposalCreatedEventResponse> proposalEvents = contract.getProposalCreatedEvents(receipt);
            if(proposalEvents.isEmpty() || proposalEvents.size() != request.getOptions().size()) {
                throw new RuntimeException("Mismatch or no ProposalCreated events found");
            }

            for (Voting.ProposalCreatedEventResponse event : proposalEvents) {
                request.getOptions().stream()
                    .filter(opt -> opt.getName().equals(event.name))
                    .findFirst()
                    .ifPresent(opt -> opt.setBlockchainOptionId(event.proposalId.longValue()));
            }

            ballotRepository.save(createdballotInDB);
            return convertToBallotResponse(createdballotInDB);

        } catch (Exception e) {
            System.out.println("Failed interacting with smart contract: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            // --- 因為我們還沒保存 ballot，所以不需要刪除 ---
            // ballotRepository.delete(createdballotInDB); 
            throw new RuntimeException("Failed to create ballot on the blockchain: " + e.getMessage(), e);
        }
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

        // System.out.println("XOption size: " + optionResponses.size());
        for(int i = 0; i < optionResponses.size(); i++){
            // System.out.println("XOption " + i + ": " + optionResponses.get(i).getName());
        }

        List<Integer> qualifiedVotersId = ballot.getQualifiedVoters().stream()
                .map(User::getId)
                .toList();

        return BallotResponse.builder()
                .id(ballot.getId())
                .blockchainBallotId(ballot.getBlockchainBallotId())
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
                .blockchainOptionId(option.getBlockchainOptionId())
                .name(option.getName())
                .description(option.getDescription())
                .voteCount(option.getVoteCount())
                .displayOrder(option.getDisplayOrder())
                .build();
    }
}

package com.voting.spring_boot_project.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import com.voting.spring_boot_project.contract.Voting;
import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.OptionResponse;
import com.voting.spring_boot_project.dto.ResultResponse;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.OptionRepository;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.repository.VoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BallotService {
    private final UserRepository userRepository; // get the current user entity
    private final BallotRepository ballotRepository;
    private final OptionRepository optionRepository;
    private final VoteRepository voteRepository;

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
            ballots = ballotRepository.findBallotsForVoter(currentUser)
                .stream()
                .filter(b -> !voteRepository.existsByBallotAndVoter(b, currentUser))
                .collect(Collectors.toList());
        } else {
            System.out.println("❓ Unknown role, returning empty list");
            ballots = new ArrayList<>();
        }

        List<Ballot> updatedBallots = ballots.stream().map(this::checkAndUpdateStatus).collect(Collectors.toList());
        
        System.out.println("📊 Found " + updatedBallots.size() + " ballots");
        
        return updatedBallots.stream()
                .map(this::convertToBallotResponse)
                .collect(Collectors.toList());
    }

    private Ballot checkAndUpdateStatus(Ballot ballot) {
        Status currentStatus = ballot.getCurrentStatus();
        
        if(ballot.getStatus() != currentStatus) { // Update status if current status is different with db
            ballot.setStatus(currentStatus);
            return ballotRepository.save(ballot);
        }
        
        return ballot; // Directly return if status are same
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

        // Ballot createdballotInDB = ballotRepository.save(ballot);

        // Calling Smart Contract
        try {
            System.out.println("Interacting with smart contract...");
            System.out.println("Contract address: " + contractAddress);
            System.out.println("Web3j URL: " + web3j.web3ClientVersion().send().getWeb3ClientVersion());
            
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

            System.out.println("Calling createBallot with:");
            System.out.println("- title: " + request.getTitle());
            System.out.println("Start Time (Epoch Seconds): " + startTimeSeconds);
            System.out.println("Current Time (Epoch Seconds): " + System.currentTimeMillis() / 1000);
            System.out.println("- durationSeconds: " + durationSeconds);
            System.out.println("Proposal Names Count: " + proposalNames.size());
            System.out.println("Proposal Names: " + proposalNames);
            System.out.println("Voter Addresses Count: " + voterAddresses.size());
            System.out.println("Voter Addresses: " + voterAddresses);
            if (voterAddresses.stream().anyMatch(addr -> addr == null || addr.trim().isEmpty())) {
                System.out.println("!!! ERROR: Found null or empty wallet address in the list.");
                throw new RuntimeException("One or more qualified voters do not have a wallet address set.");
            }
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

            List<Voting.BallotCreatedEventResponse> ballotEvents = contract.getBallotCreatedEvents(receipt);
            if(ballotEvents.isEmpty()) throw new RuntimeException("No BallotCreated event found");
            Long blockchainBallotId = ballotEvents.get(0).ballotId.longValue();
            ballot.setBlockchainBallotId(blockchainBallotId);  // Return ballot ID on blockchain

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

            ballotRepository.save(ballot);
        } catch (Exception e) {
            System.out.println("Failed interacting with smart contract: " + e.getMessage());
            System.out.println("Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            // ballotRepository.delete(createdballotInDB);
            throw new RuntimeException("Failed to create ballot on the blockchain: " + e.getMessage(), e);
        }

        return convertToBallotResponse(ballot);
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
        Ballot ballot = ballotRepository.findById(ballotId)
                        .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        if(request.getDescription() != null) {
            ballot.setDescription(request.getDescription());
        }

        try {
            BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            BigInteger gasLimit = BigInteger.valueOf(1_000_000L);
            ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

            // Loading the deployed contract
            Voting contract = Voting.load(contractAddress, web3j, credentials, gasProvider);

            BigInteger bcId = BigInteger.valueOf(ballot.getBlockchainBallotId());
            System.out.println("Attempting to update ballot on chain with blockchainBallotId: " + bcId);

            if (request.getTitle() != null && !request.getTitle().isBlank()
                && !request.getTitle().equals(ballot.getTitle())) {
                contract.updateBallotTitle(bcId, request.getTitle()).send();
                ballot.setTitle(request.getTitle());
            }

            long chainNow = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                .send().getBlock().getTimestamp().longValue();
            if (request.getStartTime() != null) {
                long newStart = request.getStartTime().toInstant().getEpochSecond();
                if (newStart <= chainNow) throw new RuntimeException("Start time must be in the future (chain time)");
                contract.updateStartTime(bcId, BigInteger.valueOf(newStart)).send();
                ballot.setStartTime(request.getStartTime());
            }

            if (request.getDuration() != null) {
                long durSec = request.getDuration().getSeconds();
                if (durSec <= 0) throw new RuntimeException("Duration must be > 0");
                contract.updateDuration(bcId, BigInteger.valueOf(durSec)).send();
                ballot.setDuration(request.getDuration());
            }

            // List<Option> requestOptions = request.getOptions();
            // List<Option> dbOptions = ballot.getOptions();
            
            // if (requestOptions.size() != dbOptions.size()) {
            //     throw new RuntimeException("Options count mismatch");
            // }

            // for (int i = 0; i < requestOptions.size(); i++) {
            //     Option reqOption = requestOptions.get(i);
            //     Option dbOption = dbOptions.get(i);
                
            //     if (reqOption.getName() != null && !reqOption.getName().isBlank()
            //         && !reqOption.getName().equals(dbOption.getName())) {
                    
            //         System.out.println("Updating option: " + dbOption.getName() + " -> " + reqOption.getName());
            //         System.out.println("Using blockchainOptionId: " + dbOption.getBlockchainOptionId());
                    
            //         TransactionReceipt receipt = contract.updateProposalName(
            //             bcId, 
            //             BigInteger.valueOf(dbOption.getBlockchainOptionId()), 
            //             reqOption.getName()
            //         ).send();
                    
            //         System.out.println("Option update successful. Hash: " + receipt.getTransactionHash());
            //         dbOption.setName(reqOption.getName());
            //     }
            // }

            List<Integer> newIds = Optional.ofNullable(request.getQualifiedVoterIds()).orElse(List.of());
            Set<Integer> oldIds = ballot.getQualifiedVoters().stream().map(User::getId).collect(Collectors.toSet());
            Set<Integer> newIdSet = new HashSet<>(newIds);

            Set<Integer> toAdd = new HashSet<>(newIdSet);
            toAdd.removeAll(oldIds);

            Set<Integer> toRemove = new HashSet<>(oldIds);
            toRemove.removeAll(newIdSet);

            List<User> addUsers = userRepository.findAllById(toAdd);
            List<User> removeUsers = userRepository.findAllById(toRemove);
            if(addUsers.stream().anyMatch(u -> u.getWalletAddress() == null || u.getWalletAddress().isBlank())) {
                throw new RuntimeException("Some added voters have no wallet address");
            }

            for(User u: addUsers) {
                contract.registerVoter(bcId, u.getWalletAddress()).send();
            }

            for(User u: removeUsers) {
                contract.unregisterVoter(bcId, u.getWalletAddress()).send();
            }

            ballot.getQualifiedVoters().clear();
            ballot.getQualifiedVoters().addAll(userRepository.findAllById(newIds));

            List<User> qualifiedVoters = request.getQualifiedVoterIds().stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

            ballot.getQualifiedVoters().clear();
            ballot.getQualifiedVoters().addAll(qualifiedVoters);

            ballotRepository.save(ballot);

        } catch (Exception e){
            System.out.println("Failed to update ballot on blockchain: " + e.getMessage());
            e.printStackTrace();
        }

        return convertToBallotResponse(ballot);
    }

    private BallotResponse convertToBallotResponse(Ballot ballot) {
        List<OptionResponse> optionResponses = ballot.getOptions().stream()
                .map(this::convertToOptionResponse)
                .collect(Collectors.toList());

        List<String> qualifiedVotersEmail = ballot.getQualifiedVoters().stream()
                .map(User::getEmail)
                .toList();

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
                .qualifiedVotersEmail(qualifiedVotersEmail)
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
                .build();
    }

    // public List<String> getResultFromBlockchain(Long blockchainBallotId) {
    //     try {
    //         ContractGasProvider gasProvider = new DefaultGasProvider();

    //         Voting contract = Voting.load(contractAddress, web3j, credentials, gasProvider);

    //         List<BigInteger> resultIds = contract.getResult(BigInteger.valueOf(blockchainBallotId)).send();

    //         Ballot ballot = ballotRepository.findByBlockchainBallotId(blockchainBallotId).orElse(null);

    //         List<String> resultNames = resultIds.stream()
    //             .map(id -> optionRepository.findByBallotAndBlockchainOptionId(ballot, id.longValue())
    //                 .map(Option::getName)
    //                 .orElse("Unknown"))
    //             .toList();

    //         return resultNames;
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed to get result from blockchain: " + e.getMessage(), e);
    //     }
    // }

    public List<ResultResponse> getResultForCurrentUser() {
        System.out.println("🚀 getResultForCurrentUser() method started");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Ballot> ballots = ballotRepository.findVotedAndEndedBallots(currentUser, Status.Ended);

        System.out.println("Found " + ballots.size() + " ballots");

        return ballots.stream()
                .map(this::convertToResultResponse)
                .collect(Collectors.toList());
    }

    public ResultResponse convertToResultResponse(Ballot ballot){ // ballot's title, description, result and option's vote count and total voter

        Date endTime = new Date(ballot.getStartTime().getTime() + ballot.getDuration().toMillis());
        
        List<String> optionNames = ballot.getOptions().stream()
                .map(Option::getName)
                .toList();
        
        List<Integer> voteCounts = ballot.getOptions().stream()
                .map(Option::getVoteCount)
                .toList();

        Long totalVotes = voteCounts.stream()
                .mapToLong(count -> count)
                .sum();
                
        List<String> resultOptionNames = ballot.getResultOptionIds().stream()
                .map(optionId -> optionRepository.findById(optionId)
                    .map(option -> option.getName())
                    .orElse("Unknown"))
                .toList();

        return ResultResponse.builder()
                .ballotId(ballot.getId())
                .title(ballot.getTitle())
                .description(ballot.getDescription())
                .startTime(ballot.getStartTime())
                .endTime(endTime)
                .optionNames(optionNames)
                .voteCounts(voteCounts)
                .totalVotes(totalVotes)
                .resultOptionNames(resultOptionNames)
                .build();
    }
}

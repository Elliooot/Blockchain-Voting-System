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
import org.springframework.transaction.annotation.Transactional;
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
import com.voting.spring_boot_project.entity.Vote;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Ballot> ballots;

        if (currentUser.getRole() == Role.ElectoralAdmin) {
            ballots = ballotRepository.findByAdmin(currentUser);
        } else if (currentUser.getRole() == Role.Voter) {
            ballots = ballotRepository.findBallotsForVoter(currentUser);
        } else {
            System.out.println("Unknown role, returning empty list");
            ballots = new ArrayList<>();
        }

        List<Ballot> updatedBallots = ballots.stream().map(this::checkAndUpdateStatus).collect(Collectors.toList());
        
        return updatedBallots.stream()
                .map(ballot -> {
                    BallotResponse response = convertToBallotResponse(ballot);

                    if(currentUser.getRole() == Role.Voter) {
                        boolean hasVoted = voteRepository.existsByBallotAndVoter(ballot, currentUser);
                        response.setHasVoted(hasVoted);
                        
                        if(hasVoted) {
                            Vote vote = voteRepository.findByBallotAndVoter(ballot, currentUser);
                            if(vote != null && vote.getTransactionHash() != null) {
                                response.setTxHash(vote.getTransactionHash());
                            } else {
                                response.setTxHash("");
                            }
                        } else {
                            response.setTxHash("");
                        }
                    }

                    return response;
                })
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
        Ballot ballot = ballotRepository.findById(ballotId)
            .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        Option[] options = ballot.getOptions().toArray(new Option[0]);
        System.out.println("XOption size: " + options.length);

        return convertToBallotResponse(ballot);
    }

    
    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    @Transactional
    public BallotResponse createBallot(CreateBallotRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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

        // Calling Smart Contract
        try {
            BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            BigInteger gasLimit = BigInteger.valueOf(1_000_000L);
            ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

            // Loading the deployed contract
            Voting contract = Voting.load(contractAddress, web3j, credentials, gasProvider);

            // Converting Date and Duration in Java into uint256(sec) in Solidity
            long startTimeSeconds = request.getStartTime().toInstant().getEpochSecond();
            long durationSeconds = request.getDuration().getSeconds();

            long proposalCount = request.getOptions().size();

            List<String> voterAddresses = qualifiedVoters.stream()
                .map(User::getWalletAddress)
                .collect(Collectors.toList());

            // Calling createBallot() method and send
            TransactionReceipt receipt = contract.createBallot(
                BigInteger.valueOf(startTimeSeconds), 
                BigInteger.valueOf(durationSeconds),
                BigInteger.valueOf(proposalCount),
                voterAddresses
            ).send();

            List<Voting.BallotCreatedEventResponse> ballotEvents = contract.getBallotCreatedEvents(receipt);
            if(ballotEvents.isEmpty()) throw new RuntimeException("No BallotCreated event found");
            Long blockchainBallotId = ballotEvents.get(0).ballotId.longValue();
            ballot.setBlockchainBallotId(blockchainBallotId);  // Return ballot ID on blockchain

            List<Voting.ProposalCreatedEventResponse> proposalEvents = contract.getProposalCreatedEvents(receipt);
            List<Option> requestOptions = request.getOptions();

            if(proposalEvents.isEmpty() || proposalEvents.size() != requestOptions.size()) {
                throw new RuntimeException("Mismatch between number of options (" + requestOptions.size() + ") and proposal events found (" + proposalEvents.size() + ")");
            }

            for (int i = 0; i < proposalEvents.size(); i++) {
                Voting.ProposalCreatedEventResponse event = proposalEvents.get(i);
                Option correspondingOption = requestOptions.get(i);
                
                correspondingOption.setBlockchainOptionId(event.proposalId.longValue());
            }

            ballotRepository.save(ballot);
        } catch (Exception e) {
            e.printStackTrace();
            // ballotRepository.delete(createdballotInDB);
            throw new RuntimeException("Failed to create ballot on the blockchain: " + e.getMessage(), e);
        }

        return convertToBallotResponse(ballot);
    }

    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public void deleteBallot(Integer ballotId){
        Ballot ballot = ballotRepository.findById(ballotId)
            .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        if(!ballotRepository.existsById(ballotId)){
            throw new RuntimeException("Ballot not found with id: " + ballotId);
        }

        List<Vote> votes = voteRepository.findByBallotId(ballotId);
        voteRepository.deleteAll(votes);

        List<Option> options = optionRepository.findByBallotId(ballotId);
        optionRepository.deleteAll(options);

        ballot.getQualifiedVoters().clear();

        if (ballot.getResultOptionIds() != null) {
            ballot.getResultOptionIds().clear();
        }

        ballotRepository.deleteById(ballotId);
    }

    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public BallotResponse updateInfo(Integer ballotId, UpdateBallotRequest request) {
        Ballot ballot = ballotRepository.findById(ballotId)
                        .orElseThrow(() -> new RuntimeException("Ballot not found with id: " + ballotId));

        if(request.getTitle() != null) {
            ballot.setTitle(request.getTitle());
        }
        
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

    @Transactional
    public void finalizeExpiredBallots() {
        List<Ballot> expiredBallots = ballotRepository.findExpiredBallotsWithoutResults(Status.Ended);

        for (Ballot ballot: expiredBallots){
            finalizeResultOnBlockchain(ballot, ballot.getBlockchainBallotId());
        }
    }

    private void finalizeResultOnBlockchain(Ballot ballot, Long blockchainBallotId) {
        try {
            BigInteger gasPrice = BigInteger.valueOf(20_000_000_000L); // 20 Gwei
            BigInteger gasLimit = BigInteger.valueOf(1_000_000L);
            ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);
            Voting contract = Voting.load(contractAddress, web3j, credentials, gasProvider);

            List<BigInteger> counts = contract.getVoteCounts(BigInteger.valueOf(blockchainBallotId)).send();
            List<Option> options = optionRepository.findByBallot(ballot);

            for(int i = 0; i < options.size(); i++) {
                options.get(i).setVoteCount(counts.get(i).intValue());
            }
            optionRepository.saveAll(options);
            
            TransactionReceipt receipt = contract.finalizeResult(BigInteger.valueOf(blockchainBallotId)).send();
    
            List<Voting.BallotResultFinalizedEventResponse> resultEvents = contract.getBallotResultFinalizedEvents(receipt);
            if(resultEvents.isEmpty()) throw new RuntimeException("No resultFinalized events found");
    
            List<BigInteger> blockchainResultIds = resultEvents.get(0).resultProposalIds;

            List<Integer> resultIds = blockchainResultIds.stream()
                    .map(id -> optionRepository.findByBallotAndBlockchainOptionId(ballot, id.longValue())
                        .map(Option::getId)
                        .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            ballot.setResultOptionIds(resultIds);
            ballotRepository.save(ballot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ResultResponse> getResultForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Ballot> ballots = ballotRepository.findVotedAndEndedBallots(currentUser, Status.Ended);

        List<Integer> demoIds = List.of(1502, 1503, 1504);

        List<Ballot> all = new ArrayList<>(ballots);

        List<Ballot> demoBallots = ballotRepository.findAllById(demoIds);
        Set<Integer> existingIds = all.stream().map(Ballot::getId).collect(Collectors.toSet());
        for (Ballot b: demoBallots) {
            if (!existingIds.contains(b.getId())) {
                all.add(b);
            }
        }

        return all.stream()
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

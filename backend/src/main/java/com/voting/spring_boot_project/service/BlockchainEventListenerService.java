package com.voting.spring_boot_project.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.gas.DefaultGasProvider;

import com.voting.spring_boot_project.contract.Voting;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.BlockchainEventState;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.BlockchainEventStateRepository;
import com.voting.spring_boot_project.repository.OptionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockchainEventListenerService {
    private final BallotRepository ballotRepository;
    private final OptionRepository optionRepository;
    private final BlockchainEventStateRepository eventStateRepository;

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    private static final String BALLOT_RESULT_EVENT_KEY = "BallotResultFinalized";
    
    @PostConstruct
    public void startListening() {
        try {
            Voting contract = Voting.load(contractAddress, web3j, credentials, new DefaultGasProvider());

            long startBlock = getStartBlock();

            // Listen to BallotResultFinalized event
            contract.ballotResultFinalizedEventFlowable(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                DefaultBlockParameterName.LATEST
            ).subscribe(event -> {
                updateBallotResult(event.ballotId.longValue(), event.resultProposalIds);
                updateLastProcessedBlock(event.log.getBlockNumber().longValue());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBallotResult(Long blockchainBallotId, List<BigInteger> resultProposalIds) {
        try {
            Ballot ballot = ballotRepository.findByBlockchainBallotId(blockchainBallotId)
                .orElseThrow(() -> new RuntimeException("Ballot not found with blockchain ID: " + blockchainBallotId));

            List<Integer> resultOptionIds = resultProposalIds.stream()
                    .map(id -> optionRepository.findByBallotAndBlockchainOptionId(ballot, id.longValue())
                        .map(Option::getId)
                        .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            ballot.setResultOptionIds(resultOptionIds);
            ballotRepository.save(ballot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getStartBlock() {
        return eventStateRepository.findById(BALLOT_RESULT_EVENT_KEY)
            .map(BlockchainEventState::getLastProcessedBlock)
            .orElseGet(() -> {
                try {
                    return web3j.ethBlockNumber().send().getBlockNumber().longValue();
                } catch (Exception e) {
                    return 0L;
                }
            });
    }

    private void updateLastProcessedBlock(long blockNumber) {
        BlockchainEventState state = eventStateRepository.findById(BALLOT_RESULT_EVENT_KEY)
            .orElse(BlockchainEventState.builder()
                .eventKey(BALLOT_RESULT_EVENT_KEY)
                .lastProcessedBlock(blockNumber)
                .build());

        if (state.getLastProcessedBlock() == null || blockNumber > state.getLastProcessedBlock()) {
            state.setLastProcessedBlock(blockNumber);
            eventStateRepository.save(state);
        }
    }
}

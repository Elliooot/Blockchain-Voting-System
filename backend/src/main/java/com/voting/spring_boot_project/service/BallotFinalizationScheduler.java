package com.voting.spring_boot_project.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import com.voting.spring_boot_project.contract.Voting;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.OptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BallotFinalizationScheduler {
    
    private final BallotRepository ballotRepository;
    private final OptionRepository optionRepository;

    // Execute once a minute
    // @Scheduled(fixedRate = 60000)
    public void finalizeExpiredBallots() {
        System.out.println("Checking for expired ballots...");

        List<Ballot> expiredBallots = ballotRepository.findExpiredBallotsWithoutResults(Status.Ended);

        System.out.println("Number of expired ballots: " + expiredBallots.size());

        for (Ballot ballot: expiredBallots){
            finalizeResultOnBlockchain(ballot, ballot.getBlockchainBallotId());
        }
    }

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

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
            System.out.println("Blockchain result IDs: " + blockchainResultIds);

            List<Integer> resultIds = blockchainResultIds.stream()
                    .map(id -> optionRepository.findByBallotAndBlockchainOptionId(ballot, id.longValue())
                        .map(Option::getId)
                        .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            System.out.println("Result IDs: " + resultIds);
    
            ballot.setResultOptionIds(resultIds);
            ballotRepository.save(ballot);

            System.out.println("Updated ballot result in DB for ballot ID: " + ballot.getId());
        } catch (Exception e) {
            System.out.println("Failed finalize ballot result on blockchain: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.voting.spring_boot_project.service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import com.voting.spring_boot_project.contract.Voting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockchainGateway {

    private final Web3j web3j;
    private final Credentials credentials;
    private final TransactionManager txManager;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    @Value("${blockchain.gas.price:20000000000}")
    private long gasPrice;

    @Value("${blockchain.gas.limit.create-ballot:1000000}")
    private long gasLimitCreateBallot;

    @Value("${blockchain.gas.limit.update-ballot:500000}")
    private long gasLimitUpdateBallot;

    @Value("${blockchain.gas.limit.vote:300000}")
    private long gasLimitVote;

    @Value("${blockchain.gas.limit.finalize:1000000}")
    private long gasLimitFinalize;

    public record CreateBallotResult(long ballotId, List<Long> proposalIds, String txHash) {}

    public Voting loadWithCredentials(ContractGasProvider gasProvider) {
        return Voting.load(contractAddress, web3j, credentials, gasProvider);
    }

    public Voting loadWithTxManager(ContractGasProvider gasProvider) {
        return Voting.load(contractAddress, web3j, txManager, gasProvider);
    }

    private ContractGasProvider gasProvider(long limit) {
        return new StaticGasProvider(BigInteger.valueOf(gasPrice), BigInteger.valueOf(limit));
    }

    public CreateBallotResult createBallot(
        long startTimeSeconds,
        long durationSeconds,
        long proposalCount,
        List<String> voterAddresses
    ) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitCreateBallot));

        TransactionReceipt receipt = contract.createBallot(
            BigInteger.valueOf(startTimeSeconds),
            BigInteger.valueOf(durationSeconds),
            BigInteger.valueOf(proposalCount),
            voterAddresses
        ).send();

        List<Voting.BallotCreatedEventResponse> ballotEvents = contract.getBallotCreatedEvents(receipt);
        if (ballotEvents.isEmpty()) {
            throw new RuntimeException("No BallotCreated event found");
        }
        long ballotId = ballotEvents.get(0).ballotId.longValue();

        List<Voting.ProposalCreatedEventResponse> proposalEvents = contract.getProposalCreatedEvents(receipt);
        List<Long> proposalIds = proposalEvents.stream()
            .map(event -> event.proposalId.longValue())
            .toList();

        return new CreateBallotResult(ballotId, proposalIds, receipt.getTransactionHash());
    }

    public void updateStartTime(long blockchainBallotId, long newStartTimeSeconds) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitUpdateBallot));
        contract.updateStartTime(BigInteger.valueOf(blockchainBallotId), BigInteger.valueOf(newStartTimeSeconds)).send();
    }

    public void updateDuration(long blockchainBallotId, long durationSeconds) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitUpdateBallot));
        contract.updateDuration(BigInteger.valueOf(blockchainBallotId), BigInteger.valueOf(durationSeconds)).send();
    }

    public void registerVoter(long blockchainBallotId, String voterAddress) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitUpdateBallot));
        contract.registerVoter(BigInteger.valueOf(blockchainBallotId), voterAddress).send();
    }

    public void unregisterVoter(long blockchainBallotId, String voterAddress) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitUpdateBallot));
        contract.unregisterVoter(BigInteger.valueOf(blockchainBallotId), voterAddress).send();
    }

    public String castVote(long blockchainBallotId, long blockchainOptionId, String voterAddress) throws Exception {
        Voting contract = loadWithTxManager(gasProvider(gasLimitVote));
        TransactionReceipt receipt = contract.vote(
            BigInteger.valueOf(blockchainBallotId),
            BigInteger.valueOf(blockchainOptionId),
            voterAddress
        ).send();
        return receipt.getTransactionHash();
    }

    public List<BigInteger> getVoteCounts(long blockchainBallotId) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitFinalize));
        return contract.getVoteCounts(BigInteger.valueOf(blockchainBallotId)).send();
    }

    public List<BigInteger> finalizeResult(long blockchainBallotId) throws Exception {
        Voting contract = loadWithCredentials(gasProvider(gasLimitFinalize));
        TransactionReceipt receipt = contract.finalizeResult(BigInteger.valueOf(blockchainBallotId)).send();
        List<Voting.BallotResultFinalizedEventResponse> resultEvents = contract.getBallotResultFinalizedEvents(receipt);
        if (resultEvents.isEmpty()) {
            throw new RuntimeException("No BallotResultFinalized event found");
        }
        return resultEvents.get(0).resultProposalIds;
    }

    public long getLatestBlockTimestamp() throws Exception {
        return web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
            .send()
            .getBlock()
            .getTimestamp()
            .longValue();
    }
}

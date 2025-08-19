package com.voting.spring_boot_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

@Configuration
public class Web3jConfig {

    @Value("${network.chainId:11155111}")
    private long chainId;
    
    @Value("${blockchain.node.url}")
    private String nodeUrl;

    @Value("${blockchain.private.key}")
    private String privateKey;

    @Bean
    public Web3j web3j() { // Through web3j instance to send JSON-RPC requests to the Ethereum node
        return Web3j.build(new HttpService(nodeUrl));
    }

    @Bean
    public Credentials credentials() {
        return Credentials.create(privateKey);
    }

    @Bean
    public TransactionReceiptProcessor txReceiptProcessor(Web3j web3j) {
        return new NoOpProcessor(web3j); // no polling and waiting for receipt
    }

    @Bean(name = "web3jTransactionManager")
    public TransactionManager web3jTransactionManager(
        Web3j web3j,
        Credentials credentials,
        TransactionReceiptProcessor txReceiptProcessor
    ) {
        return new RawTransactionManager(web3j, credentials, chainId, txReceiptProcessor);
    }
}

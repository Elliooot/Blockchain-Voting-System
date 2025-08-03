package com.voting.spring_boot_project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.AbiDefinition;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class BlockchainService {

    private final ResourceLoader resourceLoader;
    private AbiDefinition[] votingAbi;
    private String votingBytecode;

    // 使用建構子注入 ResourceLoader
    public BlockchainService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // 在應用程式啟動後執行一次，用於載入 ABI 檔案
    @PostConstruct
    public void init() throws IOException {
        System.out.println("🔧 Loading Voting contract ABI and bytecode...");

        // 從 resources 目錄中獲取 Voting.json 檔案
        Resource resource = resourceLoader.getResource("classpath:Voting.json");

        // 使用 InputStream 來讀取檔案，這在 JAR 包中也能正常工作
        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            // 提取 ABI 陣列
            JsonNode abiNode = rootNode.get("abi");
            if (abiNode == null || !abiNode.isArray()) {
                throw new IllegalArgumentException("JSON does not contain a valid 'abi' array.");
            }

            // 將 ABI JsonNode 轉換為 AbiDefinition[]
            this.votingAbi = mapper.convertValue(abiNode, AbiDefinition[].class);

            // 提取 bytecode（部署合約時需要）
            JsonNode bytecodeNode = rootNode.get("bytecode");
            if (bytecodeNode != null && bytecodeNode.isTextual()) {
                this.votingBytecode = bytecodeNode.asText();
            }

            System.out.println("✅ Voting contract ABI loaded successfully with " + this.votingAbi.length + " definitions.");
            System.out.println("✅ Voting contract bytecode loaded successfully.");
        }
    }

    // 提供一個公共方法來獲取已載入的 ABI
    public AbiDefinition[] getVotingAbi() {
        if (this.votingAbi == null) {
            throw new IllegalStateException("Voting ABI has not been loaded yet.");
        }
        return this.votingAbi;
    }

    // 提供公共方法來獲取已載入的 bytecode
    public String getVotingBytecode() {
        if (this.votingBytecode == null) {
            throw new IllegalStateException("Voting bytecode has not been loaded yet.");
        }
        return this.votingBytecode;
    }

    // ... 其他與區塊鏈互動的方法，例如部署合約，調用方法等
    // 這些方法將會使用 getVotingAbi() 來獲取 ABI
}
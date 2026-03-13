package com.voting.spring_boot_project.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class UpdateWalletRequest {
    @Size(max = 255, message = "Wallet address too long")
    private String walletAddress;
}

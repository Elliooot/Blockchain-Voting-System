package com.voting.spring_boot_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    @NotNull(message = "Blockchain ballot ID is required")
    @Min(value = 1, message = "Blockchain ballot ID must be positive")
    private Integer blockchainBallotId;
    @NotNull(message = "Option ID is required")
    @Min(value = 1, message = "Option ID must be positive")
    private Integer optionId;
}

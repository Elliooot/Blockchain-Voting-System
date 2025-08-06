package com.voting.spring_boot_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse {
    // This is created to exclude ballot, to avoid infinite loop reference
    private Integer id;
    private Long blockchainOptionId;
    private String name;
    private String description;
    private int voteCount;
    private int displayOrder;
}
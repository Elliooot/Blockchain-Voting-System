package com.voting.spring_boot_project.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {
    private Integer voteId;
    private Integer optionId;
    private String optionName;
    private Date timestamp;
}

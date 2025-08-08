package com.voting.spring_boot_project.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse {
    private Integer ballotId;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private List<String> optionNames;
    private List<Integer> voteCounts;
    private Long totalVotes;
    private List<String> resultOptionNames;
}

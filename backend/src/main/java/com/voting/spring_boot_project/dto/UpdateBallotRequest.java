package com.voting.spring_boot_project.dto;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.voting.spring_boot_project.entity.Option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBallotRequest {
    private Integer id;
    private String title;
    private String description;
    private Date startTime;
    private Duration duration;
    private List<Option> options;
    private List<Integer> qualifiedVoterIds;
}

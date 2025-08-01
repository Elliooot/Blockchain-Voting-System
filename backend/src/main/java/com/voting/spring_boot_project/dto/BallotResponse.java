package com.voting.spring_boot_project.dto;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.voting.spring_boot_project.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BallotResponse {
    private Integer id;
    private String title;
    private String description;
    private Date startTime;
    private Duration duration;
    private List<OptionResponse> options;
    private Status status;
    private String message;
}

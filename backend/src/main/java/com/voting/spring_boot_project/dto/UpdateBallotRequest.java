package com.voting.spring_boot_project.dto;

import java.util.Date;

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
    private String title;
    private String description;
    private Date startTime;
    private Date duration;
    private Option[] options;
}

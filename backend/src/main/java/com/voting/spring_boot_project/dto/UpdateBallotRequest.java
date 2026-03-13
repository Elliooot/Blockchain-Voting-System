package com.voting.spring_boot_project.dto;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.voting.spring_boot_project.entity.Option;

import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    @Size(max = 2000, message = "Description too long")
    private String description;
    private Date startTime;
    private Duration duration;
    private List<Option> options;
    private List<Integer> qualifiedVoterIds;
}

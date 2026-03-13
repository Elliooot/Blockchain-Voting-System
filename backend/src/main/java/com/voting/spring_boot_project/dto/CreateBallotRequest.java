package com.voting.spring_boot_project.dto;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.voting.spring_boot_project.entity.Option;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBallotRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be 200 characters or fewer")
    private String title;
    @Size(max = 2000, message = "Description too long")
    private String description;
    @NotNull(message = "Start time is required")
    private Date startTime;
    @NotNull(message = "Duration is required")
    private Duration duration;
    @Size(min = 2, message = "At least two options are required")
    private List<Option> options;
    private List<Integer> qualifiedVoterIds;
}

package com.voting.spring_boot_project.dto;

import java.util.Date;

import com.voting.spring_boot_project.entity.Option;

import jakarta.validation.constraints.NotBlank;
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
    private String title;
    private String description;
    
    @NotBlank(message = "Start Time is required")
    private Date startTime;

    @NotBlank(message = "Duration is required")
    private Date duration;
    private Option[] options;
}

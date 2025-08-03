package com.voting.spring_boot_project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {
    private Integer id;
    private String email;
    private List<Integer> userIds;
    private List<String> userEmails;
}

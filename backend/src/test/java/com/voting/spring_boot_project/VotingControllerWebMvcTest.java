package com.voting.spring_boot_project;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.spring_boot_project.controller.VotingController;
import com.voting.spring_boot_project.dto.VoteRecordResponse;
import com.voting.spring_boot_project.dto.VoteRequest;
import com.voting.spring_boot_project.dto.VoteResponse;
import com.voting.spring_boot_project.service.JwtService;
import com.voting.spring_boot_project.service.VotingService;

@WebMvcTest(controllers = VotingController.class)
@AutoConfigureMockMvc(addFilters = false)
class VotingControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private VotingService votingService;
    @MockitoBean private JwtService jwtService;

    @Test
    void vote_ok() throws Exception {
        VoteRequest req = new VoteRequest();
        req.setOptionId(1);
        when(votingService.castVote(req)).thenReturn(VoteResponse.builder().voteId(1).build());

        mockMvc.perform(post("/api/v1/voting/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    void records_ok() throws Exception {
        when(votingService.getVoteRecordsForCurrentUser()).thenReturn(List.of(VoteRecordResponse.builder().voteId(1).build()));
        mockMvc.perform(get("/api/v1/voting/records")).andExpect(status().isOk());
    }
}

package com.voting.spring_boot_project;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.voting.spring_boot_project.controller.BallotController;
import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.OptionResponse;
import com.voting.spring_boot_project.dto.ResultResponse;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.service.BallotService;
import com.voting.spring_boot_project.service.JwtService;

@WebMvcTest(controllers = BallotController.class)
@AutoConfigureMockMvc(addFilters = false)
class BallotControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BallotService ballotService;
    @MockitoBean private JwtService jwtService;

    @Test
    @DisplayName("GET /api/v1/ballots returns 200")
    void getBallotsForCurrentUser_ok() throws Exception {
        BallotResponse b = BallotResponse.builder()
            .id(1)
            .title("T")
            .description("D")
            .startTime(java.util.Date.from(Instant.now()))
            .duration(Duration.ofMinutes(5))
            .options(List.of(OptionResponse.builder().id(1).name("A").build()))
            .qualifiedVotersEmail(List.of())
            .qualifiedVotersId(List.of())
            .status(Status.Pending)
            .build();
        when(ballotService.getBallotsForCurrentUser()).thenReturn(List.of(b));

        mockMvc.perform(get("/api/v1/ballots").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/ballots/{id} returns 200")
    void getBallotById_ok() throws Exception {
        when(ballotService.getBallotById(1)).thenReturn(BallotResponse.builder().id(1).build());
        mockMvc.perform(get("/api/v1/ballots/1").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/ballots/result returns 200")
    void getResults_ok() throws Exception {
        when(ballotService.getResultForCurrentUser()).thenReturn(List.of(ResultResponse.builder().ballotId(1).build()));
        mockMvc.perform(get("/api/v1/ballots/result").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/ballots/delete/{id} returns 200")
    void delete_ok() throws Exception {
        mockMvc.perform(delete("/api/v1/ballots/delete/1").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }
}

package com.voting.spring_boot_project;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.spring_boot_project.controller.BallotController;
import com.voting.spring_boot_project.dto.BallotResponse;
import com.voting.spring_boot_project.dto.CreateBallotRequest;
import com.voting.spring_boot_project.dto.ResultResponse;
import com.voting.spring_boot_project.dto.UpdateBallotRequest;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.service.BallotService;
import com.voting.spring_boot_project.service.JwtService;

@WebMvcTest(controllers = BallotController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters for controller tests
class BallotControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private BallotService ballotService;
    @MockitoBean private JwtService jwtService;

    @Test
    void getBallotsForCurrentUser_ok() throws Exception {
        when(ballotService.getBallotsForCurrentUser()).thenReturn(
            List.of(BallotResponse.builder().id(1).title("T").status(Status.Pending).build())
        );

        mockMvc.perform(get("/api/v1/ballots"))
               .andExpect(status().isOk());

        verify(ballotService).getBallotsForCurrentUser();
    }

    @Test
    void getBallotById_ok() throws Exception {
        when(ballotService.getBallotById(1))
            .thenReturn(BallotResponse.builder().id(1).title("T").status(Status.Pending).build());

        mockMvc.perform(get("/api/v1/ballots/{id}", 1))
               .andExpect(status().isOk());

        verify(ballotService).getBallotById(1);
    }

    @Test
    void createBallot_ok() throws Exception {
        CreateBallotRequest req = new CreateBallotRequest();
        req.setTitle("Title");
        req.setDescription("Desc");
        req.setStartTime(java.util.Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
        req.setDuration(Duration.ofHours(1));
        req.setOptions(List.of());
        req.setQualifiedVoterIds(List.of());

        when(ballotService.createBallot(any(CreateBallotRequest.class)))
            .thenReturn(BallotResponse.builder().id(10).title("Title").status(Status.Pending).build());

        mockMvc.perform(post("/api/v1/ballots/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk());

        verify(ballotService).createBallot(any(CreateBallotRequest.class));
    }

    @Test
    void updateBallot_ok() throws Exception {
        UpdateBallotRequest req = new UpdateBallotRequest();
        req.setTitle("NewTitle");
        req.setDescription("NewDesc");

        when(ballotService.updateInfo(eq(1), any(UpdateBallotRequest.class)))
            .thenReturn(BallotResponse.builder().id(1).title("NewTitle").status(Status.Pending).build());

        mockMvc.perform(patch("/api/v1/ballots/update/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk());

        verify(ballotService).updateInfo(eq(1), any(UpdateBallotRequest.class));
    }

    @Test
    void deleteBallot_ok() throws Exception {
        doNothing().when(ballotService).deleteBallot(1);

        mockMvc.perform(delete("/api/v1/ballots/delete/{id}", 1))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("Ballot id: 1 delete successfully"));

        verify(ballotService).deleteBallot(1);
    }

    @Test
    void getBallotResult_ok() throws Exception {
        when(ballotService.getResultForCurrentUser())
            .thenReturn(List.of(ResultResponse.builder().ballotId(1).title("T").build()));

        mockMvc.perform(get("/api/v1/ballots/result"))
               .andExpect(status().isOk());

        verify(ballotService).finalizeExpiredBallots();
        verify(ballotService).getResultForCurrentUser();
    }
}
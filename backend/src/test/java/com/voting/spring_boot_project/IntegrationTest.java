package com.voting.spring_boot_project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.spring_boot_project.dto.AuthenticationRequest;
import com.voting.spring_boot_project.dto.RegisterRequest;
import com.voting.spring_boot_project.dto.UpdateWalletRequest;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.service.BlockchainEventListenerService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc // security filters ON by default
@ActiveProfiles("test")
class IntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // Prevents @PostConstruct from trying to connect to blockchain during test
    @MockitoBean private BlockchainEventListenerService blockchainEventListenerService;

    private String registerAndAuthenticate(String email, String password) throws Exception {
        // Register
        RegisterRequest reg = new RegisterRequest();
        reg.setFirstName("Test");
        reg.setLastName("User");
        reg.setEmail(email);
        reg.setPassword(password);
        reg.setRole(Role.Voter);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isOk());

        // Authenticate
        AuthenticationRequest authReq = new AuthenticationRequest();
        authReq.setEmail(email);
        authReq.setPassword(password);

        String authJson = mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(authJson);
        return node.get("token").asText();
    }

    @Test
    @DisplayName("Core integration flow: register -> authenticate -> user wallet get/update -> ballots list -> user search")
    void core_flow() throws Exception {
        final String email = "it_user@example.com";
        final String pwd   = "Password1!";
        final String token = registerAndAuthenticate(email, pwd);

        // GET wallet (should be null or absent)
        mockMvc.perform(get("/api/v1/user/get_wallet")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        // Update wallet
        UpdateWalletRequest uw = new UpdateWalletRequest();
        uw.setWalletAddress("0xabc1234567890def");

        mockMvc.perform(put("/api/v1/user/update_wallet")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uw)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.walletAddress").value("0xabc1234567890def"));

        // List ballots for current user (likely empty array initially)
        mockMvc.perform(get("/api/v1/ballots")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Search user by email
        mockMvc.perform(get("/api/v1/user/search")
                .header("Authorization", "Bearer " + token)
                .param("email", email))
            .andExpect(status().isOk());
    }
}
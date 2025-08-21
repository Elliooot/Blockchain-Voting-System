package com.voting.spring_boot_project;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.spring_boot_project.controller.AuthenticationController;
import com.voting.spring_boot_project.dto.AuthenticationRequest;
import com.voting.spring_boot_project.dto.AuthenticationResponse;
import com.voting.spring_boot_project.dto.RegisterRequest;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.service.AuthenticationService;
import com.voting.spring_boot_project.service.JwtService;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthenticationService authenticationService;
    @MockitoBean private JwtService jwtService;

    @Test
    void register_ok() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("user@example.com");
        req.setPassword("pwd");
        req.setRole(Role.Voter);

        when(authenticationService.register(req)).thenReturn(AuthenticationResponse.builder().token("t").build());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    void authenticate_ok() throws Exception {
        AuthenticationRequest req = new AuthenticationRequest();
        req.setEmail("user@example.com");
        req.setPassword("pwd");

        when(authenticationService.authenticate(req)).thenReturn(AuthenticationResponse.builder().token("t").build());

        mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }
}

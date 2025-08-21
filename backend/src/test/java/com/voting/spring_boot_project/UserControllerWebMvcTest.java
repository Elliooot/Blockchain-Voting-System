package com.voting.spring_boot_project;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.spring_boot_project.controller.UserController;
import com.voting.spring_boot_project.dto.UpdateWalletRequest;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.service.JwtService;
import com.voting.spring_boot_project.service.UserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebMvcTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserRepository userRepository;
    @MockitoBean private UserService userService;
    @MockitoBean private JwtService jwtService;

    @Test
    void search_user_ok() throws Exception {
        User u = User.builder().id(1).email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));

        mockMvc.perform(get("/api/v1/user/search").param("email", "user@example.com"))
               .andExpect(status().isOk());
    }

    @Test
    void get_all_ids_ok() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(User.builder().id(1).email("a@b.c").build()));
        mockMvc.perform(get("/api/v1/user/get_all_ids"))
               .andExpect(status().isOk());
    }

    @Test
    void get_wallet_ok() throws Exception {
        when(userService.getWalletAddress()).thenReturn(Map.of("walletAddress", "0xabc"));
        mockMvc.perform(get("/api/v1/user/get_wallet"))
               .andExpect(status().isOk());
    }

    @Test
    void update_wallet_ok() throws Exception {
        UpdateWalletRequest req = new UpdateWalletRequest();
        req.setWalletAddress("0x123");
        when(userService.updateWalletAddress(req)).thenReturn(Map.of("message", "ok", "walletAddress", "0x123"));

        mockMvc.perform(put("/api/v1/user/update_wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk());
    }
}

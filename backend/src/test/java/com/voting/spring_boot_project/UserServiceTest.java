package com.voting.spring_boot_project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.voting.spring_boot_project.dto.UpdateWalletRequest;
import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.entity.Vote;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.repository.VoteRepository;
import com.voting.spring_boot_project.service.JwtService;
import com.voting.spring_boot_project.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BallotRepository ballotRepository;
    @Mock private VoteRepository voteRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;
    @Mock private JwtService jwtService;

    private SecurityContext original;

    @BeforeEach
    void setup() {
        // Set a real Authentication into the static context
        var auth = new UsernamePasswordAuthenticationToken("user@example.com", "pwd");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getWalletAddress_returns_current_wallet() {
        User u = User.builder().id(1).email("user@example.com").walletAddress("0xabc").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));

        Map<String, String> res = userService.getWalletAddress();
        assertEquals("0xabc", res.get("walletAddress"));
    }

    @Test
    void updateWalletAddress_updates_value_and_persists() {
        User u = User.builder().id(1).email("user@example.com").walletAddress(null).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateWalletRequest req = new UpdateWalletRequest();
        req.setWalletAddress("0x123");
        Map<String, String> res = userService.updateWalletAddress(req);

        assertEquals("Wallet address updated successfully", res.get("message"));
        assertEquals("0x123", res.get("walletAddress"));
    }

    @Test
    void changePassword_validates_and_updates() {
        User u = User.builder().id(1).email("user@example.com").password("ENC").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("new", "ENC")).thenReturn(false);
        when(passwordEncoder.encode("new")).thenReturn("NEWENC");

        userService.changePassword("user@example.com", "old", "new");

        verify(userRepository).save(argThat(saved -> "NEWENC".equals(saved.getPassword())));
    }

    @Test
    void changePassword_rejects_same_password() {
        User u = User.builder().id(1).email("user@example.com").password("ENC").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        // new password equals current (matches)
        when(passwordEncoder.matches("old", "ENC")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> userService.changePassword("user@example.com", "old", "old"));
    }

    @Test
    void changePassword_rejects_wrong_current_password() {
        User u = User.builder().id(1).email("user@example.com").password("ENC").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("bad", "ENC")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> userService.changePassword("user@example.com", "bad", "new"));
    }
}

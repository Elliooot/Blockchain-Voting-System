package com.voting.spring_boot_project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voting.spring_boot_project.dto.GetUserResponse;
import com.voting.spring_boot_project.dto.UpdateWalletRequest;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.UserRepository;
import com.voting.spring_boot_project.service.UserService;

import lombok.RequiredArgsConstructor;
import okhttp3.Response;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    @Autowired
    private final UserRepository userRepository;

    private final UserService userService;

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public ResponseEntity<GetUserResponse> getUser(
        @RequestParam("email") String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        GetUserResponse response = GetUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("get_all_ids")
    @PreAuthorize("hasAuthority('ElectoralAdmin')")
    public ResponseEntity<GetUserResponse> getAllUser() {
        List<User> users = userRepository.findAll();

        List<Integer> userIds = users.stream()
                                    .map(User::getId)
                                    .collect(Collectors.toList());

        List<String> userEmails = users.stream()
                                    .map(User::getEmail)
                                    .collect(Collectors.toList());

        GetUserResponse response = GetUserResponse.builder()
                .userIds(userIds)
                .userEmails(userEmails)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_wallet")
    public ResponseEntity<Map<String, String>> getWalletAddress() {
        return ResponseEntity.ok(userService.getWalletAddress());
    }
    
    @PutMapping("/update_wallet")
    public ResponseEntity<Map<String, String>> updateWalletAddress(
        @RequestBody UpdateWalletRequest request
    ){
        return ResponseEntity.ok(userService.updateWalletAddress(request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        userService.deleteAccount(userEmail);
        
        return ResponseEntity.ok("Account deleted successfully");
    }
    
}

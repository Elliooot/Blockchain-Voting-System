package com.voting.spring_boot_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting.spring_boot_project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        userService.deleteAccount(userEmail);
        
        return ResponseEntity.ok("Account deleted successfully");
    }
    
}

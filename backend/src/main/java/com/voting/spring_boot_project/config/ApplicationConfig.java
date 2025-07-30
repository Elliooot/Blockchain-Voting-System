package com.voting.spring_boot_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            System.out.println("🔍 UserDetailsService - Looking for user: '" + username + "'");
            
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        System.out.println("❌ UserDetailsService - User not found: '" + username + "'");
                        return new UsernameNotFoundException("User not found");
                    });
            
            System.out.println("✅ UserDetailsService - Found user: " + user.getEmail());
            System.out.println("🎭 UserDetailsService - User role: " + user.getRole());
            return user;
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

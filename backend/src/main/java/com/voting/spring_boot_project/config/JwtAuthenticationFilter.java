package com.voting.spring_boot_project.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.voting.spring_boot_project.service.JwtService;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // --- 在方法最開始就加入 debug ---
        System.out.println("=================================");
        System.out.println("🌐 JWT Filter - Request URL: " + request.getRequestURL());
        System.out.println("🔧 JWT Filter - Request Method: " + request.getMethod());
        
        final String authHeader = request.getHeader("Authorization");
        System.out.println("🔑 JWT Filter - Authorization Header: " + authHeader);
        
        final String jwt;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ JWT Filter - No valid Authorization header, skipping JWT processing");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("🎫 JWT Filter - Extracted JWT (first 50 chars): " + jwt.substring(0, Math.min(50, jwt.length())) + "...");
        
        try {
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("📧 JWT Filter - Extracted email: '" + userEmail + "'");
            System.out.println("📧 JWT Filter - Email length: " + (userEmail != null ? userEmail.length() : "null"));
            System.out.println("📧 JWT Filter - Email bytes: " + (userEmail != null ? java.util.Arrays.toString(userEmail.getBytes()) : "null"));
        } catch (Exception e) {
            System.out.println("❌ JWT Filter - Failed to extract email: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                System.out.println("👤 JWT Filter - Loaded user: " + userDetails.getUsername());
                System.out.println("🔐 JWT Filter - User authorities: " + userDetails.getAuthorities());
                
                if(jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("✅ JWT Filter - Token is valid");
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println("🔒 JWT Filter - SecurityContext set successfully");
                    System.out.println("🎫 JWT Filter - Final authorities in context: " + 
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    System.out.println("❌ JWT Filter - Token is invalid");
                }
            } catch (Exception e) {
                System.out.println("❌ JWT Filter - Error during user loading or token validation: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ JWT Filter - User email is null or authentication already exists");
        }
        
        System.out.println("🏁 JWT Filter - Processing complete, continuing filter chain");
        System.out.println("=================================");
        
        filterChain.doFilter(request, response);
    }
    
}

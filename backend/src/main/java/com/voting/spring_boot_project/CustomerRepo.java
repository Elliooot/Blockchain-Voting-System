package com.voting.spring_boot_project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    
    // Custom query methods can be defined here if needed
    // For example, to find a customer by email:
    // Optional<Customer> findByEmail(String email);
    
}

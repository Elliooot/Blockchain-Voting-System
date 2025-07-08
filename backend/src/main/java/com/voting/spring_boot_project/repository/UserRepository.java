package com.voting.spring_boot_project.repository;

import java.util.Optional;
import com.voting.spring_boot_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);    
}

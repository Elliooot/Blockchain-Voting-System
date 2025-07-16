package com.voting.spring_boot_project.repository;

// import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voting.spring_boot_project.entity.Ballot;

@Repository
public interface BallotRepository extends JpaRepository<Ballot, Integer>{
    // Optional<Ballot> findById(Integer id);
}

package com.voting.spring_boot_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voting.spring_boot_project.entity.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>{
    
}

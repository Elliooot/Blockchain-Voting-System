package com.voting.spring_boot_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.entity.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>{
    boolean existsByBallotAndVoter(Ballot ballot, User voter);
    List<Vote> findByVoter(User voter);
}

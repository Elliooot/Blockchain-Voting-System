package com.voting.spring_boot_project.repository;

import java.util.List;
import java.util.Optional;

// import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.entity.User;

@Repository
public interface BallotRepository extends JpaRepository<Ballot, Integer>{
    List<Ballot> findByAdmin(User admin);

    @Query("SELECT b FROM Ballot b JOIN b.qualifiedVoters qv WHERE qv = :voter")
    List<Ballot> findBallotsForVoter(@Param("voter") User voter);

    @Query("SELECT DISTINCT v.ballot FROM Vote v WHERE v.voter = :voter AND v.ballot.status = :status")
    List<Ballot> findVotedAndEndedBallots(@Param("voter") User voter, @Param("status") Status status);

    @Query("SELECT b FROM Ballot b WHERE b.status = :endedStatus AND b.resultOptionIds IS EMPTY")
    List<Ballot> findExpiredBallotsWithoutResults(@Param ("endedStatus") Status endedStatus);

    Optional<Ballot> findByBlockchainBallotId(Long blockchainBallotId);
}

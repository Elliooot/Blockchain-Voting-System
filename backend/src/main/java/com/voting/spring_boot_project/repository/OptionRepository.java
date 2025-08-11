package com.voting.spring_boot_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer>{
    Optional<Option> findByBallotAndBlockchainOptionId(Ballot ballot, Long blockchainOptionId);
}

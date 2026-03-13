package com.voting.spring_boot_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voting.spring_boot_project.entity.BlockchainEventState;

@Repository
public interface BlockchainEventStateRepository extends JpaRepository<BlockchainEventState, String> {
}

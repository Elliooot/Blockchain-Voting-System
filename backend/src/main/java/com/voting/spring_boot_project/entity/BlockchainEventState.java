package com.voting.spring_boot_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blockchain_event_state")
public class BlockchainEventState {
    @Id
    @Column(name = "event_key", length = 128)
    private String eventKey;

    @Column(name = "last_processed_block", nullable = false)
    private Long lastProcessedBlock;
}

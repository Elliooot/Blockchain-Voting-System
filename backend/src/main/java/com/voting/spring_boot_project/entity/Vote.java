package com.voting.spring_boot_project.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "vote")
public class Vote {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ballot_id", nullable = false)
    private Ballot ballot;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User voter;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    private String transactionHash;
    private Date timestamp;
}

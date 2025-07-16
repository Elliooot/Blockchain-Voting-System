package com.voting.spring_boot_project.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// @Table(name = "vote")
public class Vote {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Integer electionId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Integer optionId;

    private Date timestamp;
    private String transactionHash;
    private boolean isSuccess;
}

package com.voting.spring_boot_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
// @Table(name = "option")
public class Option {
    @Id
    @GeneratedValue
    @Column(name = "option_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Integer electionId;

    private String name;
    private String description;
    private int voteCount;
    private int displayOrder;

}

package com.voting.spring_boot_project.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
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
@Table(name = "\"ballot\"")
public class Ballot {
    @Id
    @GeneratedValue
    @Column(name = "ballot_id")
    private Integer id;
    private User admin;
    private String title;
    private String description;
    private Date startTime;
    private Date duration;
    
    @Enumerated(EnumType.STRING)
    private Status status;

    private Option[] options;

    @Column(name = "contract_address", nullable = false, length = 255, unique = true)
    private String contractAddress;

    private String result;


}

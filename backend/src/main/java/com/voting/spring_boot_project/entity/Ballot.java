package com.voting.spring_boot_project.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
    
    @ManyToMany
    @JoinTable(
        name = "ballot_qualified_voters",
        joinColumns = @JoinColumn(name = "ballot_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> qualifiedVoters = new ArrayList<>();

    private String title;
    private String description;
    private Date startTime;
    private Duration duration;
    
    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder.Default
    @OneToMany(mappedBy = "ballot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    // @Column(name = "contract_address", nullable = false, length = 255, unique = true)
    private String contractAddress;

    private String result;

    @Transient // Not to map this method
    public Status getCurrentStatus() {
        Instant now = Instant.now();
        Instant startTime = this.getStartTime().toInstant();
        Instant endTime = startTime.plus(this.getDuration());

        if (now.isBefore(startTime)) {
            return Status.Pending;
        } else if (now.isBefore(endTime)) {
            return Status.Active;
        } else {
            return Status.Ended;
        }
    }
}

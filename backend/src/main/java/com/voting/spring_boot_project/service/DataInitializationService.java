package com.voting.spring_boot_project.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voting.spring_boot_project.entity.Ballot;
import com.voting.spring_boot_project.entity.Option;
import com.voting.spring_boot_project.entity.Role;
import com.voting.spring_boot_project.entity.Status;
import com.voting.spring_boot_project.entity.User;
import com.voting.spring_boot_project.repository.BallotRepository;
import com.voting.spring_boot_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements ApplicationRunner {

    private final BallotRepository ballotRepository;
    private final UserRepository userRepository;

    @Value("${demo.ballot-ids:}")
    private String demoBallotIdsCsv;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (demoBallotIdsCsv == null || demoBallotIdsCsv.isBlank()) {
            return;
        }

        List<Integer> demoIds = Arrays.stream(demoBallotIdsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        // Create demo admin user if not exists
        User demoAdmin = createDemoAdminIfNotExists();

        // Create demo ballots
        for (Integer ballotId : demoIds) {
            createDemoBallotIfNotExists(ballotId, demoAdmin);
        }
    }

    private User createDemoAdminIfNotExists() {
        String adminEmail = "demo.admin@voting.system";
        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);
        
        if (existingAdmin.isPresent()) {
            return existingAdmin.get();
        }

        User demoAdmin = User.builder()
                .firstName("Demo")
                .lastName("Admin")
                .email(adminEmail)
                .password("$2a$10$demoHashedPassword") // This is a placeholder - won't be used for login
                .role(Role.ElectoralAdmin)
                .build();

        userRepository.save(demoAdmin);
        return demoAdmin;
    }

    private void createDemoBallotIfNotExists(Integer ballotId, User admin) {
        // Check if a demo ballot with this title already exists instead of specific ID
        String title = getDemoBallotTitle(ballotId);
    boolean ballotExists = ballotRepository.findAll().stream()
        .anyMatch(b -> Objects.equals(b.getTitle(), title));
                
        if (ballotExists) {
            return;
        }

        // Create ballot options
        List<Option> options = createDemoBallotOptions(ballotId);

        // Create the ballot (let ID be auto-generated)
        Ballot demoBallot = Ballot.builder()
                .title(title)
                .description(getDemoBallotDescription(ballotId))
                .startTime(Date.from(Instant.now().minus(Duration.ofDays(60)))) // Started 60 days ago
                .duration(Duration.ofDays(30)) // 30 days duration
                .status(Status.Ended)
                .admin(admin)
                .options(options)
                .build();

        // Save ballot first to generate option IDs
        Ballot savedBallot = ballotRepository.save(demoBallot);

        Option maxVoteOption = null;
        List<Option> savedOptions = savedBallot.getOptions();
        if (savedOptions != null && !savedOptions.isEmpty()) {
            maxVoteOption = savedOptions.stream()
                .filter(opt -> opt != null)
                .max(java.util.Comparator.comparingInt(opt -> Optional.ofNullable(opt.getVoteCount()).orElse(0)))
                .orElse(null);
        }

        if (maxVoteOption != null) {
            // Hibernate may attempt to mutate collection instances during merge.
            // Use a mutable list implementation to avoid UnsupportedOperationException from immutable List.of(...).
            savedBallot.setResultOptionIds(new ArrayList<>(List.of(maxVoteOption.getId())));
            ballotRepository.save(savedBallot);
        }
    }

    private List<Option> createDemoBallotOptions(Integer ballotId) {
        return switch (ballotId) {
            case 102 -> new ArrayList<>(Arrays.asList(
                Option.builder().name("approve").description("Approve the project").voteCount(890).build(),
                Option.builder().name("reject").description("Reject the project").voteCount(368).build()
            ));
            case 103 -> new ArrayList<>(Arrays.asList(
                Option.builder().name("Candidate A").description("Candidate A is the best candidate").voteCount(1020).build(),
                Option.builder().name("Candidate B").description("Candidate B is the best candidate").voteCount(1560).build(),
                Option.builder().name("Candidate C").description("Candidate C is the best candidate").voteCount(870).build()
            ));
            case 104 -> new ArrayList<>(Arrays.asList(
                Option.builder().name("approve").description("Approve the project").voteCount(968).build(),
                Option.builder().name("reject").description("Reject the project").voteCount(1320).build()
            ));
            default -> new ArrayList<>(Arrays.asList(
                Option.builder().name("Option A").description("First option").build(),
                Option.builder().name("Option B").description("Second option").build()
            ));
        };
    }

    private String getDemoBallotTitle(Integer ballotId) {
        return switch (ballotId) {
            case 102 -> "Community Park Renovation Project";
            case 103 -> "Annual School Board Election";
            case 104 -> "City Council Motion #2025-08";
            default -> "Demo Ballot " + ballotId;
        };
    }

    private String getDemoBallotDescription(Integer ballotId) {
        return switch (ballotId) {
            case 102 -> "Vote on the proposed budget and design for the renovation of the central community park.";
            case 103 -> "Electing two new members to the district school board for the upcoming term.";
            case 104 -> "A motion to approve the new zoning regulations for the downtown commercial area.";
            default -> "This is a demonstration ballot created for testing purposes.";
        };
    }
}

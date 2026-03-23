package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.CreateCommitRequest;
import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.WeeklyCommitDto;
import com.weeklyplanning.domain.entity.PlanningWeek;
import com.weeklyplanning.domain.entity.WeeklyCommit;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidHierarchyException;
import com.weeklyplanning.domain.exception.WeekLockedException;
import com.weeklyplanning.infrastructure.repository.PlanningWeekRepository;
import com.weeklyplanning.infrastructure.repository.WeeklyCommitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WeeklyCommitServiceTest {

    @Autowired
    private WeeklyCommitService weeklyCommitService;

    @Autowired
    private PlanningWeekService planningWeekService;

    @Autowired
    private PlanningWeekRepository planningWeekRepository;

    @Autowired
    private WeeklyCommitRepository weeklyCommitRepository;

    @Test
    void createCommit_validatesHierarchy() {
        // RC 1 -> DO 1 -> Outcome 1 is a valid chain (from seed data)
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        CreateCommitRequest request = new CreateCommitRequest(
                "Test commit", "Description",
                1L, 1L, 1L, "KING", 1, false
        );

        WeeklyCommitDto result = weeklyCommitService.createCommit(week.id(), request);

        assertNotNull(result);
        assertEquals("Test commit", result.title());
        assertEquals(1L, result.rallyCryId());
    }

    @Test
    void createCommit_rejectsInvalidHierarchy() {
        // RC 1 -> DO 1 -> Outcome 5 is invalid (Outcome 5 belongs to DO 3)
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        CreateCommitRequest request = new CreateCommitRequest(
                "Bad commit", "Description",
                1L, 1L, 5L, "KING", 1, false
        );

        assertThrows(InvalidHierarchyException.class,
                () -> weeklyCommitService.createCommit(week.id(), request));
    }

    @Test
    void createCommit_rejectsLockedWeek() {
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        // Add a commit and lock the week
        WeeklyCommit commit = new WeeklyCommit();
        commit.setPlanningWeekId(week.id());
        commit.setTitle("Initial commit");
        commit.setRallyCryId(1L);
        commit.setDefiningObjectiveId(1L);
        commit.setOutcomeId(1L);
        commit.setChessCategoryCode("KING");
        commit.setPriorityRank(1);
        commit.setCreatedAt(Instant.now());
        commit.setUpdatedAt(Instant.now());
        weeklyCommitRepository.save(commit);

        planningWeekService.lockWeek(week.id(), 1L);

        CreateCommitRequest request = new CreateCommitRequest(
                "New commit", "Description",
                1L, 1L, 1L, "KING", 2, false
        );

        assertThrows(WeekLockedException.class,
                () -> weeklyCommitService.createCommit(week.id(), request));
    }
}

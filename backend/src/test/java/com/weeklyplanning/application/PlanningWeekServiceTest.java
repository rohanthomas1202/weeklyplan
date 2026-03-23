package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.domain.entity.PlanningWeek;
import com.weeklyplanning.domain.entity.WeeklyCommit;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidStatusTransitionException;
import com.weeklyplanning.infrastructure.repository.PlanningWeekRepository;
import com.weeklyplanning.infrastructure.repository.WeeklyCommitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlanningWeekServiceTest {

    @Autowired
    private PlanningWeekService planningWeekService;

    @Autowired
    private PlanningWeekRepository planningWeekRepository;

    @Autowired
    private WeeklyCommitRepository weeklyCommitRepository;

    @Test
    void getCurrentWeek_createsDraftIfNotExists() {
        // userId=1 is Sarah Chen (IC, team 1) from seed data
        PlanningWeekDto dto = planningWeekService.getCurrentWeek(1L);

        assertNotNull(dto);
        assertEquals(PlanningWeekStatus.DRAFT, dto.status());
        assertEquals(1L, dto.userId());

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        assertEquals(monday, dto.weekStartDate());
        assertEquals(monday.plusDays(4), dto.weekEndDate());
    }

    @Test
    void getCurrentWeek_returnsExistingWeek() {
        PlanningWeekDto first = planningWeekService.getCurrentWeek(1L);
        PlanningWeekDto second = planningWeekService.getCurrentWeek(1L);

        assertEquals(first.id(), second.id());
    }

    @Test
    void lockWeek_transitionsDraftToLocked() {
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        // Add a commit so we can lock
        WeeklyCommit commit = new WeeklyCommit();
        commit.setPlanningWeekId(week.id());
        commit.setTitle("Test commit");
        commit.setRallyCryId(1L);
        commit.setDefiningObjectiveId(1L);
        commit.setOutcomeId(1L);
        commit.setChessCategoryCode("KING");
        commit.setPriorityRank(1);
        commit.setCreatedAt(Instant.now());
        commit.setUpdatedAt(Instant.now());
        weeklyCommitRepository.save(commit);

        PlanningWeekDto locked = planningWeekService.lockWeek(week.id(), 1L);

        assertEquals(PlanningWeekStatus.LOCKED, locked.status());
        assertNotNull(locked.lockedAt());
    }

    @Test
    void lockWeek_failsIfNoCommits() {
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        assertThrows(IllegalArgumentException.class,
                () -> planningWeekService.lockWeek(week.id(), 1L));
    }

    @Test
    void lockWeek_failsIfNotDraft() {
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        // Add a commit and lock it
        WeeklyCommit commit = new WeeklyCommit();
        commit.setPlanningWeekId(week.id());
        commit.setTitle("Test commit");
        commit.setRallyCryId(1L);
        commit.setDefiningObjectiveId(1L);
        commit.setOutcomeId(1L);
        commit.setChessCategoryCode("KING");
        commit.setPriorityRank(1);
        commit.setCreatedAt(Instant.now());
        commit.setUpdatedAt(Instant.now());
        weeklyCommitRepository.save(commit);

        planningWeekService.lockWeek(week.id(), 1L);

        assertThrows(InvalidStatusTransitionException.class,
                () -> planningWeekService.lockWeek(week.id(), 1L));
    }
}

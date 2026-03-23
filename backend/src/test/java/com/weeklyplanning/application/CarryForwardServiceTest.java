package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.ReconcileCommitRequest;
import com.weeklyplanning.domain.entity.WeeklyCommit;
import com.weeklyplanning.domain.enums.CommitDisposition;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.infrastructure.repository.WeeklyCommitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CarryForwardServiceTest {

    @Autowired
    private CarryForwardService carryForwardService;

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private PlanningWeekService planningWeekService;

    @Autowired
    private WeeklyCommitRepository weeklyCommitRepository;

    @Test
    void carryForward_clonesMarkedCommitsToNextWeek() {
        // Create a draft week with 3 commits
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

        for (int i = 1; i <= 3; i++) {
            WeeklyCommit commit = new WeeklyCommit();
            commit.setPlanningWeekId(week.id());
            commit.setTitle("Commit " + i);
            commit.setRallyCryId(1L);
            commit.setDefiningObjectiveId(1L);
            commit.setOutcomeId(1L);
            commit.setChessCategoryCode("KING");
            commit.setPriorityRank(i);
            commit.setCreatedAt(Instant.now());
            commit.setUpdatedAt(Instant.now());
            weeklyCommitRepository.save(commit);
        }

        // Lock
        PlanningWeekDto locked = planningWeekService.lockWeek(week.id(), 1L);

        // Start reconciliation
        PlanningWeekDto reconciling = reconciliationService.startReconciliation(locked.id(), 1L);

        // Reconcile commit 1 as CARRIED_FORWARD
        reconciliationService.reconcileCommit(reconciling.id(),
                reconciling.commits().get(0).id(),
                new ReconcileCommitRequest(CommitDisposition.CARRIED_FORWARD,
                        null, BigDecimal.valueOf(50), null, false, null));

        // Reconcile commit 2 as COMPLETED (no carry forward)
        reconciliationService.reconcileCommit(reconciling.id(),
                reconciling.commits().get(1).id(),
                new ReconcileCommitRequest(CommitDisposition.COMPLETED,
                        "Done", BigDecimal.valueOf(100), null, false, null));

        // Reconcile commit 3 as CARRIED_FORWARD
        reconciliationService.reconcileCommit(reconciling.id(),
                reconciling.commits().get(2).id(),
                new ReconcileCommitRequest(CommitDisposition.CARRIED_FORWARD,
                        null, BigDecimal.valueOf(30), null, false, null));

        // Complete reconciliation
        PlanningWeekDto reconciled = reconciliationService.reconcileWeek(reconciling.id(), 1L);
        assertEquals(PlanningWeekStatus.RECONCILED, reconciled.status());

        // Carry forward
        PlanningWeekDto nextWeek = carryForwardService.carryForward(reconciled.id(), 1L);

        assertNotNull(nextWeek);
        assertEquals(PlanningWeekStatus.DRAFT, nextWeek.status());
        assertEquals(2, nextWeek.commits().size());

        // Verify source commit IDs are set
        assertTrue(nextWeek.commits().stream().allMatch(c -> c.sourceCommitId() != null));

        // Verify the next week starts 7 days after the current week
        assertEquals(reconciled.weekStartDate().plusDays(7), nextWeek.weekStartDate());
    }
}

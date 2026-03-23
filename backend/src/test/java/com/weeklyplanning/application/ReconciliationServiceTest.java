package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.ReconcileCommitRequest;
import com.weeklyplanning.api.dto.ReconciliationDto;
import com.weeklyplanning.domain.entity.WeeklyCommit;
import com.weeklyplanning.domain.enums.CommitDisposition;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidStatusTransitionException;
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
class ReconciliationServiceTest {

    @Autowired
    private ReconciliationService reconciliationService;

    @Autowired
    private PlanningWeekService planningWeekService;

    @Autowired
    private WeeklyCommitRepository weeklyCommitRepository;

    private PlanningWeekDto createLockedWeekWithCommit() {
        PlanningWeekDto week = planningWeekService.getCurrentWeek(1L);

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

        return planningWeekService.lockWeek(week.id(), 1L);
    }

    @Test
    void startReconciliation_transitionsLockedToReconciling() {
        PlanningWeekDto locked = createLockedWeekWithCommit();

        PlanningWeekDto reconciling = reconciliationService.startReconciliation(locked.id(), 1L);

        assertEquals(PlanningWeekStatus.RECONCILING, reconciling.status());
        assertNotNull(reconciling.reconcilingAt());
    }

    @Test
    void startReconciliation_failsIfNotLocked() {
        PlanningWeekDto draft = planningWeekService.getCurrentWeek(1L);

        assertThrows(InvalidStatusTransitionException.class,
                () -> reconciliationService.startReconciliation(draft.id(), 1L));
    }

    @Test
    void reconcileCommit_rejectsCompletedWithCarryForward() {
        PlanningWeekDto locked = createLockedWeekWithCommit();
        PlanningWeekDto reconciling = reconciliationService.startReconciliation(locked.id(), 1L);

        Long commitId = reconciling.commits().get(0).id();

        ReconcileCommitRequest request = new ReconcileCommitRequest(
                CommitDisposition.COMPLETED, "Done", BigDecimal.valueOf(100),
                null, true, null
        );

        assertThrows(IllegalArgumentException.class,
                () -> reconciliationService.reconcileCommit(reconciling.id(), commitId, request));
    }

    @Test
    void reconcileCommit_autoSetsCarryForwardForCarriedForwardDisposition() {
        PlanningWeekDto locked = createLockedWeekWithCommit();
        PlanningWeekDto reconciling = reconciliationService.startReconciliation(locked.id(), 1L);

        Long commitId = reconciling.commits().get(0).id();

        ReconcileCommitRequest request = new ReconcileCommitRequest(
                CommitDisposition.CARRIED_FORWARD, null, BigDecimal.valueOf(50),
                null, false, "Carrying forward"
        );

        ReconciliationDto result = reconciliationService.reconcileCommit(
                reconciling.id(), commitId, request);

        assertTrue(result.carryForward());
        assertEquals(CommitDisposition.CARRIED_FORWARD, result.disposition());
    }

    @Test
    void reconcileWeek_failsIfNotAllReconciled() {
        PlanningWeekDto locked = createLockedWeekWithCommit();
        PlanningWeekDto reconciling = reconciliationService.startReconciliation(locked.id(), 1L);

        // Don't reconcile the commit, then try to complete
        assertThrows(IllegalArgumentException.class,
                () -> reconciliationService.reconcileWeek(reconciling.id(), 1L));
    }
}

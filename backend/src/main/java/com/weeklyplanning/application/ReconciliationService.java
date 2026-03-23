package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.ReconcileCommitRequest;
import com.weeklyplanning.api.dto.ReconciliationDto;
import com.weeklyplanning.domain.entity.PlanningWeek;
import com.weeklyplanning.domain.entity.WeeklyCommit;
import com.weeklyplanning.domain.entity.WeeklyCommitReconciliation;
import com.weeklyplanning.domain.enums.CommitDisposition;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidStatusTransitionException;
import com.weeklyplanning.infrastructure.repository.PlanningWeekRepository;
import com.weeklyplanning.infrastructure.repository.WeeklyCommitReconciliationRepository;
import com.weeklyplanning.infrastructure.repository.WeeklyCommitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ReconciliationService {

    private final PlanningWeekRepository planningWeekRepository;
    private final WeeklyCommitRepository weeklyCommitRepository;
    private final WeeklyCommitReconciliationRepository reconciliationRepository;
    private final PlanningWeekService planningWeekService;

    public ReconciliationService(PlanningWeekRepository planningWeekRepository,
                                  WeeklyCommitRepository weeklyCommitRepository,
                                  WeeklyCommitReconciliationRepository reconciliationRepository,
                                  PlanningWeekService planningWeekService) {
        this.planningWeekRepository = planningWeekRepository;
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.planningWeekService = planningWeekService;
    }

    public PlanningWeekDto startReconciliation(Long weekId, Long userId) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));

        if (week.getStatus() != PlanningWeekStatus.LOCKED) {
            throw new InvalidStatusTransitionException(week.getStatus().name(), PlanningWeekStatus.RECONCILING.name());
        }

        week.setStatus(PlanningWeekStatus.RECONCILING);
        week.setReconcilingAt(Instant.now());
        week = planningWeekRepository.save(week);
        return planningWeekService.toDto(week);
    }

    public ReconciliationDto reconcileCommit(Long weekId, Long commitId, ReconcileCommitRequest request) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));

        if (week.getStatus() != PlanningWeekStatus.RECONCILING) {
            throw new InvalidStatusTransitionException(week.getStatus().name(), "RECONCILE_COMMIT");
        }

        WeeklyCommit commit = weeklyCommitRepository.findById(commitId)
                .orElseThrow(() -> new EntityNotFoundException("Commit not found"));

        boolean carryForward = request.carryForward();
        CommitDisposition disposition = request.disposition();

        if (disposition == CommitDisposition.CARRIED_FORWARD) {
            carryForward = true;
        }

        if (disposition == CommitDisposition.COMPLETED && request.carryForward()) {
            throw new IllegalArgumentException("Cannot carry forward a completed commit");
        }

        if (disposition == CommitDisposition.DROPPED && request.carryForward()) {
            throw new IllegalArgumentException("Cannot carry forward a dropped commit");
        }

        WeeklyCommitReconciliation recon = reconciliationRepository.findByWeeklyCommitId(commitId)
                .orElseGet(() -> {
                    WeeklyCommitReconciliation r = new WeeklyCommitReconciliation();
                    r.setWeeklyCommitId(commitId);
                    return r;
                });

        recon.setDisposition(disposition);
        recon.setActualResult(request.actualResult());
        recon.setPercentComplete(request.percentComplete());
        recon.setBlockerNotes(request.blockerNotes());
        recon.setCarryForward(carryForward);
        recon.setReconciliationNotes(request.reconciliationNotes());
        recon.setReconciledAt(Instant.now());

        recon = reconciliationRepository.save(recon);

        return new ReconciliationDto(
                recon.getId(), recon.getWeeklyCommitId(), recon.getDisposition(),
                recon.getActualResult(), recon.getPercentComplete(), recon.getBlockerNotes(),
                recon.isCarryForward(), recon.getReconciliationNotes(), recon.getReconciledAt()
        );
    }

    public PlanningWeekDto updateWeekSummary(Long weekId, String blockersSummary, String managerNotes) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));

        if (week.getStatus() != PlanningWeekStatus.RECONCILING) {
            throw new InvalidStatusTransitionException(week.getStatus().name(), "UPDATE_SUMMARY");
        }

        week.setBlockersSummary(blockersSummary);
        week.setManagerNotes(managerNotes);
        week = planningWeekRepository.save(week);
        return planningWeekService.toDto(week);
    }

    public PlanningWeekDto reconcileWeek(Long weekId, Long userId) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));

        if (week.getStatus() != PlanningWeekStatus.RECONCILING) {
            throw new InvalidStatusTransitionException(week.getStatus().name(), PlanningWeekStatus.RECONCILED.name());
        }

        List<WeeklyCommit> commits = weeklyCommitRepository
                .findByPlanningWeekIdOrderByPriorityRankAsc(weekId);
        List<Long> commitIds = commits.stream().map(WeeklyCommit::getId).toList();

        long reconciledCount = reconciliationRepository.findByWeeklyCommitIdIn(commitIds).size();
        if (reconciledCount < commits.size()) {
            throw new IllegalArgumentException("All commits must be reconciled before completing reconciliation");
        }

        week.setStatus(PlanningWeekStatus.RECONCILED);
        week.setReconciledAt(Instant.now());
        week = planningWeekRepository.save(week);
        return planningWeekService.toDto(week);
    }
}

package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.*;
import com.weeklyplanning.domain.entity.*;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidStatusTransitionException;
import com.weeklyplanning.infrastructure.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanningWeekService {

    private final PlanningWeekRepository planningWeekRepository;
    private final WeeklyCommitRepository weeklyCommitRepository;
    private final WeeklyCommitReconciliationRepository reconciliationRepository;
    private final AppUserRepository appUserRepository;
    private final RallyCryRepository rallyCryRepository;
    private final DefiningObjectiveRepository definingObjectiveRepository;
    private final OutcomeRepository outcomeRepository;
    private final ChessCategoryRepository chessCategoryRepository;

    public PlanningWeekService(PlanningWeekRepository planningWeekRepository,
                               WeeklyCommitRepository weeklyCommitRepository,
                               WeeklyCommitReconciliationRepository reconciliationRepository,
                               AppUserRepository appUserRepository,
                               RallyCryRepository rallyCryRepository,
                               DefiningObjectiveRepository definingObjectiveRepository,
                               OutcomeRepository outcomeRepository,
                               ChessCategoryRepository chessCategoryRepository) {
        this.planningWeekRepository = planningWeekRepository;
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.appUserRepository = appUserRepository;
        this.rallyCryRepository = rallyCryRepository;
        this.definingObjectiveRepository = definingObjectiveRepository;
        this.outcomeRepository = outcomeRepository;
        this.chessCategoryRepository = chessCategoryRepository;
    }

    public PlanningWeekDto getCurrentWeek(Long userId) {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate friday = monday.plusDays(4);

        return planningWeekRepository.findByUserIdAndWeekStartDate(userId, monday)
                .map(this::toDto)
                .orElseGet(() -> {
                    AppUser user = appUserRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found"));

                    PlanningWeek week = new PlanningWeek();
                    week.setUserId(userId);
                    week.setTeamId(user.getTeamId());
                    week.setWeekStartDate(monday);
                    week.setWeekEndDate(friday);
                    week.setStatus(PlanningWeekStatus.DRAFT);
                    week.setCreatedAt(Instant.now());
                    week.setUpdatedAt(Instant.now());

                    week = planningWeekRepository.save(week);
                    return toDto(week);
                });
    }

    public PlanningWeekDto getWeek(Long weekId) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));
        return toDto(week);
    }

    public PlanningWeekDto lockWeek(Long weekId, Long userId) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));

        if (!week.getUserId().equals(userId)) {
            throw new EntityNotFoundException("Planning week not found");
        }

        if (week.getStatus() != PlanningWeekStatus.DRAFT) {
            throw new InvalidStatusTransitionException(week.getStatus().name(), PlanningWeekStatus.LOCKED.name());
        }

        int commitCount = weeklyCommitRepository.countByPlanningWeekId(weekId);
        if (commitCount == 0) {
            throw new IllegalArgumentException("Cannot lock a week with no commits");
        }

        week.setStatus(PlanningWeekStatus.LOCKED);
        week.setLockedAt(Instant.now());
        week = planningWeekRepository.save(week);
        return toDto(week);
    }

    public PlanningWeekDto toDto(PlanningWeek week) {
        List<WeeklyCommit> commits = weeklyCommitRepository
                .findByPlanningWeekIdOrderByPriorityRankAsc(week.getId());

        List<Long> commitIds = commits.stream().map(WeeklyCommit::getId).toList();
        Map<Long, WeeklyCommitReconciliation> reconciliationMap = reconciliationRepository
                .findByWeeklyCommitIdIn(commitIds).stream()
                .collect(Collectors.toMap(WeeklyCommitReconciliation::getWeeklyCommitId, Function.identity()));

        List<WeeklyCommitDto> commitDtos = commits.stream().map(c -> {
            String rallyCryTitle = rallyCryRepository.findById(c.getRallyCryId())
                    .map(RallyCry::getTitle).orElse(null);
            String doTitle = definingObjectiveRepository.findById(c.getDefiningObjectiveId())
                    .map(DefiningObjective::getTitle).orElse(null);
            String outcomeTitle = outcomeRepository.findById(c.getOutcomeId())
                    .map(Outcome::getTitle).orElse(null);
            String chessCategoryDisplayName = chessCategoryRepository.findById(c.getChessCategoryCode())
                    .map(ChessCategory::getDisplayName).orElse(null);

            WeeklyCommitReconciliation recon = reconciliationMap.get(c.getId());
            ReconciliationDto reconDto = recon != null ? new ReconciliationDto(
                    recon.getId(), recon.getWeeklyCommitId(), recon.getDisposition(),
                    recon.getActualResult(), recon.getPercentComplete(), recon.getBlockerNotes(),
                    recon.isCarryForward(), recon.getReconciliationNotes(), recon.getReconciledAt()
            ) : null;

            return new WeeklyCommitDto(
                    c.getId(), c.getTitle(), c.getDescription(),
                    c.getRallyCryId(), rallyCryTitle,
                    c.getDefiningObjectiveId(), doTitle,
                    c.getOutcomeId(), outcomeTitle,
                    c.getChessCategoryCode(), chessCategoryDisplayName,
                    c.getPriorityRank(), c.isStretch(), c.getSourceCommitId(),
                    reconDto
            );
        }).toList();

        return new PlanningWeekDto(
                week.getId(), week.getUserId(), week.getTeamId(),
                week.getWeekStartDate(), week.getWeekEndDate(),
                week.getStatus(), week.getLockedAt(),
                week.getReconcilingAt(), week.getReconciledAt(),
                week.getBlockersSummary(), week.getManagerNotes(),
                commitDtos
        );
    }
}

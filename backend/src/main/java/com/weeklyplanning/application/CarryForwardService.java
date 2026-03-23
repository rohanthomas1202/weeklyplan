package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.domain.entity.AppUser;
import com.weeklyplanning.domain.entity.PlanningWeek;
import com.weeklyplanning.domain.entity.WeeklyCommit;
import com.weeklyplanning.domain.entity.WeeklyCommitReconciliation;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidStatusTransitionException;
import com.weeklyplanning.infrastructure.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarryForwardService {

    private final PlanningWeekRepository planningWeekRepository;
    private final WeeklyCommitRepository weeklyCommitRepository;
    private final WeeklyCommitReconciliationRepository reconciliationRepository;
    private final AppUserRepository appUserRepository;
    private final PlanningWeekService planningWeekService;

    public CarryForwardService(PlanningWeekRepository planningWeekRepository,
                                WeeklyCommitRepository weeklyCommitRepository,
                                WeeklyCommitReconciliationRepository reconciliationRepository,
                                AppUserRepository appUserRepository,
                                PlanningWeekService planningWeekService) {
        this.planningWeekRepository = planningWeekRepository;
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.appUserRepository = appUserRepository;
        this.planningWeekService = planningWeekService;
    }

    public PlanningWeekDto carryForward(Long weekId, Long userId) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));

        if (week.getStatus() != PlanningWeekStatus.RECONCILED) {
            throw new InvalidStatusTransitionException(week.getStatus().name(), "CARRY_FORWARD");
        }

        List<WeeklyCommit> commits = weeklyCommitRepository
                .findByPlanningWeekIdOrderByPriorityRankAsc(weekId);
        List<Long> commitIds = commits.stream().map(WeeklyCommit::getId).toList();

        Map<Long, WeeklyCommitReconciliation> reconMap = reconciliationRepository
                .findByWeeklyCommitIdIn(commitIds).stream()
                .collect(Collectors.toMap(WeeklyCommitReconciliation::getWeeklyCommitId, Function.identity()));

        List<WeeklyCommit> carryForwardCommits = commits.stream()
                .filter(c -> {
                    WeeklyCommitReconciliation recon = reconMap.get(c.getId());
                    return recon != null && recon.isCarryForward();
                })
                .toList();

        // Calculate next Monday
        java.time.LocalDate nextMonday = week.getWeekStartDate().plusDays(7);
        java.time.LocalDate nextFriday = nextMonday.plusDays(4);

        // Get or create next week
        PlanningWeek nextWeek = planningWeekRepository
                .findByUserIdAndWeekStartDate(userId, nextMonday)
                .orElseGet(() -> {
                    AppUser user = appUserRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found"));

                    PlanningWeek nw = new PlanningWeek();
                    nw.setUserId(userId);
                    nw.setTeamId(user.getTeamId());
                    nw.setWeekStartDate(nextMonday);
                    nw.setWeekEndDate(nextFriday);
                    nw.setStatus(PlanningWeekStatus.DRAFT);
                    nw.setCreatedAt(Instant.now());
                    nw.setUpdatedAt(Instant.now());
                    return planningWeekRepository.save(nw);
                });

        // Count existing commits for priority offset
        int existingCount = weeklyCommitRepository.countByPlanningWeekId(nextWeek.getId());

        // Clone carry-forward commits
        for (int i = 0; i < carryForwardCommits.size(); i++) {
            WeeklyCommit source = carryForwardCommits.get(i);
            WeeklyCommit clone = new WeeklyCommit();
            clone.setPlanningWeekId(nextWeek.getId());
            clone.setTitle(source.getTitle());
            clone.setDescription(source.getDescription());
            clone.setRallyCryId(source.getRallyCryId());
            clone.setDefiningObjectiveId(source.getDefiningObjectiveId());
            clone.setOutcomeId(source.getOutcomeId());
            clone.setChessCategoryCode(source.getChessCategoryCode());
            clone.setStretch(source.isStretch());
            clone.setSourceCommitId(source.getId());
            clone.setPriorityRank(existingCount + i + 1);
            clone.setCreatedAt(Instant.now());
            clone.setUpdatedAt(Instant.now());
            weeklyCommitRepository.save(clone);
        }

        return planningWeekService.toDto(nextWeek);
    }
}

package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.*;
import com.weeklyplanning.domain.entity.*;
import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import com.weeklyplanning.domain.exception.InvalidHierarchyException;
import com.weeklyplanning.domain.exception.WeekLockedException;
import com.weeklyplanning.infrastructure.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class WeeklyCommitService {

    private final WeeklyCommitRepository weeklyCommitRepository;
    private final PlanningWeekRepository planningWeekRepository;
    private final RallyCryRepository rallyCryRepository;
    private final DefiningObjectiveRepository definingObjectiveRepository;
    private final OutcomeRepository outcomeRepository;
    private final ChessCategoryRepository chessCategoryRepository;
    private final WeeklyCommitReconciliationRepository reconciliationRepository;

    public WeeklyCommitService(WeeklyCommitRepository weeklyCommitRepository,
                               PlanningWeekRepository planningWeekRepository,
                               RallyCryRepository rallyCryRepository,
                               DefiningObjectiveRepository definingObjectiveRepository,
                               OutcomeRepository outcomeRepository,
                               ChessCategoryRepository chessCategoryRepository,
                               WeeklyCommitReconciliationRepository reconciliationRepository) {
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.planningWeekRepository = planningWeekRepository;
        this.rallyCryRepository = rallyCryRepository;
        this.definingObjectiveRepository = definingObjectiveRepository;
        this.outcomeRepository = outcomeRepository;
        this.chessCategoryRepository = chessCategoryRepository;
        this.reconciliationRepository = reconciliationRepository;
    }

    public WeeklyCommitDto createCommit(Long weekId, CreateCommitRequest request) {
        PlanningWeek week = loadAndVerifyDraft(weekId);

        validateHierarchy(request.rallyCryId(), request.definingObjectiveId(), request.outcomeId());

        WeeklyCommit commit = new WeeklyCommit();
        commit.setPlanningWeekId(weekId);
        commit.setTitle(request.title());
        commit.setDescription(request.description());
        commit.setRallyCryId(request.rallyCryId());
        commit.setDefiningObjectiveId(request.definingObjectiveId());
        commit.setOutcomeId(request.outcomeId());
        commit.setChessCategoryCode(request.chessCategoryCode());
        commit.setPriorityRank(request.priorityRank());
        commit.setStretch(request.stretch());
        commit.setCreatedAt(Instant.now());
        commit.setUpdatedAt(Instant.now());

        commit = weeklyCommitRepository.save(commit);
        return toDto(commit);
    }

    public WeeklyCommitDto updateCommit(Long weekId, Long commitId, UpdateCommitRequest request) {
        loadAndVerifyDraft(weekId);

        validateHierarchy(request.rallyCryId(), request.definingObjectiveId(), request.outcomeId());

        WeeklyCommit commit = weeklyCommitRepository.findById(commitId)
                .orElseThrow(() -> new EntityNotFoundException("Commit not found"));

        commit.setTitle(request.title());
        commit.setDescription(request.description());
        commit.setRallyCryId(request.rallyCryId());
        commit.setDefiningObjectiveId(request.definingObjectiveId());
        commit.setOutcomeId(request.outcomeId());
        commit.setChessCategoryCode(request.chessCategoryCode());
        commit.setPriorityRank(request.priorityRank());
        commit.setStretch(request.stretch());

        commit = weeklyCommitRepository.save(commit);
        return toDto(commit);
    }

    public void deleteCommit(Long weekId, Long commitId) {
        loadAndVerifyDraft(weekId);

        WeeklyCommit commit = weeklyCommitRepository.findById(commitId)
                .orElseThrow(() -> new EntityNotFoundException("Commit not found"));

        weeklyCommitRepository.delete(commit);
    }

    public List<WeeklyCommitDto> reorderCommits(Long weekId, ReorderRequest request) {
        loadAndVerifyDraft(weekId);

        List<Long> commitIds = request.commitIds();
        for (int i = 0; i < commitIds.size(); i++) {
            WeeklyCommit commit = weeklyCommitRepository.findById(commitIds.get(i))
                    .orElseThrow(() -> new EntityNotFoundException("Commit not found"));
            commit.setPriorityRank(i + 1);
            weeklyCommitRepository.save(commit);
        }

        return weeklyCommitRepository.findByPlanningWeekIdOrderByPriorityRankAsc(weekId)
                .stream().map(this::toDto).toList();
    }

    void validateHierarchy(Long rallyCryId, Long definingObjectiveId, Long outcomeId) {
        Outcome outcome = outcomeRepository.findById(outcomeId)
                .orElseThrow(() -> new EntityNotFoundException("Outcome not found"));
        if (!outcome.getDefiningObjectiveId().equals(definingObjectiveId)) {
            throw new InvalidHierarchyException("Outcome does not belong to the specified Defining Objective");
        }
        DefiningObjective defObj = definingObjectiveRepository.findById(definingObjectiveId)
                .orElseThrow(() -> new EntityNotFoundException("Defining Objective not found"));
        if (!defObj.getRallyCryId().equals(rallyCryId)) {
            throw new InvalidHierarchyException("Defining Objective does not belong to the specified Rally Cry");
        }
    }

    private PlanningWeek loadAndVerifyDraft(Long weekId) {
        PlanningWeek week = planningWeekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Planning week not found"));
        if (week.getStatus() != PlanningWeekStatus.DRAFT) {
            throw new WeekLockedException();
        }
        return week;
    }

    private WeeklyCommitDto toDto(WeeklyCommit c) {
        String rallyCryTitle = rallyCryRepository.findById(c.getRallyCryId())
                .map(RallyCry::getTitle).orElse(null);
        String doTitle = definingObjectiveRepository.findById(c.getDefiningObjectiveId())
                .map(DefiningObjective::getTitle).orElse(null);
        String outcomeTitle = outcomeRepository.findById(c.getOutcomeId())
                .map(Outcome::getTitle).orElse(null);
        String chessCategoryDisplayName = chessCategoryRepository.findById(c.getChessCategoryCode())
                .map(ChessCategory::getDisplayName).orElse(null);

        ReconciliationDto reconDto = reconciliationRepository.findByWeeklyCommitId(c.getId())
                .map(r -> new ReconciliationDto(
                        r.getId(), r.getWeeklyCommitId(), r.getDisposition(),
                        r.getActualResult(), r.getPercentComplete(), r.getBlockerNotes(),
                        r.isCarryForward(), r.getReconciliationNotes(), r.getReconciledAt()))
                .orElse(null);

        return new WeeklyCommitDto(
                c.getId(), c.getTitle(), c.getDescription(),
                c.getRallyCryId(), rallyCryTitle,
                c.getDefiningObjectiveId(), doTitle,
                c.getOutcomeId(), outcomeTitle,
                c.getChessCategoryCode(), chessCategoryDisplayName,
                c.getPriorityRank(), c.isStretch(), c.getSourceCommitId(),
                reconDto
        );
    }
}

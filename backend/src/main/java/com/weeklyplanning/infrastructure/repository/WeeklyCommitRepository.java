package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.WeeklyCommit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeeklyCommitRepository extends JpaRepository<WeeklyCommit, Long> {
    List<WeeklyCommit> findByPlanningWeekIdOrderByPriorityRankAsc(Long planningWeekId);
    int countByPlanningWeekId(Long planningWeekId);
}

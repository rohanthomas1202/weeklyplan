package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.PlanningWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanningWeekRepository extends JpaRepository<PlanningWeek, Long> {
    Optional<PlanningWeek> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate);
    List<PlanningWeek> findByTeamIdAndWeekStartDate(Long teamId, LocalDate weekStartDate);
}

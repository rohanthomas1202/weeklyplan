package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.WeeklyCommitReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WeeklyCommitReconciliationRepository extends JpaRepository<WeeklyCommitReconciliation, Long> {
    Optional<WeeklyCommitReconciliation> findByWeeklyCommitId(Long weeklyCommitId);
    List<WeeklyCommitReconciliation> findByWeeklyCommitIdIn(List<Long> commitIds);
}

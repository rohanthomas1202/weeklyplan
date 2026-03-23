package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.Outcome;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutcomeRepository extends JpaRepository<Outcome, Long> {
    List<Outcome> findByDefiningObjectiveId(Long definingObjectiveId);
}

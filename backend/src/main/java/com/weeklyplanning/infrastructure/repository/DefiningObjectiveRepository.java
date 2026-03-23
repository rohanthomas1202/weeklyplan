package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.DefiningObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DefiningObjectiveRepository extends JpaRepository<DefiningObjective, Long> {
    List<DefiningObjective> findByRallyCryId(Long rallyCryId);
}

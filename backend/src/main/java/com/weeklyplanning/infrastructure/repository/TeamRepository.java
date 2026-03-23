package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}

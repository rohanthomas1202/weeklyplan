package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.RallyCry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RallyCryRepository extends JpaRepository<RallyCry, Long> {
}

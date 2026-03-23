package com.weeklyplanning.infrastructure.repository;

import com.weeklyplanning.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByTeamId(Long teamId);
}

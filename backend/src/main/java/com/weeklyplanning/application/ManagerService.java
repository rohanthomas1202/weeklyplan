package com.weeklyplanning.application;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.TeamWeekStatusDto;
import com.weeklyplanning.domain.entity.AppUser;
import com.weeklyplanning.domain.entity.PlanningWeek;
import com.weeklyplanning.domain.enums.UserRole;
import com.weeklyplanning.infrastructure.repository.AppUserRepository;
import com.weeklyplanning.infrastructure.repository.PlanningWeekRepository;
import com.weeklyplanning.infrastructure.repository.WeeklyCommitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    private final AppUserRepository appUserRepository;
    private final PlanningWeekRepository planningWeekRepository;
    private final WeeklyCommitRepository weeklyCommitRepository;
    private final PlanningWeekService planningWeekService;

    public ManagerService(AppUserRepository appUserRepository,
                          PlanningWeekRepository planningWeekRepository,
                          WeeklyCommitRepository weeklyCommitRepository,
                          PlanningWeekService planningWeekService) {
        this.appUserRepository = appUserRepository;
        this.planningWeekRepository = planningWeekRepository;
        this.weeklyCommitRepository = weeklyCommitRepository;
        this.planningWeekService = planningWeekService;
    }

    public List<TeamWeekStatusDto> getTeamWeeks(Long managerId) {
        AppUser manager = verifyManager(managerId);

        Long teamId = manager.getTeamId();
        List<AppUser> teamMembers = appUserRepository.findByTeamId(teamId);

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        List<PlanningWeek> weeks = planningWeekRepository.findByTeamIdAndWeekStartDate(teamId, monday);
        Map<Long, PlanningWeek> weekByUserId = weeks.stream()
                .collect(Collectors.toMap(PlanningWeek::getUserId, Function.identity()));

        return teamMembers.stream().map(member -> {
            PlanningWeek week = weekByUserId.get(member.getId());
            if (week != null) {
                int commitCount = weeklyCommitRepository.countByPlanningWeekId(week.getId());
                return new TeamWeekStatusDto(
                        member.getId(), member.getName(), member.getRole().name(),
                        week.getId(), week.getStatus().name(), commitCount, week.getWeekStartDate()
                );
            } else {
                return new TeamWeekStatusDto(
                        member.getId(), member.getName(), member.getRole().name(),
                        null, null, 0, monday
                );
            }
        }).toList();
    }

    public PlanningWeekDto getTeamMemberWeek(Long managerId, Long userId, Long weekId) {
        AppUser manager = verifyManager(managerId);

        AppUser member = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!manager.getTeamId().equals(member.getTeamId())) {
            throw new ManagerAccessDeniedException("Manager and user must be on the same team");
        }

        return planningWeekService.getWeek(weekId);
    }

    private AppUser verifyManager(Long managerId) {
        AppUser manager = appUserRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (manager.getRole() != UserRole.MANAGER) {
            throw new ManagerAccessDeniedException("Only managers can access this resource");
        }
        return manager;
    }

    public static class ManagerAccessDeniedException extends RuntimeException {
        public ManagerAccessDeniedException(String message) {
            super(message);
        }
    }
}

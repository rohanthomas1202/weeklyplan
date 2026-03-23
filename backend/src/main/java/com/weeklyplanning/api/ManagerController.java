package com.weeklyplanning.api;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.TeamWeekStatusDto;
import com.weeklyplanning.application.ManagerService;
import com.weeklyplanning.infrastructure.config.UserContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/team/weeks")
    public List<TeamWeekStatusDto> getTeamWeeks() {
        return managerService.getTeamWeeks(UserContext.get().getId());
    }

    @GetMapping("/users/{userId}/weeks/{weekId}")
    public PlanningWeekDto getTeamMemberWeek(@PathVariable Long userId,
                                              @PathVariable Long weekId) {
        return managerService.getTeamMemberWeek(UserContext.get().getId(), userId, weekId);
    }
}

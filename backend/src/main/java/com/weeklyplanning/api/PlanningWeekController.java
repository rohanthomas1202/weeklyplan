package com.weeklyplanning.api;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.ReorderRequest;
import com.weeklyplanning.api.dto.WeeklyCommitDto;
import com.weeklyplanning.application.PlanningWeekService;
import com.weeklyplanning.application.WeeklyCommitService;
import com.weeklyplanning.infrastructure.config.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weeks")
public class PlanningWeekController {

    private final PlanningWeekService planningWeekService;
    private final WeeklyCommitService weeklyCommitService;

    public PlanningWeekController(PlanningWeekService planningWeekService,
                                  WeeklyCommitService weeklyCommitService) {
        this.planningWeekService = planningWeekService;
        this.weeklyCommitService = weeklyCommitService;
    }

    @GetMapping("/current")
    public PlanningWeekDto getCurrentWeek() {
        return planningWeekService.getCurrentWeek(UserContext.get().getId());
    }

    @GetMapping("/{weekId}")
    public PlanningWeekDto getWeek(@PathVariable Long weekId) {
        return planningWeekService.getWeek(weekId);
    }

    @PostMapping("/{weekId}/lock")
    public PlanningWeekDto lockWeek(@PathVariable Long weekId) {
        return planningWeekService.lockWeek(weekId, UserContext.get().getId());
    }

    @PutMapping("/{weekId}/reorder")
    public List<WeeklyCommitDto> reorderCommits(@PathVariable Long weekId,
                                                 @Valid @RequestBody ReorderRequest request) {
        return weeklyCommitService.reorderCommits(weekId, request);
    }
}

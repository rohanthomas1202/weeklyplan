package com.weeklyplanning.api;

import com.weeklyplanning.api.dto.PlanningWeekDto;
import com.weeklyplanning.api.dto.ReconcileCommitRequest;
import com.weeklyplanning.api.dto.ReconciliationDto;
import com.weeklyplanning.application.CarryForwardService;
import com.weeklyplanning.application.ReconciliationService;
import com.weeklyplanning.infrastructure.config.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weeks/{weekId}")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;
    private final CarryForwardService carryForwardService;

    public ReconciliationController(ReconciliationService reconciliationService,
                                     CarryForwardService carryForwardService) {
        this.reconciliationService = reconciliationService;
        this.carryForwardService = carryForwardService;
    }

    @PostMapping("/start-reconciliation")
    public PlanningWeekDto startReconciliation(@PathVariable Long weekId) {
        return reconciliationService.startReconciliation(weekId, UserContext.get().getId());
    }

    @PutMapping("/commits/{commitId}/reconcile")
    public ReconciliationDto reconcileCommit(@PathVariable Long weekId,
                                              @PathVariable Long commitId,
                                              @Valid @RequestBody ReconcileCommitRequest request) {
        return reconciliationService.reconcileCommit(weekId, commitId, request);
    }

    @PutMapping("/summary")
    public PlanningWeekDto updateWeekSummary(@PathVariable Long weekId,
                                              @RequestBody Map<String, String> body) {
        return reconciliationService.updateWeekSummary(
                weekId,
                body.get("blockersSummary"),
                body.get("managerNotes")
        );
    }

    @PostMapping("/reconcile")
    public PlanningWeekDto reconcileWeek(@PathVariable Long weekId) {
        return reconciliationService.reconcileWeek(weekId, UserContext.get().getId());
    }

    @PostMapping("/carry-forward")
    public PlanningWeekDto carryForward(@PathVariable Long weekId) {
        return carryForwardService.carryForward(weekId, UserContext.get().getId());
    }
}

package com.weeklyplanning.api;

import com.weeklyplanning.api.dto.*;
import com.weeklyplanning.application.StrategyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @GetMapping("/rally-cries")
    public List<RallyCryDto> getAllRallyCries() {
        return strategyService.getAllRallyCries();
    }

    @GetMapping("/rally-cries/{id}/defining-objectives")
    public List<DefiningObjectiveDto> getDefiningObjectives(@PathVariable Long id) {
        return strategyService.getDefiningObjectives(id);
    }

    @GetMapping("/defining-objectives/{id}/outcomes")
    public List<OutcomeDto> getOutcomes(@PathVariable Long id) {
        return strategyService.getOutcomes(id);
    }

    @GetMapping("/tree")
    public List<StrategyTreeDto> getStrategyTree() {
        return strategyService.getStrategyTree();
    }
}

package com.weeklyplanning.api.dto;

import java.util.List;

public record StrategyTreeDto(
        Long id, String title, String description,
        List<DefiningObjectiveTreeNode> definingObjectives
) {
    public record DefiningObjectiveTreeNode(
            Long id, String title, String description,
            List<OutcomeDto> outcomes
    ) {}
}

package com.weeklyplanning.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCommitRequest(
        @NotBlank String title,
        String description,
        @NotNull Long rallyCryId,
        @NotNull Long definingObjectiveId,
        @NotNull Long outcomeId,
        @NotBlank String chessCategoryCode,
        @NotNull Integer priorityRank,
        boolean stretch
) {}

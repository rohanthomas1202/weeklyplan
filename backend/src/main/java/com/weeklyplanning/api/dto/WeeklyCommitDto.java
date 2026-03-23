package com.weeklyplanning.api.dto;

public record WeeklyCommitDto(
        Long id,
        String title,
        String description,
        Long rallyCryId,
        String rallyCryTitle,
        Long definingObjectiveId,
        String definingObjectiveTitle,
        Long outcomeId,
        String outcomeTitle,
        String chessCategoryCode,
        String chessCategoryDisplayName,
        int priorityRank,
        boolean stretch,
        Long sourceCommitId,
        ReconciliationDto reconciliation
) {}

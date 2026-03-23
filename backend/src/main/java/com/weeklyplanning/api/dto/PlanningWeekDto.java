package com.weeklyplanning.api.dto;

import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PlanningWeekDto(
        Long id,
        Long userId,
        Long teamId,
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        PlanningWeekStatus status,
        Instant lockedAt,
        Instant reconcilingAt,
        Instant reconciledAt,
        String blockersSummary,
        String managerNotes,
        List<WeeklyCommitDto> commits
) {}

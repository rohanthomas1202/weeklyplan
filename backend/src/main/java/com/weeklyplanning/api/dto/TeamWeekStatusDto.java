package com.weeklyplanning.api.dto;

import java.time.LocalDate;

public record TeamWeekStatusDto(
        Long userId,
        String userName,
        String userRole,
        Long weekId,
        String status,
        int commitCount,
        LocalDate weekStartDate
) {}

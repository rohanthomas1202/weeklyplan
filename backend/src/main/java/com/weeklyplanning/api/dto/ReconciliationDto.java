package com.weeklyplanning.api.dto;

import com.weeklyplanning.domain.enums.CommitDisposition;
import java.math.BigDecimal;
import java.time.Instant;

public record ReconciliationDto(
        Long id,
        Long weeklyCommitId,
        CommitDisposition disposition,
        String actualResult,
        BigDecimal percentComplete,
        String blockerNotes,
        boolean carryForward,
        String reconciliationNotes,
        Instant reconciledAt
) {}

package com.weeklyplanning.api.dto;

import com.weeklyplanning.domain.enums.CommitDisposition;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ReconcileCommitRequest(
        @NotNull CommitDisposition disposition,
        String actualResult,
        BigDecimal percentComplete,
        String blockerNotes,
        boolean carryForward,
        String reconciliationNotes
) {}

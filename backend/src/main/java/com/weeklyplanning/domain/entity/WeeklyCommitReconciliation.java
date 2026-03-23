package com.weeklyplanning.domain.entity;

import com.weeklyplanning.domain.enums.CommitDisposition;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "weekly_commit_reconciliation")
public class WeeklyCommitReconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weekly_commit_id")
    private Long weeklyCommitId;

    @Enumerated(EnumType.STRING)
    private CommitDisposition disposition;

    @Column(name = "actual_result")
    private String actualResult;

    @Column(name = "percent_complete")
    private BigDecimal percentComplete;

    @Column(name = "blocker_notes")
    private String blockerNotes;

    @Column(name = "carry_forward")
    private boolean carryForward;

    @Column(name = "reconciliation_notes")
    private String reconciliationNotes;

    @Column(name = "reconciled_at")
    private Instant reconciledAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWeeklyCommitId() { return weeklyCommitId; }
    public void setWeeklyCommitId(Long weeklyCommitId) { this.weeklyCommitId = weeklyCommitId; }

    public CommitDisposition getDisposition() { return disposition; }
    public void setDisposition(CommitDisposition disposition) { this.disposition = disposition; }

    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }

    public BigDecimal getPercentComplete() { return percentComplete; }
    public void setPercentComplete(BigDecimal percentComplete) { this.percentComplete = percentComplete; }

    public String getBlockerNotes() { return blockerNotes; }
    public void setBlockerNotes(String blockerNotes) { this.blockerNotes = blockerNotes; }

    public boolean isCarryForward() { return carryForward; }
    public void setCarryForward(boolean carryForward) { this.carryForward = carryForward; }

    public String getReconciliationNotes() { return reconciliationNotes; }
    public void setReconciliationNotes(String reconciliationNotes) { this.reconciliationNotes = reconciliationNotes; }

    public Instant getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(Instant reconciledAt) { this.reconciledAt = reconciledAt; }
}

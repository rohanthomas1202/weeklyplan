package com.weeklyplanning.domain.entity;

import com.weeklyplanning.domain.enums.PlanningWeekStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "planning_week")
public class PlanningWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "week_start_date")
    private LocalDate weekStartDate;

    @Column(name = "week_end_date")
    private LocalDate weekEndDate;

    @Enumerated(EnumType.STRING)
    private PlanningWeekStatus status;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "blockers_summary")
    private String blockersSummary;

    @Column(name = "manager_notes")
    private String managerNotes;

    @Column(name = "reconciling_at")
    private Instant reconcilingAt;

    @Column(name = "reconciled_at")
    private Instant reconciledAt;

    @Version
    private int version;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public LocalDate getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }

    public LocalDate getWeekEndDate() { return weekEndDate; }
    public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }

    public PlanningWeekStatus getStatus() { return status; }
    public void setStatus(PlanningWeekStatus status) { this.status = status; }

    public Instant getLockedAt() { return lockedAt; }
    public void setLockedAt(Instant lockedAt) { this.lockedAt = lockedAt; }

    public String getBlockersSummary() { return blockersSummary; }
    public void setBlockersSummary(String blockersSummary) { this.blockersSummary = blockersSummary; }

    public String getManagerNotes() { return managerNotes; }
    public void setManagerNotes(String managerNotes) { this.managerNotes = managerNotes; }

    public Instant getReconcilingAt() { return reconcilingAt; }
    public void setReconcilingAt(Instant reconcilingAt) { this.reconcilingAt = reconcilingAt; }

    public Instant getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(Instant reconciledAt) { this.reconciledAt = reconciledAt; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

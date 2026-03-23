package com.weeklyplanning.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "weekly_commit")
public class WeeklyCommit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "planning_week_id")
    private Long planningWeekId;

    private String title;

    private String description;

    @Column(name = "rally_cry_id")
    private Long rallyCryId;

    @Column(name = "defining_objective_id")
    private Long definingObjectiveId;

    @Column(name = "outcome_id")
    private Long outcomeId;

    @Column(name = "chess_category_code")
    private String chessCategoryCode;

    @Column(name = "priority_rank")
    private Integer priorityRank;

    private boolean stretch;

    @Column(name = "source_commit_id")
    private Long sourceCommitId;

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

    public Long getPlanningWeekId() { return planningWeekId; }
    public void setPlanningWeekId(Long planningWeekId) { this.planningWeekId = planningWeekId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getRallyCryId() { return rallyCryId; }
    public void setRallyCryId(Long rallyCryId) { this.rallyCryId = rallyCryId; }

    public Long getDefiningObjectiveId() { return definingObjectiveId; }
    public void setDefiningObjectiveId(Long definingObjectiveId) { this.definingObjectiveId = definingObjectiveId; }

    public Long getOutcomeId() { return outcomeId; }
    public void setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; }

    public String getChessCategoryCode() { return chessCategoryCode; }
    public void setChessCategoryCode(String chessCategoryCode) { this.chessCategoryCode = chessCategoryCode; }

    public Integer getPriorityRank() { return priorityRank; }
    public void setPriorityRank(Integer priorityRank) { this.priorityRank = priorityRank; }

    public boolean isStretch() { return stretch; }
    public void setStretch(boolean stretch) { this.stretch = stretch; }

    public Long getSourceCommitId() { return sourceCommitId; }
    public void setSourceCommitId(Long sourceCommitId) { this.sourceCommitId = sourceCommitId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

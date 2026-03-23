package com.weeklyplanning.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "outcome")
public class Outcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "defining_objective_id")
    private Long definingObjectiveId;

    private String title;

    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDefiningObjectiveId() { return definingObjectiveId; }
    public void setDefiningObjectiveId(Long definingObjectiveId) { this.definingObjectiveId = definingObjectiveId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

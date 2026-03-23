package com.weeklyplanning.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "defining_objective")
public class DefiningObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rally_cry_id")
    private Long rallyCryId;

    private String title;

    private String description;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRallyCryId() { return rallyCryId; }
    public void setRallyCryId(Long rallyCryId) { this.rallyCryId = rallyCryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

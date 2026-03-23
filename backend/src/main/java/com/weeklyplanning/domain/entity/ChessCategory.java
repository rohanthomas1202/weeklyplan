package com.weeklyplanning.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chess_category")
public class ChessCategory {

    @Id
    private String code;

    @Column(name = "display_name")
    private String displayName;

    private String description;

    @Column(name = "sort_order")
    private int sortOrder;

    private boolean active;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

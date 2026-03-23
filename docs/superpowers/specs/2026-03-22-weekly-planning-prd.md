# Weekly Planning — Product Requirements Document

## Problem

Organizations lack referential integrity between weekly execution and strategy. Existing tools like 15Five allow teams to record weekly plans, but nothing enforces that every commitment is linked to a strategic outcome. The result: strategic drift goes undetected, managers lack alignment visibility, and weekly planning becomes a compliance exercise rather than an execution tool.

## Product Vision

A weekly planning tool where every individual commitment is provably connected to organizational strategy. The system enforces alignment at commit time, provides planned-vs-actual reconciliation at week close, and gives managers roll-up visibility into strategic execution.

## Target Users

| Role | Description | Primary Need |
|------|-------------|--------------|
| Individual Contributor (IC) | Engineers, designers, analysts — anyone doing the work | Plan weekly commits linked to strategy, reconcile at week end |
| Manager | Team leads, engineering managers | See team alignment, spot drift, review reconciliations |

## Strategic Hierarchy

The product enforces a four-level strategic hierarchy:

1. **Rally Cry** — top-level strategic theme (e.g., "Platform Reliability")
2. **Defining Objective** — a major objective under a Rally Cry (e.g., "Eliminate single points of failure")
3. **Outcome** — a measurable result under a Defining Objective (e.g., "Auth uptime 99.9%")
4. **Weekly Commit** — an individual's weekly commitment linked to an Outcome

Every weekly commit must link to an Outcome. The system validates that the Outcome → Defining Objective → Rally Cry chain is intact.

## Chess Layer

Every weekly commit is categorized using a chess-piece metaphor that expresses the strategic/operational nature of the work:

| Category | Meaning |
|----------|---------|
| KING | Mission-critical, organization-defining work |
| QUEEN | High leverage, cross-functional / broad impact |
| ROOK | Structural / delivery / operationally critical |
| BISHOP | Specialized or enabling work |
| KNIGHT | Ambiguous, exploratory, problem-solving work |
| PAWN | Routine, maintenance, follow-through work |

Categories are stored as configurable reference data, not hardcoded enums.

## Core User Journeys

### Journey 1: IC Weekly Planning

1. IC opens "My Week" — system creates a DRAFT week if none exists for the current week
2. IC adds weekly commits, each requiring:
   - Title and optional description
   - Rally Cry → Defining Objective → Outcome (cascading selector)
   - Chess category
   - Priority rank (drag-to-reorder)
   - Optional stretch flag
3. IC reviews their plan and clicks "Lock Week"
4. System validates all commits have valid RCDO links and transitions to LOCKED
5. Locked plan is read-only for planning fields

### Journey 2: Reconciliation

1. After the week's work is done, IC opens the reconciliation view
2. System transitions the week to RECONCILING
3. For each commit, IC enters:
   - Disposition: Completed / Partially Completed / Not Completed
   - Percent complete
   - Actual result notes
   - Blocker notes (optional)
   - Carry-forward checkbox
4. IC fills in week-level summary fields:
   - **Blockers summary**: what blocked execution this week?
   - **Manager notes**: what should your manager know?
5. IC submits — system validates every commit has a disposition, transitions to RECONCILED
6. IC can trigger "Carry Forward" to clone incomplete items into next week's draft

### Journey 3: Manager Review

1. Manager opens Team Dashboard
2. Sees team members listed with their week status (Draft / Locked / Reconciling / Reconciled)
3. Clicks a team member to view their week (read-only)
4. Sees planned commits and (if reconciled) the planned-vs-actual comparison

## Week Lifecycle

```
DRAFT → LOCKED → RECONCILING → RECONCILED
```

| State | What the IC can do | What changes |
|-------|-------------------|--------------|
| DRAFT | Create, edit, delete, reorder commits | All planning fields editable |
| LOCKED | Nothing — plan is frozen | Read-only |
| RECONCILING | Enter reconciliation data per commit | Only reconciliation fields editable |
| RECONCILED | Trigger carry-forward | Final, immutable |

Transitions are one-way. No going back from LOCKED to DRAFT.

## Commit Dispositions (at reconciliation)

| Disposition | Meaning |
|-------------|---------|
| COMPLETED | Done as planned |
| PARTIALLY_COMPLETED | Some progress, not fully done |
| NOT_COMPLETED | No meaningful progress |
| CARRIED_FORWARD | Explicitly moved to next week (automatically flags for carry-forward) |
| DROPPED | Intentionally abandoned |

### Disposition and Carry-Forward Relationship

The carry-forward checkbox and disposition work together:
- `CARRIED_FORWARD` disposition automatically sets carry-forward to true
- `PARTIALLY_COMPLETED` or `NOT_COMPLETED` with carry-forward checked is valid (assessed status + intent to continue)
- `COMPLETED` or `DROPPED` with carry-forward checked is invalid (contradictory)

## Carry-Forward Mechanics

- Carry-forward is a per-commit decision, not a week-level action
- Carried items are cloned into the next week's DRAFT with `source_commit_id` preserving lineage
- The clone preserves title, description, RCDO link, chess category
- Priority rank is assigned sequentially after existing commits in the target week

## Demo Scope — What's In

- Seeded strategic hierarchy (Rally Cries, DOs, Outcomes) — no CRUD needed
- IC weekly commit CRUD with enforced RCDO linkage
- Chess categorization and priority ranking with drag-to-reorder
- Full week lifecycle (DRAFT → LOCKED → RECONCILING → RECONCILED)
- Planned vs. actual reconciliation view
- Carry-forward cloning
- Manager team status view (read-only)
- Demo auth via user switcher (no real authentication)

## Demo Scope — What's Out

- Module federation / micro-frontend pattern
- Real authentication and authorization
- Strategic hierarchy CRUD
- Admin configuration UI
- Notifications and reminders
- Advanced analytics and roll-up metrics
- Multi-tenancy
- Manager comments on team members' weeks
- Audit logging

## Success Criteria

A successful demo shows:

1. An IC creates weekly commits, each forced to link to a strategic Outcome
2. The chess category and priority make the plan legible at a glance
3. Locking prevents plan changes — the week is committed
4. Reconciliation reveals the gap between plan and reality
5. Carry-forward ensures nothing silently disappears
6. A manager can see their team's alignment in one view
7. Switching between IC and manager personas is instant via the user switcher

## UI Direction

Warm and approachable: rounded corners, soft background colors, friendly feel. Similar to Asana or Monday.com. Accessible to non-technical users. The chess categories use distinct colors for visual differentiation.

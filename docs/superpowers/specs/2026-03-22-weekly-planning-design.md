# Weekly Planning — Technical Design Spec

## Overview

A full-stack weekly planning application that enforces alignment between individual weekly commitments and organizational strategy. Monorepo with a Java 21 Spring Boot backend, React 18 TypeScript frontend, and PostgreSQL database, all orchestrated via Docker Compose.

## Architecture

### High-Level

```
┌─────────────────────────────────────────────┐
│              Docker Compose                 │
│                                             │
│  ┌───────────┐  ┌──────────┐  ┌──────────┐ │
│  │ Frontend  │  │ Backend  │  │ Postgres │ │
│  │ Vite      │→ │ Spring   │→ │   16     │ │
│  │ :5173     │  │ Boot     │  │  :5432   │ │
│  │           │  │ :8080    │  │          │ │
│  └───────────┘  └──────────┘  └──────────┘ │
└─────────────────────────────────────────────┘
```

- Frontend proxies `/api` requests to the backend via Vite config
- Backend connects to Postgres, runs Flyway migrations on startup
- No service mesh, no API gateway — direct communication

### Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language (backend) | Java | 21 |
| Framework (backend) | Spring Boot | 3.x |
| ORM | Spring Data JPA / Hibernate | — |
| Migrations | Flyway | — |
| Build (backend) | Gradle | — |
| Language (frontend) | TypeScript | strict mode |
| Framework (frontend) | React | 18 |
| Build (frontend) | Vite | — |
| Server state | TanStack Query | v5 |
| Routing | React Router | v6 |
| Drag and drop | @dnd-kit/core + @dnd-kit/sortable | — |
| Database | PostgreSQL | 16 |
| Containerization | Docker Compose | — |

## Project Structure

```
weekly-planning/
├── backend/
│   ├── src/main/java/com/weeklyplanning/
│   │   ├── api/                → REST controllers
│   │   │   ├── PlanningWeekController.java
│   │   │   ├── WeeklyCommitController.java
│   │   │   ├── ReconciliationController.java
│   │   │   ├── StrategyController.java
│   │   │   ├── UserController.java
│   │   │   └── ManagerController.java
│   │   ├── application/        → service layer
│   │   │   ├── PlanningWeekService.java
│   │   │   ├── WeeklyCommitService.java
│   │   │   ├── ReconciliationService.java
│   │   │   ├── CarryForwardService.java
│   │   │   ├── StrategyService.java
│   │   │   └── ManagerService.java
│   │   ├── domain/             → entities, enums, exceptions
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   └── exception/
│   │   └── infrastructure/     → repositories, config
│   │       ├── repository/
│   │       └── config/
│   ├── src/main/resources/
│   │   ├── db/migration/       → Flyway SQL files
│   │   └── application.yml
│   ├── build.gradle
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── api/                → API client, types, query hooks
│   │   │   ├── client.ts
│   │   │   ├── types.ts
│   │   │   └── hooks/
│   │   ├── components/         → shared UI (Button, Badge, Card, etc.)
│   │   ├── features/
│   │   │   ├── planning/       → My Week view, commit form
│   │   │   ├── reconciliation/ → reconcile view
│   │   │   └── manager/        → team dashboard
│   │   ├── layout/             → shell, nav bar, user switcher
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── tsconfig.json
│   ├── vite.config.ts
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml
└── docs/
```

## Database Schema

### Strategic hierarchy (seeded, read-only)

```sql
CREATE TABLE rally_cry (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE defining_objective (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    rally_cry_id BIGINT NOT NULL REFERENCES rally_cry(id),
    title VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE outcome (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    defining_objective_id BIGINT NOT NULL REFERENCES defining_objective(id),
    title VARCHAR(255) NOT NULL,
    description TEXT
);
```

### Reference data (seeded)

```sql
CREATE TABLE chess_category (
    code VARCHAR(32) PRIMARY KEY,
    display_name VARCHAR(64) NOT NULL,
    description TEXT NOT NULL,
    sort_order INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE team (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE app_user (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(32) NOT NULL,  -- 'IC' or 'MANAGER'
    team_id BIGINT NOT NULL REFERENCES team(id)
);
```

### Weekly planning (full CRUD)

```sql
CREATE TABLE planning_week (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    team_id BIGINT NOT NULL REFERENCES team(id),  -- denormalized from app_user for manager query performance
    week_start_date DATE NOT NULL,
    week_end_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    locked_at TIMESTAMPTZ,
    blockers_summary TEXT,
    manager_notes TEXT,
    reconciling_at TIMESTAMPTZ,
    reconciled_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, week_start_date)
);

CREATE TABLE weekly_commit (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    planning_week_id BIGINT NOT NULL REFERENCES planning_week(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    rally_cry_id BIGINT NOT NULL REFERENCES rally_cry(id),
    defining_objective_id BIGINT NOT NULL REFERENCES defining_objective(id),
    outcome_id BIGINT NOT NULL REFERENCES outcome(id),
    chess_category_code VARCHAR(32) NOT NULL REFERENCES chess_category(code),
    priority_rank INT NOT NULL,
    stretch BOOLEAN NOT NULL DEFAULT FALSE,
    source_commit_id BIGINT REFERENCES weekly_commit(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (planning_week_id, priority_rank)
);

CREATE TABLE weekly_commit_reconciliation (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    weekly_commit_id BIGINT NOT NULL UNIQUE REFERENCES weekly_commit(id),
    disposition VARCHAR(32) NOT NULL,
    actual_result TEXT,
    percent_complete NUMERIC(5,2),
    blocker_notes TEXT,
    carry_forward BOOLEAN NOT NULL DEFAULT FALSE,
    reconciliation_notes TEXT,
    reconciled_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Seed data

Two Flyway seed migrations populate demo data:

**Strategy seed** — 2-3 Rally Cries, each with 2 Defining Objectives, each with 2-3 Outcomes. Example:
- Rally Cry: "Platform Reliability"
  - DO: "Eliminate single points of failure"
    - Outcome: "Auth uptime 99.9%"
    - Outcome: "Zero unplanned DB failovers"
  - DO: "Improve performance"
    - Outcome: "Dashboard p95 < 500ms"
    - Outcome: "API p99 < 200ms"
- Rally Cry: "Revenue Growth"
  - DO: "Streamline payments"
    - Outcome: "Reduce payment failures 50%"
    - Outcome: "Checkout conversion > 85%"

**Users seed** — 2 teams, 4-5 users:
- Team "Platform": Sarah Chen (IC), James Park (IC), Mike Johnson (Manager)
- Team "Product": Lisa Wang (IC), Alex Rivera (IC), Priya Patel (Manager)

**Chess categories seed** — all six chess pieces with display names and descriptions.

## REST API

### Planning Week

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|-------------|----------|
| GET | `/api/weeks/current` | Get or create current week (see semantics below) | — | PlanningWeekDto |
| GET | `/api/weeks/{weekId}` | Get week with commits | — | PlanningWeekDto |
| POST | `/api/weeks/{weekId}/lock` | DRAFT → LOCKED | — | PlanningWeekDto |

**`GET /api/weeks/current` semantics:** Resolves the user from `X-User-Id` header. Calculates the current calendar week (Monday through Friday). If a `planning_week` exists for this user and week_start_date, returns it regardless of status. If none exists, creates a new DRAFT week and returns it. This means a user always has exactly one week per calendar week.

### Weekly Commits

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|-------------|----------|
| POST | `/api/weeks/{weekId}/commits` | Add commit | CreateCommitRequest | WeeklyCommitDto |
| PUT | `/api/weeks/{weekId}/commits/{id}` | Update commit | UpdateCommitRequest | WeeklyCommitDto |
| DELETE | `/api/weeks/{weekId}/commits/{id}` | Delete commit | — | 204 |
| PUT | `/api/weeks/{weekId}/reorder` | Bulk reorder | ReorderRequest | List<WeeklyCommitDto> |

### Reconciliation

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|-------------|----------|
| POST | `/api/weeks/{weekId}/start-reconciliation` | LOCKED → RECONCILING | — | PlanningWeekDto |
| PUT | `/api/weeks/{weekId}/commits/{id}/reconcile` | Set disposition | ReconcileCommitRequest | ReconciliationDto |
| POST | `/api/weeks/{weekId}/reconcile` | RECONCILING → RECONCILED | — | PlanningWeekDto |
| POST | `/api/weeks/{weekId}/carry-forward` | Clone to next week | — | PlanningWeekDto (next week) |

### Strategy (read-only)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/strategy/rally-cries` | All rally cries |
| GET | `/api/strategy/rally-cries/{id}/defining-objectives` | DOs under a rally cry |
| GET | `/api/strategy/defining-objectives/{id}/outcomes` | Outcomes under a DO |
| GET | `/api/strategy/tree` | Full nested hierarchy (Rally Cries → DOs → Outcomes) |

The `/api/strategy/tree` endpoint returns the full hierarchy in one call, avoiding waterfall requests in the cascading selector. The individual endpoints remain available for targeted lookups.

### Users (demo)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/chess-categories` | All active chess categories |
| GET | `/api/users` | All users |
| GET | `/api/users/me` | Current user (from X-User-Id header) |

### Manager

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/manager/team/weeks` | Team members' current week statuses |
| GET | `/api/manager/users/{userId}/weeks/{weekId}` | View a team member's week |

## Request/Response DTOs

### CreateCommitRequest

```json
{
  "title": "string (required)",
  "description": "string (optional)",
  "rallyCryId": "number (required)",
  "definingObjectiveId": "number (required)",
  "outcomeId": "number (required)",
  "chessCategoryCode": "string (required)",
  "priorityRank": "number (required)",
  "stretch": "boolean (default false)"
}
```

### ReconcileCommitRequest

```json
{
  "disposition": "COMPLETED | PARTIALLY_COMPLETED | NOT_COMPLETED | CARRIED_FORWARD | DROPPED",
  "actualResult": "string (optional)",
  "percentComplete": "number 0-100 (optional)",
  "blockerNotes": "string (optional)",
  "carryForward": "boolean (default false)",
  "reconciliationNotes": "string (optional)"
}
```

### ReorderRequest

```json
{
  "commitIds": [3, 1, 2]
}
```

An ordered array of commit IDs. The backend assigns priority_rank 1, 2, 3, ... based on position.

### PlanningWeekDto

```json
{
  "id": "number",
  "userId": "number",
  "teamId": "number",
  "weekStartDate": "2026-03-16",
  "weekEndDate": "2026-03-20",
  "status": "DRAFT | LOCKED | RECONCILING | RECONCILED",
  "lockedAt": "timestamp | null",
  "reconcilingAt": "timestamp | null",
  "reconciledAt": "timestamp | null",
  "blockersSummary": "string | null",
  "managerNotes": "string | null",
  "commits": [WeeklyCommitDto]
}
```

### WeeklyCommitDto

```json
{
  "id": "number",
  "title": "string",
  "description": "string | null",
  "rallyCryId": "number",
  "rallyCryTitle": "string",
  "definingObjectiveId": "number",
  "definingObjectiveTitle": "string",
  "outcomeId": "number",
  "outcomeTitle": "string",
  "chessCategoryCode": "string",
  "chessCategoryDisplayName": "string",
  "priorityRank": "number",
  "stretch": "boolean",
  "sourceCommitId": "number | null",
  "reconciliation": "ReconciliationDto | null"
}
```

### ReconciliationDto

```json
{
  "id": "number",
  "weeklyCommitId": "number",
  "disposition": "COMPLETED | PARTIALLY_COMPLETED | NOT_COMPLETED | CARRIED_FORWARD | DROPPED",
  "actualResult": "string | null",
  "percentComplete": "number | null",
  "blockerNotes": "string | null",
  "carryForward": "boolean",
  "reconciliationNotes": "string | null",
  "reconciledAt": "timestamp"
}
```

## Demo Auth

No real authentication. The frontend includes a user switcher dropdown in the top nav. When a user is selected, all API requests include the header:

```
X-User-Id: {userId}
```

The backend reads this header via a `UserContextFilter` that runs before all controllers. The filter resolves the user from the database and makes it available via a `UserContext` thread-local or request-scoped bean.

If the header is missing, the backend returns 401. If the user doesn't exist, 404.

Manager endpoints check that the resolved user has role `MANAGER` and return 403 if not.

## Service Layer Rules

### State machine

`PlanningWeekService` owns all status transitions and enforces:

- **DRAFT → LOCKED**: All commits must have valid RCDO chain. At least one commit required. Priority ranks must be contiguous starting from 1.
- **LOCKED → RECONCILING**: No preconditions beyond current status.
- **RECONCILING → RECONCILED**: Every commit must have a reconciliation record with a disposition set.
- No reverse transitions. No skipping states.

### RCDO validation

`WeeklyCommitService` validates on every create/update:

1. Outcome exists
2. Outcome belongs to the specified Defining Objective
3. Defining Objective belongs to the specified Rally Cry

If any check fails, return 400 with a descriptive error.

### Locked week immutability

When a week is LOCKED or beyond, the backend rejects:
- POST/PUT/DELETE on commits (planning fields)
- Reorder requests

Only reconciliation endpoints accept writes, and only when status is RECONCILING.

### Carry-forward and disposition relationship

The `carryForward` boolean and `disposition` field work together:
- Setting disposition to `CARRIED_FORWARD` automatically sets `carryForward = true`
- `carryForward = true` is also valid with `PARTIALLY_COMPLETED` or `NOT_COMPLETED` dispositions (the IC assessed partial progress but wants to continue next week)
- `carryForward = true` with `COMPLETED` or `DROPPED` is rejected (contradictory)
- The `POST /api/weeks/{weekId}/carry-forward` endpoint is a batch operation that clones all commits where `carryForward = true`, regardless of their disposition

### Carry-forward cloning

`CarryForwardService` handles cloning:

1. Week must be RECONCILED
2. Find all commits where `reconciliation.carryForward = true`
3. Get or create next week's DRAFT planning_week
4. Clone each commit: copy title, description, RCDO links, chess category. Set `source_commit_id` to the original. Assign priority ranks starting after existing commits in the target week.
5. Return the next week's PlanningWeekDto

## Frontend Architecture

### TypeScript types

All API types defined in `src/api/types.ts` using discriminated unions for status:

```typescript
type PlanningWeekStatus = 'DRAFT' | 'LOCKED' | 'RECONCILING' | 'RECONCILED';
type CommitDisposition = 'COMPLETED' | 'PARTIALLY_COMPLETED' | 'NOT_COMPLETED' | 'CARRIED_FORWARD' | 'DROPPED';
type UserRole = 'IC' | 'MANAGER';
```

### API client

`src/api/client.ts` — a thin wrapper around fetch that:
- Prepends `/api` to all paths
- Adds `X-User-Id` header from the current user context
- Parses JSON responses
- Throws typed errors

### TanStack Query hooks

`src/api/hooks/` — one file per domain:
- `useCurrentWeek()`, `useWeek(weekId)`
- `useCreateCommit()`, `useUpdateCommit()`, `useDeleteCommit()`, `useReorderCommits()`
- `useLockWeek()`, `useStartReconciliation()`, `useReconcileCommit()`, `useReconcileWeek()`, `useCarryForward()`
- `useStrategyTree()` (full hierarchy for cascading selector)
- `useChessCategories()`
- `useUsers()`, `useCurrentUser()`
- `useTeamWeeks()`, `useTeamMemberWeek(userId, weekId)`

### Routing

```typescript
/                              → redirect to /my-week
/my-week                       → PlanningPage (handles DRAFT and LOCKED views)
/my-week/reconcile             → ReconciliationPage
/manager/team                  → ManagerDashboardPage
/manager/team/:userId/:weekId  → ManagerWeekDetailPage
```

### Key components

**Layout:**
- `AppShell` — top nav bar, main content area
- `NavBar` — logo, route links (My Week / Team), user switcher
- `UserSwitcher` — dropdown listing all users with role badges

**Planning:**
- `CommitList` — renders commit cards with drag-to-reorder
- `CommitCard` — displays priority, chess badge, title, RCDO breadcrumb, actions menu
- `CommitForm` — modal/drawer for creating or editing a commit
- `StrategyCascader` — cascading dropdowns: Rally Cry → DO → Outcome
- `ChessCategoryPicker` — visual selector for the six categories
- `WeekHeader` — week dates, status badge, action buttons (Lock / Start Reconciliation)
- `WeekSummaryBar` — commit count, chess category breakdown, rally cry coverage

**Reconciliation:**
- `ReconcileList` — planned vs. actual comparison for each commit
- `ReconcileCard` — left: planned commit details, right: disposition form
- `ReconcileForm` — disposition dropdown, percent complete, notes fields
- `CarryForwardButton` — triggers carry-forward after reconciliation

**Manager:**
- `TeamStatusGrid` — rows of team members with week status badges
- `MemberWeekView` — read-only version of PlanningPage / ReconciliationPage

### State management

Server state via TanStack Query (no client-side store needed for this scope). Local UI state (form inputs, drag state, modal open/close) lives in React component state.

User context (selected user for demo auth) stored in React context at the app root, persisted to localStorage so it survives page reloads.

## UI Design Direction

Warm and approachable aesthetic:

- **Colors**: Soft backgrounds (#fafafa, #eff6ff, #fefce8), blue primary (#2563eb), white cards
- **Shape**: Rounded corners (border-radius: 12px on cards, 8px on badges)
- **Typography**: System font stack, 14px base, semibold for titles
- **Cards**: White with subtle 1px border (#e5e7eb), no harsh shadows
- **Badges**: Pill-shaped with soft background colors per chess category
- **Chess category colors**: King = blue, Queen = red, Rook = amber, Bishop = green, Knight = purple, Pawn = gray
- **Status badges**: Draft = amber, Locked = blue, Reconciling = orange, Reconciled = green
- **Spacing**: Generous padding, 10px gaps between cards

## Docker Compose

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: weeklyplanning
      POSTGRES_USER: wp_user
      POSTGRES_PASSWORD: wp_pass
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/weeklyplanning
      SPRING_DATASOURCE_USERNAME: wp_user
      SPRING_DATASOURCE_PASSWORD: wp_pass
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "5173:5173"
    depends_on:
      - backend

volumes:
  pgdata:
```

## Error Handling

### Backend

All validation and business rule errors return structured JSON:

```json
{
  "error": "INVALID_STATUS_TRANSITION",
  "message": "Cannot lock week: status is LOCKED, expected DRAFT"
}
```

HTTP status codes:
- 400 — validation errors, invalid RCDO chain, invalid state transition
- 401 — missing X-User-Id header
- 403 — non-manager accessing manager endpoints
- 404 — entity not found

### Frontend

TanStack Query `onError` callbacks display toast notifications for API errors. Form validation runs client-side before submission (required fields, valid selections) but the backend is the source of truth.

## Testing Strategy

### Backend

- **Service layer unit tests**: lifecycle transitions, RCDO validation, carry-forward cloning logic
- **Repository integration tests**: constraint enforcement (duplicate priority, unique week per user)
- **Controller integration tests**: API contracts, error responses, auth header handling

### Frontend

- **Component tests**: CommitCard renders correctly, CommitForm validates required fields
- **Hook tests**: TanStack Query hooks with MSW (Mock Service Worker) for API mocking

### End-to-end critical paths

- Create week → add commits → lock → reconcile → carry forward
- Invalid RCDO chain rejected at API level
- Locked week rejects planning mutations
- Manager can view team weeks but not edit

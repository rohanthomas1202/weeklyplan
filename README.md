# WeeklyPlan

A weekly planning tool that enforces alignment between individual commitments and organizational strategy. Every weekly commit must link to a strategic outcome through the Rally Cry > Defining Objective > Outcome hierarchy.

## Why

Existing tools like 15Five let teams record weekly plans, but nothing prevents drift between execution and strategy. WeeklyPlan makes it impossible to submit a weekly plan unless every commitment is explicitly linked into the strategic hierarchy. The result: enforced alignment at commit time, planned-vs-actual reconciliation at week close, and manager visibility into strategic execution.

## Features

- **Strategic linkage** — every weekly commit links to Rally Cry > Defining Objective > Outcome
- **Chess layer** — categorize work by strategic weight (King, Queen, Rook, Bishop, Knight, Pawn)
- **Week lifecycle** — Draft > Locked > Reconciling > Reconciled
- **Reconciliation** — planned vs. actual comparison with per-commit dispositions
- **Carry-forward** — incomplete work automatically seeds next week's plan with lineage tracking
- **Manager dashboard** — team status, alignment visibility, drill-down into individual weeks
- **Dark mode** — system preference detection with manual toggle

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.x, Spring Data JPA, Flyway |
| Frontend | React 18, TypeScript (strict), Vite, TanStack Query v5, React Router v6 |
| Database | PostgreSQL 16 |
| Infrastructure | Docker Compose |

## Quick Start

### Prerequisites

- Docker Desktop (for PostgreSQL)
- Java 21
- Node.js 20+

### 1. Start PostgreSQL

```bash
docker compose up postgres -d
```

### 2. Start the backend

```bash
cd backend
./gradlew bootRun
```

The backend starts on http://localhost:8080. Flyway automatically runs all migrations and seeds demo data on first startup.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on http://localhost:5173 and proxies API requests to the backend.

### 4. Open the app

Go to http://localhost:5173. Select a user from the dropdown in the top-right corner to get started.

## Demo Users

| Name | Role | Team |
|------|------|------|
| Sarah Chen | IC | Platform |
| James Park | IC | Platform |
| Mike Johnson | Manager | Platform |
| Lisa Wang | IC | Product |
| Alex Rivera | IC | Product |
| Priya Patel | Manager | Product |

The database is pre-seeded with two weeks of realistic planning history. Try Mike Johnson (Manager) to see the team dashboard with data.

## Demo Auth

There is no login screen. The app uses a user switcher dropdown in the nav bar. Selecting a user sets an `X-User-Id` header on all API requests. This is for demo purposes only.

## Project Structure

```
weeklyplan/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/weeklyplanning/
│   │   ├── api/                # REST controllers + DTOs
│   │   ├── application/        # Service layer
│   │   ├── domain/             # Entities, enums, exceptions
│   │   └── infrastructure/     # Repositories, config, auth filter
│   └── src/main/resources/
│       └── db/migration/       # Flyway SQL migrations + seed data
├── frontend/                   # React SPA
│   └── src/
│       ├── api/                # API client, types, TanStack Query hooks
│       ├── components/         # Shared UI (Button, Badge, Card, Modal, Toast)
│       ├── context/            # User and theme contexts
│       ├── features/           # Feature modules
│       │   ├── planning/       # Weekly commit planning
│       │   ├── reconciliation/ # Week-end reconciliation
│       │   └── manager/        # Team dashboard
│       └── layout/             # App shell, nav bar, user switcher
├── docker-compose.yml
└── docs/                       # Specs and plans
```

## API Endpoints

### Planning
- `GET /api/weeks/current` — get or create current week
- `POST /api/weeks/{id}/commits` — add a commit
- `PUT /api/weeks/{id}/commits/{commitId}` — update a commit
- `DELETE /api/weeks/{id}/commits/{commitId}` — delete a commit
- `PUT /api/weeks/{id}/reorder` — reorder commit priorities
- `POST /api/weeks/{id}/lock` — lock the week plan

### Reconciliation
- `POST /api/weeks/{id}/start-reconciliation` — begin reconciliation
- `PUT /api/weeks/{id}/commits/{commitId}/reconcile` — set commit disposition
- `POST /api/weeks/{id}/reconcile` — finalize reconciliation
- `POST /api/weeks/{id}/carry-forward` — carry incomplete work to next week

### Strategy & Reference
- `GET /api/strategy/tree` — full Rally Cry > DO > Outcome hierarchy
- `GET /api/chess-categories` — chess layer categories
- `GET /api/users` — all users

### Manager
- `GET /api/manager/team/weeks` — team members' current week statuses
- `GET /api/manager/users/{userId}/weeks/{weekId}` — view a team member's week

## Running Tests

```bash
cd backend
./gradlew test
```

Tests use H2 in-memory database with the same Flyway migrations.

## License

MIT

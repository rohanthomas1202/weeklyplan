# Weekly Planning Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a full-stack weekly planning app where every IC commitment links to organizational strategy, with lifecycle management, reconciliation, carry-forward, and manager visibility.

**Architecture:** Monorepo with Spring Boot 3.x (Java 21) backend and React 18 (TypeScript strict) frontend. PostgreSQL 16 via Docker Compose. Flyway for migrations. TanStack Query for server state. Demo auth via X-User-Id header.

**Tech Stack:** Java 21, Spring Boot 3.x, Spring Data JPA, Flyway, Gradle, React 18, TypeScript (strict), Vite, TanStack Query v5, React Router v6, @dnd-kit, PostgreSQL 16, Docker Compose

**Spec:** `docs/superpowers/specs/2026-03-22-weekly-planning-design.md`

---

## File Structure

### Backend

```
backend/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── src/main/java/com/weeklyplanning/
│   ├── WeeklyPlanningApplication.java
│   ├── api/
│   │   ├── PlanningWeekController.java
│   │   ├── WeeklyCommitController.java
│   │   ├── ReconciliationController.java
│   │   ├── StrategyController.java
│   │   ├── UserController.java
│   │   ├── ManagerController.java
│   │   └── dto/
│   │       ├── CreateCommitRequest.java
│   │       ├── UpdateCommitRequest.java
│   │       ├── ReconcileCommitRequest.java
│   │       ├── ReorderRequest.java
│   │       ├── PlanningWeekDto.java
│   │       ├── WeeklyCommitDto.java
│   │       ├── ReconciliationDto.java
│   │       ├── RallyCryDto.java
│   │       ├── DefiningObjectiveDto.java
│   │       ├── OutcomeDto.java
│   │       ├── StrategyTreeDto.java
│   │       ├── ChessCategoryDto.java
│   │       ├── UserDto.java
│   │       ├── TeamWeekStatusDto.java
│   │       └── ErrorResponse.java
│   ├── application/
│   │   ├── PlanningWeekService.java
│   │   ├── WeeklyCommitService.java
│   │   ├── ReconciliationService.java
│   │   ├── CarryForwardService.java
│   │   ├── StrategyService.java
│   │   └── ManagerService.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── RallyCry.java
│   │   │   ├── DefiningObjective.java
│   │   │   ├── Outcome.java
│   │   │   ├── ChessCategory.java
│   │   │   ├── Team.java
│   │   │   ├── AppUser.java
│   │   │   ├── PlanningWeek.java
│   │   │   ├── WeeklyCommit.java
│   │   │   └── WeeklyCommitReconciliation.java
│   │   ├── enums/
│   │   │   ├── PlanningWeekStatus.java
│   │   │   ├── CommitDisposition.java
│   │   │   └── UserRole.java
│   │   └── exception/
│   │       ├── InvalidStatusTransitionException.java
│   │       ├── InvalidHierarchyException.java
│   │       ├── WeekLockedException.java
│   │       └── GlobalExceptionHandler.java
│   └── infrastructure/
│       ├── repository/
│       │   ├── RallyCryRepository.java
│       │   ├── DefiningObjectiveRepository.java
│       │   ├── OutcomeRepository.java
│       │   ├── ChessCategoryRepository.java
│       │   ├── TeamRepository.java
│       │   ├── AppUserRepository.java
│       │   ├── PlanningWeekRepository.java
│       │   ├── WeeklyCommitRepository.java
│       │   └── WeeklyCommitReconciliationRepository.java
│       └── config/
│           ├── UserContextFilter.java
│           ├── UserContext.java
│           └── WebConfig.java
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       ├── V1__create_strategy_tables.sql
│       ├── V2__create_reference_tables.sql
│       ├── V3__create_planning_tables.sql
│       ├── V100__seed_strategy.sql
│       ├── V101__seed_reference.sql
│       └── V102__seed_users.sql
└── src/test/java/com/weeklyplanning/
    ├── application/
    │   ├── PlanningWeekServiceTest.java
    │   ├── WeeklyCommitServiceTest.java
    │   ├── ReconciliationServiceTest.java
    │   └── CarryForwardServiceTest.java
    └── api/
        ├── PlanningWeekControllerTest.java
        ├── WeeklyCommitControllerTest.java
        ├── ReconciliationControllerTest.java
        └── StrategyControllerTest.java
```

### Frontend

```
frontend/
├── package.json
├── tsconfig.json
├── vite.config.ts
├── index.html
├── Dockerfile
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── App.css
    ├── api/
    │   ├── client.ts
    │   ├── types.ts
    │   └── hooks/
    │       ├── useWeeks.ts
    │       ├── useCommits.ts
    │       ├── useReconciliation.ts
    │       ├── useStrategy.ts
    │       ├── useUsers.ts
    │       └── useManager.ts
    ├── components/
    │   ├── Button.tsx
    │   ├── Button.css
    │   ├── Badge.tsx
    │   ├── Badge.css
    │   ├── Card.tsx
    │   ├── Card.css
    │   ├── Modal.tsx
    │   ├── Modal.css
    │   ├── Toast.tsx
    │   └── Toast.css
    ├── context/
    │   └── UserContext.tsx
    ├── layout/
    │   ├── AppShell.tsx
    │   ├── AppShell.css
    │   ├── NavBar.tsx
    │   ├── NavBar.css
    │   ├── UserSwitcher.tsx
    │   └── UserSwitcher.css
    └── features/
        ├── planning/
        │   ├── PlanningPage.tsx
        │   ├── PlanningPage.css
        │   ├── CommitList.tsx
        │   ├── CommitList.css
        │   ├── CommitCard.tsx
        │   ├── CommitCard.css
        │   ├── CommitForm.tsx
        │   ├── CommitForm.css
        │   ├── StrategyCascader.tsx
        │   ├── StrategyCascader.css
        │   ├── ChessCategoryPicker.tsx
        │   ├── ChessCategoryPicker.css
        │   ├── WeekHeader.tsx
        │   ├── WeekHeader.css
        │   ├── WeekSummaryBar.tsx
        │   └── WeekSummaryBar.css
        ├── reconciliation/
        │   ├── ReconciliationPage.tsx
        │   ├── ReconciliationPage.css
        │   ├── ReconcileList.tsx
        │   ├── ReconcileList.css
        │   ├── ReconcileCard.tsx
        │   ├── ReconcileCard.css
        │   ├── ReconcileForm.tsx
        │   └── ReconcileForm.css
        └── manager/
            ├── ManagerDashboardPage.tsx
            ├── ManagerDashboardPage.css
            ├── TeamStatusGrid.tsx
            ├── TeamStatusGrid.css
            ├── MemberWeekView.tsx
            └── MemberWeekView.css
```

---

## Task 1: Project Scaffolding & Infrastructure

**Files:**
- Create: `docker-compose.yml`
- Create: `backend/build.gradle`
- Create: `backend/settings.gradle`
- Create: `backend/Dockerfile`
- Create: `backend/src/main/java/com/weeklyplanning/WeeklyPlanningApplication.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `frontend/package.json`
- Create: `frontend/tsconfig.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/index.html`
- Create: `frontend/Dockerfile`
- Create: `frontend/src/main.tsx`
- Create: `frontend/src/App.tsx`
- Create: `frontend/src/App.css`
- Create: `.gitignore`

- [ ] **Step 1: Initialize git repo**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git init
```

- [ ] **Step 2: Create root .gitignore**

Create `.gitignore`:

```gitignore
# Java
backend/build/
backend/.gradle/
*.class
*.jar

# Node
frontend/node_modules/
frontend/dist/

# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store

# Docker
pgdata/

# Superpowers
.superpowers/
```

- [ ] **Step 3: Create docker-compose.yml**

Create `docker-compose.yml` per the spec — three services: postgres (port 5432), backend (port 8080), frontend (port 5173). Include pgdata volume. Use postgres:16 image with `weeklyplanning` database, `wp_user` / `wp_pass` credentials.

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
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U wp_user -d weeklyplanning"]
      interval: 5s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/weeklyplanning
      SPRING_DATASOURCE_USERNAME: wp_user
      SPRING_DATASOURCE_PASSWORD: wp_pass
    depends_on:
      postgres:
        condition: service_healthy

  frontend:
    build: ./frontend
    ports:
      - "5173:5173"
    depends_on:
      - backend

volumes:
  pgdata:
```

- [ ] **Step 4: Create backend Gradle project**

Create `backend/settings.gradle`:

```groovy
rootProject.name = 'weekly-planning-backend'
```

Create `backend/build.gradle`:

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.4.1'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.weeklyplanning'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-postgresql'
    runtimeOnly 'org.postgresql:postgresql'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'com.h2database:h2'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

- [ ] **Step 5: Create Spring Boot application class**

Create `backend/src/main/java/com/weeklyplanning/WeeklyPlanningApplication.java`:

```java
package com.weeklyplanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeeklyPlanningApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeeklyPlanningApplication.class, args);
    }
}
```

- [ ] **Step 6: Create application.yml**

Create `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/weeklyplanning
    username: wp_user
    password: wp_pass
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080
```

- [ ] **Step 7: Create backend Dockerfile**

Create `backend/Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 8: Add Gradle wrapper**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
gradle wrapper --gradle-version 8.12
```

- [ ] **Step 9: Create frontend project**

Create `frontend/package.json`:

```json
{
  "name": "weekly-planning-frontend",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-router-dom": "^6.28.0",
    "@tanstack/react-query": "^5.62.0",
    "@dnd-kit/core": "^6.3.1",
    "@dnd-kit/sortable": "^10.0.0",
    "@dnd-kit/utilities": "^3.2.2"
  },
  "devDependencies": {
    "@types/react": "^18.3.12",
    "@types/react-dom": "^18.3.1",
    "@vitejs/plugin-react": "^4.3.4",
    "typescript": "~5.6.2",
    "vite": "^6.0.1"
  }
}
```

Create `frontend/tsconfig.json`:

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "isolatedModules": true,
    "moduleDetection": "force",
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "noUncheckedIndexedAccess": true,
    "forceConsistentCasingInFileNames": true
  },
  "include": ["src"]
}
```

Create `frontend/vite.config.ts`:

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

Create `frontend/index.html`:

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Weekly Planning</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

Create `frontend/Dockerfile`:

```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install
COPY . .
EXPOSE 5173
CMD ["npm", "run", "dev"]
```

- [ ] **Step 10: Create minimal React app**

Create `frontend/src/main.tsx`:

```tsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { App } from './App';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
```

Create `frontend/src/App.tsx`:

```tsx
import './App.css';

export function App() {
  return (
    <div className="app">
      <h1>Weekly Planning</h1>
      <p>App is running.</p>
    </div>
  );
}
```

Create `frontend/src/App.css`:

```css
*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 14px;
  color: #111827;
  background: #fafafa;
  -webkit-font-smoothing: antialiased;
}

.app {
  min-height: 100vh;
}
```

- [ ] **Step 11: Install frontend dependencies**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npm install
```

- [ ] **Step 12: Verify backend compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 13: Verify frontend builds**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 14: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add .
git commit -m "chore: scaffold monorepo with Spring Boot backend, React frontend, and Docker Compose"
```

---

## Task 2: Database Schema & Seed Data

**Files:**
- Create: `backend/src/main/resources/db/migration/V1__create_strategy_tables.sql`
- Create: `backend/src/main/resources/db/migration/V2__create_reference_tables.sql`
- Create: `backend/src/main/resources/db/migration/V3__create_planning_tables.sql`
- Create: `backend/src/main/resources/db/migration/V100__seed_strategy.sql`
- Create: `backend/src/main/resources/db/migration/V101__seed_reference.sql`
- Create: `backend/src/main/resources/db/migration/V102__seed_users.sql`

- [ ] **Step 1: Create V1 — strategy tables**

Create `backend/src/main/resources/db/migration/V1__create_strategy_tables.sql`:

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

- [ ] **Step 2: Create V2 — reference tables**

Create `backend/src/main/resources/db/migration/V2__create_reference_tables.sql`:

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
    role VARCHAR(32) NOT NULL,
    team_id BIGINT NOT NULL REFERENCES team(id)
);
```

- [ ] **Step 3: Create V3 — planning tables**

Create `backend/src/main/resources/db/migration/V3__create_planning_tables.sql`:

```sql
CREATE TABLE planning_week (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    team_id BIGINT NOT NULL REFERENCES team(id),
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

- [ ] **Step 4: Create V100 — seed strategy data**

Create `backend/src/main/resources/db/migration/V100__seed_strategy.sql`:

```sql
-- Rally Cries
INSERT INTO rally_cry (title, description) VALUES
    ('Platform Reliability', 'Ensure our platform is rock-solid and always available'),
    ('Revenue Growth', 'Accelerate revenue through product improvements'),
    ('Customer Delight', 'Create experiences that make customers love the product');

-- Defining Objectives for Platform Reliability (id=1)
INSERT INTO defining_objective (rally_cry_id, title, description) VALUES
    (1, 'Eliminate single points of failure', 'Remove all SPOF from critical infrastructure'),
    (1, 'Improve performance', 'Make the platform faster for all users');

-- Defining Objectives for Revenue Growth (id=2)
INSERT INTO defining_objective (rally_cry_id, title, description) VALUES
    (2, 'Streamline payments', 'Reduce friction in the payment flow'),
    (2, 'Expand self-serve', 'Enable customers to onboard without sales');

-- Defining Objectives for Customer Delight (id=3)
INSERT INTO defining_objective (rally_cry_id, title, description) VALUES
    (3, 'Redesign onboarding', 'Create a world-class first-run experience'),
    (3, 'Improve support response', 'Faster and more helpful customer support');

-- Outcomes for Eliminate SPOFs (do_id=1)
INSERT INTO outcome (defining_objective_id, title, description) VALUES
    (1, 'Auth uptime 99.9%', 'Authentication service maintains 99.9% uptime'),
    (1, 'Zero unplanned DB failovers', 'No unplanned database failovers in the quarter');

-- Outcomes for Improve performance (do_id=2)
INSERT INTO outcome (defining_objective_id, title, description) VALUES
    (2, 'Dashboard p95 < 500ms', 'Dashboard page loads under 500ms at p95'),
    (2, 'API p99 < 200ms', 'All API endpoints respond under 200ms at p99');

-- Outcomes for Streamline payments (do_id=3)
INSERT INTO outcome (defining_objective_id, title, description) VALUES
    (3, 'Reduce payment failures 50%', 'Cut payment failure rate in half'),
    (3, 'Checkout conversion > 85%', 'Improve checkout completion rate above 85%');

-- Outcomes for Expand self-serve (do_id=4)
INSERT INTO outcome (defining_objective_id, title, description) VALUES
    (4, 'Self-serve signup rate 60%', '60% of new customers sign up without sales'),
    (4, 'Trial-to-paid conversion 25%', 'Convert 25% of trial users to paid');

-- Outcomes for Redesign onboarding (do_id=5)
INSERT INTO outcome (defining_objective_id, title, description) VALUES
    (5, 'Onboarding completion 80%', '80% of new users complete onboarding'),
    (5, 'Time to first value < 5 min', 'Users reach first value moment in under 5 minutes');

-- Outcomes for Improve support response (do_id=6)
INSERT INTO outcome (defining_objective_id, title, description) VALUES
    (6, 'First response < 1 hour', 'All support tickets get first response within 1 hour'),
    (6, 'CSAT score > 4.5', 'Customer satisfaction score above 4.5 out of 5');
```

- [ ] **Step 5: Create V101 — seed chess categories**

Create `backend/src/main/resources/db/migration/V101__seed_reference.sql`:

```sql
INSERT INTO chess_category (code, display_name, description, sort_order) VALUES
    ('KING', 'King', 'Mission-critical, organization-defining work', 1),
    ('QUEEN', 'Queen', 'High leverage, cross-functional / broad impact', 2),
    ('ROOK', 'Rook', 'Structural / delivery / operationally critical', 3),
    ('BISHOP', 'Bishop', 'Specialized or enabling work', 4),
    ('KNIGHT', 'Knight', 'Ambiguous, exploratory, problem-solving work', 5),
    ('PAWN', 'Pawn', 'Routine, maintenance, follow-through work', 6);
```

- [ ] **Step 6: Create V102 — seed teams and users**

Create `backend/src/main/resources/db/migration/V102__seed_users.sql`:

```sql
INSERT INTO team (name) VALUES ('Platform'), ('Product');

INSERT INTO app_user (name, email, role, team_id) VALUES
    ('Sarah Chen', 'sarah.chen@example.com', 'IC', 1),
    ('James Park', 'james.park@example.com', 'IC', 1),
    ('Mike Johnson', 'mike.johnson@example.com', 'MANAGER', 1),
    ('Lisa Wang', 'lisa.wang@example.com', 'IC', 2),
    ('Alex Rivera', 'alex.rivera@example.com', 'IC', 2),
    ('Priya Patel', 'priya.patel@example.com', 'MANAGER', 2);
```

- [ ] **Step 7: Verify migrations run against Docker Postgres**

```bash
cd /Users/rohanthomas/Code/weekly-planning
docker compose up postgres -d
```

Wait for healthy, then:

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew bootRun
```

Expected: Application starts, Flyway runs all migrations successfully. Stop with Ctrl+C.

- [ ] **Step 8: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/main/resources/db/
git commit -m "feat: add Flyway migrations for schema and seed data"
```

---

## Task 3: Backend Domain Entities & Enums

**Files:**
- Create: `backend/src/main/java/com/weeklyplanning/domain/enums/PlanningWeekStatus.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/enums/CommitDisposition.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/enums/UserRole.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/RallyCry.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/DefiningObjective.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/Outcome.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/ChessCategory.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/Team.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/AppUser.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/PlanningWeek.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/WeeklyCommit.java`
- Create: `backend/src/main/java/com/weeklyplanning/domain/entity/WeeklyCommitReconciliation.java`

- [ ] **Step 1: Create enums**

Create `PlanningWeekStatus.java`:

```java
package com.weeklyplanning.domain.enums;

public enum PlanningWeekStatus {
    DRAFT, LOCKED, RECONCILING, RECONCILED
}
```

Create `CommitDisposition.java`:

```java
package com.weeklyplanning.domain.enums;

public enum CommitDisposition {
    COMPLETED, PARTIALLY_COMPLETED, NOT_COMPLETED, CARRIED_FORWARD, DROPPED
}
```

Create `UserRole.java`:

```java
package com.weeklyplanning.domain.enums;

public enum UserRole {
    IC, MANAGER
}
```

- [ ] **Step 2: Create strategy entities**

Create `RallyCry.java` — JPA entity mapping to `rally_cry` table. Fields: `id` (Long, generated identity), `title` (String), `description` (String).

Create `DefiningObjective.java` — JPA entity mapping to `defining_objective` table. Fields: `id`, `rallyCryId` (Long, column `rally_cry_id`), `title`, `description`.

Create `Outcome.java` — JPA entity mapping to `outcome` table. Fields: `id`, `definingObjectiveId` (Long, column `defining_objective_id`), `title`, `description`.

- [ ] **Step 3: Create reference entities**

Create `ChessCategory.java` — JPA entity mapping to `chess_category` table. Primary key is `code` (String). Fields: `displayName`, `description`, `sortOrder`, `active`.

Create `Team.java` — JPA entity mapping to `team` table. Fields: `id`, `name`.

Create `AppUser.java` — JPA entity mapping to `app_user` table. Fields: `id`, `name`, `email`, `role` (UserRole, stored as STRING), `teamId` (Long).

- [ ] **Step 4: Create planning entities**

Create `PlanningWeek.java` — JPA entity mapping to `planning_week` table. Fields: `id`, `userId`, `teamId`, `weekStartDate` (LocalDate), `weekEndDate` (LocalDate), `status` (PlanningWeekStatus, stored as STRING), `lockedAt` (Instant), `blockersSummary`, `managerNotes`, `reconcilingAt` (Instant), `reconciledAt` (Instant), `version` (int, annotated with `@Version`), `createdAt` (Instant), `updatedAt` (Instant). Add `@PreUpdate` to set `updatedAt`.

Create `WeeklyCommit.java` — JPA entity mapping to `weekly_commit` table. Fields: `id`, `planningWeekId`, `title`, `description`, `rallyCryId`, `definingObjectiveId`, `outcomeId`, `chessCategoryCode`, `priorityRank`, `stretch`, `sourceCommitId`, `createdAt`, `updatedAt`. Add `@PreUpdate` to set `updatedAt`.

Create `WeeklyCommitReconciliation.java` — JPA entity mapping to `weekly_commit_reconciliation` table. Fields: `id`, `weeklyCommitId`, `disposition` (CommitDisposition, stored as STRING), `actualResult`, `percentComplete` (BigDecimal), `blockerNotes`, `carryForward`, `reconciliationNotes`, `reconciledAt` (Instant).

- [ ] **Step 5: Verify entities compile and boot**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/main/java/com/weeklyplanning/domain/
git commit -m "feat: add JPA entities and enums for all domain objects"
```

---

## Task 4: Repositories & Demo Auth

**Files:**
- Create: All repository interfaces in `backend/src/main/java/com/weeklyplanning/infrastructure/repository/`
- Create: `backend/src/main/java/com/weeklyplanning/infrastructure/config/UserContext.java`
- Create: `backend/src/main/java/com/weeklyplanning/infrastructure/config/UserContextFilter.java`
- Create: `backend/src/main/java/com/weeklyplanning/infrastructure/config/WebConfig.java`

- [ ] **Step 1: Create repository interfaces**

Create Spring Data JPA repositories for all entities. Each extends `JpaRepository<Entity, KeyType>`.

Custom query methods needed:
- `PlanningWeekRepository`: `Optional<PlanningWeek> findByUserIdAndWeekStartDate(Long userId, LocalDate weekStartDate)`, `List<PlanningWeek> findByTeamIdAndWeekStartDate(Long teamId, LocalDate weekStartDate)`
- `WeeklyCommitRepository`: `List<WeeklyCommit> findByPlanningWeekIdOrderByPriorityRankAsc(Long planningWeekId)`, `int countByPlanningWeekId(Long planningWeekId)`
- `WeeklyCommitReconciliationRepository`: `Optional<WeeklyCommitReconciliation> findByWeeklyCommitId(Long weeklyCommitId)`, `List<WeeklyCommitReconciliation> findByWeeklyCommitIdIn(List<Long> commitIds)`
- `DefiningObjectiveRepository`: `List<DefiningObjective> findByRallyCryId(Long rallyCryId)`
- `OutcomeRepository`: `List<Outcome> findByDefiningObjectiveId(Long definingObjectiveId)`
- `AppUserRepository`: `List<AppUser> findByTeamId(Long teamId)`
- `ChessCategoryRepository`: `List<ChessCategory> findByActiveTrueOrderBySortOrderAsc()`

- [ ] **Step 2: Create UserContext**

Create `backend/src/main/java/com/weeklyplanning/infrastructure/config/UserContext.java`:

```java
package com.weeklyplanning.infrastructure.config;

import com.weeklyplanning.domain.entity.AppUser;

public class UserContext {
    private static final ThreadLocal<AppUser> currentUser = new ThreadLocal<>();

    public static void set(AppUser user) {
        currentUser.set(user);
    }

    public static AppUser get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
```

- [ ] **Step 3: Create UserContextFilter**

Create `backend/src/main/java/com/weeklyplanning/infrastructure/config/UserContextFilter.java`:

A `jakarta.servlet.Filter` registered as a Spring `@Component`. On every request:
1. Read `X-User-Id` header
2. If missing, return 401 with JSON error body `{"error": "MISSING_USER", "message": "X-User-Id header required"}`
3. Parse as Long. Look up user from `AppUserRepository`
4. If not found, return 404 with `{"error": "USER_NOT_FOUND", "message": "User not found"}`
5. Set `UserContext.set(user)`
6. Call `chain.doFilter(request, response)`
7. In `finally` block, call `UserContext.clear()`

The filter should skip non-API paths (those not starting with `/api/`).

- [ ] **Step 4: Create WebConfig**

Create `backend/src/main/java/com/weeklyplanning/infrastructure/config/WebConfig.java`:

A `@Configuration` class. Add CORS configuration allowing all origins (for dev/demo). Register the `UserContextFilter` with URL pattern `/api/*`.

- [ ] **Step 5: Verify compilation**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/main/java/com/weeklyplanning/infrastructure/
git commit -m "feat: add repositories and demo auth via X-User-Id header filter"
```

---

## Task 5: Exception Handling & DTOs

**Files:**
- Create: All exception classes in `backend/src/main/java/com/weeklyplanning/domain/exception/`
- Create: All DTO records in `backend/src/main/java/com/weeklyplanning/api/dto/`

- [ ] **Step 1: Create exception classes**

Create `InvalidStatusTransitionException.java` — extends `RuntimeException`. Takes `currentStatus` and `targetStatus` strings. Message: `"Cannot transition from {current} to {target}"`.

Create `InvalidHierarchyException.java` — extends `RuntimeException`. Takes a message string.

Create `WeekLockedException.java` — extends `RuntimeException`. Message: `"Week is locked and cannot be modified"`.

- [ ] **Step 2: Create GlobalExceptionHandler**

Create `backend/src/main/java/com/weeklyplanning/domain/exception/GlobalExceptionHandler.java`:

A `@RestControllerAdvice` that handles:
- `InvalidStatusTransitionException` → 400, error code `INVALID_STATUS_TRANSITION`
- `InvalidHierarchyException` → 400, error code `INVALID_HIERARCHY`
- `WeekLockedException` → 400, error code `WEEK_LOCKED`
- `jakarta.persistence.EntityNotFoundException` → 404, error code `NOT_FOUND`
- `IllegalArgumentException` → 400, error code `VALIDATION_ERROR`

All return `ErrorResponse` record.

- [ ] **Step 3: Create DTO records**

Create all DTOs as Java records in `backend/src/main/java/com/weeklyplanning/api/dto/`:

`ErrorResponse.java`:
```java
public record ErrorResponse(String error, String message) {}
```

`CreateCommitRequest.java`:
```java
public record CreateCommitRequest(
    @NotBlank String title,
    String description,
    @NotNull Long rallyCryId,
    @NotNull Long definingObjectiveId,
    @NotNull Long outcomeId,
    @NotBlank String chessCategoryCode,
    @NotNull Integer priorityRank,
    boolean stretch
) {}
```

`UpdateCommitRequest.java` — same fields as CreateCommitRequest.

`ReconcileCommitRequest.java`:
```java
public record ReconcileCommitRequest(
    @NotNull CommitDisposition disposition,
    String actualResult,
    BigDecimal percentComplete,
    String blockerNotes,
    boolean carryForward,
    String reconciliationNotes
) {}
```

`ReorderRequest.java`:
```java
public record ReorderRequest(@NotEmpty List<Long> commitIds) {}
```

`PlanningWeekDto.java`, `WeeklyCommitDto.java`, `ReconciliationDto.java`, `RallyCryDto.java`, `DefiningObjectiveDto.java`, `OutcomeDto.java`, `StrategyTreeDto.java`, `ChessCategoryDto.java`, `UserDto.java`, `TeamWeekStatusDto.java` — all as records matching the spec DTOs.

`StrategyTreeDto.java`:
```java
public record StrategyTreeDto(
    Long id,
    String title,
    String description,
    List<DefiningObjectiveTreeNode> definingObjectives
) {
    public record DefiningObjectiveTreeNode(
        Long id,
        String title,
        String description,
        List<OutcomeDto> outcomes
    ) {}
}
```

`TeamWeekStatusDto.java`:
```java
public record TeamWeekStatusDto(
    Long userId,
    String userName,
    String userRole,
    Long weekId,
    String status,
    int commitCount,
    LocalDate weekStartDate
) {}
```

- [ ] **Step 4: Verify compilation**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/main/java/com/weeklyplanning/domain/exception/ backend/src/main/java/com/weeklyplanning/api/dto/
git commit -m "feat: add exception handling and all DTO records"
```

---

## Task 6: Strategy & User Services + Controllers

**Files:**
- Create: `backend/src/main/java/com/weeklyplanning/application/StrategyService.java`
- Create: `backend/src/main/java/com/weeklyplanning/api/StrategyController.java`
- Create: `backend/src/main/java/com/weeklyplanning/api/UserController.java`
- Test: `backend/src/test/java/com/weeklyplanning/api/StrategyControllerTest.java`

- [ ] **Step 1: Write failing test for strategy tree endpoint**

Create `backend/src/test/java/com/weeklyplanning/api/StrategyControllerTest.java`:

A `@SpringBootTest` + `@AutoConfigureMockMvc` test using H2 in-memory database. Add `application-test.yml` in `src/test/resources/` pointing to H2 with Flyway enabled. The test should verify `GET /api/strategy/tree` returns the seeded hierarchy with correct nesting.

Test method `getStrategyTree_returnsNestedHierarchy`:
1. Call `GET /api/strategy/tree` with `X-User-Id: 1` header
2. Assert 200 status
3. Assert response JSON has 3 rally cries
4. Assert first rally cry has 2 defining objectives
5. Assert first defining objective has 2 outcomes

- [ ] **Step 2: Run test to verify it fails**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*StrategyControllerTest*' -i
```

Expected: FAIL (StrategyService and StrategyController don't exist yet)

- [ ] **Step 3: Create test application config**

Create `backend/src/test/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  flyway:
    enabled: true
    locations: classpath:db/migration
  test:
    database:
      replace: none
```

- [ ] **Step 4: Implement StrategyService**

Create `backend/src/main/java/com/weeklyplanning/application/StrategyService.java`:

A `@Service` class with methods:
- `List<RallyCryDto> getAllRallyCries()` — returns all rally cries
- `List<DefiningObjectiveDto> getDefiningObjectives(Long rallyCryId)` — returns DOs for a rally cry
- `List<OutcomeDto> getOutcomes(Long definingObjectiveId)` — returns outcomes for a DO
- `List<StrategyTreeDto> getStrategyTree()` — loads all rally cries, for each loads DOs, for each loads outcomes. Returns nested structure.

- [ ] **Step 5: Implement StrategyController**

Create `backend/src/main/java/com/weeklyplanning/api/StrategyController.java`:

A `@RestController` with `@RequestMapping("/api/strategy")`:
- `GET /rally-cries` → `getAllRallyCries()`
- `GET /rally-cries/{id}/defining-objectives` → `getDefiningObjectives(id)`
- `GET /defining-objectives/{id}/outcomes` → `getOutcomes(id)`
- `GET /tree` → `getStrategyTree()`

- [ ] **Step 6: Implement UserController**

Create `backend/src/main/java/com/weeklyplanning/api/UserController.java`:

A `@RestController` with `@RequestMapping("/api")`:
- `GET /users` → returns all users as `List<UserDto>`
- `GET /users/me` → returns the current user from `UserContext.get()` as `UserDto`
- `GET /chess-categories` → returns all active chess categories ordered by sort_order

- [ ] **Step 7: Run tests**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*StrategyControllerTest*' -i
```

Expected: PASS

- [ ] **Step 8: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/
git commit -m "feat: add strategy tree, users, and chess categories endpoints"
```

---

## Task 7: PlanningWeek Service + Controller (TDD)

**Files:**
- Create: `backend/src/main/java/com/weeklyplanning/application/PlanningWeekService.java`
- Create: `backend/src/main/java/com/weeklyplanning/api/PlanningWeekController.java`
- Test: `backend/src/test/java/com/weeklyplanning/application/PlanningWeekServiceTest.java`
- Test: `backend/src/test/java/com/weeklyplanning/api/PlanningWeekControllerTest.java`

- [ ] **Step 1: Write failing service test — getCurrentWeek creates draft**

Create `PlanningWeekServiceTest.java` as a `@SpringBootTest` with `@ActiveProfiles("test")`.

Test `getCurrentWeek_createsDraftIfNotExists`:
1. Call `service.getCurrentWeek(userId=1)`
2. Assert returned week has status DRAFT
3. Assert `weekStartDate` is the Monday of the current calendar week
4. Assert `weekEndDate` is the Friday
5. Call again — assert same week returned (idempotent)

- [ ] **Step 2: Run test to verify it fails**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*PlanningWeekServiceTest.getCurrentWeek*' -i
```

Expected: FAIL

- [ ] **Step 3: Implement PlanningWeekService.getCurrentWeek**

Create `PlanningWeekService.java` as a `@Service @Transactional` class.

Method `getCurrentWeek(Long userId)`:
1. Calculate Monday of current week: `LocalDate.now().with(DayOfWeek.MONDAY)`
2. Calculate Friday: monday + 4 days
3. Look up `planningWeekRepository.findByUserIdAndWeekStartDate(userId, monday)`
4. If present, return it (mapped to DTO with commits)
5. If absent, create new PlanningWeek with status DRAFT, save, return DTO

- [ ] **Step 4: Run test to verify it passes**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*PlanningWeekServiceTest.getCurrentWeek*' -i
```

Expected: PASS

- [ ] **Step 5: Write failing test — lockWeek validates and transitions**

Test `lockWeek_transitionsDraftToLocked`:
1. Create a draft week with one valid commit (inserted via repository)
2. Call `service.lockWeek(weekId, userId)`
3. Assert status is LOCKED and `lockedAt` is set

Test `lockWeek_failsIfNoCommits`:
1. Create a draft week with zero commits
2. Call `service.lockWeek(weekId, userId)`
3. Assert throws `IllegalArgumentException`

Test `lockWeek_failsIfNotDraft`:
1. Create a LOCKED week
2. Call `service.lockWeek(weekId, userId)`
3. Assert throws `InvalidStatusTransitionException`

- [ ] **Step 6: Run tests to verify they fail**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*PlanningWeekServiceTest.lockWeek*' -i
```

Expected: FAIL

- [ ] **Step 7: Implement lockWeek**

Method `lockWeek(Long weekId, Long userId)`:
1. Load week, verify it belongs to userId
2. Check status is DRAFT, else throw `InvalidStatusTransitionException`
3. Check at least one commit exists, else throw `IllegalArgumentException`
4. Verify all commits have valid RCDO chain (delegate to `WeeklyCommitService.validateHierarchy`)
5. Set status to LOCKED, set `lockedAt` to `Instant.now()`
6. Save and return DTO

- [ ] **Step 8: Run tests to verify they pass**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*PlanningWeekServiceTest.lockWeek*' -i
```

Expected: PASS

- [ ] **Step 9: Implement PlanningWeekController**

Create `PlanningWeekController.java`:

```java
@RestController
@RequestMapping("/api/weeks")
public class PlanningWeekController {

    @GetMapping("/current")
    public PlanningWeekDto getCurrentWeek() {
        AppUser user = UserContext.get();
        return planningWeekService.getCurrentWeek(user.getId());
    }

    @GetMapping("/{weekId}")
    public PlanningWeekDto getWeek(@PathVariable Long weekId) {
        return planningWeekService.getWeek(weekId);
    }

    @PostMapping("/{weekId}/lock")
    public PlanningWeekDto lockWeek(@PathVariable Long weekId) {
        AppUser user = UserContext.get();
        return planningWeekService.lockWeek(weekId, user.getId());
    }
}
```

- [ ] **Step 10: Write controller integration test**

Create `PlanningWeekControllerTest.java`. Test `GET /api/weeks/current` returns 200 with a DRAFT week. Test `POST /api/weeks/{id}/lock` with no commits returns 400.

- [ ] **Step 11: Run all tests**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test -i
```

Expected: ALL PASS

- [ ] **Step 12: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/
git commit -m "feat: add PlanningWeek service and controller with lifecycle management"
```

---

## Task 8: WeeklyCommit Service + Controller (TDD)

**Files:**
- Create: `backend/src/main/java/com/weeklyplanning/application/WeeklyCommitService.java`
- Create: `backend/src/main/java/com/weeklyplanning/api/WeeklyCommitController.java`
- Test: `backend/src/test/java/com/weeklyplanning/application/WeeklyCommitServiceTest.java`
- Test: `backend/src/test/java/com/weeklyplanning/api/WeeklyCommitControllerTest.java`

- [ ] **Step 1: Write failing test — RCDO hierarchy validation**

Create `WeeklyCommitServiceTest.java`.

Test `createCommit_validatesHierarchy`:
1. Create draft week
2. Call createCommit with valid rallyCryId=1, definingObjectiveId=1, outcomeId=1
3. Assert commit created

Test `createCommit_rejectsInvalidHierarchy`:
1. Call createCommit with rallyCryId=1, definingObjectiveId=1, outcomeId=5 (outcome 5 belongs to DO 3, not DO 1)
2. Assert throws `InvalidHierarchyException`

- [ ] **Step 2: Run tests to verify they fail**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*WeeklyCommitServiceTest*' -i
```

Expected: FAIL

- [ ] **Step 3: Implement WeeklyCommitService**

Create `WeeklyCommitService.java` as a `@Service @Transactional` class.

Methods:
- `createCommit(Long weekId, CreateCommitRequest request)` — validate week is DRAFT, validate RCDO hierarchy, save commit, return DTO
- `updateCommit(Long weekId, Long commitId, UpdateCommitRequest request)` — validate week is DRAFT, validate hierarchy, update, return DTO
- `deleteCommit(Long weekId, Long commitId)` — validate week is DRAFT, delete commit
- `reorderCommits(Long weekId, ReorderRequest request)` — validate week is DRAFT, reassign priority_rank based on position in commitIds list
- `validateHierarchy(Long rallyCryId, Long definingObjectiveId, Long outcomeId)` — check outcome belongs to DO, DO belongs to Rally Cry

The RCDO validation:
```java
void validateHierarchy(Long rallyCryId, Long definingObjectiveId, Long outcomeId) {
    Outcome outcome = outcomeRepository.findById(outcomeId)
        .orElseThrow(() -> new EntityNotFoundException("Outcome not found"));
    if (!outcome.getDefiningObjectiveId().equals(definingObjectiveId)) {
        throw new InvalidHierarchyException("Outcome does not belong to the specified Defining Objective");
    }
    DefiningObjective defObj = definingObjectiveRepository.findById(definingObjectiveId)
        .orElseThrow(() -> new EntityNotFoundException("Defining Objective not found"));
    if (!defObj.getRallyCryId().equals(rallyCryId)) {
        throw new InvalidHierarchyException("Defining Objective does not belong to the specified Rally Cry");
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*WeeklyCommitServiceTest*' -i
```

Expected: PASS

- [ ] **Step 5: Write failing test — locked week rejects mutations**

Test `createCommit_rejectsLockedWeek`:
1. Create and lock a week
2. Call createCommit
3. Assert throws `WeekLockedException`

- [ ] **Step 6: Run test to verify it fails**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*WeeklyCommitServiceTest.createCommit_rejectsLockedWeek*' -i
```

Expected: FAIL

- [ ] **Step 7: Add locked week check to service**

At the top of `createCommit`, `updateCommit`, `deleteCommit`, `reorderCommits`:
```java
PlanningWeek week = planningWeekRepository.findById(weekId)
    .orElseThrow(() -> new EntityNotFoundException("Week not found"));
if (week.getStatus() != PlanningWeekStatus.DRAFT) {
    throw new WeekLockedException();
}
```

- [ ] **Step 8: Run test to verify it passes**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*WeeklyCommitServiceTest.createCommit_rejectsLockedWeek*' -i
```

Expected: PASS

- [ ] **Step 9: Implement WeeklyCommitController**

Create `WeeklyCommitController.java`:

```java
@RestController
@RequestMapping("/api/weeks/{weekId}/commits")
public class WeeklyCommitController {

    @PostMapping
    public ResponseEntity<WeeklyCommitDto> createCommit(
            @PathVariable Long weekId,
            @Valid @RequestBody CreateCommitRequest request) {
        return ResponseEntity.status(201).body(
            weeklyCommitService.createCommit(weekId, request));
    }

    @PutMapping("/{commitId}")
    public WeeklyCommitDto updateCommit(
            @PathVariable Long weekId,
            @PathVariable Long commitId,
            @Valid @RequestBody UpdateCommitRequest request) {
        return weeklyCommitService.updateCommit(weekId, commitId, request);
    }

    @DeleteMapping("/{commitId}")
    public ResponseEntity<Void> deleteCommit(
            @PathVariable Long weekId,
            @PathVariable Long commitId) {
        weeklyCommitService.deleteCommit(weekId, commitId);
        return ResponseEntity.noContent().build();
    }

}
```

Note: The reorder endpoint is `PUT /api/weeks/{weekId}/reorder` — this lives on `PlanningWeekController`, not `WeeklyCommitController`. Add it to `PlanningWeekController`:

```java
@PutMapping("/{weekId}/reorder")
public List<WeeklyCommitDto> reorderCommits(
        @PathVariable Long weekId,
        @Valid @RequestBody ReorderRequest request) {
    return weeklyCommitService.reorderCommits(weekId, request);
}
```

- [ ] **Step 10: Write controller integration test**

Create `WeeklyCommitControllerTest.java`. Test POST creates a commit, PUT updates it, DELETE removes it, and invalid hierarchy returns 400.

- [ ] **Step 11: Run all tests**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test -i
```

Expected: ALL PASS

- [ ] **Step 12: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/
git commit -m "feat: add WeeklyCommit CRUD with RCDO validation and locked week enforcement"
```

---

## Task 9: Reconciliation & Carry-Forward Services + Controllers (TDD)

**Files:**
- Create: `backend/src/main/java/com/weeklyplanning/application/ReconciliationService.java`
- Create: `backend/src/main/java/com/weeklyplanning/application/CarryForwardService.java`
- Create: `backend/src/main/java/com/weeklyplanning/api/ReconciliationController.java`
- Test: `backend/src/test/java/com/weeklyplanning/application/ReconciliationServiceTest.java`
- Test: `backend/src/test/java/com/weeklyplanning/application/CarryForwardServiceTest.java`

- [ ] **Step 1: Write failing test — start reconciliation**

Create `ReconciliationServiceTest.java`.

Test `startReconciliation_transitionsLockedToReconciling`:
1. Create a LOCKED week
2. Call `service.startReconciliation(weekId, userId)`
3. Assert status is RECONCILING

Test `startReconciliation_failsIfNotLocked`:
1. Create a DRAFT week
2. Call `service.startReconciliation(weekId, userId)`
3. Assert throws `InvalidStatusTransitionException`

- [ ] **Step 2: Run tests to verify they fail**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*ReconciliationServiceTest*' -i
```

Expected: FAIL

- [ ] **Step 3: Implement ReconciliationService**

Create `ReconciliationService.java` as a `@Service @Transactional` class.

Methods:
- `startReconciliation(Long weekId, Long userId)` — verify LOCKED, transition to RECONCILING
- `reconcileCommit(Long weekId, Long commitId, ReconcileCommitRequest request)` — verify week is RECONCILING, validate disposition/carryForward combination (COMPLETED+carryForward and DROPPED+carryForward are rejected), create or update reconciliation record. If disposition is CARRIED_FORWARD, auto-set carryForward=true.
- `updateWeekSummary(Long weekId, String blockersSummary, String managerNotes)` — verify week is RECONCILING, update the `blockers_summary` and `manager_notes` fields on `planning_week`
- `reconcileWeek(Long weekId, Long userId)` — verify RECONCILING, verify all commits have reconciliation records, transition to RECONCILED

- [ ] **Step 4: Run tests to verify they pass**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*ReconciliationServiceTest*' -i
```

Expected: PASS

- [ ] **Step 5: Write failing test — reconcile commit disposition rules**

Test `reconcileCommit_rejectsCompletedWithCarryForward`:
1. Create RECONCILING week with a commit
2. Call reconcileCommit with disposition=COMPLETED, carryForward=true
3. Assert throws `IllegalArgumentException`

Test `reconcileCommit_autoSetsCarryForwardForCarriedForwardDisposition`:
1. Call reconcileCommit with disposition=CARRIED_FORWARD, carryForward=false
2. Assert stored reconciliation has carryForward=true

- [ ] **Step 6: Run tests, verify fail, implement, verify pass**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*ReconciliationServiceTest.reconcileCommit*' -i
```

- [ ] **Step 7: Write failing test — carry-forward cloning**

Create `CarryForwardServiceTest.java`.

Test `carryForward_clonesMarkedCommitsToNextWeek`:
1. Create a RECONCILED week with 3 commits. 2 have carryForward=true, 1 has carryForward=false.
2. Call `carryForwardService.carryForward(weekId, userId)`
3. Assert next week was created with status DRAFT
4. Assert next week has 2 commits (cloned from carry-forward ones)
5. Assert each cloned commit has `sourceCommitId` pointing to original
6. Assert RCDO links, chess category, and title are preserved

- [ ] **Step 8: Run test, verify fail**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*CarryForwardServiceTest*' -i
```

Expected: FAIL

- [ ] **Step 9: Implement CarryForwardService**

Create `CarryForwardService.java` as a `@Service @Transactional` class.

Method `carryForward(Long weekId, Long userId)`:
1. Load week, verify RECONCILED status
2. Find all commits for this week
3. Find reconciliations where carryForward=true
4. Calculate next Monday (weekStartDate + 7)
5. Get or create next week's DRAFT PlanningWeek
6. Count existing commits in target week for priority offset
7. For each carry-forward commit: clone with new planningWeekId, new priorityRank (offset + index + 1), sourceCommitId = original commit id
8. Return next week's DTO

- [ ] **Step 10: Run test, verify pass**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test --tests '*CarryForwardServiceTest*' -i
```

Expected: PASS

- [ ] **Step 11: Implement ReconciliationController**

Create `ReconciliationController.java`:

```java
@RestController
@RequestMapping("/api/weeks/{weekId}")
public class ReconciliationController {

    @PostMapping("/start-reconciliation")
    public PlanningWeekDto startReconciliation(@PathVariable Long weekId) {
        AppUser user = UserContext.get();
        return reconciliationService.startReconciliation(weekId, user.getId());
    }

    @PutMapping("/commits/{commitId}/reconcile")
    public ReconciliationDto reconcileCommit(
            @PathVariable Long weekId,
            @PathVariable Long commitId,
            @Valid @RequestBody ReconcileCommitRequest request) {
        return reconciliationService.reconcileCommit(weekId, commitId, request);
    }

    @PutMapping("/summary")
    public PlanningWeekDto updateWeekSummary(
            @PathVariable Long weekId,
            @RequestBody Map<String, String> body) {
        return reconciliationService.updateWeekSummary(
            weekId, body.get("blockersSummary"), body.get("managerNotes"));
    }

    @PostMapping("/reconcile")
    public PlanningWeekDto reconcileWeek(@PathVariable Long weekId) {
        AppUser user = UserContext.get();
        return reconciliationService.reconcileWeek(weekId, user.getId());
    }

    @PostMapping("/carry-forward")
    public PlanningWeekDto carryForward(@PathVariable Long weekId) {
        AppUser user = UserContext.get();
        return carryForwardService.carryForward(weekId, user.getId());
    }
}
```

- [ ] **Step 12: Run all backend tests**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test -i
```

Expected: ALL PASS

- [ ] **Step 13: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/
git commit -m "feat: add reconciliation workflow and carry-forward cloning"
```

---

## Task 10: Manager Service + Controller

**Files:**
- Create: `backend/src/main/java/com/weeklyplanning/application/ManagerService.java`
- Create: `backend/src/main/java/com/weeklyplanning/api/ManagerController.java`

- [ ] **Step 1: Implement ManagerService**

Create `ManagerService.java` as a `@Service` class.

Methods:
- `getTeamWeeks(Long managerId)` — load the manager's team, find all PlanningWeeks for team members for the current week, return as `List<TeamWeekStatusDto>`. Include team members without a week (status = null / "NOT_STARTED").
- `getTeamMemberWeek(Long managerId, Long userId, Long weekId)` — verify manager is on same team as user, load the week with commits and reconciliations, return as `PlanningWeekDto`.

Both methods verify the calling user has role MANAGER, throw 403 otherwise.

- [ ] **Step 2: Implement ManagerController**

Create `ManagerController.java`:

```java
@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    @GetMapping("/team/weeks")
    public List<TeamWeekStatusDto> getTeamWeeks() {
        AppUser user = UserContext.get();
        return managerService.getTeamWeeks(user.getId());
    }

    @GetMapping("/users/{userId}/weeks/{weekId}")
    public PlanningWeekDto getTeamMemberWeek(
            @PathVariable Long userId,
            @PathVariable Long weekId) {
        AppUser user = UserContext.get();
        return managerService.getTeamMemberWeek(user.getId(), userId, weekId);
    }
}
```

- [ ] **Step 3: Run all backend tests**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test -i
```

Expected: ALL PASS

- [ ] **Step 4: Verify full backend boots and responds**

```bash
cd /Users/rohanthomas/Code/weekly-planning
docker compose up postgres -d
cd backend
./gradlew bootRun &
sleep 10
curl -s -H "X-User-Id: 1" http://localhost:8080/api/weeks/current | python3 -m json.tool
curl -s -H "X-User-Id: 1" http://localhost:8080/api/strategy/tree | python3 -m json.tool
curl -s -H "X-User-Id: 1" http://localhost:8080/api/users | python3 -m json.tool
curl -s -H "X-User-Id: 1" http://localhost:8080/api/chess-categories | python3 -m json.tool
```

Expected: All return valid JSON.

Kill the backend process after verification.

- [ ] **Step 5: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add backend/src/
git commit -m "feat: add manager team dashboard endpoints"
```

---

## Task 11: Frontend Foundation — Types, API Client, User Context, Layout

**Files:**
- Create: `frontend/src/api/types.ts`
- Create: `frontend/src/api/client.ts`
- Create: `frontend/src/context/UserContext.tsx`
- Create: `frontend/src/layout/AppShell.tsx`
- Create: `frontend/src/layout/AppShell.css`
- Create: `frontend/src/layout/NavBar.tsx`
- Create: `frontend/src/layout/NavBar.css`
- Create: `frontend/src/layout/UserSwitcher.tsx`
- Create: `frontend/src/layout/UserSwitcher.css`
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/App.css`

- [ ] **Step 1: Create TypeScript types**

Create `frontend/src/api/types.ts` with all types from the spec:

```typescript
export type PlanningWeekStatus = 'DRAFT' | 'LOCKED' | 'RECONCILING' | 'RECONCILED';
export type CommitDisposition = 'COMPLETED' | 'PARTIALLY_COMPLETED' | 'NOT_COMPLETED' | 'CARRIED_FORWARD' | 'DROPPED';
export type UserRole = 'IC' | 'MANAGER';

export interface UserDto {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  teamId: number;
}

export interface ChessCategoryDto {
  code: string;
  displayName: string;
  description: string;
  sortOrder: number;
}

export interface RallyCryDto {
  id: number;
  title: string;
  description: string | null;
}

export interface DefiningObjectiveDto {
  id: number;
  title: string;
  description: string | null;
}

export interface OutcomeDto {
  id: number;
  title: string;
  description: string | null;
}

export interface StrategyTreeNode {
  id: number;
  title: string;
  description: string | null;
  definingObjectives: {
    id: number;
    title: string;
    description: string | null;
    outcomes: OutcomeDto[];
  }[];
}

export interface ReconciliationDto {
  id: number;
  weeklyCommitId: number;
  disposition: CommitDisposition;
  actualResult: string | null;
  percentComplete: number | null;
  blockerNotes: string | null;
  carryForward: boolean;
  reconciliationNotes: string | null;
  reconciledAt: string;
}

export interface WeeklyCommitDto {
  id: number;
  title: string;
  description: string | null;
  rallyCryId: number;
  rallyCryTitle: string;
  definingObjectiveId: number;
  definingObjectiveTitle: string;
  outcomeId: number;
  outcomeTitle: string;
  chessCategoryCode: string;
  chessCategoryDisplayName: string;
  priorityRank: number;
  stretch: boolean;
  sourceCommitId: number | null;
  reconciliation: ReconciliationDto | null;
}

export interface PlanningWeekDto {
  id: number;
  userId: number;
  teamId: number;
  weekStartDate: string;
  weekEndDate: string;
  status: PlanningWeekStatus;
  lockedAt: string | null;
  reconcilingAt: string | null;
  reconciledAt: string | null;
  blockersSummary: string | null;
  managerNotes: string | null;
  commits: WeeklyCommitDto[];
}

export interface TeamWeekStatusDto {
  userId: number;
  userName: string;
  userRole: string;
  weekId: number | null;
  status: string | null;
  commitCount: number;
  weekStartDate: string | null;
}

export interface CreateCommitRequest {
  title: string;
  description?: string;
  rallyCryId: number;
  definingObjectiveId: number;
  outcomeId: number;
  chessCategoryCode: string;
  priorityRank: number;
  stretch?: boolean;
}

export interface ReconcileCommitRequest {
  disposition: CommitDisposition;
  actualResult?: string;
  percentComplete?: number;
  blockerNotes?: string;
  carryForward?: boolean;
  reconciliationNotes?: string;
}

export interface ReorderRequest {
  commitIds: number[];
}

export interface ErrorResponse {
  error: string;
  message: string;
}
```

- [ ] **Step 2: Create API client**

Create `frontend/src/api/client.ts`:

```typescript
import type { ErrorResponse } from './types';

let currentUserId: number | null = null;

export function setCurrentUserId(id: number) {
  currentUserId = id;
}

export function getCurrentUserId(): number | null {
  return currentUserId;
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public errorResponse: ErrorResponse,
  ) {
    super(errorResponse.message);
  }
}

export async function apiClient<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (currentUserId !== null) {
    headers['X-User-Id'] = String(currentUserId);
  }

  const response = await fetch(`/api${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const body = await response.json().catch(() => ({
      error: 'UNKNOWN',
      message: response.statusText,
    }));
    throw new ApiError(response.status, body as ErrorResponse);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}
```

- [ ] **Step 3: Create UserContext**

Create `frontend/src/context/UserContext.tsx`:

```tsx
import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { setCurrentUserId } from '../api/client';
import type { UserDto } from '../api/types';

interface UserContextType {
  currentUser: UserDto | null;
  setUser: (user: UserDto) => void;
}

const UserCtx = createContext<UserContextType | null>(null);

export function UserProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<UserDto | null>(() => {
    const stored = localStorage.getItem('wp_current_user');
    if (stored) {
      const user = JSON.parse(stored) as UserDto;
      setCurrentUserId(user.id);
      return user;
    }
    return null;
  });

  function setUser(user: UserDto) {
    setCurrentUser(user);
    setCurrentUserId(user.id);
    localStorage.setItem('wp_current_user', JSON.stringify(user));
  }

  return (
    <UserCtx.Provider value={{ currentUser, setUser }}>
      {children}
    </UserCtx.Provider>
  );
}

export function useUserContext(): UserContextType {
  const ctx = useContext(UserCtx);
  if (!ctx) throw new Error('useUserContext must be used within UserProvider');
  return ctx;
}
```

- [ ] **Step 4: Create layout components**

Create `NavBar.tsx`, `NavBar.css` — top navigation bar with:
- Logo/title "WeeklyPlan" on the left
- Nav links: "My Week" (links to `/my-week`), "Team" (links to `/manager/team`, only visible if current user role is MANAGER)
- `UserSwitcher` component on the right

Create `UserSwitcher.tsx`, `UserSwitcher.css` — dropdown that:
- Fetches all users via `GET /api/users` (uses TanStack Query)
- Shows current user name + role badge
- On select, calls `setUser` from UserContext
- Styled with the warm/approachable design (rounded select, soft borders)

Create `AppShell.tsx`, `AppShell.css` — wraps NavBar + `<Outlet />` from React Router.

- [ ] **Step 5: Update App.tsx with routing**

Update `frontend/src/App.tsx`:

```tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { UserProvider } from './context/UserContext';
import { AppShell } from './layout/AppShell';
import './App.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { staleTime: 30_000 },
  },
});

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <UserProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<AppShell />}>
              <Route index element={<Navigate to="/my-week" replace />} />
              <Route path="/my-week" element={<div>Planning Page (coming soon)</div>} />
              <Route path="/my-week/reconcile" element={<div>Reconciliation Page (coming soon)</div>} />
              <Route path="/manager/team" element={<div>Manager Dashboard (coming soon)</div>} />
              <Route path="/manager/team/:userId/:weekId" element={<div>Member Week View (coming soon)</div>} />
            </Route>
          </Routes>
        </BrowserRouter>
      </UserProvider>
    </QueryClientProvider>
  );
}
```

- [ ] **Step 6: Create shared UI components**

Create `frontend/src/components/Button.tsx` and `Button.css` — primary and secondary variants, rounded (border-radius: 10px), warm colors.

Create `frontend/src/components/Badge.tsx` and `Badge.css` — pill-shaped badge for status and chess categories. Accepts `variant` prop for color mapping.

Create `frontend/src/components/Card.tsx` and `Card.css` — white card with rounded corners, subtle border.

Create `frontend/src/components/Modal.tsx` and `Modal.css` — overlay + centered card for commit form.

Create `frontend/src/components/Toast.tsx` and `Toast.css` — simple notification toast for API errors. Position fixed bottom-right, auto-dismiss after 5 seconds.

- [ ] **Step 7: Verify TypeScript compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 8: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add frontend/src/
git commit -m "feat: add frontend foundation — types, API client, user context, layout, shared components"
```

---

## Task 12: Frontend TanStack Query Hooks

**Files:**
- Create: `frontend/src/api/hooks/useWeeks.ts`
- Create: `frontend/src/api/hooks/useCommits.ts`
- Create: `frontend/src/api/hooks/useReconciliation.ts`
- Create: `frontend/src/api/hooks/useStrategy.ts`
- Create: `frontend/src/api/hooks/useUsers.ts`
- Create: `frontend/src/api/hooks/useManager.ts`

- [ ] **Step 1: Create useWeeks hooks**

Create `frontend/src/api/hooks/useWeeks.ts`:

```typescript
import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { PlanningWeekDto } from '../types';

export function useCurrentWeek() {
  return useQuery({
    queryKey: ['weeks', 'current'],
    queryFn: () => apiClient<PlanningWeekDto>('/weeks/current'),
  });
}

export function useWeek(weekId: number) {
  return useQuery({
    queryKey: ['weeks', weekId],
    queryFn: () => apiClient<PlanningWeekDto>(`/weeks/${weekId}`),
    enabled: weekId > 0,
  });
}
```

- [ ] **Step 2: Create useCommits hooks**

Create `frontend/src/api/hooks/useCommits.ts` with mutation hooks:

- `useCreateCommit(weekId)` — POST to `/weeks/${weekId}/commits`, invalidates `['weeks']` queries on success
- `useUpdateCommit(weekId)` — PUT to `/weeks/${weekId}/commits/${commitId}`
- `useDeleteCommit(weekId)` — DELETE to `/weeks/${weekId}/commits/${commitId}`
- `useReorderCommits(weekId)` — PUT to `/weeks/${weekId}/reorder`
- `useLockWeek()` — POST to `/weeks/${weekId}/lock`

All mutations invalidate `['weeks']` queries on success.

- [ ] **Step 3: Create useReconciliation hooks**

Create `frontend/src/api/hooks/useReconciliation.ts`:

- `useStartReconciliation()` — POST mutation
- `useReconcileCommit(weekId)` — PUT mutation
- `useReconcileWeek()` — POST mutation
- `useCarryForward()` — POST mutation

All invalidate `['weeks']` on success.

- [ ] **Step 4: Create useStrategy hooks**

Create `frontend/src/api/hooks/useStrategy.ts`:

- `useStrategyTree()` — GET `/strategy/tree`, returns `StrategyTreeNode[]`
- `useChessCategories()` — GET `/chess-categories`, returns `ChessCategoryDto[]`

- [ ] **Step 5: Create useUsers hooks**

Create `frontend/src/api/hooks/useUsers.ts`:

- `useUsers()` — GET `/users`
- `useCurrentUser()` — GET `/users/me`

- [ ] **Step 6: Create useManager hooks**

Create `frontend/src/api/hooks/useManager.ts`:

- `useTeamWeeks()` — GET `/manager/team/weeks`
- `useTeamMemberWeek(userId, weekId)` — GET `/manager/users/${userId}/weeks/${weekId}`

- [ ] **Step 7: Verify TypeScript compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 8: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add frontend/src/api/hooks/
git commit -m "feat: add TanStack Query hooks for all API endpoints"
```

---

## Task 13: Frontend Planning Page — My Week View

**Files:**
- Create: `frontend/src/features/planning/PlanningPage.tsx`
- Create: `frontend/src/features/planning/PlanningPage.css`
- Create: `frontend/src/features/planning/WeekHeader.tsx`
- Create: `frontend/src/features/planning/WeekHeader.css`
- Create: `frontend/src/features/planning/CommitList.tsx`
- Create: `frontend/src/features/planning/CommitList.css`
- Create: `frontend/src/features/planning/CommitCard.tsx`
- Create: `frontend/src/features/planning/CommitCard.css`
- Create: `frontend/src/features/planning/WeekSummaryBar.tsx`
- Create: `frontend/src/features/planning/WeekSummaryBar.css`
- Modify: `frontend/src/App.tsx` — replace placeholder route

- [ ] **Step 1: Create WeekHeader component**

Displays:
- Week date range (formatted from weekStartDate/weekEndDate)
- Status badge (DRAFT=amber, LOCKED=blue, RECONCILING=orange, RECONCILED=green)
- "Add Commit" button (only in DRAFT)
- "Lock Week" button (only in DRAFT)
- "Start Reconciliation" button (only in LOCKED)
- Click handlers passed as props

- [ ] **Step 2: Create CommitCard component**

Displays a single commit:
- Priority rank number (left side, gray box)
- Chess category badge (colored pill)
- Title
- RCDO breadcrumb: Rally Cry → Defining Objective → Outcome
- Stretch badge if applicable
- Three-dot menu (edit/delete) — only in DRAFT status
- Click to expand/select

Props: `commit: WeeklyCommitDto`, `weekStatus: PlanningWeekStatus`, `onEdit`, `onDelete`

Style per the mockup: white card, rounded 12px, 1px border, soft colors.

- [ ] **Step 3: Create CommitList with drag-and-drop**

Uses `@dnd-kit/core` and `@dnd-kit/sortable`:
- Wraps CommitCards in `SortableContext`
- Each card wrapped in `useSortable` hook
- On drag end, calls `useReorderCommits` mutation with new order
- Only enables drag when week status is DRAFT

```tsx
import { DndContext, closestCenter, type DragEndEvent } from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
```

- [ ] **Step 4: Create WeekSummaryBar**

Shows at the bottom of the commit list:
- Total commit count
- Chess category breakdown (e.g., "1 King, 2 Rook, 1 Knight")
- Rally Cry coverage count

- [ ] **Step 5: Create PlanningPage**

Assembles the page:
1. Call `useCurrentWeek()` to load the week
2. Loading state while fetching
3. If no user selected, show prompt to select from user switcher
4. Render: WeekHeader → CommitList → WeekSummaryBar
5. "Add Commit" opens CommitForm modal (Task 14)
6. "Lock Week" calls `useLockWeek` mutation, shows confirmation
7. Handle error toasts

- [ ] **Step 6: Wire up route in App.tsx**

Replace the placeholder `<div>Planning Page</div>` with `<PlanningPage />`.

- [ ] **Step 7: Verify TypeScript compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 8: Manual smoke test**

Start backend + postgres + frontend:
```bash
cd /Users/rohanthomas/Code/weekly-planning
docker compose up postgres -d
cd backend && ./gradlew bootRun &
cd ../frontend && npm run dev &
```

Open http://localhost:5173. Select a user from the switcher. Verify the My Week page shows with the DRAFT status and an empty commit list.

- [ ] **Step 9: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add frontend/src/
git commit -m "feat: add Planning Page with commit list, drag-to-reorder, and week lifecycle controls"
```

---

## Task 14: Frontend Commit Form — Strategy Cascader & Chess Picker

**Files:**
- Create: `frontend/src/features/planning/CommitForm.tsx`
- Create: `frontend/src/features/planning/CommitForm.css`
- Create: `frontend/src/features/planning/StrategyCascader.tsx`
- Create: `frontend/src/features/planning/StrategyCascader.css`
- Create: `frontend/src/features/planning/ChessCategoryPicker.tsx`
- Create: `frontend/src/features/planning/ChessCategoryPicker.css`

- [ ] **Step 1: Create StrategyCascader component**

Three cascading dropdowns:
1. Rally Cry dropdown — populated from strategy tree
2. Defining Objective dropdown — filtered by selected Rally Cry
3. Outcome dropdown — filtered by selected DO

Uses `useStrategyTree()` hook. When Rally Cry changes, reset DO and Outcome. When DO changes, reset Outcome.

Props: `value: { rallyCryId, definingObjectiveId, outcomeId }`, `onChange`

Style: rounded selects with soft borders, labels above each.

- [ ] **Step 2: Create ChessCategoryPicker component**

Visual grid of 6 chess categories:
- Each as a selectable card with color-coded icon/badge and name
- Uses `useChessCategories()` hook
- Selected state has colored border
- Colors: King=blue, Queen=red, Rook=amber, Bishop=green, Knight=purple, Pawn=gray

Props: `value: string | null`, `onChange: (code: string) => void`

- [ ] **Step 3: Create CommitForm component**

Modal form for creating/editing a commit:
- Title input (required)
- Description textarea (optional)
- StrategyCascader (required — all three must be selected)
- ChessCategoryPicker (required)
- Stretch checkbox
- Save / Cancel buttons

On save:
- If creating: call `useCreateCommit` with priorityRank = current commit count + 1
- If editing: call `useUpdateCommit`
- On success: close modal, invalidate queries
- On error: show toast with error message

Props: `weekId: number`, `commit?: WeeklyCommitDto` (if editing), `commitCount: number`, `onClose: () => void`

- [ ] **Step 4: Wire CommitForm into PlanningPage**

- "Add Commit" button opens CommitForm in create mode
- Edit action on CommitCard opens CommitForm in edit mode with existing values pre-filled

- [ ] **Step 5: Verify TypeScript compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 6: Manual smoke test**

With backend running:
1. Open http://localhost:5173
2. Select Sarah Chen from user switcher
3. Click "Add Commit"
4. Fill in title, select Rally Cry → DO → Outcome, pick chess category
5. Save — verify commit appears in list
6. Add 2-3 more commits
7. Drag to reorder — verify order persists
8. Edit a commit — verify changes save
9. Delete a commit — verify it disappears
10. Click "Lock Week" — verify status changes and editing is disabled

- [ ] **Step 7: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add frontend/src/
git commit -m "feat: add commit form with strategy cascader and chess category picker"
```

---

## Task 15: Frontend Reconciliation Page

**Files:**
- Create: `frontend/src/features/reconciliation/ReconciliationPage.tsx`
- Create: `frontend/src/features/reconciliation/ReconciliationPage.css`
- Create: `frontend/src/features/reconciliation/ReconcileList.tsx`
- Create: `frontend/src/features/reconciliation/ReconcileList.css`
- Create: `frontend/src/features/reconciliation/ReconcileCard.tsx`
- Create: `frontend/src/features/reconciliation/ReconcileCard.css`
- Create: `frontend/src/features/reconciliation/ReconcileForm.tsx`
- Create: `frontend/src/features/reconciliation/ReconcileForm.css`
- Modify: `frontend/src/App.tsx` — replace placeholder route

- [ ] **Step 1: Create ReconcileForm component**

Inline form for reconciling a single commit:
- Disposition dropdown: Completed, Partially Completed, Not Completed, Carried Forward, Dropped
- Percent complete input (number, 0-100)
- Actual result textarea
- Blocker notes textarea
- Carry-forward checkbox (auto-checked if disposition is CARRIED_FORWARD, disabled if COMPLETED or DROPPED)
- Save button

Props: `commit: WeeklyCommitDto`, `weekId: number`, `onSaved: () => void`

Calls `useReconcileCommit` mutation on save.

- [ ] **Step 2: Create ReconcileCard component**

Side-by-side layout:
- **Left panel (planned)**: title, RCDO breadcrumb, chess badge, priority rank
- **Right panel (actual)**: ReconcileForm (if RECONCILING), or read-only reconciliation data (if RECONCILED)

If the commit already has reconciliation data, pre-fill the form.

- [ ] **Step 3: Create ReconcileList component**

Renders all commits as ReconcileCards. Shows a progress indicator: "3 of 5 reconciled".

- [ ] **Step 4: Create ReconciliationPage**

Full page:
1. Load current week via `useCurrentWeek()`
2. If status is LOCKED, show "Start Reconciliation" button
3. If status is RECONCILING, show ReconcileList
4. Week-level summary fields at bottom:
   - "What blocked you?" textarea (binds to blockersSummary)
   - "What should your manager know?" textarea (binds to managerNotes)
5. "Submit Reconciliation" button — calls `useReconcileWeek`, validates all commits have dispositions
6. If status is RECONCILED, show everything read-only + "Carry Forward" button
7. "Carry Forward" button calls `useCarryForward`, then navigates to `/my-week` showing the next week

- [ ] **Step 5: Wire up route in App.tsx**

Replace the placeholder with `<ReconciliationPage />`.

Also add navigation: after locking on PlanningPage, show a link/button to navigate to `/my-week/reconcile`.

- [ ] **Step 6: Verify TypeScript compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 7: Manual smoke test — full lifecycle**

With backend running:
1. Select Sarah Chen
2. Create 3 commits on My Week
3. Lock the week
4. Navigate to Reconciliation
5. Start Reconciliation
6. Set dispositions on all 3 commits (mark one as CARRIED_FORWARD)
7. Fill in blocker summary and manager notes
8. Submit Reconciliation
9. Click "Carry Forward"
10. Verify new week has 1 commit (the carried-forward one) with source_commit_id link

- [ ] **Step 8: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add frontend/src/
git commit -m "feat: add Reconciliation Page with planned vs actual comparison and carry-forward"
```

---

## Task 16: Frontend Manager Dashboard

**Files:**
- Create: `frontend/src/features/manager/ManagerDashboardPage.tsx`
- Create: `frontend/src/features/manager/ManagerDashboardPage.css`
- Create: `frontend/src/features/manager/TeamStatusGrid.tsx`
- Create: `frontend/src/features/manager/TeamStatusGrid.css`
- Create: `frontend/src/features/manager/MemberWeekView.tsx`
- Create: `frontend/src/features/manager/MemberWeekView.css`
- Modify: `frontend/src/App.tsx` — replace placeholder routes

- [ ] **Step 1: Create TeamStatusGrid component**

Table/grid showing team members:
- Columns: Name, Role, Week Status, Commits
- Each row shows the team member's current week status as a colored badge
- Members without a week show "Not Started" in gray
- Row is clickable — navigates to `/manager/team/:userId/:weekId`

Uses `useTeamWeeks()` hook.

- [ ] **Step 2: Create ManagerDashboardPage**

Simple page:
- Heading: "Team Dashboard"
- If current user is not MANAGER, show "Access denied" message
- Otherwise render TeamStatusGrid

- [ ] **Step 3: Create MemberWeekView component**

Read-only view of a team member's week:
- Uses `useTeamMemberWeek(userId, weekId)` hook
- Shows the same commit cards and reconciliation data as the IC views
- No edit/delete buttons, no status transition buttons
- Back button to return to `/manager/team`

- [ ] **Step 4: Wire up routes in App.tsx**

Replace placeholders with `<ManagerDashboardPage />` and `<MemberWeekView />`.

- [ ] **Step 5: Verify TypeScript compiles**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 6: Manual smoke test — manager flow**

1. Select Mike Johnson (Manager) from user switcher
2. Navigate to "Team" tab
3. Verify Sarah Chen and James Park appear with their week statuses
4. Click Sarah Chen's row to see her week detail
5. Verify the view is read-only
6. Navigate back to Team Dashboard

- [ ] **Step 7: Commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add frontend/src/
git commit -m "feat: add Manager Dashboard with team status grid and member week view"
```

---

## Task 17: Polish & End-to-End Verification

**Files:**
- Modify: Various CSS files for final styling adjustments

- [ ] **Step 1: Run full backend test suite**

```bash
cd /Users/rohanthomas/Code/weekly-planning/backend
./gradlew test -i
```

Expected: ALL PASS

- [ ] **Step 2: Run frontend TypeScript check**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npx tsc --noEmit
```

Expected: No errors

- [ ] **Step 3: Build frontend for production**

```bash
cd /Users/rohanthomas/Code/weekly-planning/frontend
npm run build
```

Expected: Build succeeds

- [ ] **Step 4: Full Docker Compose integration test**

```bash
cd /Users/rohanthomas/Code/weekly-planning
docker compose down -v
docker compose up --build
```

Verify all three services start and the app is accessible at http://localhost:5173.

- [ ] **Step 5: End-to-end demo walkthrough**

Run through the full demo flow:
1. Open app, select Sarah Chen (IC)
2. My Week shows DRAFT with empty commit list
3. Add 3-4 commits, each linked to different outcomes and chess categories
4. Drag to reorder priorities
5. Lock the week
6. Navigate to Reconciliation
7. Start reconciliation
8. Reconcile all commits — complete some, carry forward one
9. Submit reconciliation
10. Carry forward → verify next week has carried items
11. Switch to Mike Johnson (Manager)
12. Team dashboard shows Sarah's reconciled week
13. Click to view detail — read-only

- [ ] **Step 6: Fix any issues found during walkthrough**

Address any bugs, styling issues, or missing functionality discovered during the end-to-end test.

- [ ] **Step 7: Final commit**

```bash
cd /Users/rohanthomas/Code/weekly-planning
git add .
git commit -m "chore: polish and verify end-to-end demo flow"
```

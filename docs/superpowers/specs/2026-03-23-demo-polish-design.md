# Demo Polish — Design Spec

## Overview

Polish pass on the existing weekly planning app to make it demo-ready. Four areas: visual polish, dark mode, richer seed data, and reconciliation UX improvements. No new features or backend logic changes — only CSS refinements, a theme system, a seed migration, and frontend UX tweaks.

## 1. Visual Polish

### Form Controls

Replace all browser-default form controls with custom-styled versions using a global CSS approach.

**Selects:** Custom appearance with hidden native arrow, custom chevron via background-image SVG, 8px border-radius, 8px 12px padding, 1px solid #d1d5db border, font-size 14px.

**Textareas:** 8px border-radius, 8px 12px padding, font-family inherit, resize vertical only, placeholder color #9ca3af.

**Checkboxes:** Hide native checkbox, use a styled div with 18px square, 4px border-radius, blue border and background when checked, white checkmark.

**Inputs:** Match select styling — 8px border-radius, consistent padding, border color.

**Focus states:** All interactive elements get `outline: 2px solid #2563eb; outline-offset: 2px` on focus-visible.

### Empty States

Replace plain text empty states with structured empty state component:
- 48px icon container (rounded square, light blue background)
- Title text (15px, semibold)
- Description text (13px, gray)
- Optional CTA button

Used in:
- CommitList: clipboard icon, "No commits yet", "Plan your week by adding commits linked to strategy", "+ Add Your First Commit" button
- ReconcileList: checkmark icon, "No commits to reconcile"
- TeamStatusGrid: team icon, "No team members found"

### Read-Only States

When a week is RECONCILED, reconciliation data displays as static content instead of disabled form controls:
- Disposition shown as colored badge (same colors as active state)
- Percent complete as text next to badge
- Actual result and notes as quoted text on #f9fafb background with rounded corners
- No input elements rendered in read-only mode

### Card Hover & Transitions

- CommitCard: add `transition: box-shadow 0.15s ease, transform 0.15s ease` and on hover `box-shadow: 0 2px 8px rgba(0,0,0,0.08); transform: translateY(-1px)`
- Dropdown menus: fade-in via opacity transition (150ms)
- Modal: fade + slight scale animation on open (opacity 0→1, scale 0.95→1, 150ms)

### Loading States

Replace CSS spinner with skeleton placeholders:
- Card-shaped gray rectangles with shimmer animation
- Show 3 skeleton cards while loading
- Skeleton CSS: background linear-gradient animated left-to-right

### RCDO Chips

- Increase max-width from 180px to 220px
- Add `title` attribute with full text for tooltip on truncated items

## 2. Dark Mode

### Implementation

CSS custom properties (variables) defined on `:root` for light theme, overridden under `[data-theme="dark"]` on the `<html>` element.

### Color Tokens

| Token | Light | Dark |
|-------|-------|------|
| `--bg-page` | #fafafa | #0f172a |
| `--bg-card` | #ffffff | #1e293b |
| `--bg-card-hover` | #f9fafb | #253449 |
| `--bg-input` | #ffffff | #1e293b |
| `--bg-section-highlight` | #f0f9ff | #1a2744 |
| `--border-default` | #e5e7eb | #334155 |
| `--border-input` | #d1d5db | #475569 |
| `--text-primary` | #111827 | #f1f5f9 |
| `--text-secondary` | #6b7280 | #94a3b8 |
| `--text-tertiary` | #9ca3af | #64748b |
| `--color-primary` | #2563eb | #3b82f6 |
| `--bg-primary` | #2563eb | #3b82f6 |
| `--bg-overlay` | rgba(0,0,0,0.5) | rgba(0,0,0,0.7) |

### Badge Colors (Dark Mode Variants)

Chess categories keep the same hue but use darker backgrounds with lighter text:
- KING: bg #1e3a5f, text #93c5fd
- QUEEN: bg #5c1a1a, text #fca5a5
- ROOK: bg #5c4b1a, text #fde68a
- BISHOP: bg #1a4d2e, text #86efac
- KNIGHT: bg #4c1d6e, text #d8b4fe
- PAWN: bg #374151, text #d1d5db

Status badges:
- DRAFT: bg #5c4b1a, text #fde68a
- LOCKED: bg #1e3a5f, text #93c5fd
- RECONCILING: bg #5c2e1a, text #fdba74
- RECONCILED: bg #1a4d2e, text #86efac

### Toggle Button

Sun/moon icon button in the NavBar, between the nav links and the user switcher. 28px square, rounded, subtle border. Shows moon icon in light mode, sun icon in dark mode. Uses simple SVG or Unicode characters.

### Theme Logic

```
1. Check localStorage for 'wp_theme' key
2. If found, use that value ('light' or 'dark')
3. If not found, check window.matchMedia('(prefers-color-scheme: dark)')
4. Set data-theme attribute on <html>
5. On toggle: flip theme, save to localStorage, update attribute
```

Implemented as a `useTheme()` hook or small context. The toggle updates `document.documentElement.dataset.theme`.

### Migration Path

All existing hardcoded colors in CSS files must be replaced with `var(--token-name)` references. This is the bulk of the dark mode work — every CSS file needs to use the tokens.

## 3. Richer Seed Data

### New Migration: `V103__seed_demo_plans.sql`

Seeds two weeks of planning data for all four ICs. Uses fixed dates relative to the seeding (two weeks ago and one week ago) to avoid conflicting with the current week's auto-creation.

**ID resolution:** All references to users, rally cries, defining objectives, and outcomes must use subqueries rather than hard-coded integer IDs. Example: `(SELECT id FROM app_user WHERE email = 'sarah.chen@example.com')` instead of `1`. This ensures the migration is resilient to sequence changes.

**Self-referential FKs (carry-forward):** Week 2 commits that reference Week 1 commits via `source_commit_id` must use CTEs with `RETURNING id` to capture generated Week 1 commit IDs. This is the required approach because commit titles are not unique-constrained, making subquery-by-title lookup unsafe. Insert Week 1 commits in a CTE, capture their IDs, then reference those IDs when inserting Week 2 carry-forward commits.

**Note on user_id references in tables below:** The `(user_id=N)` annotations in the section headings are for human readability only. The actual SQL must use subqueries by email as specified above.

**Week dates:**
- Week 1: two weeks before current Monday (calculated as `CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14` for Monday, +4 for Friday)
- Week 2: one week before current Monday

### Week 1 (two weeks ago) — All RECONCILED

**Sarah Chen (user_id=1):**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Update dependency versions across services | PAWN | Platform Reliability / Eliminate SPOFs / Auth uptime 99.9% | COMPLETED | "Updated 12 packages, no breaking changes" |
| 2 | Fix flaky CI pipeline for auth service | PAWN | Platform Reliability / Eliminate SPOFs / Zero unplanned DB failovers | COMPLETED | "Root cause was race condition in test teardown" |
| 3 | Write runbook for database failover | PAWN | Platform Reliability / Eliminate SPOFs / Zero unplanned DB failovers | COMPLETED | "Documented in Confluence, shared with oncall" |
| 4 | Implement connection pooling for auth service | KING | Platform Reliability / Eliminate SPOFs / Auth uptime 99.9% | CARRIED_FORWARD | "Blocked — waiting on infra team to provision test environment" |
| 5 | Design circuit breaker pattern for payment calls | QUEEN | Revenue Growth / Streamline payments / Reduce payment failures 50% | CARRIED_FORWARD | "Started design doc, needs architecture review" |

Blockers summary: "Infra team backlog is blocking our critical path work. Test environment provisioning has been pending for 2 weeks."
Manager notes: "Most of my completed work was maintenance. The strategic items are stuck on external dependencies."

**James Park (user_id=2):**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Redesign auth token refresh flow | KING | Platform Reliability / Eliminate SPOFs / Auth uptime 99.9% | NOT_COMPLETED | "Design review got cancelled twice. Rescheduled for next week." |
| 2 | Build dashboard caching layer | QUEEN | Platform Reliability / Improve performance / Dashboard p95 < 500ms | NOT_COMPLETED | "Blocked — need decision on Redis vs Memcached from architecture team" |
| 3 | Fix N+1 query in user list endpoint | ROOK | Platform Reliability / Improve performance / API p99 < 200ms | COMPLETED | "Reduced query count from 47 to 2, response time down 80%" |
| 4 | Update API documentation for v2 endpoints | PAWN | Platform Reliability / Improve performance / API p99 < 200ms | COMPLETED | "All v2 endpoints documented in OpenAPI spec" |

Blockers summary: "Architecture team hasn't made caching decision. Design review keeps getting bumped."
Manager notes: "Frustrated — the important work keeps getting deprioritized by other teams' schedules."

**Lisa Wang (user_id=4):**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Build payment retry logic with exponential backoff | ROOK | Revenue Growth / Streamline payments / Reduce payment failures 50% | COMPLETED | "3 retry attempts with 1s/4s/16s delays. Failure rate down 30% in staging." |
| 2 | Add payment failure analytics dashboard | BISHOP | Revenue Growth / Streamline payments / Reduce payment failures 50% | COMPLETED | "Grafana dashboard with failure types, rates, and retry success rate" |
| 3 | Implement idempotency keys for payment API | KING | Revenue Growth / Streamline payments / Reduce payment failures 50% | COMPLETED | "UUID-based, stored in Redis with 24h TTL" |
| 4 | Write integration tests for payment retry flow | ROOK | Revenue Growth / Streamline payments / Reduce payment failures 50% | COMPLETED | "18 test cases covering all retry scenarios and edge cases" |
| 5 | Investigate Stripe webhook reliability | KNIGHT | Revenue Growth / Streamline payments / Reduce payment failures 50% | COMPLETED | "Found we're missing 0.3% of webhooks. Proposed polling fallback." |
| 6 | Optimize checkout page load time (stretch) | BISHOP | Revenue Growth / Streamline payments / Checkout conversion > 85% | COMPLETED | "Lazy-loaded payment form, saved 200ms on initial render" |

Blockers summary: "None — clean week."
Manager notes: "All 6 completed including stretch goal. Feeling productive."

**Alex Rivera (user_id=5):**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Set up monitoring alerts for self-serve signup flow | ROOK | Revenue Growth / Expand self-serve / Self-serve signup rate 60% | COMPLETED | "PagerDuty alerts for signup failure rate > 5%" |
| 2 | Research competitor onboarding flows | KNIGHT | Customer Delight / Redesign onboarding / Onboarding completion 80% | DROPPED | "Deprioritized — PM said to focus on signup metrics instead" |
| 3 | Build A/B test framework for signup page | QUEEN | Revenue Growth / Expand self-serve / Self-serve signup rate 60% | DROPPED | "Reorg moved A/B testing to growth team. No longer our scope." |

Blockers summary: "Team reorg mid-week shifted ownership of A/B testing and onboarding research to growth team."
Manager notes: "Lost 2 of 3 planned items to reorg. Need clarity on what Product team owns now."

### Week 2 (last week) — Mixed statuses

**Sarah Chen (user_id=1) — RECONCILED:**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Implement connection pooling for auth service | KING | Platform Reliability / Eliminate SPOFs / Auth uptime 99.9% | CARRIED_FORWARD | source_commit from week 1. "Infra team still hasn't provisioned. Escalated to engineering director." |
| 2 | Design circuit breaker pattern for payment calls | QUEEN | Revenue Growth / Streamline payments / Reduce payment failures 50% | CARRIED_FORWARD | source_commit from week 1. "Architecture review scheduled but cancelled again." |
| 3 | Migrate auth service config to environment variables | ROOK | Platform Reliability / Eliminate SPOFs / Auth uptime 99.9% | COMPLETED | "Removed all hardcoded config, using Spring profiles now" |
| 4 | Add health check endpoints to all platform services | PAWN | Platform Reliability / Improve performance / API p99 < 200ms | COMPLETED | "Added /health and /ready endpoints to 4 services" |

Blockers summary: "Same blockers as last week. Connection pooling and circuit breaker have been carried forward for 3 weeks now. Escalation in progress."
Manager notes: "I'm completing the work I can control but the high-priority items are stuck on other teams. This is becoming a pattern."

**James Park (user_id=2) — RECONCILED:**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Redesign auth token refresh flow | KING | Platform Reliability / Eliminate SPOFs / Auth uptime 99.9% | PARTIALLY_COMPLETED | "Design approved. Implementation 60% done. Need 1 more day." carry_forward=true |
| 2 | Build dashboard caching layer (Redis) | QUEEN | Platform Reliability / Improve performance / Dashboard p95 < 500ms | COMPLETED | "Architecture team chose Redis. Layer implemented, p95 down to 380ms." |
| 3 | Add request tracing headers to all services | ROOK | Platform Reliability / Improve performance / API p99 < 200ms | COMPLETED | "OpenTelemetry trace IDs propagated through all 6 services" |
| 4 | Optimize slow database queries in reporting module | BISHOP | Platform Reliability / Improve performance / Dashboard p95 < 500ms | COMPLETED | "Added indexes, rewrote 3 queries. Reporting 4x faster." |
| 5 | Investigate memory leak in notification service | KNIGHT | Platform Reliability / Eliminate SPOFs / Zero unplanned DB failovers | COMPLETED | "Found: unbounded event listener list. Fix deployed." |

Blockers summary: "Much better week. Caching decision unblocked the pipeline. Token refresh needs one more push."
Manager notes: "Best week in a month. 4 of 5 completed. Token refresh is close — should finish early next week."

**Lisa Wang (user_id=4) — LOCKED (last week, locked but not yet reconciled):**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Build payment webhook polling fallback | ROOK | Revenue Growth / Streamline payments / Reduce payment failures 50% | — | |
| 2 | Add Stripe payment method validation | BISHOP | Revenue Growth / Streamline payments / Reduce payment failures 50% | — | |
| 3 | Implement payment receipt email notifications | PAWN | Revenue Growth / Streamline payments / Checkout conversion > 85% | — | |
| 4 | Performance test payment flow under load | KNIGHT | Revenue Growth / Streamline payments / Reduce payment failures 50% | — | |

**Alex Rivera (user_id=5) — DRAFT:**

| # | Title | Chess | Rally Cry / DO / Outcome | Disposition | Notes |
|---|-------|-------|--------------------------|-------------|-------|
| 1 | Fix broken email validation on signup form | PAWN | Revenue Growth / Expand self-serve / Self-serve signup rate 60% | — | |
| 2 | Update signup page copy per marketing request | PAWN | Revenue Growth / Expand self-serve / Self-serve signup rate 60% | — | |

### Warning Signs This Data Reveals

1. **Chronic carry-forward:** Sarah's connection pooling and circuit breaker items have been stuck for 3 weeks
2. **Strategic work blocked:** James's most important items keep failing, only routine work ships
3. **Dangerous focus:** Lisa completes everything but only works on one Rally Cry — zero contribution to Platform Reliability despite being on Platform team
4. **Disengagement:** Alex planning minimal pawn-level work, reorg disruption unresolved
5. **Rally Cry imbalance:** "Customer Delight" has almost zero coverage across the team
6. **Chess distribution skew:** Too much Pawn/Rook work, not enough King/Queen strategic items
7. **External dependency pattern:** Multiple team members blocked by architecture and infra teams

## 4. Reconciliation UX Improvements

### Step Indicator

Horizontal 3-step progress bar at the top of ReconciliationPage:

```
[1. Review Commits] ——— [2. Week Summary] ——— [3. Submit]
```

- Steps are numbered circles connected by lines
- Current/completed steps are blue, upcoming steps are gray
- Step 1 is active when any commit is unreconciled
- Step 2 is active when all commits are reconciled
- Step 3 is active when all commits are reconciled (Step 3 is always reachable — the summary fields are optional, so Step 3 activates as soon as Step 1 is complete)
- Visual only — no separate pages, user scrolls through one page
- Step state is derived from frontend component state (commits reconciled count vs total, etc.) — no new backend field or endpoint

### Per-Commit Completion Indicators

Each ReconcileCard shows status:
- **Not reconciled:** amber left border, no checkmark
- **Reconciled (saved):** green left border, green checkmark icon in top-right corner
- Progress bar text: "3 of 5 reconciled" with filled progress bar

### Post-Lock Navigation

After successfully locking a week on PlanningPage:
- Show a success toast: "Week locked successfully"
- Toast includes an action button: "Start Reconciliation →"
- Clicking navigates to `/my-week/reconcile`

### Carry-Forward Preview

When clicking "Carry Forward" on a RECONCILED week:
- Instead of immediately executing, show a confirmation modal
- The list of carry-forward items is derived client-side by filtering `week.commits` where `reconciliation.carryForward === true` — no new backend endpoint needed
- Modal title: "Carry Forward to Next Week"
- List of items being carried: title + chess badge for each
- "These X items will be added to your next week's draft plan."
- Confirm / Cancel buttons

### Week Summary Section Polish

The blockersSummary and managerNotes textareas get a visual upgrade:
- Wrapped in a card with subtle background (uses `var(--bg-section-highlight)` — #f0f9ff light, #1a2744 dark)
- Section header: "Reflect on Your Week" with a thought bubble or pencil icon
- Textarea prompts:
  - "What prevented you from completing your plan?"
  - "What should your manager know about this week?"
- Visual grouping separates this section clearly from the commit reconciliation list above

## Scope Boundaries

**In scope:**
- All CSS changes for visual polish + dark mode
- Theme toggle and persistence
- One new Flyway seed migration
- Frontend UX tweaks to ReconciliationPage and PlanningPage
- Empty state component
- Skeleton loading component

**Out of scope:**
- New backend endpoints or business logic changes
- New features beyond what's described
- Mobile-responsive design
- Automated tests for visual changes
- Analytics or metrics dashboard

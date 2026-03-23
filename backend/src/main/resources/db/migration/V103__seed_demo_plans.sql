-- V103__seed_demo_plans.sql
-- Seeds two weeks of realistic demo planning data.
-- Week 1: two Mondays ago (all RECONCILED)
-- Week 2: last Monday (mixed statuses)

-- ============================================================
-- WEEK 1 — PLANNING WEEKS (all RECONCILED)
-- ============================================================
INSERT INTO planning_week (
    user_id, team_id, week_start_date, week_end_date,
    status,
    locked_at, blockers_summary, manager_notes,
    reconciling_at, reconciled_at
)
VALUES
-- Sarah Chen, Week 1
(
    (SELECT id FROM app_user WHERE email = 'sarah.chen@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'sarah.chen@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10,
    'RECONCILED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 11 AS TIMESTAMP) + INTERVAL '17' HOUR),
    'Infra team backlog is blocking our critical path work. Test environment provisioning has been pending for 2 weeks.',
    'Most of my completed work was maintenance. The strategic items are stuck on external dependencies.',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '9' HOUR),
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '11' HOUR)
),
-- James Park, Week 1
(
    (SELECT id FROM app_user WHERE email = 'james.park@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'james.park@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10,
    'RECONCILED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 11 AS TIMESTAMP) + INTERVAL '17' HOUR),
    'Architecture team hasn''t made caching decision. Design review keeps getting bumped.',
    'Frustrated — the important work keeps getting deprioritized by other teams'' schedules.',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '9' HOUR),
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '11' HOUR)
),
-- Lisa Wang, Week 1
(
    (SELECT id FROM app_user WHERE email = 'lisa.wang@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'lisa.wang@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10,
    'RECONCILED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 11 AS TIMESTAMP) + INTERVAL '17' HOUR),
    'None — clean week.',
    'All 6 completed including stretch goal. Feeling productive.',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '9' HOUR),
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '11' HOUR)
),
-- Alex Rivera, Week 1
(
    (SELECT id FROM app_user WHERE email = 'alex.rivera@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'alex.rivera@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10,
    'RECONCILED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 11 AS TIMESTAMP) + INTERVAL '17' HOUR),
    'Team reorg mid-week shifted ownership of A/B testing and onboarding research to growth team.',
    'Lost 2 of 3 planned items to reorg. Need clarity on what Product team owns now.',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '9' HOUR),
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 10 AS TIMESTAMP) + INTERVAL '11' HOUR)
);

-- ============================================================
-- WEEK 1 — COMMITS (with RETURNING to capture IDs for Week 2)
-- ============================================================

-- Sarah Chen Week 1 commits
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT id FROM planning_week
    WHERE user_id = (SELECT id FROM app_user WHERE email = 'sarah.chen@example.com')
      AND week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14
) pw,
(VALUES
    ('Update dependency versions across services',          'PAWN',  'Platform Reliability', 'Eliminate single points of failure', 'Auth uptime 99.9%',          1),
    ('Fix flaky CI pipeline for auth service',              'PAWN',  'Platform Reliability', 'Eliminate single points of failure', 'Zero unplanned DB failovers', 2),
    ('Write runbook for database failover',                 'PAWN',  'Platform Reliability', 'Eliminate single points of failure', 'Zero unplanned DB failovers', 3),
    ('Implement connection pooling for auth service',       'KING',  'Platform Reliability', 'Eliminate single points of failure', 'Auth uptime 99.9%',          4),
    ('Design circuit breaker pattern for payment calls',   'QUEEN', 'Revenue Growth',       'Streamline payments',               'Reduce payment failures 50%', 5)
) AS v(title, chess, rc, do_title, outcome, prank);

-- James Park Week 1 commits
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT id FROM planning_week
    WHERE user_id = (SELECT id FROM app_user WHERE email = 'james.park@example.com')
      AND week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14
) pw,
(VALUES
    ('Redesign auth token refresh flow',         'KING',   'Platform Reliability', 'Eliminate single points of failure', 'Auth uptime 99.9%',         1),
    ('Build dashboard caching layer',            'QUEEN',  'Platform Reliability', 'Improve performance',                'Dashboard p95 < 500ms',     2),
    ('Fix N+1 query in user list endpoint',      'ROOK',   'Platform Reliability', 'Improve performance',                'API p99 < 200ms',           3),
    ('Update API documentation for v2 endpoints','PAWN',   'Platform Reliability', 'Improve performance',                'API p99 < 200ms',           4)
) AS v(title, chess, rc, do_title, outcome, prank);

-- Lisa Wang Week 1 commits
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT id FROM planning_week
    WHERE user_id = (SELECT id FROM app_user WHERE email = 'lisa.wang@example.com')
      AND week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14
) pw,
(VALUES
    ('Build payment retry logic with exponential backoff', 'ROOK',   'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  1),
    ('Add payment failure analytics dashboard',            'BISHOP', 'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  2),
    ('Implement idempotency keys for payment API',         'KING',   'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  3),
    ('Write integration tests for payment retry flow',     'ROOK',   'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  4),
    ('Investigate Stripe webhook reliability',             'KNIGHT', 'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  5),
    ('Optimize checkout page load time',                   'BISHOP', 'Revenue Growth', 'Streamline payments', 'Checkout conversion > 85%',    6)
) AS v(title, chess, rc, do_title, outcome, prank);

-- Alex Rivera Week 1 commits
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT id FROM planning_week
    WHERE user_id = (SELECT id FROM app_user WHERE email = 'alex.rivera@example.com')
      AND week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14
) pw,
(VALUES
    ('Set up monitoring alerts for self-serve signup flow', 'ROOK',   'Revenue Growth', 'Expand self-serve', 'Self-serve signup rate 60%',    1),
    ('Research competitor onboarding flows',               'KNIGHT', 'Customer Delight','Redesign onboarding', 'Onboarding completion 80%',   2),
    ('Build A/B test framework for signup page',           'QUEEN',  'Revenue Growth', 'Expand self-serve', 'Self-serve signup rate 60%',    3)
) AS v(title, chess, rc, do_title, outcome, prank);

-- ============================================================
-- WEEK 1 — RECONCILIATIONS
-- ============================================================

-- Sarah Chen Week 1 reconciliations
INSERT INTO weekly_commit_reconciliation (
    weekly_commit_id, disposition, actual_result, percent_complete, carry_forward
)
SELECT
    wc.id,
    v.disposition,
    v.actual_result,
    v.pct,
    v.cf
FROM weekly_commit wc
JOIN planning_week pw ON pw.id = wc.planning_week_id
JOIN app_user u ON u.id = pw.user_id
JOIN (VALUES
    ('Update dependency versions across services',
     'COMPLETED', 'Updated 12 packages, no breaking changes', 100, FALSE),
    ('Fix flaky CI pipeline for auth service',
     'COMPLETED', 'Root cause was race condition in test teardown', 100, FALSE),
    ('Write runbook for database failover',
     'COMPLETED', 'Documented in Confluence, shared with oncall', 100, FALSE),
    ('Implement connection pooling for auth service',
     'CARRIED_FORWARD', 'Blocked — waiting on infra team to provision test environment', 10, TRUE),
    ('Design circuit breaker pattern for payment calls',
     'CARRIED_FORWARD', 'Started design doc, needs architecture review', 20, TRUE)
) AS v(title, disposition, actual_result, pct, cf)
  ON wc.title = v.title
WHERE u.email = 'sarah.chen@example.com'
  AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14;

-- James Park Week 1 reconciliations
INSERT INTO weekly_commit_reconciliation (
    weekly_commit_id, disposition, actual_result, percent_complete, carry_forward
)
SELECT
    wc.id,
    v.disposition,
    v.actual_result,
    v.pct,
    v.cf
FROM weekly_commit wc
JOIN planning_week pw ON pw.id = wc.planning_week_id
JOIN app_user u ON u.id = pw.user_id
JOIN (VALUES
    ('Redesign auth token refresh flow',
     'NOT_COMPLETED', 'Design review got cancelled twice. Rescheduled for next week.', 5, FALSE),
    ('Build dashboard caching layer',
     'NOT_COMPLETED', 'Blocked — need decision on Redis vs Memcached from architecture team', 15, FALSE),
    ('Fix N+1 query in user list endpoint',
     'COMPLETED', 'Reduced query count from 47 to 2, response time down 80%', 100, FALSE),
    ('Update API documentation for v2 endpoints',
     'COMPLETED', 'All v2 endpoints documented in OpenAPI spec', 100, FALSE)
) AS v(title, disposition, actual_result, pct, cf)
  ON wc.title = v.title
WHERE u.email = 'james.park@example.com'
  AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14;

-- Lisa Wang Week 1 reconciliations
INSERT INTO weekly_commit_reconciliation (
    weekly_commit_id, disposition, actual_result, percent_complete, carry_forward
)
SELECT
    wc.id,
    v.disposition,
    v.actual_result,
    v.pct,
    v.cf
FROM weekly_commit wc
JOIN planning_week pw ON pw.id = wc.planning_week_id
JOIN app_user u ON u.id = pw.user_id
JOIN (VALUES
    ('Build payment retry logic with exponential backoff',
     'COMPLETED', '3 retry attempts with 1s/4s/16s delays. Failure rate down 30% in staging.', 100, FALSE),
    ('Add payment failure analytics dashboard',
     'COMPLETED', 'Grafana dashboard with failure types, rates, and retry success rate', 100, FALSE),
    ('Implement idempotency keys for payment API',
     'COMPLETED', 'UUID-based, stored in Redis with 24h TTL', 100, FALSE),
    ('Write integration tests for payment retry flow',
     'COMPLETED', '18 test cases covering all retry scenarios and edge cases', 100, FALSE),
    ('Investigate Stripe webhook reliability',
     'COMPLETED', 'Found we''re missing 0.3% of webhooks. Proposed polling fallback.', 100, FALSE),
    ('Optimize checkout page load time',
     'COMPLETED', 'Lazy-loaded payment form, saved 200ms on initial render', 100, FALSE)
) AS v(title, disposition, actual_result, pct, cf)
  ON wc.title = v.title
WHERE u.email = 'lisa.wang@example.com'
  AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14;

-- Alex Rivera Week 1 reconciliations
INSERT INTO weekly_commit_reconciliation (
    weekly_commit_id, disposition, actual_result, percent_complete, carry_forward
)
SELECT
    wc.id,
    v.disposition,
    v.actual_result,
    v.pct,
    v.cf
FROM weekly_commit wc
JOIN planning_week pw ON pw.id = wc.planning_week_id
JOIN app_user u ON u.id = pw.user_id
JOIN (VALUES
    ('Set up monitoring alerts for self-serve signup flow',
     'COMPLETED', 'PagerDuty alerts for signup failure rate > 5%', 100, FALSE),
    ('Research competitor onboarding flows',
     'DROPPED', 'Deprioritized — PM said to focus on signup metrics instead', 0, FALSE),
    ('Build A/B test framework for signup page',
     'DROPPED', 'Reorg moved A/B testing to growth team. No longer our scope.', 0, FALSE)
) AS v(title, disposition, actual_result, pct, cf)
  ON wc.title = v.title
WHERE u.email = 'alex.rivera@example.com'
  AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14;

-- ============================================================
-- WEEK 2 — PLANNING WEEKS (mixed statuses)
-- ============================================================
INSERT INTO planning_week (
    user_id, team_id, week_start_date, week_end_date,
    status,
    locked_at, blockers_summary, manager_notes,
    reconciling_at, reconciled_at
)
VALUES
-- Sarah Chen, Week 2 — RECONCILED
(
    (SELECT id FROM app_user WHERE email = 'sarah.chen@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'sarah.chen@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3,
    'RECONCILED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 4 AS TIMESTAMP) + INTERVAL '17' HOUR),
    'Same blockers as last week. Connection pooling and circuit breaker have been carried forward for 3 weeks now. Escalation in progress.',
    'I''m completing the work I can control but the high-priority items are stuck on other teams. This is becoming a pattern.',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3 AS TIMESTAMP) + INTERVAL '9' HOUR),
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3 AS TIMESTAMP) + INTERVAL '11' HOUR)
),
-- James Park, Week 2 — RECONCILED
(
    (SELECT id FROM app_user WHERE email = 'james.park@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'james.park@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3,
    'RECONCILED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 4 AS TIMESTAMP) + INTERVAL '17' HOUR),
    'Much better week. Caching decision unblocked the pipeline. Token refresh needs one more push.',
    'Best week in a month. 4 of 5 completed. Token refresh is close — should finish early next week.',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3 AS TIMESTAMP) + INTERVAL '9' HOUR),
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3 AS TIMESTAMP) + INTERVAL '11' HOUR)
),
-- Lisa Wang, Week 2 — LOCKED
(
    (SELECT id FROM app_user WHERE email = 'lisa.wang@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'lisa.wang@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3,
    'LOCKED',
    (CAST(CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7 AS TIMESTAMP) + INTERVAL '9' HOUR),
    NULL,
    NULL,
    NULL,
    NULL
),
-- Alex Rivera, Week 2 — DRAFT
(
    (SELECT id FROM app_user WHERE email = 'alex.rivera@example.com'),
    (SELECT team_id FROM app_user WHERE email = 'alex.rivera@example.com'),
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7,
    CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 3,
    'DRAFT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
);

-- ============================================================
-- WEEK 2 — COMMITS
-- Sarah's first 2 commits reference Week 1 source commits
-- ============================================================

-- Sarah Chen Week 2 commits (priorities 1 and 2 carry forward from Week 1)
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
VALUES
(
    (SELECT pw.id FROM planning_week pw
     JOIN app_user u ON u.id = pw.user_id
     WHERE u.email = 'sarah.chen@example.com'
       AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7),
    'Implement connection pooling for auth service',
    'KING',
    (SELECT id FROM rally_cry WHERE title = 'Platform Reliability'),
    (SELECT id FROM defining_objective WHERE title = 'Eliminate single points of failure'),
    (SELECT id FROM outcome WHERE title = 'Auth uptime 99.9%'),
    1, FALSE,
    (SELECT wc.id FROM weekly_commit wc
     JOIN planning_week pw ON pw.id = wc.planning_week_id
     JOIN app_user u ON u.id = pw.user_id
     WHERE u.email = 'sarah.chen@example.com'
       AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14
       AND wc.title = 'Implement connection pooling for auth service')
),
(
    (SELECT pw.id FROM planning_week pw
     JOIN app_user u ON u.id = pw.user_id
     WHERE u.email = 'sarah.chen@example.com'
       AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7),
    'Design circuit breaker pattern for payment calls',
    'QUEEN',
    (SELECT id FROM rally_cry WHERE title = 'Revenue Growth'),
    (SELECT id FROM defining_objective WHERE title = 'Streamline payments'),
    (SELECT id FROM outcome WHERE title = 'Reduce payment failures 50%'),
    2, FALSE,
    (SELECT wc.id FROM weekly_commit wc
     JOIN planning_week pw ON pw.id = wc.planning_week_id
     JOIN app_user u ON u.id = pw.user_id
     WHERE u.email = 'sarah.chen@example.com'
       AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 14
       AND wc.title = 'Design circuit breaker pattern for payment calls')
),
(
    (SELECT pw.id FROM planning_week pw
     JOIN app_user u ON u.id = pw.user_id
     WHERE u.email = 'sarah.chen@example.com'
       AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7),
    'Migrate auth service config to environment variables',
    'ROOK',
    (SELECT id FROM rally_cry WHERE title = 'Platform Reliability'),
    (SELECT id FROM defining_objective WHERE title = 'Eliminate single points of failure'),
    (SELECT id FROM outcome WHERE title = 'Auth uptime 99.9%'),
    3, FALSE, NULL
),
(
    (SELECT pw.id FROM planning_week pw
     JOIN app_user u ON u.id = pw.user_id
     WHERE u.email = 'sarah.chen@example.com'
       AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7),
    'Add health check endpoints to all platform services',
    'PAWN',
    (SELECT id FROM rally_cry WHERE title = 'Platform Reliability'),
    (SELECT id FROM defining_objective WHERE title = 'Improve performance'),
    (SELECT id FROM outcome WHERE title = 'API p99 < 200ms'),
    4, FALSE, NULL
);

-- James Park Week 2 commits
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT pw.id FROM planning_week pw
    JOIN app_user u ON u.id = pw.user_id
    WHERE u.email = 'james.park@example.com'
      AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7
) pw,
(VALUES
    ('Redesign auth token refresh flow',                  'KING',   'Platform Reliability', 'Eliminate single points of failure', 'Auth uptime 99.9%',     1),
    ('Build dashboard caching layer (Redis)',             'QUEEN',  'Platform Reliability', 'Improve performance',                'Dashboard p95 < 500ms', 2),
    ('Add request tracing headers to all services',      'ROOK',   'Platform Reliability', 'Improve performance',                'API p99 < 200ms',       3),
    ('Optimize slow database queries in reporting module','BISHOP', 'Platform Reliability', 'Improve performance',                'Dashboard p95 < 500ms', 4),
    ('Investigate memory leak in notification service',  'KNIGHT', 'Platform Reliability', 'Eliminate single points of failure', 'Zero unplanned DB failovers', 5)
) AS v(title, chess, rc, do_title, outcome, prank);

-- Lisa Wang Week 2 commits (LOCKED — no reconciliation)
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT pw.id FROM planning_week pw
    JOIN app_user u ON u.id = pw.user_id
    WHERE u.email = 'lisa.wang@example.com'
      AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7
) pw,
(VALUES
    ('Build payment webhook polling fallback',  'ROOK',   'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  1),
    ('Add Stripe payment method validation',    'BISHOP', 'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  2),
    ('Implement payment receipt email notifications', 'PAWN', 'Revenue Growth', 'Streamline payments', 'Checkout conversion > 85%', 3),
    ('Performance test payment flow under load','KNIGHT', 'Revenue Growth', 'Streamline payments', 'Reduce payment failures 50%',  4)
) AS v(title, chess, rc, do_title, outcome, prank);

-- Alex Rivera Week 2 commits (DRAFT — no reconciliation)
INSERT INTO weekly_commit (
    planning_week_id, title, chess_category_code,
    rally_cry_id, defining_objective_id, outcome_id,
    priority_rank, stretch, source_commit_id
)
SELECT
    pw.id,
    v.title,
    v.chess,
    (SELECT id FROM rally_cry WHERE title = v.rc),
    (SELECT id FROM defining_objective WHERE title = v.do_title),
    (SELECT id FROM outcome WHERE title = v.outcome),
    v.prank,
    FALSE,
    NULL
FROM (
    SELECT pw.id FROM planning_week pw
    JOIN app_user u ON u.id = pw.user_id
    WHERE u.email = 'alex.rivera@example.com'
      AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7
) pw,
(VALUES
    ('Fix broken email validation on signup form',    'PAWN', 'Revenue Growth', 'Expand self-serve', 'Self-serve signup rate 60%', 1),
    ('Update signup page copy per marketing request', 'PAWN', 'Revenue Growth', 'Expand self-serve', 'Self-serve signup rate 60%', 2)
) AS v(title, chess, rc, do_title, outcome, prank);

-- ============================================================
-- WEEK 2 — RECONCILIATIONS (Sarah and James only)
-- ============================================================

-- Sarah Chen Week 2 reconciliations
INSERT INTO weekly_commit_reconciliation (
    weekly_commit_id, disposition, actual_result, percent_complete, carry_forward
)
SELECT
    wc.id,
    v.disposition,
    v.actual_result,
    v.pct,
    v.cf
FROM weekly_commit wc
JOIN planning_week pw ON pw.id = wc.planning_week_id
JOIN app_user u ON u.id = pw.user_id
JOIN (VALUES
    ('Implement connection pooling for auth service',
     'CARRIED_FORWARD', 'Infra team still hasn''t provisioned. Escalated to engineering director.', 15, TRUE),
    ('Design circuit breaker pattern for payment calls',
     'CARRIED_FORWARD', 'Architecture review scheduled but cancelled again.', 25, TRUE),
    ('Migrate auth service config to environment variables',
     'COMPLETED', 'Removed all hardcoded config, using Spring profiles now', 100, FALSE),
    ('Add health check endpoints to all platform services',
     'COMPLETED', 'Added /health and /ready endpoints to 4 services', 100, FALSE)
) AS v(title, disposition, actual_result, pct, cf)
  ON wc.title = v.title
WHERE u.email = 'sarah.chen@example.com'
  AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7;

-- James Park Week 2 reconciliations
INSERT INTO weekly_commit_reconciliation (
    weekly_commit_id, disposition, actual_result, percent_complete, carry_forward
)
SELECT
    wc.id,
    v.disposition,
    v.actual_result,
    v.pct,
    v.cf
FROM weekly_commit wc
JOIN planning_week pw ON pw.id = wc.planning_week_id
JOIN app_user u ON u.id = pw.user_id
JOIN (VALUES
    ('Redesign auth token refresh flow',
     'PARTIALLY_COMPLETED', 'Design approved. Implementation 60% done. Need 1 more day.', 60, TRUE),
    ('Build dashboard caching layer (Redis)',
     'COMPLETED', 'Architecture team chose Redis. Layer implemented, p95 down to 380ms.', 100, FALSE),
    ('Add request tracing headers to all services',
     'COMPLETED', 'OpenTelemetry trace IDs propagated through all 6 services', 100, FALSE),
    ('Optimize slow database queries in reporting module',
     'COMPLETED', 'Added indexes, rewrote 3 queries. Reporting 4x faster.', 100, FALSE),
    ('Investigate memory leak in notification service',
     'COMPLETED', 'Found: unbounded event listener list. Fix deployed.', 100, FALSE)
) AS v(title, disposition, actual_result, pct, cf)
  ON wc.title = v.title
WHERE u.email = 'james.park@example.com'
  AND pw.week_start_date = CURRENT_DATE - ((EXTRACT(DOW FROM CURRENT_DATE)::int + 6) % 7) - 7;

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

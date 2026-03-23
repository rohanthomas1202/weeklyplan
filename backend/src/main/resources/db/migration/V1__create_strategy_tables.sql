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

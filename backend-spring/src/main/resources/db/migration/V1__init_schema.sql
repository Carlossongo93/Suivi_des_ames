-- V1__init_schema.sql
-- Script de création initial pour PostgreSQL (Cloud)

CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- ADMIN, LEADER, MEMBER
    team_id INT REFERENCES teams(id) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contacts (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    address TEXT,
    team_id INT REFERENCES teams(id) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE interactions (
    id SERIAL PRIMARY KEY,
    contact_id INT REFERENCES contacts(id) NOT NULL,
    user_id INT REFERENCES users(id) NOT NULL,
    type VARCHAR(50) NOT NULL, -- CALL, VISIT, MESSAGE
    notes TEXT,
    health_score INT CHECK (health_score >= 1 AND health_score <= 5),
    interaction_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pour optimiser les requêtes filtrées par équipe (Axe B de notre ADD)
CREATE INDEX idx_contacts_team_id ON contacts(team_id);
CREATE INDEX idx_users_team_id ON users(team_id);
CREATE INDEX idx_interactions_contact_id ON interactions(contact_id);
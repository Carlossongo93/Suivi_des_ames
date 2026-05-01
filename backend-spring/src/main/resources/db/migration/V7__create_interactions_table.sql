-- 1. Suppression de la table "brouillon" générée lors de la V1
DROP TABLE IF EXISTS interactions;

-- 2. Ajout de la date de dernière interaction sur la table des contacts
ALTER TABLE contacts ADD COLUMN last_interaction_at TIMESTAMP;

-- 3. Création de la table des interactions (Version Finale)
CREATE TABLE interactions (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    sub_type VARCHAR(100),
    notes TEXT,
    contact_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interaction_contact FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE CASCADE,
    CONSTRAINT fk_interaction_user FOREIGN KEY (created_by) REFERENCES users(id)
);
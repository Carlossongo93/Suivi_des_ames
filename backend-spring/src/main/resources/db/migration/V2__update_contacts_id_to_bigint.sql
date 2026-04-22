ALTER TABLE contacts ALTER COLUMN team_id TYPE bigint;
ALTER TABLE users ALTER COLUMN team_id TYPE bigint;
ALTER TABLE interactions ALTER COLUMN contact_id TYPE bigint;
ALTER TABLE interactions ALTER COLUMN user_id TYPE bigint;
ALTER TABLE teams ALTER COLUMN id TYPE bigint;
ALTER TABLE users ALTER COLUMN id TYPE bigint;
ALTER TABLE contacts ALTER COLUMN id TYPE bigint;
ALTER TABLE interactions ALTER COLUMN id TYPE bigint;
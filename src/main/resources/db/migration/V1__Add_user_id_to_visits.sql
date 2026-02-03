-- Add user_id column to visits table
ALTER TABLE visits
ADD COLUMN user_id BIGINT NOT NULL DEFAULT 1;

-- Add foreign key constraint
ALTER TABLE visits
ADD CONSTRAINT fk_visits_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

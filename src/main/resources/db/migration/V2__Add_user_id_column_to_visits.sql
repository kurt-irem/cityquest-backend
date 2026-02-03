-- Add user_id column to visits table
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.columns 
        WHERE table_name = 'visits' AND column_name = 'user_id'
    ) THEN
        ALTER TABLE visits ADD COLUMN user_id BIGINT NOT NULL DEFAULT 1;
        
        ALTER TABLE visits
        ADD CONSTRAINT fk_visits_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END
$$;

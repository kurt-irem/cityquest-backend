-- Alter the image column in visits table from VARCHAR(255) to TEXT
ALTER TABLE visits
ALTER COLUMN image TYPE TEXT;

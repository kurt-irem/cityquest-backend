-- Add image column to places for base64 or URL storage
ALTER TABLE places
ADD COLUMN IF NOT EXISTS image TEXT;
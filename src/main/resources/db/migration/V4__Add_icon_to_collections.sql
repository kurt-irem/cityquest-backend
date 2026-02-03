ALTER TABLE collections ADD COLUMN IF NOT EXISTS icon VARCHAR(100);
UPDATE collections SET icon = COALESCE(icon, 'bookmark');

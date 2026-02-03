CREATE TABLE IF NOT EXISTS achievement_goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    month_key VARCHAR(7) NOT NULL,
    goal INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_achievement_goal_user_month UNIQUE (user_id, month_key)
);

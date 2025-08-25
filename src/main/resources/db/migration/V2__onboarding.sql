CREATE TABLE IF NOT EXISTS user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    height_cm INTEGER NOT NULL CHECK (height_cm BETWEEN 80 AND 250),
    weight_kg INTEGER NOT NULL CHECK (weight_kg BETWEEN 20 AND 400),
    body_fat_percent INTEGER NULL CHECK (body_fat_percent BETWEEN 10 AND 100),
    activity_level VARCHAR(32) NOT NULL,
    sleep_hours INTEGER NULL CHECK (sleep_hours BETWEEN 0 AND 24),
    diet_notes TEXT NULL,
    smoking BOOLEAN NULL,
    alcohol BOOLEAN NULL
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON user_profiles (user_id);

CREATE TABLE IF NOT EXISTS goals (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    goal_type VARCHAR(32) NOT NULL,
    target_weight_kg INTEGER NULL CHECK (target_weight_kg BETWEEN 20 AND 400),
    target_timeline_weeks INTEGER NULL CHECK (target_timeline_weeks BETWEEN 1 AND 104),
    plan_detail_level VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_goals_user_id ON goals (user_id);

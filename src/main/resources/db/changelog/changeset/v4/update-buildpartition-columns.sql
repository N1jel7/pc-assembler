ALTER TABLE component_types RENAME COLUMN parent_type TO max_amount;

ALTER TABLE component_types
    ALTER COLUMN max_amount TYPE INTEGER,
    ALTER COLUMN max_amount SET NOT NULL,
    ALTER COLUMN max_amount SET DEFAULT 1,
    ADD COLUMN min_amount INTEGER NOT NULL DEFAULT 1;
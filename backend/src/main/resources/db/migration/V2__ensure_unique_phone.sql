-- V2: Ensure phone uniqueness (safe check)
-- This migration will fail with a helpful message if duplicate phone numbers exist.

DO $$
BEGIN
    IF EXISTS (
        SELECT phone FROM users WHERE phone IS NOT NULL GROUP BY phone HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Cannot add unique constraint on users.phone: duplicate phone numbers exist. Please remove duplicates before applying this migration.';
    END IF;
END
$$;

-- If no duplicates were found, create a unique index on users.phone (IF NOT EXISTS for safety)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone_unique ON users (phone);

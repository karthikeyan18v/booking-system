-- Use TIMESTAMPTZ so PostgreSQL correctly stores all times as UTC
ALTER TABLE sessions
    ALTER COLUMN start_time TYPE TIMESTAMPTZ USING start_time AT TIME ZONE 'UTC',
    ALTER COLUMN end_time   TYPE TIMESTAMPTZ USING end_time   AT TIME ZONE 'UTC';

ALTER TABLE teachers
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE parents
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE courses
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE offerings
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE sessions
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC';

ALTER TABLE bookings
    ALTER COLUMN booked_at TYPE TIMESTAMPTZ USING booked_at AT TIME ZONE 'UTC';

-- DB-level guard: one confirmed booking per parent per offering
ALTER TABLE bookings
    ADD CONSTRAINT uq_bookings_parent_offering UNIQUE (parent_id, offering_id);

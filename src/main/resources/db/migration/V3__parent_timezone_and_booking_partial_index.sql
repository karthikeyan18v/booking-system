-- Drop the overly broad unique constraint added in V2.
-- It blocks a parent from rebooking an offering they previously cancelled.
ALTER TABLE bookings DROP CONSTRAINT IF EXISTS uq_bookings_parent_offering;

-- Replace with a partial unique index: only one CONFIRMED booking per parent per offering.
-- Cancelled bookings do not count, so a parent can rebook after cancellation.
CREATE UNIQUE INDEX uq_bookings_confirmed_per_parent_offering
    ON bookings(parent_id, offering_id)
    WHERE status = 'CONFIRMED';

-- Parents now store their preferred timezone (default UTC for existing rows).
ALTER TABLE parents ADD COLUMN IF NOT EXISTS timezone VARCHAR(100) NOT NULL DEFAULT 'UTC';

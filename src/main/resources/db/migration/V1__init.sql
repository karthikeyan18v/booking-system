CREATE TABLE teachers (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    timezone    VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE parents (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE courses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE offerings (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id   UUID         NOT NULL REFERENCES courses(id),
    teacher_id  UUID         NOT NULL REFERENCES teachers(id),
    title       VARCHAR(255) NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE sessions (
    id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    offering_id UUID      NOT NULL REFERENCES offerings(id),
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_session_times CHECK (end_time > start_time)
);

CREATE TABLE bookings (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    parent_id   UUID        NOT NULL REFERENCES parents(id),
    offering_id UUID        NOT NULL REFERENCES offerings(id),
    status      VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    booked_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sessions_offering  ON sessions(offering_id);
CREATE INDEX idx_sessions_times     ON sessions(start_time, end_time);
CREATE INDEX idx_bookings_parent    ON bookings(parent_id);
CREATE INDEX idx_bookings_offering  ON bookings(offering_id);

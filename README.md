# Global Class Offering Booking System

A production-ready backend service for a global live-learning platform where teachers conduct online classes for students across different countries and timezones.

---

## Project Overview

Teachers create **courses**, then schedule **offerings** (sections) with multiple **sessions**. Parents browse available offerings and **book** them. The system enforces:

- Booking at the offering level (all sessions booked together)
- Time-conflict detection across all of a parent's existing bookings
- Concurrent booking safety via pessimistic database locking
- Full timezone conversion — sessions stored in UTC, displayed in any IANA timezone

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.0 |
| Database | PostgreSQL 15+ |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |

---

## Database Schema Overview

```
teachers        — id, name, email (unique), timezone, created_at
parents         — id, name, email (unique), timezone, created_at
courses         — id, name, description, created_at
offerings       — id, course_id (FK), teacher_id (FK), title, status, created_at
sessions        — id, offering_id (FK), start_time (TIMESTAMPTZ), end_time (TIMESTAMPTZ), created_at
bookings        — id, parent_id (FK), offering_id (FK), status, booked_at
```

### Key constraints
- `sessions.end_time > sessions.start_time` — enforced by CHECK constraint
- `sessions.start_time / end_time` — stored as `TIMESTAMPTZ` (always UTC in PostgreSQL)
- `bookings(parent_id, offering_id) WHERE status = 'CONFIRMED'` — partial unique index prevents duplicate active bookings while allowing rebooking after cancellation

### Indexes
```sql
idx_sessions_offering  ON sessions(offering_id)
idx_sessions_times     ON sessions(start_time, end_time)
idx_bookings_parent    ON bookings(parent_id)
idx_bookings_offering  ON bookings(offering_id)
```

### Migrations (applied in order by Flyway)
| File | Description |
|---|---|
| `V1__init.sql` | Initial schema — all tables, FK constraints, indexes |
| `V2__fix_timestamptz_and_booking_unique.sql` | Convert timestamps to TIMESTAMPTZ |
| `V3__parent_timezone_and_booking_partial_index.sql` | Add parent timezone, replace broad unique with partial index |

---

## Environment Variables / Configuration

All configuration lives in `src/main/resources/application.yaml`.

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/booking_system` | PostgreSQL JDBC URL |
| `spring.datasource.username` | `postgres` | DB username |
| `spring.datasource.password` | `postgres` | DB password |
| `server.port` | `8080` | HTTP port |

To override without editing the file, use environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/booking_system
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=yourpassword
```

---

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/booking-system.git
cd booking-system
```

### 2. Create the database

```sql
CREATE DATABASE booking_system;
```

### 3. Configure credentials (if different from defaults)

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/booking_system
    username: postgres
    password: yourpassword
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

Flyway will automatically apply all migrations on startup.

### 5. Verify it started

```
http://localhost:8080/swagger-ui/index.html
```

---

## Steps to Run the Application Locally

```bash
# 1. Start PostgreSQL (if using Docker)
docker run --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=booking_system -p 5432:5432 -d postgres:15

# 2. Clone and build
git clone https://github.com/YOUR_USERNAME/booking-system.git
cd booking-system
./mvnw clean package -DskipTests

# 3. Run
./mvnw spring-boot:run

# 4. Open Swagger UI
open http://localhost:8080/swagger-ui/index.html
```

---

## API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON spec:
```
http://localhost:8080/v3/api-docs
```

### Endpoint Summary

#### Teacher APIs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/teachers` | Register a teacher |
| GET | `/api/teachers/{teacherId}/offerings` | View teacher's offerings (in teacher's timezone) |

#### Course APIs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/courses` | Create a course (teacher only — requires `teacherId` in body) |
| GET | `/api/courses` | List all courses |

#### Offering APIs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/offerings` | Create an offering |
| POST | `/api/offerings/{offeringId}/sessions` | Add sessions to an offering |
| GET | `/api/offerings?timezone=` | Browse active offerings |
| DELETE | `/api/offerings/{offeringId}` | Cancel offering (cascades to bookings) |

#### Parent APIs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/parents` | Register a parent/student |
| GET | `/api/parents/{parentId}/bookings` | View parent's bookings |

#### Booking APIs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/bookings?timezone=` | Book an offering |

---

## Assumptions Made

1. **No authentication** — The assignment focuses on core booking logic. In production, JWT-based auth would gate teacher vs parent endpoints.
2. **Teacher creates courses** — `POST /api/courses` requires a `teacherId` in the body to enforce that only registered teachers create courses.
3. **Offerings can have sessions added incrementally** — Sessions can be added to an offering at any time before it is cancelled.
4. **Booking requires at least one session** — An offering with zero sessions cannot be booked.
5. **Parent can rebook after cancellation** — If a teacher cancels an offering and re-creates it, the parent is free to book again. The partial unique index on bookings allows this.
6. **Timezone is mandatory for both teachers and parents** — This ensures session times are always interpreted and displayed correctly without falling back to server time.
7. **Sessions are always stored in UTC** — The `TIMESTAMPTZ` column type in PostgreSQL ensures this.
8. **Cancelling an offering cascade-cancels all confirmed bookings** — This is done via a single bulk UPDATE query, not row-by-row.

---

## Concurrency Handling Approach

The system uses **pessimistic write locking** at the database level to prevent race conditions.

### Problem
Two parents booking simultaneously, or the same parent submitting two requests at the same time, could both pass the conflict check before either commits — resulting in a double-booking.

### Solution

When a booking request arrives, the first thing `BookingService` does is:

```java
Parent parent = parentRepository.findByIdForUpdate(request.getParentId())
```

This executes:
```sql
SELECT * FROM parents WHERE id = ? FOR UPDATE
```

This places an **exclusive row lock** on the parent record for the duration of the transaction. Any concurrent booking request for the same parent blocks at this line until the first transaction commits or rolls back. This serialises all booking attempts per parent, making conflict detection safe under concurrency.

### Additional safety net
A **partial unique index** on `bookings(parent_id, offering_id) WHERE status = 'CONFIRMED'` provides a database-level guard against duplicate confirmed bookings, even if application logic is somehow bypassed.

---

## Timezone Handling Approach

### Storage
All session times are stored as `TIMESTAMPTZ` in PostgreSQL, which normalises everything to UTC internally.

### Teacher input
When a teacher adds sessions, they provide times as plain local date-time strings (e.g. `2025-06-07T18:00:00`). The system reads the teacher's stored `timezone` field and converts:

```java
Instant utc = LocalDateTime.parse(input).atZone(teacherZone).toInstant();
```

### Parent / viewer output
On any read (browse offerings, view bookings), the caller can pass `?timezone=America/New_York`. The system converts each UTC instant to that zone before returning:

```java
String local = utcInstant.atZone(requestedZone).format(ISO_LOCAL_DATE_TIME);
```

If no timezone is passed, the parent's stored timezone is used as the default.

### Conflict detection
Conflict detection operates entirely in UTC (comparing `Instant` values), so there are no DST or offset edge cases — two sessions overlap if and only if `startA < endB AND endA > startB`, evaluated in UTC.

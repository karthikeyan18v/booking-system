package com.task.bookingsystem.repository;

import com.task.bookingsystem.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    List<Session> findByOfferingId(UUID offeringId);

    @Query("""
        SELECT COUNT(s) FROM Session s
        WHERE s.offering.id IN (
            SELECT b.offering.id FROM Booking b
            WHERE b.parent.id = :parentId AND b.status = com.task.bookingsystem.entity.BookingStatus.CONFIRMED
        )
        AND s.startTime < :endTime
        AND s.endTime > :startTime
    """)
    long countConflicts(@Param("parentId") UUID parentId,
                        @Param("startTime") Instant startTime,
                        @Param("endTime") Instant endTime);
}

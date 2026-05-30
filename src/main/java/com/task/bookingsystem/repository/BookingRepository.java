package com.task.bookingsystem.repository;

import com.task.bookingsystem.entity.Booking;
import com.task.bookingsystem.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    boolean existsByParentIdAndOfferingIdAndStatus(UUID parentId, UUID offeringId, BookingStatus status);

    // JOIN FETCH sessions eliminates N+1 when rendering booking responses
    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.offering o
        JOIN FETCH o.course
        JOIN FETCH o.teacher
        LEFT JOIN FETCH o.sessions
        WHERE b.parent.id = :parentId
    """)
    List<Booking> findByParentId(@Param("parentId") UUID parentId);

    @Modifying
    @Query("""
        UPDATE Booking b SET b.status = com.task.bookingsystem.entity.BookingStatus.CANCELLED
        WHERE b.offering.id = :offeringId
        AND b.status = com.task.bookingsystem.entity.BookingStatus.CONFIRMED
    """)
    void cancelConfirmedByOfferingId(@Param("offeringId") UUID offeringId);
}

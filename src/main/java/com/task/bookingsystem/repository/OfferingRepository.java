package com.task.bookingsystem.repository;

import com.task.bookingsystem.entity.Offering;
import com.task.bookingsystem.entity.OfferingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OfferingRepository extends JpaRepository<Offering, UUID> {

    // DISTINCT prevents row duplication when an offering has multiple sessions
    @Query("""
        SELECT DISTINCT o FROM Offering o
        JOIN FETCH o.course
        JOIN FETCH o.teacher
        LEFT JOIN FETCH o.sessions
        WHERE o.teacher.id = :teacherId
    """)
    List<Offering> findByTeacherId(@Param("teacherId") UUID teacherId);

    @Query("""
        SELECT DISTINCT o FROM Offering o
        JOIN FETCH o.course
        JOIN FETCH o.teacher
        LEFT JOIN FETCH o.sessions
        WHERE o.status = :status
    """)
    List<Offering> findByStatus(@Param("status") OfferingStatus status);
}

package com.task.bookingsystem.service;

import com.task.bookingsystem.dto.request.BookOfferingRequest;
import com.task.bookingsystem.dto.response.BookingResponse;
import com.task.bookingsystem.dto.response.CourseResponse;
import com.task.bookingsystem.dto.response.OfferingResponse;
import com.task.bookingsystem.dto.response.SessionResponse;
import com.task.bookingsystem.entity.*;
import com.task.bookingsystem.exception.ConflictException;
import com.task.bookingsystem.exception.ResourceNotFoundException;
import com.task.bookingsystem.repository.*;
import com.task.bookingsystem.util.TimezoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParentRepository parentRepository;
    private final OfferingRepository offeringRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public BookingResponse bookOffering(BookOfferingRequest request, String timezoneOverride) {
        // Pessimistic write lock on parent prevents concurrent overlapping bookings from the same parent
        Parent parent = parentRepository.findByIdForUpdate(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        Offering offering = offeringRepository.findById(request.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (offering.getStatus() == OfferingStatus.CANCELLED) {
            throw new ConflictException("Offering is not available for booking");
        }

        if (bookingRepository.existsByParentIdAndOfferingIdAndStatus(
                parent.getId(), offering.getId(), BookingStatus.CONFIRMED)) {
            throw new ConflictException("You have already booked this offering");
        }

        List<Session> newSessions = sessionRepository.findByOfferingId(offering.getId());
        if (newSessions.isEmpty()) {
            throw new ConflictException("Offering has no sessions — cannot book yet");
        }

        for (Session session : newSessions) {
            long conflicts = sessionRepository.countConflicts(
                    parent.getId(), session.getStartTime(), session.getEndTime());
            if (conflicts > 0) {
                throw new ConflictException(
                        "Time conflict: a session in this offering overlaps with an existing booking");
            }
        }

        Booking booking = new Booking();
        booking.setParent(parent);
        booking.setOffering(offering);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        ZoneId zone = resolveZone(timezoneOverride, parent.getTimezone());
        return toResponse(booking, newSessions, zone);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getParentBookings(UUID parentId, String timezoneOverride) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        ZoneId zone = resolveZone(timezoneOverride, parent.getTimezone());

        // findByParentId JOIN FETCHes offering + sessions — no N+1
        return bookingRepository.findByParentId(parentId).stream()
                .map(booking -> toResponse(booking, booking.getOffering().getSessions(), zone))
                .toList();
    }

    private ZoneId resolveZone(String override, String stored) {
        if (override != null && !override.isBlank()) {
            return TimezoneUtil.validateAndGet(override);
        }
        return TimezoneUtil.validateAndGet(stored);
    }

    private BookingResponse toResponse(Booking booking, List<Session> sessions, ZoneId zone) {
        List<SessionResponse> sessionResponses = sessions.stream()
                .map(s -> SessionResponse.builder()
                        .id(s.getId())
                        .offeringId(booking.getOffering().getId())
                        .teacherId(booking.getOffering().getTeacher().getId())
                        .startTime(TimezoneUtil.toLocalString(s.getStartTime(), zone))
                        .endTime(TimezoneUtil.toLocalString(s.getEndTime(), zone))
                        .timezone(zone.getId())
                        .build())
                .toList();

        Offering offering = booking.getOffering();
        OfferingResponse offeringResponse = OfferingResponse.builder()
                .id(offering.getId())
                .title(offering.getTitle())
                .status(offering.getStatus().name())
                .course(CourseResponse.builder()
                        .id(offering.getCourse().getId())
                        .name(offering.getCourse().getName())
                        .description(offering.getCourse().getDescription())
                        .build())
                .teacherName(offering.getTeacher().getName())
                .sessions(sessionResponses)
                .build();

        return BookingResponse.builder()
                .id(booking.getId())
                .status(booking.getStatus().name())
                .bookedAt(booking.getBookedAt().toString())
                .offering(offeringResponse)
                .build();
    }
}

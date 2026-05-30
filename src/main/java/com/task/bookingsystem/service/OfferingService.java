package com.task.bookingsystem.service;

import com.task.bookingsystem.dto.request.AddSessionsRequest;
import com.task.bookingsystem.dto.request.CreateOfferingRequest;
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

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final SessionRepository sessionRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public OfferingResponse createOffering(CreateOfferingRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Offering offering = new Offering();
        offering.setCourse(course);
        offering.setTeacher(teacher);
        offering.setTitle(request.getTitle());
        offering = offeringRepository.save(offering);

        return toResponse(offering, List.of(), teacher.getTimezone());
    }

    @Transactional
    public List<SessionResponse> addSessions(UUID offeringId, AddSessionsRequest request) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (offering.getStatus() == OfferingStatus.CANCELLED) {
            throw new ConflictException("Cannot add sessions to a cancelled offering");
        }

        Teacher teacher = offering.getTeacher();
        ZoneId teacherZone = TimezoneUtil.validateAndGet(teacher.getTimezone());

        List<Session> sessions = request.getSessions().stream().map(item -> {
            Instant start = TimezoneUtil.toUtc(item.getStartTime(), teacherZone);
            Instant end = TimezoneUtil.toUtc(item.getEndTime(), teacherZone);

            if (!end.isAfter(start)) {
                throw new IllegalArgumentException(
                        "End time must be after start time for session starting at " + item.getStartTime());
            }

            Session session = new Session();
            session.setOffering(offering);
            session.setStartTime(start);
            session.setEndTime(end);
            return session;
        }).toList();

        return sessionRepository.saveAll(sessions).stream()
                .map(s -> SessionResponse.builder()
                        .id(s.getId())
                        .offeringId(offering.getId())
                        .teacherId(teacher.getId())
                        .startTime(TimezoneUtil.toLocalString(s.getStartTime(), teacherZone))
                        .endTime(TimezoneUtil.toLocalString(s.getEndTime(), teacherZone))
                        .timezone(teacher.getTimezone())
                        .build())
                .toList();
    }

    @Transactional
    public void cancelOffering(UUID offeringId) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        if (offering.getStatus() == OfferingStatus.CANCELLED) {
            throw new ConflictException("Offering is already cancelled");
        }

        offering.setStatus(OfferingStatus.CANCELLED);
        offeringRepository.save(offering);

        // Single bulk UPDATE — no per-row loop
        bookingRepository.cancelConfirmedByOfferingId(offeringId);
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getActiveOfferings(String timezone) {
        ZoneId zone = (timezone != null && !timezone.isBlank())
                ? TimezoneUtil.validateAndGet(timezone)
                : ZoneId.of("UTC");

        // findByStatus JOIN FETCHes sessions — no N+1
        return offeringRepository.findByStatus(OfferingStatus.ACTIVE).stream()
                .map(offering -> {
                    List<SessionResponse> sessions = offering.getSessions().stream()
                            .map(s -> SessionResponse.builder()
                                    .id(s.getId())
                                    .offeringId(offering.getId())
                                    .teacherId(offering.getTeacher().getId())
                                    .startTime(TimezoneUtil.toLocalString(s.getStartTime(), zone))
                                    .endTime(TimezoneUtil.toLocalString(s.getEndTime(), zone))
                                    .timezone(zone.getId())
                                    .build())
                            .toList();
                    return toResponse(offering, sessions, zone.getId());
                })
                .toList();
    }

    private OfferingResponse toResponse(Offering offering, List<SessionResponse> sessions, String timezone) {
        return OfferingResponse.builder()
                .id(offering.getId())
                .title(offering.getTitle())
                .status(offering.getStatus().name())
                .course(CourseResponse.builder()
                        .id(offering.getCourse().getId())
                        .name(offering.getCourse().getName())
                        .description(offering.getCourse().getDescription())
                        .build())
                .teacherName(offering.getTeacher().getName())
                .sessions(sessions)
                .build();
    }
}

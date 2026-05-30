package com.task.bookingsystem.service;

import com.task.bookingsystem.dto.request.CreateTeacherRequest;
import com.task.bookingsystem.dto.response.CourseResponse;
import com.task.bookingsystem.dto.response.OfferingResponse;
import com.task.bookingsystem.dto.response.SessionResponse;
import com.task.bookingsystem.dto.response.TeacherResponse;
import com.task.bookingsystem.entity.Teacher;
import com.task.bookingsystem.exception.ResourceNotFoundException;
import com.task.bookingsystem.repository.OfferingRepository;
import com.task.bookingsystem.repository.TeacherRepository;
import com.task.bookingsystem.util.TimezoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final OfferingRepository offeringRepository;

    @Transactional
    public TeacherResponse createTeacher(CreateTeacherRequest request) {
        TimezoneUtil.validateAndGet(request.getTimezone());

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setTimezone(request.getTimezone());

        teacher = teacherRepository.save(teacher);
        return toResponse(teacher);
    }

    @Transactional(readOnly = true)
    public List<OfferingResponse> getTeacherOfferings(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        ZoneId teacherZone = TimezoneUtil.validateAndGet(teacher.getTimezone());

        // findByTeacherId JOIN FETCHes sessions — no N+1
        return offeringRepository.findByTeacherId(teacherId).stream()
                .map(offering -> {
                    List<SessionResponse> sessions = offering.getSessions().stream()
                            .map(s -> SessionResponse.builder()
                                    .id(s.getId())
                                    .offeringId(offering.getId())
                                    .teacherId(teacher.getId())
                                    .startTime(TimezoneUtil.toLocalString(s.getStartTime(), teacherZone))
                                    .endTime(TimezoneUtil.toLocalString(s.getEndTime(), teacherZone))
                                    .timezone(teacher.getTimezone())
                                    .build())
                            .toList();

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
                })
                .toList();
    }

    private TeacherResponse toResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .email(teacher.getEmail())
                .timezone(teacher.getTimezone())
                .build();
    }
}

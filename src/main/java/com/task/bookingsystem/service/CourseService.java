package com.task.bookingsystem.service;

import com.task.bookingsystem.dto.request.CreateCourseRequest;
import com.task.bookingsystem.dto.response.CourseResponse;
import com.task.bookingsystem.entity.Course;
import com.task.bookingsystem.exception.ResourceNotFoundException;
import com.task.bookingsystem.repository.CourseRepository;
import com.task.bookingsystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        if (!teacherRepository.existsById(request.getTeacherId())) {
            throw new ResourceNotFoundException("Teacher not found");
        }

        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course = courseRepository.save(course);
        return toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .build();
    }
}

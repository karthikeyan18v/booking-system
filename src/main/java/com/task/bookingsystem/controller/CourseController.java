package com.task.bookingsystem.controller;

import com.task.bookingsystem.dto.request.CreateCourseRequest;
import com.task.bookingsystem.dto.response.CourseResponse;
import com.task.bookingsystem.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Courses", description = "Course catalog — creation requires a valid teacherId (teacher-only action)")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(
        summary = "Create a course (teacher only)",
        description = """
            Registers a new course in the catalog.
            **Requires a valid `teacherId`** — only registered teachers can create courses.
            Provide the teacher's UUID in the request body; the system validates the teacher exists before saving.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Course created successfully"),
        @ApiResponse(responseCode = "400", description = "Missing or invalid fields",
                     content = @Content(schema = @Schema(example = "{\"status\":400,\"message\":\"teacherId: must not be null\"}"))),
        @ApiResponse(responseCode = "404", description = "Teacher not found",
                     content = @Content(schema = @Schema(example = "{\"status\":404,\"message\":\"Teacher not found\"}")))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.createCourse(request);
    }

    @Operation(summary = "List all courses", description = "Returns every course in the catalog. Open to all users.")
    @ApiResponse(responseCode = "200", description = "Course list returned")
    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }
}

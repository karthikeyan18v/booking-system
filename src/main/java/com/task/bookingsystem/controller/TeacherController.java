package com.task.bookingsystem.controller;

import com.task.bookingsystem.dto.request.CreateTeacherRequest;
import com.task.bookingsystem.dto.response.OfferingResponse;
import com.task.bookingsystem.dto.response.TeacherResponse;
import com.task.bookingsystem.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Teachers")
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "Register a teacher",
               description = "Creates a teacher profile. The `timezone` field (e.g. `Asia/Kolkata`, `America/New_York`) " +
                             "is used to interpret session times when teachers add sessions to their offerings.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherResponse createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        return teacherService.createTeacher(request);
    }

    @Operation(summary = "Get teacher offerings",
               description = "Returns all offerings created by the teacher, with sessions shown in the teacher's own timezone.")
    @GetMapping("/{teacherId}/offerings")
    public List<OfferingResponse> getTeacherOfferings(
            @Parameter(description = "Teacher UUID") @PathVariable UUID teacherId) {
        return teacherService.getTeacherOfferings(teacherId);
    }
}

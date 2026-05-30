package com.task.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request body for creating a new course — only a registered teacher can create a course")
public class CreateCourseRequest {

    @NotNull
    @Schema(description = "ID of the teacher creating this course", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID teacherId;

    @NotBlank
    @Schema(description = "Course name", example = "Python Coding")
    private String name;

    @Schema(description = "Optional longer description", example = "Learn Python from scratch through fun projects.")
    private String description;
}

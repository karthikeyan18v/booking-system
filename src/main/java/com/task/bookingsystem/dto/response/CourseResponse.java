package com.task.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "A course in the catalog")
public class CourseResponse {

    @Schema(description = "Course unique identifier")
    private UUID id;

    @Schema(description = "Course name", example = "Python Coding")
    private String name;

    @Schema(description = "Optional description", example = "Learn Python from scratch through fun projects.")
    private String description;
}

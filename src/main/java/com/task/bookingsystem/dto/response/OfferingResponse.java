package com.task.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "An offering (section) of a course with its sessions")
public class OfferingResponse {

    @Schema(description = "Offering unique identifier")
    private UUID id;

    @Schema(description = "Display title of this section", example = "Saturday Morning Batch")
    private String title;

    @Schema(description = "ACTIVE or CANCELLED", example = "ACTIVE")
    private String status;

    @Schema(description = "Course this offering belongs to")
    private CourseResponse course;

    @Schema(description = "Name of the teacher conducting this offering", example = "Alice Smith")
    private String teacherName;

    @Schema(description = "Scheduled sessions, with times in the requested timezone")
    private List<SessionResponse> sessions;
}

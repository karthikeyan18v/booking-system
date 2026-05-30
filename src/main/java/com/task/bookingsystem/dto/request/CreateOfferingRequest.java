package com.task.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request body for creating a new offering (section) of a course")
public class CreateOfferingRequest {

    @NotNull
    @Schema(description = "ID of the course this offering belongs to",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID courseId;

    @NotNull
    @Schema(description = "ID of the teacher who will conduct this offering",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID teacherId;

    @NotBlank
    @Schema(description = "Display name for this section", example = "Saturday Morning Batch")
    private String title;
}

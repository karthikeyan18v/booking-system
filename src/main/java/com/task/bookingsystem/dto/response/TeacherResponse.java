package com.task.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Registered teacher profile")
public class TeacherResponse {

    @Schema(description = "Unique identifier")
    private UUID id;

    @Schema(description = "Full name", example = "Alice Smith")
    private String name;

    @Schema(description = "Email address", example = "alice@school.com")
    private String email;

    @Schema(description = "IANA timezone used to interpret session times they create", example = "Asia/Kolkata")
    private String timezone;
}

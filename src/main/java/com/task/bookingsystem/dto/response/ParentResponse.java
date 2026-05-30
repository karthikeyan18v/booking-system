package com.task.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Registered parent/student profile")
public class ParentResponse {

    @Schema(description = "Unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Full name", example = "Jane Doe")
    private String name;

    @Schema(description = "Email address", example = "jane@example.com")
    private String email;

    @Schema(description = "Stored IANA timezone", example = "America/New_York")
    private String timezone;
}

package com.task.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for registering a new parent or student")
public class CreateParentRequest {

    @NotBlank
    @Schema(description = "Full name of the parent/student", example = "Jane Doe")
    private String name;

    @NotBlank
    @Email
    @Schema(description = "Unique email address", example = "jane@example.com")
    private String email;

    @NotBlank
    @Schema(description = "IANA timezone identifier — used as the default when viewing session times",
            example = "America/New_York")
    private String timezone;
}

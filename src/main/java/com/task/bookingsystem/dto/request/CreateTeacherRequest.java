package com.task.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for registering a new teacher")
public class CreateTeacherRequest {

    @NotBlank
    @Schema(description = "Full name of the teacher", example = "Alice Smith")
    private String name;

    @NotBlank
    @Email
    @Schema(description = "Unique email address", example = "alice@school.com")
    private String email;

    @NotBlank
    @Schema(description = "IANA timezone — session times entered by this teacher are interpreted in this zone",
            example = "Asia/Kolkata")
    private String timezone;
}

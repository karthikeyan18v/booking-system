package com.task.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request body for adding sessions to an offering")
public class AddSessionsRequest {

    @NotEmpty
    @Valid
    @Schema(description = "One or more sessions to add. Times are in ISO local date-time format and interpreted in the teacher's timezone.")
    private List<SessionItem> sessions;

    @Data
    @Schema(description = "A single session time slot")
    public static class SessionItem {

        @NotBlank
        @Schema(description = "Session start time in ISO local date-time format (teacher's timezone)",
                example = "2025-06-07T18:00:00")
        private String startTime;

        @NotBlank
        @Schema(description = "Session end time in ISO local date-time format (teacher's timezone)",
                example = "2025-06-07T19:00:00")
        private String endTime;
    }
}

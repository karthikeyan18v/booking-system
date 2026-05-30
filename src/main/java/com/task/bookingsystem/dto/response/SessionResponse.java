package com.task.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "A single session (meeting time) within an offering")
public class SessionResponse {

    @Schema(description = "Session unique identifier")
    private UUID id;

    @Schema(description = "Offering this session belongs to")
    private UUID offeringId;

    @Schema(description = "Teacher conducting this session")
    private UUID teacherId;

    @Schema(description = "Session start time in ISO local date-time, expressed in the requested timezone",
            example = "2025-06-07T18:00:00")
    private String startTime;

    @Schema(description = "Session end time in ISO local date-time, expressed in the requested timezone",
            example = "2025-06-07T19:00:00")
    private String endTime;

    @Schema(description = "IANA timezone the start/end times are expressed in", example = "America/New_York")
    private String timezone;
}

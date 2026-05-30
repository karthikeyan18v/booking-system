package com.task.bookingsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Confirmed (or cancelled) booking made by a parent")
public class BookingResponse {

    @Schema(description = "Booking unique identifier")
    private UUID id;

    @Schema(description = "CONFIRMED or CANCELLED", example = "CONFIRMED")
    private String status;

    @Schema(description = "ISO-8601 timestamp when the booking was created", example = "2025-05-30T10:15:30Z")
    private String bookedAt;

    @Schema(description = "The booked offering with sessions in the requested timezone")
    private OfferingResponse offering;
}

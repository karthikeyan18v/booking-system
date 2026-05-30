package com.task.bookingsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request body for booking an entire offering")
public class BookOfferingRequest {

    @NotNull
    @Schema(description = "ID of the parent/student making the booking",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID parentId;

    @NotNull
    @Schema(description = "ID of the offering to book",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID offeringId;
}

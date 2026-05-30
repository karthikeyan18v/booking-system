package com.task.bookingsystem.controller;

import com.task.bookingsystem.dto.request.BookOfferingRequest;
import com.task.bookingsystem.dto.response.BookingResponse;
import com.task.bookingsystem.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookings")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Book an offering",
               description = "Books an entire offering for a parent. Enforces three rules:\n" +
                             "1. A parent cannot book the same offering twice.\n" +
                             "2. A parent cannot book an offering whose sessions overlap with any already-booked sessions.\n" +
                             "3. Concurrent booking attempts are serialised via a pessimistic write lock on the parent row.\n\n" +
                             "Pass a `timezone` query param (e.g. `Asia/Kolkata`) to receive session times in that timezone.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bookOffering(
            @Valid @RequestBody BookOfferingRequest request,
            @Parameter(description = "IANA timezone for the response (e.g. Asia/Kolkata). Defaults to UTC.")
            @RequestParam(required = false) String timezone) {
        return bookingService.bookOffering(request, timezone);
    }
}

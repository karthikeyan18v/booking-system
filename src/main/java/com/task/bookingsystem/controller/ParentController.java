package com.task.bookingsystem.controller;

import com.task.bookingsystem.dto.request.CreateParentRequest;
import com.task.bookingsystem.dto.response.BookingResponse;
import com.task.bookingsystem.dto.response.ParentResponse;
import com.task.bookingsystem.service.BookingService;
import com.task.bookingsystem.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Parents")
@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final BookingService bookingService;

    @Operation(summary = "Register a parent/student",
               description = "Creates a parent profile. The `timezone` field (e.g. `America/New_York`) is stored " +
                             "and used as the default when viewing session times. It can be overridden per-request.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParentResponse createParent(@Valid @RequestBody CreateParentRequest request) {
        return parentService.createParent(request);
    }

    @Operation(summary = "Get parent bookings",
               description = "Returns all bookings for the parent. Session times are shown in the parent's stored timezone " +
                             "by default. Pass `timezone` to override (e.g. `Asia/Tokyo`).")
    @GetMapping("/{parentId}/bookings")
    public List<BookingResponse> getBookings(
            @Parameter(description = "Parent UUID") @PathVariable UUID parentId,
            @Parameter(description = "Optional IANA timezone override (e.g. Asia/Tokyo). Defaults to the parent's stored timezone.")
            @RequestParam(required = false) String timezone) {
        return bookingService.getParentBookings(parentId, timezone);
    }
}

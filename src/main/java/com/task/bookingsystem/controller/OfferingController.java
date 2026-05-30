package com.task.bookingsystem.controller;

import com.task.bookingsystem.dto.request.AddSessionsRequest;
import com.task.bookingsystem.dto.request.CreateOfferingRequest;
import com.task.bookingsystem.dto.response.OfferingResponse;
import com.task.bookingsystem.dto.response.SessionResponse;
import com.task.bookingsystem.service.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Offerings")
@RestController
@RequestMapping("/api/offerings")
@RequiredArgsConstructor
public class OfferingController {

    private final OfferingService offeringService;

    @Operation(summary = "Create an offering",
               description = "Creates a new offering (section) for a course, associated with a teacher. " +
                             "Sessions are added separately via the add-sessions endpoint.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferingResponse createOffering(@Valid @RequestBody CreateOfferingRequest request) {
        return offeringService.createOffering(request);
    }

    @Operation(summary = "Add sessions to an offering",
               description = "Adds one or more sessions (meeting times) to an existing offering. " +
                             "Session times must be provided as ISO local date-time strings (e.g. `2025-06-07T18:00:00`) " +
                             "in the **teacher's timezone** — the system converts them to UTC for storage.")
    @PostMapping("/{offeringId}/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SessionResponse> addSessions(
            @Parameter(description = "Offering UUID") @PathVariable UUID offeringId,
            @Valid @RequestBody AddSessionsRequest request) {
        return offeringService.addSessions(offeringId, request);
    }

    @Operation(summary = "Browse active offerings",
               description = "Returns all active offerings with their sessions. Pass a `timezone` query param " +
                             "(e.g. `America/New_York`) to view session times in the caller's local timezone. " +
                             "Defaults to UTC when omitted.")
    @GetMapping
    public List<OfferingResponse> getActiveOfferings(
            @Parameter(description = "IANA timezone (e.g. America/New_York). Defaults to UTC.")
            @RequestParam(required = false) String timezone) {
        return offeringService.getActiveOfferings(timezone);
    }

    @Operation(summary = "Cancel an offering",
               description = "Marks the offering as CANCELLED and cascade-cancels all confirmed bookings for it.")
    @DeleteMapping("/{offeringId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOffering(
            @Parameter(description = "Offering UUID") @PathVariable UUID offeringId) {
        offeringService.cancelOffering(offeringId);
    }
}

package com.example.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Controller", description = "Operations for managing bookings")
public class BookingController {

    @Operation(
            summary = "Get all bookings",
            description = "Retrieve a list of all bookings in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved bookings"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "List of all bookings");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get booking by ID",
            description = "Retrieve a specific booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved booking"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(
            @Parameter(description = "ID of the booking to retrieve", required = true)
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking details for ID: " + id);
        response.put("bookingId", id);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new booking",
            description = "Create a new booking in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(
            @Parameter(description = "Booking details", required = true)
            @RequestBody Map<String, Object> bookingData) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking created successfully");
        response.put("bookingData", bookingData);
        response.put("status", "success");
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Update an existing booking",
            description = "Update an existing booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBooking(
            @Parameter(description = "ID of the booking to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated booking details", required = true)
            @RequestBody Map<String, Object> bookingData) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking updated successfully");
        response.put("bookingId", id);
        response.put("updatedData", bookingData);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a booking",
            description = "Delete an existing booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(
            @Parameter(description = "ID of the booking to delete", required = true)
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking deleted successfully");
        response.put("bookingId", id);
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}

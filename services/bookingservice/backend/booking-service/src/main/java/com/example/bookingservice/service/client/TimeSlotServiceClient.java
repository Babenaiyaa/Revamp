package com.example.bookingservice.service.client;

// REMOVE 'import lombok.Data;' from here if it was present
import java.time.LocalDate; // Import the DTO from its new location
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bookingservice.dto.TimeSlotStatusUpdateRequest;
import com.example.bookingservice.model.RemoteTimeSlotDto;

// Feign client for interacting with the external Time Slot Service
@FeignClient(name = "${time-slot-service.name}", fallback = TimeSlotServiceFallback.class)
public interface TimeSlotServiceClient {

    // Assumed API endpoint to get available time slots for a specific date
    @GetMapping("/api/time-slots/available")
    List<RemoteTimeSlotDto> getAvailableTimeSlots(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    // Assumed API endpoint to get a single time slot by date and slot string
    // This is used for validating a specific slot selection
    @GetMapping("/api/time-slots/by-date-and-slot")
    Optional<RemoteTimeSlotDto> getTimeSlotByDateAndSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String slot);

    // Assumed API endpoint to update a time slot's availability status in the external service
    // This is called by your booking service when a booking is created or finalized.
    @PutMapping("/api/time-slots/{id}/status")
    RemoteTimeSlotDto updateTimeSlotStatus(@PathVariable("id") String id, @RequestBody TimeSlotStatusUpdateRequest request);
}

// Fallback implementation for TimeSlotServiceClient
// This is invoked if the Time Slot Service is unreachable or throws an error
class TimeSlotServiceFallback implements TimeSlotServiceClient {
    @Override
    public List<RemoteTimeSlotDto> getAvailableTimeSlots(LocalDate date) {
        System.err.println("FALLBACK: TimeSlotService - Could not retrieve available time slots for date " + date);
        return Collections.emptyList(); // Return empty list to prevent application crash
    }

    @Override
    public Optional<RemoteTimeSlotDto> getTimeSlotByDateAndSlot(LocalDate date, String slot) {
        System.err.println("FALLBACK: TimeSlotService - Could not retrieve time slot " + slot + " for date " + date);
        return Optional.empty(); // Return empty Optional
    }

    @Override
    public RemoteTimeSlotDto updateTimeSlotStatus(String id, TimeSlotStatusUpdateRequest request) {
        System.err.println("FALLBACK: TimeSlotService - Could not update time slot status for ID " + id);
        return null; // Return null to indicate the update failed
    }
}
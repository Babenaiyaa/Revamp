package com.example.bookingservice.dto;

import java.time.LocalDate;

// DTO for representing a time slot fetched from the external Time Slot Service
// and sent to the frontend.
public class TimeSlotResponseDto {

    private String id;
    private LocalDate date;
    private String slot;
    private boolean isAvailable;

    // Manual constructors to fix IDE issues
    public TimeSlotResponseDto() {
    }

    public TimeSlotResponseDto(String id, LocalDate date, String slot, boolean isAvailable) {
        this.id = id;
        this.date = date;
        this.slot = slot;
        this.isAvailable = isAvailable;
    }
}

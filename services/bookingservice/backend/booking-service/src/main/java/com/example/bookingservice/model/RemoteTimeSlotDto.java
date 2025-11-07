package com.example.bookingservice.model;

import java.time.LocalDate;

// DTO representing a time slot received from the external Time Slot Service
// This is NOT a @Document as it's not stored in your booking service's database
public class RemoteTimeSlotDto {

    private String id; // The unique ID of the time slot in the external Time Slot Service
    private LocalDate date;
    private String slot; // e.g., "09:00-10:00"
    private boolean isAvailable;
    // The external service might have other fields like bookedByAppointmentId,
    // but your service only needs this basic info.

    // Constructors
    public RemoteTimeSlotDto() {
    }

    public RemoteTimeSlotDto(String id, LocalDate date, String slot, boolean isAvailable) {
        this.id = id;
        this.date = date;
        this.slot = slot;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}

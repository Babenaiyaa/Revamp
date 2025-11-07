package com.example.bookingservice.dto;

/**
 * DTO for sending requests to the external Time Slot Service to update the
 * status of a time slot. This is used by your Booking Service when a time slot
 * is booked or freed up.
 */
public class TimeSlotStatusUpdateRequest {

    private boolean isAvailable;         // The new availability status for the slot (true to free, false to book)
    private String bookedByAppointmentId; // The ID of the appointment that booked this slot. Set to null when freeing up.

    // Manual constructors to fix IDE issues
    public TimeSlotStatusUpdateRequest() {
    }

    public TimeSlotStatusUpdateRequest(boolean isAvailable, String bookedByAppointmentId) {
        this.isAvailable = isAvailable;
        this.bookedByAppointmentId = bookedByAppointmentId;
    }

    // Manual getters and setters for IDE compatibility
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getBookedByAppointmentId() {
        return bookedByAppointmentId;
    }

    public void setBookedByAppointmentId(String bookedByAppointmentId) {
        this.bookedByAppointmentId = bookedByAppointmentId;
    }
}

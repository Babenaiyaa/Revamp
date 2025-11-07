package com.example.bookingservice.dto;

import com.example.bookingservice.model.Appointment;

// DTO for sending booking creation responses back to the client
public class BookingResponse {

    private String bookingId;
    private Appointment.AppointmentStatus status;
    private String message;
    private Double estimatedCost;        // Null for service, calculated for modification
    private Double estimatedDurationHours; // Null for service, calculated for modification

    // Constructors
    public BookingResponse() {
    }

    public BookingResponse(String bookingId, Appointment.AppointmentStatus status, String message) {
        this.bookingId = bookingId;
        this.status = status;
        this.message = message;
    }

    public BookingResponse(String bookingId, Appointment.AppointmentStatus status, String message, Double estimatedCost, Double estimatedDurationHours) {
        this.bookingId = bookingId;
        this.status = status;
        this.message = message;
        this.estimatedCost = estimatedCost;
        this.estimatedDurationHours = estimatedDurationHours;
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Appointment.AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(Appointment.AppointmentStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Double getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Double estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }
}

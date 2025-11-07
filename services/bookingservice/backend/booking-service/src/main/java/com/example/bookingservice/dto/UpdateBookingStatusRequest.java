package com.example.bookingservice.dto;

import com.example.bookingservice.model.Appointment;

// DTO for updating the status of an existing booking (used by admin/employee)
public class UpdateBookingStatusRequest {

    private Appointment.AppointmentStatus newStatus; // Mandatory: The new status for the booking
    private String assignedEmployeeId;             // Optional: ID of the employee to assign
    private String adminRemarks;                   // Optional: Remarks from the admin
    private Boolean paid;                          // Optional: Flag to mark booking as paid

    // Manual constructors to fix IDE issues
    public UpdateBookingStatusRequest() {
    }

    public UpdateBookingStatusRequest(Appointment.AppointmentStatus newStatus, String assignedEmployeeId,
            String adminRemarks, Boolean paid) {
        this.newStatus = newStatus;
        this.assignedEmployeeId = assignedEmployeeId;
        this.adminRemarks = adminRemarks;
        this.paid = paid;
    }

    // Getters and Setters
    public Appointment.AppointmentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Appointment.AppointmentStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}

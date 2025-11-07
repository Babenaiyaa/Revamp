package com.example.bookingservice.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "appointments") // Specifies the MongoDB collection name
public class Appointment {

    @Id // Marks this field as the document's primary identifier
    private String id;
    private String customerId; // ID of the customer who made the booking
    private String vehicleId;  // ID of the registered vehicle (if applicable)
    private VehicleDetails newVehicleDetails; // Details for "other" (unregistered) vehicles

    private AppointmentType type; // Type of appointment: SERVICE or MODIFICATION
    private Map<String, Object> details; // A flexible map to store service-specific or modification-specific details

    private LocalDate date;        // Date of the appointment/modification start
    private String timeSlot;       // Specific time slot (only for SERVICE type, null for MODIFICATION)

    private AppointmentStatus status; // Current status of the appointment
    private String remarks;            // Customer's remarks for the booking
    private Double estimatedCost;      // Estimated cost (calculated for MODIFICATION, optional for SERVICE)
    private Boolean paid;              // Flag indicating if the payment has been made
    private Double estimatedDurationHours; // Estimated duration (calculated for MODIFICATION, optional for SERVICE)

    private String assignedEmployeeId; // ID of the employee assigned to this booking (by admin)
    private String adminRemarks;       // Remarks/instructions from admin

    private LocalDateTime createdAt;   // Timestamp when the booking was created
    private LocalDateTime updatedAt;   // Timestamp when the booking was last updated

    // Enum for appointment types
    public enum AppointmentType {
        SERVICE, MODIFICATION
    }

    // Enum for appointment statuses
    public enum AppointmentStatus {
        PENDING, // Waiting for admin approval/assignment
        ACCEPTED, // Admin has accepted/confirmed it
        IN_PROGRESS, // Employee has started working on it
        COMPLETED, // Employee has finished the work
        DELIVERED, // Vehicle has been handed back to the customer
        CANCELED       // Booking has been canceled
    }

    // Embedded class for 'other' vehicle details, not a separate collection
    public static class VehicleDetails { // Made static to be self-contained

        private String type; // e.g., Car, Truck, Motorcycle
        private String brand;
        private String model;
        private Integer year;
        private String licensePlate;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public VehicleDetails getNewVehicleDetails() {
        return newVehicleDetails;
    }

    public void setNewVehicleDetails(VehicleDetails newVehicleDetails) {
        this.newVehicleDetails = newVehicleDetails;
    }

    public AppointmentType getType() {
        return type;
    }

    public void setType(AppointmentType type) {
        this.type = type;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Double getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Double estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

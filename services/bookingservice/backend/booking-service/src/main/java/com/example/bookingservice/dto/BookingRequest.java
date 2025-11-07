package com.example.bookingservice.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.bookingservice.model.Appointment;

// DTO for incoming booking creation requests
public class BookingRequest {

    private String customerId; // Mandatory: ID of the customer making the booking
    private String vehicleId;  // Optional: If customer selects an already registered vehicle
    private Appointment.VehicleDetails newVehicleDetails; // Optional: If customer provides new vehicle details ('other' option)

    private Appointment.AppointmentType type; // Mandatory: SERVICE or MODIFICATION

    // Fields specific to SERVICE type
    private String serviceName; // e.g., "Oil Change", "Tire Rotation"

    // Fields specific to MODIFICATION type
    private List<String> selectedModificationItemIds; // List of IDs from the Modification Catalog Service

    private LocalDate date;     // Mandatory for both: Date of the booking
    private String timeSlot;    // Mandatory for SERVICE type, null/optional for MODIFICATION type

    private String remarks;     // Optional: Customer's additional remarks

    // Manual constructors to fix IDE issues
    public BookingRequest() {
    }

    public BookingRequest(String customerId, String vehicleId, Appointment.VehicleDetails newVehicleDetails,
            Appointment.AppointmentType type, String serviceName, List<String> selectedModificationItemIds,
            LocalDate date, String timeSlot, String remarks) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.newVehicleDetails = newVehicleDetails;
        this.type = type;
        this.serviceName = serviceName;
        this.selectedModificationItemIds = selectedModificationItemIds;
        this.date = date;
        this.timeSlot = timeSlot;
        this.remarks = remarks;
    }

    // Getters and Setters
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

    public Appointment.VehicleDetails getNewVehicleDetails() {
        return newVehicleDetails;
    }

    public void setNewVehicleDetails(Appointment.VehicleDetails newVehicleDetails) {
        this.newVehicleDetails = newVehicleDetails;
    }

    public Appointment.AppointmentType getType() {
        return type;
    }

    public void setType(Appointment.AppointmentType type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getSelectedModificationItemIds() {
        return selectedModificationItemIds;
    }

    public void setSelectedModificationItemIds(List<String> selectedModificationItemIds) {
        this.selectedModificationItemIds = selectedModificationItemIds;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

package com.example.bookingservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookingservice.dto.BookingRequest; // Import TimeSlotServiceClient
import com.example.bookingservice.dto.BookingResponse; // Import TimeSlotStatusUpdateRequest
import com.example.bookingservice.dto.TimeSlotResponseDto;
import com.example.bookingservice.dto.TimeSlotStatusUpdateRequest;
import com.example.bookingservice.dto.UpdateBookingStatusRequest; // For local MongoDB transactions
import com.example.bookingservice.exception.ResourceNotFoundException;
import com.example.bookingservice.model.Appointment;
import com.example.bookingservice.model.RemoteModificationItemDto;
import com.example.bookingservice.model.RemoteTimeSlotDto;
import com.example.bookingservice.model.RemoteVehicleDto;
import com.example.bookingservice.repository.AppointmentRepository;
import com.example.bookingservice.service.client.ModificationCatalogServiceClient;
import com.example.bookingservice.service.client.TimeSlotServiceClient;
import com.example.bookingservice.service.client.UserProfileServiceClient;

@Service // Marks this class as a Spring Service component
public class AppointmentService {

    private final AppointmentRepository appointmentRepository; // Inject your local MongoDB repository
    private final UserProfileServiceClient userProfileServiceClient; // Inject Feign client for User Profile Service
    private final ModificationCatalogServiceClient modificationCatalogServiceClient; // Inject Feign client for Modification Catalog Service
    private final TimeSlotServiceClient timeSlotServiceClient; // Inject Feign client for Time Slot Service

    // Constructor for dependency injection
    public AppointmentService(AppointmentRepository appointmentRepository,
            UserProfileServiceClient userProfileServiceClient,
            ModificationCatalogServiceClient modificationCatalogServiceClient,
            TimeSlotServiceClient timeSlotServiceClient) {
        this.appointmentRepository = appointmentRepository;
        this.userProfileServiceClient = userProfileServiceClient;
        this.modificationCatalogServiceClient = modificationCatalogServiceClient;
        this.timeSlotServiceClient = timeSlotServiceClient;
    }

    // Optional: If using Kafka for asynchronous communication, uncomment and inject
    // private final KafkaTemplate<String, String> kafkaTemplate;
    /**
     * Creates a new booking, handling both service appointments and
     * modification projects. Interacts with external services for vehicle
     * details, modification options, and time slot management.
     *
     * @param request The BookingRequest DTO containing all booking details.
     * @return A BookingResponse DTO with details of the created booking.
     * @throws IllegalArgumentException If request data is invalid or external
     * service data is inconsistent.
     * @throws ResourceNotFoundException If a referenced external resource (like
     * a modification item or time slot) is not found.
     * @throws RuntimeException For unexpected external service errors during
     * critical updates.
     */
    @Transactional // Ensures atomicity for operations within this service's database context
    public BookingResponse createBooking(BookingRequest request) {
        // --- 1. Basic Validation: Customer ID ---
        if (request.getCustomerId() == null || request.getCustomerId().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required for booking.");
        }

        // --- 2. Handle Vehicle Details (from request or external User Profile Service) ---
        String vehicleIdToStore = null;
        Appointment.VehicleDetails vehicleDetailsToStore = null;

        if (request.getVehicleId() != null && !request.getVehicleId().isEmpty()) {
            // Customer selected a registered vehicle, store its ID
            vehicleIdToStore = request.getVehicleId();
            // OPTIONAL: Call UserProfileService here to verify if `vehicleId` actually exists and belongs to `customerId`.
            // RemoteVehicleDto remoteVehicle = userProfileServiceClient.getVehicleById(request.getVehicleId());
            // if (remoteVehicle == null || !remoteVehicle.getCustomerId().equals(request.getCustomerId())) {
            //     throw new IllegalArgumentException("Invalid vehicleId or vehicle not registered to customer.");
            // }
        } else if (request.getNewVehicleDetails() != null) {
            // Customer provided 'other' vehicle details, store them directly in the booking
            vehicleDetailsToStore = request.getNewVehicleDetails();
        } else {
            throw new IllegalArgumentException("Vehicle details (registered ID or new details) are required.");
        }

        // --- 3. Time Slot Handling (for SERVICE type only, interacts with external Time Slot Service) ---
        RemoteTimeSlotDto bookedRemoteSlot = null; // Stores the remote slot we intend to book, if applicable
        if (Appointment.AppointmentType.SERVICE.equals(request.getType())) {
            if (request.getDate() == null || request.getTimeSlot() == null || request.getTimeSlot().isEmpty()) {
                throw new IllegalArgumentException("Date and time slot are required for service appointments.");
            }

            // Call external Time Slot Service to check and retrieve the specific slot
            Optional<RemoteTimeSlotDto> remoteSlotOpt = timeSlotServiceClient.getTimeSlotByDateAndSlot(request.getDate(), request.getTimeSlot());
            if (remoteSlotOpt.isEmpty()) {
                throw new ResourceNotFoundException("Time slot not found or invalid from Time Slot Service for " + request.getDate() + " " + request.getTimeSlot() + ".");
            }
            RemoteTimeSlotDto remoteSlot = remoteSlotOpt.get();

            if (!remoteSlot.isAvailable()) {
                throw new IllegalArgumentException("Selected time slot " + request.getTimeSlot() + " for " + request.getDate() + " is not available from Time Slot Service.");
            }

            bookedRemoteSlot = remoteSlot; // Keep reference for later update in external service
        } else if (Appointment.AppointmentType.MODIFICATION.equals(request.getType())) {
            if (request.getDate() == null) {
                throw new IllegalArgumentException("Date is required for modification projects.");
            }
            // Modification bookings do not use a specific time slot, only a start date
        } else {
            throw new IllegalArgumentException("Invalid appointment type specified.");
        }

        // --- 4. Build Appointment Object (for YOUR booking service's database) ---
        Appointment appointment = new Appointment();
        appointment.setCustomerId(request.getCustomerId());
        appointment.setVehicleId(vehicleIdToStore);
        appointment.setNewVehicleDetails(vehicleDetailsToStore);

        appointment.setType(request.getType());
        appointment.setDate(request.getDate());
        appointment.setTimeSlot(request.getTimeSlot()); // Will be null for MODIFICATION type
        appointment.setRemarks(request.getRemarks());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING); // Initial status
        appointment.setPaid(false); // Default payment status
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        double estimatedCost = 0.0;
        double estimatedDurationHours = 0.0;

        // --- 5. Populate Service/Modification Specific Details ---
        if (Appointment.AppointmentType.SERVICE.equals(request.getType())) {
            Map<String, Object> serviceDetails = new HashMap<>();
            serviceDetails.put("serviceName", request.getServiceName()); // Store service name in details map
            appointment.setDetails(serviceDetails);
            // If you had a predefined service catalog in YOUR DB or another service for simple services,
            // you'd fetch baseCost and estimatedDurationHours here. For now, it's 0.0.
            // For example:
            // ServiceItem serviceInfo = anotherServiceClient.getServiceDetails(request.getServiceName());
            // if (serviceInfo != null) { estimatedCost = serviceInfo.getBaseCost(); estimatedDurationHours = serviceInfo.getEstimatedDurationHours(); }
            // If a service has a fixed cost/duration, you'd set them here or derive from a lookup.
            estimatedCost = 0.0; // Placeholder
            estimatedDurationHours = 0.0; // Placeholder

        } else if (Appointment.AppointmentType.MODIFICATION.equals(request.getType())) {
            if (request.getSelectedModificationItemIds() == null || request.getSelectedModificationItemIds().isEmpty()) {
                throw new IllegalArgumentException("At least one modification option must be selected for modification projects.");
            }

            List<String> selectedModIds = request.getSelectedModificationItemIds();
            Map<String, Object> modificationDetails = new HashMap<>();
            modificationDetails.put("selectedModificationItemIds", selectedModIds); // Store selected IDs in details map

            // Call external Modification Catalog Service for cost and duration calculation
            for (String modId : selectedModIds) {
                RemoteModificationItemDto modItem = modificationCatalogServiceClient.getModificationItemById(modId);
                if (modItem == null) {
                    throw new ResourceNotFoundException("Modification item not found from Modification Catalog Service with ID: " + modId);
                }
                estimatedCost += modItem.getBaseCost();
                estimatedDurationHours += modItem.getBaseDurationHours();
            }
            appointment.setEstimatedCost(estimatedCost);
            appointment.setEstimatedDurationHours(estimatedDurationHours);
            appointment.setDetails(modificationDetails); // Store modification-specific details
        }

        // --- 6. Save Appointment to your booking service's database ---
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // --- 7. Finalize Time Slot booking (for SERVICE type, update external Time Slot Service) ---
        if (Appointment.AppointmentType.SERVICE.equals(request.getType()) && bookedRemoteSlot != null) {
            TimeSlotStatusUpdateRequest updateRequest = new TimeSlotStatusUpdateRequest();
            updateRequest.setAvailable(false);
            updateRequest.setBookedByAppointmentId(savedAppointment.getId());
            // It's important to handle potential errors here if the update to external service fails
            try {
                timeSlotServiceClient.updateTimeSlotStatus(bookedRemoteSlot.getId(), updateRequest);
            } catch (Exception e) {
                // Log the error and potentially revert the booking, or set booking status to "FAILED_TO_UPDATE_TIMESLOT"
                System.err.println("CRITICAL ERROR: Failed to update time slot status in external Time Slot Service for slot ID " + bookedRemoteSlot.getId() + ". Booking ID: " + savedAppointment.getId() + ". Error: " + e.getMessage());
                throw new RuntimeException("Booking created, but failed to secure time slot in external service. Please contact support.", e);
            }
        }

        // --- 8. Publish Event (Optional, for asynchronous communication with other services) ---
        // e.g., to Kafka for Notification Service, Analytics Service, Employee Assignment Service
        // if (kafkaTemplate != null) {
        //     kafkaTemplate.send("booking-events", "booking-created", savedAppointment.getId());
        //     // The message could be a JSON payload:
        //     // { "eventType": "BookingCreated", "appointmentId": "...", "customerId": "...", "type": "SERVICE", ... }
        // }
        return new BookingResponse(
                savedAppointment.getId(),
                savedAppointment.getStatus(),
                "Booking created successfully!",
                estimatedCost > 0 ? estimatedCost : null, // Only return if calculated/applicable
                estimatedDurationHours > 0 ? estimatedDurationHours : null // Only return if calculated/applicable
        );
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param id The ID of the booking.
     * @return The Appointment object.
     * @throws ResourceNotFoundException If the booking is not found.
     */
    public Appointment getBookingById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
    }

    /**
     * Retrieves all bookings for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A list of Appointment objects.
     */
    public List<Appointment> getBookingsByCustomerId(String customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    /**
     * Retrieves all bookings with PENDING status (typically for admin review).
     *
     * @return A list of PENDING Appointment objects.
     */
    public List<Appointment> getAllPendingBookings() {
        return appointmentRepository.findByStatus(Appointment.AppointmentStatus.PENDING);
    }

    /**
     * Updates the status of an existing booking. Also handles freeing
     * up/securing time slots in the external Time Slot Service based on status
     * changes.
     *
     * @param id The ID of the booking to update.
     * @param updateRequest The DTO containing the new status and other optional
     * fields.
     * @return The updated Appointment object.
     * @throws ResourceNotFoundException If the booking is not found.
     * @throws IllegalArgumentException If an invalid status transition is
     * attempted (e.g., trying to set a delivered status to an uncompleted
     * booking).
     */
    @Transactional
    public Appointment updateBookingStatus(String id, UpdateBookingStatusRequest updateRequest) {
        Appointment appointment = getBookingById(id);

        Appointment.AppointmentStatus oldStatus = appointment.getStatus();

        // Update basic fields
        if (updateRequest.getNewStatus() != null) {
            appointment.setStatus(updateRequest.getNewStatus());
        }
        if (updateRequest.getAssignedEmployeeId() != null) {
            appointment.setAssignedEmployeeId(updateRequest.getAssignedEmployeeId());
        }
        if (updateRequest.getAdminRemarks() != null) {
            appointment.setAdminRemarks(updateRequest.getAdminRemarks());
        }
        if (updateRequest.getPaid() != null) {
            appointment.setPaid(updateRequest.getPaid());
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // --- Handle External Time Slot Service Update (if SERVICE type) ---
        if (Appointment.AppointmentType.SERVICE.equals(updatedAppointment.getType()) && updatedAppointment.getTimeSlot() != null) {
            boolean shouldFreeSlot = false;
            // Define conditions to free up the time slot in the external service
            // For example, if status changes to CANCELED or DELIVERED.
            if (Appointment.AppointmentStatus.CANCELED.equals(updatedAppointment.getStatus())
                    || Appointment.AppointmentStatus.DELIVERED.equals(updatedAppointment.getStatus())) {
                shouldFreeSlot = true;
            }

            if (shouldFreeSlot) {
                // Find the original remote slot ID using date and slot (or store it in Appointment model)
                Optional<RemoteTimeSlotDto> remoteSlotOpt = timeSlotServiceClient.getTimeSlotByDateAndSlot(updatedAppointment.getDate(), updatedAppointment.getTimeSlot());
                remoteSlotOpt.ifPresent(remoteSlot -> {
                    TimeSlotStatusUpdateRequest slotUpdateRequest = new TimeSlotStatusUpdateRequest();
                    slotUpdateRequest.setAvailable(true); // Mark as available
                    slotUpdateRequest.setBookedByAppointmentId(null); // Clear booking reference
                    try {
                        timeSlotServiceClient.updateTimeSlotStatus(remoteSlot.getId(), slotUpdateRequest);
                        System.out.println("Time slot " + remoteSlot.getSlot() + " on " + remoteSlot.getDate() + " freed up for booking ID: " + updatedAppointment.getId());
                    } catch (Exception e) {
                        System.err.println("WARNING: Failed to free up time slot in external service for ID " + remoteSlot.getId() + ". Error: " + e.getMessage());
                        // Consider manual intervention or alert system here
                    }
                });
            }
            // Add logic here to re-book a slot if status changes from CANCELED to PENDING/ACCEPTED and slot is available
        }

        // --- Publish Event (Optional) ---
        // if (kafkaTemplate != null) {
        //     kafkaTemplate.send("booking-events", "booking-status-updated", updatedAppointment.getId() + ":" + updatedAppointment.getStatus().name());
        // }
        return updatedAppointment;
    }

    // --- Remote Data Fetching Methods (for Frontend to pre-populate dropdowns/forms) ---
    /**
     * Fetches a list of registered vehicles for a given customer from the User
     * Profile Service.
     *
     * @param customerId The ID of the customer.
     * @return A list of RemoteVehicleDto objects.
     */
    public List<RemoteVehicleDto> getCustomerRegisteredVehicles(String customerId) {
        return userProfileServiceClient.getCustomerVehicles(customerId);
    }

    /**
     * Fetches a list of available modification options from the Modification
     * Catalog Service.
     *
     * @return A list of RemoteModificationItemDto objects.
     */
    public List<RemoteModificationItemDto> getAllRemoteModificationItems() {
        return modificationCatalogServiceClient.getAllModificationItems();
    }

    /**
     * Fetches available time slots for a specific date from the Time Slot
     * Service.
     *
     * @param date The date for which to retrieve time slots.
     * @return A list of TimeSlotResponseDto objects.
     */
    public List<TimeSlotResponseDto> getAvailableTimeSlots(LocalDate date) {
        List<RemoteTimeSlotDto> remoteSlots = timeSlotServiceClient.getAvailableTimeSlots(date);
        return remoteSlots.stream()
                .map(this::convertToTimeSlotResponseDto)
                .collect(Collectors.toList());
    }

    // --- Helper for DTO Conversion ---
    private TimeSlotResponseDto convertToTimeSlotResponseDto(RemoteTimeSlotDto remoteTimeSlot) {
        return new TimeSlotResponseDto(
                remoteTimeSlot.getId(),
                remoteTimeSlot.getDate(),
                remoteTimeSlot.getSlot(),
                remoteTimeSlot.isAvailable()
        );
    }
}

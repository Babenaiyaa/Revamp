package com.example.bookingservice.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.dto.BookingResponse;
import com.example.bookingservice.dto.TimeSlotResponseDto;
import com.example.bookingservice.dto.UpdateBookingStatusRequest;
import com.example.bookingservice.exception.ResourceNotFoundException;
import com.example.bookingservice.model.Appointment;
import com.example.bookingservice.model.RemoteModificationItemDto;
import com.example.bookingservice.model.RemoteVehicleDto;
import com.example.bookingservice.service.AppointmentService;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/bookings") // Base path for all endpoints in this controller
public class BookingController {

    private final AppointmentService appointmentService; // Inject your service layer

    // Constructor for dependency injection
    public BookingController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // --- Customer-Facing Endpoints ---
    /**
     * Endpoint for customers to create a new service appointment or
     * modification project booking.
     *
     * @param request The BookingRequest DTO containing all details.
     * @return ResponseEntity with BookingResponse and HTTP status (201 CREATED
     * on success, 400 BAD REQUEST on error).
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        try {
            BookingResponse response = appointmentService.createBooking(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            // Catch specific business logic errors and resource not found errors
            return ResponseEntity.badRequest().body(new BookingResponse(null, null, e.getMessage()));
        } catch (Exception e) {
            // Catch any other unexpected errors during booking creation
            System.err.println("Error creating booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BookingResponse(null, null, "An unexpected error occurred during booking creation."));
        }
    }

    /**
     * Endpoint for customers to view a specific booking's details.
     *
     * @param id The ID of the booking.
     * @return ResponseEntity with the Appointment object and HTTP status (200
     * OK, 404 NOT FOUND).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getBookingDetails(@PathVariable String id) {
        try {
            Appointment appointment = appointmentService.getBookingById(id);
            return ResponseEntity.ok(appointment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for customers to view all their bookings.
     *
     * @param customerId The ID of the customer.
     * @return ResponseEntity with a list of Appointment objects and HTTP status
     * (200 OK).
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Appointment>> getCustomerBookings(@PathVariable String customerId) {
        List<Appointment> bookings = appointmentService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Endpoint to get available time slots for a specific date from the
     * external Time Slot Service. This is called by the frontend when a
     * customer selects a date.
     *
     * @param date The date in YYYY-MM-DD format.
     * @return ResponseEntity with a list of TimeSlotResponseDto and HTTP status
     * (200 OK).
     */
    @GetMapping("/time-slots/available")
    public ResponseEntity<List<TimeSlotResponseDto>> getAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotResponseDto> timeSlots = appointmentService.getAvailableTimeSlots(date);
        return ResponseEntity.ok(timeSlots);
    }

    /**
     * Endpoint to get a list of registered vehicles for a customer from the
     * external User Profile Service. This is called by the frontend to
     * pre-populate vehicle selection for a customer.
     *
     * @param customerId The ID of the customer.
     * @return ResponseEntity with a list of RemoteVehicleDto and HTTP status
     * (200 OK).
     */
    @GetMapping("/customer/{customerId}/vehicles")
    public ResponseEntity<List<RemoteVehicleDto>> getCustomerRegisteredVehicles(@PathVariable String customerId) {
        List<RemoteVehicleDto> vehicles = appointmentService.getCustomerRegisteredVehicles(customerId);
        return ResponseEntity.ok(vehicles);
    }

    /**
     * Endpoint to get all available modification options from the external
     * Modification Catalog Service. This is called by the frontend to display
     * checkboxes for modification project booking.
     *
     * @return ResponseEntity with a list of RemoteModificationItemDto and HTTP
     * status (200 OK).
     */
    @GetMapping("/modification-options")
    public ResponseEntity<List<RemoteModificationItemDto>> getAllModificationOptions() {
        List<RemoteModificationItemDto> modificationItems = appointmentService.getAllRemoteModificationItems();
        return ResponseEntity.ok(modificationItems);
    }

    // --- Admin/Employee-Facing Endpoints ---
    // These would typically be secured with role-based access control (e.g., using Spring Security)
    /**
     * Endpoint for administrators to view all bookings that are currently in
     * 'PENDING' status.
     *
     * @return ResponseEntity with a list of Appointment objects and HTTP status
     * (200 OK).
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<Appointment>> getAllPendingBookings() {
        List<Appointment> pendingBookings = appointmentService.getAllPendingBookings();
        return ResponseEntity.ok(pendingBookings);
    }

    /**
     * Endpoint for administrators or employees to update the status of a
     * booking, assign an employee, or add admin remarks.
     *
     * @param id The ID of the booking to update.
     * @param request The UpdateBookingStatusRequest DTO with the new status and
     * other details.
     * @return ResponseEntity with the updated Appointment object and HTTP
     * status (200 OK, 404 NOT FOUND, 400 BAD REQUEST).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateBookingStatus(
            @PathVariable String id,
            @RequestBody UpdateBookingStatusRequest request) {
        try {
            Appointment updatedAppointment = appointmentService.updateBookingStatus(id, request);
            return ResponseEntity.ok(updatedAppointment);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build(); // Booking not found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Invalid status transition or data
        } catch (Exception e) {
            System.err.println("Error updating booking status for ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Unexpected error
        }
    }
}

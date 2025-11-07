package com.example.bookingservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.bookingservice.model.Appointment;

// MongoRepository provides basic CRUD operations for the Appointment document
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    // Custom query methods for fetching appointments
    List<Appointment> findByCustomerId(String customerId);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    List<Appointment> findByDate(LocalDate date);
    Optional<Appointment> findByDateAndTimeSlot(LocalDate date, String timeSlot); // To check unique service bookings
}
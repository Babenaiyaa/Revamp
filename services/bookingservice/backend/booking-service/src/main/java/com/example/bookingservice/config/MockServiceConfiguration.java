package com.example.bookingservice.config;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.bookingservice.dto.TimeSlotStatusUpdateRequest;
import com.example.bookingservice.model.RemoteModificationItemDto;
import com.example.bookingservice.model.RemoteTimeSlotDto;
import com.example.bookingservice.model.RemoteVehicleDto;
import com.example.bookingservice.service.client.ModificationCatalogServiceClient;
import com.example.bookingservice.service.client.TimeSlotServiceClient;
import com.example.bookingservice.service.client.UserProfileServiceClient;

/**
 * Mock implementations for development when other services are not available
 * Activate with: spring.profiles.active=mock-services
 */
@Configuration
@Profile("mock-services")
public class MockServiceConfiguration {

    @Bean
    public UserProfileServiceClient mockUserProfileServiceClient() {
        return new UserProfileServiceClient() {
            @Override
            public List<RemoteVehicleDto> getCustomerVehicles(String customerId) {
                // Return mock registered vehicles
                return Arrays.asList(
                        new RemoteVehicleDto("vehicle1", customerId, "Car", "Toyota", "Corolla", 2020, "ABC-1234"),
                        new RemoteVehicleDto("vehicle2", customerId, "Motorcycle", "Honda", "CBR600", 2019, "XYZ-5678")
                );
            }

            @Override
            public RemoteVehicleDto getVehicleById(String vehicleId) {
                return new RemoteVehicleDto(vehicleId, "customer123", "Car", "Toyota", "Corolla", 2020, "ABC-1234");
            }
        };
    }

    @Bean
    public TimeSlotServiceClient mockTimeSlotServiceClient() {
        return new TimeSlotServiceClient() {
            @Override
            public List<RemoteTimeSlotDto> getAvailableTimeSlots(LocalDate date) {
                // Return mock available time slots
                return Arrays.asList(
                        new RemoteTimeSlotDto("slot1", date, "09:00-10:00", true),
                        new RemoteTimeSlotDto("slot2", date, "10:00-11:00", true),
                        new RemoteTimeSlotDto("slot3", date, "14:00-15:00", false), // Not available
                        new RemoteTimeSlotDto("slot4", date, "15:00-16:00", true)
                );
            }

            @Override
            public Optional<RemoteTimeSlotDto> getTimeSlotByDateAndSlot(LocalDate date, String slot) {
                return Optional.of(new RemoteTimeSlotDto("slot1", date, slot, true));
            }

            @Override
            public RemoteTimeSlotDto updateTimeSlotStatus(String id, TimeSlotStatusUpdateRequest request) {
                System.out.println("MOCK: Updated time slot " + id + " availability to: " + request.isAvailable());
                return new RemoteTimeSlotDto(id, LocalDate.now(), "09:00-10:00", request.isAvailable());
            }
        };
    }

    @Bean
    public ModificationCatalogServiceClient mockModificationCatalogServiceClient() {
        return new ModificationCatalogServiceClient() {
            @Override
            public List<RemoteModificationItemDto> getAllModificationItems() {
                // Return mock modification options
                return Arrays.asList(
                        new RemoteModificationItemDto("mod1", "Engine Upgrade", "High performance engine modification", 1500.0, 8.0),
                        new RemoteModificationItemDto("mod2", "Paint Job", "Custom paint and finish", 800.0, 4.0),
                        new RemoteModificationItemDto("mod3", "Interior Upgrade", "Leather seats and premium interior", 1200.0, 6.0),
                        new RemoteModificationItemDto("mod4", "Sound System", "Premium audio system installation", 600.0, 3.0),
                        new RemoteModificationItemDto("mod5", "Suspension Upgrade", "Sport suspension and handling", 900.0, 5.0)
                );
            }

            @Override
            public RemoteModificationItemDto getModificationItemById(String id) {
                switch (id) {
                    case "mod1":
                        return new RemoteModificationItemDto("mod1", "Engine Upgrade", "High performance engine modification", 1500.0, 8.0);
                    case "mod2":
                        return new RemoteModificationItemDto("mod2", "Paint Job", "Custom paint and finish", 800.0, 4.0);
                    case "mod3":
                        return new RemoteModificationItemDto("mod3", "Interior Upgrade", "Leather seats and premium interior", 1200.0, 6.0);
                    default:
                        return null;
                }
            }
        };
    }
}

package com.example.bookingservice.service.client;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bookingservice.model.RemoteVehicleDto;

// Feign client for interacting with the User Profile Service
@FeignClient(name = "${user-profile-service.name}", fallback = UserProfileServiceFallback.class)
public interface UserProfileServiceClient {

    // Assumed API endpoint to get all registered vehicles for a specific customer
    @GetMapping("/api/customers/{customerId}/vehicles")
    List<RemoteVehicleDto> getCustomerVehicles(@PathVariable("customerId") String customerId);

    // Assumed API endpoint to get details of a single vehicle by its ID
    @GetMapping("/api/vehicles/{vehicleId}")
    RemoteVehicleDto getVehicleById(@PathVariable("vehicleId") String vehicleId);
}

// Fallback implementation for UserProfileServiceClient
// This is invoked if the User Profile Service is unreachable or throws an error
class UserProfileServiceFallback implements UserProfileServiceClient {
    @Override
    public List<RemoteVehicleDto> getCustomerVehicles(String customerId) {
        System.err.println("FALLBACK: UserProfileService - Could not retrieve vehicles for customer " + customerId);
        return Collections.emptyList(); // Return empty list to prevent application crash
    }

    @Override
    public RemoteVehicleDto getVehicleById(String vehicleId) {
        System.err.println("FALLBACK: UserProfileService - Could not retrieve vehicle " + vehicleId);
        return null; // Return null to indicate the vehicle could not be fetched
    }
}
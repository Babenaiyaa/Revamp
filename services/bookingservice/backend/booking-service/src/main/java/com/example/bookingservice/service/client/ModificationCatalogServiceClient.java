package com.example.bookingservice.service.client;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bookingservice.model.RemoteModificationItemDto;

// Feign client for interacting with the Modification Catalog Service
@FeignClient(name = "${modification-catalog-service.name}", fallback = ModificationCatalogServiceFallback.class)
public interface ModificationCatalogServiceClient {

    // Assumed API endpoint to get all available modification items
    @GetMapping("/api/modifications")
    List<RemoteModificationItemDto> getAllModificationItems();

    // Assumed API endpoint to get details of a specific modification item by its ID
    @GetMapping("/api/modifications/{id}")
    RemoteModificationItemDto getModificationItemById(@PathVariable("id") String id);
}

// Fallback implementation for ModificationCatalogServiceClient
// This is invoked if the Modification Catalog Service is unreachable or throws an error
class ModificationCatalogServiceFallback implements ModificationCatalogServiceClient {
    @Override
    public List<RemoteModificationItemDto> getAllModificationItems() {
        System.err.println("FALLBACK: ModificationCatalogService - Could not retrieve all modification items.");
        return Collections.emptyList(); // Return empty list to prevent application crash
    }

    @Override
    public RemoteModificationItemDto getModificationItemById(String id) {
        System.err.println("FALLBACK: ModificationCatalogService - Could not retrieve modification item with ID: " + id);
        return null; // Return null to indicate the item could not be fetched
    }
}
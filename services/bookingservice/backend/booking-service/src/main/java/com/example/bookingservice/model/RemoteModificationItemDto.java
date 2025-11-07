package com.example.bookingservice.model;

// DTO representing a modification item received from the external Modification Catalog Service
// This is NOT a @Document as it's not stored in your booking service's database
public class RemoteModificationItemDto {

    private String id;
    private String name;        // e.g., "ENGINE_UPGRADE", "CUSTOM_PAINT"
    private String description;
    private Double baseCost;
    private Double baseDurationHours;

    // Constructors
    public RemoteModificationItemDto() {
    }

    public RemoteModificationItemDto(String id, String name, String description, Double baseCost, Double baseDurationHours) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseCost = baseCost;
        this.baseDurationHours = baseDurationHours;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Double baseCost) {
        this.baseCost = baseCost;
    }

    public Double getBaseDurationHours() {
        return baseDurationHours;
    }

    public void setBaseDurationHours(Double baseDurationHours) {
        this.baseDurationHours = baseDurationHours;
    }
}

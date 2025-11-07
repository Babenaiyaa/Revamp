package com.example.bookingservice.model;

// DTO representing a vehicle received from the external User Profile Service
// This is NOT a @Document as it's not stored in your booking service's database
public class RemoteVehicleDto {

    private String id; // The unique ID of the vehicle in the external User Profile Service
    private String customerId; // Added to know which customer owns this vehicle
    private String type; // e.g., Car, Truck, Motorcycle
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    // The external service might also include customerId, VIN, etc., but this is sufficient for your needs

    // Constructors
    public RemoteVehicleDto() {
    }

    public RemoteVehicleDto(String id, String customerId, String type, String brand, String model, Integer year, String licensePlate) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
    }

    // Getters and Setters
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

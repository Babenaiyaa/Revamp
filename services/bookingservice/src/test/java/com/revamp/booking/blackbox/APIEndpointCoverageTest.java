package com.revamp.booking.blackbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * API Coverage Tests for Booking Service Tests all REST API endpoints using
 * Spring TestRestTemplate
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(MockConfiguration.class)
public class APIEndpointCoverageTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testCreateAppointmentEndpoint() {
        String appointmentJson = """
            {
                "customerName": "Test User",
                "customerId": "test123",
                "customerEmail": "test@example.com",
                "customerPhone": "1234567890",
                "vehicleId": "vehicle123",
                "vehiclePlateNumber": "TEST123",
                "vehicleModel": "Test Car",
                "serviceType": "Modification",
                "date": "2025-12-01",
                "services": ["Test Service"],
                "cost": 100.0,
                "description": "Test appointment"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(appointmentJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/api/bookings/appointments", entity, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError(),
                "Create appointment should return 2xx or 4xx, got: " + response.getStatusCode());
    }

    @Test
    void testGetAllAppointmentsEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/appointments", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Get all appointments should return 200");
        assertNotNull(response.getBody(), "Response body should not be null");
    }

    @Test
    void testGetAppointmentByIdEndpoint() {
        String testId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/appointments/" + testId, String.class);

        assertTrue(response.getStatusCode() == HttpStatus.OK
                || response.getStatusCode() == HttpStatus.NOT_FOUND
                || response.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Get appointment by ID should return 200, 404, or 400, got: " + response.getStatusCode());
    }

    @Test
    void testGetAppointmentsByCustomerIdEndpoint() {
        String customerId = "testCustomer123";

        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/appointments/customer/" + customerId, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Get appointments by customer ID should return 200");
        assertNotNull(response.getBody(), "Response body should not be null");
    }

    @Test
    void testUpdateAppointmentStatusEndpoint() {
        String testId = "507f1f77bcf86cd799439011"; // Valid ObjectId format
        String statusJson = """
            {
                "status": "Approved"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(statusJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/bookings/appointments/" + testId + "/status",
                HttpMethod.PUT, entity, String.class);

        assertTrue(response.getStatusCode() == HttpStatus.OK
                || response.getStatusCode() == HttpStatus.NOT_FOUND
                || response.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Update appointment status should return 200, 404, or 400, got: " + response.getStatusCode());
    }

    @Test
    void testAssignEmployeesEndpoint() {
        String testId = "507f1f77bcf86cd799439011"; // Valid ObjectId format
        String employeesJson = """
            {
                "employeeIds": ["emp001", "emp002"],
                "employeeNames": ["John Doe", "Jane Smith"]
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(employeesJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/bookings/appointments/" + testId + "/assign-employees",
                HttpMethod.PUT, entity, String.class);

        assertTrue(response.getStatusCode() == HttpStatus.OK
                || response.getStatusCode() == HttpStatus.NOT_FOUND
                || response.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Assign employees should return 200, 404, or 400, got: " + response.getStatusCode());
    }

    @Test
    void testCancelAppointmentEndpoint() {
        String testId = "507f1f77bcf86cd799439011"; // Valid ObjectId format

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/bookings/appointments/" + testId,
                HttpMethod.DELETE, null, String.class);

        assertTrue(response.getStatusCode() == HttpStatus.OK
                || response.getStatusCode() == HttpStatus.NOT_FOUND,
                "Cancel appointment should return 200 or 404, got: " + response.getStatusCode());
    }

    @Test
    void testGetAppointmentsByDateRangeEndpoint() {
        String url = getBaseUrl() + "/api/bookings/appointments/range?startDate=2025-11-01&endDate=2025-12-31";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertTrue(response.getStatusCode() == HttpStatus.OK
                || response.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Get appointments by date range should return 200 or 400, got: " + response.getStatusCode());
    }

    @Test
    void testValidateBookingEndpoint() {
        String validationJson = """
            {
                "serviceType": "Modification",
                "date": "2025-12-01"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(validationJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/api/bookings/appointments/validate", entity, String.class);

        assertTrue(response.getStatusCode() == HttpStatus.OK
                || response.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Validate booking should return 200 or 400, got: " + response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
    }

    @Test
    void testTimeSlotEndpoints() {
        // Test GET time slots
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/timeslots?date=2025-12-01", String.class);

        assertTrue(getResponse.getStatusCode() == HttpStatus.OK
                || getResponse.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Get time slots should return 200 or 400, got: " + getResponse.getStatusCode());

        // Test POST create time slots
        String timeSlotsJson = """
            {
                "date": "2025-12-01"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(timeSlotsJson, headers);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/bookings/timeslots", entity, String.class);

        assertTrue(postResponse.getStatusCode() == HttpStatus.OK
                || postResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || postResponse.getStatusCode() == HttpStatus.CONFLICT,
                "Create time slots should return 200, 400, or 409, got: " + postResponse.getStatusCode());
    }

    @Test
    void testUnavailableDateEndpoints() {
        // Test GET unavailable dates
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/unavailable-dates", String.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode(),
                "Get unavailable dates should return 200");

        // Test POST add unavailable date
        String unavailableDateJson = """
            {
                "date": "2025-12-25",
                "reason": "Christmas Holiday",
                "description": "Shop closed for Christmas"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(unavailableDateJson, headers);

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/bookings/unavailable-dates", entity, String.class);

        assertTrue(postResponse.getStatusCode() == HttpStatus.OK
                || postResponse.getStatusCode() == HttpStatus.BAD_REQUEST
                || postResponse.getStatusCode() == HttpStatus.CONFLICT,
                "Add unavailable date should return 200, 400, or 409, got: " + postResponse.getStatusCode());
    }

    @Test
    void testInvalidEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/invalid-endpoint", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Invalid endpoint should return 404");
    }

    @Test
    void testMalformedJsonHandling() {
        String malformedJson = """
            {
                "customerName": "Test User"
                "serviceType": "Service"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(malformedJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/api/bookings/appointments", entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Malformed JSON should return 400");
    }

    @Test
    void testHealthCheck() {
        // Test if the service is running and responding
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/api/bookings/appointments", String.class);

        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(),
                "Service should not return 500 error - indicates service is running");
    }
}

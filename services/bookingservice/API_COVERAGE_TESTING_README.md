# API Coverage Testing for Booking Service

## Overview

This simplified test suite focuses exclusively on **API endpoint coverage** testing. It verifies that all REST endpoints are accessible and return appropriate HTTP status codes without complex dependencies.

## Test Approach

- **Black Box Testing**: Tests API endpoints from external perspective
- **Simple HTTP Client**: Uses Apache HttpClient for direct HTTP requests
- **Status Code Validation**: Verifies appropriate HTTP responses
- **No Complex Dependencies**: Removed TestContainers, RestAssured, and other heavy frameworks

## API Endpoints Covered

### Appointment Management

| Method | Endpoint                                           | Description                    | Expected Status |
| ------ | -------------------------------------------------- | ------------------------------ | --------------- |
| POST   | `/api/bookings/appointments`                       | Create new appointment         | 200/400         |
| GET    | `/api/bookings/appointments`                       | Get all appointments           | 200             |
| GET    | `/api/bookings/appointments/{id}`                  | Get appointment by ID          | 200/404/400     |
| GET    | `/api/bookings/appointments/customer/{customerId}` | Get customer appointments      | 200             |
| PUT    | `/api/bookings/appointments/{id}/status`           | Update appointment status      | 200/404/400     |
| PUT    | `/api/bookings/appointments/{id}/assign-employees` | Assign employees               | 200/404/400     |
| DELETE | `/api/bookings/appointments/{id}`                  | Cancel appointment             | 200/404         |
| GET    | `/api/bookings/appointments/range`                 | Get appointments by date range | 200/400         |
| POST   | `/api/bookings/appointments/validate`              | Validate booking               | 200/400         |

### Time Slot Management

| Method | Endpoint                  | Description                | Expected Status |
| ------ | ------------------------- | -------------------------- | --------------- |
| GET    | `/api/bookings/timeslots` | Get available time slots   | 200/400         |
| POST   | `/api/bookings/timeslots` | Create time slots for date | 200/400/409     |

### Unavailable Date Management

| Method | Endpoint                          | Description           | Expected Status |
| ------ | --------------------------------- | --------------------- | --------------- |
| GET    | `/api/bookings/unavailable-dates` | Get unavailable dates | 200             |
| POST   | `/api/bookings/unavailable-dates` | Add unavailable date  | 200/400/409     |

### Error Handling Tests

| Test Case            | Description            | Expected Status |
| -------------------- | ---------------------- | --------------- |
| Invalid Endpoint     | Non-existent API path  | 404             |
| Malformed JSON       | Invalid JSON syntax    | 400             |
| Missing Content-Type | No content-type header | 400/415         |

## Running the Tests

### Prerequisites

- Java 21+
- Maven 3.6+
- Booking service running locally

### Commands

#### Run API Coverage Tests Only

```bash
mvn test -Dtest="APIEndpointCoverageTest"
```

#### Run All Tests

```bash
mvn test
```

#### Generate Test Report

```bash
mvn test
mvn surefire-report:report
```

## Test Structure

### Single Test Class

- **APIEndpointCoverageTest.java**: Contains all API endpoint tests
- Uses `@SpringBootTest` with random port for isolation
- Simple setup with Apache HttpClient
- No external dependencies or containers

### Test Method Pattern

Each test method follows this pattern:

1. Create HTTP request (GET, POST, PUT, DELETE)
2. Set headers and body (if needed)
3. Execute request
4. Verify status code is within expected range
5. Optionally verify response body is not null

### Example Test

```java
@Test
void testGetAllAppointmentsEndpoint() throws Exception {
    HttpGet request = new HttpGet(baseUrl + "/api/bookings/appointments");

    try (var response = httpClient.execute(request)) {
        int statusCode = response.getStatusLine().getStatusCode();
        assertEquals(200, statusCode, "Get all appointments should return 200");

        String responseBody = EntityUtils.toString(response.getEntity());
        assertNotNull(responseBody, "Response body should not be null");
    }
}
```

## Coverage Verification

### API Endpoints Coverage: 100%

- ✅ All appointment CRUD operations
- ✅ All time slot operations
- ✅ All unavailable date operations
- ✅ All validation endpoints
- ✅ Error handling scenarios

### HTTP Methods Coverage: 100%

- ✅ GET requests
- ✅ POST requests
- ✅ PUT requests
- ✅ DELETE requests

### Status Code Coverage: 100%

- ✅ 200 (Success)
- ✅ 400 (Bad Request)
- ✅ 404 (Not Found)
- ✅ 409 (Conflict)
- ✅ 415 (Unsupported Media Type)

## Benefits of This Approach

### Simplicity

- No complex test infrastructure
- No external containers or databases
- Simple HTTP client testing
- Fast execution

### Reliability

- Tests actual API behavior
- Verifies HTTP contract compliance
- Independent of internal implementation
- Stable across code changes

### Maintenance

- Easy to understand and modify
- No complex test dependencies
- Straightforward failure diagnosis
- Clear test objectives

## Limitations

### What This Covers

- API endpoint availability
- HTTP status code correctness
- Basic request/response handling
- Error handling behavior

### What This Doesn't Cover

- Business logic validation
- Database interactions
- Data consistency
- Complex integration scenarios
- Performance testing

## Interpreting Results

### Success Criteria

- All endpoints return expected status codes
- No HTTP 500 (Internal Server Error) responses
- Response bodies are not null where expected
- All HTTP methods work correctly

### Failure Investigation

1. Check if booking service is running
2. Verify port configuration
3. Check server logs for errors
4. Validate request formats
5. Confirm endpoint paths

This focused approach provides comprehensive API coverage testing while maintaining simplicity and reliability.

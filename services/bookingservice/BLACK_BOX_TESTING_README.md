# Black Box Testing for Booking Service

## Overview

This document outlines the black box testing strategy implemented for the Booking Service. Black box testing focuses on validating system functionality from an external perspective without knowledge of internal implementation details.

## Test Structure

### Base Classes

- **BaseBlackBoxTest**: Abstract base class providing common setup and helper methods
  - Configures TestContainers for isolated MongoDB instance
  - Sets up REST Assured for API testing
  - Provides helper methods for creating test data

### Test Categories

#### 1. Appointment Creation Tests (`AppointmentCreationBlackBoxTest`)

- **Positive Tests:**

  - Valid service appointments with time slots
  - Valid modification appointments
  - Proper status assignment (Pending)

- **Negative Tests:**
  - Missing required fields
  - Invalid date formats
  - Sunday booking attempts
  - Service appointments without time slots
  - Malformed JSON handling

#### 2. Appointment Retrieval Tests (`AppointmentRetrievalBlackBoxTest`)

- **Single Record Tests:**

  - Retrieve by valid ID
  - Handle non-existent IDs (404 responses)
  - Invalid ID format handling

- **Collection Tests:**
  - Retrieve all appointments
  - Filter by customer ID
  - Date range queries
  - Empty result handling

#### 3. Appointment Management Tests (`AppointmentManagementBlackBoxTest`)

- **Status Updates:**

  - Approved, Completed, Rejected status changes
  - Invalid appointment ID handling
  - Empty status handling

- **Employee Assignment:**

  - Valid employee assignment
  - Mismatched employee data handling
  - Non-existent appointment handling

- **Cancellation:**
  - Successful appointment deletion
  - Graceful handling of non-existent appointments

#### 4. Booking Validation Tests (`BookingValidationBlackBoxTest`)

- **Business Rule Validation:**

  - Sunday booking restrictions
  - Time slot requirements for services
  - Date format validation
  - Missing data handling

- **Edge Cases:**
  - Past date bookings
  - Unknown service types
  - Non-existent time slots

#### 5. Integration Tests (`BookingIntegrationBlackBoxTest`)

- **End-to-End Workflows:**

  - Complete appointment lifecycle
  - Cancellation workflows
  - Multiple appointments per customer
  - Validation before creation

- **Concurrent Operations:**
  - Simultaneous appointment creation
  - Data consistency verification

#### 6. Performance Tests (`BookingPerformanceBlackBoxTest`)

- **Load Testing:**

  - Rapid sequential operations
  - Concurrent request handling
  - Large dataset queries

- **Response Time Validation:**
  - API response time limits
  - Performance under varying loads
  - Efficiency with growing datasets

## Test Data Management

### Test Isolation

- Each test class uses TestContainers to provide isolated MongoDB instances
- Tests are designed to be independent and can run in any order
- No shared state between test methods

### Helper Methods

Base class provides standardized test data creation:

- `createValidServiceAppointment()`
- `createValidModificationAppointment()`
- `createInvalidAppointment()`
- `createSundayAppointment()`

## Running the Tests

### Prerequisites

- Docker installed and running (for TestContainers)
- Maven 3.6+ or Gradle
- Java 21+

### Execution Commands

#### Run All Black Box Tests

```bash
mvn test -Dtest="com.revamp.booking.blackbox.**"
```

#### Run Specific Test Class

```bash
mvn test -Dtest="AppointmentCreationBlackBoxTest"
```

#### Run Test Suite

```bash
mvn test -Dtest="BlackBoxTestSuite"
```

#### Generate Test Report

```bash
mvn surefire-report:report
```

### Test Configuration

- Test configuration: `src/test/resources/application-test.properties`
- Separate test profile with appropriate timeouts and logging
- TestContainers automatically handles database setup/teardown

## Test Coverage Areas

### Functional Coverage

- ✅ Appointment CRUD operations
- ✅ Business rule validation
- ✅ Error handling and edge cases
- ✅ Data format validation
- ✅ Status workflow management
- ✅ Employee assignment functionality

### Non-Functional Coverage

- ✅ Performance and response times
- ✅ Concurrent operation handling
- ✅ Data consistency
- ✅ Error recovery
- ✅ Load capacity

### API Coverage

- ✅ POST /api/bookings/appointments
- ✅ GET /api/bookings/appointments
- ✅ GET /api/bookings/appointments/{id}
- ✅ GET /api/bookings/appointments/customer/{customerId}
- ✅ GET /api/bookings/appointments/range
- ✅ PUT /api/bookings/appointments/{id}/status
- ✅ PUT /api/bookings/appointments/{id}/assign-employees
- ✅ DELETE /api/bookings/appointments/{id}
- ✅ POST /api/bookings/appointments/validate

## Expected Behaviors

### Success Scenarios

- Valid appointments return 200 with proper data structure
- Successful operations include all required fields in responses
- Status changes reflect immediately in subsequent queries
- Cancellation properly removes appointments from system

### Error Scenarios

- Invalid requests return 400 with descriptive error messages
- Non-existent resources return 404
- Malformed JSON returns 400
- Business rule violations return 400 with specific messages

### Performance Expectations

- Individual API calls complete within 5 seconds
- Batch operations scale reasonably with data size
- Concurrent operations maintain data consistency
- System remains responsive under moderate load

## Continuous Integration

### CI Pipeline Integration

Tests are designed to run in CI/CD pipelines:

- Isolated test environment via TestContainers
- No external dependencies beyond Docker
- Deterministic test results
- Comprehensive logging for debugging

### Test Reporting

- JUnit XML reports for CI integration
- Detailed assertion messages for debugging
- Performance metrics logging
- Coverage reports integration

## Maintenance

### Adding New Tests

1. Extend appropriate test class or create new one
2. Follow naming convention: `should[ExpectedBehavior]When[Condition]()`
3. Use helper methods for test data creation
4. Include both positive and negative test cases
5. Add performance considerations if applicable

### Test Data Updates

- Update helper methods in BaseBlackBoxTest for schema changes
- Ensure backward compatibility with existing tests
- Document any new test data requirements

## Troubleshooting

### Common Issues

1. **TestContainer startup failures**: Ensure Docker is running
2. **Port conflicts**: Tests use random ports, but check for Docker port issues
3. **Timeout failures**: Adjust timeout values in test configuration
4. **MongoDB connection issues**: Verify TestContainer MongoDB setup

### Debug Tips

- Enable DEBUG logging in test configuration
- Use REST Assured logging: `RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()`
- Check TestContainer logs for database issues
- Verify test data setup in beforeEach methods

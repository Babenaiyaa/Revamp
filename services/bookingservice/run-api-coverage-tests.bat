@echo off
REM API Coverage Test Runner for Booking Service
REM Simple script to run only API endpoint coverage tests

echo =========================================
echo Booking Service - API Coverage Testing
echo =========================================

REM Navigate to booking service directory
cd /d "%~dp0"

echo 📁 Current directory: %cd%

REM Check if the service is compiled
echo 🔍 Checking if service is compiled...
if not exist "target\classes" (
    echo 📦 Compiling service first...
    call mvn compile -q
    if errorlevel 1 (
        echo ❌ Compilation failed. Please fix compilation errors first.
        exit /b 1
    )
)

echo ✅ Service compiled successfully

REM Run API coverage tests only
echo 🚀 Running API Coverage Tests...
echo Testing all REST endpoints...

call mvn test -Dtest="APIEndpointCoverageTest" -q

set TEST_EXIT_CODE=%errorlevel%

REM Check test results
if %TEST_EXIT_CODE% equ 0 (
    echo ✅ All API Coverage Tests PASSED!
    echo 📊 All REST endpoints are accessible and responding correctly
) else (
    echo ❌ Some API Coverage Tests FAILED!
    echo 📋 Check the output above for details
)

REM Display test summary
echo.
echo =========================================
echo API Coverage Test Summary
echo =========================================
echo Exit Code: %TEST_EXIT_CODE%
echo Endpoints Tested:
echo   - POST /api/bookings/appointments
echo   - GET  /api/bookings/appointments
echo   - GET  /api/bookings/appointments/{id}
echo   - PUT  /api/bookings/appointments/{id}/status
echo   - PUT  /api/bookings/appointments/{id}/assign-employees
echo   - DELETE /api/bookings/appointments/{id}
echo   - GET  /api/bookings/appointments/range
echo   - POST /api/bookings/appointments/validate
echo   - GET  /api/bookings/timeslots
echo   - POST /api/bookings/timeslots
echo   - GET  /api/bookings/unavailable-dates
echo   - POST /api/bookings/unavailable-dates
echo =========================================

exit /b %TEST_EXIT_CODE%
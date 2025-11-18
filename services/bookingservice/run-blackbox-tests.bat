@echo off
REM Black Box Testing Runner Script for Booking Service (Windows)
REM This script runs all black box tests and generates reports

echo =========================================
echo Booking Service - Black Box Test Runner
echo =========================================

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker is not running. Please start Docker and try again.
    exit /b 1
)

echo ✅ Docker is running

REM Navigate to booking service directory
cd /d "%~dp0"

echo 📁 Current directory: %cd%

REM Clean previous test results
echo 🧹 Cleaning previous test results...
call mvn clean -q

REM Run all black box tests
echo 🚀 Running Black Box Tests...
echo This may take a few minutes as TestContainers need to start...

call mvn test -Dtest="com.revamp.booking.blackbox.**" -Dspring.profiles.active=test

set TEST_EXIT_CODE=%errorlevel%

REM Generate test report
echo 📊 Generating test reports...
call mvn surefire-report:report -q

REM Check test results
if %TEST_EXIT_CODE% equ 0 (
    echo ✅ All Black Box Tests PASSED!
    echo 📄 Test report available at: target\site\surefire-report.html
) else (
    echo ❌ Some Black Box Tests FAILED!
    echo 📄 Check the test report at: target\site\surefire-report.html
    echo 📋 Detailed logs available at: target\surefire-reports\
)

REM Display test summary
echo.
echo =========================================
echo Test Execution Summary
echo =========================================
echo Exit Code: %TEST_EXIT_CODE%
echo Report Location: target\site\surefire-report.html
echo Detailed Logs: target\surefire-reports\
echo =========================================

exit /b %TEST_EXIT_CODE%
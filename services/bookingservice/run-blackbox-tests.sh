#!/bin/bash

# Black Box Testing Runner Script for Booking Service
# This script runs all black box tests and generates reports

echo "========================================="
echo "Booking Service - Black Box Test Runner"
echo "========================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker and try again."
    exit 1
fi

echo "✅ Docker is running"

# Navigate to booking service directory
cd "$(dirname "$0")"

echo "📁 Current directory: $(pwd)"

# Clean previous test results
echo "🧹 Cleaning previous test results..."
mvn clean -q

# Run all black box tests
echo "🚀 Running Black Box Tests..."
echo "This may take a few minutes as TestContainers need to start..."

mvn test -Dtest="com.revamp.booking.blackbox.**" -Dspring.profiles.active=test

TEST_EXIT_CODE=$?

# Generate test report
echo "📊 Generating test reports..."
mvn surefire-report:report -q

# Check test results
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✅ All Black Box Tests PASSED!"
    echo "📄 Test report available at: target/site/surefire-report.html"
else
    echo "❌ Some Black Box Tests FAILED!"
    echo "📄 Check the test report at: target/site/surefire-report.html"
    echo "📋 Detailed logs available at: target/surefire-reports/"
fi

# Display test summary
echo ""
echo "========================================="
echo "Test Execution Summary"
echo "========================================="
echo "Exit Code: $TEST_EXIT_CODE"
echo "Report Location: target/site/surefire-report.html"
echo "Detailed Logs: target/surefire-reports/"
echo "========================================="

exit $TEST_EXIT_CODE
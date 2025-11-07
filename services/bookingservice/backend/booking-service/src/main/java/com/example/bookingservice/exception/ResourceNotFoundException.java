package com.example.bookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Custom exception for when a requested resource is not found (maps to HTTP 404)
@ResponseStatus(HttpStatus.NOT_FOUND) // Spring will map this exception to a 404 HTTP status
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
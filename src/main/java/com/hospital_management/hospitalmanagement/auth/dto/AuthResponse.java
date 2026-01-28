package com.hospital_management.hospitalmanagement.auth.dto;

public record AuthResponse(
        String token,

        String message
) {
}

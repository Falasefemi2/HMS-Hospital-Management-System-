package com.hospital_management.hospitalmanagement.auth.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}

package com.hospital_management.hospitalmanagement.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChangeTempPasswordRequest(
        @NotNull(message = "Staff Id cannot be blank")
        UUID staffId,

        @NotBlank(message = "Temporary password is required")
        String tempPassword,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least eight characters")
        String password
) {
}

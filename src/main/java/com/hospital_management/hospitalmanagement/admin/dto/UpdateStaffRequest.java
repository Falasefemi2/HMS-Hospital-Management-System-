package com.hospital_management.hospitalmanagement.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateStaffRequest(
        @NotBlank(message = "Full name is required")
        String fullname,

        @Email
        @NotBlank(message = "Email is required")
        String email
) {
}

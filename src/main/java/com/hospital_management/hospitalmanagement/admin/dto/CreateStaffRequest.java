package com.hospital_management.hospitalmanagement.admin.dto;

import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStaffRequest(
        @NotBlank(message = "Full name is required")
        String fullname,

        @Email
        @NotBlank(message = "Email is required")
        String email,

        @NotNull
        Role role,

        @NotNull(message = "Department cannot be empty")
        UUID departmentId
) {
}

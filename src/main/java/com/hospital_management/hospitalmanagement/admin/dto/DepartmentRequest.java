package com.hospital_management.hospitalmanagement.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "Department name is required")
        String name
) {
}

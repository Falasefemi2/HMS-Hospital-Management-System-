package com.hospital_management.hospitalmanagement.admin.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateStaffDepartment(
        @NotNull(message = "Staff Id cannot be blank")
        UUID staffId,

        @NotNull(message = "Department cannot be empty")
        UUID departmentId
) {
}

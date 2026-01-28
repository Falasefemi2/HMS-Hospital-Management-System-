package com.hospital_management.hospitalmanagement.admin.dto;

import com.hospital_management.hospitalmanagement.roles.Role;

import java.util.List;
import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String name,
        int doctorCount,
        int nurseCount
) {
}

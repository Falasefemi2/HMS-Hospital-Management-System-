package com.hospital_management.hospitalmanagement.admin.dto;

import java.util.UUID;

public record StaffDepartmentResponse(
        UUID staffId,

        String fullname,

        String name,

        UUID id
) {
}

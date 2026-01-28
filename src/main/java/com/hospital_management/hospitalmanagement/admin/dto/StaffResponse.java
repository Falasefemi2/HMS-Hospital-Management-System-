package com.hospital_management.hospitalmanagement.admin.dto;

import com.hospital_management.hospitalmanagement.roles.Role;

import java.util.UUID;

public record StaffResponse(
        UUID id,

        String name,

        String email,

        Role role,

        boolean active
) {
}

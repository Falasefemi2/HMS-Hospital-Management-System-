package com.hospital_management.hospitalmanagement.roles;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_ADMIN,
    ROLE_DOCTOR,
    ROLE_LAB_TECH,
    ROLE_PATIENT;

    @JsonCreator
    public static Role from(String role) {
        String normalized = role.toUpperCase();

        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }

        return Role.valueOf(normalized);
    }
}

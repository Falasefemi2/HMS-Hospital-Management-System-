package com.hospital_management.hospitalmanagement.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProfileRequest(
        @NotNull(message = "Phone number is required")
        String phone,

        @NotBlank(message = "Next ok kin is required")
        String nextOfKin,

        @NotNull(message = "Next ok kin phone number is required")
        String nextOfKinContact
) {
}

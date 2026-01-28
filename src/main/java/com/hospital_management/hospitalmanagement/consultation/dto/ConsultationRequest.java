package com.hospital_management.hospitalmanagement.consultation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConsultationRequest(
        @NotNull(message = "Appointment id is required")
        UUID appointmentId,

        @NotBlank(message = "Notes is required")
        String notes,

        @NotBlank(message = "Diagnoses cannot be blank")
        String diagnoses
) {
}

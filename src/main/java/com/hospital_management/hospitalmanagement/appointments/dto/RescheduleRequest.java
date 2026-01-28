package com.hospital_management.hospitalmanagement.appointments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleRequest(
        @NotNull
        LocalDateTime newDateTime
) {
}

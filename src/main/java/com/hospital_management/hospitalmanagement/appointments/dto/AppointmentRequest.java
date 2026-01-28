package com.hospital_management.hospitalmanagement.appointments.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull
        UUID departmentId,

        @NotNull
        UUID doctorId,

        @NotNull
        LocalDateTime appointmentTime
) {
}

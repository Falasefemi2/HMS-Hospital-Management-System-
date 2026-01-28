package com.hospital_management.hospitalmanagement.appointments.dto;

import com.hospital_management.hospitalmanagement.appointments.enumFolder.Status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientAppointmentResponse(
        UUID appointmentId,

        String appUserName,

        String doctorNamee,

        LocalDateTime dateTime,

        Enum<Status> status,

        Instant createdAt
) {
}

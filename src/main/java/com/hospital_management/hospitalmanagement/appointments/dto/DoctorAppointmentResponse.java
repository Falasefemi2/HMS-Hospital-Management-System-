package com.hospital_management.hospitalmanagement.appointments.dto;

import com.hospital_management.hospitalmanagement.appointments.enumFolder.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record DoctorAppointmentResponse(
        UUID appointmentId,

        String doctorName,

        UUID patientId,

        LocalDateTime date,

        Enum<Status> status
) {
}

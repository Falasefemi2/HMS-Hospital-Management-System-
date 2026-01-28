package com.hospital_management.hospitalmanagement.consultation.dto;

import java.time.Instant;
import java.util.UUID;

public record ConsultationResponse(
        UUID consultationId,

        UUID appointmentId,

        String doctorFullName,

        UUID patientId,

        String notes,

        String diagnosis,

        Instant createdAt

) {
}

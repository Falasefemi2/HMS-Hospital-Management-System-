package com.hospital_management.hospitalmanagement.prescriptions.dto;

import java.time.Instant;
import java.util.UUID;

public record PrescriptionResponse(
        UUID prescriptionId,

        UUID consultationId,

        UUID patientId,

        String patientName,

        UUID doctorId,

        String doctorName,

        String medication,

        String dosage,

        String duration,

        Instant createdAt
) {
}

package com.hospital_management.hospitalmanagement.prescriptions.dto;

import java.time.Instant;
import java.util.UUID;

public record PatientPrescriptionResponse(
        UUID prescription_id,

        String patientName,

        String doctorName,

        String medication,

        String dosage,

        String duration,

        Instant createdAt
) {
}

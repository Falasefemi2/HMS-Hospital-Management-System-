package com.hospital_management.hospitalmanagement.prescriptions.dto;

import java.util.UUID;

public record PrescriptionRequest(
        UUID consultationId,

        String medication,

        String dosage,

        String duration
) {
}

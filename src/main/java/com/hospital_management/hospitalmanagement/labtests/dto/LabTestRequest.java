package com.hospital_management.hospitalmanagement.labtests.dto;

import java.time.Instant;
import java.util.UUID;

public record LabTestRequest(
        UUID consultationId,

        UUID patientId,

        String testName,

        Instant createdAt
) {
}

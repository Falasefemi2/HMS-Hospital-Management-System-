package com.hospital_management.hospitalmanagement.labtests.dto;

import com.hospital_management.hospitalmanagement.appointments.enumFolder.Status;

import java.time.Instant;
import java.util.UUID;

public record LabTestResponse(
        UUID labTestId,

        UUID labTechId,

        UUID consultationId,

        UUID patientId,

        String testName,

        String result,

        Status status,

        Instant createdAt
) {
}

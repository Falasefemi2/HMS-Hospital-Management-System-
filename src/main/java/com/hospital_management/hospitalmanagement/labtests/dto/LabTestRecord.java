package com.hospital_management.hospitalmanagement.labtests.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record LabTestRecord(
        UUID labTestId,

        UUID consultationId,

        UUID patientId,

        String testName,

        @NotBlank
        String result

) {
}

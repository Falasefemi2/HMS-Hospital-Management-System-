package com.hospital_management.hospitalmanagement.admin.dto;

import java.util.UUID;

public record DoctorResponse(
        UUID doctorId,

        String doctorName
) {
}

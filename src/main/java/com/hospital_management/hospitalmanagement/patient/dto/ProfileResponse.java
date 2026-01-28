package com.hospital_management.hospitalmanagement.patient.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileResponse(
        UUID id,

        String name,

        LocalDate dob,

        String gender,

        String phone,

        String nextOfKin,

        String nextOfKinContact

) {
}

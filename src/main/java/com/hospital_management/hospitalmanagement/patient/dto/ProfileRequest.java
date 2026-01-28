package com.hospital_management.hospitalmanagement.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProfileRequest(
        @NotNull(message = "Date of birt is required")
        LocalDate dob,

        @NotBlank(message = "Gender is required")
        String gender,

        @NotBlank(message = "Phone number is required")
        String phone,

        @NotBlank(message = "Next of kin is required")
        String nextOfKin,

        @NotBlank(message = "Next of kin phone number is required")
        String nextOfKinContact
) {
}

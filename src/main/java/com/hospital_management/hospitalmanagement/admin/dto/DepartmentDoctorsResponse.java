package com.hospital_management.hospitalmanagement.admin.dto;

import java.util.List;
import java.util.UUID;

public record DepartmentDoctorsResponse(
        UUID departmentId,

        String departmentName,

        List<DoctorResponse> doctor
) {
}

package com.hospital_management.hospitalmanagement.admin.mapper;

import com.hospital_management.hospitalmanagement.admin.dto.DepartmentResponse;
import com.hospital_management.hospitalmanagement.admin.entity.Department;
import com.hospital_management.hospitalmanagement.roles.Role;
import org.springframework.stereotype.Component;

@Component
public class DepartmentResponseMapper {
    public DepartmentResponse toDepartmentResponse(Department department) {
        int doctorCount = (int) department.getAppUser().stream()
                .filter(staff -> staff.getRole() == Role.ROLE_DOCTOR)
                .count();

        int nurseCount = (int) department.getAppUser().stream()
                .filter(staff -> staff.getRole() == Role.ROLE_LAB_TECH)
                .count();

        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                doctorCount,
                nurseCount
        );
    }
}

package com.hospital_management.hospitalmanagement.admin.service;

import com.hospital_management.hospitalmanagement.admin.dto.DepartmentRequest;
import com.hospital_management.hospitalmanagement.admin.dto.DepartmentResponse;
import com.hospital_management.hospitalmanagement.admin.entity.Department;
import com.hospital_management.hospitalmanagement.admin.mapper.DepartmentResponseMapper;
import com.hospital_management.hospitalmanagement.admin.repo.DepartmentRepo;
import com.hospital_management.hospitalmanagement.admin.dto.DepartmentDoctorsResponse;
import com.hospital_management.hospitalmanagement.admin.dto.DoctorResponse;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DepartmentService {
    private final AppUserRepo appUserRepo;
    private final DepartmentRepo departmentRepo;
    private final DepartmentResponseMapper departmentResponseMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createDepartment(DepartmentRequest request) {
        if (departmentRepo.existsByName(request.name())) {
            throw new IllegalArgumentException("Department already exists");
        }

        Department department = new Department();
        department.setName(request.name());

        departmentRepo.save(department);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void updateDepartment(
            DepartmentRequest request,
            UUID departmentId
    ) {
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        department.setName(request.name());

        departmentRepo.save(department);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void deleteDepartment(UUID departmentId) {
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        departmentRepo.delete(department);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> geAllDepartments(Pageable pageable) {
        Page<Department> page = departmentRepo.findAll(pageable);

        return page.map(departmentResponseMapper::toDepartmentResponse);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentId(UUID departmentId) {
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        return departmentResponseMapper.toDepartmentResponse(department);
    }

    public DepartmentDoctorsResponse getDoctorsByDepartment(UUID departmentId) {
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        List<DoctorResponse> doctor = department.getAppUser().stream()
                .filter(appUser -> appUser.getRole() == Role.ROLE_DOCTOR)
                .map(doc -> new DoctorResponse(
                        doc.getId(),
                        doc.getFullName()
                ))
                .toList();

        return new DepartmentDoctorsResponse(
                department.getId(),
                department.getName(),
                doctor
        );
    }
}

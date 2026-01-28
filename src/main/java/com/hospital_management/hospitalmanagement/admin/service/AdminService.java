package com.hospital_management.hospitalmanagement.admin.service;

import com.hospital_management.hospitalmanagement.admin.dto.*;
import com.hospital_management.hospitalmanagement.admin.entity.Department;
import com.hospital_management.hospitalmanagement.admin.repo.DepartmentRepo;
import com.hospital_management.hospitalmanagement.admin.util.PasswordUtil;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.exception.UserAlreadyExists;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.email.EmailService;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminService {
    private final AppUserRepo appUserRepo;
    private final DepartmentRepo departmentRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void registerStaff(CreateStaffRequest request) {

        if (request.role() != Role.ROLE_DOCTOR && request.role() != Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctors and nurses can be registered via staff management");
        }

        if (appUserRepo.existsByEmailIgnoreCase(request.email())) {
            throw new UserAlreadyExists(request.role() + " already exists");
        }

        Department department = departmentRepo.findById(request.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        AppUser staff = new AppUser();
        staff.setFullName(request.fullname());
        staff.setEmail(request.email());
        staff.setRole(request.role());
        staff.setFirstLogin(true);
        staff.setActive(true);
        staff.setDepartment(department);

        String tempPassword = PasswordUtil.generate(10);
        staff.setPassword(passwordEncoder.encode(tempPassword));

        appUserRepo.save(staff);

        emailService.sendTemporaryPassword(staff.getEmail(), tempPassword);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateStaff(UUID staffId, UpdateStaffRequest request) {
        AppUser staff = appUserRepo.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() == Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctors and nurses can be updated via staff management");
        }

        if (!staff.getEmail().equals(request.email())
                && appUserRepo.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        staff.setFullName(request.fullname());
        staff.setEmail(request.email());
        staff.setUpdatedAt(Instant.now());

        appUserRepo.save(staff);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateStaffStatus(UUID staffId, boolean active) {
        AppUser staff = appUserRepo.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() == Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctors and nurses status can be updated via staff management");
        }

        staff.setActive(active);
        staff.setUpdatedAt(Instant.now());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public StaffResponse getStaffById(UUID staffId) {
        AppUser staff = appUserRepo.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() == Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctor and nurse can be seen via staff management");
        }

        return new StaffResponse(
                staff.getId(),
                staff.getFullName(),
                staff.getEmail(),
                staff.getRole(),
                staff.isActive()
        );
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<StaffResponse> getAllStaff(Pageable pageable) {
       Page<AppUser> page = appUserRepo.findByRoleInAndActiveTrue(
               List.of(Role.ROLE_DOCTOR, Role.ROLE_LAB_TECH),
               pageable
       );

       return page.map(staff -> new StaffResponse(
               staff.getId(),
               staff.getFullName(),
               staff.getEmail(),
               staff.getRole(),
               staff.isActive()
       ));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteStaff(UUID staffId) {
        AppUser staff = appUserRepo.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() != Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctors and nurses can be deactivated via staff management");
        }

        staff.setActive(false);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void removeStaffDepartment(UpdateStaffDepartment request) {
        AppUser staff = appUserRepo.findById(request.staffId())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() != Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctors and nurses can be updated to departments");
        }

        staff.setDepartment(null);

        appUserRepo.save(staff);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public StaffDepartmentResponse getStaffDepartment(UUID staffId) {
        AppUser staff = appUserRepo.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() != Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only doctors and nurses departments can be seen");
        }

        Department department = staff.getDepartment();

        if (department == null) {
            throw new EntityNotFoundException("Staff has no assigned department");
        }

        return new StaffDepartmentResponse(
                staff.getId(),
                staff.getFullName(),
                department.getName(),
                department.getId()
        );
    }
}

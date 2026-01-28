package com.hospital_management.hospitalmanagement.staff;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class StaffPasswordService {
    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;

    public void changeTempPassword(ChangeTempPasswordRequest request) {
        AppUser staff = appUserRepo.findById(request.staffId())
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        if (staff.getRole() != Role.ROLE_DOCTOR && staff.getRole() != Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only staff can change temporary password");
        }

        if (!passwordEncoder.matches(request.tempPassword(), staff.getPassword())) {
            throw new IllegalArgumentException("Temporary password does not match");
        }

        staff.setPassword(passwordEncoder.encode(request.password()));
        staff.setFirstLogin(false);
        staff.setUpdatedAt(Instant.now());

        appUserRepo.save(staff);
    }
}

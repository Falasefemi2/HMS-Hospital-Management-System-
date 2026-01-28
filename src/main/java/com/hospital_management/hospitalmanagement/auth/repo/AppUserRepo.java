package com.hospital_management.hospitalmanagement.auth.repo;

import com.hospital_management.hospitalmanagement.admin.entity.Department;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.roles.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AppUserRepo extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<AppUser> findByRoleInAndActiveTrue(List<Role> roles, Pageable pageable);

    List<AppUser> findByRoleAndDepartment(Role role, Department department);
}

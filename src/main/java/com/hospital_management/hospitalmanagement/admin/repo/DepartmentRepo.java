package com.hospital_management.hospitalmanagement.admin.repo;

import com.hospital_management.hospitalmanagement.admin.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepo extends JpaRepository<Department, UUID> {
    Optional<Department> findByName(String name);

    boolean existsByName(String name);
}

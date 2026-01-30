package com.hospital_management.hospitalmanagement.admin.controller;

import com.hospital_management.hospitalmanagement.admin.dto.DepartmentRequest;
import com.hospital_management.hospitalmanagement.admin.dto.DepartmentResponse;
import com.hospital_management.hospitalmanagement.admin.service.DepartmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("/hms/admin/create/department")
    public ResponseEntity<String> createDepartment(
            @Valid @RequestBody DepartmentRequest request
    ) {
        departmentService.createDepartment(request);
        return ResponseEntity.ok("Department created");
    }

    @PostMapping("/hms/admin/update/dept/{departmentId}")
    public ResponseEntity<String> updateDepartment(
            @Valid @RequestBody DepartmentRequest request,
            @PathVariable UUID departmentId
    ) {
        departmentService.updateDepartment(request, departmentId);
        return ResponseEntity.ok("Department updated");
    }

    @PostMapping("/hms/admin/delete/dept/{departmentId}")
    public ResponseEntity<String> deleteDepartment(
            @PathVariable UUID departmentId
    ) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok("Department deleted");
    }

    @GetMapping("/hms/admin/departments")
    public Page<DepartmentResponse> getAllDepartment(
            @ParameterObject
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return departmentService.geAllDepartments(pageable);
    }

    @GetMapping("/hms/admin/dept/{departmentId}")
    public DepartmentResponse getDepartmentId(
            @PathVariable UUID departmentId
    ) {
        return departmentService.getDepartmentId(departmentId);
    }
}

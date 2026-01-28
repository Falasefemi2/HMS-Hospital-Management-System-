package com.hospital_management.hospitalmanagement.admin.controller;

import com.hospital_management.hospitalmanagement.admin.dto.*;
import com.hospital_management.hospitalmanagement.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/hms/admin/register/staff")
    public ResponseEntity<String> registerStaff(
            @Valid @RequestBody CreateStaffRequest request
    ) {
        adminService.registerStaff(request);
        return ResponseEntity.ok("Staff successfully registered");
    }

    @PostMapping("/hms/admin/update/staff")
    public ResponseEntity<String> updateStaff(
            @PathVariable UUID staffId,
            @Valid @RequestBody UpdateStaffRequest request
    ) {
        adminService.updateStaff(staffId, request);
        return ResponseEntity.ok("Staff successfully updated");
    }

    @PostMapping("/hms/admin/update/staff/status")
    public ResponseEntity<String> updateStaffStatus(
            @PathVariable UUID staffId,
            @RequestParam boolean active
    ) {
        adminService.updateStaffStatus(staffId, active);
        return ResponseEntity.ok("Staff status successfully updated");
    }

    @GetMapping("hms/admin/staff/{staffId}")
    public StaffResponse getStaffId(
            @PathVariable UUID staffId
    ) {
        return adminService.getStaffById(staffId);
    }

    @GetMapping("hms/admin/staffs")
    public Page<StaffResponse> getAllStaff(Pageable pageable) {
        return adminService.getAllStaff(pageable);
    }

    @DeleteMapping("/hms/admin/delete/{staffId}")
    public ResponseEntity<String> deleteStaffId(
            @PathVariable UUID staffId
    ) {
        adminService.deleteStaff(staffId);
        return ResponseEntity.ok("Staff successfully deleted");
    }

    @DeleteMapping("/hms/admin/remove/staff/dept")
    public ResponseEntity<String> removeStaffDepartment(
            @Valid @RequestBody UpdateStaffDepartment request
    ) {
        adminService.removeStaffDepartment(request);
        return ResponseEntity.ok("Staff successfully removed");
    }

    @GetMapping("hms/admin/staff/dept/{staffId}")
    public StaffDepartmentResponse getStaffDepartment(
            @PathVariable UUID staffId
    ) {
        return adminService.getStaffDepartment(staffId);
    }
}

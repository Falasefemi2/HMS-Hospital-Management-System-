package com.hospital_management.hospitalmanagement.staff.controller;

import com.hospital_management.hospitalmanagement.staff.dto.ChangeTempPasswordRequest;
import com.hospital_management.hospitalmanagement.staff.service.StaffPasswordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ChangeTempPasswordController {
    private final StaffPasswordService staffPasswordService;

    @PostMapping("hms/staff/change/temp/password")
    public ResponseEntity<String> changeTempPassword(
            @Valid @RequestBody ChangeTempPasswordRequest request
    ) {
        staffPasswordService.changeTempPassword(request);
        return ResponseEntity.ok("Temporary password successfully updated");
    }
}

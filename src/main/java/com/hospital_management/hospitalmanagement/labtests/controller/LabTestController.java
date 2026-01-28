package com.hospital_management.hospitalmanagement.labtests.controller;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestRecord;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestRequest;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestResponse;
import com.hospital_management.hospitalmanagement.labtests.service.LabTestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class LabTestController {
    private final LabTestService labTestService;

    @PostMapping("hms/doctor/request/patient/test")
    public ResponseEntity<String> requestPatientTests(
            @AuthenticationPrincipal(expression = "appUser")AppUser doctor,
            @Valid @RequestBody LabTestRequest labTestRequest
    ) {
        labTestService.requestPatientTests(doctor, labTestRequest);
        return ResponseEntity.ok("Request successfully sent");
    }

    @PostMapping("hms/lab_tech/record/patient/test")
    public ResponseEntity<String> recordPatientTests(
            @AuthenticationPrincipal(expression = "appUser")AppUser labTech,
            @Valid @RequestBody LabTestRecord labTestRequest
    ) {
        labTestService.recordPatientTests(labTech, labTestRequest);
        return ResponseEntity.ok("Test successfully recorded");
    }

    @GetMapping("hms/get/patient/test/{consultationId}")
    public List<LabTestResponse> viewPatientTest(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable  UUID consultationId,

            @PathVariable(required = false) UUID labTestId,
            @PathVariable(required = false) UUID patientId,
            @PathVariable(required = false) Instant createdAt
    ) {
        return labTestService.viewPatientTest(appUser, labTestId, consultationId, patientId, createdAt);
    }
}

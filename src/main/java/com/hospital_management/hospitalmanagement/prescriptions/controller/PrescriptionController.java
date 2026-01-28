package com.hospital_management.hospitalmanagement.prescriptions.controller;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.prescriptions.dto.PatientPrescriptionResponse;
import com.hospital_management.hospitalmanagement.prescriptions.dto.PrescriptionRequest;
import com.hospital_management.hospitalmanagement.prescriptions.dto.PrescriptionResponse;
import com.hospital_management.hospitalmanagement.prescriptions.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @PostMapping("hms/doctor/create/prescription")
    public ResponseEntity<String> createPrescription(
            @AuthenticationPrincipal(expression = "appUser") AppUser doctor,
            @Valid @RequestBody PrescriptionRequest request
    )  {
        prescriptionService.createPrescription(doctor, request);
        return ResponseEntity.ok("Prescription created");
    }

    @GetMapping("hms/patient/view/prescription/{prescriptionId}")
    public PatientPrescriptionResponse viewMyPrescription(
            @AuthenticationPrincipal(expression = "appUser") AppUser patient,
            @PathVariable UUID prescriptionId
    ) {
        return prescriptionService.viewMyPrescription(patient, prescriptionId);
    }

    @GetMapping("hms/view/prescription/")
    public List<PrescriptionResponse> viewMyPatientsPrescriptions(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable(value = "prescriptionId", required = false) UUID prescriptionId,
            @PathVariable(value = "doctorID", required = false) UUID doctorId,
            @PathVariable(value = "prescriptionId", required = false) UUID patientId,
            @PathVariable(value = "prescriptionId", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdAt
    ) {
        return prescriptionService.viewMyPatientsPrescriptions(appUser, prescriptionId, doctorId, patientId, createdAt);
    }
}

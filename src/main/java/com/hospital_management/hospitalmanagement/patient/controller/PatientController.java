package com.hospital_management.hospitalmanagement.patient.controller;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.patient.dto.ProfileRequest;
import com.hospital_management.hospitalmanagement.patient.dto.ProfileResponse;
import com.hospital_management.hospitalmanagement.patient.dto.UpdateProfileRequest;
import com.hospital_management.hospitalmanagement.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("hms/patient/create/profile")
    public ResponseEntity<String> postProfile(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @Valid @RequestBody ProfileRequest request
    ) {
        patientService.postProfile(appUser, request);
        return ResponseEntity.ok("Profile successfully created");
    }

    @PostMapping("hms/patient/update/profile")
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        patientService.updateProfile(appUser, request);
        return ResponseEntity.ok("Profile successfully updated");
    }

    @PostMapping("hms/patient/view/profile")
    public ProfileResponse viewProfile(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser
    ) {
        return patientService.viewProfile(appUser);
    }
}

package com.hospital_management.hospitalmanagement.consultation.controller;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.consultation.dto.ConsultationRequest;
import com.hospital_management.hospitalmanagement.consultation.dto.ConsultationResponse;
import com.hospital_management.hospitalmanagement.consultation.service.ConsultationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ConsultationController {
    private final ConsultationService consultationService;

    @PostMapping("hms/doctor/create/consultation")
    public ResponseEntity<String> cancelAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser doctor,
            @Valid @RequestBody ConsultationRequest request
    ) {
        consultationService.createConsultation(doctor, request);
        return ResponseEntity.ok("Consultation created");
    }

    @GetMapping("hms/view/consultation/{appointmentId}")
    public ConsultationResponse viewConsultation(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId
    ) {
        return consultationService.viewConsultation(appUser, appointmentId);
    }
}

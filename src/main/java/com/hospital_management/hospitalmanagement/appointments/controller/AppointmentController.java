package com.hospital_management.hospitalmanagement.appointments.controller;

import com.hospital_management.hospitalmanagement.appointments.dto.AppointmentRequest;
import com.hospital_management.hospitalmanagement.appointments.dto.DoctorAppointmentResponse;
import com.hospital_management.hospitalmanagement.appointments.dto.PatientAppointmentResponse;
import com.hospital_management.hospitalmanagement.appointments.dto.RescheduleRequest;
import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.appointments.service.AppointmentService;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/hms/patient/book/appointment")
    public ResponseEntity<String> bookAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @Valid @RequestBody AppointmentRequest request
    ) {
       appointmentService.bookAppointment(appUser, request);
       return ResponseEntity.ok("Appointment booked");
    }

    @PatchMapping("hms/user/cancel/appointment/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId
    ) {
        appointmentService.cancelAppointment(appUser, appointmentId);
        return ResponseEntity.ok("Appointment cancelled");
    }

    @PostMapping("hms/user/reschedule/appointment/{appointmentId}")
    public ResponseEntity<String> rescheduleAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId,
            @Valid @RequestBody RescheduleRequest request
    ) {
        appointmentService.rescheduleAppointment(appUser, appointmentId, request);
        return ResponseEntity.ok("Appointment rescheduled");
    }

    @PatchMapping("/hms/doctor/confirm/appointment/{appointmentId}")
    public ResponseEntity<String> confirmAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId
    ) {
        appointmentService.confirmAppointment(appUser, appointmentId);
        return ResponseEntity.ok("Appointment confirmed");
    }

    @PatchMapping("/hms/mark/no-show/appointment/{appointmentId}")
    public ResponseEntity<String> markNoShowAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId
    ) {
        appointmentService.markNoShow(appUser, appointmentId);
        return ResponseEntity.ok("Appointment is a no-show");
    }

    @PatchMapping("/hms/complete/appointment/{appointmentId}")
    public ResponseEntity<String> completeAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId
    ) {
        appointmentService.completeAppointment(appUser, appointmentId);
        return ResponseEntity.ok("Appointment completed");
    }

    @GetMapping("/hms/patient/view/appointment/{appointmentId}")
    public PatientAppointmentResponse viewMyAppointment(
            @AuthenticationPrincipal(expression = "appUser") AppUser appUser,
            @PathVariable UUID appointmentId
    ) {
        return appointmentService.viewPatientAppointment(appUser, appointmentId);
    }

    @GetMapping("/hms/doctor/view/appointments/{doctorId}")
    public Page<DoctorAppointmentResponse> viewDoctorAppointments(
            Pageable pageable,
            @PathVariable UUID doctorId
    ) {
        return appointmentService.viewDoctorAppointments(pageable, doctorId);
    }
}

package com.hospital_management.hospitalmanagement.appointments.service;

import com.hospital_management.hospitalmanagement.admin.entity.Department;
import com.hospital_management.hospitalmanagement.admin.repo.DepartmentRepo;
import com.hospital_management.hospitalmanagement.appointments.dto.RescheduleRequest;
import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.appointments.repo.AppointmentRepo;
import com.hospital_management.hospitalmanagement.appointments.enumFolder.Status;
import com.hospital_management.hospitalmanagement.appointments.enumFolder.RescheduleReason;
import com.hospital_management.hospitalmanagement.appointments.dto.AppointmentRequest;
import com.hospital_management.hospitalmanagement.appointments.dto.DoctorAppointmentResponse;
import com.hospital_management.hospitalmanagement.appointments.dto.PatientAppointmentResponse;
import com.hospital_management.hospitalmanagement.auditLogs.service.AuditService;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppointmentService {
    private final AppointmentRepo appointmentRepo;
    private final AppUserRepo appUserRepo;
    private final DepartmentRepo departmentRepo;
    private final AuditService auditService;

    @PreAuthorize("hasRole('PATIENT')")
    @Transactional
    public void bookAppointment(AppUser appUser, AppointmentRequest request) {
        Patient patient = appUser.getPatient();

        if (patient == null) {
            throw new EntityNotFoundException("Patient not found");
        }

        Department department = departmentRepo.findById(request.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));

        AppUser doctor;

        if (request.doctorId() != null) {
            doctor = appUserRepo.findById(request.doctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

            if (doctor.getRole() != Role.ROLE_DOCTOR) {
                throw new IllegalArgumentException("Set appointments with doctors only");
            }

            if (!department.equals(doctor.getDepartment())) {
                throw new IllegalStateException("Doctor does not belong to selected department");
            }
        } else {
            List<AppUser> doctors = appUserRepo.findByRoleAndDepartment(Role.ROLE_DOCTOR, department);

            if (doctors.isEmpty()) {
                throw new IllegalStateException("No doctors available in this department");
            }

            doctor = doctors.getFirst();
        }

        boolean exists
                = appointmentRepo.existsByDoctorAndAppointmentTime(doctor, request.appointmentTime());

        if (exists) {
            throw new IllegalStateException("Doctor is not available on this date");
        }

        Appointment appointment = new Appointment();

        appointment.setStatus(Status.PENDING);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(request.appointmentTime());
        appointment.setRescheduleCount(0);

        appointmentRepo.save(appointment);

        auditService.log(
                appUser,
                "Booked appointment " + appointment.getUuid()
        );
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @Transactional
    public void cancelAppointment(AppUser appUser, UUID appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        boolean isPatientOwner = appUser.getRole() == Role.ROLE_PATIENT &&
                appointment.getPatient().getAppUser().getId().equals(appUser.getId());

        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR &&
                appointment.getDoctor().getId().equals(appUser.getId());

        if (!isPatientOwner && !isDoctor) {
            throw new IllegalArgumentException("Only doctors and patients can cancel appointments");
        }

        if (appointment.getStatus() != Status.PENDING &&
                appointment.getStatus() != Status.COMPLETED
        ) {
            throw new IllegalStateException("Only pending and completed appointments can be cancelled");
        }

        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Past appointments cannot be cancelled");
        }

        appointment.setStatus(Status.CANCELLED);
        appointment.setUpdatedAt(Instant.now());
        appointmentRepo.save(appointment);

        auditService.log(
                 appUser,
                "Cancelled appointment " + appointment.getUuid()
        );
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    @Transactional
    public void rescheduleAppointment(
            AppUser appUser,
            UUID appointmentId,
            RescheduleRequest request
    )  {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        boolean isPatientOwner = appUser.getRole() == Role.ROLE_PATIENT &&
                appointment.getPatient().getAppUser().getId().equals(appUser.getId());

        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR &&
                appointment.getDoctor().getId().equals(appUser.getId());

        boolean isAdmin = appUser.getRole() == Role.ROLE_ADMIN;

        if (!isPatientOwner && !isDoctor && !isAdmin) {
            throw new IllegalArgumentException("Not allowed to reschedule appointment");
        }

        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Appointment has already occurred and cannot be rescheduled");
        }

        if (request.newDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("New appointment time must be in the future");
        }

        if (appointment.getStatus() == Status.COMPLETED ||
                appointment.getStatus() == Status.NO_SHOW
        ) {
            throw new IllegalStateException("Cannot reschedule completed appointments");
        }

        if (isAdmin) {
            appointment.setRescheduleReason(RescheduleReason.ADMIN);
        } else if (isDoctor) {
            appointment.setRescheduleReason(RescheduleReason.DOCTOR);
        } else  {
            appointment.setRescheduleReason(RescheduleReason.PATIENT);
        }

        if (appointment.getRescheduleCount() >= 3) {
            throw new IllegalArgumentException("You have gone beyond the reschedule limit, " +
                    "you have to book an appointment");
        }

        appointment.setAppointmentTime(request.newDateTime());
        appointment.setStatus(Status.PENDING);
        appointment.setUpdatedAt(Instant.now());
        appointment.setRescheduleCount(appointment.getRescheduleCount() + 1);

        appointmentRepo.save(appointment);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional
    public void confirmAppointment(AppUser appUser, UUID appointmentId) {
        if (appUser.getRole() != Role.ROLE_DOCTOR) {
            throw new IllegalArgumentException("Only doctors can confirm appointments");
        }

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(appUser.getId())) {
            throw new IllegalArgumentException("You can only confirm your own appointments");
        }

        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot confirm a past appointment");
        }

        if (appointment.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Only pending appointments can be confirmed");
        }

        appointment.setStatus(Status.CONFIRMED);
        appointment.setUpdatedAt(Instant.now());
        appointmentRepo.save(appointment);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Transactional
    public void markNoShow(AppUser appUser, UUID appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR
            && appointment.getDoctor().getId().equals(appUser.getId());

        boolean isAdmin = appUser.getRole() == Role.ROLE_ADMIN;

        if (!isDoctor && !isAdmin) {
            throw new IllegalArgumentException("Only doctors or admins can mark appointment as a no show");
        }

        if (appointment.getAppointmentTime().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot mark no-show before appointment time");
        }

        if (!appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot mark no-show before appointment time");
        }

        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new IllegalStateException("only confirmed appointments can be marked as no-show");
        }

        appointment.setStatus(Status.NO_SHOW);
        appointment.setUpdatedAt(Instant.now());
        appointmentRepo.save(appointment);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Transactional
    public void completeAppointment(AppUser appUser, UUID appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR &&
                appointment.getDoctor().getId().equals(appUser.getId());

        boolean isAdmin = appUser.getRole() == Role.ROLE_ADMIN;

        if (!isDoctor && !isAdmin) {
            throw new IllegalArgumentException("Only doctors or admins can complete appointments");
        }

        if (!appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot complete appointment before appointment time");
        }

        if (appointment.getStatus() != Status.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointments can be completed");
        }

        appointment.setStatus(Status.COMPLETED);
        appointment.setUpdatedAt(Instant.now());
        appointmentRepo.save(appointment);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @Transactional(readOnly = true)
    public PatientAppointmentResponse viewPatientAppointment(AppUser patient, UUID appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (patient.getRole() != Role.ROLE_PATIENT) {
            throw new IllegalArgumentException("Only patients can see patients appointments");
        }

        if (!appointment.getPatient().getAppUser().getId().equals(patient.getId())) {
            throw new IllegalArgumentException("You are not allowed to view this appointment");
        }

        return new PatientAppointmentResponse(
               appointment.getUuid(),
               appointment.getPatient().getAppUser().getFullName(),
               appointment.getDoctor().getFullName(),
               appointment.getAppointmentTime(),
               appointment.getStatus(),
               appointment.getCreatedAt()
        );
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional(readOnly = true)
    public Page<DoctorAppointmentResponse> viewDoctorAppointments(Pageable pageable, UUID doctorId) {
        AppUser doctor = appUserRepo.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        if (doctor.getRole() != Role.ROLE_DOCTOR) {
            throw new IllegalArgumentException("Only doctors can see doctors appointments");
        }

        Page<Appointment> page = appointmentRepo.findByDoctor(doctor, pageable);

        return page.map(appointment -> new DoctorAppointmentResponse(
            appointment.getUuid(),
            appointment.getDoctor().getFullName(),
            appointment.getPatient().getId(),
            appointment.getAppointmentTime(),
            appointment.getStatus()
        ));
    }
}

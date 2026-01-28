package com.hospital_management.hospitalmanagement.consultation.service;

import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.appointments.enumFolder.Status;
import com.hospital_management.hospitalmanagement.appointments.repo.AppointmentRepo;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.consultation.dto.ConsultationRequest;
import com.hospital_management.hospitalmanagement.consultation.dto.ConsultationResponse;
import com.hospital_management.hospitalmanagement.consultation.entity.Consultation;
import com.hospital_management.hospitalmanagement.consultation.repo.ConsultationRepo;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ConsultationService {
    private final ConsultationRepo consultationRepo;
    private final AppointmentRepo appointmentRepo;

    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @Transactional
    public void createConsultation(AppUser doctor, ConsultationRequest request) {
        if (doctor.getRole() != Role.ROLE_DOCTOR) {
            throw new IllegalArgumentException("Only doctors can create consultation");
        }

        Appointment appointment = appointmentRepo.findById(request.appointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalArgumentException("You can only consult your own appointments");
        }

        if (appointment.getStatus() != Status.COMPLETED) {
            throw new IllegalArgumentException("Consultation can only be created after appointment completion");
        }

        if (consultationRepo.findByAppointment_Uuid(appointment.getUuid()).isPresent()) {
            throw new IllegalArgumentException("Consultation already exists for this appointment");
        }

        Patient patient = appointment.getPatient();

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }

        Consultation consultation = new Consultation();
        consultation.setAppointment(appointment);
        consultation.setDoctor(doctor);
        consultation.setPatient(patient);
        consultation.setNotes(request.notes());
        consultation.setDiagnosis(request.diagnoses());

        consultationRepo.save(consultation);
    }

    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @Transactional
    public ConsultationResponse viewConsultation(AppUser appUser, UUID appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR &&
                appointment.getDoctor().getId().equals(appUser.getId());

        boolean isPatient = appUser.getRole() == Role.ROLE_PATIENT &&
                appointment.getPatient().getAppUser().getId().equals(appUser.getId());

        boolean isAdmin = appUser.getRole() == Role.ROLE_ADMIN;

        if (!isDoctor && !isPatient && !isAdmin) {
            throw new IllegalArgumentException("Not authorized to view consultation");
        }

        Consultation consultation = appointment.getConsultation();

        if (consultation == null) {
            throw new EntityNotFoundException("Consultation not available yet");
        }

        return new ConsultationResponse(
                consultation.getId(),
                appointment.getUuid(),
                consultation.getDoctor().getFullName(),
                consultation.getPatient().getId(),
                consultation.getNotes(),
                consultation.getDiagnosis(),
                consultation.getCreatedAt()
        );
    }
}

package com.hospital_management.hospitalmanagement.prescriptions.service;

import com.hospital_management.hospitalmanagement.auditLogs.service.AuditService;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.consultation.entity.Consultation;
import com.hospital_management.hospitalmanagement.consultation.repo.ConsultationRepo;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import com.hospital_management.hospitalmanagement.prescriptions.dto.PatientPrescriptionResponse;
import com.hospital_management.hospitalmanagement.prescriptions.repo.PrescriptionRepo;
import com.hospital_management.hospitalmanagement.prescriptions.dto.PrescriptionRequest;
import com.hospital_management.hospitalmanagement.prescriptions.dto.PrescriptionResponse;
import com.hospital_management.hospitalmanagement.prescriptions.entity.Prescription;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PrescriptionService {
    private final PrescriptionRepo prescriptionRepo;
    private final ConsultationRepo consultationRepo;
    private final AuditService auditService;

    @PreAuthorize("hasRole('DOCTOR')")
    @Transactional
    public void createPrescription(AppUser doctor, PrescriptionRequest request) {
        Consultation consultation = consultationRepo.findById(request.consultationId())
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));

        validatePrescriptionCreation(doctor, consultation);

        Prescription prescription = buildPrescription(doctor, consultation, request);

        prescriptionRepo.save(prescription);

        auditService.log(
                doctor,
                "Prescription created " + prescription.getId()
        );
    }

    private void validatePrescriptionCreation(AppUser doctor, Consultation consultation) {
        if (consultation.getLabTests() == null || consultation.getLabTests().isEmpty()) {
            throw new IllegalStateException("Cannot create prescription: consultation has no lab tests");
        }

        if (!consultation.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalArgumentException("Not allowed to prescribe for the consultation");
        }
    }

    private Prescription buildPrescription(AppUser doctor, Consultation consultation, PrescriptionRequest request) {
        Patient patient = consultation.getAppointment().getPatient();

        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setMedication(request.medication());
        prescription.setDosage(request.dosage());
        prescription.setDuration(request.duration());
        prescription.setConsultation(consultation);

        consultation.getPrescriptions().add(prescription);

        return prescription;
    }


    @Transactional(readOnly = true)
    public PatientPrescriptionResponse viewMyPrescription(AppUser patient, UUID prescriptionId) {
        if (patient.getRole() != Role.ROLE_PATIENT) {
            throw new IllegalArgumentException("Only patients can view their prescriptions");
        }

        Prescription prescription = prescriptionRepo.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));

        if (!prescription.getPatient().getAppUser().getId().equals(patient.getId())) {
            throw new IllegalArgumentException("You are not allowed to view this prescription");
        }

        return new PatientPrescriptionResponse(
                prescription.getId(),
                prescription.getPatient().getAppUser().getFullName(),
                prescription.getDoctor().getFullName(),
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getDuration(),
                prescription.getCreatedAt()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> viewMyPatientsPrescriptions(
            AppUser appUser,
            UUID prescriptionId,
            UUID doctorId,
            UUID patientId,
            Instant createdAt
    ) {
        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR;
        boolean isAdmin = appUser.getRole() == Role.ROLE_ADMIN;

        if (!isDoctor && !isAdmin) {
            throw new IllegalArgumentException("Only doctors and admins can view patient prescriptions");
        }

        if (isDoctor) {
            doctorId = appUser.getId();
        }

        return prescriptionRepo.findAllByDoctorWithPatientAndCreatedAt(
             prescriptionId,
             doctorId,
             patientId,
             createdAt
        );
    }
}

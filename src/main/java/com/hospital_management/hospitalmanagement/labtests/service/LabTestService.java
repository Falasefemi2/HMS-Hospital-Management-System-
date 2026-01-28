package com.hospital_management.hospitalmanagement.labtests.service;

import com.hospital_management.hospitalmanagement.appointments.enumFolder.Status;
import com.hospital_management.hospitalmanagement.auditLogs.service.AuditService;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.consultation.entity.Consultation;
import com.hospital_management.hospitalmanagement.consultation.repo.ConsultationRepo;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestRequest;
import com.hospital_management.hospitalmanagement.labtests.entity.LabTest;
import com.hospital_management.hospitalmanagement.labtests.repo.LabTestRepo;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestRecord;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestResponse;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
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
public class LabTestService {
    private final LabTestRepo labTestRepo;
    private final ConsultationRepo consultationRepo;
    private final AuditService auditService;

    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @Transactional
    public void requestPatientTests(
            AppUser doctor,
            LabTestRequest request
    ) {
        if (doctor.getRole() != Role.ROLE_DOCTOR) {
            throw new IllegalArgumentException("Only doctors can request for patient tests");
        }

        Consultation consultation = consultationRepo.findById(request.consultationId())
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));

        Patient patient = consultation.getPatient();

        LabTest labTest = new LabTest();
        labTest.setConsultation(consultation);
        labTest.setPatient(patient);
        labTest.setTestName(request.testName());

        labTestRepo.save(labTest);

        auditService.log(
                doctor,
                "Requested lab test " + labTest.getId()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_LAB_TECH')")
    @Transactional
    public void recordPatientTests(AppUser labTech, LabTestRecord request) {
        if (labTech.getRole() != Role.ROLE_LAB_TECH) {
            throw new IllegalArgumentException("Only lab techs can record patient tests");
        }

        LabTest labTest = labTestRepo.findById(request.labTestId())
                .orElseThrow(() -> new EntityNotFoundException("Lab test not found"));

        if (labTest.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Lab test already completed");
        }

        if (labTest.getLabTech() != null && !labTest.getLabTech().getId().equals(labTech.getId())) {
            throw new IllegalArgumentException("You are not assigned to this lab test");
        }

        if (labTest.getLabTech() == null) {
            labTest.setLabTech(labTech);
        }

        if (labTest.getConsultation() == null) {
            Consultation consultation = consultationRepo.findById(request.consultationId())
                    .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
            labTest.setConsultation(consultation);
            labTest.setPatient(consultation.getPatient());
        }


        labTest.setTestName(request.testName());
        labTest.setResult(request.result());
        labTest.setStatus(Status.COMPLETED);
        labTestRepo.save(labTest);

        auditService.log(
                labTech,
                "Completed lab test " + labTest.getId()
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR','ROLE_LAB_TECH')")
    @Transactional(readOnly = true)
    public List<LabTestResponse> viewPatientTest(
            AppUser appUser,
            UUID labTestId,
            UUID consultationId,
            UUID patientId,
            Instant createdAt
    ) {
        boolean isDoctor = appUser.getRole() == Role.ROLE_DOCTOR;
        boolean isLabTech = appUser.getRole() == Role.ROLE_LAB_TECH;

        if (!isDoctor && !isLabTech) {
            throw new IllegalArgumentException("Access denied");
        }

        UUID labTechId = isLabTech ? appUser.getId() : null;

        if (isDoctor) {
            if (consultationId == null) {
                throw new IllegalArgumentException(
                        "Doctor must provide consultationId to view lab tests"
                );
            }

            Consultation consultation = consultationRepo.findById(consultationId)
                    .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));

            if (!consultation.getDoctor().getId().equals(appUser.getId())) {
                throw new IllegalArgumentException(
                        "You are not allowed to view labTests for this consultation"
                );
            }

            patientId = consultation.getPatient().getId();
        }

        return labTestRepo.filterByLabTest(
                labTestId,
                labTechId,
                consultationId,
                patientId,
                createdAt
        );
    }
}

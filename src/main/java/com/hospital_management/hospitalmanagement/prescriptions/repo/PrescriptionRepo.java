package com.hospital_management.hospitalmanagement.prescriptions.repo;

import com.hospital_management.hospitalmanagement.prescriptions.dto.PrescriptionResponse;
import com.hospital_management.hospitalmanagement.prescriptions.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PrescriptionRepo extends JpaRepository<Prescription, UUID> {
    @Query("""
           SELECT new com.hospital_management.hospitalmanagement.prescriptions.dto.PrescriptionResponse(
               p.id,
               p.consultation.id,
               p.patient.id,
               p.patient.appUser.fullName,
               p.doctor.id,
               p.doctor.fullName,
               p.medication,
               p.dosage,
               p.duration,
               p.createdAt
           )
           FROM Prescription p
           WHERE p.id = COALESCE(:prescriptionId, p.id)
              AND p.doctor.id = COALESCE(:doctorId, p.doctor.id)
              AND p.patient.id = COALESCE(:patientId, p.patient.id)
              AND p.createdAt >= COALESCE(:createdAt, p.createdAt)
           ORDER BY p.createdAt DESC 
           """)
    List<PrescriptionResponse> findAllByDoctorWithPatientAndCreatedAt(
            @Param("prescriptionId") UUID prescriptionId,
            @Param("doctorId") UUID doctorId,
            @Param("patientId") UUID patientId,
            @Param("createdAt") Instant createdAt
    );
}


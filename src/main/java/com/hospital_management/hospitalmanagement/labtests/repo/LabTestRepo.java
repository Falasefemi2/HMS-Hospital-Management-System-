package com.hospital_management.hospitalmanagement.labtests.repo;

import com.hospital_management.hospitalmanagement.labtests.entity.LabTest;
import com.hospital_management.hospitalmanagement.labtests.dto.LabTestResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface LabTestRepo extends JpaRepository<LabTest, UUID> {
    @Query("""
           SELECT new com.hospital_management.hospitalmanagement.labtests.dto.LabTestResponse(
               p.id,
               p.labTech.id,
               p.consultation.id,
               p.consultation.patient.id,
               p.testName,
               p.result,
               p.status,
               p.createdAt
           )
           FROM LabTest p
             WHERE p.id = COALESCE(:labTestId, p.id)
             AND p.labTech.id = COALESCE(:labTechId, p.labTech.id)
             AND p.consultation.id = COALESCE(:consultationId, p.consultation.id)
             AND p.consultation.patient.id = COALESCE(:patientId, p.consultation.patient.id)
             AND p.createdAt >= COALESCE(:createdAt, p.createdAt)
           ORDER BY p.createdAt DESC
           """)
    List<LabTestResponse> filterByLabTest(
            @Param("labTestId") UUID labTestId,
            @Param("labTechId") UUID labTechId,
            @Param("consultationId") UUID consultationId,
            @Param("patientId") UUID patientId,
            @Param("createdAt") Instant createdAt
    );
}

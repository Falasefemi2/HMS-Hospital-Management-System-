package com.hospital_management.hospitalmanagement.consultation.repo;

import com.hospital_management.hospitalmanagement.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsultationRepo extends JpaRepository<Consultation, UUID> {
    Optional<Consultation> findByAppointment_Uuid(UUID appointmentId);
}

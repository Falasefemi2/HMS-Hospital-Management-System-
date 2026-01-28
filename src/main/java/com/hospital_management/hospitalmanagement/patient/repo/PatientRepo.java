package com.hospital_management.hospitalmanagement.patient.repo;

import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepo extends JpaRepository<Patient, UUID> {
}

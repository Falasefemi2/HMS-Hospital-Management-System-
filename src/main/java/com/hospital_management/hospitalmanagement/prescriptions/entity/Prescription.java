package com.hospital_management.hospitalmanagement.prescriptions.entity;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.consultation.entity.Consultation;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Prescription {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private AppUser doctor;

    @NotBlank
    @Column(nullable = false)
    private String medication;

    @NotBlank
    @Column(nullable = false)
    private String dosage;

    @NotBlank
    @Column(nullable = false)
    private String duration;

    @CreationTimestamp
    @Column(nullable = false, unique = false)
    private Instant createdAt;
}

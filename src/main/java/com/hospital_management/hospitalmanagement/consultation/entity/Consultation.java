package com.hospital_management.hospitalmanagement.consultation.entity;

import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.labtests.entity.LabTest;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import com.hospital_management.hospitalmanagement.prescriptions.entity.Prescription;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Consultation {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private AppUser doctor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "consultation", fetch = FetchType.LAZY)
    private Set<LabTest> labTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private Set<Prescription> prescriptions = new LinkedHashSet<>();
}

package com.hospital_management.hospitalmanagement.auth.entity;

import com.hospital_management.hospitalmanagement.admin.entity.Department;
import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.consultation.entity.Consultation;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppUser {
    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean active = true;

    private boolean firstLogin = true;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL )
    private Patient patient;

    @OneToMany(
            mappedBy = "doctor",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @OrderBy("appointmentTime DESC")
    private Set<Appointment> appointments = new LinkedHashSet<>();

    @OneToMany(
           mappedBy = "doctor",
           fetch = FetchType.LAZY,
           cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<Consultation> consultations = new LinkedHashSet<>();
}

package com.hospital_management.hospitalmanagement.patient.entity;

import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Patient {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(
            name = "app_user_id",
            unique = true
    )
    private AppUser appUser;

    @OneToMany(
            mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Appointment> appointments = new LinkedHashSet<>();

    private LocalDate dob;

    private String gender;

    private String phone;

    private String nextOfKin;

    private String nextOfKinContact;

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
}

package com.hospital_management.hospitalmanagement.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private Long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String token;

    @OneToOne
    @JoinColumn(
            name = "app_user_id",
            nullable = false
    )
    private AppUser appUser;

    @Column(nullable = false)
    private Instant expiresAt;
}

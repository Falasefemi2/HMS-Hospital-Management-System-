package com.hospital_management.hospitalmanagement.auth.service;


import com.hospital_management.hospitalmanagement.auth.dto.ForgotPasswordRequest;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.entity.PasswordResetToken;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.auth.repo.PasswordTokenRepo;
import com.hospital_management.hospitalmanagement.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetService {

    private final AppUserRepo appUserRepo;
    private final PasswordTokenRepo passwordTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        appUserRepo.findByEmail(request.email()).ifPresent(appUser -> {
            passwordTokenRepo.deleteByUser(appUser);

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = new PasswordResetToken();

            resetToken.setToken(token);
            resetToken.setAppUser(appUser);
            resetToken.setExpiresAt(
                    Instant.now().plus(15, ChronoUnit.HOURS)
            );

            passwordTokenRepo.save(resetToken);

            String resetLink = "https://hms.com/reset-password?token=" + token;

            emailService.sendPasswordResetToken(
                    appUser.getEmail(),
                    "Password Reset",
                    "Click the link below to reset your password:\n" + resetLink
            );
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordTokenRepo.findValidToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        AppUser appUser = resetToken.getAppUser();

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Password too short");
        }

        appUser.setPassword(passwordEncoder.encode(newPassword));

        appUserRepo.save(appUser);
        passwordTokenRepo.delete(resetToken);
    }
}

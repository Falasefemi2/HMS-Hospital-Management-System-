package com.hospital_management.hospitalmanagement.auth.controller;


import com.hospital_management.hospitalmanagement.auth.dto.ForgotPasswordRequest;
import com.hospital_management.hospitalmanagement.auth.dto.ResetPasswordRequest;
import com.hospital_management.hospitalmanagement.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PasswordResetTokenController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/hms/account/forgot/password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(
                "A token has been sent to your mail, to reset your password"
        );
    }

    @PostMapping("/hms/account/reset/password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(
                "Password successfully reset"
        );
    }
}

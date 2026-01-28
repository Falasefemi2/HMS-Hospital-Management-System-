package com.hospital_management.hospitalmanagement.auth.controller;

import com.hospital_management.hospitalmanagement.auth.dto.AuthResponse;
import com.hospital_management.hospitalmanagement.auth.dto.LoginRequest;
import com.hospital_management.hospitalmanagement.auth.dto.RegisterRequest;
import com.hospital_management.hospitalmanagement.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/hms/account/register")
    public AuthResponse register(
           @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("hms/account/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}

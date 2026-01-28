package com.hospital_management.hospitalmanagement.auth.service;

import com.hospital_management.hospitalmanagement.auth.dto.AuthResponse;
import com.hospital_management.hospitalmanagement.auth.dto.ForgotPasswordRequest;
import com.hospital_management.hospitalmanagement.auth.dto.LoginRequest;
import com.hospital_management.hospitalmanagement.auth.dto.RegisterRequest;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.entity.PasswordResetToken;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.auth.repo.PasswordTokenRepo;
import com.hospital_management.hospitalmanagement.auth.security.JwtService;
import com.hospital_management.hospitalmanagement.auth.exception.UserAlreadyExists;
import com.hospital_management.hospitalmanagement.auth.security.UserPrincipal;
import com.hospital_management.hospitalmanagement.roles.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;


@Service
@AllArgsConstructor
public class AuthService {
    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        appUserRepo.findByEmail(request.email())
                .ifPresent(appUser -> {
                    throw new UserAlreadyExists("User with " + request.email() + " already exists");
                });

        AppUser appUser = new AppUser();
        appUser.setFullName(request.fullname());
        appUser.setEmail(request.email());
        appUser.setPassword(passwordEncoder.encode(request.password()));
        appUser.setRole(Role.ROLE_PATIENT);
        appUser.setFirstLogin(false);

        appUserRepo.save(appUser);

        String jwtToken = jwtService.generateToken(new UserPrincipal(appUser));

        return new AuthResponse(
                jwtToken,
                "Registration is successful"
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       request.email(),
                       request.password()
               )
       );

       AppUser appUser = appUserRepo.findByEmail(request.email())
               .orElseThrow(() ->
                       new UsernameNotFoundException("Invalid email or password"));

       if (appUser.isFirstLogin()) {
            return new AuthResponse(null, "CHANGE_PASSWORD_REQUIRED");
       }

       String jwtToken = jwtService.generateToken(new UserPrincipal(appUser));

       return new AuthResponse(
               jwtToken,
               "Login is successful"
       );
    }
}

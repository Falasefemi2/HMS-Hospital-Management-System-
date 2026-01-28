package com.hospital_management.hospitalmanagement.patient.service;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.patient.dto.ProfileRequest;
import com.hospital_management.hospitalmanagement.patient.dto.ProfileResponse;
import com.hospital_management.hospitalmanagement.patient.dto.UpdateProfileRequest;
import com.hospital_management.hospitalmanagement.patient.entity.Patient;
import com.hospital_management.hospitalmanagement.patient.repo.PatientRepo;
import com.hospital_management.hospitalmanagement.roles.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
@AllArgsConstructor
public class PatientService {
    private final PatientRepo patientRepo;
    private final AppUserRepo appUserRepo;

    public void postProfile(AppUser appUser, ProfileRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Profile cannot be empty");
        }

        if (appUser.getRole() != Role.ROLE_PATIENT) {
            throw new IllegalArgumentException("Only patients can create profile");
        }

        if (appUser.getPatient() != null) {
            throw new IllegalStateException("Patient profile already exists");
        }

        Patient patient = new Patient();

        patient.setDob(request.dob());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setNextOfKin(request.nextOfKin());
        patient.setNextOfKinContact(request.nextOfKinContact());
        patient.setAppUser(appUser);

        appUser.setPatient(patient);
        appUserRepo.save(appUser);
    }

    public void updateProfile(AppUser appUser, UpdateProfileRequest request) {
        if (appUser.getRole() != Role.ROLE_PATIENT) {
            throw new IllegalArgumentException("Only patients can update profile");
        }

        Patient patient = appUser.getPatient();

        if (patient == null) {
            throw new EntityNotFoundException("Patient profile not found");
        }

        patient.setPhone(request.phone());
        patient.setNextOfKin(request.nextOfKin());
        patient.setNextOfKinContact(request.nextOfKinContact());
        patient.setUpdatedAt(Instant.now());

        appUserRepo.save(appUser);
    }

    public ProfileResponse viewProfile(AppUser appUser) {
        if (appUser.getRole() != Role.ROLE_PATIENT) {
            throw new IllegalArgumentException("Only patients can view profile");
        }

        Patient patient = appUser.getPatient();

        if (patient == null) {
            throw new EntityNotFoundException("Patient profile not found");
        }

        return new ProfileResponse(
                patient.getId(),
                appUser.getFullName(),
                patient.getDob(),
                patient.getGender(),
                patient.getPhone(),
                patient.getNextOfKin(),
                patient.getNextOfKinContact()
        );
    }
}

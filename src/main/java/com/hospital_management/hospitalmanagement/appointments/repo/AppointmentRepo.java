package com.hospital_management.hospitalmanagement.appointments.repo;

import com.hospital_management.hospitalmanagement.appointments.entity.Appointment;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentRepo extends JpaRepository<Appointment, UUID> {
    boolean existsByDoctorAndAppointmentTime(AppUser doctor, LocalDateTime appointmentTime);

    Page<Appointment> findByDoctor(AppUser doctor, Pageable pageable);

}

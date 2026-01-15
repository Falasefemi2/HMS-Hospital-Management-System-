
package com.hospital_management.hospitalmanagement;

import com.hospital_management.hospitalmanagement.entities.User;
import com.hospital_management.hospitalmanagement.entities.UserRole;
import com.hospital_management.hospitalmanagement.repositories.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HospitalmanagementApplication implements CommandLineRunner {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private Environment environment;

  public static void main(String[] args) {
    SpringApplication.run(HospitalmanagementApplication.class, args);
  }

  @Override
  public void run(String... args) {

    // Detect if running in test mode
    String[] profiles = environment.getActiveProfiles();
    boolean isTest = profiles.length > 0 && "test".equals(profiles[0]);

    if (isTest) {
      System.out.println("\nTEST PROFILE: Skipping data initialization\n");
      return;
    }

    System.out.println("\n╔════════════════════════════════════════════════╗");
    System.out.println("║  Hospital Management System                    ║");
    System.out.println("╚════════════════════════════════════════════════╝\n");

    try {
      long count = userRepo.count();
      System.out.println("Database connected. Users in database: " + count);

      if (count == 0) {
        System.out.println("Creating test data...\n");
        createUser("admin001", "admin@hospital.com", "admin123", "John", "Administrator", UserRole.ADMIN);
        createUser("doctor001", "doctor@hospital.com", "doctor123", "Sarah", "Medicine", UserRole.DOCTOR);
        createUser("nurse001", "nurse@hospital.com", "nurse123", "Emily", "Care", UserRole.NURSE);
        createUser("patient001", "patient@example.com", "patient123", "James", "Patient", UserRole.PATIENT);
        System.out.println();
      }

      System.out.println("╔════════════════════════════════════════════════╗");
      System.out.println("║  Application Ready on http://localhost:8080    ║");
      System.out.println("╚════════════════════════════════════════════════╝\n");

    } catch (Exception e) {
      System.err.println("Warning during startup (not fatal): " + e.getMessage());
    }
  }

  private void createUser(
      String username,
      String email,
      String password,
      String firstName,
      String lastName,
      UserRole role) {
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setRole(role);
    user.setIsActive(true);
    user.setPhone("+1234567890");

    userRepo.save(user);
    System.out.println("Created " + role + ": " + username);
  }
}

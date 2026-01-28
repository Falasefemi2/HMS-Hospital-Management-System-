package com.hospital_management.hospitalmanagement.auth.security;

import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.auth.repo.AppUserRepo;
import com.hospital_management.hospitalmanagement.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final AppUserRepo userRepo;

    @Value("${ADMIN_PASS}")
    private String adminPassword;

    @Bean
    public UserDetailsService userDetailsService() {
       return username -> {
           AppUser appUser = userRepo.findByEmail(username)
                   .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

           return new UserPrincipal(appUser);
       };
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AppUserRepo userRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepo.findByEmail("admin@example.com").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ROLE_ADMIN);
                admin.setFirstLogin(false);

                userRepo.save(admin);

                System.out.println("Admin user created successfully");
            }
        };
    }
}

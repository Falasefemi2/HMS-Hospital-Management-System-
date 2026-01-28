package com.hospital_management.hospitalmanagement.auditLogs.service;

import com.hospital_management.hospitalmanagement.auditLogs.entity.AuditLog;
import com.hospital_management.hospitalmanagement.auditLogs.repo.AuditLogRepo;
import com.hospital_management.hospitalmanagement.auth.entity.AppUser;
import com.hospital_management.hospitalmanagement.roles.Role;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuditService {
    private final AuditLogRepo auditLogRepo;

    public void log(AppUser appUser, String action) {
        AuditLog log = new AuditLog();

        log.setAppUser(appUser);
        log.setAction(action);

        auditLogRepo.save(log);
    }

    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepo.findAll(pageable);
    }
}

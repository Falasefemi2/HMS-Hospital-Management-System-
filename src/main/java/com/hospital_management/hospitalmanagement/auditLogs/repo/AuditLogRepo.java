package com.hospital_management.hospitalmanagement.auditLogs.repo;

import com.hospital_management.hospitalmanagement.auditLogs.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {
}

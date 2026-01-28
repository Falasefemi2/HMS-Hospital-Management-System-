package com.hospital_management.hospitalmanagement.auditLogs.controller;

import com.hospital_management.hospitalmanagement.auditLogs.entity.AuditLog;
import com.hospital_management.hospitalmanagement.auditLogs.service.AuditService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hms/admin/audit-logs")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@AllArgsConstructor
public class AuditLogController {
    private final AuditService auditService;

    @GetMapping
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditService.findAll(pageable);
    }
}

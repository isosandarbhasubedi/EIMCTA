package com.iso.Service;


import org.springframework.stereotype.Service;

import com.iso.Model.AuditLog;
import com.iso.Repository.AuditLogRepository;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditRepo;

    public AuditService(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    public void log(String action,
                    String entityType,
                    Long entityId,
                    String performedBy,
                    String ipAddress,
                    String details) {

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setPerformedAt(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        log.setDetails(details);

        auditRepo.save(log);
    }
}

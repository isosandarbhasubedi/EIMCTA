package com.iso.Service;

import org.springframework.stereotype.Service;
import com.iso.Model.SchoolAuditLog;
import com.iso.Repository.SchoolAuditLogRepository;

import java.time.LocalDateTime;

@Service
public class SchoolAuditService {

    private final SchoolAuditLogRepository repo;

    public SchoolAuditService(SchoolAuditLogRepository repo) {
        this.repo = repo;
    }

    public void log(String action,
                    String entityType,
                    Long entityId,
                    String performedBy,
                    String ipAddress,
                    String details) {

        SchoolAuditLog log = new SchoolAuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setIpAddress(ipAddress);
        log.setDetails(details);
        log.setPerformedAt(LocalDateTime.now());

        repo.save(log);
    }
}

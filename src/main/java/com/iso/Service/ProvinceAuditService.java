package com.iso.Service;

import org.springframework.stereotype.Service;

import com.iso.Model.ProvinceAuditLog;
import com.iso.Repository.ProvinceAuditLogRepository;

import java.time.LocalDateTime;

@Service
public class ProvinceAuditService {

    private final ProvinceAuditLogRepository repo;

    public ProvinceAuditService(ProvinceAuditLogRepository repo) {
        this.repo = repo;
    }

    public void log(String action,
                    String entityType,
                    Long entityId,
                    String performedBy,
                    String ipAddress,
                    String details) {

        ProvinceAuditLog log = new ProvinceAuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setPerformedAt(LocalDateTime.now());
        log.setDetails(details);
        log.setIpAddress(ipAddress);

        repo.save(log);
    }
}
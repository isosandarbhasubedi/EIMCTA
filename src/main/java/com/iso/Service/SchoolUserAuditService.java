package com.iso.Service;

import org.springframework.stereotype.Service;
import com.iso.Model.SchoolUserAuditLog;
import com.iso.Repository.SchoolUserAuditLogRepository;

import java.time.LocalDateTime;

@Service
public class SchoolUserAuditService {

    private final SchoolUserAuditLogRepository repo;

    public SchoolUserAuditService(SchoolUserAuditLogRepository repo) {
        this.repo = repo;
    }

    public void log(String action,
                    Long userId,
                    String userEmail,
                    String userRole,
                    String performedBy,
                    String ipAddress,
                    String details) {

        SchoolUserAuditLog log = new SchoolUserAuditLog();
        log.setAction(action);
        log.setUserId(userId);
        log.setUserEmail(userEmail);
        log.setUserRole(userRole);
        log.setPerformedBy(performedBy);
        log.setIpAddress(ipAddress);
        log.setDetails(details);
        log.setPerformedAt(LocalDateTime.now());

        repo.save(log);
    }
}

package com.iso.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.SchoolAuditLog;
import java.util.List;

public interface SchoolAuditLogRepository
        extends JpaRepository<SchoolAuditLog, Long> {

    List<SchoolAuditLog> findAllByOrderByPerformedAtDesc();

    List<SchoolAuditLog> findByActionOrderByPerformedAtDesc(String action);
}

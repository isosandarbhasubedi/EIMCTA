package com.iso.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.SchoolUserAuditLog;
import java.util.List;

public interface SchoolUserAuditLogRepository
        extends JpaRepository<SchoolUserAuditLog, Long> {

    List<SchoolUserAuditLog> findAllByOrderByPerformedAtDesc();

    List<SchoolUserAuditLog> findByActionOrderByPerformedAtDesc(String action);
}

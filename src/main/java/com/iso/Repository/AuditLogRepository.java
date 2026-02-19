package com.iso.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.AuditLog;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByPerformedAtDesc();
    
    List<AuditLog> findByActionOrderByPerformedAtDesc(String action);

}

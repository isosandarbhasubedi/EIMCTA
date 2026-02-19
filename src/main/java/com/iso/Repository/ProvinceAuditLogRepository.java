package com.iso.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.iso.Model.ProvinceAuditLog;

import java.util.List;

public interface ProvinceAuditLogRepository
        extends JpaRepository<ProvinceAuditLog, Long> {


    List<ProvinceAuditLog> findAllByOrderByPerformedAtDesc();
    
    List<ProvinceAuditLog> findByActionOrderByPerformedAtDesc(String action);

}
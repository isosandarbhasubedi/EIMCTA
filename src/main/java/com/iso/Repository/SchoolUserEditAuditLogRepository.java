package com.iso.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.iso.Model.SchoolUserEditAuditLog;

public interface SchoolUserEditAuditLogRepository 
extends JpaRepository<SchoolUserEditAuditLog, Long>{

}

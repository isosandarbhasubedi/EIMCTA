package com.iso.Repository;





import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.RegistrationStatus;
import com.iso.Model.RegistrationStatusAuditLog;




public interface RegistrationStatusAuditLogRepository  extends JpaRepository<RegistrationStatusAuditLog, Long> {

	List<RegistrationStatusAuditLog> 
	findByToStatusOrderByChangedAtDesc(RegistrationStatus toStatus);

	List<RegistrationStatusAuditLog> 
	findAllByOrderByChangedAtDesc();
}

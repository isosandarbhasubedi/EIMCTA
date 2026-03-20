package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.ContactStatus;
import com.iso.Model.ContactStatusAuditLog;


public interface ContactStatusAuditLogRepository  extends JpaRepository<ContactStatusAuditLog, Long> {

	List<ContactStatusAuditLog> 
	findByToStatusOrderByChangedAtDesc(ContactStatus toStatus);

	List<ContactStatusAuditLog> 
	findAllByOrderByChangedAtDesc();
}

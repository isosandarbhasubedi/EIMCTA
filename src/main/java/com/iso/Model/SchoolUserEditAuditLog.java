package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SchoolUserEditAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String performedBy; // email of the user who did the section

    private String entityType; // PRINCIPAL / ADMIN
    
    private String performedFor; // email of the user whose record was edited

    private String action; // EDIT_SECTION_1

    @Column(length = 2000)
    private String details;

    private String ipAddress;

    private LocalDateTime performedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(String performedBy) {
		this.performedBy = performedBy;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getAction() {
		return action;
	}
	
	

	public String getPerformedFor() {
		return performedFor;
	}

	public void setPerformedFor(String performedFor) {
		this.performedFor = performedFor;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public LocalDateTime getPerformedAt() {
		return performedAt;
	}

	public void setPerformedAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}

    // getters setters
    
    
}
package com.iso.Model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class RegistrationStatusAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long registrationId;

    private String changedBy; // admin username

    @Enumerated(EnumType.STRING)
    private RegistrationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus toStatus;

    private LocalDateTime changedAt;

    private LocalDateTime performedAt; // original registration date

    private String ipAddress;
    
    // auto set change time
    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(Long registrationId) {
		this.registrationId = registrationId;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public RegistrationStatus getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(RegistrationStatus fromStatus) {
		this.fromStatus = fromStatus;
	}

	public RegistrationStatus getToStatus() {
		return toStatus;
	}

	public void setToStatus(RegistrationStatus toStatus) {
		this.toStatus = toStatus;
	}

	public LocalDateTime getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(LocalDateTime changedAt) {
		this.changedAt = changedAt;
	}

	public LocalDateTime getPerformedAt() {
		return performedAt;
	}

	public void setPerformedAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	
   
}

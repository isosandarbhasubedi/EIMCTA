package com.iso.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class ContactStatusAuditLog {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long contactId;

	    private String changedBy; // admin username

	    @Enumerated(EnumType.STRING)
	    private ContactStatus fromStatus;

	    @Enumerated(EnumType.STRING)
	    private ContactStatus toStatus;

	    private LocalDateTime changedAt;

	    private LocalDateTime performedAt; // original registration date

	    private String ipAddress;
	    
	    // auto set change time
	    @PrePersist
	    protected void onCreate() {
	        this.changedAt = LocalDateTime.now();
	    }

	    
	    // Getter and Setter
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getContactId() {
			return contactId;
		}

		public void setContactId(Long contactId) {
			this.contactId = contactId;
		}

		public String getChangedBy() {
			return changedBy;
		}

		public void setChangedBy(String changedBy) {
			this.changedBy = changedBy;
		}

		public ContactStatus getFromStatus() {
			return fromStatus;
		}

		public void setFromStatus(ContactStatus fromStatus) {
			this.fromStatus = fromStatus;
		}

		public ContactStatus getToStatus() {
			return toStatus;
		}

		public void setToStatus(ContactStatus toStatus) {
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

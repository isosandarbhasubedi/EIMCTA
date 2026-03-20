package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String address;
    private String contactNumber;

    @Column(length = 2000)
    private String message;

    private LocalDateTime performedAt;

    // Save type to pending at first
    @Enumerated(EnumType.STRING)
    private ContactStatus status = ContactStatus.PENDING;
    
    
    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }

    //Getter and Setter Method
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getPerformedAt() {
		return performedAt;
	}

	public void setPerformedAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}

	public ContactStatus getStatus() {
		return status;
	}

	public void setStatus(ContactStatus status) {
		this.status = status;
	}

   
}
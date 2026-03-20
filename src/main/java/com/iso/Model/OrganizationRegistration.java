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
public class OrganizationRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Organization Info
    private String organizationName;
    private String organizationType;
    private String organizationAddress;
    private String organizationEmail;
    private String organizationContactNumber;
    private String organizationWebsite;

    // Personal Info
    private String yourName;
    private String yourAddress;
    private String yourContactNumber;
    private String yourEmail;
    
    private LocalDateTime performedAt;
    
    // Save type to pending at first
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;
    
 // Automatically set date before saving
    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }
    
    //Getter and Setter Methods
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}
	public String getOrganizationAddress() {
		return organizationAddress;
	}
	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
	}
	public String getOrganizationEmail() {
		return organizationEmail;
	}
	public void setOrganizationEmail(String organizationEmail) {
		this.organizationEmail = organizationEmail;
	}
	public String getOrganizationContactNumber() {
		return organizationContactNumber;
	}
	public void setOrganizationContactNumber(String organizationContactNumber) {
		this.organizationContactNumber = organizationContactNumber;
	}
	public String getOrganizationWebsite() {
		return organizationWebsite;
	}
	public void setOrganizationWebsite(String organizationWebsite) {
		this.organizationWebsite = organizationWebsite;
	}
	public String getYourName() {
		return yourName;
	}
	public void setYourName(String yourName) {
		this.yourName = yourName;
	}
	public String getYourAddress() {
		return yourAddress;
	}
	public void setYourAddress(String yourAddress) {
		this.yourAddress = yourAddress;
	}
	public String getYourContactNumber() {
		return yourContactNumber;
	}
	public void setYourContactNumber(String yourContactNumber) {
		this.yourContactNumber = yourContactNumber;
	}
	public String getYourEmail() {
		return yourEmail;
	}
	public void setYourEmail(String yourEmail) {
		this.yourEmail = yourEmail;
	}
	
	public LocalDateTime getPerformedAt() {
		return performedAt;
	}
	public void setPerformedAt(LocalDateTime performedAt) {
		this.performedAt = performedAt;
	}
	public RegistrationStatus getStatus() {
		return status;
	}
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}
    
    
    

    
    
}
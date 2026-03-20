package com.iso.Model;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Embeddable;

@Embeddable
public class PrincipalPersonalInfo {

	private String title;
    private String firstName;
    private String surName;
    private String maidenName;
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String dateOfBirthNepali;
    private String citizenshipNumber;
    private String issuedDistrict;
    private String panNumber;
    private String ssf;
    private String phoneNumber;
    private String emergencyContactNumber;
    private String socialmediaId;
    private String preferredCommunication;
    private String permanentAddress;
    private String currentAddress;
    
    
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public String getMaidenName() {
		return maidenName;
	}
	public void setMaidenName(String maidenName) {
		this.maidenName = maidenName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getDateOfBirthNepali() {
		return dateOfBirthNepali;
	}
	public void setDateOfBirthNepali(String dateOfBirthNepali) {
		this.dateOfBirthNepali = dateOfBirthNepali;
	}
	
	public String getCitizenshipNumber() {
		return citizenshipNumber;
	}
	public void setCitizenshipNumber(String citizenshipNumber) {
		this.citizenshipNumber = citizenshipNumber;
	}
	public String getIssuedDistrict() {
		return issuedDistrict;
	}
	public void setIssuedDistrict(String issuedDistrict) {
		this.issuedDistrict = issuedDistrict;
	}
	public String getPanNumber() {
		return panNumber;
	}
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	
	public String getSsf() {
		return ssf;
	}
	public void setSsf(String ssf) {
		this.ssf = ssf;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmergencyContactNumber() {
		return emergencyContactNumber;
	}
	public void setEmergencyContactNumber(String emergencyContactNumber) {
		this.emergencyContactNumber = emergencyContactNumber;
	}
	public String getSocialmediaId() {
		return socialmediaId;
	}
	public void setSocialmediaId(String socialmediaId) {
		this.socialmediaId = socialmediaId;
	}
	public String getPreferredCommunication() {
		return preferredCommunication;
	}
	public void setPreferredCommunication(String preferredCommunication) {
		this.preferredCommunication = preferredCommunication;
	}
	public String getPermanentAddress() {
		return permanentAddress;
	}
	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
	public String getCurrentAddress() {
		return currentAddress;
	}
	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}
	
    
    
    
}

package com.iso.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login & Role
    private String username;
    private String email;              // used for login
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;


    // ===== Employee Personal Details =====
    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    private String maritalStatus;
    private String nationality;

    private String idNumber;

    // ===== Contact Details =====
    private String officeEmail;
    private String personalEmail;

    private String officeMobileNumber;
    private String personalMobileNumber;

    // ===== Address =====
    @Column(length = 500)
    private String permanentAddress;

    @Column(length = 500)
    private String currentAddress;

    // ✅ Employee Image (store file name or path)
    private String image;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private boolean deleted = false;
    
    
    @ManyToOne
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;

    private LocalDateTime deletedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school; // NULL only for EIMCTA USER
    
 // ===== Province Relation =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;   // For Province Users

    
    
    // ===== Constructors =====
    public User() {}

    
    // ===== Getters & Setters =====
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getOfficeEmail() {
		return officeEmail;
	}

	public void setOfficeEmail(String officeEmail) {
		this.officeEmail = officeEmail;
	}

	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}

	public String getOfficeMobileNumber() {
		return officeMobileNumber;
	}

	public void setOfficeMobileNumber(String officeMobileNumber) {
		this.officeMobileNumber = officeMobileNumber;
	}

	public String getPersonalMobileNumber() {
		return personalMobileNumber;
	}

	public void setPersonalMobileNumber(String personalMobileNumber) {
		this.personalMobileNumber = personalMobileNumber;
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


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}


	public School getSchool() {
		return school;
	}
	
	


	public Boolean getDeleted() {
		return deleted;
	}


	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	

	public User getDeletedBy() {
		return deletedBy;
	}


	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}


	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}


	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}


	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


	public void setSchool(School school) {
		this.school = school;
	}


	public Province getProvince() {
		return province;
	}


	public void setProvince(Province province) {
		this.province = province;
	}

  
    

}

package com.iso.Model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName;
    
    private int currentSection = 1;   // tracks current working section
    private int completedSection = 0; // last completed section
    private boolean formCompleted = false;
 // 🔹 Section 7 - Principal Approval
    private boolean principalApproved = false;
    
    // 🔹 Link with User (Authentication)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🔹 Section 1A - Personal Info
    @Embedded
    private PrincipalPersonalInfo personalInfo;

    // 🔹 Section 1B - Family Info
    @Embedded
    private PrincipalFamilyInfo familyInfo;

    // 🔹 Section 2 - Educational Qualification (Multiple)
    @OneToMany(mappedBy = "principal", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PrincipalEducation> educations;

    // 🔹 Section 3 - Work Experience (Multiple)
    @OneToMany(mappedBy = "principal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrincipalWorkExperience> workExperiences;

    // 🔹 Section 4 - Current Job Description
    @OneToOne(mappedBy = "principal", cascade = CascadeType.ALL)
    private PrincipalCurrentJob currentJob;

    // 🔹 Section 5 - Capability Rating
    @OneToOne(mappedBy = "principal", cascade = CascadeType.ALL)
    private PrincipalCapabilityRating capabilityRating;

 // 🔹 Section 6 - Attachments
 // 🔹 Section 6 - Attachments (Multiple)
    @OneToMany(mappedBy = "principal", cascade = CascadeType.ALL)
    private List<PrincipalAttachment> attachments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	

	public int getCurrentSection() {
		return currentSection;
	}

	public void setCurrentSection(int currentSection) {
		this.currentSection = currentSection;
	}

	public int getCompletedSection() {
		return completedSection;
	}

	public void setCompletedSection(int completedSection) {
		this.completedSection = completedSection;
	}

	public boolean isFormCompleted() {
		return formCompleted;
	}

	public void setFormCompleted(boolean formCompleted) {
		this.formCompleted = formCompleted;
	}

	public boolean isPrincipalApproved() {
		return principalApproved;
	}

	public void setPrincipalApproved(boolean principalApproved) {
		this.principalApproved = principalApproved;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PrincipalPersonalInfo getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(PrincipalPersonalInfo personalInfo) {
		this.personalInfo = personalInfo;
	}

	public PrincipalFamilyInfo getFamilyInfo() {
		return familyInfo;
	}

	public void setFamilyInfo(PrincipalFamilyInfo familyInfo) {
		this.familyInfo = familyInfo;
	}

	public List<PrincipalEducation> getEducations() {
		return educations;
	}

	public void setEducations(List<PrincipalEducation> educations) {
		this.educations = educations;
	}

	public List<PrincipalWorkExperience> getWorkExperiences() {
		return workExperiences;
	}

	public void setWorkExperiences(List<PrincipalWorkExperience> workExperiences) {
		this.workExperiences = workExperiences;
	}

	public PrincipalCurrentJob getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(PrincipalCurrentJob currentJob) {
		this.currentJob = currentJob;
	}

	public PrincipalCapabilityRating getCapabilityRating() {
		return capabilityRating;
	}

	public void setCapabilityRating(PrincipalCapabilityRating capabilityRating) {
		this.capabilityRating = capabilityRating;
	}

	public List<PrincipalAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<PrincipalAttachment> attachments) {
		this.attachments = attachments;
	}

	

	

    
    
}
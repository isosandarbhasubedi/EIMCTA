package com.iso.Model;

import jakarta.persistence.Embeddable;

@Embeddable
public class PrincipalFamilyInfo {

    private String maritalStatus;
    private String bloodGroup;
    private String dependent;
    private String spouseName;
    private String familyStatus;
    private String childrenNumber;
    private String dependentNumber;
    private String emergencyContact;
    private String activeEarners;
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getDependent() {
		return dependent;
	}
	public void setDependent(String dependent) {
		this.dependent = dependent;
	}
	public String getSpouseName() {
		return spouseName;
	}
	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	public String getFamilyStatus() {
		return familyStatus;
	}
	public void setFamilyStatus(String familyStatus) {
		this.familyStatus = familyStatus;
	}
	public String getChildrenNumber() {
		return childrenNumber;
	}
	public void setChildrenNumber(String childrenNumber) {
		this.childrenNumber = childrenNumber;
	}
	public String getDependentNumber() {
		return dependentNumber;
	}
	public void setDependentNumber(String dependentNumber) {
		this.dependentNumber = dependentNumber;
	}
	public String getEmergencyContact() {
		return emergencyContact;
	}
	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}
	public String getActiveEarners() {
		return activeEarners;
	}
	public void setActiveEarners(String activeEarners) {
		this.activeEarners = activeEarners;
	}
    
    
    
}

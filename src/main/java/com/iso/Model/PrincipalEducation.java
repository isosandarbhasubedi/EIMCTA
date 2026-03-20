package com.iso.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PrincipalEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String highestQualification;
    private String specializedIn;
    private String completionYear;
    private String board;
    private String institutionName;


    @ManyToOne
    @JoinColumn(name = "principal_id")
    private Principal principal;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getHighestQualification() {
		return highestQualification;
	}


	public void setHighestQualification(String highestQualification) {
		this.highestQualification = highestQualification;
	}


	public String getSpecializedIn() {
		return specializedIn;
	}


	public void setSpecializedIn(String specializedIn) {
		this.specializedIn = specializedIn;
	}


	public String getCompletionYear() {
		return completionYear;
	}


	public void setCompletionYear(String completionYear) {
		this.completionYear = completionYear;
	}


	public String getBoard() {
		return board;
	}


	public void setBoard(String board) {
		this.board = board;
	}


	public String getInstitutionName() {
		return institutionName;
	}


	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}


	public Principal getPrincipal() {
		return principal;
	}


	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}
    
    
    
}

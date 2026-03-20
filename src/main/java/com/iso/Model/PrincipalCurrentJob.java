package com.iso.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class PrincipalCurrentJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String workingHours;

    @OneToOne
    @JoinColumn(name = "principal_id")
    private Principal principal;

    @OneToMany(mappedBy = "currentJob", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrincipalCurrentJobDetail> jobDetails = new ArrayList<>();

    // getters & setters


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public List<PrincipalCurrentJobDetail> getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(List<PrincipalCurrentJobDetail> jobDetails) {
		this.jobDetails = jobDetails;
	}
    
	
	

    
}

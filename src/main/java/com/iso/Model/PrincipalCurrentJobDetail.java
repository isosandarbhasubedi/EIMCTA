package com.iso.Model;

import jakarta.persistence.*;

@Entity
public class PrincipalCurrentJobDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String periodNumber;
    private String subject;
    private String learnersNumber;
    private String level;

    @ManyToOne
    @JoinColumn(name = "current_job_id")
    private PrincipalCurrentJob currentJob;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(String periodNumber) {
		this.periodNumber = periodNumber;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLearnersNumber() {
		return learnersNumber;
	}

	public void setLearnersNumber(String learnersNumber) {
		this.learnersNumber = learnersNumber;
	}

	public PrincipalCurrentJob getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(PrincipalCurrentJob currentJob) {
		this.currentJob = currentJob;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	

    
}
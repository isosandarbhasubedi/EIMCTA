package com.iso.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class LearnerEntranceScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Learner learner;

    @OneToMany(mappedBy = "learnerEntranceScore", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearnerEntranceSubject> subjects;

    private Double totalK;
    private Double totalU;
    private Double totalA;
    private Double totalHA;

    private Double overallPercentage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Learner getLearner() {
		return learner;
	}

	public void setLearner(Learner learner) {
		this.learner = learner;
	}

	public List<LearnerEntranceSubject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<LearnerEntranceSubject> subjects) {
		this.subjects = subjects;
	}

	public Double getTotalK() {
		return totalK;
	}

	public void setTotalK(Double totalK) {
		this.totalK = totalK;
	}

	public Double getTotalU() {
		return totalU;
	}

	public void setTotalU(Double totalU) {
		this.totalU = totalU;
	}

	public Double getTotalA() {
		return totalA;
	}

	public void setTotalA(Double totalA) {
		this.totalA = totalA;
	}

	public Double getTotalHA() {
		return totalHA;
	}

	public void setTotalHA(Double totalHA) {
		this.totalHA = totalHA;
	}

	public Double getOverallPercentage() {
		return overallPercentage;
	}

	public void setOverallPercentage(Double overallPercentage) {
		this.overallPercentage = overallPercentage;
	}

    // Getters & Setters
    
}
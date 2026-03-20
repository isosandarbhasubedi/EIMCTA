package com.iso.Model;

import jakarta.persistence.*;

@Entity
public class LearnerSubjectMarks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectName;

    // Full Marks
    private Integer fullMarksTheory;
    private Integer fullMarksPractical;
    private Integer fullMarksTotal;

    // Obtained Marks
    private Integer obtainedMarksTheory;
    private Integer obtainedMarksPractical;
    private Integer obtainedMarksTotal;

    private Double percentage;
    private Double gpa;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private LearnerPastSchoolPerformance performance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getFullMarksTheory() {
		return fullMarksTheory;
	}

	public void setFullMarksTheory(Integer fullMarksTheory) {
		this.fullMarksTheory = fullMarksTheory;
	}

	public Integer getFullMarksPractical() {
		return fullMarksPractical;
	}

	public void setFullMarksPractical(Integer fullMarksPractical) {
		this.fullMarksPractical = fullMarksPractical;
	}

	public Integer getFullMarksTotal() {
		return fullMarksTotal;
	}

	public void setFullMarksTotal(Integer fullMarksTotal) {
		this.fullMarksTotal = fullMarksTotal;
	}

	public Integer getObtainedMarksTheory() {
		return obtainedMarksTheory;
	}

	public void setObtainedMarksTheory(Integer obtainedMarksTheory) {
		this.obtainedMarksTheory = obtainedMarksTheory;
	}

	public Integer getObtainedMarksPractical() {
		return obtainedMarksPractical;
	}

	public void setObtainedMarksPractical(Integer obtainedMarksPractical) {
		this.obtainedMarksPractical = obtainedMarksPractical;
	}

	public Integer getObtainedMarksTotal() {
		return obtainedMarksTotal;
	}

	public void setObtainedMarksTotal(Integer obtainedMarksTotal) {
		this.obtainedMarksTotal = obtainedMarksTotal;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public Double getGpa() {
		return gpa;
	}

	public void setGpa(Double gpa) {
		this.gpa = gpa;
	}

	public LearnerPastSchoolPerformance getPerformance() {
		return performance;
	}

	public void setPerformance(LearnerPastSchoolPerformance performance) {
		this.performance = performance;
	}

    // Getters & Setters...
    
    
}
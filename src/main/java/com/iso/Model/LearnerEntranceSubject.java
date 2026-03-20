package com.iso.Model;

import jakarta.persistence.*;

@Entity
public class LearnerEntranceSubject {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 
   	
    private String subjectName;

    // Knowledge
    private Integer kFull;
    private Integer kObtained;
    private Double kPercentage;

    // Understanding
    private Integer uFull;
    private Integer uObtained;
    private Double uPercentage;

    // Application
    private Integer aFull;
    private Integer aObtained;
    private Double aPercentage;

    // Higher Ability
    private Integer haFull;
    private Integer haObtained;
    private Double haPercentage;

    // Total
    private Integer totalFull;
    private Integer totalObtained;
    private Double totalPercentage;


    @ManyToOne
    @JoinColumn(name = "entrance_score_id")
   private LearnerEntranceScore learnerEntranceScore;


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


	public Integer getkFull() {
		return kFull;
	}


	public void setkFull(Integer kFull) {
		this.kFull = kFull;
	}


	public Integer getkObtained() {
		return kObtained;
	}


	public void setkObtained(Integer kObtained) {
		this.kObtained = kObtained;
	}


	public Double getkPercentage() {
		return kPercentage;
	}


	public void setkPercentage(Double kPercentage) {
		this.kPercentage = kPercentage;
	}


	public Integer getuFull() {
		return uFull;
	}


	public void setuFull(Integer uFull) {
		this.uFull = uFull;
	}


	public Integer getuObtained() {
		return uObtained;
	}


	public void setuObtained(Integer uObtained) {
		this.uObtained = uObtained;
	}


	public Double getuPercentage() {
		return uPercentage;
	}


	public void setuPercentage(Double uPercentage) {
		this.uPercentage = uPercentage;
	}


	public Integer getaFull() {
		return aFull;
	}


	public void setaFull(Integer aFull) {
		this.aFull = aFull;
	}


	public Integer getaObtained() {
		return aObtained;
	}


	public void setaObtained(Integer aObtained) {
		this.aObtained = aObtained;
	}


	public Double getaPercentage() {
		return aPercentage;
	}


	public void setaPercentage(Double aPercentage) {
		this.aPercentage = aPercentage;
	}


	public Integer getHaFull() {
		return haFull;
	}


	public void setHaFull(Integer haFull) {
		this.haFull = haFull;
	}


	public Integer getHaObtained() {
		return haObtained;
	}


	public void setHaObtained(Integer haObtained) {
		this.haObtained = haObtained;
	}


	public Double getHaPercentage() {
		return haPercentage;
	}


	public void setHaPercentage(Double haPercentage) {
		this.haPercentage = haPercentage;
	}


	public Integer getTotalFull() {
		return totalFull;
	}


	public void setTotalFull(Integer totalFull) {
		this.totalFull = totalFull;
	}


	public Integer getTotalObtained() {
		return totalObtained;
	}


	public void setTotalObtained(Integer totalObtained) {
		this.totalObtained = totalObtained;
	}


	public Double getTotalPercentage() {
		return totalPercentage;
	}


	public void setTotalPercentage(Double totalPercentage) {
		this.totalPercentage = totalPercentage;
	}


	public LearnerEntranceScore getLearnerEntranceScore() {
		return learnerEntranceScore;
	}


	public void setLearnerEntranceScore(LearnerEntranceScore learnerEntranceScore) {
		this.learnerEntranceScore = learnerEntranceScore;
	}

	
    
    

    // Getters & Setters
}
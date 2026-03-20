package com.iso.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class LearnerPastSchoolPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className; // Selected class

    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Learner learner;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearnerSubjectMarks> subjectMarks;

    // Getters & Setters
    public Long getId() { return id; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Learner getLearner() { return learner; }
    public void setLearner(Learner learner) { this.learner = learner; }
	public List<LearnerSubjectMarks> getSubjectMarks() {
		return subjectMarks;
	}
	public void setSubjectMarks(List<LearnerSubjectMarks> subjectMarks) {
		this.subjectMarks = subjectMarks;
	}
	public void setId(Long id) {
		this.id = id;
	}
   
}
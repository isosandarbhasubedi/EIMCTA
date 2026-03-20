package com.iso.Model;

import jakarta.persistence.*;

@Entity
public class LearnerRubricsEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer discipline;
    private Integer attitude;
    private Integer listening;
    private Integer reading;
    private Integer handwriting;
    private Integer speaking;

    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Learner learner;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Integer discipline) {
		this.discipline = discipline;
	}

	public Integer getAttitude() {
		return attitude;
	}

	public void setAttitude(Integer attitude) {
		this.attitude = attitude;
	}

	public Integer getListening() {
		return listening;
	}

	public void setListening(Integer listening) {
		this.listening = listening;
	}

	public Integer getReading() {
		return reading;
	}

	public void setReading(Integer reading) {
		this.reading = reading;
	}

	public Integer getHandwriting() {
		return handwriting;
	}

	public void setHandwriting(Integer handwriting) {
		this.handwriting = handwriting;
	}

	public Integer getSpeaking() {
		return speaking;
	}

	public void setSpeaking(Integer speaking) {
		this.speaking = speaking;
	}

	public Learner getLearner() {
		return learner;
	}

	public void setLearner(Learner learner) {
		this.learner = learner;
	}

    // Getters & Setters
    
    
}
package com.iso.Model.dto;

import com.iso.Model.LearnerEntranceScore;
import com.iso.Model.LearnerPastSchoolPerformance;
import com.iso.Model.LearnerRubricsEvaluation;

public class LearnerSection2DTO {

	private LearnerPastSchoolPerformance performance;
    private LearnerEntranceScore entrance;
    private LearnerRubricsEvaluation rubrics;
	public LearnerPastSchoolPerformance getPerformance() {
		return performance;
	}
	public void setPerformance(LearnerPastSchoolPerformance performance) {
		this.performance = performance;
	}
	public LearnerEntranceScore getEntrance() {
		return entrance;
	}
	public void setEntrance(LearnerEntranceScore entrance) {
		this.entrance = entrance;
	}
	public LearnerRubricsEvaluation getRubrics() {
		return rubrics;
	}
	public void setRubrics(LearnerRubricsEvaluation rubrics) {
		this.rubrics = rubrics;
	}
    
    
    
}

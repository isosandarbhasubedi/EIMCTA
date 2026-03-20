package com.iso.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.Learner;
import com.iso.Model.LearnerPastSchoolPerformance;

public interface LearnerPastSchoolPerformanceRepository 
	extends JpaRepository<LearnerPastSchoolPerformance, Long> {
	
	    Optional<LearnerPastSchoolPerformance> findByLearner(Learner learner);
	}
	


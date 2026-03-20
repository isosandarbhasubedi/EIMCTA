package com.iso.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.Learner;
import com.iso.Model.LearnerRubricsEvaluation;

public interface LearnerRubricsEvaluationRepository extends JpaRepository<LearnerRubricsEvaluation, Long> {
    Optional<LearnerRubricsEvaluation> findByLearner(Learner learner);
}

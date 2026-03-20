package com.iso.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.Learner;
import com.iso.Model.LearnerEntranceScore;

public interface LearnerEntranceScoreRepository extends JpaRepository<LearnerEntranceScore, Long> {
    Optional<LearnerEntranceScore> findByLearner(Learner learner);
}

package com.iso.Repository;

import com.iso.Model.Learner;
import com.iso.Model.LearnerBasicDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearnerBasicDetailsRepository extends JpaRepository<LearnerBasicDetails, Long> {

    Optional<LearnerBasicDetails> findByLearner(Learner learner);
}
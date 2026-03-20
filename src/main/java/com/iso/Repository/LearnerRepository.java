package com.iso.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.Learner;
import com.iso.Model.School;
import com.iso.Model.User;

public interface LearnerRepository extends JpaRepository<Learner, Long> {
    List<Learner> findBySchool(School school);
    
    Optional<Learner> findByUser(User userId);
}

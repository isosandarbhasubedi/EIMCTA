package com.iso.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.iso.Model.LearnerEnrollment;
import com.iso.Model.dto.LearnerEnrollmentView;

public interface LearnerEnrollmentRepository extends JpaRepository<LearnerEnrollment, Long> {

    boolean existsByLearnerIdAndSectionId(Long learnerId, Long sectionId);
    
    boolean existsByLearnerIdAndAcademicYearId(Long learnerId, Long academicYearId);

 // Check if learner is already enrolled in section for an academic year
    boolean existsByLearnerIdAndSectionIdAndAcademicYearId(Long learnerId, Long sectionId, Long academicYearId);

    //In order to fetch learners by section
    List<LearnerEnrollmentView>
    findBySectionIdAndAcademicYearId(Long sectionId, Long academicYearId);
    
    Optional<LearnerEnrollment> findById(Long id);
    
    List<LearnerEnrollmentView>
    findBySectionIdAndAcademicYearIdAndActiveTrue(Long sectionId, Long academicYearId);
    
    List<LearnerEnrollment> findAllBySectionIdAndAcademicYearIdAndActiveTrue(
            Long sectionId,
            Long academicYearId
    );
    
    List<LearnerEnrollment> findByAcademicYearId(Long academicYearId);

    
   
}
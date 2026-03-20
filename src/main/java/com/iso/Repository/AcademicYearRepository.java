package com.iso.Repository;

import com.iso.Model.AcademicYear;
import com.iso.Model.School;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    List<AcademicYear> findBySchoolId(Long schoolId);

    Optional<AcademicYear> findBySchoolIdAndActiveTrue(Long schoolId);
    
    //For learner promotion system
    Optional<AcademicYear> findByActiveTrue();
    
  //For learner promotion system
    List<AcademicYear> findAllByActiveTrue();
    
    boolean existsByNameAndSchoolId(String name, Long schoolId);
   
}
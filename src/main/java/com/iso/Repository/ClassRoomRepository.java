package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iso.Model.AcademicYear;
import com.iso.Model.ClassRoom;
import com.iso.Model.School;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    List<ClassRoom> findBySchoolId(Long schoolId);
    
    boolean existsByNameAndSchoolId(String name, Long schoolId);
    
    // To list classroom by academic year
    List<ClassRoom> findByAcademicYearId(Long academicYearId);
    
    //To search classroom by academic year
    List<ClassRoom> findByNameContainingIgnoreCaseAndAcademicYearId(
            String name, Long academicYearId);
    
    //for learner promotion system
    List<ClassRoom> findBySchoolAndAcademicYearAndActiveTrue(
            School school,
            AcademicYear academicYear
    );
    
 // To list classroom by academic year
    List<ClassRoom> findBySchoolIdAndAcademicYearId(Long schoolId,Long academicYearId);
    
    //To List classroom by active Academic Year
    List<ClassRoom> findBySchoolIdAndAcademicYearIdAndActiveTrue(
            Long schoolId,
            Long academicYearId
    );
    
    boolean existsByNameAndSchoolIdAndAcademicYearId(String name, Long schoolId, Long academicYearId);
    boolean existsByNameAndSchoolIdAndAcademicYear(String name, Long schoolId, AcademicYear academicYearId);
}
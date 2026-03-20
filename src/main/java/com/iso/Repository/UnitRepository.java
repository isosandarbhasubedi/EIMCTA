package com.iso.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.iso.Model.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
	
	List<Unit> findBySubjectSectionClassRoomSchoolId(Long schoolId);
	
	 // Check if a unit with the same name already exists for the given subject and section
    boolean existsByNameAndSubjectId(String name, Long subjectId);
    
    
    
    List<Unit> findBySubjectSectionClassRoomSchoolIdAndSubjectSectionClassRoomIdAndSubjectSectionIdAndSubjectId(
            Long schoolId,
            Long classRoomId,
            Long sectionId,
            Long subjectId
    );
    
    
    //For selecting according to academic year
    List<Unit> findBySubjectSectionClassRoomAcademicYearId(Long academicYearId);

    //For search button in unit page with Academic year
    List<Unit> findByNameContainingIgnoreCaseAndSubjectSectionClassRoomAcademicYearId(
            String name, Long academicYearId);
    
    List<Unit> findBySubjectId(Long subjectId);
    
    boolean existsByNameIgnoreCaseAndSubjectIdAndIdNot(String name, Long subjectId, Long id);

}

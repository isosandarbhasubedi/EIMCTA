package com.iso.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import com.iso.Model.EducatorAssignment;

public interface EducatorAssignmentRepository extends JpaRepository<EducatorAssignment, Long> {

    //This is for educators assignment page to view educators
    List<EducatorAssignment> findBySectionClassRoomSchoolId(Long schoolId);
    
    //This is search function in educator assignment page
    List<EducatorAssignment> 
    findBySectionClassRoomSchoolIdAndEducatorUsernameContainingIgnoreCase(
            Long schoolId, String name);
    
    //After setting academic year
    boolean existsByEducatorIdAndSubjectIdAndSectionId(
            Long educatorId, Long subjectId, Long sectionId);

    List<EducatorAssignment> findBySectionClassRoomAcademicYearId(Long academicYearId);
    
 // Find all assignments for a specific educator in an academic year and not restricted
 List<EducatorAssignment> findByEducatorIdAndSectionClassRoomAcademicYearIdAndRestrictedFalse(
         Long educatorId, Long academicYearId
 );
    
    

}